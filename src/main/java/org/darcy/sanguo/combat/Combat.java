package org.darcy.sanguo.combat;

public abstract class Combat
{
  protected abstract boolean isFinished();

  protected abstract void processOver();

  public abstract void init();

  public abstract void combat();

  public abstract void next();
}
