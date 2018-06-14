 package org.darcy.sanguo.item.itemeffect;
 
 import org.darcy.sanguo.player.Player;
 
 public class AddBuffItemEffect extends AbstractItemEffect
 {
   private int buffId;
 
   public AddBuffItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     return new EffectResult(0, 1);
   }
 
   public void initParams(String[] params)
   {
     this.buffId = Integer.valueOf(params[0]).intValue();
   }
 }
