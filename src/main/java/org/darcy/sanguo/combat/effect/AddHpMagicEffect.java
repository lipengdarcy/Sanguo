 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class AddHpMagicEffect extends Effect
 {
   private int rate;
   private int minCureRate;
   private int maxCureRate;
 
   public AddHpMagicEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddHpMagicEffect e = new AddHpMagicEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.rate = this.rate;
     e.minCureRate = this.minCureRate;
     e.maxCureRate = this.maxCureRate;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.minCureRate = Integer.parseInt(this.params[1]);
     this.maxCureRate = Integer.parseInt(this.params[2]);
     this.rate = Integer.parseInt(this.params[3]);
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
 
   public int[] effectBuff(Unit owner, Unit caster, Section section)
   {
     Platform.getLog().logCombat("Effect: " + this.description);
     int ran = section.getRandomBox().getNextRandom();
     if (ran < this.rate) {
       Platform.getLog().logCombat("Effect: " + this.description);
       ran = section.getRandomBox().getNextRandom();
       int mod = this.maxCureRate - this.minCureRate + 1;
       int value = ran % mod + this.minCureRate;
 
       int cure = (caster.getAttributes().get(6) + caster.getAttributes().get(5)) * value / 100 + 
         caster.getAttributes().get(19) + 
         owner.getAttributes().get(20);
 
       cure = cure * (10000 + owner.getAttributes().get(27)) / 10000;
       if (cure < 0) {
         cure = 0;
       }
       if (owner.getStates().hasState(1))
       {
         cure = 0;
       }
       int hp = owner.getAttributes().getHp();
       int left = hp + cure;
       if (left < 0) {
         left = 0;
       }
       if (left > owner.getAttributes().get(7)) {
         left = owner.getAttributes().get(7);
       }
       owner.getAttributes().setHp(left);
       this.effectType = 1;
       return new int[] { 1990, cure };
     }
 
     return this.rstNo;
   }
 }
