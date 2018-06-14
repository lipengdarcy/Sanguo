 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class OnStageWarriorLevelFC extends AbstractFCondition
 {
   int gole;
 
   public OnStageWarriorLevelFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     OnStageWarriorLevelFC fc = new OnStageWarriorLevelFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2023, 
       2033 };
   }
 
   public void initParams(String[] params)
   {
     this.gole = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     for (Warrior w : player.getWarriors().getWarriors().values()) {
       if (w.getLevel() < this.gole) {
         return false;
       }
     }
     return true;
   }
 }
