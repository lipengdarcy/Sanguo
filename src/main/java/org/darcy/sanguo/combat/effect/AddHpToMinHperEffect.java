package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class AddHpToMinHperEffect extends Effect {
	private int rate;

	public AddHpToMinHperEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		AddHpToMinHperEffect e = new AddHpToMinHperEffect(this.id, this.type, this.description, this.paramCount,
				this.catagory);
		e.effectId = this.effectId;
		e.rate = this.rate;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.rate = Integer.parseInt(this.params[1]);
	}

	public int[] effectBuff(Unit src, Unit tar, CombatContent combat) {
		Section section = combat.getCbSkill().getAction().getSection();
		Team team = section.getOwnerTeam(src);
		Unit u = (Unit) team.getTargets(0, 10, src, true).get(0);
		int hp = (int) (combat.totalDamage * this.rate / 10000L);
		if (u.getStates().hasState(1)) {
			hp = 0;
		}
		int left = u.getAttributes().getHp() + hp;
		if (left > u.getAttributes().get(7)) {
			left = u.getAttributes().get(7);
		}
		u.getAttributes().setHp(left);
		this.effectType = 1;

		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
				.setActor(section.getRecordUtil().getActor(u, section))
				.setType(PbRecord.ActionRecord.RecordType.EFFECT);

		PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id).setBlock(false)
				.setCrit(false).setEffectType(this.effectType).setAnimationId(this.effectId).addAttachParam(hp);
		r.setEffectRecord(eb);

		section.getRecordUtil().addRecord(r);
		return new int[] { 1001 };
	}
}
