 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddChargeItemEffect extends AbstractItemEffect
 {
   private int num;
   private int type;
 
   public AddChargeItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     boolean isCharge = this.type != 0;
     player.addCharge(this.num, isCharge);
     return new EffectResult(0, 1, new Reward[] { new Reward(12, this.num, null) });
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
     this.type = Integer.valueOf(params[1]).intValue();
   }
 }
