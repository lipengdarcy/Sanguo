package org.darcy.sanguo.worldcompetition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.MiniPlayer;

import sango.packet.PbDown;

public class GetRankAsyncCall extends AsyncCall {
	Map<WorldCompetition, MiniPlayer> players = new HashMap();

	List<WorldCompetition> list = new ArrayList();

	int errorNum = 0;

	public GetRankAsyncCall(ClientSession session, List<WorldCompetition> list) {
		super(session, null);
		this.list = list;
	}

	public void callback() {
		PbDown.WorldCompetitionRankRst.Builder builder = PbDown.WorldCompetitionRankRst.newBuilder();
		if (this.errorNum == 0) {
			builder.setResult(true);
			Set<Entry<WorldCompetition, MiniPlayer>> set = this.players.entrySet();
			for (Map.Entry entry : set) {
				WorldCompetition wc = (WorldCompetition) entry.getKey();
				MiniPlayer player = (MiniPlayer) entry.getValue();
				builder.addRanker(WorldCompetition.genWorldCompetitionRanker(player, wc.getRank(), wc.getScore()));
			}
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1084, builder.build());
	}

	public void netOrDB() {
		Iterator itx = this.list.iterator();
		while (itx.hasNext()) {
			WorldCompetition wc = (WorldCompetition) itx.next();
			MiniPlayer player = null;
			try {
				player = Platform.getPlayerManager().getMiniPlayer(wc.getPlayerId());
			} catch (Exception e) {
				this.errorNum = 1;
				Platform.getLog().logError("GetRankAsyncCall get ranks exception,id:" + wc.getPlayerId(), e);
				return;
			}
			if (player == null) {
				this.errorNum = 1;
				Platform.getLog().logError("GetRankAsyncCall get ranks null,id:" + wc.getPlayerId());
				return;
			}
			this.players.put(wc, player);
		}
	}
}
