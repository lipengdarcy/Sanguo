package org.darcy.sanguo.gate;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ProtobufIntZlibFrameDecoder;
import org.darcy.sanguo.net.ProtobufIntZlibFrameEncoder;
import org.darcy.sanguo.net.SangoProtobufDecoder;
import org.darcy.sanguo.net.SangoProtobufEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import sango.packet.PbGate;
import sango.packet.PbPacket;

public class GateConnector implements Runnable {
	String ip;
	int port;
	Channel channel;
	EventLoopGroup workerGroup = new NioEventLoopGroup();
	ChannelHandlerContext ctx;

	public GateConnector(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		while (true) {
			if ((this.channel == null) || (!(this.channel.isOpen()))) {
				try {
					Bootstrap b = new Bootstrap();
					b.group(this.workerGroup);
					b.channel(NioSocketChannel.class);
					b.option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
					b.handler(new ChannelInitializer() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast("frameDecoder", new ProtobufIntZlibFrameDecoder())
									.addLast("protobufDecoder",
											new SangoProtobufDecoder(PbPacket.Packet.getDefaultInstance()))
									.addLast("frameEncoder", new ProtobufIntZlibFrameEncoder())
									.addLast("protobufEncoder", new SangoProtobufEncoder())
									.addLast(new ChannelHandler[] { new GateClientHandler() });
						}
					});
					this.channel = b.connect(this.ip, this.port).sync().channel();
					Platform.getLog().logSystem("Connect to gate " + this.ip + ":" + this.port + " success;");
				} catch (Exception e) {
					Platform.getLog().logSystem("Connect to gate " + this.ip + ":" + this.port + " failure;");
				}
			} else {
				PbGate.GateHeart heart = PbGate.GateHeart.newBuilder().build();
				PbPacket.Packet pt = PbPacket.Packet.newBuilder().setData(heart.toByteString()).setPtCode(103).build();
				ChannelFuture f = this.channel.writeAndFlush(pt);
				try {
					f.sync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
