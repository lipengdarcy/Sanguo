 package org.darcy.sanguo.activity.item;
 
 public abstract class AbstractActivityItem
   implements ActivityItem
 {
   protected int roundId;
 
   public int getRoundId()
   {
     return this.roundId;
   }
 
   public void setRoundId(int roundId) {
     this.roundId = roundId;
   }
 }
