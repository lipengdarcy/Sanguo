package org.darcy.sanguo.account.check;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckYouku extends AbstractAccountCheckAsyncCall {
	private static final String PAYKEY = "09d4d17f27fba33c9e1aadf4d111233d";
	private static final String APPKEY = "0b6ef684bfa6b45f";
	private static final String ALGORITHM = "HmacMD5";
	private static final String SUCCESS = "success";
	private String session;

	public AccountCheckYouku(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.session = ((String) this.params.get(0));
	}

	public String getUrl() {
		return "http://sdk.api.gamex.mobile.youku.com/game/user/infomation";
	}

	public String getPostData() {
		StringBuilder postData = new StringBuilder();
		postData.append("appkey=").append("0b6ef684bfa6b45f").append("&sessionid=").append(this.session);
		try {
			SecretKeySpec sk = new SecretKeySpec("09d4d17f27fba33c9e1aadf4d111233d".getBytes(), "HmacMD5");
			Mac mac = Mac.getInstance("HmacMD5");
			mac.init(sk);
			byte[] bytes = mac.doFinal(postData.toString().getBytes());
			String _sign = Calc.bytesToHexString(bytes);
			postData.append("&sign=").append(_sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return postData.toString();
	}

	public void unPack(JSONObject json) {
		if ((json != null) && (!(json.isEmpty())) && ("success".equals(json.getString("status")))) {
			this.account.setAccountId(json.getString("uid"));
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			return;
		}

		this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
		Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType());
	}

	public String getRequestMethod() {
		return "POST";
	}
}
