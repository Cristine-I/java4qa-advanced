package com.db.edu.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Student on 01.06.2016.
 */
public class ClientTransport {

    Socket inSocket;
    BufferedReader socketReader;
    BufferedWriter socketWriter;

    public ClientTransport(Socket inSocket) throws IOException {
        socketReader = new BufferedReader(new InputStreamReader(inSocket.getInputStream()));
        socketWriter = new BufferedWriter(new OutputStreamWriter(inSocket.getOutputStream()));
    }

    public String read() throws IOException {
        return socketReader.readLine();
    }

    public void write(String text) throws IOException {
        if (checkConnection()) {
            socketWriter.write(text);
            socketWriter.newLine();
            socketWriter.flush();
        }
    }

    public void close() throws IOException {
        inSocket.close();
    }

    public String getTransportInfo() {
        return "Transport: " + inSocket.getInetAddress() + inSocket.getPort();
    }

    public boolean checkConnection() {
        if (inSocket.isClosed() || !inSocket.isBound() || !inSocket.isConnected() )
            return false;
        else
            return true;
    }
}
