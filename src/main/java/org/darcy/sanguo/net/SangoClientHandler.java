package org.darcy.sanguo.net;

import java.util.HashSet;
import java.util.Set;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.BanIp;
import org.darcy.sanguo.guard.Counter;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import sango.packet.PbPacket;

public class SangoClientHandler extends ChannelHandlerAdapter {
	public static final Set<Integer> besides = new HashSet<Integer>();
	public static final AttributeKey<Integer> SESSION_ID;

	static {
		besides.add(Integer.valueOf(1103));
		besides.add(Integer.valueOf(2183));
		besides.add(Integer.valueOf(1051));
		besides.add(Integer.valueOf(1053));
		besides.add(Integer.valueOf(1055));

		SESSION_ID = AttributeKey.valueOf("SESSION_ID");
	}

	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg instanceof PbPacket.Packet) {
				int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
				ClientSession session = Platform.getClientSessionManager().getClientSession(sid);
				if (session != null) {
					try {
						PbPacket.Packet packet = (PbPacket.Packet) msg;
						if (!(besides.contains(Integer.valueOf(packet.getPtCode())))) {
							int pid = session.getPlayerId();
							if (pid != 0) {
								Counter.add(String.valueOf(pid));
								int count = Counter.check(String.valueOf(pid), 60000L);
								if (count > 100) {
									Platform.getLog().logNet("RECEIVE " + ((PbPacket.Packet) msg).getPtCode() + ":"
											+ ctx.channel().remoteAddress());
									Platform.getLog()
											.logError("Guard: packet too many!! Player id: " + pid + "   Count:" + count
													+ " Recent opCode:" + ((PbPacket.Packet) msg).getPtCode() + " Ip:"
													+ session.getIp());
									ctx.close();
									Counter.clear(String.valueOf(pid));

									ReferenceCountUtil.release(msg);

									return;
								}
								if (count > 50) {
									Platform.getLog()
											.logWarn("Guard: packet too many!! Player id: " + pid + "   Count:" + count
													+ " Recent opCode:" + ((PbPacket.Packet) msg).getPtCode() + " Ip:"
													+ session.getIp());
								}
							}
						}

						session.addPacket(packet);
					} catch (Exception e) {
						Platform.getLog().logWarn(e);
					}
				}
			}

		} finally {
			ReferenceCountUtil.release(msg);
		}
		ReferenceCountUtil.release(msg);
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		if (BanIp.isBan(ClientSession.getIp(ctx))) {
			ctx.close();
			return;
		}
		ClientSession session = new ClientSession(ctx);
		Platform.getClientSessionManager().addClientSession(session);
		ctx.attr(SESSION_ID).set(Integer.valueOf(session.getId()));
	}

	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		super.channelUnregistered(ctx);
		int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
		ClientSession session = Platform.getClientSessionManager().getClientSession(sid);
		if (session != null)
			session.disconnect();
	}

	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		super.channelWritabilityChanged(ctx);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		int sid = ((Integer) ctx.attr(SESSION_ID).get()).intValue();
		ClientSession session = Platform.getClientSessionManager().getClientSession(sid);
		if (session != null) {
			session.disconnect();
		}

		ctx.close();
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		if (evt instanceof IdleStateEvent)
			ctx.close();
	}

	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
	}

	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
	}
}
