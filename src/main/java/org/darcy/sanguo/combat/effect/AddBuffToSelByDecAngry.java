package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class AddBuffToSelByDecAngry extends Effect {
	private int buffId;
	private int value;
	private int addBuffEffet;

	public AddBuffToSelByDecAngry(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		AddBuffToSelByDecAngry e = new AddBuffToSelByDecAngry(this.id, this.type, this.description, this.paramCount,
				this.catagory);
		e.effectId = this.effectId;
		e.buffId = this.buffId;
		e.value = this.value;
		e.addBuffEffet = this.addBuffEffet;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.addBuffEffet = Integer.parseInt(this.params[1]);
		this.value = Integer.parseInt(this.params[2]);
		this.buffId = Integer.parseInt(this.params[3]);
	}

	public int[] effectBuff(Unit owner, Unit caster, Action action) {
		return effectBuff(owner, caster, action.getSection());
	}

	public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill) {
		return effectBuff(owner, caster, cbSkill.getAction());
	}

	public int[] effectBuff(Unit owner, Unit caster, CombatContent combatContent) {
		return effectBuff(owner, caster, combatContent.getCbSkill());
	}

	public int[] effectBuff(Unit owner, Unit caster, Section section) {
		int angry = owner.getAttributes().get(0);
		if (angry >= this.value) {
			Platform.getLog().logCombat("Effect: " + this.description);
			owner.getAttributes().set(0, angry - this.value);

			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
					.setActor(section.getRecordUtil().getActor(owner, section))
					.setType(PbRecord.ActionRecord.RecordType.EFFECT);

			PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
					.setCrit(false).setEffectType(4).setAnimationId(this.effectId).addAttachParam(-this.value);

			r.setEffectRecord(eb);
			section.getRecordUtil().addRecord(r);

			Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
			if (buff != null) {
				Buff bf = buff.copy();
				bf.setCaster(owner);
				bf.setOwner(owner);
				owner.getBuffs().addBuff(bf, section);

				recordEffect(section, this.addBuffEffet, new int[] { 1990, this.buffId }, owner);
			}
		}
		return this.rstNo;
	}
}
