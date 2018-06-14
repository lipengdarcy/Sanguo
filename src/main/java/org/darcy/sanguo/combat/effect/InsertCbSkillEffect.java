 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;
 
 public class InsertCbSkillEffect extends Effect
 {
   private int skillId;
   private int rate = 10000;
 
   public InsertCbSkillEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     InsertCbSkillEffect e = new InsertCbSkillEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.skillId = this.skillId;
     e.rate = this.rate;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.skillId = Integer.parseInt(this.params[1]);
     if (this.paramCount > 2)
       this.rate = Integer.parseInt(this.params[2]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, Action action)
   {
     int ran = action.getRandomBox().getNextRandom();
     if (ran < this.rate) {
       CbSkill cs = new CbSkill(action);
       Skill s = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getSkill(this.skillId);
       cs.init(owner, s, s.getType(), action.getUnit());
       action.insertSkill(cs);
     }
 
     return this.rstNo;
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill)
   {
     int ran = cbSkill.getRandomBox().getNextRandom();
     if (ran < this.rate) {
       CbSkill cs = new CbSkill(cbSkill.getAction());
       Skill s = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getSkill(this.skillId);
       cs.init(owner, s, s.getType(), cbSkill.getActor());
       cbSkill.getAction().insertSkill(cs);
     }
     return this.rstNo;
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CombatContent combat)
   {
     int ran = combat.getRandomBox().getNextRandom();
     if (ran < this.rate) {
       CbSkill cs = new CbSkill(combat.getCbSkill().getAction());
       Skill s = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getSkill(this.skillId);
       cs.init(owner, s, s.getType(), combat.getSrc());
       combat.getCbSkill().getAction().insertSkill(cs);
     }
     return this.rstNo;
   }
 }
