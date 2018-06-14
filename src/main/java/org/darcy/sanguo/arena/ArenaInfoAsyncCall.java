package org.darcy.sanguo.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.ArenaService;
import org.darcy.sanguo.util.DBUtil;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class ArenaInfoAsyncCall extends AsyncCall {
	Player player;
	int[] ranks;
	Map<Integer, MiniPlayer> players = new HashMap();

	int errorNum = 0;

	public ArenaInfoAsyncCall(ClientSession session, PbPacket.Packet packet, Player player, int[] ranks) {
		super(session, packet);
		this.player = player;
		this.ranks = ranks;
	}

	public void callback() {
		PbDown.ArenaInfoRst.Builder builder = PbDown.ArenaInfoRst.newBuilder();
		if (this.errorNum == 0) {
			builder.setResult(true);
			builder.setRewardHour(Arena.rewardTime[0]);
			builder.setRestTime(this.player.getArena().getRestTimeToRwardTime());
			builder.setRank(this.player.getArena().getRank());
			List<Reward> list = ((ArenaService) Platform.getServiceManager().get(ArenaService.class))
					.getRewards(this.player.getArena().getRank(), System.currentTimeMillis());
			if (list != null) {
				for (Reward reward : list) {
					builder.addRewards(reward.genPbReward());
				}
			}
			if (this.player.getArena().rivalIds == null)
				this.player.getArena().rivalIds = new ArrayList();
			else {
				this.player.getArena().rivalIds.clear();
			}
			Set<Entry<Integer, MiniPlayer>> set = this.players.entrySet();
			for (Map.Entry entry : set) {
				int rank = ((Integer) entry.getKey()).intValue();
				MiniPlayer miniPlayer = (MiniPlayer) entry.getValue();
				builder.addRivals(Arena.genArenaRival(miniPlayer, rank));
				if (miniPlayer.getId() != this.player.getId())
					this.player.getArena().rivalIds.add(Integer.valueOf(miniPlayer.getId()));
			}
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1096, builder.build());
	}

	public void netOrDB() {
		for (int i = 0; i < this.ranks.length; ++i) {
			int rank = this.ranks[i];
			Arena arena = null;
			try {
				arena = DBUtil.getArenaByRank(rank);
				if (arena != null) {
					MiniPlayer player = Platform.getPlayerManager().getMiniPlayer(arena.getPlayerId());
					if (player == null) {
						Platform.getLog().logWarn("ArenaInfoAsyncCall error: rival is null,id:" + arena.getPlayerId());
						this.errorNum = 1;
						return;
					}
					this.players.put(Integer.valueOf(rank), player);
				}
			} catch (Exception e) {
				e.printStackTrace();
				this.errorNum = 1;
				return;
			}
		}
	}
}
