package org.darcy.sanguo.drop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.utils.ExcelUtils;

public class DropService implements Service {
	public static Map<Integer, DropGroup> dropGroups = new HashMap<Integer, DropGroup>();

	public void startup() {
		loadDrops();
	}

	public void shutdown() {
	}

	public DropGroup getDropGroup(int id) {
		if (id == -1) {
			return null;
		}

		return ((DropGroup) dropGroups.get(Integer.valueOf(id)));
	}

	private void loadDrops() {
		List<Row> list = ExcelUtils.getRowList("drop.xls", 2);
		for (Row row : list) {
			int pos = 0;
			int id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int mode = (int) row.getCell(pos++).getNumericCellValue();
			int count = (int) row.getCell(pos++).getNumericCellValue();
			DropGroup dg = new DropGroup(id, mode, "desc");
			for (int c = 0; c < count; ++c) {
				String[] dropstr = row.getCell(pos++).getStringCellValue().split("\\|");
				float rate = (float) row.getCell(pos++).getNumericCellValue();
				int droptype = Integer.parseInt(dropstr[0].trim());
				int dropitemid = Integer.parseInt(dropstr[1].trim());
				int dropamount = Integer.parseInt(dropstr[2].trim());
				if (droptype == 0) {
					ItemDrop drop = new ItemDrop(dropitemid, dropamount, rate);
					dg.addDrop(drop);
				} else if (droptype == 1) {
					SubDrop drop = new SubDrop(dropitemid, rate);
					dg.addDrop(drop);
				} else if (droptype == 2) {
					MoneyDrop drop = new MoneyDrop(dropamount, rate);
					dg.addDrop(drop);
				} else if (droptype == 4) {
					ExpDrop drop = new ExpDrop(dropamount, rate);
					dg.addDrop(drop);
				} else if (droptype == 3) {
					JewelDrop drop = new JewelDrop(dropamount, rate);
					dg.addDrop(drop);
				} else if (droptype == 6) {
					StaminaDrop drop = new StaminaDrop(dropamount, rate);
					dg.addDrop(drop);
				} else if (droptype == 5) {
					VitalityDrop drop = new VitalityDrop(dropamount, rate);
					dg.addDrop(drop);
				} else {
					if ((droptype != 8) && (droptype != 7) && (droptype != 9) && (droptype != 11) && (droptype != 10))
						continue;
					DigitalDrop drop = new DigitalDrop(droptype, dropamount, rate);
					dg.addDrop(drop);
				}
			}
			dropGroups.put(Integer.valueOf(id), dg);
		}
	}

	public void reload() throws Exception {
		dropGroups.clear();
		loadDrops();
	}
}
