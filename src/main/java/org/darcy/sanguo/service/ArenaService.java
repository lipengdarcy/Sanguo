package org.darcy.sanguo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.arena.ArenaCompeteAsyncCall;
import org.darcy.sanguo.arena.ArenaData;
import org.darcy.sanguo.arena.ArenaInfoAsyncCall;
import org.darcy.sanguo.arena.RankInfoAsyncCall;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

/**
 * 竞技场
 */
public class ArenaService implements Service, PacketHandler, EventHandler {
	private Map<Integer, List<Reward>> arenaRewards = new HashMap<Integer, List<Reward>>();

	public void startup() throws Exception {
		loadArenaData();
		loadArenaRewards();
		loadArenaRivalRule();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		new Crontab("22 1 0", 2005);
	}

	public int[] getCodes() {
		return new int[] { 1093, 1095, 1097 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		if (!(FunctionService.isOpenFunction(player.getLevel(), 12))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1093:
			rankInfo(player);
			break;
		case 1095:
			arenaInfo(player);
			break;
		case 1097:
			compete(player, packet);
		case 1094:
		case 1096:
		}
	}

	public List<Reward> getRewards(int rank, long time) {
		List<Reward> list = this.arenaRewards.get(Integer.valueOf(rank));
		List<Reward> result = null;
		if (list != null) {
			result = new ArrayList<Reward>();
			boolean isActivity = ActivityInfo.isOpenActivity(5, time);
			for (Reward reward : list) {
				Reward newR = reward.copy();
				if ((newR.type == 10) && (isActivity)) {
					newR.count *= 2;
				}
				result.add(newR);
			}
		}
		return result;
	}

	private void loadArenaRewards() {
		List<Row> list = ExcelUtils.getRowList("arena.xls", 2);
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int rank = (int) row.getCell(pos++).getNumericCellValue();
			int coin = (int) row.getCell(pos++).getNumericCellValue();
			int prestige = (int) row.getCell(pos++).getNumericCellValue();

			List<Reward> list1 = new ArrayList<Reward>();
			list1.add(new Reward(3, coin, null));
			list1.add(new Reward(10, prestige, null));
			this.arenaRewards.put(Integer.valueOf(rank), list1);
		}
	}

	private void loadArenaData() {
		List<Row> list = ExcelUtils.getRowList("arena.xls", 2, 1);
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			++pos;
			ArenaData.challengeDayCount = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.winMoneyRatio = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.loseMoneyRatio = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.winExpRatio = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.loseExpRatio = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.costStamina = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.winPrestige = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.losePrestige = (int) row.getCell(pos++).getNumericCellValue();
		}
	}

	private void loadArenaRivalRule() {
		List<Row> list = ExcelUtils.getRowList("arena.xls", 2, 2);
		ArenaData.rankArray = new int[list.size()];
		ArenaData.intervalArray = new int[list.size()];
		int i = 0;
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int start = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int interval = (int) row.getCell(pos++).getNumericCellValue();
			ArenaData.rankArray[i] = start;
			ArenaData.intervalArray[i] = interval;
			i++;
		}

	}

	private void rankInfo(Player player) {
		RankInfoAsyncCall call = new RankInfoAsyncCall(player.getSession(), null);
		Platform.getThreadPool().execute(call);
	}

	private void arenaInfo(Player player) {
		int[] rivals = ArenaData.getRivals(player.getArena().getRank());
		ArenaInfoAsyncCall call = new ArenaInfoAsyncCall(player.getSession(), null, player, rivals);
		Platform.getThreadPool().execute(call);
	}

	private void compete(Player player, PbPacket.Packet packet) {
		PbDown.ArenaCompeteRst.Builder builder = PbDown.ArenaCompeteRst.newBuilder();
		builder.setResult(true);
		int id = 0;
		try {
			PbUp.ArenaCompete compete = PbUp.ArenaCompete.parseFrom(packet.getData());
			id = compete.getCompetitor();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("挑战失败");
			player.send(1098, builder.build());
			return;
		}
		if (player.getArena().getRestTimeToRwardTime() == 0) {
			builder.setResult(false);
			builder.setErrInfo("发奖时间，不能挑战");
		} else if (player.getStamina() < ArenaData.costStamina) {
			builder.setResult(false);
			builder.setErrInfo("精力不足");
		} else if (!(player.getArena().rivalIds.contains(Integer.valueOf(id)))) {
			builder.setResult(false);
			builder.setErrInfo("你不能挑战该玩家");
		} else {
			int count = player.getPool().getInt(3, ArenaData.challengeDayCount);
			if (count <= 0) {
				builder.setResult(false);
				builder.setErrInfo("今日次数已用完");
			} else {
				ArenaCompeteAsyncCall call = new ArenaCompeteAsyncCall(player.getSession(), null, player, id);
				Platform.getThreadPool().execute(call);
				return;
			}
		}
		player.send(1098, builder.build());
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	public int[] getEventCodes() {
		return new int[] { 2005 };
	}

	public void handleEvent(Event event) {
		if (event.type == 2005)
			for (Player p : Platform.getPlayerManager().players.values())
				if ((p != null) && (p.getArena() != null)) {
					p.getArena().addInfo(p.getArena().getRank(), false, true, null);
					p.getArena().reward(p);
				}
	}
}
