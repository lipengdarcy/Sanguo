package org.darcy.sanguo.union;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.util.Calc;

import sango.packet.PbLeague;

/**
 * 军团
 */
public class League {
	private int id;
	private String name;
	private int level = 1;
	private int rank;
	private int buildValue;
	private String notice = "大家一起来建设军团吧！";

	private int memberLimit = 20;
	private int leader;
	private int shopLevel;
	private int goodsLevel;
	private int bossFacilityLevel;
	private LeagueInfo info = new LeagueInfo();

	Comparator<LeagueMember> boxRankCompar = new Comparator<LeagueMember>() {
		@Override
		public int compare(LeagueMember o1, LeagueMember o2) {
			return (o2.getBoxTotalCount() - o1.getBoxTotalCount());
		}
	};

	public void save() {
		try {
			((DbService) Platform.getServiceManager().get(DbService.class)).update(this);
		} catch (Exception e) {
			Platform.getLog().logError("League save error,id:" + this.id, e);
		}
	}

	public void refreshNewWeek() {
		int times = this.info.getBossweekCount();
		int curReward = -1;
		for (Integer t : LeagueService.bossweekRewards.keySet()) {
			if (times < t.intValue())
				break;
			curReward = t.intValue();
		}
		if (curReward > 0) {
			List rewards = (List) LeagueService.bossweekRewards.get(Integer.valueOf(curReward));
			Set set = this.info.getMembers().keySet();
			for (Iterator localIterator2 = set.iterator(); localIterator2.hasNext();) {
				int playerId = ((Integer) localIterator2.next()).intValue();
				MailService.sendSystemMail(20, playerId, "军团Boss击杀奖励（周）", MessageFormat.format(
						"<p style=21>本周，您的所在军团</p><p style=20>【{0}】</p><p style=21>共击杀军团Boss</p></p style=19>{1}</p><p style=21>次。\n根据本周击杀次数，全军团成员将获得以下奖励：</p>",
						new Object[] { this.name, Integer.valueOf(this.info.getBossweekCount()) }), null, rewards);
			}
		}

		this.info.setBossweekCount(0);
	}

	public void refreshRareGoods() {
		Map<Integer, LeagueRareGoods> map = this.info.getRareGoods();
		map.clear();
		int size = 4;
		if (LeagueService.rareGoods.size() <= size) {
			for (LeagueRareGoodTemplate template : LeagueService.rareGoods.values()) {
				LeagueRareGoods goods = new LeagueRareGoods(template);
				map.put(Integer.valueOf(template.id), goods);
			}
		} else {
			Set<Integer> ids = new HashSet<Integer>();
			Object list = new ArrayList(LeagueService.rareGoods.keySet());
			while (ids.size() < size) {
				int index = Calc.nextInt(((List) list).size());
				ids.add((Integer) ((List) list).get(index));
			}
			for (Iterator localIterator2 = ids.iterator(); localIterator2.hasNext();) {
				int id = ((Integer) localIterator2.next()).intValue();
				LeagueRareGoodTemplate template = (LeagueRareGoodTemplate) LeagueService.rareGoods
						.get(Integer.valueOf(id));
				LeagueRareGoods goods = new LeagueRareGoods(template);
				map.put(Integer.valueOf(template.id), goods);
			}
		}
	}

	public void addMember(int id, String name) {
		LeagueMember lm = new LeagueMember(id, name);
		this.info.getMembers().put(Integer.valueOf(id), lm);
	}

	public boolean isMember(int id) {
		return this.info.getMembers().containsKey(Integer.valueOf(id));
	}

	public LeagueMember getMember(int id) {
		return ((LeagueMember) this.info.getMembers().get(Integer.valueOf(id)));
	}

	public int getMemberCount() {
		return this.info.getMembers().size();
	}

	public int getApplyCount() {
		return this.info.getApplys().size();
	}

	public void quit(int id, int costBuildValue) {
		if (isMember(id)) {
			if (this.info.getViceleaders().contains(Integer.valueOf(id))) {
				this.info.getViceleaders().remove(new Integer(id));
			}
			this.info.getMembers().remove(Integer.valueOf(id));
			this.buildValue -= costBuildValue;
			this.buildValue = Math.max(0, this.buildValue);
		}
	}

	public boolean isFull() {
		return (this.info.getMembers().size() < this.memberLimit);
	}

	public boolean isFullApply() {
		return (this.info.getApplys().size() < LeagueData.LEAGUE_APPLY_MEMBER_LIMIT);
	}

	public boolean isApply(int id) {
		return this.info.getApplys().contains(Integer.valueOf(id));
	}

	public void apply(int id) {
		if (!(this.info.getApplys().contains(Integer.valueOf(id))))
			this.info.getApplys().add(new Integer(id));
	}

	public void removeApply(int id) {
		this.info.getApplys().remove(new Integer(id));
	}

	public boolean isMaxLevel() {
		return (this.level < LeagueData.LEAGUE_MAX_LEVEL);
	}

	public boolean isActivity(LeagueMember lm) {
		int[] arrayOfInt;
		int j = (arrayOfInt = LeagueData.LEAGUE_FACILITY).length;
		for (int i = 0; i < j; ++i) {
			Integer itemId = Integer.valueOf(arrayOfInt[i]);
			if (isActivity(lm, itemId.intValue())) {
				return true;
			}
		}
		return false;
	}

	public boolean isActivity(LeagueMember lm, int itemId) {
		if (itemId == 1) {
			int noBuildDay = getMember(lm.getId())
					.getNoBuildDay(LeagueService.getToday0Time(System.currentTimeMillis()));

			return (noBuildDay < 0);
		}

		if (itemId == 3)
			return (!(lm.isGetGoods()));
		if (itemId == 4) {
			Player p = Platform.getPlayerManager().getPlayerById(lm.getId());

			return ((p == null) || (p.getUnion() == null) || (!(isOpenBoss()))
					|| (p.getUnion().getBossSurplusNum() <= 0) || (p.getUnion().getFightBossColdTime() >= 1L)
					|| (this.info.getBoss().getReviveTime() >= 1L));
		}

		return false;
	}

	public boolean isOpenFacility(int itemId) {
		return ((itemId != 1) && (itemId != 2) && (itemId != 3) && (itemId != 4) && (itemId != 6));
	}

	public int getFacilityLevel(int itemId) {
		if (itemId == 1)
			return this.level;
		if (itemId == 2)
			return this.shopLevel;
		if (itemId == 3)
			return this.goodsLevel;
		if (itemId == 4)
			return this.bossFacilityLevel;
		if (itemId == 7) {
			return this.level;
		}
		return -1;
	}

	public int getNeedExpToNextLevel(int itemId) {
		int expId;
		int level = 0;

		if (itemId == 1) {
			level = this.level;
			expId = LeagueData.LEAGUE_EXP_ID_HALL;
		} else if (itemId == 2) {
			level = this.shopLevel;
			expId = LeagueData.LEAGUE_EXP_ID_SHOP;
		} else if (itemId == 3) {
			level = this.goodsLevel;
			expId = LeagueData.LEAGUE_EXP_ID_GOODS;
		} else if (itemId == 4) {
			level = this.bossFacilityLevel;
			expId = LeagueData.LEAGUE_EXP_ID_BOSS;
		} else {
			return -1;
		}
		ExpService es = (ExpService) Platform.getServiceManager().get(ExpService.class);
		return es.getRestExpToNextLevel(expId, 0, level + 1);
	}

	public void levelUp(int itemId, Player p) {
		int need = getNeedExpToNextLevel(itemId);
		if (itemId == 1) {
			this.level += 1;
			this.memberLimit = LeagueService.getLimit(this.level);
			Platform.getLog().logLeague(this, "leaguelevelup", p.getId());
		} else if (itemId == 2) {
			this.shopLevel += 1;
			Platform.getLog().logLeague(this, "leaguelevelupshop", p.getId());
		} else if (itemId == 3) {
			this.goodsLevel += 1;
			Platform.getLog().logLeague(this, "leaguelevelupgoods", p.getId());
		} else if (itemId == 4) {
			this.bossFacilityLevel += 1;
			Platform.getLog().logLeague(this, "leaguelevelupboss", p.getId());
		} else {
			return;
		}
		this.buildValue -= need;
		if (this.buildValue < 0) {
			this.buildValue = 0;
		}
		Platform.getLeagueManager().sort(this);
	}

	public int getCostBuildValue() {
		int value = this.buildValue;
		ExpService es = (ExpService) Platform.getServiceManager().get(ExpService.class);
		value += es.calTotalExpByLevel(LeagueData.LEAGUE_EXP_ID_HALL, this.level);
		value += es.calTotalExpByLevel(LeagueData.LEAGUE_EXP_ID_SHOP, this.shopLevel);
		value += es.calTotalExpByLevel(LeagueData.LEAGUE_EXP_ID_GOODS, this.goodsLevel);
		value += es.calTotalExpByLevel(LeagueData.LEAGUE_EXP_ID_BOSS, this.bossFacilityLevel);
		return value;
	}

	public void build(Player player, LeagueBuildData bd) {
		if (this.info.getTodayBuildCount() < this.memberLimit) {
			this.buildValue += bd.buildValue;
			this.info.addBuildCount();
		}
		LeagueBuild build = new LeagueBuild(player.getId(), player.getName(), bd.id);
		if (this.info.getBuildRecords().size() >= LeagueData.LEAGUE_NOTICE_NUM) {
			this.info.getBuildRecords().poll();
		}
		this.info.getBuildRecords().offer(build);
		getMember(player.getId()).build(bd);
		player.getUnion().build(player, bd);
		Platform.getEventManager().addEvent(new Event(2096, new Object[] { player }));

		Platform.getLeagueManager().sort(this);
	}

	public void getGoods(Player player, LeagueGoods goods) {
		goods.reward(player);
		LeagueMember lm = getMember(player.getId());
		if (lm != null) {
			lm.getGoods();
		}
		player.getUnion().decContribution(player, LeagueData.LEAGUE_GET_GOODS_COST, "leaguegetgoods");
		Platform.getEventManager().addEvent(new Event(2096, new Object[] { player }));
	}

	public int getFightBossReward() {
		return LeagueService.getBossFightRewards(this.info.getBoss().level);
	}

	public void fightBoss(Player p, int damage, boolean isWin, int money) {
		this.info.getBoss().fightBoss(p, damage, isWin, money);
		p.getUnion().fightBoss();
		Platform.getEventManager().addEvent(new Event(2096, new Object[] { p }));
	}

	public void refreshNewDay() {
		this.info.setTodayBuildCount(0);
		this.info.setLotteryRefreshTimes(0);
		Calendar cal = Calendar.getInstance();
		if (cal.get(7) == 2) {
			refreshLeagueBox();
		}
		this.info.getLuckyDogs().clear();
	}

	public void createBox() {
		int count = this.info.getBoxCount();
		++count;
		if (LeagueService.boxScore.containsKey(Integer.valueOf(count))) {
			int build = ((Integer) LeagueService.boxScore.get(Integer.valueOf(count))).intValue();
			this.buildValue += build;
		}
		this.info.setBoxCount(count);
	}

	public int getBoxExchangeSurplus(int id) {
		LeagueMember lm = getMember(id);
		if (lm == null) {
			return 0;
		}
		return (this.info.getBoxCount() - lm.getBoxExchangeCount());
	}

	public int getBoxGetSurplus(int id) {
		LeagueMember lm = getMember(id);
		if (lm == null) {
			return 0;
		}
		return (lm.getBoxCount() - lm.getBoxGetCount());
	}

	public List<PbLeague.LeagueBoxRanker> getBoxRankers() {
		List list = new ArrayList(this.info.getMembers().values());
		Collections.sort(list, this.boxRankCompar);

		List results = new ArrayList();
		for (int i = 0; i < list.size(); ++i) {
			LeagueMember lm = (LeagueMember) list.get(i);
			MiniPlayer p = Platform.getPlayerManager().getMiniPlayer(lm.getId());
			if (p != null) {
				PbLeague.LeagueBoxRanker.Builder b = PbLeague.LeagueBoxRanker.newBuilder();
				b.setId(lm.getId());
				b.setName(lm.getName());
				b.setBtlCapability(p.getBtlCapability());
				b.setRank(i + 1);
				b.setJob(PbLeague.LeagueJob.valueOf(lm.getJob(this)));
				b.setContribution(lm.getTotalContribution());
				b.setLevel(p.getLevel());
				b.setCount(lm.getBoxTotalCount());
				results.add(b.build());
			}
		}
		return results;
	}

	public void refreshLeagueBox() {
		this.info.setBoxCount(0);
		for (LeagueMember lm : this.info.getMembers().values())
			lm.refreshLeagueBox();
	}

	public void refreshBoss() {
		this.info.getBoss().refresh(this);
	}

	public boolean isOpenBoss() {
		return (this.level < LeagueData.LEAGUE_BOSS_OPEN_LEVEL);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getBuildValue() {
		return this.buildValue;
	}

	public void setBuildValue(int buildValue) {
		this.buildValue = buildValue;
	}

	public String getNotice() {
		return this.notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public int getLeader() {
		return this.leader;
	}

	public void setLeader(int leader) {
		this.leader = leader;
	}

	public int getShopLevel() {
		return this.shopLevel;
	}

	public void setShopLevel(int shopLevel) {
		this.shopLevel = shopLevel;
	}

	public int getGoodsLevel() {
		return this.goodsLevel;
	}

	public void setGoodsLevel(int goodsLevel) {
		this.goodsLevel = goodsLevel;
	}

	public int getMemberLimit() {
		return this.memberLimit;
	}

	public void setMemberLimit(int memberLimit) {
		this.memberLimit = memberLimit;
	}

	public int getBossFacilityLevel() {
		return this.bossFacilityLevel;
	}

	public void setBossFacilityLevel(int bossFacilityLevel) {
		this.bossFacilityLevel = bossFacilityLevel;
	}

	public LeagueInfo getInfo() {
		return this.info;
	}

	public void setInfo(LeagueInfo info) {
		this.info = info;
	}

	public PbLeague.League genLeague() {
		PbLeague.League.Builder b = PbLeague.League.newBuilder();
		b.setId(this.id);
		b.setName(this.name);
		b.setLeaderName(getMember(this.leader).getName());
		b.setRank(this.rank);
		b.setLevel(this.level);
		b.setMembers(this.info.getMembers().size());
		b.setLimit(this.memberLimit);
		return b.build();
	}

	public PbLeague.MyMiniLeague genMyMiniLeague() {
		return PbLeague.MyMiniLeague.newBuilder().setBuildValue(this.buildValue).build();
	}

	public PbLeague.ListLeague genListLeague(Player player) {
		PbLeague.ListLeague.Builder b = PbLeague.ListLeague.newBuilder();
		b.setLeague(genLeague());
		if (player.getUnion().getLeagueId() < 1) {
			b.setApply(player.getUnion().getApplys().contains(Integer.valueOf(this.id)));
		}
		return b.build();
	}

	public PbLeague.LeagueFacility genLeagueFacility(Player player, int itemId) {
		PbLeague.LeagueFacility.Builder b = PbLeague.LeagueFacility.newBuilder();
		b.setItem(PbLeague.LeagueFacilityItem.valueOf(itemId));
		b.setOpen(isOpenFacility(itemId));
		b.setIsActivity(isActivity(getMember(player.getId()), itemId));
		int level = getFacilityLevel(itemId);
		if (level != -1) {
			b.setLevel(level);
		}
		if (itemId == 1) {
			if (isMaxLevel())
				b.setNextExp(-1);
			else
				b.setNextExp(getNeedExpToNextLevel(itemId));
		} else if (itemId == 2) {
			if ((this.shopLevel >= this.level) && (isMaxLevel()))
				b.setNextExp(-1);
			else {
				b.setNextExp(getNeedExpToNextLevel(itemId));
			}
		} else if (itemId == 3) {
			if ((this.goodsLevel >= this.level) && (isMaxLevel()))
				b.setNextExp(-1);
			else
				b.setNextExp(getNeedExpToNextLevel(itemId));
		} else if (itemId == 4) {
			if ((this.bossFacilityLevel >= this.level) && (isMaxLevel()))
				b.setNextExp(-1);
			else
				b.setNextExp(getNeedExpToNextLevel(itemId));
		} else if (itemId == 7) {
			b.setNextExp(-1);
		}
		return b.build();
	}
}
