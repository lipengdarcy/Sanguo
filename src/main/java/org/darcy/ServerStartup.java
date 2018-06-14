package org.darcy;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.CallBackManager;
import org.darcy.sanguo.awardcenter.AwardsService;
import org.darcy.sanguo.boss.BossManager;
import org.darcy.sanguo.charge.ChargeService;
import org.darcy.sanguo.chat.ChatService;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.divine.DivineService;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.en.EnService;
import org.darcy.sanguo.event.EventManager;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.gate.GateService;
import org.darcy.sanguo.gm.GmService;
import org.darcy.sanguo.keyword.KeyWordManager;
import org.darcy.sanguo.log.LogManager;
import org.darcy.sanguo.loottreasure.LootTreasureService;
import org.darcy.sanguo.monster.MonsterService;
import org.darcy.sanguo.net.ClientSessionManager;
import org.darcy.sanguo.net.NettyService;
import org.darcy.sanguo.packethandler.PacketHandlerManager;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.persist.EntityManager;
import org.darcy.sanguo.persist.PlayerBlobService;
import org.darcy.sanguo.player.PlayerManager;
import org.darcy.sanguo.randomshop.RandomShopService;
import org.darcy.sanguo.recruit.RecruitService;
import org.darcy.sanguo.relation.RelationService;
import org.darcy.sanguo.service.AccountService;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.service.ArenaService;
import org.darcy.sanguo.service.CoupService;
import org.darcy.sanguo.service.DestinyService;
import org.darcy.sanguo.service.ExchangeService;
import org.darcy.sanguo.service.GlobalDropService;
import org.darcy.sanguo.service.GloryService;
import org.darcy.sanguo.service.HeroService;
import org.darcy.sanguo.service.MapService;
import org.darcy.sanguo.service.NoticeService;
import org.darcy.sanguo.service.PayReturnService;
import org.darcy.sanguo.service.PlayerService;
import org.darcy.sanguo.service.RewardService;
import org.darcy.sanguo.service.RobotService;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.ServiceManager;
import org.darcy.sanguo.service.TaskService;
import org.darcy.sanguo.service.TowerService;
import org.darcy.sanguo.service.VipService;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.service.common.TacticService;
import org.darcy.sanguo.star.StarService;
import org.darcy.sanguo.startcatalog.StarcatalogService;
import org.darcy.sanguo.threadpool.ThreadPool;
import org.darcy.sanguo.time.Time;
import org.darcy.sanguo.top.AsyncRanker;
import org.darcy.sanguo.top.TopManager;
import org.darcy.sanguo.top.TopService;
import org.darcy.sanguo.union.LeagueManager;
import org.darcy.sanguo.union.LeagueService;
import org.darcy.sanguo.union.combat.LeagueCombatService;
import org.darcy.sanguo.updater.UpdaterManager;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.world.World;
import org.darcy.sanguo.worldcompetition.WorldCompetitionService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 服务端主线程
 */
public class ServerStartup implements Runnable {

	public static boolean running = true;

	private static boolean shutdown = false;

	long lastTime;

	public static int tick = 0;

	public static void main(String[] args) throws Exception {
		ServerStartup server = new ServerStartup();
		try {
			server.init();
			new Thread(server, "server").start();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void init() throws Exception {
		initConfiguration();
		EventManager eventManager = new EventManager();
		Platform.setEventManager(eventManager);
		LogManager loggerManager = new LogManager();
		loggerManager.init();
		Platform.setLogManager(loggerManager);
		Platform.setServiceManager(new ServiceManager());
		Platform.setPacketHanderManager(new PacketHandlerManager());
		Platform.setUpdaterManager(new UpdaterManager());
		Platform.getUpdaterManager().addSyncUpdatable(eventManager);
		Platform.getUpdaterManager().addSyncUpdatable(new ChargeService());
		Platform.setThreadPool(new ThreadPool());
		CallBackManager callBackManager = new CallBackManager();
		Platform.setCallBackManager(callBackManager);
		Platform.getUpdaterManager().addSyncUpdatable(callBackManager);
		PlayerManager pm = new PlayerManager();
		Platform.setPlayerManager(pm);
		Platform.getUpdaterManager().addSyncUpdatable(pm);
		Platform.getServiceManager().add(new CombatService());
		Platform.getServiceManager().add(new ItemService());
		Platform.getServiceManager().add(new MonsterService());
		Platform.getServiceManager().add(new MapService());
		Platform.getServiceManager().add(new DropService());
		Platform.getServiceManager().add(new RecruitService());
		Platform.setEntityManager(new EntityManager());
		Platform.getServiceManager().add(new WorldCompetitionService());
		Platform.getServiceManager().add(new DbService());
		Platform.getServiceManager().add(new EnService());
		Platform.getServiceManager().add(new StarcatalogService());
		Platform.getServiceManager().add(new ExpService());
		Platform.getServiceManager().add(new HeroService());
		Platform.getServiceManager().add(new GmService());
		Platform.getServiceManager().add(new PlayerService());
		Platform.getServiceManager().add(new RandomShopService());
		Platform.getServiceManager().add(new ArenaService());
		Platform.setBossManager(new BossManager());
		//Platform.getServiceManager().add(new BossService());
		Platform.getServiceManager().add(new TowerService());
		TopManager tm = new TopManager();
		//tm.init();
		Platform.setTopManager(tm);
		Platform.getServiceManager().add(new RelationService());
		Platform.getServiceManager().add(new DestinyService());
		//Platform.getServiceManager().add(new MailService());
		Platform.getServiceManager().add(new CoupService());
		Platform.getServiceManager().add(new DivineService());
		Platform.getServiceManager().add(new ChatService());
		Platform.getServiceManager().add(new StarService());
		Platform.getServiceManager().add(new TopService());
		Platform.getServiceManager().add(new LootTreasureService());
		Platform.getServiceManager().add(new ExchangeService());
		Platform.getServiceManager().add(new GlobalDropService());
		Platform.getServiceManager().add(new AwardsService());
		Platform.getServiceManager().add(new VipService());
		//Platform.getServiceManager().add(new PayService());

		ClientSessionManager clientSessionManager = new ClientSessionManager();
		Platform.setClientSessionManager(clientSessionManager);
		Platform.getUpdaterManager().addSyncUpdatable(clientSessionManager);
		Platform.getServiceManager().add(new AccountService());
		Platform.getServiceManager().add(new PlayerBlobService());
		Platform.getServiceManager().add(new RewardService());
		Platform.getServiceManager().add(new ActivityService());
		Platform.getServiceManager().add(new TacticService());
		Platform.getServiceManager().add(new TaskService());
		Platform.getServiceManager().add(new FunctionService());
		Platform.getServiceManager().add(new NoticeService());
		Platform.setKeyWordManager(new KeyWordManager());
		Platform.setLeagueManager(new LeagueManager());
		Platform.getServiceManager().add(new LeagueService());
		Platform.getServiceManager().add(new GloryService());
		Platform.getServiceManager().add(new PayReturnService());
		Platform.getServiceManager().add(new LeagueCombatService());
		Platform.getUpdaterManager().addSyncUpdatable(Platform.getLeagueManager().getCombat());

		World world = (World) ((DbService) Platform.getServiceManager().get(DbService.class)).get(World.class,
				Integer.valueOf(1));
		if (world == null) {
			world = new World();
			world.setId(1);
			world.setCreateRobot(0);
			((DbService) Platform.getServiceManager().get(DbService.class)).add(world);
			world.init();
			Platform.setWorld(world);
			DBUtil.updatePlayerAutoIncrement();
			DBUtil.updateLeagueAutoIncrement();
			ActivityService.startRank7Activity();
			Platform.getEntityManager().putInEhCache("persist", "CACHE_KEY_OPENSERVERTIME",
					Long.valueOf(System.currentTimeMillis()));
		} else {
			world.init();
			Platform.setWorld(world);
		}

		Platform.getServiceManager().add(new RobotService());
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook()));

		Platform.getPlayerManager().loadMiniPlayer();

		long b = System.currentTimeMillis();
		Platform.getTopManager().refreshBTLRank();
		Platform.getLog().logSystem("rank btl cost : " + (System.currentTimeMillis() - b));
		long c = System.currentTimeMillis();
		Platform.getTopManager().refreshLevelRank();
		Platform.getLog().logSystem("rank level cost : " + (System.currentTimeMillis() - c));
		long d = System.currentTimeMillis();
		Platform.getTopManager().loadOldRanks();
		Platform.getLog().logSystem("rank load cost : " + (System.currentTimeMillis() - d));

		Thread t = new Thread(new AsyncRanker(), "AsyncRanker");
		t.setDaemon(true);
		t.start();
		Thread timer = new Thread(new Time(), "Timer");
		timer.setDaemon(true);
		timer.start();

		Platform.getServiceManager().add(new GateService());
		Platform.getServiceManager().add(new NettyService());
	}

	public void initConfiguration() {
		SAXReader reader = new SAXReader();
		try {
			URL url = ServerStartup.class.getClassLoader().getResource("cfg/config.xml");
			File f = new File(url.getFile());
			// System.out.println(f.getAbsolutePath());
			Document doc = reader.read(f);
			Element root = doc.getRootElement();
			Configuration.name = root.element("name").getStringValue();
			Configuration.serverId = Integer.parseInt(root.element("id").getStringValue());
			Configuration.resourcedir = root.element("resourcedir").getStringValue();
			Configuration.serverIp = root.element("serverip").getStringValue();
			Configuration.serverPort = Integer.parseInt(root.element("serverport").getStringValue());

			Configuration.test = root.element("test").getStringValue().equals("true");
			Configuration.pushPay = root.element("pushpay").getStringValue().equals("1");
			Configuration.gmIp = root.element("gmip").getStringValue();
			Configuration.gmPort = Integer.parseInt(root.element("gmport").getStringValue());
			Configuration.billingAdd = root.element("billing").getStringValue();
			Configuration.numbers = Calc.split(root.element("numbers").getStringValue(), ",");

			ChargeService.CHANNEL = root.element("defaultChannel").getStringValue();
			ChargeService.CHARGE_RATE = Integer.parseInt(root.element("chargeRate").getStringValue());
			ChargeService.MONTH_RMB = Integer.parseInt(root.element("montRMB").getStringValue());
			ChargeService.MONTH_CARD_ID = Integer.parseInt(root.element("monthCard").getStringValue());

			Element gateelem = root.element("gates");
			List<?> gates = gateelem.elements("gate");
			Configuration.gateIps = new String[gates.size()];
			Configuration.gatePorts = new int[gates.size()];
			for (int i = 0; i < gates.size(); ++i) {
				Element elem = (Element) gates.get(i);
				String ip = elem.attributeValue("ip");
				int port = Integer.parseInt(elem.attributeValue("port"));
				Configuration.gateIps[i] = ip;
				Configuration.gatePorts[i] = port;
			}
			// Element functionElement = root.element("functions");

			if (root.element("gmheart") != null)
				Configuration.gmHeart = root.element("gmheart").getStringValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		int ROUND_TIME = 100;
		Platform.getLog().logSystem("Server startup ok!");
		System.out.println("Server startup ok!");
		boolean coreSizeMark = false;
		while (running) {
			try {
				this.lastTime = System.currentTimeMillis();
				Platform.getUpdaterManager().update();
				if (tick == 2147483647)
					tick = 0;
				else {
					tick += 1;
				}
				long dis = System.currentTimeMillis() - this.lastTime;
				if (dis > 80L) {
					Platform.getLog().logWarn("Server Cycle too long:" + dis);
				}
				if (dis < ROUND_TIME) {
					if (dis < 0L) {
						dis = 0L;
					}
					Thread.sleep(ROUND_TIME - dis);
				}
				if (tick % 100 == 52) {
					int size = Platform.getThreadPool().getSize();
					int maxSize = 300;
					if ((size > 10) && (Platform.getThreadPool().getCoreSize() < maxSize))
						Platform.getThreadPool().setCoreSize(maxSize);
					else if ((size == 0) && (Platform.getThreadPool().getCoreSize() == maxSize)) {
						if (!(coreSizeMark)) {
							coreSizeMark = true;
						} else {
							Platform.getThreadPool().setCoreSize(10);
							coreSizeMark = false;
						}
					}
					if (size > 200)
						Platform.getLog().logError("Thread pool too large :" + size);
				}
			} catch (Throwable e) {
				Platform.getLog().logError(e);
			}
		}

		Platform.getLog().logSystem("Main Thread shutdown!!");
		shutdown = true;
	}

	class ShutdownHook implements Runnable {
		public void run() {
			try {
				ServerStartup.running = false;

				Platform.getLog().logSystem("shutdown hook cycle start");
				if ((ServerStartup.shutdown) && (Platform.getThreadPool().getSize() == 0)) {
					Platform.getLog().logSystem("start server shutdown!!");
					for (Service service : ServiceManager.services.values())
						try {
							service.shutdown();
							Platform.getLog().logSystem(service.getClass() + ":shutdown ok");
						} catch (Throwable e) {
							Platform.getLog().logError(service.getClass() + ":shutdown fail", e);
						}
					try {
						Platform.getEntityManager().update(Platform.getWorld());
					} catch (Exception e) {
						Platform.getLog().logError("World shutdown fail", e);
					}
					try {
						Platform.getTopManager().save();
					} catch (Exception e) {
						Platform.getLog().logError("Top shutdown fail", e);
					}
					Platform.getLog().logSystem("Server shutdown ok");
					return;
				}
				Thread.sleep(100L);
			} catch (Exception e) {
				Platform.getLog().logError(e);
			}
		}
	}
}
