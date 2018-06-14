package org.darcy.sanguo.item.equip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbEquipment;

/**
 * 武器装备洗属性
 */
public class PolishAttr {
	public int id;
	public List<Reward> normalCost = new ArrayList<Reward>();
	public PolishRandom normalRandom;
	public List<Reward> moneyCost = new ArrayList<Reward>();
	public PolishRandom moneyRandom;
	public List<Reward> jewelCost = new ArrayList<Reward>();
	public PolishRandom jewelRandom;
	public Map<Integer, PolishAttrValue> attrValue = new HashMap<Integer, PolishAttrValue>();

	public PbEquipment.EquipmentPolishCostInfo genEquipmentPolishCostInfo(int type) {
		Reward reward;
		Iterator<Reward> localIterator;
		PbEquipment.EquipmentPolishCostInfo.Builder builder = PbEquipment.EquipmentPolishCostInfo.newBuilder();
		builder.setType(type);
		if (type == 1)
			for (localIterator = this.normalCost.iterator(); localIterator.hasNext();) {
				reward = (Reward) localIterator.next();
				builder.addCosts(reward.genPbReward());
			}
		else if (type == 2)
			for (localIterator = this.moneyCost.iterator(); localIterator.hasNext();) {
				reward = (Reward) localIterator.next();
				builder.addCosts(reward.genPbReward());
			}
		else if (type == 3) {
			for (localIterator = this.jewelCost.iterator(); localIterator.hasNext();) {
				reward = (Reward) localIterator.next();
				builder.addCosts(reward.genPbReward());
			}
		}
		return builder.build();
	}

	public class PolishAttrValue {
		public int type;
		public int value;
	}
}
