 package org.darcy.sanguo.drop;
 
 public class DigitalDrop extends AbstractDrop
 {
   int value;
 
   public DigitalDrop(int type, int value, float rate)
   {
     this.type = type;
     this.value = value;
     this.rate = rate;
   }
 }
