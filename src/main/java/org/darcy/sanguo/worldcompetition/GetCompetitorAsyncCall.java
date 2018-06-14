package org.darcy.sanguo.worldcompetition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;

import sango.packet.PbDown;
import sango.packet.PbWorldCompetition;

public class GetCompetitorAsyncCall extends AsyncCall {
	PbDown.WorldCompetitionInfoRst.Builder builder;
	Player player;
	List<WorldCompetition> list;
	List<PbWorldCompetition.WorldCompetitor> competitors = new ArrayList();

	int errorNum = 0;

	public GetCompetitorAsyncCall(Player player, PbDown.WorldCompetitionInfoRst.Builder builder,
			List<WorldCompetition> list) {
		super(player.getSession(), null);
		this.builder = builder;
		this.player = player;
		this.list = list;
	}

	public void callback() {
		if (this.errorNum == 0) {
			this.builder.setResult(true);
			for (PbWorldCompetition.WorldCompetitor competitor : this.competitors)
				this.builder.addCompetitors(competitor);
		} else {
			this.builder.setResult(false);
			this.builder.setErrInfo("数据异常");
		}
		this.session.send(1080, this.builder.build());
	}

	public void netOrDB() {
		WorldCompetition competition = this.player.getWorldCompetition();
		Iterator itx = this.list.iterator();
		while (itx.hasNext()) {
			WorldCompetition wc = (WorldCompetition) itx.next();
			int scoreLevel = 0;
			MiniPlayer competitor = null;
			try {
				competitor = Platform.getPlayerManager().getMiniPlayer(wc.getPlayerId());
				scoreLevel = competition.getScoreLevelByScore(wc.getScore());
			} catch (Exception e) {
				this.errorNum = 1;
				Platform.getLog().logError("GetCompetitorAsyncCall get competitor exception,id:" + wc.getPlayerId(), e);
				return;
			}
			if (competitor == null) {
				this.errorNum = 1;
				Platform.getLog().logError("GetCompetitorAsyncCall get competitor null,id:" + wc.getPlayerId());
				return;
			}
			this.competitors.add(WorldCompetition.genWorldCompetitor(competitor, scoreLevel));
		}
	}
}
