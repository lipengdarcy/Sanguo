package org.darcy.sanguo.gate;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import sango.packet.PbGate;
import sango.packet.PbPacket;

class GateClientHandler extends ChannelHandlerAdapter {
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		PbGate.GateRegister.Builder register = PbGate.GateRegister.newBuilder().setId(Configuration.serverId)
				.setIp(Configuration.serverIp).setName(Configuration.name).setPort(Configuration.serverPort)
				.setTest(Configuration.test);
		for (int number : Configuration.numbers) {
			register.addNumbers(number);
		}
		PbPacket.Packet pt = PbPacket.Packet.newBuilder().setPtCode(101).setData(register.build().toByteString())
				.build();
		ctx.writeAndFlush(pt);

		PbGate.GateVersion v = PbGate.GateVersion.newBuilder().build();
		pt = PbPacket.Packet.newBuilder().setPtCode(105).setData(v.toByteString()).build();
		ctx.writeAndFlush(pt);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (!(msg instanceof PbPacket.Packet))
				return;
			try {
				PbPacket.Packet packet = (PbPacket.Packet) msg;
				if (packet.getPtCode() == 106) {
					PbGate.GateVersionRst rst = PbGate.GateVersionRst.parseFrom(packet.getData());
					Configuration.version = rst.getVersion();
				}
			} catch (Exception e) {
				Platform.getLog().logWarn(e);
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}
}
