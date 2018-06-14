package org.darcy.sanguo.combat.record;

import java.util.List;

import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.unit.Unit;

import sango.packet.PbRecord;

public class RecordUtil {
	private int outIndex = 0;
	private int inIndex = 0;

	private PbRecord.StageRecord.Builder stageRecord = PbRecord.StageRecord.newBuilder();
	private PbRecord.SectionRecord.Builder sectionReBuilder;
	private PbRecord.StageRecord inStageRecord;
	private List<PbRecord.ActionRecord> inRecords;
	private PbRecord.SectionRecord inSectionRecord;

	public void prepareNewSection(int index) {
		if (this.inStageRecord != null) {
			this.inSectionRecord = this.inStageRecord.getSections(index);
			this.inRecords = this.inSectionRecord.getActionsList();
		}
	}

	public List<Integer> getReviveIds() {
		if (this.inSectionRecord != null) {
			return this.inSectionRecord.getReviveIdsList();
		}
		return null;
	}

	public List<Integer> getOffens() {
		if (this.inSectionRecord != null) {
			return this.inSectionRecord.getOffenTeamList();
		}
		return null;
	}

	public void newSection(int index, Team offen) {
		if (this.sectionReBuilder != null) {
			this.stageRecord.addSections(this.sectionReBuilder);
		}
		this.outIndex = 0;
		this.sectionReBuilder = PbRecord.SectionRecord.newBuilder();
		this.sectionReBuilder.setIndex(index);
		for (Unit unit : offen.getUnits())
			if (unit != null)
				this.sectionReBuilder.addOffenTeam(unit.getId());
			else
				this.sectionReBuilder.addOffenTeam(-1);
	}

	public void setResult(boolean rst) {
		this.stageRecord.addSections(this.sectionReBuilder);
		this.stageRecord.setResult(rst);
	}

	public void addRecord(PbRecord.ActionRecord.Builder r) {
		this.sectionReBuilder.addActions(r.setIndex(this.outIndex++));

		if (this.inRecords != null)
			this.inIndex += 1;
	}

	public PbRecord.Actor getActor(Unit unit, Section section) {
		return PbRecord.Actor.newBuilder().setTeamType(section.getOwnerTeamType(unit))
				.setLocation(section.getOwnerTeam(unit).getIndex(unit)).build();
	}

	public void setInStageRecord(PbRecord.StageRecord inStageRecord) {
		this.inStageRecord = inStageRecord;
	}

	public PbRecord.ActionRecord getNextManualSkillRecord() {
		if (this.inRecords == null) {
			return null;
		}

		for (int i = this.outIndex; i < this.inRecords.size(); ++i) {
			PbRecord.ActionRecord r = (PbRecord.ActionRecord) this.inRecords.get(i);
			if (r.getType() == PbRecord.ActionRecord.RecordType.USESKILL) {
				if (r.getUseSkillRecord().getActionType() != PbRecord.UseSkillRecord.ActionType.MANUAL)
					continue;
				return r;
			}
			if (r.getType() == PbRecord.ActionRecord.RecordType.TOKEN) {
				if (r.getTokenRecord().getType() != PbRecord.TokenRecord.TokenType.END)
					continue;
				return null;
			}
			if (r.getType() == PbRecord.ActionRecord.RecordType.NEWROUND) {
				return null;
			}
		}
		return null;
	}

	public boolean isPvP() {
		return (this.inSectionRecord != null);
	}

	public String toString() {
		return this.stageRecord.build().toString();
	}

	public PbRecord.StageRecord getInStageRecord() {
		return this.inStageRecord;
	}

	public PbRecord.StageRecord.Builder getStageRecord() {
		return this.stageRecord;
	}

	public boolean isFailure() {
		return ((this.inStageRecord == null) || (this.inStageRecord.getResult()));
	}
}
