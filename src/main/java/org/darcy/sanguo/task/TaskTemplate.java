 package org.darcy.sanguo.task;
 
 import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.task.finishcondition.FinishCondition;
 
 public class TaskTemplate
 {
   public int id;
   public int type;
   public String name;
   public String content;
   public int minLevel;
   public int maxLevel;
   public int acceptActivityId;
   public int preId;
   public int[] nextIds;
   private List<Reward> rewards = new ArrayList();
   public int rewardActivityId;
   private List<Reward> activityRewards = new ArrayList();
   public int guideType;
   public FinishCondition condition;
   public Set<Integer> events = new HashSet();
 
   public void init() {
     if (this.condition != null) {
       int[] events = this.condition.getRegisterEvent();
       for (int i : events)
         this.events.add(Integer.valueOf(i));
     }
   }
 
   public List<Reward> getRewards()
   {
     if ((this.rewardActivityId != -1) && 
       (ActivityInfo.isOpenActivity(this.rewardActivityId))) {
       return this.activityRewards;
     }
     return this.rewards;
   }
 
   public void setRewards(List<Reward> rewards) {
     this.rewards = rewards;
   }
 
   public void setActivityRewards(List<Reward> activityRewards) {
     this.activityRewards = activityRewards;
   }
 }
