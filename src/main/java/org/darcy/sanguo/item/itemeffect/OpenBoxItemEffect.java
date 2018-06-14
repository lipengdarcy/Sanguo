 package org.darcy.sanguo.item.itemeffect;
 
 import java.util.List;

import org.darcy.sanguo.player.Player;
 
 public class OpenBoxItemEffect extends AbstractItemEffect
 {
   int dropId;
 
   public OpenBoxItemEffect(int paramCount)
   {
     super(paramCount);
   }
 
   public EffectResult used(Player player)
   {
     List list = player.getGlobalDrop().openBoxDrop(player);
     return new EffectResult(0, 2, list);
   }
 
   public void initParams(String[] params)
   {
     this.dropId = Integer.valueOf(params[0]).intValue();
   }
 }
