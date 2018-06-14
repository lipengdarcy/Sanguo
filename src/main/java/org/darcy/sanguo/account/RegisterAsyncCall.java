package org.darcy.sanguo.account;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.awardcenter.Awards;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.guard.Counter;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.hero.Warriors;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.loottreasure.ShieldInfo;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.relation.Relations;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.PayReturnService;
import org.darcy.sanguo.service.PlayerService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.util.PlayerLockService;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class RegisterAsyncCall extends AsyncCall {
	Player player = null;

	int errorNum = 0;

	long start = System.currentTimeMillis();
	long getStar;
	long getEnd;
	long back;
	long backEnd;

	public RegisterAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);

		String ip = session.getIp();
		if (ip != null) {
			Counter.add(String.valueOf(ip));
			int count = Counter.check(String.valueOf(ip), 60000L);
			if (count > 50)
				Platform.getLog().logError("Guard: Register packet too many!! ip: " + ip + "   Count:" + count);
			else if (count > 20)
				Platform.getLog().logWarn("Guard: Register packet too many!! ip: " + ip + "   Count:" + count);
		}
	}

	public void callback() {
		this.back = System.currentTimeMillis();
		PbDown.RegisterRst.Builder builder = PbDown.RegisterRst.newBuilder();
		if (this.errorNum == 0) {
			Player op = this.session.getPlayer();
			if (op != null) {
				op.setSession(null);
			}
			this.session.setPlayer(this.player);
			this.player.setSession(this.session);
			Platform.getPlayerManager().addPlayer(this.player);

			int[] itemIds = { 11001, 22001, 22003, 1125 };
			for (int i : itemIds) {
				Item item = ItemService.generateItem(i, this.player);
				this.player.getBags().addItem(item, 1, "regist");
			}

			builder.setResult(true);
			builder.setUser(this.player.genUserData());
			Function.notifyMainInterfaceFunction(this.player);
			MailService.checkAndendGlobalMail(this.player, 2);
			this.player.getRewardRecord().activateTimeLimitRewardIfExist(this.player);
			Platform.getLog().logLogin(this.player);
			Platform.getLog().logRegist(this.player);
		} else {
			if (this.errorNum == 1)
				builder.setErrInfo("账号已存在");
			else if (this.errorNum == 2)
				builder.setErrInfo("注册账号失败");
			else if (this.errorNum == 3)
				builder.setErrInfo("名字被占用，请更换名字");
			else if (this.errorNum == 4)
				builder.setErrInfo("名字须为1-6位");
			else if (this.errorNum == 5)
				builder.setErrInfo("性别选择有误");
			else if (this.errorNum == 6) {
				builder.setErrInfo("登录状态过期，请重新登录帐号");
			}
			builder.setResult(false);
		}
		PbDown.RegisterRst rst = builder.build();
		this.session.send(1004, rst);
		this.backEnd = System.currentTimeMillis();

		if (this.player != null) {
			this.player.loginSync();

			((PayReturnService) Platform.getServiceManager().get(PayReturnService.class)).registCheck(this.player);
			((PayReturnService) Platform.getServiceManager().get(PayReturnService.class)).newDayCheck(this.player);

			if (ActivityService.getRank7ActivityLeftTime() != -1L)
				this.player.getPool().set(26, Boolean.valueOf(true));
		}
	}

	public void netOrDB() {
		this.getStar = System.currentTimeMillis();
		PbUp.Register register = null;
		try {
			register = PbUp.Register.parseFrom(this.packet.getData());
		} catch (Exception e) {
			this.errorNum = 2;
			e.printStackTrace();
			return;
		}
		String uuid = register.getUuid().trim();
		int channelType = 1;
		if (!(Configuration.test)) {
			Account account = this.session.getAccount();
			if (account == null) {
				this.errorNum = 6;
				return;
			}
			uuid = account.getAccountId();
			if (uuid == null) {
				this.errorNum = 6;
				return;
			}
			channelType = account.getChannelType();
		}
		String name = register.getName().trim();
		int gender = register.getGender();
		if ((gender != 1) && (gender != 2)) {
			this.errorNum = 5;
			return;
		}
		int selectIndex = PlayerService.getSelectedIndex(gender);
		if (name.length() < 1) {
			this.errorNum = 4;
			return;
		}
		if ((Platform.getKeyWordManager().contains("blackword.txt", name))
				|| (Platform.getKeyWordManager().contains("nameblackword.txt", name))) {
			this.errorNum = 3;
			return;
		}
		synchronized (PlayerLockService.getLock("ac*" + uuid)) {
			long a = System.currentTimeMillis();
			Integer tmpId = DBUtil.getPlayerIdByAccountId(uuid, channelType);

			if (tmpId != null) {
				this.errorNum = 1;
				return;
			}
			a = System.currentTimeMillis();
			Player tmp = DBUtil.getPlayerByName(name);

			if (tmp != null) {
				this.errorNum = 3;
				return;
			}
			this.player = new Player();
			this.player.setName(name);
			this.player.setAccountId(uuid);
			this.player.setChannelType(channelType);
			this.player.setGender(gender);
			this.player.setRegisterTime(System.currentTimeMillis());

			ItemTemplate template = ItemService.getItemTemplate(selectIndex);
			MainWarrior warrior = new MainWarrior(template, this.player.getBags().getNewItemId());
			this.player.getBags().addItem(warrior, 1, "regist");
			Warriors w = new Warriors(warrior);
			this.player.setWarriors(w);
			this.player.setHeroIds(w.generateHeroIds());
			this.player.init();
			a = System.currentTimeMillis();
			this.player.getRewardRecord().registInit(this.player);
			((DbService) Platform.getServiceManager().get(DbService.class)).add(this.player);
		}

		if (this.player.getId() <= 0) {
			this.errorNum = 2;
			return;
		}

		ShieldInfo shieldInfo = new ShieldInfo(this.player.getId());
		Platform.getEntityManager().putInEhCache(ShieldInfo.class.getName(), Integer.valueOf(this.player.getId()),
				shieldInfo);

		Awards awards = new Awards();
		awards.setPlayerId(this.player.getId());
		this.player.setAwards(awards);
		long a = System.currentTimeMillis();

		Relations r = new Relations();
		r.setId(this.player.getId());
		this.player.setRelations(r);
		r.init();
		a = System.currentTimeMillis();

		this.player.getTaskRecord().refreshNewTask(this.player);
		this.getEnd = System.currentTimeMillis();
	}
}
