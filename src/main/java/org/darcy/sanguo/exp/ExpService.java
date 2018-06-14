package org.darcy.sanguo.exp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.utils.ExcelUtils;

public class ExpService implements Service {
	public static final int MAX_LEVEL = 200;
	public Map<Integer, Integer[]> exps = new HashMap<Integer, Integer[]>();

	private void loadExp() {
		List<Row> list = ExcelUtils.getRowList("exp.xls");
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			Integer[] exp = new Integer[200];
			for (int j = 0; j < exp.length; ++j) {
				exp[j] = Integer.valueOf((int) row.getCell(pos++).getNumericCellValue());
			}
			this.exps.put(Integer.valueOf(id), exp);
		}
	}

	public int getExp(int expId, int level) {
		if ((level < 0) || (level > 200)) {
			return -1;
		}
		Integer[] exp = getExps(expId);
		if ((exp == null) || (exp.length <= 0)) {
			return -1;
		}
		if (level == 0) {
			return 0;
		}
		return exp[(level - 1)].intValue();
	}

	public int calLevelByExp(int expRuleId, int exp) {
		int level = 0;
		Integer[] exps = getExps(expRuleId);
		if (exps == null) {
			return 0;
		}
		int tmpExp = 0;
		boolean isFullLevel = true;
		for (int i = 0; i < exps.length; ++i) {
			tmpExp += exps[i].intValue();
			if (tmpExp > exp) {
				level = i;
				isFullLevel = false;
				break;
			}
		}
		if (level < 1) {
			level = 1;
		}
		if (isFullLevel) {
			level = exps.length;
		}
		return level;
	}

	public int calTotalExpByLevel(int expRuleId, int level) {
		Integer[] exps = getExps(expRuleId);
		if (exps == null) {
			return 0;
		}
		if ((level < 1) || (level > exps.length)) {
			return 0;
		}
		int tmpExp = 0;
		for (int i = 0; i < level; ++i) {
			tmpExp += exps[i].intValue();
		}
		return tmpExp;
	}

	public int getRestExpToNextLevel(int expRuleId, int exp, int nextLevel) {
		int needExp = getExp(expRuleId, nextLevel);
		int restExp = 0;
		if (exp < needExp) {
			restExp = needExp - exp;
		}
		return restExp;
	}

	public Integer[] getExps(int expId) {
		return ((Integer[]) this.exps.get(Integer.valueOf(expId)));
	}

	public void startup() throws Exception {
		loadExp();
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
