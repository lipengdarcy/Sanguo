 package org.darcy.sanguo.randomshop;
 
 import java.util.ArrayList;
import java.util.HashMap;
 
 public class Warehouse
 {
   private int id;
   private HashMap<Integer, ArrayList<Integer>> goodsLists = new HashMap();
 
   public void addGoodList(int index, String var) {
     if ((var == null) || (var.trim().length() == 0)) {
       throw new IllegalArgumentException();
     }
 
     String[] ls = var.split(",");
     ArrayList list = new ArrayList();
     for (String s : ls) {
       list.add(Integer.valueOf(Integer.parseInt(s)));
     }
 
     this.goodsLists.put(Integer.valueOf(index), list);
   }
 
   public int getId() {
     return this.id;
   }
 
   public HashMap<Integer, ArrayList<Integer>> getGoodsLists() {
     return this.goodsLists;
   }
 
   public void setId(int id) {
     this.id = id;
   }
 
   public void setGoodsLists(HashMap<Integer, ArrayList<Integer>> goodsLists) {
     this.goodsLists = goodsLists;
   }
 }
