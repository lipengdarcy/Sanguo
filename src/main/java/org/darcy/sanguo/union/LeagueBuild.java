 package org.darcy.sanguo.union;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
 
 public class LeagueBuild
 {
   public static final int version = 1;
   private int playerId;
   private String name;
   private int buildId;
 
   public LeagueBuild(int id, String name, int buildId)
   {
     this.playerId = id;
     this.name = name;
     this.buildId = buildId;
   }
 
   public int getPlayerId() {
     return this.playerId;
   }
 
   public void setPlayerId(int playerId) {
     this.playerId = playerId;
   }
 
   public String getName() {
     return this.name;
   }
 
   public void setName(String name) {
     this.name = name;
   }
 
   public int getBuildId() {
     return this.buildId;
   }
 
   public void setBuildId(int buildId) {
     this.buildId = buildId;
   }
 
   public String getNotice()
   {
     int value = 0;
     LeagueBuildData data = LeagueService.getBuildData(this.buildId);
     if (data != null) {
       value = data.buildValue;
     }
     String str = MessageFormat.format("<p style=21>【{0}】成功使用了</p><p style=19>【{1}】</p><p style=21>军团建设！</p>", new Object[] { this.name, Integer.valueOf(value) });
     return str;
   }
 
   public static LeagueBuild readObject(ObjectInputStream in) {
     try {
       in.readInt();
       int id = in.readInt();
 
       int length = in.readInt();
       byte[] bytes = new byte[length];
       in.readFully(bytes);
       String name = new String(bytes, Charset.forName("utf-8"));
 
       int buildId = in.readInt();
 
       LeagueBuild lb = new LeagueBuild(id, name, buildId);
       return lb;
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     return null;
   }
 
   public void writeObject(ObjectOutputStream out) throws IOException {
     out.writeInt(1);
     out.writeInt(this.playerId);
 
     out.writeInt(this.name.getBytes(Charset.forName("utf-8")).length);
     out.write(this.name.getBytes(Charset.forName("utf-8")));
 
     out.writeInt(this.buildId);
   }
 }
