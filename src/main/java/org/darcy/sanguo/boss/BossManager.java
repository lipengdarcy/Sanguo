package org.darcy.sanguo.boss;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Fall;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.monster.Monster;
import org.darcy.sanguo.monster.MonsterService;
import org.darcy.sanguo.monster.MonsterTemplate;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.BossService;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbDown;

public class BossManager {
	private static final int OPEN_HOUR = 21;
	private static final int OPEN_MIN = 0;
	private static final int LAST_SEC = 900;
	private static final int BOSS_ID = 21014;
	private static final int HP_PER_LEVEL = 5000000;
	private BossDamageComparator comparator = new BossDamageComparator();
	private LeagueDamageComparator lComparator = new LeagueDamageComparator();
	private Monster boss = new Monster((MonsterTemplate) MonsterService.monsterTemplates.get(Integer.valueOf(21014)));

	private Set<Player> pool = new HashSet<Player>();

	public BossData data = new BossData();

	public void init() {
		try {
			this.data = ((BossData) ((DbService) Platform.getServiceManager().get(DbService.class)).get(BossData.class,
					Integer.valueOf(Configuration.serverId)));
			if (this.data == null) {
				this.data = new BossData();
				this.data.id = Configuration.serverId;
				this.data.serverId = Configuration.serverId;
				((DbService) Platform.getServiceManager().get(DbService.class)).add(this.data);
			}
			this.data.open = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		if (isOpen()) {
			try {
				refreshRank();
				refreshLeagueRank();
			} catch (Throwable t) {
				Platform.getLog().logError(t);
			}
		}
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this.data);
	}

	public void refreshRank() {
		synchronized (this.data.ranks) {
			Collections.sort(this.data.ranks, this.comparator);
		}
	}

	public void refreshLeagueRank() {
		synchronized (this.data.leagueRanks) {
			Platform.getLog().logWarn("boss leagueRank sort~");
			Collections.sort(this.data.leagueRanks, this.lComparator);
		}
	}

	public void addToLeagueRank(int leagueId) {
		if (!(this.data.leagueRanks.contains(Integer.valueOf(leagueId))))
			synchronized (this.data.leagueRanks) {
				this.data.leagueRanks.add(Integer.valueOf(leagueId));
			}
	}

	public void removeLeagueRank(int leagueId) {
		if (this.data.leagueRanks.contains(Integer.valueOf(leagueId)))
			synchronized (this.data.leagueRanks) {
				this.data.leagueRanks.remove(new Integer(leagueId));
			}
	}

	public void addPlayer(Player player) {
		this.pool.add(player);
	}

	public void removePlayer(Player player) {
		this.pool.remove(player);
	}

	public Set<Player> getPool() {
		return this.pool;
	}

	public void addToRank(Player player) {
		if (getRank(player.getId()) == -1)
			synchronized (this.data.ranks) {
				this.data.ranks.add(Integer.valueOf(player.getId()));
			}
	}

	public boolean isOpen() {
		return this.data.open;
	}

	private Calendar getStartCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.set(11, 21);
		cal.set(12, 0);
		cal.set(13, 0);
		return cal;
	}

	public int getLeftOpenSeconds() {
		Calendar now = Calendar.getInstance();
		Calendar t = getStartCalendar();
		if (now.after(t)) {
			t.add(6, 1);
		}
		return (int) ((t.getTimeInMillis() - now.getTimeInMillis()) / 1000L);
	}

	public int getLeftChanllengeSeconds() {
		if (!(this.data.open)) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		if (cal.get(11) != 21) {
			return 0;
		}
		int min = cal.get(12);
		int sec = cal.get(13);
		int past = min * 60 + sec;
		int left = 900 - past;
		if (left <= 0) {
			endBoss();
			return 0;
		}
		return left;
	}

	public void startBoss() {
		Platform.getLog().logSystem("Boss start!");
		this.boss.refreshAttributes(false);
		int dif = this.data.bossLevel - 1;
		if (dif > 0) {
			this.boss.getAttributes().set(7, this.boss.getAttributes().get(7) + dif * 5000000);
			this.boss.getAttributes().setHp(this.boss.getAttributes().getHp() + dif * 5000000);
		}
		this.data.ranks.clear();
		this.pool.clear();
		this.data.killer = -1;
		Platform.getLeagueManager().worldBossDamageClear();
		this.data.open = true;
	}

	public void endBoss() {
		int rank;
		Player kp;
		Player p;
		Platform.getLog().logSystem("Boss end!");
		this.data.open = false;
		refreshRank();
		refreshLeagueRank();

		if (this.boss.isAlive())
			if (this.data.bossLevel > 1)
				this.data.bossLevel -= 1;
			else {
				this.data.bossLevel += 1;
			}

		this.boss.rest(Unit.RestType.STAGE);

		String killerName = "未被击杀";

		if (this.data.killer != -1) {
			MiniPlayer killer = Platform.getPlayerManager().getMiniPlayer(this.data.killer);
			killerName = killer.getName();

			rank = getRank(this.data.killer);
			List l1 = getRewards(0, BossService.rewards);
			List l2 = getRewards(rank, BossService.rewards);

			if (l2 != null) {
				l1.addAll(l2);
			}
			l1 = Reward.mergeReward(l1);
			try {
				kp = Platform.getPlayerManager().getPlayer(this.data.killer, true, true);
				MailService.sendSystemMail(6, this.data.killer, "铜雀神魔奖励", MessageFormat.format(
						"<p style=21>恭喜主公，您在今天的铜雀魔神挑战中对Boss造成了</p><p style=19>{0}</p><p style=21>的总伤害量，获得第</p><p style=20>{1}</p><p style=21>排名的好成绩。\n并且您还完成了对Boss的最后一击，额外获得了一份击杀奖励。\n本次铜雀魔神获得奖励如下：</p>",
						new Object[] { Integer.valueOf(kp.getBossRecord().getTotalDamage()), Integer.valueOf(rank) }),
						null, l1);

				p = Platform.getPlayerManager().getPlayer(this.data.killer, false, false);
				if ((p != null) && (this.pool.contains(p))) {
					pushEndInfo(p, ((Reward) l1.get(0)).count, ((Reward) l1.get(1)).count, killerName, rank);
					this.pool.remove(p);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		for (int i = 0; i < this.data.ranks.size(); ++i) {
			rank = i + 1;
			int pid = ((Integer) this.data.ranks.get(i)).intValue();
			if (pid == this.data.killer) {
				continue;
			}
			try {
				List rewards = getRewards(i + 1, BossService.rewards);
				kp = Platform.getPlayerManager().getPlayer(pid, true, true);
				MailService.sendSystemMail(6, pid, "铜雀神魔奖励", MessageFormat.format(
						"<p style=21>恭喜主公，您在今天的铜雀魔神挑战中对Boss造成了</p><p style=19>{0}</p><p style=21>的总伤害量，获得第</p><p style=20>{1}</p><p style=21>排名的好成绩，获得奖励如下：</p>",
						new Object[] { Integer.valueOf(kp.getBossRecord().getTotalDamage()), Integer.valueOf(rank) }),
						null, rewards);

				p = Platform.getPlayerManager().getPlayer(pid, false, false);
				if ((p != null) && (this.pool.contains(p))) {
					pushEndInfo(p, ((Reward) rewards.get(0)).count, ((Reward) rewards.get(1)).count, killerName, rank);
					this.pool.remove(p);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		for (Player p1 : this.pool) {
			pushEndInfo(p1, 0, 0, killerName, -1);
		}
		this.pool.clear();

		for (int i = 0; i < this.data.leagueRanks.size(); ++i) {
			rank = i + 1;
			int id = ((Integer) this.data.leagueRanks.get(i)).intValue();
			League l = Platform.getLeagueManager().getLeagueById(id);
			if (l == null) {
				continue;
			}
			int damage = l.getInfo().getWorldBossDamage();
			if (damage <= 0) {
				return;
			}
			List rewards = getRewards(rank, BossService.leagueRewards);
			Set set = new HashSet(l.getInfo().getMembers().keySet());
			for (Iterator localIterator = set.iterator(); localIterator.hasNext();) {
				int playerId = ((Integer) localIterator.next()).intValue();
				MailService.sendSystemMail(6, playerId, "【铜雀魔神】军团伤害排行奖励", MessageFormat.format(
						"<p style=21>恭喜主公，您所在的军团【{0}】在今日的铜雀魔神挑战中对Boss造成了</p><p style=19>【{1}】</p><p style=21>的总伤害量，获得军团第</p><p style=20>【{2}】</p><p style=21>排名的好成绩。\n根据军团伤害总量排名，本次获得的军团排名奖励如下：</p>",
						new Object[] { l.getName(), Integer.valueOf(damage), Integer.valueOf(rank) }), null, rewards);
			}
		}
	}

	private void pushEndInfo(Player p, int money, int prestige, String killerName, int rank) {
		PbDown.BossRewardRst.Builder rst = PbDown.BossRewardRst.newBuilder();
		rst.setKill(killerName).setDamage(p.getBossRecord().getTotalDamage()).setMoney(money).setPrestige(prestige)
				.setRank(rank);
		p.send(2100, rst.build());
	}

	private List<Reward> getRewards(int rank, List<BossReward> list) {
		List rewards = new ArrayList();
		BossReward reward = null;
		for (BossReward r : list) {
			if ((rank <= r.maxRank) && (rank >= r.minRank)) {
				reward = r;
				break;
			}
		}
		if (reward == null) {
			reward = (BossReward) list.get(list.size() - 1);
		}
		rewards.add(new Reward(2, reward.money, null));
		rewards.add(new Reward(10, reward.prestige, null));
		if (reward.dropId != -1) {
			DropService ds = (DropService) Platform.getServiceManager().get(DropService.class);
			DropGroup dg = ds.getDropGroup(reward.dropId);
			if (dg != null) {
				List<Fall> falls = dg.fall();
				for (Fall fall : falls) {
					rewards.add(fall.genReward());
				}
			}
		}
		return rewards;
	}

	public Player getKiller() {
		Player p = null;
		if (this.data.killer != -1) {
			try {
				p = Platform.getPlayerManager().getPlayer(this.data.killer, true, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return p;
	}

	public String getBoardCastMsg() {
		League l;
		StringBuilder sb = new StringBuilder();

		List ranks = this.data.leagueRanks;
		if ((ranks != null) && (ranks.size() > 0)) {
			for (int i = 0; (i < 3) && (i < ranks.size()); ++i) {
				int id = ((Integer) ranks.get(i)).intValue();
				l = Platform.getLeagueManager().getLeagueById(id);
				if (l != null) {
					sb.append(",").append(l.getName());
				}
			}
		}

		String msg = MessageFormat.format("<p style=17>在</p><p style=15>軍團【{0}】</p><p style=17>的帶領下，銅雀化身最終被擊退了！</p>",
				new Object[] { (sb.length() > 0) ? sb.substring(1) : sb.toString() });
		Player killer = getKiller();
		if (killer != null) {
			l = Platform.getLeagueManager().getLeagueByPlayerId(killer.getId());
			if (l == null)
				msg = msg + MessageFormat.format("<p style=17>（本次铜雀化身的最终击杀者是</p><p style=13>{0}</p><p style=17>！）</p>",
						new Object[] { killer.getName() });
			else {
				msg = msg + MessageFormat.format(
						"<p style=17>（本次銅雀化身的最終擊殺者是</p><p style=15>軍團【{0}】</p><p style=17>的</p><p style=13>{1}</p><p style=17>！）</p>",
						new Object[] { l.getName(), killer.getName() });
			}
		}

		return msg;
	}

	public List<Integer> getRanks() {
		return this.data.ranks;
	}

	public void setKiller(Player killer) {
		this.data.killer = killer.getId();
	}

	public void setRanks(List<Integer> ranks) {
		this.data.ranks = ranks;
	}

	public Monster getBoss() {
		return this.boss;
	}

	public void setBoss(Monster boss) {
		this.boss = boss;
	}

	public void challenge(Player player) {
	}

	private int getRank(int pid) {
		for (int i = 0; i < this.data.ranks.size(); ++i) {
			if (((Integer) this.data.ranks.get(i)).intValue() == pid) {
				return (i + 1);
			}
		}

		return -1;
	}

	public List<Integer> getLeagueRanks() {
		return this.data.leagueRanks;
	}

	public int getLeagueRank(int leagueId) {
		for (int i = 0; i < this.data.leagueRanks.size(); ++i) {
			if (((Integer) this.data.leagueRanks.get(i)).intValue() == leagueId) {
				return (i + 1);
			}
		}
		return -1;
	}

	public int getRank(Player player) {
		int rank = getRank(player.getId());
		return rank;
	}

	class BossDamageComparator implements Comparator<Integer> {
		public int compare(Integer pId1, Integer pId2) {
			try {
				Player p1 = Platform.getPlayerManager().getPlayer(pId1.intValue(), true, true);
				Player p2 = Platform.getPlayerManager().getPlayer(pId2.intValue(), true, true);
				return (p2.getBossRecord().getTotalDamage() - p1.getBossRecord().getTotalDamage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

	class LeagueDamageComparator implements Comparator<Integer> {
		public int compare(Integer i1, Integer i2) {
			League l1 = Platform.getLeagueManager().getLeagueById(i1.intValue());
			League l2 = Platform.getLeagueManager().getLeagueById(i2.intValue());

			if ((l1 == null) || (l2 == null)) {
				return 0;
			}
			return (l2.getInfo().getWorldBossDamage() - l1.getInfo().getWorldBossDamage());
		}
	}
}
