 package org.darcy.sanguo.union.combat;
 
 import org.darcy.sanguo.Platform;

import com.google.protobuf.GeneratedMessage;
 
 public class Send
   implements Runnable
 {
   int lid;
   int ptCode;
   GeneratedMessage msg;
 
   public Send(int lid, int ptCode, GeneratedMessage msg)
   {
     this.lid = lid;
     this.ptCode = ptCode;
     this.msg = msg;
   }
 
   public void run()
   {
     try
     {
       Thread.sleep(30000L);
       ((LeagueCombatService)Platform.getServiceManager().get(LeagueCombatService.class)).send2League(this.lid, this.ptCode, this.msg);
     } catch (InterruptedException e) {
       e.printStackTrace();
     }
   }
 }
