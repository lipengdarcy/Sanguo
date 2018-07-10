package org.darcy.sanguo.account.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckXiaoMi extends AbstractAccountCheckAsyncCall {
	public static final String BASE_URL = "http://mis.migc.xiaomi.com/api/biz/service/verifySession.do";
	public static final String APP_ID = "2882303761517331309";
	public static final String SECRET_KEY = "QklM6yBrgROKTHtLDnjEdA==";
	public static final int CODE_SUCCESS = 200;
	private String session;
	private String uid;

	public AccountCheckXiaoMi(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.uid = ((String) this.params.get(0));
		this.session = ((String) this.params.get(1));
	}

	public String getUrl() {
		Map params = new HashMap();
		params.put("appId", String.valueOf("2882303761517331309"));
		params.put("uid", this.uid);
		params.put("session", this.session);
		String url = null;
		try {
			url = getRequestUrl(params, "http://mis.migc.xiaomi.com/api/biz/service/verifySession.do");
		} catch (Exception e) {
			e.printStackTrace();
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
		}
		return url;
	}

	public String getPostData() {
		return null;
	}

	private String getRequestUrl(Map<String, String> params, String baseUrl) throws Exception {
		String signString = getSortQueryString(params);
		String signature = getSign(params);
		return baseUrl + "?" + signString + "&signature=" + signature;
	}

	private String getSign(Map<String, String> params) throws Exception {
		String signString = getSortQueryString(params);
		return hmacSHA1Encrypt(signString, "QklM6yBrgROKTHtLDnjEdA==");
	}

	private String hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
		byte[] data = encryptKey.getBytes("UTF-8");

		SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1");

		Mac mac = Mac.getInstance("HmacSHA1");

		mac.init(secretKey);
		byte[] text = encryptText.getBytes("UTF-8");

		byte[] digest = mac.doFinal(text);
		StringBuilder sBuilder = bytesToHexString(digest);
		return sBuilder.toString();
	}

	public static StringBuilder bytesToHexString(byte[] bytesArray) {
		if (bytesArray == null) {
			return null;
		}
		StringBuilder sBuilder = new StringBuilder();
		for (byte b : bytesArray) {
			String hv = String.format("%02x", new Object[] { Byte.valueOf(b) });
			sBuilder.append(hv);
		}
		return sBuilder;
	}

	private String getSortQueryString(Map<String, String> params) throws Exception {
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		StringBuffer sb = new StringBuffer();
		for (Object key : keys) {
			sb.append(String.valueOf(key)).append("=").append((String) params.get(String.valueOf(key))).append("&");
		}

		String text = sb.toString();
		if (text.endsWith("&")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	public void unPack(JSONObject json) {
		int errcode = json.getInt("errcode");
		if (200 == errcode) {
			this.account.setAccountId(this.uid);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog()
					.logError("Login error  accountType:" + this.account.getChannelType() + " Code: " + errcode);
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
