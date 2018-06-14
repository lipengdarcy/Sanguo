package org.darcy.sanguo.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.glory.Glory;
import org.darcy.sanguo.glory.GloryRecord;
import org.darcy.sanguo.glory.GloryTemplate;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class GloryService implements Service, PacketHandler {
	public static Map<Integer, GloryTemplate> glories = new HashMap();

	public static Map<Integer, Integer> gloryOpen = new HashMap();

	public void startup() throws Exception {
		loadGlory();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1291, 1293, 1295, 1297 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 45))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1291:
			gloryInfo(player);
			break;
		case 1293:
			gloryUse(player, packet);
			break;
		case 1295:
			gloryReset(player, packet);
			break;
		case 1297:
			gloryLevelUp(player, packet);
		case 1292:
		case 1294:
		case 1296:
		}
	}

	private void loadGlory() {
		try {
			int pos;
			Row row;
			File file = new File(Configuration.resourcedir, "commons/glory.xlsx");
			HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = book.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 3; i < rows; ++i) {
				pos = 0;
				row = sheet.getRow(i);
				if (row == null) {
					break;
				}
				if (row.getCell(pos) == null) {
					break;
				}
				GloryTemplate t = new GloryTemplate();
				t.id = (int) row.getCell(pos++).getNumericCellValue();
				row.getCell(pos).setCellType(1);
				t.name = row.getCell(pos++).getStringCellValue();
				t.type = (int) row.getCell(pos++).getNumericCellValue();
				++pos;
				String str = row.getCell(pos++).getStringCellValue();
				t.frontBuffs = Calc.split(str, ",");
				str = row.getCell(pos++).getStringCellValue();
				t.behindBuffs = Calc.split(str, ",");

				glories.put(Integer.valueOf(t.id), t);
			}

			sheet = book.getSheetAt(1);
			rows = sheet.getPhysicalNumberOfRows();
			for (int i = 3; i < rows; ++i) {
				pos = 0;
				row = sheet.getRow(i);
				if (row == null) {
					return;
				}
				if (row.getCell(pos) == null) {
					return;
				}
				int type = (int) row.getCell(pos++).getNumericCellValue();
				int id = (int) row.getCell(pos++).getNumericCellValue();
				gloryOpen.put(Integer.valueOf(type), Integer.valueOf(id));
			}
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}

	private void gloryInfo(Player player) {
		PbDown.GloryInfoRst.Builder b = PbDown.GloryInfoRst.newBuilder().setResult(true);
		GloryRecord gr = player.getGloryRecord();
		Map<Integer, List> map = new HashMap();
		for (Glory g : gr.getGlories().values()) {
			List list = (List) map.get(Integer.valueOf(g.getType()));
			if (list == null) {
				list = new ArrayList();
				map.put(Integer.valueOf(g.getType()), list);
			}
			list.add(g.genGlory());
		}
		for (Integer type : map.keySet()) {
			PbCommons.GloryGroup.Builder ggb = PbCommons.GloryGroup.newBuilder();
			ggb.setType(type.intValue());
			List list = (List) map.get(type);
			ggb.addAllG(list);
			if (gr.getUsed().containsKey(type))
				ggb.setUsed(((Integer) gr.getUsed().get(type)).intValue());
			else {
				ggb.setUsed(-1);
			}
			b.addGroups(ggb.build());
		}
		player.send(1292, b.build());
	}

	private void gloryUse(Player player, PbPacket.Packet packet) {
		PbDown.GloryUseRst.Builder b = PbDown.GloryUseRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.GloryUse req = PbUp.GloryUse.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			Platform.getLog().logError(e);
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1294, b.build());
			return;
		}

		GloryRecord gr = player.getGloryRecord();
		if (gr.getGlory(id) == null) {
			b.setResult(false);
			b.setErrInfo("您尚未获得光环");
		} else if (gr.isUsed(id)) {
			b.setResult(false);
			b.setErrInfo("该光环已被使用");
		} else {
			gr.use(id, player);
			Platform.getEventManager().addEvent(new Event(2084, new Object[] { player }));
		}

		player.send(1294, b.build());
	}

	private void gloryReset(Player player, PbPacket.Packet packet) {
		PbDown.GloryResetRst.Builder b = PbDown.GloryResetRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.GloryReset req = PbUp.GloryReset.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			Platform.getLog().logError(e);
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1296, b.build());
			return;
		}

		GloryRecord gr = player.getGloryRecord();
		Glory g = gr.getGlory(id);
		if (g == null) {
			b.setResult(false);
			b.setErrInfo("您尚未获得光环");
		} else if (g.getLevel() < 2) {
			b.setResult(false);
			b.setErrInfo("该光环无需重置");
		} else {
			int cost = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(202, g.getLevel());
			if (player.getJewels() < cost) {
				b.setResult(false);
				b.setErrInfo("元宝不足");
			} else {
				int exp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).calTotalExpByLevel(201,
						g.getLevel());
				ItemTemplate it = ItemService.getItemTemplate(11007);
				Item item = ItemService.generateItem(it, player);
				player.getBags().addItem(item, exp, "gloryreset");
				player.decJewels(cost, "gloryreset");
				gr.reset(player, id);
			}
		}

		player.send(1296, b.build());
	}

	private void gloryLevelUp(Player player, PbPacket.Packet packet) {
		PbDown.GloryLevelUpRst.Builder b = PbDown.GloryLevelUpRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.GloryLevelUp req = PbUp.GloryLevelUp.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			Platform.getLog().logError(e);
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1298, b.build());
			return;
		}

		GloryRecord gr = player.getGloryRecord();
		Glory g = gr.getGlory(id);
		if (g == null) {
			b.setResult(false);
			b.setErrInfo("您尚未获得光环");
		} else if (g.getLevel() >= 10) {
			b.setResult(false);
			b.setErrInfo("该光环已达最大等级");
		} else {
			int needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(201,
					g.getLevel() + 1);
			if (needExp > 0) {
				int count = player.getBags().getItemCount(11007);
				if (count < needExp) {
					b.setResult(false);
					b.setErrInfo("物品不足");
				} else {
					player.getBags().removeItem(0, 11007, needExp, "glorylevelup");
					gr.levelUp(player, id);
				}
			}
		}

		player.send(1298, b.build());
	}

	public static GloryTemplate getTemplate(int id) {
		return ((GloryTemplate) glories.get(Integer.valueOf(id)));
	}

	public static int getOpenLevel(int type) {
		return ((Integer) gloryOpen.get(Integer.valueOf(type))).intValue();
	}

	public static boolean isOpen(int type, int level) {
		return (level < getOpenLevel(type));
	}

	public static int getOpenTypeByTowerId(int id) {
		if (gloryOpen.containsValue(Integer.valueOf(id))) {
			Set<Entry<Integer, Integer>> set = gloryOpen.entrySet();
			for (Map.Entry entry : set) {
				int type = ((Integer) entry.getKey()).intValue();
				int openId = ((Integer) entry.getValue()).intValue();
				if (id == openId) {
					return type;
				}
			}
		}
		return -1;
	}

	public static List<GloryTemplate> getTemplatesByType(int type) {
		List list = new ArrayList();
		for (GloryTemplate t : glories.values()) {
			if (t.type == type) {
				list.add(t);
			}
		}
		return list;
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
