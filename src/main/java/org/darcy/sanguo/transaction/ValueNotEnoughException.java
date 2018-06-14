 package org.darcy.sanguo.transaction;
 
 public class ValueNotEnoughException extends TransactionException
 {
   public ValueNotEnoughException(String message)
   {
     super(message);
   }
 }
