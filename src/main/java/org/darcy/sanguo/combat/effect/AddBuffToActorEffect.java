 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;
 
 public class AddBuffToActorEffect extends Effect
 {
   private int buffId;
   private int rate;
 
   public AddBuffToActorEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddBuffToActorEffect e = new AddBuffToActorEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.buffId = this.buffId;
     e.rate = this.rate;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.buffId = Integer.parseInt(this.params[1]);
     this.rate = Integer.parseInt(this.params[2]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill)
   {
     Platform.getLog().logCombat("Effect: " + this.description);
     if (cbSkill.getRandomBox().getNextRandom() < this.rate) {
       Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
       if (buff != null) {
         Buff bf = buff.copy();
         bf.setCaster(owner);
         bf.setOwner(cbSkill.getActor());
         cbSkill.getActor().getBuffs().addBuff(bf, cbSkill.getAction().getSection());
 
         recordEffect(cbSkill.getAction().getSection(), new int[] { 1990, this.buffId }, cbSkill.getActor());
       }
     }
 
     return this.rstNo;
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CombatContent combat)
   {
     return effectBuff(owner, caster, combat.getCbSkill());
   }
 }
