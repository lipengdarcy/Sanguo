package org.darcy.sanguo.divine;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbCommons;

public class DivineReward {
	public int id;
	public int needScore;
	public Reward reward;
	public int dropId;

	public Reward getReward(DivineRecord r) {
		if (!(r.isUpdated())) {
			return this.reward;
		}
		return ((Reward) r.getRewards().get(this.id - 1));
	}

	public PbCommons.DivineReward.Builder genPb(DivineRecord r) {
		PbCommons.DivineReward.Builder rst = PbCommons.DivineReward.newBuilder();
		rst.setId(this.id).setNeedScore(this.needScore);
		if (r.getTotalScores() < this.needScore)
			rst.setState(PbCommons.DivineReward.DivineRewardState.DISABLE);
		else if (r.getRewardRecords().contains(Integer.valueOf(this.id)))
			rst.setState(PbCommons.DivineReward.DivineRewardState.GOT);
		else {
			rst.setState(PbCommons.DivineReward.DivineRewardState.ENABLE);
		}
		rst.setReward(getReward(r).genPbReward());
		return rst;
	}
}
