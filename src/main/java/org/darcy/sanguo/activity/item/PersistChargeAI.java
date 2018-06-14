 package org.darcy.sanguo.activity.item;
 
 import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.drop.Reward;
 
 public class PersistChargeAI extends AbstractActivityItem
 {
   public Map<Integer, List<Reward>> rewards = new HashMap();
 
   public void addRewards(int day, List<Reward> list) {
     if (!(this.rewards.containsKey(Integer.valueOf(day))))
       this.rewards.put(Integer.valueOf(day), list);
   }
 
   public int getActivityId()
   {
     return 13;
   }
 
   public boolean containsKey(int key)
   {
     return this.rewards.containsKey(Integer.valueOf(key));
   }
 
   public Set<Integer> keySet()
   {
     return this.rewards.keySet();
   }
 }
