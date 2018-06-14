package org.darcy.sanguo.worldcompetition;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbWorldCompetition;

public class WorldCompetitionReward {
	public int id;
	public String desc;
	public int start;
	public int end;
	public List<Reward> rewards = new ArrayList();

	public PbWorldCompetition.WorldCompetitionReward genWorldCompetitionReward() {
		PbWorldCompetition.WorldCompetitionReward.Builder builder = PbWorldCompetition.WorldCompetitionReward
				.newBuilder();
		builder.setId(this.id);
		builder.setStart(this.start);
		builder.setEndLua(this.end);
		for (Reward reward : this.rewards) {
			builder.addRewards(reward.genPbReward());
		}
		return builder.build();
	}
}
