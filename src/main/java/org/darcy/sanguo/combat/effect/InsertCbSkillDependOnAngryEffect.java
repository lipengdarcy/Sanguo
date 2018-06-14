 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;
 
 public class InsertCbSkillDependOnAngryEffect extends Effect
 {
   private int skillId;
   private int refer;
 
   public InsertCbSkillDependOnAngryEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     InsertCbSkillDependOnAngryEffect e = new InsertCbSkillDependOnAngryEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.skillId = this.skillId;
     e.refer = this.refer;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.refer = Integer.parseInt(this.params[1]);
     this.skillId = Integer.parseInt(this.params[2]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, Action action)
   {
     if (owner.getAttributes().get(0) >= this.refer) {
       CbSkill cs = new CbSkill(action);
       Skill s = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getSkill(this.skillId);
       cs.init(owner, s, s.getType(), action.getUnit());
       action.insertSkill(cs);
     }
 
     return this.rstNo;
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
