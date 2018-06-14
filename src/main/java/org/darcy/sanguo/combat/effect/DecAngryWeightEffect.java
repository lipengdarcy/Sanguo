 package org.darcy.sanguo.combat.effect;
 
 import java.util.Arrays;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.Calc;
 
 public class DecAngryWeightEffect extends Effect
 {
   private int[] weights = new int[6];
 
   public DecAngryWeightEffect(int id, int type, String description, int paramCount, int catagory) {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     DecAngryWeightEffect e = new DecAngryWeightEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.weights = Arrays.copyOf(this.weights, this.weights.length);
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     for (int i = 1; i <= this.paramCount - 1; ++i)
       this.weights[(i - 1)] = Integer.parseInt(this.params[i]);
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
     int rst = Calc.weight(ran, this.weights);
     int left = owner.getAttributes().get(0);
     left -= rst;
     if (left < 0) {
       left = 0;
     }
     owner.getAttributes().set(0, left);
     this.effectType = 4;
     return new int[] { 1990, -rst };
   }
 }
