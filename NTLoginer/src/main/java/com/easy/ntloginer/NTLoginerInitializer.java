package com.easy.ntloginer;
import java.util.concurrent.ThreadPoolExecutor;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import com.easy.ntdb.dao.IUserDAO;
import com.easy.ntloginer.handler.HttpLoginHandler;
import com.easynetty4.bootstrap.IEasyNettyConnector;
 
public class NTLoginerInitializer extends ChannelInitializer<SocketChannel> {
	private IEasyNettyConnector client;
	private IUserDAO userDAO;
	ThreadPoolExecutor threadPoolExecutor;
	public NTLoginerInitializer(IEasyNettyConnector client,IUserDAO userDAO,ThreadPoolExecutor threadPoolExecutor){
		this.client = client;
		this.userDAO =userDAO;
		this.threadPoolExecutor = threadPoolExecutor;
	}
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
 
        if (NTLoginerServer.isSSL) {
            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
            engine.setNeedClientAuth(true); //ssl双向认证
            engine.setUseClientMode(false);
            engine.setWantClientAuth(true);
            engine.setEnabledProtocols(new String[]{"SSLv3"});
            pipeline.addLast("ssl", new SslHandler(engine));
        }
 
        /**
         * http-request解码器
         * http服务器端对request解码
         */
        pipeline.addLast("decoder", new HttpRequestDecoder());
        /**
         * http-response解码器
         * http服务器端对response编码
         */
        pipeline.addLast("encoder", new HttpResponseEncoder());
 
        //pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        /**
         * 压缩
         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
         * while respecting the "Accept-Encoding" header.
         * If there is no matching encoding, no compression is done.
         */
        //pipeline.addLast("deflater", new HttpContentCompressor());
 
        pipeline.addLast("handler", new HttpLoginHandler(client,userDAO,threadPoolExecutor));
    }
}