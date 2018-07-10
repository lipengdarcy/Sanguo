package org.darcy.gate.config;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.darcy.gate.listener.FileListenerService;
import org.darcy.gate.net.ProtobufIntFrameDecoder;
import org.darcy.gate.net.ProtobufIntFrameEncoder;
import org.darcy.gate.net.ProtobufIntZlibFrameDecoder;
import org.darcy.gate.net.ProtobufIntZlibFrameEncoder;
import org.darcy.gate.net.SangoProtobufDecoder;
import org.darcy.gate.net.SangoProtobufEncoder;
import org.darcy.gate.notice.NoticeManager;
import org.darcy.gate.server.ServerManager;
import org.darcy.gate.version.VersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sango.packet.PbPacket;

/**
 * 网关配置
 */
@Configuration
public class NettyConfig {

	private final Log log = LogFactory.getLog(getClass());

	@Value("${boss.thread.count}")
	private int bossCount;

	@Value("${worker.thread.count}")
	private int workerCount;

	@Value("${gate.address}")
	private String address;

	@Value("${gate.port}")
	private int port;

	@Value("${so.keepalive}")
	private boolean keepAlive;

	@Value("${so.backlog}")
	private int backlog;

	@Autowired
	@Qualifier("nettyProtocolInitalizer")
	private NettyProtocolInitalizer NettyProtocolInitalizer;

	// bootstrap配置
	@SuppressWarnings("unchecked")
	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap() {

		VersionManager.reload();
		NoticeManager.reload();
		ServerManager.getInstance();

		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup(), workerGroup()).channel(NioServerSocketChannel.class).childHandler(NettyProtocolInitalizer);
		Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
		Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
		for (@SuppressWarnings("rawtypes")
		ChannelOption option : keySet) {
			b.option(option, tcpChannelOptions.get(option));
		}
		log.info("1.Netty Channel初始化");
		return b;
	}

	@Bean(name = "fileListenerService")
	public FileListenerService fileListenerService() {
		FileListenerService a = new FileListenerService();
		a.start();
		return a;
	}

	// bossGroup就是parentGroup，负责处理TCP/IP连接
	@Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		log.info("2.Netty bossGroup，负责处理TCP/IP连接，连接数" + bossCount );
		return new NioEventLoopGroup(bossCount);
	}

	// workerGroup就是childGroup，负责处理Channel（通道）的I/O事件。
	@Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		log.info("3.Netty workerGroup，负责处理Channel（通道）的I/O事件，连接数" + workerCount );
		return new NioEventLoopGroup(workerCount);
	}

	@Bean(name = "tcpSocketAddress")
	public InetSocketAddress gateAddress() {
		log.info("4.Netty 网关地址：" + address + ": " + port);
		return new InetSocketAddress(address, port);
	}

	@Bean(name = "tcpChannelOptions")
	public Map<ChannelOption<?>, Object> tcpChannelOptions() {
		Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
		options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
		options.put(ChannelOption.SO_BACKLOG, backlog);
		options.put(ChannelOption.SO_REUSEADDR, true);
		return options;
	}

	@Bean(name = "FrameDecoder")
	public ProtobufIntFrameDecoder FrameDecoder() {
		return new ProtobufIntFrameDecoder();
	}

	@Bean(name = "FrameEncoder")
	public ProtobufIntFrameEncoder FrameEncoder() {
		return new ProtobufIntFrameEncoder();
	}

	@Bean(name = "ZlibFrameDecoder")
	public ProtobufIntZlibFrameDecoder ZlibFrameDecoder() {
		return new ProtobufIntZlibFrameDecoder();
	}

	@Bean(name = "ZlibFrameEncoder")
	public ProtobufIntZlibFrameEncoder ZlibFrameEncoder() {
		return new ProtobufIntZlibFrameEncoder();
	}

	@Bean(name = "bufDecoder")
	public SangoProtobufDecoder bufDecoder() {
		return new SangoProtobufDecoder(PbPacket.Packet.getDefaultInstance());
	}

	@Bean(name = "bufEncoder")
	public SangoProtobufEncoder bufEncoder() {
		return new SangoProtobufEncoder();
	}

	/**
	 * Necessary to make the Value annotations work.
	 *
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
