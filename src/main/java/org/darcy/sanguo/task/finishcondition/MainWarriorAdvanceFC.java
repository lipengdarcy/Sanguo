 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class MainWarriorAdvanceFC extends AbstractFCondition
 {
   int advanceLevel;
 
   public MainWarriorAdvanceFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     MainWarriorAdvanceFC fc = new MainWarriorAdvanceFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2021 };
   }
 
   public void initParams(String[] params)
   {
     this.advanceLevel = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     MainWarrior w = player.getWarriors().getMainWarrior();
 
     return (w.getAdvanceLevel() < this.advanceLevel);
   }
 }
