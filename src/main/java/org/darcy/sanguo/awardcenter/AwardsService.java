package org.darcy.sanguo.awardcenter;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class AwardsService implements Service, PacketHandler {
	public void startup() throws Exception {
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1123, 1125, 1127, 1131 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 1123:
			awardsInfo(player);
			break;
		case 1125:
			accept(player, packet);
			break;
		case 1127:
			acceptAll(player);
			break;
		case 1131:
			awardsCount(player);
		case 1124:
		case 1126:
		case 1128:
		case 1129:
		case 1130:
		}
	}

	private void awardsInfo(Player player) {
		PbDown.AwardsInfoRst rst = PbDown.AwardsInfoRst.newBuilder().setResult(true)
				.setInfos(player.getAwards().genAwardsInfo()).build();
		player.send(1124, rst);
	}

	private void acceptAll(Player player) {
		PbDown.AwardsAccpetAllRst rst = PbDown.AwardsAccpetAllRst.newBuilder()
				.setResult(player.getAwards().acceptAll(player)).build();
		player.send(1128, rst);
	}

	private void accept(Player player, PbPacket.Packet packet) {
		int id;
		PbDown.AwardsAccpetRst.Builder builder = PbDown.AwardsAccpetRst.newBuilder();
		try {
			id = PbUp.AwardsAccpet.parseFrom(packet.getData()).getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("领奖失败");
			player.send(1126, builder.build());
			return;
		}
		boolean result = player.getAwards().accept(id, player);
		builder.setResult(result);
		if (!(result)) {
			builder.setErrInfo("领奖失败");
		}
		player.send(1126, builder.build());
	}

	private void awardsCount(Player player) {
		PbDown.AwardsCountRst rst = PbDown.AwardsCountRst.newBuilder().setResult(true)
				.setCount(player.getAwards().getAwardCount()).build();
		player.send(1132, rst);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
