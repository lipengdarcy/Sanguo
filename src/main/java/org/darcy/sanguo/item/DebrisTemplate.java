 package org.darcy.sanguo.item;
 
 import java.util.ArrayList;
import java.util.List;
 
 public class DebrisTemplate extends ItemTemplate
 {
   public int maxStack;
   public int debrisType;
   public String reward;
   public List<String> costs = new ArrayList();
   public int lootDropNpc;
   public int lootDropPlayer;
 
   public DebrisTemplate(int id, String name)
   {
     super(id, 3, name);
   }
 
   public int getObjectTemplateId()
   {
     String[] params = this.reward.split("\\|");
     int id = Integer.parseInt(params[1]);
     return id;
   }
 }
