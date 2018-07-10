package org.darcy.sanguo.union;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.MiniPlayer;

import sango.packet.PbDown;
import sango.packet.PbLeague;

public class LeagueBossRankAsyncCall extends AsyncCall {
	League l;
	Map<Integer, MiniPlayer> players = new HashMap();
	Map<Integer, Integer> ranks = new LinkedHashMap();
	int rankLevel;
	int errorNum = 0;

	public LeagueBossRankAsyncCall(ClientSession session, League l) {
		super(session, null);
		this.l = l;
		this.ranks = l.getInfo().getBoss().ranks;
		this.rankLevel = l.getInfo().getBoss().rankLevel;
	}

	public void callback() {
		PbDown.LeagueBossRankRst.Builder b = PbDown.LeagueBossRankRst.newBuilder().setResult(true);
		if (this.errorNum == 0) {
			b.setLevel(this.rankLevel);
			int count = 1;
			for (Integer playerId : this.ranks.keySet())
				if (this.l.isMember(playerId.intValue())) {
					int damage = ((Integer) this.ranks.get(playerId)).intValue();
					MiniPlayer player = (MiniPlayer) this.players.get(playerId);
					if (player != null) {
						PbLeague.LeagueBossRanker.Builder rb = PbLeague.LeagueBossRanker.newBuilder();
						rb.setDamage(damage);
						rb.setRank(count);
						rb.setUser(player.genMiniUser());
						if (damage > 0) {
							List<Reward> rs = LeagueService.getBossDayReward(this.l.getInfo().getBoss().rankLevel, count);
							if ((rs != null) && (rs.size() > 0)) {
								for (Reward r : rs) {
									rb.addReward(r.genPbReward());
								}
							}
						}
						b.addRankers(rb.build());
						++count;
					}
				}
		} else {
			b.setResult(false);
			b.setErrInfo("数据异常");
		}
		this.session.send(1312, b.build());
	}

	public void netOrDB() {
		Iterator itx = this.ranks.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			MiniPlayer player = null;
			try {
				player = Platform.getPlayerManager().getMiniPlayer(id);
			} catch (Exception e) {
				this.errorNum = 1;
				Platform.getLog().logError("LeagueRankAsyncCall get ranks exception,id:" + id, e);
				return;
			}
			if (player == null) {
				this.errorNum = 1;
				Platform.getLog().logError("LeagueRankAsyncCall get ranks null,id:" + id);
				return;
			}
			this.players.put(Integer.valueOf(id), player);
		}
	}
}
