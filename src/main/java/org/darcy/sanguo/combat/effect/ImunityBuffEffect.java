 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class ImunityBuffEffect extends Effect
 {
   private int buffCatagoryId;
 
   public ImunityBuffEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     ImunityBuffEffect e = new ImunityBuffEffect(this.id, this.type, this.description, 
       this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.buffCatagoryId = this.buffCatagoryId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.buffCatagoryId = Integer.parseInt(this.params[1]);
   }
 
   public int[] added(Unit owner, Unit caster, Section section)
   {
     owner.getStates().addState(8, this.buffCatagoryId);
     this.effectType = 5;
     return new int[] { 1990, 8 };
   }
 
   public int[] removed(Unit owner, Unit caster)
   {
     this.effectType = 6;
     owner.getStates().remove(8, this.buffCatagoryId);
     return this.rstNo;
   }
 }
