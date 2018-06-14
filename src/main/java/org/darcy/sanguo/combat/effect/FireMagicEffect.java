package org.darcy.sanguo.combat.effect;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Action;
import org.darcy.sanguo.combat.CbSkill;
import org.darcy.sanguo.combat.CombatContent;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;

public class FireMagicEffect extends Effect {
	private int minRate;
	private int maxRate;

	public FireMagicEffect(int id, int type, String description, int paramCount, int catagory) {
		super(id, type, description, paramCount, catagory);
	}

	public Effect copy() {
		FireMagicEffect e = new FireMagicEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
		e.effectId = this.effectId;
		e.minRate = this.minRate;
		e.maxRate = this.maxRate;
		return e;
	}

	public void initParams() {
		this.effectId = Integer.parseInt(this.params[0]);
		this.minRate = Integer.parseInt(this.params[1]);
		this.maxRate = Integer.parseInt(this.params[2]);
	}

	public int[] effectBuff(Unit owner, Unit caster, Action action) {
		return effectBuff(owner, caster, action.getSection());
	}

	public int[] effectBuff(Unit owner, Unit caster, CbSkill cbSkill) {
		return effectBuff(owner, caster, cbSkill.getAction());
	}

	public int[] effectBuff(Unit owner, Unit caster, CombatContent combatContent) {
		return effectBuff(owner, caster, combatContent.getCbSkill());
	}

	public int[] effectBuff(Unit owner, Unit caster, Section section) {
		int atk = caster.getAttributes().get(6) + caster.getAttributes().get(5);
		Platform.getLog().logCombat("Effect: " + this.description);
		int ran = section.getRandomBox().getNextRandom();
		int mod = this.maxRate - this.minRate + 1;
		int value = ran % mod + this.minRate;
		Platform.getLog().logCombat("rate " + value);
		Platform.getLog().logCombat("atk " + atk);
		Platform.getLog().logCombat("fd " + caster.getAttributes().get(23));
		Platform.getLog().logCombat("fddec " + owner.getAttributes().get(24));
		int damage = atk * value / 100 + caster.getAttributes().get(23) - owner.getAttributes().get(24);
		Platform.getLog().logCombat("damage " + damage);
		int finalDamage = owner.getAttributes().buffFinalDamageModify(damage);
		int hp = owner.getAttributes().getHp();
		int left = hp - finalDamage;
		Platform.getLog().logCombat("effect: fire damage " + finalDamage);
		if (left <= 0) {
			left = 0;
		}
		owner.getAttributes().setHp(left);
		if (!(owner.isAlive())) {
			section.processDie(owner);
		}
		Platform.getLog().logCombat("effect: " + owner.getName() + " hp:" + owner.getAttributes().getHp());
		this.effectType = 2;
		return new int[] { 1990, finalDamage };
	}
}
