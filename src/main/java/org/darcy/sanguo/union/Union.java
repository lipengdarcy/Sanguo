package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;

public class Union {
	public static final int LEAGUE_ID_NO = 0;
	private int playerId;
	private int leagueId;
	private int contribution;
	private List<Integer> applys = new ArrayList();

	private Date lastExitTime = new Date(1262334094970L);

	private int bossSurplusNum = LeagueData.LEAGUE_BOSS_FIGHT_NUM;
	private long lastFightBoss;

	public void quit() {
		setLeagueId(0);
		this.lastExitTime = new Date();
	}

	public void save() {
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this);
	}

	public void syncContribution(Player p) {
		p.getDataSyncManager().addNumSync(17, this.contribution);
	}

	public long getColdTime() {
		long now = System.currentTimeMillis();
		return (this.lastExitTime.getTime() + LeagueData.LEAGUE_EXIT_COLD_TIME * 60 * 1000 - now);
	}

	public boolean isFullApplyNum() {
		return (this.applys.size() < LeagueData.LEAGUE_APPLY_NUM_LIMIT);
	}

	public boolean isApply(int id) {
		return this.applys.contains(Integer.valueOf(id));
	}

	public void apply(int id) {
		this.applys.add(new Integer(id));
	}

	public void removeApply(int id) {
		this.applys.remove(new Integer(id));
	}

	public void refresh(Player player) {
		this.bossSurplusNum = LeagueData.LEAGUE_BOSS_FIGHT_NUM;
		if (this.leagueId > 0) {
			League l = Platform.getLeagueManager().getLeagueById(this.leagueId);
			if (l == null)
				return;
			LeagueMember lm = l.getMember(player.getId());
			if (lm != null)
				lm.refresh();
		}
	}

	public void bossReward(Player player) {
		if (this.leagueId > 0) {
			League l = Platform.getLeagueManager().getLeagueById(this.leagueId);
			if (l == null)
				return;
			LeagueBoss lb = l.getInfo().getBoss();
			List<LeagueBossReward> list = (List) lb.rewards.get(Integer.valueOf(player.getId()));
			if (list != null) {
				for (LeagueBossReward reward : list) {
					if (reward != null) {
						reward.sendMail(player.getId(), false, l);
					}
				}
				lb.rewards.remove(Integer.valueOf(player.getId()));
			}
		}
	}

	public void build(Player p, LeagueBuildData data) {
		addContribution(p, data.contribution, "leaguebuild");
	}

	public void decContribution(Player p, int value, String optType) {
		value = Math.min(this.contribution, value);
		this.contribution -= value;
		syncContribution(p);
		Platform.getLog().logCost(p, "leaguecontribution", value, this.contribution, optType);
	}

	public void addContribution(Player p, int value, String optType) {
		if (value > 0) {
			this.contribution += value;
			syncContribution(p);
			Platform.getLog().logAcquire(p, "leaguecontribution", value, this.contribution, optType);
		}
	}

	public void fightBoss() {
		this.bossSurplusNum -= 1;
		if (this.bossSurplusNum < 0) {
			this.bossSurplusNum = 0;
		}
		this.lastFightBoss = System.currentTimeMillis();
	}

	public long getFightBossColdTime() {
		return (this.lastFightBoss + LeagueData.LEAGUE_BOSS_COLD_TIME * 60 * 1000 - System.currentTimeMillis());
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getLeagueId() {
		return this.leagueId;
	}

	public void setLeagueId(int leagueId) {
		Platform.getLeagueManager().updatePlayerLeague(this.playerId, leagueId);
		this.leagueId = leagueId;
	}

	public int getContribution() {
		return this.contribution;
	}

	public void setContribution(int contribution) {
		this.contribution = contribution;
	}

	public List<Integer> getApplys() {
		return this.applys;
	}

	public void setApplys(List<Integer> applys) {
		this.applys = applys;
	}

	public Date getLastExitTime() {
		return this.lastExitTime;
	}

	public void setLastExitTime(Date lastExitTime) {
		this.lastExitTime = lastExitTime;
	}

	public int getBossSurplusNum() {
		return this.bossSurplusNum;
	}

	public void setBossSurplusNum(int bossSurplusNum) {
		this.bossSurplusNum = bossSurplusNum;
	}

	public long getLastFightBoss() {
		return this.lastFightBoss;
	}

	public void setLastFightBoss(long lastFightBoss) {
		this.lastFightBoss = lastFightBoss;
	}
}
