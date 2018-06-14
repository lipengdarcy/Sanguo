 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;
 
 public class AddBuffToSelDependOnTeamDie extends Effect
 {
   private int buffId;
 
   public AddBuffToSelDependOnTeamDie(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddBuffToSelDependOnTeamDie e = new AddBuffToSelDependOnTeamDie(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.buffId = this.buffId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.buffId = Integer.parseInt(this.params[1]);
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
     boolean die = true;
     Team team = section.getOwnerTeam(owner);
     for (Unit u : team.getUnits()) {
       if ((u != null) && (u.getId() != owner.getId()) && (u.isAlive())) {
         die = false;
         break;
       }
     }
 
     if (die) {
       Platform.getLog().logCombat("Effect: " + this.description);
       Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
       if (buff != null) {
         Buff bf = buff.copy();
         bf.setCaster(owner);
         bf.setOwner(owner);
         owner.getBuffs().addBuff(bf, section);
 
         recordEffect(section, new int[] { 1990, this.buffId }, owner);
       }
     }
     return this.rstNo;
   }
 }
