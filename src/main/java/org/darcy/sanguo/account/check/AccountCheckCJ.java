package org.darcy.sanguo.account.check;

import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;

import com.alibaba.druid.util.HexBin;
import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckCJ extends AbstractAccountCheckAsyncCall {
	public static final String gameKey = "h62ex6afy3dex6n2yhezwanfmh4cj5m7";
	public static final String gameId = "92504f729bda4862c48f46ee99c170b2";

	static {
		try {
			TrustManager[] trustAllCerts = { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(javax.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(javax.security.cert.X509Certificate[] certs, String authType) {
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
				}
			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	public AccountCheckCJ(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
	}

	public String getUrl() {
		return "https://tokens.16801.com:8643/inter/checkLogin.page";
	}

	public String getPostData() {
		String channelID = "92504f729bda4862c48f46ee99c170b2".substring(0, 16);
		String gameID = "92504f729bda4862c48f46ee99c170b2".substring(16, 32);
		String result = "{'funcNo':9,'version':4,'sysType':0,'channelID':'" + channelID + "','gameID':'" + gameID
				+ "','uId':'" + ((String) this.params.get(0)) + "'";
		String result1 = result + ",\"gameKey\":\"" + "h62ex6afy3dex6n2yhezwanfmh4cj5m7" + "\"}";
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(result1.getBytes());
			String sign = HexBin.encode(md5.digest()).toLowerCase();
			return "param=" + result + "}&sign=" + sign;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public void unPack(JSONObject json) {
		JSONObject state = json;
		String code = state.getString("code");
		if (code.equals("1")) {
			this.account.setAccountId((String) this.params.get(0));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		} else if (code.equals("40000004")) {
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code);
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code:" + code);
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
