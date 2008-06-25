package com.vayoodoot.server;

import org.apache.log4j.Logger;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.SSLFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;



import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.nio.charset.Charset;

import com.vayoodoot.session.Connection;
import com.vayoodoot.session.ConnectionHandler;
import com.vayoodoot.session.UserConnection;
import com.vayoodoot.properties.VDProperties;
import com.vayoodoot.util.*;
import com.vayoodoot.user.UserManager;
import com.vayoodoot.packet.*;
import com.vayoodoot.research.MinaServerHandler;
import com.vayoodoot.message.VDMessageDecoder;
import com.vayoodoot.message.VDMessageDecoderFactory;

import javax.net.ssl.SSLContext;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Jan 14, 2007
 * Time: 6:31:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Server  {

    public static int serverPort = VDProperties.getNumericProperty("MAIN_SERVER_PORT");
    private ServerSocket serverSocket;
    private VDThreadRunner socketThread;

    private static Logger logger = Logger.getLogger(Server.class);

    ConnectionHandler connectionHandler = new ConnectionHandler();

    IoAcceptor acceptor;

    // UDP Socket Stuff
    private VDDatagramSocket datagramSocket;
    private PacketListener packetListener;
    private PacketMessageHandler packetMessageHandler;
    private PacketMessageSender packetMessageSender;

    public Server() {

    }

    public Server(int serverPort) {

        this.serverPort = serverPort;

    }



    private void startDatagramSocket() {

        try {
            datagramSocket = new VDDatagramSocket("Server",serverPort);

            // Start the packet listener
            packetListener = new PacketListener("PacketListener: Server",datagramSocket);
            packetListener.startListening();

            packetMessageSender = new PacketMessageSender(datagramSocket);

            packetMessageHandler = new  ServerPacketMessageHandler("PacketMessageHandler: Server", packetListener, packetMessageSender);
            packetMessageHandler.startProcessing();

        } catch (Exception e) {
            System.out.println("Client Could not listen on port: 4444" + e);
            logger.fatal("Exception Occurred while listening: " + e);
        }




    }

    public void startMinaServer () throws ServerException {

//        System.setProperty("javax.net.ssl.keyStore",
//                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\myKeystore");
//        System.setProperty("javax.net.ssl.keyStorePassword",
//                "sachin");
//        System.setProperty("javax.net.ssl.trustStore",
//                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\vdcert.cer");




        try {

            ActivityLoggerFactory.getActivityLogger().serverStarted();

            ByteBuffer.setUseDirectBuffers(false);
            ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
            acceptor = new SocketAcceptor();
            SocketAcceptorConfig cfg = new SocketAcceptorConfig();
            cfg.getSessionConfig().setReuseAddress( true );
            Packet2MessageConverter packet2MessageConverter = new Packet2MessageConverter();
            SSLFilter sslFilter = new SSLFilter(SSLContext.getDefault());
            cfg.getFilterChain().addLast("sslFilter", sslFilter);
            cfg.getFilterChain().addLast( "codec", new ProtocolCodecFilter(new VDMessageDecoderFactory()));
            cfg.getFilterChain().addLast( "logger", new LoggingFilter());
            acceptor.bind( new InetSocketAddress(serverPort), new MinaIOHandler(packet2MessageConverter, packetMessageSender) , cfg);
            // Recieved the client connection, store it
//                Connection connection = new UserConnection(clientSocket, packetMessageSender);
//                connectionHandler.addConnection(connection);



            System.out.println("MinaServer Started...");
            logger.warn("Version read from properties is: " + VDProperties.getProperty("VERSION"));
            System.out.println("Version read from properties is: " + VDProperties.getProperty("VERSION"));
            
            logger.debug("Mina Server Started...");
        }
        catch (Exception e) {
            logger.fatal(e);
            throw new ServerException("Error occurred in Server", e);
        }

    }


    public void close() throws IOException {


        ActivityLoggerFactory.getActivityLogger().serverStopped();
        System.out.println("Shutting down server");
        datagramSocket.close();
        packetMessageHandler.close();
        packetListener.stop();
        acceptor.unbindAll();
        UserManager.closeAllUserConnections();
        System.out.println("Server successfully shutdown");

    }

    public void startServer() throws ServerException, IOException {

        startDatagramSocket();
        startMinaServer();


    }

    public void stopServer() throws ServerException, IOException, VDThreadException {

        close();

    }




}
