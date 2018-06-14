package org.darcy.sanguo.item.itemeffect;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.player.Player;

public class DropGroupFallItemEffect extends AbstractItemEffect {
	private int drapGroupId;
	private int showType;

	public DropGroupFallItemEffect(int paramCount) {
		super(paramCount);
	}

	public EffectResult used(Player player) {
		DropGroup dg = ((DropService) Platform.getServiceManager().get(DropService.class))
				.getDropGroup(this.drapGroupId);
		List<Gain> gains = dg.genGains(player);

		List list = new ArrayList();
		for (Gain gain : gains) {
			gain.gain(player, "itemuse");
			list.add(gain.newReward());
		}
		return new EffectResult(0, 2, list);
	}

	public void initParams(String[] params) {
		this.drapGroupId = Integer.valueOf(params[0]).intValue();
		this.showType = Integer.valueOf(params[1]).intValue();
	}
}
