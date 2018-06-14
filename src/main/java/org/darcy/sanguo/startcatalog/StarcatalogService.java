package org.darcy.sanguo.startcatalog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class StarcatalogService implements Service, PacketHandler {
	public static Map<Integer, Starcatalog> catalogs = new HashMap();

	public static Map<Integer, Integer[]> costGroups = new HashMap();

	public static Map<Integer, Attri[]> attrgroups = new HashMap();

	public static Map<Integer, StarcatalogEvent> events = new HashMap();
	public static final int FAVOR_COST = 11011;
	public static final int MAX_FAVOR = 100;

	public int[] getCodes() {
		return new int[] { 3013, 3015, 3019, 3017 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 3013:
			starcatalogInfo(player);
			break;
		case 3015:
			achieveInfo(player);
			break;
		case 3019:
			receiveAchieve(player, packet);
			break;
		case 3017:
			increaseFavor(player, packet);
		case 3014:
		case 3016:
		case 3018:
		}
	}

	private void increaseFavor(Player p, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(p.getLevel(), 69))) {
			return;
		}

		PbDown.IncreasefavorRst.Builder builder = PbDown.IncreasefavorRst.newBuilder().setResult(true);
		try {
			int wid = PbUp.Increasefavor.parseFrom(packet.getData()).getHeroid();
			StarcatalogRecord r = p.getStarcatalogRecord();
			WarriorInfo w = r.getWarrior(wid);

			if (w == null)
				return;

			Starcatalog catalog = (Starcatalog) catalogs.get(Integer.valueOf(w.id));
			if ((catalog == null) || ((w.favor >= ((Attri[]) attrgroups.get(Integer.valueOf(catalog.attrid))).length)
					&& (w.favor >= 100))) {
				builder.setResult(false);
				builder.setErrInfo("好感度の上限に達しました。");
				// break label615:
			}

			Integer cost = ((Integer[]) costGroups
					.get(Integer.valueOf(((Starcatalog) catalogs.get(Integer.valueOf(wid))).itemCostid)))[w.favor];
			ItemTemplate template = ItemService.getItemTemplate(11011);

			if (p.getBags().getItemCount(11011) < cost.intValue()) {
				cost = ((Integer[]) costGroups
						.get(Integer.valueOf(((Starcatalog) catalogs.get(Integer.valueOf(wid))).costid)))[w.favor];

				if (cost.intValue() > p.getJewels()) {
					builder.setResult(false);
					builder.setErrInfo("元宝不足");
					// break label615:
				}
				p.decJewels(cost.intValue(), "starcatalog");
			} else {
				new Reward(0, cost.intValue(), template).remove(p, "starcatalog");
			}

			w.favor += 1;

			r.check(p);
			Starcatalog s = (Starcatalog) catalogs.get(Integer.valueOf(w.id));
			builder.setFavorAmount(r.favorAmount()).setAttrs(r.attrAmount().genAttribute()).setHerofavor(w.favor);
			if (w.favor > 0) {
				builder.setCur(r.attrAdd(s.attrid, w.favor - 1).genAttribute());
			}

			if ((w.favor < ((Attri[]) attrgroups
					.get(Integer.valueOf(((Starcatalog) catalogs.get(Integer.valueOf(w.id))).attrid))).length)
					&& (w.favor < 100)) {
				builder.setNext(r.attrAdd(s.attrid, w.favor).genAttribute());
				builder.setCost(((Integer[]) costGroups.get(Integer.valueOf(s.costid)))[w.favor].intValue());
				builder.setItemCost(((Integer[]) costGroups.get(Integer.valueOf(s.itemCostid)))[w.favor].intValue());
			}

			for (Warrior wr : p.getWarriors().getWarriors().values())
				wr.refreshAttributes(true);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
		}

		// label615: p.send(3018, builder.build());
	}

	private void receiveAchieve(Player p, PbPacket.Packet packet) {
		PbDown.StarcatalogAchieveReceiveRst.Builder builder = PbDown.StarcatalogAchieveReceiveRst.newBuilder()
				.setResult(true);
		try {
			int aid = PbUp.StarcatalogAchieveReceive.parseFrom(packet.getData()).getAchieveid();

			Map eventIds = p.getStarcatalogRecord().getEventids();

			if ((!(eventIds.containsKey(Integer.valueOf(aid))))
					|| (((Boolean) eventIds.get(Integer.valueOf(aid))).booleanValue())
					|| (events.get(Integer.valueOf(aid)) == null)) {
				return;
			}
			List<Reward> rewards = ((StarcatalogEvent) events.get(Integer.valueOf(aid))).getRewards();
			if ((rewards != null) && (rewards.size() > 0)) {
				for (Reward r : rewards)
					r.add(p, "starcatalogachieve");
				p.notifyGetItem(2, rewards);
			}
			eventIds.put(Integer.valueOf(aid), Boolean.valueOf(true));

			for (Warrior wr : p.getWarriors().getWarriors().values()) {
				wr.refreshAttributes(true);
			}

			if (!(eventIds.containsValue(Boolean.valueOf(false))))
				Function.notifyMainNum(p, 45, -1);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
		}

		p.send(3020, builder.build());
	}

	private void achieveInfo(Player p) {
		StarcatalogRecord r = p.getStarcatalogRecord();
		PbDown.StarCatalogAchieveRst.Builder builder = PbDown.StarCatalogAchieveRst.newBuilder().setResult(true);
		Map ids = r.getEventids();
		for (Iterator localIterator1 = events.values().iterator(); localIterator1.hasNext();) {
			Attri a;
			StarcatalogEvent e = (StarcatalogEvent) localIterator1.next();
			if ((e.getLast() != null) && (!(ids.keySet().containsAll(e.getLast()))))
				continue;
			PbCommons.StarCatalogAchieve.Builder b = PbCommons.StarCatalogAchieve.newBuilder().setId(e.getId())
					.setType(e.getType()).setCamp(-1);

			if ((e.getAttrs() != null) && (e.getAttrs().size() > 0)) {
				Attributes as = new Attributes();
				for (Iterator localIterator2 = e.getAttrs().iterator(); localIterator2.hasNext();) {
					a = (Attri) localIterator2.next();
					as.addAttri(a);
				}
				b.setAttrs(as.genAttribute());
			}

			if ((e.getRewards() != null) && (e.getRewards().size() > 0)) {
				for (Reward reward : e.getRewards()) {
					b.addRewards(reward.genPbReward());
				}
			}

			Boolean bl = (Boolean) ids.get(Integer.valueOf(e.getId()));
			if (bl != null)
				b.setStatus((bl.booleanValue()) ? 1 : 0);
			else {
				b.setStatus(2);
			}

			switch (e.getType()) {
			case 1:
				b.setContent(
						MessageFormat.format(e.getContent(), new Object[] { Integer.valueOf(r.getInfos().size()) }));
				break;
			case 2:
				String heros = (String) e.getParams().get(0);
				for (String heroid : heros.split(",")) {
					b.addScws(PbCommons.StarCatalogWarrior.newBuilder().setHeroid(Integer.parseInt(heroid))
							.setHas(r.getInfos().containsKey(Integer.valueOf(Integer.parseInt(heroid)))).build());
				}
				break;
			case 3:
				int j;
				int camp = Integer.parseInt((String) e.getParams().get(0));
				b.setCamp(camp);
				j = 0;
				for (Iterator<WarriorInfo> it = r.getInfos().values().iterator(); it.hasNext();) {
					WarriorInfo wi = it.next();
					if (wi.camp == camp)
						++j;
				}
				b.setContent(MessageFormat.format(e.getContent(), new Object[] { Integer.valueOf(j) }));
				break;
			case 4:
				b.setContent(MessageFormat.format(e.getContent(), new Object[] { Integer.valueOf(r.favorAmount()) }));
				break;
			case 5:
				String ws = (String) e.getParams().get(0);
				int favor = Integer.parseInt((String) e.getParams().get(1));
				for (String w : ws.split(",")) {
					int wid = Integer.parseInt(w);
					PbCommons.StarCatalogWarrior.Builder br = PbCommons.StarCatalogWarrior.newBuilder().setHeroid(wid)
							.setHas(r.getInfos().containsKey(Integer.valueOf(wid)));
					if (r.getInfos().get(Integer.valueOf(wid)) != null) {
						br.setFavor(((WarriorInfo) r.getInfos().get(Integer.valueOf(wid))).favor);
					}
					b.addScws(br.build());
				}
				b.setAppointFavor(favor);
				break;
			case 6:
				int cp = Integer.parseInt((String) e.getParams().get(0));
				b.setCamp(cp);
				int i1 = 0;
				for (Iterator<WarriorInfo> it = r.getInfos().values().iterator(); it.hasNext();) {
					WarriorInfo wi = it.next();
					if (wi.camp == cp)
						i1 += wi.favor;
				}
				b.setContent(MessageFormat.format(e.getContent(), new Object[] { Integer.valueOf(i1) }));
			}

			builder.addAchieves(b.build());
		}

		p.send(3016, builder.build());
	}

	private void starcatalogInfo(Player p) {
		if (!(FunctionService.isOpenFunction(p.getLevel(), 68))) {
			return;
		}

		StarcatalogRecord r = p.getStarcatalogRecord();
		PbDown.StarCatalogInfoRst builder = PbDown.StarCatalogInfoRst.newBuilder().setResult(true)
				.setFavorAmount(r.favorAmount()).setAttrs(r.attrAmount().genAttribute()).setAmount(catalogs.size())
				.setCur(r.curWarriors()).addAllInfos(r.genAllStarcatalog()).setItemid(11011).build();

		p.send(3014, builder);
	}

	public void startup() throws Exception {
		loadStarcatalog();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	private void loadStarcatalog() {

		int pos;

		Integer id;
		int i, j;

		List<Row> list = ExcelUtils.getRowList("startcatalog.xls", 2);
		for (Row row : list) {

			pos = 0;

			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			Starcatalog scl = new Starcatalog();
			scl.heroid = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			scl.costid = (int) row.getCell(pos++).getNumericCellValue();
			scl.itemCostid = (int) row.getCell(pos++).getNumericCellValue();
			scl.attrid = (int) row.getCell(pos++).getNumericCellValue();
			catalogs.put(Integer.valueOf(scl.heroid), scl);
		}

		List<Row> list2 = ExcelUtils.getRowList("startcatalog.xls", 2, 1);
		for (Row row : list2) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			StarcatalogEvent se = new StarcatalogEvent();
			id = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String rewards = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String attrs = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String last = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String next = row.getCell(pos++).getStringCellValue();
			int type = (int) row.getCell(pos++).getNumericCellValue();
			String content = null;
			Cell cell = row.getCell(pos);
			if (cell != null) {
				row.getCell(pos).setCellType(1);
				content = row.getCell(pos++).getStringCellValue();
			} else {
				++pos;
			}
			++pos;
			int size = (int) row.getCell(pos++).getNumericCellValue();
			List params = new ArrayList(size);
			for (j = 0; j < size; ++j) {
				row.getCell(pos).setCellType(1);
				params.add(row.getCell(pos++).getStringCellValue());
			}

			se.setId(id);
			se.setType(type);
			if ((rewards != null) && (!(rewards.equals("-1")))) {
				List rs = new ArrayList();
				for (String r : rewards.split(",")) {
					rs.add(new Reward(r));
				}
				se.setRewards(rs);
			}
			if ((attrs != null) && (!(attrs.equals("-1")))) {
				List as = new ArrayList();
				for (String a : attrs.split(",")) {
					as.add(new Attri(a));
				}
				se.setAttrs(as);
			}
			if ((last != null) && (!(last.endsWith("-1")))) {
				String[] lstrs = last.split(",");
				List ls = new ArrayList(lstrs.length);
				for (j = 0; j < lstrs.length; ++j) {
					ls.add(Integer.valueOf(Integer.parseInt(lstrs[j])));
				}
				se.setLast(ls);
			}
			if ((next != null) && (!(next.endsWith("-1")))) {
				String[] nstrs = next.split(",");
				List ns = new ArrayList(nstrs.length);
				for (j = 0; j < nstrs.length; ++j) {
					ns.add(Integer.valueOf(Integer.parseInt(nstrs[j])));
				}
				se.setNext(ns);
			}
			se.setParams(params);
			se.setContent(content);
			events.put(Integer.valueOf(id), se);
		}

		List<Row> list3 = ExcelUtils.getRowList("startcatalog.xls", 2, 2);
		for (Row row : list3) {
			pos = 0;
			if (row == null) {
				break;
			}
			if (row.getCell(pos) == null) {
				break;
			}
			id = Integer.valueOf((int) row.getCell(pos++).getNumericCellValue());
			int len = row.getPhysicalNumberOfCells() - 1;
			Integer[] costBylevels = new Integer[(len < 100) ? len : 100];
			for (j = 0; j < costBylevels.length; ++j) {
				costBylevels[j] = Integer.valueOf((int) row.getCell(pos++).getNumericCellValue());
			}
			costGroups.put(id, costBylevels);
		}

		List<Row> list4 = ExcelUtils.getRowList("startcatalog.xls", 2, 3);
		for (Row row : list4) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			id = Integer.valueOf((int) row.getCell(pos++).getNumericCellValue());
			int max = (int) row.getCell(pos++).getNumericCellValue();
			Attri[] attris = new Attri[max];
			for (j = 0; j < attris.length; ++j) {
				attris[j] = new Attri(row.getCell(pos++).getStringCellValue());
			}
			attrgroups.put(id, attris);
		}
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
