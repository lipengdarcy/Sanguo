 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;
 
 public class AddBuffToSelfDependOnTargetBuffEffect extends Effect
 {
   private int buffCatagory;
   private int buffId;
   private int rate;
   private int relationEffect;
 
   public AddBuffToSelfDependOnTargetBuffEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddBuffToSelfDependOnTargetBuffEffect e = new AddBuffToSelfDependOnTargetBuffEffect(this.id, this.type, this.description, 
       this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.buffCatagory = this.buffCatagory;
     e.buffId = this.buffId;
     e.rate = this.rate;
     e.relationEffect = this.relationEffect;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.buffCatagory = Integer.parseInt(this.params[1]);
     this.buffId = Integer.parseInt(this.params[2]);
     this.rate = Integer.parseInt(this.params[3]);
     this.relationEffect = Integer.parseInt(this.params[4]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CombatContent combat)
   {
     Unit tar = combat.getTarget();
     if (!(canEffectRelation(this.relationEffect, combat.getCbSkill().getAction().getSection().getEffectRalation(owner, tar)))) {
       return this.rstNo;
     }
     if (tar.getBuffs().hasBuffCatagory(this.buffCatagory)) {
       int ran = combat.getRandomBox().getNextRandom();
       if (ran < this.rate) {
         Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
         if (buff != null) {
           Buff bf = buff.copy();
           bf.setCaster(owner);
           bf.setOwner(owner);
           owner.getBuffs().addBuff(bf, combat.getCbSkill().getAction().getSection());
 
           recordEffect(combat.getCbSkill().getAction().getSection(), new int[] { 1990, 
             this.buffId }, owner);
         }
       }
     }
 
     return this.rstNo;
   }
 }
