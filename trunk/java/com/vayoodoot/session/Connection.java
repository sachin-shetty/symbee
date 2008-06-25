package com.vayoodoot.session;

import org.apache.log4j.Logger;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.vayoodoot.message.MessageHandler;
import com.vayoodoot.message.ServerMessageHandler;
import com.vayoodoot.message.Message;
import com.vayoodoot.server.ServerException;

/**
 * This class represents a connection between the server and a client,
 * kind of class corresponding to Session on the client side
 */
public class Connection {

    /**
     * Socket connection with the target
     */
    protected Socket socket = null;

    protected InputStream inputStream;
    protected OutputStream outputStream;

    protected String remoteIP;
    protected int remotePort;



    private static Logger logger = Logger.getLogger(Connection.class);

    // Message Handler to recieve all the messages that appear on the inputstream
    protected MessageHandler messageHandler;



    protected Connection(Socket socket) throws IOException, ServerException {

        this.socket = socket;

        // Need to find a better way to do this?
        socket.getOutputStream().write("<root>".getBytes());
        socket.getOutputStream();

    }

    public Connection() {
        
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public synchronized void sendResponse(String message) throws IOException {

        logger.info("Wrote The Message: " + message);
        writeToStream(message);

    }

    protected void writeToStream(String message) throws IOException {
        byte[] contents = message.getBytes();

        for (int i = 0; i < contents.length;) {
            if (i + Message.PACKET_LENGTH < contents.length) {
                outputStream.write(contents, i, Message.PACKET_LENGTH);
            } else {
                outputStream.write(contents, i, contents.length);
            }
            i = i + Message.PACKET_LENGTH;
            outputStream.flush();
        }
        outputStream.flush();

    }


    public String getClientIP() {
        return remoteIP;
    }

    public void setClientIP(String clientIP) {
        this.remoteIP = clientIP;
    }

    public int getClientPort() {
        return remotePort;
    }

    public void setClientPort(int clientPort) {
        this.remotePort = clientPort;
    }

    public void close() throws IOException {
        socket.close();
    }

}
