package org.darcy.sanguo.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.record.RecordUtil;
import org.darcy.sanguo.combat.skill.Behavior;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;

import sango.packet.PbRecord;

public class Action {
	public static final int TYPE_NOMAL = 0;
	public static final int TYPE_MANUAL = 1;
	public static final int TYPE_INSERT = 2;
	private Section section;
	private Queue<Behavior> attacks = new ConcurrentLinkedQueue();
	LinkedBlockingQueue<CbSkill> insertSkills = new LinkedBlockingQueue();
	private int type;
	private Unit actor;
	private Skill skill;
	private boolean hit = false;

	public Action(Section section) {
		this.section = section;
	}

	protected boolean isFinished() {
		return ((this.section.getDefensive().countAliveUnit() != 0)
				&& (this.section.getOffensive().countAliveUnit() != 0));
	}

	protected void processOver() {
	}

	public void init(Unit actor) {
		this.actor = actor;

		this.type = 0;

		Platform.getLog().logCombat("Action :: " + actor.getName() + " type:" + this.type);
	}

	public void insertSkill(CbSkill skill) {
		this.insertSkills.offer(skill);
	}

	public Unit getUnit() {
		return this.actor;
	}

	public Skill getSkill() {
		return this.skill;
	}

	public void init(Unit actor, Skill skill, int type) {
		this.actor = actor;
		this.skill = skill;
		this.type = type;
	}

	public void combat() {
		if (!(this.actor.isAlive()))
			return;

		this.actor.getBuffs().effectBuff(2, this);

		if ((!(this.actor.getStates().hasState(3))) && (!(this.actor.getStates().hasState(4)))) {
			CbSkill cbSkill = new CbSkill(this);
			if (this.skill == null) {
				Platform.getLog().logCombat("get skill.  left:" + this.actor.getAttributes().get(0));
				this.skill = this.actor.getActionSkill();
			}
			this.attacks.addAll(this.skill.getBehaviors());
			cbSkill.init(this.actor, this.skill, this.skill.getType(), null);
			insertSkill(cbSkill);

			CbSkill cbs = null;
			int count = 100;
			while (((cbs = (CbSkill) this.insertSkills.poll()) != null) && (!(isFinished())) && (count-- > 0)) {
				if (cbs.getActor().getStates().hasState(3)) {
					continue;
				}

				Platform.getLog().logCombat(cbs.getActor().getName() + " Action :: " + cbs.getSkill().getName());
				recordStart(cbs);
				cbs.combat();
				recordEnd(cbs);
			}

		}

		this.actor.getBuffs().effectBuff(3, this);
	}

	public void next(Unit actor) {
		this.actor = actor;

		this.type = 0;
		this.attacks.addAll(this.skill.getBehaviors());
		this.hit = false;
		Platform.getLog()
				.logCombat("Action :: " + actor.getName() + " skill:" + this.skill.getName() + " type:" + this.type);
	}

	public RandomBox getRandomBox() {
		return this.section.getRandomBox();
	}

	public RecordUtil getRecordUtil() {
		return this.section.getRecordUtil();
	}

	public Section getSection() {
		return this.section;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	private void recordStart(CbSkill skill) {
		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
				.setActor(getRecordUtil().getActor(skill.getActor(), this.section))
				.setType(PbRecord.ActionRecord.RecordType.TOKEN)
				.setTokenRecord(PbRecord.TokenRecord.newBuilder().setType(PbRecord.TokenRecord.TokenType.START));
		getRecordUtil().addRecord(r);
	}

	private void recordEnd(CbSkill skill) {
		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
				.setActor(getRecordUtil().getActor(skill.getActor(), this.section))
				.setType(PbRecord.ActionRecord.RecordType.TOKEN)
				.setTokenRecord(PbRecord.TokenRecord.newBuilder().setType(PbRecord.TokenRecord.TokenType.END));

		getRecordUtil().addRecord(r);
	}
}
