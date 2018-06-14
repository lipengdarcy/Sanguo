 package org.darcy.sanguo.union;
 
 @Deprecated
 public class LeagueFacility
 {
   private int id;
   private boolean isOpen;
   private int level;
 
   public LeagueFacility(int id)
   {
     this(id, 0, true);
   }
 
   public LeagueFacility(int id, int level, boolean isOpen) {
     this.id = id;
     this.level = level;
     this.isOpen = isOpen;
   }
 
   public int getId() {
     return this.id;
   }
 
   public void setId(int id) {
     this.id = id;
   }
 
   public boolean isOpen() {
     return this.isOpen;
   }
 
   public void setOpen(boolean isOpen) {
     this.isOpen = isOpen;
   }
 
   public int getLevel() {
     return this.level;
   }
 
   public void setLevel(int level) {
     this.level = level;
   }
 }
