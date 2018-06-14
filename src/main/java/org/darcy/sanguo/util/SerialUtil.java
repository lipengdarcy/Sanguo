 package org.darcy.sanguo.util;
 
 import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
 
 public class SerialUtil
 {
   public static byte[] serialize(Serializable obj)
   {
     try
     {
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       ObjectOutputStream oos = new ObjectOutputStream(bos);
       oos.writeObject(obj);
       return bos.toByteArray();
     } catch (Exception e) {
       e.printStackTrace(); }
     return null;
   }
 
   public static Object deSerialize(byte[] bytes)
   {
     try {
       ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
       ObjectInputStream ois = new ObjectInputStream(bis);
       return ois.readObject();
     } catch (Exception e) {
       e.printStackTrace(); }
     return null;
   }
 }
