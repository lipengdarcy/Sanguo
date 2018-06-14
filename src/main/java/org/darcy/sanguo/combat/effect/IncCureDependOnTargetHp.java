 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class IncCureDependOnTargetHp extends Effect
 {
   private int refer;
   private int value;
   private int rate;
 
   public IncCureDependOnTargetHp(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     IncCureDependOnTargetHp e = new IncCureDependOnTargetHp(this.id, this.type, this.description, this.paramCount, this.catagory);
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
     if (combatContent.getTarget().getAttributes().getHp() * 10000L <= this.refer * combatContent.getTarget().getAttributes().get(7)) {
       combatContent.tCureInc += this.value;
       combatContent.tCureIncRate += this.rate;
     }
 
     return this.rstNo;
   }
 }
