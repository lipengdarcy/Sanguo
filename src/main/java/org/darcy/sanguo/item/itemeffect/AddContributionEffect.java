 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddContributionEffect extends AbstractItemEffect
 {
   private int num;
 
   public AddContributionEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     if (player.getUnion() != null) {
       player.getUnion().addContribution(player, this.num, "itemuse");
     }
     return new EffectResult(0, 1, new Reward[] { new Reward(13, this.num, null) });
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
   }
 }
