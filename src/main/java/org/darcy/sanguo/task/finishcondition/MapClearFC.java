 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.map.ClearMap;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class MapClearFC extends AbstractFCondition
 {
   int mapId;
 
   public MapClearFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     MapClearFC fc = new MapClearFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2008 };
   }
 
   public void initParams(String[] params)
   {
     this.mapId = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     ClearMap cm = player.getMapRecord().getClearMap(this.mapId);
     if (cm == null) {
       return false;
     }
 
     return (!(cm.isFinished()));
   }
 }
