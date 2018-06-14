package org.darcy.sanguo.item.itemeffect;

import org.darcy.sanguo.player.Player;

public abstract interface ItemEffect
{
  public static final int USE_SUCCESS = 0;
  public static final int USE_FAILURE = 1;

  public abstract EffectResult used(Player paramPlayer);

  public abstract ItemEffect copy();

  public abstract int getParamCount();

  public abstract void initParams(String[] paramArrayOfString);
}
