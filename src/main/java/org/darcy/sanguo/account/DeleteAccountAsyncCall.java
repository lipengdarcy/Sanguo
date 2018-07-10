package org.darcy.sanguo.account;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.player.Player;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class DeleteAccountAsyncCall extends AsyncCall {
	int resultNum = 0;

	public DeleteAccountAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
	}

	public void callback() {
		PbDown.DeleteAccountRst.Builder builder = PbDown.DeleteAccountRst.newBuilder();
		if (this.resultNum == 0) {
			builder.setResult(true);
		} else {
			builder.setResult(false);
			if (this.resultNum == 1)
				builder.setErrInfo("删除账号失败");
			else if (this.resultNum == 2) {
				builder.setErrInfo("账号不存在");
			}
		}
		PbDown.DeleteAccountRst rst = builder.build();
		this.session.send(1006, rst);
	}

	public void netOrDB() {
		PbUp.DeleteAccount deleteAccount = null;
		try {
			deleteAccount = PbUp.DeleteAccount.parseFrom(this.packet.getData());
		} catch (InvalidProtocolBufferException e) {
			this.resultNum = 1;
			e.printStackTrace();
			return;
		}
		String accountId = deleteAccount.getUuid().trim();

		Player player = this.session.getPlayer();
		if (player == null) {
			this.resultNum = 2;
			return;
		}

		player.setAccountId(accountId + System.currentTimeMillis());

		player.save();
	}
}
