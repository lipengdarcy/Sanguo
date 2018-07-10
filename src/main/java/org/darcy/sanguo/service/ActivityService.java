package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityData;
import org.darcy.sanguo.activity.ActivityExchange;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.ActivityRecord;
import org.darcy.sanguo.activity.Rank7Reward;
import org.darcy.sanguo.activity.item.ActivityItem;
import org.darcy.sanguo.activity.item.ChargeRewardAI;
import org.darcy.sanguo.activity.item.CostRewardAI;
import org.darcy.sanguo.activity.item.DayChargeAI;
import org.darcy.sanguo.activity.item.ExchangeAI;
import org.darcy.sanguo.activity.item.LoginAwardAI;
import org.darcy.sanguo.activity.item.MapDropAI;
import org.darcy.sanguo.activity.item.PersistChargeAI;
import org.darcy.sanguo.activity.item.PrayAI;
import org.darcy.sanguo.activity.item.RecruitDropAI;
import org.darcy.sanguo.activity.item.SpecialMapAI;
import org.darcy.sanguo.activity.item.TurnPlateAI;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Fall;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.pay.MonthCard;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.reward.RewardRecord;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.top.Top;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.Vip;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import sango.packet.PbActivity;
import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class ActivityService implements Service, PacketHandler, EventHandler {
	public static int[] activityList;
	public static int[] saleList;
	public static final String KEY_RANK7 = "KEY_RANK7";
	public static final String KEY_RANK7_CMREWARD_CANCEL = "KEY_RANK7_CMREWARD_CANCEL";
	private final SimpleDateFormat firstChargeTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Map<Integer, MonthCard> monthCards = new HashMap();

	public static Map<Integer, List<Reward>[]> day30Rewards = new HashMap();

	public static Map<Integer, List<Reward>[]> day7Rewards = new HashMap();

	public static List<Rank7Reward> rank7Rewards = new ArrayList();

	public static List<Long> firstGiveResetTime = new ArrayList();

	public static List<Long> firstPackageResetTime = new ArrayList();

	public void startup() throws Exception {
		loadActivityList();
		loadActivity();
		checkActivity();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		new Crontab("* 30 0", 1004);
	}

	public int[] getCodes() {
		return new int[] { 1167, 1237, 1239, 1241, 1243, 1263, 1265, 1267, 1269, 1275, 1277, 1279, 1281, 1283, 1287,
				1289, 1315, 1317, 1319, 1321, 1329, 2215, 2213, 1337, 1379, 1377 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1167:
			activityList(player);
			break;
		case 1237:
			activityExchangeInfo(player);
			break;
		case 1239:
			activityExchange(player, packet);
			break;
		case 1241:
			loginAwardInfo(player);
			break;
		case 1243:
			loginAwardGet(player, packet);
			break;
		case 1263:
			chargeRewardInfo(player);
			break;
		case 1265:
			chargeRewardGet(player, packet);
			break;
		case 1267:
			costRewardInfo(player);
			break;
		case 1269:
			costRewardGet(player, packet);
			break;
		case 1275:
			turnPlateInfo(player);
			break;
		case 1277:
			turnPlateGetScoreReward(player, packet);
			break;
		case 1279:
			turnPlatePlay(player, packet);
			break;
		case 1281:
			prayInfo(player);
			break;
		case 1283:
			pray(player);
			break;
		case 1287:
			lifeMemberInfo(player);
			break;
		case 1289:
			lifeMemberGet(player);
			break;
		case 1315:
			dayChargeRewardInfo(player);
			break;
		case 1317:
			dayChargeRewardGet(player, packet);
			break;
		case 1319:
			dayRewardInfo(player, packet);
			break;
		case 1321:
			dayRewardGet(player, packet);
			break;
		case 1329:
			monthCardInfo(player, packet);
			break;
		case 2215:
			rank7GetReward(player);
			break;
		case 2213:
			rank7Info(player);
			break;
		case 1337:
			saleList(player);
			break;
		case 1379:
			persistChargeGet(player, packet);
			break;
		case 1377:
			persistChargeInfo(player);
		}
	}

	private void loadActivityList() {
		activityList = new int[] { 30, 5, 34, 25, 32, 29, 53, 43, 31, 35, 33, 42 };
		saleList = new int[] { 36, 54, 55, 51, 52, 47, 46, 37, 38, 61 };
	}

	private void loadActivity() {

		int roundId;
		int count;
		String rewardStr;
		String[] rewardArray;
		ActivityItem ai;
		String str;
		int i, j, len, pos, id;
		ActivityExchange ae;
		String[] array;
		String[] arrayOfString4;
		Object localObject2;
		List<Row> list = ExcelUtils.getRowList("activity.xls", 2);
		for (Row row : list) {

			pos = 0;

			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			int activityId = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String starStr = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String endStr = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String content = row.getCell(pos++).getStringCellValue();

			ActivityData ad = new ActivityData();
			ad.activityId = activityId;
			ad.roundId = roundId;
			ad.content = content;
			try {
				ad.start = ActivityInfo.sdf.parse(starStr).getTime();
				ad.end = ActivityInfo.sdf.parse(endStr).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (ad.roundId == 0) {
				int start = (int) row.getCell(pos++).getNumericCellValue();
				int end = (int) row.getCell(pos++).getNumericCellValue();
				ad.firstWeekStart = start;
				ad.firstWeekEnd = end;
			}

			ActivityInfo.addData(ad);
		}

		Map<Integer, ActivityItem> exchangeAIMap = new HashMap();
		List<Row> list2 = ExcelUtils.getRowList("activity.xls", 2, 1);
		for (Row row : list2) {

			String[] arrayOfString2;
			pos = 0;

			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (ExchangeAI) exchangeAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new ExchangeAI();
				exchangeAIMap.put(Integer.valueOf(roundId), ai);
			}

			id = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();
			int online = (int) row.getCell(pos++).getNumericCellValue();
			int countType = (int) row.getCell(pos++).getNumericCellValue();
			count = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String costStr = row.getCell(pos++).getStringCellValue();

			ae = new ActivityExchange();
			ae.id = id;
			ae.reward = new Reward(rewardStr);
			ae.online = (online == 1);
			ae.countType = countType;
			ae.count = count;
			array = costStr.split(",");
			for (int k = 0; k < array.length; ++k) {
				str = array[k];
				ae.costs.add(new Reward(str));
			}
			((ExchangeAI) ai).addExchange(id, ae);
		}
		ActivityInfo.addItem(exchangeAIMap);

		Map chargeAIMap = new HashMap();
		List<Row> list3 = ExcelUtils.getRowList("activity.xls", 2, 2);
		for (Row row : list3) {

			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (ChargeRewardAI) chargeAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new ChargeRewardAI();
				chargeAIMap.put(Integer.valueOf(roundId), ai);
			}
			count = (int) row.getCell(pos++).getNumericCellValue();
			rewardStr = row.getCell(pos++).getStringCellValue();

			List list_tmp = new ArrayList();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (j = 0; j < rewardArray.length; ++j) {
					str = rewardArray[j];
					list_tmp.add(new Reward(str));
				}
			}
			((ChargeRewardAI) ai).addRewards(count, list_tmp);
		}
		ActivityInfo.addItem(chargeAIMap);

		Map costChargeAIMap = new HashMap();
		List<Row> list4 = ExcelUtils.getRowList("activity.xls", 2, 3);
		for (Row row : list4) {

			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			count = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			List list_tmp = new ArrayList();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				for (j = 0; j < rewardArray.length; ++j) {
					str = rewardArray[j];
					list_tmp.add(new Reward(str));
				}
			}
			ai = (CostRewardAI) costChargeAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new CostRewardAI();
				costChargeAIMap.put(Integer.valueOf(roundId), ai);
			}
			((CostRewardAI) ai).addRewards(count, list_tmp);
		}
		ActivityInfo.addItem(costChargeAIMap);

		// 登录奖励
		Map loginAwardAIMap = new HashMap();
		List<Row> list5 = ExcelUtils.getRowList("activity.xls", 2, 4);
		for (Row row : list5) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (LoginAwardAI) loginAwardAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new LoginAwardAI();
				loginAwardAIMap.put(Integer.valueOf(roundId), ai);
			}

			int day = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			List list1 = new ArrayList();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				String[] arrayOfString1;
				rewardArray = rewardStr.split(",");
				for (j = 0; j < rewardArray.length; ++j) {
					str = rewardArray[j];
					list1.add(new Reward(str));
				}
			}
			((LoginAwardAI) ai).addRewards(day, list1);
		}
		ActivityInfo.addItem(loginAwardAIMap);

		Map mapDropAIMap = new HashMap();
		List<Row> list6 = ExcelUtils.getRowList("activity.xls", 2, 5);
		for (Row row : list6) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			int drop = (int) row.getCell(pos++).getNumericCellValue();

			ai = (MapDropAI) mapDropAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new MapDropAI();
				((MapDropAI) ai).dropId = drop;
				mapDropAIMap.put(Integer.valueOf(roundId), ai);
			}
		}
		ActivityInfo.addItem(mapDropAIMap);

		// 7.转盘活动
		Map turnPlateAIMap = new HashMap();
		List<Row> list7 = ExcelUtils.getRowList("activity.xls", 2, 6);
		for (Row row : list7) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (TurnPlateAI) turnPlateAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new TurnPlateAI();
				turnPlateAIMap.put(Integer.valueOf(roundId), ai);
			}

			id = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(0);
			Integer key = Integer.valueOf((int) row.getCell(pos++).getNumericCellValue());
			Map.Entry entry = new AbstractMap.SimpleEntry(key, new Reward(rewardStr));
			((TurnPlateAI) ai).rewards.put(Integer.valueOf(id), entry);

			for (j = 1; j <= 10; ++j) {
				count = (int) row.getCell(pos++).getNumericCellValue();
				if (!(((TurnPlateAI) ai).counts.containsKey(Integer.valueOf(j)))) {
					((TurnPlateAI) ai).counts.put(Integer.valueOf(j), new HashMap());
				}
				((Map) ((TurnPlateAI) ai).counts.get(Integer.valueOf(j))).put(Integer.valueOf(id),
						Integer.valueOf(count));
			}

		}

		List<Row> list8 = ExcelUtils.getRowList("activity.xls", 2, 7);
		for (Row row : list8) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (TurnPlateAI) turnPlateAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				throw new RuntimeException("turnplate score data has error roundId:" + roundId);
			}

			int score = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos);
			rewardStr = row.getCell(pos++).getStringCellValue();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				List list1 = new ArrayList();
				rewardArray = rewardStr.split(",");
				for (String str1 : rewardArray) {
					list1.add(new Reward(str1));
				}
				((TurnPlateAI) ai).scoreRewards.put(Integer.valueOf(score), list1);
			}
		}
		ActivityInfo.addItem(turnPlateAIMap);

		Map prayAIMap = new HashMap();
		List<Row> list9 = ExcelUtils.getRowList("activity.xls", 2, 8);
		for (Row row : list9) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (PrayAI) prayAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new PrayAI();
				prayAIMap.put(Integer.valueOf(roundId), ai);
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int[] goals = new int[10];
			for (j = 0; j < 10; ++j) {
				goals[j] = (int) row.getCell(pos++).getNumericCellValue();
			}
			int drop = (int) row.getCell(pos++).getNumericCellValue();
			((PrayAI) ai).drop = drop;
			((PrayAI) ai).goals.put(Integer.valueOf(id), goals);
		}
		ActivityInfo.addItem(prayAIMap);

		Map recruitDropAIMap = new HashMap();
		List<Row> list10 = ExcelUtils.getRowList("activity.xls", 2, 9);
		for (Row row : list10) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (RecruitDropAI) recruitDropAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new RecruitDropAI();
				recruitDropAIMap.put(Integer.valueOf(roundId), ai);
			}

			id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int isEffect = (int) row.getCell(pos++).getNumericCellValue();
			int drop = (int) row.getCell(pos++).getNumericCellValue();
			((RecruitDropAI) ai).addData(id, (isEffect == 0) ? false : true, drop);
		}
		ActivityInfo.addItem(recruitDropAIMap);

		List<Row> list11 = ExcelUtils.getRowList("activity.xls", 2, 10);
		for (Row row : list11) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int get = (int) row.getCell(pos++).getNumericCellValue();
			int last = (int) row.getCell(pos++).getNumericCellValue();

			MonthCard mc = new MonthCard();
			mc.id = id;
			mc.reward = get;
			mc.last = last;
			monthCards.put(Integer.valueOf(id), mc);
		}

		Map dayChargeAIMap = new HashMap();
		List<Row> list12 = ExcelUtils.getRowList("activity.xls", 2, 11);
		for (Row row : list12) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (DayChargeAI) dayChargeAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new DayChargeAI();
				dayChargeAIMap.put(Integer.valueOf(roundId), ai);
			}

			count = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			list = new ArrayList();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				len = rewardArray.length;
				for (j = 0; j < len; ++j) {
					str = rewardArray[j];
					((List) list).add(new Reward(str));
				}
			}
			((DayChargeAI) ai).addRewards(count, (List) list);
		}
		ActivityInfo.addItem(dayChargeAIMap);

		List<Row> list13 = ExcelUtils.getRowList("activity.xls", 2, 12);
		for (Row row : list13) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			int type = (int) row.getCell(pos++).getNumericCellValue();
			int day = 0;
			try {
				day = (int) row.getCell(pos).getNumericCellValue();
			} catch (Exception e) {
				day = Integer.valueOf(row.getCell(pos).getStringCellValue());
			}
			pos++;

			rewardStr = row.getCell(pos++).getStringCellValue();
			String superRewardStr = row.getCell(pos++).getStringCellValue();

			List list_tmp = new ArrayList();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				rewardArray = rewardStr.split(",");
				len = rewardArray.length;
				for (j = 0; j < len; ++j) {
					str = rewardArray[j];
					((List) list_tmp).add(new Reward(str));
				}
			}

			List superReward = new ArrayList();
			if ((superRewardStr != null) && (!(superRewardStr.equals("-1")))) {
				rewardArray = superRewardStr.split(",");
				len = rewardArray.length;
				for (j = 0; j < len; ++j) {
					str = rewardArray[j];
					((List) superReward).add(new Reward(str));
				}
			}

			List<Reward>[] rewards = new ArrayList[2];
			rewards[0] = list_tmp;
			rewards[1] = superReward;
			if (type == 1)
				day30Rewards.put(Integer.valueOf(day), rewards);
			else {
				day7Rewards.put(Integer.valueOf(day), rewards);
			}
		}

		List<Row> list14 = ExcelUtils.getRowList("activity.xls", 2, 13);
		for (Row row : list14) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}

			id = (int) row.getCell(pos++).getNumericCellValue();
			int startRank = (int) row.getCell(pos++).getNumericCellValue();
			int endRank = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String superRewardStr = row.getCell(pos++).getStringCellValue();

			list = new ArrayList();
			if ((superRewardStr != null) && (!(superRewardStr.equals("-1")))) {
				Object localObject1;
				String[] superReward = superRewardStr.split(",");
				len = superReward.length;
				for (j = 0; j < len; ++j) {
					String rewards = superReward[j];
					((List) list).add(new Reward((String) rewards));
				}
			}

			Rank7Reward r1 = new Rank7Reward();
			r1.setId(id);
			r1.setEndRank(endRank);
			r1.setStartRank(startRank);
			r1.setRewards((List) list);

			rank7Rewards.add(r1);
		}

		Map persistChargeAIMap = new HashMap();
		List<Row> list15 = ExcelUtils.getRowList("activity.xls", 2, 14);
		for (Row row : list15) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (PersistChargeAI) persistChargeAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new PersistChargeAI();
				persistChargeAIMap.put(Integer.valueOf(roundId), ai);
			}

			int day = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			rewardStr = row.getCell(pos++).getStringCellValue();

			list = new ArrayList();
			if ((rewardStr != null) && (!(rewardStr.equals("-1")))) {
				String[] rewards = rewardStr.split(",");
				for (String str4 : rewards) {
					((List) list).add(new Reward(str4));
				}
			}

			((PersistChargeAI) ai).addRewards(day, (List) list);
		}
		ActivityInfo.addItem(persistChargeAIMap);

		Map specialAIMap = new HashMap();
		List<Row> list16 = ExcelUtils.getRowList("activity.xls", 2, 15);
		for (Row row : list16) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			roundId = (int) row.getCell(pos++).getNumericCellValue();
			ai = (SpecialMapAI) specialAIMap.get(Integer.valueOf(roundId));
			if (ai == null) {
				ai = new SpecialMapAI();
				specialAIMap.put(Integer.valueOf(roundId), ai);
			}

			int type = (int) row.getCell(pos++).getNumericCellValue();
			count = (int) row.getCell(pos++).getNumericCellValue();

			((SpecialMapAI) ai).count = count;
			((SpecialMapAI) ai).type = type;
		}
		ActivityInfo.addItem(specialAIMap);

		List<Row> list17 = ExcelUtils.getRowList("activity.xls", 2, 16);
		for (Row row : list17) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			row.getCell(pos).setCellType(1);
			String timeStr = row.getCell(pos++).getStringCellValue();
			long time = 0;
			try {
				time = this.firstChargeTimeFormat.parse(timeStr).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			row.getCell(pos).setCellType(0);
			int i1 = ((int) row.getCell(pos++).getNumericCellValue() == 1) ? 1 : 0;
			if (i1 != 0) {
				firstGiveResetTime.add(Long.valueOf(time));
			}

			row.getCell(pos).setCellType(0);
			int i2 = ((int) row.getCell(pos++).getNumericCellValue() == 1) ? 1 : 0;
			if (i2 != 0)
				firstPackageResetTime.add(Long.valueOf(time));
		}
	}

	private void activityList(Player player) {
		PbDown.ActivityListRst.Builder builder = PbDown.ActivityListRst.newBuilder().setResult(true);
		for (int id : activityList) {
			PbCommons.ActivityListInfo info = Function.genActivityListInfo(id, player);
			if (info != null) {
				builder.addInfos(info);
			}
		}
		player.send(1168, builder.build());
	}

	private void saleList(Player player) {
		PbDown.SaleListRst.Builder builder = PbDown.SaleListRst.newBuilder().setResult(true);
		for (int id : saleList) {
			PbCommons.ActivityListInfo info = Function.genActivityListInfo(id, player);
			if (info != null) {
				builder.addInfos(info);
			}
		}
		player.send(1338, builder.build());
	}

	private void activityExchangeInfo(Player player) {
		PbDown.ActivityExchangeInfoRst.Builder b = PbDown.ActivityExchangeInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(1, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			ExchangeAI ai = (ExchangeAI) ActivityInfo.getItem(player, 1);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				Map<Integer, ActivityExchange> map = ai.exchanges;
				if ((map != null) && (map.size() > 0)) {
					for (ActivityExchange ae : map.values()) {
						if (ae.online) {
							if (!(player.getActivityRecord().getExchanges().containsKey(Integer.valueOf(ae.id)))) {
								player.getActivityRecord().updateExchanges(ae.id, 0);
							}
							b.addItems(ae.genActivityExchangeData(player.getActivityRecord().getCount(ae.id)));
						}
					}
				}
				b.setTime(ActivityInfo.getEndTime(1) - System.currentTimeMillis());
			}
		}
		player.send(1238, b.build());
	}

	private void activityExchange(Player player, PbPacket.Packet packet) {
		PbDown.ActivityExchangeRst.Builder b = PbDown.ActivityExchangeRst.newBuilder().setResult(true);
		int id = 0;
		int count = 0;
		try {
			PbUp.ActivityExchange req = PbUp.ActivityExchange.parseFrom(packet.getData());
			id = req.getId();
			count = req.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1240, b.build());
			return;
		}
		if (!(ActivityInfo.isOpenActivity(1, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			ExchangeAI ai = (ExchangeAI) ActivityInfo.getItem(player, 1);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				ActivityExchange ae = (ActivityExchange) ai.exchanges.get(Integer.valueOf(id));
				if ((ae == null) || (!(ae.online))) {
					b.setResult(false);
					b.setErrInfo("兑换数据不存在");
				} else if (count < 1) {
					b.setResult(false);
					b.setErrInfo("兑换次数有误");
				} else {
					if (!(player.getActivityRecord().getExchanges().containsKey(Integer.valueOf(id)))) {
						player.getActivityRecord().updateExchanges(id, 0);
					}
					int surplus = player.getActivityRecord().getSurplusCount(ae);
					if (surplus < count) {
						b.setResult(false);
						b.setErrInfo("兑换次数不足，无法兑换");
					} else {
						List list = ae.getCost(count);
						Map costMap = player.getBags().getExcludeItemIds(list);
						Iterator itx = costMap.keySet().iterator();
						while (itx.hasNext()) {
							Reward r = (Reward) itx.next();
							List excludeIds = (List) costMap.get(r);
							String str = null;
							if ((excludeIds != null) && (excludeIds.size() > 0))
								str = r.check(player, excludeIds);
							else {
								str = r.check(player);
							}
							if (str != null) {
								b.setResult(false);
								b.setErrInfo("材料不足");
								player.send(1240, b.build());
								return;
							}
						}
						Reward reward = ae.reward.copy();
						reward.count *= count;
						reward.add(player, "activityexchange");
						itx = costMap.keySet().iterator();
						while (itx.hasNext()) {
							Reward r = (Reward) itx.next();
							List excludeIds = (List) costMap.get(r);
							if ((excludeIds != null) && (excludeIds.size() > 0))
								r.remove(player, "activityexchange", excludeIds);
							else {
								r.remove(player, "activityexchange");
							}
						}
						player.getActivityRecord().updateExchanges(id, player.getActivityRecord().getCount(id) + count);
						player.notifyGetItem(2, new Reward[] { reward });
					}
				}
			}
		}
		player.send(1240, b.build());
	}

	private void loginAwardInfo(Player player) {
		PbDown.LoginAwardInfoRst.Builder b = PbDown.LoginAwardInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(2, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			LoginAwardAI ai = (LoginAwardAI) ActivityInfo.getItem(player, 2);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				Map infos = player.getRewardRecord().getLoginAwardInfos();
				Map loginAwards = ai.rewards;
				if (loginAwards != null) {
					Set<Map.Entry> set = loginAwards.entrySet();
					for (Map.Entry entry : set) {
						int day = ((Integer) entry.getKey()).intValue();
						List<Reward> list = (List) entry.getValue();

						Boolean get = null;
						if (infos.containsKey(Integer.valueOf(day))) {
							if (!(((Boolean) infos.get(Integer.valueOf(day))).booleanValue()))
								get = Boolean.valueOf(true);
						} else {
							get = Boolean.valueOf(false);
						}
						if (get != null) {
							PbCommons.LoginAward.Builder builder = PbCommons.LoginAward.newBuilder();
							builder.setDay(day);
							builder.setCanGet(get.booleanValue());
							for (Reward reward : list) {
								builder.addRewards(reward.genPbReward());
							}
							b.addAwards(builder.build());
						}
					}
				}
				b.setContent(ActivityInfo.getContent(2));
				b.setStartStr(ActivityInfo.getStartTimeStr(2));
				b.setEndStr(ActivityInfo.getEndTimeStr(2));
			}
		}
		player.send(1242, b.build());
	}

	private void loginAwardGet(Player player, PbPacket.Packet packet) {
		PbDown.LoginAwardGetRst.Builder b = PbDown.LoginAwardGetRst.newBuilder().setResult(true);
		int day = 0;
		try {
			PbUp.LoginAwardGet req = PbUp.LoginAwardGet.parseFrom(packet.getData());
			day = req.getDay();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1244, b.build());
			return;
		}
		RewardRecord rr = player.getRewardRecord();
		if (!(ActivityInfo.isOpenActivity(2, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			LoginAwardAI ai = (LoginAwardAI) ActivityInfo.getItem(player, 2);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (!(ai.containsKey(day))) {
				b.setResult(false);
				b.setErrInfo("奖励不存在或已过期");
			} else if (player.getBags().getFullBag() != -1) {
				b.setResult(false);
				b.setErrInfo("背包已满，请先清理背包");
			} else if (!(rr.isLoginForAward(day))) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else if (rr.isGetLoginAward(day)) {
				b.setResult(false);
				b.setErrInfo("已经领取过该奖励");
			} else {
				List list = (List) ai.rewards.get(Integer.valueOf(day));
				rr.getLoginAward(player, day, list);
				b.setResult(true);
				player.notifyGetItem(2, list);
				Platform.getEventManager().addEvent(new Event(2064, new Object[] { player }));
			}
		}

		player.send(1244, b.build());
	}

	public static void startRank7Activity() {
		Calendar cal = Calendar.getInstance();
		cal.add(6, 7);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		long time = cal.getTimeInMillis();
		Cache cache = Platform.getEntityManager().getEhCache("common");
		cache.put(new Element("KEY_RANK7", Long.valueOf(time)));

		Platform.getEntityManager().putInEhCache("persist", "KEY_RANK7_CMREWARD_CANCEL", Boolean.TRUE);
	}

	public static boolean rank7CMRewardCanceled() {
		Object obj = Platform.getEntityManager().getFromEhCache("persist", "KEY_RANK7_CMREWARD_CANCEL");

		return (obj == null);
	}

	public static long getRank7ActivityLeftTime() {
		return getOpenServer7DayLeftTimeByTime(System.currentTimeMillis());
	}

	public boolean hasGetRank7Reward(Player player) {
		long record = player.getPool().getLong(23, -1L);

		return (record == -1L);
	}

	private void rank7GetReward(Player player) {
		PbDown.Rank7RewardRst.Builder rst = PbDown.Rank7RewardRst.newBuilder();
		rst.setResult(false);
		if ((getRank7ActivityLeftTime() != -1L) || (rank7CMRewardCanceled())) {
			rst.setErrInfo("活动尚未结束，不能领奖");
		} else if (hasGetRank7Reward(player)) {
			rst.setErrInfo("已经领取过该奖励");
		} else {
			List<Reward> list = ((Rank7Reward) rank7Rewards.get(0)).getRewards();
			for (Reward r : list) {
				r.add(player, "rank7dayreward");
			}

			rst.setResult(true);
			player.getPool().set(23, Long.valueOf(1234567890L));
			player.notifyGetItem(2, list);
			Platform.getEventManager().addEvent(new Event(2102, new Object[] { player }));
		}

		player.send(2216, rst.build());
	}

	private void rank7Info(Player player) {
		PbDown.Rank7InfoRst.Builder rst = PbDown.Rank7InfoRst.newBuilder();
		rst.setResult(false);
		long leftTime = getRank7ActivityLeftTime();
		boolean cancel = rank7CMRewardCanceled();
		if ((leftTime != -1L) || (!(hasGetRank7Reward(player)))) {
			rst.setResult(true);
			rst.setLeftTime((int) leftTime);
			rst.setMyRank(Platform.getTopManager().getRank(2, player.getId()));
			rst.setIsGetReward(hasGetRank7Reward(player));
			rst.setNoCommonReward(cancel);
			for (Rank7Reward r : rank7Rewards) {
				if ((cancel) && (r.getStartRank() == 0) && (r.getEndRank() == 0)) {
					continue;
				}
				PbCommons.RankReward.Builder rb = PbCommons.RankReward.newBuilder();
				rb.setStartRank(r.getStartRank()).setEndRank(r.getEndRank());

				for (Reward rd : r.getRewards()) {
					rb.addRewards(rd.genPbReward());
				}

				rst.addRewards(rb);
			}
		} else {
			rst.setErrInfo("活动已经结束");
		}

		player.send(2214, rst.build());
	}

	private void chargeRewardInfo(Player player) {
		PbDown.ChargeRewardInfoRst.Builder b = PbDown.ChargeRewardInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(3, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			ChargeRewardAI ai = (ChargeRewardAI) ActivityInfo.getItem(player, 3);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				Set getSet = player.getActivityRecord().getChargeRewards();
				Map chargeRewards = ai.rewards;
				if (chargeRewards != null) {
					Set<Map.Entry> set = chargeRewards.entrySet();
					for (Map.Entry entry : set) {
						int count = ((Integer) entry.getKey()).intValue();
						if (getSet.contains(Integer.valueOf(count)))
							continue;
						List<Reward> list = (List) ai.rewards.get(Integer.valueOf(count));
						PbCommons.ChargeReward.Builder cb = PbCommons.ChargeReward.newBuilder();
						cb.setCount(count);
						for (Reward reward : list) {
							cb.addRewards(reward.genPbReward());
						}
						b.addRewards(cb.build());
					}
				}

				b.setContent(ActivityInfo.getContent(3));
				b.setStartStr(ActivityInfo.getStartTimeStr(3));
				b.setEndStr(ActivityInfo.getEndTimeStr(3));
				b.setCurCount(player.getActivityRecord().getCharge());
			}
		}
		player.send(1264, b.build());
	}

	private void chargeRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.ChargeRewardGetRst.Builder b = PbDown.ChargeRewardGetRst.newBuilder().setResult(true);
		int count = 0;
		try {
			PbUp.ChargeRewardGet req = PbUp.ChargeRewardGet.parseFrom(packet.getData());
			count = req.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1266, b.build());
			return;
		}
		if (!(ActivityInfo.isOpenActivity(3, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			ChargeRewardAI ai = (ChargeRewardAI) ActivityInfo.getItem(player, 3);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (!(ai.containsKey(count))) {
				b.setResult(false);
				b.setErrInfo("奖励不存在或已过期");
			} else if (player.getActivityRecord().getChargeRewards().contains(Integer.valueOf(count))) {
				b.setResult(false);
				b.setErrInfo("已经领取过该奖励");
			} else if (player.getActivityRecord().getCharge() < count) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else {
				List list = (List) ai.rewards.get(Integer.valueOf(count));
				player.getActivityRecord().chargeRewardGet(player, count, list);
				player.notifyGetItem(2, list);
			}
		}
		player.send(1266, b.build());
	}

	private void costRewardInfo(Player player) {
		PbDown.CostRewardInfoRst.Builder b = PbDown.CostRewardInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(4, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			Set getSet = player.getActivityRecord().getCostRewards();
			CostRewardAI ai = (CostRewardAI) ActivityInfo.getItem(player, 4);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				Map costRewards = ai.rewards;
				Set<Map.Entry> set = costRewards.entrySet();
				for (Map.Entry entry : set) {
					int count = ((Integer) entry.getKey()).intValue();
					if (getSet.contains(Integer.valueOf(count)))
						continue;
					List<Reward> list = (List) costRewards.get(Integer.valueOf(count));
					if (list != null) {
						PbCommons.CostReward.Builder rb = PbCommons.CostReward.newBuilder();
						rb.setCount(count);
						for (Reward reward : list) {
							rb.addRewards(reward.genPbReward());
						}
						b.addRewards(rb.build());
					}
				}

				b.setContent(ActivityInfo.getContent(4));
				b.setStartStr(ActivityInfo.getStartTimeStr(4));
				b.setEndStr(ActivityInfo.getEndTimeStr(4));
				b.setCurCount(player.getActivityRecord().getCost());
			}
		}
		player.send(1268, b.build());
	}

	private void costRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.CostRewardGetRst.Builder b = PbDown.CostRewardGetRst.newBuilder().setResult(true);
		int count = 0;
		try {
			PbUp.CostRewardGet req = PbUp.CostRewardGet.parseFrom(packet.getData());
			count = req.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1270, b.build());
			return;
		}
		if (!(ActivityInfo.isOpenActivity(4, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			CostRewardAI ai = (CostRewardAI) ActivityInfo.getItem(player, 4);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (!(ai.containsKey(count))) {
				b.setResult(false);
				b.setErrInfo("奖励不存在或已过期");
			} else if (player.getActivityRecord().getCostRewards().contains(Integer.valueOf(count))) {
				b.setResult(false);
				b.setErrInfo("已经领取过该奖励");
			} else if (player.getActivityRecord().getCost() < count) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else {
				List list = (List) ai.rewards.get(Integer.valueOf(count));
				player.getActivityRecord().costRewardGet(player, count, list);
				player.notifyGetItem(2, list);
			}
		}
		player.send(1270, b.build());
	}

	private void turnPlateInfo(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 42))) {
			return;
		}
		PbDown.TurnPlateInfoRst.Builder b = PbDown.TurnPlateInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(9, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			ActivityRecord ar = player.getActivityRecord();
			TurnPlateAI ai = (TurnPlateAI) ActivityInfo.getItem(player, 9);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				b.addAllItems(ai.genTurnPlateItemList());
				b.addAllRewards(ar.genTurnPlateSocreRewardList(ai));
				b.setBaseInfo(ar.genTurnPlateBaseInfo(player));
				b.setTime(ActivityInfo.getEndTime(9) - System.currentTimeMillis());
			}
		}
		player.send(1276, b.build());
	}

	private void turnPlateGetScoreReward(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 42))) {
			return;
		}
		PbDown.TurnPlateGetScoreRewardRst.Builder b = PbDown.TurnPlateGetScoreRewardRst.newBuilder().setResult(true);
		int score = 0;
		try {
			PbUp.TurnPlateGetScoreReward req = PbUp.TurnPlateGetScoreReward.parseFrom(packet.getData());
			score = req.getScore();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1278, b.build());
			return;
		}

		if (!(ActivityInfo.isOpenActivity(9, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			TurnPlateAI ai = (TurnPlateAI) ActivityInfo.getItem(player, 9);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (!(ai.scoreRewards.containsKey(Integer.valueOf(score)))) {
				b.setResult(false);
				b.setErrInfo("没有该积分对应的奖励");
			} else if (player.getActivityRecord().isGetTurnPlateSocreReward(score)) {
				b.setResult(false);
				b.setErrInfo("已经领取过该奖励");
			} else if (player.getActivityRecord().getTurnPlateTotalCount() < score) {
				b.setResult(false);
				b.setErrInfo("未获得奖励对应积分数");
			} else {
				List list = (List) ai.scoreRewards.get(Integer.valueOf(score));
				player.getActivityRecord().turnPlateScoreRewardGet(player, score, list);
				player.notifyGetItem(2, list);
			}
		}

		player.send(1278, b.build());
	}

	private void turnPlatePlay(Player player, PbPacket.Packet packet) {
		int count;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 42))) {
			return;
		}
		PbDown.TurnPlatePlayRst.Builder b = PbDown.TurnPlatePlayRst.newBuilder().setResult(true);
		try {
			PbUp.TurnPlatePlay req = PbUp.TurnPlatePlay.parseFrom(packet.getData());
			count = req.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1280, b.build());
			return;
		}

		if (!(ActivityInfo.isOpenActivity(9, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else if ((count != 1) && (count != 10)) {
			b.setResult(false);
			b.setErrInfo("幸运转盘抽取次数不正确");
		} else {
			int cost = 0;
			ActivityRecord ar = player.getActivityRecord();
			if (player.getBags().getItemCount(11014) >= count)
				cost = 0;
			else {
				cost = (count == 1) ? 50 : 500;
			}

			int raffles = player.getBags().getItemCount(11014);
			if ((cost > 0) && (player.getJewels() < cost)) {
				b.setResult(false);
				b.setErrInfo("元宝不足");
			} else if ((count <= 0) && (count > raffles)) {
				b.setResult(false);
				b.setErrInfo("物品不足");
			} else {
				TurnPlateAI ai = (TurnPlateAI) ActivityInfo.getItem(player, 9);
				if (ai == null) {
					b.setResult(false);
					b.setErrInfo("活动尚未开启或已结束");
				} else {
					List<Integer> ids = ar.turnPlatePlay(player, count, ai);
					List rewards = new ArrayList();
					List keyRewards = new ArrayList();
					for (Integer id : ids) {
						Map.Entry entry = (Map.Entry) ai.rewards.get(id);
						Reward r = (Reward) entry.getValue();
						rewards.add(r);
						r.add(player, "turnplate");
						if (((Integer) entry.getKey()).intValue() == 1) {
							keyRewards.add(r);
						}
					}
					if (keyRewards.size() > 0) {
						int i;
						StringBuilder names = new StringBuilder().append(keyRewards.get(0));
						for (i = 1; i < keyRewards.size(); ++i) {
							names.append(",").append(keyRewards.get(i));
						}
						Platform.getPlayerManager().boardCast(MessageFormat.format(
								"<p style=13>[{0}]</p><p style=17>人品爆发，在【活动：地藏探宝】中获得了</p><p style=15>{1}</p><p style=17>，真是羡煞旁人。</p>",
								new Object[] { player.getName(), names.toString() }));
					}
					if (cost > 0)
						player.decJewels(cost, "turnplate");
					else {
						new Reward(0, count, ItemService.getItemTemplate(11014)).remove(player, "turnplate");
					}
					b.addAllIds(ids);
					b.setBaseInfo(ar.genTurnPlateBaseInfo(player));
					player.notifyGetItem(2, rewards);
				}
			}
		}
		player.send(1280, b.build());
	}

	private void prayInfo(Player player) {
		PbDown.PrayInfoRst.Builder b = PbDown.PrayInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(10, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			ActivityRecord ar = player.getActivityRecord();
			PrayAI ai = (PrayAI) ActivityInfo.getItem(player, 10);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				b.setTime(ActivityInfo.getEndTime(10) - System.currentTimeMillis());
				b.setSpiritJade(ai.getNeedCountToNextPray(5, ar.getPrayProcess(5)));
				b.setJewels(ai.getNeedCountToNextPray(1, ar.getPrayProcess(1)));
				b.setStamina(ai.getNeedCountToNextPray(3, ar.getPrayProcess(3)));
				b.setVitality(ai.getNeedCountToNextPray(4, ar.getPrayProcess(4)));
				b.setSurplus(ar.getPraySurplus());
				for (Fall f : ((DropGroup) DropService.dropGroups.get(Integer.valueOf(ai.drop))).fall()) {
					b.addRewards(f.genReward().genPbReward());
				}
			}
		}

		player.send(1282, b.build());
	}

	private void pray(Player player) {
		PbDown.PrayRst.Builder b = PbDown.PrayRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(10, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			PrayAI ai = (PrayAI) ActivityInfo.getItem(player, 10);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				ActivityRecord ar = player.getActivityRecord();
				int count = ar.getPraySurplus();
				if (count < 1) {
					b.setResult(false);
					b.setErrInfo("剩余次数不足");
				} else {
					List list = new ArrayList();
					if (count > 10)
						count = 10;
					DropGroup dg = (DropGroup) DropService.dropGroups.get(Integer.valueOf(ai.drop));
					for (int i = 0; i < count; ++i) {
						List<Gain> gains = dg.genGains(player);
						for (Gain gain : gains) {
							gain.gain(player, "pray");
							list.add(gain.newReward());
						}
					}
					b.setReceive(count);
					ar.setPraySurplus(ar.getPraySurplus() - count);
					player.notifyGetItem(2, list);
					Platform.getEventManager().addEvent(new Event(2088, new Object[] { player }));
					PlayerService.boardCastGetNBItem(player, list,
							"<p style=13>[{0}]</p><p style=17>人品爆发，祈福时获得了</p><p style=15>{1}</p><p style=17>，真是羡煞旁人。</p>");
				}
			}
		}
		player.send(1284, b.build());
	}

	private void lifeMemberInfo(Player player) {
		PbDown.LifeMemberInfoRst.Builder b = PbDown.LifeMemberInfoRst.newBuilder().setResult(true);
		Vip vip = player.getVip();
		b.setLevel(vip.level);
		if (vip.dayRewards != null) {
			for (Reward r : vip.dayRewards) {
				b.addRewards(r.genPbReward());
			}
		}
		Vip next = vip.getNext();
		if (next != null) {
			b.setNextLevel(next.level);
			if (next.dayRewards != null) {
				for (Reward r : next.dayRewards) {
					b.addNextRewards(r.genPbReward());
				}
			}
			b.setNeed(next.charge - player.getCharge());
		} else {
			b.setNextLevel(-1);
			b.setNeed(-1);
		}
		b.setIsGet(player.getPool().getBool(7, false));
		player.send(1288, b.build());
	}

	private void lifeMemberGet(Player player) {
		PbDown.LifeMemberGetRst.Builder b = PbDown.LifeMemberGetRst.newBuilder().setResult(true);
		Vip vip = player.getVip();
		if (vip.dayRewards == null) {
			b.setResult(false);
			b.setErrInfo("当前VIP等级无每日奖励可领取");
		} else if (player.getPool().getBool(7, false)) {
			b.setResult(false);
			b.setErrInfo("今天的奖励已经领取过，请明日再来");
		} else if (player.getBags().getFullBag() != -1) {
			b.setResult(false);
			b.setErrInfo("背包已满，请先清理背包");
		} else {
			for (Reward r : vip.dayRewards) {
				r.add(player, "vipdayreward");
			}
			player.getPool().set(7, Boolean.valueOf(true));
			player.notifyGetItem(2, vip.dayRewards);
			Platform.getEventManager().addEvent(new Event(2082, new Object[] { player }));
		}

		player.send(1290, b.build());
	}

	private void dayChargeRewardInfo(Player player) {
		PbDown.DayChargeRewardInfoRst.Builder b = PbDown.DayChargeRewardInfoRst.newBuilder().setResult(true);
		if (!(ActivityInfo.isOpenActivity(12, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			Set getSet = player.getActivityRecord().getDayChargeRewards();
			DayChargeAI ai = (DayChargeAI) ActivityInfo.getItem(player, 12);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				Map dayChargeRewards = ai.rewards;
				Set<Map.Entry> set = dayChargeRewards.entrySet();
				for (Map.Entry entry : set) {
					int count = ((Integer) entry.getKey()).intValue();
					if (!(getSet.contains(Integer.valueOf(count)))) {
						List<Reward> list = (List) dayChargeRewards.get(Integer.valueOf(count));
						if (list != null) {
							PbActivity.DayChargeReward.Builder rb = PbActivity.DayChargeReward.newBuilder();
							rb.setCount(count);
							for (Reward reward : list) {
								rb.addRewards(reward.genPbReward());
							}
							b.addRewards(rb);
						}
					}
				}
				b.setCount(player.getActivityRecord().getDayCharge());
				b.setStartStr(ActivityInfo.getStartTimeStr(12));
				b.setEndStr(ActivityInfo.getEndTimeStr(12));
			}
		}
		player.send(1316, b.build());
	}

	private void dayChargeRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.DayChargeRewardGetRst.Builder b = PbDown.DayChargeRewardGetRst.newBuilder().setResult(true);
		int count = 0;
		try {
			PbUp.DayChargeRewardGet req = PbUp.DayChargeRewardGet.parseFrom(packet.getData());
			count = req.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1318, b.build());
			return;
		}
		if (!(ActivityInfo.isOpenActivity(12, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			DayChargeAI ai = (DayChargeAI) ActivityInfo.getItem(player, 12);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (!(ai.containsKey(count))) {
				b.setResult(false);
				b.setErrInfo("奖励不存在或已过期");
			} else if (player.getActivityRecord().getDayChargeRewards().contains(Integer.valueOf(count))) {
				b.setResult(false);
				b.setErrInfo("已经领取过该奖励");
			} else if (player.getActivityRecord().getDayCharge() < count) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else {
				List list = (List) ai.rewards.get(Integer.valueOf(count));
				player.getActivityRecord().dayChargeRewardGet(player, count, list);
				player.notifyGetItem(2, list);
			}
		}
		player.send(1318, b.build());
	}

	private void dayRewardInfo(Player player, PbPacket.Packet packet) {
		PbDown.DayRewardInfoRst.Builder b = PbDown.DayRewardInfoRst.newBuilder().setResult(true);
		try {
			ActivityRecord ar = player.getActivityRecord();
			if (ar.dayRewardIsOver()) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
				player.send(1320, b.build());
				return;
			}
			Map<Integer, List<Reward>[]> dayRewards = (ar.getCurRoundType() != 2) ? day30Rewards : day7Rewards;
			for (Integer day : dayRewards.keySet())
				if (!(ar.getDayRewardGet().contains(day))) {
					Reward r;
					PbActivity.DayReward.Builder db = PbActivity.DayReward.newBuilder();
					db.setDay(day.intValue());
					List list = ((List[]) dayRewards.get(day))[0];
					for (Iterator localIterator2 = list.iterator(); localIterator2.hasNext();) {
						r = (Reward) localIterator2.next();
						db.addRewards(r.genPbReward());
					}
					list = ((List[]) dayRewards.get(day))[1];
					for (Iterator it = list.iterator(); it.hasNext();) {
						r = (Reward) it.next();
						db.addSuperRewards(r.genPbReward());
					}
					if (ar.getDayRewardCount() >= day.intValue())
						db.setCanGet(true);
					else {
						db.setCanGet(false);
					}
					db.setRewardType(ar.getChargeType(day.intValue()));
					b.addRewards(db.build());
				}
		} catch (Exception e) {
			e.printStackTrace();
			b.setResult(false);
		}
		player.send(1320, b.build());
	}

	private void dayRewardGet(Player player, PbPacket.Packet packet) {
		PbDown.DayRewardGetRst.Builder builder = PbDown.DayRewardGetRst.newBuilder().setResult(true);
		int day = 0;
		try {
			PbUp.DayRewardGet get = PbUp.DayRewardGet.parseFrom(packet.getData());
			day = get.getDay();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1322, builder.build());
			return;
		}
		ActivityRecord ar = player.getActivityRecord();
		Map dayRewards = (ar.getCurRoundType() != 2) ? day30Rewards : day7Rewards;
		if (ar.dayRewardIsOver()) {
			builder.setResult(false);
			builder.setErrInfo("活动尚未开启或已结束");
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (!(dayRewards.containsKey(Integer.valueOf(day)))) {
			builder.setResult(false);
			builder.setErrInfo("奖励不存在或已过期");
		} else if (ar.getDayRewardGet().contains(Integer.valueOf(day))) {
			builder.setResult(false);
			builder.setErrInfo("已经领取过该奖励");
		} else if (ar.getDayRewardCount() < day) {
			builder.setResult(false);
			builder.setErrInfo("你不能领取该奖励");
		} else {
			List list = ((List[]) dayRewards.get(Integer.valueOf(day)))[ar.getChargeType(day)];
			ar.getDayReward(player, day, list);
			player.notifyGetItem(2, list);
		}
		player.send(1322, builder.build());
	}

	private void monthCardInfo(Player player, PbPacket.Packet packet) {
	}

	private void persistChargeInfo(Player player) {
		PbDown.PersistChargeInfoRst.Builder b = PbDown.PersistChargeInfoRst.newBuilder().setResult(true);
		ActivityRecord ar = player.getActivityRecord();
		if (!(ActivityInfo.isOpenActivity(13, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else {
			PersistChargeAI ai = (PersistChargeAI) ActivityInfo.getItem(player, 13);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else {
				for (Integer day : ai.keySet()) {
					if (!(ar.getPersistChargeGet().contains(day))) {
						PbActivity.PersistChargeReward.Builder rb = PbActivity.PersistChargeReward.newBuilder();
						rb.setDay(day.intValue());
						List<Reward> list = (List) ai.rewards.get(day);
						for (Reward r : list) {
							rb.addRewards(r.genPbReward());
						}
						if (ar.getPersistChargeCount() >= day.intValue())
							rb.setCanGet(true);
						else {
							rb.setCanGet(false);
						}
						b.addRewards(rb);
					}
				}
				b.setStartStr(ActivityInfo.getStartTimeStr(13));
				b.setEndStr(ActivityInfo.getEndTimeStr(13));
			}
		}

		player.send(1378, b.build());
	}

	private void persistChargeGet(Player player, PbPacket.Packet packet) {
		PbDown.PersistChargeGetRst.Builder b = PbDown.PersistChargeGetRst.newBuilder().setResult(true);
		int day = 0;
		try {
			PbUp.PersistChargeGet get = PbUp.PersistChargeGet.parseFrom(packet.getData());
			day = get.getDay();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1380, b.build());
			return;
		}
		ActivityRecord ar = player.getActivityRecord();
		if (!(ActivityInfo.isOpenActivity(13, player))) {
			b.setResult(false);
			b.setErrInfo("活动尚未开启或已结束");
		} else if (player.getBags().getFullBag() != -1) {
			b.setResult(false);
			b.setErrInfo("背包已满，请先清理背包");
		} else {
			PersistChargeAI ai = (PersistChargeAI) ActivityInfo.getItem(player, 13);
			if (ai == null) {
				b.setResult(false);
				b.setErrInfo("活动尚未开启或已结束");
			} else if (!(ai.containsKey(day))) {
				b.setResult(false);
				b.setErrInfo("奖励不存在或已过期");
			} else if (ar.getPersistChargeGet().contains(Integer.valueOf(day))) {
				b.setResult(false);
				b.setErrInfo("已经领取过该奖励");
			} else if (ar.getPersistChargeCount() < day) {
				b.setResult(false);
				b.setErrInfo("你不能领取该奖励");
			} else {
				List list = (List) ai.rewards.get(Integer.valueOf(day));
				ar.getPersistChargeReward(player, day, list);
				player.notifyGetItem(2, list);
			}
		}
		player.send(1380, b.build());
	}

	public static MonthCard getMonthCard(int id) {
		return ((MonthCard) monthCards.get(Integer.valueOf(id)));
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	public int[] getEventCodes() {
		return new int[] { 2013, 2014, 1002, 1004 };
	}

	public void handleEvent(Event event) {
		Player player;
		Iterator localIterator;
		if (event.type == 2013) {
			for (localIterator = Platform.getPlayerManager().players.values().iterator(); localIterator.hasNext();) {
				player = (Player) localIterator.next();
				if (player != null)
					Function.notifyMainNum(player, 16, 1);
			}
		} else if (event.type == 2014) {
			for (localIterator = Platform.getPlayerManager().players.values().iterator(); localIterator.hasNext();) {
				player = (Player) localIterator.next();
				if (player != null)
					Function.notifyMainCheck(player, new int[] { 16 });
			}
		} else if (event.type == 1002) {
			Cache cache = Platform.getEntityManager().getEhCache("common");
			Element e = cache.get("KEY_RANK7");
			if (e != null) {
				long endTime = ((Long) e.getObjectValue()).longValue();
				long leftTime = endTime - System.currentTimeMillis();
				if (leftTime <= 60000L) {
					cache.remove("KEY_RANK7");
					sendRank7Rewards();
				}
			}
		} else {
			if ((event.type != 1004) || (getRank7ActivityLeftTime() <= 0L))
				return;
			StringBuilder sb = new StringBuilder();
			List btlRanks = Platform.getTopManager().getRanks(2);
			for (int i = 0; i < 3; ++i) {
				if (i != 0) {
					sb.append("，");
				}
				sb.append(Platform.getPlayerManager().getMiniPlayer(((Top) btlRanks.get(i)).getPid()).getName());
			}

			String msg = MessageFormat.format(
					"<p style=17>“七日战力比拼”活动快报！截止目前，战力最高的三名玩家依次是</p><p style=13>{0}</p><p style=17>！ → 更多活动规则奖励详情，请到“活动中心”查看！</p>",
					new Object[] { sb.toString() });
			Platform.getPlayerManager().boardCast(msg);
		}
	}

	private void sendRank7Rewards() {
		List list = new ArrayList(Platform.getTopManager().getRanks(2));
		HashMap rewards = new HashMap();
		for (Rank7Reward r : rank7Rewards) {
			if ((r.getEndRank() != 0) && (r.getStartRank() != 0)) {
				for (int i = r.getStartRank(); i <= r.getEndRank(); ++i) {
					rewards.put(Integer.valueOf(i), r);
				}
			}
		}

		for (int rank = 1; rank <= list.size(); ++rank) {
			Top top = (Top) list.get(rank - 1);
			Rank7Reward reward = (Rank7Reward) rewards.get(Integer.valueOf(rank));
			MailService.sendSystemMail(18, top.getPid(), "七日比拼排名奖励", MessageFormat.format(
					"<p style=21>恭喜主公，截止到“七日战力比拼”活动结束，您的战力为</p><p style=19>【{0}】</p><p style=21>，全服排行第</p><p style=20>【{1}】</p><p style=21>名。获得奖励如下：</p>",
					new Object[] { Integer.valueOf(top.getValue()), Integer.valueOf(rank) }), new Date(),
					reward.getRewards());
		}
	}

	public static long getOpenServer7DayLeftTimeByTime(long time) {
		long endTime = getOpenServer7DayTime();
		if (endTime > 0L) {
			long leftTime = endTime - time;
			if (leftTime >= 0L) {
				return leftTime;
			}
		}
		return -1L;
	}

	public static long getOpenServer7DayTime() {
		Cache cache = Platform.getEntityManager().getEhCache("common");
		Element e = cache.get("KEY_RANK7");
		if (e != null) {
			long endTime = ((Long) e.getObjectValue()).longValue();
			return endTime;
		}
		return -1L;
	}

	private void checkActivity() {
		for (Iterator localIterator1 = ActivityInfo.activities.keySet().iterator(); localIterator1.hasNext();) {
			int i;
			int activityId = ((Integer) localIterator1.next()).intValue();

			if ((activityId == 5) || (activityId == 6) || (activityId == 7) || (activityId == 14) || (activityId == 16)
					|| (activityId == 15))
				continue;
			if (activityId > 100) {
				continue;
			}

			if (!(ActivityInfo.activityItems.containsKey(Integer.valueOf(activityId)))) {
				throw new RuntimeException("activity has no data! activity:" + activityId);
			}
			Map map = (Map) ActivityInfo.activities.get(Integer.valueOf(activityId));

			for (Iterator localIterator2 = map.keySet().iterator(); localIterator2.hasNext();) {
				int roundId = ((Integer) localIterator2.next()).intValue();
				if (!(((Map) ActivityInfo.activityItems.get(Integer.valueOf(activityId)))
						.containsKey(Integer.valueOf(roundId)))) {
					throw new RuntimeException(
							"activity has no round data! activity:" + activityId + ", round:" + roundId);
				}
			}

			List list = new ArrayList(map.values());
			for (Iterator itx = list.iterator(); itx.hasNext();) {
				ActivityData ad = (ActivityData) itx.next();
				if (ad.roundId == 0) {
					itx.remove();
				}
			}
			for (i = 0; i < list.size(); ++i) {
				ActivityData data = (ActivityData) list.get(i);
				if (data.end <= data.start) {
					throw new RuntimeException("activity endTime lower than startTime! activity:" + data.activityId
							+ ", round:" + data.roundId);
				}
				for (int j = i + 1; j < list.size(); ++j) {
					ActivityData tmp = (ActivityData) list.get(j);
					if ((data.end < tmp.start) || (data.start > tmp.end)) {
						continue;
					}
					throw new RuntimeException("activity time error! activity:" + data.activityId + ", round:"
							+ data.roundId + ", nextRound:" + tmp.roundId);
				}
			}
		}
	}
}
