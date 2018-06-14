package org.darcy.sanguo.account.check;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckAnZhi extends AbstractAccountCheckAsyncCall {
	private static final String URL = "http://user.anzhi.com/web/api/sdk/third/1/queryislogin";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static final String APPKEY = "14364110279q5tDVK9L7il7bjk28wn";
	private static final String APPSECRET = "NgAWo85Evs5UqkYICNntVcB6";
	private String accountId;
	private String sid;

	public AccountCheckAnZhi(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.accountId = ((String) this.params.get(0));
		this.sid = ((String) this.params.get(1));
	}

	public String getUrl() {
		return "http://user.anzhi.com/web/api/sdk/third/1/queryislogin";
	}

	public String getPostData() {
		try {
			// 签名token
			// String sign = new
			// String(Base64.getEncoder().encode("14364110279q5tDVK9L7il7bjk28wn" +
			// this.accountId+ this.sid + "NgAWo85Evs5UqkYICNntVcB6".getBytes("utf-8")),
			// "utf-8");
			String sign = "";
			return MessageFormat.format("time={0}&appkey={1}&account={2}&sid={3}&sign={4}", new Object[] {
					this.sdf.format(new Date()), "14364110279q5tDVK9L7il7bjk28wn", this.accountId, this.sid, sign });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void unPack(JSONObject json) {
		String message = "";
		int code = json.getInt("sc");
		switch (code) {
		case 1:
		case 200:
			try {
				JSONObject msg = JSONObject.fromObject(
						new String(Base64.getDecoder().decode(json.getString("msg").getBytes("utf-8")), "utf-8"));
				if (msg.has("uid")) {
					this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
					this.account.setAccountId(msg.getString("uid"));
				}
				this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
				Platform.getLog().logError(
						"Login error  accountType:" + this.account.getChannelType() + " Code: " + msg.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
				Platform.getLog().logError(
						"Login error  accountType:" + this.account.getChannelType() + " Code: " + e.getMessage());
			}

			break;
		case 0:
			message = "失败(sid 无效)";
			break;
		case 5:
			message = "Sign 无效";
			break;
		case 10:
			message = "请求参数错误";
			break;
		case 202:
			message = "登录失败，用户状态不正常";
			break;
		case 205:
			message = "账号不存在";
			break;
		case 206:
			message = "用户已经登录";
			break;
		case 207:
			message = "用户已经退出";
			break;
		case 208:
			message = "用户名、邮箱、手机号已存在";
			break;
		case 905:
			message = "内部接口错误";
			break;
		case 906:
			message = "IO 异常";
			break;
		case 907:
			message = "SQL 异常";
			break;
		case 908:
			message = "Runtime 异常";
			break;
		case 999:
			message = "其他错误";
		}

		if ((code != 200) && (code != 1))
			label389: Platform.getLog().logError(
					"Login error  accountType:" + this.account.getChannelType() + " Code: " + code + " Msg:" + message);
	}

	public String getRequestMethod() {
		return "POST";
	}
}
