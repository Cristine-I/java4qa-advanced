package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
		BusinessLogicHandler.handleMessage(inSocket, clientsSockets);
	}
}
