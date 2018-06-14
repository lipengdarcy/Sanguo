package org.darcy.gate.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.ByteOrder;
import java.util.List;

public class ProtobufIntFrameDecoder extends ByteToMessageDecoder {
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int count = 10;
		in = in.order(ByteOrder.BIG_ENDIAN);
		while (count-- > 0) {
			in.markReaderIndex();
			if (in.readableBytes() > 4) {
				int index = in.readerIndex();

				in.readerIndex(index);
				int length = in.readInt();
				if (in.readableBytes() >= length) {
					ByteBuf bytes = in.readBytes(length);
					out.add(bytes);

					continue;
				}
				in.resetReaderIndex();
				return;
			}

			return;
		}
	}
}
