package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class StealAngryEffect extends Effect {
	private int steal;
	private int value;
	private int buffId;
	private int rate;
	private int addBuffEffect;

	public StealAngryEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		StealAngryEffect e = new StealAngryEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
		e.effectId = this.effectId;
		e.rate = this.rate;
		e.value = this.value;
		e.steal = this.steal;
		e.buffId = this.buffId;
		e.addBuffEffect = this.addBuffEffect;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.addBuffEffect = Integer.parseInt(this.params[1]);
		this.steal = Integer.parseInt(this.params[2]);
		this.value = Integer.parseInt(this.params[3]);
		this.buffId = Integer.parseInt(this.params[4]);
		this.rate = Integer.parseInt(this.params[5]);
	}

	public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill) {
		Platform.getLog().logCombat("Effect: " + this.description);
		int ran = cbSkill.getRandomBox().getNextRandom();
		if (ran < this.rate) {
			Unit tar = cbSkill.getActor();
			int angry = tar.getAttributes().get(0);
			if (angry < this.value) {
				Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class))
						.getBuff(this.buffId);
				if (buff != null) {
					Buff bf = buff.copy();
					bf.setCaster(owner);
					bf.setOwner(tar);
					tar.getBuffs().addBuff(bf, cbSkill.getAction().getSection());

					recordEffect(cbSkill.getAction().getSection(), this.addBuffEffect, new int[] { 1990, this.buffId },
							tar);
				}
			} else {
				tar.getAttributes().set(0, angry - this.value);
				PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
						.setActor(cbSkill.getRecordUtil().getActor(tar, cbSkill.getAction().getSection()))
						.setType(PbRecord.ActionRecord.RecordType.EFFECT);

				PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id)
						.setBlock(false).setCrit(false).setEffectType(4).setAnimationId(this.effectId)
						.addAttachParam(-this.value);
				r.setEffectRecord(eb);
				cbSkill.getRecordUtil().addRecord(r);
				if (this.steal == 1) {
					Platform.getLog().logCombat(owner.getName() + " 未封怒 " + (!(owner.getStates().hasState(2))));
					if (!(owner.getStates().hasState(2))) {
						owner.getAttributes().set(0, owner.getAttributes().get(0) + this.value);
						r = PbRecord.ActionRecord.newBuilder()
								.setActor(cbSkill.getRecordUtil().getActor(owner, cbSkill.getAction().getSection()))
								.setType(PbRecord.ActionRecord.RecordType.EFFECT);

						eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setEffectType(3)
								.setAnimationId(this.effectId).setCrit(false).setBlock(false)
								.addAttachParam(this.value);
						r.setEffectRecord(eb);
						cbSkill.getRecordUtil().addRecord(r);
					}
				}
			}
		}
		return this.rstNo;
	}

	public int[] effectBuff(Unit owner, Unit caster, CombatContent combatContent) {
		return effectBuff(owner, caster, combatContent.getCbSkill());
	}
}
