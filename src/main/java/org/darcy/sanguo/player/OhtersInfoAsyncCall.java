package org.darcy.sanguo.player;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class OhtersInfoAsyncCall extends AsyncCall {
	Player others = null;

	int errorNum = 0;

	public OhtersInfoAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
	}

	public void callback() {
		PbDown.OthersInfoRst.Builder builder = PbDown.OthersInfoRst.newBuilder();
		if (this.errorNum == 0) {
			builder.setResult(true);
			builder.setOthers(this.others.genOthersData());
		} else {
			builder.setResult(false);
			if (this.errorNum == 1)
				builder.setErrInfo("该玩家不存在");
			else if (this.errorNum == 2) {
				builder.setErrInfo("查看玩家信息失败");
			}
		}
		this.session.send(1130, builder.build());
	}

	public void netOrDB() {
		try {
			int id = PbUp.OthersInfo.parseFrom(this.packet.getData()).getId();

			Player player = Platform.getPlayerManager().getPlayer(id, true, true);
			if (player == null) {
				this.errorNum = 1;
				return;
			}

			this.others = player;
		} catch (Exception e) {
			e.printStackTrace();
			this.errorNum = 2;
		}
	}
}
