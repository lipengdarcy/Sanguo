 package org.darcy.sanguo.activity;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
 
 public class MonthCardInfo
 {
   public String channel;
   public int goodsId;
   public int surplus;
   public long lastGetTime;
 
   public void reward(long time)
   {
     this.surplus -= 1;
     this.lastGetTime = time;
   }
 
   public static MonthCardInfo readObject(ObjectInputStream in) {
     try {
       MonthCardInfo info = new MonthCardInfo();
       info.goodsId = in.readInt();
       info.surplus = in.readInt();
       info.lastGetTime = in.readLong();
       int length = in.readInt();
       byte[] bytes = new byte[length];
       in.readFully(bytes);
       info.channel = new String(bytes, Charset.forName("utf-8"));
       return info;
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     return null;
   }
 
   public void writeObject(ObjectOutputStream out) throws IOException {
     out.writeInt(this.goodsId);
     out.writeInt(this.surplus);
     out.writeLong(this.lastGetTime);
     out.writeInt(this.channel.getBytes(Charset.forName("utf-8")).length);
     out.write(this.channel.getBytes(Charset.forName("utf-8")));
   }
 }
