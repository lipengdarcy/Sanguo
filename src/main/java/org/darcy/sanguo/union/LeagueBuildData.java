package org.darcy.sanguo.union;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbLeague;

public class LeagueBuildData {
	public int id;
	public String name;
	public Reward cost;
	public int buildValue;
	public int contribution;

	public PbLeague.LeagueBuildInfo genLeagueBuildInfo() {
		PbLeague.LeagueBuildInfo.Builder b = PbLeague.LeagueBuildInfo.newBuilder();
		b.setId(this.id);
		b.setName(this.name);
		b.setCost(this.cost.genPbReward());
		b.setBuildValue(this.buildValue);
		b.setContribution(this.contribution);
		return b.build();
	}
}
