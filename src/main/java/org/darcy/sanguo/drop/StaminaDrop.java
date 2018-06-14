 package org.darcy.sanguo.drop;
 
 public class StaminaDrop extends AbstractDrop
 {
   int value;
 
   public StaminaDrop(int value, float rate)
   {
     this.type = 6;
     this.value = value;
     this.rate = rate;
   }
 }
