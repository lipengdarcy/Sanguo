 package org.darcy.sanguo.drop;
 
 public class JewelDrop extends AbstractDrop
 {
   int value;
 
   public JewelDrop(int value, float rate)
   {
     this.type = 3;
     this.value = value;
     this.rate = rate;
   }
 }
