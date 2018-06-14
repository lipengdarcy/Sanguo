package org.darcy.sanguo.util;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.mail.Mail;
import org.darcy.sanguo.pay.Receipt;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.top.Top;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.worldcompetition.WorldCompetition;

public class DBUtil {
	public static Player getPlayerByName(String name) {
		String hql = "from Player player where player.name = ?";
		List<Player> list = Platform.getEntityManager().query(Player.class, hql, new Object[] { name });
		if ((list != null) && (list.size() > 0)) {
			return ((Player) list.get(0));
		}
		return null;
	}

	public static Receipt getReceipt(String coOrderId) {
		String hql = "from Receipt r where r.coOrderId = ?";
		List<Receipt> list = Platform.getEntityManager().query(Receipt.class, hql, new Object[] { coOrderId });
		if ((list != null) && (list.size() > 0)) {
			return ((Receipt) list.get(0));
		}
		return null;
	}

	public static List<Player> getPlayerByBlurName(String name, int pageIndex, int pageSize) {
		String hql = "from Player player where player.name like ? order by id";
		List<Player> list = Platform.getEntityManager().queryPage(Player.class, hql, pageSize, pageIndex,
				new Object[] { "%" + name + "%" });
		return list;
	}

	public static int getPlayerCountByBlurName(String name) {
		String hql = "select count(*) from Player player where player.name like ? order by id";
		List<Long> list = Platform.getEntityManager().query(Long.class, hql, new Object[] { "%" + name + "%" });
		return ((Long) list.get(0)).intValue();
	}

	public static Integer getPlayerIdByAccountId(String accountId, int channelType) {
		String hql = "Select id from Player player where player.accountId = ? and player.channelType = ?";
		List<Integer> list = Platform.getEntityManager().query(Integer.class, hql,
				new Object[] { accountId, Integer.valueOf(channelType) });
		if ((list != null) && (list.size() > 0)) {
			return ((Integer) list.get(0));
		}
		return null;
	}

	public static List<Top> getTops(int type) {
		String hql = "from Top top where top.type = ? order by rank asc";
		return Platform.getEntityManager().query(Top.class, hql, new Object[] { Integer.valueOf(type) });
	}

	public static List<Top> getTopsNoRank(int type) {
		String hql = "from Top top where top.type = ?";
		return Platform.getEntityManager().query(Top.class, hql, new Object[] { Integer.valueOf(type) });
	}

	public static List<MiniPlayer> loadMiniPlayers() {
		String hql = "from MiniPlayer mini";
		return Platform.getEntityManager().query(MiniPlayer.class, hql, new Object[0]);
	}

	public static List<Mail> getMails(int pid) {
		long time = System.currentTimeMillis();
		Date date = new Date(time - 1209600000L);
		String hql = "from Mail mail where mail.targetId = ? and (mail.sendTime > ? or type = ?) order by sendTime desc";
		List<Mail> list = Platform.getEntityManager().query(Mail.class, hql,
				new Object[] { Integer.valueOf(pid), date, Integer.valueOf(12) });
		if (list != null) {
			return new CopyOnWriteArrayList<Mail>(list);
		}
		return list;
	}

	public static List<Integer> getCompesationIds(Date start, Date end, int minLevel, int maxLevel, int optType) {
		List<Integer> list = null;
		String hql = null;
		if (optType == 1) {
			hql = "select id from Player player where player.level >= ? and player.level <= ? and player.lastLogin >= ? and player.lastLogin <= ?";
			list = Platform.getEntityManager().query(Integer.class, hql,
					new Object[] { Integer.valueOf(minLevel), Integer.valueOf(maxLevel), start, end });
		} else if (optType == 2) {
			hql = "select id from Player player where player.level >= ? and player.level <= ? and player.registerTime >= ? and player.registerTime <= ?";
			list = Platform.getEntityManager().query(Integer.class, hql, new Object[] { Integer.valueOf(minLevel),
					Integer.valueOf(maxLevel), Long.valueOf(start.getTime()), Long.valueOf(end.getTime()) });
		}
		return list;
	}

	public static void clearOverDueMail(Date date) {
		String hql = "delete Mail mail where mail.sendTime < ? and type != ? and type!= ?";
		Platform.getEntityManager().excute(hql, new Object[] { date, Integer.valueOf(12), Integer.valueOf(16) });
	}

	public static Arena getArenaByRank(int rank) {
		List<Arena> list = Platform.getEntityManager().query(Arena.class, "from Arena arena where arena.rank = ?",
				new Object[] { Integer.valueOf(rank) });
		if ((list != null) && (list.size() > 0)) {
			return ((Arena) list.get(0));
		}
		return null;
	}

	public static List<WorldCompetition> getWorldCompetitionWithRank() {
		List<WorldCompetition> list = Platform.getEntityManager().query(WorldCompetition.class,
				"from WorldCompetition wc where wc.rank > 0", new Object[0]);
		return list;
	}

	public static void updatePlayerAutoIncrement() {
		int count = Configuration.serverId * 1000000 + 1;
		String sql = "alter table player AUTO_INCREMENT = ?";
		Platform.getEntityManager().excuteSql(sql, new Object[] { Integer.valueOf(count) });
	}

	public static void updateLeagueAutoIncrement() {
		int count = Configuration.serverId * 1000000 + 1;
		String sql = "alter table league AUTO_INCREMENT = ?";
		Platform.getEntityManager().excuteSql(sql, new Object[] { Integer.valueOf(count) });
	}

	public static List<League> getAllLeague() {
		List<League> list = Platform.getEntityManager().query(League.class, "from League", new Object[0]);
		return list;
	}

	public static List<Receipt> getReceitByPlayer(int playerId) {
		String hql = "from Receipt r where r.pid = ?";
		List<Receipt> list = Platform.getEntityManager().query(Receipt.class, hql,
				new Object[] { Integer.valueOf(playerId) });
		return list;
	}

	public static List<Integer> getChargePlayerList() {
		String hql = "select distinct pid from Receipt where state = 1";
		return Platform.getEntityManager().query(Integer.class, hql, new Object[0]);
	}
}
