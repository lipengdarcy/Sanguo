 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddStaminaItemEffect extends AbstractItemEffect
 {
   private int value;
 
   public AddStaminaItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     player.addStamina(this.value, "itemuse");
     return new EffectResult(0, 1, new Reward[] { new Reward(6, this.value, null) });
   }
 
   public void initParams(String[] params)
   {
     this.value = Integer.valueOf(params[0]).intValue();
   }
 }
