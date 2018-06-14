package org.darcy.sanguo.worldcompetition;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;

import sango.packet.PbWorldCompetition;

public class WorldCompetition implements Serializable {
	private static final long serialVersionUID = 571819413493967927L;
	private int playerId;
	private int rank;
	private int score;
	private long lastLoad;
	private long lastReward;
	private List<Integer> enemy = new ArrayList();
	private long lastRefreshTime;
	private List<Integer> competitor = new ArrayList();

	public void init(Player player) {
		if (player.getSession() != null)
			reward(player);
	}

	private void reward(Player player) {
		Calendar now = Calendar.getInstance();
		Calendar cur = WorldCompetitionData.getCurRewardTime();
		Calendar last = Calendar.getInstance();
		last.setTimeInMillis(this.lastReward);

		if (now.getTime().before(cur.getTime())) {
			cur.add(3, -1);
		}
		if (last.getTimeInMillis() < cur.getTimeInMillis()) {
			List rewards = WorldCompetitionData.getRewardsByRank(this.rank);
			if ((rewards != null) && (rewards.size() > 0)) {
				if (this.rank > 0)
					MailService.sendSystemMail(5, player.getId(), "争霸赛每周排名奖励", MessageFormat.format(
							"<p style=21>恭喜主公，截止到本周六23:00，您在争霸赛中获得第</p><p style=20>{0}</p><p style=21>排名的好成绩，获得奖励如下：</p>",
							new Object[] { Integer.valueOf(this.rank) }), cur.getTime(), rewards);
				else {
					MailService.sendSystemMail(5, player.getId(), "争霸赛每周排名奖励",
							"<p style=21>很遗憾，截止到本周六23:00，您未能在争霸赛中获得任何名次。\n作为鼓励，我们给您准备了以下奖励。下周努力再战哦~：</p>",
							cur.getTime(), rewards);
				}
			}
			this.lastReward = cur.getTimeInMillis();
		}
	}

	public int canRefresh() {
		if (this.competitor == null) {
			this.competitor = new ArrayList();
			return 0;
		}
		if (this.competitor.size() == 0) {
			return 0;
		}
		long now = System.currentTimeMillis();
		if (this.lastRefreshTime + WorldCompetitionData.cd * 1000 > now) {
			return (int) Math.ceil((this.lastRefreshTime + WorldCompetitionData.cd * 1000 - now) / 1000L);
		}
		return 0;
	}

	public void refresh(List<Integer> competitor, boolean refreshTime) {
		this.competitor = competitor;
		if (refreshTime)
			this.lastRefreshTime = System.currentTimeMillis();
	}

	public void save() {
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this);
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getLastLoad() {
		return this.lastLoad;
	}

	public void setLastLoad(long lastLoad) {
		this.lastLoad = lastLoad;
	}

	public long getLastReward() {
		return this.lastReward;
	}

	public void setLastReward(long lastReward) {
		this.lastReward = lastReward;
	}

	public long getLastRefreshTime() {
		return this.lastRefreshTime;
	}

	public List<Integer> getCompetitor() {
		return this.competitor;
	}

	public void setCompetitor(List<Integer> competitor) {
		this.competitor = competitor;
	}

	public List<Integer> getEnemy() {
		return this.enemy;
	}

	public void setEnemy(List<Integer> enemy) {
		this.enemy = enemy;
	}

	public int getScoreLevelByScore(int score) {
		double ratio = score / this.score;
		for (int i = 0; i < WorldCompetitionData.scoreLimit.length; ++i) {
			if (i == 0) {
				if (ratio < WorldCompetitionData.scoreLimit[i])
					continue;
				return i;
			}

			if ((ratio >= WorldCompetitionData.scoreLimit[i]) && (ratio < WorldCompetitionData.scoreLimit[(i - 1)])) {
				return i;
			}
		}

		return 0;
	}

	public static WorldCompetition newWorldCompetition(int id) {
		WorldCompetition competition = new WorldCompetition();
		competition.setPlayerId(id);
		competition.setScore(WorldCompetitionData.baseScore);
		competition.setRank(Platform.getWorld().getNewWorldComeptitionCount());
		competition.setLastLoad(System.currentTimeMillis());
		Calendar lastRewardCal = WorldCompetitionData.getCurRewardTime();
		if (!(WorldCompetitionData.isOver(Calendar.getInstance()))) {
			lastRewardCal.add(3, -1);
		}
		competition.setLastReward(lastRewardCal.getTimeInMillis());
		return competition;
	}

	public static PbWorldCompetition.WorldCompetitor genWorldCompetitor(MiniPlayer player, int scoreLevel) {
		PbWorldCompetition.WorldCompetitor.Builder builder = PbWorldCompetition.WorldCompetitor.newBuilder();
		builder.setUser(player.genMiniUser());
		builder.setScoreLevel(scoreLevel);
		return builder.build();
	}

	public static PbWorldCompetition.WorldCompetitionRanker genWorldCompetitionRanker(MiniPlayer player, int rank,
			int score) {
		PbWorldCompetition.WorldCompetitionRanker.Builder builder = PbWorldCompetition.WorldCompetitionRanker
				.newBuilder();
		builder.setRank(rank);
		builder.setUser(player.genMiniUser());
		builder.setScore(score);
		return builder.build();
	}

	public static PbWorldCompetition.WorldCompetitionEnemy genWorldCompetitionEnemy(MiniPlayer player) {
		PbWorldCompetition.WorldCompetitionEnemy.Builder builder = PbWorldCompetition.WorldCompetitionEnemy
				.newBuilder();
		builder.setUser(player.genMiniUser());
		return builder.build();
	}
}
