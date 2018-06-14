package org.darcy.sanguo.combat;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.combat.effect.Effect;
import org.darcy.sanguo.combat.skill.Behavior;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.utils.ExcelUtils;

public class CombatService implements Service {
	private HashMap<Integer, Effect> effects = new HashMap<Integer, Effect>();
	private HashMap<Integer, Buff> buffs = new HashMap<Integer, Buff>();
	private HashMap<Integer, Behavior> behaviors = new HashMap<Integer, Behavior>();
	private HashMap<Integer, Skill> skills = new HashMap<Integer, Skill>();

	public void loadEffects() {
		List<Row> list = ExcelUtils.getRowList("effect.xls");
		for (Row r : list) {
			int pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;

			int id = (int) r.getCell(pos++).getNumericCellValue();
			int catagory = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			String description = r.getCell(pos++).getStringCellValue();
			String className = r.getCell(pos++).getStringCellValue();
			if (className == null)
				continue;
			if (className.length() < 1) {
				continue;
			}
			int paramCount = (int) r.getCell(pos++).getNumericCellValue();
			try {
				Class ec = Class.forName("org.darcy.sanguo.combat.effect." + className);
				Constructor<?> constructor = ec.getConstructor(
						new Class[] { Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE, Integer.TYPE });
				Object obj = constructor.newInstance(new Object[] { Integer.valueOf(id), Integer.valueOf(type),
						description, Integer.valueOf(paramCount), Integer.valueOf(catagory) });
				this.effects.put(Integer.valueOf(id), (Effect) obj);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}

	public void loadBuffs() {
		List<Row> list = ExcelUtils.getRowList("buff.xls");
		for (Row r : list) {
			int pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;

			int id = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			String description = r.getCell(pos++).getStringCellValue();
			int iconId = (int) r.getCell(pos++).getNumericCellValue();
			int animation = (int) r.getCell(pos++).getNumericCellValue();
			int nature = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			int catagory = (int) r.getCell(pos++).getNumericCellValue();
			int uniqId = (int) r.getCell(pos++).getNumericCellValue();
			int uniqLevel = (int) r.getCell(pos++).getNumericCellValue();
			int effectPoint = (int) r.getCell(pos++).getNumericCellValue();
			int skillCondition = (int) r.getCell(pos++).getNumericCellValue();
			int cbtCondition = (int) r.getCell(pos++).getNumericCellValue();
			int maxEffectCount = (int) r.getCell(pos++).getNumericCellValue();
			int maxRound = (int) r.getCell(pos++).getNumericCellValue();
			int count = (int) r.getCell(pos++).getNumericCellValue();

			Buff b = new Buff(id, name, description);
			b.setIconId(iconId);
			b.setAnimation(animation);
			b.setNature(nature);
			b.setTriggerType(effectPoint);
			b.setMaxEffectTimes(maxEffectCount);
			b.setType(type);
			b.setCatagory(catagory);
			b.setUniqId(uniqId);
			b.setUniqLevel(uniqLevel);
			b.setSkillCondition(Skill.ConditionType.valueOf(skillCondition));
			b.setCombatCondition(cbtCondition);
			b.setMaxDurationRounds(maxRound);
			for (int i = 0; i < count; ++i) {
				int effectId = (int) r.getCell(pos++).getNumericCellValue();
				if (this.effects.get(Integer.valueOf(effectId)) == null) {
					System.out.println("buff: " + id + "  不存在的 effect ：　" + effectId);
				}
				Effect e = ((Effect) this.effects.get(Integer.valueOf(effectId))).copy();
				String[] param = new String[e.getParamCount()];
				for (int m = 0; m < param.length; ++m) {
					param[m] = Integer.toString((int)r.getCell(pos++).getNumericCellValue());
				}
				e.setParams(param);
				e.initParams();
				b.getEffects().add(e);
			}

			this.buffs.put(Integer.valueOf(id), b);
		}

	}

	public void loadBehaviors() {
		List<Row> list = ExcelUtils.getRowList("behavior.xls");
		for (Row r : list) {
			int pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;

			int id = (int) r.getCell(pos++).getNumericCellValue();
			String description = r.getCell(pos++).getStringCellValue();
			int atkTarget = (int) r.getCell(pos++).getNumericCellValue();
			int atkRange = (int) r.getCell(pos++).getNumericCellValue();
			int includeSelf = (int) r.getCell(pos++).getNumericCellValue();
			int bSureHit = (int) r.getCell(pos++).getNumericCellValue();
			int count = (int) r.getCell(pos++).getNumericCellValue();

			Behavior b = new Behavior();
			b.setId(id);
			b.setDescription(description);
			b.setAtkRange(atkRange);
			b.setAtkTarget(atkTarget);
			b.setIncludeSelf(includeSelf == 1);
			b.setbSureHit(bSureHit == 1);

			for (int i = 0; i < count; ++i) {
				int effectId = (int) r.getCell(pos++).getNumericCellValue();
				Effect e = ((Effect) this.effects.get(Integer.valueOf(effectId))).copy();
				String[] param = new String[e.getParamCount()];
				for (int m = 0; m < param.length; ++m) {
					param[m] = Integer.toString((int) r.getCell(pos++).getNumericCellValue());
				}
				e.setParams(param);
				e.initParams();
				b.getEffects().add(e);
			}
			this.behaviors.put(Integer.valueOf(id), b);
		}
	}

	public void loadSkills() {
		List<Row> list = ExcelUtils.getRowList("skill.xls");
		for (Row r : list) {
			int pos = 0;
			if (r == null) {
				return;
			}
			if (r.getCell(pos) == null) {
				return;
			}

			int id = (int) r.getCell(pos++).getNumericCellValue();
			int actionGroupId = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			String description = r.getCell(pos++).getStringCellValue();
			int iconId = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			int cost = (int) r.getCell(pos++).getNumericCellValue();
			//String behaviorList = Integer.toString((int)r.getCell(pos++).getNumericCellValue());
			String behaviorList = null;
			try {
				behaviorList = r.getCell(pos).getStringCellValue();
			}catch(Exception e) {
				behaviorList = Integer.toString((int)r.getCell(pos).getNumericCellValue());
			}
			pos++;
			String unBeat = null;
			try {
				unBeat = r.getCell(pos).getStringCellValue();
			}catch(Exception e) {
				unBeat = Integer.toString((int)r.getCell(pos).getNumericCellValue());
			}
			pos++;
			
			int bBeatBack = (int) r.getCell(pos++).getNumericCellValue();
			int bFollowAtk = (int) r.getCell(pos++).getNumericCellValue();

			Skill s = new Skill(type, id, name);
			s.setActionGroupId(actionGroupId);
			s.setDescription(description);
			s.setIconId(iconId);
			s.setKillPointCost(cost);
			s.setUnBeatActionID(unBeat);
			s.setBeatBack(bBeatBack == 1);
			s.setFollowAtk(bFollowAtk == 1);
			String[] ls = behaviorList.split(",");
			for (String b : ls) {
				Behavior bh = (Behavior) this.behaviors.get(Integer.valueOf(Integer.parseInt(b)));
				if (bh == null) {
					throw new RuntimeException("skill表配置错误~ behaviorId:" + b);
				}
				s.getBehaviors().add(bh);
			}
			this.skills.put(Integer.valueOf(id), s);
		}
	}

	public void startup() throws Exception {
		loadEffects();
		loadBuffs();
		loadBehaviors();
		loadSkills();
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		this.effects.clear();
		this.buffs.clear();
		this.behaviors.clear();
		this.skills.clear();

		loadEffects();
		loadBuffs();
		loadBehaviors();
		loadSkills();
	}

	public Buff getBuff(int buffId) {
		return ((Buff) this.buffs.get(Integer.valueOf(buffId)));
	}

	public Skill getSkill(int skillId) {
		return ((Skill) this.skills.get(Integer.valueOf(skillId)));
	}
}
