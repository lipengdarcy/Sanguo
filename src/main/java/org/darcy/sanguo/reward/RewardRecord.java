package org.darcy.sanguo.reward;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.RewardService;
import org.darcy.sanguo.service.common.FunctionService;

public class RewardRecord implements PlayerBlobEntity {
	public static final int GROWREWARD_COST = 1000;
	public static final int GROWREWARD_VIP = 3;
	public static final int DRAW_MONEY_LAST_TIME = 172800000;
	private static final long serialVersionUID = 3225581929680125993L;
	private static final int version = 6;
	private List<Integer> levelRewardIds = new ArrayList<Integer>();
	private boolean isGetSignReward;
	private int signCount = 1;

	private Set<Integer> recovers = new HashSet<Integer>();

	private boolean[][] loginInfo = new boolean[RewardService.loginRewards.size()][2];
	private int touchGoldenCount;
	private int trainCount;
	private boolean isBuyGrowReward;
	private Set<Integer> growRewards = new HashSet<Integer>();

	private Map<Integer, Boolean> loginAwardInfos = new HashMap<Integer, Boolean>();
	private int drawMoneyCount;
	private long lastGetOnlineReward = System.currentTimeMillis();

	private int onlineRewardCount = 1;

	private Set<Integer> globalChargeRewards = new HashSet<Integer>();

	private Map<Integer, Boolean> superRewardRecord = new HashMap<Integer, Boolean>();

	private int login7DayCount = 1;

	private Set<Integer> login7DayGet = new HashSet<Integer>();

	private long curTimeLimitRewardId = -1L;
	private long activateTimeLimitRewardTime;
	private int timeLimitRewardCount;
	private Map<Long, Map<Integer, Integer>> timelimits = new HashMap<Long, Map<Integer, Integer>>();

	public Map<Integer, Integer> buyRecord(Long tlrId) {
		return ((Map<Integer, Integer>) this.timelimits.get(tlrId));
	}

	public Map<Integer, Integer> putBuyRecord(Long tlrId, Map<Integer, Integer> r) {
		return ((Map<Integer, Integer>) this.timelimits.put(tlrId, r));
	}

	public void registInit(Player player) {
		this.loginInfo[0][0] = true;
		if (ActivityInfo.isOpenActivity(2, player))
			this.loginAwardInfos.put(Integer.valueOf(this.loginAwardInfos.size() + 1), Boolean.valueOf(false));
	}

	public void refresh(Player player) {
		boolean[] info;
		this.recovers.clear();

		for (int i = 0; i < this.loginInfo.length; ++i) {
			info = this.loginInfo[i];
			if (!info[0]) {
				info[0] = true;
				break;
			}

		}

		this.touchGoldenCount = 0;
		this.trainCount = 0;

		if (ActivityInfo.isOpenActivity(2, player)) {
			this.loginAwardInfos.put(Integer.valueOf(this.loginAwardInfos.size() + 1), Boolean.valueOf(false));
		}

		if (this.login7DayCount < 7) {
			this.login7DayCount += 1;
		}

		this.timeLimitRewardCount = 0;
		for (Map<Integer, Integer> map : this.timelimits.values()) {
			for (Integer subId : map.keySet()) {
				map.put(subId, Integer.valueOf(0));
			}
		}

		this.onlineRewardCount = 1;
		this.lastGetOnlineReward = System.currentTimeMillis();
		Platform.getEventManager().addEvent(new Event(2098, new Object[] { player }));
	}

	public void levelReward(Player player, LevelReward reward) {
		if (!(this.levelRewardIds.contains(Integer.valueOf(reward.id)))) {
			if ((reward.rewards != null) && (reward.rewards.size() > 0)) {
				for (Reward tmp : reward.rewards) {
					tmp.add(player, "levelreward");
				}
			}
			this.levelRewardIds.add(Integer.valueOf(reward.id));
		}
	}

	public void getSignReward() {
		this.isGetSignReward = true;
	}

	public int recover(Player player, int id) {
		if (this.recovers.contains(Integer.valueOf(id))) {
			return 0;
		}
		CookWine cw = RewardService.getCookWine(id);
		if (cw == null) {
			return 0;
		}
		this.recovers.add(Integer.valueOf(id));
		int value = cw.getRecover();
		if (ActivityInfo.isOpenActivity(7)) {
			value *= 2;
		}
		player.addVitality(value, "cookwine");
		return value;
	}

	public boolean isRecover(int id) {
		return this.recovers.contains(Integer.valueOf(id));
	}

	public boolean isLoginForReward(int day) {
		if ((day < 0) || (day > this.loginInfo.length)) {
			return false;
		}
		return this.loginInfo[(day - 1)][0];
	}

	public boolean isGetLoginReward(int day) {
		if ((day < 0) || (day > this.loginInfo.length)) {
			return false;
		}
		if (!(isLoginForReward(day))) {
			return false;
		}
		return this.loginInfo[(day - 1)][1];
	}

	public void getLoginReward(Player player, LoginReward lr) {
		int day = lr.day;
		if ((this.loginInfo[(day - 1)][0]) && (!this.loginInfo[(day - 1)][1])) {
			if ((lr.rewards != null) && (lr.rewards.size() > 0)) {
				for (Reward tmp : lr.rewards) {
					tmp.add(player, "loginreward");
				}
			}
			this.loginInfo[(day - 1)][1] = true;
		}
	}

	public boolean isLoginForAward(int day) {
		return this.loginAwardInfos.containsKey(Integer.valueOf(day));
	}

	public boolean isGetLoginAward(int day) {
		Boolean isGet = (Boolean) this.loginAwardInfos.get(Integer.valueOf(day));
		if (isGet == null) {
			return false;
		}
		return isGet.booleanValue();
	}

	public void getLoginAward(Player player, int day, List<Reward> rewards) {
		if ((this.loginAwardInfos.containsKey(Integer.valueOf(day)))
				&& (!(((Boolean) this.loginAwardInfos.get(Integer.valueOf(day))).booleanValue()))) {
			if ((rewards != null) && (rewards.size() > 0)) {
				for (Reward tmp : rewards) {
					tmp.add(player, "loginaward");
				}
			}
			this.loginAwardInfos.put(Integer.valueOf(day), Boolean.valueOf(true));
		}
	}

	public void touchGolden(Player player, TouchGolden tg) {
		this.touchGoldenCount += 1;
		player.decJewels(tg.cost, "touchgold");
		player.addMoney(tg.reward, "touchgold");
	}

	public void train(Player player, TouchGolden tg, boolean free) {
		this.trainCount += 1;
		if ((tg.cost > 0) && (!(free))) {
			player.decJewels(tg.cost, "getTrainPoints");
		}
		addTrainPoint(player, tg.reward, "getTrainPoints");
	}

	public void addTrainPoint(Player player, int addValue, String opt) {
		if (addValue > 0) {
			int points = player.getPool().getInt(22, 0);
			points += addValue;
			player.getPool().set(22, Integer.valueOf(points));
			Platform.getLog().logAcquire(player, "trainpoint", addValue, points, opt);
			player.getDataSyncManager().addNumSync(18, points);
		}
	}

	public boolean isGetGrowReward(int level) {
		return this.growRewards.contains(Integer.valueOf(level));
	}

	public void growRewardGet(GrowReward gr, Player player) {
		if (!(this.growRewards.contains(Integer.valueOf(gr.level)))) {
			player.addJewels(gr.reward, "growreward");
			this.growRewards.add(Integer.valueOf(gr.level));
		}
	}

	public void GrowRewardBuy(Player player) {
		this.isBuyGrowReward = true;
		player.decJewels(1000, "growreward");
	}

	public boolean isOpenDrawMoney(Player player) {
		return (System.currentTimeMillis() - player.getRegisterTime() >= 172800000L);
	}

	public boolean hasMoreDrawMoney() {
		return (this.drawMoneyCount >= RewardService.drawMoneys.size());
	}

	public long getDrawMoneySurplusTime(Player player) {
		long time = player.getRegisterTime() + 172800000L - System.currentTimeMillis();
		return Math.max(0L, time);
	}

	public int drawMoneyGet(Player player, DrawMoney dm) {
		int addJewels = dm.getReward();
		player.addJewels(addJewels - dm.cost, "drawmoney");
		this.drawMoneyCount += 1;
		return addJewels;
	}

	public void getOnlineReward(Player p, OnlineReward or) {
		if (this.onlineRewardCount == or.count) {
			if ((or.rewards != null) && (or.rewards.size() > 0)) {
				for (Reward r : or.rewards) {
					r.add(p, "getonlinereward");
				}
			}
			int nextCount = or.count + 1;
			if (RewardService.onlineRewards.containsKey(Integer.valueOf(nextCount)))
				this.onlineRewardCount = nextCount;
			else {
				this.onlineRewardCount = -1;
			}
			this.lastGetOnlineReward = System.currentTimeMillis();
			Platform.getEventManager().addEvent(new Event(2098, new Object[] { p }));
		}
	}

	public boolean isGetGlobalChargeReward(int num) {
		return this.globalChargeRewards.contains(Integer.valueOf(num));
	}

	public List<Reward> getGlobalChargeReward(Player player, int num) {
		if (!(this.globalChargeRewards.contains(Integer.valueOf(num)))) {
			List<Reward> list = (List<Reward>) RewardService.globalChargeRewards.get(Integer.valueOf(num));
			if ((list != null) && (list.size() > 0)) {
				for (Reward r : list) {
					r.add(player, "getglobalchargereward");
				}
				this.globalChargeRewards.add(Integer.valueOf(num));
				Platform.getEventManager().addEvent(new Event(2101, new Object[] { player }));
				return list;
			}
		}
		return null;
	}

	public List<Integer> getLevelRewardIds() {
		return this.levelRewardIds;
	}

	public void setLevelRewardIds(List<Integer> levelRewardIds) {
		this.levelRewardIds = levelRewardIds;
	}

	public boolean isGetSignReward() {
		return this.isGetSignReward;
	}

	public void setGetSignReward(boolean isGetSignReward) {
		this.isGetSignReward = isGetSignReward;
	}

	public int getSignCount() {
		return this.signCount;
	}

	public void setSignCount(int signCount) {
		this.signCount = signCount;
	}

	public Set<Integer> getRecovers() {
		return this.recovers;
	}

	public void setRecovers(Set<Integer> recovers) {
		this.recovers = recovers;
	}

	public boolean[][] getLoginInfo() {
		return this.loginInfo;
	}

	public void setLoginInfo(boolean[][] loginInfo) {
		this.loginInfo = loginInfo;
	}

	public int getTouchGoldenCount() {
		return this.touchGoldenCount;
	}

	public void setTouchGoldenCount(int touchGoldenCount) {
		this.touchGoldenCount = touchGoldenCount;
	}

	public boolean isBuyGrowReward() {
		return this.isBuyGrowReward;
	}

	public void setBuyGrowReward(boolean isBuyGrowReward) {
		this.isBuyGrowReward = isBuyGrowReward;
	}

	public Set<Integer> getGrowRewards() {
		return this.growRewards;
	}

	public void setGrowRewards(Set<Integer> growRewards) {
		this.growRewards = growRewards;
	}

	public Map<Integer, Boolean> getLoginAwardInfos() {
		return this.loginAwardInfos;
	}

	public void setLoginAwardInfos(Map<Integer, Boolean> loginAwardInfos) {
		this.loginAwardInfos = loginAwardInfos;
	}

	public int getDrawMoneyCount() {
		return this.drawMoneyCount;
	}

	public void setDrawMoneyCount(int drawMoneyCount) {
		this.drawMoneyCount = drawMoneyCount;
	}

	public long getLastGetOnlineReward() {
		return this.lastGetOnlineReward;
	}

	public void setLastGetOnlineReward(long lastGetOnlineReward) {
		this.lastGetOnlineReward = lastGetOnlineReward;
	}

	public int getOnlineRewardCount() {
		return this.onlineRewardCount;
	}

	public void setOnlineRewardCount(int onlineRewardCount) {
		this.onlineRewardCount = onlineRewardCount;
	}

	public Set<Integer> getGlobalChargeRewards() {
		return this.globalChargeRewards;
	}

	public void setGlobalChargeRewards(Set<Integer> globalChargeRewards) {
		this.globalChargeRewards = globalChargeRewards;
	}

	public Map<Integer, Boolean> getSuperRewardRecord() {
		return this.superRewardRecord;
	}

	public void setSuperRewardRecord(Map<Integer, Boolean> superRewardRecord) {
		this.superRewardRecord = superRewardRecord;
	}

	public int getLogin7DayCount() {
		return this.login7DayCount;
	}

	public void setLogin7DayCount(int login7DayCount) {
		this.login7DayCount = login7DayCount;
	}

	public Set<Integer> getLogin7DayGet() {
		return this.login7DayGet;
	}

	public void setLogin7DayGet(Set<Integer> login7DayGet) {
		this.login7DayGet = login7DayGet;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int i, day;
			int version = in.readInt();

			int size = in.readInt();
			this.levelRewardIds = new ArrayList<Integer>();
			for (i = 0; i < size; ++i) {
				this.levelRewardIds.add(Integer.valueOf(in.readInt()));
			}

			this.signCount = Math.max(in.readInt(), 1);
			this.recovers = new HashSet<Integer>();
			if (version == 1) {
				in.readBoolean();
				in.readBoolean();
			} else if (version > 1) {
				size = in.readInt();
				for (i = 0; i < size; ++i) {
					this.recovers.add(Integer.valueOf(in.readInt()));
				}
			}
			this.loginInfo = new boolean[RewardService.loginRewards.size()][2];

			this.touchGoldenCount = in.readInt();

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.loginInfo[i][0] = in.readBoolean();
				this.loginInfo[i][1] = in.readBoolean();
			}
			this.growRewards = new HashSet<Integer>();
			this.loginAwardInfos = new HashMap<Integer, Boolean>();
			this.globalChargeRewards = new HashSet<Integer>();
			this.superRewardRecord = new HashMap<Integer, Boolean>();
			this.login7DayGet = new HashSet<Integer>();
			this.timelimits = new HashMap<Long, Map<Integer, Integer>>();

			this.isBuyGrowReward = in.readBoolean();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.growRewards.add(Integer.valueOf(in.readInt()));
			}

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				day = in.readInt();
				boolean isGet = in.readBoolean();
				this.loginAwardInfos.put(Integer.valueOf(day), Boolean.valueOf(isGet));
			}

			this.isGetSignReward = in.readBoolean();

			this.drawMoneyCount = in.readInt();
			if (version <= 2)
				return;
			this.trainCount = in.readInt();
			if (version <= 3)
				return;
			this.onlineRewardCount = in.readInt();
			this.lastGetOnlineReward = in.readLong();

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				int num = in.readInt();
				this.globalChargeRewards.add(Integer.valueOf(num));
			}

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.superRewardRecord.put(Integer.valueOf(in.readInt()), Boolean.valueOf(in.readBoolean()));
			}

			this.login7DayCount = in.readInt();
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				day = in.readInt();
				this.login7DayGet.add(Integer.valueOf(day));
			}

			if (version > 4) {
				this.curTimeLimitRewardId = in.readLong();
				this.activateTimeLimitRewardTime = in.readLong();
				this.timeLimitRewardCount = in.readInt();
				if (version > 5) {
					size = in.readInt();
					for (i = 0; i < size; ++i) {
						long tlrId = in.readLong();
						Map<Integer, Integer> items = (Map<Integer, Integer>) this.timelimits.get(Long.valueOf(tlrId));
						if (items == null) {
							items = new HashMap<Integer, Integer>();
							this.timelimits.put(Long.valueOf(tlrId), items);
						}
						int len = in.readInt();
						for (int j = 0; j < len; ++j) {
							items.put(Integer.valueOf(in.readInt()), Integer.valueOf(in.readInt()));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Integer id;
		out.writeInt(6);

		out.writeInt(this.levelRewardIds.size());
		for (Iterator<Integer> localIterator1 = this.levelRewardIds.iterator(); localIterator1.hasNext();) {
			id = (Integer) localIterator1.next();
			out.writeInt(id.intValue());
		}

		out.writeInt(this.signCount);

		out.writeInt(this.recovers.size());
		for (Iterator<Integer> localIterator1 = this.recovers.iterator(); localIterator1.hasNext();) {
			id = (Integer) localIterator1.next();
			out.writeInt(id.intValue());
		}

		out.writeInt(this.touchGoldenCount);

		out.writeInt(this.loginInfo.length);
		for (int i = 0; i < this.loginInfo.length; ++i) {
			out.writeBoolean(this.loginInfo[i][0]);
			out.writeBoolean(this.loginInfo[i][1]);
		}

		out.writeBoolean(this.isBuyGrowReward);
		out.writeInt(this.growRewards.size());
		for (Integer level : this.growRewards) {
			out.writeInt(level.intValue());
		}

		out.writeInt(this.loginAwardInfos.size());
		Set<Entry<Integer, Boolean>> set = this.loginAwardInfos.entrySet();
		for (Map.Entry entry : set) {
			out.writeInt(((Integer) entry.getKey()).intValue());
			out.writeBoolean(((Boolean) entry.getValue()).booleanValue());
		}

		out.writeBoolean(this.isGetSignReward);

		out.writeInt(this.drawMoneyCount);

		out.writeInt(this.trainCount);

		out.writeInt(this.onlineRewardCount);
		out.writeLong(this.lastGetOnlineReward);

		out.writeInt(this.globalChargeRewards.size());
		for (Iterator<Integer> it = this.globalChargeRewards.iterator(); it.hasNext();) {
			int num = it.next();
			out.writeInt(num);
		}

		out.writeInt(this.superRewardRecord.size());
		for (Integer rewardId : this.superRewardRecord.keySet()) {
			boolean isGet = ((Boolean) this.superRewardRecord.get(rewardId)).booleanValue();
			out.writeInt(rewardId.intValue());
			out.writeBoolean(isGet);
		}

		out.writeInt(this.login7DayCount);
		out.writeInt(this.login7DayGet.size());
		for (Iterator<Integer> it = this.login7DayGet.iterator(); it.hasNext();) {
			int day = it.next();
			out.writeInt(day);
		}

		out.writeLong(this.curTimeLimitRewardId);
		out.writeLong(this.activateTimeLimitRewardTime);
		out.writeInt(this.timeLimitRewardCount);
		out.writeInt(this.timelimits.size());
		for (Long tlrId : this.timelimits.keySet()) {
			out.writeLong(tlrId.longValue());
			Map<Integer, Integer> items = (Map<Integer, Integer>) this.timelimits.get(tlrId);
			out.write(items.size());
			for (Integer subId : items.keySet()) {
				out.writeInt(subId.intValue());
				out.writeInt(((Integer) items.get(subId)).intValue());
			}
		}
	}

	public int getBlobId() {
		return 12;
	}

	public int getTrainCount() {
		return this.trainCount;
	}

	public void setTrainCount(int trainCount) {
		this.trainCount = trainCount;
	}

	public List<Reward> receiveSuperReward(Player player) {
		List<Reward> lr = new LinkedList<Reward>();
		for (Integer goodId : this.superRewardRecord.keySet()) {
			if (!(((Boolean) this.superRewardRecord.get(goodId)).booleanValue())) {
				for (Reward r : (ArrayList<Reward>) RewardService.superReward.get(goodId)) {
					r.add(player, "superReward");
					lr.add(r);
				}
				this.superRewardRecord.put(goodId, Boolean.valueOf(true));
			}
		}
		return lr;
	}

	public boolean canGetSuperReward(int goodId) {
		if (this.superRewardRecord.get(Integer.valueOf(goodId)) == null)
			return false;
		return (!(((Boolean) this.superRewardRecord.get(Integer.valueOf(goodId))).booleanValue()));
	}

	public void superRewardHandler(int goodId) {
		if ((RewardService.superReward.get(Integer.valueOf(goodId)) == null)
				|| (this.superRewardRecord.get(Integer.valueOf(goodId)) != null))
			return;
		this.superRewardRecord.put(Integer.valueOf(goodId), Boolean.valueOf(false));
	}

	public boolean hasUnReceivedSuperReward() {
		return this.superRewardRecord.containsValue(Boolean.valueOf(false));
	}

	public boolean isAllReceivedSuperReward() {
		if (this.superRewardRecord.isEmpty()) {
			return false;
		}
		return (!(this.superRewardRecord.containsValue(Boolean.valueOf(false))));
	}

	public List<Integer> getUnReceivedGoodIds() {
		List<Integer> ids = new ArrayList<Integer>();
		for (Integer id : this.superRewardRecord.keySet()) {
			if (!(((Boolean) this.superRewardRecord.get(id)).booleanValue())) {
				ids.add(id);
			}
		}
		return ids;
	}

	public boolean isParticipatedSuperRewardActivity() {
		return (!(this.superRewardRecord.isEmpty()));
	}

	public TimeLimitReward getCurTimeLimitReward() {
		return ((TimeLimitReward) Platform.getEntityManager().getFromEhCache(TimeLimitReward.class.getName(),
				Long.valueOf(this.curTimeLimitRewardId)));
	}

	public void activateTimeLimitRewardIfExist(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 67))) {
			return;
		}
		long now = System.currentTimeMillis();
		TimeLimitReward reward = RewardService.getTimeLimitRewardByActivateTime(now);
		if ((reward != null) && (this.curTimeLimitRewardId != reward.id)) {
			if ((reward.targetType == 1) && (!(reward.targets.contains(Integer.valueOf(player.getId()))))) {
				return;
			}
			this.curTimeLimitRewardId = reward.id;
			this.activateTimeLimitRewardTime = now;
			this.timeLimitRewardCount = 0;
		}
	}

	public long getCurTimeLimitRewardId() {
		return this.curTimeLimitRewardId;
	}

	public void setCurTimeLimitRewardId(long curTimeLimitRewardId) {
		this.curTimeLimitRewardId = curTimeLimitRewardId;
	}

	public long getActivateTimeLimitRewardTime() {
		return this.activateTimeLimitRewardTime;
	}

	public void setActivateTimeLimitRewardTime(long activateTimeLimitRewardTime) {
		this.activateTimeLimitRewardTime = activateTimeLimitRewardTime;
	}

	public int getTimeLimitRewardCount() {
		return this.timeLimitRewardCount;
	}

	public void setTimeLimitRewardCount(int timeLimitRewardCount) {
		this.timeLimitRewardCount = timeLimitRewardCount;
	}
}
