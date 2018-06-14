 package org.darcy.sanguo.transaction;
 
 public class IllegalValueException extends TransactionException
 {
   public IllegalValueException()
   {
   }
 
   public IllegalValueException(String message)
   {
     super(message);
   }
 }
