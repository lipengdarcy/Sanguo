package org.darcy.sanguo.hero;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.attri.BtlCalc;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.coup.CoupEn;
import org.darcy.sanguo.coup.CoupRecord;
import org.darcy.sanguo.en.En;
import org.darcy.sanguo.en.EnService;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Equipments;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.item.Treasures;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.CoupService;
import org.darcy.sanguo.service.HeroService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.startcatalog.StarcatalogRecord;
import org.darcy.sanguo.talent.Talent;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbWarrior;

public class Warrior extends Unit {
	public static final int STAGE_STATUS_BAG = 0;
	public static final int STAGE_STATUS_ONSTAGE = 1;
	public static final int STAGE_STATUS_FELLOW = 2;
	public static final int MAX_LEVEL = 200;
	private int exp;
	private int advanceLevel;
	protected boolean isMainWarrior = false;

	private boolean ensUpdated = false;
	private boolean ensPlanB = false;

	protected Equipments equips = new Equipments();

	protected Treasures treasures = new Treasures();

	protected Set<En> ens = new HashSet();
	private WeakReference<Player> player;

	public Warrior(ItemTemplate template, int id) {
		super(template);
		this.id = id;
		this.name = template.name;
		refreshAttributes(true);
	}

	public void clear() {
		setExp(0);
		setLevel(1);
		setAdvanceLevel(0);
		this.ensPlanB = false;
		this.ensUpdated = false;
		refreshAttributes(true);
	}

	public void init(Player player) {
		this.player = new WeakReference(player);
		this.equips.init(player);
		this.treasures.init(player);
		refreshTalents();
		refreshAttributes(false);
	}

	public void refreshTalents() {
		Talent[] arrayOfTalent;
		this.buffs.clearBuff();
		Player player = getPlayer();
		if (player != null) {
			player.getTacticRecord().addBuff(this, player.getWarriors().getStandIndex(this));
			player.getGloryRecord().addBuff(this, player.getWarriors().getStandIndex(this));
		}
		if (((HeroTemplate) this.template).talents == null) {
			return;
		}
		int j = (arrayOfTalent = ((HeroTemplate) this.template).talents).length;
		for (int i = 0; i < j; ++i) {
			Buff buff;
			Talent t = arrayOfTalent[i];
			if (t.getUnlockType() == Talent.UnlockType.LEVEL) {
				if (this.level >= t.getLevel()) {
					buff = t.getBuff();
					buff.setCaster(this);
					buff.setOwner(this);
					this.buffs.addBuff(buff);
				}
			} else {
				if ((t.getUnlockType() != Talent.UnlockType.ADVANCE_LEVE) || (this.advanceLevel < t.getLevel()))
					continue;
				buff = t.getBuff();
				buff.setCaster(this);
				buff.setOwner(this);
				this.buffs.addBuff(buff);
			}
		}
	}

	public void addTacticBuff(int buffId) {
		removeTacticBuff(buffId);
		Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getBuff(buffId);
		if (buff != null) {
			buff = buff.copy();
			buff.setCaster(this);
			buff.setOwner(this);
			this.buffs.addBuff(buff);
		}
	}

	public void removeTacticBuff(int buffId) {
		this.buffs.removeBuff(buffId);
	}

	public void addGloryBuff(int buffId) {
		removeGloryBuff(buffId);
		Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getBuff(buffId);
		if (buff != null) {
			buff = buff.copy();
			buff.setCaster(this);
			buff.setOwner(this);
			this.buffs.addBuff(buff);
		}
	}

	public void removeGloryBuff(int buffId) {
		this.buffs.removeBuff(buffId);
	}

	public void addLeagueBossBuff(int buffId) {
		removeLeagueBossBuff(buffId);
		Buff buff = ((CombatService) Platform.getServiceManager().get(CombatService.class)).getBuff(buffId);
		if (buff != null) {
			buff = buff.copy();
			buff.setCaster(this);
			buff.setOwner(this);
			this.buffs.addBuff(buff);
		}
	}

	public void removeLeagueBossBuff(int buffId) {
		this.buffs.removeBuff(buffId);
	}

	private void refreshStarcatalog(Player p) {
		if (getStageStatus() == 1) {
			StarcatalogRecord r = p.getStarcatalogRecord();
			Attributes.addAttr(new Attributes[] { this.attributes, r.eventAttrAmount(), r.attrAmount() });
		}
	}

	public int getId() {
		return this.id;
	}

	public Equipments getEquips() {
		return this.equips;
	}

	public void setEquips(Equipments equips) {
		this.equips = equips;
	}

	public Treasures getTreasures() {
		return this.treasures;
	}

	public void setTreasures(Treasures treasures) {
		this.treasures = treasures;
	}

	public int getAtkType() {
		return ((HeroTemplate) this.template).atkType;
	}

	public Skill getfjSkill() {
		if (this.tmpFjSkill != null) {
			return this.tmpFjSkill;
		}
		return ((HeroTemplate) this.template).fjSkill;
	}

	public Skill getActionSkill() {
		if (getStates().hasState(0)) {
			return ((HeroTemplate) this.template).ptSkill;
		}
		if (this.attributes.get(0) >= 4) {
			return ((HeroTemplate) this.template).angrySkill;
		}
		return ((HeroTemplate) this.template).ptSkill;
	}

	public int getStageStatus() {
		Player player = getPlayer();
		if (player != null) {
			return player.getWarriors().getStageStatus(this.id);
		}
		return 0;
	}

	public PbWarrior.Warrior.Builder genWarrior() {
		PbWarrior.Warrior.Builder builder = PbWarrior.Warrior.newBuilder();
		builder.setId(getId());
		builder.setAttribute(this.attributes.genAttribute());
		builder.setName(getName());
		builder.setStageStatus(getStageStatus());
		builder.setTemplateId(getTemplateId());
		builder.setLevel(this.level);
		builder.setAptitude(((HeroTemplate) this.template).aptitude);
		builder.setIsMain(this.isMainWarrior);
		builder.setExp(this.exp);
		builder.setAdvanceLevel(this.advanceLevel);
		builder.setEnsUpdated(this.ensUpdated);
		builder.setEnsPlanB(this.ensPlanB);
		for (Buff b : this.buffs.getBuffs()) {
			if (b.getType() != 0) {
				builder.addActiveTalents(b.getId());
			}
		}
		if (this.ens != null) {
			for (En en : this.ens) {
				builder.addActiveEns(en.id);
			}
		}
		builder.setBtlcapability(getBtlCapa());
		return builder;
	}

	public PbWarrior.OthersWarrior genOthersWarrior(Player player) {
		PbWarrior.OthersWarrior.Builder builder = PbWarrior.OthersWarrior.newBuilder();
		builder.setWarrior(genWarrior());
		builder.setStageIndex(player.getWarriors().getStageIndex(this));
		builder.setStandIndex(player.getWarriors().getStandIndex(this));
		for (Equipment equip : this.equips.getEquips()) {
			if (equip != null) {
				builder.addEquips(equip.genEquipment());
			}
		}
		for (Treasure treasure : this.treasures.getTreasures()) {
			if (treasure != null) {
				builder.addTreasures(treasure.genTreasure());
			}
		}
		return ((PbWarrior.OthersWarrior) builder.build());
	}

	public void sync() {
		Player player = getPlayer();
		if (player != null) {
			BagGrid grid = player.getBags().getGrid(this.id, getTemplateId());
			if (grid != null)
				player.getDataSyncManager().addBagSync(2, grid, 0);
		}
	}

	public void refreshAttributes(boolean isSync) {
		calAttributes(this.attributes, this.level, this.advanceLevel);
		if (isSync) {
			if (getStageStatus() == 1) {
				Player player = getPlayer();
				if (player != null) {
					player.refreshBtlCapa();
				}
			}
			sync();
		}
	}

	public void calAttributes(Attributes attr, int level, int advanceLevel) {
		attr.clear();
		Player p = getPlayer();
		Attributes star = null;
		if (p != null) {
			star = p.getStarRecord().getAttributes(this.template.id);
		}
		Attributes train = null;
		Attributes eventAttrAmount = null;
		Attributes favorAmount = null;
		if ((p != null) && (getStageStatus() == 1)) {
			StarcatalogRecord r = p.getStarcatalogRecord();
			eventAttrAmount = r.eventAttrAmount();
			favorAmount = r.attrAmount();
		}
		if (p != null) {
			train = p.getStarRecord().getTrainAttribues(this.template.id);
		}
		Attributes title = null;
		if ((getStageStatus() == 1) && (p != null)) {
			title = p.getTowerRecord().getTitleAttribute();
		}
		Attributes[] attrs = { attr, getHeroAttributes(level, advanceLevel), this.equips.getTotalAttributes(),
				this.treasures.getTotalAttributes(), star, train, title, eventAttrAmount, favorAmount };

		Attributes.addAttr(attrs);
		for (En en : this.ens) {
			attr.addEn(en);
		}
		this.buffs.effectAttribute(attr);
		attr.rateModify();
		attr.setHp(this.attributes.get(7));
		this.isAlive = true;
	}

	protected Attributes getHeroAttributes(int level, int advanceLevel) {
		Attributes attr = new Attributes();
		getHeroInitAttribute(attr);
		getHeroCurAttirbute(attr, level, advanceLevel);
		return attr;
	}

	private void getHeroInitAttribute(Attributes attr) {
		Attributes.addAttr(new Attributes[] { attr, ((HeroTemplate) this.template).attr });

		int heroInitHp = ((HeroTemplate) this.template).attr.get(7);
		int heroInitPhyDef = ((HeroTemplate) this.template).attr.get(8);
		int heroInitMagDef = ((HeroTemplate) this.template).attr.get(9);
		int actualLeadShip = attr.get(1);
		int actualForce = attr.get(2);
		int actualWisdom = attr.get(3);

		int actualHp = (int) Math.floor(heroInitHp + (actualLeadShip + actualForce - 10000) * heroInitHp / 20000.0D);

		int actualPhyDef = (int) Math.floor(heroInitPhyDef + (actualForce - 5000) * heroInitPhyDef / 20000.0D);

		int actualMagDef = (int) Math.floor(heroInitMagDef + (actualWisdom - 5000) * heroInitMagDef / 20000.0D);

		attr.set(7, actualHp);
		attr.set(8, actualPhyDef);
		attr.set(9, actualMagDef);
	}

	private void getHeroCurAttirbute(Attributes attr, int level, int advanceLevel) {
		int tmpLevel = level;

		if (advanceLevel > 0) {
			int ruleId = ((Integer) ((HeroTemplate) this.template).advanceRule.get(Integer.valueOf(advanceLevel - 1)))
					.intValue();
			HeroAdvance advance = ((HeroService) Platform.getServiceManager().get(HeroService.class))
					.getHeroAdvance(ruleId);
			tmpLevel += advance.advanceValue;
		}

		int actualLeadShip = attr.get(1);
		int actualForce = attr.get(2);
		int actualWisdom = attr.get(3);

		int hpInitGrow = ((HeroTemplate) this.template).hpGrow;
		int atkInitGrow = ((HeroTemplate) this.template).attackGrow;
		int phyDefInitGrow = ((HeroTemplate) this.template).phyDefenceGrow;
		int magDefInitGrow = ((HeroTemplate) this.template).magDefenceGrow;

		int hpGrow = (int) Math.floor(hpInitGrow + (actualLeadShip + actualForce - 10000) * hpInitGrow / 20000.0D);
		int atkGrow = (int) Math.floor(atkInitGrow + (actualWisdom - 5000) * atkInitGrow / 20000.0D);
		int phyDefGrow = (int) Math.floor(phyDefInitGrow + (actualForce - 5000) * phyDefInitGrow / 20000.0D);
		int magDefGrow = (int) Math.floor(magDefInitGrow + (actualWisdom - 5000) * magDefInitGrow / 20000.0D);

		attr.set(7, (int) (attr.get(7) + hpGrow * (tmpLevel - 1) / 100.0D));
		attr.set(6, (int) (attr.get(6) + atkGrow * (tmpLevel - 1) / 100.0D));
		attr.set(8, (int) (attr.get(8) + phyDefGrow * (tmpLevel - 1) / 100.0D));
		attr.set(9, (int) (attr.get(9) + magDefGrow * (tmpLevel - 1) / 100.0D));
	}

	public void advance(int newTempalteId) {
		setAdvanceLevel(this.advanceLevel + 1);
		if ((newTempalteId != -1) && (newTempalteId != getTemplateId())) {
			this.template = ItemService.getItemTemplate(newTempalteId);
		}

		refreshTalents();
		refreshAttributes(true);
	}

	public void addExp(int addExp, int playerlevel) {
		int oldLevel = this.level;
		setExp(this.exp + addExp);
		while (true) {
			if (this.level >= 200) {
				this.exp = 0;
				break;
			}
			if (this.level >= playerlevel)
				break;
			int nextLevel = this.level + 1;
			int expRuleId = ((HeroTemplate) this.template).intensifyRule;
			int needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(expRuleId,
					nextLevel);
			if (this.exp < needExp)
				break;
			levelUp();
			setExp(this.exp - needExp);
		}

		if (this.level > oldLevel) {
			refreshTalents();
			refreshAttributes(true);
		}
	}

	private void levelUp() {
		setLevel(this.level + 1);
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}

	public List<Integer> getAllEns() {
		int[] enIds = ((HeroTemplate) getTemplate()).enIds;
		if ((this.ensPlanB) && (((HeroTemplate) getTemplate()).enIdsB != null)) {
			enIds = ((HeroTemplate) getTemplate()).enIdsB;
		}
		List list = new ArrayList();
		for (int id : enIds) {
			list.add(Integer.valueOf(id));
		}
		if (this.isMainWarrior) {
			list.clear();
			Player player = getPlayer();
			if (player != null) {
				CoupRecord cr = player.getCoupRecord();
				int[] coupLevels = cr.getLevels();
				for (int k = 1; k < 5; ++k) {
					int level = coupLevels[(k - 1)];
					List<CoupEn> ens = (List) CoupService.coupEns.get(Integer.valueOf(k));
					CoupEn en = null;
					for (CoupEn e : ens) {
						if (level >= e.coupLevel) {
							if (en == null)
								en = e;
							else if (e.coupLevel > en.coupLevel) {
								en = e;
							}
						}
					}

					if (en != null) {
						for (int i : en.enIds) {
							list.add(Integer.valueOf(i));
						}
					}
				}
			}
		}

		return list;
	}

	public void refreshkEns(Warrior[] wars) {
		this.ens.clear();
		List<Integer> enIds = getAllEns();
		for (Integer i : enIds) {
			En en = (En) EnService.enTemplates.get(i);
			if ((en == null) || (!(checkEn(en, wars))))
				continue;
			this.ens.add(en);
		}
	}

	private boolean checkEn(En en, Warrior[] wars) {
		int[] arrayOfInt;
		Set used = null;
		int j = (arrayOfInt = en.itemList).length;
		for (int i = 0; i < j; ++i) {
			Warrior[] arrayOfWarrior;
			int itemId = arrayOfInt[i];
			if (itemId > 0) {
				ItemTemplate tplt = ItemService.getItemTemplate(itemId);
				if (tplt != null) {
					boolean tmp;
					Object localObject;
					if (tplt.type == 2) {
						tmp = false;
						for (int k = 0; k < wars.length; ++k) {
							Warrior w = wars[k];
							if ((w != null) && (w.getTemplateId() == tplt.id)) {
								tmp = true;
								break;
							}
						}
						if (tmp)
							continue;
						return false;
					}
					if (tplt.type == 4) {
						tmp = false;
						for (int k = 0; k < this.equips.getEquips().length; ++k) {
							Equipment e = this.equips.getEquips()[k];
							if ((e != null) && (e.getTemplate().id == tplt.id)) {
								tmp = true;
								break;
							}
						}
						if (tmp)
							continue;
						return false;
					}
					if (tplt.type == 1) {
						tmp = false;
						for (int k = 0; k < this.treasures.getTreasures().length; ++k) {
							Treasure t = this.treasures.getTreasures()[k];
							if ((t != null) && (t.getTemplate().id == tplt.id)) {
								tmp = true;
								break;
							}
						}
						if (tmp)
							continue;
						return false;
					}
					return false;
				}
				return false;
			}

			if (used == null) {
				used = new HashSet();
				used.add(Integer.valueOf(this.id));
			}
			boolean tmp = false;
			for (int t = 0; t < wars.length; ++t) {
				Warrior w = wars[t];
				if ((!(used.contains(Integer.valueOf(w.getId())))) && (-itemId == w.getCamp())) {
					used.add(Integer.valueOf(w.getId()));
					tmp = true;
				}
			}

			if (!(tmp)) {
				return false;
			}
		}

		return true;
	}

	public int getExp() {
		return this.exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getAdvanceLevel() {
		return this.advanceLevel;
	}

	public void setAdvanceLevel(int advanceLevel) {
		this.advanceLevel = advanceLevel;
	}

	public boolean isMainWarrior() {
		return this.isMainWarrior;
	}

	public void setMainWarrior(boolean isMainWarrior) {
		this.isMainWarrior = isMainWarrior;
	}

	public int getCamp() {
		return ((HeroTemplate) this.template).camp;
	}

	public boolean isEnsUpdated() {
		return this.ensUpdated;
	}

	public boolean isEnsPlanB() {
		return this.ensPlanB;
	}

	public void setEnsUpdated(boolean ensUpdated) {
		this.ensUpdated = ensUpdated;
	}

	public void setEnsPlanB(boolean ensPlanB) {
		this.ensPlanB = ensPlanB;
	}

	public int getGender() {
		return ((HeroTemplate) this.template).gender;
	}

	public int getBtlCapa() {
		HeroTemplate heroTemplate = (HeroTemplate) this.template;
		Map map = (Map) HeroService.advanceCapaBonus.get(Integer.valueOf(heroTemplate.aptitude));
		if ((this.advanceLevel > 0) && (map != null) && (map.size() > 0)
				&& (map.get(Integer.valueOf(this.advanceLevel)) != null)) {
			return (BtlCalc.calc(this.attributes) + ((Integer) map.get(Integer.valueOf(this.advanceLevel))).intValue());
		}
		return BtlCalc.calc(this.attributes);
	}

	public int getMaxAdvanceLevel(Player player) {
		int start = this.advanceLevel;
		int end = ((HeroTemplate) getTemplate()).advanceRule.size();
		for (; start < end; ++start) {
			int advanceRuleId = ((Integer) ((HeroTemplate) getTemplate()).advanceRule.get(Integer.valueOf(start)))
					.intValue();
			HeroAdvance rule = ((HeroService) Platform.getServiceManager().get(HeroService.class))
					.getHeroAdvance(advanceRuleId);
			if (rule.playerLevel > player.getLevel()) {
				break;
			}
		}

		return start;
	}
}
