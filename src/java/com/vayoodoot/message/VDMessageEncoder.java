package com.vayoodoot.message;

import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ByteBuffer;
import org.apache.log4j.Logger;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import com.vayoodoot.util.Packet2MessageConverter;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 17, 2007
 * Time: 9:44:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class VDMessageEncoder implements ProtocolEncoder {


    private CharsetEncoder encoder;
    private static Logger logger = Logger.getLogger(VDMessageEncoder.class);


    public VDMessageEncoder()  {

        encoder = Charset.defaultCharset().newEncoder();

    }


    public synchronized void encode(IoSession ioSession, Object object,
                       ProtocolEncoderOutput protocolEncoderOutput) throws Exception {

        String message = (String)object;
        ByteBuffer buf = ByteBuffer.allocate(message.length())
                     .setAutoExpand(true);
        buf.putString(message, encoder);
        logger.debug("Writing Message: " + message);
        buf.flip();
        protocolEncoderOutput.write(buf);
        protocolEncoderOutput.flush();

    }

    public void dispose(IoSession ioSession) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
