 package org.darcy.sanguo.utils;
 
 import java.security.MessageDigest;
 
 public class MD5
 {
   public static final String MD5(String s)
   {
     char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
     try {
       byte[] strTemp = s.getBytes();
 
       MessageDigest mdTemp = MessageDigest.getInstance("MD5");
       mdTemp.update(strTemp);
       byte[] md = mdTemp.digest();
       int j = md.length;
       char[] str = new char[j * 2];
       int k = 0;
       for (int i = 0; i < j; ++i) {
         byte b = md[i];
 
         str[(k++)] = hexDigits[(b >> 4 & 0xF)];
         str[(k++)] = hexDigits[(b & 0xF)];
       }
       return new String(str); } catch (Exception e) {
     }
     return null;
   }
 
   public static void main(String[] args)
   {
     System.out.println(MD5("{\"adCode\":\"adid_can_not_get\",\"channelID\":\"3333333333333333\",\"funcNo\":\"7\",\"gameID\":\"2222222222222222\",\"gameServer\":\"1区\",\"imei\":\"\",\"mac\":\"A23F0F2D-469B-4B50-B005-7CD3609A2520\",\"sysType\":\"2\",\"uId\":\"ydz111\",\"version\":\"4\"}"));
   }
 }
