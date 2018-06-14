 package org.darcy.sanguo.task;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
 
 public class TaskVarStore
 {
   public int id;
   public int[] vars = new int[0];
 
   public static TaskVarStore readObject(ObjectInputStream in) {
     try {
       TaskVarStore store = new TaskVarStore();
       store.id = in.readInt();
       int size = in.readInt();
       store.vars = new int[size];
       for (int i = 0; i < size; ++i) {
         store.vars[i] = in.readInt();
       }
       return store;
     } catch (IOException e) {
       e.printStackTrace();
     }
     return null;
   }
 
   public void writeObject(ObjectOutputStream out) throws IOException
   {
     out.writeInt(this.id);
     out.writeInt(this.vars.length);
     for (int i = 0; i < this.vars.length; ++i)
       out.writeInt(this.vars[i]);
   }
 }
