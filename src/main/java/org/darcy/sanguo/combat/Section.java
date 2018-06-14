package org.darcy.sanguo.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.record.RecordUtil;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;

import sango.packet.PbRecord;

public class Section {
	private boolean auto;
	private Team offensive;
	private Team defensive;
	private int roundIndex;
	private Stage stage;
	private boolean win;
	private int maxRound;
	private ActionList actionList = new ActionList();

	LinkedBlockingQueue<Action> insertActions = new LinkedBlockingQueue();

	public Section(Stage stage) {
		this.stage = stage;
	}

	private boolean isFinished() {
		if (this.roundIndex >= this.maxRound)
			return true;

		return ((this.defensive.hasAlive()) && (this.offensive.hasAlive()));
	}

	private void processOver() {
		this.win = false;
		if (this.roundIndex > this.maxRound) {
			this.win = false;
			Platform.getLog().logCombat("failure: more than 30 rounds.");
		} else if (!(this.defensive.hasAlive())) {
			this.win = true;
			Platform.getLog().logCombat("Win.");
		} else {
			Platform.getLog().logCombat("failure: all dead.");
		}
	}

	public void init(boolean auto, Team offen, Team defen) {
		this.auto = auto;
		this.offensive = offen;
		this.defensive = defen;
		this.roundIndex = 0;
		this.maxRound = this.stage.getMaxRound();

		Platform.getLog().logCombat("section start!");
		Platform.getLog().logCombat("auto: " + auto);
		Platform.getLog().logCombat("offens :: " + offen.toString());
		Platform.getLog().logCombat("defens :: " + defen.toString());

		this.actionList.init(this.offensive, this.defensive);
	}

	public int getEffectRalation(Unit owner, Unit tar) {
		Team team = getOwnerTeam(owner);
		for (Unit u : team.getUnits()) {
			if (u == tar) {
				return 2;
			}
		}

		return 1;
	}

	public void combat() {
		try {
			MainWarrior first = getActionList().getFirstMain();
			if (first != null) {
				first.refreshSkillUseRecord();
			}
			MainWarrior second = getActionList().getSecondMain();
			if (second != null) {
				second.refreshSkillUseRecord();
			}

			this.offensive.effectBuff(15, this);
			this.defensive.effectBuff(15, this);
			Round round = null;
			while (!(isFinished())) {
				Platform.getLog().logCombat("Round  " + this.roundIndex);
				PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder()
						.setActor(PbRecord.Actor.newBuilder().setTeamType(-1).setLocation(-1)).setIndex(0)
						.setType(PbRecord.ActionRecord.RecordType.NEWROUND)
						.setNewRoundRecord(PbRecord.NewRoundRecord.newBuilder().setIndex(this.roundIndex));
				getRecordUtil().addRecord(r);
				if (round == null) {
					round = new Round(this);
					round.init();
				} else {
					round.next();
				}

				round.combat();
				this.roundIndex += 1;
			}
			this.offensive.effectBuff(16, this);
			this.defensive.effectBuff(16, this);
			Platform.getLog().logCombat("Round  over");
			processOver();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("战斗记录异常！！！");
			Platform.getLog().logCombat("战斗记录异常！！！");
			getRecordUtil().setResult(false);
			Platform.getLog().logCombat(getRecordUtil());
			this.win = false;
		}
	}

	public void checkAlive() {
		for (Unit u : this.offensive.getUnits()) {
			if ((u == null) || (!(u.isAlive())) || (u.checkAlive()))
				continue;
			processDie(u);
		}
		for (Unit u : this.defensive.getUnits()) {
			if ((u == null) || (!(u.isAlive())) || (u.checkAlive()))
				continue;
			processDie(u);
		}
	}

	public void next(boolean auto, Team defen) {
		this.auto = auto;
		this.defensive = defen;
		this.roundIndex = 0;
		Platform.getLog().logCombat("section start!");
		Platform.getLog().logCombat("auto: " + auto);
		Platform.getLog().logCombat("offens :: " + this.offensive.toString());
		Platform.getLog().logCombat("defens :: " + defen.toString());

		this.actionList.init(this.offensive, this.defensive);
	}

	public void processDie(Unit target) {
	}

	public boolean isAuto() {
		return this.auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	public Team getOffensive() {
		return this.offensive;
	}

	public void setOffensive(Team offensive) {
		this.offensive = offensive;
	}

	public Team getDefensive() {
		return this.defensive;
	}

	public Team getOwnerTeam(Unit unit) {
		if (this.defensive.hasUnit(unit)) {
			return this.defensive;
		}
		return this.offensive;
	}

	public int getOwnerTeamType(Unit unit) {
		if (this.defensive.hasUnit(unit)) {
			return 0;
		}
		return 1;
	}

	public int getTeamType(Team team) {
		if (team == this.defensive) {
			return 0;
		}
		return 1;
	}

	public Unit getUnit(PbRecord.Actor actor) {
		Team t = (actor.getTeamType() == 1) ? this.offensive : this.defensive;
		return t.getUnit(actor.getLocation());
	}

	public List<Unit> getTargets(Team targetTeam, int targetPoint, int range, Unit actor, boolean includeSelf) {
		if (range == 11) {
			List list = new ArrayList();
			for (Unit u : this.offensive.getUnits()) {
				if ((u == null) || (!(u.isAlive())) || ((!(includeSelf)) && (u.getId() == actor.getId())))
					continue;
				list.add(u);
			}

			int len = this.defensive.getUnits().length;
			if (len == 0)
				return list;
			int i = 0;
			while (true) {
				Unit u = this.defensive.getUnits()[i++];
				if ((u != null) && (u.isAlive()) && (((includeSelf) || (u.getId() != actor.getId()))))
					list.add(u);
				if (i >= len)
					break;
			}
		}
		return targetTeam.getTargets(targetPoint, range, actor, includeSelf);
	}

	public Team getTargetTeam(Unit unit) {
		if (this.defensive.hasUnit(unit)) {
			return this.offensive;
		}
		return this.defensive;
	}

	public void setDefensive(Team defensive) {
		this.defensive = defensive;
	}

	public int getRoundIndex() {
		return this.roundIndex;
	}

	public void setRoundIndex(int roundIndex) {
		this.roundIndex = roundIndex;
	}

	public Stage getStage() {
		return this.stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public boolean isWin() {
		return this.win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public RandomBox getRandomBox() {
		return this.stage.randomBox;
	}

	public RecordUtil getRecordUtil() {
		return this.stage.recordUtil;
	}

	public ActionList getActionList() {
		return this.actionList;
	}

	public void setActionList(ActionList actionList) {
		this.actionList = actionList;
	}

	public LinkedBlockingQueue<Action> getInsertActions() {
		return this.insertActions;
	}

	public int getMaxRound() {
		return this.maxRound;
	}

	public void setMaxRound(int maxRound) {
		this.maxRound = maxRound;
	}
}
