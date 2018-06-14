package org.darcy.sanguo.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import sango.packet.PbCommons;

public class LeagueInfo implements Serializable {
	private static final long serialVersionUID = -199786641034239375L;
	private static final int version = 5;
	private int todayBuildCount = 0;

	private int worldBossDamage = 0;

	private int worldBossInspireCount = 0;

	private int boxCount = 0;

	private List<Integer> viceleaders = new ArrayList();

	private Map<Integer, LeagueMember> members = new HashMap();

	private List<Integer> applys = new ArrayList();

	private Queue<LeagueBuild> buildRecords = new LinkedList();

	private Map<Integer, LeagueRareGoods> rareGoods = new HashMap();

	private LeagueBoss boss = new LeagueBoss();

	private int bossweekCount = 0;

	private List<Integer> lotteryRewards = new ArrayList();

	private List<Boolean> lotteryRecords = new ArrayList();

	private List<Integer> lotteryShowIndexes = new ArrayList();

	private List<PbCommons.LuckyDog> luckyDogs = new ArrayList();

	private int lotteryRefreshTimes = 0;

	private Set<Integer> lotteryGetRecords = new HashSet();

	public int getBossweekCount() {
		return this.bossweekCount;
	}

	public void setBossweekCount(int bossweekCount) {
		this.bossweekCount = bossweekCount;
	}

	public void addWorldBossDamage(int damage) {
		this.worldBossDamage += damage;
	}

	public void addWorldBossInspireCount() {
		this.worldBossInspireCount += 1;
	}

	public int getWorldBossDamage() {
		return this.worldBossDamage;
	}

	public void setWorldBossDamage(int worldBossDamage) {
		this.worldBossDamage = worldBossDamage;
	}

	public int getWorldBossInspireCount() {
		return this.worldBossInspireCount;
	}

	public void setWorldBossInspireCount(int worldBossInspireCount) {
		this.worldBossInspireCount = worldBossInspireCount;
	}

	public int getBoxCount() {
		return this.boxCount;
	}

	public void setBoxCount(int boxCount) {
		this.boxCount = boxCount;
	}

	public int getLeftLotteryRefreshTimes(int level) {
		int rst = ((Integer) LeagueService.lotteryRrfreshTimes.get(Integer.valueOf(level))).intValue()
				- this.lotteryRefreshTimes;
		if (rst < 0) {
			return 0;
		}
		return rst;
	}

	public void refreshLottery(int level) {
		this.lotteryRecords.clear();
		this.lotteryRewards.clear();
		this.lotteryShowIndexes.clear();
		this.lotteryGetRecords.clear();

		int[] tmpShowId = new int[4];

		List pools = (List) LeagueService.lotteryRewards.get(Integer.valueOf(level));
		for (int i = 0; i < 40; ++i) {
			this.lotteryRecords.add(Boolean.valueOf(false));
			int[] pool = (int[]) pools.get(i);
			int random = Calc.nextInt(pool.length);
			this.lotteryRewards.add(Integer.valueOf(pool[random]));
			if (i < 4) {
				tmpShowId[i] = pool[random];
			}
		}

		Collections.shuffle(this.lotteryRewards);
		Set set = new HashSet();
		for (int i = 0; i < 4; ++i)
			for (int index = 0; index < this.lotteryRewards.size(); ++index) {
				if ((tmpShowId[i] != ((Integer) this.lotteryRewards.get(index)).intValue())
						|| (set.contains(Integer.valueOf(index))))
					continue;
				this.lotteryShowIndexes.add(Integer.valueOf(index));
				set.add(Integer.valueOf(index));
				break;
			}
	}

	public int getBackupLeader(int besides) {
		for (int i = 3; i <= 7; ++i) {
			int rst = getBackupLeader(i, besides);
			if (rst != -1) {
				return rst;
			}
		}
		return -1;
	}

	private int getBackupLeader(int days, int besides) {
		int rst = -1;
		for (LeagueMember mem : this.members.values()) {
			if ((mem.getLastBuildTime() + days * 24 * 3600 * 1000 > System.currentTimeMillis())
					&& (mem.getId() != besides)) {
				if (rst == -1)
					rst = mem.getId();
				else if (mem.getTotalContribution() > ((LeagueMember) this.members.get(Integer.valueOf(rst)))
						.getTotalContribution()) {
					rst = mem.getId();
				}
			}
		}
		return rst;
	}

	public void addBuildCount() {
		this.todayBuildCount += 1;
	}

	public List<Integer> getViceleaders() {
		return this.viceleaders;
	}

	public void setViceleaders(List<Integer> viceleaders) {
		this.viceleaders = viceleaders;
	}

	public Map<Integer, LeagueMember> getMembers() {
		return this.members;
	}

	public void setMembers(Map<Integer, LeagueMember> members) {
		this.members = members;
	}

	public List<Integer> getApplys() {
		return this.applys;
	}

	public void setApplys(List<Integer> applys) {
		this.applys = applys;
	}

	public Queue<LeagueBuild> getBuildRecords() {
		return this.buildRecords;
	}

	public void setBuildRecords(Queue<LeagueBuild> buildRecords) {
		this.buildRecords = buildRecords;
	}

	public Map<Integer, LeagueRareGoods> getRareGoods() {
		return this.rareGoods;
	}

	public void setRareGoods(Map<Integer, LeagueRareGoods> rareGoods) {
		this.rareGoods = rareGoods;
	}

	public LeagueBoss getBoss() {
		return this.boss;
	}

	public Set<Integer> getLotteryGetRecords() {
		return this.lotteryGetRecords;
	}

	public void setLotteryGetRecords(Set<Integer> lotteryGetRecords) {
		this.lotteryGetRecords = lotteryGetRecords;
	}

	public void setBoss(LeagueBoss boss) {
		this.boss = boss;
	}

	public int getTodayBuildCount() {
		return this.todayBuildCount;
	}

	public void setTodayBuildCount(int todayBuildCount) {
		this.todayBuildCount = todayBuildCount;
	}

	public List<Integer> getLotteryRewards() {
		return this.lotteryRewards;
	}

	public List<Boolean> getLotteryRecords() {
		return this.lotteryRecords;
	}

	public List<Integer> getLotteryShowIndexes() {
		return this.lotteryShowIndexes;
	}

	public List<PbCommons.LuckyDog> getLuckyDogs() {
		return this.luckyDogs;
	}

	public int getLotteryRefreshTimes() {
		return this.lotteryRefreshTimes;
	}

	public void setLotteryRewards(List<Integer> lotteryRewards) {
		this.lotteryRewards = lotteryRewards;
	}

	public void setLotteryRecords(List<Boolean> lotteryRecords) {
		this.lotteryRecords = lotteryRecords;
	}

	public void setLotteryShowIndexes(List<Integer> lotteryShowIndexes) {
		this.lotteryShowIndexes = lotteryShowIndexes;
	}

	public void setLuckyDogs(List<PbCommons.LuckyDog> luckyDogs) {
		this.luckyDogs = luckyDogs;
	}

	public void setLotteryRefreshTimes(int lotteryRefreshTimes) {
		this.lotteryRefreshTimes = lotteryRefreshTimes;
	}

	private void readObject(ObjectInputStream in) {
		int i;
		this.lotteryRewards = new ArrayList();
		this.lotteryRecords = new ArrayList();
		this.lotteryShowIndexes = new ArrayList();
		this.luckyDogs = new ArrayList();
		this.lotteryGetRecords = new HashSet();
		try {
			int id;
			int version = in.readInt();

			this.viceleaders = new ArrayList();
			int size = in.readInt();
			for (i = 0; i < size; ++i) {
				id = in.readInt();
				this.viceleaders.add(Integer.valueOf(id));
			}

			this.members = new HashMap();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				id = in.readInt();
				LeagueMember lm = LeagueMember.readObject(in);
				if (lm != null) {
					this.members.put(Integer.valueOf(id), lm);
				}
			}

			this.applys = new ArrayList();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				id = in.readInt();
				this.applys.add(Integer.valueOf(id));
			}

			this.buildRecords = new LinkedList();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				LeagueBuild lb = LeagueBuild.readObject(in);
				this.buildRecords.add(lb);
			}

			this.rareGoods = new HashMap();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				id = in.readInt();
				LeagueRareGoods goods = LeagueRareGoods.readObject(in);
				if (goods != null) {
					this.rareGoods.put(Integer.valueOf(id), goods);
				}
			}

			this.boss = LeagueBoss.readObject(in);
			if (version > 1) {
				this.todayBuildCount = in.readInt();
			}

			if (version > 2) {
				size = in.readInt();
				for (i = 0; i < size; ++i) {
					this.lotteryRewards.add(Integer.valueOf(in.readInt()));
				}

				size = in.readInt();
				for (i = 0; i < size; ++i) {
					this.lotteryRecords.add(Boolean.valueOf(in.readBoolean()));
				}

				size = in.readInt();
				for (i = 0; i < size; ++i) {
					this.lotteryShowIndexes.add(Integer.valueOf(in.readInt()));
				}

				size = in.readInt();
				for (i = 0; i < size; ++i) {
					PbCommons.LuckyDog.Builder b = PbCommons.LuckyDog.newBuilder();
					b.setName(in.readUTF()).setRewardName(in.readUTF());
					this.luckyDogs.add(b.build());
				}

				size = in.readInt();
				for (i = 0; i < size; ++i) {
					this.lotteryGetRecords.add(Integer.valueOf(in.readInt()));
				}
				if (version <= 3)
					return;
				this.worldBossDamage = in.readInt();
				this.worldBossInspireCount = in.readInt();

				this.boxCount = in.readInt();
				if (version > 4) {
					this.bossweekCount = in.readInt();
				}
			}
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Integer id;
		int i;
		LeagueRareGoods lrg;
		out.writeInt(5);

		out.writeInt(this.viceleaders.size());
		for (Iterator localIterator = this.viceleaders.iterator(); localIterator.hasNext();) {
			id = (Integer) localIterator.next();
			out.writeInt(id.intValue());
		}

		out.writeInt(this.members.size());
		for (Iterator localIterator = this.members.keySet().iterator(); localIterator.hasNext();) {
			id = (Integer) localIterator.next();
			LeagueMember lm = (LeagueMember) this.members.get(id);
			if (lm != null) {
				out.writeInt(id.intValue());
				lm.writeObject(out);
			}
		}

		out.writeInt(this.applys.size());
		for (Iterator localIterator = this.applys.iterator(); localIterator.hasNext();) {
			id = (Integer) localIterator.next();
			out.writeInt(id.intValue());
		}

		out.writeInt(this.buildRecords.size());
		for (LeagueBuild lb : this.buildRecords) {
			lb.writeObject(out);
		}

		out.writeInt(this.rareGoods.size());
		for (Iterator localIterator = this.rareGoods.keySet().iterator(); localIterator.hasNext();) {
			id = (Integer) localIterator.next();
			lrg = (LeagueRareGoods) this.rareGoods.get(id);
			if (lrg != null) {
				out.writeInt(id.intValue());
				lrg.writeObject(out);
			}
		}

		this.boss.writeObject(out);

		out.writeInt(this.todayBuildCount);

		int size = this.lotteryRewards.size();
		out.writeInt(size);
		for (i = 0; i < size; ++i) {
			out.writeInt(((Integer) this.lotteryRewards.get(i)).intValue());
		}

		size = this.lotteryRecords.size();
		out.writeInt(size);
		for (int j = 0; j < size; ++j) {
			out.writeBoolean(((Boolean) this.lotteryRecords.get(j)).booleanValue());
		}

		size = this.lotteryShowIndexes.size();
		out.writeInt(size);
		for (int k = 0; k < size; ++k) {
			out.writeInt(((Integer) this.lotteryShowIndexes.get(k)).intValue());
		}

		size = this.luckyDogs.size();
		out.writeInt(size);
		for (int l = 0; l < size; ++l) {
			out.writeUTF(((PbCommons.LuckyDog) this.luckyDogs.get(l)).getName());
			out.writeUTF(((PbCommons.LuckyDog) this.luckyDogs.get(l)).getRewardName());
		}

		size = this.lotteryGetRecords.size();
		out.writeInt(size);
		for (Integer a : this.lotteryGetRecords) {
			out.writeInt(a.intValue());
		}

		out.writeInt(this.worldBossDamage);
		out.writeInt(this.worldBossInspireCount);

		out.writeInt(this.boxCount);

		out.writeInt(this.bossweekCount);
	}
}
