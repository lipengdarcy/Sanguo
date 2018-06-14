 package org.darcy.sanguo.map;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class MapTemplate
 {
   public int id;
   public int preId;
   public int nextId;
   public String name;
   public int iconId;
   public int mapImage;
   public int resourceId;
   public int openLevel;
   public StageTemplate[] stageTemplates;
   public int[] startNeeds = new int[3];
 
   public List<Reward>[] starRewards = new ArrayList[3];
 
   public int[] dropIds = new int[3];
   public int type;
   private int maxStars = 0;
 
   public void setStarRewards(int index, String rewards)
   {
     if (!(rewards.equals("-1"))) {
       String[] ls = rewards.split(",");
       List list = new ArrayList(ls.length);
       for (String s : ls) {
         Reward r = new Reward(s);
         list.add(r);
       }
       this.starRewards[index] = list;
     }
   }
 
   public int getMaxStars()
   {
     if (this.maxStars == 0) {
       for (StageTemplate s : this.stageTemplates) {
         if (s != null) {
           this.maxStars += s.getMaxStars();
         }
       }
     }
     return this.maxStars;
   }
 }
