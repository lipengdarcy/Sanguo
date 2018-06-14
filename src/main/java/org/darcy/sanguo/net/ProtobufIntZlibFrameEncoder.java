 package org.darcy.sanguo.net;
 
 import java.io.ByteArrayOutputStream;
import java.nio.ByteOrder;
import java.util.zip.DeflaterOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
 
 public class ProtobufIntZlibFrameEncoder extends MessageToByteEncoder<ByteBuf>
 {
   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
     throws Exception
   {
     int bodyLen = msg.readableBytes();
     int headerLen = 4;
     out.ensureWritable(headerLen + bodyLen);
     out = out.order(ByteOrder.BIG_ENDIAN);
     if (bodyLen > 1024) {
       byte[] array = msg.array();
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       DeflaterOutputStream zipin = new DeflaterOutputStream(bos);
       zipin.write(array);
       zipin.finish();
       byte[] ar = bos.toByteArray();
       out.writeInt(ar.length);
       out.writeByte(1);
       out.writeBytes(ar);
     } else {
       out.writeInt(bodyLen);
       out.writeByte(0);
       msg.markReaderIndex();
       out.writeBytes(msg);
       msg.resetReaderIndex();
     }
   }
 }
