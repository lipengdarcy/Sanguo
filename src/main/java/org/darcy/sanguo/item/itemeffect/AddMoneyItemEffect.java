 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddMoneyItemEffect extends AbstractItemEffect
 {
   private int num;
 
   public AddMoneyItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     player.addMoney(this.num, "itemuse");
     return new EffectResult(0, 1, new Reward[] { new Reward(2, this.num, null) });
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
   }
 }
