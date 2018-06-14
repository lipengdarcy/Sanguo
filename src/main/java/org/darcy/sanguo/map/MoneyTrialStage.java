package org.darcy.sanguo.map;

import java.util.ArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;
import org.darcy.sanguo.unit.Unit;

public class MoneyTrialStage extends Stage {
	public static final int MAX_ROUND = 5;
	public static final double DAMAGE_MONEY_FACTOR = 10.0D;
	private MapTemplate mapTemplate;
	private StageTemplate stageTemplate;
	private static final int channel = 0;
	private Player player;
	private int damage;
	private int money;
	private Reward reward;

	public MoneyTrialStage(MapTemplate mt, StageTemplate st, Player player) {
		super(5, st.channels[0].getPositionInfo(), st.name, st.secenId);
		this.mapTemplate = mt;
		this.stageTemplate = st;
		this.player = player;
	}

	public void init() {
		this.offen = new Team(this);
		this.offen.setUnits(this.player.getWarriors().getStands());

		this.sectionTemplates = this.stageTemplate.channels[0].getSectionTemlateList();
		this.deffens = new ArrayList(this.sectionTemplates.size());
		for (int i = 0; i < this.sectionTemplates.size(); ++i) {
			SectionTemplate sct = (SectionTemplate) this.sectionTemplates.get(i);
			Team deffen = new Team(this);
			deffen.setUnits(sct.getMonsters());
			this.deffens.add(deffen);
		}
		super.init();
	}

	public void combat(Player player) {
		for (int i = 0; i < this.deffens.size(); ++i) {
			this.recordUtil.prepareNewSection(i);
			this.recordUtil.newSection(i, this.offen);
			if (i == 0) {
				this.section = new Section(this);
				this.section.init(true, this.offen, (Team) this.deffens.get(0));
				this.section.combat();
			} else {
				Team deffen = (Team) this.deffens.get(i);
				this.section.next(true, deffen);
				this.section.combat();
			}
			if (!(this.section.isWin())) {
				break;
			}
		}
		this.recordUtil.setResult(this.section.isWin());
		this.offen.rest(Unit.RestType.STAGE);
		calDamage();

		Platform.getLog().logRecordOut(getRecordUtil());
	}

	public void proccessReward(Player player) {
		int money = (int) Math.floor(this.damage / 10.0D);
		Reward reward = MapService.getRewardByDamage(this.damage);
		if ((reward != null) && (reward.type == 2)) {
			money += reward.count;
		}
		this.reward = new Reward(2, money, null);
		this.reward.add(this.player, "moneytrial");
		this.money = money;
		MapRecord record = player.getMapRecord();
		record.decActivityMapLeftTimes(this.mapTemplate.type);
		Platform.getEventManager().addEvent(new Event(2058, new Object[] { player }));

		Platform.getLog().logPveFight(player, this.mapTemplate, this.stageTemplate, isWin(), 1,
				player.getMapRecord().getActivityMapLeftTimes(this.mapTemplate.type));
	}

	public void calDamage() {
		int damage = 0;
		for (Team deffen : this.deffens) {
			for (Unit unit : deffen.getUnits()) {
				if (unit != null) {
					damage += unit.getAttributes().get(7) - unit.getAttributes().getHp();
				}
			}
		}
		if (damage < 0) {
			damage = 0;
		}
		setDamage(damage);
	}

	public int getDamage() {
		return this.damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public Reward getReward() {
		return this.reward;
	}

	public void setReward(Reward reward) {
		this.reward = reward;
	}

	public int getMoney() {
		return this.money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getMaxRound() {
		return 5;
	}

	public void setNames() {
		this.offenName = this.player.getName();
		this.defenName = this.stageTemplate.name;
	}

	public boolean isPvP() {
		return true;
	}
}
