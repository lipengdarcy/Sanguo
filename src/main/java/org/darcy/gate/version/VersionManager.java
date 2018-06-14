package org.darcy.gate.version;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.servlet.VersionList;
import org.darcy.sanguo.utils.ExcelUtils;

public class VersionManager {
	public static List<Version> versions = new CopyOnWriteArrayList<Version>();
	public static void reload() {
		List<Version> tmp = new CopyOnWriteArrayList<Version>();
		List<Row> list = ExcelUtils.getGateInfo("versions.xls");
		for (Row r : list) {

			int pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			int id = (int) r.getCell(pos++).getNumericCellValue();
			if (id == 0)
				continue;
			String version = r.getCell(pos++).getStringCellValue();
			String url = r.getCell(pos++).getStringCellValue();
			String md5 = r.getCell(pos++).getStringCellValue();
			int size = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			tmp.add(new Version(id, version, url, md5, size, type));
		}

		versions = tmp;
		VersionList.tmpVersionList.clear();
	}

	public static List<Version> getUps(String v) {
		List<Version> vs = new ArrayList<Version>();
		boolean hit = false;
		for (Version version : versions) {
			if ((hit) || (compare(v, version.getVerion()) < 0)) {
				hit = true;
				vs.add(version);
			}
		}

		return vs;
	}

	public static int compare(String v1, String v2) {
		int[] iv1 = split(v1, "\\.");
		int[] iv2 = split(v2, "\\.");
		for (int i = 0; i < iv1.length; ++i) {
			if (iv1[i] < iv2[i])
				return -1;
			if (iv1[i] > iv2[i]) {
				return 1;
			}
		}

		return 0;
	}

	public static int[] split(String s, String flag) {
		int[] rst = null;
		try {
			String[] ls = s.split(flag);
			rst = new int[ls.length];
			for (int i = 0; i < rst.length; ++i) {
				String l = ls[i];
				rst[i] = Integer.parseInt(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(s);
		}

		return rst;
	}

	public static String getLatestVersion() {
		if (versions.size() > 0) {
			return ((Version) versions.get(versions.size() - 1)).getVerion();
		}
		return "0.0.0";
	}
}
