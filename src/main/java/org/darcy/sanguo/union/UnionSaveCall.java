 package org.darcy.sanguo.union;
 
 public class UnionSaveCall
   implements Runnable
 {
   private Union u;
 
   public UnionSaveCall(Union u)
   {
     this.u = u;
   }
 
   public void run()
   {
     this.u.save();
   }
 }
