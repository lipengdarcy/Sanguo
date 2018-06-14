 package org.darcy.sanguo.arena;
 
 public class ArenaSaveCall
   implements Runnable
 {
   Arena arena;
 
   public ArenaSaveCall(Arena arena)
   {
     this.arena = arena;
   }
 
   public void run()
   {
     this.arena.save();
   }
 }
