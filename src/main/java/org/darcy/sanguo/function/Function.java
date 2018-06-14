package org.darcy.sanguo.function;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.ActivityRecord;
import org.darcy.sanguo.activity.item.TurnPlateAI;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.coup.CoupRecord;
import org.darcy.sanguo.destiny.DestinyRecord;
import org.darcy.sanguo.destiny.DestinyTemplate;
import org.darcy.sanguo.glory.GloryRecord;
import org.darcy.sanguo.hero.Formation;
import org.darcy.sanguo.item.Debris;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.map.ClearMap;
import org.darcy.sanguo.map.MapRecord;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.reward.CookWine;
import org.darcy.sanguo.reward.DrawMoney;
import org.darcy.sanguo.reward.GrowReward;
import org.darcy.sanguo.reward.LevelReward;
import org.darcy.sanguo.reward.OnlineReward;
import org.darcy.sanguo.reward.RewardRecord;
import org.darcy.sanguo.reward.TimeLimitItem;
import org.darcy.sanguo.reward.TimeLimitReward;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.service.PayService;
import org.darcy.sanguo.service.RewardService;
import org.darcy.sanguo.service.VipService;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.Union;
import org.darcy.sanguo.vip.Vip;

import sango.packet.PbCommons;
import sango.packet.PbDown;

public class Function {
	public static final int FUNCTION_STAGE = 1;
	public static final int FUNCTION_HERO_ADVANCE = 2;
	public static final int FUNCTION_STAND = 3;
	public static final int FUNCTION_RECRUIT = 4;
	public static final int FUNCTION_REWARD_LEVEL = 5;
	public static final int FUNCTION_TASK = 6;
	public static final int FUNCTION_MAIN_WARRIOR = 7;
	public static final int FUNCTION_COUP = 8;
	public static final int FUNCTION_JIAOCHANG = 9;
	public static final int FUNCTION_LOOT_TREASURE = 10;
	public static final int FUNCTION_SIGN = 11;
	public static final int FUNCTION_ARENA = 12;
	public static final int FUNCTION_MAP_PRO = 13;
	public static final int FUNCTION_RANDOM_SHOP = 14;
	public static final int FUNCTION_STAR = 15;
	public static final int FUNCTION_LEAGUE = 16;
	public static final int FUNCTION_DESTINY = 17;
	public static final int FUNCTION_MAP_ACTIVITY = 18;
	public static final int FUNCTION_EQUIP_POLISH = 19;
	public static final int FUNCTION_TOWER = 20;
	public static final int FUNCTION_WORLDCOMPETITION = 21;
	public static final int FUNCTION_TREASURE_ENHANCE = 22;
	public static final int FUNCTION_DIVINE = 23;
	public static final int FUNCTION_BOSS = 24;
	public static final int FUNCTION_COOKWINE = 25;
	public static final int FUNCTION_2_TIMES = 26;
	public static final int FUNCTION_3_TIEMS = 27;
	public static final int FUNCTION_MULTICHALLENGE = 28;
	public static final int FUNCTION_TOUCHGOLDEN = 29;
	public static final int FUNCTION_REWARD_LOGIN = 30;
	public static final int FUNCTION_ACTIVITY_EXCHANGE = 31;
	public static final int FUNCTION_REWARD_GROW = 32;
	public static final int FUNCTION_LOGIN_AWARD = 33;
	public static final int FUNCTION_DRAW_MONEY = 34;
	public static final int FUNCTION_REWARD_CODE = 35;
	public static final int FUNCTION_REWARD_FIRST_CHARGE = 36;
	public static final int FUNCTION_CHARGE_REWARD = 37;
	public static final int FUNCTION_COST_REWARD = 38;
	public static final int FUNCTION_WARRIOR_INTENSIFY = 39;
	public static final int FUNCTION_EQUIPMENT_INTENSIFY = 40;
	public static final int FUNCTION_NEW_WARRIOR_ON_STAGE = 41;
	public static final int FUNCTION_TURNPLATE = 42;
	public static final int FUNCTION_PRAY = 43;
	public static final int FUNCTION_LIFE_MEMBER = 44;
	public static final int FUNCTION_GLORY = 45;
	public static final int FUNCTION_DAY_REWARD = 46;
	public static final int FUNCTION_DAY_CHARGE_REWARD = 47;
	public static final int FUNCTION_TOUCH_INFO = 48;
	public static final int FUNCTION_GET_TRAIN_POINTS = 49;
	public static final int FUNCTION_TRAIN = 50;
	public static final int FUNCTION_SUPER_REWARD = 51;
	public static final int FUNCTION_GLOBAL_CHARGE_REWARD = 52;
	public static final int FUNCTION_7DAY_BTLCAPA = 53;
	public static final int FUNCTION_MONTH_CARD = 54;
	public static final int FUNCTION_QUARTER_CARD = 55;
	public static final int FUNCTION_EQUIPMENT_FORGE = 56;
	public static final int FUNCTION_PERSIST_CHARGE = 61;
	public static final int FUNCTION_TIMELIMIT_REWARD_JP = 67;
	public static final int FUNCTION_STAR_MUITGIVE = 75;
	public static final int FUNCTION_STARCATALOG = 68;
	public static final int FUNCTION_STARCATALOG_INCREASEFAVOR = 69;
	public int id;
	public String name;
	public boolean isShow;
	public int iconId;
	public int openLevel;
	public String openContent;
	public String needContent;
	public int jump;

	public static int getActivityCanGetNum(int id, Player player) {
		int count = 0;
		int score;
		boolean isAllReward;
		GrowReward gr;
		int i;
		boolean[] info;
		if (id == 25) {
			CookWine cw = RewardService.getCurCookwine();
			if (cw == null) {
				return 0;
			}
			if (!(player.getRewardRecord().isRecover(cw.getId()))) {
				return 1;
			}
			return 0;
		}
		if (id == 11) {
			if (player.getRewardRecord().isGetSignReward()) {
				return 0;
			}
			return 1;
		}
		if (id == 5) {
			RewardRecord rr = player.getRewardRecord();
			Iterator itx = RewardService.levelRewards.keySet().iterator();
			isAllReward = true;
			int canReward = 0;
			while (itx.hasNext()) {
				int tmpId = ((Integer) itx.next()).intValue();
				if (!(rr.getLevelRewardIds().contains(Integer.valueOf(tmpId)))) {
					isAllReward = false;
					LevelReward lr = (LevelReward) RewardService.levelRewards.get(Integer.valueOf(tmpId));
					if (player.getLevel() >= lr.level) {
						++canReward;
					}
				}
			}
			if (isAllReward) {
				return -1;
			}
			return canReward;
		}
		if (id == 30) {
			boolean[][] infos = player.getRewardRecord().getLoginInfo();
			isAllReward = true;
			for (i = 0; i < infos.length; ++i) {
				info = infos[i];
				if (info[0]) {
					if (!info[1]) {
						isAllReward = false;
						++count;
					}
				} else {
					isAllReward = false;
					break;
				}
			}
			if (isAllReward) {
				return -1;
			}
			return count;
		}
		if (id == 29)
			return Math.max(player.getVip().trainTimes - player.getRewardRecord().getTrainCount(), 0);
		if (id == 31) {
			if (ActivityInfo.isOpenActivity(1, player)) {
				return 0;
			}
			return -1;
		}
		if (id == 32) {
			if (!(player.getRewardRecord().isBuyGrowReward())) {
				return 0;
			}
			count = 0;
			boolean flag = true;
			for (Iterator it = RewardService.growRewards.values().iterator(); it.hasNext();) {
				gr = (GrowReward) it.next();
				if (player.getLevel() >= gr.level) {
					if (!(player.getRewardRecord().isGetGrowReward(gr.level)))
						++count;
				} else {
					flag = false;
				}
			}
			if ((count == 0) && (flag)) {
				return -1;
			}
			return count;
		}
		if (id == 33) {
			if (ActivityInfo.isOpenActivity(2, player)) {
				count = 0;
				Iterator it = player.getRewardRecord().getLoginAwardInfos().values().iterator();
				while (true) {
					boolean isGet = ((Boolean) it.next()).booleanValue();
					if (!(isGet))
						++count;
					if (!(it.hasNext())) {
						return count;
					}
				}
			}
			return -1;
		}
		if (id == 34) {
			if (player.getRewardRecord().isOpenDrawMoney(player)) {
				if (player.getRewardRecord().hasMoreDrawMoney()) {
					DrawMoney dm = (DrawMoney) RewardService.drawMoneys
							.get(Integer.valueOf(player.getRewardRecord().getDrawMoneyCount() + 1));
					if (player.getJewels() >= dm.cost) {
						return 1;
					}
				}
				return 0;
			}
			return -1;
		}
		if (id == 35)
			return 0;
		if (id == 36) {
			if (player.getPool().getBool(8, false))
				return -1;
			if (player.isAlreadyCharge()) {
				return 1;
			}
			return 0;
		}
		if (id == 37) {
			if (ActivityInfo.isOpenActivity(3, player)) {
				return player.getActivityRecord().getCanGetChargeRewardNum(player);
			}
			return -1;
		}
		if (id == 38) {
			if (ActivityInfo.isOpenActivity(4, player)) {
				return player.getActivityRecord().getCanGetCostRewardNum(player);
			}
			return -1;
		}
		if (id == 42) {
			if (ActivityInfo.isOpenActivity(9, player)) {
				TurnPlateAI ai = (TurnPlateAI) ActivityInfo.getItem(player, 9);
				if (ai != null) {
					Iterator it = ai.scoreRewards.keySet().iterator();
					while (true) {
						score = ((Integer) it.next()).intValue();
						if ((!(player.getActivityRecord().isGetTurnPlateSocreReward(score)))
								&& (player.getActivityRecord().getTurnPlateTotalCount() >= score))
							return 1;
						if (!(it.hasNext())) {
							return 0;
						}
					}
				}
			}
			return -1;
		}
		if (id == 43) {
			if (ActivityInfo.isOpenActivity(10, player)) {
				return player.getActivityRecord().getPraySurplus();
			}
			return -1;
		}
		if (id == 44)
			return 0;
		if (id == 47) {
			if (ActivityInfo.isOpenActivity(12, player)) {
				return player.getActivityRecord().getCanGetDayChargeRewardNum(player);
			}
			return -1;
		}
		if (id == 46) {
			ActivityRecord ar = player.getActivityRecord();
			if (ar.dayRewardIsOver()) {
				return -1;
			}
			return (ar.getDayRewardCount() - ar.getDayRewardGet().size());
		}
		if (id == 51) {
			for (Iterator it = player.getRewardRecord().getSuperRewardRecord().values().iterator(); it.hasNext();) {
				boolean isGet = ((Boolean) it.next()).booleanValue();
				if (!(isGet)) {
					return 1;
				}
			}
			if (player.getRewardRecord().getSuperRewardRecord().size() < RewardService.superReward.size()) {
				return 0;
			}
			return -1;
		}
		if (id == 52) {
			Set rewards = player.getRewardRecord().getGlobalChargeRewards();
			if (rewards.size() >= RewardService.globalChargeRewards.size()) {
				return -1;
			}
			int chargeNum = PayService.getChargePlayerCount();
			count = 0;
			Iterator it = RewardService.globalChargeRewards.keySet().iterator();
			while (true) {
				int chargeCount = ((Integer) it.next()).intValue();
				if ((chargeNum >= chargeCount) && (!(player.getRewardRecord().isGetGlobalChargeReward(chargeCount))))
					++count;
				if (!(it.hasNext())) {
					return count;
				}
			}
		}
		if (id == 53) {
			if (!(player.getPool().getBool(26, false))) {
				return -1;
			}
			if (ActivityService.getRank7ActivityLeftTime() > -1L) {
				return 0;
			}
			if ((!(((ActivityService) Platform.getServiceManager().get(ActivityService.class))
					.hasGetRank7Reward(player))) && (ActivityService.getRank7ActivityLeftTime() == -1L)
					&& (!(ActivityService.rank7CMRewardCanceled()))) {
				return 1;
			}
			return -1;
		}
		if ((id != 54) && (id != 55) && (id == 61)) {
			if (ActivityInfo.isOpenActivity(13, player)) {
				return player.getActivityRecord().getCanGetPersistChargeNum(player);
			}
			return -1;
		}

		return -1;
	}

	public static int getMainInterfaceFunctionNum(Player player, int funcId) {
		int num;
		int i;
		Object cm;
		Debris d;
		if (funcId == 1)
			return player.getMails().getMailNotReadCount(1);
		if (funcId == 2) {
			DestinyRecord dr = player.getDestinyRecord();
			DestinyTemplate dt = dr.getNextDestiny();
			if ((dt != null) && (dr.getLeftStars() >= dt.starCost)) {
				return 1;
			}

			return 0;
		}
		if (funcId == 3) {
			num = Formation.getOpenStageNum(player.getLevel());
			if (player.getWarriors().getWarriorsCount() < num) {
				return (num - player.getWarriors().getWarriorsCount());
			}
			return 0;
		}
		if (funcId == 31) {
			num = Formation.getOpenFellowNum(player.getLevel());
			if (player.getWarriors().getFellowCount() < num) {
				return (num - player.getWarriors().getFellowCount());
			}
			return 0;
		}
		if ((funcId == 4) || (funcId == 5)) {

			int type = (funcId == 4) ? 1 : 2;
			List list = player.getBags().getBag(3).getGrids();
			Iterator localIterator = list.iterator();
			while (true) {
				BagGrid grid = (BagGrid) localIterator.next();
				d = (Debris) grid.getItem();
				if ((((DebrisTemplate) d.getTemplate()).debrisType == type) && (d.canCompound(player) == null))
					return 1;
				if (!(localIterator.hasNext())) {
					return 0;
				}
			}
		}
		if (funcId == 6)
			return 0;
		if (funcId == 7) {
			num = player.getRelations().getRelation(1).getPlayerIds().size();
			if (num > 0) {
				return num;
			}
			return 0;
		}
		if (funcId == 29) {
			num = player.getRelations().getGivenStaminas().size();
			int getNum = player.getRelations().getLeftStaminaTimes();
			num = Math.min(num, getNum);
			if (num > 0) {
				return num;
			}
			return 0;
		}
		if ((funcId == 8) || (funcId == 16) || (funcId == 17) || (funcId == 18) || (funcId == 20) || (funcId == 21)
				|| (funcId == 22) || (funcId == 23) || (funcId == 24) || (funcId == 33) || (funcId == 35)
				|| (funcId == 34) || (funcId == 36) || (funcId == 38) || (funcId == 41) || (funcId == 42)
				|| (funcId == 46)) {
			Integer id;
			int[] arrayOfInt;
			int func = 0;
			if (funcId == 8)
				func = 11;
			else if (funcId == 16)
				func = 25;
			else if (funcId == 17)
				func = 5;
			else if (funcId == 18)
				func = 30;
			else if (funcId == 20)
				func = 32;
			else if (funcId == 21)
				func = 33;
			else if (funcId == 22)
				func = 36;
			else if (funcId == 23)
				func = 37;
			else if (funcId == 24)
				func = 38;
			else if (funcId == 33)
				func = 43;
			else if (funcId == 35)
				func = 47;
			else if (funcId == 34)
				func = 42;
			else if (funcId == 36)
				func = 46;
			else if (funcId == 38)
				func = 34;
			else if (funcId == 41)
				func = 52;
			else if (funcId == 42)
				func = 53;
			else if (funcId == 46) {
				func = 61;
			}
			boolean flag = false;
			int len = ActivityService.activityList.length;
			for (i = 0; i < len; ++i) {
				id = Integer.valueOf(ActivityService.activityList[i]);
				if (func == id.intValue()) {
					flag = true;
					break;
				}
			}
			if (!(flag)) {
				len = ActivityService.saleList.length;
				for (i = 0; i < len; ++i) {
					id = Integer.valueOf(ActivityService.activityList[i]);
					if (func == id.intValue()) {
						flag = true;
						break;
					}
				}
			}

			if (flag) {
				num = getActivityCanGetNum(func, player);
				return Math.max(0, num);
			}
			return 0;
		}
		if (funcId == 9)
			return player.getTaskRecord().getFinishedTasks().size();
		if (funcId == 10)
			return player.getMails().getMailNotReadCount(2);
		if (funcId == 11)
			return player.getTowerRecord().getLeftFreeResetTimes();
		if (funcId == 12) {
			num = player.getDivineRecord().getLeftDivineTimes();
			if (num > 0) {
				return num;
			}
			return player.getDivineRecord().getRewardsCount();
		}
		if (funcId == 13) {
			boolean treasureTrial = MapRecord.isOpenActivity(4);
			boolean warriorTrial = MapRecord.isOpenActivity(3);
			num = player.getMapRecord().getMoneyMapLeftTimes();
			if (warriorTrial) {
				num = Math.max(player.getMapRecord().getWarriorMapLeftTimes(), num);
			}
			if (treasureTrial) {
				num = Math.max(num, player.getMapRecord().getTreasureMapLeftTimes());
			}
			return num;
		}
		if (funcId == 19) {
			if (Platform.getBossManager().isOpen()) {
				return 1;
			}
			return 0;
		}
		if (funcId == 26) {
			CoupRecord r = player.getCoupRecord();
			int count = 0;
			i = 0;
			while (true) {
				int id = i + 1;
				if (r.canOptCoup(id, player))
					++count;
				++i;
				if (i >= 4) {
					label938: return count;
				}
			}
		}
		if (funcId == 27) {
			MapRecord r = player.getMapRecord();
			List clearMaps = r.getClearMaps();
			i = 0;
			while (true) {
				cm = (ClearMap) clearMaps.get(i);
				if (((ClearMap) cm).hasGotStarReward())
					return 1;
				++i;
				if (i >= clearMaps.size()) {
					label994: return 0;
				}
			}
		}
		if (funcId == 28) {
			return 0;
		}
		if (funcId == 30) {
			int count = 0;
			Set set = player.getPool().getIntegers(9);
			cm = VipService.vips.iterator();
			while (true) {
				Vip vip = (Vip) ((Iterator) cm).next();
				if ((vip.level <= player.getVip().level) && (!(set.contains(Integer.valueOf(vip.level))))
						&& (vip.vipBagId != -1))
					++count;
				if (!(((Iterator) cm).hasNext())) {
					label1102: return count;
				}
			}
		}
		if (funcId == 25) {
			if ((player.getVip().dayRewards == null) || (player.getPool().getBool(7, false))) {
				return 0;
			}
			return 1;
		}
		if (funcId == 32) {
			GloryRecord gr = player.getGloryRecord();
			return (gr.getGlories().size() / 3 - gr.getUsed().size());
		}
		if (funcId == 37) {
			Union u = player.getUnion();
			if ((u != null) && (u.getLeagueId() > 0)) {
				League l = Platform.getLeagueManager().getLeagueById(u.getLeagueId());
				if ((l != null) && (l.isMember(player.getId())) && (l.isActivity(l.getMember(player.getId())))) {
					return 1;
				}
			}

			return 0;
		}
		if (funcId == 40) {
			if (!(player.isAlreadyCharge())) {
				return 1;
			}
			return -1;
		}
		if (funcId == 44) {
			RewardRecord rr = player.getRewardRecord();
			if (rr.getLogin7DayGet().size() >= 7) {
				return -1;
			}
			return (rr.getLogin7DayCount() - rr.getLogin7DayGet().size());
		}
		return 0;
	}

	public static void notify(Player player, int[] funcs) {
		PbDown.MainInterfaceRst.Builder b = PbDown.MainInterfaceRst.newBuilder();
		if ((funcs != null) && (funcs.length > 0)) {
			label518: for (int func : funcs) {
				PbCommons.MainInterfaceInfo.Builder builder = null;
				if (func == 14) {
					long betterTime = player.getRecruitRecord().getRestTimeToNextFree(2);
					long bestTime = player.getRecruitRecord().getRestTimeToNextFree(3);
					if ((betterTime <= 0L) || (bestTime <= 0L)) {
						builder = genMainInterfaceInfo(func, 1);
					} else {
						builder = genMainInterfaceInfo(func, 0);
						builder.setRecruitTime(Math.min(betterTime, bestTime));
					}
				} else {
					int leftTime;
					if (func == 15) {
						leftTime = player.getRandomShop().getLeftFreeTimes();
						if (leftTime > 0) {
							builder = genMainInterfaceInfo(func, 1);
						} else {
							builder = genMainInterfaceInfo(func, 0);
							builder.setRandomShopTime(player.getRandomShop().getLeftaAddSeconds() * 1000);
						}
					} else if (func == 47) {
						leftTime = player.getRandomShop().getLeftFreeTimes();
						if (leftTime > 0) {
							builder = genMainInterfaceInfo(func, 1);
						} else {
							builder = genMainInterfaceInfo(func, 0);
							builder.setRandomShopTime(player.getRandomShop().getLeftCherishRefreshSeconds() * 1000);
						}
					} else {
						long now;
						long surplus;
						if (func == 39) {
							if (player.getRewardRecord().getOnlineRewardCount() > 0) {
								OnlineReward or = RewardService
										.getOnlineReward(player.getRewardRecord().getOnlineRewardCount());
								if (or != null) {
									long last = player.getRewardRecord().getLastGetOnlineReward();
									now = System.currentTimeMillis();
									surplus = last + or.time - now;
									if (surplus > 0L) {
										builder = genMainInterfaceInfo(func, 0);
										builder.setOnlineReward(surplus);
									} else {
										builder = genMainInterfaceInfo(func, 1);
									}
								} else {
									builder = genMainInterfaceInfo(func, -1);
								}
							} else {
								builder = genMainInterfaceInfo(func, -1);
							}
						} else if (func == 43) {
							boolean flag = true;
							TimeLimitReward tlr = player.getRewardRecord().getCurTimeLimitReward();
							if (tlr != null) {
								boolean hasCount = player.getRewardRecord().getTimeLimitRewardCount() < tlr.count;
								if (!(hasCount)) {
									Map re = player.getRewardRecord().buyRecord(Long.valueOf(tlr.id));
									if (re != null) {
										for (TimeLimitItem item : tlr.items.values()) {
											if (re.get(Integer.valueOf(item.id)) != null) {
												if (((Integer) re.get(Integer.valueOf(item.id)))
														.intValue() >= item.count)
													continue;
												hasCount = true;
												break label518;
											}

											hasCount = true;
											break label518;
										}
									} else {
										hasCount = true;
									}
								}
								if (hasCount) {
									now = System.currentTimeMillis();
									surplus = 0L;
									if (tlr.lastTime == 0L)
										surplus = tlr.end - now;
									else {
										surplus = player.getRewardRecord().getActivateTimeLimitRewardTime()
												+ tlr.lastTime - now;
									}
									if (surplus > 0L) {
										builder = genMainInterfaceInfo(func, 1);
										builder.setTimeLimitReward(surplus);
										flag = false;
									}
								}
							}

							if (flag)
								builder = genMainInterfaceInfo(func, -1);
						} else if (func == 45) {
							builder = genMainInterfaceInfo(func,
									(player.getStarcatalogRecord().getEventids().containsValue(Boolean.valueOf(false)))
											? 1
											: -1);
						} else {
							int num = getMainInterfaceFunctionNum(player, func);
							builder = genMainInterfaceInfo(func, num);
						}
					}
				}
				b.addInfos(builder);
			}
		}
		if (b.getInfosCount() > 0)
			player.send(1170, b.build());
	}

	public static void notifyMainInterfaceFunction(Player player) {
		PbCommons.MainInterfaceInfo.Function[] funcs = PbCommons.MainInterfaceInfo.Function.values();
		int[] ids = new int[funcs.length];
		for (int i = 0; i < ids.length; ++i) {
			ids[i] = funcs[i].getNumber();
		}
		notify(player, ids);
	}

	public static void notifyMainCheck(Player player, int[] ids) {
		notify(player, ids);
	}

	public static void notifyMainNum(Player player, int funcId, int num) {
		PbDown.MainInterfaceRst.Builder b = PbDown.MainInterfaceRst.newBuilder();
		b.addInfos(genMainInterfaceInfo(funcId, num));
		player.send(1170, b.build());
	}

	public static PbCommons.MainInterfaceInfo.Builder genMainInterfaceInfo(int funcId, int num) {
		return PbCommons.MainInterfaceInfo.newBuilder().setFunc(PbCommons.MainInterfaceInfo.Function.valueOf(funcId))
				.setData(num);
	}

	public static PbCommons.ActivityListInfo genActivityListInfo(int id, Player player) {
		int num = getActivityCanGetNum(id, player);
		if (num == -1) {
			return null;
		}
		PbCommons.ActivityListInfo.Builder builder = PbCommons.ActivityListInfo.newBuilder();
		builder.setId(id);
		builder.setNum(num);
		return builder.build();
	}

	public static void main(String[] args) {
		System.out.println(0);
	}
}
