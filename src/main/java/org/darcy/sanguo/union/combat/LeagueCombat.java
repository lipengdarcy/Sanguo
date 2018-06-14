package org.darcy.sanguo.union.combat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.LeagueManager;
import org.darcy.sanguo.updater.Updatable;

import sango.packet.PbDown;
import sango.packet.PbLeague;

public class LeagueCombat implements Updatable {
	public static final int END_HOUR = 20;
	public static final int END_MIN = 10;
	public static final int MIN_MEMBERS = 20;
	private CombatData data;
	private Map<Integer, Long> lootTimes = new HashMap();

	private Map<Integer, Long> randomFightTimes = new HashMap();
	private PbDown.LCInfoRst.LCState state;
	private List<LeagueCombatRank> tmpRanks = new ArrayList();
	private List<LeagueCombatRank> tmpLastRanks = new ArrayList();

	public void init() {
	}

	public void start(long m) {
		for (Pair pair : this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)).values())
			for (City city : pair.getCities())
				city.setTimeMark(m);
	}

	public void reOrder() {
		for (Pair pair : this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)).values()) {
			int lid = pair.getOffenLeagueId();
			boolean oWin = pair.isWin(lid);
			LeagueCombatRank rd = (LeagueCombatRank) this.data.rankData.get(Integer.valueOf(lid));
			if (rd == null) {
				rd = new LeagueCombatRank();
				rd.setAccumulateScores(pair.getAccumulateScore(lid));
				rd.setLeagueId(lid);
				rd.setLoseCount((oWin) ? 0 : 1);
				rd.setWinCount((oWin) ? 1 : 0);
				rd.setWonScores(pair.getWinScore(lid));
				this.data.rankData.put(Integer.valueOf(lid), rd);
			} else {
				rd.setAccumulateScores(pair.getAccumulateScore(lid) + rd.getAccumulateScores());
				rd.setLoseCount(((oWin) ? 0 : 1) + rd.getLoseCount());
				rd.setWinCount(((oWin) ? 1 : 0) + rd.getWinCount());
				rd.setWonScores(pair.getWinScore(lid) + rd.getWonScores());
			}

			lid = pair.getDeffenLeagueId();
			if (lid != -1) {
				rd = (LeagueCombatRank) this.data.rankData.get(Integer.valueOf(lid));
				if (rd == null) {
					rd = new LeagueCombatRank();
					rd.setAccumulateScores(pair.getAccumulateScore(lid));
					rd.setLeagueId(lid);
					rd.setLoseCount((oWin) ? 1 : 0);
					rd.setWinCount((oWin) ? 0 : 1);
					rd.setWonScores(pair.getWinScore(lid));
					this.data.rankData.put(Integer.valueOf(lid), rd);
				} else {
					rd.setAccumulateScores(pair.getAccumulateScore(lid) + rd.getAccumulateScores());
					rd.setLoseCount(((oWin) ? 1 : 0) + rd.getLoseCount());
					rd.setWinCount(((oWin) ? 0 : 1) + rd.getWinCount());
					rd.setWonScores(pair.getWinScore(lid) + rd.getWonScores());
				}
			}
		}

		this.tmpRanks = new ArrayList(this.data.rankData.values());
		Collections.sort(this.tmpRanks, new Comparator<LeagueCombatRank>() {
			public int compare(LeagueCombatRank o1, LeagueCombatRank o2) {
				if (o1.getAccumulateScores() == o2.getAccumulateScores()) {
					return (o2.getWonScores() - o1.getWonScores());
				}
				return (o2.getAccumulateScores() - o1.getAccumulateScores());
			}
		});
	}

	public Pair getPair(int lid) {
		return ((Pair) ((Map) this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)))
				.get(Integer.valueOf(lid)));
	}

	public void setLootDieTime(int pid) {
		this.lootTimes.put(Integer.valueOf(pid), Long.valueOf(System.currentTimeMillis()));
	}

	public void setRandomFight(int pid) {
		this.randomFightTimes.put(Integer.valueOf(pid), Long.valueOf(System.currentTimeMillis()));
	}

	public boolean isFighting(int lid) {
		if (this.state == PbDown.LCInfoRst.LCState.FIGHTING) {
			Pair pair = (Pair) ((Map) this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)))
					.get(Integer.valueOf(lid));
			if (pair != null) {
				return pair.isFighting();
			}
		}
		return false;
	}

	public int getRank(int lid) {
		for (int i = 0; i < this.tmpRanks.size(); ++i) {
			LeagueCombatRank rank = (LeagueCombatRank) this.tmpRanks.get(i);
			if (rank.getLeagueId() == lid) {
				return (i + 1);
			}
		}
		return -1;
	}

	public League getTargetLeague(int lid) {
		Pair pair = (Pair) ((Map) this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)))
				.get(Integer.valueOf(lid));
		if (pair != null) {
			int tid = pair.getTargetLid(lid);
			return Platform.getLeagueManager().getLeagueById(tid);
		}
		return null;
	}

	public PbLeague.LCFightingInfo.Builder genFightInfo(int lid) {
		PbLeague.LCFightingInfo.Builder info = PbLeague.LCFightingInfo.newBuilder();
		Pair pair = (Pair) ((Map) this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)))
				.get(Integer.valueOf(lid));
		if (pair == null)
			return null;
		info.setMyResource(pair.genMyResource(lid)).setTargetResource(pair.genTargetResource(lid));
		for (City city : pair.getCities()) {
			info.addCities(city.genPb());
		}
		return info;
	}

	public long getLeftRandomTime(int pid) {
		Long t = (Long) this.randomFightTimes.get(Integer.valueOf(pid));
		if (t != null) {
			long rst = 30000L - (System.currentTimeMillis() - t.longValue());
			if (rst < 0L) {
				return 0L;
			}
			if (rst > 30000L) {
				this.randomFightTimes.put(Integer.valueOf(pid), Long.valueOf(System.currentTimeMillis()));
				return 30000L;
			}
			return rst;
		}
		return 0L;
	}

	public long getLeftLootTime(int pid) {
		Long t = (Long) this.lootTimes.get(Integer.valueOf(pid));
		if (t != null) {
			long rst = System.currentTimeMillis() - t.longValue();
			rst = 30000L - rst;
			if (rst < 0L) {
				return 0L;
			}
			if (rst > 30000L) {
				this.lootTimes.put(Integer.valueOf(pid), Long.valueOf(System.currentTimeMillis()));
				return 30000L;
			}
			return rst;
		}
		return 0L;
	}

	public void newRound(int day) {
		if ((this.data.currentRound == 5) || (this.data.currentRound < 1) || (day == 2)) {
			this.data.currentRound = 1;
			this.data.lastCombatPairs = this.data.currentCombatPairs;
			this.data.lastRankData = this.data.rankData;
			this.tmpLastRanks = this.tmpRanks;

			this.data.currentCombatPairs = new HashMap();
			this.data.rankData = new HashMap();
			this.tmpRanks = new ArrayList();
			this.data.currentPeriod += 1;
		} else {
			this.data.currentRound += 1;
		}

		group();
		pair();

		this.state = PbDown.LCInfoRst.LCState.PRPARE;
	}

	private void group() {
		int count;
		LeagueManager lm = Platform.getLeagueManager();
		if (Calendar.getInstance().get(7) == 2) {
			this.data.deffens.clear();
			this.data.offens.clear();
			Collection<League> list = lm.getLeagues().values();
			count = 0;
			for (League l : list) {
				if (l.getInfo().getMembers().size() >= 20)
					if (count++ % 2 == 0)
						this.data.deffens.add(Integer.valueOf(l.getId()));
					else
						this.data.offens.add(Integer.valueOf(l.getId()));
			}
		} else {
			for (League l : lm.getLeagues().values()) {
				if ((this.data.deffens.contains(Integer.valueOf(l.getId())))
						|| (this.data.offens.contains(Integer.valueOf(l.getId())))
						|| (l.getInfo().getMembers().size() < 20))
					continue;
				this.data.offens.add(Integer.valueOf(l.getId()));
			}
		}
	}

	private void pair() {
		Pair p;
		HashMap pairs = new HashMap();
		this.data.currentCombatPairs.put(Integer.valueOf(this.data.currentRound), pairs);

		Collections.shuffle(this.data.deffens);
		Collections.shuffle(this.data.offens);

		List list = this.data.offens;
		for (int i = 0; i < list.size(); i += 2) {
			if (i + 1 == list.size()) {
				p = new Pair();
				p.setOffenLeagueId(((Integer) list.get(i)).intValue());
				pairs.put((Integer) list.get(i), p);
			} else {
				p = new Pair();
				p.setDeffenLeagueId(((Integer) list.get(i)).intValue());
				p.setOffenLeagueId(((Integer) list.get(i + 1)).intValue());
				pairs.put((Integer) list.get(i), p);
				pairs.put((Integer) list.get(i + 1), p);
			}
		}

		list = this.data.deffens;
		for (int i = 0; i < list.size(); i += 2) {
			if (i + 1 == list.size()) {
				p = new Pair();
				p.setOffenLeagueId(((Integer) list.get(i)).intValue());
				pairs.put((Integer) list.get(i), p);
			} else {
				p = new Pair();
				p.setDeffenLeagueId(((Integer) list.get(i)).intValue());
				p.setOffenLeagueId(((Integer) list.get(i + 1)).intValue());
				pairs.put((Integer) list.get(i), p);
				pairs.put((Integer) list.get(i + 1), p);
			}
		}

		this.data.offens.clear();
		this.data.deffens.clear();
	}

	public static PbDown.LCInfoRst.LCState getTimedState() {
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(7);
		if ((dayOfWeek == 1) || (dayOfWeek == 7))
			return PbDown.LCInfoRst.LCState.END;
		if (dayOfWeek == 6) {
			if (cal.get(11) < 20)
				return PbDown.LCInfoRst.LCState.PRPARE;
			if ((cal.get(11) == 20) && (cal.get(12) < 10)) {
				return PbDown.LCInfoRst.LCState.FIGHTING;
			}
			return PbDown.LCInfoRst.LCState.END;
		}

		if (cal.get(11) < 20)
			return PbDown.LCInfoRst.LCState.PRPARE;
		if ((cal.get(11) == 20) && (cal.get(12) < 10)) {
			return PbDown.LCInfoRst.LCState.FIGHTING;
		}
		return PbDown.LCInfoRst.LCState.REST;
	}

	public static void main(String[] args) {
		System.out.println(getTimedState());
		long s = System.currentTimeMillis();
		for (int i = 0; i < 10000000; ++i) {
			getTimedState();
		}
		System.out.println(System.currentTimeMillis() - s);
	}

	public int getCurrentRound() {
		return this.data.currentRound;
	}

	public int getCurrentPeriod() {
		return this.data.currentPeriod;
	}

	public List<Integer> getWinners() {
		return this.data.deffens;
	}

	public List<Integer> getLosers() {
		return this.data.offens;
	}

	public Map<Integer, Map<Integer, Pair>> getCurrentCombatPairs() {
		return this.data.currentCombatPairs;
	}

	public Map<Integer, Map<Integer, Pair>> getLastCombatPairs() {
		return this.data.lastCombatPairs;
	}

	public Map<Integer, LeagueCombatRank> getRankData() {
		return this.data.rankData;
	}

	public Map<Integer, Long> getLootTimes() {
		return this.lootTimes;
	}

	public Map<Integer, Long> getRandomFightTimes() {
		return this.randomFightTimes;
	}

	public void setCurrentRound(int currentRound) {
		this.data.currentRound = currentRound;
	}

	public void setCurrentPeriod(int currentPeriod) {
		this.data.currentPeriod = currentPeriod;
	}

	public void setWinners(List<Integer> winners) {
		this.data.deffens = winners;
	}

	public void setLosers(List<Integer> losers) {
		this.data.offens = losers;
	}

	public void setCurrentCombatPairs(Map<Integer, Map<Integer, Pair>> currentCombatPairs) {
		this.data.currentCombatPairs = currentCombatPairs;
	}

	public void setLastCombatPairs(Map<Integer, Map<Integer, Pair>> lastCombatPairs) {
		this.data.lastCombatPairs = lastCombatPairs;
	}

	public void setRankData(Map<Integer, LeagueCombatRank> rankData) {
		this.data.rankData = rankData;
	}

	public List<LeagueCombatRank> getTmpRanks() {
		return this.tmpRanks;
	}

	public void setTmpRanks(List<LeagueCombatRank> tmpRanks) {
		this.tmpRanks = tmpRanks;
	}

	public void setLootTimes(Map<Integer, Long> lootTimes) {
		this.lootTimes = lootTimes;
	}

	public void setRandomFightTimes(Map<Integer, Long> randomFightTimes) {
		this.randomFightTimes = randomFightTimes;
	}

	public PbDown.LCInfoRst.LCState getState() {
		return this.state;
	}

	public void setState(PbDown.LCInfoRst.LCState state) {
		this.state = state;
	}

	public Map<Integer, LeagueCombatRank> getLastRankData() {
		return this.data.lastRankData;
	}

	public List<LeagueCombatRank> getTmpLastRanks() {
		return this.tmpLastRanks;
	}

	public void setLastRankData(Map<Integer, LeagueCombatRank> lastRankData) {
		this.data.lastRankData = lastRankData;
	}

	public void setTmpLastRanks(List<LeagueCombatRank> tmpLastRanks) {
		this.tmpLastRanks = tmpLastRanks;
	}

	public void endAll() {
		for (Pair pair : this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)).values())
			if (!(pair.isEnd()))
				((LeagueCombatService) Platform.getServiceManager().get(LeagueCombatService.class)).end(pair);
	}

	public boolean update() {
		this.state = getTimedState();
		if ((this.state == PbDown.LCInfoRst.LCState.FIGHTING) && (this.data.currentRound != 0)) {
			for (Pair pair : this.data.currentCombatPairs.get(Integer.valueOf(this.data.currentRound)).values()) {
				if ((!(pair.isEnd())) && (pair.endCheck())) {
					((LeagueCombatService) Platform.getServiceManager().get(LeagueCombatService.class)).end(pair);
				}
			}
		}

		return false;
	}

	public CombatData getData() {
		return this.data;
	}

	public void setData(CombatData data) {
		this.data = data;

		this.tmpRanks = new ArrayList(data.rankData.values());
		Collections.sort(this.tmpRanks, new Comparator<LeagueCombatRank>() {
			public int compare(LeagueCombatRank o1, LeagueCombatRank o2) {
				if (o1.getAccumulateScores() == o2.getAccumulateScores()) {
					return (o2.getWonScores() - o1.getWonScores());
				}
				return (o2.getAccumulateScores() - o1.getAccumulateScores());
			}
		});
		this.tmpLastRanks = new ArrayList(data.lastRankData.values());
		Collections.sort(this.tmpLastRanks, new Comparator<LeagueCombatRank>() {
			public int compare(LeagueCombatRank o1, LeagueCombatRank o2) {
				if (o1.getAccumulateScores() == o2.getAccumulateScores()) {
					return (o2.getWonScores() - o1.getWonScores());
				}
				return (o2.getAccumulateScores() - o1.getAccumulateScores());
			}
		});
	}
}
