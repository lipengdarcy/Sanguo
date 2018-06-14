 package org.darcy.sanguo.event;
 
 public class Event
 {
   public int type;
   public Object[] params;
 
   public Event(int type)
   {
     this.type = type;
   }
 
   public Event(int type, Object[] params) {
     this.type = type;
     this.params = params;
   }
 }
