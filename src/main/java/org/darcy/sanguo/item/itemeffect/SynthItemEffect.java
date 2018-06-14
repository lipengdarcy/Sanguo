package org.darcy.sanguo.item.itemeffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.player.Player;

public class SynthItemEffect extends AbstractItemEffect {
	public String reward;
	public List<String> costs = new ArrayList();

	public SynthItemEffect(int count) {
		super(count);
	}

	public EffectResult used(Player player) {
		Reward cost;
		Reward reward = new Reward(this.reward);
		List costs = new ArrayList();
		for (String str : this.costs) {
			costs.add(new Reward(str));
		}

		int actualCount = reward.count;
		ItemTemplate template = reward.template;

		for (Iterator localIterator2 = costs.iterator(); localIterator2.hasNext();) {
			cost = (Reward) localIterator2.next();
			if (cost.check(player) != null) {
				return new EffectResult(0, 1);
			}

		}

		reward.add(player, "itemuse");

		for (Iterator localIterator2 = costs.iterator(); localIterator2.hasNext();) {
			cost = (Reward) localIterator2.next();
			cost.remove(player, "itemuse");
		}
		return new EffectResult(0, 1, new Reward[] { reward });
	}

	public void initParams(String[] params) {
		this.reward = params[0];
		String[] costArray = params[1].split(",");
		for (String str : costArray)
			this.costs.add(str);
	}
}
