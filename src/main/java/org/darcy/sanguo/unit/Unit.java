package org.darcy.sanguo.unit;

import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.combat.state.States;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.monster.Monster;

import sango.packet.PbWarrior;

public abstract class Unit extends Item {
	public static final int TYPE_PLAYER = 0;
	public static final int TYPE_MONSTER = 1;
	protected String name;
	protected int type;
	protected Buffs buffs = new Buffs();
	protected States states = new States();
	protected Attributes attributes = new Attributes();
	protected Skill tmpFjSkill;
	protected boolean isAlive;

	public Unit(ItemTemplate template) {
		super(template);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public int getType() {
		return this.type;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Attributes getAttributes() {
		return this.attributes;
	}

	public boolean canCastSkill(Skill skill) {
		return true;
	}

	public boolean isAlive() {
		return this.isAlive;
	}

	public boolean checkAlive() {
		if (this.attributes.getHp() <= 0) {
			this.isAlive = false;
			return false;
		}
		return true;
	}

	public Buffs getBuffs() {
		return this.buffs;
	}

	public void setBuffs(Buffs buffs) {
		this.buffs = buffs;
	}

	public States getStates() {
		return this.states;
	}

	public void setStates(States states) {
		this.states = states;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public int getPrice() {
		return this.template.price;
	}

	public void setPrice(int price) {
	}

	public void rest(RestType type) {
		if (type == RestType.SECTION) {
			if (isAlive()) {
				int killPoints = 0;
				killPoints = this.attributes.getKillPoints();
				this.buffs.clearBtlBuff();
				refreshAttributes(false);
				this.attributes.setKillPoints(killPoints);
			}
		} else {
			this.buffs.clearBtlBuff();
			refreshAttributes(false);
			this.attributes.setKillPoints(0);
		}
	}

	public void revive() {
		this.attributes.setHp(this.attributes.get(7));
	}

	public abstract int getAtkType();

	public abstract Skill getfjSkill();

	public abstract Skill getActionSkill();

	public abstract int getCamp();

	public abstract int getGender();

	public abstract int getBtlCapa();

	public abstract void refreshAttributes(boolean paramBoolean);

	public PbWarrior.Unit.Builder genUnit() {
		if (this.type == 1) {
			Monster monster = (Monster) this;
			return PbWarrior.Unit.newBuilder().setUnitType(PbWarrior.Unit.UnitType.MONSTER)
					.setMonster(monster.genMonster());
		}
		Warrior warrior = (Warrior) this;
		return PbWarrior.Unit.newBuilder().setUnitType(PbWarrior.Unit.UnitType.WARRIOR)
				.setWarrior(warrior.genWarrior());
	}

	public boolean canSell() {
		return false;
	}

	public Skill getTmpFjSkill() {
		return this.tmpFjSkill;
	}

	public void setTmpFjSkill(Skill tmpFjSkill) {
		this.tmpFjSkill = tmpFjSkill;
	}

	public static enum RestType {
		SECTION, STAGE;
	}
}
