package org.darcy.sanguo.gm;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import sango.packet.PbGm;
import sango.packet.PbPacket;

class GmClientHandler extends ChannelInboundHandlerAdapter {
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		PbGm.GmRegister register = PbGm.GmRegister.newBuilder().setId(Configuration.serverId)
				.setIp(Configuration.serverIp).setName(Configuration.name).setPort(Configuration.serverPort).build();
		PbPacket.Packet pt = PbPacket.Packet.newBuilder().setPtCode(201).setData(register.toByteString()).build();
		ctx.writeAndFlush(pt);
		super.channelActive(ctx);
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof PbPacket.Packet))
			return;
		try {
			GmPacketHandler.handlePacket(ctx, (PbPacket.Packet) msg);
		} catch (Throwable e) {
			Platform.getLog().logError(e);
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
}
