 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class MapProClearFC extends AbstractFCondition
 {
   int mapId;
 
   public MapProClearFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     MapProClearFC fc = new MapProClearFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2009 };
   }
 
   public void initParams(String[] params)
   {
     this.mapId = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     return player.getMapRecord().hasClearProMap(this.mapId);
   }
 }
