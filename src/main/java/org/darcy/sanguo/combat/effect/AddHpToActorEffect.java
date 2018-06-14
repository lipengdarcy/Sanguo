 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;
 
 public class AddHpToActorEffect extends Effect
 {
   private int rate;
 
   public AddHpToActorEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     AddHpToActorEffect e = new AddHpToActorEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.effectId = this.effectId;
     e.rate = this.rate;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.rate = Integer.parseInt(this.params[1]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, CombatContent combat)
   {
     Unit u = combat.getSrc();
     int hp = (int)(combat.totalDamage * this.rate / 10000L);
     if (u.getStates().hasState(1))
     {
       hp = 0;
     }
     int left = u.getAttributes().getHp() + hp;
     if (left > u.getAttributes().get(7)) {
       left = u.getAttributes().get(7);
     }
     u.getAttributes().setHp(left);
     this.effectType = 1;
     return new int[] { 1990, hp };
   }
 }
