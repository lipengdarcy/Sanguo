package org.darcy.sanguo.combat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.effect.Effect;
import org.darcy.sanguo.combat.record.RecordUtil;
import org.darcy.sanguo.combat.skill.Behavior;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;

import sango.packet.PbRecord;

public class CombatContent {
	private Unit src;
	private Unit target;
	private Behavior attack;
	private CbSkill cbSkill;
	public long tPhyDamageRate;
	public long tMagicDamageRate;
	public long tAngryInc;
	public long tAngryDec;
	public long tPhyHealRate;
	public long tMagicHealRate;
	public long tDamageInc;
	public long tDamageIncRate;
	public long tCureInc;
	public long tCureIncRate;
	public long attachParam = -1L;
	public long totalDamage = 0L;

	public boolean rstHit = false;
	public boolean rstCrit = false;
	public boolean rstBlock = false;

	public boolean noRecord = false;

	public AtkType atkType = AtkType.NONE;

	public void clear() {
		this.tPhyDamageRate = 0L;
		this.tMagicDamageRate = 0L;
		this.tAngryInc = 0L;
		this.tAngryDec = 0L;
		this.tPhyHealRate = 0L;
		this.tMagicHealRate = 0L;
		this.rstCrit = false;
		this.rstBlock = false;
		this.rstHit = false;
		this.atkType = AtkType.NONE;
		this.tDamageInc = 0L;
		this.tDamageIncRate = 0L;
		this.attachParam = -1L;
		this.totalDamage = 0L;
		this.tCureInc = 0L;
		this.tCureIncRate = 0L;
		this.noRecord = false;
	}

	public CombatContent(Unit src, Unit target, Behavior attack, CbSkill cbSkill) {
		this.src = src;
		this.target = target;
		this.cbSkill = cbSkill;
		this.attack = attack;
		Platform.getLog().logCombat("CombatContent: " + src.getName() + " vs " + target.getName());
	}

	public void apply(Effect effect) {
		int hp;
		int left;
		int attachParam = -1;
		if ((this.atkType == AtkType.PHYSICATK) || (this.atkType == AtkType.MAGICATK)) {
			int finalDamage = damageCalc();
			this.totalDamage = finalDamage;
			hp = this.target.getAttributes().getHp();
			left = hp - finalDamage;
			if (left < 0) {
				left = 0;
			}
			this.target.getAttributes().setHp(left);
			Platform.getLog().logCombat(
					"CombatContent: " + this.target.getName() + " hp:" + this.target.getAttributes().getHp());
			if (this.target.getAttributes().getHp() == 0) {
				this.cbSkill.cbtCondition[5] = true;
				this.cbSkill.zeroHpTargets.add(this.target);
			}
			attachParam = finalDamage;
			this.cbSkill.cbtCondition[6] = true;
			this.cbSkill.damageTargets.add(this.target);
		} else if ((this.atkType == AtkType.PHYSICCURE) || (this.atkType == AtkType.MAGICCURE)) {
			if (this.target.getStates().hasState(1)) {
				return;
			}
			int cure = cureCalc();
			hp = this.target.getAttributes().getHp();
			left = hp + cure;
			if (left > this.target.getAttributes().get(7)) {
				left = this.target.getAttributes().get(7);
			}
			this.target.getAttributes().setHp(left);
			attachParam = cure;
			this.cbSkill.cbtCondition[7] = true;
			this.cbSkill.cureTargets.add(this.target);
		} else if ((this.atkType == AtkType.DECANGRY) || (this.atkType == AtkType.INCANGRY)) {
			Platform.getLog().logCombat(this.target.getName() + " 加怒 " + (this.atkType == AtkType.INCANGRY));
			Platform.getLog().logCombat(this.target.getName() + " 未封怒 " + (!(this.target.getStates().hasState(2))));
			if ((this.atkType == AtkType.INCANGRY) && (this.target.getStates().hasState(2))) {
				this.tAngryInc = 0L;
			}
			int angry = this.target.getAttributes().get(0);
			angry = (int) (angry + this.tAngryInc);
			angry = (int) (angry - this.tAngryDec);
			if (angry < 0) {
				angry = 0;
			}
			this.target.getAttributes().set(0, angry);
			Platform.getLog()
					.logCombat("CombatContent: "
							+ this.cbSkill.getAction().getSection()
									.getTeamType(this.cbSkill.getAction().getSection().getTargetTeam(this.target))
							+ this.target.getName() + " angry:" + this.target.getAttributes().get(0));
			attachParam = (int) (this.tAngryInc - this.tAngryDec);
			if (this.atkType == AtkType.DECANGRY) {
				this.cbSkill.cbtCondition[9] = true;
				this.cbSkill.decAngryTargets.add(this.target);
			} else {
				this.cbSkill.cbtCondition[8] = true;
				this.cbSkill.incAngryTargets.add(this.target);
			}
			attachParam = (int) (this.tAngryInc - this.tAngryDec);
		} else if (this.atkType == AtkType.ADDBUFF) {
			return;
		}

		if (this.noRecord) {
			return;
		}

		PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
				.setActor(getRecordUtil().getActor(this.target, this.cbSkill.getAction().getSection()))
				.setType(PbRecord.ActionRecord.RecordType.EFFECT)
				.setEffectRecord(PbRecord.EffectRecord.newBuilder().setAnimationId(effect.getEffectId())
						.addAttachParam(attachParam).setBlock(this.rstBlock).setEffectType(effect.getEffectType())
						.setCrit(this.rstCrit).setEffectId(effect.getId()));
		getRecordUtil().addRecord(r);
	}

	private int cureCalc() {
		int phyCure = 0;
		int magicCure = 0;
		if (this.tPhyHealRate > 0L) {
			phyCure = (int) ((this.src.getAttributes().get(6) + this.src.getAttributes().get(4)) * this.tPhyHealRate
					/ 100L + this.src.getAttributes().get(19) + this.target.getAttributes().get(20));
			Platform.getLog().logCombat(" CombatContent: cure rate " + this.tPhyHealRate);
			Platform.getLog()
					.logCombat(this.src.getName() + " CombatContent: cure " + this.src.getAttributes().get(19));
			Platform.getLog()
					.logCombat(this.target.getName() + " CombatContent: def " + this.target.getAttributes().get(20));
		}
		if (this.tMagicHealRate > 0L) {
			magicCure = (int) ((this.src.getAttributes().get(6) + this.src.getAttributes().get(5)) * this.tMagicHealRate
					/ 100L + this.src.getAttributes().get(19) + this.target.getAttributes().get(20));
			Platform.getLog().logCombat(
					" CombatContent: cure atk " + (this.src.getAttributes().get(6) + this.src.getAttributes().get(5)));
			Platform.getLog().logCombat(" CombatContent: cure rate " + this.tMagicHealRate);
			Platform.getLog()
					.logCombat(this.src.getName() + " CombatContent: cure " + this.src.getAttributes().get(19));
			Platform.getLog()
					.logCombat(this.target.getName() + " CombatContent: def " + this.target.getAttributes().get(20));
		}

		int cure = phyCure + magicCure;
		Platform.getLog().logCombat(this.src.getName() + " CombatContent: cure " + cure);

		cure = (int) (this.tCureInc + cure * (10000L + this.tCureIncRate) / 10000L);
		Platform.getLog().logCombat(this.src.getName() + " 治疗修正 " + cure);

		cure = (int) (cure * (10000L + this.target.getAttributes().get(27)) / 10000L);
		Platform.getLog().logCombat(this.src.getName() + " 治疗效率修正 " + cure);
		if (cure < 0)
			cure = 0;
		Platform.getLog().logCombat("CombatContent: cure : " + cure);
		return cure;
	}

	private int damageCalc() {
		int basePhyDamage = 0;
		if (this.atkType == AtkType.PHYSICATK) {
			basePhyDamage = (int) ((this.src.getAttributes().get(6) + this.src.getAttributes().get(4))
					* this.tPhyDamageRate / 100L - this.target.getAttributes().get(8));
			Platform.getLog().logCombat(this.src.getName() + " CombatContent: atk "
					+ (this.src.getAttributes().get(6) + this.src.getAttributes().get(4)));
			Platform.getLog()
					.logCombat(this.target.getName() + " CombatContent: def " + this.target.getAttributes().get(8));

			Platform.getLog().logCombat("CombatContent: rate " + this.tPhyDamageRate);
			Platform.getLog().logCombat("CombatContent: phy damage " + basePhyDamage);
		}
		int baseMagicDamage = 0;
		if (this.atkType == AtkType.MAGICATK) {
			baseMagicDamage = (int) ((this.src.getAttributes().get(6) + this.src.getAttributes().get(5))
					* this.tMagicDamageRate / 100L - this.target.getAttributes().get(9));
			Platform.getLog().logCombat("CombatContent: rate " + this.tMagicDamageRate);
			Platform.getLog().logCombat("CombatContent: magic damage " + baseMagicDamage);
		}
		int baseDamage = basePhyDamage + baseMagicDamage;
		if (baseDamage < 0) {
			Platform.getLog().logCombat("CombatContent: baseDamage = 1");
			baseDamage = 0;
		}

		if (this.rstCrit) {
			baseDamage *= this.src.getAttributes().get(11);
			baseDamage /= 100;
			Platform.getLog().logCombat("CombatContent: 暴击: " + baseDamage);
			this.cbSkill.cbtCondition[2] = true;
			this.cbSkill.critTargets.add(this.target);
		}

		if (this.rstBlock) {
			baseDamage /= 2;
			this.cbSkill.addBlockTargets(this.target);
			Platform.getLog().logCombat("CombatContent: 格挡 : " + baseDamage);
		}

		if (this.atkType == AtkType.PHYSICATK)
			baseDamage = (int) (baseDamage * (10000L - this.target.getAttributes().get(25)) / 10000L);
		else if (this.atkType == AtkType.MAGICATK) {
			baseDamage = (int) (baseDamage * (10000L - this.target.getAttributes().get(26)) / 10000L);
		}
		Platform.getLog().logCombat("CombatContent: 免伤修正 : " + baseDamage);

		int finalDamage = 0;
		finalDamage = baseDamage + this.src.getAttributes().get(17) - this.target.getAttributes().get(18);

		if (finalDamage < 1) {
			finalDamage = 1;
		}
		Platform.getLog().logCombat("CombatContent: 最终伤害 : " + finalDamage);

		finalDamage = (int) (this.tDamageInc + (int) (finalDamage * (10000L + this.tDamageIncRate) / 10000L));
		Platform.getLog().logCombat("CombatContent: 伤害加成buff修正 : " + finalDamage);

		Attributes a = this.target.getAttributes();
		finalDamage = a.buffFinalDamageModify(finalDamage);
		Platform.getLog().logCombat("CombatContent: 最终伤害buff修正 : " + finalDamage);

		return finalDamage;
	}

	public void combat() {
		if (calcHit()) {
			Platform.getLog().logCombat("CombatContent: hit");
			for (Effect effect : this.attack.getEffects()) {
				if (!(this.target.isAlive()))
					continue;
				if (!(this.target.getStates().canEffect(effect.getCatagory()))) {
					continue;
				}
				clear();
				this.atkType = AtkType.valueOf(effect.getId());
				if ((this.atkType == AtkType.PHYSICATK) || (this.atkType == AtkType.MAGICATK)) {
					this.rstCrit = calcCrit();
					this.rstBlock = calcBlock();
				}
				effect.effectCombat(this.src, this.target, this);
				this.src.getBuffs().effectBuff(8, this);
				this.target.getBuffs().effectBuff(13, this);
				if (this.target.isAlive()) {
					Platform.getLog().logCombat("effect combat: " + effect.getDescription());
					apply(effect);
				}
				this.src.getBuffs().effectBuff(9, this);
				this.target.getBuffs().effectBuff(14, this);
			}

			this.rstHit = true;
			this.cbSkill.hitTargets.add(this.target);
		} else {
			Platform.getLog().logCombat("CombatContent: miss");
			this.rstHit = false;

			PbRecord.ActionRecord.Builder r = PbRecord.ActionRecord.newBuilder().setIndex(0)
					.setActor(getRecordUtil().getActor(this.target, this.cbSkill.getAction().getSection()))
					.setType(PbRecord.ActionRecord.RecordType.UNBEAT)
					.setUnBeatRecord(PbRecord.UnBeatRecord.newBuilder().setHit(false));

			getRecordUtil().addRecord(r);
			this.cbSkill.missTargets.add(this.target);
		}
	}

	public boolean calcHit() {
		Platform.getLog().logCombat("CombatContent: 计算命中");
		if (this.attack.isbSureHit()) {
			getRandomBox().getNextRandom();
			return true;
		}
		int hitRate = this.src.getAttributes().get(14) - this.target.getAttributes().get(13);
		int random = getRandomBox().getNextRandom();
		return (random < hitRate);
	}

	public boolean calcCrit() {
		Platform.getLog().logCombat("CombatContent: 计算暴击");
		int critRate = this.src.getAttributes().get(10) - this.target.getAttributes().get(12);
		Platform.getLog().logCombat(this.src.getName() + " 暴击率 " + this.src.getAttributes().get(10));
		Platform.getLog().logCombat(this.target.getName() + " 抗暴率 " + this.target.getAttributes().get(12));
		Platform.getLog().logCombat("暴率 " + critRate);

		int random = getRandomBox().getNextRandom();
		return (random < critRate);
	}

	public boolean calcBlock() {
		Platform.getLog().logCombat("CombatContent: 计算格挡");
		int blockRate = this.target.getAttributes().get(15) - this.src.getAttributes().get(16);
		Platform.getLog().logCombat("受击方格挡率：" + this.target.getAttributes().get(15));
		Platform.getLog().logCombat("攻击方抗格挡：" + this.src.getAttributes().get(16));
		int random = getRandomBox().getNextRandom();
		return (random < blockRate);
	}

	public RandomBox getRandomBox() {
		return this.cbSkill.getRandomBox();
	}

	public RecordUtil getRecordUtil() {
		return this.cbSkill.getRecordUtil();
	}

	public Unit getSrc() {
		return this.src;
	}

	public CbSkill getCbSkill() {
		return this.cbSkill;
	}

	public void setCbSkill(CbSkill cbSkill) {
		this.cbSkill = cbSkill;
	}

	public Unit getTarget() {
		return this.target;
	}

	public static enum AtkType {
		NONE, PHYSICATK, MAGICATK, INCANGRY, DECANGRY, ADDBUFF, PHYSICCURE, MAGICCURE;

		public static AtkType valueOf(int value) {
			switch (value) {
			case 0:
				return NONE;
			case 1:
				return PHYSICATK;
			case 2:
				return MAGICATK;
			case 3:
				return INCANGRY;
			case 4:
				return DECANGRY;
			case 5:
				return ADDBUFF;
			case 6:
				return PHYSICCURE;
			case 7:
				return MAGICCURE;
			}
			return NONE;
		}
	}
}
