 package org.darcy.sanguo.combat.state;
 
 public class State
 {
   public static final int NON_SKILL = 0;
   public static final int NON_ANGRY = 2;
   public static final int DIZZ = 3;
   public static final int PARALYSIS = 4;
   public static final int IMUNITY_EFFECT = 5;
   public static final int NON_BACK = 6;
   public static final int NON_HP = 1;
   public static final int IMUNITY_BUFF = 8;
   protected int count;
   protected int id;
 
   public int getCount()
   {
     return this.count; }
 
   public int getId() {
     return this.id; }
 
   public void setCount(int count) {
     this.count = count; }
 
   public void setId(int id) {
     this.id = id;
   }
 
   public State(int id) {
     this.id = id;
     this.count = 1;
   }
 
   public boolean remove(int[] params) {
     if (this.count > 1) {
       this.count -= 1;
       return false;
     }
     return true;
   }
 }
