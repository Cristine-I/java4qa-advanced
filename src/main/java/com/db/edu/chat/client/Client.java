package com.db.edu.chat.client;

import com.db.edu.chat.server.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
	public static void main(String... args) throws IOException {
		final Socket socket = new Socket(Server.HOST, Server.PORT);
		final BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		final BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		final BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

		(new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						String message = socketReader.readLine();
						if(message == null) break;
						
						System.out.println(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		while(true) {
			pushMessageToServer(socketWriter, consoleReader);
		}
	}

	private static void pushMessageToServer(BufferedWriter socketWriter, BufferedReader consoleReader) throws IOException {
		socketWriter.write(consoleReader.readLine());
		socketWriter.newLine();
		socketWriter.flush();
	}
}
