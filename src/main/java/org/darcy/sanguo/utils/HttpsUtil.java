 package org.darcy.sanguo.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsUtil {
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String DEFAULT_CHARSET = "utf-8";

	public static String sendPost(String url, String params) {
		try {
			String charset = "UTF-8";
			int connectTimeout = 30000;
			int readTimeout = 30000;
			byte[] content = new byte[0];
			if (params != null) {
				content = params.getBytes(charset);
			}
			return doPost(url, null, content, connectTimeout, readTimeout);
		} catch (Exception e) {
		}
		return "系统异常";
	}

	public static String doJsonPost(String url, String params, String charset, int connectTimeout, int readTimeout)
			throws Exception {
		String ctype = "application/json;charset=" + charset;
		byte[] content = new byte[0];
		if (params != null) {
			content = params.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	public static String doPost(String url, String contentType, String params, String charset, int connectTimeout,
			int readTimeout) throws Exception {
		String ctype = null;
		if (contentType != null) {
			ctype = contentType + ";charset=" + charset;
		}
		byte[] content = new byte[0];
		if (params != null) {
			content = params.getBytes(charset);
		}
		return doPost(url, ctype, content, connectTimeout, readTimeout);
	}

	public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
			throws Exception {
		HttpsURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
				SSLContext.setDefault(ctx);

				conn = getConnection(new URL(url), "POST", ctype);
				conn.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				out = new DataOutputStream(conn.getOutputStream());

				out.write(content);
				out.flush();
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	public static String doGet(String url, String contentType, int connectTimeout, int readTimeout) throws Exception {
		HttpsURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
				SSLContext.setDefault(ctx);
				conn = getConnection(new URL(url), "GET", contentType);
				conn.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				conn.setSSLSocketFactory(ctx.getSocketFactory());
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	public static String doGet2(String url, String contentType, int connectTimeout, int readTimeout, File certFile)
			throws Exception {
		HttpsURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] { new MyX509TrustManager(certFile) },
						new SecureRandom());
				SSLContext.setDefault(ctx);

				conn = getConnection(new URL(url), "GET", contentType);
				conn.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
				conn.setSSLSocketFactory(ctx.getSocketFactory());
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				rsp = getResponseAsString(conn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpsURLConnection getConnection(URL url, String method, String ctype) throws IOException {
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		if (ctype != null) {
			conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
			conn.setRequestProperty("User-Agent", "stargate");
			conn.setRequestProperty("Content-Type", ctype);
		}
		return conn;
	}

	protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			return getStreamAsString(conn.getInputStream(), charset);
		}
		String msg = getStreamAsString(es, charset);

		System.out.println(msg);
		if (msg == null) {
			throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
		}
		throw new IOException(msg);
	}

	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null)
				stream.close();
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = "utf-8";

		if (ctype != null) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if ((pair.length != 2) || (pair[1] == null))
						break;
					charset = pair[1].trim();

					break;
				}
			}
		}

		return charset;
	}

	public static void main(String[] arg) throws Exception {
		String rs = doPost("https://192.168.1.46:8543/test.jsp", null, "s=aa", "utf8", 1000, 1000);
		System.out.println(rs);
	}

	private static class DefaultTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	private static class MyX509TrustManager implements X509TrustManager {
		File certFile;
		X509TrustManager sunJSSEX509TrustManager;

		public MyX509TrustManager(File certFile) throws Exception {
			this.certFile = certFile;
			MyX509TrustManager();
		}

		public void MyX509TrustManager() throws Exception {
			KeyStore ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream(this.certFile), "password".toCharArray());

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			tmf.init(ks);

			TrustManager[] tms = tmf.getTrustManagers();

			for (int i = 0; i < tms.length; ++i) {
				if (tms[i] instanceof X509TrustManager) {
					this.sunJSSEX509TrustManager = ((X509TrustManager) tms[i]);
					return;
				}

			}

			throw new Exception("Couldn't initialize");
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			try {
				this.sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
			} catch (CertificateException localCertificateException) {
			}
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			try {
				this.sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
			} catch (CertificateException localCertificateException) {
			}
		}

		public X509Certificate[] getAcceptedIssuers() {
			return this.sunJSSEX509TrustManager.getAcceptedIssuers();
		}
	}
}
