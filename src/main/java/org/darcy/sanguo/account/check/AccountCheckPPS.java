package org.darcy.sanguo.account.check;

import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckPPS extends AbstractAccountCheckAsyncCall {
	private static final String KEY = "74974bf301ff7e270d0e1e6860735f38";
	private String uid;
	private String time;
	private String sign;

	public AccountCheckPPS(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.uid = ((String) this.params.get(0));
		this.time = ((String) this.params.get(1));
		this.sign = ((String) this.params.get(2));
	}

	public void netOrDB() {
		String _sign = Calc.md5(MessageFormat.format("{0}&{1}&{2}",
				new Object[] { this.uid, this.time, "74974bf301ff7e270d0e1e6860735f38" }));

		if (_sign.equals(this.sign)) {
			this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
			this.account.setAccountId(this.uid);
		} else {
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog().logError("Login error accountType:PPS Code: sign error");
		}
	}

	public String getUrl() {
		return null;
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
	}

	public String getRequestMethod() {
		return null;
	}
}
