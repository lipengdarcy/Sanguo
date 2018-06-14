package org.darcy.sanguo.net;

import java.util.concurrent.LinkedBlockingQueue;

import org.darcy.ServerStartup;
import org.darcy.sanguo.Platform;

import io.netty.channel.ChannelHandlerContext;

public class AutoFlusher implements Runnable {
	public static LinkedBlockingQueue<ChannelHandlerContext> ctxs = new LinkedBlockingQueue(8164);

	public static void addFlush(ChannelHandlerContext ctx) {
		if (ctx != null)
			ctxs.offer(ctx);
	}

	public void run() {
		while (ServerStartup.running)
			try {
				long s = System.currentTimeMillis();
				ChannelHandlerContext ctx = null;
				while ((ctx = (ChannelHandlerContext) ctxs.poll()) != null) {
					//ctx.flush();
				}
				long diff = System.currentTimeMillis() - s;
				if (diff > 20L) {
					Platform.getLog().logWarn("flush loop too long: " + diff);
				}
				Thread.sleep(10L);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
