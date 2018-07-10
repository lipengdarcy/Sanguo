package test.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * netty http 开发
 * 
 * netty天生异步事件驱动的架构，无论是在性能上还是在可靠性上，都表现优异，非常适合在非Web容器的场景下应用，
 * 相比于传统的Tomcat，Jetty等Web容器，更加的轻量和小巧、灵活性和定制性也更好。
 * 
 * 文件服务器使用HTTP协议对外提供服务
 * 
 * 当客户端通过浏览器访问文件服务器时，对访问路径进行检查，检查失败返回403
 * 
 * 检查通过，以链接的方式打开当前文件目录，每个目录或者都是个超链接，可以递归访问
 * 
 * 如果是目录，可以继续递归访问它下面的目录或者文件，如果是文件并且可读，则可以在浏览器端直接打开，或者通过[目标另存为]下载
 */
public class HttpFileServer {

	private static final String DEFAULT_URL = "/";

	public void run(final int port, final String url) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast("http-decoder", new HttpRequestDecoder()); // 请求消息解码器
							ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));// 目的是将多个消息转换为单一的request或者response对象
							ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());// 响应解码器
							ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());// 目的是支持异步大文件传输（）
							ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));// 业务逻辑
						}
					});
			ChannelFuture future = b.bind("127.0.0.1", port).sync();
			System.out.println("HTTP文件目录服务器启动，网址是 : " + "http://127.0.0.1:" + port + url);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		String url = DEFAULT_URL;
		if (args.length > 1)
			url = args[1];
		new HttpFileServer().run(port, url);
	}
}