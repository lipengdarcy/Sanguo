 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class StarLevelFC extends AbstractFCondition
 {
   int num;
 
   public StarLevelFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     StarLevelFC fc = new StarLevelFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2030 };
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     return (player.getStarRecord().getTotalLevel() < this.num);
   }
 
   public int[] getProcess(Player player, Task task)
   {
     int[] process = new int[2];
     process[1] = this.num;
     int count = player.getStarRecord().getTotalLevel();
     process[0] = Math.min(this.num, count);
     return process;
   }
 }
