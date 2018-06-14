package org.darcy.sanguo.arena;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.ArenaService;
import org.darcy.sanguo.service.MailService;

import sango.packet.PbArena;

public class Arena {
	public static final int MAX_CHALLENGE_INFO_COUNT = 3;
	public static int[] rewardTime = { 22 };
	public static final long REWARD_LAST_TIME = 600000L;
	private int curId;
	private AtomicInteger idGen;
	private int playerId;
	private int rank;
	private ArenaInfo info;
	public List<Integer> rivalIds = new ArrayList();

	public Arena() {
	}

	public Arena(int rank) {
		this.rank = rank;
		this.info = new ArenaInfo();
		this.idGen = new AtomicInteger(this.curId);

		Calendar rewardCal = getActualRewardCalendar(Calendar.getInstance());
		RankInfo rankInfo = new RankInfo(getId(), rank, rewardCal.getTimeInMillis());
		this.info.getRankInfos().put(Integer.valueOf(rankInfo.id), rankInfo);
	}

	public int getId() {
		this.curId = this.idGen.incrementAndGet();
		return this.curId;
	}

	public void init(Player player) {
		if (this.idGen == null) {
			this.idGen = new AtomicInteger(this.curId);
		}

		if (player.getSession() != null) {
			addInfo(this.rank, false, true, null);
			reward(player);
		}
	}

	public void reward(Player player) {
		long now = System.currentTimeMillis();
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTimeInMillis(now);
		Calendar actualRewardCal = getActualRewardCalendar(nowCal);
		Iterator itx = this.info.getRankInfos().keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			RankInfo info = (RankInfo) this.info.getRankInfos().get(Integer.valueOf(id));
			if (info.time < actualRewardCal.getTimeInMillis()) {
				int rank = info.rank;
				List list = ((ArenaService) Platform.getServiceManager().get(ArenaService.class)).getRewards(rank,
						info.time);
				if (list != null) {
					MailService.sendSystemMail(4, player.getId(), "竞技场每日排名奖励", MessageFormat.format(
							"<p style=21>恭喜主公，截止到今天22:00,您在竞技场中获得第</p><p style=20>{0}</p><p style=21>排名的好成绩，获得奖励如下：</p>",
							new Object[] { Integer.valueOf(rank) }), new Date(info.time), list);
				}
				itx.remove();
			}
		}
	}

	public void addInfo(int rank, boolean beChallenged, boolean isWin, String name) {
		long now = System.currentTimeMillis();
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTimeInMillis(now);

		RankInfo latest = getLatestRankInfo(this.info.getRankInfos().values());
		Calendar actualRewardCal = getActualRewardCalendar(nowCal);
		Calendar prevRewardCal = getPrevActualRewardCalendar(nowCal);
		RankInfo comparator = null;
		if (actualRewardCal.getTimeInMillis() == latest.time) {
			if (this.info.getRankInfos().size() > 1) {
				Map tmp = new HashMap(this.info.getRankInfos());
				tmp.remove(Integer.valueOf(latest.id));
				comparator = getLatestRankInfo(tmp.values());
			} else {
				comparator = latest;
			}
		} else {
			comparator = latest;
			RankInfo info = new RankInfo(getId(), rank, actualRewardCal.getTimeInMillis());
			this.info.getRankInfos().put(Integer.valueOf(info.id), info);
		}
		if (comparator.time < prevRewardCal.getTimeInMillis()) {
			Calendar laterCal = Calendar.getInstance();
			laterCal.setTimeInMillis(comparator.time);
			int apartDay = calApartDayByTwoTime(laterCal, prevRewardCal);
			int reserveNum = 14;
			apartDay = Math.min(apartDay, reserveNum);
			for (int i = 0; i < apartDay; ++i) {
				Calendar tmpCal = Calendar.getInstance();
				tmpCal.setTimeInMillis(prevRewardCal.getTimeInMillis());
				tmpCal.add(5, i * -1);
				RankInfo rankInfo = new RankInfo(getId(), comparator.rank, tmpCal.getTimeInMillis());
				this.info.getRankInfos().put(Integer.valueOf(rankInfo.id), rankInfo);
			}
			checkRankInfos();
		} else {
			comparator.rank = rank;
		}

		if (beChallenged) {
			BeChallengedInfo beChallengedInfo = new BeChallengedInfo(getId(), isWin, now, name);
			checkBeChallengedInfos();
			this.info.getChallengeInfos().put(Integer.valueOf(beChallengedInfo.id), beChallengedInfo);
		}
	}

	private void checkRankInfos() {
		int reserveNum = 14;
		if (this.info.getRankInfos().size() > reserveNum) {
			List list = new ArrayList(this.info.getRankInfos().values());
			Collections.sort(list, new Comparator<RankInfo>() {
				public int compare(RankInfo o1, RankInfo o2) {
					return (int) (o1.time - o2.time);
				}
			});
			for (int i = 0; i < this.info.getRankInfos().size() - reserveNum; ++i)
				this.info.getRankInfos().remove(Integer.valueOf(((RankInfo) list.get(i)).id));
		}
	}

	private void checkBeChallengedInfos() {
		if (this.info.getChallengeInfos().size() == 3) {
			int clearId = 0;
			BeChallengedInfo prevInfo = null;
			Iterator itx = this.info.getChallengeInfos().keySet().iterator();
			while (itx.hasNext()) {
				int id = ((Integer) itx.next()).intValue();
				BeChallengedInfo info = (BeChallengedInfo) this.info.getChallengeInfos().get(Integer.valueOf(id));
				if (prevInfo == null) {
					prevInfo = info;
					clearId = info.id;
				} else if (info.time < prevInfo.time) {
					prevInfo = info;
					clearId = info.id;
				}
			}

			this.info.getChallengeInfos().remove(Integer.valueOf(clearId));
		}
	}

	private RankInfo getLatestRankInfo(Collection<RankInfo> list) {
		RankInfo result = null;
		for (RankInfo info : list) {
			if (result == null) {
				result = info;
			} else if (info.time > result.time) {
				result = info;
			}
		}

		return result;
	}

	private int calApartDayByTwoTime(Calendar start, Calendar end) {
		Calendar startRewardCalendar = getActualRewardCalendar(start);
		Calendar endRewardCalendar = getActualRewardCalendar(end);
		return (int) ((endRewardCalendar.getTimeInMillis() - startRewardCalendar.getTimeInMillis()) / 86400000L);
	}

	public static Calendar getRewardCalendar(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, rewardTime[0], rewardTime[1], rewardTime[2]);
		cal.set(14, 0);
		return cal;
	}

	public static Calendar getActualRewardCalendar(Calendar cal) {
		Calendar thisRewardCal = getRewardCalendar(cal.get(1), cal.get(2), cal.get(5));
		Calendar objCal = Calendar.getInstance();
		objCal.setTimeInMillis(thisRewardCal.getTimeInMillis());
		if (cal.getTime().after(thisRewardCal.getTime())) {
			objCal.add(5, 1);
		}
		return objCal;
	}

	private Calendar getPrevActualRewardCalendar(Calendar cal) {
		Calendar actual = getActualRewardCalendar(cal);
		actual.add(5, -1);
		return actual;
	}

	public int getRestTimeToRwardTime() {
		Calendar now = Calendar.getInstance();
		Calendar reward = getActualRewardCalendar(now);
		int rest = (int) (reward.getTimeInMillis() - now.getTimeInMillis());
		if (rest < 85800000L) {
			return rest;
		}
		return 0;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public ArenaInfo getInfo() {
		return this.info;
	}

	public void setInfo(ArenaInfo info) {
		this.info = info;
	}

	public int getCurId() {
		return this.curId;
	}

	public void setCurId(int curId) {
		this.curId = curId;
	}

	public void save() {
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this);
	}

	public static PbArena.ArenaRival genArenaRival(MiniPlayer player, int rank) {
		PbArena.ArenaRival.Builder builder = PbArena.ArenaRival.newBuilder();
		builder.setUser(player.genMiniUser());
		builder.setRank(rank);
		List<Reward> list = ((ArenaService) Platform.getServiceManager().get(ArenaService.class)).getRewards(rank,
				System.currentTimeMillis());
		if ((list != null) && (list.size() > 0)) {
			for (Reward reward : list) {
				builder.addReward(reward.genPbReward());
			}
		}
		return builder.build();
	}
}
