 package org.darcy.sanguo.activity.item;
 
 import java.util.HashMap;
import java.util.Map;
import java.util.Set;
 
 public class PrayAI extends AbstractActivityItem
 {
   public static final int TYPE_JEWELS = 1;
   public static final int TYPE_MONEY = 2;
   public static final int TYPE_STAMINA = 3;
   public static final int TYPE_VITALITY = 4;
   public static final int TYPE_SPIRITJADE = 5;
   public int drop;
   public Map<Integer, int[]> goals = new HashMap();
 
   public int getAddPrayCount(int type, int old, int now)
   {
     if (now <= old) {
       return 0;
     }
     int[] array = (int[])this.goals.get(Integer.valueOf(type));
     if (array == null) {
       return 0;
     }
     int oldCount = 0;
     int nowCount = 0;
     int index = 0;
     int process = 0;
     while (true) {
       boolean oldFlag = true;
       boolean nowFlag = true;
       if (index >= array.length) {
         index = array.length - 1;
       }
       process += array[index];
       if (old >= process) {
         ++oldCount;
         oldFlag = false;
       }
       if (now >= process) {
         ++nowCount;
         nowFlag = false;
       }
       if ((oldFlag) && (nowFlag)) {
         break;
       }
       ++index;
     }
     return (nowCount - oldCount);
   }
 
   public int getNeedCountToNextPray(int type, int now)
   {
     int[] array = (int[])this.goals.get(Integer.valueOf(type));
     if (array == null) {
       return 0;
     }
     int index = 0;
     int process = 0;
     while (true) {
       if (index >= array.length) {
         index = array.length - 1;
       }
       process += array[index];
       if (process > now) {
         break;
       }
       ++index;
     }
     return (process - now);
   }
 
   public int getActivityId()
   {
     return 10;
   }
 
   public boolean containsKey(int key)
   {
     return false;
   }
 
   public Set<Integer> keySet()
   {
     return null;
   }
 }
