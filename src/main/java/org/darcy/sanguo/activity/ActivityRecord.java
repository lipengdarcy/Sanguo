package org.darcy.sanguo.activity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.item.ExchangeAI;
import org.darcy.sanguo.activity.item.PrayAI;
import org.darcy.sanguo.activity.item.TurnPlateAI;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.pay.MonthCard;
import org.darcy.sanguo.pay.PayItem;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.PayService;

import sango.packet.PbActivity;

public class ActivityRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 2988959108109865094L;
	private static int version = 8;
	private static int superDayRewardCharges = 300;

	private Map<Integer, Integer> activityIds = new HashMap();

	private Map<Integer, Integer> exchanges = new HashMap();
	private int charge;
	private Set<Integer> chargeRewards = new HashSet();
	private int cost;
	private Set<Integer> costRewards = new HashSet();

	private int turnPlateCurRound = 1;

	private Map<Integer, Integer> turnPlateCounts = new HashMap();
	private int turnPlateTotalCount;
	private Map<Integer, Boolean> turnPlateRewardGets = new HashMap();
	private int praySurplus;
	private Map<Integer, Integer> prayProcess = new HashMap();

	private Map<Integer, MonthCardInfo> monthCards = new HashMap();
	private int dayCharge;
	private Set<Integer> dayChargeRewards = new HashSet();
	private int dayRewardCount;
	private Set<Integer> dayRewardGet = new HashSet();

	private boolean dayRewardTodayCharge = false;

	private int curRoundType = 1;

	private Map<Integer, Integer> chargeType = new HashMap();

	private long dayRewardFreshFlag = -1L;
	private int persistChargeCount;
	private Set<Integer> persistChargeGet = new HashSet();

	private boolean persistChargeTodayCharge = false;

	private Map<Long, Boolean> fistGiveRecords = new HashMap();

	private Map<Long, Boolean> fistPackageRecords = new HashMap();

	public void checkDayRewardFresh(Player player) {
		if ((0L >= this.dayRewardFreshFlag) || (this.dayRewardFreshFlag >= System.currentTimeMillis()))
			if ((this.dayRewardCount < ((this.curRoundType != 2) ? ActivityService.day30Rewards
					: ActivityService.day7Rewards).size()) || (this.dayRewardTodayCharge))
				return;
		List rewards = new ArrayList();
		Map dayReward = (this.curRoundType != 2) ? ActivityService.day30Rewards : ActivityService.day7Rewards;
		for (int i = 1; i <= this.dayRewardCount; ++i) {
			if (!(this.dayRewardGet.contains(Integer.valueOf(i)))) {
				rewards.addAll(((List[]) dayReward.get(Integer.valueOf(i)))[getChargeType(i)]);
			}
		}

		List rs = Reward.mergeReward(rewards);
		if (rs.size() > 0) {
			MailService.sendSystemMail(2, player.getId(), "天天好礼未领取奖励补发",
					"<p style=21>您有天天好礼奖励未领取，由于活动更新，我们将您未领取的奖励邮寄给您，请确认查收！</p>", new Date(), rs);
		}

		this.dayRewardCount = 0;
		this.dayRewardGet.clear();
		this.dayRewardTodayCharge = false;
		this.chargeType.clear();
		this.curRoundType = 2;
		this.dayRewardFreshFlag = -1L;
	}

	public int getRoundId(int activityId) {
		if (!(this.activityIds.containsKey(Integer.valueOf(activityId)))) {
			this.activityIds.put(Integer.valueOf(activityId), Integer.valueOf(-1));
		}
		return ((Integer) this.activityIds.get(Integer.valueOf(activityId))).intValue();
	}

	public void updateRoundId(int activityId, int id) {
		this.activityIds.put(Integer.valueOf(activityId), Integer.valueOf(id));
	}

	public void updateExchanges(int id, int count) {
		this.exchanges.put(Integer.valueOf(id), Integer.valueOf(count));
	}

	public int getCount(int id) {
		if (!(this.exchanges.containsKey(Integer.valueOf(id)))) {
			updateExchanges(id, 0);
		}
		return ((Integer) this.exchanges.get(Integer.valueOf(id))).intValue();
	}

	public int getSurplusCount(ActivityExchange ae) {
		int count = 0;
		if (this.exchanges.get(Integer.valueOf(ae.id)) != null) {
			count = ((Integer) this.exchanges.get(Integer.valueOf(ae.id))).intValue();
		}
		int surplus = ae.count - count;
		if (surplus < 0) {
			surplus = 0;
			updateExchanges(ae.id, ae.count);
		}
		return surplus;
	}

	public void refresh(Player player) {
		if (ActivityInfo.isOpenActivity(1, player)) {
			ExchangeAI ai = (ExchangeAI) ActivityInfo.getItem(player, 1);
			if (ai != null) {
				Iterator itx = this.exchanges.keySet().iterator();
				while (itx.hasNext()) {
					int id = ((Integer) itx.next()).intValue();
					ActivityExchange ae = (ActivityExchange) ai.exchanges.get(Integer.valueOf(id));
					if (ae == null) {
						itx.remove();
					} else if (ae.countType == 1) {
						if (Calendar.getInstance().get(7) == 2)
							updateExchanges(id, 0);
					} else if (ae.countType == 2) {
						updateExchanges(id, 0);
					}

				}

			}

		}

		if (ActivityInfo.isOpenActivity(10, player)) {
			this.prayProcess.clear();
		}

		if (this.monthCards.size() > 0) {
			Iterator itx = this.monthCards.keySet().iterator();
			while (itx.hasNext()) {
				int id = ((Integer) itx.next()).intValue();
				MonthCardInfo info = (MonthCardInfo) this.monthCards.get(Integer.valueOf(id));
				Calendar cal = Calendar.getInstance();
				int lastRefreshDay = player.getLastRefreshDay();
				int interval = cal.get(6) - lastRefreshDay;
				if (interval > 0) {
					int count = Math.min(interval, info.surplus);
					cal.set(11, 0);
					cal.set(12, 0);
					cal.set(13, 0);
					for (int i = 1; i <= count; ++i) {
						cal.set(6, lastRefreshDay + i);
						boolean isRemove = rewardMonthCard(id, player, cal.getTimeInMillis(), info.channel);
						if (isRemove) {
							itx.remove();
						}
					}
				}
			}
		}

		if (ActivityInfo.isOpenActivity(12, player)) {
			this.dayCharge = 0;
			this.dayChargeRewards.clear();
		}

		this.dayRewardTodayCharge = false;
		checkDayRewardFresh(player);

		this.persistChargeTodayCharge = false;
	}

	public void chargeInActivity(Player player, int count) {
		if (ActivityInfo.isOpenActivity(3, player)) {
			this.charge += count;
			Platform.getEventManager().addEvent(new Event(2067, new Object[] { player, Integer.valueOf(count) }));
		}
		if (ActivityInfo.isOpenActivity(12, player)) {
			this.dayCharge += count;
			Platform.getEventManager().addEvent(new Event(2091, new Object[] { player }));
		}
		if ((!(dayRewardIsOver())) && (!(this.dayRewardTodayCharge))) {
			this.dayRewardTodayCharge = true;
			this.dayRewardCount += 1;
			this.chargeType.put(Integer.valueOf(this.dayRewardCount),
					Integer.valueOf((count >= superDayRewardCharges) ? 1 : 0));
			if (this.dayRewardCount >= ((this.curRoundType != 2) ? ActivityService.day30Rewards
					: ActivityService.day7Rewards).size()) {
				Calendar cal = Calendar.getInstance();
				cal.set(11, 24);
				cal.set(12, 0);
				cal.set(13, 0);
				cal.set(14, 0);
				this.dayRewardFreshFlag = cal.getTimeInMillis();
			}
			Platform.getEventManager().addEvent(new Event(2093, new Object[] { player }));
		}
		if ((ActivityInfo.isOpenActivity(13, player)) && (!(this.persistChargeTodayCharge))) {
			this.persistChargeCount += 1;
			this.persistChargeTodayCharge = true;
			Platform.getEventManager().addEvent(new Event(2109, new Object[] { player }));
		}
	}

	public void chargeRewardGet(Player player, int count, List<Reward> rewards) {
		if (!(this.chargeRewards.contains(Integer.valueOf(count)))) {
			for (Reward reward : rewards) {
				reward.add(player, "chargereward");
			}
			this.chargeRewards.add(Integer.valueOf(count));
			Platform.getEventManager().addEvent(new Event(2069, new Object[] { player }));
		}
	}

	public int getCanGetChargeRewardNum(Player player) {
		if (ActivityInfo.isOpenActivity(3, player)) {
			int count = 0;
			Set set = ActivityInfo.getItem(player, 3).keySet();
			Iterator localIterator = set.iterator();
			while (true) {
				Integer tmp = (Integer) localIterator.next();
				if ((!(this.chargeRewards.contains(tmp))) && (this.charge >= tmp.intValue()))
					++count;
				if (!(localIterator.hasNext())) {
					return count;
				}
			}
		}
		return 0;
	}

	public void costInActivity(Player player, int count) {
		if (ActivityInfo.isOpenActivity(4, player)) {
			this.cost += count;
			Platform.getEventManager().addEvent(new Event(2068, new Object[] { player, Integer.valueOf(count) }));
		}
	}

	public void costRewardGet(Player player, int count, List<Reward> rewards) {
		if (!(this.costRewards.contains(Integer.valueOf(count)))) {
			for (Reward reward : rewards) {
				reward.add(player, "costreward");
			}
			this.costRewards.add(Integer.valueOf(count));
			Platform.getEventManager().addEvent(new Event(2070, new Object[] { player }));
		}
	}

	public int getCanGetCostRewardNum(Player player) {
		if (ActivityInfo.isOpenActivity(4, player)) {
			int count = 0;
			Set set = ActivityInfo.getItem(player, 4).keySet();
			Iterator localIterator = set.iterator();
			while (true) {
				Integer tmp = (Integer) localIterator.next();
				if ((!(this.costRewards.contains(tmp))) && (this.cost >= tmp.intValue()))
					++count;
				if (!(localIterator.hasNext())) {
					return count;
				}
			}
		}
		return 0;
	}

	public boolean isGetTurnPlateSocreReward(int score) {
		if (!(this.turnPlateRewardGets.containsKey(Integer.valueOf(score)))) {
			this.turnPlateRewardGets.put(Integer.valueOf(score), Boolean.valueOf(false));
		}
		return ((Boolean) this.turnPlateRewardGets.get(Integer.valueOf(score))).booleanValue();
	}

	public void turnPlateScoreRewardGet(Player player, int score, List<Reward> rewards) {
		if (!(isGetTurnPlateSocreReward(score))) {
			for (Reward reward : rewards) {
				reward.add(player, "turnplatescorereward");
			}
			this.turnPlateRewardGets.put(Integer.valueOf(score), Boolean.valueOf(true));
			Platform.getEventManager().addEvent(new Event(2090, new Object[] { player }));
		}
	}

	private int turnPlateGetCount() {
		int result = 0;
		for (Integer count : this.turnPlateCounts.values()) {
			result += count.intValue();
		}
		return result;
	}

	public List<Integer> turnPlatePlay(Player p, int count, TurnPlateAI ai) {
		List list = new ArrayList();

		int totalCount = ai.getRoundTotalCount(this.turnPlateCurRound);
		for (int j = 0; j < count; ++j) {
			if (!(ai.counts.containsKey(Integer.valueOf(this.turnPlateCurRound)))) {
				this.turnPlateCurRound = 1;
				totalCount = ai.getRoundTotalCount(this.turnPlateCurRound);
			}
			if (turnPlateGetCount() >= totalCount) {
				this.turnPlateCurRound += 1;
				this.turnPlateCounts.clear();
				totalCount = ai.getRoundTotalCount(this.turnPlateCurRound);
				--j;
			} else {
				Map alreadyCounts = this.turnPlateCounts;
				Map total = (Map) ai.counts.get(Integer.valueOf(this.turnPlateCurRound));

				int id = 0;
				Map weights = new HashMap();
				int sum = 0;
				Iterator itx = total.keySet().iterator();
				while (itx.hasNext()) {
					int item = ((Integer) itx.next()).intValue();
					if (!(alreadyCounts.containsKey(Integer.valueOf(item)))) {
						alreadyCounts.put(Integer.valueOf(item), Integer.valueOf(0));
					}
					int select = Math.max(0, ((Integer) total.get(Integer.valueOf(item))).intValue()
							- ((Integer) alreadyCounts.get(Integer.valueOf(item))).intValue());
					weights.put(Integer.valueOf(item), Integer.valueOf(select));
					sum += select;
				}
				int rnd = (int) (Math.random() * sum);
				int seed = 0;
				itx = weights.keySet().iterator();
				while (itx.hasNext()) {
					int item = ((Integer) itx.next()).intValue();
					int select = ((Integer) weights.get(Integer.valueOf(item))).intValue();
					seed += select;
					if (rnd < seed) {
						id = item;
						break;
					}
				}
				list.add(Integer.valueOf(id));
				alreadyCounts.put(Integer.valueOf(id),
						Integer.valueOf(((Integer) alreadyCounts.get(Integer.valueOf(id))).intValue() + 1));
			}
		}
		this.turnPlateTotalCount += count;
		Platform.getEventManager().addEvent(new Event(2089, new Object[] { p }));
		return list;
	}

	public int getPrayProcess(int type) {
		if (!(this.prayProcess.containsKey(Integer.valueOf(type)))) {
			this.prayProcess.put(Integer.valueOf(type), Integer.valueOf(0));
		}
		return ((Integer) this.prayProcess.get(Integer.valueOf(type))).intValue();
	}

	public void prayAddProcess(Player player, int type, int count) {
		if (ActivityInfo.isOpenActivity(10, player)) {
			PrayAI ai = (PrayAI) ActivityInfo.getItem(player, 10);
			if (ai != null) {
				int old = getPrayProcess(type);
				int now = old + count;
				int add = ai.getAddPrayCount(type, old, now);
				this.praySurplus += add;
				this.prayProcess.put(Integer.valueOf(type), Integer.valueOf(now));
				Platform.getEventManager().addEvent(new Event(2087, new Object[] { player }));
			}
		}
	}

	public boolean rewardMonthCard(int id, Player p, long time, String channel) {
		MonthCardInfo info = (MonthCardInfo) this.monthCards.get(Integer.valueOf(id));
		if (info == null) {
			return false;
		}
		if (info.lastGetTime >= time)
			return false;
		MonthCard mc = ActivityService.getMonthCard(info.goodsId);
		if (mc == null)
			return false;
		int get = mc.reward;

		List list = new ArrayList();
		list.add(new Reward(3, get, null));

		PayItem pi = (PayItem) ((HashMap) PayService.pays.get(channel)).get(Integer.valueOf(id));

		int mailType = 0;
		String title = "";
		String content = "";
		if (id == 1001) {
			mailType = 12;
			title = "月卡每日奖励";
			content = MessageFormat.format(
					"<p style=21>尊敬的主公，呈上今天的</p><p style=19>【{0}】</p><p style=21>每日奖励，请您笑纳。\n温馨提醒：月卡剩余</p><p style=20>【{1}】</p><p style=21>天。</p>",
					new Object[] { pi.name, Integer.valueOf(info.surplus - 1) });
		} else if (id == 1002) {
			mailType = 16;
			title = "季卡每日奖励";
			content = MessageFormat.format(
					"<p style=21>尊敬的主公，呈上今天的</p><p style=19>【{0}】</p><p style=21>每日奖励，请您笑纳。\n温馨提醒：季卡剩余</p><p style=20>【{1}】</p><p style=21>天。</p>",
					new Object[] { pi.name, Integer.valueOf(info.surplus - 1) });
		}

		MailService.sendSystemMail(mailType, p.getId(), title, content, new Date(time), list);

		info.reward(time);

		label261: return (info.surplus > 0);
	}

	public int getMonthCardSurplus(int id) {
		MonthCardInfo info = (MonthCardInfo) this.monthCards.get(Integer.valueOf(id));
		if (info == null) {
			return 0;
		}
		return info.surplus;
	}

	public boolean addMonthCard(int id, Player p, String channel) {
		if (isBuyMonthCard(id)) {
			return false;
		}
		MonthCard mc = ActivityService.getMonthCard(id);
		if (mc == null) {
			return false;
		}
		MonthCardInfo info = new MonthCardInfo();
		info.goodsId = id;
		info.surplus = mc.last;
		info.channel = channel;
		this.monthCards.put(Integer.valueOf(id), info);

		rewardMonthCard(id, p, System.currentTimeMillis(), channel);
		Platform.getEventManager().addEvent(new Event(2099, new Object[] { p }));
		return true;
	}

	public boolean isBuyMonthCard(int id) {
		return this.monthCards.containsKey(Integer.valueOf(id));
	}

	public void dayChargeRewardGet(Player player, int count, List<Reward> rewards) {
		if (!(this.dayChargeRewards.contains(Integer.valueOf(count)))) {
			for (Reward reward : rewards) {
				reward.add(player, "daychargereward");
			}
			this.dayChargeRewards.add(Integer.valueOf(count));
			Platform.getEventManager().addEvent(new Event(2092, new Object[] { player }));
		}
	}

	public int getCanGetDayChargeRewardNum(Player player) {
		if (ActivityInfo.isOpenActivity(12, player)) {
			int count = 0;
			Set set = ActivityInfo.getItem(player, 12).keySet();
			Iterator localIterator = set.iterator();
			while (true) {
				Integer tmp = (Integer) localIterator.next();
				if ((!(this.dayChargeRewards.contains(tmp))) && (this.dayCharge >= tmp.intValue()))
					++count;
				if (!(localIterator.hasNext())) {
					return count;
				}
			}
		}
		return 0;
	}

	public boolean dayRewardIsOver() {
		return false;
	}

	public void getDayReward(Player player, int day, List<Reward> list) {
		if (!(this.dayRewardGet.contains(Integer.valueOf(day)))) {
			this.dayRewardGet.add(Integer.valueOf(day));
			for (Reward r : list) {
				r.add(player, "dayreward");
			}
			Platform.getEventManager().addEvent(new Event(2094, new Object[] { player }));
		}
	}

	public void getPersistChargeReward(Player player, int day, List<Reward> list) {
		if (!(this.persistChargeGet.contains(Integer.valueOf(day)))) {
			this.persistChargeGet.add(Integer.valueOf(day));
			for (Reward r : list) {
				r.add(player, "getpersistcharge");
			}
			Platform.getEventManager().addEvent(new Event(2108, new Object[] { player }));
		}
	}

	public int getCanGetPersistChargeNum(Player player) {
		if (ActivityInfo.isOpenActivity(13, player)) {
			int count = 0;
			Set set = ActivityInfo.getItem(player, 13).keySet();
			Iterator localIterator = set.iterator();
			while (true) {
				Integer day = (Integer) localIterator.next();
				if ((!(this.persistChargeGet.contains(day))) && (this.persistChargeCount >= day.intValue()))
					++count;
				if (!(localIterator.hasNext())) {
					return count;
				}
			}
		}
		return 0;
	}

	public Map<Integer, Integer> getExchanges() {
		return this.exchanges;
	}

	public void setExchanges(Map<Integer, Integer> exchanges) {
		this.exchanges = exchanges;
	}

	public int getCharge() {
		return this.charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public int getCost() {
		return this.cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Map<Integer, Integer> getActivityIds() {
		return this.activityIds;
	}

	public void setActivityIds(Map<Integer, Integer> activityIds) {
		this.activityIds = activityIds;
	}

	public Set<Integer> getChargeRewards() {
		return this.chargeRewards;
	}

	public void setChargeRewards(Set<Integer> chargeRewards) {
		this.chargeRewards = chargeRewards;
	}

	public Set<Integer> getCostRewards() {
		return this.costRewards;
	}

	public void setCostRewards(Set<Integer> costRewards) {
		this.costRewards = costRewards;
	}

	public int getTurnPlateCurRound() {
		return this.turnPlateCurRound;
	}

	public void setTurnPlateCurRound(int turnPlateCurRound) {
		this.turnPlateCurRound = turnPlateCurRound;
	}

	public Map<Integer, Integer> getTurnPlateCounts() {
		return this.turnPlateCounts;
	}

	public void setTurnPlateCounts(Map<Integer, Integer> turnPlateCounts) {
		this.turnPlateCounts = turnPlateCounts;
	}

	public int getTurnPlateTotalCount() {
		return this.turnPlateTotalCount;
	}

	public void setTurnPlateTotalCount(int turnPlateTotalCount) {
		this.turnPlateTotalCount = turnPlateTotalCount;
	}

	public Map<Integer, Boolean> getTurnPlateRewardGets() {
		return this.turnPlateRewardGets;
	}

	public void setTurnPlateRewardGets(Map<Integer, Boolean> turnPlateRewardGets) {
		this.turnPlateRewardGets = turnPlateRewardGets;
	}

	public int getPraySurplus() {
		return this.praySurplus;
	}

	public void setPraySurplus(int praySurplus) {
		this.praySurplus = praySurplus;
	}

	public Map<Integer, Integer> getPrayProcess() {
		return this.prayProcess;
	}

	public void setPrayProcess(Map<Integer, Integer> prayProcess) {
		this.prayProcess = prayProcess;
	}

	public Map<Integer, MonthCardInfo> getMonthCards() {
		return this.monthCards;
	}

	public void setMonthCards(Map<Integer, MonthCardInfo> monthCards) {
		this.monthCards = monthCards;
	}

	public int getDayCharge() {
		return this.dayCharge;
	}

	public void setDayCharge(int dayCharge) {
		this.dayCharge = dayCharge;
	}

	public Set<Integer> getDayChargeRewards() {
		return this.dayChargeRewards;
	}

	public void setDayChargeRewards(Set<Integer> dayChargeRewards) {
		this.dayChargeRewards = dayChargeRewards;
	}

	public int getDayRewardCount() {
		return this.dayRewardCount;
	}

	public void setDayRewardCount(int dayRewardCount) {
		this.dayRewardCount = dayRewardCount;
	}

	public Set<Integer> getDayRewardGet() {
		return this.dayRewardGet;
	}

	public void setDayRewardGet(Set<Integer> dayRewardGet) {
		this.dayRewardGet = dayRewardGet;
	}

	public boolean isDayRewardTodayCharge() {
		return this.dayRewardTodayCharge;
	}

	public void setDayRewardTodayCharge(boolean dayRewardTodayCharge) {
		this.dayRewardTodayCharge = dayRewardTodayCharge;
	}

	public int getPersistChargeCount() {
		return this.persistChargeCount;
	}

	public void setPersistChargeCount(int persistChargeCount) {
		this.persistChargeCount = persistChargeCount;
	}

	public Set<Integer> getPersistChargeGet() {
		return this.persistChargeGet;
	}

	public void setPersistChargeGet(Set<Integer> persistChargeGet) {
		this.persistChargeGet = persistChargeGet;
	}

	public boolean isPersistChargeTodayCharge() {
		return this.persistChargeTodayCharge;
	}

	public void setPersistChargeTodayCharge(boolean persistChargeTodayCharge) {
		this.persistChargeTodayCharge = persistChargeTodayCharge;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int id, i;
			int count;
			int version = in.readInt();

			this.exchanges = new HashMap();
			int size = in.readInt();
			for (i = 0; i < size; ++i) {
				id = in.readInt();
				count = in.readInt();
				this.exchanges.put(Integer.valueOf(id), Integer.valueOf(count));
			}

			this.activityIds = new HashMap();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				int activityId = in.readInt();
				id = in.readInt();
				this.activityIds.put(Integer.valueOf(activityId), Integer.valueOf(id));
			}

			this.charge = in.readInt();
			this.cost = in.readInt();
			this.chargeRewards = new HashSet();
			this.costRewards = new HashSet();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.chargeRewards.add(Integer.valueOf(in.readInt()));
			}
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.costRewards.add(Integer.valueOf(in.readInt()));
			}
			this.monthCards = new HashMap();
			this.prayProcess = new HashMap();
			this.turnPlateCounts = new HashMap();
			this.turnPlateRewardGets = new HashMap();
			this.dayChargeRewards = new HashSet();
			this.dayRewardGet = new HashSet();
			this.persistChargeGet = new HashSet();
			this.chargeType = new HashMap();
			this.fistGiveRecords = new HashMap();
			this.fistPackageRecords = new HashMap();
			if (version > 1) {
				if (version == 2) {
					in.readInt();
					in.readInt();
					in.readLong();
				} else {
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						MonthCardInfo info = MonthCardInfo.readObject(in);
						if (info != null) {
							this.monthCards.put(Integer.valueOf(info.goodsId), info);
						}
					}
					if (version <= 3)
						return;
					this.praySurplus = in.readInt();
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						int type = in.readInt();
						int process = in.readInt();
						this.prayProcess.put(Integer.valueOf(type), Integer.valueOf(process));
					}

					if (version < 6) {
						in.readInt();
					}
					this.turnPlateCurRound = in.readInt();
					this.turnPlateTotalCount = in.readInt();
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						id = in.readInt();
						count = in.readInt();
						this.turnPlateCounts.put(Integer.valueOf(id), Integer.valueOf(count));
					}
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						int score = in.readInt();
						boolean isGet = in.readBoolean();
						this.turnPlateRewardGets.put(Integer.valueOf(score), Boolean.valueOf(isGet));
					}

					this.dayCharge = in.readInt();
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						this.dayChargeRewards.add(Integer.valueOf(in.readInt()));
					}

					this.dayRewardCount = in.readInt();
					this.dayRewardTodayCharge = in.readBoolean();
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						this.dayRewardGet.add(Integer.valueOf(in.readInt()));
					}
					if (version <= 4)
						return;
					this.persistChargeCount = in.readInt();
					this.persistChargeTodayCharge = in.readBoolean();
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						this.persistChargeGet.add(Integer.valueOf(in.readInt()));
					}
					if (version > 6) {
						this.curRoundType = in.readInt();
						this.dayRewardFreshFlag = in.readLong();
						size = in.readInt();
						for (i = 0; i < size; ++i) {
							int day = in.readInt();
							int type = in.readInt();
							this.chargeType.put(Integer.valueOf(day), Integer.valueOf(type));
						}
						if (version > 7) {
							long time;
							boolean isReseted;
							size = in.readInt();
							for (i = 0; i < size; ++i) {
								time = in.readLong();
								isReseted = in.readBoolean();
								this.fistGiveRecords.put(Long.valueOf(time), Boolean.valueOf(isReseted));
							}
							size = in.readInt();
							for (i = 0; i < size; ++i) {
								time = in.readLong();
								isReseted = in.readBoolean();
								this.fistPackageRecords.put(Long.valueOf(time), Boolean.valueOf(isReseted));
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Integer day;
		Map.Entry entry;
		out.writeInt(version);
		int count, id;
		out.writeInt(this.exchanges.size());
		Iterator itx = this.exchanges.keySet().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			count = ((Integer) this.exchanges.get(Integer.valueOf(id))).intValue();
			out.writeInt(id);
			out.writeInt(count);
		}

		out.writeInt(this.activityIds.size());
		itx = this.activityIds.keySet().iterator();
		while (itx.hasNext()) {
			int activityId = ((Integer) itx.next()).intValue();
			id = ((Integer) this.activityIds.get(Integer.valueOf(activityId))).intValue();
			out.writeInt(activityId);
			out.writeInt(id);
		}

		out.writeInt(this.charge);
		out.writeInt(this.cost);
		out.writeInt(this.chargeRewards.size());
		for (Iterator it = this.chargeRewards.iterator(); it.hasNext();) {
			count = (Integer) it.next();
			out.writeInt(count);
		}
		out.writeInt(this.costRewards.size());
		for (Iterator it = this.costRewards.iterator(); it.hasNext();) {
			count = (Integer) it.next();
			out.writeInt(count);
		}

		out.writeInt(this.monthCards.size());
		for (MonthCardInfo info : this.monthCards.values()) {
			info.writeObject(out);
		}

		out.writeInt(this.praySurplus);
		out.writeInt(this.prayProcess.size());
		for (Integer type : this.prayProcess.keySet()) {
			Integer process = (Integer) this.prayProcess.get(type);
			if (process != null) {
				out.writeInt(type.intValue());
				out.writeInt(process.intValue());
			}
		}

		out.writeInt(this.turnPlateCurRound);
		out.writeInt(this.turnPlateTotalCount);
		out.writeInt(this.turnPlateCounts.size());
		for (Integer iid : this.turnPlateCounts.keySet()) {
			count = ((Integer) this.turnPlateCounts.get(iid)).intValue();
			out.writeInt(iid);
			out.writeInt(count);
		}
		out.writeInt(this.turnPlateRewardGets.size());
		for (Integer score : this.turnPlateRewardGets.keySet()) {
			boolean isGet = ((Boolean) this.turnPlateRewardGets.get(score)).booleanValue();
			out.writeInt(score.intValue());
			out.writeBoolean(isGet);
		}

		out.writeInt(this.dayCharge);
		out.writeInt(this.dayChargeRewards.size());
		for (Iterator it = this.dayChargeRewards.iterator(); it.hasNext();) {
			count = (Integer) it.next();
			out.writeInt(count);
		}

		out.writeInt(this.dayRewardCount);
		out.writeBoolean(this.dayRewardTodayCharge);
		out.writeInt(this.dayRewardGet.size());
		for (Iterator it = this.dayRewardGet.iterator(); it.hasNext();) {
			day = (Integer) it.next();
			out.writeInt(day.intValue());
		}

		out.writeInt(this.persistChargeCount);
		out.writeBoolean(this.persistChargeTodayCharge);
		out.writeInt(this.persistChargeGet.size());
		for (Iterator it = this.persistChargeGet.iterator(); it.hasNext();) {
			day = (Integer) it.next();
			out.writeInt(day.intValue());
		}
		out.writeInt(this.curRoundType);
		out.writeLong(this.dayRewardFreshFlag);
		out.writeInt(this.chargeType.size());
		for (Iterator it = this.chargeType.entrySet().iterator(); it.hasNext();) {
			entry = (Map.Entry) it.next();
			out.writeInt(((Integer) entry.getKey()).intValue());
			out.writeInt(((Integer) entry.getValue()).intValue());
		}

		out.writeInt(this.fistGiveRecords.size());
		for (Iterator it = this.fistGiveRecords.entrySet().iterator(); it.hasNext();) {
			entry = (Map.Entry) it.next();
			out.writeLong(((Long) entry.getKey()).longValue());
			out.writeBoolean(((Boolean) entry.getValue()).booleanValue());
		}

		out.writeInt(this.fistPackageRecords.size());
		for (Iterator it = this.fistPackageRecords.entrySet().iterator(); it.hasNext();) {
			entry = (Map.Entry) it.next();
			out.writeLong(((Long) entry.getKey()).longValue());
			out.writeBoolean(((Boolean) entry.getValue()).booleanValue());
		}
	}

	public int getBlobId() {
		return 19;
	}

	public List<PbActivity.TurnPlateSocreReward> genTurnPlateSocreRewardList(TurnPlateAI ai) {
		List list = new ArrayList();
		for (Integer score : ai.scoreRewards.keySet()) {
			List<Reward> rewards = (List) ai.scoreRewards.get(score);
			boolean isGet = isGetTurnPlateSocreReward(score.intValue());
			PbActivity.TurnPlateSocreReward.Builder b = PbActivity.TurnPlateSocreReward.newBuilder();
			b.setScore(score.intValue());
			b.setIsGet(isGet);
			for (Reward r : rewards) {
				b.addReward(r.genPbReward());
			}
			list.add(b.build());
		}
		return list;
	}

	public PbActivity.TurnPlateBaseInfo genTurnPlateBaseInfo(Player p) {
		PbActivity.TurnPlateBaseInfo.Builder b = PbActivity.TurnPlateBaseInfo.newBuilder();
		b.setFreeCount(0);
		b.setScore(this.turnPlateTotalCount);
		b.setSurplus(1500 - this.turnPlateTotalCount);
		b.setRaffles(p.getBags().getItemCount(11014));
		b.setRafflesId(11014);
		return b.build();
	}

	public int getCurRoundType() {
		return this.curRoundType;
	}

	public void setCurRoundType(int curRoundType) {
		this.curRoundType = curRoundType;
	}

	public int getChargeType(int day) {
		Integer type = (Integer) this.chargeType.get(Integer.valueOf(day));
		if (type != null) {
			return type.intValue();
		}
		return 0;
	}

	public void setChargeType(Map<Integer, Integer> chargeType) {
		this.chargeType = chargeType;
	}

	public Map<Long, Boolean> getFistGiveRecords() {
		return this.fistGiveRecords;
	}

	public void setFistGiveRecords(Map<Long, Boolean> fistGiveRecords) {
		this.fistGiveRecords = fistGiveRecords;
	}

	public Map<Long, Boolean> getFistPackageRecords() {
		return this.fistPackageRecords;
	}

	public void setFistPackageRecords(Map<Long, Boolean> fistPackageRecords) {
		this.fistPackageRecords = fistPackageRecords;
	}
}
