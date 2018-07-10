package org.darcy.sanguo.union.combat;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.GeneratedMessage;

import sango.packet.PbDown;
import sango.packet.PbLeague;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class LeagueCombatService implements Service, PacketHandler, EventHandler {
	public static final int REVIVE_PRICE = 100;
	public static final int START_HOUR = 20;
	public static final long LOOT_CD = 30000L;
	public static final long RANDOM_CD = 30000L;
	public static final int RANDOM_WIN_SCORE = 10;
	public static final int RANDOM_LOSE_SCORE = 5;
	public static final int WIN_SCORE = 6000;
	public static final int NULL_SCORE = 1000;
	public static final int ALMOST_WIN_SCORE = 5000;
	public static final long MAX_LAST_MINISECONDS = 600000L;
	public static Map<Integer, Integer> countryBuffs = new HashMap<Integer, Integer>();

	public static Map<Integer, Integer> cityRates = new HashMap<Integer, Integer>();

	public static Map<Integer, Integer> cityAtkAdds = new HashMap<Integer, Integer>();

	public static int PVP_DROP = 30016;
	public static List<WeekReward> weekRewards = new ArrayList<WeekReward>();

	public static List<Reward> winRewards = new ArrayList<Reward>();
	public static List<Reward> loseRewards = new ArrayList<Reward>();

	public static Set<Integer> syncList = new HashSet<Integer>();

	private static Set<Integer> tmpBroadCast = new HashSet<Integer>();

	public int[] getCodes() {
		return new int[] { 2273, 2283, 2285, 2287, 2289, 2291, 2294, 2279, 2281, 2277, 2301 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		syncList.add(Integer.valueOf(player.getId()));

		switch (packet.getPtCode()) {
		case 2273:
			info(player);
			break;
		case 2283:
			PbUp.LCOccupy occupy = PbUp.LCOccupy.parseFrom(packet.getData());
			occupy(player, occupy.getIndex());
			break;
		case 2285:
			PbUp.LCLoot loot = PbUp.LCLoot.parseFrom(packet.getData());
			loot(player, loot.getIndex());
			break;
		case 2287:
			PbUp.LCGiveup giveup = PbUp.LCGiveup.parseFrom(packet.getData());
			giveup(player, giveup.getIndex());
			break;
		case 2289:
			randomFight(player);
			break;
		case 2291:
			revive(player);
			break;
		case 2294:
			PbUp.LCSyncRst sync = PbUp.LCSyncRst.parseFrom(packet.getData());
			if (!(sync.getStop()))
				return;
			syncQuit(player);

			break;
		case 2279:
			PbUp.LCVSDetail detail = PbUp.LCVSDetail.parseFrom(packet.getData());
			vsDetail(player, detail.getPeriod());
			break;
		case 2281:
			PbUp.LCInnerRank inner = PbUp.LCInnerRank.parseFrom(packet.getData());
			innerRank(player, inner.getPeriod());
			break;
		case 2277:
			PbUp.LCRank rank = PbUp.LCRank.parseFrom(packet.getData());
			rank(player, rank.getPeriod());
			break;
		case 2301:
			PbUp.LCGetMiniUser get = PbUp.LCGetMiniUser.parseFrom(packet.getData());
			getMiniUser(player, get.getIndex());
		}
	}

	private void getMiniUser(Player player, int index) {
		PbDown.LCGetMiniUserRst.Builder rst = PbDown.LCGetMiniUserRst.newBuilder();
		rst.setResult(false);
		rst.setIndex(index);
		try {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			if (combat.getLeftLootTime(player.getId()) > 0L) {
				rst.setErrInfo("请耐心等待复活");
			}
			Pair pair = combat.getPair(l.getId());
			City city = (City) pair.getCities().get(index);
			if (city.getPid() <= 0) {
				rst.setErrInfo("无主城池，请直接占领即可");
			}
			MiniPlayer mp = Platform.getPlayerManager().getMiniPlayer(city.getPid());
			rst.setMini(mp.genMiniUser());
			rst.setResult(true);
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2302, rst.build());
	}

	public void send2League(int lid, int ptCode, GeneratedMessage msg) {
		League league = Platform.getLeagueManager().getLeagueById(lid);
		for (Iterator<?> localIterator = league.getInfo().getMembers().keySet().iterator(); localIterator.hasNext();) {
			int pid = ((Integer) localIterator.next()).intValue();
			if (syncList.contains(Integer.valueOf(pid))) {
				Player p = Platform.getPlayerManager().getPlayerById(pid);
				if (p != null)
					p.send(ptCode, msg);
			}
		}
	}

	public void sync(Pair pair) {
		PbDown.LCSync.Builder rst = PbDown.LCSync.newBuilder();
		LeagueCombat combat = Platform.getLeagueManager().getCombat();
		if (pair.isFighting()) {
			int lid = pair.getOffenLeagueId();
			rst.setInfo(combat.genFightInfo(lid));
			send2League(lid, 2293, rst.build());
			int tid = pair.getTargetLid(lid);
			rst.setInfo(combat.genFightInfo(tid));
			send2League(tid, 2293, rst.build());
		}
	}

	public void end(Pair pair) {
		try {
			pair.end();
			Calendar cal = Calendar.getInstance();
			cal.set(11, 20);
			cal.set(12, 0);
			cal.set(13, 0);
			cal.set(14, 0);
			pair.setLastMiniSeconds(System.currentTimeMillis() - cal.getTimeInMillis());

			int deffenLid = pair.getDeffenLeagueId();
			if (deffenLid != -1) {
				InnerRank ir;
				PbLeague.LCInnerNode.Builder ib;
				List<Reward> rewards;
				Reward r;
				Iterator<Reward> localIterator2;
				League deffen = Platform.getLeagueManager().getLeagueById(deffenLid);
				List<InnerRank> deffenList = new ArrayList<InnerRank>();
				List<InnerRank> offenList = new ArrayList<InnerRank>();
				for (Integer pid : pair.getPersonalScores().keySet()) {
					ir = new InnerRank();
					ir.setPid(pid.intValue());
					ir.setScore(((Integer) pair.getPersonalScores().get(pid)).intValue());
					Integer lose = (Integer) pair.getLoseCount().get(pid);
					ir.setLose((lose == null) ? 0 : lose.intValue());
					Integer win = (Integer) pair.getWinCount().get(pid);
					ir.setWin((win == null) ? 0 : win.intValue());
					if (deffen.isMember(pid.intValue()))
						deffenList.add(ir);
					else {
						offenList.add(ir);
					}
				}

				Collections.sort(deffenList, new Comparator<InnerRank>() {
					public int compare(InnerRank o1, InnerRank o2) {
						return (o2.getScore() - o1.getScore());
					}
				});
				Collections.sort(offenList, new Comparator<InnerRank>() {
					public int compare(InnerRank o1, InnerRank o2) {
						return (o2.getScore() - o1.getScore());
					}
				});
				League off = Platform.getLeagueManager().getLeagueById(pair.getTargetLid(deffenLid));
				League deff = Platform.getLeagueManager().getLeagueById(deffenLid);

				if ((off == null) || (deff == null)) {
					return;
				}

				PbDown.LCEnd.Builder ob = PbDown.LCEnd.newBuilder();
				ob.setIsWin(pair.isWin(off.getId())).setLastTime(pair.getLastMiniSeconds()).setMyName(off.getName())
						.setMyScore(String.valueOf(pair.getScore(off.getId()))).setTarName(deff.getName())
						.setTarScore(String.valueOf(pair.getScore(deff.getId())));

				PbDown.LCEnd.Builder db = PbDown.LCEnd.newBuilder();
				db.setIsWin(pair.isWin(deff.getId())).setLastTime(pair.getLastMiniSeconds()).setMyName(deff.getName())
						.setMyScore(String.valueOf(pair.getScore(deff.getId()))).setTarName(off.getName())
						.setTarScore(String.valueOf(pair.getScore(off.getId())));

				for (int i = 1; i <= offenList.size(); ++i) {
					ir = (InnerRank) offenList.get(i - 1);
					ib = ir.genPb(off);

					if (pair.isWin(off.getId()))
						rewards = winRewards;
					else {
						rewards = loseRewards;
					}
					for (localIterator2 = rewards.iterator(); localIterator2.hasNext();) {
						r = (Reward) localIterator2.next();
						ib.addRewards(r.genPbReward());
					}

					ob.addMyNodes(ib);
					db.addTarNodes(ib);

					if (pair.isWin(off.getId()))
						MailService.sendSystemMail(24, ir.getPid(), "军团战奖励", MessageFormat.format(
								"<p style=21>恭喜主公，您的所在军团</p><p style=77>【{0}】</p><p style=21>在本轮军团战中战胜了军团</p><p style=76>【{1}】</p><p style=21>，鉴于您的卓越贡献，将获得以下奖励：</p>",
								new Object[] { off.getName(), deffen.getName() }), new Date(), rewards);
					else {
						MailService.sendSystemMail(24, ir.getPid(), "军团战奖励", MessageFormat.format(
								"<p style=21>很遗憾！本轮军团战中，军团</p><p style=76>【{0}】</p><p style=21>战胜了您的所在军团</p><p style=77>【{1}】</p><p style=21>。鉴于您的卓越贡献，将获得以下奖励：</p>",
								new Object[] { deffen.getName(), off.getName() }), new Date(), rewards);
					}
				}
				for (int i = 1; i <= deffenList.size(); ++i) {
					ir = (InnerRank) deffenList.get(i - 1);
					ib = ir.genPb(deff);

					if (pair.isWin(deff.getId()))
						rewards = winRewards;
					else {
						rewards = loseRewards;
					}
					for (localIterator2 = rewards.iterator(); localIterator2.hasNext();) {
						r = (Reward) localIterator2.next();
						ib.addRewards(r.genPbReward());
					}

					ob.addTarNodes(ib);
					db.addMyNodes(ib);

					if (pair.isWin(deff.getId()))
						MailService.sendSystemMail(24, ir.getPid(), "军团战奖励", MessageFormat.format(
								"<p style=21>恭喜主公，您的所在军团</p><p style=77>【{0}】</p><p style=21>在本轮军团战中战胜了军团</p><p style=76>【{1}】</p><p style=21>，鉴于您的卓越贡献，将获得以下奖励：</p>",
								new Object[] { deff.getName(), off.getName() }), new Date(), rewards);
					else {
						MailService.sendSystemMail(24, ir.getPid(), "军团战奖励", MessageFormat.format(
								"<p style=21>很遗憾！本轮军团战中，军团</p><p style=76>【{0}】</p><p style=21>战胜了您的所在军团</p><p style=77>【{1}】</p><p style=21>。鉴于您的卓越贡献，将获得以下奖励：</p>",
								new Object[] { off.getName(), deff.getName() }), new Date(), rewards);
					}
				}

				send2League(off.getId(), 2296, ob.build());
				send2League(deff.getId(), 2296, db.build());
				Platform.getLog().logLeagueCombatEnd(off, deff, pair);
			}

		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}

	public WeekReward getWeekReward(int lRank, int pRank) {
		for (WeekReward w : weekRewards) {
			if ((w.lStart <= lRank) && (((w.lEnd >= lRank) || (w.lEnd == -1))) && (w.pStart <= pRank)
					&& (((w.pEnd >= pRank) || (w.pEnd == -1)))) {
				return w;
			}
		}

		return null;
	}

	public void broadcast(String msg, League league) {
		PbDown.LCBroadCast.Builder b = PbDown.LCBroadCast.newBuilder();
		b.setMsg(msg);
		send2League(league.getId(), 2298, b.build());
	}

	public void broadcast1500If(Pair pair, int lid) {
		if (!(tmpBroadCast.contains(Integer.valueOf(lid)))) {
			broadcast("<p style=77>我军</p><p style=21>累计资源已达</p><p style=19>【1500】</p><p style=21>！！加油！胜利就在眼前！！</p>",
					Platform.getLeagueManager().getLeagueById(lid));
			broadcast("<p style=76>敌军</p><p style=21>累计资源已达</p><p style=19>【1500】</p><p style=21>！！</p>",
					Platform.getLeagueManager().getLeagueById(pair.getTargetLid(lid)));
			tmpBroadCast.add(Integer.valueOf(lid));
		}
	}

	public void reviveInfo(Player player, PbDown.LCReviveInfo.LCDeadType type) {
		PbDown.LCReviveInfo.Builder b = PbDown.LCReviveInfo.newBuilder();
		LeagueCombat combat = Platform.getLeagueManager().getCombat();
		b.setLeftTime(combat.getLeftLootTime(player.getId())).setRevivePrice(100);
		b.setDeadType(type);
		player.send(2300, b.build());
	}

	private void rank(Player player, int period) {
		PbDown.LCRankRst.Builder rst = PbDown.LCRankRst.newBuilder();
		rst.setPeriod(period);
		rst.setResult(true);
		try {
			List<?> list;
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if (period == -1) {
				rst.setPeriodNumber(combat.getCurrentPeriod() - 1);
				list = combat.getTmpLastRanks();
			} else {
				rst.setPeriodNumber(combat.getCurrentPeriod());
				list = combat.getTmpRanks();
			}

			for (int i = 1; i <= list.size(); ++i)
				rst.addRankNodes(((LeagueCombatRank) list.get(i - 1)).genPb(i));
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
			rst.setResult(false);
		}

		player.send(2278, rst.build());
	}

	private void innerRank(Player player, int period) {
		PbDown.LCInnerRankRst.Builder rst = PbDown.LCInnerRankRst.newBuilder();
		rst.setPeriod(period);
		rst.setResult(true);
		try {
			Pair pair;
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			Map<Integer, Map<Integer, Pair>> map = null;
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setResult(false);
				rst.setErrInfo("您尚未加入军团!");
				player.send(2282, rst.build());
				return;
			}

			if (period == -1) {
				rst.setPeriodNumber(combat.getCurrentPeriod() - 1);
				map = combat.getLastCombatPairs();
			} else {
				rst.setPeriodNumber(combat.getCurrentPeriod());
				map = combat.getCurrentCombatPairs();
			}

			Map<Integer, InnerRank> ranks = new HashMap<Integer, InnerRank>();
			for (Map<?, ?> pairs : map.values()) {
				pair = (Pair) pairs.get(Integer.valueOf(l.getId()));
				if (pair != null) {
					for (Integer pid : pair.getPersonalScores().keySet()) {
						if (l.isMember(pid.intValue())) {
							InnerRank ir = (InnerRank) ranks.get(pid);
							if (ir == null) {
								ir = new InnerRank();
								ir.setPid(pid.intValue());
								ranks.put(pid, ir);
							}
							ir.addLose((Integer) pair.getLoseCount().get(pid));
							ir.addWin((Integer) pair.getWinCount().get(pid));
							ir.addScore((Integer) pair.getPersonalScores().get(pid));
						}
					}
				}
			}

			List<InnerRank> list = new ArrayList<InnerRank>(ranks.values());
			Collections.sort(list, new Comparator<InnerRank>() {
				public int compare(InnerRank o1, InnerRank o2) {
					return (o2.getScore() - o1.getScore());
				}
			});
			for (InnerRank ir : list)
				rst.addNodes(ir.genPb(l));
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
			rst.setResult(false);
		}

		player.send(2282, rst.build());
	}

	private void vsDetail(Player player, int period) {
		PbDown.LCVSDetailRst.Builder rst = PbDown.LCVSDetailRst.newBuilder();
		rst.setPeriod(period);
		rst.setResult(true);
		try {
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			Map<Integer, Map<Integer, Pair>> map = null;
			if (period == -1) {
				rst.setPeriodNumber(combat.getCurrentPeriod() - 1);
				map = combat.getLastCombatPairs();
			} else {
				rst.setPeriodNumber(combat.getCurrentPeriod());
				map = combat.getCurrentCombatPairs();
			}

			for (Integer i : map.keySet()) {
				PbLeague.LCDetailRound.Builder round = PbLeague.LCDetailRound.newBuilder();
				round.setRound(i.intValue());
				Map<Integer, Pair> pairs = (Map<Integer, Pair>) map.get(i);
				for (Pair pair : pairs.values()) {
					if (pair != null) {
						round.addDetailNodes(pair.genDetailNode());
					}
				}
				rst.addRounds(round);
			}

		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
			rst.setResult(false);
		}

		player.send(2280, rst.build());
	}

	private void syncQuit(Player player) {
		syncList.remove(Integer.valueOf(player.getId()));
	}

	private void revive(Player player) {
		PbDown.LCReviveRst.Builder rst = PbDown.LCReviveRst.newBuilder();
		rst.setResult(false);
		try {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			if (player.getJewels() < 100) {
				rst.setErrInfo("元宝不足");
			}
			if (combat.getLeftLootTime(player.getId()) <= 0L) {
				rst.setResult(true);
			}
			player.decJewels(100, "leagueCombatRevive");
			combat.getLootTimes().remove(Integer.valueOf(player.getId()));
			rst.setResult(true);
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}
		player.send(2292, rst.build());
	}

	private void randomFight(Player player) {
		PbDown.LCRandomFightRst.Builder rst = PbDown.LCRandomFightRst.newBuilder();
		rst.setResult(false);
		try {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			Pair pair = combat.getPair(l.getId());
			League targetLeague = combat.getTargetLeague(l.getId());
			int targetPid = pair.getRandomTargetPid(targetLeague);
			if (targetPid == -1) {
				rst.setErrInfo("对方军团全都龟缩城内，去嘲讽他吧");
			}
			if (combat.getLeftRandomTime(player.getId()) > 0L) {
				rst.setErrInfo("请耐心等待复活");
			}
			RandomFightAsyncCall call = new RandomFightAsyncCall(player, targetPid);
			Platform.getThreadPool().execute(call);
			return;
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}
		player.send(2290, rst.build());
	}

	private void giveup(Player player, int index) {
		PbDown.LCGiveupRst.Builder rst = PbDown.LCGiveupRst.newBuilder();
		rst.setResult(false);
		rst.setIndex(index);
		try {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			Pair pair = combat.getPair(l.getId());
			City city = pair.getCityByOwner(player.getId());
			if (city != null) {
				pair.endCity(city, -1, -1);
			}
			rst.setResult(true);
			sync(pair);
			Platform.getLog().logLeagueCombat(l,
					Platform.getLeagueManager().getLeagueById(pair.getTargetLid(l.getId())), city, "放弃城池", player, null,
					true);
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2288, rst.build());
	}

	private void loot(Player player, int index) {
		PbDown.LCLootRst.Builder rst = PbDown.LCLootRst.newBuilder();
		rst.setResult(false);
		rst.setIndex(index);
		try {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			if (combat.getLeftLootTime(player.getId()) > 0L) {
				rst.setErrInfo("请耐心等待复活");
			}
			Pair pair = combat.getPair(l.getId());
			City city = (City) pair.getCities().get(index);
			if (city.getPid() <= 0) {
				rst.setErrInfo("无主城池，请直接占领即可");
			}
			AsyncCall call = new LCLootAsyncCall(player, city, pair);
			Platform.getThreadPool().execute(call);
			label228: return;
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");

			player.send(2286, rst.build());
		}
	}

	private void occupy(Player player, int index) {
		PbDown.LCOccupyRst.Builder rst = PbDown.LCOccupyRst.newBuilder();
		rst.setResult(false);
		rst.setIndex(index);
		try {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			Pair pair = combat.getPair(l.getId());
			City city = (City) pair.getCities().get(index);
			if (city.getPid() > 0) {
				rst.setErrInfo("该城池已经有主");
			}
			rst.setResult(true);
			pair.occupy(city, player.getId(), l.getId());
			sync(pair);
			Platform.getLog().logLeagueCombat(l,
					Platform.getLeagueManager().getLeagueById(pair.getTargetLid(l.getId())), city, "城池占领", player, null,
					true);
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		label243: player.send(2284, rst.build());
	}

	private void info(Player player) {
		PbDown.LCInfoRst.Builder b = PbDown.LCInfoRst.newBuilder();
		b.setResult(false);
		try {
			MiniPlayer myleader;
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			b.setState(combat.getState());
			if ((l == null) || (!(l.isMember(player.getId())))) {
				b.setErrInfo("您尚未加入军团!");
			}
			if (combat.getCurrentRound() <= 0) {
				b.setState(PbDown.LCInfoRst.LCState.REST).setResult(true);
			}
			if (combat.getPair(l.getId()) == null) {
				b.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
			}
			b.setState(combat.getState()).setResult(true);
			if (combat.isFighting(l.getId())) {
				b.setFighting(combat.genFightInfo(l.getId()));
				b.setLeftRandomTime(combat.getLeftRandomTime(player.getId()))
						.setLeftReviveTime(combat.getLeftLootTime(player.getId())).setRevivePrice(100);
			}
			if (combat.getState() == PbDown.LCInfoRst.LCState.PRPARE) {
				PbLeague.LCPrepare.Builder pre = PbLeague.LCPrepare.newBuilder();
				League targetLeauge = combat.getTargetLeague(l.getId());
				if (targetLeauge == null) {
					b.setState(PbDown.LCInfoRst.LCState.REST);
				}
				myleader = Platform.getPlayerManager().getMiniPlayer(l.getLeader());
				MiniPlayer tarleader = Platform.getPlayerManager().getMiniPlayer(targetLeauge.getLeader());
				pre.setLeftTime(getCombatLeftTime()).setMyLeader(myleader.getName()).setMyName(l.getName())
						.setMyRank(combat.getRank(l.getId())).setTargetLeader(tarleader.getName())
						.setTargetName(targetLeauge.getName()).setTargetRank(combat.getRank(targetLeauge.getId()));
				b.setPrepare(pre);
			}
			if (combat.getState() == PbDown.LCInfoRst.LCState.END) {
				int r = 1;
				for (LeagueCombatRank rank : combat.getTmpRanks()) {
					PbLeague.LCRankNode.Builder rb = rank.genPb(r++);
					b.addRankNodes(rb);
				}
			}
			b.setState(PbDown.LCInfoRst.LCState.REST);
		} catch (Exception e) {
			Platform.getLog().logError(e);
			b.setErrInfo("服务器繁忙，请稍后再试");
			b.setState(PbDown.LCInfoRst.LCState.PRPARE);
		}

		player.send(2274, b.build());
	}

	public static long getCombatLeftTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(11, 20);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);

		Calendar now = Calendar.getInstance();
		return (cal.getTimeInMillis() - now.getTimeInMillis());
	}

	public void startup() throws Exception {
		loadData();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		new Crontab("20 0 0", 2110);
		new Crontab("20 10 0", 2111);
		new Crontab("19 40 0", 2112);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		countryBuffs.clear();
		cityRates.clear();
		cityAtkAdds.clear();
		winRewards.clear();
		loseRewards.clear();
		weekRewards.clear();
		loadData();
	}

	public int[] getEventCodes() {
		return new int[] { 2111, 2110, 1002, 2112 };
	}

	public void handleEvent(Event event) {
		int day = Calendar.getInstance().get(7);
		switch (event.type) {
		case 1002:
			if ((day == 7) || (day == 1))
				return;
			Platform.getLeagueManager().getCombat().newRound(day);

			break;
		case 2111:
			if ((day != 7) && (day != 1)) {
				Platform.getLeagueManager().getCombat().endAll();
				Platform.getLeagueManager().getCombat().reOrder();
			}
			if (day != 6)
				return;
			sendWeekReward();

			break;
		case 2110:
			if ((day == 7) || (day == 1))
				return;
			startCombat();

			break;
		case 2112:
			if ((day != 7) && (day != 1)) {
				broadCast(false);
				return;
			}
			broadCast(true);
		}
	}

	private void broadCast(boolean rest) {
		String msg;
		LeagueCombat combat = Platform.getLeagueManager().getCombat();
		if (!(rest)) {
			StringBuilder sb = new StringBuilder();
			if (combat.getTmpRanks().size() <= 0) {
				return;
			}
			for (int i = 0; (i < combat.getTmpRanks().size()) && (i < 3); ++i) {
				sb.append(Platform.getLeagueManager()
						.getLeagueById(((LeagueCombatRank) combat.getTmpRanks().get(i)).getLeagueId()).getName())
						.append(" ");
			}
			msg = MessageFormat.format(
					"<p style=21>第</p><p style=19>【{0}】</p><p style=21>届军团战激战正酣！！</p><p style=21>目前积分排名前三的军团分别是</p><p style=20>【{1}】</p>",
					new Object[] { Integer.valueOf(combat.getCurrentPeriod()), sb.toString() });
		} else {
			LeagueCombatRank rank = (LeagueCombatRank) combat.getTmpRanks().get(0);
			if (rank == null) {
				return;
			}
			League l = Platform.getLeagueManager().getLeagueById(rank.getLeagueId());
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(l.getLeader());
			msg = MessageFormat.format(
					"<p style=21>第</p><p style=19>【{0}】</p><p style=21>届军团战落下帷幕，本届冠军为军团长</p><p style=20>{1}</p><p style=21>带领的军团</p><p style=20>【{2}】</p><p style=21>！！</p>",
					new Object[] { Integer.valueOf(combat.getCurrentPeriod()), mini.getName(), l.getName() });
		}

		Platform.getPlayerManager().boardCast(msg);
	}

	private void startCombat() {
		tmpBroadCast.clear();
		long mark = System.currentTimeMillis();
		Platform.getLeagueManager().getCombat().start(mark);
	}

	public void sendWeekReward() {
		LeagueCombat combat = Platform.getLeagueManager().getCombat();
		for (Iterator<?> localIterator1 = combat.getTmpRanks().iterator(); localIterator1.hasNext();) {
			int i;
			LeagueCombatRank cr = (LeagueCombatRank) localIterator1.next();
			int lRank = combat.getRank(cr.getLeagueId());

			Map<Integer, InnerRank> ranks = new HashMap<Integer, InnerRank>();
			League l = Platform.getLeagueManager().getLeagueById(cr.getLeagueId());
			for (Map<?, ?> pairs : combat.getCurrentCombatPairs().values()) {
				Pair pair = (Pair) pairs.get(Integer.valueOf(cr.getLeagueId()));
				if (pair != null) {
					for (Integer pid : pair.getPersonalScores().keySet()) {
						if (l.isMember(pid.intValue())) {
							InnerRank ir = (InnerRank) ranks.get(pid);
							if (ir == null) {
								ir = new InnerRank();
								ir.setPid(pid.intValue());
								ranks.put(pid, ir);
							}
							ir.addLose((Integer) pair.getLoseCount().get(pid));
							ir.addWin((Integer) pair.getWinCount().get(pid));
							ir.addScore((Integer) pair.getPersonalScores().get(pid));
						}
					}
				}
			}

			List<InnerRank> list = new ArrayList<InnerRank>(ranks.values());
			Collections.sort(list, new Comparator<InnerRank>() {
				public int compare(InnerRank o1, InnerRank o2) {
					return (o2.getScore() - o1.getScore());
				}
			});
			for (i = 1; i <= list.size(); ++i) {
				InnerRank ir = (InnerRank) list.get(i - 1);
				int pid = ir.getPid();
				WeekReward wr = getWeekReward(lRank, i);
				MailService.sendSystemMail(25, pid, "军团战排名奖励", MessageFormat.format(
						"<p style=21>恭喜主公，您的所在军团</p><p style=20>【{0}】</p><p style=21>在本届军团战中获得了</p><p style=19>{1}</p><p style=21>积分、</p><p style=20>{2}</p><p style=21>净胜资源的好成绩。排名第</p><p style=20>【{3}】</p><p style=21>，为了感谢您对军团的突出贡献，现为您颁发以下奖励：</p>",
						new Object[] { l.getName(), Integer.valueOf(cr.getAccumulateScores()),
								Integer.valueOf(cr.getWonScores()), Integer.valueOf(lRank) }),
						new Date(), wr.rewards);
			}
		}
	}

	private void loadData() {
		int pos;
		List<Row> list0 = ExcelUtils.getRowList("leagueCombat.xls", 2, 0);
		for (Row row : list0) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}

			int type = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int rate = (int) row.getCell(pos++).getNumericCellValue();
			cityRates.put(Integer.valueOf(type), Integer.valueOf(rate));
		}

		List<Row> list1 = ExcelUtils.getRowList("leagueCombat.xls", 2, 1);
		for (Row row : list1) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}

			int country = (int) row.getCell(pos++).getNumericCellValue();
			int buffId = (int) row.getCell(pos++).getNumericCellValue();
			countryBuffs.put(Integer.valueOf(country), Integer.valueOf(buffId));
		}

		List<Row> list2 = ExcelUtils.getRowList("leagueCombat.xls", 2, 2);
		for (Row row : list2) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}

			int count = (int) row.getCell(pos++).getNumericCellValue();
			int add = (int) row.getCell(pos++).getNumericCellValue();
			cityAtkAdds.put(Integer.valueOf(count), Integer.valueOf(add));
		}

		List<Row> list3 = ExcelUtils.getRowList("leagueCombat.xls", 2, 3);
		if (list3.size() > 0) {
			String rw = list3.get(0).getCell(1).getStringCellValue();
			String[] ls = rw.split(",");
			for (String s : ls) {
				winRewards.add(new Reward(s));
			}

			rw = list3.get(1).getCell(1).getStringCellValue();
			ls = rw.split(",");
			for (String s : ls) {
				loseRewards.add(new Reward(s));
			}

		}

		List<Row> list4 = ExcelUtils.getRowList("leagueCombat.xls", 2, 4);
		for (Row row : list4) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int lStart = (int) row.getCell(pos++).getNumericCellValue();
			int lEnd = (int) row.getCell(pos++).getNumericCellValue();
			int pStart = (int) row.getCell(pos++).getNumericCellValue();
			int pEnd = (int) row.getCell(pos++).getNumericCellValue();
			String rw = row.getCell(pos++).getStringCellValue();

			WeekReward dr = new WeekReward();
			String[] ls = rw.split(",");
			for (String l : ls) {
				dr.rewards.add(new Reward(l));
			}
			dr.id = id;
			dr.pEnd = pEnd;
			dr.pStart = pStart;
			dr.lStart = lStart;
			dr.lEnd = lEnd;
			weekRewards.add(dr);
		}
	}
}
