 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class IncDamageDependOnTargetAngryMore extends Effect
 {
   private int refer;
   private int value;
   private int rate;
 
   public IncDamageDependOnTargetAngryMore(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     IncDamageDependOnTargetAngryMore e = new IncDamageDependOnTargetAngryMore(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.rate = this.rate;
     e.value = this.value;
     e.refer = this.refer;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.refer = Integer.parseInt(this.params[1]);
     this.value = Integer.parseInt(this.params[2]);
     this.rate = Integer.parseInt(this.params[3]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CombatContent combatContent)
   {
     Platform.getLog().logCombat(combatContent.getTarget().getName() + " : 怒气  " + combatContent.getTarget().getAttributes().get(0));
     if (combatContent.getTarget().getAttributes().get(0) >= this.refer) {
       combatContent.tDamageInc += this.value;
       combatContent.tDamageIncRate += this.rate;
     }
 
     return this.rstNo;
   }
 }
