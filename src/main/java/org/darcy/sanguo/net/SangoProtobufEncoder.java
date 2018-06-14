 package org.darcy.sanguo.net;
 
 import java.util.List;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
 
 @ChannelHandler.Sharable
 public class SangoProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder>
 {
   protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder msg, List<Object> out)
     throws Exception
   {
     if (msg instanceof MessageLite) {
       out.add(Unpooled.wrappedBuffer(((MessageLite)msg).toByteArray()));
       return;
     }
     if (msg instanceof MessageLite.Builder)
       out.add(Unpooled.wrappedBuffer(((MessageLite.Builder)msg).build().toByteArray()));
   }
 }
