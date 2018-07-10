package org.darcy.sanguo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.destiny.BreakTemplate;
import org.darcy.sanguo.destiny.DestinyRecord;
import org.darcy.sanguo.destiny.DestinyTemplate;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.HeroTemplate;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class DestinyService implements Service, PacketHandler {
	public static HashMap<Integer, DestinyTemplate> destinies = new HashMap<Integer, DestinyTemplate>();
	public static HashMap<Integer, BreakTemplate> breaks = new HashMap<Integer, BreakTemplate>();

	public void loadData() {
		List<Row> list = ExcelUtils.getRowList("destiny.xls", 2);
		int i = 0;
		for (Row r : list) {
			if (i == 0)
				continue;
			i++;
			int pos;
			int id;
			int monyCost;
			int isBreak;
			DestinyTemplate d;

			pos = 0;
			if ((r == null) || (r.getCell(pos) == null))
				return;

			id = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			String atts = r.getCell(pos++).getStringCellValue();
			int starCost = (int) r.getCell(pos++).getNumericCellValue();
			int preId = (int) r.getCell(pos++).getNumericCellValue();
			int nextId = (int) r.getCell(pos++).getNumericCellValue();
			int color = (int) r.getCell(pos++).getNumericCellValue();
			monyCost = (int) r.getCell(pos++).getNumericCellValue();
			isBreak = (int) r.getCell(pos++).getNumericCellValue();

			d = new DestinyTemplate();
			d.id = id;
			d.name = name;
			if (!(atts.equals("-1"))) {
				String[] ls = atts.split(",");
				d.attris = new ArrayList<Attri>();
				for (String l : ls) {
					Attri a = new Attri(l);
					d.attris.add(a);
				}
			}
			d.starCost = starCost;
			d.preId = preId;
			d.nextId = nextId;
			d.color = color;
			d.moneyCost = monyCost;
			d.isBreak = (isBreak == 1);
			destinies.put(Integer.valueOf(id), d);
		}

		List<Row> list2 = ExcelUtils.getRowList("destiny.xls", 2, 1);

		for (Row r : list2) {
			int pos = 0;
			if ((r == null) || (r.getCell(pos) == null))
				return;
			int id = (int) r.getCell(pos++).getNumericCellValue();
			String description = r.getCell(pos++).getStringCellValue();
			String attris = null;
			try {
				attris = r.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				attris = Integer.toString((int) r.getCell(pos).getNumericCellValue());
			}
			pos++;

			String heroIds = null;
			try {
				heroIds = r.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				heroIds = Integer.toString((int) r.getCell(pos).getNumericCellValue());
			}
			pos++;
			
			BreakTemplate bt = new BreakTemplate();
			bt.id = id;
			bt.description = description;
			if (!(attris.equals("-1"))) {
				String[] ls = attris.split(",");
				bt.attris = new ArrayList<Attri>();
				for (String l : ls) {
					Attri a = new Attri(l);
					bt.attris.add(a);
				}
			}
			if (!(heroIds.equals("-1"))) {
				bt.heroIds = Calc.split(heroIds, ",");
			}
			breaks.put(Integer.valueOf(id), bt);
		}
	}

	public int[] getCodes() {
		return new int[] { 2095, 2097 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 17))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 2095:
			infos(player);
			break;
		case 2097:
			upgrade(player);
		case 2096:
		}
	}

	private void upgrade(Player player) {
		PbDown.DestinyUpgradeRst.Builder rst = PbDown.DestinyUpgradeRst.newBuilder();
		rst.setResult(false);
		DestinyRecord r = player.getDestinyRecord();
		DestinyTemplate dt = r.getNextDestiny();
		if (dt != null)
			if (player.getMoney() < dt.moneyCost) {
				rst.setErrInfo("银币不足");
			} else if (r.getLeftStars() < dt.starCost) {
				rst.setErrInfo("剩余星数不足，请通过更多普通关卡来获得星数");
			} else {
				rst.setResult(true);
				player.decMoney(dt.moneyCost, "destiny");
				r.decStars(dt.starCost);
				for (Attri a : dt.attris) {
					r.getAttris().addAttri(a);
				}
				r.setCurrDestinyId(r.getCurrDestinyId() + 1);
				if (dt.isBreak) {
					BreakTemplate bt = r.getNextBreak();
					r.setCurrBreakId(r.getCurrBreakId() + 1);
					if (bt.attris != null) {
						for (Attri a : bt.attris) {
							r.getAttris().addAttri(a);
						}
					}
					if (bt.heroIds != null) {
						Platform.getServiceManager().get(ItemService.class);
						int heroId = (player.getGender() == 1) ? bt.heroIds[0] : bt.heroIds[1];
						HeroTemplate ht = (HeroTemplate) ItemService.getItemTemplate(heroId);
						if (ht != null) {
							player.getWarriors().getMainWarrior().setTemplate(ht);
						}
					}
				}
				player.getWarriors().getMainWarrior().refreshAttributes(true);
				Platform.getEventManager().addEvent(new Event(2047, new Object[] { player }));
				Platform.getLog().logDestiny(player, dt.starCost);
			}
		else {
			rst.setErrInfo("已经达到最天命等级");
		}
		player.send(2098, rst.build());
	}

	private void infos(Player player) {
		PbDown.DestinyInfoRst.Builder rst = PbDown.DestinyInfoRst.newBuilder();
		DestinyRecord r = player.getDestinyRecord();
		DestinyTemplate dt = r.getNextDestiny();
		BreakTemplate bt = r.getNextBreak();

		rst.setResult(true).setCurrAttri(r.getNextAttri().genAttribute()).setDestinyId(r.getCurrDestinyId())
				.setLeftStars(r.getLeftStars()).setNeedMoney((dt == null) ? -1 : dt.moneyCost)
				.setNeedStars((dt == null) ? -1 : dt.starCost).setNextBreakDesc((bt == null) ? "" : bt.description)
				.setTotalAttri(r.getAttris().genAttribute());

		player.send(2096, rst.build());
	}

	public void startup() throws Exception {
		loadData();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		destinies.clear();
		breaks.clear();
		loadData();
	}
}
