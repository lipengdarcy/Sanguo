package org.darcy.sanguo.reward;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbCommons;

public class LevelReward {
	public int id;
	public int level;
	public List<Reward> rewards = new ArrayList();

	public PbCommons.LevelReward genLevelReward() {
		PbCommons.LevelReward.Builder builder = PbCommons.LevelReward.newBuilder();
		builder.setId(this.id);
		builder.setLevel(this.level);
		for (Reward reward : this.rewards) {
			builder.addRewards(reward.genPbReward());
		}
		return builder.build();
	}
}
