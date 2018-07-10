package org.darcy.sanguo.player;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityRecord;
import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.awardcenter.Awards;
import org.darcy.sanguo.bag.Bags;
import org.darcy.sanguo.boss.BossRecord;
import org.darcy.sanguo.coup.CoupRecord;
import org.darcy.sanguo.destiny.DestinyRecord;
import org.darcy.sanguo.divine.DivineRecord;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exchange.Exchanges;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.globaldrop.GlobalDrop;
import org.darcy.sanguo.glory.GloryRecord;
import org.darcy.sanguo.hero.Formation;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.hero.Warriors;
import org.darcy.sanguo.loottreasure.LootTreasure;
import org.darcy.sanguo.mail.Mails;
import org.darcy.sanguo.map.MapRecord;
import org.darcy.sanguo.pay.PayRecord;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.persist.PlayerBlob;
import org.darcy.sanguo.randomshop.RandomShopRecord;
import org.darcy.sanguo.recruit.RecruitRecord;
import org.darcy.sanguo.relation.Relations;
import org.darcy.sanguo.reward.RewardRecord;
import org.darcy.sanguo.service.CoupService;
import org.darcy.sanguo.service.PayReturnService;
import org.darcy.sanguo.service.PlayerService;
import org.darcy.sanguo.service.VipService;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.star.StarRecord;
import org.darcy.sanguo.startcatalog.StarcatalogRecord;
import org.darcy.sanguo.sync.DataSyncManager;
import org.darcy.sanguo.tactic.TacticRecord;
import org.darcy.sanguo.task.TaskRecord;
import org.darcy.sanguo.tower.TowerRecord;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.LeagueMember;
import org.darcy.sanguo.union.Union;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.util.PlayerLockService;
import org.darcy.sanguo.vip.Vip;
import org.darcy.sanguo.worldcompetition.WorldCompetition;
import org.darcy.sanguo.worldcompetition.WorldCompetitionData;
import org.darcy.sanguo.worldcompetition.WorldCompetitionService;

import com.google.protobuf.GeneratedMessage;

import sango.packet.PbDown;
import sango.packet.PbUser;
import sango.packet.PbUser.UserData;

/**
 * 玩家信息
 */
public class Player {
	public static final String NAME2ID = "select id from Player where name = ?";
	public static final long RESTORE_VITALITY_TIME = 360000L;
	public static final long RESTORE_STAMINA_TIME = 900000L;
	public static final int MAX_VITALITY = 150;
	public static final int STAMINA_INIT_LIMIT = 20;
	public static final int MAX_LEVEL = 80;
	public static final int GENDER_MALE = 1;
	public static final int GENDER_FEMALE = 2;
	private int id;
	private String name;
	private String accountId;
	private int channelType;
	private int gender;
	private int exp;
	private int level = 1;

	private int vitality = 150;
	private long lastRecoverVitality;
	private int jewels;
	private int money;
	private int stamina = 20;

	private int staminaLimit = 20;
	private long lastRecoverStamina;
	private long registerTime;
	private int warriorSpirit;
	private int spiritJade;
	private int honor;
	private int prestige;
	private int lastRefreshDay = Calendar.getInstance().get(6);
	private String heroIds;
	private int btlCapability;
	private Date lastLogin = new Date();

	private Date lastLogout = new Date();
	private int charge;
	private PlayerBlob blob = new PlayerBlob(this);
	private Warriors warriors;
	private RecruitRecord recruitRecord = new RecruitRecord();

	private Bags bags = new Bags();

	private Exchanges exchanges = new Exchanges();

	private LootTreasure lootTreasure = new LootTreasure();

	private GlobalDrop globalDrop = new GlobalDrop();

	private PropertyPool pool = new PropertyPool();

	private MapRecord mapRecord = new MapRecord();

	private RandomShopRecord randomShop = new RandomShopRecord();

	private BossRecord bossRecord = new BossRecord();

	private TowerRecord towerRecord = new TowerRecord();

	private RewardRecord rewardRecord = new RewardRecord();

	private DestinyRecord destinyRecord = new DestinyRecord();

	private DivineRecord divineRecord = new DivineRecord();

	private TacticRecord tacticRecord = new TacticRecord();

	private CoupRecord coupRecord = new CoupRecord();

	private TaskRecord taskRecord = new TaskRecord();

	private StarRecord starRecord = new StarRecord();

	private ActivityRecord activityRecord = new ActivityRecord();

	private GloryRecord gloryRecord = new GloryRecord();

	private StarcatalogRecord starcatalogRecord = new StarcatalogRecord();
	private Awards awards;
	private WorldCompetition worldCompetition;
	private Arena arena;
	private Relations relations;
	private Mails mails = new Mails();
	private Union union;
	private ClientSession session;
	private DataSyncManager dataSyncManager = new DataSyncManager();
	public static final int NOTIFY_ITEM_WORD = 1;
	public static final int NOTIFY_ITEM_DIALOG = 2;

	public MiniPlayer getMiniPlayer() {
		return new MiniPlayer(this.id, this.name, this.level, getHeroIds(), this.btlCapability, this.lastLogout);
	}

	public void setSession(ClientSession session) {
		this.session = session;
	}

	public ClientSession getSession() {
		return this.session;
	}

	public void newDayRefreshIf() {
		if (this.lastRefreshDay == Calendar.getInstance().get(6))
			return;
		this.randomShop.newDayRefresh();
		this.exchanges.refresh();
		this.mapRecord.refresh(this);
		this.towerRecord.refresh();
		this.relations.refresh();
		this.taskRecord.refreshDayTask(this);
		this.divineRecord.refresh(this);
		this.bossRecord.refresh();
		this.rewardRecord.refresh(this);
		this.pool.refresh();
		this.activityRecord.refresh(this);
		if (this.union != null) {
			this.union.refresh(this);
		}
		PayRecord.refresh(this);

		((PayReturnService) Platform.getServiceManager().get(PayReturnService.class)).newDayCheck(this);

		this.lastRefreshDay = Calendar.getInstance().get(6);
	}

	public void offLineClear() {
		this.session = null;
	}

	public void send(int ptCode, GeneratedMessage msg) {
		if (this.session != null)
			this.session.send(ptCode, msg);
	}

	public int getExp() {
		return this.exp;
	}

	public void setExp(int exp) {
		if (this.exp != exp) {
			this.dataSyncManager.addNumSync(0, exp);
		}
		this.exp = exp;
	}

	public void addExp(int exp, String optType) {
		if (exp <= 0) {
			return;
		}
		setExp(this.exp + exp);
		while (true) {
			if (this.level >= 80) {
				setExp(0);
				break;
			}
			int nextLevel = this.level + 1;
			PlayerLevel playerLevel = ((PlayerService) Platform.getServiceManager().get(PlayerService.class))
					.getPlayerLevel(nextLevel);
			if ((playerLevel == null) || (this.exp < playerLevel.needExp))
				break;
			levelUp();
			decExp(playerLevel.needExp);
		}

		Platform.getLog().logAcquire(this, "exp", exp, this.exp, optType);
	}

	public void decExp(int exp) {
		if ((this.exp < exp) || (exp < 0)) {
			throw new RuntimeException("invalid exp value : " + exp);
		}
		setExp(this.exp - exp);
	}

	public int getVitality() {
		return this.vitality;
	}

	public void setVitality(int vitality) {
		if (this.vitality != vitality) {
			this.dataSyncManager.addNumSync(2, vitality);
		}
		this.vitality = vitality;
	}

	public void addVitality(int vitality, String optType) {
		if (vitality > 0) {
			setVitality(this.vitality + vitality);
			Platform.getLog().logAcquire(this, "vitality", vitality, this.vitality, optType);
		}
	}

	public void decVitality(int vitality, String optType) {
		if ((this.vitality < vitality) || (vitality < 0)) {
			throw new RuntimeException("invalid vitality value : " + vitality);
		}
		setVitality(this.vitality - vitality);
		this.activityRecord.prayAddProcess(this, 4, vitality);
		Platform.getLog().logCost(this, "vitality", vitality, this.vitality, optType);
	}

	public int getJewels() {
		return this.jewels;
	}

	public void setJewels(int jewels) {
		if (this.jewels != jewels) {
			this.dataSyncManager.addNumSync(3, jewels);
		}
		this.jewels = jewels;
	}

	public void addJewels(int jewels, String optType) {
		if (jewels > 0) {
			setJewels(this.jewels + jewels);
			Platform.getLog().logAcquire(this, "gold", jewels, this.jewels, optType);

			if (this.rewardRecord.isOpenDrawMoney(this))
				Platform.getEventManager().addEvent(new Event(2095, new Object[] { this }));
		}
	}

	public void decJewels(int jewels, String optType) {
		if ((this.jewels < jewels) || (jewels < 0)) {
			throw new RuntimeException("invalid jewel value : " + jewels);
		}
		setJewels(this.jewels - jewels);
		this.activityRecord.costInActivity(this, jewels);
		this.activityRecord.prayAddProcess(this, 1, jewels);

		Platform.getLog().logCost(this, "gold", jewels, this.jewels, optType);
	}

	public int getMoney() {
		return this.money;
	}

	public void setMoney(int money) {
		if (this.money != money) {
			this.dataSyncManager.addNumSync(4, money);
		}
		this.money = money;
	}

	public void addMoney(int money, String optType) {
		if (money > 0) {
			setMoney(this.money + money);
			Platform.getLog().logAcquire(this, "silver", money, this.money, optType);
		}
	}

	public void decMoney(int money, String optType) {
		if ((this.money < money) || (money < 0)) {
			throw new RuntimeException("invalid money value : " + money);
		}
		setMoney(this.money - money);
		Platform.getLog().logCost(this, "silver", money, this.money, optType);
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

	public int getStamina() {
		return this.stamina;
	}

	public void setStamina(int stamina) {
		if (this.stamina != stamina) {
			this.dataSyncManager.addNumSync(5, stamina);
		}
		this.stamina = stamina;
	}

	public String getHeroIds() {
		try {
			return this.warriors.generateHeroIds();
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
		return "";
	}

	public void setHeroIds(String heroIds) {
		this.heroIds = heroIds;
	}

	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Date getLastLogout() {
		return this.lastLogout;
	}

	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}

	public int getBtlCapability() {
		return this.btlCapability;
	}

	public void setBtlCapability(int btlCapability) {
		this.btlCapability = btlCapability;
	}

	public void addStamina(int stamina, String optType) {
		if (stamina > 0) {
			setStamina(this.stamina + stamina);
			Platform.getLog().logAcquire(this, "stamina", stamina, this.stamina, optType);
		}
	}

	public void decStamina(int stamina, String optType) {
		if ((this.stamina < stamina) || (stamina < 0)) {
			throw new RuntimeException("invalid stamina value : " + stamina);
		}
		setStamina(this.stamina - stamina);
		this.activityRecord.prayAddProcess(this, 3, stamina);
		Platform.getLog().logCost(this, "stamina", stamina, this.stamina, optType);
	}

	public void setName(String name) {
		this.name = name;
	}

	public BossRecord getBossRecord() {
		return this.bossRecord;
	}

	public Relations getRelations() {
		return this.relations;
	}

	public void setRelations(Relations relations) {
		this.relations = relations;
	}

	public long getRefreshTime() {
		Calendar cal = Calendar.getInstance();
		cal.set(6, this.lastRefreshDay + 1);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		return cal.getTimeInMillis();
	}

	public int getLastRefreshDay() {
		return this.lastRefreshDay;
	}

	public void setLastRefreshDay(int lastRefreshDay) {
		this.lastRefreshDay = lastRefreshDay;
	}

	public void setBossRecord(BossRecord bossRecord) {
		this.bossRecord = bossRecord;
	}

	public MapRecord getMapRecord() {
		return this.mapRecord;
	}

	public void setMapRecord(MapRecord mapRecord) {
		this.mapRecord = mapRecord;
	}

	public RandomShopRecord getRandomShop() {
		return this.randomShop;
	}

	public TowerRecord getTowerRecord() {
		return this.towerRecord;
	}

	public void setTowerRecord(TowerRecord towerRecord) {
		this.towerRecord = towerRecord;
	}

	public void setRandomShop(RandomShopRecord randomShop) {
		this.randomShop = randomShop;
	}

	public Warriors getWarriors() {
		return this.warriors;
	}

	public void setWarriors(Warriors warriors) {
		this.warriors = warriors;
	}

	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public int getChannelType() {
		return this.channelType;
	}

	public void setChannelType(int channelType) {
		this.channelType = channelType;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		if (this.level != level) {
			this.dataSyncManager.addNumSync(1, level);
		}
		this.level = level;
	}

	public int getWarriorSpirit() {
		return this.warriorSpirit;
	}

	public void setWarriorSpirit(int warriorSpirit) {
		if (this.warriorSpirit != warriorSpirit) {
			this.dataSyncManager.addNumSync(8, warriorSpirit);
		}
		this.warriorSpirit = warriorSpirit;
	}

	public void addWarriorSpirit(int spirit, String optType) {
		if (spirit > 0) {
			setWarriorSpirit(this.warriorSpirit + spirit);
			Platform.getLog().logAcquire(this, "exploits", spirit, this.warriorSpirit, optType);
		}
	}

	public void decWarriorSpirit(int spirit, String optType) {
		if ((this.warriorSpirit < spirit) || (spirit < 0)) {
			throw new RuntimeException("invalid siprit value : " + spirit);
		}
		setWarriorSpirit(this.warriorSpirit - spirit);
		Platform.getLog().logCost(this, "exploits", spirit, this.warriorSpirit, optType);
	}

	public int getGender() {
		return this.gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Bags getBags() {
		return this.bags;
	}

	public void setBags(Bags bags) {
		this.bags = bags;
	}

	public RecruitRecord getRecruitRecord() {
		return this.recruitRecord;
	}

	public void setRecruitRecord(RecruitRecord record) {
		this.recruitRecord = record;
	}

	public long getLastRecoverVitality() {
		return this.lastRecoverVitality;
	}

	public void setLastRecoverVitality(long lastRecoverVitality) {
		this.lastRecoverVitality = lastRecoverVitality;
	}

	public int getSpiritJade() {
		return this.spiritJade;
	}

	public void setSpiritJade(int spiritJade) {
		if (this.spiritJade != spiritJade) {
			this.dataSyncManager.addNumSync(7, spiritJade);
		}
		this.spiritJade = spiritJade;
	}

	public void addSpiritJade(int spiritJade, String optType) {
		if (spiritJade > 0) {
			setSpiritJade(this.spiritJade + spiritJade);
			Platform.getLog().logAcquire(this, "spiritjade", spiritJade, this.spiritJade, optType);
		}
	}

	public void decSpiritJade(int spiritJade, String optType) {
		if ((this.spiritJade < spiritJade) || (spiritJade < 0)) {
			throw new RuntimeException("invalid spiritJade value : " + spiritJade);
		}
		setSpiritJade(this.spiritJade - spiritJade);
		this.activityRecord.prayAddProcess(this, 5, spiritJade);
		Platform.getLog().logCost(this, "spiritjade", spiritJade, this.spiritJade, optType);
	}

	public int getHonor() {
		return this.honor;
	}

	public void setHonor(int honor) {
		if (this.honor != honor) {
			this.dataSyncManager.addNumSync(10, honor);
		}
		this.honor = honor;
	}

	public void addHonor(int honor, String optType) {
		if (honor > 0) {
			setHonor(this.honor + honor);
			Platform.getLog().logAcquire(this, "honor", honor, this.honor, optType);
		}
	}

	public void decHonor(int honor, String optType) {
		if ((this.honor < honor) || (honor < 0)) {
			throw new RuntimeException("invalid honor value : " + honor);
		}
		setHonor(this.honor - honor);
		Platform.getLog().logCost(this, "honor", honor, this.honor, optType);
	}

	public int getPrestige() {
		return this.prestige;
	}

	public void setPrestige(int prestige) {
		if (this.prestige != prestige) {
			this.dataSyncManager.addNumSync(11, prestige);
		}
		this.prestige = prestige;
	}

	public Mails getMails() {
		return this.mails;
	}

	public void setMails(Mails mails) {
		this.mails = mails;
	}

	public StarRecord getStarRecord() {
		return this.starRecord;
	}

	public void setStarRecord(StarRecord starRecord) {
		this.starRecord = starRecord;
	}

	public Union getUnion() {
		return this.union;
	}

	public void setUnion(Union union) {
		this.union = union;
	}

	public ActivityRecord getActivityRecord() {
		return this.activityRecord;
	}

	public void setActivityRecord(ActivityRecord activityRecord) {
		this.activityRecord = activityRecord;
	}

	public GloryRecord getGloryRecord() {
		return this.gloryRecord;
	}

	public void setGloryRecord(GloryRecord gloryRecord) {
		this.gloryRecord = gloryRecord;
	}

	public void addPrestige(int prestige, String optType) {
		if (prestige > 0) {
			setPrestige(this.prestige + prestige);
			Platform.getLog().logAcquire(this, "prestige", prestige, this.prestige, optType);
		}
	}

	public void decPrestige(int prestige, String optType) {
		if ((this.prestige < prestige) || (prestige < 0)) {
			throw new RuntimeException("invalid prestige value : " + this.honor);
		}
		setPrestige(this.prestige - prestige);
		Platform.getLog().logCost(this, "prestige", prestige, this.prestige, optType);
	}

	public int getStaminaLimit() {
		return this.staminaLimit;
	}

	public void setStaminaLimit(int staminaLimit) {
		if (this.staminaLimit != staminaLimit) {
			this.dataSyncManager.addNumSync(6, staminaLimit);
		}
		this.staminaLimit = staminaLimit;
	}

	public void addCharge(int addCharge, boolean isCharge) {
		if (addCharge > 0) {
			setCharge(this.charge + addCharge);
			if (isCharge) {
				this.activityRecord.chargeInActivity(this, addCharge);
				int charge = addCharge + this.pool.getInt(21, 0);
				this.pool.set(21, Integer.valueOf(charge));
			}
			refreshVip();
		}
	}

	public int getCharge() {
		return this.charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public void addStaminaLimt(int staminaLimit) {
		if (staminaLimit > 0)
			setStaminaLimit(this.staminaLimit + staminaLimit);
	}

	public long getLastRecoverStamina() {
		return this.lastRecoverStamina;
	}

	public void setLastRecoverStamina(long lastRecoverStamina) {
		this.lastRecoverStamina = lastRecoverStamina;
	}

	public DataSyncManager getDataSyncManager() {
		return this.dataSyncManager;
	}

	public PropertyPool getPool() {
		return this.pool;
	}

	public void setPool(PropertyPool pool) {
		this.pool = pool;
	}

	public Awards getAwards() {
		return this.awards;
	}

	public void setAwards(Awards awards) {
		this.awards = awards;
	}

	public WorldCompetition getWorldCompetition() {
		return this.worldCompetition;
	}

	public void setWorldCompetition(WorldCompetition worldCompetition) {
		this.worldCompetition = worldCompetition;
	}

	public Exchanges getExchanges() {
		return this.exchanges;
	}

	public void setExchanges(Exchanges exchanges) {
		this.exchanges = exchanges;
	}

	public long getRegisterTime() {
		return this.registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public Arena getArena() {
		return this.arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public LootTreasure getLootTreasure() {
		return this.lootTreasure;
	}

	public void setLootTreasure(LootTreasure lootTreasure) {
		this.lootTreasure = lootTreasure;
	}

	public DivineRecord getDivineRecord() {
		return this.divineRecord;
	}

	public void setDivineRecord(DivineRecord divineRecord) {
		this.divineRecord = divineRecord;
	}

	public GlobalDrop getGlobalDrop() {
		return this.globalDrop;
	}

	public void setGlobalDrop(GlobalDrop globalDrop) {
		this.globalDrop = globalDrop;
	}

	public DestinyRecord getDestinyRecord() {
		return this.destinyRecord;
	}

	public void setDestinyRecord(DestinyRecord destinyRecord) {
		this.destinyRecord = destinyRecord;
	}

	public CoupRecord getCoupRecord() {
		return this.coupRecord;
	}

	public void setCoupRecord(CoupRecord coupRecord) {
		this.coupRecord = coupRecord;
	}

	public PlayerBlob getBlob() {
		return this.blob;
	}

	public void setBlob(PlayerBlob blob) {
		this.blob = blob;
	}

	public RewardRecord getRewardRecord() {
		return this.rewardRecord;
	}

	public void setRewardRecord(RewardRecord rewardRecord) {
		this.rewardRecord = rewardRecord;
	}

	public TacticRecord getTacticRecord() {
		return this.tacticRecord;
	}

	public void setTacticRecord(TacticRecord tacticRecord) {
		this.tacticRecord = tacticRecord;
	}

	public TaskRecord getTaskRecord() {
		return this.taskRecord;
	}

	public void setTaskRecord(TaskRecord taskRecord) {
		this.taskRecord = taskRecord;
	}

	public void init() {
		this.blob.init(this);
		this.bags.init(this);
		this.warriors.init(this);
		this.warriors.getMainWarrior().updateLevel(this.level, false);
		this.coupRecord.init(this);
		this.bags.initEquipAndTreasure(this);
		this.lootTreasure.init();
		initVitality();
		initStamina();
		initArena();
		initWorldCompetition();
		this.mapRecord.init();
		this.destinyRecord.init();
		this.tacticRecord.init(this);
		this.taskRecord.init(this);
		this.gloryRecord.init(this);
	}

	public void asyncInit() throws Exception {
		asyncInitRelation();
		asyncInitArena();
		asyncInitAwards();
		asyncInitWorldCompetition();
		asyncInitMails();
		asyncInitUnion();
	}

	private void asyncInitMails() {
		this.mails = new Mails();
		List ls = DBUtil.getMails(this.id);
		if (ls != null)
			this.mails.setMails(ls);
	}

	private void asyncInitRelation() throws Exception {
		this.relations = ((Relations) ((DbService) Platform.getServiceManager().get(DbService.class))
				.get(Relations.class, Integer.valueOf(this.id)));
		if (this.relations == null) {
			this.relations = new Relations();
			this.relations.setId(this.id);
			((DbService) Platform.getServiceManager().get(DbService.class)).add(this.relations);
		}
		this.relations.init();
	}

	private void asyncInitArena() throws Exception {
		if (FunctionService.isOpenFunction(this.level, 12)) {
			this.arena = ((Arena) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Arena.class,
					Integer.valueOf(this.id)));
			if (this.arena == null) {
				this.arena = new Arena(Platform.getWorld().getNewArenaCount());
				this.arena.setPlayerId(this.id);
				((DbService) Platform.getServiceManager().get(DbService.class)).add(this.arena);
			}
		}
	}

	private void asyncInitUnion() throws Exception {
		if (FunctionService.isOpenFunction(this.level, 16)) {
			this.union = ((Union) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Union.class,
					Integer.valueOf(this.id)));
			if (this.union == null) {
				this.union = new Union();
				this.union.setPlayerId(this.id);
				((DbService) Platform.getServiceManager().get(DbService.class)).add(this.union);
			}
		}
	}

	private void asyncInitAwards() throws Exception {
		this.awards = ((Awards) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Awards.class,
				Integer.valueOf(this.id)));
		if (this.awards == null) {
			this.awards = new Awards();
			this.awards.setPlayerId(this.id);
			((DbService) Platform.getServiceManager().get(DbService.class)).add(this.awards);
		}
		this.awards.init();
	}

	private void asyncInitWorldCompetition() throws Exception {
		if (FunctionService.isOpenFunction(this.level, 21)) {
			long now = System.currentTimeMillis();
			this.worldCompetition = ((WorldCompetitionService) Platform.getServiceManager()
					.get(WorldCompetitionService.class)).getWorldCompetitionByPlayerId(this.id);
			if (this.worldCompetition == null) {
				this.worldCompetition = ((WorldCompetition) ((DbService) Platform.getServiceManager()
						.get(DbService.class)).get(WorldCompetition.class, Integer.valueOf(this.id)));
				if (this.worldCompetition == null) {
					this.worldCompetition = WorldCompetition.newWorldCompetition(this.id);
					((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
							.addCompetition(this.worldCompetition);
					((DbService) Platform.getServiceManager().get(DbService.class)).add(this.worldCompetition);
					boolean isError = ((WorldCompetitionService) Platform.getServiceManager()
							.get(WorldCompetitionService.class)).sort(this.worldCompetition, true);
					if (isError) {
						((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
								.sort();
					}
					((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
							.clearOutData();
					return;
				}
				Calendar lastLoadCal = Calendar.getInstance();
				lastLoadCal.setTimeInMillis(this.worldCompetition.getLastLoad());
				Calendar nowCal = Calendar.getInstance();
				nowCal.setTimeInMillis(now);
				if ((lastLoadCal.get(1) != nowCal.get(1)) || (lastLoadCal.get(3) != nowCal.get(3))) {
					this.worldCompetition.setScore(WorldCompetitionData.baseScore);
					this.worldCompetition.getEnemy().clear();
					this.worldCompetition.getCompetitor().clear();
				}
			}

			this.worldCompetition.setLastLoad(now);
		}
	}

	private void initVitality() {
		long cur = System.currentTimeMillis();
		int recover = (int) ((cur - this.lastRecoverVitality) / 360000L);
		if ((this.vitality < 150) && (recover > 0)) {
			this.lastRecoverVitality += 360000L * recover;
			if (this.vitality + recover > 150)
				setVitality(150);
			else
				setVitality(this.vitality + recover);
		}
	}

	private void initStamina() {
		long cur = System.currentTimeMillis();
		int recover = (int) ((cur - this.lastRecoverStamina) / 900000L);
		if ((this.stamina < this.staminaLimit) && (recover > 0)) {
			this.lastRecoverStamina += 900000L * recover;
			if (this.stamina + recover > this.staminaLimit)
				setStamina(this.staminaLimit);
			else
				setStamina(this.stamina + recover);
		}
	}

	private void initArena() {
		if (this.arena != null)
			this.arena.init(this);
	}

	private void initWorldCompetition() {
		if (this.worldCompetition != null)
			this.worldCompetition.init(this);
	}

	private void levelUp() {
		UserData old = genUserData();

		setLevel(this.level + 1);
		Platform.getLog().logLevelUp(this, 1);
		this.taskRecord.refreshNewTask(this);
		this.dataSyncManager.addNumSync(9, getExpLimit());
		this.warriors.getMainWarrior().updateLevel(this.level, true);
		FunctionService.openNewFunction(this);
		Platform.getEventManager().addEvent(new Event(2028, new Object[] { this }));

		PlayerLevel playerLevel = ((PlayerService) Platform.getServiceManager().get(PlayerService.class))
				.getPlayerLevel(this.level);
		if (playerLevel != null) {
			addJewels(playerLevel.moneyReward, "levelup");
			if (playerLevel.staminaReward > 0) {
				addStamina(playerLevel.staminaReward, "levelup");
			}

			PbDown.LevelUpRst.Builder builder = PbDown.LevelUpRst.newBuilder();
			builder.setOldUser(old);
			builder.setLevel(this.level);
			builder.setJewel(playerLevel.moneyReward);
			builder.setOnStageNume(playerLevel.onStageWarriorNum);
			builder.setSkill(CoupService.getLvlSkills(this.level));
			int index = -1;
			if ((index = Formation.getIndexByLevel(this.level)) != -1) {
				builder.setWarriorIndex(index);
				Platform.getEventManager().addEvent(new Event(2037, new Object[] { this }));
			}
			if ((index = Formation.getFriendIndexByLevel(this.level)) != -1) {
				builder.setFriendIndex(index);
				Platform.getEventManager().addEvent(new Event(2038, new Object[] { this }));
			}
			if (playerLevel.staminaReward > 0) {
				builder.setStamina(playerLevel.staminaReward);
			}
			if (playerLevel.tacticPointReward > 0) {
				this.tacticRecord.addPoint(playerLevel.tacticPointReward, this);
				builder.setTacticPoint(playerLevel.tacticPointReward);
			}

			send(1034, builder.build());
		}
	}

	public long getRestTimeToNextRecoverVitality(long curTime) {
		if (this.vitality >= 150) {
			return 0L;
		}
		return (this.lastRecoverVitality + 360000L - curTime);
	}

	public long getRestTimeToFullVitality(long curTime) {
		if (this.vitality >= 150) {
			return 0L;
		}
		return (getRestTimeToNextRecoverVitality(curTime) + (150 - this.vitality - 1) * 360000L);
	}

	public long getRestTimeToNextRecoverStamina(long curTime) {
		if (this.stamina >= this.staminaLimit) {
			return 0L;
		}
		return (this.lastRecoverStamina + 900000L - curTime);
	}

	public long getRestTimeToFullStamina(long curTime) {
		if (this.stamina >= this.staminaLimit) {
			return 0L;
		}
		return (getRestTimeToNextRecoverStamina(curTime) + (this.staminaLimit - this.stamina - 1) * 900000L);
	}

	public void update() {
		long curTime = System.currentTimeMillis();
		if (curTime - this.lastRecoverVitality >= 360000L) {
			if (this.vitality < 150) {
				addVitality(1, "recover");
			}
			this.lastRecoverVitality = curTime;
		}
		if (curTime - this.lastRecoverStamina >= 900000L) {
			if (this.stamina < this.staminaLimit) {
				addStamina(1, "recover");
			}
			this.lastRecoverStamina = curTime;
		}
		this.dataSyncManager.update(this);
	}

	public void save() {
		try {
			synchronized (PlayerLockService.getLock(this.id)) {
				DbService ds = (DbService) Platform.getServiceManager().get(DbService.class);
				ds.update(this);
				if (this.relations != null) {
					ds.update(this.relations);
				}
				if (this.awards != null) {
					ds.update(this.awards);
				}
				if (this.arena != null) {
					ds.update(this.arena);
				}
				if (this.worldCompetition != null) {
					ds.update(this.worldCompetition);
				}
				if (this.union != null) {
					ds.update(this.union);
				}

			}

		} catch (Exception e) {
			Platform.getLog().logError("Player save error,id:" + this.id, e);
		}
	}

	public int getExpLimit() {
		int nextLevel = this.level + 1;
		PlayerLevel playerLevel = ((PlayerService) Platform.getServiceManager().get(PlayerService.class))
				.getPlayerLevel(nextLevel);
		if (playerLevel != null) {
			return playerLevel.needExp;
		}
		return 0;
	}

	public int getNumericValue(int dropType) {
		switch (dropType) {
		case 2:
			return this.money;
		case 3:
			return this.jewels;
		case 4:
			return this.exp;
		case 5:
			return this.vitality;
		case 6:
			return this.stamina;
		case 10:
			return this.prestige;
		case 9:
			return this.honor;
		case 7:
			return this.spiritJade;
		case 8:
			return this.warriorSpirit;
		case 11:
			return this.tacticRecord.getSurplusPoint();
		}
		return -1;
	}

	public void refreshBtlCapa() {
		int rst = 0;
		for (Warrior w : this.warriors.getStands()) {
			if (w != null) {
				rst += w.getBtlCapa();
			}
		}
		if (this.btlCapability != rst) {
			this.dataSyncManager.addNumSync(12, rst);
			((ItemService) Platform.getServiceManager().get(ItemService.class)).indexSync(this);
		}
		this.btlCapability = rst;

		Platform.getPlayerManager().updateMiniPlayer(this);
	}

	public void notifyGetItem(int type, List<Reward> rewards) {
		PbDown.NotifyGetItem.Builder builder = PbDown.NotifyGetItem.newBuilder().setType(type);
		for (Reward reward : rewards) {
			builder.addItems(reward.genPbReward());
		}
		send(1040, builder.build());
	}

	public void notifyGetItem(int type, Reward[] rewards) {
		PbDown.NotifyGetItem.Builder builder = PbDown.NotifyGetItem.newBuilder().setType(type);
		for (Reward reward : rewards) {
			builder.addItems(reward.genPbReward());
		}
		send(1040, builder.build());
	}

	public void clear() {
		Platform.getBossManager().removePlayer(this);
	}

	public UserData genUserData() {
		UserData.Builder builder = UserData.newBuilder();
		builder.setId(this.id);
		builder.setName(this.name);
		builder.setLevel(this.level);
		builder.setExp(this.exp);
		builder.setMaxExp(getExpLimit());
		builder.setVitality(this.vitality);
		builder.setMoney(this.money);
		builder.setJewel(this.jewels);
		builder.setGender(this.gender);
		builder.setWarriorSpirit(this.warriorSpirit);
		builder.setSpiritJade(this.spiritJade);
		long curTime = System.currentTimeMillis();
		builder.setNextRecoverVitalityTime(getRestTimeToNextRecoverVitality(curTime));
		builder.setFullRecoverVitalityTime(getRestTimeToFullVitality(curTime));
		builder.setStamina(this.stamina);
		builder.setStaminaLimi(this.staminaLimit);
		builder.setNextRecoverStaminaTime(getRestTimeToNextRecoverStamina(curTime));
		builder.setFullRecoverStaminaTime(getRestTimeToFullStamina(curTime));
		builder.setBtlcapability(this.btlCapability);
		builder.setPrestige(this.prestige);
		builder.setHonor(this.honor);
		builder.setVipLevel(getVip().level);
		return builder.build();
	}

	public PbUser.OthersData genOthersData() {
		PbUser.OthersData.Builder builder = PbUser.OthersData.newBuilder();
		builder.setUser(getMiniPlayer().genMiniUser());
		for (Warrior warrior : getWarriors().getWarriors().values()) {
			builder.addWarriors(warrior.genOthersWarrior(this));
		}
		for (Warrior friend : getWarriors().getFriends().values()) {
			builder.addFellows(friend.genWarrior().getTemplateId());
		}
		builder.addAllGroup(this.gloryRecord.getGloryGroups());
		return builder.build();
	}

	public Vip getVip() {
		return VipService.getVip(this.charge);
	}

	public void refreshVip() {
		int vipLevel = getVip().level;
		this.dataSyncManager.addNumSync(13, vipLevel);
		Function.notifyMainCheck(this, new int[] { 30 });
	}

	public boolean isAlreadyCharge() {
		return (this.pool.getInt(21, 0) <= 0);
	}

	public void loginSync() {
		((ItemService) Platform.getServiceManager().get(ItemService.class)).indexSync(this);
		this.dataSyncManager.addNumSync(18, this.pool.getInt(22, 0));
		if (this.union != null) {
			this.union.syncContribution(this);
			this.union.bossReward(this);
		}
	}

	public StarcatalogRecord getStarcatalogRecord() {
		return this.starcatalogRecord;
	}

	public void setStarcatalogRecord(StarcatalogRecord starcatalogRecord) {
		this.starcatalogRecord = starcatalogRecord;
	}

	private void createLeagueBox(int cost) {
		League l = Platform.getLeagueManager().getLeagueByPlayerId(this.id);
		if (l != null) {
			LeagueMember lm = l.getMember(this.id);
			if (lm != null)
				lm.createBox(cost, l, this);
		}
	}
}
