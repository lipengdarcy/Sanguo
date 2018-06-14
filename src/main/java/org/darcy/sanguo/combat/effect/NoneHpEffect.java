 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
 
 public class NoneHpEffect extends Effect
 {
   public NoneHpEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     Effect e = new NoneHpEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
   }
 
   public int[] added(Unit owner, Unit caster, Section section)
   {
     owner.getStates().addState(1, -1);
     this.effectType = 5;
     return new int[] { 1990, 1 };
   }
 
   public int[] removed(Unit owner, Unit caster)
   {
     owner.getStates().remove(1, -1);
     this.effectType = 6;
     return new int[] { 1990, 1 };
   }
 }
