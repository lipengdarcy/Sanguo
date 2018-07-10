package org.darcy.sanguo.account.check;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckKuaiYong extends AbstractAccountCheckAsyncCall {
	public static final String APPKEY = "9f5bfb621856afa7175a5f024cfa6c26";
	private String token;

	public AccountCheckKuaiYong(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
	}

	public String getUrl() {
		return "http://f_signin.bppstore.com/loginCheck.php?tokenKey=" + this.token + "&sign=" + getSign();
	}

	private String getSign() {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			String data = "9f5bfb621856afa7175a5f024cfa6c26" + ((String) this.params.get(0));
			digest.update(data.getBytes(Charset.forName("utf-8")));
			return Calc.bytesToHexString(digest.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		int code = json.getInt("code");
		if (code == 2) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " Message:" + json.getString("msg"));
		} else if ((code == 1) || (code == 3) || (code == 4) || (code == 5) || (code == 100) || (code == 6)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " Message:" + json.getString("msg"));
		} else {
			this.account.setAccountId(json.getJSONObject("data").getString("guid"));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
