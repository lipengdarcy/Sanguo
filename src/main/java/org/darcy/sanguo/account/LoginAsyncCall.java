package org.darcy.sanguo.account;

import java.util.Date;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.guard.Counter;
import org.darcy.sanguo.loottreasure.ShieldInfo;
import org.darcy.sanguo.pay.PayRecord;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.util.DBUtil;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class LoginAsyncCall extends AsyncCall {
	Player player = null;

	int errorNum = 0;

	long start = System.currentTimeMillis();
	long getStar;
	long getEnd;
	long back;

	public LoginAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);

		String ip = session.getIp();
		if (ip != null) {
			Counter.add(String.valueOf(ip));
			int count = Counter.check(String.valueOf(ip), 60000L);
			if (count > 50)
				Platform.getLog().logError("Guard: Login packet too many!! ip: " + ip + "   Count:" + count);
			else if (count > 20)
				Platform.getLog().logWarn("Guard: Login packet too many!! ip: " + ip + "   Count:" + count);
		}
	}

	public void callback() {
		this.back = System.currentTimeMillis();
		PbDown.LoginRst.Builder builder = PbDown.LoginRst.newBuilder();
		if (this.errorNum == 0) {
			this.player.setSession(this.session);
			this.session.setPlayer(this.player);

			this.player.init();
			Platform.getPlayerManager().addPlayer(this.player);
			this.player.newDayRefreshIf();
			MailService.checkAndendGlobalMail(this.player, 1);
			this.player.setLastLogin(new Date());
			this.player.getRewardRecord().activateTimeLimitRewardIfExist(this.player);
			this.player.getStarcatalogRecord().activeCatalog(this.player);
			this.player.getActivityRecord().checkDayRewardFresh(this.player);
			PayRecord.loginRefresh(this.player);

			builder.setResult(true);
			builder.setUser(this.player.genUserData());
			Function.notifyMainInterfaceFunction(this.player);
			Platform.getLog().logLogin(this.player);
		} else {
			builder.setResult(true);
			if (this.errorNum == 1) {
				builder.setErrInfo("账号不存在");
			} else if (this.errorNum == 2) {
				builder.setResult(false);
				builder.setErrInfo("登陆失败");
			} else if (this.errorNum == 3) {
				builder.setResult(false);
				builder.setErrInfo("版本号过低，重启更新客户端");
			} else if (this.errorNum == 4) {
				builder.setResult(false);
				builder.setErrInfo("账号已被封停，如有异议请联系客服处理");
			} else if (this.errorNum == 6) {
				builder.setResult(false);
				builder.setErrInfo("登录状态过期，请重新登录帐号");
			}
		}
		if (this.player != null) {
			builder.setGuide(this.player.getPool().getString(18, ""));
		}
		PbDown.LoginRst rst = builder.build();
		this.session.send(1002, rst);

		if (this.player != null) {
			this.player.loginSync();
			this.player.getRandomShop().cherishRefreshIf(this.player);
			if (ActivityService.getRank7ActivityLeftTime() != -1L)
				this.player.getPool().set(26, Boolean.valueOf(true));
		}
	}

	public void netOrDB() {
		this.getStar = System.currentTimeMillis();
		PbUp.Login login = null;
		try {
			login = PbUp.Login.parseFrom(this.packet.getData());
		} catch (InvalidProtocolBufferException e) {
			this.errorNum = 2;
			e.printStackTrace();
			return;
		}

		if ((Configuration.version != null) && (login.hasVersion())
				&& (Calc.versionCompare(login.getVersion(), Configuration.version) < 0)) {
			this.errorNum = 3;
			return;
		}

		String uuid = login.getUuid().trim();

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
		Integer id = DBUtil.getPlayerIdByAccountId(uuid, channelType);
		if (id == null) {
			this.errorNum = 1;
			return;
		}

		if (BanAccount.isBan(uuid)) {
			this.errorNum = 4;
			return;
		}

		this.player = Platform.getPlayerManager().getPlayerById(id.intValue());

		if (this.player == null) {
			try {
				this.player = Platform.getPlayerManager().getPlayer(id.intValue(), true, false);
			} catch (Exception e) {
				this.errorNum = 2;
				e.printStackTrace();
				return;
			}
		}
		this.player.save();
		if (this.player.getSession() != null) {
			this.player.getSession().send(2188, null);
			this.player.getSession().disconnect();
		}

		try {
			this.player.asyncInit();
		} catch (Exception e) {
			this.errorNum = 2;
			e.printStackTrace();
			return;
		}

		ShieldInfo.getShiledInfo(this.player.getId());

		this.getEnd = System.currentTimeMillis();
	}
}
