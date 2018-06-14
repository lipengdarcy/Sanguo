package org.darcy.sanguo.task.finishcondition;

import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;

public abstract interface FinishCondition
{
  public abstract int[] getRegisterEvent();

  public abstract int getParamCount();

  public abstract FinishCondition copy();

  public abstract void registerEvent();

  public abstract void initParams(String[] paramArrayOfString);

  public abstract boolean isFinish(Player paramPlayer, Task paramTask);

  public abstract void processEvent(Player paramPlayer, Event paramEvent, Task paramTask);

  public abstract boolean canAccept();

  public abstract int[] getProcess(Player paramPlayer, Task paramTask);

  public abstract void initVar(Player paramPlayer, Task paramTask);
}
