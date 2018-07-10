package org.darcy.sanguo.relation;

import java.util.Calendar;
import java.util.List;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbRelation;
import sango.packet.PbUp;

public class RelationService implements Service, PacketHandler {
	public static final int PAGE_SIZE = 4;

	public void startup() {
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public int[] getCodes() {
		return new int[] { 2087, 2093, 2085, 2083, 2075, 2073, 2091, 2069, 2079, 2081, 2077, 2071, 2089 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		AsyncCall call;
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 2087:
			call = new AgreeAsyncall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2093:
			call = new FriendApplyAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2085:
			PbUp.FriendDisagree dis = PbUp.FriendDisagree.parseFrom(packet.getData());
			disagree(player, dis.getPlayerId());
			break;
		case 2083:
			applyInfos(player);
			break;
		case 2075:
			call = new FriendChallengeAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2073:
			call = new FriendDeleteAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2091:
			call = new FriendFindAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2069:
			friends(player);
			break;
		case 2079:
			PbUp.FriendGetStamina get = PbUp.FriendGetStamina.parseFrom(packet.getData());
			getStamina(player, get.getPlayerId());
			break;
		case 2081:
			getStaminaAll(player);
			break;
		case 2077:
			staminaInfos(player);
			break;
		case 2071:
			call = new FriendGiveAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2089:
			PbUp.FriendRecommond rcm = PbUp.FriendRecommond.parseFrom(packet.getData());
			recommonedPlayers(player, rcm.getPage());
		case 2070:
		case 2072:
		case 2074:
		case 2076:
		case 2078:
		case 2080:
		case 2082:
		case 2084:
		case 2086:
		case 2088:
		case 2090:
		case 2092:
		}
	}

	private void recommonedPlayers(Player player, int page) {
		if (page <= 0)
			page = 1;
		if (page == 1) {
			player.getRelations().refreshRecommendPlayers();
		}
		PbDown.FriendRecommondRst.Builder rst = PbDown.FriendRecommondRst.newBuilder();
		List ids = player.getRelations().getRecommendPlayers();
		int start = (page - 1) * 4;
		int end = start + 4;
		if (ids.size() < start) {
			rst.setResult(false);
			rst.setErrInfo("对方已经是你的好友");
		} else {
			rst.setResult(true);
			rst.setMaxPage((ids.size() - 1) / 4 + 1);
			rst.setCurrentPage(page);
			for (int i = start; (i < ids.size()) && (i < end); ++i) {
				rst.addUnits(
						Platform.getPlayerManager().getMiniPlayer(((Integer) ids.get(i)).intValue()).genMiniUser());
			}
		}
		player.send(2090, rst.build());
	}

	private void applyInfos(Player player) {
		PbDown.FriendApplyInfosRst.Builder rst = PbDown.FriendApplyInfosRst.newBuilder();
		rst.setResult(true);
		Relation r = player.getRelations().getRelation(1);
		for (MiniPlayer m : r.getMiniPlayers()) {
			rst.addUnits(PbRelation.FriendTimeUnit.newBuilder().setMiniUser(m.genMiniUser())
					.setTime(getTimeStr(r.getTime(m.getId()))));
		}

		player.send(2084, rst.build());
	}

	private void disagree(Player player, int tid) {
		PbDown.FriendDisagreeRst.Builder rst = PbDown.FriendDisagreeRst.newBuilder();
		player.getRelations().getRelation(1).removePlayer(tid);
		rst.setResult(true).setPlayerId(tid);
		Platform.getEventManager().addEvent(new Event(2052, new Object[] { player }));
		player.send(2086, rst.build());
	}

	private void friends(Player player) {
		PbDown.FriendMyFriendsRst.Builder rst = PbDown.FriendMyFriendsRst.newBuilder();
		Relation relation = player.getRelations().getRelation(0);
		rst.setResult(true);
		rst.setMaxCount(player.getRelations().getMaxCount(0));
		for (MiniPlayer m : relation.getMiniPlayers()) {
			rst.addUnits(PbRelation.FriendUnit.newBuilder().setMiniUser(m.genMiniUser()).setIsOnline(m.isOnline())
					.setIsGiven(player.getRelations().getGiveRecords().contains(Integer.valueOf(m.getId()))));
		}

		player.send(2070, rst.build());
	}

	private void staminaInfos(Player player) {
		PbDown.FriendGetStaminaInfosRst.Builder rst = PbDown.FriendGetStaminaInfosRst.newBuilder();
		rst.setResult(true);
		Relations rs = player.getRelations();
		for (GivenStamina gs : rs.getOrderedGivenStaminas()) {
			rst.addUnits(gs.genPb());
		}
		rst.setLeftTimes(rs.getLeftStaminaTimes());
		player.send(2078, rst.build());
	}

	private void getStamina(Player player, int tid) {
		PbDown.FriendGetStaminaRst.Builder rst = PbDown.FriendGetStaminaRst.newBuilder();
		Relations rs = player.getRelations();
		if (player.getStamina() >= player.getStaminaLimit()) {
			rst.setResult(false);
			rst.setPlayerId(tid);
			rst.setErrInfo("精力已满");
		}
		if (rs.getLeftStaminaTimes() < 1) {
			rst.setResult(false);
			rst.setPlayerId(tid);
			rst.setErrInfo("达到今日领取次数上限，请明日再来");
		} else if (rs.getGivenStaminas().containsKey(Integer.valueOf(tid))) {
			rst.setPlayerId(tid);
			rst.setResult(true);
			rs.getGivenStaminas().remove(Integer.valueOf(tid));
			player.addStamina(1, "friendgive");
			Platform.getEventManager().addEvent(new Event(2053, new Object[] { player }));
			rs.setGetStaminaTimes(rs.getStaminaTimes + 1);
			AsyncCall call = new FriendGiveAsyncCall(player, tid);
			Platform.getThreadPool().execute(call);
		} else {
			rst.setResult(false);
			rst.setPlayerId(tid);
			rst.setErrInfo("没有该玩家赠送的精力");
		}

		player.send(2080, rst.build());
	}

	private void getStaminaAll(Player player) {
		Relations rs = player.getRelations();
		PbDown.FriendGetAllStaminaRst.Builder rst = PbDown.FriendGetAllStaminaRst.newBuilder();
		rst.setResult(true);
		for (GivenStamina gs : rs.getDeOrderedGivenStaminas()) {
			if (player.getStamina() >= player.getStaminaLimit()) {
				break;
			}
			if (rs.getLeftStaminaTimes() < 1) {
				break;
			}
			player.addStamina(1, "friendgive");
			rs.getGivenStaminas().remove(Integer.valueOf(gs.playerId));
			rst.addPlayerIds(gs.playerId);
			rs.setGetStaminaTimes(rs.getStaminaTimes + 1);
			AsyncCall call = new FriendGiveAsyncCall(player, gs.playerId);
			Platform.getThreadPool().execute(call);
		}
		Platform.getEventManager().addEvent(new Event(2053, new Object[] { player }));
		player.send(2082, rst.build());
	}

	public static String getTimeStr(long time) {
		int today = Calendar.getInstance().get(6);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		int day = cal.get(6);
		int dif = today - day;
		if (dif < 0) {
			dif += 365;
		}
		if (dif == 0) {
			return "今天";
		}
		return String.valueOf(dif) + "天前";
	}

	public void reload() throws Exception {
	}
}
