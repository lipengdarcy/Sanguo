package org.darcy.sanguo.relation;

import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.DBUtil;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class FriendFindAsyncCall extends AsyncCall {
	private Player player;
	private String name;
	private MiniPlayer mini;
	private boolean badName;
	private Player target;

	public FriendFindAsyncCall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.FriendSearch s = PbUp.FriendSearch.parseFrom(packet.getData());
		this.name = s.getName();
	}

	public void callback() {
		PbDown.FriendFindRst.Builder rst = PbDown.FriendFindRst.newBuilder();
		if ((!(this.badName)) && (this.target != null)) {
			this.target.init();
			rst.setResult(true);
			rst.setUnit(this.target.getMiniPlayer().genMiniUser());
		} else {
			rst.setResult(false);
			rst.setErrInfo("查找的玩家不存在");
		}
		this.player.send(2092, rst.build());
	}

	public void netOrDB() {
		if (this.name == null) {
			this.badName = true;
			return;
		}
		this.name = this.name.trim();
		if ((this.name.contains("*")) || (this.name.contains("%")) || (this.name.length() < 1)) {
			this.badName = true;
			return;
		}
		this.target = DBUtil.getPlayerByName(this.name);
	}
}
