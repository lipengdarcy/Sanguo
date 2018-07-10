package org.darcy.sanguo.account.check;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckPP extends AbstractAccountCheckAsyncCall {
	public AccountCheckPP(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
	}

	public String getUrl() {
		return "http://passport_i.25pp.com:8080/account?tunnel-command=2852126760";
	}

	private String getAppkey() {
		return "02a5280492e995bb795eaaca6f0d283c";
	}

	private String getSign() {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			String data = "sid=" + ((String) this.params.get(0)) + getAppkey();
			digest.update(data.getBytes(Charset.forName("utf-8")));
			return Calc.bytesToHexString(digest.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPostData() {
		HashMap data = new HashMap();
		HashMap game = new HashMap();
		game.put("gameId", Integer.valueOf(5671));
		HashMap dt = new HashMap();
		dt.put("sid", (String) this.params.get(0));
		data.put("id", String.valueOf(System.currentTimeMillis() / 1000L));
		data.put("service", "account.verifySession");
		data.put("game", game);
		data.put("encrypt", "md5");
		data.put("sign", getSign());
		data.put("data", dt);
		JSONObject json = JSONObject.fromObject(data);
		return json.toString();
	}

	public void unPack(JSONObject json) {
		JSONObject state = json.getJSONObject("state");
		int code = state.getInt("code");
		if (code == 1) {
			JSONObject data = json.getJSONObject("data");
			this.account.setAccountId(data.getString("creator") + data.getString("accountId"));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if ((code == 10) || (code == 99)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " " + state.getString("msg"));
		} else if (code == 11) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " " + state.getString("message"));
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " " + state.getString("message"));
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
