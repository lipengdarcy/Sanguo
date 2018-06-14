package org.darcy.sanguo.item;

import java.text.MessageFormat;

import org.darcy.sanguo.map.ClearMap;
import org.darcy.sanguo.map.ClearStage;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;

import sango.packet.PbCommons;

public class DebrisDropPath {
	public int mapId;
	public int stageId;

	public DebrisDropPath(int mapId, int stageId) {
		this.mapId = mapId;
		this.stageId = stageId;
	}

	public PbCommons.DebrisPath genDebrisPath(Player player) {
		PbCommons.DebrisPath.Builder b = PbCommons.DebrisPath.newBuilder();
		MapTemplate mt = (MapTemplate) MapService.mapTemplates.get(Integer.valueOf(this.mapId));
		StageTemplate st = (StageTemplate) MapService.stageTemplates.get(Integer.valueOf(this.stageId));
		b.setResourceId(mt.resourceId);
		b.setMapName(mt.name);
		b.setStageName(st.name);
		b.setMapId(this.mapId);
		b.setStageId(this.stageId);
		ClearMap cm = player.getMapRecord().getClearMap(mt.id);
		if (cm == null) {
			b.setIsOpen(false);
			if (mt.preId > 0) {
				ClearMap preCm = player.getMapRecord().getClearMap(mt.preId);
				if ((preCm == null) || (!(preCm.isFinished())))
					b.setOpenCondition("开启条件：" + MessageFormat.format("需要通关{0}", new Object[] {
							((MapTemplate) MapService.mapTemplates.get(Integer.valueOf(mt.preId))).name }));
				else
					b.setOpenCondition("开启条件："
							+ MessageFormat.format("需要等级达到{0}级才可以攻打", new Object[] { Integer.valueOf(mt.openLevel) }));
			} else {
				b.setOpenCondition("开启条件："
						+ MessageFormat.format("需要等级达到{0}级才可以攻打", new Object[] { Integer.valueOf(mt.openLevel) }));
			}
		} else {
			ClearStage cs = cm.getClearStage(this.stageId);
			if (cs == null)
				if (st.preId > 0) {
					b.setIsOpen(false);
					b.setOpenCondition("开启条件：" + MessageFormat.format("需要通关{0}", new Object[] {
							((StageTemplate) MapService.stageTemplates.get(Integer.valueOf(st.preId))).name }));
				} else {
					b.setIsOpen(true);
				}
			else {
				b.setIsOpen(true);
			}
		}
		return b.build();
	}
}
