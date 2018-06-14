package org.darcy.sanguo.combat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.record.RecordUtil;
import org.darcy.sanguo.combat.skill.Behavior;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;

import sango.packet.PbRecord;

public class CbSkill {
	public static final int CBT_CONDITION_COUNT = 10;
	public static final int CONDITION_HIT = 1;
	public static final int CONDITION_CRIT = 2;
	public static final int CONDITION_BLOCK = 3;
	public static final int CONDITION_MISSED = 4;
	public static final int CONDITION_ZEROHP = 5;
	public static final int CONDITION_DAMAGE = 6;
	public static final int CONDITION_CURE = 7;
	public static final int CONDITION_INCANGRY = 8;
	public static final int CONDITION_DECANGRY = 9;
	boolean[] cbtCondition = new boolean[10];
	private Action action;
	private Unit actor;
	private Skill skill;
	private Queue<Behavior> attacks = new ConcurrentLinkedQueue();
	private Unit damageSource = null;
	Set<Unit> blockTargets = new HashSet();
	List<Unit> allTargets = new ArrayList();
	Set<Unit> hitTargets = new HashSet();
	Set<Unit> critTargets = new HashSet();
	Set<Unit> missTargets = new HashSet();
	Set<Unit> zeroHpTargets = new HashSet();
	Set<Unit> damageTargets = new HashSet();
	Set<Unit> cureTargets = new HashSet();
	Set<Unit> incAngryTargets = new HashSet();
	Set<Unit> decAngryTargets = new HashSet();
	public Unit curTarget;
	private int tmpAngry;

	public boolean isCondition(Unit owner, int cdt) {
		if (cdt == -1)
			return true;
		if (cdt < 10) {
			return this.cbtCondition[cdt];
		}

		switch (cdt) {
		case 21:
			return this.hitTargets.contains(owner);
		case 22:
			return this.critTargets.contains(owner);
		case 23:
			return this.blockTargets.contains(owner);
		case 24:
			return this.missTargets.contains(owner);
		case 25:
			return this.zeroHpTargets.contains(owner);
		case 26:
			return this.damageTargets.contains(owner);
		case 27:
			return this.cureTargets.contains(owner);
		case 28:
			return this.incAngryTargets.contains(owner);
		case 29:
			return this.decAngryTargets.contains(owner);
		}
		return false;
	}

	public CbSkill(Action action) {
		this.action = action;
	}

	protected boolean isFinished() {
		return false;
	}

	protected void processOver() {
	}

	public void addBlockTargets(Unit tar) {
		this.blockTargets.add(tar);
	}

	public Action getAction() {
		return this.action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void init(Unit actor, Skill skill, int type, Unit damageSource) {
		this.actor = actor;
		this.skill = skill;
		this.attacks.addAll(this.skill.getBehaviors());
		this.damageSource = damageSource;
		this.cbtCondition = new boolean[10];
		this.blockTargets.clear();
		this.allTargets.clear();
		this.hitTargets.clear();
		this.critTargets.clear();
		this.missTargets.clear();
		this.zeroHpTargets.clear();
		this.damageTargets.clear();
		this.cureTargets.clear();
		this.incAngryTargets.clear();
		this.decAngryTargets.clear();
	}

	public void combat() {
		Team targetTeam;
		this.action.getSection().checkAlive();
		Platform.getLog().logCombat(this.actor.getName() + "    Skill : " + this.skill.getName());

		if (this.skill.getType() == 1) {
			this.tmpAngry = this.actor.getAttributes().get(0);
		}

		this.actor.getBuffs().effectBuff(4, this);
		Platform.getLog().logCombat(this.actor.getName() + " Skill :" + this.skill.getDescription());
		PbRecord.ActionRecord.Builder r = null;

		boolean tempFirstAttact = true;

		List targets = null;
		Behavior attack = null;
		int count = 100;
		while (((attack = (Behavior) this.attacks.poll()) != null) && (!(isFinished()))
				&& (this.actor.canCastSkill(this.skill)) && (count-- > 0)) {
			Unit target;
			this.actor.getBuffs().effectBuff(6, this);
			Platform.getLog().logCombat(this.actor.getName() + " behave :" + attack.getDescription());

			Team sTeam = this.action.getSection().getOwnerTeam(this.actor);
			Team tTeam = this.action.getSection().getTargetTeam(this.actor);
			targetTeam = sTeam.getTargetTeam(sTeam, tTeam, attack.getAtkTarget());
			int targetPoint = sTeam.getTargetPoint(sTeam, targetTeam, this.actor, this.damageSource,
					attack.getAtkTarget());
			targets = this.action.getSection().getTargets(targetTeam, targetPoint, attack.getAtkRange(), this.actor,
					attack.isIncludeSelf());
			Unit tar = null;
			if ((targetPoint != -1) && (targets.size() > 0)) {
				tar = targetTeam.getUnit(targetPoint);
				if ((attack.getAtkRange() == 8) || (attack.getAtkRange() == 9) || (attack.getAtkRange() == 10)) {
					tar = (Unit) targets.get(0);
				}
			}

			if ((tempFirstAttact) && (targets.size() > 0)) {
				r = PbRecord.ActionRecord.newBuilder().setIndex(0)
						.setActor(getRecordUtil().getActor(this.actor, this.action.getSection()))
						.setType(PbRecord.ActionRecord.RecordType.USESKILL)
						.setUseSkillRecord(PbRecord.UseSkillRecord.newBuilder()
								.setActionType(PbRecord.UseSkillRecord.ActionType.valueOf(this.action.getType()))
								.setSource(getRecordUtil().getActor(this.actor, this.action.getSection()))
								.setTargetPoint(getRecordUtil().getActor(tar, this.action.getSection()))
								.setActionGroupId(this.skill.getActionGroupId())
								.setBeatAction(this.skill.getUnBeatAction()).setSkillId(this.skill.getId()));

				getRecordUtil().addRecord(r);
				tempFirstAttact = false;
			}

			Platform.getLog().logCombat("Behave targets: " + Team.toString(targets));
			for (Iterator localIterator = targets.iterator(); localIterator.hasNext();) {
				target = (Unit) localIterator.next();
				if (target.isAlive()) {
					this.allTargets.add(target);
					this.curTarget = target;
					target.getBuffs().effectBuff(11, this);
					CombatContent combat = new CombatContent(this.actor, target, attack, this);
					combat.combat();
					if (!this.cbtCondition[1] && (combat.rstHit)) {
						this.cbtCondition[1] = true;
					}
					if (!this.cbtCondition[2] && (combat.rstCrit)
							&& (((combat.atkType == CombatContent.AtkType.MAGICATK)
									|| (combat.atkType == CombatContent.AtkType.PHYSICATK)))) {
						this.cbtCondition[2] = true;
					}
					if ((!(combat.rstHit)) && !this.cbtCondition[4]) {
						this.cbtCondition[4] = true;
					}
				}
			}

			this.curTarget = null;

			for (Iterator localIterator = targets.iterator(); localIterator.hasNext();) {
				target = (Unit) localIterator.next();
				if (target.isAlive()) {
					Platform.getLog().logCombat(target.getName());
					target.getBuffs().effectBuff(12, this);
				}

			}

			this.actor.getBuffs().effectBuff(7, this);
		}

		this.cbtCondition[3] = ((this.blockTargets.size() > 0) ? true : false);

		if (this.skill.getType() == 1) {
			int now = this.actor.getAttributes().get(0);
			int left = now - this.tmpAngry;
			if (left < 0) {
				left = 0;
			}

			r = PbRecord.ActionRecord.newBuilder().setIndex(0)
					.setActor(getRecordUtil().getActor(this.actor, this.action.getSection()))
					.setType(PbRecord.ActionRecord.RecordType.RESOURCE)
					.setResourceRecord(PbRecord.ResourceRecord.newBuilder()
							.setType(PbRecord.ResourceRecord.ResourceType.ANGRYPOINT).setValue(-1 * this.tmpAngry));
			getRecordUtil().addRecord(r);

			this.actor.getAttributes().set(0, left);
			Platform.getLog().logCombat("reset angry.  dec:" + this.tmpAngry);
			Platform.getLog().logCombat("reset angry.  left:" + left);
		} else if (this.skill.getType() == 2) {
			int killPoint = this.actor.getAttributes().getKillPoints();
			this.actor.getAttributes().setKillPoints(0);
			Platform.getLog().logCombat("discount killPoints.");
			r = PbRecord.ActionRecord.newBuilder().setIndex(0)
					.setActor(getRecordUtil().getActor(this.actor, this.action.getSection()))
					.setType(PbRecord.ActionRecord.RecordType.RESOURCE).setResourceRecord(PbRecord.ResourceRecord
							.newBuilder().setType(PbRecord.ResourceRecord.ResourceType.KILLPOINT).setValue(-killPoint));
			getRecordUtil().addRecord(r);
		}

		Platform.getLog().logCombat(this.actor.getName() + " 技能类型 " + this.skill.getType());
		Platform.getLog().logCombat(this.actor.getName() + " 命中 " + this.cbtCondition[1]);
		Platform.getLog().logCombat(this.actor.getName() + " 未封怒 " + (!(this.actor.getStates().hasState(2))));
		if ((this.skill.getType() == 0) && this.cbtCondition[1] && (!(this.actor.getStates().hasState(2)))) {
			this.actor.getAttributes().set(0, this.actor.getAttributes().get(0) + 2);
			Platform.getLog().logCombat(this.actor.getName() + " add angry " + this.actor.getAttributes().get(0));

			r = PbRecord.ActionRecord.newBuilder().setIndex(0)
					.setActor(getRecordUtil().getActor(this.actor, this.action.getSection()))
					.setType(PbRecord.ActionRecord.RecordType.RESOURCE).setResourceRecord(PbRecord.ResourceRecord
							.newBuilder().setType(PbRecord.ResourceRecord.ResourceType.ANGRYPOINT).setValue(2));

			getRecordUtil().addRecord(r);
		}

		this.action.getSection().checkAlive();

		this.actor.getBuffs().effectBuff(5, this);
		Set tempAll = new HashSet();
		if (targets != null)
			for (Unit target : this.allTargets)
				if ((!(tempAll.contains(target))) && (target.isAlive())) {
					target.getBuffs().effectBuff(10, this);
					tempAll.add(target);
				}
	}

	public void next(Unit actor, Skill skill, int type, Unit damageSource) {
		this.actor = actor;
		this.skill = skill;
		this.attacks.addAll(this.skill.getBehaviors());
		this.damageSource = null;
		this.cbtCondition = new boolean[10];
		this.blockTargets.clear();
		this.allTargets.clear();
		this.hitTargets.clear();
		this.critTargets.clear();
		this.missTargets.clear();
		this.zeroHpTargets.clear();
		this.damageTargets.clear();
		this.cureTargets.clear();
		this.incAngryTargets.clear();
		this.decAngryTargets.clear();
		this.curTarget = null;
	}

	public RandomBox getRandomBox() {
		return this.action.getRandomBox();
	}

	public RecordUtil getRecordUtil() {
		return this.action.getRecordUtil();
	}

	public Unit getActor() {
		return this.actor;
	}

	public Skill getSkill() {
		return this.skill;
	}

	public void setActor(Unit actor) {
		this.actor = actor;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}
}
