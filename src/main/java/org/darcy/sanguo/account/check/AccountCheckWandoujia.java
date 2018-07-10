package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckWandoujia extends AbstractAccountCheckAsyncCall {
	private static final String APPKEY_ID = "100025999";
	private String uid;
	private String token;

	public AccountCheckWandoujia(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.uid = ((String) this.params.get(0));
		this.token = ((String) this.params.get(1));
	}

	public String getUrl() {
		StringBuilder url = new StringBuilder();
		url.append("https://pay.wandoujia.com/api/uid/check?").append("uid=").append(this.uid).append("&token=")
				.append(this.token).append("&appkey_id=").append("100025999");
		return url.toString();
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		if ((json != null) && ("true".equals(json.getString("result")))) {
			this.account.setAccountId(this.uid);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: fail");
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
