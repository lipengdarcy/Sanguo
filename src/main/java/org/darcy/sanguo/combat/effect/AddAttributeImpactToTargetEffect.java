package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class AddAttributeImpactToTargetEffect extends Effect {
	private int aid;
	private int value;
	private int relationEffect;

	public AddAttributeImpactToTargetEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		AddAttributeImpactToTargetEffect e = new AddAttributeImpactToTargetEffect(this.id, this.type, this.description,
				this.paramCount, this.catagory);
		e.effectId = this.effectId;
		e.aid = this.aid;
		e.value = this.value;
		e.relationEffect = this.relationEffect;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.aid = Integer.parseInt(this.params[1]);
		this.value = Integer.parseInt(this.params[2]);
		this.relationEffect = Integer.parseInt(this.params[3]);
	}

	public int[] effectBuff(Unit owner, Unit caster, Action action) {
		return effectBuff(owner, caster, action.getSection());
	}

	public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill) {
		return effectBuff(owner, caster, cbSkill.getAction());
	}

	public int[] effectBuff(Unit owner, Unit caster, CombatContent combat) {
		int add = 0;
		int index = 0;
		Unit u = combat.getTarget();
		if (!(canEffectRelation(this.relationEffect,
				combat.getCbSkill().getAction().getSection().getEffectRalation(owner, u)))) {
			return this.rstNo;
		}
		if (this.aid < 50) {
			add = this.value;
			index = this.aid;
		} else {
			index = this.aid - 50;
			add = u.getAttributes().getBase(index) * this.value / 10000;
		}
		u.getAttributes().set(index, u.getAttributes().get(index) + add);
		if ((index == 7) && (!(u.getStates().hasState(1)))) {
			int hp = u.getAttributes().getHp();
			int total = hp + add;
			if (total > u.getAttributes().get(7)) {
				total = u.getAttributes().get(7);
			}
			u.getAttributes().setHp(total);

			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
					.setActor(combat.getRecordUtil().getActor(u, combat.getCbSkill().getAction().getSection()))
					.setType(PbRecord.ActionRecord.RecordType.EFFECT);

			PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
					.setCrit(false).setEffectType(1).setAnimationId(this.effectId).addAttachParam(index)
					.addAttachParam(add);

			r.setEffectRecord(eb);
		}
		this.effectType = 7;

		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
				.setActor(combat.getRecordUtil().getActor(u, combat.getCbSkill().getAction().getSection()))
				.setType(PbRecord.ActionRecord.RecordType.EFFECT);

		PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
				.setCrit(false).setEffectType(this.effectType).setAnimationId(this.effectId).addAttachParam(index)
				.addAttachParam(add);

		r.setEffectRecord(eb);

		combat.getRecordUtil().addRecord(r);
		return new int[] { 1990, index, add };
	}
}
