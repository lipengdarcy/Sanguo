package org.darcy.sanguo.reward;

import sango.packet.PbCommons;

public class GrowReward {
	public int level;
	public int reward;

	public PbCommons.GrowReward genGrowReward(boolean isGet) {
		PbCommons.GrowReward.Builder b = PbCommons.GrowReward.newBuilder();
		b.setLevel(this.level);
		b.setReward(this.reward);
		b.setIsGet(isGet);
		return b.build();
	}
}
