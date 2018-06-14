 package org.darcy.sanguo.mail;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class Attachment
   implements Serializable
 {
   private static final long serialVersionUID = -3600292135396926487L;
   private int version = 1;
   private List<Reward> rewards;
 
   public List<Reward> getRewards()
   {
     return this.rewards;
   }
 
   public void setRewards(List<Reward> rewards) {
     this.rewards = rewards;
   }
 
   private void readObject(ObjectInputStream in) {
     try {
       in.readInt();
       int size = in.readInt();
       this.rewards = new ArrayList(size);
       for (int i = 0; i < size; ++i) {
         Reward r = Reward.readObject(in);
         this.rewards.add(r);
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   private void writeObject(ObjectOutputStream out)
     throws IOException
   {
     out.writeInt(this.version);
     int size = this.rewards.size();
     out.writeInt(size);
     for (Reward r : this.rewards)
       r.writeObject(out);
   }
 }
