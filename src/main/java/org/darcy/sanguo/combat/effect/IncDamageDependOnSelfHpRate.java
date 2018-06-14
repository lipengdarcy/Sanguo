 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class IncDamageDependOnSelfHpRate extends Effect
 {
   private int value;
   private int rate;
   private int conditionType;
 
   public IncDamageDependOnSelfHpRate(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     IncDamageDependOnSelfHpRate e = new IncDamageDependOnSelfHpRate(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.rate = this.rate;
     e.value = this.value;
     e.conditionType = this.conditionType;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.conditionType = Integer.parseInt(this.params[1]);
     this.value = Integer.parseInt(this.params[2]);
     this.rate = Integer.parseInt(this.params[3]);
   }
 
   public int[] effectBuff(Unit src, Unit tar, CombatContent combatContent)
   {
     Unit actor = combatContent.getSrc();
     Unit target = combatContent.getTarget();
     if ((this.conditionType == 0) && (actor.getAttributes().getHp() * target.getAttributes().get(7) < target.getAttributes().getHp() * actor.getAttributes().get(7))) {
       combatContent.tDamageInc += this.value;
       combatContent.tDamageIncRate += this.rate;
     } else if ((this.conditionType == 1) && (actor.getAttributes().getHp() * target.getAttributes().get(7) >= target.getAttributes().getHp() * actor.getAttributes().get(7))) {
       combatContent.tDamageInc += this.value;
       combatContent.tDamageIncRate += this.rate;
     }
     return this.rstNo;
   }
 }
