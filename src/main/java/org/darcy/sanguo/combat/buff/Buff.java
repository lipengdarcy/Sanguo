package org.darcy.sanguo.combat.buff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Round;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.effect.Effect;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class Buff {
	public static final int TRIGGER_POINT_ROUND_START = 0;
	public static final int TRIGGER_POINT_ROUND_END = 1;
	public static final int TRIGGER_POINT_ACTION_START = 2;
	public static final int TRIGGER_POINT_ACTION_END = 3;
	public static final int TRIGGER_POINT_ATTACK_START = 6;
	public static final int TRIGGER_POINT_ATTACK_END = 7;
	public static final int TRIGGER_POINT_BEATTACK_START = 11;
	public static final int TRIGGER_POINT_BEATTACK_END = 12;
	public static final int TRIGGER_POINT_SECTION_START = 15;
	public static final int TRIGGER_POINT_SECTION_END = 16;
	public static final int TRIGGER_POINT_DAMAGEAPPPLY_START = 8;
	public static final int TRIGGER_POINT_BEDAMAGEAPPPLY_START = 13;
	public static final int TRIGGER_POINT_DAMAGEAPPPLY_END = 9;
	public static final int TRIGGER_POINT_BEDAMAGEAPPPLY_END = 14;
	public static final int TRIGGER_POINT_SKILL_START = 4;
	public static final int TRIGGER_POINT_SKILL_END = 5;
	public static final int TRIGGER_POINT_BESKILL_END = 10;
	public static final int TRIGGER_POINT_ADDED = 17;
	private Unit caster;
	private Unit owner;
	private int id;
	private int triggerType;
	private String name;
	private String description;
	private int iconId;
	private int durationRounds;
	private int maxDurationRounds;
	private int effectTimes;
	private int maxEffectTimes;
	private int lastUpdateRound;
	private int animation;
	private int nature;
	private int type;
	private Skill.ConditionType skillCondition;
	private int combatCondition;
	private int catagory;
	private int uniqId;
	private int uniqLevel;
	List<Effect> effects = new ArrayList();

	public Buff(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public int getDurationRounds() {
		return this.durationRounds;
	}

	public void setDurationRounds(int durationRounds) {
		this.durationRounds = durationRounds;
	}

	public Unit getCaster() {
		return this.caster;
	}

	public void setCaster(Unit caster) {
		this.caster = caster;
	}

	public Unit getOwner() {
		return this.owner;
	}

	public void setOwner(Unit owner) {
		this.owner = owner;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTriggerType() {
		return this.triggerType;
	}

	public int getAnimation() {
		return this.animation;
	}

	public void setAnimation(int animation) {
		this.animation = animation;
	}

	public int getNature() {
		return this.nature;
	}

	public void setNature(int nature) {
		this.nature = nature;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	public int getCatagory() {
		return this.catagory;
	}

	public void setCatagory(int catagory) {
		this.catagory = catagory;
	}

	public int getEffectTimes() {
		return this.effectTimes;
	}

	public int getMaxEffectTimes() {
		return this.maxEffectTimes;
	}

	public void setEffectTimes(int effectTimes) {
		this.effectTimes = effectTimes;
	}

	public void setMaxEffectTimes(int maxEffectTimes) {
		this.maxEffectTimes = maxEffectTimes;
	}

	public int getUniqId() {
		return this.uniqId;
	}

	public int getUniqLevel() {
		return this.uniqLevel;
	}

	public void setUniqId(int uniqId) {
		this.uniqId = uniqId;
	}

	public void setUniqLevel(int uniqLevel) {
		this.uniqLevel = uniqLevel;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIconId() {
		return this.iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public int getMaxDurationRounds() {
		return this.maxDurationRounds;
	}

	public void setMaxDurationRounds(int maxDurationRounds) {
		this.maxDurationRounds = maxDurationRounds;
	}

	public List<Effect> getEffects() {
		return this.effects;
	}

	public void setEffects(List<Effect> effects) {
		this.effects = effects;
	}

	public int getLastUpdateRound() {
		return this.lastUpdateRound;
	}

	public void setLastUpdateRound(int lastUpdateRound) {
		this.lastUpdateRound = lastUpdateRound;
	}

	public Buff copy() {
		Buff buff = new Buff(this.id, this.name, this.description);
		buff.triggerType = this.triggerType;
		buff.iconId = this.iconId;
		buff.durationRounds = this.durationRounds;
		buff.maxDurationRounds = this.maxDurationRounds;
		buff.animation = this.animation;
		buff.type = this.type;
		buff.catagory = this.catagory;
		buff.combatCondition = this.combatCondition;
		buff.effectTimes = this.effectTimes;
		buff.lastUpdateRound = this.lastUpdateRound;
		buff.maxEffectTimes = this.maxEffectTimes;
		buff.nature = this.nature;
		buff.skillCondition = this.skillCondition;
		buff.uniqId = this.uniqId;
		buff.uniqLevel = this.uniqLevel;
		for (Effect ef : this.effects) {
			buff.effects.add(ef.copy());
		}

		return buff;
	}

	public boolean effect(int triggerPoint, Section section) {
		if (this.triggerType == triggerPoint) {
			Platform.getLog().logCombat(this.owner.getName() + "  buff effect: ." + getName());
			Iterator localIterator = this.effects.iterator();
			while (true) {
				Effect e = (Effect) localIterator.next();
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (this.owner.getStates().canEffect(e.getCatagory())) {
					int[] rst = e.effectBuff(this.owner, this.caster, section);
					recordEffect(e, section, rst);
					if (rst[0] == 1990) {
						Platform.getLog().logCombat(
								this.owner.getName() + "  buff effect: ." + getName() + " Rst:" + Arrays.toString(rst));
					}
				}
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (!(localIterator.hasNext())) {
					return checkOverDueEffect();
				}
			}
		}
		return false;
	}

	public boolean effect(int triggerPoint, CombatContent combatContent) {
		if ((this.triggerType == triggerPoint)
				&& (combatContent.getCbSkill().isCondition(this.owner, this.combatCondition))
				&& (combatContent.getCbSkill().getSkill().isConditionType(this.skillCondition))) {
			Platform.getLog().logCombat(this.owner.getName() + "  buff effect: ." + getName());
			Iterator localIterator = this.effects.iterator();
			while (true) {
				Effect e = (Effect) localIterator.next();
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (this.owner.getStates().canEffect(e.getCatagory())) {
					int[] rst = e.effectBuff(this.owner, this.caster, combatContent);
					recordEffect(e, combatContent.getCbSkill().getAction().getSection(), rst);
					if (rst[0] == 1990) {
						Platform.getLog().logCombat(
								this.owner.getName() + "  buff effect: ." + getName() + " Rst:" + Arrays.toString(rst));
					}
				}
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (!(localIterator.hasNext())) {
					return checkOverDueEffect();
				}
			}
		}
		return false;
	}

	public boolean effect(int triggerPoint, Action action) {
		if (this.triggerType == triggerPoint) {
			Platform.getLog().logCombat(this.owner.getName() + "  buff effect: ." + getName());
			Iterator localIterator = this.effects.iterator();
			while (true) {
				Effect e = (Effect) localIterator.next();
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (this.owner.getStates().canEffect(e.getCatagory())) {
					int[] rst = e.effectBuff(this.owner, this.caster, action);
					recordEffect(e, action.getSection(), rst);
					if (rst[0] == 1990) {
						Platform.getLog().logCombat(
								this.owner.getName() + "  buff effect: ." + getName() + " Rst:" + Arrays.toString(rst));
					}
				}
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (!(localIterator.hasNext())) {
					label200: return checkOverDueEffect();
				}
			}
		}
		return false;
	}

	public boolean effect(int triggerPoint, CbSkill cbSkill) {
		if ((this.triggerType == triggerPoint) && (cbSkill.isCondition(this.owner, this.combatCondition))
				&& (cbSkill.getSkill().isConditionType(this.skillCondition))) {
			Platform.getLog().logCombat(this.owner.getName() + "  buff effect: ." + getName());
			Iterator localIterator = this.effects.iterator();

			while (true) {
				Effect e = (Effect) localIterator.next();
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (this.owner.getStates().canEffect(e.getCatagory())) {
					int[] rst = e.effectBuff(this.owner, this.caster, cbSkill);
					recordEffect(e, cbSkill.getAction().getSection(), rst);
					if (rst[0] == 1990) {
						Platform.getLog().logCombat(
								this.owner.getName() + "  buff effect: ." + getName() + " Rst:" + Arrays.toString(rst));
					}
				}
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (!(localIterator.hasNext())) {
					label232: return checkOverDueEffect();
				}
			}
		}
		return false;
	}

	public boolean checkOverDueRound() {
		this.durationRounds += 1;
		Platform.getLog().logCombat(this.owner.getName() + " buff check R :  " + getName() + "type" + this.type
				+ "     " + this.durationRounds + " vs " + this.maxDurationRounds);

		return ((this.durationRounds < this.maxDurationRounds) || (this.maxDurationRounds == -1));
	}

	private boolean checkOverDueEffect() {
		this.effectTimes += 1;
		Platform.getLog().logCombat(this.owner.getName() + " buff check A :  " + getName() + "type" + this.type
				+ "     " + this.effectTimes + " vs " + this.maxEffectTimes);

		return ((this.type != 0) || (this.effectTimes < this.maxEffectTimes) || (this.maxEffectTimes == -1));
	}

	public boolean effect(int triggerPoint, Round round) {
		if (this.triggerType == triggerPoint) {
			Platform.getLog().logCombat(this.owner.getName() + "  buff effect: ." + getName());
			Iterator localIterator = this.effects.iterator();

			while (true) {
				Effect e = (Effect) localIterator.next();
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (this.owner.getStates().canEffect(e.getCatagory())) {
					int[] rst = e.effectBuff(this.owner, this.caster, round.getSection());
					recordEffect(e, round.getSection(), rst);
					if (rst[0] == 1990) {
						Platform.getLog().logCombat(
								this.owner.getName() + "  buff effect: ." + getName() + " Rst:" + Arrays.toString(rst));
					}
				}
				if (!(this.owner.isAlive())) {
					return false;
				}
				if (!(localIterator.hasNext())) {
					label203: return checkOverDueEffect();
				}
			}
		}
		return false;
	}

	public void effectAttribute(Attributes attri) {
		for (Effect e : this.effects)
			e.effectAttributes(attri);
	}

	public void removed() {
		for (Effect e : this.effects) {
			e.removed(this.owner, this.caster);
		}
		destroy();
	}

	public void removed(Section section) {
		for (Effect e : this.effects) {
			int[] rst = e.removed(this.owner, this.caster);
			recordEffect(e, section, rst);
		}
		destroy();
	}

	private void destroy() {
	}

	public void added(Section section) {
		Platform.getLog().logCombat("Add buff:" + getName());
		for (Effect e : this.effects) {
			if (this.owner.getStates().canEffect(e.getCatagory())) {
				int[] rst = e.added(this.owner, this.caster, section);
				recordEffect(e, section, rst);
			}
		}
		effect(17, section);
	}

	public Skill.ConditionType getSkillCondition() {
		return this.skillCondition;
	}

	public int getCombatCondition() {
		return this.combatCondition;
	}

	public void setSkillCondition(Skill.ConditionType skillCondition) {
		this.skillCondition = skillCondition;
	}

	public void setCombatCondition(int combatCondition) {
		this.combatCondition = combatCondition;
	}

	public void recordEffect(Effect effect, Section section, int[] rst) {
		if (rst[0] == 1990) {
			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
					.setActor(section.getRecordUtil().getActor(this.owner, section))
					.setType(PbRecord.ActionRecord.RecordType.EFFECT);

			PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(effect.getId())
					.setBlock(false).setCrit(false).setEffectType(effect.getEffectType())
					.setAnimationId(effect.getEffectId());
			for (int i = 1; i < rst.length; ++i) {
				eb.addAttachParam(rst[i]);
			}
			r.setEffectRecord(eb);

			section.getRecordUtil().addRecord(r);
		}
	}
}
