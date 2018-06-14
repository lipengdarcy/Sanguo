 package org.darcy.sanguo.hero;
 
 import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
 
 public class Formation
 {
   public static int[] openSort = new int[6];
 
   public static int[] openPositionLevel = new int[6];
   public static int leadPosition;
   public static Map<Integer, Integer> openIndexByLevel = new HashMap();
 
   public static Map<Integer, Integer> openFriendByLevel = new HashMap();
 
   public static int getIndexByLevel(int level)
   {
     if (openIndexByLevel.containsValue(Integer.valueOf(level))) {
       Iterator itx = openIndexByLevel.keySet().iterator();
       int index = 0;
       while (itx.hasNext()) {
         if ((index = ((Integer)openIndexByLevel.get(itx.next())).intValue()) == level) {
           return index;
         }
       }
     }
     return -1;
   }
 
   public static int getFriendIndexByLevel(int level)
   {
     if (openFriendByLevel.containsValue(Integer.valueOf(level))) {
       Iterator itx = openFriendByLevel.keySet().iterator();
       int index = 0;
       while (itx.hasNext()) {
         if ((index = ((Integer)openFriendByLevel.get(itx.next())).intValue()) == level) {
           return index;
         }
       }
     }
     return -1;
   }
 
   public static int getOpenLevelByIndex(int index)
   {
     return ((Integer)openIndexByLevel.get(Integer.valueOf(index))).intValue();
   }
 
   public static int getOpenLevelByFriendIndex(int index)
   {
     return ((Integer)openFriendByLevel.get(Integer.valueOf(index))).intValue();
   }
 
   public static int getOpenStageNum(int playerLevel)
   {
     Iterator itx = openIndexByLevel.keySet().iterator();
     int num = 0;
     while (itx.hasNext()) {
       int index = ((Integer)itx.next()).intValue();
       int level = ((Integer)openIndexByLevel.get(Integer.valueOf(index))).intValue();
       if (playerLevel >= level) {
         ++num;
       }
     }
     return num;
   }
 
   public static int getOpenFellowNum(int playerLevel)
   {
     Iterator itx = openFriendByLevel.keySet().iterator();
     int num = 0;
     while (itx.hasNext()) {
       int index = ((Integer)itx.next()).intValue();
       int level = ((Integer)openFriendByLevel.get(Integer.valueOf(index))).intValue();
       if (playerLevel >= level) {
         ++num;
       }
     }
     return num;
   }
 
   public static boolean isOpenByPosition(int position, int level)
   {
     for (int i = 0; i < openSort.length; ++i) {
       if (openSort[i] == position)
       {
         return (level < openPositionLevel[i]);
       }
 
     }
 
     return false;
   }
 }
