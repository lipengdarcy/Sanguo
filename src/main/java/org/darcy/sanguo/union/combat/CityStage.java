package org.darcy.sanguo.union.combat;

import java.util.ArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.map.SectionTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.unit.Unit;

public class CityStage extends Stage {
	private Player player;
	private Player target;
	private Pair pair;

	public CityStage(int type, String location, String name, int senceId, Player player, Player target, Pair pair) {
		super(type, location, name, senceId);
		this.pair = pair;
		this.player = player;
		this.target = target;
	}

	public void init() {
		Attributes a;
		this.offen = new Team(this);
		this.offen.setUnits(this.player.getWarriors().getStands());
		this.deffens = new ArrayList();
		Team deffen = new Team(this);
		deffen.setUnits(this.target.getWarriors().getStands());
		this.deffens.add(deffen);
		super.init();

		int offenVillageCount = this.pair.getCityCount(3, this.player.getUnion().getLeagueId());
		if (offenVillageCount > 0) {
			long atkAdd = ((Integer) LeagueCombatService.cityAtkAdds.get(Integer.valueOf(offenVillageCount)))
					.intValue();
			for (Unit u : this.offen.getUnits()) {
				if (u != null) {
					a = u.getAttributes();
					a.set(6, (int) (a.get(6) * (100L + atkAdd) / 100L));
				}
			}
		}

		int deffenVillageCount = this.pair.getCityCount(3,
				this.pair.getTargetLid(this.player.getUnion().getLeagueId()));
		if (deffenVillageCount > 0) {
			long atkAdd = ((Integer) LeagueCombatService.cityAtkAdds.get(Integer.valueOf(deffenVillageCount)))
					.intValue();
			for (Unit u : ((Team) this.deffens.get(0)).getUnits())
				if (u != null) {
					a = u.getAttributes();
					a.set(6, (int) (a.get(6) * (100L + atkAdd) / 100L));
				}
		}
	}

	public void proccessReward(Player player) {
	}

	public boolean isPvP() {
		return true;
	}

	public void setNames() {
		this.offenName = this.player.getName();
		this.defenName = this.target.getName();
	}

	public void combat(Player player) {
		Team deffen;
		for (int i = 0; i < this.deffens.size(); ++i) {
			this.recordUtil.prepareNewSection(i);
			this.recordUtil.newSection(i, this.offen);
			if (i == 0) {
				this.section = new Section(this);
				SectionTemplate sct = (SectionTemplate) this.sectionTemplates.get(0);
				this.section.init(sct.auto, this.offen, (Team) this.deffens.get(0));
				this.section.combat();
			} else {
				deffen = (Team) this.deffens.get(i);
				SectionTemplate st = (SectionTemplate) this.sectionTemplates.get(i);
				this.section.next(st.auto, deffen);
				this.section.combat();
			}
			if (!(this.section.isWin())) {
				break;
			}
		}

		this.offen.rest(Unit.RestType.STAGE);
		for (Team t : this.deffens) {
			t.rest(Unit.RestType.STAGE);
		}

		this.recordUtil.setResult(this.section.isWin());
		Platform.getLog().logCombat(getRecordUtil());
	}
}
