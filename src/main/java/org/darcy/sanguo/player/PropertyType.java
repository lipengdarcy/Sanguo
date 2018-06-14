 package org.darcy.sanguo.player;
 
 import java.util.HashMap;
import java.util.Map;
 
 public class PropertyType
 {
   public static final int PERSISTENCE_TYPE_INTEGER = 1;
   public static final int PERSISTENCE_TYPE_BYTE = 2;
   public static final int PERSISTENCE_TYPE_STRING = 3;
   public static final int PERSISTENCE_TYPE_DOUBLE = 4;
   public static final int PERSISTENCE_TYPE_LONG = 5;
   public static final int PERSISTENCE_TYPE_INTEGERS = 6;
   public static final int PERSISTENCE_TYPE_BOOL = 7;
   private static Map<Integer, Integer> typeMap = new HashMap();
   public static final int BAG_EXTEND_TIME = 1;
   public static final int WORLD_COMPETITION_COUNT = 2;
   public static final int ARENA_COUNT = 3;
   public static final int CHAT_WORLD_COUNT = 4;
   public static final int PAY_RECORD = 6;
   public static final int VIP_DAYREWARD = 7;
   public static final int VIP_FIRSTREWARD = 8;
   public static final int VIPBAG_BUY_RECORD = 9;
   public static final int MAP_CLEARCD_TIMES = 10;
   public static final int COMPETITION_ADD_TIMES = 11;
   public static final int TOWER_ADD_TIMES = 12;
   public static final int MAP_ADD_TIMES = 13;
   public static final int PROMAP_ADD_TIMES = 14;
   public static final int MONEYTRIAL_ADD_TIMES = 15;
   public static final int WARRIORTRIAL_ADD_TIMES = 16;
   public static final int TREASURETRIAL_ADD_TIMES = 17;
   public static final int GUIDE_PROGRESS = 18;
   public static final int DIVINE_FIRST_CHANGE = 19;
   public static final int CODE_CHECKS = 20;
   public static final int CHARGE = 21;
   public static final int STAR_TRAIN_POINTS = 22;
   public static final int RANK7_GET_REWARD = 23;
   public static final int LOGINREWARD_7WARRIOR_GET_REWARD = 24;
   public static final int PAY_RETURN = 25;
   public static final int RANK7_MARK = 26;
   public static final int MAIL_RECORD = 27;
 
   public static void register()
   {
     typeMap.put(Integer.valueOf(1), Integer.valueOf(3));
     typeMap.put(Integer.valueOf(2), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(3), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(4), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(20), Integer.valueOf(6));
     typeMap.put(Integer.valueOf(6), Integer.valueOf(6));
     typeMap.put(Integer.valueOf(7), Integer.valueOf(7));
     typeMap.put(Integer.valueOf(8), Integer.valueOf(7));
     typeMap.put(Integer.valueOf(9), Integer.valueOf(6));
     typeMap.put(Integer.valueOf(10), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(11), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(12), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(13), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(14), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(15), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(16), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(17), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(18), Integer.valueOf(3));
     typeMap.put(Integer.valueOf(19), Integer.valueOf(7));
     typeMap.put(Integer.valueOf(21), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(22), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(23), Integer.valueOf(5));
     typeMap.put(Integer.valueOf(24), Integer.valueOf(7));
     typeMap.put(Integer.valueOf(25), Integer.valueOf(1));
     typeMap.put(Integer.valueOf(26), Integer.valueOf(7));
     typeMap.put(Integer.valueOf(27), Integer.valueOf(5));
   }
 
   public static int getPersistenceType(int type)
   {
     if (typeMap.containsKey(Integer.valueOf(type)))
       return ((Integer)typeMap.get(Integer.valueOf(type))).intValue();
     try
     {
       throw new RuntimeException("property type error:" + type);
     } catch (Exception e) {
       e.printStackTrace();
     }
     return -1;
   }
 }
