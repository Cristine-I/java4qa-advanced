package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Collection;

public class BusinessLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);

    BusinessLogicHandler() {
    }

    public static void handleMessage(ClientTransport inTransport, Collection<ClientTransport> transports)  {
        while(true) {
            try {
                String messagetext = inTransport.read();
                if(messagetext == null) break;
                logger.info("Message from: " + inTransport.getTransportInfo() + messagetext);
                populateMessageToAllClients(inTransport, transports, messagetext);

            } catch (IOException e) {
                logger.error("Network reading message from transport " + inTransport, e);
                try {
                    inTransport.close();
                } catch (IOException innerE) {
                    logger.debug("Error closing transport ", innerE);
                }

                logger.error("Removing socket and stop this handler thread");
                transports.remove(inTransport);
                return;
            }
        }
    }

    private static void populateMessageToAllClients(ClientTransport inTransport, Collection<ClientTransport> transports, String text) {
        for (ClientTransport outTransport : transports) {
            try {
                logger.info("Writing message " + text + " to transport " + outTransport);
                outTransport.write(text);
            } catch (IOException e) {
                logger.error("Error writing message " + text + " to transport " + outTransport + ". Closing transport", e);
                try {
                    outTransport.close();
                } catch (IOException innerE) {
                    logger.error("Error closing transport ", innerE);
                }

                logger.error("Removing transport " + outTransport);
                transports.remove(outTransport);
            }
        }
    }

    private static void pushMessageToClient(String message, Socket outSocket) throws IOException {
        BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(outSocket.getOutputStream()));
        socketWriter.write(message);
        socketWriter.newLine();
        socketWriter.flush();
    }
}
