package org.darcy.gate.net;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import sango.packet.PbPacket;

public class SangoServerHandler extends ChannelHandlerAdapter {
	public static final AttributeKey<Integer> SESSION_ID = AttributeKey.valueOf("SESSION_ID");

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (!(msg instanceof PbPacket.Packet))
				return;
			int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
			ClientSession session = ClientSessionManager.getClientSession(sid);
			if (session == null)
				return;
			session.addPacket((PbPacket.Packet) msg);
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ClientSession session = new ClientSession(ctx);
		ClientSessionManager.addClientSession(session);
		ctx.attr(SESSION_ID).set(Integer.valueOf(session.getId()));
	}

	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
		ClientSession session = ClientSessionManager.getClientSession(sid);
		if (session != null)
			session.disconnect();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
		ClientSession session = ClientSessionManager.getClientSession(sid);
		if (session != null) {
			session.disconnect();
		}
		ctx.close();
	}
}
