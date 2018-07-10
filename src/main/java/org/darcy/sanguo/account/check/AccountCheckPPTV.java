package org.darcy.sanguo.account.check;

import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckPPTV extends AbstractAccountCheckAsyncCall {
	private static final String URL = "http://api.user.vas.pptv.com/c/v2/cksession.php";
	private static final String APP = "mobgame";
	private static final String TYPE = "login";
	private String sessionId;
	private String username;

	public AccountCheckPPTV(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.sessionId = ((String) this.params.get(0));
		this.username = ((String) this.params.get(1));
	}

	public String getUrl() {
		return MessageFormat.format("{0}?type={1}&sessionid={2}&username={3}&app={4}", new Object[] {
				"http://api.user.vas.pptv.com/c/v2/cksession.php", "login", this.sessionId, this.username, "mobgame" });
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		int code = json.getInt("status");
		switch (code) {
		case 1:
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId((String) this.params.get(2));
			break;
		case 0:
		default:
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: " + code
					+ " Message:" + json.getString("message"));
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
