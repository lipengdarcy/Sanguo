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

public class FriendApplyAsyncCall extends AsyncCall {
	private Player player;
	private Player target;
	private int targetId;

	public FriendApplyAsyncCall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.FriendApply apply = PbUp.FriendApply.parseFrom(packet.getData());
		this.targetId = apply.getPlayerId();
	}

	public void callback() {
		PbDown.FriendApplyRst.Builder rst = PbDown.FriendApplyRst.newBuilder();
		rst.setPlayerId(this.targetId);

		if (this.target != null) {
			if (this.targetId == this.player.getId()) {
				rst.setResult(false);
				rst.setErrInfo("您不能添加自己为好友");
			} else if (this.player.getRelations().hasRealtion(0, this.targetId)) {
				rst.setResult(false);
				rst.setErrInfo("对方已经是你的好友");
			} else if (this.target.getRelations().hasRealtion(1, this.player.getId())) {
				rst.setResult(false);
				rst.setErrInfo("申请已提交");
			} else if (this.player.getRelations().isFull(0)) {
				rst.setResult(false);
				rst.setErrInfo("自身好友人数达到上限，无法申请");
			} else if (this.target.getRelations().isFull(1)) {
				rst.setResult(false);
				rst.setErrInfo("对方好友申请人数达到上限，无法申请");
			} else {
				Relations r = this.target.getRelations();
				r.getRelation(1).addPlayer(this.player);
				rst.setResult(true);
				if (this.target.getSession() == null) {
					PlayerSaveCall call = new PlayerSaveCall(this.target);
					Platform.getThreadPool().execute(call);
				} else {
					Platform.getEventManager().addEvent(new Event(2040, new Object[] { this.target }));
				}
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("查找玩家失败");
		}
		this.player.send(2094, rst.build());
	}

	public void netOrDB() {
		try {
			this.target = Platform.getPlayerManager().getPlayer(this.targetId, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
