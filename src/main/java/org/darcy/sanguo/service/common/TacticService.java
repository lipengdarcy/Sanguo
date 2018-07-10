package org.darcy.sanguo.service.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.tactic.Tactic;
import org.darcy.sanguo.tactic.TacticRecord;
import org.darcy.sanguo.tactic.TacticTemplate;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class TacticService implements Service, PacketHandler {
	public static Map<Integer, Map<Integer, TacticTemplate>> tactics = new HashMap();

	public void startup() throws Exception {
		loadTactics();
	}

	public int[] getCodes() {
		return new int[] { 1157, 1159, 1161 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		if (!(FunctionService.isOpenFunction(player.getLevel(), 3))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1157:
			tacticInfo(player);
			break;
		case 1159:
			tacticComprehend(player);
			break;
		case 1161:
			tacticReset(player);
		case 1158:
		case 1160:
		}
	}

	private void loadTactics() {
		List<Row> list = ExcelUtils.getRowList("tactics.xls");
		for (Row row : list) {

			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			TacticTemplate t = new TacticTemplate();
			t.id = (int) row.getCell(pos++).getNumericCellValue();
			t.name = row.getCell(pos++).getStringCellValue();
			int type = (int) row.getCell(pos++).getNumericCellValue();
			t.type = type;
			t.buffs = new int[6][];
			for (int j = 0; j < 6; ++j) {
				String str = row.getCell(pos++).getStringCellValue();
				t.buffs[j] = Calc.split(str, ",");
			}
			Map map = (Map) tactics.get(Integer.valueOf(type));
			if (map == null) {
				map = new HashMap();
				tactics.put(Integer.valueOf(type), map);
			}
			map.put(Integer.valueOf(t.id), t);
		}
	}

	private void tacticInfo(Player player) {
		PbDown.TacticInfoRst.Builder builder = PbDown.TacticInfoRst.newBuilder();
		builder.setResult(true);
		TacticRecord tr = player.getTacticRecord();
		builder.setSelect(tr.getSelect());
		builder.setSurplusPoint(tr.getSurplusPoint());
		for (Tactic t : tr.getTactics().values()) {
			builder.addTactic(t.genTactic());
		}
		player.send(1158, builder.build());
	}

	private void tacticComprehend(Player player) {
		PbDown.TacticComprehendRst.Builder builder = PbDown.TacticComprehendRst.newBuilder().setResult(true);
		TacticRecord tr = player.getTacticRecord();
		if (tr.getSurplusPoint() < 1) {
			builder.setResult(false);
			builder.setErrInfo("阵法点不足，无法领悟");
		} else if (!(tr.canComprehend())) {
			builder.setResult(false);
			builder.setErrInfo("阵法已全部领悟且达到最高级");
		} else {
			Tactic t = tr.comprehend(player);
			builder.setTactic(t.genTactic());
			builder.setSurplusPoint(tr.getSurplusPoint());
			Platform.getLog().logTactic(player, "tacticcomprehend");
		}
		player.send(1160, builder.build());
	}

	private void tacticReset(Player player) {
		PbDown.TacticResetRst.Builder builder = PbDown.TacticResetRst.newBuilder().setResult(true);
		TacticRecord tr = player.getTacticRecord();
		if (tr.getTacticCount() < 1) {
			builder.setResult(false);
			builder.setErrInfo("尚未领悟阵法，无需重置");
		} else if (player.getJewels() < 50) {
			builder.setResult(false);
			builder.setErrInfo("元宝不足");
		} else {
			tr.reset(player);
			player.decJewels(50, "tacticreset");
			builder.setSurplusPoint(tr.getSurplusPoint());
			Platform.getLog().logTactic(player, "tacticreset");
		}
		player.send(1162, builder.build());
	}

	public static Map<Integer, TacticTemplate> getTemplates(int type) {
		return ((Map) tactics.get(Integer.valueOf(type)));
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
