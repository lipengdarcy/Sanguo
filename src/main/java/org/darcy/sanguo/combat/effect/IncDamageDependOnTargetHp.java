 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class IncDamageDependOnTargetHp extends Effect
 {
   private int refer;
   private int value;
   private int rate;
 
   public IncDamageDependOnTargetHp(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     IncDamageDependOnTargetHp e = new IncDamageDependOnTargetHp(this.id, this.type, this.description, this.paramCount, this.catagory);
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
 
   public int[] effectBuff(Unit src, Unit tar, CombatContent combatContent)
   {
     Unit u = combatContent.getTarget();
     int maxHp = u.getAttributes().get(7);
     int time = (maxHp - u.getAttributes().getHp()) * 10000 / maxHp / this.refer;
     combatContent.tDamageInc -= this.value * time;
     combatContent.tDamageIncRate -= this.rate * time;
 
     return this.rstNo;
   }
 }
