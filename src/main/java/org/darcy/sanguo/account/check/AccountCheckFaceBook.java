package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckFaceBook extends AbstractAccountCheckAsyncCall {
	private String userId;
	private String access_token;

	public AccountCheckFaceBook(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.access_token = ((String) this.params.get(0));
	}

	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://graph.facebook.com/v2.2/me?access_token=");
		sb.append(this.access_token);
		sb.append("&fields=id&format=json");

		return sb.toString();
	}

	public String getPostData() {
		return "";
	}

	public void unPack(JSONObject json) {
		String desc = null;
		if (json.has("id")) {
			this.userId = json.getString("id");

			this.account.setAccountId(this.userId);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			JSONObject error = JSONObject.fromObject(json.get("error"));
			desc = error.getString("message");
		}

		if (this.loginError != PbDown.LoginCheckRst.LoginError.SUCCESS)
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + desc);
	}

	public String getRequestMethod() {
		return "GET";
	}
}
