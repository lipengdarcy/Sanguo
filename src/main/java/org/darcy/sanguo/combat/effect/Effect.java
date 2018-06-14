package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public abstract class Effect {
	public static final int TYPE_BUFF = 1;
	public static final int TYPE_SKILL = 0;
	public static final int RECORD_NO = 1001;
	public static final int RECORD_YES = 1990;
	public static final int EFFECT_TYPE_NONE = 0;
	public static final int EFFECT_TYPE_ADDHP = 1;
	public static final int EFFECT_TYPE_REDUCEHP = 2;
	public static final int EFFECT_TYPE_ADDANGRY = 3;
	public static final int EFFECT_TYPE_REDUCEANGRY = 4;
	public static final int EFFECT_TYPE_ADDSTATE = 5;
	public static final int EFFECT_TYPE_REMOVESTATE = 6;
	public static final int EFFECT_TYPE_ADDMODITYATTRIBUTE = 7;
	public static final int EFFECT_TYPE_REMOVEMODITYATTRIBUTE = 8;
	public static final int RELATION_EFFECT_ALL = 0;
	public static final int RELATION_EFFECT_ENEMY = 1;
	public static final int RELATION_EFFECT_FRIEND = 2;
	protected int id;
	protected int type;
	protected int paramCount;
	protected String description;
	protected int effectId;
	protected int catagory;
	protected int effectType = 0;
	protected String[] params;
	protected int[] rstNo = { 1001 };
	protected int[] rstYesDefault = { 1990, -1 };

	public Effect(int id, int type, String description, int paramCount, int catagory) {
		this.id = id;
		this.description = description;
		this.paramCount = paramCount;
		this.type = type;
		this.catagory = catagory;
		this.params = new String[paramCount];
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String[] getParams() {
		return this.params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getParamCount() {
		return this.paramCount;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getEffectId() {
		return this.effectId;
	}

	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}

	public int getCatagory() {
		return this.catagory;
	}

	public void setCatagory(int catagory) {
		this.catagory = catagory;
	}

	public abstract Effect copy();

	public int[] removed(Unit owner, Unit caster) {
		return this.rstNo;
	}

	public int[] effectBuff(Unit owner, Unit caster, Section section) {
		return this.rstNo;
	}

	public int[] effectBuff(Unit owner, Unit caster, Action action) {
		return this.rstNo;
	}

	public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill) {
		return this.rstNo;
	}

	public int[] effectBuff(Unit owner, Unit caster, CombatContent combatContent) {
		return this.rstNo;
	}

	public void effectCombat(Unit src, Unit tar, CombatContent combat) {
	}

	public int[] added(Unit owner, Unit caster, Section section) {
		return this.rstNo;
	}

	public void effectAttributes(Attributes attri) {
	}

	public abstract void initParams();

	public int getEffectType() {
		return this.effectType;
	}

	public void setEffectType(int effectType) {
		this.effectType = effectType;
	}

	public void recordEffect(Section section, int[] rst, Unit target) {
		if (rst[0] == 1990) {
			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
					.setActor(section.getRecordUtil().getActor(target, section))
					.setType(PbRecord.ActionRecord.RecordType.EFFECT);

			PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
					.setCrit(false).setEffectType(this.effectType).setAnimationId(this.effectId);
			for (int i = 1; i < rst.length; ++i) {
				eb.addAttachParam(rst[i]);
			}
			r.setEffectRecord(eb);

			section.getRecordUtil().addRecord(r);
		}
	}

	public void recordEffect(Section section, int eId, int[] rst, Unit target) {
		if (rst[0] == 1990) {
			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
					.setActor(section.getRecordUtil().getActor(target, section))
					.setType(PbRecord.ActionRecord.RecordType.EFFECT);

			PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
					.setCrit(false).setEffectType(this.effectType).setAnimationId(eId);
			for (int i = 1; i < rst.length; ++i) {
				eb.addAttachParam(rst[i]);
			}
			r.setEffectRecord(eb);

			section.getRecordUtil().addRecord(r);
		}
	}

	public boolean canEffectRelation(int relationEffect, int relation) {
		if (relationEffect == 0) {
			return true;
		}

		return (relationEffect != relation);
	}
}
