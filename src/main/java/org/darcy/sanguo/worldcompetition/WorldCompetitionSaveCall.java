 package org.darcy.sanguo.worldcompetition;
 
 import java.util.List;
 
 public class WorldCompetitionSaveCall
   implements Runnable
 {
   List<WorldCompetition> list;
 
   public WorldCompetitionSaveCall(List<WorldCompetition> list)
   {
     this.list = list;
   }
 
   public void run()
   {
     for (WorldCompetition wc : this.list)
       wc.save();
   }
 }
