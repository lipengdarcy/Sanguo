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

public class FriendGiveAsyncCall extends AsyncCall {
	private Player target;
	private Player player;
	private int playerId;
	private final int MAX_GIVEN = 60;

	public FriendGiveAsyncCall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		this.playerId = PbUp.FriendGive.parseFrom(packet.getData()).getPlayerId();
	}

	public FriendGiveAsyncCall(Player player, int playerId) {
		super(null, null);
		this.player = player;
		this.playerId = playerId;
	}

	public void callback() {
		PbDown.FriendGiveRst.Builder rst = PbDown.FriendGiveRst.newBuilder();
		if (this.target != null) {
			if (this.player.getRelations().getGiveRecords().contains(Integer.valueOf(this.playerId))) {
				rst.setResult(false);
				rst.setErrInfo("已经给该玩家送过了");
			} else if (!(this.player.getRelations().hasRealtion(0, this.playerId))) {
				rst.setResult(false);
				rst.setErrInfo("不是好友关系");
			} else {
				Relations r = this.target.getRelations();
				if (r.getGivenStaminas().size() < 60) {
					r.getGivenStaminas().put(Integer.valueOf(this.player.getId()),
							Long.valueOf(System.currentTimeMillis()));
				}
				rst.setResult(true);
				rst.setPlayerId(this.playerId);
				this.player.getRelations().getGiveRecords().add(Integer.valueOf(this.playerId));
				if (this.target.getSession() == null) {
					PlayerSaveCall call = new PlayerSaveCall(this.target);
					Platform.getThreadPool().execute(call);
				} else {
					Platform.getEventManager().addEvent(new Event(2041, new Object[] { this.target }));
				}
				Platform.getEventManager().addEvent(new Event(2026, new Object[] { this.player }));
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("查找玩家失败");
		}
		if (this.session != null)
			this.player.send(2072, rst.build());
	}

	public void netOrDB() {
		try {
			this.target = Platform.getPlayerManager().getPlayer(this.playerId, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
