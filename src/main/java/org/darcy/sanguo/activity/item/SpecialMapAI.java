 package org.darcy.sanguo.activity.item;
 
 import java.util.Set;
 
 public class SpecialMapAI extends AbstractActivityItem
 {
   public int count;
   public int type;
 
   public int getActivityId()
   {
     return this.type;
   }
 
   public boolean containsKey(int key)
   {
     return true;
   }
 
   public Set<Integer> keySet()
   {
     return null;
   }
 }
