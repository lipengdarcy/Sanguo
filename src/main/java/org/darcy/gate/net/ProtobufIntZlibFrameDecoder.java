package org.darcy.gate.net;

import java.io.ByteArrayInputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.zip.InflaterInputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ProtobufIntZlibFrameDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int count = 10;
		in = in.order(ByteOrder.BIG_ENDIAN);
		while (count-- > 0) {
			in.markReaderIndex();
			if (in.readableBytes() > 4) {
				int index = in.readerIndex();
				in.readerIndex(index);
				int length = in.readInt();
				if (in.readableBytes() >= length + 1) {
					byte compress = in.readByte();
					ByteBuf bytes = in.readBytes(length);
					if (compress == 0) {
						out.add(bytes);
						continue;
					}
					try {
						byte[] array = bytes.array();
						ByteArrayInputStream bin = new ByteArrayInputStream(array);
						InflaterInputStream zipin = new InflaterInputStream(bin);
						zipin.available();
						byte[] buffer = new byte[1048576];
						int len = 0;
						while (zipin.available() == 1) {
							int tmp = zipin.read(buffer, len, buffer.length - len);
							if (tmp > 0) {
								len += tmp;
							}
						}
						ByteBuf o = Unpooled.buffer(len);
						o.writeBytes(buffer, 0, len);
						out.add(o);
					} catch (Throwable e) {
						e.printStackTrace();
					}
					continue;
				}
				in.resetReaderIndex();
				return;
			}
			return;
		}
	}

}
