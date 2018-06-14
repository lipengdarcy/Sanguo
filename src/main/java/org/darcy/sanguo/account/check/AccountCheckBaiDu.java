package org.darcy.sanguo.account.check;

import java.net.URLDecoder;
import java.security.MessageDigest;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckBaiDu extends AbstractAccountCheckAsyncCall {
	public static final String APP_ID = "5830784";
	public static final String SECRET_KEY = "eBR4TuVFn0gn84g1I7HSOV0WKNHnKMXB";
	private String accessToken;

	public AccountCheckBaiDu(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.accessToken = ((String) this.params.get(0));
	}

	public String getUrl() {
		return "http://querysdkapi.91.com/CpLoginStateQuery.ashx";
	}

	public String getPostData() {
		String sign = md5("5830784" + this.accessToken + "eBR4TuVFn0gn84g1I7HSOV0WKNHnKMXB");
		StringBuilder param = new StringBuilder();
		param.append("AppID=").append("5830784");
		param.append("&AccessToken=").append(this.accessToken);
		param.append("&Sign=").append(sign.toLowerCase());
		return param.toString();
	}

	public void unPack(JSONObject json) {
		try {
			if ((Integer.parseInt(json.getString("ResultCode")) == 1) && (md5("5830784" + json.getString("ResultCode")
					+ URLDecoder.decode(json.getString("Content"), "utf-8") + "eBR4TuVFn0gn84g1I7HSOV0WKNHnKMXB")
							.toLowerCase().equals(json.getString("Sign").toLowerCase()))) {
				String content = URLDecoder.decode(json.getString("Content"), "utf-8");
				String jsonStr = decode(content);
				JSONObject contentJson = JSONObject.fromObject(jsonStr);
				this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
				this.account.setAccountId(contentJson.getString("UID"));
				return;
			}
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: "
					+ json.getString("ResultCode"));
		} catch (Exception e) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: "
					+ json.getString("ResultCode"));
		}
	}

	public String getRequestMethod() {
		return "POST";
	}

	private String md5(String sourceStr) {
		String signStr = "";
		try {
			byte[] bytes = sourceStr.getBytes("utf-8");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(bytes);
			byte[] md5Byte = md5.digest();
			if (md5Byte != null)
				signStr = HexBin.encode(md5Byte);
		} catch (Exception e) {
			Platform.getLog()
					.logError("Login error  accountType:" + this.account.getChannelType() + " Code: create signs");
		}
		return signStr;
	}

	private String decode(String s) {
		byte[] buf;
		try {
			buf = decodeToByteArray(s);
			s = new String(buf, "UTF-8");
			s = s.replaceAll("[\\n|\\r]", "");
		} catch (Exception e) {
			return "";
		}
		return s;
	}

	private byte[] decodeToByteArray(String s) throws Exception {
		if (s.length() == 0)
			return null;
		byte[] buf = s.getBytes("iso-8859-1");
		byte[] debuf = new byte[buf.length * 3 / 4];
		byte[] tempBuf = new byte[4];
		int index = 0;
		int index1 = 0;

		int count = 0;
		int count1 = 0;
		for (int i = 0; i < buf.length; ++i) {
			if ((buf[i] >= 65) && (buf[i] < 91)) {
				tempBuf[(index++)] = (byte) (buf[i] - 65);
			} else if ((buf[i] >= 97) && (buf[i] < 123)) {
				tempBuf[(index++)] = (byte) (buf[i] - 71);
			} else if ((buf[i] >= 48) && (buf[i] < 58)) {
				tempBuf[(index++)] = (byte) (buf[i] + 4);
			} else if (buf[i] == 43) {
				tempBuf[(index++)] = 62;
			} else if (buf[i] == 47) {
				tempBuf[(index++)] = 63;
			} else if (buf[i] == 61) {
				tempBuf[(index++)] = 0;
				++count1;
			} else {
				if ((buf[i] == 10) || (buf[i] == 13) || (buf[i] == 32))
					continue;
				if (buf[i] == 9)
					continue;
				throw new RuntimeException("Illegal character found in encoded string!");
			}
			if (index == 4) {
				int temp = tempBuf[0] << 18 | tempBuf[1] << 12 | tempBuf[2] << 6 | tempBuf[3];
				debuf[(index1++)] = (byte) (temp >> 16);
				debuf[(index1++)] = (byte) (temp >> 8 & 0xFF);
				debuf[(index1++)] = (byte) (temp & 0xFF);
				count += 3;
				index = 0;
			}
		}
		byte[] hold = new byte[count - count1];
		System.arraycopy(debuf, 0, hold, 0, count - count1);
		return hold;
	}

	private static final class HexBin {
		private static final int BASELENGTH = 128;
		private static final int LOOKUPLENGTH = 16;
		private static final byte[] hexNumberTable = new byte[128];
		private static final char[] lookUpHexAlphabet = new char[16];

		static {
			int i;
			for (i = 0; i < 128; ++i) {
				hexNumberTable[i] = -1;
			}
			for (i = 57; i >= 48; --i) {
				hexNumberTable[i] = (byte) (i - 48);
			}
			for (i = 70; i >= 65; --i) {
				hexNumberTable[i] = (byte) (i - 65 + 10);
			}
			for (i = 102; i >= 97; --i) {
				hexNumberTable[i] = (byte) (i - 97 + 10);
			}

			for (i = 0; i < 10; ++i) {
				lookUpHexAlphabet[i] = (char) (48 + i);
			}
			for (i = 10; i <= 15; ++i)
				lookUpHexAlphabet[i] = (char) (65 + i - 10);
		}

		public static String encode(byte[] binaryData) {
			if (binaryData == null)
				return null;
			int lengthData = binaryData.length;
			int lengthEncode = lengthData * 2;
			char[] encodedData = new char[lengthEncode];

			for (int i = 0; i < lengthData; ++i) {
				int temp = binaryData[i];
				if (temp < 0)
					temp += 256;
				encodedData[(i * 2)] = lookUpHexAlphabet[(temp >> 4)];
				encodedData[(i * 2 + 1)] = lookUpHexAlphabet[(temp & 0xF)];
			}
			return new String(encodedData);
		}
	}
}
