package org.darcy.sanguo.monster;

import java.util.Arrays;
import java.util.List;

import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.attri.BtlCalc;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.Calc;

import sango.packet.PbWarrior;

public class Monster extends Unit {
	private MonsterTemplate tplt;

	public Monster(MonsterTemplate tplt) {
		super(null);
		this.tplt = tplt;
		this.type = 1;
		this.id = Calc.getTempId();
		this.level = tplt.level;
		this.name = tplt.name;
		this.attributes = ((Attributes) tplt.attributes.clone());
		this.attributes.setHp(this.attributes.get(7));
		List<Buff> bfs = tplt.getBuffs();
		if (bfs != null)
			for (Buff b : bfs) {
				b.setOwner(this);
				b.setCaster(this);
				this.buffs.addBuff(b);
			}
	}

	public String getName() {
		return this.tplt.name;
	}

	public int getTemplateId() {
		return this.tplt.id;
	}

	public int getAtkType() {
		return this.tplt.actType;
	}

	public Skill getfjSkill() {
		if (this.tmpFjSkill != null) {
			return this.tmpFjSkill;
		}
		return this.tplt.fjSkill;
	}

	public Skill getActionSkill() {
		if (getStates().hasState(0)) {
			return this.tplt.ptSkill;
		}
		if (this.attributes.get(0) >= 4) {
			return this.tplt.angrySkill;
		}
		return this.tplt.ptSkill;
	}

	public void refreshAttributes(boolean isSync) {
		this.attributes = ((Attributes) this.tplt.attributes.clone());
		this.attributes.baseAttris = Arrays.copyOf(this.attributes.attris, this.attributes.attris.length / 2);
		this.attributes.setHp(this.tplt.attributes.get(7));
		this.isAlive = true;
		List<Buff> bfs = this.tplt.getBuffs();
		if (bfs != null)
			for (Buff b : bfs) {
				b.setOwner(this);
				b.setCaster(this);
				this.buffs.addBuff(b);
			}
	}

	public PbWarrior.Monster.Builder genMonster() {
		PbWarrior.Monster.Builder builder = PbWarrior.Monster.newBuilder();
		builder.setTemplateId(this.tplt.id);
		return builder;
	}

	public int getCamp() {
		return this.tplt.camp;
	}

	public int getGender() {
		return this.tplt.gender;
	}

	public int getBtlCapa() {
		return BtlCalc.calc(this.attributes);
	}

	public void resetAngry() {
		this.attributes.attris[0] = this.tplt.attributes.attris[0];
	}
}
