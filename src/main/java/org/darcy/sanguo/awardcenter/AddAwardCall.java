 package org.darcy.sanguo.awardcenter;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;
 
 public class AddAwardCall
   implements Runnable
 {
   int playerId;
   Award award;
 
   public AddAwardCall(int playerId, Award award)
   {
     this.playerId = playerId;
     this.award = award;
   }
 
   public void run()
   {
     try
     {
       Awards awards = (Awards)((DbService)Platform.getServiceManager().get(DbService.class)).get(Awards.class, Integer.valueOf(this.playerId));
       if (awards != null) {
         awards.addAward(this.award, this.playerId);
         awards.save();
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 }
