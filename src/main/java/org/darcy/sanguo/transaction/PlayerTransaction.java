 package org.darcy.sanguo.transaction;
 
 import java.util.LinkedList;
import java.util.List;

import org.darcy.sanguo.player.Player;
 
 public class PlayerTransaction
   implements Transaction
 {
   public Player player;
   public String cause;
   List<TransactionEntity> entities = new LinkedList();
 
   public PlayerTransaction(Player player, String cause) {
     this.player = player;
     this.cause = cause;
   }
 
   public void internalCommint() {
     for (TransactionEntity tx : this.entities)
       tx.commit();
   }
 
   public void internalRollback()
   {
     for (TransactionEntity tx : this.entities)
       tx.rollback();
   }
 
   public void commit()
   {
   }
 
   public void rollback()
   {
   }
 
   public void addTransactionEntity(TransactionEntity entity)
   {
     this.entities.add(entity);
   }
 }
