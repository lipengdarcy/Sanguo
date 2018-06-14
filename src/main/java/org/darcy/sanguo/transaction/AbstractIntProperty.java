 package org.darcy.sanguo.transaction;
 
 public abstract class AbstractIntProperty
 {
   public int wantAddValue;
   public int wantDecValue;
 
   public abstract int getValue();
 
   public abstract int getMaxValue();
 
   public abstract void modifyValue(int paramInt, String paramString);
 
   public synchronized void add(int addValue, PlayerTransaction tx)
   {
     if (addValue < 0) {
       throw new IllegalArgumentException();
     }
     if (getValue() + this.wantAddValue - this.wantDecValue + addValue < 0) {
       throw new IllegalArgumentException();
     }
     this.wantAddValue += addValue;
     IntTransactionEntity entity = new IntTransactionEntity(tx, this, addValue);
     tx.addTransactionEntity(entity);
   }
 
   public synchronized void dec(int decValue, PlayerTransaction tx) throws ValueNotEnoughException {
     if (decValue < 0) {
       throw new IllegalArgumentException();
     }
     if (getValue() + this.wantAddValue - this.wantDecValue - decValue < 0) {
       throw new ValueNotEnoughException("");
     }
     this.wantDecValue += decValue;
     IntTransactionEntity entity = new IntTransactionEntity(tx, this, -decValue);
     tx.addTransactionEntity(entity);
   }
 
   public synchronized void release(IntTransactionEntity entity, boolean commit, String cause) {
     if (commit) {
       if (entity.value > 0) {
         modifyValue(entity.value, cause);
         this.wantAddValue -= entity.value;
       } else {
         modifyValue(entity.value, cause);
         this.wantDecValue += entity.value;
       }
     }
     else if (entity.value > 0)
       this.wantAddValue -= entity.value;
     else
       this.wantDecValue += entity.value;
   }
 }
