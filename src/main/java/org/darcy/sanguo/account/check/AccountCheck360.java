package org.darcy.sanguo.account.check;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheck360 extends AbstractAccountCheckAsyncCall {
	private String userId;
	private String access_token;

	public AccountCheck360(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.access_token = ((String) this.params.get(0));
	}

	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://openapi.360.cn/user/me?access_token=");
		sb.append(this.access_token);

		return sb.toString();
	}

	public String getPostData() {
		return "";
	}

	public void unPack(JSONObject json) {
		if (json.has("id")) {
			this.userId = json.getString("id");

			this.account.setAccountId(this.userId);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + ",result:" + json.toString());
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
