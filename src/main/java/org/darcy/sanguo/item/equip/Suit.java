 package org.darcy.sanguo.item.equip;
 
 import java.util.HashMap;
import java.util.Map;
 
 public class Suit
 {
   public int id;
   public String name;
   public int[] equipIds;
   public Map<Integer, Map<Integer, Integer>> attrs = new HashMap();
 }
