package org.darcy.sanguo.account.check;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckNewMuZhiWan extends AbstractAccountCheckAsyncCall {
	private static final String URL = "http://i.muzhiwan.com/foreign/oauth/verification2.php";
	private final String gamekey = "15d714034651695bbb096a6dab1c5eca";
	private static final String SECURITY_KEY = "pAWG5KSsGV71OXnfXldv1zn6mjDD117r";
	private String token;
	private String openid;
	private String time;

	public AccountCheckNewMuZhiWan(ClientSession session, PbPacket.Packet packet)
			throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
		this.openid = ((String) this.params.get(1));
		this.time = String.valueOf(System.currentTimeMillis() / 1000L);
	}

	public String getUrl() {
		return "http://i.muzhiwan.com/foreign/oauth/verification2.php";
	}

	public String getPostData() {
		try {
			return MessageFormat
					.format("token={0}&gamekey={1}&openid={2}&timestamp={3}&_sign={4}",
							new Object[] {
									this.token, "15d714034651695bbb096a6dab1c5eca", this.openid, this.time, Calc
											.md5(Calc
													.md5(new StringBuilder().append("gamekey=")
															.append(URLEncoder.encode(
																	"15d714034651695bbb096a6dab1c5eca", "utf-8"))
															.append("&openid=")
															.append(URLEncoder.encode(this.openid, "utf-8"))
															.append("&timestamp=")
															.append(URLEncoder.encode(this.time, "utf-8"))
															.append("&token=")
															.append(URLEncoder.encode(this.token, "utf-8")).toString())
													+ "pAWG5KSsGV71OXnfXldv1zn6mjDD117r") });
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void unPack(JSONObject json) {
		if (json.getInt("result") == 0) {
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId(this.openid);
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: "
					+ json.getString("result_desc"));
		}
	}

	public String getRequestMethod() {
		return "POST";
	}
}
