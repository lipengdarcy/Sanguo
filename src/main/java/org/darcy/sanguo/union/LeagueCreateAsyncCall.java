package org.darcy.sanguo.union;

import java.text.MessageFormat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.asynccall.AsyncUpdater;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;

import sango.packet.PbDown;

public class LeagueCreateAsyncCall extends AsyncCall {
	Player player;
	String name;
	int type;
	League l;
	int errorNum = 0;

	public LeagueCreateAsyncCall(Player player, String name, int type) {
		super(player.getSession(), null);
		this.player = player;
		this.name = name;
		this.type = type;
	}

	public void callback() {
		PbDown.LeagueCreateRst.Builder b = PbDown.LeagueCreateRst.newBuilder();
		if (this.errorNum == 0) {
			b.setResult(true);

			if (Platform.getLeagueManager().getLeagueByName(this.name) != null) {
				this.l.setName(this.l.getName() + "1");
			}

			this.l.setRank(Platform.getLeagueManager().getLeagueCount() + 1);
			Platform.getLeagueManager().addLeague(this.l);

			this.player.getUnion().setLeagueId(this.l.getId());

			this.l.refreshRareGoods();

			AsyncUpdater updater = new AsyncUpdater(this.l);
			Platform.getThreadPool().execute(updater);

			if (this.type == 1)
				this.player.decJewels(Math.min(this.player.getJewels(), LeagueData.LEAGUE_CREATE_COST_JEWEL),
						"leaguecreate");
			else if (this.type == 2) {
				this.player.decMoney(Math.min(this.player.getMoney(), LeagueData.LEAGUE_CREATE_COST_MONEY),
						"leaguecreate");
			}
			Platform.getPlayerManager().boardCast(MessageFormat.format(
					"<p style=13>[{0}]</p><p style=17>创建了军团</p><p style=13>[{1}]</p><p style=17>，邀请广大志同道合的朋友加入！</p>",
					new Object[] { this.player.getName(), this.name }));
			Platform.getLog().logLeague(this.l, "leaguecreate", this.player.getId());
		} else {
			b.setResult(false);
			b.setErrInfo("操作失败，请稍后再试");
		}
		this.session.send(1178, b.build());
	}

	public void netOrDB() {
		try {
			League l = new League();
			l.setName(this.name);
			l.setLeader(this.player.getId());
			l.addMember(this.player.getId(), this.player.getName());

			l.getInfo().getBoss().initBoss(0);

			((DbService) Platform.getServiceManager().get(DbService.class)).add(l);

			this.l = l;
		} catch (Exception e) {
			Platform.getLog().logError(e);
			this.errorNum = 1;
		}
	}
}
