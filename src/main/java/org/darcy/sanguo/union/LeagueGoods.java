package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;

import sango.packet.PbLeague;

public class LeagueGoods {
	public int level;
	public List<Reward> rewards = new ArrayList();

	public void reward(Player player) {
		for (Reward r : this.rewards)
			r.add(player, "leaguegetgoods");
	}

	public PbLeague.LeagueGoods genLeagueGoods() {
		PbLeague.LeagueGoods.Builder b = PbLeague.LeagueGoods.newBuilder();
		b.setLevel(this.level);
		for (Reward r : this.rewards) {
			b.addGoods(r.genPbReward());
		}
		return b.build();
	}
}
