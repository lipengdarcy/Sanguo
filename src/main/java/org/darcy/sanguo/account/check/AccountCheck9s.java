package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheck9s extends AbstractAccountCheckAsyncCall {
	private static final String API_KEY = "1d15af79c6f0b90a7b447f6fe06d5175";
	private static final String APP_ID = "CBKR";
	private String uid;
	private String token;

	public AccountCheck9s(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.uid = ((String) this.params.get(0));
		this.token = ((String) this.params.get(1));
	}

	public String getUrl() {
		return "http://niceplay.trafficmanager.net/api/MemberLoginCheck/";
	}

	private String getSign() {
		StringBuilder sb = new StringBuilder();
		sb.append("1d15af79c6f0b90a7b447f6fe06d5175").append("CBKR").append(this.uid).append(this.token)
				.append("1d15af79c6f0b90a7b447f6fe06d5175");
		return Calc.md5(sb.toString());
	}

	public String getPostData() {
		StringBuilder sb = new StringBuilder();
		sb.append("appid=").append("CBKR").append("&uid=").append(this.uid).append("&token=").append(this.token)
				.append("&sign=").append(getSign());
		return sb.toString();
	}

	public void unPack(JSONObject json) {
		String msg = json.getString("Message");
		int code = json.getInt("Code");
		if (code == 1) {
			this.account.setAccountId(this.uid);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if (code == 0) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
		} else if ((code == -1) || (code == -2)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
		}
		if (this.loginError != PbDown.LoginCheckRst.LoginError.SUCCESS)
			Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + " Code:" + code + " Message:" + msg);
	}

	public String getRequestMethod() {
		return "POST";
	}
}
