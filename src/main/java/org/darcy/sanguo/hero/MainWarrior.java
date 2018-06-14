package org.darcy.sanguo.hero;

import java.util.List;

import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.en.En;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.startcatalog.StarcatalogRecord;

import sango.packet.PbWarrior;

public class MainWarrior extends Warrior {
	private LockSkill[] manualSkills;
	private boolean[] tmpUseRecord = new boolean[4];

	public MainWarrior(ItemTemplate template, int id) {
		super(template, id);
		this.isMainWarrior = true;
	}

	public void updateLevel(int level, boolean sync) {
		setLevel(level);
		refreshAttributes(sync);
	}

	public PbWarrior.Warrior.Builder genWarrior() {
		PbWarrior.Warrior.Builder builder = super.genWarrior();
		for (LockSkill ls : this.manualSkills) {
			builder.addManualSkillIds(ls.genPb());
		}
		builder.setCoup(-1);
		Player p = getPlayer();
		if (p != null) {
			builder.setTitleId(p.getTowerRecord().getTitleId());
		}
		List allEns = getAllEns();
		builder.addAllAllEns(allEns);
		return builder;
	}

	public void calAttributes(Attributes attr, int level, int advanceLevel) {
		attr.clear();
		Player p = getPlayer();
		Attributes star = null;
		if (p != null) {
			star = p.getStarRecord().getAttributes(this.template.id);
		}
		Attributes eventAttrAmount = null;
		Attributes favorAmount = null;
		if ((p != null) && (getStageStatus() == 1)) {
			StarcatalogRecord r = p.getStarcatalogRecord();
			eventAttrAmount = r.eventAttrAmount();
			favorAmount = r.attrAmount();
		}
		Attributes title = null;
		if ((getStageStatus() == 1) && (p != null)) {
			title = p.getTowerRecord().getTitleAttribute();
		}
		Attributes[] attrs = { attr, getHeroAttributes(level, advanceLevel), this.equips.getTotalAttributes(),
				this.treasures.getTotalAttributes(), star, title, eventAttrAmount, favorAmount };

		Attributes.addAttr(attrs);
		if (getPlayer() != null) {
			Attributes.addAttr(new Attributes[] { attr, getPlayer().getDestinyRecord().getAttris() });
		}
		for (En en : this.ens) {
			attr.addEn(en);
		}
		this.buffs.effectAttribute(attr);
		attr.rateModify();
		attr.setHp(this.attributes.get(7));
		this.isAlive = true;
	}

	public LockSkill[] getManualSkills() {
		return this.manualSkills;
	}

	public void setManualSkills(LockSkill[] manualSkills) {
		this.manualSkills = manualSkills;
	}

	public void refreshSkillUseRecord() {
		for (int i = 0; i < this.tmpUseRecord.length; ++i)
			this.tmpUseRecord[i] = false;
	}

	public Skill getAutoSkill() {
		for (int i = 0; i < this.tmpUseRecord.length; ++i) {
			if (!this.tmpUseRecord[i] && this.manualSkills[i].isOpen()) {
				this.tmpUseRecord[i] = true;
				return this.manualSkills[i].getSkill();
			}
		}

		return null;
	}
}
