package org.darcy.sanguo.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureTemplate extends ItemTemplate {
	public static final int TYPE_HORSE = 1;
	public static final int TYPE_BOOK = 2;
	public int treasureType;
	public Map<Integer, Integer> baseAttr = new HashMap();

	public Map<Integer, Integer> growAttr = new HashMap();

	public Map<Integer, Map<Integer, Integer>> extraAttr = new HashMap();
	public int maxLevel;
	public List<Integer> debris = new ArrayList();
	public boolean canEnhance;
	public int baseExp;
	public int intensifyCostMoneyRatio;
	public int intensifyRule;
	public int grade;
	public Map<Integer, Integer> enhanceAttr = new HashMap();
	public int maxEnhanceLevel;
	public int[] enhanceMoney;
	public Map<Integer, List<String>> enhanceCost = new HashMap();

	public TreasureTemplate(int id, String name) {
		super(id, 1, name);
	}
}
