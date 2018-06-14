package org.darcy.gate.notice;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.utils.ExcelUtils;
import net.sf.json.JSONArray;

/**
 * 服务公告
 */
public class NoticeManager {
	public static List<Notice> notices = new CopyOnWriteArrayList<Notice>();

	public static String noticeJson = "";

	public static void reload() {
		List<Row> list = ExcelUtils.getGateInfo("notice.xls");
		List<Notice> tmp = new CopyOnWriteArrayList<Notice>();
		for (Row r : list) {
			int pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			int id = (int) r.getCell(pos++).getNumericCellValue();
			String title = r.getCell(pos++).getStringCellValue();
			String content = r.getCell(pos++).getStringCellValue();
			tmp.add(new Notice(id, title, content));
		}

		notices = tmp;
		JSONArray jsonArray = JSONArray.fromObject(notices);
		noticeJson = jsonArray.toString();
	}

}
