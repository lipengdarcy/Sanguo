package org.darcy.sanguo.account.check;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckHuawei extends AbstractAccountCheckAsyncCall {
	private static final String URL = "https://api.vmall.com/rest.php";
	private static final String NSP_SVC = "OpenUP.User.getInfo";
	private String token;
	private String nspTs;

	public AccountCheckHuawei(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
		this.nspTs = Long.toString(System.currentTimeMillis() / 1000L);
	}

	public String getUrl() {
		return "https://api.vmall.com/rest.php";
	}

	public String getPostData() {
		try {
			return MessageFormat.format("nsp_svc={0}&nsp_ts={1}&access_token={2}", new Object[] { "OpenUP.User.getInfo",
					this.nspTs, URLEncoder.encode(this.token, "utf-8").replaceAll("+", "%2B") });
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void unPack(JSONObject json) {
		if (json.has("userID")) {
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId(json.getString("userID"));
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Message:"
					+ ((json.has("error")) ? json.getString("error") : json.toString()));
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
