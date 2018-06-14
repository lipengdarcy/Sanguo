 package org.darcy.sanguo.combat.effect;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;
 
 public class AddBuffToTargetTeamRandomEffect extends Effect
 {
   private int buffId;
 
   public AddBuffToTargetTeamRandomEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddBuffToTargetTeamRandomEffect e = new AddBuffToTargetTeamRandomEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
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
     Team team = section.getTargetTeam(owner);
     Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
     Buff bf = buff.copy();
     Unit tar = null;
     List list = new ArrayList();
     for (Unit u : team.getUnits()) {
       if ((u != null) && (u.isAlive()) && (u.getId() != owner.getId())) {
         list.add(u);
       }
     }
 
     if (list.size() > 0) {
       Platform.getLog().logCombat("Effect: " + this.description);
       int ran = section.getRandomBox().getNextRandom();
       ran %= list.size();
       tar = (Unit)list.get(ran);
       if (tar != null) {
         bf.setCaster(owner);
         bf.setOwner(tar);
         tar.getBuffs().addBuff(bf, section);
 
         recordEffect(section, new int[] { 1990, this.buffId }, tar);
       }
     }
 
     return this.rstNo;
   }
 }
