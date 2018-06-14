 package org.darcy.sanguo.arena;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
 
 public class RankInfo
 {
   public int id;
   public long time;
   public int rank;
 
   public RankInfo()
   {
   }
 
   public RankInfo(int id, int rank, long time)
   {
     this.id = id;
     this.rank = rank;
     this.time = time;
   }
 
   public void readObject(ObjectInputStream in) {
     try {
       this.id = in.readInt();
       this.rank = in.readInt();
       this.time = in.readLong();
     } catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   public void writeObject(ObjectOutputStream out) throws IOException {
     out.writeInt(this.id);
     out.writeInt(this.rank);
     out.writeLong(this.time);
   }
 }
