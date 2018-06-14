package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exchange.ExchangeTemplate;
import org.darcy.sanguo.map.ClearMap;
import org.darcy.sanguo.map.ClearStage;
import org.darcy.sanguo.map.MapRecord;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.randomshop.CherishDiscount;
import org.darcy.sanguo.randomshop.RandomShopService;
import org.darcy.sanguo.tower.TowerRecord;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.ChallengeAddPrice;
import org.darcy.sanguo.vip.Vip;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class VipService implements Service, PacketHandler {
	public static List<Vip> vips = new ArrayList();

	private static List<PbCommons.VipPrivileges> privileges = new ArrayList();

	public static List<Reward> firstPayRewards = new ArrayList();

	public static Vip getVip(int charge) {
		Vip vip = null;
		for (Vip v : vips) {
			if (vip == null) {
				vip = v;
			} else {
				if (charge < v.charge)
					break;
				vip = v;
			}
		}
		return vip;
	}

	public void startup() throws Exception {
		loadData();
		generatePrivileges();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	private void generatePrivileges() {
		for (Iterator localIterator1 = vips.iterator(); localIterator1.hasNext();) {
			StringBuffer sb;
			Vip vip = (Vip) localIterator1.next();
			PbCommons.VipPrivileges.Builder p = PbCommons.VipPrivileges.newBuilder();
			p.setLevel(vip.level);
			if (vip.dayRewards != null) {
				sb = new StringBuffer();
				for (Reward r : vip.dayRewards) {
					if (r != null)
						sb.append(r.toString()).append(" ");
				}
				p.addPrivileges(MessageFormat.format("每天可以领取奖励：{0}", new Object[] { sb.toString() }));
			}
			if (vip.proMapTimes > 0)
				p.addPrivileges(
						MessageFormat.format("精英副本每天可以购买{0}次挑战次数", new Object[] { Integer.valueOf(vip.proMapTimes) }));
			if (vip.moneyTrialTimes > 0)
				p.addPrivileges(MessageFormat.format("千年树妖每天可以购买{0}次挑战次数",
						new Object[] { Integer.valueOf(vip.moneyTrialTimes) }));
			if (vip.warriorTrialTimes > 0)
				p.addPrivileges(MessageFormat.format("讨伐黄巾每天可以购买{0}次挑战次数",
						new Object[] { Integer.valueOf(vip.warriorTrialTimes) }));
			if (vip.treasureTrialTimes > 0)
				p.addPrivileges(MessageFormat.format("金书银马每天可以购买{0}次挑战次数",
						new Object[] { Integer.valueOf(vip.treasureTrialTimes) }));
			if (vip.touchGoldTimes > 0)
				p.addPrivileges(
						MessageFormat.format("每天可以进行官银兑换{0}次", new Object[] { Integer.valueOf(vip.touchGoldTimes) }));
			if (vip.trainTimes > 0)
				p.addPrivileges(
						MessageFormat.format("每天可以免费领取训练点{0}次", new Object[] { Integer.valueOf(vip.trainTimes) }));
			if (vip.level >= 3) {
				p.addPrivileges("夺宝解锁一键抢夺功能");
			}
			if (vip.mapTimes > 0)
				p.addPrivileges(
						MessageFormat.format("普通副本每天可重置{0}次挑战次数", new Object[] { Integer.valueOf(vip.mapTimes) }));
			if (vip.randomShopRefreshTimes > 0)
				p.addPrivileges(MessageFormat.format("神秘商店每天可元宝刷新{0}次",
						new Object[] { Integer.valueOf(vip.randomShopRefreshTimes) }));
			if (vip.divineRewardRefreshTimes > 0)
				p.addPrivileges(MessageFormat.format("占卜奖励每天可元宝刷新{0}次",
						new Object[] { Integer.valueOf(vip.divineRewardRefreshTimes) }));
			if (vip.towerLife > 0)
				p.addPrivileges(
						MessageFormat.format("过关斩将每天可购买{0}次失败挑战次数", new Object[] { Integer.valueOf(vip.towerLife) }));
			if (vip.worldCompetitionTimes > 0)
				p.addPrivileges(MessageFormat.format("争霸赛每天可购买{0}次挑战次数",
						new Object[] { Integer.valueOf(vip.worldCompetitionTimes) }));
			if (vip.shopLimits != null) {
				sb = new StringBuffer();
				int nr = 0;
				for (Integer i : vip.shopLimits.keySet()) {
					int exchgId = i.intValue();
					int count = ((Integer) vip.shopLimits.get(i)).intValue();
					ExchangeTemplate et = ((ExchangeService) Platform.getServiceManager().get(ExchangeService.class))
							.getTemplate(exchgId);
					if ((et != null) && (et.reward.template != null)) {
						sb.append(et.reward.template.name).append("x").append(count).append(" ");
						++nr;
					}

					if (nr % 3 == 0) {
						p.addPrivileges(MessageFormat.format("每天可购买{0}", new Object[] { sb.toString() }));
						sb = new StringBuffer();
					}
				}
				if (sb.length() > 0) {
					p.addPrivileges(MessageFormat.format("每天可购买{0}", new Object[] { sb.toString() }));
				}
			}
			privileges.add(p.build());
		}
	}

	/**
	 * 加载vip用户的数据
	 */
	private void loadData() throws Exception {
		int i;
		String buy;
		List<Row> list = ExcelUtils.getRowList("vip.xls", 2);
		for (Row row : list) {

			Reward localReward2;
			Reward localReward3;
			Reward r;
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			Vip vip = new Vip();
			vip.level = (int) row.getCell(pos++).getNumericCellValue();
			vip.charge = (int) row.getCell(pos++).getNumericCellValue();
			vip.vipBagId = (int) row.getCell(pos++).getNumericCellValue();
			String vipBag = row.getCell(pos++).getStringCellValue();
			if (!(vipBag.equals("-1"))) {
				String[] ls = vipBag.split(",");
				vip.vipBag = new ArrayList(ls.length);
				int len = ls.length;
				for (i = 0; i < len; ++i) {
					String s = ls[i];
					r = new Reward(s);
					vip.vipBag.add(r);
				}
			}
			vip.vipBagPrice = (int) row.getCell(pos++).getNumericCellValue();
			String dayRewards = row.getCell(pos++).getStringCellValue();
			if (!(dayRewards.equals("-1"))) {
				String[] ls = dayRewards.split(",");
				vip.dayRewards = new ArrayList(ls.length);
				int len = ls.length;
				for (i = 0; i < len; ++i) {
					String s = ls[i];
					r = new Reward(s);
					vip.dayRewards.add(r);
				}
			}
			vip.proMapTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.moneyTrialTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.warriorTrialTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.treasureTrialTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.touchGoldTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.mapTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.randomShopRefreshTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.cherishShopRefreshTimes = (int) row.getCell(pos++).getNumericCellValue();
			vip.towerLife = (int) row.getCell(pos++).getNumericCellValue();
			vip.worldCompetitionTimes = (int) row.getCell(pos++).getNumericCellValue();
			buy = row.getCell(pos++).getStringCellValue();
			if (!(buy.equals("-1"))) {
				String[] ls = buy.split(",");
				vip.shopLimits = new HashMap();
				int len = ls.length;
				for (i = 0; i < len; ++i) {
					String s = ls[i];
					int[] ids = Calc.split(s, "\\|");
					vip.shopLimits.put(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]));
				}
			}
			vip.trainTimes = (int) row.getCell(pos++).getNumericCellValue();

			vips.add(vip);
		}

		List<Row> list3 = ExcelUtils.getRowList("vip.xls", 2, 2);
		String fr = list3.get(0).getCell(0).getStringCellValue();
		String[] ls = fr.split(",");
		int dayRewards = ls.length;
		for (int vipBag = 0; vipBag < dayRewards; ++vipBag) {
			String s = ls[vipBag];
			Reward r = new Reward(s);
			firstPayRewards.add(r);
		}

		List<Row> list2 = ExcelUtils.getRowList("vip.xls", 2, 1);
		for (Row row : list2) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			String price = row.getCell(pos++).getStringCellValue();
			int[] array = Calc.split(price, ",");
			ChallengeAddPrice.prices.put(Integer.valueOf(id), array);
		}
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		vips.clear();
		loadData();
		generatePrivileges();
	}

	public int[] getCodes() {
		return new int[] { 2165, 2163, 2167, 2169, 2173, 2175, 2171, 2161, 1253, 1259 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 2165:
			getReward(player);
			break;
		case 2163:
			rewardInfo(player);
			break;
		case 2161:
			PbUp.VipInfo v = PbUp.VipInfo.parseFrom(packet.getData());
			info(player, v.getLevel());
			break;
		case 2167:
			firstRewardGet(player);
			break;
		case 2169:
			firstRewardInfo(player);
			break;
		case 2173:
			PbUp.VipShopBuy buy = PbUp.VipShopBuy.parseFrom(packet.getData());
			shopBuy(player, buy.getLevel());
			break;
		case 2175:
			PbUp.VipShopDetail detail = PbUp.VipShopDetail.parseFrom(packet.getData());
			shopDetail(player, detail.getLevel());
			break;
		case 2171:
			shopInfo(player);
			break;
		case 1253:
			challengeTimeBuy(player, packet);
			break;
		case 1259:
			challengeTimeBuyCost(player, packet);
		}
	}

	private void shopBuy(Player player, int level) {
		Set set = player.getPool().getIntegers(9);
		PbDown.VipShopBuyRst.Builder rst = PbDown.VipShopBuyRst.newBuilder();
		rst.setResult(false);
		if (set.contains(Integer.valueOf(level))) {
			rst.setErrorInfo("已经购买过该礼包");
		} else {
			Vip vip = (Vip) vips.get(level);
			if ((vip != null) && (vip.vipBagPrice > 0)) {
				if (player.getJewels() < vip.vipBagPrice) {
					rst.setErrorInfo("元宝不足");
				} else {
					player.decJewels(vip.vipBagPrice, "vipshop");
					for (Reward r : vip.vipBag) {
						r.add(player, "vipshop");
					}
					set.add(Integer.valueOf(level));
					player.notifyGetItem(2, vip.vipBag);
					rst.setResult(true);

					Platform.getEventManager().addEvent(new Event(2081, new Object[] { player }));
				}
			}
		}

		player.send(2174, rst.build());
	}

	private void shopDetail(Player player, int level) {
		PbDown.VipShopBuyDetail.Builder rst = PbDown.VipShopBuyDetail.newBuilder();
		rst.setResult(true).setLevel(level);
		if ((level >= 0) && (level < vips.size())) {
			Vip vip = (Vip) vips.get(level);
			if ((vip != null) && (vip.vipBag != null)) {
				for (Reward r : vip.vipBag) {
					rst.addRewards(r.genPbReward());
				}
			}
		}

		player.send(2176, rst.build());
	}

	private void shopInfo(Player player) {
		PbDown.VipShopInfoRst.Builder rst = PbDown.VipShopInfoRst.newBuilder();

		rst.setResult(true);
		Set set = player.getPool().getIntegers(9);
		for (Vip vip : vips) {
			if ((!(set.contains(Integer.valueOf(vip.level)))) && (vip.vipBagId != -1)) {
				PbCommons.VipShopRecord.Builder r = PbCommons.VipShopRecord.newBuilder();
				r.setItemTemplateId(vip.vipBagId).setLevel(vip.level).setPrice(vip.vipBagPrice);
				rst.addRecords(r);
			}

		}

		player.send(2172, rst.build());
	}

	private void firstRewardGet(Player player) {
		PbDown.VipFirstRewardGetRst.Builder rst = PbDown.VipFirstRewardGetRst.newBuilder();
		if (player.getPool().getBool(8, false)) {
			rst.setResult(false);
			rst.setErrorInfo("已经领取过该奖励");
		} else if (!(player.isAlreadyCharge())) {
			rst.setResult(false);
			rst.setErrorInfo("尚未充值");
		} else {
			for (Reward r : firstPayRewards) {
				r.add(player, "firstCharge");
			}
			player.notifyGetItem(2, firstPayRewards);

			rst.setResult(true);
			player.getPool().set(8, Boolean.valueOf(true));
			Platform.getEventManager().addEvent(new Event(2066, new Object[] { player }));
		}
		player.send(2168, rst.build());
	}

	private void firstRewardInfo(Player player) {
		PbDown.VipFirstRewardInfoRst.Builder rst = PbDown.VipFirstRewardInfoRst.newBuilder();
		rst.setResult(true);
		for (Reward r : firstPayRewards) {
			rst.addRewards(r.genPbReward());
		}
		rst.setFetched(player.getPool().getBool(8, false));
		rst.setIsCharge(player.isAlreadyCharge());
		player.send(2170, rst.build());
	}

	private void getReward(Player player) {
		PbDown.VipRewardsGetRst.Builder rst = PbDown.VipRewardsGetRst.newBuilder();
		rst.setResult(false);
		Vip vip = player.getVip();
		if (vip.dayRewards == null) {
			rst.setErrorInfo("当前VIP等级无每日奖励可领取");
		} else if (player.getPool().getBool(7, false)) {
			rst.setErrorInfo("今天的奖励已经领取过，请明日再来");
		} else if (player.getBags().getFullBag() != -1) {
			rst.setErrorInfo("背包已满，请先清理背包");
		} else {
			rst.setResult(true);
			for (Reward r : vip.dayRewards) {
				r.add(player, "vipdayreward");
			}
			player.getPool().set(7, Boolean.valueOf(true));
			player.notifyGetItem(2, vip.dayRewards);
			Platform.getEventManager().addEvent(new Event(2071, new Object[] { player }));
		}

		player.send(2166, rst.build());
	}

	private void rewardInfo(Player player) {
		PbDown.VipRewardsInfoRst.Builder rst = PbDown.VipRewardsInfoRst.newBuilder();
		Vip vip = player.getVip();
		if (vip.dayRewards != null) {
			rst.setResult(true);
			for (Reward r : vip.dayRewards) {
				rst.addRewards(r.genPbReward());
			}
			rst.setFetched(player.getPool().getBool(7, false));
		} else {
			rst.setResult(false);
			rst.setErrorInfo("当前VIP等级无每日奖励可领取");
		}
		player.send(2164, rst.build());
	}

	private void info(Player player, int level) {
		PbDown.VipInfoRst.Builder rst = PbDown.VipInfoRst.newBuilder();
		rst.setResult(true);
		if (level >= privileges.size()) {
			rst.setResult(false);
			rst.setErrorInfo("已达最大VIP等级");
		} else {
			Vip vip = player.getVip();
			Vip next = vip.getNext();
			rst.setLevel(vip.level).setCharge(player.getCharge());
			if (next == null)
				rst.setMaxCharge(-1);
			else {
				rst.setMaxCharge(next.charge);
			}
			rst.setPrivileges((PbCommons.VipPrivileges) privileges.get(level));
		}

		player.send(2162, rst.build());
	}

	private void challengeTimeBuy(Player player, PbPacket.Packet packet) {
		int addCount;
		int price;
		PbDown.ChallengeTimeBuyRst.Builder b = PbDown.ChallengeTimeBuyRst.newBuilder().setResult(true);
		PbCommons.VipBuyTimeType type = null;
		int mapId = 0;
		int stageId = 0;
		try {
			PbUp.ChallengeTimeBuy req = PbUp.ChallengeTimeBuy.parseFrom(packet.getData());
			type = req.getType();
			if (type.getNumber() != 3)
				return;
			if ((req.hasMapId()) && (req.hasStageId())) {
				mapId = req.getMapId();
				stageId = req.getStageId();
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1254, b.build());
			return;
		}
		if (type.getNumber() == 1) {
			int count = player.getPool().getInt(2, 20);
			if (count >= 20) {
				b.setResult(false);
				b.setErrInfo("无需购买");
			} else {
				addCount = player.getPool().getInt(11, 0);
				if (addCount >= player.getVip().worldCompetitionTimes) {
					notifyChallengeBuyTimeLack(player, type);
					return;
				}
				price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
				if (player.getJewels() < price) {
					PlayerService.JewelsLackNotice(player);
					return;
				}
				player.decJewels(price, "competitiontimeadd");
				player.getPool().set(11, Integer.valueOf(addCount + 1));
				player.getPool().set(2, Integer.valueOf(count + 1));
				b.setSurplus(count + 1);
			}

		} else if (type.getNumber() == 2) {
			TowerRecord r = player.getTowerRecord();
			if (r.getLeftChallengeTimes() > 0) {
				b.setResult(false);
				b.setErrInfo("无需购买");
			} else {
				addCount = player.getPool().getInt(12, 0);
				if (addCount >= player.getVip().towerLife) {
					notifyChallengeBuyTimeLack(player, type);
					return;
				}
				price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
				if (player.getJewels() < price) {
					PlayerService.JewelsLackNotice(player);
					return;
				}
				player.decJewels(price, "towertimeadd");
				player.getPool().set(12, Integer.valueOf(addCount + 1));
				r.setChallengeTimes(r.getChallengeTimes() - 1);
				b.setSurplus(r.getLeftChallengeTimes());
			}
		} else {
			MapRecord mr;
			if (type.getNumber() == 3) {
				mr = player.getMapRecord();
				ClearMap cm = mr.getClearMap(mapId);
				if (cm == null) {
					return;
				}
				ClearStage cs = cm.getClearStage(stageId);
				if (cs == null) {
					return;
				}
				if (cs.getLeftChanllengeTimes() > 0) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					int resetCount = cs.getResetTimes();
					if (resetCount >= player.getVip().mapTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + resetCount);
					if (player.getJewels() < price) {
						PlayerService.JewelsLackNotice(player);
						return;
					}
					player.decJewels(price, "maptimeadd");
					cs.setResetTimes(1 + resetCount);
					cs.setChanllengeTimes(0);
					b.setSurplus(cs.getLeftChanllengeTimes());
				}

			} else if (type.getNumber() == 4) {
				mr = player.getMapRecord();
				if (mr.getLeftProMapChallengeTimes() > 3) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(14, 0);
					if (addCount >= player.getVip().proMapTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					if (player.getJewels() < price) {
						PlayerService.JewelsLackNotice(player);
						return;
					}
					player.decJewels(price, "promaptimeadd");
					player.getPool().set(14, Integer.valueOf(addCount + 1));
					mr.setProMapChallengeTimes(mr.getProMapChallengeTimes() - 1);
					b.setSurplus(mr.getLeftProMapChallengeTimes());
				}

			} else if (type.getNumber() == 5) {
				mr = player.getMapRecord();
				if (mr.getActivityMapLeftTimes(5) >= 2) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(15, 0);
					if (addCount >= player.getVip().moneyTrialTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					if (player.getJewels() < price) {
						PlayerService.JewelsLackNotice(player);
						return;
					}
					player.decJewels(price, "moneytrialtimeadd");
					player.getPool().set(15, Integer.valueOf(addCount + 1));
					mr.setMoneyMapLeftTimes(mr.getMoneyMapLeftTimes() + 1);
					b.setSurplus(mr.getActivityMapLeftTimes(5));
				}

			} else if (type.getNumber() == 6) {
				mr = player.getMapRecord();
				if (mr.getActivityMapLeftTimes(4) >= 2) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(17, 0);
					if (addCount >= player.getVip().treasureTrialTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					if (player.getJewels() < price) {
						PlayerService.JewelsLackNotice(player);
						return;
					}
					player.decJewels(price, "treasuretimeadd");
					player.getPool().set(17, Integer.valueOf(addCount + 1));
					mr.setTreasureMapLeftTimes(mr.getTreasureMapLeftTimes() + 1);
					b.setSurplus(mr.getActivityMapLeftTimes(4));
				}

			} else if (type.getNumber() == 7) {
				mr = player.getMapRecord();
				if (mr.getActivityMapLeftTimes(3) >= 2) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(16, 0);
					if (addCount >= player.getVip().warriorTrialTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					if (player.getJewels() < price) {
						PlayerService.JewelsLackNotice(player);
						return;
					}
					player.decJewels(price, "warriortimeadd");
					player.getPool().set(16, Integer.valueOf(addCount + 1));
					mr.setWarriorMapLeftTimes(mr.getWarriorMapLeftTimes() + 1);
					b.setSurplus(mr.getActivityMapLeftTimes(3));
				}
			}
		}

		player.send(1254, b.build());
	}

	private void challengeTimeBuyCost(Player player, PbPacket.Packet packet) {
		PbCommons.VipBuyTimeType type = null;
		try {
			PbUp.ChallengeTimeBuyCost req = PbUp.ChallengeTimeBuyCost.parseFrom(packet.getData());
			type = req.getType();
			if (type.getNumber() != 3) {
				notifyChallengeTimeBuyCost(player, type);
				return;
			}
			if ((req.hasMapId()) && (req.hasStageId())) {
				int mapId = req.getMapId();
				int stageId = req.getStageId();
				notifyChallengeTimeBuyCost(player, type, mapId, stageId);
				notifyChallengeTimeBuyCost(player, type);
			}
			return;
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			return;
		}

	}

	public static void notifyChallengeTimeBuyCost(Player player, PbCommons.VipBuyTimeType type) {
		notifyChallengeTimeBuyCost(player, type, -1, -1);
	}

	public static void notifyChallengeTimeBuyCost(Player player, PbCommons.VipBuyTimeType type, int mapId,
			int stageId) {
		int addCount;
		int price;
		PbDown.ChallengeTimeBuyCostRst.Builder b = PbDown.ChallengeTimeBuyCostRst.newBuilder().setResult(true);
		if (type.getNumber() == 1) {
			int count = player.getPool().getInt(2, 20);
			if (count >= 20) {
				b.setResult(false);
				b.setErrInfo("无需购买");
			} else {
				addCount = player.getPool().getInt(11, 0);
				if (addCount >= player.getVip().worldCompetitionTimes) {
					notifyChallengeBuyTimeLack(player, type);
					return;
				}
				price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
				b.setCost(price);
			}
		} else if (type.getNumber() == 2) {
			TowerRecord r = player.getTowerRecord();
			if (r.getLeftChallengeTimes() > 0) {
				b.setResult(false);
				b.setErrInfo("无需购买");
			} else {
				addCount = player.getPool().getInt(12, 0);
				if (addCount >= player.getVip().towerLife) {
					notifyChallengeBuyTimeLack(player, type);
					return;
				}
				price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
				b.setCost(price);
			}
		} else {
			MapRecord mr;
			if (type.getNumber() == 3) {
				mr = player.getMapRecord();
				ClearMap cm = mr.getClearMap(mapId);
				if (cm == null) {
					return;
				}
				ClearStage cs = cm.getClearStage(stageId);
				if (cs == null) {
					return;
				}
				if (cs.getLeftChanllengeTimes() > 0) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					int resetCount = cs.getResetTimes();
					if (resetCount >= player.getVip().mapTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + resetCount);
					b.setCost(price);
				}
			} else if (type.getNumber() == 4) {
				mr = player.getMapRecord();
				if (mr.getLeftProMapChallengeTimes() > 3) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(14, 0);
					if (addCount >= player.getVip().proMapTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					b.setCost(price);
				}
			} else if (type.getNumber() == 5) {
				mr = player.getMapRecord();
				if (mr.getActivityMapLeftTimes(5) >= 2) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(15, 0);
					if (addCount >= player.getVip().moneyTrialTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					b.setCost(price);
				}
			} else if (type.getNumber() == 6) {
				mr = player.getMapRecord();
				if (mr.getActivityMapLeftTimes(4) >= 2) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(17, 0);
					if (addCount >= player.getVip().treasureTrialTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					b.setCost(price);
				}
			} else if (type.getNumber() == 7) {
				mr = player.getMapRecord();
				if (mr.getActivityMapLeftTimes(3) >= 2) {
					b.setResult(false);
					b.setErrInfo("无需购买");
				} else {
					addCount = player.getPool().getInt(16, 0);
					if (addCount >= player.getVip().warriorTrialTimes) {
						notifyChallengeBuyTimeLack(player, type);
						return;
					}
					price = ChallengeAddPrice.getPrice(type.getNumber(), 1 + addCount);
					b.setCost(price);
				}
			}
		}
		b.setType(type);
		b.setMapId(mapId);
		b.setStageId(stageId);
		player.send(1260, b.build());
	}

	public static void notifyChallengeBuyTimeLack(Player player, PbCommons.VipBuyTimeType type) {
		PbDown.ChallengeBuyTimeLackRst.Builder b = PbDown.ChallengeBuyTimeLackRst.newBuilder();
		Vip vip = player.getVip();
		Vip next = vip.getNext();
		boolean flag = true;
		while (next != null) {
			int curNum = 0;
			int nextNum = 0;
			switch (type.getNumber()) {
			case 1:
				curNum = vip.worldCompetitionTimes;
				nextNum = next.worldCompetitionTimes;
				break;
			case 2:
				curNum = vip.towerLife;
				nextNum = next.towerLife;
				break;
			case 3:
				curNum = vip.mapTimes;
				nextNum = next.mapTimes;
				break;
			case 4:
				curNum = vip.proMapTimes;
				nextNum = next.proMapTimes;
				break;
			case 5:
				curNum = vip.moneyTrialTimes;
				nextNum = next.moneyTrialTimes;
				break;
			case 6:
				curNum = vip.treasureTrialTimes;
				nextNum = next.treasureTrialTimes;
				break;
			case 7:
				curNum = vip.warriorTrialTimes;
				nextNum = next.warriorTrialTimes;
			}

			if (nextNum > curNum) {
				b.setVipLevel(next.level);
				b.setAddTime(nextNum - curNum);
				flag = false;
				break;
			}
			next = next.getNext();
		}
		b.setType(type);
		if (flag) {
			b.setVipLevel(-1);
		}
		player.send(1258, b.build());
	}

	public static void notifyVipBuyTimeLack(Player player, PbCommons.VipBuyTimeType type) {
		notifyVipBuyTimeLack(player, type, -1);
	}

	public static void notifyVipBuyTimeLack(Player player, PbCommons.VipBuyTimeType type, int shopId) {
		PbDown.VipBuyTimeLackRst.Builder b = PbDown.VipBuyTimeLackRst.newBuilder();
		Vip vip = player.getVip();
		Vip next = vip.getNext();
		boolean flag = true;
		while (next != null) {
			int curNum = 0;
			int nextNum = 0;
			switch (type.getNumber()) {
			case 8:
				curNum = ((Integer) vip.shopLimits.get(Integer.valueOf(shopId))).intValue();
				nextNum = ((Integer) next.shopLimits.get(Integer.valueOf(shopId))).intValue();
				break;
			case 9:
				curNum = vip.touchGoldTimes;
				nextNum = next.touchGoldTimes;
				break;
			case 10:
				curNum = vip.randomShopRefreshTimes;
				nextNum = next.randomShopRefreshTimes;
				break;
			case 11:
				curNum = vip.divineRewardRefreshTimes;
				nextNum = next.divineRewardRefreshTimes;
				break;
			case 12:
				curNum = vip.cherishShopRefreshTimes;
				nextNum = next.cherishShopRefreshTimes;
				CherishDiscount cd = RandomShopService.getCherishDiscount();
				if (cd != null) {
					curNum += cd.getAddTime(vip.level);
					nextNum += cd.getAddTime(next.level);
				}

			}

			if (nextNum > curNum) {
				b.setVipLevel(next.level);
				b.setAddTime(nextNum - curNum);
				flag = false;
				break;
			}
			next = next.getNext();
		}

		b.setType(type);
		if (flag) {
			b.setVipLevel(-1);
		}
		player.send(1262, b.build());
	}
}
