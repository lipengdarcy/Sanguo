package org.darcy.sanguo.transaction;

public abstract interface Transaction
{
  public abstract void commit()
    throws TransactionException;

  public abstract void rollback()
    throws TransactionException;
}
