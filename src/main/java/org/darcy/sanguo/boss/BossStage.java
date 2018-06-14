package org.darcy.sanguo.boss;

import java.util.ArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.monster.Monster;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.BossService;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.unit.Unit;

public class BossStage extends Stage {
	public static final int MAX_ROUND = 30;
	private Player player;
	private Monster boss;
	private int startHp;
	private int endHp;
	public int damage;
	int addAtk;

	public BossStage(String location, String name, int senceId, Player player, Monster boss, int addAtk) {
		super(2, location, name, senceId);
		this.player = player;
		this.boss = boss;
		this.startHp = boss.getAttributes().getHp();
		this.addAtk = addAtk;
	}

	public void init() {
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

		for (Unit u : this.offen.getUnits()) {
			if (u != null) {
				int atk = u.getAttributes().getBase(6);
				u.getAttributes().set(6, u.getAttributes().get(6) + atk * this.addAtk / 10000);
			}
		}

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
				this.boss.resetAngry();
				this.recordUtil.setResult(this.section.isWin());
				Platform.getLog().logCombat(getRecordUtil());

				this.endHp = this.boss.getAttributes().getHp();
				this.offen.rest(Unit.RestType.STAGE);
				return;
			}
		}
		this.endHp = this.boss.getAttributes().getHp();
		this.offen.rest(Unit.RestType.STAGE);

		this.recordUtil.setResult(this.section.isWin());
		Platform.getLog().logCombat(getRecordUtil());
	}

	public void proccessReward(Player player) {
		BossRecord r = player.getBossRecord();
		BossManager m = Platform.getBossManager();
		this.damage = (this.startHp - this.endHp);
		r.setTotalDamage(r.getTotalDamage() + this.damage);
		League l = Platform.getLeagueManager().getLeagueByPlayerId(player.getId());
		if (l != null) {
			l.getInfo().addWorldBossDamage(this.damage);
		}
		if (r.getTotalDamage() > 0) {
			m.addToRank(player);
		}
		if (isWin()) {
			m.setKiller(player);
			m.endBoss();
			for (Player p : Platform.getPlayerManager().players.values()) {
				if (p != null) {
					Function.notifyMainNum(p, 19, 0);
				}
			}
			Platform.getPlayerManager().boardCast(m.getBoardCastMsg());
		} else {
			r.setDie(true);
			r.setLastDieTime(System.currentTimeMillis());
		}
		player.addPrestige(10, "boss");
		((BossService) Platform.getServiceManager().get(BossService.class)).pushDamage(player, this.damage);

		Platform.getEventManager().addEvent(new Event(2019, new Object[] { player }));
	}

	public void setNames() {
		this.defenName = this.boss.getName();
		this.offenName = this.player.getName();
	}

	public boolean isPvP() {
		return true;
	}
}
