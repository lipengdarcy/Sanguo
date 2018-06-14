 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;
 
 public class AddBuffToTargetEffect extends Effect
 {
   private int buffId;
   private int rate;
   private int relationEffect;
 
   public AddBuffToTargetEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddBuffToTargetEffect e = new AddBuffToTargetEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.buffId = this.buffId;
     e.rate = this.rate;
     e.relationEffect = this.relationEffect;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.buffId = Integer.parseInt(this.params[1]);
     this.rate = Integer.parseInt(this.params[2]);
     this.relationEffect = Integer.parseInt(this.params[3]);
   }
 
   public int[] effectBuff(Unit src, Unit caster, CbSkill cbSkill)
   {
     Unit tar = cbSkill.curTarget;
     if (!(canEffectRelation(this.relationEffect, cbSkill.getAction().getSection().getEffectRalation(src, tar)))) {
       return this.rstNo;
     }
     if (tar == null) return this.rstNo;
     Platform.getLog().logCombat("Effect: " + this.description);
     int ran = cbSkill.getRandomBox().getNextRandom();
     if (ran < this.rate) {
       Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
       if (buff != null) {
         Buff bf = buff.copy();
         bf.setCaster(src);
         bf.setOwner(tar);
         tar.getBuffs().addBuff(bf, cbSkill.getAction().getSection());
 
         recordEffect(cbSkill.getAction().getSection(), new int[] { 1990, this.buffId }, tar);
       }
 
     }
 
     return this.rstNo;
   }
 
   public int[] effectBuff(Unit src, Unit caster, CombatContent combat)
   {
     Unit tar = combat.getTarget();
     if (!(canEffectRelation(this.relationEffect, combat.getCbSkill().getAction().getSection().getEffectRalation(src, tar)))) {
       return this.rstNo;
     }
     Platform.getLog().logCombat("Effect: " + this.description);
     int ran = combat.getRandomBox().getNextRandom();
     if (ran < this.rate) {
       Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
       if (buff != null) {
         Buff bf = buff.copy();
         bf.setCaster(src);
         bf.setOwner(tar);
         tar.getBuffs().addBuff(bf, combat.getCbSkill().getAction().getSection());
 
         recordEffect(combat.getCbSkill().getAction().getSection(), new int[] { 1990, this.buffId }, tar);
       }
     }
 
     return this.rstNo;
   }
 }
