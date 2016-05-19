package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Collection;

public class ClientConnectionHandler implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);
	
	private final Socket inSocket;
	private final Collection<Socket> clientsSockets;

	public ClientConnectionHandler(Socket clientSocket, Collection<Socket> clientsSockets) throws IOException {
		this.inSocket = clientSocket;
		this.clientsSockets = clientsSockets;
	}

	
	public void run() {
		while(true) {
			try {
				String message = messageWaiting();
				if(message == null) break;

				logger.info("Message from client "
						+ inSocket.getInetAddress() + ":"
						+ inSocket.getPort() + "> "
						+ message);

				for (Socket outSocket : clientsSockets) {
					try {
						if (outSocket.isClosed()) continue;
						if (!outSocket.isBound()) continue;
						if (!outSocket.isConnected()) continue;
						if (outSocket == this.inSocket) continue;
						logger.info("Writing message " + message + " to socket " + outSocket);

						pushMessageToClient(message, outSocket);

					} catch (IOException e) {
						logger.error("Error writing message " + message + " to socket " + outSocket + ". Closing socket", e);
						try {
							outSocket.close();
						} catch (IOException innerE) {
							logger.error("Error closing socket ", innerE);
						}

						logger.error("Removing connection " + outSocket);
						clientsSockets.remove(outSocket);
					}
				}

			} catch (IOException e) {
				logger.error("Network reading message from socket " + inSocket, e);
				try {
					inSocket.close();
				} catch (IOException innerE) {
					logger.debug("Error closing socket ", innerE);
				}

				logger.error("Removing socket and stop this handler thread");
				clientsSockets.remove(inSocket);
				return;
			}

		}
	}

	private void pushMessageToClient(String message, Socket outSocket) throws IOException {
		BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(outSocket.getOutputStream()));
		socketWriter.write(message);
		socketWriter.newLine();
		socketWriter.flush();
	}

	private String messageWaiting() throws IOException {
		BufferedReader socketReader = new BufferedReader(new InputStreamReader(inSocket.getInputStream()));
		return socketReader.readLine();
	}
}
