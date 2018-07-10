package org.darcy.sanguo.worldcompetition;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncUpdater;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerManager;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.RobotService;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.VipService;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class WorldCompetitionService implements Service, EventHandler, PacketHandler {

	public ConcurrentHashMap<Integer, WorldCompetition> rankCompetitions = new ConcurrentHashMap<Integer, WorldCompetition>();

	private ConcurrentHashMap<Integer, WorldCompetition> idCompetitions = new ConcurrentHashMap<Integer, WorldCompetition>();

	public void startup() throws Exception {
		//init();
		new Crontab("23 10 0", 2003);
		new Crontab("23 30 0", 2004);
		new Crontab("* 0 0", 1011);
		new Thread(new UpdateRunnable(), "UpdateWorldCompetitionRunnable").start();
		Platform.getEventManager().registerListener(this);
		Platform.getPacketHanderManager().registerHandler(this);
		loadData();
	}

	public int[] getCodes() {
		return new int[] { 1079, 1081, 1083, 1085, 1087, 1141 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		if (!(FunctionService.isOpenFunction(player.getLevel(), 21))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1079:
			worldCompetitionInfo(player);
			break;
		case 1081:
			worldCompetitionRefresh(player);
			break;
		case 1083:
			rankInfo(player);
			break;
		case 1085:
			compete(player, packet);
			break;
		case 1087:
			enemyInfo(player);
			break;
		case 1141:
			rewardView(player);
		}
	}

	private void loadData() {
		List<Row> list = ExcelUtils.getRowList("contest.xls", 2);
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}

			++pos;
			WorldCompetitionData.baseScore = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.winBaseScore = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.loseBaseScore = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.maxScore = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.winScoreRatio = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.loseScoreRatio = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.winExp = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.loseExp = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.winHonor = (int) row.getCell(pos++).getNumericCellValue();
			WorldCompetitionData.cd = (int) row.getCell(pos++).getNumericCellValue();
		}
		List<Row> list2 = ExcelUtils.getRowList("contest.xls", 2, 1);
		for (Row row : list2) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String desc = row.getCell(pos++).getStringCellValue();
			int start = (int) row.getCell(pos++).getNumericCellValue();
			int end = (int) row.getCell(pos++).getNumericCellValue();
			int coin = (int) row.getCell(pos++).getNumericCellValue();
			int honor = (int) row.getCell(pos++).getNumericCellValue();
			int gold = (int) row.getCell(pos++).getNumericCellValue();

			String items = null;
			try {
				items = row.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				items = Integer.toString((int) row.getCell(pos).getNumericCellValue());
			}
			pos++;

			WorldCompetitionReward reward = new WorldCompetitionReward();
			reward.id = id;
			reward.desc = desc;
			reward.start = start;
			reward.end = end;
			if (coin > 0) {
				reward.rewards.add(new Reward(2, coin, null));
			}
			if (gold > 0) {
				reward.rewards.add(new Reward(3, gold, null));
			}
			if (honor > 0) {
				reward.rewards.add(new Reward(9, honor, null));
			}
			if ((items != null) && (!(items.equals(""))) && (!(items.equals("-1")))) {
				String[] array = items.split(",");
				for (String str : array) {
					reward.rewards.add(new Reward(str));
				}
			}
			WorldCompetitionData.rewards.put(Integer.valueOf(reward.id), reward);
		}
	}

	private void worldCompetitionInfo(Player player) {
		PbDown.WorldCompetitionInfoRst.Builder builder = PbDown.WorldCompetitionInfoRst.newBuilder();
		builder.setResult(true);
		Calendar now = Calendar.getInstance();
		boolean isOver = WorldCompetitionData.isOver(now);
		builder.setIsOver(isOver);
		WorldCompetition competition = player.getWorldCompetition();
		builder.setRank(competition.getRank());
		builder.setScore(competition.getScore());
		if (!(isOver)) {
			int rest = competition.canRefresh();
			builder.setCdTime(rest);
			int count = player.getPool().getInt(2, 20);
			builder.setRestCount(count);
			if (competition.getCompetitor().size() == 0) {
				refreshCompetitor(player, false);
			}

			List<WorldCompetition> list = new ArrayList<WorldCompetition>();
			while ((list.size() == 0) && (competition.getCompetitor().size() > 0)) {
				boolean flag = true;
				for (Integer i : competition.getCompetitor()) {
					WorldCompetition wc = (WorldCompetition) this.idCompetitions.get(i);
					if (wc == null) {
						flag = false;
						break;
					}
					list.add(wc);
				}

				if (!(flag)) {
					list.clear();
					refreshCompetitor(player, false);
				}
			}

			GetCompetitorAsyncCall call = new GetCompetitorAsyncCall(player, builder, list);
			Platform.getThreadPool().execute(call);
			return;
		}
		player.send(1080, builder.build());
	}

	private void worldCompetitionRefresh(Player player) {
		PbDown.WorldCompetitionRefreshRst.Builder builder = PbDown.WorldCompetitionRefreshRst.newBuilder();
		if (!(WorldCompetitionData.inCompeteTime())) {
			builder.setResult(false);
			builder.setErrInfo("争霸赛时间为每周一到周六的8:00-23:00");
		} else {
			WorldCompetition competition = player.getWorldCompetition();
			int rest = competition.canRefresh();
			if (rest != 0) {
				builder.setResult(false);
				builder.setErrInfo("处于冷却中，无法刷新");
			} else {
				refreshCompetitor(player, true);
				builder.setCdTime(WorldCompetitionData.cd);

				List<WorldCompetition> list = new ArrayList<WorldCompetition>();
				while ((list.size() == 0) && (competition.getCompetitor().size() > 0)) {
					boolean flag = true;
					for (Integer i : competition.getCompetitor()) {
						WorldCompetition wc = (WorldCompetition) this.idCompetitions.get(i);
						if (wc == null) {
							flag = false;
							break;
						}
						list.add(wc);
					}

					if (!(flag)) {
						list.clear();
						refreshCompetitor(player, false);
					}
				}

				RefreshCompetitiorAsyncCall call = new RefreshCompetitiorAsyncCall(player, builder, list);
				Platform.getThreadPool().execute(call);
				return;
			}
		}
		player.send(1082, builder.build());
	}

	public void refreshCompetitor(Player player, boolean refreshTime) {
		int i;
		List<Integer> result = new ArrayList<Integer>();
		WorldCompetition competition = player.getWorldCompetition();
		if ((competition == null) || (competition.getPlayerId() != player.getId())) {
			return;
		}
		Random random = new Random();
		int rank = competition.getRank();
		int maxRank = Platform.getWorld().getCurWorldCompetitionMaxRank();
		List<Integer> ranks = new ArrayList<Integer>();
		if (maxRank <= WorldCompetitionData.rankLimit.length) {
			for (i = 1; i <= maxRank; ++i)
				if (i != rank)
					ranks.add(Integer.valueOf(i));
		} else {
			int tmpRank;
			if (rank > 0) {
				for (i = 0; i < WorldCompetitionData.rankLimit.length; ++i) {
					int tmp = WorldCompetitionData.rankLimit[i];
					if (rank <= tmp) {
						if (rank == 1)
							tmpRank = rank;
						else
							tmpRank = random.nextInt(rank - 1) + 1;
					} else {
						tmpRank = 1;
						if (tmp > 0) {
							tmpRank = rank - (1 + random.nextInt(tmp));
						} else {
							tmp *= -1;
							if (tmp + rank > maxRank) {
								tmp = maxRank - rank;
							}
							if (tmp == 0)
								tmpRank = rank;
							else {
								tmpRank = rank + 1 + random.nextInt(tmp);
							}
						}
					}
					while ((ranks.contains(Integer.valueOf(tmpRank))) || (tmpRank == rank)) {
						tmpRank = random.nextInt(maxRank) + 1;
					}
					ranks.add(Integer.valueOf(tmpRank));
				}
			} else {
				for (i = 0; i < WorldCompetitionData.rankLimit.length; ++i) {
					tmpRank = random.nextInt(100) + 1 + 5000 - 100;

					if (ranks.contains(Integer.valueOf(tmpRank)))
						--i;
					else {
						ranks.add(Integer.valueOf(tmpRank));
					}
				}
			}
		}
		for (Integer tmpRank : ranks) {
			WorldCompetition competition2 = getWorldCompetitionByRank(tmpRank.intValue());
			if (competition2 != null) {
				result.add(Integer.valueOf(competition2.getPlayerId()));
			}
		}

		competition.refresh(result, refreshTime);
	}

	private void rankInfo(Player player) {
		List<WorldCompetition> list = new ArrayList<WorldCompetition>();
		for (int i = 1; i <= 10; ++i) {
			WorldCompetition competition = (WorldCompetition) this.rankCompetitions.get(Integer.valueOf(i));
			if (competition != null) {
				list.add(competition);
			}
		}
		GetRankAsyncCall call = new GetRankAsyncCall(player.getSession(), list);
		Platform.getThreadPool().execute(call);
	}

	private void compete(Player player, PbPacket.Packet packet) {
		PbDown.WorldCompetitionCompeteRst.Builder builder = PbDown.WorldCompetitionCompeteRst.newBuilder();
		builder.setResult(true);
		int id = 0;
		try {
			PbUp.WorldCompetitionCompete compete = PbUp.WorldCompetitionCompete.parseFrom(packet.getData());
			id = compete.getCompetitor();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("挑战失败");
			player.send(1086, builder.build());
			return;
		}
		if (!(WorldCompetitionData.inCompeteTime())) {
			builder.setResult(false);
			builder.setErrInfo("争霸赛时间为每周一到周六的8:00-23:00");
		} else {
			int count = player.getPool().getInt(2, 20);
			if (count <= 0) {
				VipService.notifyChallengeTimeBuyCost(player, PbCommons.VipBuyTimeType.COMPETITION);
				return;
			}
			if (player.getStamina() < 2) {
				builder.setResult(false);
				builder.setErrInfo("精力不足");
			} else if ((!(player.getWorldCompetition().getCompetitor().contains(Integer.valueOf(id))))
					&& (!(player.getWorldCompetition().getEnemy().contains(Integer.valueOf(id))))) {
				builder.setResult(false);
				builder.setErrInfo("该玩家不在你的挑战列表中");
			} else {
				WorldCompetitionCompeteAsyncCall call = new WorldCompetitionCompeteAsyncCall(player, id);
				Platform.getThreadPool().execute(call);
				return;
			}
		}
		player.send(1086, builder.build());
	}

	private void enemyInfo(Player player) {
		List<Integer> ids = new ArrayList<Integer>();
		WorldCompetition competition = player.getWorldCompetition();
		if (competition != null) {
			for (Integer id : competition.getEnemy()) {
				ids.add(id);
			}
		}
		GetEnemyAsyncCall call = new GetEnemyAsyncCall(player.getSession(), ids);
		Platform.getThreadPool().execute(call);
	}

	private void rewardView(Player player) {
		PbDown.WorldCompetitionViewRst.Builder builder = PbDown.WorldCompetitionViewRst.newBuilder();
		builder.setResult(true);
		Iterator<Integer> itx = WorldCompetitionData.rewards.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			WorldCompetitionReward reward = (WorldCompetitionReward) WorldCompetitionData.rewards
					.get(Integer.valueOf(id));
			builder.addRewards(reward.genWorldCompetitionReward());
		}
		player.send(1142, builder.build());
	}

	private void init() {
		List<WorldCompetition> list = DBUtil.getWorldCompetitionWithRank();
		if ((list != null) && (list.size() > 0))
			for (WorldCompetition wc : list)
				addCompetition(wc);
	}

	public WorldCompetition getWorldCompetitionByRank(int rank) {
		return ((WorldCompetition) this.rankCompetitions.get(Integer.valueOf(rank)));
	}

	public WorldCompetition getWorldCompetitionByPlayerId(int playerId) {
		return ((WorldCompetition) this.idCompetitions.get(Integer.valueOf(playerId)));
	}

	public void addCompetition(WorldCompetition competition) {
		if ((competition != null) && (competition.getRank() > 0)) {
			this.idCompetitions.put(Integer.valueOf(competition.getPlayerId()), competition);
			this.rankCompetitions.put(Integer.valueOf(competition.getRank()), competition);
		}
	}

	public void sort() {
		Platform.getLog().logSystem("competition sort start...");
		long start = System.currentTimeMillis();

		this.rankCompetitions.clear();
		List<WorldCompetition> list = new ArrayList<WorldCompetition>(this.idCompetitions.values());

		Collections.sort(list, new Comparator<WorldCompetition>() {
			public int compare(WorldCompetition o1, WorldCompetition o2) {
				return (o2.getScore() - o1.getScore());
			}
		});
		for (int i = 1; i <= list.size(); ++i) {
			((WorldCompetition) list.get(i - 1)).setRank(i);
		}

		for (WorldCompetition w : list) {
			this.rankCompetitions.put(Integer.valueOf(w.getRank()), w);
		}

		long end = System.currentTimeMillis();
		Platform.getLog().logSystem(
				"competition sort end, spend:" + (end - start) + ", opt num:" + this.rankCompetitions.size());
	}

	public void clearOutData() {
		int num = this.rankCompetitions.size() - 5000;
		if (num > 0) {
			List<WorldCompetition> saveList = new ArrayList<WorldCompetition>();
			for (int i = 0; i < num; ++i) {
				WorldCompetition wc = (WorldCompetition) this.rankCompetitions
						.get(Integer.valueOf(this.rankCompetitions.size() - i - 1));
				if (wc != null) {
					saveList.add(wc);
				}
			}
			for (WorldCompetition wc : saveList) {
				wc.setRank(-1);
				this.rankCompetitions.remove(Integer.valueOf(wc.getRank()));
				this.idCompetitions.remove(Integer.valueOf(wc.getPlayerId()));
				if (Platform.getPlayerManager().getPlayerById(wc.getPlayerId()) != null) {
					Platform.getThreadPool().execute(new AsyncUpdater(wc));
				}
			}

			Platform.getLog().logSystem("competition clear outdata, num:" + num);
		}
	}

	public boolean sort(WorldCompetition comp, boolean isUp) {
		if (isUp) {
			int start = 1;
			if (comp.getRank() == -1)
				start = this.rankCompetitions.size();
			else {
				start = comp.getRank() - 1;
			}
			for (int i = start; i > 0; --i) {
				WorldCompetition tmp = (WorldCompetition) this.rankCompetitions.get(Integer.valueOf(i));
				if (tmp == null) {
					Platform.getLog().logError("worldcompetition up sort error!! curRank:" + i + ", start:" + start);
					return true;
				}
				if (comp.getScore() > tmp.getScore()) {
					tmp.setRank(i + 1);
					this.rankCompetitions.put(Integer.valueOf(tmp.getRank()), tmp);
					if (i == 1) {
						comp.setRank(i);
						this.rankCompetitions.put(Integer.valueOf(comp.getRank()), comp);
					}
				} else {
					comp.setRank(i + 1);
					this.rankCompetitions.put(Integer.valueOf(comp.getRank()), comp);
				}
			}
		} else if (comp.getRank() > 0) {
			int size = this.rankCompetitions.size();
			int start = comp.getRank() + 1;
			for (int i = start; i <= size; ++i) {
				WorldCompetition tmp = (WorldCompetition) this.rankCompetitions.get(Integer.valueOf(i));
				if (tmp == null) {
					Platform.getLog().logError("worldcompetition down sort error!! curRank:" + i + ", start:" + start);
					return true;
				}
				if (comp.getScore() <= tmp.getScore()) {
					tmp.setRank(i - 1);
					this.rankCompetitions.put(Integer.valueOf(tmp.getRank()), tmp);
					if (i == size) {
						comp.setRank(i);
						this.rankCompetitions.put(Integer.valueOf(comp.getRank()), comp);
					}
				} else {
					comp.setRank(i - 1);
					this.rankCompetitions.put(Integer.valueOf(comp.getRank()), comp);
					break;
				}
			}
		}

		return false;
	}

	public void lostScoreModify(WorldCompetition comp, int loseScore, WorldCompetition winComp) {
		if (Platform.getWorld().getTotalWorldCompetitionCount() <= 5000) {
			comp.setScore(comp.getScore() - loseScore);
			return;
		}
		if (comp.getRank() > 0) {
			WorldCompetition lastComp = (WorldCompetition) this.rankCompetitions
					.get(Integer.valueOf(this.rankCompetitions.size()));
			if (winComp.getRank() > 0) {
				if (comp.getPlayerId() == lastComp.getPlayerId()) {
					return;
				}
				if (winComp.getPlayerId() == lastComp.getPlayerId()) {
					lastComp = (WorldCompetition) this.rankCompetitions
							.get(Integer.valueOf(this.rankCompetitions.size() - 1));
				}
				if (lastComp.getScore() <= comp.getScore() - loseScore) {
					comp.setScore(comp.getScore() - loseScore);
					return;
				}
				comp.setScore(lastComp.getScore());
				return;
			}

			if (winComp.getScore() <= lastComp.getScore()) {
				if (comp.getPlayerId() == lastComp.getPlayerId()) {
					return;
				}
				if (lastComp.getScore() > comp.getScore() - loseScore) {
					comp.setScore(lastComp.getScore());
					return;
				}
			}
		}

	}

	public int[] getEventCodes() {
		return new int[] { 2003, 2004, 1011 };
	}

	public void handleEvent(Event event) {
		WorldCompetition wc;
		if (event.type == 2003) {
			if (Calendar.getInstance().get(7) == 7) {
				for (Player p : Platform.getPlayerManager().players.values())
					if (p != null) {
						wc = p.getWorldCompetition();
						if (wc != null) {
							List<Reward> rewards = WorldCompetitionData.getRewardsByRank(wc.getRank());
							long rewardTime = WorldCompetitionData.getCurRewardTime().getTimeInMillis();
							if ((rewards != null) && (rewards.size() > 0)) {
								if (wc.getRank() > 0)
									MailService.sendSystemMail(5, p.getId(), "争霸赛每周排名奖励", MessageFormat.format(
											"<p style=21>恭喜主公，截止到本周六23:00，您在争霸赛中获得第</p><p style=20>{0}</p><p style=21>排名的好成绩，获得奖励如下：</p>",
											new Object[] { Integer.valueOf(wc.getRank()) }), new Date(), rewards);
								else {
									MailService.sendSystemMail(5, p.getId(), "争霸赛每周排名奖励",
											"<p style=21>很遗憾，截止到本周六23:00，您未能在争霸赛中获得任何名次。\n作为鼓励，我们给您准备了以下奖励。下周努力再战哦~：</p>",
											new Date(), rewards);
								}
							}
							wc.setLastReward(rewardTime);
						}
					}
			}
		} else if (event.type == 2004) {
			if (Calendar.getInstance().get(7) == 1)
				new Thread(new RefreshRunnable()).start();
		} else {
			int i;
			if ((event.type != 1011) || (Platform.getWorld().getTotalWorldCompetitionCount() <= RobotService.robotNum))
				return;
			StringBuilder sb = new StringBuilder();
			for (i = 1; i <= 3; ++i) {
				wc = getWorldCompetitionByRank(i);
				if (wc != null) {
					MiniPlayer mp = Platform.getPlayerManager().getMiniPlayer(wc.getPlayerId());
					if (mp != null) {
						sb.append(",").append(mp.getName());
					}
				}
			}
			String msg = (sb.length() > 0) ? sb.substring(1) : sb.toString();
			Calendar now = Calendar.getInstance();
			if (WorldCompetitionData.isOver(now))
				msg = MessageFormat.format("<p style=17>本周争霸赛已结束，获得前三名的玩家分别是</p><p style=13>{0}</p><p style=17>！</p>",
						new Object[] { msg });
			else {
				msg = MessageFormat.format("<p style=17>本周争霸赛快报，目前积分最高的三名玩家分别是</p><p style=13>{0}</p><p style=17>！</p>",
						new Object[] { msg });
			}
			Platform.getPlayerManager().boardCast(msg);
		}
	}

	public void shutdown() {
		List<Object> list = new ArrayList<Object>(this.idCompetitions.values());
		Platform.getEntityManager().updateBatch(list);
	}

	public void reload() throws Exception {
	}

	public ConcurrentHashMap<Integer, WorldCompetition> getRankCompetitions() {
		return this.rankCompetitions;
	}

	public ConcurrentHashMap<Integer, WorldCompetition> getIdCompetitions() {
		return this.idCompetitions;
	}

	class RefreshRunnable implements Runnable {
		public void run() {
			List<WorldCompetition> list = new ArrayList<WorldCompetition>(
					WorldCompetitionService.this.idCompetitions.values());
			for (WorldCompetition worldCompetition : list) {
				worldCompetition.setScore(WorldCompetitionData.baseScore);
				worldCompetition.getEnemy().clear();
				worldCompetition.getCompetitor().clear();
			}
		}
	}

	class UpdateRunnable implements Runnable {
		int i;

		UpdateRunnable() {
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
						PlayerManager pm = Platform.getPlayerManager();
						int count = 0;

						for (WorldCompetition wc : WorldCompetitionService.this.idCompetitions.values()) {
							if ((wc == null) || (pm.getPlayerById(wc.getPlayerId()) != null)
									|| (wc.getPlayerId() % 10 != this.i))
								continue;
							wc.save();
							++count;
						}

						long l = System.currentTimeMillis() - pre;
						if (l < 60000L) {
							Thread.sleep(60000L - l);
						} else {
							Platform.getLog()
									.logWarn("Update Competition Thread run too long,count:" + count + ",time:" + l);
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
