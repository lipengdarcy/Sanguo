 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class DizzEffect extends Effect
 {
   public DizzEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     Effect e = new DizzEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
   }
 
   public int[] added(Unit owner, Unit caster, Section section)
   {
     owner.getStates().addState(3, -1);
     this.effectType = 5;
     return new int[] { 1990, 3 };
   }
 
   public int[] removed(Unit owner, Unit caster)
   {
     this.effectType = 6;
     owner.getStates().remove(3, -1);
     return new int[] { 1990, 3 };
   }
 }
