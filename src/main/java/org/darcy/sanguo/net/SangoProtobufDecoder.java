package org.darcy.sanguo.net;

import java.util.List;

import org.darcy.sanguo.Platform;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

@ChannelHandler.Sharable
public class SangoProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
	private static final boolean HAS_PARSER;
	private final MessageLite prototype;
	private final ExtensionRegistry extensionRegistry;

	static {
		boolean hasParser = false;
		try {
			MessageLite.class.getDeclaredMethod("getParserForType", new Class[0]);
			hasParser = true;
		} catch (Throwable localThrowable) {
		}
		HAS_PARSER = hasParser;
	}

	public SangoProtobufDecoder(MessageLite prototype) {
		this(prototype, null);
	}

	public SangoProtobufDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
		if (prototype == null) {
			throw new NullPointerException("prototype");
		}
		this.prototype = prototype.getDefaultInstanceForType();
		this.extensionRegistry = extensionRegistry;
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		byte[] array;
		int offset;
		int length = msg.readableBytes();
		if (msg.hasArray()) {
			array = msg.array();
			offset = msg.arrayOffset() + msg.readerIndex();
		} else {
			array = new byte[length];
			msg.getBytes(msg.readerIndex(), array, 0, length);
			offset = 0;
		}
		try {
			if (this.extensionRegistry == null) {
				if (HAS_PARSER) {
					out.add(this.prototype.getParserForType().parseFrom(array, offset, length));
					return;
				}
				out.add(this.prototype.newBuilderForType().mergeFrom(array, offset, length).build());

				return;
			}
			if (HAS_PARSER) {
				out.add(this.prototype.getParserForType().parseFrom(array, offset, length, this.extensionRegistry));
				return;
			}
			out.add(this.prototype.newBuilderForType().mergeFrom(array, offset, length, this.extensionRegistry)
					.build());
		} catch (Exception e) {
			Platform.getLog().logError(e);
			ctx.close();
			throw e;
		}
	}

}