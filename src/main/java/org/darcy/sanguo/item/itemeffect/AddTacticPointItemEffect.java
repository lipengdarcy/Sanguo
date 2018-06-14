 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;
 
 public class AddTacticPointItemEffect extends AbstractItemEffect
 {
   private int value;
 
   public AddTacticPointItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     player.getTacticRecord().addPoint(this.value, player);
     return new EffectResult(0, 1, new Reward[] { new Reward(11, this.value, null) });
   }
 
   public void initParams(String[] params)
   {
     this.value = Integer.valueOf(params[0]).intValue();
   }
 }
