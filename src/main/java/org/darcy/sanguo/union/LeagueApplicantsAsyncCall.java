package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;

import sango.packet.PbDown;
import sango.packet.PbLeague;

public class LeagueApplicantsAsyncCall extends AsyncCall {
	Player player;
	List<Integer> ids;
	List<PbLeague.LeagueApplicant> applicants;

	public LeagueApplicantsAsyncCall(Player player, List<Integer> ids) {
		super(player.getSession(), null);
		this.player = player;
		this.ids = ids;
	}

	public void callback() {
		PbDown.LeagueApplicantsRst.Builder b = PbDown.LeagueApplicantsRst.newBuilder().setResult(true);
		b.addAllApplicants(this.applicants);
		this.player.send(1302, b.build());
	}

	public void netOrDB() {
		this.applicants = new ArrayList(this.ids.size());
		for (Integer id : this.ids) {
			PbLeague.LeagueApplicant.Builder b = PbLeague.LeagueApplicant.newBuilder();
			b.setId(id.intValue());
			String name = "";
			int btlCapability = 0;
			int level = 0;

			Player player = Platform.getPlayerManager().getPlayerById(id.intValue());
			if (player != null) {
				name = player.getName();
				btlCapability = player.getBtlCapability();
				level = player.getLevel();
			} else {
				MiniPlayer mp = Platform.getPlayerManager().getMiniPlayer(id.intValue());
				btlCapability = mp.getBtlCapability();
				name = mp.getName();
				level = mp.getLevel();
			}
			b.setBtlCapability(btlCapability);
			b.setName(name);
			b.setLevel(level);
			this.applicants.add(b.build());
		}
	}
}
