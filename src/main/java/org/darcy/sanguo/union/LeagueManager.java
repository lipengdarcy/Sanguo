package org.darcy.sanguo.union;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.union.combat.LeagueCombat;
import org.darcy.sanguo.util.DBUtil;

public class LeagueManager {
	private ConcurrentHashMap<Integer, League> idLeagues = new ConcurrentHashMap();

	private Map<Integer, League> rankLeagues = new HashMap();

	private Map<Integer, Integer> playerLeagues = new HashMap();
	private LeagueCombat combat = new LeagueCombat();

	public void init() {
		List list = DBUtil.getAllLeague();
		if ((list != null) && (list.size() > 0)) {
			League l;
			Set rankSet = new HashSet();
			for (Iterator localIterator1 = list.iterator(); localIterator1.hasNext();) {
				l = (League) localIterator1.next();
				rankSet.add(Integer.valueOf(l.getRank()));
			}
			if (list.size() != rankSet.size()) {
				Platform.getLog().logSystem("league rank error!! start recorrect...");
				Collections.sort(list, new LeagueSortComparator());

				for (int i = 1; i <= list.size(); ++i) {
					((League) list.get(i - 1)).setRank(i);
				}
				Platform.getLog().logSystem("league rank recorrect finish !!");
			}

			for (Iterator localIterator1 = list.iterator(); localIterator1.hasNext();) {
				l = (League) localIterator1.next();
				l.setMemberLimit(LeagueService.getLimit(l.getLevel()));
				addLeague(l);

				for (Iterator localIterator2 = l.getInfo().getMembers().keySet().iterator(); localIterator2
						.hasNext();) {
					int playerId = ((Integer) localIterator2.next()).intValue();
					this.playerLeagues.put(Integer.valueOf(playerId), Integer.valueOf(l.getId()));
				}
			}
		}
		new Thread(new LeagueUpdateRunnable(), "UpdateLeagueThread").start();
		Platform.getBossManager().refreshLeagueRank();
		this.combat = new LeagueCombat();
	}

	public LeagueCombat getCombat() {
		return this.combat;
	}

	public void setCombat(LeagueCombat combat) {
		this.combat = combat;
	}

	public void refreshLottery() {
		for (League l : this.idLeagues.values())
			l.getInfo().refreshLottery(l.getLevel());
	}

	public List<League> getRank(int count) {
		List list = new ArrayList();
		for (int i = 0; i < this.rankLeagues.size(); ++i) {
			if (list.size() >= count)
				break;
			League l = (League) this.rankLeagues.get(Integer.valueOf(i + 1));
			if (l != null) {
				list.add(l);
			}
		}

		return list;
	}

	public int getLeagueCount() {
		return this.idLeagues.size();
	}

	public void addLeague(League l) {
		this.idLeagues.put(Integer.valueOf(l.getId()), l);
		this.rankLeagues.put(Integer.valueOf(l.getRank()), l);
		Platform.getBossManager().addToLeagueRank(l.getId());
	}

	public void removeLeague(League l) {
		this.idLeagues.remove(Integer.valueOf(l.getId()));
		this.rankLeagues.remove(Integer.valueOf(l.getRank()));
		removeSort(l.getRank());
		Platform.getBossManager().removeLeagueRank(l.getId());

		LeagueDismissAsyncCall call = new LeagueDismissAsyncCall(l);
		Platform.getThreadPool().execute(call);
	}

	public void updatePlayerLeague(int playerId, int leagueId) {
		if (leagueId == 0)
			this.playerLeagues.remove(Integer.valueOf(playerId));
		else
			this.playerLeagues.put(Integer.valueOf(playerId), Integer.valueOf(leagueId));
	}

	public League getLeagueByPlayerId(int playerId) {
		return getLeagueById(getLeagueIdByPlayerId(playerId));
	}

	public int getLeagueIdByPlayerId(int playerId) {
		if (this.playerLeagues.containsKey(Integer.valueOf(playerId))) {
			return ((Integer) this.playerLeagues.get(Integer.valueOf(playerId))).intValue();
		}
		return 0;
	}

	public void worldBossDamageClear() {
		for (League l : this.idLeagues.values())
			if (l != null) {
				l.getInfo().setWorldBossDamage(0);
				l.getInfo().setWorldBossInspireCount(0);
			}
	}

	public void saveAll() {
		List list = new ArrayList(this.idLeagues.values());
		Platform.getEntityManager().updateBatch(list);
	}

	public Map<Integer, League> getRankLeagues() {
		return this.rankLeagues;
	}

	public Map<Integer, League> getLeagues() {
		return this.idLeagues;
	}

	public League getLeagueById(int id) {
		return ((League) this.idLeagues.get(Integer.valueOf(id)));
	}

	public League getLeagueByRank(int rank) {
		return ((League) this.rankLeagues.get(Integer.valueOf(rank)));
	}

	public League getLeagueByName(String name) {
		for (League l : this.idLeagues.values()) {
			if (l.getName().equals(name)) {
				return l;
			}
		}
		return null;
	}

	public void sort(League l) {
		if (l.getRank() == 1) {
			return;
		}
		int start = l.getRank() - 1;
		for (int i = start; i > 0; --i) {
			League tmp = (League) this.rankLeagues.get(Integer.valueOf(i));
			boolean swap = false;
			if (l.getLevel() > tmp.getLevel()) {
				tmp.setRank(i + 1);
				this.rankLeagues.put(Integer.valueOf(i + 1), tmp);
				swap = true;
			} else if ((l.getLevel() == tmp.getLevel()) && (l.getCostBuildValue() > tmp.getCostBuildValue())) {
				tmp.setRank(i + 1);
				this.rankLeagues.put(Integer.valueOf(i + 1), tmp);
				swap = true;
			}

			if (swap) {
				if (i != 1)
					continue;
				l.setRank(i);
				this.rankLeagues.put(Integer.valueOf(i), l);
				return;
			}

			l.setRank(i + 1);
			this.rankLeagues.put(Integer.valueOf(i + 1), l);
			return;
		}
	}

	public void removeSort(int rank) {
		int i = rank + 1;
		int num = this.rankLeagues.size();
		for (; i <= num + 1; ++i) {
			League tmp = (League) this.rankLeagues.get(Integer.valueOf(i));
			tmp.setRank(i - 1);
			this.rankLeagues.put(Integer.valueOf(tmp.getRank()), tmp);
		}
		this.rankLeagues.remove(Integer.valueOf(num + 1));
	}

	public void recall() {
		for (League league : this.idLeagues.values()) {
			LeagueMember leader = league.getMember(league.getLeader());
			if ((leader == null) || (leader.getLastBuildTime() + 604800000L >= System.currentTimeMillis()))
				continue;
			int newLeader = league.getInfo().getBackupLeader(leader.getId());
			if (newLeader != -1) {
				if (league.getInfo().getViceleaders().contains(Integer.valueOf(newLeader))) {
					league.getInfo().getViceleaders().remove(new Integer(newLeader));
				}
				league.setLeader(newLeader);
				MailService.sendSystemMail(1, leader.getId(), "职位变动",
						MessageFormat.format("</p style=21>你对军团的不作为，使你在军团中的职位变更为团员。新团长为</p><p style=20>【{0}】</p>",
								new Object[] { league.getMember(newLeader).getName() }));
				MailService.sendSystemMail(1, newLeader, "职位变动", "</p style=21>你对军团的劳心劳力，使你在军团中的职位变更为团长。</p>");
			}
		}
	}

	static class LeagueSortComparator implements Comparator<League> {
		public int compare(League l1, League l2) {
			if (l2.getLevel() > l1.getLevel())
				return 1;
			if (l2.getLevel() < l1.getLevel()) {
				return -1;
			}
			return (l2.getCostBuildValue() - l1.getCostBuildValue());
		}
	}

	class LeagueUpdateRunnable implements Runnable {
		int i;

		LeagueUpdateRunnable() {
			this.i = 0;
		}

		public void run() {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (true)
				try {
					do {
						long pre = System.currentTimeMillis();
						int count = 0;

						for (League l : LeagueManager.this.idLeagues.values()) {
							if ((l == null) || (l.getId() % 10 != this.i))
								continue;
							l.save();
							++count;
						}

						long l = System.currentTimeMillis() - pre;
						if (l < 60000L) {
							Thread.sleep(60000L - l);
						} else {
							Platform.getLog()
									.logWarn("Update League Thread run too long,count:" + count + ",time:" + l);
							Thread.sleep(1000L);
						}
						this.i += 1;
					} while (this.i < 10);
					this.i = 0;
				} catch (Exception e) {
					Platform.getLog().logError(e);
				}
		}
	}
}
