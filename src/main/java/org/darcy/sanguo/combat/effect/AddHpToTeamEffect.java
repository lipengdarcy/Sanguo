package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class AddHpToTeamEffect extends Effect {
	private int rate;

	public AddHpToTeamEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		AddHpToTeamEffect e = new AddHpToTeamEffect(this.id, this.type, this.description, this.paramCount,
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
		this.effectType = 1;
		Section section = combat.getCbSkill().getAction().getSection();
		Team team = section.getOwnerTeam(src);
		int hp = (int) (combat.totalDamage * this.rate / 10000L);
		for (Unit u : team.getUnits()) {
			if ((u != null) && (u.isAlive()) && (u.getId() != src.getId()) && (!(u.getStates().hasState(1)))) {
				int left = u.getAttributes().getHp() + hp;
				if (left > u.getAttributes().get(7)) {
					left = u.getAttributes().get(7);
				}
				u.getAttributes().setHp(left);

				PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
						.setActor(section.getRecordUtil().getActor(u, section))
						.setType(PbRecord.ActionRecord.RecordType.EFFECT);

				PbRecord.EffectRecord.Builder eb = PbRecord.EffectRecord.newBuilder().setEffectId(this.id)
						.setBlock(false).setCrit(false).setEffectType(this.effectType).setAnimationId(this.effectId)
						.addAttachParam(hp);
				r.setEffectRecord(eb);

				r.setEffectRecord(eb);
				combat.getRecordUtil().addRecord(r);
			}
		}
		return this.rstNo;
	}
}
