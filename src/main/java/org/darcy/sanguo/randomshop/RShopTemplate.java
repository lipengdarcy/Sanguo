 package org.darcy.sanguo.randomshop;
 
 import java.util.ArrayList;
import java.util.HashMap;
 
 public class RShopTemplate
 {
   static HashMap<Integer, ArrayList<Integer>> warehouses = new HashMap();
   static HashMap<Integer, ArrayList<Integer>> cherishWarehouses = new HashMap();
 
   public static void addWarehouse(int id, String arg) {
     if ((arg == null) || (arg.trim().length() == 0)) {
       throw new IllegalArgumentException();
     }
 
     String[] ls = arg.split(",");
     ArrayList list = new ArrayList();
     for (String s : ls) {
       list.add(Integer.valueOf(Integer.parseInt(s)));
     }
     warehouses.put(Integer.valueOf(id), list);
   }
 
   public static void addChrishWarehouse(int id, String arg) {
     if ((arg == null) || (arg.trim().length() == 0)) {
       throw new IllegalArgumentException();
     }
 
     String[] ls = arg.split(",");
     ArrayList list = new ArrayList();
     for (String s : ls) {
       list.add(Integer.valueOf(Integer.parseInt(s)));
     }
     cherishWarehouses.put(Integer.valueOf(id), list);
   }
 }
