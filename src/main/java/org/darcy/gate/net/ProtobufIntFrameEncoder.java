package org.darcy.gate.net;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public class ProtobufIntFrameEncoder extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int bodyLen = msg.readableBytes();
		int headerLen = 4;
		out.ensureWritable(headerLen + bodyLen);
		out = out.order(ByteOrder.BIG_ENDIAN);
		out.writeInt(bodyLen);
		out.writeBytes(msg);
	}

}
