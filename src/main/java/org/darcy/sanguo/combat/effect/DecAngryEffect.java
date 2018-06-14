 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class DecAngryEffect extends Effect
 {
   private int rate;
   private int value;
 
   public DecAngryEffect(int id, int type, String desc, int count, int catagory)
   {
     super(id, type, desc, count, catagory);
   }
 
   public Effect copy()
   {
     DecAngryEffect e = new DecAngryEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.rate = this.rate;
     e.value = this.value;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.value = Integer.parseInt(this.params[1]);
     this.rate = Integer.parseInt(this.params[2]);
   }
 
   public void effectCombat(Unit src, Unit tar, CombatContent combat)
   {
     Platform.getLog().logCombat("Effect: " + this.description);
     int random = combat.getRandomBox().getNextRandom();
     if (random < this.rate)
       combat.tAngryDec += this.value;
     else {
       combat.noRecord = true;
     }
     this.effectType = 4;
   }
 }
