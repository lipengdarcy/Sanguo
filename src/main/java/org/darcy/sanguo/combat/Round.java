package org.darcy.sanguo.combat;

import java.util.concurrent.LinkedBlockingQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.record.RecordUtil;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;

import sango.packet.PbRecord;

public class Round {
	Section section;
	LinkedBlockingQueue<Action> manualActions = new LinkedBlockingQueue();

	public Round(Section section) {
		this.section = section;
	}

	public void init() {
		this.section.getActionList().refresh();
		this.manualActions.clear();
	}

	public void next() {
		this.section.getActionList().refresh();
		this.manualActions.clear();
	}

	private Unit getActor() {
		if (isFinished()) {
			return null;
		}

		return this.section.getActionList().getActor();
	}

	public void addSkillPoint(MainWarrior main) {
		int killPoint = main.getAttributes().getKillPoints();
		if (killPoint < 4) {
			killPoint += 2;
		}
		main.getAttributes().setKillPoints(killPoint);
		Platform.getLog().logCombat("add killPoints.");
		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
				.setActor(getRecordUtil().getActor(main, this.section))
				.setType(PbRecord.ActionRecord.RecordType.RESOURCE).setResourceRecord(PbRecord.ResourceRecord
						.newBuilder().setType(PbRecord.ResourceRecord.ResourceType.KILLPOINT).setValue(2));
		getRecordUtil().addRecord(r);
	}

	public void combat() {
		MainWarrior first = this.section.getActionList().getFirstMain();
		if (first != null) {
			addSkillPoint(first);
		}
		MainWarrior second = this.section.getActionList().getSecondMain();
		if (second != null) {
			addSkillPoint(second);
		}

		this.section.getOffensive().effectBuff(0, this);
		this.section.getDefensive().effectBuff(0, this);
		int count = 100;
		while (count-- > 0) {
			if ((this.section.insertActions.size() == 0) && (!(this.section.getStage().isPvP()))) {
				PbRecord.ActionRecord ar = getRecordUtil().getNextManualSkillRecord();
				if (ar != null) {
					Action mannualAction = new Action(this.section);
					Skill mannualSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
							.getSkill(ar.getUseSkillRecord().getSkillId());
					PbRecord.Actor actor = ar.getActor();
					Unit u = getSection().getUnit(actor);
					mannualAction.init(u, mannualSkill, 1);
					this.section.insertActions.offer(mannualAction);
				}
			} else if ((this.section.insertActions.size() == 0) && (this.section.getStage().isPvP())
					&& (this.section.getActionList().getFirstIndex() == 0)) {
				Skill mannualSkill;
				Action mannualAction;
				MainWarrior main = this.section.getActionList().getFirstMain();
				if ((main != null) && (!(main.getStates().hasState(3))) && (!(main.getStates().hasState(4)))
						&& (main.getAttributes().getKillPoints() >= 4)) {
					mannualSkill = main.getAutoSkill();
					if (mannualSkill != null) {
						mannualAction = new Action(this.section);
						mannualAction.init(main, mannualSkill, 1);
						this.section.insertActions.offer(mannualAction);
					}

				}

				main = this.section.getActionList().getSecondMain();
				if ((main != null) && (!(main.getStates().hasState(3))) && (!(main.getStates().hasState(4)))
						&& (main.getAttributes().getKillPoints() >= 4) && (this.section.insertActions.size() == 0)) {
					mannualSkill = main.getAutoSkill();
					if (mannualSkill != null) {
						mannualAction = new Action(this.section);
						mannualAction.init(main, mannualSkill, 1);
						this.section.insertActions.offer(mannualAction);
					}

				}

			}

			if (this.section.insertActions.size() == 0) {
				Unit actor = getActor();
				if ((actor != null) && (!(isAllDie()))) {
					Platform.getLog().logCombat("NormalAction :: name" + actor.getName());
					Platform.getLog().logCombat("NormalAction :: hp" + actor.getAttributes().getHp());

					Action action = new Action(this.section);
					action.init(actor);
					this.section.insertActions.offer(action);
				}
			}

			Action insertAction = null;
			if (((insertAction = (Action) this.section.insertActions.poll()) == null) || (isAllDie()))
				break;
			insertAction.combat();
		}

		Platform.getLog().logCombat("Round over.");
		this.section.getOffensive().effectBuff(1, this);
		this.section.getDefensive().effectBuff(1, this);
		this.section.getOffensive().checkBuffRound(this.section);
		this.section.getDefensive().checkBuffRound(this.section);
		processOver();
	}

	protected boolean isFinished() {
		if (this.section.getActionList().isRoundFinished()) {
			return true;
		}

		return ((this.section.getDefensive().countAliveUnit() != 0)
				&& (this.section.getOffensive().countAliveUnit() != 0));
	}

	private boolean isAllDie() {
		return ((this.section.getDefensive().countAliveUnit() != 0)
				&& (this.section.getOffensive().countAliveUnit() != 0));
	}

	protected void processOver() {
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

	public LinkedBlockingQueue<Action> getInsertActions() {
		return this.section.insertActions;
	}

	public void setSection(Section section) {
		this.section = section;
	}
}
