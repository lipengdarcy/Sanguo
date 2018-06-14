 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddJewelsItemEffect extends AbstractItemEffect
 {
   private int num;
 
   public AddJewelsItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     player.addJewels(this.num, "itemuse");
     return new EffectResult(0, 1, new Reward[] { new Reward(3, this.num, null) });
   }
 
   public void initParams(String[] params)
   {
     this.num = Integer.valueOf(params[0]).intValue();
   }
 }
