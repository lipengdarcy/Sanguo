package org.darcy.sanguo.account.check;

import java.text.MessageFormat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckAYouXi extends AbstractAccountCheckAsyncCall {
	private static final String URL = "http://asdk.ay99.net:8081/loginvalid.php";
	private static final String GAMEID = "100225";
	private static final String APPKEY = "d34cbc2b5257a909";
	private String accountId;
	private String sessionId;

	public AccountCheckAYouXi(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.accountId = ((String) this.params.get(0));
		this.sessionId = ((String) this.params.get(1));
	}

	public String getUrl() {
		return MessageFormat.format("{0}?accountid={1}&sessionid={2}&gameid={3}&sign={4}",
				new Object[] { "http://asdk.ay99.net:8081/loginvalid.php", this.accountId, this.sessionId, "100225",
						Calc.md5(MessageFormat.format("accountid={0}&gameid={1}&sessionid={2}{3}",
								new Object[] { this.accountId, "100225", this.sessionId, "d34cbc2b5257a909" })) });
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		if (json.getInt("code") == 0) {
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId(this.accountId);
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: "
					+ json.getInt("code") + " Msg:" + json.getString("msg"));
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
