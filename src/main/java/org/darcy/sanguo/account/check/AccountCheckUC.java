package org.darcy.sanguo.account.check;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.alibaba.druid.util.HexBin;
import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckUC extends AbstractAccountCheckAsyncCall {
	public static final int gameId = 552526;
	public static final int cpId = 43429;
	public static final String apiKey = "4e191fec84306dc7523a338786279639";

	public AccountCheckUC(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
	}

	public String getUrl() {
		return ((Configuration.test) ? "http://sdk.test4.g.uc.cn/cp/account.verifySession"
				: "http://sdk.g.uc.cn/cp/account.verifySession");
	}

	public String getPostData() {
		HashMap json = new HashMap();
		HashMap data = new HashMap();
		data.put("sid", (String) this.params.get(0));
		HashMap game = new HashMap();
		game.put("gameId", Integer.valueOf(552526));
		json.put("id", Long.valueOf(System.currentTimeMillis() / 1000L));
		json.put("data", data);
		json.put("game", game);
		json.put("sign", getSign());

		return JSONObject.fromObject(json).toString();
	}

	public void unPack(JSONObject json) {
		JSONObject state = json.getJSONObject("state");
		int code = state.getInt("code");
		if (code == 1) {
			JSONObject data = json.getJSONObject("data");
			this.account.setAccountId(data.getString("creator") + "_" + data.getString("accountId"));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if ((code == 10) || (code == 99)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code);
		} else if (code == 11) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code);
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code);
		}
	}

	public String getRequestMethod() {
		return "POST";
	}

	public String getSign() {
		String data = "sid=" + ((String) this.params.get(0)) + "4e191fec84306dc7523a338786279639";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data.getBytes(Charset.forName("utf-8")));
			return HexBin.encode(digest.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return "";
	}
}
