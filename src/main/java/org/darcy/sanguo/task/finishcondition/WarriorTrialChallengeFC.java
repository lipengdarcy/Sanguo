 package org.darcy.sanguo.task.finishcondition;
 
 import java.util.Calendar;

import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.map.MapRecord;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public class WarriorTrialChallengeFC extends AbstractFCondition
 {
   int gole;
 
   public WarriorTrialChallengeFC(int paramCount)
   {
     super(paramCount);
   }
 
   public FinishCondition copy()
   {
     WarriorTrialChallengeFC fc = new WarriorTrialChallengeFC(this.paramCount);
     super.copy(fc);
     return fc;
   }
 
   public void registerEvent()
   {
     this.events = new int[] { 
       2011 };
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
     if (event.type == 2011) {
       int value = player.getTaskRecord().getVar(task, 0);
       ++value;
       if (value > this.gole) {
         value = this.gole;
       }
       player.getTaskRecord().setVar(task, 0, value);
       super.processEvent(player, event, task);
     }
   }
 
   public boolean canAccept()
   {
     Calendar cal = Calendar.getInstance();
     int day = cal.get(7);
     for (int tmp : MapRecord.warriorMapRunDays) {
       if (day == tmp) {
         return true;
       }
     }
     return false;
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
