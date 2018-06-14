package org.darcy.sanguo.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
	public static final String WARN = "warn";
	public static final String ERROR = "error";
	public static final String SYSTEM = "system";
	public static final String NET = "net";
	public static final String WORLD = "world";
	public static final String COMBAT = "combat";
	public static final String RECORDIN = "rIn";
	public static final String RECORDOUT = "rOut";
	public static final String LOGIN = "login";
	public static final String REGIST = "regist";
	public static final String LOGOUT = "logout";
	public static final String LEVELUP = "levelup";
	public static final String RECRUIT = "recruit";
	public static final String ACQUIRE = "acquire";
	public static final String COST = "cost";
	public static final String TASK = "task";
	public static final String GETAWARD = "getaward";
	public static final String GETITEM = "getitem";
	public static final String REMOVEITEM = "removeitem";
	public static final String PVEFIGHT = "pvefight";
	public static final String TREASURE = "treasure";
	public static final String EQUIPMENT = "equipment";
	public static final String WARRIOR = "warrior";
	public static final String STAGE = "stage";
	public static final String FELLOW = "fellow";
	public static final String TACTIC = "tactic";
	public static final String DESTINY = "destiny";
	public static final String ARENA = "arena";
	public static final String COMPETITION = "competition";
	public static final String STAR = "star";
	public static final String TOWER = "tower";
	public static final String EQUIP = "equip";
	public static final String DIVINE = "divine";
	public static final String COUP = "coup";
	public static final String BAN = "ban";
	public static final String ONLINE = "online";
	public static final String CHARGE = "charge";
	public static final String NEW_GUIDE = "guide";
	public static final String MAIL = "mail";
	public static final String PAYCHECK = "paycheck";
	public static final String LEAGUE = "league";
	public static final String TRAIN = "train";
	public static final String INHERIT = "inherit";
	public static final String LEAGUEBOSS = "leagueboss";
	public static final String PAYPUSH = "paypush";
	public static final String GM_MAIL = "gm";
	public static final String LEAGUEBOX = "leaguebox";
	public static final String LEAGUECOMBAT = "leaguecombat";
	public static final String LEAGUECOMBATEND = "leaguecombatend";
	protected Logger logger = null;

	public Log(String type) {
		this.logger = LoggerFactory.getLogger(type);
	}

	public void flush() {
		this.logger.error("");
	}
}
