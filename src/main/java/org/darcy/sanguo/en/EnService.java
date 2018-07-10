package org.darcy.sanguo.en;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class EnService implements Service, PacketHandler {
	public static HashMap<Integer, En> enTemplates = new HashMap<Integer, En>();
	public static final int UPDATE_PRICE = 50;
	public static final int UPDATE_MIN_LEVEL = 30;

	private void loadData() {
		List<Row> list = ExcelUtils.getRowList("relationship.xls");
		for (Row r : list) {
			int pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;
			int id = (int) r.getCell(pos++).getNumericCellValue();
			String itemList = null;
			try {
				itemList = r.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				itemList = Integer.toString((int) r.getCell(pos).getNumericCellValue());
			}
			pos++;
			++pos;
			String attriIndexs = null;
			try {
				attriIndexs = r.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				attriIndexs = Integer.toString((int) r.getCell(pos).getNumericCellValue());
			}
			pos++;
			
			String attriValues = null;
			try {
				attriValues = r.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				attriValues = Integer.toString((int) r.getCell(pos).getNumericCellValue());
			}
			pos++;
			
			String name = r.getCell(pos++).getStringCellValue();
			String desc = r.getCell(pos++).getStringCellValue();
			int groupId = (int) r.getCell(pos++).getNumericCellValue();
			int order = (int) r.getCell(pos++).getNumericCellValue();

			En et = new En();
			et.id = id;
			et.itemList = Calc.split(itemList, ",");
			et.attriIndexs = Calc.split(attriIndexs, ",");
			et.attriValues = Calc.split(attriValues, ",");
			et.name = name;
			et.description = desc;
			et.groupId = groupId;
			et.order = order;
			enTemplates.put(Integer.valueOf(id), et);
		}
	}

	public void startup() throws Exception {
		loadData();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		enTemplates.clear();
		loadData();
	}

	public int[] getCodes() {
		return new int[] { 2149, 2147, 2151 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 2149:
			PbUp.EnUpdateChange ch = PbUp.EnUpdateChange.parseFrom(packet.getData());
			change(player, ch.getWarriorId());
			break;
		case 2147:
			PbUp.EnUpdateInfo info = PbUp.EnUpdateInfo.parseFrom(packet.getData());
			info(player, info.getWarriorId());
			break;
		case 2151:
			PbUp.EnUpdateUpdate up = PbUp.EnUpdateUpdate.parseFrom(packet.getData());
			update(player, up.getWarriorId());
		case 2148:
		case 2150:
		}
	}

	private void change(Player player, int wid) {
		PbDown.EnUpdateChangeRst.Builder rst = PbDown.EnUpdateChangeRst.newBuilder();
		rst.setWarriorId(wid).setResult(false);
		Item item = player.getBags().getItemById(wid);
		if ((item != null) && (item instanceof Warrior)) {
			Warrior w = (Warrior) item;
			if (!(w.isEnsUpdated())) {
				rst.setErrInfo("请先升级机缘");
			} else {
				rst.setResult(true);
				w.setEnsPlanB(!(w.isEnsPlanB()));
				w.refreshkEns(player.getWarriors().getAllWarriorAndFellow());
				w.refreshAttributes(true);
			}
		} else {
			rst.setErrInfo("不存在该武将");
		}
		player.send(2150, rst.build());
	}

	private void update(Player player, int wid) {
		PbDown.EnUpdateUpdateRst.Builder rst = PbDown.EnUpdateUpdateRst.newBuilder();
		rst.setWarriorId(wid).setResult(false);
		Item item = player.getBags().getItemById(wid);
		if ((item != null) && (item instanceof Warrior)) {
			Warrior w = (Warrior) item;
			if (!(w.isEnsUpdated()))
				if (player.getJewels() < 50) {
					rst.setErrInfo("元宝不足");
				} else if (w.getLevel() < 30) {
					rst.setErrInfo(MessageFormat.format("升级机缘需要武将升级到{0}级", new Object[] { Integer.valueOf(30) }));
				} else {
					rst.setResult(true);
					player.decJewels(50, "enuplevel");
					w.setEnsUpdated(true);
					w.setEnsPlanB(true);
					w.refreshkEns(player.getWarriors().getAllWarriorAndFellow());
					w.refreshAttributes(true);
				}
			else
				rst.setErrInfo("该武将机缘已经升级");
		} else {
			rst.setErrInfo("不存在该武将");
		}
		player.send(2152, rst.build());
	}

	private void info(Player player, int wid) {
		PbDown.EnUpdateInfoRst.Builder rst = PbDown.EnUpdateInfoRst.newBuilder();
		rst.setWarriorId(wid).setResult(false);
		Item item = player.getBags().getItemById(wid);
		if (item != null)
			rst.setResult(true).setPrice(50);
		else {
			rst.setErrInfo("不存在该武将");
		}
		player.send(2148, rst.build());
	}
}
