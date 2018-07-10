package org.darcy.sanguo.account.check;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckYingYongBao extends AbstractAccountCheckAsyncCall {
	private static final String URL = "http://msdktest.qq.com/auth/{0}?timestamp={1}&appid={2}&sig={3}&openid={4}&encode=1";
	private static final String QQAPPID = "1104765392";
	private static final String QQAPPKEY = "c0dQQeQcNhU661vM";
	private static final String WXAPPID = "wx5fc0dd7bef8bfd6b";
	private static final String WXAPPKEY = "f4a5a6e2208eae8b035a3b6e63848ff7";
	private static final String QQ = "2";
	private static final String QQ_BRANCH = "verify_login";
	private static final String WX_BRANCH = "check_token";
	private String channel;
	private String openId;
	private String openKey;
	private String time;

	public AccountCheckYingYongBao(ClientSession session, PbPacket.Packet packet)
			throws InvalidProtocolBufferException {
		super(session, packet);
		this.openId = ((String) this.params.get(0));
		this.openKey = ((String) this.params.get(1));
		this.channel = ((String) this.params.get(2));
		this.time = Long.toString(System.currentTimeMillis());
	}

	public String getUrl() {
		return MessageFormat.format(
				"http://msdktest.qq.com/auth/{0}?timestamp={1}&appid={2}&sig={3}&openid={4}&encode=1",
				new Object[] { ("2".equals(this.channel)) ? "verify_login" : "check_token", this.time,
						("2".equals(this.channel)) ? "1104765392" : "wx5fc0dd7bef8bfd6b",
						Calc.md5((("2".equals(this.channel)) ? "c0dQQeQcNhU661vM" : "f4a5a6e2208eae8b035a3b6e63848ff7")
								+ this.time),
						this.openId });
	}

	public String getPostData() {
		Map params = new HashMap();
		params.put("openid", this.openId);
		if ("2".equals(this.channel)) {
			params.put("openkey", this.openKey);
			params.put("appid", "1104765392");
			params.put("userip", this.session.getIp());
		} else {
			params.put("accessToken", this.openKey);
		}
		return JSONObject.fromObject(params).toString();
	}

	public void unPack(JSONObject json) {
		if (json.getInt("ret") == 0) {
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId(this.openId);
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + " Code:" + json.getString("msg"));
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
