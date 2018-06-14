 package org.darcy.sanguo.drop;
 
 public class SubDrop extends AbstractDrop
 {
   public int groupId;
 
   public SubDrop(int groupId, float rate)
   {
     this.groupId = groupId;
     this.rate = rate;
     this.type = 1;
   }
 }
