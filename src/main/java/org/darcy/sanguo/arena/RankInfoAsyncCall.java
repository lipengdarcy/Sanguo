package org.darcy.sanguo.arena;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.util.DBUtil;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class RankInfoAsyncCall extends AsyncCall {
	Map<Integer, MiniPlayer> players = new HashMap();

	int errorNum = 0;

	public RankInfoAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
	}

	public void callback() {
		PbDown.ArenaRankRst.Builder builder = PbDown.ArenaRankRst.newBuilder();
		if (this.errorNum == 0) {
			builder.setResult(true);
			Set<Entry<Integer, MiniPlayer>> set = this.players.entrySet();
			for (Map.Entry entry : set) {
				int rank = ((Integer) entry.getKey()).intValue();
				MiniPlayer player = (MiniPlayer) entry.getValue();
				builder.addRanker(Arena.genArenaRival(player, rank));
			}
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1094, builder.build());
	}

	public void netOrDB() {
		for (int i = 1; i <= 10; ++i) {
			Arena arena = null;
			try {
				arena = DBUtil.getArenaByRank(i);
				if (arena != null) {
					MiniPlayer player = Platform.getPlayerManager().getMiniPlayer(arena.getPlayerId());
					if (player == null) {
						Platform.getLog().logWarn("RankInfoAsyncCall error: player is null,id:" + arena.getPlayerId());
						this.errorNum = 1;
						return;
					}
					this.players.put(Integer.valueOf(i), player);
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.errorNum = 1;
				return;
			}
		}
	}
}
