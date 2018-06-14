package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class AddBuffEffect extends Effect {
	private int rate;
	private int buffId;

	public AddBuffEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		AddBuffEffect e = new AddBuffEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
		e.effectId = this.effectId;
		e.rate = this.rate;
		e.buffId = this.buffId;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.buffId = Integer.parseInt(this.params[1]);
		this.rate = Integer.parseInt(this.params[2]);
	}

	public void effectCombat(Unit src, Unit tar, CombatContent combat) {
		Platform.getLog().logCombat("Effect: " + this.description);
		if (combat.getRandomBox().getNextRandom() < this.rate) {
			Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getBuff(this.buffId);
			if (buff != null) {
				Buff bf = buff.copy();
				bf.setCaster(src);
				bf.setOwner(tar);
				boolean rst = tar.getBuffs().addBuff(bf, combat.getCbSkill().getAction().getSection());
				if (rst) {
					PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
							.setActor(combat.getRecordUtil().getActor(combat.getTarget(),
									combat.getCbSkill().getAction().getSection()))
							.setType(PbRecord.ActionRecord.RecordType.EFFECT).setEffectRecord(
									PbRecord.EffectRecord.newBuilder().setAnimationId(getEffectId()).addAttachParam(-1)
											.setBlock(false).setEffectType(0).setCrit(false).setEffectId(this.id));
					combat.getRecordUtil().addRecord(r);
				}
			}
		}
	}
}
