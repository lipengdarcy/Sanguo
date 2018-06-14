 package org.darcy.sanguo.drop;
 
 public class ItemDrop extends AbstractDrop
 {
   public int itemId;
   public int count;
 
   public ItemDrop(int itemId, int count, float rate)
   {
     this.type = 0;
     this.itemId = itemId;
     this.count = count;
     this.rate = rate;
   }
 }
