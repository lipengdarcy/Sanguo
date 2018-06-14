package org.darcy.sanguo.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.record.RecordUtil;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.map.MapStage;
import org.darcy.sanguo.map.SectionTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;

import sango.packet.PbRecord;

public abstract class Stage {
	public static final int MAX_ROUND = 30;
	public static final int STAGE_TYPE_MAP = 0;
	public static final int STAGE_TYPE_MAPPRO = 1;
	public static final int STAGE_TYPE_BOSS = 2;
	public static final int STAGE_TYPE_WARRIOR_TRIAL = 3;
	public static final int STAGE_TYPE_TREASURE_TRIAL = 4;
	public static final int STAGE_TYPE_MONEY_TRIAL = 5;
	public static final int STAGE_TYPE_ARENA = 6;
	public static final int STAGE_TYPE_WORLD_COMPETITION = 7;
	public static final int STAGE_TYPE_LOOT_TREASURE = 8;
	public static final int STAGE_TYPE_FRIEND = 9;
	public static final int STAGE_TYPE_TOWER = 10;
	public static final int STAGE_TYPE_LEAGUE_BOSS = 11;
	public static final int STAGE_TYPE_LCCITY = 14;
	public static final int STAGE_TYPE_LCTOWN = 15;
	public static final int STAGE_TYPE_LCVILLAGE = 16;
	public static final int STAGE_TYPE_LCRANDOM = 17;
	protected String offenName;
	protected String defenName;
	protected RandomBox randomBox = new RandomBox();
	protected RecordUtil recordUtil = new RecordUtil();
	protected String location;
	protected String name;
	protected int senceId;
	protected int type;
	protected boolean needCheck = true;
	long cid = System.currentTimeMillis();
	protected Team offen;
	protected List<SectionTemplate> sectionTemplates;
	protected List<Team> deffens;
	protected Section section;

	public Stage(int type, String location, String name, int senceId) {
		this.location = location;
		this.name = name;
		this.senceId = senceId;
		this.type = type;
	}

	public RandomBox getRandomBox() {
		return this.randomBox;
	}

	public RecordUtil getRecordUtil() {
		return this.recordUtil;
	}

	public void setRandomBox(RandomBox randomBox) {
		this.randomBox = randomBox;
	}

	public void setRecordUtil(RecordUtil recordUtil) {
		this.recordUtil = recordUtil;
	}

	public void init() {
		Team t;
		this.offen.clearTmpBuffs();
		for (Iterator localIterator = this.deffens.iterator(); localIterator.hasNext();) {
			t = (Team) localIterator.next();
			t.clearTmpBuffs();
		}
		this.offen.rest(Unit.RestType.STAGE);
		for (Iterator localIterator = this.deffens.iterator(); localIterator.hasNext();) {
			t = (Team) localIterator.next();
			t.rest(Unit.RestType.STAGE);
		}
		if (this.sectionTemplates == null) {
			this.sectionTemplates = new ArrayList();
			for (Iterator localIterator = this.deffens.iterator(); localIterator.hasNext();) {
				t = (Team) localIterator.next();
				SectionTemplate dft = new SectionTemplate();
				dft.id = 0;
				dft.auto = true;
				this.sectionTemplates.add(dft);
			}
		}
		setNames();
	}

	public void beforeCombat() {
	}

	public void combat(Player player) {
		Team deffen;
		if (this.recordUtil.isFailure()) {
			return;
		}
		beforeCombat();
		if (!(this.needCheck)) {
			return;
		}
		Platform.getLog().logCombat("=====combat R " + this.cid + " start=====");

		for (int i = 0; i < this.deffens.size(); ++i) {
			this.recordUtil.prepareNewSection(i);

			this.offen.rest(Unit.RestType.SECTION);
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

		beforeRest();

		this.offen.rest(Unit.RestType.STAGE);
		for (Team t : this.deffens) {
			t.rest(Unit.RestType.STAGE);
		}

		this.recordUtil.setResult(this.section.isWin());
		Platform.getLog().logCombat("=====combat R " + this.cid + " end=====");
		if (!(getRecordUtil().isPvP())) {
			Platform.getLog().logRecordOut(getRecordUtil().getStageRecord().build().toString());
			Platform.getLog().logRecordIn(getRecordUtil().getInStageRecord().toString());
			Platform.getLog().logCombat("=====combat C " + this.cid + " start=====");
			Platform.getLog().logCombat(getRecordUtil().getInStageRecord().toString());
			Platform.getLog().logCombat("=====combat C " + this.cid + " end=====");
			Platform.getLog().logCombat("=====combat S " + this.cid + " start=====");
			Platform.getLog().logCombat(getRecordUtil().getStageRecord().build().toString());
			Platform.getLog().logCombat("=====combat S " + this.cid + " end=====");
			if (!(getRecordUtil().getStageRecord().build().toString()
					.equals(getRecordUtil().getInStageRecord().toString())))
				Platform.getLog().logCombat("======miss match " + this.cid + "=name:" + player.getName() + " =pid:"
						+ player.getId() + "=" + this.name + "=====");
		} else {
			Platform.getLog().logCombat("=====combat S " + this.cid + " start=====");
			Platform.getLog().logCombat(getRecordUtil().getStageRecord().build().toString());
			Platform.getLog().logCombat("=====combat S " + this.cid + " end=====");
		}
	}

	public void beforeRest() {
		if ((!(isPvP())) && (!(this.section.isWin()))) {
			int maxHp = 0;
			int leftHp = 0;
			for (Unit u : this.section.getDefensive().getUnits()) {
				if (u != null) {
					maxHp += u.getAttributes().get(7);
					leftHp += u.getAttributes().getHp();
				}
			}

			String name = "";
			for (Unit u : this.section.getOffensive().getUnits()) {
				if (u instanceof MainWarrior) {
					name = u.getName();
				}
			}

			Platform.getLog().logWarn("combatFix=" + name + "=" + this.cid + "=can fix=" + (leftHp * 100 / maxHp));
			if (leftHp * 100 / maxHp <= 75) {
				Platform.getLog().logWarn("combatFix=" + name + "=" + this.cid + "=fixed=");
				this.section.setWin(true);
			}
		}
	}

	public abstract void proccessReward(Player paramPlayer);

	public abstract boolean isPvP();

	public int getMaxRound() {
		return 30;
	}

	public abstract void setNames();

	public PbRecord.StageBtlInfo.Builder getInfoBuilder() {
		PbRecord.StageBtlInfo.Builder b = PbRecord.StageBtlInfo.newBuilder();
		PbRecord.SectionBtlInfo.Builder sb = PbRecord.SectionBtlInfo.newBuilder();
		b.setLocation(this.location).setName(this.name).setOffenName(this.offenName).setDefenName(this.defenName)
				.setBtlCapa(this.offen.getBtlCapa()).setSenceId(this.senceId);
		for (int i = 0; i < this.offen.getUnits().length; ++i) {
			Unit u = this.offen.getUnit(i);
			if (u != null) {
				PbRecord.Seat.Builder sb1 = PbRecord.Seat.newBuilder().setIndex(i).setMain(u instanceof MainWarrior)
						.setUnit(u.genUnit());
				b.addOffens(sb1);
			}
		}
		for (int i = 0; i < this.deffens.size(); ++i) {
			Team team = (Team) this.deffens.get(i);
			
			PbRecord.SectionBtlInfo.newBuilder().setAuto(true).setBtlCapa(team.getBtlCapa()).setBossIndex(-1)
					.setIndex(i);
			if (this instanceof MapStage) {
				SectionTemplate st = (SectionTemplate) this.sectionTemplates.get(i);
				sb.setBossIndex(st.bossIndex);
			}
			for (int j = 0; j < team.getUnits().length; ++j) {
				Unit u = team.getUnit(j);
				if (u != null) {
					PbRecord.Seat.Builder seat = PbRecord.Seat.newBuilder().setIndex(j)
							.setMain(u instanceof MainWarrior).setUnit(u.genUnit());
					sb.addUnits(seat);
				}
			}
			b.addSections(sb);
		}
		int len = this.randomBox.getRandoms().length;
		for (int i = 0; i < len; ++i) {
			int v = this.randomBox.getRandoms()[i];
			b.addRandoms(v);
		}
		b.setMapType(this.type);
		return b;
	}

	public boolean isWin() {
		if (!(this.needCheck)) {
			return (!(this.recordUtil.isFailure()));
		}
		if (this.recordUtil.isFailure()) {
			return false;
		}
		return this.section.isWin();
	}
}
