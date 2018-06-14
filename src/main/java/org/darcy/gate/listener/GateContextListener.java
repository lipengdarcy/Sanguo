package org.darcy.gate.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.darcy.gate.net.ClientSessionManager;
import org.darcy.gate.net.ProtobufIntZlibFrameDecoder;
import org.darcy.gate.net.ProtobufIntZlibFrameEncoder;
import org.darcy.gate.net.SangoProtobufDecoder;
import org.darcy.gate.net.SangoProtobufEncoder;
import org.darcy.gate.net.SangoServerHandler;
import org.darcy.gate.notice.NoticeManager;
import org.darcy.gate.server.ServerManager;
import org.darcy.gate.version.VersionManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import sango.packet.PbPacket;

/**
 * 2.网关启动内容
 * 
 * ServletContextListener是servlet容器中的一个API接口,
 * 它用来监听ServletContext的生命周期，也就是相当于用来监听Web应用的生命周期
 */
@WebListener
public class GateContextListener implements ServletContextListener {

	private final Log log = LogFactory.getLog(getClass());

	EventLoopGroup bossGroup = new NioEventLoopGroup(2);
	EventLoopGroup workerGroup = new NioEventLoopGroup(2);
	FileListenerService filelis;
	ChannelFuture future = null;
	ServerBootstrap b;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			ClientSessionManager.shutdown();
			ServerManager.getInstance().shutdown();
			this.future.channel().close().sync();
			this.future.channel().closeFuture().sync();
			this.workerGroup.shutdownGracefully().sync();
			this.bossGroup.shutdownGracefully().sync();
			this.filelis.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("网关关闭成功！");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg) {
		VersionManager.reload();
		NoticeManager.reload();
		ServerManager.getInstance();
		this.filelis = new FileListenerService();
		this.filelis.start();
		this.b = new ServerBootstrap();

		((ServerBootstrap) ((ServerBootstrap) ((ServerBootstrap) this.b.group(this.bossGroup, this.workerGroup)
				.channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer<Channel>() {
					@Override
					public void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast("loger", new LoggingHandler("Netty"))
								.addLast("frameDecoder", new ProtobufIntZlibFrameDecoder())
								.addLast("protobufDecoder",
										new SangoProtobufDecoder(PbPacket.Packet.getDefaultInstance()))
								.addLast("frameEncoder", new ProtobufIntZlibFrameEncoder())
								.addLast("protobufEncoder", new SangoProtobufEncoder())
								.addLast("sangoServerHandler", new SangoServerHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, Integer.valueOf(128))).option(ChannelOption.SO_REUSEADDR,
						Boolean.valueOf(true))).childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));

		int port = 12001;
		try {
			this.future = this.b.bind(port).sync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("网关启动成功！监听端口：" + port);

		ClientSessionManager manager = new ClientSessionManager();
		Thread t = new Thread(manager, "ClientSessionManager");
		t.setDaemon(true);
		t.start();
	}
}
