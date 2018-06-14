 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class ImunityEffectEffect extends Effect
 {
   private int effectCatagoryId;
 
   public ImunityEffectEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     ImunityEffectEffect e = new ImunityEffectEffect(this.id, this.type, this.description, 
       this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.effectCatagoryId = this.effectCatagoryId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.effectCatagoryId = Integer.parseInt(this.params[1]);
   }
 
   public int[] added(Unit owner, Unit caster, Section section)
   {
     owner.getStates().addState(5, this.effectCatagoryId);
     this.effectType = 5;
     return new int[] { 1990, this.effectCatagoryId };
   }
 
   public int[] removed(Unit owner, Unit caster)
   {
     this.effectType = 6;
     owner.getStates().remove(5, this.effectCatagoryId);
     return this.rstNo;
   }
 }
