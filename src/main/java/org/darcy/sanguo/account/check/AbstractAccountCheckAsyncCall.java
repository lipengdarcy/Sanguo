package org.darcy.sanguo.account.check;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.Account;
import org.darcy.sanguo.asynccall.AsyncCall;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public abstract class AbstractAccountCheckAsyncCall extends AsyncCall {
	public static final String REQUEST_METHOD_POST = "POST";
	public static final String REQUEST_METHOD_GET = "GET";
	protected Account account;
	protected List<String> params;
	protected PbDown.LoginCheckRst.LoginError loginError;

	public AbstractAccountCheckAsyncCall(ClientSession session, PbPacket.Packet packet)
			throws InvalidProtocolBufferException {
		super(session, packet);
		PbUp.LoginCheck check = PbUp.LoginCheck.parseFrom(packet.getData());
		this.params = check.getAttatchParamsList();
		this.account = new Account(check.getAccountType(), check.getDeviceId(), check.getIp());
		session.setAccount(this.account);
	}

	public abstract String getUrl();

	public abstract String getPostData();

	public abstract void unPack(JSONObject paramJSONObject);

	public abstract String getRequestMethod();

	public void callback() {
		PbDown.LoginCheckRst.Builder rst = PbDown.LoginCheckRst.newBuilder();
		if ((this.loginError != null) && (this.loginError == PbDown.LoginCheckRst.LoginError.SUCCESS)) {
			rst.setResult(true);
			rst.addAttachedParams(this.account.getAccountId());
		} else if ((this.loginError != null) && (this.loginError == PbDown.LoginCheckRst.LoginError.OVERDUE)) {
			rst.setResult(false);
			rst.setErrorInfo("服务器繁忙，请稍后再试");
			rst.setLoginError(this.loginError);
		} else {
			rst.setResult(false);
			rst.setErrorInfo("登录状态过期，请重新登陆");
			rst.setLoginError(PbDown.LoginCheckRst.LoginError.BUSY);
		}

		this.session.send(2180, rst.build());
	}

	public void netOrDB() {
		try {
			String code;
			URL httpUrl = new URL(getUrl());
			HttpURLConnection http = (HttpURLConnection) httpUrl.openConnection();
			http.setConnectTimeout(5000);
			http.setReadTimeout(30000);
			http.setRequestMethod(getRequestMethod());
			http.setDoInput(true);
			http.setDoOutput(true);
			if (getRequestMethod().equals("POST")) {
				PrintStream out = new PrintStream(http.getOutputStream(), true, "UTF-8");
				out.print(getPostData());
				out.flush();
			}
			StringBuffer result = new StringBuffer();
			InputStream is = http.getInputStream();
			InputStreamReader read = new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(read);

			while ((code = reader.readLine()) != null) {
				result.append(code);
			}
			String message = result.toString();
			JSONObject json = null;
			try {
				json = JSONObject.fromObject(message);
			} catch (Exception e) {
				try {
					String msg = "{\"result\":\"" + message + "\"}";
					json = JSONObject.fromObject(msg);
				} catch (JSONException je) {
					message = "{\"result\":\"" + new String(Base64.getEncoder().encode(message.getBytes())) + "\"}";
					json = JSONObject.fromObject(message);
				}
			}
			unPack(json);
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}
}
