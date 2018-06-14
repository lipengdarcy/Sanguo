 package org.darcy.sanguo.activity.item;
 
 import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.drop.Reward;
 
 public class ChargeRewardAI extends AbstractActivityItem
 {
   public Map<Integer, List<Reward>> rewards = new HashMap();
 
   public void addRewards(int count, List<Reward> list) {
     if (!(this.rewards.containsKey(Integer.valueOf(count))))
       this.rewards.put(Integer.valueOf(count), list);
   }
 
   public int getActivityId()
   {
     return 3;
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
