package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckTongbutui extends AbstractAccountCheckAsyncCall {
	public static final String appid = "150411";
	private String session;

	public AccountCheckTongbutui(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.session = ((String) this.params.get(0));
	}

	public String getUrl() {
		return "http://tgi.tongbu.com/api/LoginCheck.ashx?session=" + this.session + "&appid=" + "150411";
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		String result = json.getString("result");
		if (result.equals("0")) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + result);
		} else if (result.equals("-1")) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + result);
		} else {
			this.account.setAccountId(result);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
