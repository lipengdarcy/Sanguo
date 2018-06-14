 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class RecruitBestNotFreeFC extends AbstractFCondition
 {
   int gole;
 
   public RecruitBestNotFreeFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     RecruitBestNotFreeFC fc = new RecruitBestNotFreeFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2029 };
   }
 
   public void initParams(String[] params)
   {
     this.gole = Integer.valueOf(params[0]).intValue();
   }
 
   public boolean isFinish(Player player, Task task)
   {
     return (player.getTaskRecord().getVar(task, 0) < this.gole);
   }
 
   public void processEvent(Player player, Event event, Task task)
   {
     if (event.type == 2029) {
       int value = player.getTaskRecord().getVar(task, 0);
       value += ((Integer)event.params[1]).intValue();
       if (value > this.gole) {
         value = this.gole;
       }
       player.getTaskRecord().setVar(task, 0, value);
       super.processEvent(player, event, task);
     }
   }
 
   public int[] getProcess(Player player, Task task)
   {
     int[] process = new int[2];
     process[1] = this.gole;
     process[0] = player.getTaskRecord().getVar(task, 0);
     return process;
   }
 
   public void initVar(Player player, Task task)
   {
     player.getTaskRecord().setVar(task, 0, 0);
   }
 }
