package org.darcy.sanguo.union;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.MapService;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.time.DayCrontab;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbLeague;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class LeagueService implements Service, PacketHandler, EventHandler {
	public static final int LOTTERY_COUNT = 40;
	public static final int EXP_LOTTERY_GOLD_REFRESH_PRICE = 5015;
	public static final int EXP_LOTTERY_CONTRI_REFRESH_PRICE = 5016;
	public static final int LOTTERY_GET_PRICE = 50;
	public static Map<Integer, LeagueBuildData> buildDatas = new HashMap<Integer, LeagueBuildData>();
	public static Map<Integer, LeagueGoods> goods = new HashMap<Integer, LeagueGoods>();

	public static Map<Integer, LeagueRareGoodTemplate> rareGoods = new HashMap<Integer, LeagueRareGoodTemplate>();
	public static Map<Integer, LeagueNormalGoods> normalGoods = new HashMap<Integer, LeagueNormalGoods>();

	public static Map<Integer, Map<Integer, List<Reward>>> bossDayRewards = new HashMap<Integer, Map<Integer, List<Reward>>>();

	public static Map<Integer, Integer> bossBuff = new HashMap<Integer, Integer>();

	public static Map<Integer, Integer> limit = new HashMap<Integer, Integer>();

	public static Map<Integer, Integer> lotteryRrfreshTimes = new HashMap<Integer, Integer>();

	public static Map<Integer, List<int[]>> lotteryRewards = new HashMap<Integer, List<int[]>>();

	public static Map<Integer, Reward> rewardPool = new HashMap<Integer, Reward>();
	public static LeagueBox[] boxes;
	public static Map<Integer, Integer> boxScore = new HashMap<Integer, Integer>();

	private Map<Integer, List<Reward>> bosstimeRewards = new HashMap<Integer, List<Reward>>();

	public static Map<Integer, List<Reward>> bossweekRewards = new HashMap<Integer, List<Reward>>();

	public void startup() throws Exception {
		loadData();
		StringBuilder sb = new StringBuilder();
		sb.append(LeagueData.LEAGUE_RARE_SHOP_REFRESH_HOUR).append(" 0 0");
		new Crontab(sb.toString(), 2032);
		new Crontab("0 30 0", 2086);
		new DayCrontab("2 0 0 0", 1014);
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		Platform.getLeagueManager().init();
	}

	private void loadData() {
		int i, j, id, pos;
		int level;
		String rewardsStr;
		String r;
		String s;
		LeagueBuildData data;
		Object list;

		List<Row> list0 = ExcelUtils.getRowList("league.xls", 2, 0);
		for (Row row : list0) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			LeagueData.LEAGUE_LIST_PAGE_SIZE = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_CREATE_COST_JEWEL = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_CREATE_COST_MONEY = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_NOTICE_SIZE = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_NOTICE_NUM = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_VICELEADER_NUM = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_NAME_SIZE = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_EXIT_COLD_TIME = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_APPLY_MEMBER_LIMIT = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_APPLY_NUM_LIMIT = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_MAX_LEVEL = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_EXP_ID_HALL = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_EXP_ID_SHOP = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_EXP_ID_GOODS = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_RARE_SHOP_REFRESH_HOUR = (int) row.getCell(pos++).getNumericCellValue();
			String str = row.getCell(pos++).getStringCellValue();
			LeagueData.LEAGUE_FACILITY = Calc.split(str, ",");
			LeagueData.LEAGUE_GET_GOODS_COST = (int) row.getCell(pos++).getNumericCellValue();
			String bossIds = row.getCell(pos++).getStringCellValue();
			LeagueData.LEAGUE_BOSS_ID = Calc.split(bossIds, ",");
			String bossFightMoney = row.getCell(pos++).getStringCellValue();
			LeagueData.LEAGUE_BOSS_FIGHT_MONEY = Calc.split(bossFightMoney, ",");
			LeagueData.LEAGUE_BOSS_OPEN_LEVEL = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_BOSS_REVIVE_TIME = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_BOSS_FIGHT_NUM = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_BOSS_COLD_TIME = (int) row.getCell(pos++).getNumericCellValue();
			LeagueData.LEAGUE_EXP_ID_BOSS = (int) row.getCell(pos++).getNumericCellValue();
		}

		List<Row> list1 = ExcelUtils.getRowList("league.xls", 2, 1);
		for (Row row : list1) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String name = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String cost = row.getCell(pos++).getStringCellValue();
			int build = (int) row.getCell(pos++).getNumericCellValue();
			int contribution = (int) row.getCell(pos++).getNumericCellValue();

			data = new LeagueBuildData();
			data.id = id;
			data.name = name;
			data.cost = new Reward(cost);
			data.buildValue = build;
			data.contribution = contribution;
			buildDatas.put(Integer.valueOf(id), data);
		}

		List<Row> list2 = ExcelUtils.getRowList("league.xls", 2, 2);
		for (Row row : list2) {
			String[] arrayOfString1;
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String goodsStr = row.getCell(pos++).getStringCellValue();

			LeagueGoods good = new LeagueGoods();
			good.level = level;
			String[] array = goodsStr.split(",");
			for (j = 0; j < array.length; ++j) {
				String tmp = array[j];
				good.rewards.add(new Reward(tmp));
			}
			goods.put(Integer.valueOf(level), good);
		}

		List<Row> list3 = ExcelUtils.getRowList("league.xls", 2, 3);
		for (Row row : list3) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			int count = (int) row.getCell(pos++).getNumericCellValue();
			int cost = (int) row.getCell(pos++).getNumericCellValue();
			String itemStr = row.getCell(pos++).getStringCellValue();

			LeagueRareGoodTemplate template = new LeagueRareGoodTemplate();
			template.id = level;
			template.count = count;
			template.cost = cost;
			template.reward = new Reward(itemStr);
			rareGoods.put(Integer.valueOf(level), template);
		}

		List<Row> list4 = ExcelUtils.getRowList("league.xls", 2, 4);
		for (Row row : list4) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			String itemStr = row.getCell(pos++).getStringCellValue();
			int count = (int) row.getCell(pos++).getNumericCellValue();
			int cost = (int) row.getCell(pos++).getNumericCellValue();
			int shopLevel = (int) row.getCell(pos++).getNumericCellValue();

			LeagueNormalGoods goods = new LeagueNormalGoods();
			goods.id = level;
			goods.item = new Reward(itemStr);
			goods.count = count;
			goods.cost = cost;
			goods.shopLevel = shopLevel;

			goods.refresh = true;
			normalGoods.put(Integer.valueOf(level), goods);
		}

		List<Row> list5 = ExcelUtils.getRowList("league.xls", 2, 5);
		for (Row row : list5) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			String first = row.getCell(pos++).getStringCellValue();
			String second = row.getCell(pos++).getStringCellValue();
			String third = row.getCell(pos++).getStringCellValue();
			String other = row.getCell(pos++).getStringCellValue();
			Map rewards = new HashMap();
			list = new ArrayList();
			String[] array = first.split(",");
			for (j = 0; j < array.length; ++j) {
				((List) list).add(new Reward(array[j]));
			}
			rewards.put(Integer.valueOf(1), list);

			list = new ArrayList();
			array = second.split(",");
			for (j = 0; j < array.length; ++j) {
				((List) list).add(new Reward(array[j]));
			}
			rewards.put(Integer.valueOf(2), list);

			list = new ArrayList();
			array = third.split(",");
			for (j = 0; j < array.length; ++j) {
				((List) list).add(new Reward(array[j]));
			}
			rewards.put(Integer.valueOf(3), list);

			list = new ArrayList();
			array = other.split(",");
			for (j = 0; j < array.length; ++j) {
				((List) list).add(new Reward(array[j]));
			}
			rewards.put(Integer.valueOf(-1), list);

			bossDayRewards.put(Integer.valueOf(level), rewards);
		}

		List<Row> list6 = ExcelUtils.getRowList("league.xls", 2, 6);
		for (Row row : list6) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			id = (int) row.getCell(pos++).getNumericCellValue();

			bossBuff.put(Integer.valueOf(level), Integer.valueOf(id));
		}

		List<Row> list9 = ExcelUtils.getRowList("league.xls", 2, 9);
		for (Row row : list9) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			int limit1 = (int) row.getCell(pos++).getNumericCellValue();

			limit.put(Integer.valueOf(level), Integer.valueOf(limit1));
		}

		List<Row> list10 = ExcelUtils.getRowList("league.xls", 2, 10);
		for (Row row : list10) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			level = (int) row.getCell(pos++).getNumericCellValue();
			int times = (int) row.getCell(pos++).getNumericCellValue();

			lotteryRrfreshTimes.put(Integer.valueOf(level), Integer.valueOf(times));
			List pools = new ArrayList();
			for (int pi = 0; pi < 40; ++pi) {
				row.getCell(pos);
				s = row.getCell(pos++).getStringCellValue();
				pools.add(Calc.split(s, ","));
			}
			lotteryRewards.put(Integer.valueOf(level), pools);
		}

		List<Row> list11 = ExcelUtils.getRowList("league.xls", 2, 11);
		for (Row row : list11) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int rid = (int) row.getCell(pos++).getNumericCellValue();
			s = row.getCell(pos++).getStringCellValue();
			rewardPool.put(Integer.valueOf(rid), new Reward(s));
		}

		List<Row> list7 = ExcelUtils.getRowList("league.xls", 2, 7);
		for (Row row : list7) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			row.getCell(pos);
			level = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos);
			rewardsStr = row.getCell(pos++).getStringCellValue();
			List rewards = new ArrayList();
			String[] ids = rewardsStr.split(",");
			for (j = 0; j < ids.length; ++j) {
				r = ids[j];
				rewards.add(new Reward(r));
			}
			this.bosstimeRewards.put(Integer.valueOf(level), rewards);
		}

		List<Row> list8 = ExcelUtils.getRowList("league.xls", 2, 8);
		for (Row row : list8) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			row.getCell(pos);
			level = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos);
			rewardsStr = row.getCell(pos++).getStringCellValue();
			List rewards = new ArrayList();
			String[] ids = rewardsStr.split(",");
			for (j = 0; j < ids.length; ++j) {
				r = ids[j];
				rewards.add(new Reward(r));
			}
			bossweekRewards.put(Integer.valueOf(level), rewards);
		}
	}

	public int[] getEventCodes() {
		return new int[] { 2032, 2086, 1002, 1013, 1014 };
	}

	public int[] getCodes() {
		return new int[] { 1171, 1173, 1175, 1177, 1179, 1181, 1183, 1185, 1187, 1189, 1191, 1193, 1195, 1197, 1199,
				1201, 1203, 1209, 1211, 1213, 1215, 1217, 1219, 1273, 1301, 1303, 1307, 1309, 1311, 1313, 1323, 2269,
				2267, 2271, 1371, 1373, 1375 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		if ((packet.getPtCode() != 1273) && (!(FunctionService.isOpenFunction(player.getLevel(), 16)))) {
			return;
		}

		switch (packet.getPtCode()) {
		case 1171:
			leagueList(player, packet);
			break;
		case 1173:
			leagueApplyList(player);
			break;
		case 1175:
			leagueFindByName(player, packet);
			break;
		case 1177:
			leagueCreate(player, packet);
			break;
		case 1179:
			myLeague(player);
			break;
		case 1181:
			updateNoitce(player, packet);
			break;
		case 1183:
			abdicate(player, packet);
			break;
		case 1185:
			appoint(player, packet);
			break;
		case 1187:
			kickout(player, packet);
			break;
		case 1189:
			quit(player);
			break;
		case 1191:
			apply(player, packet);
			break;
		case 1193:
			accept(player, packet);
			break;
		case 1195:
			refuse(player, packet);
			break;
		case 1197:
			leagueActivity(player, packet);
			break;
		case 1199:
			leagueBuildInfo(player);
			break;
		case 1201:
			leagueBuild(player, packet);
			break;
		case 1203:
			leagueGoods(player);
			break;
		case 1209:
			getGoods(player);
			break;
		case 1211:
			leagueRareShop(player);
			break;
		case 1213:
			leagueNormalShop(player);
			break;
		case 1215:
			exchangeRareGoods(player, packet);
			break;
		case 1217:
			exchangeNormalGoods(player, packet);
			break;
		case 1219:
			levelUp(player, packet);
			break;
		case 1273:
			hasLeague(player);
			break;
		case 1301:
			leagueApplicants(player);
			break;
		case 1303:
			cancelApply(player, packet);
			break;
		case 1307:
			bossInfo(player);
			break;
		case 1309:
			bossChallenge(player);
			break;
		case 1311:
			bossRank(player);
			break;
		case 1313:
			bossRankReward(player);
			break;
		case 1323:
			myMiniLeague(player);
			break;
		case 2267:
			lotteryInfo(player);
			break;
		case 2269:
			PbUp.LeagueLotteryGet get = PbUp.LeagueLotteryGet.parseFrom(packet.getData());
			lotteryGet(player, get.getIndex());
			break;
		case 2271:
			PbUp.LeagueLotteryRefresh refresh = PbUp.LeagueLotteryRefresh.parseFrom(packet.getData());
			lotterRefresh(player, refresh.getType());
			break;
		case 1371:
			leagueBoxInfo(player);
			break;
		case 1373:
			leagueBoxExchange(player, packet);
			break;
		case 1375:
			leagueBoxGet(player);
		}
	}

	private void leagueBoxInfo(Player player) {
		PbDown.LeagueBoxInfoRst.Builder b = PbDown.LeagueBoxInfoRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			b.addAllScores(boxScore.keySet());
			b.setCurScore(l.getInfo().getBoxCount());
			b.setExchangeCost(20);
			b.setExchangeSurplus(l.getBoxExchangeSurplus(player.getId()));
			b.setGetSurplus(l.getBoxGetSurplus(player.getId()));
			LeagueMember lm = l.getMember(player.getId());
			b.setCostForCreate(lm.calCostForCreate());
			b.setBoxCount(lm.getBoxTotalCount());

			List list = l.getBoxRankers();
			for (int i = 0; (i < list.size()) && (i < 10); ++i) {
				b.addRankers((PbLeague.LeagueBoxRanker) list.get(i));
			}
		}
		player.send(1372, b.build());
	}

	private void leagueBoxExchange(Player player, PbPacket.Packet packet) {
		PbDown.LeagueBoxExchangeRst.Builder b = PbDown.LeagueBoxExchangeRst.newBuilder().setResult(true);
		int count = 1;
		try {
			PbUp.LeagueBoxExchangeReq req = PbUp.LeagueBoxExchangeReq.parseFrom(packet.getData());
			count = req.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1374, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			count = Math.max(1, count);
			if (l.getBoxExchangeSurplus(player.getId()) < count) {
				b.setResult(false);
				b.setErrInfo("兑换次数不足，无法兑换");
			} else if (player.getUnion().getContribution() < 20 * count) {
				b.setResult(false);
				b.setErrInfo("贡献不足，无法兑换");
			} else {
				LeagueMember lm = l.getMember(player.getId());
				lm.exchangeLeagueBox(player, count);
			}
		}
		player.send(1374, b.build());
	}

	private void leagueBoxGet(Player player) {
		PbDown.LeagueBoxGetRst.Builder b = PbDown.LeagueBoxGetRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (l.getBoxGetSurplus(player.getId()) < 1) {
			b.setResult(false);
			b.setErrInfo("剩余次数不足");
		} else {
			LeagueMember lm = l.getMember(player.getId());
			lm.getLeagueBox(player);
		}

		player.send(1376, b.build());
	}

	private void lotterRefresh(Player player, boolean type) {
		PbDown.LeagueLotteryRefreshRst.Builder rst = PbDown.LeagueLotteryRefreshRst.newBuilder();
		rst.setResult(false);
		try {
			Union union = player.getUnion();
			int leagueId = union.getLeagueId();
			League league = Platform.getLeagueManager().getLeagueById(leagueId);
			if (league != null) {
				LeagueInfo info = league.getInfo();
				if (league.getLeader() == player.getId()) {
					int price;
					int time = info.getLotteryRefreshTimes();
					if (!(type)) {
						price = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5015,
								time + 1);
						if (player.getJewels() < price) {
							rst.setErrInfo("元宝不足");
							player.send(2272, rst.build());
							return;
						}
						player.decJewels(price, "leaguelotteryrefresh");
						union.addContribution(player, 3000, "leaguelotteryrefresh");
					} else {
						price = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5016,
								time + 1);
						if (league.getBuildValue() < price) {
							rst.setErrInfo("军团建设不足");
							player.send(2272, rst.build());
							return;
						}
						league.setBuildValue(league.getBuildValue() - price);
					}

					info.refreshLottery(league.getLevel());
					lotteryInfo(player);
					rst.setResult(true);
					info.setLotteryRefreshTimes(info.getLotteryRefreshTimes() + 1);
					Platform.getLog().logLeague(league, "leaguelotteryrefresh", player.getId());
				}
				rst.setErrInfo("只有团长才可以进行操作");
			}
			rst.setErrInfo("您尚未加入军团!");
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}
		label303: player.send(2272, rst.build());
	}

	private void lotteryGet(Player player, int index) {
		PbDown.LeagueLotteryGetRst.Builder rst = PbDown.LeagueLotteryGetRst.newBuilder();
		rst.setResult(false);
		try {
			Union union = player.getUnion();
			int leagueId = union.getLeagueId();
			League league = Platform.getLeagueManager().getLeagueById(leagueId);
			if (league != null) {
				LeagueInfo info = league.getInfo();
				if (info.getLotteryGetRecords().contains(Integer.valueOf(player.getId()))) {
					rst.setErrInfo("本轮你已经抽奖，请等团长大人刷新后再来");
				}
				if (((Boolean) info.getLotteryRecords().get(index)).booleanValue()) {
					rst.setErrInfo("该奖励已经被抽走，请换一个吧");
				}
				if (union.getContribution() < 50) {
					rst.setErrInfo("贡献值不足,不能抽奖");
				}
				union.decContribution(player, 50, "leaguelotteryget");
				info.getLotteryGetRecords().add(Integer.valueOf(player.getId()));
				info.getLotteryRecords().set(index, Boolean.valueOf(true));
				int rid = ((Integer) info.getLotteryRewards().get(index)).intValue();
				Reward reward = (Reward) rewardPool.get(Integer.valueOf(rid));
				reward.add(player, "leaguelotteryget");
				player.notifyGetItem(2, new Reward[] { reward });
				rst.setResult(true);
				if (info.getLotteryShowIndexes().contains(Integer.valueOf(index))) {
					PbCommons.LuckyDog.Builder ld = PbCommons.LuckyDog.newBuilder();
					ld.setName(player.getName()).setRewardName(reward.toString());
					info.getLuckyDogs().add(ld.build());
				}
			}
			rst.setErrInfo("您尚未加入军团!");
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}
		player.send(2270, rst.build());
	}

	private void lotteryInfo(Player player) {
		PbDown.LeagueLotteryInfoRst.Builder rst = PbDown.LeagueLotteryInfoRst.newBuilder();
		rst.setResult(false);
		try {
			Union union = player.getUnion();
			int leagueId = union.getLeagueId();
			if (leagueId != 0) {
				League league = Platform.getLeagueManager().getLeagueById(union.getLeagueId());
				if ((league.getInfo().getLotteryRewards().size() == 0)
						|| (league.getInfo().getLotteryShowIndexes().size() == 0)) {
					league.getInfo().refreshLottery(league.getLevel());
				}
				int goldPrice = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5015,
						league.getInfo().getLotteryRefreshTimes() + 1);
				int contriPrice = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5016,
						league.getInfo().getLotteryRefreshTimes() + 1);
				rst.setMyContribution(league.getBuildValue())
						.setLeftRefreshTimes(league.getInfo().getLeftLotteryRefreshTimes(league.getLevel()))
						.setMaxRefreshTimes(
								((Integer) lotteryRrfreshTimes.get(Integer.valueOf(league.getLevel()))).intValue())
						.setRefreshContriPrice(contriPrice).setRefreshGoldPrice(goldPrice).setGetPrice(50)
						.setLevel(league.getLevel()).setIsLeader(league.getLeader() == player.getId());
				LeagueInfo info = league.getInfo();
				for (PbCommons.LuckyDog dog : info.getLuckyDogs()) {
					rst.addDogs(dog);
				}
				for (Boolean b : info.getLotteryRecords()) {
					rst.addHasFetched(b.booleanValue());
				}
				for (int i = 0; i < 4; ++i) {
					int index = ((Integer) info.getLotteryShowIndexes().get(i)).intValue();
					boolean fetched = ((Boolean) info.getLotteryRecords().get(index)).booleanValue();
					int rid = ((Integer) info.getLotteryRewards().get(index)).intValue();
					Reward r = (Reward) rewardPool.get(Integer.valueOf(rid));
					PbCommons.LotteryShow.Builder ls = PbCommons.LotteryShow.newBuilder();
					ls.setHasFetched(fetched).setReward(r.genPbReward());
					rst.addShows(ls);
				}
				rst.setResult(true);
			}
			rst.setErrInfo("您尚未加入军团!");
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}
		label492: player.send(2268, rst.build());
	}

	private void hasLeague(Player player) {
		PbDown.HasLeagueRst.Builder b = PbDown.HasLeagueRst.newBuilder().setResult(true).setHas(true);
		if (player.getUnion() == null) {
			b.setHas(false);
		} else {
			League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
			if ((l == null) || (!(l.isMember(player.getId())))) {
				b.setHas(false);
			}
		}
		player.send(1274, b.build());
	}

	private void leagueList(Player player, PbPacket.Packet packet) {
		PbDown.LeagueListRst.Builder builder = PbDown.LeagueListRst.newBuilder().setResult(true);
		int page = 0;
		try {
			PbUp.LeagueList req = PbUp.LeagueList.parseFrom(packet.getData());
			page = req.getPage();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1172, builder.build());
			return;
		}
		builder.setIsJoin(player.getUnion().getLeagueId() > 0);
		List<League> list = new ArrayList(Platform.getLeagueManager().getLeagues().values());

		for (League a : list) {
			if (a != null) {
				builder.addLeagues(a.genListLeague(player));
			}
		}
		player.send(1172, builder.build());
	}

	private void leagueApplyList(Player player) {
		PbDown.LeagueApplyListRst.Builder b = PbDown.LeagueApplyListRst.newBuilder().setResult(true);
		if (player.getUnion().getLeagueId() > 0) {
			b.setResult(false);
			b.setErrInfo("您已加入一个军团!");
		} else {
			for (Integer id : player.getUnion().getApplys()) {
				League l = Platform.getLeagueManager().getLeagueById(id.intValue());
				if (l != null) {
					b.addLeagues(l.genListLeague(player));
				}
			}
		}
		player.send(1174, b.build());
	}

	private void leagueFindByName(Player player, PbPacket.Packet packet) {
		PbDown.LeagueFindByNameRst.Builder b = PbDown.LeagueFindByNameRst.newBuilder().setResult(true);
		String name = null;
		try {
			PbUp.LeagueFindByName req = PbUp.LeagueFindByName.parseFrom(packet.getData());
			name = req.getName().trim();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1176, b.build());
			return;
		}
		if ((name.length() < 1) || (name.length() > LeagueData.LEAGUE_NAME_SIZE)) {
			b.setResult(false);
			b.setErrInfo(MessageFormat.format("军团名称长度必须在{0}字以内",
					new Object[] { Integer.valueOf(LeagueData.LEAGUE_NAME_SIZE) }));
		} else {
			League l = Platform.getLeagueManager().getLeagueByName(name);
			if (l == null) {
				b.setResult(false);
				b.setErrInfo("没有找到该军团");
			} else {
				b.setLeagues(l.genListLeague(player));
			}
		}
		player.send(1176, b.build());
	}

	private void leagueCreate(Player player, PbPacket.Packet packet) {
		PbDown.LeagueCreateRst.Builder b = PbDown.LeagueCreateRst.newBuilder().setResult(true);
		String name = null;
		PbUp.LeagueCreate.CreateType type = null;
		try {
			PbUp.LeagueCreate req = PbUp.LeagueCreate.parseFrom(packet.getData());
			name = req.getName().trim();
			type = req.getType();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1178, b.build());
			return;
		}
		long coldTime = player.getUnion().getColdTime();
		if (coldTime > 0L) {
			int hour = (int) (coldTime / 1000L / 60L / 60L + 1L);
			b.setResult(false);
			b.setErrInfo(MessageFormat.format("您刚退出了一个军团，{0}小时后才能申请加入军团!", new Object[] { Integer.valueOf(hour) }));
		} else if ((name.length() < 1) || (name.length() > LeagueData.LEAGUE_NAME_SIZE)) {
			b.setResult(false);
			b.setErrInfo(MessageFormat.format("军团名称长度必须在{0}字以内",
					new Object[] { Integer.valueOf(LeagueData.LEAGUE_NAME_SIZE) }));
		} else if ((type.getNumber() == 1) && (player.getJewels() < LeagueData.LEAGUE_CREATE_COST_JEWEL)) {
			b.setResult(false);
			b.setErrInfo("元宝不足");
		} else if ((type.getNumber() == 2) && (player.getMoney() < LeagueData.LEAGUE_CREATE_COST_MONEY)) {
			b.setResult(false);
			b.setErrInfo("银币不足");
		} else if (Platform.getLeagueManager().getLeagueByName(name) != null) {
			b.setResult(false);
			b.setErrInfo("该军团名字已被使用，请输入其他名称");
		} else if (player.getUnion().getLeagueId() > 0) {
			b.setResult(false);
			b.setErrInfo("您已加入一个军团!");
		} else {
			List<Integer> applys = player.getUnion().getApplys();
			if ((applys != null) && (applys.size() > 0)) {
				for (Integer lid : applys) {
					League league = Platform.getLeagueManager().getLeagueById(lid.intValue());
					if (league != null) {
						league.removeApply(player.getId());
					}
				}
				player.getUnion().getApplys().clear();
			}
			LeagueCreateAsyncCall call = new LeagueCreateAsyncCall(player, name, type.getNumber());
			Platform.getThreadPool().execute(call);
			return;
		}
		player.send(1178, b.build());
	}

	private void leagueApplicants(Player player) {
		PbDown.LeagueApplicantsRst.Builder b = PbDown.LeagueApplicantsRst.newBuilder().setResult(true);

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (l.getMember(player.getId()).isMember(l)) {
			b.setResult(false);
			b.setErrInfo("您无权进行该操作");
		} else {
			List ids = l.getInfo().getApplys();
			if (ids.size() > 0) {
				LeagueApplicantsAsyncCall call = new LeagueApplicantsAsyncCall(player, ids);
				Platform.getThreadPool().execute(call);
				return;
			}
		}
		player.send(1302, b.build());
	}

	private void myLeague(Player player) {
		PbDown.LeagueMyRst.Builder b = PbDown.LeagueMyRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l != null) && (l.isMember(player.getId()))) {
			MyLeagueAsyncCall call = new MyLeagueAsyncCall(player, l);
			Platform.getThreadPool().execute(call);
			return;
		}
		b.setResult(false);
		b.setErrInfo("您尚未加入军团!");
		player.send(1180, b.build());
	}

	private void updateNoitce(Player player, PbPacket.Packet packet) {
		PbDown.LeagueUpdateNoticeRst.Builder b = PbDown.LeagueUpdateNoticeRst.newBuilder().setResult(true);
		String notice = null;
		try {
			PbUp.LeagueUpdateNotice req = PbUp.LeagueUpdateNotice.parseFrom(packet.getData());
			notice = req.getNotice().trim();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1182, b.build());
			return;
		}
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (l.getMember(player.getId()).isMember(l)) {
			b.setResult(false);
			b.setErrInfo("您无权修改军团公告");
		} else if ((notice.length() < 1) || (notice.length() > LeagueData.LEAGUE_NOTICE_SIZE)) {
			b.setResult(false);
			b.setErrInfo(MessageFormat.format("军团公告长度必须在{0}字以内",
					new Object[] { Integer.valueOf(LeagueData.LEAGUE_NOTICE_SIZE) }));
		} else {
			l.setNotice(notice);
		}
		player.send(1182, b.build());
	}

	private void abdicate(Player player, PbPacket.Packet packet) {
		PbDown.LeagueAbdicateRst.Builder b = PbDown.LeagueAbdicateRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueAbdicate req = PbUp.LeagueAbdicate.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1184, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.getMember(player.getId()).isLeader(l))) {
			b.setResult(false);
			b.setErrInfo("只有团长才可以进行操作");
		} else if (player.getId() == id) {
			b.setResult(false);
			b.setErrInfo("不能对团长进行操作");
		} else if (!(l.isMember(id))) {
			b.setResult(false);
			b.setErrInfo("该玩家尚未加入军团");
		} else if (!(l.getMember(id).isViceLeader(l))) {
			b.setResult(false);
			b.setErrInfo("只能让位给副团长");
		} else {
			l.getInfo().getViceleaders().remove(new Integer(id));
			l.getInfo().getViceleaders().add(Integer.valueOf(l.getLeader()));
			l.setLeader(id);
			b.setName(l.getMember(l.getLeader()).getName());
			Platform.getLog().logLeague(l, "leagueabdicate", id);
		}
		player.send(1184, b.build());
	}

	private void appoint(Player player, PbPacket.Packet packet) {
		PbDown.LeagueAppointRst.Builder b = PbDown.LeagueAppointRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueAppoint req = PbUp.LeagueAppoint.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1186, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.getMember(player.getId()).isLeader(l))) {
			b.setResult(false);
			b.setErrInfo("只有团长才可以进行操作");
		} else if (player.getId() == id) {
			b.setResult(false);
			b.setErrInfo("不能对团长进行操作");
		} else if (!(l.isMember(id))) {
			b.setResult(false);
			b.setErrInfo("该玩家尚未加入军团");
		} else if (l.getMember(id).isViceLeader(l)) {
			l.getInfo().getViceleaders().remove(new Integer(id));

			Platform.getLog().logLeague(l, "leagueappointcancle", id);
		} else if (l.getInfo().getViceleaders().size() >= LeagueData.LEAGUE_VICELEADER_NUM) {
			b.setResult(false);
			b.setErrInfo("副团长数量已达上限，不能任命");
		} else {
			l.getInfo().getViceleaders().add(Integer.valueOf(id));

			Platform.getLog().logLeague(l, "leagueappoint", id);
		}

		player.send(1186, b.build());
	}

	private void kickout(Player player, PbPacket.Packet packet) {
		PbDown.LeagueKickoutRst.Builder b = PbDown.LeagueKickoutRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueKickout req = PbUp.LeagueKickout.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1188, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			LeagueKickoutAsyncCall call = new LeagueKickoutAsyncCall(player, l, id);
			Platform.getThreadPool().execute(call);
			return;
		}
		player.send(1188, b.build());
	}

	private void quit(Player player) {
		PbDown.LeagueQuitRst.Builder b = PbDown.LeagueQuitRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if ((l.getMember(player.getId()).isLeader(l)) && (l.getMemberCount() > 1)) {
			b.setResult(false);
			b.setErrInfo("团长不能退出军团，请让位后继续操作");
		} else {
			player.getUnion().quit();
			l.quit(player.getId(), 0);
			Platform.getLog().logLeague(l, "leaguequit", player.getId());
			if (l.getMemberCount() == 0) {
				Platform.getLeagueManager().removeLeague(l);
			}
		}
		player.send(1190, b.build());
	}

	private void apply(Player player, PbPacket.Packet packet) {
		PbDown.LeagueApplyRst.Builder b = PbDown.LeagueApplyRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueApply req = PbUp.LeagueApply.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1192, b.build());
			return;
		}

		League myLeague = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if (myLeague != null) {
			b.setResult(false);
			b.setErrInfo("您已加入一个军团!");
		} else {
			long coldTime = player.getUnion().getColdTime();
			if (coldTime > 0L) {
				int hour = (int) (coldTime / 1000L / 60L / 60L + 1L);
				b.setResult(false);
				b.setErrInfo(MessageFormat.format("您刚退出了一个军团，{0}小时后才能申请加入军团!", new Object[] { Integer.valueOf(hour) }));
			} else {
				League l = Platform.getLeagueManager().getLeagueById(id);
				if (l == null) {
					b.setResult(false);
					b.setErrInfo("您申请的军团不存在");
				} else if (player.getUnion().isApply(id)) {
					b.setResult(false);
					b.setErrInfo("您已申请过加入该军团");
				} else if (l.isFull()) {
					b.setResult(false);
					b.setErrInfo("该军团人数已达上限，请稍后再试!");
				} else if (l.isFullApply()) {
					b.setResult(false);
					b.setErrInfo("该军团申请人数过多，请稍后再试!");
				} else if (player.getUnion().isFullApplyNum()) {
					b.setResult(false);
					b.setErrInfo(MessageFormat.format("最多同时向{0}个军团发出申请",
							new Object[] { Integer.valueOf(LeagueData.LEAGUE_APPLY_NUM_LIMIT) }));
				} else {
					player.getUnion().apply(id);
					l.apply(player.getId());
					Platform.getLog().logLeague(l, "leagueapply", player.getId());
				}
			}
		}
		player.send(1192, b.build());
	}

	private void cancelApply(Player player, PbPacket.Packet packet) {
		PbDown.LeagueCancelApplyRst.Builder b = PbDown.LeagueCancelApplyRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueCancelApply req = PbUp.LeagueCancelApply.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1304, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(id);
		if (l == null) {
			b.setResult(false);
			b.setErrInfo("您申请的军团不存在");
		} else {
			l.removeApply(player.getId());
			player.getUnion().removeApply(id);
			Platform.getLog().logLeague(l, "leagueapplycancle", player.getId());
		}
		player.send(1304, b.build());
	}

	private void accept(Player player, PbPacket.Packet packet) {
		PbDown.LeagueAcceptRst.Builder b = PbDown.LeagueAcceptRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueAccept req = PbUp.LeagueAccept.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1194, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (l.getMember(player.getId()).isMember(l)) {
			b.setResult(false);
			b.setErrInfo("您无权进行该操作");
		} else {
			LeagueAcceptAsyncCall call = new LeagueAcceptAsyncCall(player.getSession(), l, id);
			Platform.getThreadPool().execute(call);
			return;
		}
		player.send(1194, b.build());
	}

	private void refuse(Player player, PbPacket.Packet packet) {
		PbDown.LeagueRefuseRst.Builder b = PbDown.LeagueRefuseRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueRefuse req = PbUp.LeagueRefuse.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1196, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (l.getMember(player.getId()).isMember(l)) {
			b.setResult(false);
			b.setErrInfo("您无权进行该操作");
		} else if (l.getApplyCount() < 1) {
			b.setResult(false);
			b.setErrInfo("无人申请加入军团");
		} else {
			List ids = new ArrayList();
			if (id > 0)
				ids.add(Integer.valueOf(id));
			else {
				ids.addAll(l.getInfo().getApplys());
			}
			LeagueRefuseAsyncCall call = new LeagueRefuseAsyncCall(player.getSession(), l, ids);
			Platform.getThreadPool().execute(call);
			return;
		}
		player.send(1196, b.build());
	}

	private void leagueActivity(Player player, PbPacket.Packet packet) {
		PbDown.LeagueAcitityRst.Builder b = PbDown.LeagueAcitityRst.newBuilder().setResult(true);

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			int[] arrayOfInt;
			int j = (arrayOfInt = LeagueData.LEAGUE_FACILITY).length;
			for (int i = 0; i < j; ++i) {
				Integer itemId = Integer.valueOf(arrayOfInt[i]);
				b.addFacilities(l.genLeagueFacility(player, itemId.intValue()));
			}
		}
		player.send(1198, b.build());
	}

	private void leagueBuildInfo(Player player) {
		PbDown.LeagueBuildInfoRst.Builder b = PbDown.LeagueBuildInfoRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			for (LeagueBuildData data : buildDatas.values()) {
				b.addInfos(data.genLeagueBuildInfo());
			}
			for (LeagueBuild bu : l.getInfo().getBuildRecords()) {
				b.addNotes(bu.getNotice());
			}
			b.setCanBuild(l.isActivity(l.getMember(player.getId()), 1));
			b.setCount(l.getInfo().getTodayBuildCount());
			b.setTotal(l.getMemberLimit());
		}
		player.send(1200, b.build());
	}

	private void leagueBuild(Player player, PbPacket.Packet packet) {
		PbDown.LeagueBuildRst.Builder b = PbDown.LeagueBuildRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueBuild req = PbUp.LeagueBuild.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1202, b.build());
			return;
		}

		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(buildDatas.containsKey(Integer.valueOf(id)))) {
			b.setResult(false);
			b.setErrInfo("没有该类型的军团建设");
		} else if (!(l.isActivity(l.getMember(player.getId()), 1))) {
			b.setResult(false);
			b.setErrInfo("今天已经建设过，请明天再来");
		} else {
			LeagueBuildData bd = (LeagueBuildData) buildDatas.get(Integer.valueOf(id));
			String errorMsg = bd.cost.check(player);
			if (errorMsg != null) {
				b.setResult(false);
				b.setErrInfo(errorMsg);
			} else {
				l.build(player, bd);
				bd.cost.remove(player, "leaguebuild");
				b.setValue(l.getBuildValue());
				Platform.getLog().logLeague(l, "leaguebuild", player.getId());
			}
		}
		player.send(1202, b.build());
	}

	private void leagueGoods(Player player) {
		PbDown.LeagueGoodsRst.Builder b = PbDown.LeagueGoodsRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			LeagueGoods goods = getGoods(l.getGoodsLevel());
			b.setGoods(goods.genLeagueGoods());
			b.setCost(LeagueData.LEAGUE_GET_GOODS_COST);
			b.setCanGet(l.isActivity(l.getMember(player.getId()), 3));
		}
		player.send(1204, b.build());
	}

	private void getGoods(Player player) {
		PbDown.LeagueGetGoodsRst.Builder b = PbDown.LeagueGetGoodsRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.isActivity(l.getMember(player.getId()), 3))) {
			b.setResult(false);
			b.setErrInfo("你今天已领取过物资");
		} else if (player.getUnion().getContribution() < LeagueData.LEAGUE_GET_GOODS_COST) {
			b.setResult(false);
			b.setErrInfo("贡献不足，无法领取物资");
		} else {
			LeagueGoods goods = getGoods(l.getGoodsLevel());
			l.getGoods(player, goods);
			Platform.getLog().logLeague(l, "leaguegetgoods", player.getId());
		}
		player.send(1210, b.build());
	}

	private void leagueRareShop(Player player) {
		PbDown.LeagueRareGoodsRst.Builder b = PbDown.LeagueRareGoodsRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			for (LeagueRareGoods goods : l.getInfo().getRareGoods().values()) {
				b.addGoods(goods.genLeagueRareGoods(player.getId()));
			}
		}
		player.send(1212, b.build());
	}

	private void leagueNormalShop(Player player) {
		PbDown.LeagueNormalGoodsRst.Builder b = PbDown.LeagueNormalGoodsRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			LeagueMember lm = l.getMember(player.getId());
			for (LeagueNormalGoods goods : normalGoods.values()) {
				b.addGoods(goods.genLeagueNormalGoods(lm));
			}
		}
		player.send(1214, b.build());
	}

	private void exchangeRareGoods(Player player, PbPacket.Packet packet) {
		PbDown.LeagueExchangeRareGoodsRst.Builder b = PbDown.LeagueExchangeRareGoodsRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.LeagueExchangeRareGoods req = PbUp.LeagueExchangeRareGoods.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1216, b.build());
			return;
		}
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			LeagueRareGoods goods = (LeagueRareGoods) l.getInfo().getRareGoods().get(Integer.valueOf(id));
			if (goods == null) {
				b.setResult(false);
				b.setErrInfo("兑换数据不存在");
			} else if (goods.getCount() <= 0) {
				b.setResult(false);
				b.setErrInfo("该商品已兑换完，请下次再来");
			} else if (!(goods.canBuy(player.getId()))) {
				b.setResult(false);
				b.setErrInfo("您已购买过该商品");
			} else if (player.getUnion().getContribution() < goods.getNeedContribution()) {
				b.setResult(false);
				b.setErrInfo("贡献不足，无法兑换");
			} else {
				goods.exchange(player);
			}
		}
		player.send(1216, b.build());
	}

	private void exchangeNormalGoods(Player player, PbPacket.Packet packet) {
		PbDown.LeagueExchangeNormalGoodsRst.Builder b = PbDown.LeagueExchangeNormalGoodsRst.newBuilder()
				.setResult(true);
		int id = 0;
		try {
			PbUp.LeagueExchangeNormalGoods req = PbUp.LeagueExchangeNormalGoods.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1218, b.build());
			return;
		}
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else {
			LeagueNormalGoods goods = (LeagueNormalGoods) normalGoods.get(Integer.valueOf(id));
			LeagueMember lm = l.getMember(player.getId());
			if (goods == null) {
				b.setResult(false);
				b.setErrInfo("兑换数据不存在");
			} else if (l.getShopLevel() < goods.shopLevel) {
				b.setResult(false);
				b.setErrInfo("军团等级尚未达到，无法兑换");
			} else if (lm.getSurplusCount(id) <= 0) {
				b.setResult(false);
				b.setErrInfo("该商品已兑换完，请下次再来");
			} else if (player.getUnion().getContribution() < goods.cost) {
				b.setResult(false);
				b.setErrInfo("贡献不足，无法兑换");
			} else {
				lm.exchange(player, goods);
			}
		}
		player.send(1218, b.build());
	}

	private void levelUp(Player player, PbPacket.Packet packet) {
		PbDown.LeagueFacilityLevelUpRst.Builder b = PbDown.LeagueFacilityLevelUpRst.newBuilder().setResult(true);
		PbLeague.LeagueFacilityItem item = null;
		try {
			PbUp.LeagueFacilityLevelUp req = PbUp.LeagueFacilityLevelUp.parseFrom(packet.getData());
			item = req.getItem();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1220, b.build());
			return;
		}
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (l.getMember(player.getId()).isMember(l)) {
			b.setResult(false);
			b.setErrInfo("您无权进行该操作");
		} else if (item == null) {
			b.setResult(false);
			b.setErrInfo("军团设施不存在");
		} else if (!(l.isOpenFacility(item.getNumber()))) {
			b.setResult(false);
			b.setErrInfo("该军团设施尚未开放");
		} else if ((item.getNumber() == 1) && (l.isMaxLevel())) {
			b.setResult(false);
			b.setErrInfo("军团大厅已达最大等级");
		} else if ((item.getNumber() != 1) && (l.getFacilityLevel(item.getNumber()) >= l.getLevel())) {
			b.setResult(false);
			b.setErrInfo("其他设施等级不能超过大厅等级");
		} else if (l.getBuildValue() < l.getNeedExpToNextLevel(item.getNumber())) {
			b.setResult(false);
			b.setErrInfo("军团建设不足");
		} else {
			l.levelUp(item.getNumber(), player);
			b.setValue(l.getBuildValue());
			b.setLimit(l.getMemberLimit());
		}
		player.send(1220, b.build());
	}

	private void bossInfo(Player player) {
		PbDown.LeagueBossInfoRst.Builder b = PbDown.LeagueBossInfoRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.isOpenBoss())) {
			b.setResult(false);
			b.setErrInfo("军团boss尚未开启");
		} else {
			b.setBoss(l.getInfo().getBoss().genLeagueBoss(l));
			b.setCodeTime(player.getUnion().getFightBossColdTime());
			b.setNum(player.getUnion().getBossSurplusNum());
			int count = l.getInfo().getBossweekCount();
			int times = l.getInfo().getBossweekCount();
			int nextReward = -1;
			int curReward = -1;
			for (Integer t : bossweekRewards.keySet()) {
				nextReward = t.intValue();
				if (times < t.intValue())
					break;
				curReward = t.intValue();
			}
			b.setCount(count);
			b.setNext((count >= nextReward) ? 0 : nextReward - count);
			if (curReward > 0) {
				for (Reward r : (ArrayList<Reward>) bossweekRewards.get(Integer.valueOf(curReward))) {
					b.addRewards(r.genPbReward());
				}
			}
		}
		player.send(1308, b.build());
	}

	private void bossChallenge(Player player) {
		PbDown.LeagueBossChallengeRst.Builder b = PbDown.LeagueBossChallengeRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.isOpenBoss())) {
			b.setResult(false);
			b.setErrInfo("军团boss尚未开启");
		} else if (player.getUnion().getBossSurplusNum() < 1) {
			b.setResult(false);
			b.setErrInfo("挑战次数不足，请明日再来");
		} else if (player.getUnion().getFightBossColdTime() > 0L) {
			b.setResult(false);
			b.setErrInfo("正在冷却，不能挑战boss");
		} else if (l.getInfo().getBoss().getReviveTime() > 0L) {
			b.setResult(false);
			b.setErrInfo("boss尚未复活，请稍等");
		} else {
			int bossLevel = l.getInfo().getBoss().level;

			MapTemplate mt = MapService.getSpecialMapTemplate(11);
			StageTemplate st = mt.stageTemplates[0];
			LeagueBossStage stage = new LeagueBossStage(st.channels[0].getPositionInfo(), mt.name, st.secenId, player,
					l.getInfo().getBoss().boss, l);
			stage.init();
			stage.combat(player);
			stage.proccessReward(player);

			if (stage.isWin()) {
				LeagueInfo linfo = l.getInfo();
				linfo.setBossweekCount(linfo.getBossweekCount() + 1);

				LeagueBoss boss = linfo.getBoss();
				List rewards = (List) this.bosstimeRewards.get(Integer.valueOf(bossLevel));
				Set set = l.getInfo().getMembers().keySet();
				for (Iterator localIterator = set.iterator(); localIterator.hasNext();) {
					int playerId = ((Integer) localIterator.next()).intValue();
					MailService.sendSystemMail(19, playerId,
							MessageFormat.format("军团Boss Lv.{0}击杀奖励", new Object[] { Integer.valueOf(bossLevel) }),
							MessageFormat.format(
									"<p style=21>您的所在军团</p><p style=20>【{0}】</p><p style=21>又一次击败了军团Boss，现为您颁发\"{1} Lv.{2}击杀奖励\"：</p>",
									new Object[] { l.getName(), boss.boss.getName(), Integer.valueOf(bossLevel) }),
							null, rewards);
				}
			}

			b.setResult(true).setWin(stage.isWin()).setDamage(stage.damage).setMoney(stage.reward)
					.setStageInfo(stage.getInfoBuilder()).setStageRecord(stage.getRecordUtil().getStageRecord());
			Platform.getLog().logLeagueBoss(player, stage.damage, (stage.isWin()) ? 1 : 0);
		}
		player.send(1310, b.build());
	}

	private void bossRank(Player player) {
		PbDown.LeagueBossRankRst.Builder b = PbDown.LeagueBossRankRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.isOpenBoss())) {
			b.setResult(false);
			b.setErrInfo("军团boss尚未开启");
		} else {
			LeagueBossRankAsyncCall call = new LeagueBossRankAsyncCall(player.getSession(), l);
			Platform.getThreadPool().execute(call);
			return;
		}
		player.send(1312, b.build());
	}

	private void bossRankReward(Player player) {
		PbDown.LeagueBossRankRewardViewRst.Builder b = PbDown.LeagueBossRankRewardViewRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l == null) || (!(l.isMember(player.getId())))) {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		} else if (!(l.isOpenBoss())) {
			b.setResult(false);
			b.setErrInfo("军团boss尚未开启");
		} else {
			int bossLevel = l.getInfo().getBoss().level;
			Map<Integer, Object> cur = (Map) bossDayRewards.get(Integer.valueOf(bossLevel));
			if ((cur != null) && (cur.size() > 0)) {
				for (Integer rank : cur.keySet()) {
					List list = (List) cur.get(rank);
					b.addCurLevel(LeagueBoss.genLeagueBossRankReward(rank.intValue(), list));
				}
			}
			int nextLevel = bossLevel + 1;
			if (nextLevel <= 20) {
				Map<Integer, Object> next = (Map) bossDayRewards.get(Integer.valueOf(nextLevel));
				if ((next != null) && (next.size() > 0)) {
					for (Integer rank : next.keySet()) {
						List list = (List) next.get(rank);
						b.addNextLevel(LeagueBoss.genLeagueBossRankReward(rank.intValue(), list));
					}
				}
			}
		}
		player.send(1314, b.build());
	}

	private void myMiniLeague(Player player) {
		PbDown.LeagueMyMiniRst.Builder b = PbDown.LeagueMyMiniRst.newBuilder().setResult(true);
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		if ((l != null) && (l.isMember(player.getId()))) {
			b.setLeague(l.genMyMiniLeague());
		} else {
			b.setResult(false);
			b.setErrInfo("您尚未加入军团!");
		}
		player.send(1324, b.build());
	}

	public static LeagueBuildData getBuildData(int id) {
		return ((LeagueBuildData) buildDatas.get(Integer.valueOf(id)));
	}

	public static LeagueGoods getGoods(int level) {
		return ((LeagueGoods) goods.get(Integer.valueOf(level)));
	}

	public static int getBossFightRewards(int level) {
		if (level > LeagueData.LEAGUE_BOSS_FIGHT_MONEY.length) {
			return LeagueData.LEAGUE_BOSS_FIGHT_MONEY[(LeagueData.LEAGUE_BOSS_FIGHT_MONEY.length - 1)];
		}
		return LeagueData.LEAGUE_BOSS_FIGHT_MONEY[(level - 1)];
	}

	public static int getBossId(int level) {
		if (level > LeagueData.LEAGUE_BOSS_ID.length) {
			return LeagueData.LEAGUE_BOSS_ID[(LeagueData.LEAGUE_BOSS_ID.length - 1)];
		}
		return LeagueData.LEAGUE_BOSS_ID[(level - 1)];
	}

	public static List<Reward> getBossDayReward(int bossLevel, int rank) {
		Map map = (Map) bossDayRewards.get(Integer.valueOf(bossLevel));
		if (map != null) {
			if (map.containsKey(Integer.valueOf(rank))) {
				return ((List) map.get(Integer.valueOf(rank)));
			}
			return ((List) map.get(Integer.valueOf(-1)));
		}
		return null;
	}

	public static int getBossBuff(int bossFacilityLevel) {
		if (bossBuff.containsKey(Integer.valueOf(bossFacilityLevel))) {
			return ((Integer) bossBuff.get(Integer.valueOf(bossFacilityLevel))).intValue();
		}
		return ((Integer) bossBuff.get(Integer.valueOf(0))).intValue();
	}

	public static LeagueNormalGoods getNormalGoods(int id) {
		return ((LeagueNormalGoods) normalGoods.get(Integer.valueOf(id)));
	}

	public static int getLimit(int level) {
		if (limit.containsKey(Integer.valueOf(level))) {
			return ((Integer) limit.get(Integer.valueOf(level))).intValue();
		}
		return ((Integer) limit.get(Integer.valueOf(0))).intValue();
	}

	public static long getToday0Time(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		return cal.getTimeInMillis();
	}

	public void shutdown() {
		Platform.getLeagueManager().saveAll();
	}

	public void reload() throws Exception {
	}

	public void handleEvent(Event event) {
		League l;
		Iterator localIterator;
		if (event.type == 2032) {
			for (localIterator = Platform.getLeagueManager().getLeagues().values().iterator(); localIterator
					.hasNext();) {
				l = (League) localIterator.next();
				if (l != null)
					l.refreshRareGoods();
			}
		} else if (event.type == 2086) {
			for (localIterator = Platform.getLeagueManager().getLeagues().values().iterator(); localIterator
					.hasNext();) {
				l = (League) localIterator.next();
				if (l != null)
					l.refreshBoss();
			}
		} else if (event.type == 1002) {
			for (localIterator = Platform.getLeagueManager().getLeagues().values().iterator(); localIterator
					.hasNext();) {
				l = (League) localIterator.next();
				if (l != null) {
					l.refreshNewDay();
				}
			}
			Platform.getLeagueManager().recall();
		} else if (event.type == 1013) {
			Platform.getLeagueManager().refreshLottery();
		} else if (event.type == 1014) {
			for (localIterator = Platform.getLeagueManager().getLeagues().values().iterator(); localIterator
					.hasNext();) {
				l = (League) localIterator.next();
				if (l != null)
					l.refreshNewWeek();
			}
		}
	}
}
