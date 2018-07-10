package org.darcy.sanguo.account.check;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.Base64;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckLenovo extends AbstractAccountCheckAsyncCall {
	private static final String APPID = "1505120181085.app.ln";
	private static final String HOST = "https://passport.lenovo.com/interserver/authen/1.2/getaccountid";
	private static final String SUCCESS_ROOT = "IdentityInfo";
	private static final String FAIL_ROOT = "Error";
	private String token;

	public AccountCheckLenovo(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
	}

	public String getUrl() {
		return MessageFormat.format("{0}?lpsust={1}&realm={2}",
				new Object[] { "https://passport.lenovo.com/interserver/authen/1.2/getaccountid", this.token,
						"1505120181085.app.ln" });
	}

	public String getPostData() {
		return null;
	}

	public void unPack(JSONObject json) {
		String xml = new String(Base64.getDecoder().decode(json.getString("result").getBytes()));
		try {
			Document doc = new SAXReader().read(new ByteArrayInputStream(xml.getBytes()));
			Element root = doc.getRootElement();
			if ("IdentityInfo".equals(root.getName())) {
				this.account.setAccountId(root.element("AccountID").getStringValue());
				this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
				return;
			}
			this.loginError = PbDown.LoginCheckRst.LoginError.OVERDUE;
			Platform.getLog().logError("Login error  accountType:" + this.account.getChannelType() + " Code: "
					+ root.element("Code").getStringValue());
		} catch (DocumentException e) {
			e.printStackTrace();
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog()
					.logError("Login error  accountType:" + this.account.getChannelType() + " Code: parse xml");
		}
	}

	public String getRequestMethod() {
		return "GET";
	}
}
