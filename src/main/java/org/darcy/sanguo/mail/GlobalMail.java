 package org.darcy.sanguo.mail;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class GlobalMail
   implements Serializable
 {
   private static final long serialVersionUID = 928908329676435410L;
   private int version = 1;
   public static final int LOGIN = 1;
   public static final int REGIST = 2;
   private long id;
   private int type;
   private long start;
   private long end;
   private int minLevel;
   private int maxLevel;
   private int mailType;
   private String title;
   private String content;
   private List<Reward> rewards;
 
   public GlobalMail()
   {
     this.id = System.currentTimeMillis();
   }
 
   public long getId()
   {
     return this.id; }
 
   public void setId(long id) {
     this.id = id; }
 
   public int getType() {
     return this.type; }
 
   public void setType(int type) {
     this.type = type; }
 
   public long getStart() {
     return this.start; }
 
   public void setStart(long start) {
     this.start = start; }
 
   public long getEnd() {
     return this.end; }
 
   public void setEnd(long end) {
     this.end = end; }
 
   public int getMinLevel() {
     return this.minLevel; }
 
   public void setMinLevel(int minLevel) {
     this.minLevel = minLevel; }
 
   public int getMaxLevel() {
     return this.maxLevel; }
 
   public void setMaxLevel(int maxLevel) {
     this.maxLevel = maxLevel; }
 
   public int getMailType() {
     return this.mailType; }
 
   public void setMailType(int mailType) {
     this.mailType = mailType; }
 
   public String getTitle() {
     return this.title; }
 
   public void setTitle(String title) {
     this.title = title; }
 
   public String getContent() {
     return this.content; }
 
   public void setContent(String content) {
     this.content = content; }
 
   public List<Reward> getRewards() {
     return this.rewards; }
 
   public void setRewards(List<Reward> rewards) {
     this.rewards = rewards;
   }
 
   public String toString()
   {
     return "GlobalMail [id=" + this.id + ", type=" + this.type + ", start=" + new Date(this.start) + ", end=" + new Date(this.end) + "]";
   }
 
   private void readObject(ObjectInputStream in)
   {
     try {
       in.readInt();
       this.id = in.readLong();
       this.type = in.readInt();
       this.start = in.readLong();
       this.end = in.readLong();
       this.minLevel = in.readInt();
       this.maxLevel = in.readInt();
       this.mailType = in.readInt();
 
       int length = in.readInt();
       byte[] bytes = new byte[length];
       in.readFully(bytes);
       this.title = new String(bytes, Charset.forName("utf-8"));
 
       length = in.readInt();
       bytes = new byte[length];
       in.readFully(bytes);
       this.content = new String(bytes, Charset.forName("utf-8"));
 
       int size = in.readInt();
       this.rewards = new ArrayList(size);
       for (int i = 0; i < size; ++i) {
         Reward r = Reward.readObject(in);
         if (r != null)
           this.rewards.add(r);
       }
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   private void writeObject(ObjectOutputStream out)
     throws IOException
   {
     out.writeInt(this.version);
 
     out.writeLong(this.id);
     out.writeInt(this.type);
     out.writeLong(this.start);
     out.writeLong(this.end);
     out.writeInt(this.minLevel);
     out.writeInt(this.maxLevel);
     out.writeInt(this.mailType);
 
     out.writeInt(this.title.getBytes(Charset.forName("utf-8")).length);
     out.write(this.title.getBytes(Charset.forName("utf-8")));
 
     out.writeInt(this.content.getBytes(Charset.forName("utf-8")).length);
     out.write(this.content.getBytes(Charset.forName("utf-8")));
 
     int size = this.rewards.size();
     out.writeInt(size);
     for (Reward r : this.rewards)
       r.writeObject(out);
   }
 }
