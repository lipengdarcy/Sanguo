package org.darcy.gate.config;

import org.darcy.gate.net.ProtobufIntZlibFrameDecoder;
import org.darcy.gate.net.ProtobufIntZlibFrameEncoder;
import org.darcy.gate.net.SangoProtobufDecoder;
import org.darcy.gate.net.SangoProtobufEncoder;
import org.darcy.gate.net.SangoServerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * netty 通道初始化
 */
@Component
@Qualifier("nettyProtocolInitializer")
public class NettyProtocolInitalizer extends ChannelInitializer<SocketChannel> {

	@Autowired
	ProtobufIntZlibFrameDecoder ZlibFrameDecoder;

	@Autowired
	ProtobufIntZlibFrameEncoder ZlibFrameEncoder;
	
	@Autowired
	SangoProtobufDecoder bufDecoder;

	@Autowired
	SangoProtobufEncoder bufEncoder;

	@Autowired
	SangoServerHandler SangoServerHandler;

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("loger", new LoggingHandler("Netty"));
		//pipeline.addLast("ZlibFrameDecoder", ZlibFrameDecoder);
		pipeline.addLast("ZlibFrameDecoder", new ProtobufIntZlibFrameDecoder());
		pipeline.addLast("ZlibFrameEncoder", ZlibFrameEncoder);
		pipeline.addLast("bufDecoder", bufDecoder);		
		pipeline.addLast("bufEncoder", bufEncoder);
		pipeline.addLast("sangoServerHandler", SangoServerHandler);

	}

}
