 package org.darcy.sanguo.top;
 
 import org.darcy.ServerStartup;
import org.darcy.sanguo.Platform;
 
 public class AsyncRanker
   implements Runnable
 {
   public void run()
   {
     while (ServerStartup.running)
       try {
         Thread.sleep(5000L);
         try {
           Platform.getTopManager().refreshBTLRank();
         } catch (Exception e) {
           e.printStackTrace();
         }
         try {
           Platform.getTopManager().refreshLevelRank();
         } catch (Exception e) {
           e.printStackTrace();
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
   }
 }
