 package org.darcy.sanguo.drop;
 
 public class ExpDrop extends AbstractDrop
 {
   int value;
 
   public ExpDrop(int value, float rate)
   {
     this.type = 4;
     this.value = value;
     this.rate = rate;
   }
 }
