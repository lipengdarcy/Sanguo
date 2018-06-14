 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class DivineNumDayFC extends AbstractFCondition
 {
   int num;
 
   public DivineNumDayFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     DivineNumDayFC fc = new DivineNumDayFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2031 };
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     return (player.getDivineRecord().getTotalScores() < this.num);
   }
 
   public int[] getProcess(Player player, Task task)
   {
     int[] process = new int[2];
     process[1] = this.num;
     process[0] = Math.min(this.num, player.getDivineRecord().getTotalScores());
     return process;
   }
 }
