package org.darcy.sanguo.transaction;

public abstract interface TransactionEntity
{
  public abstract void commit();

  public abstract void rollback();
}
