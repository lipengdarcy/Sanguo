package org.darcy.sanguo.account.check;

import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckMuZhiWan extends AbstractAccountCheckAsyncCall {
	private static final String URL = "http://sdk.muzhiwan.com/oauth2/getuser.php";
	private static final String APPKEY = "fcfcf1e012d8fc17e5f0a1b1e1dac985";
	private String token;

	public AccountCheckMuZhiWan(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
	}

	public String getUrl() {
		return MessageFormat.format("{0}?token={1}&appkey={2}", new Object[] {
				"http://sdk.muzhiwan.com/oauth2/getuser.php", this.token, "fcfcf1e012d8fc17e5f0a1b1e1dac985" });
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		if (json.getInt("code") == 1) {
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId(json.getJSONObject("user").getString("uid"));
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + " Code: " + json.getString("msg"));
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
