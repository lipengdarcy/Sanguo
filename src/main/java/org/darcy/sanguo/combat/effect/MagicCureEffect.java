 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class MagicCureEffect extends Effect
 {
   private int minCureRate;
   private int maxCureRate;
 
   public MagicCureEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     MagicCureEffect e = new MagicCureEffect(this.id, this.type, this.description, 
       this.paramCount, this.catagory);
     e.minCureRate = this.minCureRate;
     e.maxCureRate = this.maxCureRate;
     e.effectId = this.effectId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.minCureRate = Integer.parseInt(this.params[1]);
     this.maxCureRate = Integer.parseInt(this.params[2]);
   }
 
   public void effectCombat(Unit src, Unit tar, CombatContent combat)
   {
     Platform.getLog().logCombat("Effect: " + this.description);
     int ran = combat.getRandomBox().getNextRandom();
     int mod = this.maxCureRate - this.minCureRate + 1;
     int value = ran % mod + this.minCureRate;
     combat.tMagicHealRate += value;
     this.effectType = 1;
   }
 }
