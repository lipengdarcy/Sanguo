package org.darcy.sanguo.account.chujian;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.utils.SdkHelp;

public class ChujianManager implements Runnable {
	public static final String test = "http://60.55.38.62:8280/inter";
	public static final String live = "https://ga.16801.com";
	private static LinkedBlockingQueue<ChujianLog> queue = new LinkedBlockingQueue<ChujianLog>(10000);
	public static HashMap<String, ChujianKey> keys = new HashMap<String, ChujianKey>();

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

	public static void loadData() {
	}

	public static void addLog(ChujianLog log) {
		if (log == null)
			return;
		queue.offer(log);
	}

	private static boolean paymentLog(ChujianLog cj) {
		try {
			ChujianKey key = (ChujianKey) keys.get(cj.channel);
			if (key == null) {
				Platform.getLog().logError("CJSDK:  send error. no game keys:" + cj.toString());
				return true;
			}
			if ((cj.count > 3) && (System.currentTimeMillis() - cj.lastSendTime < 120000L)) {
				return false;
			}

			cj.lastSendTime = System.currentTimeMillis();
			cj.count += 1;

			String msg = SdkHelp.saverechargeLogs(
					(Configuration.test) ? "http://60.55.38.62:8280/inter" : "https://ga.16801.com", key.gameId,
					key.gameKey, cj.accountId, cj.level, cj.orderId, cj.itemId, Double.valueOf(cj.currencyAmount),
					Integer.valueOf(cj.vcAmount), "CNY", String.valueOf(cj.serverId), 1);
			Platform.getLog().logWorld("CJSDK:  send:" + cj.orderId + "  " + msg);
			if (msg.contains("fail")) {
				Platform.getLog().logError("CJSDK:  send error. Feedback:" + msg);
				return false;
			}
			return true;
		} catch (Exception e) {
			Platform.getLog().logError("CJSDK: send error.");
			Platform.getLog().logError(e);
		}
		return false;
	}

	public void run() {
		try {
			ChujianLog log;
			while (true) {
				do {
					Thread.sleep(20L);
					log = (ChujianLog) queue.take();
				} while (paymentLog(log));

				if (log.count >= 10)
					break;
				addLog(log);
			}

			Platform.getLog().logError("CJSDK: faild too many times. " + log.toString());
		} catch (Throwable e) {
			Platform.getLog().logError(e);
		}
	}

	public static void test() {
		ChujianLog cj = new ChujianLog("AISI", "6838963", 2, "0101P3RIBMTAZ7KZ", "1003", 6.0F, 60, 1);
		paymentLog(cj);
	}
}
