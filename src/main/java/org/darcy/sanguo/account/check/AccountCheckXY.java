package org.darcy.sanguo.account.check;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckXY extends AbstractAccountCheckAsyncCall {
	public AccountCheckXY(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
	}

	public String getUrl() {
		return "http://passport.xyzs.com/checkLogin.php";
	}

	public String getPostData() {
		return "uid=" + ((String) this.params.get(0)) + "&appid=100007464&token=" + ((String) this.params.get(1));
	}

	public void unPack(JSONObject json) {
		int code = json.getInt("ret");
		if (code == 0) {
			this.account.setAccountId((String) this.params.get(0));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if ((code == 2) || (code == 20) || (code == 999)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " " + json.getString("error"));
		} else if (code == 997) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " " + json.getString("error"));
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code
					+ " " + json.getString("error"));
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
