package org.darcy.sanguo.combat;

import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.unit.Unit;

public class ActionList {
	Team curr;
	Team first;
	Team second;
	private MainWarrior firstMain;
	private MainWarrior secondMain;
	private int firstIndex;
	private int secondIndex;

	public MainWarrior getFirstMain() {
		if ((this.firstMain != null) && (this.firstMain.isAlive())) {
			return this.firstMain;
		}
		return null;
	}

	public MainWarrior getSecondMain() {
		if ((this.secondMain != null) && (this.secondMain.isAlive())) {
			return this.secondMain;
		}
		return null;
	}

	public int getFirstIndex() {
		return this.firstIndex;
	}

	public void init(Team offen, Team deffen) {
		if (this.first == null) {
			if (offen.getBtlCapa() < deffen.getBtlCapa()) {
				this.first = deffen;
				this.second = offen;
			} else {
				this.first = offen;
				this.second = deffen;
			}
		} else if (offen == this.first)
			this.second = deffen;
		else {
			this.first = deffen;
		}

		for (Unit u : this.first.getUnits()) {
			if (u instanceof MainWarrior) {
				this.firstMain = ((MainWarrior) u);
			}
		}
		for (Unit u : this.second.getUnits())
			if (u instanceof MainWarrior)
				this.secondMain = ((MainWarrior) u);
	}

	public void refresh() {
		this.firstIndex = 0;
		this.secondIndex = 0;
		this.curr = this.first;
	}

	public boolean isRoundFinished() {
		return ((this.firstIndex < this.first.getUnits().length) || (this.secondIndex < this.second.getUnits().length));
	}

	private Unit innerGet() {
		if (this.curr == this.first) {
			for (; this.firstIndex < this.curr.getUnits().length; this.firstIndex += 1)
				if ((this.curr.getUnit(this.firstIndex) != null) && (this.curr.getUnit(this.firstIndex).isAlive()))
					return this.curr.getUnit(this.firstIndex++);
		} else {
			do {
				if ((this.curr.getUnit(this.secondIndex) != null) && (this.curr.getUnit(this.secondIndex).isAlive()))
					return this.curr.getUnit(this.secondIndex++);
				this.secondIndex += 1;
			} while (this.secondIndex < this.curr.getUnits().length);
		}

		return null;
	}

	public Unit getActor() {
		Unit u = innerGet();

		if (this.curr == this.first)
			this.curr = this.second;
		else {
			this.curr = this.first;
		}

		if (u == null) {
			u = innerGet();
		}

		return u;
	}
}
