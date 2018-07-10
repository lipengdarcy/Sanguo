package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckIApple extends AbstractAccountCheckAsyncCall {
	private static final String GAME_KEY = "34cccbc03d023ad588747303d5e29519";
	private static final String SECRET_KEY = "3f4b137844aec9e29ab21449ebcd849e";
	private String userId;
	private String session;

	public AccountCheckIApple(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.userId = ((String) this.params.get(0));
		this.session = ((String) this.params.get(1));
	}

	public String getUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append("http://ucenter.iiapple.com/foreign/oauth/verification.php?");
		sb.append("user_id").append("=").append(this.userId).append("&");
		sb.append("session").append("=").append(this.session).append("&");
		sb.append("game_id").append("=").append("34cccbc03d023ad588747303d5e29519").append("&");
		sb.append("_sign").append("=").append(getSign());

		return sb.toString();
	}

	private String getSign() {
		StringBuilder sb = new StringBuilder();
		String key = "game_id=34cccbc03d023ad588747303d5e29519&session=" + this.session + "&user_id=" + this.userId;
		sb.append(Calc.md5(key));
		sb.append("3f4b137844aec9e29ab21449ebcd849e");
		String sign = Calc.md5(sb.toString());
		return sign;
	}

	public String getPostData() {
		return "";
	}

	public void unPack(JSONObject json) {
		String desc = json.getString("desc");
		int status = json.getInt("status");
		if (status == 1) {
			this.account.setAccountId(this.userId);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if (status == 0) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
		} else if ((status == 1000001) || (status == 1000002) || (status == 1000003)) {
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
