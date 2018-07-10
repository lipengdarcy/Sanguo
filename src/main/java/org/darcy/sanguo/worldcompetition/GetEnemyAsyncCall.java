package org.darcy.sanguo.worldcompetition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.MiniPlayer;

import sango.packet.PbDown;

public class GetEnemyAsyncCall extends AsyncCall {
	List<Integer> ids;
	List<MiniPlayer> enemys = new ArrayList();

	int errorNum = 0;

	public GetEnemyAsyncCall(ClientSession session, List<Integer> ids) {
		super(session, null);
		this.ids = ids;
	}

	public void callback() {
		PbDown.WorldCompetitionEnemyRst.Builder builder = PbDown.WorldCompetitionEnemyRst.newBuilder();
		if (this.errorNum == 0) {
			builder.setResult(true);
			for (MiniPlayer enemy : this.enemys)
				builder.addEnemy(WorldCompetition.genWorldCompetitionEnemy(enemy));
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1088, builder.build());
	}

	public void netOrDB() {
		Iterator itx = this.ids.iterator();
		while (itx.hasNext()) {
			int playerId = ((Integer) itx.next()).intValue();
			MiniPlayer player = null;
			try {
				player = Platform.getPlayerManager().getMiniPlayer(playerId);
			} catch (Exception e) {
				this.errorNum = 1;
				Platform.getLog().logError("GetEnemyAsyncCall get enemy exception,id:" + playerId, e);
				return;
			}
			if (player == null) {
				this.errorNum = 1;
				Platform.getLog().logError("GetEnemyAsyncCall get enemy null,id:" + playerId);
				return;
			}
			this.enemys.add(player);
		}
	}
}
