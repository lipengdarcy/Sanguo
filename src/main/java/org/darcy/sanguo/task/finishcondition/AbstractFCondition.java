 package org.darcy.sanguo.task.finishcondition;
 
 import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;
 
 public abstract class AbstractFCondition
   implements FinishCondition
 {
   public int[] events = new int[0];
   protected int paramCount;
 
   public AbstractFCondition(int paramCount)
   {
     this.paramCount = paramCount;
   }
 
   public int[] getRegisterEvent()
   {
     return this.events;
   }
 
   public int getParamCount()
   {
     return this.paramCount;
   }
 
   public void copy(AbstractFCondition fc) {
     int[] es = new int[this.events.length];
     for (int i = 0; i < es.length; ++i) {
       es[i] = this.events[i];
     }
     fc.events = es;
   }
 
   public void processEvent(Player player, Event event, Task task)
   {
     player.getTaskRecord().update(player, task.getId());
   }
 
   public boolean canAccept()
   {
     return true;
   }
 
   public int[] getProcess(Player player, Task task)
   {
     int[] process = new int[2];
     process[1] = 1;
     if (isFinish(player, task))
       process[0] = 1;
     else {
       process[0] = 0;
     }
     return process;
   }
 
   public void initVar(Player player, Task task)
   {
   }
 }
