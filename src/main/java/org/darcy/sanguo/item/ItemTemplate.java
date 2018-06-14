 package org.darcy.sanguo.item;
 
 public abstract class ItemTemplate
 {
   public int id;
   public String name;
   public int type;
   public int level;
   public int iconId;
   public int quality;
   public String desc;
   public int price;
   public boolean canUse = false;
 
   public ItemTemplate(int id, int type, String name) { this.id = id;
     this.type = type;
     this.name = name;
   }
 }
