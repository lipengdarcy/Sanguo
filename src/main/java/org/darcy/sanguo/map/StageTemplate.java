 package org.darcy.sanguo.map;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class StageTemplate
 {
   public int id;
   public int preId;
   public int nextId;
   public String name;
   public int type;
   public int secenId;
   public int icon;
   public int maxChanllengeTimes;
   public int vitalityCost;
   public int rentou;
   public List<Reward> preViewRewards = new ArrayList();
 
   public StageChannel[] channels = new StageChannel[3];
 
   private int maxStars = 0;
 
   public void setPreViewRewards(String rewards) {
     if (rewards.equals("-1")) return;
     String[] ls = rewards.split(",");
     for (String s : ls) {
       Reward r = new Reward(s);
       this.preViewRewards.add(r);
     }
   }
 
   public int getMaxStars() {
     if (this.maxStars == 0) {
       for (StageChannel s : this.channels) {
         if (s != null) {
           this.maxStars += 1;
         }
       }
     }
     return this.maxStars;
   }
 }
