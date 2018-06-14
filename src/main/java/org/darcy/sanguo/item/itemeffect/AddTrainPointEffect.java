 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddTrainPointEffect extends AbstractItemEffect
 {
   private int num;
 
   public AddTrainPointEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     player.getRewardRecord().addTrainPoint(player, this.num, "itemuse");
     return new EffectResult(0, 1, new Reward[] { new Reward(14, this.num, null) });
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
   }
 }
