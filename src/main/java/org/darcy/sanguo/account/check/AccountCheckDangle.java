package org.darcy.sanguo.account.check;

import java.security.MessageDigest;
import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckDangle extends AbstractAccountCheckAsyncCall {
	private static final String APPID = "3449";
	private static final String HOST = "http://ngsdk.d.cn/api/cp/checkToken";
	private static final String APPKEY = "uyX3plyl";
	private static final String ALGORITHM = "MD5";
	private String token;
	private String umid;

	public AccountCheckDangle(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
		this.umid = ((String) this.params.get(1));
	}

	public String getUrl() {
		String needMd5Str = MessageFormat.format("{0}|{1}|{2}|{3}",
				new Object[] { "3449", "uyX3plyl", this.token, this.umid });
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md5Bytes = md5.digest(needMd5Str.getBytes("UTF-8"));
			if (md5Bytes == null)
				return null;
			StringBuilder hexValue = new StringBuilder();
			for (int i = 0; i < md5Bytes.length; ++i) {
				int val = md5Bytes[i] & 0xFF;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			return MessageFormat.format("{0}?appid={1}&umid={2}&token={3}&sig={4}", new Object[] {
					"http://ngsdk.d.cn/api/cp/checkToken", "3449", this.umid, this.token, hexValue.toString() });
		} catch (Exception e) {
			e.printStackTrace();
			Platform.getLog()
					.logError("Login error  accountType:" + this.account.getChannelType() + " Code: create signs");
		}
		return null;
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		if ((json != null) && (json.has("msg_code"))) {
			int msg_code = json.getInt("msg_code");
			if (msg_code == 2000) {
				if (json.getInt("valid") == 1) {
					this.account.setAccountId(this.umid);
					this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
				} else {
					this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
					Platform.getLog().logError(
							"Login error  accountType:" + this.account.getChannelType() + " Code: token error");
				}
			} else {
				this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
				Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: "
						+ msg_code + ";msg_desc:" + json.getString("msg_desc"));
			}
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + " Code: dangleserver error");
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
