package org.darcy.sanguo.arena;

import java.util.HashMap;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.util.DBUtil;

import sango.packet.PbCommons;
import sango.packet.PbDown;

/**
 * 竞技场排名
 */
public class ArenaRankAsync implements Runnable {
	private Player player;

	public ArenaRankAsync(Player player) {
		this.player = player;
	}

	public void run() {
		Map<Integer, Object> players = new HashMap<Integer, Object>();
		for (int i = 1; i <= 50; ++i) {
			Arena arena = null;
			try {
				arena = DBUtil.getArenaByRank(i);
				if (arena != null) {
					MiniPlayer player = Platform.getPlayerManager().getMiniPlayer(arena.getPlayerId());
					if (player == null) {
						Platform.getLog().logWarn("RankInfoAsyncCall error: player is null,id:" + arena.getPlayerId());
					} else
						players.put(Integer.valueOf(i), player);
				}
			} catch (Exception localException) {
			}
		}
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		String info = "尚未开启";
		Arena arena = this.player.getArena();
		if (arena != null) {
			info = String.valueOf(arena.getRank());
			rst.setMyRank(this.player.getArena().getRank());
		} else {
			rst.setMyRank(-1);
		}
		rst.setMyInfo(info);
		rst.setChange(0);

		for (Integer rank : players.keySet()) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			MiniPlayer mini = (MiniPlayer) players.get(rank);
			node.setIconId(mini.getHeroList()[0]);
			node.setInfo("");
			node.setLevel(mini.getLevel());
			node.setName(mini.getName());
			node.setPlayerId(mini.getId());
			node.setRank(rank.intValue());
			League l = Platform.getLeagueManager().getLeagueByPlayerId(mini.getId());
			if (l != null) {
				node.setInfoName(l.getName());
			}
			node.setTitleId(Platform.getTopManager().getTitleId(mini.getId()));
			rst.addNodes(node);
		}

		this.player.send(2212, rst.build());
	}
}
