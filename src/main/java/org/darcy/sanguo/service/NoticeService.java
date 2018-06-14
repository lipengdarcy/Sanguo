package org.darcy.sanguo.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.notice.Notice;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class NoticeService implements Service, PacketHandler {
	public static Map<Integer, Notice> notices = new HashMap<Integer, Notice>();

	private Comparator<Notice> comparator = new Comparator<Notice>() {
		public int compare(Notice o1, Notice o2) {
			return (o2.weight - o1.weight);
		}
	};

	public void startup() throws Exception {
		loadNotices();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1251 };
	}

	private void loadNotices() {
		int pos = 0;
		List<Row> list1 = ExcelUtils.getRowList("notice.xls", 2, 1);
		for (Row row : list1) {

			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String title = row.getCell(pos++).getStringCellValue();
			String content = row.getCell(pos++).getStringCellValue();
			String startStr = row.getCell(pos++).getStringCellValue();
			String endStr = row.getCell(pos++).getStringCellValue();

			Notice n = new Notice();
			n.id = id;
			n.title = title;
			n.content = content;
			try {
				n.start = Notice.sdf.parse(startStr);
				n.end = Notice.sdf.parse(endStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			n.weight = id;

			notices.put(Integer.valueOf(id), n);
		}
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 1251:
			noticeInfo(player);
		}
	}

	private void noticeInfo(Player player) {
		Date now = new Date();
		PbDown.NoticeInfoRst.Builder b = PbDown.NoticeInfoRst.newBuilder().setResult(true);
		List<Notice> list = new ArrayList<Notice>(notices.values());
		Collections.sort(list, this.comparator);
		for (Notice n : list) {
			if ((now.after(n.start)) && (now.before(n.end))) {
				b.addNotices(n.genNotice());
			}
		}
		player.send(1252, b.build());
	}

	public static void updateNotice(Notice notice) {
		if (notice.id <= 0) {
			int id = notices.size();
			while (notices.containsKey(Integer.valueOf(id))) {
				++id;
			}
			notice.id = id;
			notices.put(Integer.valueOf(id), notice);
		} else {
			Notice old = (Notice) notices.get(Integer.valueOf(notice.id));
			if (old != null) {
				old.title = notice.title;
				old.content = notice.content;
				old.start = new Date(notice.start.getTime());
				old.end = new Date(notice.end.getTime());
				old.weight = notice.weight;
			}
		}
	}

	public static void removeNotice(Notice notice) {
		notices.remove(Integer.valueOf(notice.id));
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
