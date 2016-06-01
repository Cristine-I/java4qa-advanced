package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class ClientConnectionHandler implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);

	private final Socket inSocket;
	private final ClientTransport inTransport;

	private final Collection<Socket> clientsSockets;
	private final Collection<ClientTransport> transports = null;

	public ClientConnectionHandler(Socket inSocket, Collection<Socket> clientsSockets) throws IOException {
		this.inSocket = inSocket;
		this.clientsSockets = clientsSockets;

		this.inTransport = new ClientTransport(this.inSocket);

		for (Socket outSocket : this.clientsSockets) {
			ClientTransport outTransport = new ClientTransport(outSocket);
			transports.add(outTransport);
		}

	}

	public void run() {
		BusinessLogicHandler businessLogic = new BusinessLogicHandler(inTransport, transports);
		businessLogic.handleMessage();
	}
}
