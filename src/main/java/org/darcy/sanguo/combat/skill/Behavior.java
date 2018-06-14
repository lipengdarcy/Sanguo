 package org.darcy.sanguo.combat.skill;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.combat.effect.Effect;
 
 public class Behavior
 {
   public static final int ATK_TARGET_HEAD = 0;
   public static final int ATK_TARGET_BACK = 1;
   public static final int ATK_SELF = 2;
   public static final int ATK_DAMAGESRC = 3;
   public static final int ATK_SELF_HEAD = 4;
   public static final int ATK_SELF_BACK = 5;
   public static final int ATK_ALL_RANDOM = 6;
   public static final int RANGE_SINGLE = 0;
   public static final int RANGE_ROW = 1;
   public static final int RANGE_COL = 2;
   public static final int RANGE_CROSS = 3;
   public static final int RANGE_ALL = 4;
   public static final int RANGE_RANDOM1 = 5;
   public static final int RANGE_RANDOM2 = 6;
   public static final int RANGE_RANDOM3 = 7;
   public static final int RANGE_MINHP = 8;
   public static final int RANGE_MAXHP = 9;
   public static final int RANGE_MINHPRATE = 10;
   public static final int RANGE_BOTHTEAM = 11;
   private int id;
   private String description;
   private int atkTarget;
   private int atkRange;
   private boolean includeSelf;
   private boolean bSureHit;
   private List<Effect> effects = new ArrayList();
 
   public int getId() {
     return this.id;
   }
 
   public void setId(int id) {
     this.id = id;
   }
 
   public String getDescription() {
     return this.description;
   }
 
   public void setDescription(String description) {
     this.description = description;
   }
 
   public int getAtkTarget() {
     return this.atkTarget;
   }
 
   public void setAtkTarget(int atkTarget) {
     this.atkTarget = atkTarget;
   }
 
   public boolean isbSureHit() {
     return this.bSureHit;
   }
 
   public void setbSureHit(boolean bSureHit) {
     this.bSureHit = bSureHit;
   }
 
   public List<Effect> getEffects() {
     return this.effects;
   }
 
   public void setEffects(List<Effect> effects) {
     this.effects = effects;
   }
 
   public int getAtkRange() {
     return this.atkRange;
   }
 
   public void setAtkRange(int atkRange) {
     this.atkRange = atkRange;
   }
 
   public boolean isIncludeSelf() {
     return this.includeSelf;
   }
 
   public void setIncludeSelf(boolean includeSelf) {
     this.includeSelf = includeSelf;
   }
 }
