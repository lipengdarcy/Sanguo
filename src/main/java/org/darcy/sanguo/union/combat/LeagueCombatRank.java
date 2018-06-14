package org.darcy.sanguo.union.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.union.League;

import sango.packet.PbLeague;

public class LeagueCombatRank {
	private int leagueId;
	private int winCount;
	private int loseCount;
	private int accumulateScores;
	private int wonScores;

	public PbLeague.LCRankNode.Builder genPb(int rank) {
		PbLeague.LCRankNode.Builder b = PbLeague.LCRankNode.newBuilder();
		League l = Platform.getLeagueManager().getLeagueById(this.leagueId);
		MiniPlayer leader = Platform.getPlayerManager().getMiniPlayer(l.getLeader());
		b.setAccumulateScore(this.accumulateScores).setCount(l.getInfo().getMembers().size())
				.setLeaderName(leader.getName()).setLevel(l.getLevel()).setLose(this.loseCount)
				.setMaxCount(l.getMemberLimit()).setName(l.getName()).setRank(rank).setWin(this.winCount)
				.setWinScore(this.wonScores);
		return b;
	}

	public int getLeagueId() {
		return this.leagueId;
	}

	public int getWinCount() {
		return this.winCount;
	}

	public int getLoseCount() {
		return this.loseCount;
	}

	public int getAccumulateScores() {
		return this.accumulateScores;
	}

	public int getWonScores() {
		return this.wonScores;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	public void setLoseCount(int loseCount) {
		this.loseCount = loseCount;
	}

	public void setAccumulateScores(int accumulateScores) {
		this.accumulateScores = accumulateScores;
	}

	public void setWonScores(int wonScores) {
		this.wonScores = wonScores;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.leagueId);
		out.writeInt(this.winCount);
		out.writeInt(this.loseCount);
		out.writeInt(this.accumulateScores);
		out.writeInt(this.wonScores);
	}

	public static LeagueCombatRank readObject(ObjectInputStream in) throws IOException {
		LeagueCombatRank l = new LeagueCombatRank();

		l.leagueId = in.readInt();
		l.winCount = in.readInt();
		l.loseCount = in.readInt();
		l.accumulateScores = in.readInt();
		l.wonScores = in.readInt();

		return l;
	}
}
