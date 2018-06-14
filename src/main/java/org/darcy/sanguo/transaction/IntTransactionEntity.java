 package org.darcy.sanguo.transaction;
 
 public class IntTransactionEntity
   implements TransactionEntity
 {
   public PlayerTransaction tx;
   public AbstractIntProperty property;
   public int value;
 
   public IntTransactionEntity(PlayerTransaction tx, AbstractIntProperty pro, int value)
   {
     this.tx = tx;
     this.property = pro;
     this.value = value;
   }
 
   public synchronized void commit()
   {
     this.property.release(this, true, this.tx.cause);
   }
 
   public void rollback()
   {
     this.property.release(this, false, this.tx.cause);
   }
 }
