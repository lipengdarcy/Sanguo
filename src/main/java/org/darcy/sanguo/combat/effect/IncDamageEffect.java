 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class IncDamageEffect extends Effect
 {
   private int value;
   private int rate;
 
   public IncDamageEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     IncDamageEffect e = new IncDamageEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
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
 
   public int[] effectBuff(Unit src, Unit tar, CombatContent combatContent)
   {
     combatContent.tDamageInc += this.value;
     combatContent.tDamageIncRate += this.rate;
 
     return this.rstNo;
   }
 }
