 package org.darcy.sanguo.persist;
 
 public class DataAccessException extends RuntimeException
 {
   public DataAccessException(Throwable cause)
   {
     super(cause);
   }
 
   public DataAccessException(String message) {
     super(message);
   }
 }
