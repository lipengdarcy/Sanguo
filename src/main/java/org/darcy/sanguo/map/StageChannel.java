package org.darcy.sanguo.map;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.service.MapService;

public class StageChannel {
	private int[] sectionList;
	private String positionInfo;
	private int money;
	private int warriorSpirit;
	private int dropId;

	public List<SectionTemplate> getSectionTemlateList() {
		List list = new ArrayList(this.sectionList.length);
		for (int i = 0; i < this.sectionList.length; ++i) {
			SectionTemplate s = (SectionTemplate) MapService.sectionTemplates.get(Integer.valueOf(this.sectionList[i]));
			if (s != null) {
				list.add(s);
			}
		}

		return list;
	}

	public int[] getSectionList() {
		return this.sectionList;
	}

	public String getPositionInfo() {
		return this.positionInfo;
	}

	public int getMoney() {
		return this.money;
	}

	public int getWarriorSpirit() {
		return this.warriorSpirit;
	}

	public int getDropId() {
		return this.dropId;
	}

	public void setSectionList(int[] sectionList) {
		this.sectionList = sectionList;
	}

	public void setPositionInfo(String positionInfo) {
		this.positionInfo = positionInfo;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void setWarriorSpirit(int warriorSpirit) {
		this.warriorSpirit = warriorSpirit;
	}

	public void setDropId(int dropId) {
		this.dropId = dropId;
	}
}
