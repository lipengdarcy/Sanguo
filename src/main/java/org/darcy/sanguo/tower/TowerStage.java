package org.darcy.sanguo.tower;

import java.util.ArrayList;
import java.util.Arrays;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.TowerService;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class TowerStage extends Stage {
	public TowerStageTemplate tst;
	Player player;
	public boolean finish;

	public TowerStage(String location, int senceId, TowerStageTemplate tst, Player player) {
		super(10, location, tst.layerName, senceId);
		this.tst = tst;
		this.player = player;
	}

	public void init(Team offen, Team deffen) {
		this.offen = offen;
		this.deffens = new ArrayList();
		this.deffens.add(deffen);
		super.init();
	}

	public void proccessReward(Player player) {
		TowerRecord r = player.getTowerRecord();
		if ((isWin()) && (this.finish)) {
			player.addMoney(this.tst.money, "tower");
			player.addWarriorSpirit(this.tst.warriorSpirit, "tower");
			if (this.tst.rewards != null) {
				for (Reward rwd : this.tst.rewards) {
					rwd.add(player, "tower");
				}

			}

			if (r.getLevel() > r.getMaxLevel()) {
				r.setMaxLevel(r.getLevel());

				TowerStageTemplate tt = (TowerStageTemplate) TowerService.towerTemplates
						.get(Integer.valueOf(r.getMaxLevel()));
				r.addAttris(tt.titleAttris);
				player.getWarriors().refresh(true);
				Platform.getEventManager()
						.addEvent(new Event(1010, new Object[] { player, Integer.valueOf(r.getMaxLevel()) }));
			}
			r.setPass(true);
			if (this.tst.nextLayer != -1) {
				r.setLevel(this.tst.nextLayer);
				r.setPass(false);
			}

			Platform.getEventManager().addEvent(new Event(2020, new Object[] { player }));
			r.setChallengeTimes(r.getChallengeTimes() - 1);
		}
	}

	public void setNames() {
		this.offenName = this.player.getName();
		this.defenName = this.tst.layerName;
	}

	public boolean isPvP() {
		return false;
	}

	private int getDieCount() {
		PbRecord.EffectRecord er;
		PbRecord.Actor actor;
		int index;
		int[] hps = new int[6];
		int[] mhps = new int[6];
		int tmp = 0;
		for (Unit u : this.offen.getUnits()) {
			if (u != null) {
				hps[tmp] = u.getAttributes().get(7);
				mhps[tmp] = u.getAttributes().get(7);
			} else {
				hps[tmp] = -1;
				mhps[tmp] = -1;
			}
			++tmp;
		}

		for (PbRecord.ActionRecord ar : this.section.getRecordUtil().getInStageRecord().getSections(0)
				.getActionsList()) {
			if (ar.getType() == PbRecord.ActionRecord.RecordType.EFFECT) {
				er = ar.getEffectRecord();
				actor = ar.getActor();
				if (actor.getTeamType() == 0) {
					continue;
				}
				index = actor.getLocation();
				if (er != null) {
					if (er.getEffectType() == 1) {
						hps[index] += er.getAttachParam(0);
						if (hps[index] > mhps[index])
							hps[index] = mhps[index];
					} else if (er.getEffectType() == 2) {
						hps[index] -= er.getAttachParam(0);
						if (hps[index] < 0) {
							hps[index] = 0;
						}
					}
				}
			}
		}

		int dieCount = 0;
		for (int j = 0; j < hps.length; ++j) {
			int i = hps[j];
			if ((i <= 0) && (i != -1)) {
				++dieCount;
			}
		}

		return dieCount;
	}

	private int getHpCost() {
		int[] hps = new int[6];
		int[] mhps = new int[6];
		int tmp = 0;
		for (Unit u : this.offen.getUnits()) {
			if (u != null) {
				hps[tmp] = u.getAttributes().get(7);
				mhps[tmp] = u.getAttributes().get(7);
			} else {
				hps[tmp] = -1;
				mhps[tmp] = -1;
			}
			++tmp;
		}

		for (PbRecord.ActionRecord ar : this.section.getRecordUtil().getInStageRecord().getSections(0)
				.getActionsList()) {
			if (ar.getType() == PbRecord.ActionRecord.RecordType.EFFECT) {
				PbRecord.EffectRecord er = ar.getEffectRecord();
				PbRecord.Actor actor = ar.getActor();
				if (actor.getTeamType() == 0) {
					continue;
				}
				int index = actor.getLocation();
				if (er != null) {
					if (er.getEffectType() == 1) {
						hps[index] += er.getAttachParam(0);
						if (hps[index] > mhps[index])
							hps[index] = mhps[index];
					} else if (er.getEffectType() == 2) {
						hps[index] -= er.getAttachParam(0);
						if (hps[index] < 0) {
							hps[index] = 0;
						}
					}
				}
			}
		}

		long hp = 0L;
		long mhp = 0L;
		for (int i : hps) {
			if (i != -1) {
				hp += i;
			}
		}
		for (int i : mhps) {
			if (i != -1) {
				mhp += i;
			}
		}
		System.out.println(Arrays.toString(hps));
		System.out.println(Arrays.toString(mhps));
		return (100 - (int) (hp * 100L / mhp));
	}

	public void beforeRest() {
		super.beforeRest();

		if (this.tst.fCondition == null)
			this.finish = true;
		else
			switch (this.tst.fCondition.type) {
			case 3:
				int count = 0;
				for (Unit u : this.offen.getUnits()) {
					if ((u != null) && (!(u.isAlive()))) {
						++count;
					}
				}
				if (count > this.tst.fCondition.value)
					return;
				this.finish = true;

				break;
			case 2:
				int max = 0;
				int j = 0;
				for (Unit u : this.offen.getUnits()) {
					if (u != null) {
						max += u.getAttributes().get(7);
						j += u.getAttributes().getHp();
					}
				}
				if ((max - j) * 100L >= max * this.tst.fCondition.value)
					return;
				this.finish = true;

				break;
			case 1:
				int l = 0;
				for (PbRecord.ActionRecord ar : this.section.getRecordUtil().getInStageRecord().getSections(0)
						.getActionsList()) {
					if (ar.getType() == PbRecord.ActionRecord.RecordType.NEWROUND) {
						++l;
					}
				}
				if (l > this.tst.fCondition.value)
					return;
				this.finish = true;

				break;
			default:
				this.finish = true;
			}
	}

	public void beforeCombat() {
		TowerRecord r = this.player.getTowerRecord();
		if (r.getLevel() < r.getMaxLevel()) {
			this.needCheck = false;
			this.finish = true;
		}
	}
}
