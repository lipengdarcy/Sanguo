package org.darcy.sanguo.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.globaldrop.MapDropData;
import org.darcy.sanguo.globaldrop.OpenBoxDropData;
import org.darcy.sanguo.globaldrop.TurnCardDropData;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class GlobalDropService implements Service, PacketHandler {
	public void startup() throws Exception {
		loadGlobalDrop();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1121 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 1121:
			besureTurnCard(player);
		}
	}

	private void loadGlobalDrop() {
		int pos = 0;
		List<Row> list = ExcelUtils.getRowList("globaldrop.xls", 2);
		for (Row row : list) {

			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			++pos;
			TurnCardDropData.showDrop = (int) row.getCell(pos++).getNumericCellValue();
			TurnCardDropData.level = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			TurnCardDropData.normalDrop = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			int[] besureTimes = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			int[] besureDrop = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			for (int i = 0; i < besureTimes.length; ++i) {
				TurnCardDropData.besureDrop.put(Integer.valueOf(besureTimes[i]), Integer.valueOf(besureDrop[i]));
			}
			TurnCardDropData.lootMoneyPercent = (int) row.getCell(pos++).getNumericCellValue();
			TurnCardDropData.lootMoneyRatio = (int) row.getCell(pos++).getNumericCellValue();
			TurnCardDropData.lootMoneyMax = (int) row.getCell(pos++).getNumericCellValue();
		}

		List<Row> list2 = ExcelUtils.getRowList("globaldrop.xls", 2, 1);
		for (Row row : list2) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			++pos;
			MapDropData.level = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			MapDropData.normalDrop = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			int[] besureTimes = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			int[] besureDrop = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			for (int i = 0; i < besureTimes.length; ++i) {
				MapDropData.besureDrop.put(Integer.valueOf(besureTimes[i]), Integer.valueOf(besureDrop[i]));
			}
			MapDropData.ratio = (int) row.getCell(pos++).getNumericCellValue();
		}

		List<Row> list3 = ExcelUtils.getRowList("globaldrop.xls", 2, 2);
		for (Row row : list3) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			++pos;
			OpenBoxDropData.normalDrop = (int) row.getCell(pos++).getNumericCellValue();
			int[] besureTimes = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			int[] besureDrop = Calc.split(row.getCell(pos++).getStringCellValue(), ",");
			for (int i = 0; i < besureTimes.length; ++i) {
				OpenBoxDropData.besureDrop.put(Integer.valueOf(besureTimes[i]), Integer.valueOf(besureDrop[i]));
			}
			OpenBoxDropData.clearTime = besureTimes[(besureTimes.length - 1)];
		}
	}

	private void besureTurnCard(Player player) {
		PbDown.TurnCardRst rst = PbDown.TurnCardRst.newBuilder().setResult(true).build();
		player.send(1122, rst);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
