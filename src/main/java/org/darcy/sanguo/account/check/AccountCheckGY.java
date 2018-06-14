package org.darcy.sanguo.account.check;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckGY extends AbstractAccountCheckAsyncCall {
	private static final String GAME_KEY = "2a5fa67825a9bb8305be74f599ac1b5e";
	private String accountId;
	private String token;

	public AccountCheckGY(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.accountId = ((String) this.params.get(0));
		this.token = ((String) this.params.get(1));
	}

	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(Configuration.billingAdd).append("/AccountLoginCheck?");
		sb.append("id").append("=").append(this.accountId).append("&");
		sb.append("token").append("=").append(this.token).append("&");
		sb.append("sign").append("=").append(getSign());

		return sb.toString();
	}

	private String getSign() {
		StringBuilder sb = new StringBuilder();
		String key = "id=" + this.accountId + "&token=" + this.token;
		sb.append(Calc.md5(key));
		sb.append("2a5fa67825a9bb8305be74f599ac1b5e");
		String sign = Calc.md5(sb.toString());
		return sign;
	}

	public String getPostData() {
		return "";
	}

	public void unPack(JSONObject json) {
		String desc = json.getString("desc");
		int status = json.getInt("status");
		if (status == 0) {
			this.account.setAccountId(this.accountId);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if (status == 1) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
		} else if ((status == 2) || (status == 3)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
		}
		if (this.loginError != PbDown.LoginCheckRst.LoginError.SUCCESS)
			Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + " Status:" + status + " " + desc);
	}

	public String getRequestMethod() {
		return "GET";
	}
}
