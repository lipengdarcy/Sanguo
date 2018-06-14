 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class MagicDamageEffect extends Effect
 {
   private int minDamageRate;
   private int maxDamageRate;
 
   public MagicDamageEffect(int id, int type, String desc, int count, int catagory)
   {
     super(id, type, desc, count, catagory);
   }
 
   public Effect copy()
   {
     MagicDamageEffect e = new MagicDamageEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.minDamageRate = this.minDamageRate;
     e.maxDamageRate = this.maxDamageRate;
     e.effectId = this.effectId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.minDamageRate = Integer.parseInt(this.params[1]);
     this.maxDamageRate = Integer.parseInt(this.params[2]);
   }
 
   public void effectCombat(Unit src, Unit tar, CombatContent combat)
   {
     Platform.getLog().logCombat("Effect: " + this.description);
     int ran = combat.getRandomBox().getNextRandom();
     int mod = this.maxDamageRate - this.minDamageRate + 1;
     int value = ran % mod + this.minDamageRate;
     combat.tMagicDamageRate += value;
     this.effectType = 2;
   }
 }
