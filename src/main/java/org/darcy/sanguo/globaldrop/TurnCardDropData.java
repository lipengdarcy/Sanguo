 package org.darcy.sanguo.globaldrop;
 
 import java.util.HashMap;
import java.util.Map;
 
 public class TurnCardDropData
 {
   public static int showDrop;
   public static int[] level;
   public static int[] normalDrop;
   public static Map<Integer, Integer> besureDrop = new HashMap();
   public static int lootMoneyPercent;
   public static int lootMoneyRatio;
   public static int lootMoneyMax;
 
   public static int getNormalDrop(int playerLevel)
   {
     if (normalDrop.length > level.length) {
       for (int i = 0; i < level.length; ++i) {
         if (playerLevel <= level[i]) {
           return normalDrop[i];
         }
       }
     }
     return normalDrop[(normalDrop.length - 1)];
   }
 }
