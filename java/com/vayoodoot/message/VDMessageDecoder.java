package com.vayoodoot.message;

import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ByteBuffer;
import org.apache.log4j.Logger;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

import com.vayoodoot.util.Packet2MessageConverter;
import com.vayoodoot.session.UserConnection;
import com.vayoodoot.session.Connection;


/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 17, 2007
 * Time: 5:37:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDMessageDecoder implements ProtocolDecoder {


    private static final String MESSAGE_END = "</vayoodoot>";
    private static final int MESSAGE_LENGTH = MESSAGE_END.length();
    private static final String PENDING_MESSAGE = "PENDING_MESSAGE";
    static volatile int count =0;

    private CharsetDecoder decoder;
    private Packet2MessageConverter packet2MessageConverter;

    private static Logger logger = Logger.getLogger(VDMessageDecoder.class);

    public VDMessageDecoder() throws Exception {

        decoder = Charset.defaultCharset().newDecoder();
        packet2MessageConverter = new Packet2MessageConverter();
        packet2MessageConverter.init();
        logger.debug("VDMessageDecoder created: " + count++);

    }


    public synchronized void decode(IoSession session, ByteBuffer byteBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

        String text = byteBuffer.getString(decoder);

        StringBuilder pendingMessage = (StringBuilder)session.getAttribute(PENDING_MESSAGE);
        if (pendingMessage != null) {
            text = text + pendingMessage;
            pendingMessage.setLength(0);
        } else {
            pendingMessage = new StringBuilder();
        }


        int end = text.indexOf(MESSAGE_END);
        if (end != -1) {
            while (end != -1) {
                String message;
                if (end + MESSAGE_LENGTH > text.length()) {
                    // Received a message
                    message = text.substring(0, end + 12);
                    if (pendingMessage == null) {
                        pendingMessage = new StringBuilder();
                    }
                    pendingMessage.append(text.substring(end + 13, text.length()));
                }
                else {
                    message = text;
                }
                logger.info("Xml Message is: " + message);
                Message xmlMessage = packet2MessageConverter.getMessage(message.getBytes(), message.length());
                logger.info("Message received...");
                protocolDecoderOutput.write(xmlMessage);
                text = text.substring(end + MESSAGE_LENGTH , text.length());
                end = text.indexOf(MESSAGE_END);
            }
        }
        else {
            pendingMessage.append(text);
        }
        session.setAttribute(PENDING_MESSAGE, pendingMessage);


    }

    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        logger.debug("Finish Decoding: ");
    }

    public void dispose(IoSession ioSession) throws Exception {
        logger.debug("Disposing Decoding: ");
    }


}
