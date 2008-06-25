package com.vayoodoot.server;

import org.apache.mina.common.*;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.apache.log4j.Logger;
import com.vayoodoot.util.Packet2MessageConverter;
import com.vayoodoot.session.UserConnection;
import com.vayoodoot.session.Connection;
import com.vayoodoot.message.Message;
import com.vayoodoot.packet.PacketMessageSender;
import com.vayoodoot.user.UserManager;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 17, 2007
 * Time: 5:10:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinaIOHandler implements IoHandler {

    private static Logger logger = Logger.getLogger(MinaIOHandler.class);

    private static final String USER_CONNECTION = "USER_CONNECTION";


    private Packet2MessageConverter packet2MessageConverter;
    private PacketMessageSender packetMessageSender;


    public MinaIOHandler(Packet2MessageConverter packet2MessageConverter, PacketMessageSender packetMessageSender) {
        this.packet2MessageConverter = packet2MessageConverter;
        this.packetMessageSender = packetMessageSender;
    }

    public void messageReceived(IoSession session, Object msg) throws Exception {

        Message xmlMessage = (Message)msg;
        UserConnection userConnection = (UserConnection)session.getAttribute(USER_CONNECTION);
        if (userConnection == null) {
            // User is not Logged in
            logger.info("Just Created a new User connection");
            userConnection = new UserConnection(packetMessageSender, session);
            session.setAttribute(USER_CONNECTION, userConnection);
        }

        userConnection.processMessage(xmlMessage);

    }

    public void messageSent(IoSession ioSession, Object object) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sessionCreated(IoSession session) throws Exception {
        logger.info("New User Logged in " + session.getRemoteAddress());
        if( session.getTransportType() == TransportType.SOCKET )
            ((SocketSessionConfig) session.getConfig() ).setReceiveBufferSize( 1024 * 10  );
        session.setIdleTime( IdleStatus.BOTH_IDLE, 1000);

    }

    public void sessionOpened(IoSession ioSession) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void sessionClosed(IoSession session) throws Exception {

        logger.info("User Logged off:" + session.getRemoteAddress());
        UserConnection userConnection = (UserConnection)session.getAttribute(USER_CONNECTION);
        if (userConnection != null && session.getAttribute("ALREADY_CLOSED") == null) {
            // User is not Logged in
            ActivityLoggerFactory.getActivityLogger().userLoggedOff(UserManager.getLoggedInUser(userConnection.getUserName()));
            userConnection.close();

        }



    }

    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {
        logger.fatal(throwable);
    }


}
