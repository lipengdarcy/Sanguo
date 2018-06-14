package org.darcy.sanguo.reward;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.service.RewardService;

import sango.packet.PbCommons;

public class SignReward {
	public int id;
	public int day;
	public List<Reward> rewards = new ArrayList();

	public PbCommons.SignReward genSignReward(int signCount, boolean isGet) {
		PbCommons.SignReward.Builder builder = PbCommons.SignReward.newBuilder();
		builder.setDay(this.day);
		for (Reward reward : this.rewards) {
			builder.addRewards(reward.genPbReward());
		}

		if ((this.day == signCount) || ((signCount > this.day) && (this.day == RewardService.signRewards.size()))) {
			if (isGet)
				builder.setState(PbCommons.SignReward.SignRewardState.GOT);
			else
				builder.setState(PbCommons.SignReward.SignRewardState.ENABLE);
		} else {
			builder.setState(PbCommons.SignReward.SignRewardState.DISABLE);
		}
		return builder.build();
	}
}
