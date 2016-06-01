package com.db.edu.chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class BusinessLogicHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionHandler.class);
    private ClientTransport inTransport;
    private Collection<ClientTransport> transports;

    public BusinessLogicHandler(ClientTransport inTransport, Collection<ClientTransport> transports) {
        this.inTransport = inTransport;
        this.transports = transports;
    }

    public void handleMessage()  {
        while(true) {
            try {
                String message = inTransport.read();
                if(message == null) break;
                logger.info("Message from: " + inTransport.getTransportInfo() + message);
                populateMessageToAllClients(message);

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

    private void populateMessageToAllClients(String message) {
        for (ClientTransport outTransport : transports) {
            try {
                logger.info("Writing message " + message + " to transport " + outTransport);
                if (outTransport == this.inTransport) continue;
                outTransport.write(message);
            } catch (IOException e) {
                logger.error("Error writing message " + message + " to transport " + outTransport + ". Closing transport", e);
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
}
