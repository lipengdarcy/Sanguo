 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class TowerClearFC extends AbstractFCondition
 {
   int big;
   int small;
 
   public TowerClearFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     TowerClearFC fc = new TowerClearFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2020 };
   }
 
   public void initParams(String[] params)
   {
     this.big = Integer.valueOf(params[0]).intValue();
     this.small = Integer.valueOf(params[1]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     int maxLevel = player.getTowerRecord().getMaxLevel();
     int b = (maxLevel - 1) / 5 + 1;
     int s = (maxLevel - 1) % 5 + 1;
     if (b > this.big)
       return true;
     if (b < this.big) {
       return false;
     }
     return (s < this.small);
   }
 }
