 package org.darcy.sanguo.item.itemeffect;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class EffectResult
 {
   public int result;
   public int notifyType;
   public List<Reward> rewards;
 
   public EffectResult(int result, int notifyType)
   {
     this.result = result;
     this.notifyType = notifyType;
   }
 
   public EffectResult(int result, int notifyType, List<Reward> list) {
     this.result = result;
     this.notifyType = notifyType;
     this.rewards = new ArrayList();
     if ((list != null) && (list.size() > 0))
       this.rewards.addAll(list);
   }
 
   public EffectResult(int result, int notifyType, Reward[] list)
   {
     this.result = result;
     this.notifyType = notifyType;
     this.rewards = new ArrayList();
     if ((list != null) && (list.length > 0))
       for (Reward reward : list)
         this.rewards.add(reward);
   }
 }
