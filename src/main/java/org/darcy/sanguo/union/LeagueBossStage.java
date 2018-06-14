package org.darcy.sanguo.union;

import java.util.ArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.monster.Monster;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.unit.Unit;

public class LeagueBossStage extends Stage {
	private Player player;
	private Monster boss;
	private League league;
	private int buffId;
	private int startHp;
	private int endHp;
	int reward;
	int damage;

	public LeagueBossStage(String location, String name, int senceId, Player player, Monster boss, League l) {
		super(11, location, name, senceId);
		this.player = player;
		this.boss = boss;
		this.league = l;
		this.buffId = LeagueService.getBossBuff(this.league.getBossFacilityLevel());

		this.startHp = boss.getAttributes().getHp();
	}

	public void init() {
		Warrior[] ws = this.player.getWarriors().getStands();
		for (int i = 0; i < ws.length; ++i) {
			Warrior w = ws[i];
			if ((w == null) || (this.buffId == -1))
				continue;
			w.addLeagueBossBuff(this.buffId);
		}

		this.player.getWarriors().refresh(true);

		this.offen = new Team(this);
		this.offen.setUnits(this.player.getWarriors().getStands());
		this.deffens = new ArrayList(1);
		Team deffen = new Team(this);
		deffen.setUnits(new Unit[] { this.boss });
		this.deffens.add(deffen);

		this.offen.clearTmpBuffs();
		for (Team t : this.deffens) {
			t.clearTmpBuffs();
		}

		this.offen.rest(Unit.RestType.STAGE);

		setNames();
	}

	public void combat(Player player) {
		this.boss.getBuffs().clearBtlBuff();
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
		this.boss.resetAngry();
		this.endHp = this.boss.getAttributes().getHp();
		this.recordUtil.setResult(this.section.isWin());
		this.offen.rest(Unit.RestType.STAGE);
		Platform.getLog().logCombat(getRecordUtil());
	}

	public void proccessReward(Player player) {
		int reward = this.league.getFightBossReward();
		if (reward > 0) {
			this.reward = reward;
			player.addMoney(reward, "leaguebossfight");
		}

		Warrior[] ws = player.getWarriors().getStands();
		for (int i = 0; i < ws.length; ++i) {
			Warrior w = ws[i];
			if ((w == null) || (this.buffId == -1))
				continue;
			w.removeGloryBuff(this.buffId);
		}

		player.getWarriors().refresh(true);

		this.damage = (this.startHp - this.endHp);
		boolean isWin = isWin();
		this.league.fightBoss(player, this.damage, isWin, reward);
	}

	public boolean isPvP() {
		return true;
	}

	public void setNames() {
		this.defenName = this.boss.getName();
		this.offenName = this.player.getName();
	}
}
