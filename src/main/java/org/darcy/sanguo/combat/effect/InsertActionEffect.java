 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;
 
 public class InsertActionEffect extends Effect
 {
   private int skillId;
 
   public InsertActionEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     InsertActionEffect e = new InsertActionEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.skillId = this.skillId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.skillId = Integer.parseInt(this.params[1]);
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
     Action insertAction = new Action(section);
     Skill backSkill = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getSkill(this.skillId);
     insertAction.init(owner, backSkill, 2);
     section.getInsertActions().add(insertAction);
     return new int[] { 1990, this.skillId };
   }
 }
