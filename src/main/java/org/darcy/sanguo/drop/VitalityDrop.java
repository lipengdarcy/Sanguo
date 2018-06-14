 package org.darcy.sanguo.drop;
 
 public class VitalityDrop extends AbstractDrop
 {
   int value;
 
   public VitalityDrop(int value, float rate)
   {
     this.type = 5;
     this.value = value;
     this.rate = rate;
   }
 }
