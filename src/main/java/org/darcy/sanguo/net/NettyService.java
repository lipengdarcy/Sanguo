package org.darcy.sanguo.net;

import org.darcy.gate.net.ProtobufIntZlibFrameDecoder;
import org.darcy.gate.net.ProtobufIntZlibFrameEncoder;
import org.darcy.gate.net.SangoProtobufDecoder;
import org.darcy.gate.net.SangoProtobufEncoder;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.service.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import sango.packet.PbPacket;

public class NettyService implements Service {
	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	public void startup() throws Exception {
		ServerBootstrap b = new ServerBootstrap();

		((ServerBootstrap) ((ServerBootstrap) b.group(this.bossGroup, this.workerGroup)
				.channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast("idleHandler", new IdleStateHandler(300, 300, 300))
								.addLast("frameDecoder", new ProtobufIntZlibFrameDecoder())
								.addLast("protobufDecoder",
										new SangoProtobufDecoder(PbPacket.Packet.getDefaultInstance()))
								.addLast("frameEncoder", new ProtobufIntZlibFrameEncoder())
								.addLast("protobufEncoder", new SangoProtobufEncoder())
								.addLast("sangoServerHandler", new SangoClientHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, Integer.valueOf(1024)))
						.childOption(ChannelOption.SO_RCVBUF, Integer.valueOf(1048576))
						.childOption(ChannelOption.SO_SNDBUF, Integer.valueOf(1048576))
						.childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));

		b.bind(Configuration.serverPort).sync();
		Platform.getLog().logSystem("listen to port: " + Configuration.serverPort);

		Runnable flusher = new AutoFlusher();
		Thread t = new Thread(flusher, "autoFlusher");
		t.setDaemon(true);
		t.start();
	}

	public void shutdown() {
		this.bossGroup.shutdownGracefully();
		this.workerGroup.shutdownGracefully();
	}

	public void reload() throws Exception {
	}
}
