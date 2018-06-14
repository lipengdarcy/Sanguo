package org.darcy.sanguo.map;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.service.MapService;

public class NPCHelp {
	public int stageId;
	public int[] buffs;
	public int mainWarriorIndex;
	public int[] npcs = new int[6];
	public int[] sectionIds;

	public List<SectionTemplate> getSectionTemlateList() {
		List list = new ArrayList(this.sectionIds.length);
		for (int i = 0; i < this.sectionIds.length; ++i) {
			SectionTemplate s = (SectionTemplate) MapService.sectionTemplates.get(Integer.valueOf(this.sectionIds[i]));
			if (s != null) {
				list.add(s);
			}
		}

		return list;
	}
}
