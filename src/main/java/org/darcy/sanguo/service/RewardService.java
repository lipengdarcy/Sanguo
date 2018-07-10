package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.pay.PayItem;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.reward.CookWine;
import org.darcy.sanguo.reward.DrawMoney;
import org.darcy.sanguo.reward.GrowReward;
import org.darcy.sanguo.reward.LevelReward;
import org.darcy.sanguo.reward.LoginReward;
import org.darcy.sanguo.reward.OnlineReward;
import org.darcy.sanguo.reward.RewardRecord;
import org.darcy.sanguo.reward.SignReward;
import org.darcy.sanguo.reward.TimeLimitItem;
import org.darcy.sanguo.reward.TimeLimitReward;
import org.darcy.sanguo.reward.TouchGolden;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.Vip;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbActivity;
import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class RewardService implements Service, PacketHandler {
	public static final int MAX_TRAIN_COUNT = 100;
	public static Map<Integer, LevelReward> levelRewards = new HashMap<Integer, LevelReward>();

	public static Map<Integer, SignReward> signRewards = new HashMap<Integer, SignReward>();

	public static Map<Integer, LoginReward> loginRewards = new HashMap<Integer, LoginReward>();

	public static Map<Integer, TouchGolden> touchGoldens = new HashMap<Integer, TouchGolden>();

	public static Map<Integer, TouchGolden> trains = new HashMap<Integer, TouchGolden>();

	public static Map<Integer, GrowReward> growRewards = new HashMap<Integer, GrowReward>();

	public static Map<Integer, DrawMoney> drawMoneys = new HashMap<Integer, DrawMoney>();

	public static Map<Integer, CookWine> cookWines = new HashMap<Integer, CookWine>();

	public static Map<Integer, OnlineReward> onlineRewards = new HashMap<Integer, OnlineReward>();

	public static Map<Integer, List<Reward>> globalChargeRewards = new HashMap<Integer, List<Reward>>();

	public static Map<Integer, ArrayList<Reward>> superReward = new HashMap<Integer, ArrayList<Reward>>();

	public static Map<Integer, List<Reward>> login7DayRewards = new HashMap<Integer, List<Reward>>();

	public void startup() throws Exception {
		loadRewards();
		loadCookWine();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1145, 1147, 1149, 1151, 1153, 1155, 1223, 1225, 1227, 1229, 1231, 1233, 1235, 1245, 1247,
				2209, 1325, 1327, 1331, 1333, 3003, 3001, 1335, 1361, 1363, 1345, 3011 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1145:
			levelRewardView(player);
			break;
		case 1147:
			levelRewardGet(player, packet);
			break;
		case 1149:
			signInfo(player);
			break;
		case 1151:
			getSignReward(player);
			break;
		case 1153:
			cookWineInfo(player);
			break;
		case 1155:
			cookWine(player);
			break;
		case 1223:
			loginRewardInfo(player);
			break;
		case 1225:
			loginRewardGet(player, packet);
			break;
		case 1227:
			touchGoldenInfo(player);
			break;
		case 1229:
			touchGolden(player);
			break;
		case 1231:
			growRewardInfo(player);
			break;
		case 1233:
			growRewardBuy(player);
			break;
		case 1235:
			growRewardGet(player, packet);
			break;
		case 1245:
			drawMoneyInfo(player);
			break;
		case 1247:
			drawMoneyGet(player);
			break;
		case 2209:
			train(player);
			break;
		case 1327:
			onlineRewardGet(player);
			break;
		case 1325:
			onlineRewardInfo(player);
			break;
		case 1331:
			globalChargeRewardInfo(player);
			break;
		case 1333:
			globalChargeRewardGet(player, packet);
			break;
		case 3001:
			superRewardInfo(player, packet);
			break;
		case 3003:
			superRewardGet(player);
			break;
		case 1335:
			loginReward7WarriorGet(player, packet);
			break;
		case 1361:
			login7DayRewardInfo(player);
			break;
		case 1363:
			login7DayRewardGet(player, packet);
			break;
		case 1345:
			timeLimitRewardInfo(player, packet);
			break;
		case 3011:
			buyTimelimitReward(player, packet);
		}
	}

	private void buyTimelimitReward(Player player, PbPacket.Packet packet) {
		PbDown.BuyTimeLimitRewardRst.Builder builder = PbDown.BuyTimeLimitRewardRst.newBuilder().setResult(true);
		try {
			TimeLimitItem item;
			Reward r;
			PbUp.BuyTimeLimitReward rep = PbUp.BuyTimeLimitReward.parseFrom(packet.getData());
			long id = rep.getId();
			int type = rep.getButType();

			TimeLimitReward curTimelimit = (TimeLimitReward) Platform.getEntityManager()
					.getFromEhCache(TimeLimitReward.class.getName(), Long.valueOf(id));
			RewardRecord rr = player.getRewardRecord();

			if ((rr.getCurTimeLimitRewardId() != id) || (curTimelimit == null)) {
				builder.setResult(false);
				builder.setErrInfo("活动尚未开启或已结束");
				player.send(3012, builder.build());
				return;
			}

			if (((curTimelimit.lastTime > 0L) ? rr.getActivateTimeLimitRewardTime() + curTimelimit.lastTime
					: curTimelimit.end) - System.currentTimeMillis() <= 0L) {
				builder.setResult(false);
				builder.setErrInfo("活动尚未开启或已结束");
				player.send(3012, builder.build());
				return;
			}

			List<Reward> rs = new ArrayList<Reward>();
			if (type == 1) {
				int subId = rep.getSubId();
				item = (TimeLimitItem) curTimelimit.items.get(Integer.valueOf(subId));
				Map<Integer, Integer> items = rr.buyRecord(Long.valueOf(id));
				if (item.price > player.getJewels())
					return;
				if ((items != null) && (items.get(Integer.valueOf(subId)) != null)
						&& (((Integer) items.get(Integer.valueOf(subId))).intValue() >= item.count)) {
					return;
				}

				player.decJewels(item.price, "timelimitreward");
				for (Iterator<?> localIterator = item.rewards.iterator(); localIterator.hasNext();) {
					r = (Reward) localIterator.next();
					r.add(player, "timelimitreward");
					rs.add(r);
				}

				if (items == null) {
					items = new HashMap<Integer, Integer>();
					rr.putBuyRecord(Long.valueOf(id), items);
				}
				if (items.get(Integer.valueOf(subId)) == null)
					items.put(Integer.valueOf(subId), Integer.valueOf(1));
				else
					items.put(Integer.valueOf(subId),
							Integer.valueOf(((Integer) items.get(Integer.valueOf(subId))).intValue() + 1));
			} else {
				if ((rr.getTimeLimitRewardCount() >= curTimelimit.count)
						|| (curTimelimit.salePrice > player.getJewels()))
					return;
				player.decJewels(curTimelimit.salePrice, "timelimitreward");
				for (TimeLimitItem a : curTimelimit.items.values()) {
					for (Reward b : a.rewards) {
						b.add(player, "timelimitreward");
						rs.add(b);
					}
				}
				rr.setTimeLimitRewardCount(rr.getTimeLimitRewardCount() + 1);
			}

			player.notifyGetItem(2, rs);
			Platform.getEventManager().addEvent(new Event(2104, new Object[] { player }));
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
		}

		player.send(3012, builder.build());
	}

	public static TimeLimitReward getTimeLimitRewardByEndTime(long time) {
		List<?> list = Platform.getEntityManager().getAllFromEhCache(TimeLimitReward.class.getName());
		if (list != null) {
			for (Iterator<?> localIterator = list.iterator(); localIterator.hasNext();) {
				Object obj = localIterator.next();
				TimeLimitReward reward = (TimeLimitReward) obj;
				if (reward.isInTimeByEnd(time)) {
					return reward;
				}
			}
		}
		return null;
	}

	public static TimeLimitReward getTimeLimitRewardByActivateTime(long time) {
		List<?> list = Platform.getEntityManager().getAllFromEhCache(TimeLimitReward.class.getName());
		if (list != null) {
			for (Iterator<?> localIterator = list.iterator(); localIterator.hasNext();) {
				Object obj = localIterator.next();
				TimeLimitReward reward = (TimeLimitReward) obj;
				if (reward.isInTimeByActivate(time)) {
					return reward;
				}
			}
		}
		return null;
	}

	private void timeLimitRewardInfo(Player player, PbPacket.Packet packet) {
		PbDown.TimeLimitRewardInfoRst.Builder b = PbDown.TimeLimitRewardInfoRst.newBuilder().setResult(true);

		RewardRecord rr = player.getRewardRecord();
		TimeLimitReward tlr = rr.getCurTimeLimitReward();

		if (tlr == null) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			b.setPrice(tlr.salePrice);
			b.setName(tlr.name);
			for (Reward r : tlr.rewards) {
				b.addRewards(r.genPbReward());
			}
			b.setCur(rr.getTimeLimitRewardCount());
			b.setTotal(tlr.count);
			b.setOriginal(tlr.origPrice);
			b.setId(tlr.id);
			for (TimeLimitItem item : tlr.items.values()) {
				Map<?, ?> items = rr.buyRecord(Long.valueOf(tlr.id));
				PbCommons.TimeLimitReward.Builder builder = item.genPbTimeLimitReward();
				if ((items != null) && (items.get(Integer.valueOf(item.id)) != null))
					builder.setSurplus(item.count - ((Integer) items.get(Integer.valueOf(item.id))).intValue());
				else {
					builder.setSurplus(item.count);
				}
				b.addTimelimits(builder);
			}
		}

		player.send(1346, b.build());
	}

	private void loadRewards() {
		int reward; // 奖励值
		int id, pos, i, j;
		int count;
		int day;
		int cost;
		String rewardStr;
		TouchGolden tg;
		String[] rewardArray;
		int weight;
		int start;
		int end;
		DrawMoney.DrawMoneyWeight dmw;

		List<Row> list = ExcelUtils.getRowList("reward.xls", 2);
		for (Row row : list) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			int level = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			LevelReward levelReward = new LevelReward();
			levelReward.id = id;
			levelReward.level = level;
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (String str : rewardArray) {
					levelReward.rewards.add(new Reward(str));
				}
			}
			levelRewards.put(Integer.valueOf(id), levelReward);
		}

		List<Row> list1 = ExcelUtils.getRowList("reward.xls", 2, 1);
		for (Row row : list1) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			day = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			SignReward signReward = new SignReward();
			signReward.id = id;
			signReward.day = day;
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (String str : rewardArray) {
					signReward.rewards.add(new Reward(str));
				}
			}
			signRewards.put(Integer.valueOf(day), signReward);
		}

		List<Row> list2 = ExcelUtils.getRowList("reward.xls", 2, 2);
		for (Row row : list2) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			day = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			LoginReward loginReward = new LoginReward();
			loginReward.id = id;
			loginReward.day = day;
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (String str : rewardArray) {
					loginReward.rewards.add(new Reward(str));
				}
			}
			loginRewards.put(Integer.valueOf(day), loginReward);
		}

		List<Row> list3 = ExcelUtils.getRowList("reward.xls", 2, 3);
		for (Row row : list3) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			count = (int) row.getCell(pos++).getNumericCellValue();
			cost = (int) row.getCell(pos++).getNumericCellValue();
			reward = (int) row.getCell(pos++).getNumericCellValue();

			tg = new TouchGolden();
			tg.count = count;
			tg.cost = cost;
			tg.reward = reward;
			touchGoldens.put(Integer.valueOf(count), tg);
		}

		List<Row> list6 = ExcelUtils.getRowList("reward.xls", 2, 6);
		for (Row row : list6) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			count = (int) row.getCell(pos++).getNumericCellValue();
			cost = (int) row.getCell(pos++).getNumericCellValue();
			reward = (int) row.getCell(pos++).getNumericCellValue();

			tg = new TouchGolden();
			tg.count = count;
			tg.cost = cost;
			tg.reward = reward;
			trains.put(Integer.valueOf(count), tg);
		}

		List<Row> list4 = ExcelUtils.getRowList("reward.xls", 2, 4);
		for (Row row : list4) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int level = (int) row.getCell(pos++).getNumericCellValue();
			reward = (int) row.getCell(pos++).getNumericCellValue();

			GrowReward gr = new GrowReward();
			gr.level = level;
			gr.reward = reward;

			growRewards.put(Integer.valueOf(level), gr);
		}

		List<Row> list5 = ExcelUtils.getRowList("reward.xls", 2, 5);
		int num = 1;
		for (Row row : list5) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			cost = (int) row.getCell(pos++).getNumericCellValue();
			count = (int) row.getCell(pos++).getNumericCellValue();
			DrawMoney dm = new DrawMoney();
			dm.num = num;
			dm.cost = cost;
			for (j = 0; j < count; ++j) {
				weight = (int) row.getCell(pos++).getNumericCellValue();
				start = (int) row.getCell(pos++).getNumericCellValue();
				end = (int) row.getCell(pos++).getNumericCellValue();
				dmw = new DrawMoney().new DrawMoneyWeight();
				dmw.weight = weight;
				dmw.start = start;
				dmw.end = end;
				dm.rewards.add(dmw);
			}
			drawMoneys.put(Integer.valueOf(num), dm);
			++num;
		}

		List<Row> list7 = ExcelUtils.getRowList("reward.xls", 2, 7);
		num = 1;
		for (Row row : list7) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int time = (int) row.getCell(pos++).getNumericCellValue();
			rewardStr = row.getCell(pos++).getStringCellValue();
			OnlineReward or = new OnlineReward();
			or.count = num;
			or.time = (time * 60 * 1000);
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (String a : rewardArray) {
					or.rewards.add(new Reward(a));
				}
			}

			onlineRewards.put(Integer.valueOf(num), or);
			++num;
		}

		List<Row> list8 = ExcelUtils.getRowList("reward.xls", 2, 8);
		for (Row row : list8) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			num = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			List<Reward> list_tmp = new ArrayList<Reward>();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (String str : rewardArray) {
					list_tmp.add(new Reward(str));
				}
			}

			globalChargeRewards.put(Integer.valueOf(num), list_tmp);
		}

		List<Row> list9 = ExcelUtils.getRowList("reward.xls", 2, 9);
		for (Row row : list9) {
			i = 1;
			if (i < list9.size()) {
				pos = 0;
				if (row != null) {
					if (row.getCell(pos) != null) {
						int rewardid = (int) row.getCell(pos++).getNumericCellValue();
						String rewards = row.getCell(pos++).getStringCellValue();
						ArrayList<Reward> rewardList = new ArrayList<Reward>();
						if ((rewards != null) && (!(rewards.equals("-1")))) {
							rewardArray = rewards.split(",");
							for (String a : rewardArray) {
								rewardList.add(new Reward(a));
							}
						}
						superReward.put(Integer.valueOf(rewardid), rewardList);
					}
				}
			}
		}

		List<Row> list10 = ExcelUtils.getRowList("reward.xls", 2, 10);
		for (Row row : list10) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			day = (int) row.getCell(pos++).getNumericCellValue();
			rewardStr = row.getCell(pos++).getStringCellValue();
			List<Reward> list_tmp = new ArrayList<Reward>();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (String str : rewardArray) {
					list_tmp.add(new Reward(str));
				}
			}
			login7DayRewards.put(Integer.valueOf(day), list_tmp);
		}
	}

	private void loadCookWine() {
		List<Row> list = ExcelUtils.getRowList("cookwine.xls", 2);
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int start = (int) row.getCell(pos++).getNumericCellValue();
			int end = (int) row.getCell(pos++).getNumericCellValue();
			int recover = (int) row.getCell(pos++).getNumericCellValue();
			CookWine cw = new CookWine();
			cw.setId(id);
			cw.setStart(start);
			cw.setEnd(end);
			cw.setRecover(recover);
			cookWines.put(Integer.valueOf(id), cw);

			StringBuilder sb = new StringBuilder();
			sb.append(cw.getStart()).append(" 0 0");
			new Crontab(sb.toString(), 2013);
			sb.setLength(0);
			sb.append(cw.getEnd()).append(" 0 0");
			new Crontab(sb.toString(), 2014);
		}
	}

	private void levelRewardView(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 5))) {
			return;
		}
		PbDown.LevelRewardViewRst.Builder builder = PbDown.LevelRewardViewRst.newBuilder();
		builder.setResult(true);
		RewardRecord rr = player.getRewardRecord();
		Iterator<Integer> itx = levelRewards.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			if (!(rr.getLevelRewardIds().contains(Integer.valueOf(id)))) {
				LevelReward lr = (LevelReward) levelRewards.get(Integer.valueOf(id));
				builder.addRewards(lr.genLevelReward());
			}
		}
		player.send(1146, builder.build());
	}

	private void levelRewardGet(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 5))) {
			return;
		}
		PbDown.LevelRewardGetRst.Builder builder = PbDown.LevelRewardGetRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LevelRewardGet get = PbUp.LevelRewardGet.parseFrom(packet.getData());
			id = get.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1148, builder.build());
			return;
		}
		RewardRecord rr = player.getRewardRecord();
		if (!(levelRewards.containsKey(Integer.valueOf(id)))) {
			builder.setResult(false);
			builder.setErrInfo("奖励不存在或已过期");
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (rr.getLevelRewardIds().contains(Integer.valueOf(id))) {
			builder.setResult(false);
			builder.setErrInfo("已经领取过该奖励");
		} else {
			LevelReward lr = (LevelReward) levelRewards.get(Integer.valueOf(id));
			if (player.getLevel() < lr.level) {
				builder.setResult(false);
				builder.setErrInfo("未到达领取奖励等级");
			} else {
				rr.levelReward(player, lr);
				builder.setResult(true);
				if ((lr.rewards != null) && (lr.rewards.size() > 0)) {
					for (Reward reward : lr.rewards) {
						builder.addRewards(reward.genPbReward());
					}
				}
				Platform.getEventManager().addEvent(new Event(2054, new Object[] { player }));
			}
		}
		player.send(1148, builder.build());
	}

	private void signInfo(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 11))) {
			return;
		}
		PbDown.SignInfoRst.Builder builder = PbDown.SignInfoRst.newBuilder().setResult(true)
				.setDay(player.getRewardRecord().getSignCount());
		for (SignReward reward : signRewards.values()) {
			builder.addRewards(reward.genSignReward(player.getRewardRecord().getSignCount(),
					player.getRewardRecord().isGetSignReward()));
		}
		player.send(1150, builder.build());
	}

	private void getSignReward(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 11))) {
			return;
		}
		PbDown.SignRst.Builder builder = PbDown.SignRst.newBuilder().setResult(true);
		if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (player.getRewardRecord().isGetSignReward()) {
			builder.setResult(false);
			builder.setErrInfo("今天已签到");
		} else {
			player.getRewardRecord().getSignReward();
			SignReward sr = (SignReward) signRewards.get(Integer.valueOf(player.getRewardRecord().getSignCount()));
			if (sr == null) {
				sr = (SignReward) signRewards.get(Integer.valueOf(signRewards.size()));
			}
			if ((sr.rewards != null) && (sr.rewards.size() > 0)) {
				for (Reward reward : sr.rewards) {
					reward.add(player, "signreward");
					builder.addRewards(reward.genPbReward());
				}
			}
			Platform.getEventManager().addEvent(new Event(2042, new Object[] { player }));
		}

		player.send(1152, builder.build());
	}

	private void cookWineInfo(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 25))) {
			return;
		}
		PbDown.CookWineInfoRst.Builder builder = PbDown.CookWineInfoRst.newBuilder().setResult(true);
		CookWine cw = getCurCookwine();
		if (cw == null) {
			builder.setStatus(PbDown.CookWineInfoRst.Status.CANNOT);
		} else if (player.getRewardRecord().isRecover(cw.getId())) {
			builder.setStatus(PbDown.CookWineInfoRst.Status.GET);
		} else {
			builder.setStatus(PbDown.CookWineInfoRst.Status.CAN);
			int value = cw.getRecover();
			if (ActivityInfo.isOpenActivity(7)) {
				value *= 2;
			}
			builder.setRecover(value);
		}

		player.send(1154, builder.build());
	}

	private void cookWine(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 25))) {
			return;
		}
		PbDown.CookWineRst.Builder builder = PbDown.CookWineRst.newBuilder().setResult(true);
		CookWine cw = getCurCookwine();
		if (cw == null) {
			builder.setResult(false);
			builder.setErrInfo("还未到煮酒时间");
		} else if (player.getRewardRecord().isRecover(cw.getId())) {
			builder.setResult(false);
			builder.setErrInfo("已经煮过酒了，请下次再来");
		} else {
			int value = player.getRewardRecord().recover(player, cw.getId());
			builder.setRecover(value);
			Platform.getEventManager().addEvent(new Event(2045, new Object[] { player }));
		}

		player.send(1156, builder.build());
	}

	private void loginRewardInfo(Player player) {
		PbDown.LoginRewardInfoRst.Builder b = PbDown.LoginRewardInfoRst.newBuilder().setResult(true);
		boolean[][] infos = player.getRewardRecord().getLoginInfo();
		for (int i = 0; i < infos.length; ++i) {
			int rewardId = i + 1;
			LoginReward lr = (LoginReward) loginRewards.get(Integer.valueOf(rewardId));
			if (lr != null) {
				boolean[] info = infos[i];
				if (info[0]) {
					if (info[1] == false)
						b.addRewards(lr.genLoginReward(true));
				} else {
					b.addRewards(lr.genLoginReward(false));
				}

			}

		}

		player.send(1224, b.build());
	}

	private void loginRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.LoginRewardGetRst.Builder builder = PbDown.LoginRewardGetRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LoginRewardGet get = PbUp.LoginRewardGet.parseFrom(packet.getData());
			id = get.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1226, builder.build());
			return;
		}
		RewardRecord rr = player.getRewardRecord();
		if (!(loginRewards.containsKey(Integer.valueOf(id)))) {
			builder.setResult(false);
			builder.setErrInfo("奖励不存在或已过期");
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else {
			LoginReward lr = (LoginReward) loginRewards.get(Integer.valueOf(id));
			if (!(rr.isLoginForReward(lr.day))) {
				builder.setResult(false);
				builder.setErrInfo("你不能领取该奖励");
			} else if (rr.isGetLoginReward(lr.day)) {
				builder.setResult(false);
				builder.setErrInfo("已经领取过该奖励");
			} else {
				rr.getLoginReward(player, lr);
				builder.setResult(true);
				if ((lr.rewards != null) && (lr.rewards.size() > 0)) {
					for (Reward reward : lr.rewards) {
						builder.addRewards(reward.genPbReward());
					}
				}
				Platform.getEventManager().addEvent(new Event(2061, new Object[] { player }));
			}
		}
		player.send(1226, builder.build());
	}

	private void loginReward7WarriorGet(Player player, PbPacket.Packet packet) {
		PbDown.LoginReward7WarriorGetRst.Builder builder = PbDown.LoginReward7WarriorGetRst.newBuilder()
				.setResult(true);
		int id = 0;
		try {
			PbUp.LoginReward7WarriorGetReq get = PbUp.LoginReward7WarriorGetReq.parseFrom(packet.getData());
			id = get.getTemplateId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1336, builder.build());
			return;
		}
		RewardRecord rr = player.getRewardRecord();
		if (!(LoginReward.is7Warrior(id))) {
			builder.setResult(false);
			builder.setErrInfo("奖励不存在或已过期");
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else {
			int day = 7;
			if (rr.getLogin7DayCount() < day) {
				builder.setResult(false);
				builder.setErrInfo("你不能领取该奖励");
			} else if (rr.getLogin7DayGet().contains(Integer.valueOf(day))) {
				builder.setResult(false);
				builder.setErrInfo("已经领取过该奖励");
			} else {
				Item item = ItemService.generateItem(id, player);
				player.getBags().addItem(item, 1, "loginreward");
				rr.getLogin7DayGet().add(Integer.valueOf(day));
				Platform.getEventManager().addEvent(new Event(2103, new Object[] { player }));
			}
		}
		player.send(1336, builder.build());
	}

	private void touchGoldenInfo(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 29))) {
			return;
		}
		PbDown.TouchGoldenInfoRst.Builder b = PbDown.TouchGoldenInfoRst.newBuilder().setResult(true);
		int count = player.getRewardRecord().getTouchGoldenCount();
		int trainCount = player.getRewardRecord().getTrainCount();
		Vip vip = player.getVip();
		int num = Math.min(vip.touchGoldTimes, count + 1);
		b.setSuplus(vip.touchGoldTimes - count);
		TouchGolden tg = (TouchGolden) touchGoldens.get(Integer.valueOf(num));
		if (tg != null) {
			b.setCost(tg.cost);
			b.setReward(tg.reward);
		}
		boolean free = vip.trainTimes > trainCount;
		if (free) {
			tg = (TouchGolden) trains.get(Integer.valueOf(1));
		} else {
			int index = trainCount - vip.trainTimes + 1;
			if (index >= trains.size()) {
				index = trains.size() - 1;
			}
			tg = (TouchGolden) trains.get(Integer.valueOf(index));
		}
		if (tg != null) {
			if (free)
				b.setTrainCost(0);
			else {
				b.setTrainCost(tg.cost);
			}
			b.setTrainReward(tg.reward);
		}
		int left = 0;
		if (vip.trainTimes - trainCount > 0) {
			left = vip.trainTimes - trainCount;
		}
		b.setTrainSuplus(left);
		player.send(1228, b.build());
	}

	private void touchGolden(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 29))) {
			return;
		}
		PbDown.TouchGoldenRst.Builder b = PbDown.TouchGoldenRst.newBuilder().setResult(true);
		int count = player.getRewardRecord().getTouchGoldenCount();
		Vip vip = player.getVip();
		if (count >= vip.touchGoldTimes) {
			VipService.notifyVipBuyTimeLack(player, PbCommons.VipBuyTimeType.TOUCHGOLD);
			return;
		}
		TouchGolden tg = (TouchGolden) touchGoldens.get(Integer.valueOf(count + 1));
		if (player.getJewels() < tg.cost) {
			b.setResult(false);
			b.setErrInfo("元宝不足");
		} else {
			player.getRewardRecord().touchGolden(player, tg);
			Platform.getEventManager().addEvent(new Event(2078, new Object[] { player }));
		}

		player.send(1230, b.build());
		if (b.getResult())
			touchGoldenInfo(player);
	}

	private void train(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 49))) {
			return;
		}
		PbDown.TrainRst.Builder b = PbDown.TrainRst.newBuilder().setResult(true);
		int count = player.getRewardRecord().getTrainCount();
		Vip vip = player.getVip();
		if (count >= 100) {
			b.setResult(false);
			b.setErrorInfo("达到今天最大训练次数，请明天再来");
		} else {
			boolean free = vip.trainTimes > count;
			TouchGolden tg = null;
			if (free) {
				tg = (TouchGolden) trains.get(Integer.valueOf(1));
			} else {
				int index = count - vip.trainTimes + 1;
				if (index >= trains.size()) {
					index = trains.size() - 1;
				}
				tg = (TouchGolden) trains.get(Integer.valueOf(index));
			}
			if ((player.getJewels() < tg.cost) && (!(free))) {
				b.setResult(false);
				b.setErrorInfo("元宝不足");
			} else {
				player.getRewardRecord().train(player, tg, free);
				Platform.getEventManager().addEvent(new Event(2097, new Object[] { player }));
			}
		}
		player.send(2210, b.build());
		if (b.getResult())
			touchGoldenInfo(player);
	}

	private void growRewardInfo(Player player) {
		PbDown.GrowRewardInfoRst.Builder b = PbDown.GrowRewardInfoRst.newBuilder();
		b.setResult(true);
		b.setIsBuy(player.getRewardRecord().isBuyGrowReward());
		for (GrowReward gr : growRewards.values()) {
			b.addRewards(gr.genGrowReward(player.getRewardRecord().isGetGrowReward(gr.level)));
		}
		player.send(1232, b.build());
	}

	private void growRewardBuy(Player player) {
		PbDown.GrowRewardBuyRst.Builder b = PbDown.GrowRewardBuyRst.newBuilder().setResult(true);
		if (player.getVip().level < 3) {
			b.setResult(false);
			b.setErrInfo("VIP等级不足");
		} else if (player.getRewardRecord().isBuyGrowReward()) {
			b.setResult(false);
			b.setErrInfo("你已购买过成长计划");
		} else if (player.getJewels() < 1000) {
			b.setResult(false);
			b.setErrInfo("元宝不足");
		} else {
			player.getRewardRecord().GrowRewardBuy(player);
			Platform.getEventManager().addEvent(new Event(2062, new Object[] { player }));
		}
		player.send(1234, b.build());
	}

	private void growRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.GrowRewardGetRst.Builder b = PbDown.GrowRewardGetRst.newBuilder().setResult(true);
		int level = 0;
		try {
			PbUp.GrowRewardGet req = PbUp.GrowRewardGet.parseFrom(packet.getData());
			level = req.getLevel();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1236, b.build());
			return;
		}

		if (!(growRewards.containsKey(Integer.valueOf(level)))) {
			b.setResult(false);
			b.setErrInfo("奖励不存在或已过期");
		} else if (!(player.getRewardRecord().isBuyGrowReward())) {
			b.setResult(false);
			b.setErrInfo("你尚未购买成长计划");
		} else if (player.getRewardRecord().isGetGrowReward(level)) {
			b.setResult(false);
			b.setErrInfo("已经领取过该奖励");
		} else if (player.getLevel() < level) {
			b.setResult(false);
			b.setErrInfo(MessageFormat.format("需主角等级达到{0}级才可操作", new Object[] { Integer.valueOf(level) }));
		} else {
			GrowReward gr = (GrowReward) growRewards.get(Integer.valueOf(level));
			player.getRewardRecord().growRewardGet(gr, player);
			Platform.getEventManager().addEvent(new Event(2063, new Object[] { player }));
		}
		player.send(1236, b.build());
	}

	private void drawMoneyInfo(Player player) {
		PbDown.DrawMoneyInfoRst.Builder b = PbDown.DrawMoneyInfoRst.newBuilder().setResult(true);
		if (!(player.getRewardRecord().isOpenDrawMoney(player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else if (!(player.getRewardRecord().hasMoreDrawMoney())) {
			b.setIsTop(true);
			b.setTime(player.getRewardRecord().getDrawMoneySurplusTime(player));
		} else {
			b.setTime(player.getRewardRecord().getDrawMoneySurplusTime(player));
			DrawMoney dm = (DrawMoney) drawMoneys
					.get(Integer.valueOf(player.getRewardRecord().getDrawMoneyCount() + 1));
			if (dm == null) {
				b.setIsTop(true);
			} else {
				b.setIsTop(false);
				b.setCost(dm.cost);
				b.setMaxGet(dm.getMaxGet());
			}
		}
		player.send(1246, b.build());
	}

	private void drawMoneyGet(Player player) {
		PbDown.DrawMoneyGetRst.Builder b = PbDown.DrawMoneyGetRst.newBuilder().setResult(true);
		if (!(player.getRewardRecord().isOpenDrawMoney(player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else if (!(player.getRewardRecord().hasMoreDrawMoney())) {
			b.setResult(false);
			b.setErrInfo("该活动已达上限");
		} else {
			DrawMoney dm = (DrawMoney) drawMoneys
					.get(Integer.valueOf(player.getRewardRecord().getDrawMoneyCount() + 1));
			if (player.getJewels() < dm.cost) {
				b.setResult(false);
				b.setErrInfo("元宝不足");
			} else {
				int addJewels = player.getRewardRecord().drawMoneyGet(player, dm);
				b.setReward(addJewels);
			}
		}
		player.send(1248, b.build());
	}

	private void onlineRewardInfo(Player player) {
		PbDown.OnlineRewardInfoRst.Builder b = PbDown.OnlineRewardInfoRst.newBuilder().setResult(true);
		int count = player.getRewardRecord().getOnlineRewardCount();
		if (count < 1) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			OnlineReward or = getOnlineReward(count);
			if (or == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				for (Reward r : or.rewards) {
					b.addRewards(r.genPbReward());
				}
			}
		}
		player.send(1326, b.build());
	}

	private void onlineRewardGet(Player player) {
		PbDown.OnlineRewardGetRst.Builder b = PbDown.OnlineRewardGetRst.newBuilder().setResult(true);
		int count = player.getRewardRecord().getOnlineRewardCount();
		if (count < 1) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			OnlineReward or = getOnlineReward(count);
			if (or == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (or.time + player.getRewardRecord().getLastGetOnlineReward() > System.currentTimeMillis()) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else {
				player.getRewardRecord().getOnlineReward(player, or);
				player.notifyGetItem(2, or.rewards);
				count = player.getRewardRecord().getOnlineRewardCount();
				if (count < 1) {
					b.setTime(-1L);
				} else {
					or = getOnlineReward(count);
					if (or != null) {
						long last = player.getRewardRecord().getLastGetOnlineReward();
						long now = System.currentTimeMillis();
						long surplus = last + or.time - now;
						b.setTime(surplus);
						for (Reward r : or.rewards)
							b.addRewards(r.genPbReward());
					} else {
						b.setTime(-1L);
					}
				}
			}
		}
		player.send(1328, b.build());
	}

	private void globalChargeRewardInfo(Player player) {
		PbDown.GlobalChargeRewardInfoRst.Builder b = PbDown.GlobalChargeRewardInfoRst.newBuilder().setResult(true);

		Set<?> rewards = player.getRewardRecord().getGlobalChargeRewards();
		Iterator<Integer> itx = globalChargeRewards.keySet().iterator();
		while (itx.hasNext()) {
			int num = ((Integer) itx.next()).intValue();
			List<Reward> list = (List<Reward>) globalChargeRewards.get(Integer.valueOf(num));
			PbActivity.GlobalChargeReward.Builder gb = PbActivity.GlobalChargeReward.newBuilder();
			gb.setNum(num);
			for (Reward r : list) {
				gb.addRewards(r.genPbReward());
			}
			gb.setCanGet(rewards.contains(Integer.valueOf(num)));
			b.addRewards(gb);
		}
		b.setNum(PayService.getChargePlayerCount());

		player.send(1332, b.build());
	}

	private void globalChargeRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.GlobalChargeRewardGetRst.Builder b = PbDown.GlobalChargeRewardGetRst.newBuilder().setResult(true);
		int num = 0;
		try {
			PbUp.GlobalChargeRewardGetReq req = PbUp.GlobalChargeRewardGetReq.parseFrom(packet.getData());
			num = req.getNum();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1334, b.build());
			return;
		}

		if (!(globalChargeRewards.containsKey(Integer.valueOf(num)))) {
			b.setResult(false);
			b.setErrInfo("奖励不存在或已过期");
		} else if (player.getRewardRecord().isGetGlobalChargeReward(num)) {
			b.setResult(false);
			b.setErrInfo("已经领取过该奖励");
		} else {
			int chargeNum = PayService.getChargePlayerCount();
			if (num > chargeNum) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else {
				List<Reward> list = player.getRewardRecord().getGlobalChargeReward(player, num);
				if ((list != null) && (list.size() > 0)) {
					player.notifyGetItem(2, list);
				}
			}
		}
		player.send(1334, b.build());
	}

	private void login7DayRewardInfo(Player player) {
		PbDown.Login7DayRewardInfoRst.Builder b = PbDown.Login7DayRewardInfoRst.newBuilder().setResult(true);
		RewardRecord rr = player.getRewardRecord();
		if (rr.getLogin7DayGet().size() >= 7) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			int loginDay = rr.getLogin7DayCount();
			for (Iterator<Integer> localIterator1 = login7DayRewards.keySet().iterator(); localIterator1.hasNext();) {
				int day = ((Integer) localIterator1.next()).intValue();
				List<Reward> list = (List<Reward>) login7DayRewards.get(Integer.valueOf(day));
				PbCommons.Login7DayReward.Builder rb = PbCommons.Login7DayReward.newBuilder();
				rb.setDay(day);
				for (Reward r : list) {
					rb.addRewards(r.genPbReward());
				}
				if (rr.getLogin7DayGet().contains(Integer.valueOf(day)))
					rb.setGet(2);
				else if (loginDay >= day)
					rb.setGet(1);
				else {
					rb.setGet(0);
				}
				b.addRewards(rb);
			}
			b.setToday(rr.getLogin7DayCount());

			b.addAllHeros(LoginReward.getWarrior7());
			if (loginDay > 6)
				b.setHeroGet(true);
			else {
				b.setHeroGet(false);
			}
		}

		player.send(1362, b.build());
	}

	private void login7DayRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.Login7DayRewardGetRst.Builder builder = PbDown.Login7DayRewardGetRst.newBuilder().setResult(true);
		int day = 0;
		try {
			PbUp.Login7DayRewardGetReq req = PbUp.Login7DayRewardGetReq.parseFrom(packet.getData());
			day = req.getDay();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1364, builder.build());
			return;
		}
		RewardRecord rr = player.getRewardRecord();
		if (!(login7DayRewards.containsKey(Integer.valueOf(day)))) {
			builder.setResult(false);
			builder.setErrInfo("奖励不存在或已过期");
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else {
			List<Reward> list = (List<Reward>) login7DayRewards.get(Integer.valueOf(day));
			if (rr.getLogin7DayCount() < day) {
				builder.setResult(false);
				builder.setErrInfo("你不能领取该奖励");
			} else if (rr.getLogin7DayGet().contains(Integer.valueOf(day))) {
				builder.setResult(false);
				builder.setErrInfo("已经领取过该奖励");
			} else {
				for (Reward r : list) {
					r.add(player, "login7dayreward");
				}
				rr.getLogin7DayGet().add(Integer.valueOf(day));
				player.notifyGetItem(2, list);
				Platform.getEventManager().addEvent(new Event(2103, new Object[] { player }));
			}
		}
		player.send(1364, builder.build());
	}

	public static CookWine getCookWine(int id) {
		return ((CookWine) cookWines.get(Integer.valueOf(id)));
	}

	public static CookWine getCurCookwine() {
		Calendar now = Calendar.getInstance();
		int hour = now.get(11);
		Set<Entry<Integer, CookWine>> set = cookWines.entrySet();
		for (Map.Entry entry : set) {
			int id = ((Integer) entry.getKey()).intValue();
			CookWine cw = (CookWine) entry.getValue();
			if ((hour >= cw.getStart()) && (hour < cw.getEnd())) {
				return cw;
			}
		}
		return null;
	}

	public static SignReward getSignReward(int signCount) {
		if (signCount > signRewards.size()) {
			return ((SignReward) signRewards.get(Integer.valueOf(signRewards.size())));
		}
		return ((SignReward) signRewards.get(Integer.valueOf(signCount)));
	}

	public static OnlineReward getOnlineReward(int count) {
		return ((OnlineReward) onlineRewards.get(Integer.valueOf(count)));
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	private void superRewardGet(Player player) {
		PbDown.SuperRewardGetRst.Builder builder = PbDown.SuperRewardGetRst.newBuilder().setResult(true);
		RewardRecord rr = player.getRewardRecord();
		if (rr.isAllReceivedSuperReward()) {
			builder.setResult(false);
			builder.setErrInfo("已经领取过该奖励");
		} else if (!(rr.isParticipatedSuperRewardActivity())) {
			builder.setResult(false);
			builder.setErrInfo("尚未充值");
		} else {
			player.notifyGetItem(2, rr.receiveSuperReward(player));
		}
		player.send(3004, builder.build());
	}

	private void superRewardInfo(Player player, PbPacket.Packet packet) {
		PbDown.SuperRewardInfoRst.Builder b = PbDown.SuperRewardInfoRst.newBuilder().setResult(true);
		RewardRecord rr = player.getRewardRecord();

		if (rr.isAllReceivedSuperReward()) {
			b.setResult(false);
			b.setErrInfo("已经领取过该奖励");
			player.send(3002, b.build());
		}

		String channel = "";
		try {
			PbUp.SuperRewardInfo get = PbUp.SuperRewardInfo.parseFrom(packet.getData());
			channel = get.getPayChannel();
		} catch (InvalidProtocolBufferException e) {
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(3002, b.build());
			return;
		}

		for (Integer goodId : superReward.keySet()) {
			if (superReward.containsKey(goodId)) {
				PbActivity.SuperReward.Builder builder = PbActivity.SuperReward.newBuilder();
				builder.setRewardId(goodId.intValue());
				builder.setCoGoodsId(((PayItem) ((HashMap<?, ?>) PayService.pays.get(channel)).get(goodId)).coGoodsId);
				builder.setCanGet(rr.canGetSuperReward(goodId.intValue()));
				for (Reward r : (ArrayList<Reward>) superReward.get(goodId)) {
					builder.addRewards(r.genPbReward());
				}
				b.addRewards(builder.build());
			}
		}
		player.send(3002, b.build());
	}
}
