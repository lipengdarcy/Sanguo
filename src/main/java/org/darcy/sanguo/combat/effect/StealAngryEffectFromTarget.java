package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class StealAngryEffectFromTarget extends Effect {
	private int steal;
	private int value;
	private int buffId;
	private int rate;
	private int addBuffEffect;
	private int relationEffect;

	public StealAngryEffectFromTarget(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		StealAngryEffectFromTarget e = new StealAngryEffectFromTarget(this.id, this.type, this.description,
				this.paramCount, this.catagory);
		e.effectId = this.effectId;
		e.rate = this.rate;
		e.value = this.value;
		e.steal = this.steal;
		e.buffId = this.buffId;
		e.addBuffEffect = this.addBuffEffect;
		e.relationEffect = this.relationEffect;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.addBuffEffect = Integer.parseInt(this.params[1]);
		this.steal = Integer.parseInt(this.params[2]);
		this.value = Integer.parseInt(this.params[3]);
		this.buffId = Integer.parseInt(this.params[4]);
		this.rate = Integer.parseInt(this.params[5]);
		this.relationEffect = Integer.parseInt(this.params[6]);
	}

	public int[] effectBuff(Unit owner, Unit caster, CombatContent combat) {
		if (!(canEffectRelation(this.relationEffect,
				combat.getCbSkill().getAction().getSection().getEffectRalation(owner, combat.getTarget())))) {
			return this.rstNo;
		}
		Platform.getLog().logCombat("Effect: " + this.description);
		int ran = combat.getRandomBox().getNextRandom();
		if (ran < this.rate) {
			Unit tar = combat.getTarget();
			int angry = tar.getAttributes().get(0);
			if (angry < this.value) {
				Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class))
						.getBuff(this.buffId);
				if (buff != null) {
					Buff bf = buff.copy();
					bf.setCaster(owner);
					bf.setOwner(tar);
					tar.getBuffs().addBuff(bf, combat.getCbSkill().getAction().getSection());

					recordEffect(combat.getCbSkill().getAction().getSection(), this.addBuffEffect,
							new int[] { 1990, this.buffId }, tar);
				}
			} else {
				tar.getAttributes().set(0, angry - this.value);
				PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
						.setActor(combat.getRecordUtil().getActor(tar, combat.getCbSkill().getAction().getSection()))
						.setType(PbRecord.ActionRecord.RecordType.EFFECT);

				PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id)
						.setBlock(false).setCrit(false).setEffectType(4).setAnimationId(this.effectId)
						.addAttachParam(-this.value);
				r.setEffectRecord(eb);
				combat.getRecordUtil().addRecord(r);
				if (this.steal == 1) {
					Platform.getLog().logCombat(owner.getName() + " 未封怒 " + (!(owner.getStates().hasState(2))));
					if (!(owner.getStates().hasState(2))) {
						owner.getAttributes().set(0, owner.getAttributes().get(0) + this.value);
						r = PbRecord.ActionRecord.newBuilder()
								.setActor(combat.getRecordUtil().getActor(owner,
										combat.getCbSkill().getAction().getSection()))
								.setType(PbRecord.ActionRecord.RecordType.EFFECT);

						eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setEffectType(4)
								.setAnimationId(this.effectId).addAttachParam(this.value).setBlock(false)
								.setCrit(false);
						r.setEffectRecord(eb);
						combat.getRecordUtil().addRecord(r);
					}
				}
			}
		}
		return this.rstNo;
	}
}
