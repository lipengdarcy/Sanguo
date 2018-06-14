package org.darcy.sanguo.tower;

import java.util.List;

import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.map.SectionTemplate;

import sango.packet.PbDown;

public class TowerStageTemplate {
	public int id;
	public String name;
	public String layerName;
	public int money;
	public int warriorSpirit;
	public List<Reward> rewards;
	public int titleId;
	public List<Attri> titleAttris;
	public int nextLayer;
	public int openLevel;
	public SectionTemplate section;
	public int type;
	public int monsterModel;
	public FinishCondition fCondition;

	public PbDown.TowerInfoRst.TowerStage.Builder genPb() {
		PbDown.TowerInfoRst.TowerStage.Builder rst = PbDown.TowerInfoRst.TowerStage.newBuilder();
		rst.setLayerName(this.layerName).setMonsterModel(this.monsterModel).setStage(this.id);
		return rst;
	}

	public Attributes getAttributes() {
		Attributes rst = new Attributes();
		for (Attri a : this.titleAttris) {
			rst.addAttri(a);
		}

		return rst;
	}
}
