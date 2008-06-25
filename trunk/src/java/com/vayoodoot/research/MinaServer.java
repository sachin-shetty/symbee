package com.vayoodoot.research;

import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.SSLFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sachin Shetty
 * Date: Aug 8, 2007
 * Time: 4:47:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinaServer {

    private static final int PORT = 9123;

    public static void main(String[] args) throws Exception {

        System.setProperty("javax.net.ssl.keyStore",
                "C:\\sachin\\work\\shoonya\\svn\\trunk\\fileshare\\cert\\myKeystore");
        System.setProperty("javax.net.ssl.keyStorePassword",
                "sachin");
        

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        IoAcceptor acceptor = new SocketAcceptor();

        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setReuseAddress( true );
        SSLFilter sslFilter = new SSLFilter(SSLContext.getDefault());
        DefaultIoFilterChainBuilder chain = cfg.getFilterChain();
        chain.addLast("sslFilter", sslFilter);


//        cfg.getFilterChain().addLast( "logger", new LoggingFilter() );
//        cfg.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));


        acceptor.bind( new InetSocketAddress(PORT), new MinaServerHandler(), cfg);
        System.out.println("MINA Time server started.");
    }


}
