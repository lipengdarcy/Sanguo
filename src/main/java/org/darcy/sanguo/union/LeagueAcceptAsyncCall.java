package org.darcy.sanguo.union;

import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.log.LogManager;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;

import sango.packet.PbDown;

public class LeagueAcceptAsyncCall extends AsyncCall {
	League l;
	int id;
	Union u;
	String name;
	int errorNum = 0;

	public LeagueAcceptAsyncCall(ClientSession session, League l, int id) {
		super(session, null);
		this.l = l;
		this.id = id;
	}

	public void callback() {
		PbDown.LeagueAcceptRst.Builder b = PbDown.LeagueAcceptRst.newBuilder().setResult(true);
		if (this.errorNum == 0) {
			if (this.l.isMember(this.id)) {
				b.setResult(false);
				b.setErrInfo("该玩家已在军团中");
			} else if (this.l.isFull()) {
				b.setResult(false);
				b.setErrInfo("该军团人数已达上限，请稍后再试!");
			} else if (!(this.l.isApply(this.id))) {
				b.setResult(false);
				b.setErrInfo("该玩家已取消申请或已加入其他军团");
			} else {
				this.l.addMember(this.id, this.name);
				this.u.setLeagueId(this.l.getId());

				List<Integer> applys = this.u.getApplys();
				if ((applys != null) && (applys.size() > 0)) {
					for (Integer lid : applys) {
						League league = Platform.getLeagueManager().getLeagueById(lid.intValue());
						if (league != null) {
							league.removeApply(this.id);
						}
					}
					this.u.getApplys().clear();
				}
				if (Platform.getPlayerManager().getPlayerById(this.id) == null) {
					UnionSaveCall call = new UnionSaveCall(this.u);
					Platform.getThreadPool().execute(call);
				}
				Platform.getLog().logLeague(this.l, "leaguejoin", this.id);
			}
		} else if (this.errorNum == 1) {
			b.setResult(false);
			b.setErrInfo("操作失败，请稍后再试");
		} else if (this.errorNum == 2) {
			b.setResult(false);
			b.setErrInfo("该玩家不存在");
		} else if (this.errorNum == 3) {
			b.setResult(false);
			b.setErrInfo("该玩家已取消申请或已加入其他军团");
		}
		this.session.send(1194, b.build());
	}

	public void netOrDB() {
		Player player = Platform.getPlayerManager().getPlayerById(this.id);
		if (player != null) {
			this.u = player.getUnion();
			this.name = player.getName();
		} else {
			LogManager log = Platform.getLog();
			try {
				this.u = ((Union) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Union.class,
						Integer.valueOf(this.id)));
			} catch (Exception e) {
				log.logError("LeagueAcceptAsyncCall get union error, id:" + this.id, e);
				this.errorNum = 1;
				return;
			}
			if (this.u == null) {
				log.logError("LeagueAcceptAsyncCall union null, id:" + this.id);
				this.errorNum = 3;
				return;
			}
			MiniPlayer mp = Platform.getPlayerManager().getMiniPlayer(this.id);
			if (mp == null) {
				log.logError("LeagueAcceptAsyncCall miniplayer is null, id:" + this.id);
				this.errorNum = 2;
				return;
			}
			this.name = mp.getName();
		}
	}
}
