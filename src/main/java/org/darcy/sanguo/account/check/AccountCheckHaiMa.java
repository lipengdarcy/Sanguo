package org.darcy.sanguo.account.check;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckHaiMa extends AbstractAccountCheckAsyncCall {
	public static final String APP_ID = "46567bf3bddf83be99db35471b1fc047";
	String uid;
	String token;

	public AccountCheckHaiMa(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.uid = ((String) this.params.get(0));
		this.token = ((String) this.params.get(1));
	}

	public String getUrl() {
		return Configuration.billingAdd + "/haimaLogin";
	}

	public String getPostData() {
		return "appid=46567bf3bddf83be99db35471b1fc047&t=" + this.token;
	}

	public void unPack(JSONObject json) {
		String result = json.getString("result");
		if (result.equals("success")) {
			this.account.setAccountId(this.uid);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + result);
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
