package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckAisi extends AbstractAccountCheckAsyncCall {
	private String token;

	public AccountCheckAisi(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
	}

	public String getUrl() {
		return "https://pay.i4.cn/member_third.action?token=" + this.token;
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		int status = json.getInt("status");
		if (status == 3) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + status);
		} else if (status == 0) {
			this.account.setAccountId(json.getString("userid"));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + status);
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
