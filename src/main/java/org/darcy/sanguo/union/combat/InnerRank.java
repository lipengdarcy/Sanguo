package org.darcy.sanguo.union.combat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.union.League;

import sango.packet.PbLeague;

public class InnerRank {
	private int pid;
	private int lose;
	private int win;
	private int score;

	public PbLeague.LeagueJob getLeagueJob(League l, int pid) {
		if (l.getLeader() == pid)
			return PbLeague.LeagueJob.LEADER;
		if (l.getInfo().getViceleaders().contains(Integer.valueOf(pid))) {
			return PbLeague.LeagueJob.VICELEADER;
		}
		return PbLeague.LeagueJob.MEMBER;
	}

	public PbLeague.LCInnerNode.Builder genPb(League l) {
		PbLeague.LCInnerNode.Builder b = PbLeague.LCInnerNode.newBuilder();
		MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(this.pid);
		b.setBtlCapa(mini.getBtlCapability()).setJob(getLeagueJob(l, this.pid)).setLevel(mini.getLevel())
				.setLose(this.lose).setWin(this.win).setName(mini.getName()).setScores(this.score);
		return b;
	}

	public void addLose(Integer count) {
		if (count != null)
			this.lose += count.intValue();
	}

	public void addWin(Integer count) {
		if (count != null)
			this.win += count.intValue();
	}

	public void addScore(Integer count) {
		if (count != null)
			this.score += count.intValue();
	}

	public int getPid() {
		return this.pid;
	}

	public int getLose() {
		return this.lose;
	}

	public int getWin() {
		return this.win;
	}

	public int getScore() {
		return this.score;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
