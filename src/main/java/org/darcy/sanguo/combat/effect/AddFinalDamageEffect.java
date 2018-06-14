 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class AddFinalDamageEffect extends Effect
 {
   private int value;
   private int rate;
 
   public AddFinalDamageEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddFinalDamageEffect e = new AddFinalDamageEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.value = this.value;
     e.rate = this.rate;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.value = Integer.parseInt(this.params[1]);
     this.rate = Integer.parseInt(this.params[2]);
   }
 
   public int[] removed(Unit owner, Unit caster)
   {
     int inc = owner.getAttributes().getBuffFinalDamageInc();
     int left = inc - this.value;
     owner.getAttributes().setBuffFinalDamageInc(left);
     int incRate = owner.getAttributes().getBuffFinalDamageIncRate();
     left = incRate - this.rate;
     owner.getAttributes().setBuffFinalDamageIncRate(left);
     return this.rstNo;
   }
 
   public int[] added(Unit owner, Unit caster, Section section)
   {
     int inc = owner.getAttributes().getBuffFinalDamageInc();
     owner.getAttributes().setBuffFinalDamageInc(inc + this.value);
     int incRate = owner.getAttributes().getBuffFinalDamageIncRate();
     owner.getAttributes().setBuffFinalDamageIncRate(incRate + this.rate);
 
     return new int[] { 1990, this.value, this.rate };
   }
 }
