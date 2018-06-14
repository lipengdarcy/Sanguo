package org.darcy.sanguo.hero;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.coup.Coup;

import sango.packet.PbCommons;

public class LockSkill {
	private Skill skill;
	private int openLevel;
	private boolean open;
	private Coup coup;

	public LockSkill(int skillId, int openLevel, boolean open, Coup coup) {
		this.skill = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getSkill(skillId);
		this.openLevel = openLevel;
		this.open = open;
		this.coup = coup;
	}

	public Skill getSkill() {
		return this.skill;
	}

	public boolean isOpen() {
		return this.open;
	}

	public PbCommons.LockSkill.Builder genPb() {
		PbCommons.LockSkill.Builder lb = PbCommons.LockSkill.newBuilder();
		lb.setSkillId(this.skill.getId()).setUnLock(this.open).setBuffId(-1).setCoupId(this.coup.id)
				.setUnLockLevel(this.openLevel);
		return lb;
	}

	public int getOpenLevel() {
		return this.openLevel;
	}

	public Coup getCoup() {
		return this.coup;
	}
}
