package org.darcy.sanguo.account.check;

import java.nio.charset.Charset;
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

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckITools extends AbstractAccountCheckAsyncCall {
	public static final String APPKEY = "LS6hi6m1dpsrRIW3nTDoDwDSFj8o7GVT";
	private String session;

	static {
		disableSslVerification();
	}

	public AccountCheckITools(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.session = ((String) this.params.get(0));
	}

	private static void disableSslVerification() {
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

	public String getUrl() {
		return "https://pay.slooti.com/?r=auth/verify&appid=1048&sessionid=" + this.session + "&sign=" + getSign();
	}

	private String getSign() {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			String data = "appid=1048&sessionid=" + this.session;
			digest.update(data.getBytes(Charset.forName("utf-8")));
			return Calc.bytesToHexString(digest.digest()).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		String code = json.getString("status");
		if (code.equals("fail")) {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: fail");
		} else {
			this.account.setAccountId(this.session.split("_")[0]);
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
