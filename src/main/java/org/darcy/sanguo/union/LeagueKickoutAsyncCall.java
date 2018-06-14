package org.darcy.sanguo.union;

import java.text.MessageFormat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;

import sango.packet.PbDown;

public class LeagueKickoutAsyncCall extends AsyncCall {
	League l;
	int id;
	Player player;
	Union u;
	int errorNum = 0;

	public LeagueKickoutAsyncCall(Player player, League l, int id) {
		super(player.getSession(), null);
		this.l = l;
		this.id = id;
		this.player = player;
	}

	public void callback() {
		PbDown.LeagueKickoutRst.Builder b = PbDown.LeagueKickoutRst.newBuilder();
		if (this.errorNum == 0) {
			if (!(this.l.getMember(this.player.getId()).isLeader(this.l))) {
				b.setResult(false);
				b.setErrInfo("只有团长才可以进行操作");
			} else if (this.player.getId() == this.id) {
				b.setResult(false);
				b.setErrInfo("不能对团长进行操作");
			} else if (!(this.l.isMember(this.id))) {
				b.setResult(false);
				b.setErrInfo("该玩家尚未加入军团");
			} else {
				LeagueMember lm = this.l.getMember(this.id);
				int costBuild = lm.getKickedCostBuild();
				if (this.l.getBuildValue() < costBuild) {
					b.setResult(false);
					b.setErrInfo("军团建设不足");
				} else {
					this.l.quit(this.id, costBuild);
					this.u.quit();
					if (Platform.getPlayerManager().getPlayerById(this.id) == null) {
						UnionSaveCall call = new UnionSaveCall(this.u);
						Platform.getThreadPool().execute(call);
					}
					b.setResult(true);
					b.setBuildValue(this.l.getBuildValue());
					MailService.sendSystemMail(1, this.id,
							MessageFormat.format("您已被踢出了【{0}】军团", new Object[] { this.l.getName() }),
							MessageFormat.format(
									"<p style=21>很抱歉，您已被军团</p><p style=20>【{0}】</p><p style=21>的相关管理人员踢出军团~~\n抛弃你将是他们的损失！！快加入其他军团，证明你的实力吧！！</p>",
									new Object[] { this.l.getName() }));
					Platform.getLog().logLeague(this.l, "leaguekickout", this.id);
				}
			}
		} else {
			b.setResult(false);
			b.setErrInfo("操作失败，请稍后再试");
		}
		this.session.send(1188, b.build());
	}

	public void netOrDB() {
		Player player = Platform.getPlayerManager().getPlayerById(this.id);
		if (player != null) {
			this.u = player.getUnion();
		} else {
			try {
				this.u = ((Union) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Union.class,
						Integer.valueOf(this.id)));
			} catch (Exception e) {
				Platform.getLog().logError("LeagueQuitAsyncCall get union error, id:" + this.id, e);
				this.errorNum = 1;
				return;
			}
			if (this.u == null) {
				Platform.getLog().logError("LeagueQuitAsyncCall union null, id:" + this.id);
				this.errorNum = 1;
			}
		}
	}
}
