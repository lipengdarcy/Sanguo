 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class PlayerLevelFC extends AbstractFCondition
 {
   int level;
 
   public PlayerLevelFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     PlayerLevelFC fc = new PlayerLevelFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2028 };
   }
 
   public void initParams(String[] params)
   {
     this.level = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     return (player.getLevel() < this.level);
   }
 }
