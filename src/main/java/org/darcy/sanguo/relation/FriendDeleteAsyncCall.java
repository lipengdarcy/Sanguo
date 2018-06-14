package org.darcy.sanguo.relation;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerSaveCall;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class FriendDeleteAsyncCall extends AsyncCall {
	Player player;
	Player target;
	int targetId;

	public FriendDeleteAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
		this.player = session.getPlayer();
		try {
			PbUp.FriendGive give = PbUp.FriendGive.parseFrom(packet.getData());
			this.targetId = give.getPlayerId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}

	public void callback() {
		PbDown.FriendDeleteRst.Builder rst = PbDown.FriendDeleteRst.newBuilder();
		rst.setPlayerId(this.targetId);

		if (this.target != null) {
			if (!(this.player.getRelations().hasRealtion(0, this.targetId))) {
				rst.setResult(false);
				rst.setErrInfo("不是好友关系");
			} else {
				Relations r = this.target.getRelations();
				r.getRelation(0).removePlayer(this.player.getId());
				this.player.getRelations().getRelation(0).removePlayer(this.targetId);
				rst.setResult(true);
				if (this.target.getSession() == null) {
					PlayerSaveCall call = new PlayerSaveCall(this.target);
					Platform.getThreadPool().execute(call);
				}
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("查找玩家失败");
		}
		this.player.send(2074, rst.build());
	}

	public void netOrDB() {
		try {
			this.target = Platform.getPlayerManager().getPlayer(this.targetId, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
