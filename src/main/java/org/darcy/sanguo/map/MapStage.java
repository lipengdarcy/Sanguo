package org.darcy.sanguo.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.item.MapDropAI;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.monster.Monster;
import org.darcy.sanguo.monster.MonsterService;
import org.darcy.sanguo.monster.MonsterTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;
import org.darcy.sanguo.unit.Unit;

public class MapStage extends Stage {
	private MapTemplate mapTemplate;
	private StageTemplate stageTemplate;
	private int channel;
	private List<Gain> gains;
	private int money;
	private int wariorSpirit;
	private int exp;
	private Player player;
	private List<Buff> npcBuffs;

	public MapStage(Team offen, MapTemplate mt, StageTemplate st, int channel, Player player) {
		super(0, st.channels[channel].getPositionInfo(), st.name, st.secenId);
		this.mapTemplate = mt;
		this.offen = offen;
		this.stageTemplate = st;
		this.channel = channel;
		this.offen.setStage(this);
		this.player = player;
	}

	public void init(Player player) {
		NPCHelp help = (NPCHelp) MapService.helps.get(Integer.valueOf(this.stageTemplate.id));
		if ((help != null)
				&& (!(player.getMapRecord().getHelpStages().contains(Integer.valueOf(this.stageTemplate.id))))) {
			Unit[] units = new Unit[6];
			for (int i = 0; i < 6; ++i) {
				int mid = help.npcs[i];
				if (mid != -1) {
					Monster m = new Monster(
							(MonsterTemplate) MonsterService.monsterTemplates.get(Integer.valueOf(mid)));
					units[i] = m;
				}
			}
			units[help.mainWarriorIndex] = player.getWarriors().getMainWarrior();
			Team o = new Team(this);
			o.setUnits(units);
			this.offen = o;

			this.sectionTemplates = help.getSectionTemlateList();

			if (help.buffs != null) {
				CombatService cs = (CombatService) Platform.getServiceManager().get(CombatService.class);
				this.npcBuffs = new ArrayList();
				for (int buffId : help.buffs) {
					Buff buff = cs.getBuff(buffId).copy();
					buff.setOwner(player.getWarriors().getMainWarrior());
					buff.setCaster(player.getWarriors().getMainWarrior());
					player.getWarriors().getMainWarrior().getBuffs().addBuff(buff);
					this.npcBuffs.add(buff);
				}
			}
		} else {
			this.sectionTemplates = this.stageTemplate.channels[this.channel].getSectionTemlateList();
		}

		this.deffens = new ArrayList(this.sectionTemplates.size());
		for (int i = 0; i < this.sectionTemplates.size(); ++i) {
			SectionTemplate sct = (SectionTemplate) this.sectionTemplates.get(i);
			Team deffen = new Team(this);
			deffen.setUnits(sct.getMonsters());
			this.deffens.add(deffen);
		}

		super.init();
	}

	public void clearNpcBuff(Player player) {
		if (this.npcBuffs != null)
			for (Buff buff : this.npcBuffs)
				player.getWarriors().getMainWarrior().getBuffs().removeBuff(buff);
	}

	public void combat(Player player) {
		if (this.npcBuffs != null) {
			Buff buff;
			Unit main = null;
			for (Unit u : this.offen.getUnits()) {
				if ((u != null) && (u instanceof MainWarrior)) {
					main = u;
				}
			}
			for (Iterator localIterator = this.npcBuffs.iterator(); localIterator.hasNext();) {
				buff = (Buff) localIterator.next();
				main.getBuffs().addBuff(buff);
			}
			super.combat(player);

			for (Iterator localIterator = this.npcBuffs.iterator(); localIterator.hasNext();) {
				buff = (Buff) localIterator.next();
				main.getBuffs().removeBuff(buff);
			}
		} else {
			super.combat(player);
		}
	}

	public void proccessReward(Player player) {
		if (isWin()) {
			player.decVitality(this.stageTemplate.vitalityCost, "pve");
			StageChannel sc = this.stageTemplate.channels[this.channel];
			this.money = sc.getMoney();
			player.addMoney(this.money, "pve");
			this.wariorSpirit = sc.getWarriorSpirit();
			player.addWarriorSpirit(this.wariorSpirit, "pve");
			this.exp = (player.getLevel() * 10);
			player.addExp(this.exp, "pve");

			this.gains = new ArrayList();

			if (ActivityInfo.isOpenActivity(8, player)) {
				MapDropAI ai = (MapDropAI) ActivityInfo.getItem(player, 8);
				if (ai != null) {
					DropGroup drop = (DropGroup) DropService.dropGroups.get(Integer.valueOf(ai.dropId));
					if (drop != null) {
						this.gains.addAll(drop.genGains(player));
					}
				}
			}

			DropGroup drop = (DropGroup) DropService.dropGroups.get(Integer.valueOf(sc.getDropId()));
			if (drop != null) {
				this.gains.addAll(drop.genGains(player));
				if (this.gains != null) {
					for (Gain gain : this.gains) {
						gain.gain(player, "pve");
					}
				}
			}
			MapRecord mr = player.getMapRecord();
			ClearMap cm = mr.getClearMap(this.mapTemplate.id);
			ClearStage cs = cm.getClearStage(this.stageTemplate.id);
			if (!(cs.isFinished())) {
				cs.finish(this.channel);
				player.getDestinyRecord().addStars();
				Platform.getEventManager().addEvent(new Event(2034, new Object[] { player }));

				if (this.stageTemplate.nextId == -1) {
					if (this.mapTemplate.nextId != -1) {
						mr.addClearMap(this.mapTemplate.nextId);
					}

					Platform.getEventManager().addEvent(new Event(2008, new Object[] { player }));
				} else {
					mr.addClearStage(this.mapTemplate.id, this.stageTemplate.nextId);
				}
			}
			if (!(cs.isFinished(this.channel))) {
				cs.finish(this.channel);
				player.getDestinyRecord().addStars();
				Platform.getEventManager().addEvent(new Event(2034, new Object[] { player }));
			}

			cs.chanllengeTimes += 1;

			Platform.getEventManager().addEvent(new Event(2006, new Object[] { player,
					Integer.valueOf(mr.getTotalEarnedStars()), this.mapTemplate.id + "," + this.stageTemplate.id }));

			Platform.getEventManager().addEvent(new Event(2007, new Object[] { player, Integer.valueOf(1) }));
			NPCHelp help = (NPCHelp) MapService.helps.get(Integer.valueOf(this.stageTemplate.id));
			if ((help != null)
					&& (!(player.getMapRecord().getHelpStages().contains(Integer.valueOf(this.stageTemplate.id))))) {
				player.getMapRecord().getHelpStages().add(Integer.valueOf(this.stageTemplate.id));
			}
		}

		int surplus = player.getMapRecord().getClearMap(this.mapTemplate.id).getClearStage(this.stageTemplate.id)
				.getLeftChanllengeTimes();
		Platform.getLog().logPveFight(player, this.mapTemplate, this.stageTemplate, isWin(), 1, surplus);
	}

	public MapTemplate getMapTemplate() {
		return this.mapTemplate;
	}

	public StageTemplate getStageTemplate() {
		return this.stageTemplate;
	}

	public int getChannel() {
		return this.channel;
	}

	public void setMapTemplate(MapTemplate mapTemplate) {
		this.mapTemplate = mapTemplate;
	}

	public void setStageTemplate(StageTemplate stageTemplate) {
		this.stageTemplate = stageTemplate;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public List<Gain> getGains() {
		return this.gains;
	}

	public int getMoney() {
		return this.money;
	}

	public int getWariorSpirit() {
		return this.wariorSpirit;
	}

	public int getExp() {
		return this.exp;
	}

	public void setNames() {
		this.defenName = this.stageTemplate.name;
		this.offenName = this.player.getName();
	}

	public boolean isPvP() {
		return false;
	}

	public void beforeCombat() {
		if (this.mapTemplate.id <= 2) {
			this.needCheck = false;
			return;
		}

		if (this.stageTemplate.type == 1) {
			this.needCheck = false;
			return;
		}

		MapRecord mr = this.player.getMapRecord();
		ClearMap cm = mr.getClearMap(this.mapTemplate.id);
		ClearStage cs = cm.getClearStage(this.stageTemplate.id);
		if (cs.isFinished()) {
			this.needCheck = false;
			return;
		}
	}
}
