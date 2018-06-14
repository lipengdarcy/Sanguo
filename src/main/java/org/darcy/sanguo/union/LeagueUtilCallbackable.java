package org.darcy.sanguo.union;

import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.CallBackable;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;

public class LeagueUtilCallbackable implements CallBackable {
	public static final int TYPE_LEAGUE_CLEAN_APPLY = 1;
	public static final int TYPE_LEAGUE_CLEAN_MEMBER = 2;
	public static final int TYPE_PLAYER_CLEAN_APPLY = 3;
	public static final int TYPE_PLAYER_QUIT = 4;
	public static final int TYPE_LEAGUE_ADD_BUILD = 5;
	public int type;
	public int leagueId;
	public int playerId;
	public Object[] params;

	public void callback() {
		League l;
		Union u;
		if (this.type == 1) {
			l = Platform.getLeagueManager().getLeagueById(this.leagueId);
			if (l == null) {
				Platform.getLog().logSystem(
						"[LeagueUtil][League][CleanApply][Failure][No League][league:" + this.leagueId + "]");
				return;
			}
			l.removeApply(this.playerId);
			Platform.getLog().logSystem("[LeagueUtil][League][CleanApply][Success][League Clean][league:"
					+ this.leagueId + ", Union:" + this.playerId + "]");
			u = getUnion(this.playerId, 1);
			if (u == null) {
				Platform.getLog().logSystem("[LeagueUtil][League][CleanApply][Failure][No Union][league:"
						+ this.leagueId + ", Union:" + this.playerId + "]");
				return;
			}
			u.removeApply(this.leagueId);
			save(u);
			Platform.getLog().logSystem("[LeagueUtil][League][CleanApply][Success][Union Clean][league:" + this.leagueId
					+ ", Union:" + this.playerId + "]");
		} else if (this.type == 2) {
			l = Platform.getLeagueManager().getLeagueById(this.leagueId);
			if (l == null) {
				Platform.getLog().logSystem(
						"[LeagueUtil][League][CleanMember][Failure][No League][league:" + this.leagueId + "]");
				return;
			}
			if (!(l.isMember(this.playerId))) {
				Platform.getLog().logSystem("[LeagueUtil][League][CleanMember][Failure][Not Member][league:"
						+ this.leagueId + ", player:" + this.playerId + "]");
				return;
			}
			if (l.getMember(this.playerId).isLeader(l)) {
				Platform.getLog().logSystem("[LeagueUtil][League][CleanMember][Failure][Is Leader][league:"
						+ this.leagueId + ", player:" + this.playerId + "]");
				return;
			}
			l.quit(this.playerId, 0);
			Platform.getLog().logSystem("[LeagueUtil][League][CleanMember][Success][League Clean][league:"
					+ this.leagueId + ", player:" + this.playerId + "]");
			u = getUnion(this.playerId, 2);
			if (u == null) {
				Platform.getLog().logSystem("[LeagueUtil][League][CleanMember][Failure][No Union][league:"
						+ this.leagueId + ", Union:" + this.playerId + "]");
				return;
			}
			u.quit();
			save(u);
			if (l.getMemberCount() == 0) {
				Platform.getLeagueManager().removeLeague(l);
			}
			Platform.getLog().logSystem("[LeagueUtil][League][CleanMember][Success][Union Clean][league:"
					+ this.leagueId + ", player:" + this.playerId + "]");
		} else {
			if (this.type == 3) {
				u = getUnion(this.playerId, 3);
				if (u == null) {
					Platform.getLog().logSystem(
							"[LeagueUtil][Player][CleanMember][Failure][No Union][Player:" + this.playerId + "]");
					return;
				}
				List<Integer> applys = u.getApplys();
				if ((applys != null) && (applys.size() > 0)) {
					for (Integer lid : applys) {
						League league = Platform.getLeagueManager().getLeagueById(lid.intValue());
						if (league != null) {
							league.removeApply(u.getPlayerId());
						}
					}
					u.getApplys().clear();
				}
				save(u);
				Platform.getLog().logSystem("[LeagueUtil][Player][CleanApply][Success][Player:" + this.playerId + "]");
			} else if (this.type == 4) {
				u = getUnion(this.playerId, 3);
				if (u == null) {
					Platform.getLog()
							.logSystem("[LeagueUtil][Player][Quit][Failure][No Union][Player:" + this.playerId + "]");
					return;
				}
				if (u.getLeagueId() < 1) {
					Platform.getLog()
							.logSystem("[LeagueUtil][Player][Quit][Failure][Not Join][Player:" + this.playerId + "]");
					return;
				}
				l = Platform.getLeagueManager().getLeagueById(u.getLeagueId());
				if ((l != null) && (l.isMember(this.playerId)) && (l.getMember(this.playerId).isLeader(l))) {
					Platform.getLog().logSystem("[LeagueUtil][Player][Quit][Failure][Is Leader][league:" + l.getId()
							+ ", player:" + this.playerId + "]");
					return;
				}
				u.quit();
				save(u);
				Platform.getLog()
						.logSystem("[LeagueUtil][Player][Quit][Success][Union Clean][player:" + this.playerId + "]");
				if (l == null) {
					Platform.getLog()
							.logSystem("[LeagueUtil][Player][Quit][Failure][No League][league:" + this.leagueId + "]");
					return;
				}

				l.quit(this.playerId, 0);
				if (l.getMemberCount() == 0) {
					Platform.getLeagueManager().removeLeague(l);
				}
				Platform.getLog().logSystem("[LeagueUtil][Player][Quit][Success][League Clean][league:" + l.getId()
						+ ", player:" + this.playerId + "]");
			} else if (this.type == 5) {
				l = Platform.getLeagueManager().getLeagueById(this.leagueId);
				if (l == null) {
					Platform.getLog().logSystem(
							"[LeagueUtil][League][AddBuild][Failure][No League][league:" + this.leagueId + "]");
					return;
				}
				if ((this.params[0] == null) || (!(this.params[0] instanceof Integer))) {
					Platform.getLog().logSystem(
							"[LeagueUtil][League][AddBuild][Failure][Param error][league:" + l.getId() + "]");
					return;
				}
				int value = Math.max(0, l.getBuildValue() + ((Integer) this.params[0]).intValue());
				l.setBuildValue(value);
				Platform.getLog().logSystem("[LeagueUtil][League][AddBuild][Success][league:" + l.getId() + ", value:"
						+ this.params[0] + "]");
			}
		}
	}

	private Union getUnion(int playerId, int type) {
		Union u = null;
		Player player = Platform.getPlayerManager().getPlayerById(playerId);
		if (player != null)
			u = player.getUnion();
		else {
			try {
				u = (Union) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Union.class,
						Integer.valueOf(playerId));
			} catch (Exception e) {
				Platform.getLog().logError("LeaugeCleanCallbackable get union error,type:" + type + ", id:" + playerId,
						e);
			}
		}
		return u;
	}

	private void save(Union u) {
		if (Platform.getPlayerManager().getPlayerById(u.getPlayerId()) == null) {
			UnionSaveCall call = new UnionSaveCall(u);
			Platform.getThreadPool().execute(call);
		}
	}
}
