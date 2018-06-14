 package org.darcy.sanguo.exchange;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class ExchangeTemplate
 {
   public static final int COST_TYPE_FIX_PRICE = 1;
   public static final int COST_TYPE_CHANGE_PRICE = 2;
   public static final int SHOW_TYPE_WORLDCOMPETITION = 1;
   public static final int SHOW_TYPE_ARENA = 2;
   public static final int SHOW_TYPE_SHOP = 3;
   public int id;
   public Reward reward;
   public int countType;
   public int count;
   public int costType;
   public List<Reward> costs = new ArrayList();
   public int showType;
   public long start;
   public long end;
 
   public boolean isOnline()
   {
     long now = System.currentTimeMillis();
 
     return ((now < this.start) || (now > this.end));
   }
 
   public List<Reward> getCost(int buyCount, int curCount)
   {
     int i;
     List list = new ArrayList();
     if (this.costType == 1) {
       for (i = 0; i < curCount; ++i)
         list.add(((Reward)this.costs.get(0)).copy());
     }
     else {
       for (i = 0; i < curCount; ++i) {
         int index = i + buyCount;
         if (index >= this.costs.size())
           list.add(((Reward)this.costs.get(this.costs.size() - 1)).copy());
         else {
           list.add(((Reward)this.costs.get(index)).copy());
         }
       }
     }
     return list;
   }
 }
