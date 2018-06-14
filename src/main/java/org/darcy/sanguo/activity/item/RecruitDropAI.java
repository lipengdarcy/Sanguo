package org.darcy.sanguo.activity.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RecruitDropAI extends AbstractActivityItem {
	public static final int TYPE_GOOD = 1;
	public static final int TYPE_BETTER = 2;
	public static final int TYPE_BEST = 3;
	public static final int TYPE_TEN = 4;
	public Map<Integer, Boolean> effects = new HashMap();

	public Map<Integer, Integer> drops = new HashMap();

	public void addData(int id, boolean isEffect, int dropId) {
		if (!(this.effects.containsKey(Integer.valueOf(id)))) {
			this.effects.put(Integer.valueOf(id), Boolean.valueOf(isEffect));
			this.drops.put(Integer.valueOf(id), Integer.valueOf(dropId));
		}
	}

	public boolean isEffect(int type) {
		if (!(this.effects.containsKey(Integer.valueOf(type)))) {
			return false;
		}
		return ((Boolean) this.effects.get(Integer.valueOf(type))).booleanValue();
	}

	public int getDrop(int type) {
		if (!(this.drops.containsKey(Integer.valueOf(type)))) {
			return -1;
		}
		return ((Integer) this.drops.get(Integer.valueOf(type))).intValue();
	}

	public int getActivityId() {
		return 11;
	}

	public boolean containsKey(int key) {
		return this.effects.containsKey(Integer.valueOf(key));
	}

	public Set<Integer> keySet() {
		return this.effects.keySet();
	}
}
