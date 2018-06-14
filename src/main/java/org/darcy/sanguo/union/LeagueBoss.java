package org.darcy.sanguo.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.monster.Monster;
import org.darcy.sanguo.monster.MonsterService;
import org.darcy.sanguo.monster.MonsterTemplate;
import org.darcy.sanguo.player.Player;

import sango.packet.PbLeague;

public class LeagueBoss {
	public static final int version = 1;
	public static final int MAX_LEVEL = 20;
	int level = 1;
	long lastDeadTime;
	int rankLevel;
	Monster boss;
	Map<Integer, List<LeagueBossDamage>> damages = new HashMap();

	Map<Integer, List<LeagueBossReward>> rewards = new HashMap();

	Map<Integer, Integer> ranks = new LinkedHashMap();

	public void initBoss(int hp) {
		this.boss = new Monster((MonsterTemplate) MonsterService.monsterTemplates
				.get(Integer.valueOf(LeagueService.getBossId(this.level))));
		this.boss.refreshAttributes(false);
		if (hp > 0)
			this.boss.getAttributes().setHp(hp);
	}

	public void refresh(League l) {
		int i;
		int damage;
		LeagueBossReward leagueBossReward;
		List rs;
		this.ranks.clear();
		this.rankLevel = this.level;
		long now = System.currentTimeMillis();
		long today0Time = LeagueService.getToday0Time(now);

		Map<Integer, Integer> tmp = new HashMap<Integer, Integer>();
		Set<Integer> ids = l.getInfo().getMembers().keySet();
		for (Integer playerId : ids) {
			damage = getTotalDamageBeforeTime(playerId.intValue(), today0Time);
			tmp.put(playerId, Integer.valueOf(damage));
		}

		List<Integer> tmpList = new ArrayList<Integer>(tmp.keySet());
		Collections.sort(tmpList, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
		});
		for (Integer id : tmpList) {
			this.ranks.put(id, (Integer) tmp.get(id));
		}

		ids = this.ranks.keySet();
		i = 1;
		for (Integer playerId : ids) {
			damage = ((Integer) this.ranks.get(playerId)).intValue();
			if (damage > 0) {
				leagueBossReward = new LeagueBossReward();
				leagueBossReward.id = playerId.intValue();
				leagueBossReward.time = today0Time;
				leagueBossReward.rank = i;
				leagueBossReward.damage = damage;
				leagueBossReward.level = this.rankLevel;
				rs = LeagueService.getBossDayReward(this.rankLevel, i);
				if (rs != null) {
					leagueBossReward.rewards.addAll(rs);
				}
				if (Platform.getPlayerManager().getPlayerById(playerId.intValue()) != null) {
					leagueBossReward.sendMail(playerId.intValue(), false, l);
				} else {
					List list = (List) this.rewards.get(playerId);
					if (list == null) {
						list = new ArrayList();
						this.rewards.put(playerId, list);
					}
					list.add(leagueBossReward);
				}
			}
			++i;
		}

		Iterator itx = this.damages.keySet().iterator();
		while (itx.hasNext()) {
			int playerId = ((Integer) itx.next()).intValue();
			if (!(l.isMember(playerId))) {
				damage = getTotalDamageBeforeTime(playerId, now);
				if (damage > 0) {
					leagueBossReward = new LeagueBossReward();
					leagueBossReward.id = playerId;
					leagueBossReward.time = today0Time;
					leagueBossReward.damage = damage;
					rs = LeagueService.getBossDayReward(this.rankLevel, i);
					if (rs != null) {
						leagueBossReward.rewards.addAll(rs);
					}
					leagueBossReward.sendMail(playerId, true, l);
				}
				itx.remove();
			} else {
				List<LeagueBossDamage> list = this.damages.get(Integer.valueOf(playerId));
				if ((list != null) && (list.size() > 0)) {
					Iterator itx2 = list.iterator();
					while (itx2.hasNext()) {
						LeagueBossDamage a = (LeagueBossDamage) itx2.next();
						if (a.time < today0Time)
							itx2.remove();
					}
				}
			}
		}
	}

	private int getTotalDamageBeforeTime(int playerId, long time) {
		if (!(this.damages.containsKey(Integer.valueOf(playerId)))) {
			return 0;
		}
		int damage = 0;
		List<LeagueBossDamage> list = (List) this.damages.get(Integer.valueOf(playerId));
		if ((list != null) && (list.size() > 0)) {
			for (LeagueBossDamage leagueBossDamage : list) {
				if (leagueBossDamage.time < time) {
					damage += leagueBossDamage.damage;
				}
			}
		}
		return damage;
	}

	public void fightBoss(Player p, int damage, boolean isWin, int money) {
		LeagueBossDamage bossDamage = new LeagueBossDamage();
		bossDamage.id = p.getId();
		bossDamage.name = p.getName();
		bossDamage.bossId = LeagueService.getBossId(this.level);
		bossDamage.kill = isWin;
		bossDamage.money = money;
		bossDamage.damage = damage;
		bossDamage.time = System.currentTimeMillis();

		List list = (List) this.damages.get(Integer.valueOf(p.getId()));
		if (list == null) {
			list = new ArrayList();
			this.damages.put(Integer.valueOf(p.getId()), list);
		}
		list.add(bossDamage);

		if (isWin) {
			if (this.level < 20) {
				this.level += 1;
			}
			initBoss(0);
			this.lastDeadTime = System.currentTimeMillis();
		}
	}

	public long getReviveTime() {
		return (this.lastDeadTime + LeagueData.LEAGUE_BOSS_REVIVE_TIME * 60 * 1000 - System.currentTimeMillis());
	}

	public PbLeague.LeagueBoss genLeagueBoss(League l) {
		PbLeague.LeagueBoss.Builder b = PbLeague.LeagueBoss.newBuilder();
		b.setBossId(LeagueService.getBossId(this.level));
		b.setLevel(this.level);
		b.setHp(this.boss.getAttributes().getHp());
		b.setMaxHp(this.boss.getAttributes().get(7));
		b.setReviveTime(getReviveTime());
		b.setBuffId(LeagueService.getBossBuff(l.getBossFacilityLevel()));
		b.setBossFacitlityLevel(l.getBossFacilityLevel());
		List<LeagueBossDamage> list = new ArrayList();
		for (List tmp : this.damages.values()) {
			list.addAll(tmp);
		}
		Collections.sort(list, new Comparator<LeagueBossDamage>() {
			public int compare(LeagueBossDamage o1, LeagueBossDamage o2) {
				return (int) (o2.time - o1.time);
			}
		});
		for (LeagueBossDamage damage : list) {
			b.addDamages(damage.genLeagueBossDamage());
		}
		return b.build();
	}

	public static PbLeague.LeagueBossRankReward genLeagueBossRankReward(int rank, List<Reward> list) {
		PbLeague.LeagueBossRankReward.Builder b = PbLeague.LeagueBossRankReward.newBuilder();
		b.setRank(rank);
		if ((list != null) && (list.size() > 0)) {
			for (Reward r : list) {
				b.addReward(r.genPbReward());
			}
		}
		return b.build();
	}

	public static LeagueBoss readObject(ObjectInputStream in) {
		LeagueBoss lb;
		try {
			int playerId;
			List list;
			int num;
			int j;
			in.readInt();
			LeagueBoss boss = new LeagueBoss();
			boss.level = in.readInt();
			boss.lastDeadTime = in.readLong();
			boss.rankLevel = in.readInt();
			int hp = in.readInt();
			boss.initBoss(hp);

			boss.damages = new HashMap();
			int size = in.readInt();
			int i;
			for (i = 0; i < size; ++i) {
				playerId = in.readInt();
				list = new ArrayList();
				num = in.readInt();
				for (j = 0; j < num; ++j) {
					LeagueBossDamage damage = LeagueBossDamage.readObject(in);
					if (damage != null) {
						list.add(damage);
					}
				}
				boss.damages.put(Integer.valueOf(playerId), list);
			}

			boss.rewards = new HashMap();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				playerId = in.readInt();
				list = new ArrayList();
				num = in.readInt();
				for (j = 0; j < num; ++j) {
					LeagueBossReward reward = LeagueBossReward.readObject(in);
					if (reward != null) {
						list.add(reward);
					}
				}
				boss.rewards.put(Integer.valueOf(playerId), list);
			}

			boss.ranks = new LinkedHashMap();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				playerId = in.readInt();
				int damage = in.readInt();
				boss.ranks.put(Integer.valueOf(playerId), Integer.valueOf(damage));
			}

			return boss;
		} catch (IOException e) {
			e.printStackTrace();
			lb = new LeagueBoss();
			lb.initBoss(0);
		}
		return lb;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		Integer playerId;

		out.writeInt(1);

		out.writeInt(this.level);
		out.writeLong(this.lastDeadTime);
		out.writeInt(this.rankLevel);
		out.writeInt(this.boss.getAttributes().getHp());

		out.writeInt(this.damages.size());
		for (Iterator localIterator1 = this.damages.keySet().iterator(); localIterator1.hasNext();) {
			playerId = (Integer) localIterator1.next();
			List<LeagueBossDamage> list = this.damages.get(playerId);
			out.writeInt(playerId.intValue());
			out.writeInt(list.size());
			for (LeagueBossDamage damage : list) {
				damage.writeObject(out);
			}
		}

		out.writeInt(this.rewards.size());
		for (Iterator localIterator1 = this.rewards.keySet().iterator(); localIterator1.hasNext();) {
			playerId = (Integer) localIterator1.next();
			List<LeagueBossReward> list = (List) this.rewards.get(playerId);
			out.writeInt(playerId.intValue());
			out.writeInt(list.size());
			for (LeagueBossReward reward : list) {
				reward.writeObject(out);
			}
		}

		out.writeInt(this.ranks.size());
		for (Iterator localIterator1 = this.ranks.keySet().iterator(); localIterator1.hasNext();) {
			playerId = (Integer) localIterator1.next();
			int damage = ((Integer) this.ranks.get(playerId)).intValue();
			out.writeInt(playerId.intValue());
			out.writeInt(damage);
		}
	}

	public int getLevel() {
		return this.level;
	}
}
