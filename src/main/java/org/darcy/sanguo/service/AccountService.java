package org.darcy.sanguo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.ChangeNameAsyncCall;
import org.darcy.sanguo.account.DeleteAccountAsyncCall;
import org.darcy.sanguo.account.LoginAsyncCall;
import org.darcy.sanguo.account.RandomNameAsyncCall;
import org.darcy.sanguo.account.RegisterAsyncCall;
import org.darcy.sanguo.account.check.AccountCheck360;
import org.darcy.sanguo.account.check.AccountCheck91;
import org.darcy.sanguo.account.check.AccountCheck9s;
import org.darcy.sanguo.account.check.AccountCheckAYouXi;
import org.darcy.sanguo.account.check.AccountCheckAisi;
import org.darcy.sanguo.account.check.AccountCheckAnZhi;
import org.darcy.sanguo.account.check.AccountCheckBaiDu;
import org.darcy.sanguo.account.check.AccountCheckCJ;
import org.darcy.sanguo.account.check.AccountCheckDangle;
import org.darcy.sanguo.account.check.AccountCheckFaceBook;
import org.darcy.sanguo.account.check.AccountCheckGY;
import org.darcy.sanguo.account.check.AccountCheckHaiMa;
import org.darcy.sanguo.account.check.AccountCheckIApple;
import org.darcy.sanguo.account.check.AccountCheckITools;
import org.darcy.sanguo.account.check.AccountCheckKuaiYong;
import org.darcy.sanguo.account.check.AccountCheckLenovo;
import org.darcy.sanguo.account.check.AccountCheckMuZhiWan;
import org.darcy.sanguo.account.check.AccountCheckNewMuZhiWan;
import org.darcy.sanguo.account.check.AccountCheckOPPO;
import org.darcy.sanguo.account.check.AccountCheckPP;
import org.darcy.sanguo.account.check.AccountCheckPPS;
import org.darcy.sanguo.account.check.AccountCheckPPTV;
import org.darcy.sanguo.account.check.AccountCheckTongbutui;
import org.darcy.sanguo.account.check.AccountCheckUC;
import org.darcy.sanguo.account.check.AccountCheckWandoujia;
import org.darcy.sanguo.account.check.AccountCheckXY;
import org.darcy.sanguo.account.check.AccountCheckXiaoMi;
import org.darcy.sanguo.account.check.AccountCheckYingYongBao;
import org.darcy.sanguo.account.check.AccountCheckYouku;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class AccountService implements Service, PacketHandler {
	public List<String> first = new ArrayList<String>();
	public List<String> maleSecond = new ArrayList<String>();
	public List<String> maleThird = new ArrayList<String>();
	public List<String> femaleSecond = new ArrayList<String>();
	public List<String> femaleThird = new ArrayList<String>();

	private static Random random = new Random();

	private void loadNames() {
		List<Row> list = ExcelUtils.getRowList("radname.xls", 2);
		for (Row row : list) {
			if (row == null) {
				return;
			}
			for (int j = 0; j < 5; ++j) {
				if (row.getCell(j) == null) {
					continue;
				}
				String str = row.getCell(j).getStringCellValue();
				switch (j) {
				case 0:
					this.first.add(str);
					break;
				case 1:
					this.maleSecond.add(str);
					break;
				case 2:
					this.maleThird.add(str);
					break;
				case 3:
					this.femaleSecond.add(str);
					break;
				case 4:
					this.femaleThird.add(str);
				}
			}
		}

	}

	public int[] getCodes() {
		return new int[] { 1001, 1003, 1005, 1007, 1009, 1207, 2179 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		switch (packet.getPtCode()) {
		case 1001:
			login(session, packet);
			break;
		case 1003:
			register(session, packet);
			break;
		case 1005:
			deleteAccount(session, packet);
			break;
		case 1007:
			randomName(session, packet);
			break;
		case 1009:
			changeName(session, packet);
			break;
		case 1207:
			heart(session);
			break;
		case 2179:
			loginCheck(session, packet);
		}
	}

	private void loginCheck(ClientSession session, PbPacket.Packet packet) {
		try {
			PbUp.LoginCheck check = PbUp.LoginCheck.parseFrom(packet.getData());

			if (Platform.getPlayerManager().players.size() >= 3000) {
				PbDown.LoginCheckRst.Builder rst = PbDown.LoginCheckRst.newBuilder();
				rst.setResult(false).setLoginError(PbDown.LoginCheckRst.LoginError.FULL).setErrorInfo("服务器爆满，请稍后重新登录");
				session.send(2180, rst.build());
				return;
			}
			switch (check.getAccountType()) {
			case 5:
				AccountCheckPP pp = new AccountCheckPP(session, packet);
				Platform.getThreadPool().execute(pp);
				break;
			case 20:
				AccountCheckXY xy = new AccountCheckXY(session, packet);
				Platform.getThreadPool().execute(xy);
				break;
			case 21:
				AccountCheckIApple ia = new AccountCheckIApple(session, packet);
				Platform.getThreadPool().execute(ia);
				break;
			case 22:
				AccountCheckFaceBook fb = new AccountCheckFaceBook(session, packet);
				Platform.getThreadPool().execute(fb);
				break;
			case 23:
				AccountCheckGY gy = new AccountCheckGY(session, packet);
				Platform.getThreadPool().execute(gy);
				break;
			case 10:
				AccountCheckTongbutui tb = new AccountCheckTongbutui(session, packet);
				Platform.getThreadPool().execute(tb);
				break;
			case 24:
				AccountCheckKuaiYong ky = new AccountCheckKuaiYong(session, packet);
				Platform.getThreadPool().execute(ky);
				break;
			case 25:
				AccountCheckITools it = new AccountCheckITools(session, packet);
				Platform.getThreadPool().execute(it);
				break;
			case 8:
				AccountCheckUC uc = new AccountCheckUC(session, packet);
				Platform.getThreadPool().execute(uc);
				break;
			case 26:
				AccountCheckCJ cj = new AccountCheckCJ(session, packet);
				Platform.getThreadPool().execute(cj);
				break;
			case 7:
				AccountCheckXiaoMi xm = new AccountCheckXiaoMi(session, packet);
				Platform.getThreadPool().execute(xm);
				break;
			case 27:
				AccountCheckBaiDu bd = new AccountCheckBaiDu(session, packet);
				Platform.getThreadPool().execute(bd);
				break;
			case 9:
				AccountCheck360 ac360 = new AccountCheck360(session, packet);
				Platform.getThreadPool().execute(ac360);
				break;
			case 29:
				AccountCheckAisi aisi = new AccountCheckAisi(session, packet);
				Platform.getThreadPool().execute(aisi);
				break;
			case 28:
				AccountCheckHaiMa hm = new AccountCheckHaiMa(session, packet);
				Platform.getThreadPool().execute(hm);
				break;
			case 4:
				AccountCheck91 a91 = new AccountCheck91(session, packet);
				Platform.getThreadPool().execute(a91);
				break;
			case 30:
				AccountCheck9s ac9s = new AccountCheck9s(session, packet);
				Platform.getThreadPool().execute(ac9s);
				break;
			case 31:
				AccountCheckYouku youku = new AccountCheckYouku(session, packet);
				Platform.getThreadPool().execute(youku);
				break;
			case 6:
				AccountCheckWandoujia wdj = new AccountCheckWandoujia(session, packet);
				Platform.getThreadPool().execute(wdj);
				break;
			case 14:
				AccountCheckDangle dangle = new AccountCheckDangle(session, packet);
				Platform.getThreadPool().execute(dangle);
				break;
			case 17:
				AccountCheckLenovo lenovo = new AccountCheckLenovo(session, packet);
				Platform.getThreadPool().execute(lenovo);
				break;
			case 32:
				AccountCheckPPTV pptv = new AccountCheckPPTV(session, packet);
				Platform.getThreadPool().execute(pptv);
				break;
			case 33:
				AccountCheckMuZhiWan mzw = new AccountCheckMuZhiWan(session, packet);
				Platform.getThreadPool().execute(mzw);
				break;
			case 13:
				AccountCheckOPPO oppo = new AccountCheckOPPO(session, packet);
				Platform.getThreadPool().execute(oppo);
				break;
			case 34:
				AccountCheckPPS pps = new AccountCheckPPS(session, packet);
				Platform.getThreadPool().execute(pps);
				break;
			case 11:
				AccountCheckAnZhi az = new AccountCheckAnZhi(session, packet);
				Platform.getThreadPool().execute(az);
				break;
			case 16:
			case 35:
			case 37:
			case 38:
			case 39:
				AccountCheckAYouXi agame = new AccountCheckAYouXi(session, packet);
				Platform.getThreadPool().execute(agame);
				break;
			case 36:
				AccountCheckYingYongBao yyb = new AccountCheckYingYongBao(session, packet);
				Platform.getThreadPool().execute(yyb);
				break;
			case 40:
				AccountCheckNewMuZhiWan nmzw = new AccountCheckNewMuZhiWan(session, packet);
				Platform.getThreadPool().execute(nmzw);
				return;
			case 12:
			case 15:
			case 18:
			case 19:
			}
		} catch (InvalidProtocolBufferException e) {
			Platform.getLog().logError(e);
		}
	}

	public String genName(int gender) {
		StringBuilder sb = new StringBuilder();
		sb.append((String) this.first.get(random.nextInt(this.first.size())));
		if (gender == 1) {
			if (random.nextDouble() > 0.5D) {
				sb.append((String) this.maleSecond.get(random.nextInt(this.maleSecond.size())));
			}
			sb.append((String) this.maleThird.get(random.nextInt(this.maleThird.size())));
		} else if (gender == 2) {
			if (random.nextDouble() > 0.5D) {
				sb.append((String) this.femaleSecond.get(random.nextInt(this.femaleSecond.size())));
			}
			sb.append((String) this.femaleThird.get(random.nextInt(this.femaleThird.size())));
		}
		return sb.toString();
	}

	private void login(ClientSession session, PbPacket.Packet packet) {
		AsyncCall call = new LoginAsyncCall(session, packet);
		Platform.getThreadPool().execute(call);
	}

	private void register(ClientSession session, PbPacket.Packet packet) {
		AsyncCall call = new RegisterAsyncCall(session, packet);
		Platform.getThreadPool().execute(call);
	}

	private void deleteAccount(ClientSession session, PbPacket.Packet packet) {
		AsyncCall call = new DeleteAccountAsyncCall(session, packet);
		Platform.getThreadPool().execute(call);
	}

	private void randomName(ClientSession session, PbPacket.Packet packet) {
		AsyncCall call = new RandomNameAsyncCall(session, packet);
		Platform.getThreadPool().execute(call);
	}

	private void changeName(ClientSession session, PbPacket.Packet packet) {
		ChangeNameAsyncCall call = new ChangeNameAsyncCall(session, packet);
		Platform.getThreadPool().execute(call);
	}

	private void heart(ClientSession session) {
		PbDown.HeartRst.Builder b = PbDown.HeartRst.newBuilder();
		b.setResult(true);
		session.send(1208, b.build());
	}

	public void startup() throws Exception {
		loadNames();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
