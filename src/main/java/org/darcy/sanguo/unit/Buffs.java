package org.darcy.sanguo.unit;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Round;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.buff.Buff;

import sango.packet.PbRecord;

public class Buffs {
	private CopyOnWriteArrayList<Buff> buffs = new CopyOnWriteArrayList();

	public boolean hasBuff(int buffId) {
		for (Buff b : this.buffs) {
			if (b.getId() == buffId) {
				return true;
			}
		}

		return false;
	}

	public boolean hasBuffCatagory(int catagory) {
		for (Buff b : this.buffs) {
			if (b.getCatagory() == catagory) {
				return true;
			}
		}

		return false;
	}

	public List<Buff> getBuffs() {
		return this.buffs;
	}

	public boolean addBuff(Buff buff) {
		if (!(buff.getOwner().isAlive()))
			return false;
		Buff ub = getUniqBuff(buff.getUniqId());
		if (ub != null) {
			if (ub.getUniqLevel() <= buff.getUniqLevel()) {
				this.buffs.remove(ub);
				this.buffs.add(buff);
				Platform.getLog().logCombat("Add buff:" + buff.getName() + " replace:" + ub.getName());
				return true;
			}

			Platform.getLog().logCombat("Add buff:" + buff.getName() + " faild!");
			return false;
		}

		this.buffs.add(buff);
		Platform.getLog().logCombat(buff.getOwner().getName() + " Add buff:" + buff.getName());
		return true;
	}

	public boolean addBuff(Buff buff, Section section) {
		if (!(buff.getOwner().isAlive()))
			return false;
		if (!(buff.getOwner().getStates().canAddBuff(buff.getCatagory()))) {
			return false;
		}
		Buff ub = getUniqBuff(buff.getUniqId());
		if (ub != null) {
			if (ub.getUniqLevel() <= buff.getUniqLevel()) {
				removeBuff(ub, section);
				this.buffs.add(buff);
				addBuffRecord(buff, section);
				buff.added(section);
				return true;
			}

			return false;
		}

		this.buffs.add(buff);
		addBuffRecord(buff, section);
		buff.added(section);
		return true;
	}

	private Buff getUniqBuff(int uniqId) {
		if (uniqId == -1) {
			return null;
		}
		for (Buff b : this.buffs) {
			if (b.getUniqId() == uniqId) {
				return b;
			}
		}

		return null;
	}

	public void clearTmpBuff() {
		for (Buff buff : this.buffs)
			if ((buff.getType() == 2) || (buff.getType() == 0)) {
				Platform.getLog().logCombat(buff.getOwner().getName() + " clear coup buff: " + buff.getName());
				this.buffs.remove(buff);
				buff.removed();
			}
	}

	public void clearCoupBuff() {
		for (Buff buff : this.buffs)
			if (buff.getType() == 2) {
				Platform.getLog().logCombat(buff.getOwner().getName() + " clear coup buff: " + buff.getName());
				this.buffs.remove(buff);
				buff.removed();
			}
	}

	public void clearBtlBuff() {
		for (Buff buff : this.buffs)
			if (buff.getType() == 0) {
				Platform.getLog().logCombat(buff.getOwner().getName() + " clear btl buff: " + buff.getName());
				this.buffs.remove(buff);
				buff.removed();
			}
	}

	public void clearBuff() {
		for (Buff buff : this.buffs) {
			this.buffs.remove(buff);
			buff.removed();
		}
	}

	public void addBuffRecord(Buff buff, Section section) {
		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
				.setActor(section.getRecordUtil().getActor(buff.getOwner(), section))
				.setType(PbRecord.ActionRecord.RecordType.BUFFSTAT)
				.setBuffStatRecord(PbRecord.BuffStatRecord.newBuilder().setAnimationId(buff.getAnimation())
						.setType(PbRecord.BuffStatRecord.BuffStatType.ADD));

		section.getRecordUtil().addRecord(r);
	}

	public void removeBuff(Buff buff) {
		this.buffs.remove(buff);
	}

	public void removeBuff(int id) {
		Iterator itx = this.buffs.iterator();
		while (itx.hasNext()) {
			Buff b = (Buff) itx.next();
			if (b.getId() == id) {
				this.buffs.remove(b);
				return;
			}
		}
	}

	private void removeBuff(Buff buff, Section section) {
		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
				.setActor(section.getRecordUtil().getActor(buff.getOwner(), section))
				.setType(PbRecord.ActionRecord.RecordType.BUFFSTAT)
				.setBuffStatRecord(PbRecord.BuffStatRecord.newBuilder().setAnimationId(buff.getAnimation())
						.setType(PbRecord.BuffStatRecord.BuffStatType.REMOVE));

		section.getRecordUtil().addRecord(r);

		Platform.getLog().logCombat(buff.getOwner().getName() + " remove buff: " + buff.getName());
		this.buffs.remove(buff);
		buff.removed(section);
	}

	public void effectBuff(int triggerPoint, Action action) {
		for (Buff buff : getBuffs()) {
			boolean overdue = buff.effect(triggerPoint, action);
			if (overdue)
				removeBuff(buff, action.getSection());
		}
	}

	public void effectBuff(int triggerPoint, CbSkill cbSkill) {
		for (Buff buff : getBuffs()) {
			boolean overdue = buff.effect(triggerPoint, cbSkill);
			if (overdue)
				removeBuff(buff, cbSkill.getAction().getSection());
		}
	}

	public void effectBuff(int triggerPoint, Round round) {
		for (Buff buff : this.buffs) {
			boolean overdue = buff.effect(triggerPoint, round);
			if (overdue)
				removeBuff(buff, round.getSection());
		}
	}

	public void effectBuff(int triggerPoint, Section section) {
		for (Buff buff : this.buffs) {
			boolean overdue = buff.effect(triggerPoint, section);
			if (overdue)
				removeBuff(buff, section);
		}
	}

	public void effectAttribute(Attributes attri) {
		for (Buff b : this.buffs)
			b.effectAttribute(attri);
	}

	public void effectBuff(int triggerPoint, CombatContent combatContent) {
		for (Buff buff : this.buffs) {
			boolean overdue = buff.effect(triggerPoint, combatContent);
			if (overdue)
				removeBuff(buff, combatContent.getCbSkill().getAction().getSection());
		}
	}

	public void checkRound(Section section) {
		for (Buff buff : this.buffs) {
			boolean overdue = buff.checkOverDueRound();
			if (overdue)
				removeBuff(buff, section);
		}
	}
}
