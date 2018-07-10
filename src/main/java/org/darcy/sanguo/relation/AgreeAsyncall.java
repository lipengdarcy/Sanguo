package org.darcy.sanguo.relation;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerSaveCall;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class AgreeAsyncall extends AsyncCall {
	private Player player;
	private Player target;
	private int targetId;

	public AgreeAsyncall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.FriendDisagree dis = PbUp.FriendDisagree.parseFrom(packet.getData());
		this.targetId = dis.getPlayerId();
	}

	public void callback() {
		PbDown.FriendAgreeRst.Builder rst = PbDown.FriendAgreeRst.newBuilder();
		rst.setPlayerId(this.targetId);

		if (this.target != null) {
			if (!(this.player.getRelations().hasRealtion(1, this.targetId))) {
				rst.setResult(false);
				rst.setErrInfo("不存在的好友申请");
			} else if (this.player.getRelations().isFull(0)) {
				rst.setResult(false);
				rst.setErrInfo("好友已满");
			} else if (this.target.getRelations().isFull(0)) {
				rst.setResult(false);
				rst.setErrInfo("对方好友已满");
			} else {
				Relations r = this.target.getRelations();
				r.getRelation(0).addPlayer(this.player);
				this.player.getRelations().getRelation(0).addPlayer(this.target);
				this.player.getRelations().getRelation(1).removePlayer(this.targetId);
				r.getRelation(1).removePlayer(this.player.getId());
				rst.setResult(true);
				if (this.target.getSession() == null) {
					PlayerSaveCall call = new PlayerSaveCall(this.target);
					Platform.getThreadPool().execute(call);
				}
				Platform.getEventManager().addEvent(new Event(2051, new Object[] { this.player }));
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("查找玩家失败");
		}
		this.player.send(2088, rst.build());
	}

	public void netOrDB() {
		try {
			this.target = Platform.getPlayerManager().getPlayer(this.targetId, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
