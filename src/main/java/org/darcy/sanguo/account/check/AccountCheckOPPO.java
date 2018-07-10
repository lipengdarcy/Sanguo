package org.darcy.sanguo.account.check;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nearme.oauth.log.NearMeException;
import com.nearme.oauth.model.AccessToken;
import com.nearme.oauth.open.AccountAgent;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class AccountCheckOPPO extends AbstractAccountCheckAsyncCall {
	private String token;

	public AccountCheckOPPO(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.token = ((String) this.params.get(0));
	}

	public void netOrDB() {
		try {
			String oauth_token = this.token.split("&")[0].split("=")[1];
			String oauth_token_secret = this.token.split("&")[1].split("=")[1];
			JSONObject json = JSONObject.fromObject(
					AccountAgent.getInstance().getGCUserInfo(new AccessToken(oauth_token, oauth_token_secret)));
			if (json.has("BriefUser")) {
				this.account.setAccountId(json.getJSONObject("BriefUser").getString("id"));
				this.loginError = PbDown.LoginCheckRst.LoginError.SUCCESS;
				return;
			}
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog()
					.logError("Login error  accountType:" + this.account.getChannelType() + " Code: oauth error");
		} catch (NearMeException e) {
			e.printStackTrace();
			this.loginError = PbDown.LoginCheckRst.LoginError.BUSY;
			Platform.getLog()
					.logError("Login error  accountType:" + this.account.getChannelType() + " Code: oauth error");
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
