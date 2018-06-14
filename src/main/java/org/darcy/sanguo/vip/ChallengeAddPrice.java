 package org.darcy.sanguo.vip;
 
 import java.util.HashMap;
import java.util.Map;
 
 public class ChallengeAddPrice
 {
   public static Map<Integer, int[]> prices = new HashMap();
 
   public static int getPrice(int id, int count) {
     if (count < 1) {
       return 0;
     }
     int[] price = (int[])prices.get(Integer.valueOf(id));
     if (price == null) {
       return 0;
     }
     if (count >= price.length) {
       return price[(price.length - 1)];
     }
     return price[(count - 1)];
   }
 }
