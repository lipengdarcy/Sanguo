package org.darcy.gate.net;

import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import sango.packet.PbPacket;

/**
 * serverHandler配置这里是实现业务逻辑的地方
 */
@Component
@ChannelHandler.Sharable
public class SangoServerHandler extends ChannelHandlerAdapter {

	public static final AttributeKey<Integer> SESSION_ID = AttributeKey.valueOf("SESSION_ID");

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
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

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		ClientSession session = new ClientSession(ctx);
		ClientSessionManager.addClientSession(session);
		ctx.attr(SESSION_ID).set(Integer.valueOf(session.getId()));
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
		ClientSession session = ClientSessionManager.getClientSession(sid);
		if (session != null)
			session.disconnect();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
		t.printStackTrace();
		int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
		ClientSession session = ClientSessionManager.getClientSession(sid);
		if (session != null) {
			session.disconnect();
		}
		ctx.close();
	}

}
