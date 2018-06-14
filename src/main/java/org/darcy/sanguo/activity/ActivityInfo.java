package org.darcy.sanguo.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.darcy.sanguo.activity.item.ActivityItem;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.world.World;

public class ActivityInfo {
	public static final int ACTIVITY_EXCHANGE = 1;
	public static final int ACTIVITY_LOGIN_AWARD = 2;
	public static final int ACTIVITY_CHARGE = 3;
	public static final int ACTIVITY_COST = 4;
	public static final int ACTIVITY_ARENA_DOUBLE = 5;
	public static final int ACTIVITY_COMETITION_DOUBLE = 6;
	public static final int ACTIVITY_COOKWINE_DOUBLE = 7;
	public static final int ACTIVITY_MAP_DROP = 8;
	public static final int ACTIVITY_TURNPLATE = 9;
	public static final int ACTIVITY_PRAY = 10;
	public static final int ACTIVITY_RECRUIT_DROP = 11;
	public static final int ACTIVITY_DAY_CHARGE = 12;
	public static final int ACTIVITY_PERSIST_CHARGE = 13;
	public static final int ACTIVITY_MONEY_MAP_CHANLLENGE_TIMES = 14;
	public static final int ACTIVITY_WARRIOR_MAP_CHANLLENGE_TIMES = 15;
	public static final int ACTIVITY_TREASURE_MAP_CHANLLENGE_TIMES = 16;
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static SimpleDateFormat sendSdf = new SimpleDateFormat("yyyy/MM/dd");

	public static Map<Integer, Map<Integer, ActivityData>> activities = new HashMap();

	public static Map<Integer, Map<Integer, ActivityItem>> activityItems = new HashMap();

	public static ActivityItem getItem(Player p, int activityId) {
		Map map = (Map) activityItems.get(Integer.valueOf(activityId));
		if ((map != null) && (isOpenActivity(activityId, p))) {
			int roundId = p.getActivityRecord().getRoundId(activityId);
			if (roundId > -1) {
				return ((ActivityItem) map.get(Integer.valueOf(roundId)));
			}
		}

		return null;
	}

	public static void addItem(Map<Integer, ActivityItem> roundMap) {
		for (Iterator localIterator = roundMap.keySet().iterator(); localIterator.hasNext();) {
			int roundId = ((Integer) localIterator.next()).intValue();
			ActivityItem ai = (ActivityItem) roundMap.get(Integer.valueOf(roundId));
			int activityId = ai.getActivityId();
			Map map = (Map) activityItems.get(Integer.valueOf(activityId));
			if (map == null) {
				map = new HashMap();
				activityItems.put(Integer.valueOf(activityId), map);
			}
			if (!(map.containsKey(Integer.valueOf(roundId))))
				map.put(Integer.valueOf(roundId), ai);
		}
	}

	public static void addData(ActivityData ad) {
		Map map = (Map) activities.get(Integer.valueOf(ad.activityId));
		if (map == null) {
			map = new HashMap();
			activities.put(Integer.valueOf(ad.activityId), map);
		}
		if (!(map.containsKey(Integer.valueOf(ad.roundId))))
			map.put(Integer.valueOf(ad.roundId), ad);
		else
			throw new RuntimeException("Duplicate roundId!!! activityId:" + ad.activityId + ", roundId:" + ad.roundId);
	}

	public static int getOpenActivityRoundId(int activityId, long time) {
		Map map = (Map) activities.get(Integer.valueOf(activityId));
		if (map != null) {
			long surplus = ActivityService.getOpenServer7DayLeftTimeByTime(time);
			if (surplus >= 0L) {
				ActivityData data = (ActivityData) map.get(Integer.valueOf(0));
				if ((data == null) || (surplus >= data.firstWeekStart * 60 * 1000)
						|| (surplus <= data.firstWeekEnd * 60 * 1000))
					return -1;
				return 0;
			}

			for (Iterator localIterator = map.keySet().iterator(); localIterator.hasNext();) {
				int roundId = ((Integer) localIterator.next()).intValue();
				if (roundId > 0) {
					ActivityData data = (ActivityData) map.get(Integer.valueOf(roundId));
					if ((data != null) && (time >= data.start) && (time <= data.end)) {
						return roundId;
					}
				}
			}

		}

		return -1;
	}

	public static boolean isOpenActivity(int activityId) {
		long now = System.currentTimeMillis();
		return isOpenActivity(activityId, now);
	}

	public static boolean isOpenActivity(int activityId, long time) {
		return (getOpenActivityRoundId(activityId, time) > -1);
	}

	public static boolean isOpenActivity(int activityId, Player player) {
		int roundId = getOpenActivityRoundId(activityId, System.currentTimeMillis());
		if (roundId > -1) {
			ActivityData ad = (ActivityData) ((Map) activities.get(Integer.valueOf(activityId)))
					.get(Integer.valueOf(roundId));

			if (player.getActivityRecord().getRoundId(activityId) != ad.roundId) {
				player.getActivityRecord().updateRoundId(activityId, ad.roundId);
				if (activityId == 2) {
					player.getRewardRecord().getLoginAwardInfos().clear();
				} else if (activityId == 3) {
					player.getActivityRecord().setCharge(0);
					player.getActivityRecord().getChargeRewards().clear();
				} else if (activityId == 4) {
					player.getActivityRecord().setCost(0);
					player.getActivityRecord().getCostRewards().clear();
				} else if (activityId == 1) {
					player.getActivityRecord().getExchanges().clear();
				} else if (activityId == 9) {
					player.getActivityRecord().getTurnPlateCounts().clear();
					player.getActivityRecord().setTurnPlateTotalCount(0);
					player.getActivityRecord().getTurnPlateRewardGets().clear();
					player.getActivityRecord().setTurnPlateCurRound(1);
				} else if (activityId == 10) {
					player.getActivityRecord().setPraySurplus(0);
					player.getActivityRecord().getPrayProcess().clear();
				} else if (activityId == 12) {
					player.getActivityRecord().setDayCharge(0);
					player.getActivityRecord().getDayChargeRewards().clear();
				} else if (activityId == 13) {
					player.getActivityRecord().setPersistChargeCount(0);
					player.getActivityRecord().setPersistChargeTodayCharge(false);
					player.getActivityRecord().getPersistChargeGet().clear();
				}
			}
			return true;
		}
		return false;
	}

	public static long getStartTime(int activityId) {
		int roundId = getOpenActivityRoundId(activityId, System.currentTimeMillis());
		if (roundId > -1) {
			if (roundId == 0) {
				long endTime = ActivityService.getOpenServer7DayTime();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(endTime);
				cal.add(6, -7);
				return cal.getTimeInMillis();
			}
			long endTime = World.getFirstWeekTime();
			ActivityData ad = (ActivityData) ((Map) activities.get(Integer.valueOf(activityId)))
					.get(Integer.valueOf(roundId));
			return Math.max(ad.start, endTime);
		}

		return -1L;
	}

	public static String getStartTimeStr(int activityId) {
		long time = getStartTime(activityId);
		if (time == -1L) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return sendSdf.format(cal.getTime());
	}

	public static long getEndTime(int activityId) {
		int roundId = getOpenActivityRoundId(activityId, System.currentTimeMillis());
		if (roundId > -1) {
			if (roundId == 0) {
				long endTime = ActivityService.getOpenServer7DayTime();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(endTime);
				cal.add(13, -1);
				return cal.getTimeInMillis();
			}
			ActivityData ad = (ActivityData) ((Map) activities.get(Integer.valueOf(activityId)))
					.get(Integer.valueOf(roundId));
			return ad.end;
		}

		return -1L;
	}

	public static String getEndTimeStr(int activityId) {
		long time = getEndTime(activityId);
		if (time == -1L) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return sendSdf.format(cal.getTime());
	}

	public static String getContent(int activityId) {
		int roundId = getOpenActivityRoundId(activityId, System.currentTimeMillis());
		if (roundId > -1) {
			ActivityData ad = (ActivityData) ((Map) activities.get(Integer.valueOf(activityId)))
					.get(Integer.valueOf(roundId));
			return ad.content;
		}
		return "";
	}
}
