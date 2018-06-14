 package org.darcy.sanguo.loottreasure;
 
 import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.darcy.sanguo.drop.Reward;
 
 public class LootTreasureData
 {
   public static final int LOOT_TARGET_PICK_RANGE = 50;
   public static final int LOOT_TARGET_PLAYER_NUM = 3;
   public static final int LOOT_TARGET_NUM = 4;
   public static final int LOOT_TARGET_LEVEL_OFFSET = 5;
   public static int costStamina;
   public static int[] baseTreasure;
   public static Reward shieldItem;
   public static Reward shieldJewel;
   public static int shieldTime;
   public static int maxShieldTime;
   public static int winMoneyRatio;
   public static int winExpRatio;
   public static int loseMoneyRatio;
   public static int loseExpRatio;
   public static int[] notLootPlayer;
   public static Map<Integer, LootDrop> drops = new HashMap();
 
   public static int[] startTime = new int[3];
 
   public static int[] endTime = { 10 };
 
   public static boolean canLoot()
   {
     Calendar now = Calendar.getInstance();
 
     return ((afterStart(now)) && (beforeEnd(now)));
   }
 
   private static boolean afterStart(Calendar cal)
   {
     int hour = cal.get(11);
     if (hour > startTime[0])
       return true;
     if (hour < startTime[0]) {
       return false;
     }
     int min = cal.get(12);
     if (min > startTime[1])
       return true;
     if (min < startTime[1]) {
       return false;
     }
     int sec = cal.get(13);
 
     return (sec <= startTime[2]);
   }
 
   private static boolean beforeEnd(Calendar cal)
   {
     int hour = cal.get(11);
     if (hour < endTime[0])
       return true;
     if (hour > endTime[0]) {
       return false;
     }
     int min = cal.get(12);
     if (min < endTime[1])
       return true;
     if (min > endTime[1]) {
       return false;
     }
     int sec = cal.get(13);
 
     return (sec > endTime[2]);
   }
 
   public static boolean isDebrisOwn(int templateId)
   {
     for (int i = 0; i < notLootPlayer.length; ++i) {
       int id = notLootPlayer[i];
       if (id == templateId) {
         return false;
       }
     }
     return true;
   }
 
   public static class LootDrop
   {
     public int id;
     public int ratio;
     public String desc;
 
     public LootDrop(int id, int ratio, String desc)
     {
       this.id = id;
       this.ratio = ratio;
       this.desc = desc;
     }
   }
 }
