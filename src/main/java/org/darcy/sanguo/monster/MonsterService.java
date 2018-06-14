package org.darcy.sanguo.monster;

import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

public class MonsterService implements Service {
	public static HashMap<Integer, MonsterTemplate> monsterTemplates = new HashMap();

	public void startup() throws Exception {
		loadData();
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		monsterTemplates.clear();
		loadData();
	}

	private void loadData() {
		List<Row> list = ExcelUtils.getRowList("enemy.xls");
		for (Row r : list) {

			int pos = 0;
			if (r.getCell(pos) == null)
				return;
			int id = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			r.getCell(pos).setCellType(1);
			String desc = r.getCell(pos++).getStringCellValue();
			int gender = (int) r.getCell(pos++).getNumericCellValue();
			int nation = (int) r.getCell(pos++).getNumericCellValue();
			int isMonster = (int) r.getCell(pos++).getNumericCellValue();
			int classes = (int) r.getCell(pos++).getNumericCellValue();
			int iconId = (int) r.getCell(pos++).getNumericCellValue();
			int shapId = (int) r.getCell(pos++).getNumericCellValue();
			int ptSkillId = (int) r.getCell(pos++).getNumericCellValue();
			int angrySkillId = (int) r.getCell(pos++).getNumericCellValue();
			int fjSkillId = (int) r.getCell(pos++).getNumericCellValue();
			int mfSkillId = (int) r.getCell(pos++).getNumericCellValue();
			int hlSkillId = (int) r.getCell(pos++).getNumericCellValue();
			int angry = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			int level = (int) r.getCell(pos++).getNumericCellValue();
			int quality = (int) r.getCell(pos++).getNumericCellValue();
			int leadership = (int) r.getCell(pos++).getNumericCellValue();
			int force = (int) r.getCell(pos++).getNumericCellValue();
			int wisdom = (int) r.getCell(pos++).getNumericCellValue();
			int phAtk = (int) r.getCell(pos++).getNumericCellValue();
			int magicAtk = (int) r.getCell(pos++).getNumericCellValue();
			int atk = (int) r.getCell(pos++).getNumericCellValue();
			int hp = (int) r.getCell(pos++).getNumericCellValue();
			int phyDefens = (int) r.getCell(pos++).getNumericCellValue();
			int magicDefens = (int) r.getCell(pos++).getNumericCellValue();
			int criRate = (int) r.getCell(pos++).getNumericCellValue();
			int critTimes = (int) r.getCell(pos++).getNumericCellValue();
			int uncriRate = (int) r.getCell(pos++).getNumericCellValue();
			int dodgerRate = (int) r.getCell(pos++).getNumericCellValue();
			int hitRate = (int) r.getCell(pos++).getNumericCellValue();
			int blockRate = (int) r.getCell(pos++).getNumericCellValue();
			int unBlockRate = (int) r.getCell(pos++).getNumericCellValue();
			int finalDamage = (int) r.getCell(pos++).getNumericCellValue();
			int finalDamageDec = (int) r.getCell(pos++).getNumericCellValue();
			int cure = (int) r.getCell(pos++).getNumericCellValue();
			int cured = (int) r.getCell(pos++).getNumericCellValue();
			int poisonDamage = (int) r.getCell(pos++).getNumericCellValue();
			int poisonDamageDec = (int) r.getCell(pos++).getNumericCellValue();
			int fireDamage = (int) r.getCell(pos++).getNumericCellValue();
			int fireDamageDec = (int) r.getCell(pos++).getNumericCellValue();
			int phyDamageDec = (int) r.getCell(pos++).getNumericCellValue();
			int magDamageDec = (int) r.getCell(pos++).getNumericCellValue();
			int beCuredRate = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String bIds = r.getCell(pos++).getStringCellValue();

			MonsterTemplate mt = new MonsterTemplate();
			mt.id = id;
			mt.name = name;
			mt.level = level;
			mt.icon = iconId;
			mt.shapeId = shapId;
			mt.desc = desc;
			mt.gender = gender;
			mt.camp = nation;
			mt.isMonster = isMonster;
			mt.classes = classes;
			mt.quality = quality;
			mt.ptSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getSkill(ptSkillId);
			mt.angrySkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
					.getSkill(angrySkillId);
			mt.fjSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getSkill(fjSkillId);
			mt.mfSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getSkill(mfSkillId);
			mt.hlSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getSkill(hlSkillId);
			mt.actType = type;
			mt.attributes.set(4, phAtk);
			mt.attributes.set(5, magicAtk);
			mt.attributes.set(6, atk);
			mt.attributes.set(7, hp);
			mt.attributes.set(8, phyDefens);
			mt.attributes.set(9, magicDefens);
			mt.attributes.set(0, angry);
			mt.attributes.set(10, criRate);
			mt.attributes.set(11, critTimes);
			mt.attributes.set(12, uncriRate);
			mt.attributes.set(13, dodgerRate);
			mt.attributes.set(14, hitRate);
			mt.attributes.set(15, blockRate);
			mt.attributes.set(16, unBlockRate);
			mt.attributes.set(17, finalDamage);
			mt.attributes.set(18, finalDamageDec);
			mt.attributes.set(19, cure);
			mt.attributes.set(20, cured);
			mt.attributes.set(21, poisonDamage);
			mt.attributes.set(22, poisonDamageDec);
			mt.attributes.set(23, fireDamage);
			mt.attributes.set(24, fireDamageDec);
			mt.attributes.set(1, leadership);
			mt.attributes.set(2, force);
			mt.attributes.set(3, wisdom);
			mt.attributes.set(25, phyDamageDec);
			mt.attributes.set(26, magDamageDec);
			mt.attributes.set(27, beCuredRate);
			if (!(bIds.equals("-1"))) {
				mt.buffIds = Calc.split(bIds, ",");
			}
			monsterTemplates.put(Integer.valueOf(mt.id), mt);
		}
	}

}
