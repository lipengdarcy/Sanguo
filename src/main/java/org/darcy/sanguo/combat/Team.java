package org.darcy.sanguo.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.unit.Unit;

public class Team {
	private Stage stage;
	private Unit[] units = new Unit[6];
	private int btlCapa;
	private int index = 0;

	public Team(Stage stage) {
		this.stage = stage;
	}

	public Team() {
	}

	public Unit[] getUnits() {
		return this.units;
	}

	public void setUnits(Unit[] units) {
		this.units = units;
		refreshBtlCapa();
	}

	private void refreshBtlCapa() {
		int rst = 0;
		for (Unit u : this.units) {
			if (u != null) {
				rst += u.getBtlCapa();
			}
		}
		this.btlCapa = rst;
	}

	public boolean hasUnit(Unit u) {
		for (Unit un : this.units) {
			if ((un != null) && (un == u)) {
				return true;
			}
		}

		return false;
	}

	public void checkBuffRound(Section section) {
		for (Unit u : this.units)
			if ((u != null) && (u.isAlive()))
				u.getBuffs().checkRound(section);
	}

	public void effectBuff(int triggerPoint, Round round) {
		for (Unit u : this.units)
			if ((u != null) && (u.isAlive()))
				u.getBuffs().effectBuff(triggerPoint, round);
	}

	public void effectBuff(int triggerPoint, Section section) {
		for (Unit u : this.units)
			if ((u != null) && (u.isAlive()))
				u.getBuffs().effectBuff(triggerPoint, section);
	}

	public int countUnit() {
		int rst = 0;
		for (Unit u : this.units) {
			if (u == null)
				continue;
			++rst;
		}
		return rst;
	}

	public int countAliveUnit() {
		int rst = 0;
		for (Unit u : this.units) {
			if ((u == null) || (!(u.isAlive())))
				continue;
			++rst;
		}
		return rst;
	}

	public boolean hasAlive() {
		for (Unit u : this.units) {
			if ((u != null) && (u.isAlive())) {
				return true;
			}
		}

		return false;
	}

	public void clearTmpBuffs() {
		for (Unit u : this.units)
			if (u != null)
				u.getBuffs().clearTmpBuff();
	}

	public void rest(Unit.RestType type) {
		for (Unit u : this.units)
			if (u != null)
				u.rest(type);
	}

	public static List<Unit> generateBossActionList(Team offen, Team defen) {
		List rst = new ArrayList();
		Unit boss = null;

		for (Unit u : defen.getUnits()) {
			if (u != null) {
				boss = u;
				break;
			}
		}

		Unit u = null;
		offen.index = 0;
		rst.add(offen.getaAliveUnit());
		rst.add(boss);
		while ((u = offen.getaAliveUnit()) != null) {
			rst.add(u);
			Unit w = offen.getaAliveUnit();
			if (w != null) {
				rst.add(w);
				rst.add(boss);
			}
		}

		return rst;
	}

	public static List<Unit> generateActionList(Team offen, Team defen) {
		int count = offen.countAliveUnit() + defen.countAliveUnit();
		List rst = new ArrayList(count);
		Team first = offen;
		Team second = defen;
		if (offen.btlCapa < defen.btlCapa) {
			first = defen;
			second = offen;
		}

		first.index = 0;
		second.index = 0;

		for (int i = 0; i < count;) {
			Unit uf = first.getaAliveUnit();
			if (uf != null) {
				rst.add(i, uf);
				++i;
			}
			Unit us = second.getaAliveUnit();
			if (us != null) {
				rst.add(i, us);
				++i;
			}
		}
		return rst;
	}

	public Unit getaAliveUnit() {
		while (this.index < this.units.length) {
			Unit u = this.units[(this.index++)];
			if ((u != null) && (u.isAlive())) {
				return u;
			}
		}

		return null;
	}

	public Unit getUnit(int index) {
		return this.units[index];
	}

	public Unit getUnitById(int id) {
		for (Unit u : this.units) {
			if ((u != null) && (u.getId() == id)) {
				return u;
			}
		}
		return null;
	}

	public int getTargetPoint(Team src, Team targetTeam, Unit atker, Unit damageSrc, int atkTarget) {
		int startIndex = -1;
		int atkerIndex = src.getIndex(atker);

		switch (atkTarget) {
		case 0:
			startIndex = atkerIndex % 3;
			break;
		case 1:
			startIndex = atkerIndex % 3 + 3;
			break;
		case 2:
			startIndex = atkerIndex;
			break;
		case 3:
			startIndex = targetTeam.getIndex(damageSrc);
			break;
		case 4:
			startIndex = atkerIndex % 3;
			break;
		case 5:
			startIndex = atkerIndex % 3 + 3;
			break;
		case 6:
			Platform.getLog().logCombat("Team 获取随机原点");
			int ran = this.stage.getRandomBox().getNextRandom();
			startIndex = ran % 6;
		}

		Unit start = targetTeam.units[startIndex];
		if ((start != null) && (start.isAlive())) {
			return startIndex;
		}
		int[] list = targetTeam.generateSearchList(startIndex);
		for (int i : list) {
			Unit u = targetTeam.units[i];
			if ((u != null) && (u.isAlive())) {
				return i;
			}

		}

		return -1;
	}

	public List<Unit> getTargets(int targetPoint, int range, Unit actor, boolean includeSelf) {
		int[] indexs;
		Unit localUnit2;
		Object localObject;
		List<Unit> list = new ArrayList<Unit>();
		int len = this.units.length;
		switch (range) {
		case 4:
			for (Unit u : this.units) {
				if ((u == null) || (!(u.isAlive())) || ((!(includeSelf)) && (u.getId() == actor.getId())))
					continue;
				list.add(u);
			}
			break;
		case 2:
			int start = targetPoint / 3 * 3;
			for (int j = 0; j < 3; ++j) {
				Unit u = this.units[(start + j)];
				if ((u == null) || (!(u.isAlive())) || ((!(includeSelf)) && (u.getId() == actor.getId())))
					continue;
				list.add(u);
			}

			break;
		case 3:
			indexs = null;
			switch (targetPoint) {
			case 0:
				indexs = new int[] { 0, 1, 3 };
				break;
			case 1:
				indexs = new int[] { 0, 1, 2, 4 };
				break;
			case 2:
				indexs = new int[] { 1, 2, 5 };
				break;
			case 3:
				indexs = new int[] { 0, 3, 4 };
				break;
			case 4:
				indexs = new int[] { 1, 3, 4, 5 };
				break;
			case 5:
				indexs = new int[] { 2, 4, 5 };
				break;
			default:
				indexs = new int[] { targetPoint };
			}
			for (int i : indexs) {
				if ((i < 6) && (i >= 0)) {
					Unit u = this.units[i];
					if ((u == null) || (!(u.isAlive())) || ((!(includeSelf)) && (u.getId() == actor.getId())))
						continue;
					list.add(u);
				}

			}

			break;
		case 9:
			Unit maxHp = null;
			for (int i = 0; i < len; i++) {
				Unit u = this.units[i];
				if (maxHp == null) {
					if ((includeSelf) || (u.getId() != actor.getId()))
						maxHp = u;
				} else {
					if ((u == null) || (!(u.isAlive())) || (maxHp.getAttributes().getHp() >= u.getAttributes().getHp())
							|| ((!(includeSelf)) && (u.getId() == actor.getId())))
						continue;
					maxHp = u;
				}

			}

			if (maxHp != null) {
				list.add(maxHp);
			}
			break;
		case 8:
			Unit minHp = null;
			for (int i = 0; i < len; i++) {
				Unit u = this.units[i];
				if ((u != null) && (minHp == null) && (u.isAlive())) {
					if ((includeSelf) || (u.getId() != actor.getId()))
						minHp = u;
				} else {
					if ((u == null) || (!(u.isAlive())) || (minHp.getAttributes().getHp() <= u.getAttributes().getHp())
							|| ((!(includeSelf)) && (u.getId() == actor.getId())))
						continue;
					minHp = u;
				}

			}

			if (minHp != null) {
				list.add(minHp);
			}
			break;
		case 5:
			list = getRandomUnits(1, actor, includeSelf);
			break;
		case 6:
			list = getRandomUnits(2, actor, includeSelf);
			break;
		case 7:
			list = getRandomUnits(3, actor, includeSelf);
			break;
		case 1:
			indexs = new int[] { targetPoint, targetPoint - 3, targetPoint + 3 };
			for (int i = 0; i < len; i++) {
				Unit u = this.units[i];
				if ((i < 6) && (i >= 0)) {
					u = this.units[i];
					if ((u == null) || (!(u.isAlive())) || ((!(includeSelf)) && (u.getId() == actor.getId())))
						continue;
					list.add(u);
				}

			}

			break;
		case 0:
			if ((targetPoint >= 0) && (targetPoint < this.units.length)) {
				list.add(this.units[targetPoint]);
			}
			break;
		case 10:
			Unit minHpRate = null;
			for (int i = 0; i < len; i++) {
				Unit u = this.units[i];
				if ((u != null) && (u.isAlive())) {
					Platform.getLog()
							.logCombat(u.getName() + "  " + u.getAttributes().getHp() + "/" + u.getAttributes().get(7));
				}
				if ((u != null) && (minHpRate == null) && (u.isAlive())) {
					if ((includeSelf) || (u.getId() != actor.getId()))
						minHpRate = u;
				} else {
					if ((u == null) || (!(u.isAlive())) || (minHpRate.getAttributes().getHp()
							* u.getAttributes().get(7) <= u.getAttributes().getHp() * minHpRate.getAttributes().get(7))
							|| ((!(includeSelf)) && (u.getId() == actor.getId())))
						continue;
					minHpRate = u;
				}

			}

			if (minHpRate != null) {
				list.add(minHpRate);
				Platform.getLog().logCombat("==min===" + minHpRate.getName());
			}

		}

		if (!(includeSelf)) {
			list.remove(actor);
		}

		Collections.sort(list, new Comparator<Unit>() {
			public int compare(Unit u1, Unit u2) {
				return (Team.this.getIndex(u1) - Team.this.getIndex(u2));
			}
		});
		return ((List<Unit>) list);
	}

	private List<Unit> getRandomUnits(int count, Unit actor, boolean includeSelf) {
		List list = new ArrayList();
		List temp = new ArrayList();
		for (Unit u : this.units) {
			if ((u == null) || (!(u.isAlive())) || ((!(includeSelf)) && (u.getId() == actor.getId())))
				continue;
			temp.add(u);
		}

		for (int i = 0; i < count; ++i) {
			if (temp.size() == 0) {
				break;
			}
			Platform.getLog().logCombat("Team 获取随机玩家");
			int r = this.stage.getRandomBox().getNextRandom();
			r %= temp.size();
			Unit u = (Unit) temp.get(r);
			list.add(u);
			temp.remove(r);
		}

		return list;
	}

	private int[] generateSearchList(int startIndex) {
		int[] list = new int[this.units.length + 1];
		int tmp = startIndex;
		startIndex = startIndex / 3 * 3;
		for (int i = 0; i < 3; ++i) {
			list[i] = (startIndex++);
		}
		if (startIndex <= 3) {
			startIndex = 3;
			list[3] = (tmp + 3);
		} else {
			startIndex = 0;
			list[3] = (tmp - 3);
		}
		for (int i = 3; i < 6; ++i) {
			list[(i + 1)] = (startIndex++);
		}

		return list;
	}

	public Team getTargetTeam(Team src, Team target, int atkTarget) {
		switch (atkTarget) {
		case 0:
		case 1:
			return target;
		case 2:
		case 4:
		case 5:
			return src;
		case 3:
			return target;
		case 6:
			Platform.getLog().logCombat("Team 获取随机队伍");
			int ran = this.stage.getRandomBox().getNextRandom();
			if (ran % 2 == 0) {
				return src;
			}
			return target;
		}

		return null;
	}

	public Stage getStage() {
		return this.stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public int getIndex(Unit unit) {
		for (int i = 0; i < this.units.length; ++i) {
			Unit u = this.units[i];
			if ((u != null) && (u.getType() == unit.getType()) && (u.getId() == unit.getId())) {
				return i;
			}
		}

		return -1;
	}

	public int getBtlCapa() {
		return this.btlCapa;
	}

	public void setBtlCapa(int btlCapa) {
		this.btlCapa = btlCapa;
	}

	public String toString() {
		String rst = "";
		for (Unit u : this.units) {
			if (u != null)
				rst = rst + " " + u.getName();
			else {
				rst = rst + " null";
			}
		}
		return rst;
	}

	public static String toString(Unit[] us) {
		String rst = "";
		for (Unit u : us) {
			if (u != null)
				rst = rst + " " + u.getName();
			else {
				rst = rst + " null";
			}
		}
		return rst;
	}

	public static String toString(List<Unit> us) {
		String rst = "";
		for (Unit u : us) {
			if (u != null)
				rst = rst + " " + u.getName();
			else {
				rst = rst + " null";
			}
		}
		return rst;
	}
}
