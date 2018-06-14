package org.darcy.sanguo.gm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ProtobufIntZlibFrameDecoder;
import org.darcy.sanguo.net.ProtobufIntZlibFrameEncoder;
import org.darcy.sanguo.net.SangoProtobufDecoder;
import org.darcy.sanguo.net.SangoProtobufEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import sango.packet.PbPacket;

public class GmConnector implements Runnable {
	String ip;
	int port;
	Channel channel;
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	int count = 0;

	public GmConnector(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		while (true) {
			try {
				if ((this.channel == null) || (!(this.channel.isOpen())) || (!(this.channel.isWritable()))
						|| (!(this.channel.isActive())) || ((this.count == 6) && (!(isConnect())))) {
					Bootstrap b = new Bootstrap();
					b.group(this.workerGroup);
					b.channel(NioSocketChannel.class);
					b.option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
					b.handler(new ChannelInitializer<Channel>() {
						@Override
						public void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast("frameDecoder", new ProtobufIntZlibFrameDecoder())
									.addLast("protobufDecoder",
											new SangoProtobufDecoder(PbPacket.Packet.getDefaultInstance()))
									.addLast("frameEncoder", new ProtobufIntZlibFrameEncoder())
									.addLast("protobufEncoder", new SangoProtobufEncoder())
									.addLast(new ChannelHandler[] { new GmClientHandler() });
						}
					});
					this.channel = b.connect(this.ip, this.port).sync().channel();
					Platform.getLog().logSystem("Connect to gm " + this.ip + ":" + this.port + " success;");
				}
				this.count += 1;
			} catch (Exception e) {
				Platform.getLog().logError(e);
				Platform.getLog().logSystem("Connect to gm " + this.ip + ":" + this.port + " failure;");
				this.channel = null;
			}
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConnect() {
		try {
			String tmp;
			this.count = 0;
			URL httpUrl = null;
			if (Configuration.gmHeart == null)
				httpUrl = new URL("http", Configuration.gmIp, 80, "/sangogm/checkserver");
			else {
				httpUrl = new URL(Configuration.gmHeart + "/checkserver");
			}
			HttpURLConnection http = (HttpURLConnection) httpUrl.openConnection();
			http.setConnectTimeout(5000);
			http.setReadTimeout(5000);
			http.setRequestMethod("POST");
			http.setDoInput(true);
			http.setDoOutput(true);
			PrintStream out = new PrintStream(http.getOutputStream(), true, "UTF-8");
			out.print("serverId=" + Configuration.serverId);
			out.flush();
			InputStream is = http.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();

			while ((tmp = reader.readLine()) != null) {
				sb.append(tmp);
			}
			boolean result = Boolean.valueOf(sb.toString()).booleanValue();
			return result;
		} catch (Exception e) {
		}
		return false;
	}
}
