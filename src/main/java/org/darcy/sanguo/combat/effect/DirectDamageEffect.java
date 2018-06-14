 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class DirectDamageEffect extends Effect
 {
   private int value;
   private int ownerRate;
   private int casterRate;
 
   public DirectDamageEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     DirectDamageEffect e = new DirectDamageEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.value = this.value;
     e.casterRate = this.casterRate;
     e.ownerRate = this.ownerRate;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.value = Integer.parseInt(this.params[1]);
     this.casterRate = Integer.parseInt(this.params[2]);
     this.ownerRate = Integer.parseInt(this.params[3]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, Section section)
   {
     long ohp = owner.getAttributes().get(7);
     long chp = caster.getAttributes().get(7);
     if (ohp > 100000L) {
       ohp = 100000L;
     }
     if (chp > 100000L) {
       chp = 100000L;
     }
     int damage = (int)(this.value + ohp * this.ownerRate / 10000L + 
       chp * this.casterRate / 10000L);
 
     int hp = owner.getAttributes().getHp();
     int left = hp - damage;
     if (left < 0) {
       left = 0;
     }
     if (left > owner.getAttributes().get(7)) {
       left = owner.getAttributes().get(7);
     }
     owner.getAttributes().setHp(left);
     this.effectType = 2;
     return new int[] { 1990, damage };
   }
 
   public int[] effectBuff(Unit owner, Unit caster, Action action)
   {
     return effectBuff(owner, caster, action.getSection());
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill)
   {
     return effectBuff(owner, caster, cbSkill.getAction());
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CombatContent combatContent)
   {
     return effectBuff(owner, caster, combatContent.getCbSkill());
   }
 }
