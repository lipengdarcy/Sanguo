package org.darcy.sanguo.log;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.awardcenter.Award;
import org.darcy.sanguo.coup.CoupRecord;
import org.darcy.sanguo.destiny.DestinyRecord;
import org.darcy.sanguo.divine.DivineRecord;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.hero.Formation;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.mail.Mail;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.pay.PayPull;
import org.darcy.sanguo.pay.Receipt;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.tactic.Tactic;
import org.darcy.sanguo.tactic.TacticRecord;
import org.darcy.sanguo.task.Task;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.tower.TowerRecord;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.LeagueMember;
import org.darcy.sanguo.union.LeagueService;
import org.darcy.sanguo.union.combat.City;
import org.darcy.sanguo.union.combat.Pair;
import org.slf4j.Logger;

public class LogManager implements EventHandler {
	public static final char CR = 44;
	private static HashMap<String, Log> logs = new HashMap<String, Log>();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public LogManager() {
		Platform.getEventManager().registerListener(this);
		new Crontab("0 0 0", 1002);
	}

	public void init() {
		try {
			URL url = LogManager.class.getClassLoader().getResource("cfg/log4j.properties");
			PropertyConfigurator.configure(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logs.put("rIn", new LogOnce("rIn"));
		logs.put("rOut", new LogOnce("rOut"));

		logs.put("world", new Log("world"));
		logs.put("combat", new Log("combat"));
		logs.put("warn", new Log("warn"));
		logs.put("error", new Log("error"));
		logs.put("system", new Log("system"));
		logs.put("net", new Log("net"));

		logs.put("login", new Log("login"));
		logs.put("regist", new Log("regist"));
		logs.put("logout", new Log("logout"));
		logs.put("levelup", new Log("levelup"));
		logs.put("recruit", new Log("recruit"));
		logs.put("acquire", new Log("acquire"));
		logs.put("cost", new Log("cost"));
		logs.put("task", new Log("task"));
		logs.put("getaward", new Log("getaward"));
		logs.put("getitem", new Log("getitem"));
		logs.put("removeitem", new Log("removeitem"));
		logs.put("pvefight", new Log("pvefight"));
		logs.put("treasure", new Log("treasure"));
		logs.put("equipment", new Log("equipment"));
		logs.put("warrior", new Log("warrior"));
		logs.put("stage", new Log("stage"));
		logs.put("fellow", new Log("fellow"));
		logs.put("tactic", new Log("tactic"));
		logs.put("destiny", new Log("destiny"));
		logs.put("arena", new Log("arena"));
		logs.put("competition", new Log("competition"));
		logs.put("star", new Log("star"));
		logs.put("tower", new Log("tower"));
		logs.put("equip", new Log("equip"));
		logs.put("divine", new Log("divine"));
		logs.put("coup", new Log("coup"));
		logs.put("ban", new Log("ban"));
		logs.put("online", new Log("online"));
		logs.put("charge", new Log("charge"));
		logs.put("guide", new Log("guide"));
		logs.put("mail", new Log("mail"));
		logs.put("paycheck", new Log("paycheck"));
		logs.put("league", new Log("league"));
		logs.put("train", new Log("train"));
		logs.put("inherit", new Log("inherit"));
		logs.put("leagueboss", new Log("leagueboss"));
		logs.put("paypush", new Log("paypush"));
		logs.put("gm", new Log("gm"));
		logs.put("leaguebox", new Log("leaguebox"));
		logs.put("leaguecombat", new Log("leaguecombat"));
		logs.put("leaguecombatend", new Log("leaguecombatend"));
	}

	public void logLeagueBox(Player player, String opt) {
		Logger log = getLog("leaguebox").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("leaguebox");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		sb.append(',').append(l.getId());
		sb.append(',').append(l.getName());
		sb.append(',').append(l.getLevel());
		sb.append(',').append(l.getInfo().getBoxCount());
		LeagueMember lm = l.getMember(player.getId());
		sb.append(',').append(lm.getBoxTotalCount());
		sb.append(',').append(lm.getBoxCount());
		sb.append(',').append(lm.getBoxGetCount());
		sb.append(',').append(lm.getBoxExchangeCount());
		sb.append(',').append(opt);
		log.info(sb.toString());
	}

	public void logLeagueCombat(League off, League deff, City city, String optType, Player actor, Player target,
			boolean result) {
		try {
			Logger log = getLog("leaguecombat").logger;
			StringBuffer sb = new StringBuffer();
			sb.append(',').append("leaguecombat");
			sb.append(',').append(Configuration.serverId);
			if (city == null)
				sb.append(',').append("混战区");
			else {
				sb.append(',').append(city.getLogName());
			}
			sb.append(',').append(optType);
			sb.append(',').append(off.getName());
			sb.append(',').append(actor.getName());
			sb.append(',').append(actor.getId());
			sb.append(',').append(deff.getName());
			if (target != null) {
				sb.append(',').append(target.getName());
				sb.append(',').append(target.getId());
			} else {
				sb.append(',').append(-1);
				sb.append(',').append(-1);
			}
			sb.append(',').append(result);
			log.info(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logLeagueCombatEnd(League off, League deff, Pair pair) {
		try {
			Logger log = getLog("leaguecombatend").logger;
			StringBuffer sb = new StringBuffer();
			sb.append(',').append("leaguecombatend");
			sb.append(',').append(Configuration.serverId);
			sb.append(',').append(pair.getLastMiniSeconds() / 1000L);
			sb.append(',').append(off.getName());
			sb.append(',').append(off.getLevel());
			sb.append(',').append(off.getInfo().getMembers().size());
			sb.append(',').append(pair.getOffenScore());
			sb.append(',').append(pair.getAccumulateScore(off.getId()));
			if (deff != null) {
				sb.append(',').append(deff.getName());
				sb.append(',').append(deff.getLevel());
				sb.append(',').append(deff.getInfo().getMembers().size());
				sb.append(',').append(pair.getDeffenScore());
				sb.append(',').append(pair.getAccumulateScore(deff.getId()));
			} else {
				sb.append(',').append(-1);
				sb.append(',').append(-1);
				sb.append(',').append(-1);
				sb.append(',').append(-1);
				sb.append(',').append(-1);
			}
			sb.append(',').append(pair.isWin(off.getId()));
			log.info(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeLog(String type) {
		logs.remove(type);
	}

	private Log getLog(String logtype) {
		return ((Log) logs.get(logtype));
	}

	public void logSystem(String message) {
		Logger log = getLog("system").logger;
		log.warn(message);
	}

	public void logWarn(String warn) {
		Logger log = getLog("warn").logger;
		log.warn(warn);
	}

	public void logWarn(Throwable obj) {
		Logger log = getLog("warn").logger;
		log.error("-------warn start------");
		log.error(obj.toString());
		for (StackTraceElement e : obj.getStackTrace()) {
			log.warn(e.toString());
		}
		log.error("-------warn end------");
	}

	public void logError(String message) {
		Logger log = getLog("error").logger;
		log.error(message);
	}

	public void logError(Throwable obj) {
		Logger log = getLog("error").logger;
		log.error("-------error start------");
		log.error(obj.toString());
		for (StackTraceElement e : obj.getStackTrace()) {
			log.error(e.toString());
		}
		log.error("-------error end------");
	}

	public void logError(String message, Throwable obj) {
		Logger log = getLog("error").logger;
		log.error("-------error start------");
		log.error(message);
		for (StackTraceElement e : obj.getStackTrace()) {
			log.error(e.toString());
		}
		log.error("-------error end------");
	}

	public void logWorld(Object obj) {
		Logger log = getLog("world").logger;
		if (obj instanceof Exception) {
			for (StackTraceElement e : ((Exception) obj).getStackTrace())
				log.info(e.toString());
		} else
			log.info(obj.toString());
	}

	public void logCombat(Object obj) {
		Logger log = getLog("combat").logger;
		log.info(obj.toString());
	}

	public void logNet(String obj) {
		Logger log = getLog("net").logger;
		log.info(obj.toString());
	}

	public void logRecordIn(Object obj) {
		LogOnce log = (LogOnce) getLog("rIn");
		log.logCombat(obj.toString());
	}

	public void logRecordOut(Object obj) {
		LogOnce log = (LogOnce) getLog("rOut");
		log.logCombat(obj.toString());
	}

	public void logLogin(Player player) {
		Logger log = getLog("login").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("login");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(player.getSession().getIp());
		if (player.getSession().getAccount() == null) {
			sb.append(',').append(1);
			sb.append(',').append("GUEST");
		} else {
			sb.append(',').append(player.getSession().getAccount().getChannelType());
			sb.append(',').append(player.getSession().getAccount().getDeviceId());
		}
		log.info(sb.toString());
	}

	public void logLogout(Player player) {
		Logger log = getLog("logout").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("logout");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(player.getChannelType());
		sb.append(',').append(this.sdf.format(new Date(player.getRegisterTime())));
		sb.append(getPlayerInfo(player));
		sb.append(',').append(player.getCharge());
		sb.append(',').append(player.getJewels());
		sb.append(',').append((System.currentTimeMillis() - player.getLastLogin().getTime()) / 1000L);
		sb.append(',').append(player.getVitality());
		sb.append(',').append(player.getStamina());
		sb.append(',').append(player.getMoney());
		sb.append(',').append(player.getBtlCapability());
		sb.append(',').append(Platform.getTopManager().getRank(2, player.getId()));
		sb.append(',').append(player.getVip().level);
		log.info(sb.toString());
	}

	public void logRegist(Player player) {
		Logger log = getLog("regist").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("regist");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(player.getAccountId());
		sb.append(',').append(player.getName());
		sb.append(',').append(player.getId());
		sb.append(',').append(player.getSession().getIp());
		if (player.getSession().getAccount() == null)
			sb.append(',').append(1);
		else {
			sb.append(',').append(player.getSession().getAccount().getChannelType());
		}
		log.info(sb.toString());
	}

	public void logLevelUp(Player player, int level) {
		Logger log = getLog("levelup").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("levelup");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(level);
		log.info(sb.toString());
	}

	public void logRecruit(Player player, String recruitType, String costType) {
		Logger log = getLog("recruit").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("recruit");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(recruitType);
		sb.append(',').append(costType);
		log.info(sb.toString());
	}

	public void logAcquire(Player player, String dropType, int acquire, int value, String type) {
		Logger log = getLog("acquire").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("acquire");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(dropType);
		sb.append(',').append(acquire);
		sb.append(',').append(value);
		sb.append(',').append(type);
		log.info(sb.toString());
	}

	public void logCost(Player player, String dropType, int cost, int value, String type) {
		Logger log = getLog("cost").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("cost");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(dropType);
		sb.append(',').append(cost);
		sb.append(',').append(value);
		sb.append(',').append(type);
		log.info(sb.toString());
	}

	public void logTask(Player player, Task task, String opt) {
		Logger log = getLog("task").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("task");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(task.getId());
		sb.append(',').append(task.getTemplate().name);
		sb.append(',').append(opt);
		log.info(sb.toString());
	}

	public void logGetAward(int playerId, Award award) {
		Logger log = getLog("getaward").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("getaward");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(playerId);
		sb.append(',').append(award.getId());
		sb.append(',').append(award.getOptType());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(award.getTime());
		sb.append(',').append(this.sdf.format(cal.getTime()));
		log.info(sb.toString());
	}

	public void logRemoveItem(Player player, int templateId, String name, int id, int count, int total,
			String optType) {
		Logger log = getLog("removeitem").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("removeitem");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(templateId);
		sb.append(',').append(name);
		sb.append(',').append(count);
		sb.append(',').append(total);
		sb.append(',').append(id);
		sb.append(',').append(optType);
		log.info(sb.toString());
	}

	public void logGetItem(Player player, Item item, int count, int total, String optType) {
		Logger log = getLog("getitem").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("getitem");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(item.getTemplateId());
		sb.append(',').append(item.getName());
		sb.append(',').append(count);
		sb.append(',').append(total);
		sb.append(',').append(item.getId());
		sb.append(',').append(optType);
		log.info(sb.toString());
	}

	public void logPveFight(Player player, MapTemplate mt, StageTemplate st, boolean result, int count, int surplus) {
		Logger log = getLog("pvefight").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("pvefight");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(mt.id);
		sb.append(',').append(mt.name);
		sb.append(',').append(st.id);
		sb.append(',').append(st.name);
		sb.append(',').append(result);
		sb.append(',').append(count);
		sb.append(',').append(surplus);
		log.info(sb.toString());
	}

	public void logTreasure(Player player, Treasure t, String opt, int value) {
		Logger log = getLog("treasure").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("treasure");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getItemInfo(t));
		sb.append(',').append(t.getExp());
		sb.append(',').append(t.getEnhanceLevel());
		sb.append(',').append(opt);
		sb.append(',').append(value);
		log.info(sb.toString());
	}

	public void logEquipment(Player player, Equipment e, String opt, int value) {
		Logger log = getLog("equipment").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("equipment");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getItemInfo(e));
		sb.append(',').append(e.getPolishCount());
		sb.append(',').append(e.getForgeLevel());
		sb.append(',').append(opt);
		sb.append(',').append(value);
		log.info(sb.toString());
	}

	public void logWarrior(Player player, Warrior w, String opt, int value) {
		Logger log = getLog("warrior").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("warrior");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getItemInfo(w));
		sb.append(',').append(w.getAdvanceLevel());
		sb.append(',').append(opt);
		sb.append(',').append(value);
		log.info(sb.toString());
	}

	public void logStage(Player player, int onId, int downId) {
		Logger log = getLog("stage").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("stage");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(onId);
		sb.append(',').append(downId);
		Warrior[] ws = player.getWarriors().getStands();
		for (int i = 0; i < ws.length; ++i) {
			Warrior w = ws[i];
			if (w == null) {
				sb.append(',').append(-1);
				sb.append(',').append("null");
			} else {
				sb.append(',').append(w.getId());
				sb.append(',').append(w.getName());
			}
		}
		log.info(sb.toString());
	}

	public void logFellow(Player player, int onId, int downId) {
		Logger log = getLog("fellow").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("fellow");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(onId);
		sb.append(',').append(downId);
		Map map = player.getWarriors().getFriends();
		for (int i = 0; i < Formation.openFriendByLevel.size(); ++i) {
			int index = i + 1;
			Warrior w = (Warrior) map.get(Integer.valueOf(index));
			if (w == null) {
				sb.append(',').append(-1);
				sb.append(',').append("null");
			} else {
				sb.append(',').append(w.getId());
				sb.append(',').append(w.getName());
			}
		}
		log.info(sb.toString());
	}

	public void logTactic(Player player, String opt) {
		Logger log = getLog("tactic").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("tactic");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		TacticRecord tr = player.getTacticRecord();
		List list = new ArrayList(tr.getTactics().values());
		for (int i = 0; i < 5; ++i) {
			if (i >= list.size()) {
				sb.append(',').append("null");
				sb.append(',').append(-1);
			} else {
				Tactic t = (Tactic) list.get(i);
				sb.append(',').append(t.getTemplate().name);
				sb.append(',').append(t.getLevel());
			}
		}
		sb.append(',').append(opt);
		sb.append(',').append(tr.getSurplusPoint());
		sb.append(',').append((tr.getSelectTactic() == null) ? null : tr.getSelectTactic().getTemplate().name);
		log.info(sb.toString());
	}

	public void logDestiny(Player player, int cost) {
		Logger log = getLog("destiny").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("destiny");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		DestinyRecord dr = player.getDestinyRecord();
		sb.append(',').append(dr.getCurrBreakId());
		sb.append(',').append(dr.getCurrDestinyId());
		sb.append(',').append(dr.getLeftStars());
		sb.append(',').append(cost);
		log.info(sb.toString());
	}

	public void logArena(Player player, boolean isChallenge, Player rival, boolean isWin) {
		Logger log = getLog("arena").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("arena");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(isChallenge);
		sb.append(',').append(rival.getId());
		sb.append(',').append(rival.getName());
		sb.append(',').append(isWin);
		sb.append(',').append(player.getArena().getRank());
		sb.append(',').append(rival.getArena().getRank());
		log.info(sb.toString());
	}

	public void logCompetition(Player player, boolean isChallenge, Player rival, boolean isWin) {
		Logger log = getLog("competition").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("competition");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(isChallenge);
		sb.append(',').append(rival.getId());
		sb.append(',').append(rival.getName());
		sb.append(',').append(isWin);
		sb.append(',').append(player.getWorldCompetition().getScore());
		sb.append(',').append(player.getWorldCompetition().getRank());
		sb.append(',').append(player.getPool().getInt(2, 20));
		log.info(sb.toString());
	}

	public void logStar(Player player, int heroId, int itemId, int addExp, boolean isCrit) {
		Logger log = getLog("star").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("star");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getStarInfo(player, heroId));
		sb.append(',').append(itemId);
		sb.append(',').append(ItemService.getItemTemplate(itemId).name);
		sb.append(',').append(addExp);
		sb.append(',').append(isCrit);
		log.info(sb.toString());
	}

	public void logTower(Player player, String opt) {
		Logger log = getLog("tower").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("tower");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		TowerRecord tr = player.getTowerRecord();
		sb.append(',').append(tr.getMaxLevel());
		sb.append(',').append(tr.getCurrentLevel());
		sb.append(',').append(tr.getLeftChallengeTimes());
		sb.append(',').append(tr.getLeftFreeResetTimes());
		sb.append(',').append(opt);
		log.info(sb.toString());
	}

	public void logEquip(Player player, Item item, Warrior w, String opt) {
		Logger log = getLog("equip").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("equip");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getItemInfo(item));
		sb.append(',').append(opt);
		sb.append(',').append(w.getTemplateId());
		sb.append(',').append(w.getId());
		sb.append(',').append(w.getName());
		log.info(sb.toString());
	}

	public void logDivine(Player player, String opt) {
		Logger log = getLog("divine").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("divine");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		DivineRecord dr = player.getDivineRecord();
		sb.append(',').append(dr.getLeftDivineTimes());
		sb.append(',').append(dr.getTotalScores());
		sb.append(',').append(-1);
		sb.append(',').append(opt);
		log.info(sb.toString());
	}

	public void logCoup(Player player, String opt) {
		int[] arrayOfInt;
		Logger log = getLog("coup").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("coup");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		CoupRecord cr = player.getCoupRecord();
		int j = (arrayOfInt = cr.getCoups()).length;
		for (int i = 0; i < j; ++i) {
			Integer level = Integer.valueOf(arrayOfInt[i]);
			sb.append(',').append(level);
		}
		sb.append(',').append(-1);
		sb.append(',').append(opt);
		log.info(sb.toString());
	}

	public void logBan(String type, String key, long start, int time, String reason) {
		Logger log = getLog("ban").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("ban");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(type);
		sb.append(',').append(key);
		if (start > 0L)
			sb.append(',').append(this.sdf.format(new Date(start)));
		else {
			sb.append(',').append(start);
		}
		sb.append(',').append(time);
		sb.append(',').append(reason);
		log.info(sb.toString());
	}

	public void logOnline(int count) {
		Logger log = getLog("online").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("online");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(count);
		log.info(sb.toString());
	}

	public void logCharge(Player player, Receipt r) {
		Logger log = getLog("charge").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("charge");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(r.getChannel());
		sb.append(',').append(r.getOrderId());
		sb.append(',').append(r.getGoodsId());
		sb.append(',').append(r.getCoGoodsId());
		sb.append(',').append(r.getPrice());
		if (player.getSession() != null)
			sb.append(',').append(player.getSession().getIp());
		else {
			sb.append(',').append("null");
		}
		log.info(sb.toString());
	}

	public void logGuide(Player player, String guide) {
		Logger log = getLog("guide").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("guide");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(guide);
		log.info(sb.toString());
	}

	public void logMail(Mail m) {
		Logger log = getLog("mail").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("mail");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(m.getTargetId());
		sb.append(',').append(m.getType());
		sb.append(',').append(this.sdf.format(m.getSendTime()));
		sb.append(',').append(m.getSourceId());
		sb.append(',').append(m.getTitle());
		String attachment = "无";
		if (m.getAttachment() != null) {
			StringBuilder sb2 = new StringBuilder();
			for (Reward r : m.getAttachment().getRewards()) {
				sb2.append(r).append("|");
			}
			attachment = sb2.toString();
			if (attachment.length() > 0) {
				attachment = attachment.substring(0, attachment.length() - 1);
			}
		}
		sb.append(',').append(attachment);
		log.info(sb.toString());
	}

	public void logPayCheck(Player player, String orderId, List<String> params) {
		Logger log = getLog("paycheck").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("paycheck");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append((orderId == null) ? "null" : orderId);
		if (params != null) {
			for (String param : params)
				sb.append(',').append(param);
		} else {
			sb.append(',').append("null");
		}
		log.info(sb.toString());
	}

	public void logPayPush(PayPull pull) {
		Logger log = getLog("paypush").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("paypush");
		sb.append(',').append(Configuration.serverId);
		sb.append(',').append(pull.getNumber());
		sb.append(',').append(pull.getChannel());
		log.info(sb.toString());
	}

	public void logLeague(League l, String opt, int playerId) {
		Logger log = getLog("league").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("league");
		sb.append(',').append(Configuration.serverId);
		sb.append(getLeagueInfo(l));
		sb.append(',').append(opt);
		sb.append(',').append(playerId);
		log.info(sb.toString());
	}

	public void logTrain(Player player, int heroId, int index, int cost) {
		Logger log = getLog("train").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("train");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getStarInfo(player, heroId));
		String trainItem = "None";
		switch (index) {
		case 0:
			trainItem = "HP";
			break;
		case 1:
			trainItem = "Attack";
			break;
		case 2:
			trainItem = "PhysicsDefense";
			break;
		case 3:
			trainItem = "MagicDefense";
		}

		sb.append(',').append(trainItem);
		sb.append(',').append(player.getStarRecord().getTrainLevel(heroId)[index]);
		sb.append(',').append(cost);
		log.info(sb.toString());
	}

	public void logInherit(Player player, int srcId, int objId) {
		Logger log = getLog("inherit").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("inherit");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(getStarInfo(player, srcId));
		sb.append(getStarInfo(player, objId));
		log.info(sb.toString());
	}

	public void logLeagueBoss(Player player, int damage, int killedBoss) {
		Logger log = getLog("leagueboss").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("leagueboss");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		League l = Platform.getLeagueManager().getLeagueById(player.getUnion().getLeagueId());
		sb.append(',').append(l.getId());
		sb.append(',').append(l.getName());
		sb.append(',').append(l.getLevel());
		sb.append(getLeagueBossInfo(l));
		sb.append(',').append(damage);
		sb.append(',').append(player.getUnion().getBossSurplusNum());
		sb.append(',').append(killedBoss);
		sb.append(',').append(l.getInfo().getBossweekCount());
		log.info(sb.toString());
	}

	public String getPlayerInfo(Player player) {
		StringBuffer sb = new StringBuffer();
		sb.append(',').append(player.getAccountId());
		sb.append(',').append(player.getName());
		sb.append(',').append(player.getId());
		sb.append(',').append(player.getLevel());
		return sb.toString();
	}

	public String getItemInfo(Item i) {
		StringBuffer sb = new StringBuffer();
		sb.append(',').append(i.getTemplateId());
		sb.append(',').append(i.getId());
		sb.append(',').append(i.getName());
		sb.append(',').append(i.getLevel());
		return sb.toString();
	}

	public String getLeagueInfo(League l) {
		StringBuffer sb = new StringBuffer();
		sb.append(',').append(l.getId());
		sb.append(',').append(l.getName());
		sb.append(',').append(l.getLevel());
		sb.append(',').append(l.getRank());
		sb.append(',').append(l.getCostBuildValue());
		sb.append(',').append(l.getBuildValue());
		sb.append(',').append(l.getLeader());
		sb.append(',').append(l.getInfo().getMembers().size());
		return sb.toString();
	}

	public String getLeagueBossInfo(League l) {
		StringBuffer sb = new StringBuffer();
		sb.append(',').append(l.getBossFacilityLevel());
		sb.append(',').append(LeagueService.getBossBuff(l.getBossFacilityLevel()));
		sb.append(',').append(l.getInfo().getBoss().getLevel());
		return sb.toString();
	}

	public String getStarInfo(Player p, int heroId) {
		StringBuffer sb = new StringBuffer();
		sb.append(',').append(heroId);
		sb.append(',').append(ItemService.getItemTemplate(heroId).name);
		sb.append(',').append(p.getStarRecord().getLevel(heroId));
		sb.append(',').append(p.getStarRecord().getExp(heroId));
		return sb.toString();
	}

	public void logGm(Player player, String content) {
		Logger log = getLog("gm").logger;
		StringBuffer sb = new StringBuffer();
		sb.append(',').append("gm");
		sb.append(',').append(Configuration.serverId);
		sb.append(getPlayerInfo(player));
		sb.append(',').append(player.getVip().level);
		sb.append(',').append(content);
		log.info(sb.toString());
	}

	public int[] getEventCodes() {
		return new int[] { 1011 };
	}

	public void handleEvent(Event event) {
		if (event.type == 1011)
			for (Log log : logs.values())
				log.flush();
	}
}
