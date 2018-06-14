 package org.darcy.sanguo.drop;
 
 public class MoneyDrop extends AbstractDrop
 {
   int value;
 
   public MoneyDrop(int value, float rate)
   {
     this.type = 2;
     this.value = value;
     this.rate = rate;
   }
 }
