package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class AddAttributeBuffEffect extends Effect {
	private int aid;
	private int value;

	public AddAttributeBuffEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		AddAttributeBuffEffect e = new AddAttributeBuffEffect(this.id, this.type, this.description, this.paramCount,
				this.catagory);
		e.effectId = this.effectId;
		e.aid = this.aid;
		e.value = this.value;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.aid = Integer.parseInt(this.params[1]);
		this.value = Integer.parseInt(this.params[2]);
	}

	public int[] removed(Unit owner, Unit caster) {
		int add = 0;
		int index = 0;
		if (this.aid < 50) {
			add = this.value;
			index = this.aid;
		} else {
			index = this.aid - 50;
			add = owner.getAttributes().getBase(index) * this.value / 10000;
		}
		owner.getAttributes().set(index, owner.getAttributes().get(index) - add);
		if ((index == 7) && (owner.getAttributes().getHp() > owner.getAttributes().get(index))) {
			owner.getAttributes().setHp(owner.getAttributes().get(index));
		}

		this.effectType = 8;
		return new int[] { 1990, index, -add };
	}

	public int[] added(Unit owner, Unit caster, Section section) {
		int add = 0;
		int index = 0;
		if (this.aid < 50) {
			add = this.value;
			index = this.aid;
		} else {
			index = this.aid - 50;
			add = owner.getAttributes().getBase(index) * this.value / 10000;
		}
		Platform.getLog().logCombat("添加属性 index" + index);
		Platform.getLog().logCombat("添加属性 base" + owner.getAttributes().getBase(index));
		Platform.getLog().logCombat("添加属性 rate/value" + this.value);
		Platform.getLog().logCombat("添加属性 value" + add);
		owner.getAttributes().set(index, owner.getAttributes().get(index) + add);
		if ((index == 7) && (!(owner.getStates().hasState(1)))) {
			int hp = owner.getAttributes().getHp();
			int total = hp + add;
			if (total > owner.getAttributes().get(7)) {
				total = owner.getAttributes().get(7);
			}
			owner.getAttributes().setHp(total);

			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
					.setActor(section.getRecordUtil().getActor(owner, section))
					.setType(PbRecord.ActionRecord.RecordType.EFFECT);

			PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
					.setCrit(false).setEffectType(1).setAnimationId(this.effectId).addAttachParam(index)
					.addAttachParam(add);

			r.setEffectRecord(eb);
		}
		this.effectType = 7;
		return new int[] { 1990, index, add };
	}

	public void effectAttributes(Attributes attri) {
		attri.set(this.aid, this.value + attri.get(this.aid));
	}
}
