package org.darcy.sanguo.activity.item;

import java.util.Set;

public class MapDropAI extends AbstractActivityItem {
	public int dropId;

	public int getActivityId() {
		return 8;
	}

	public boolean containsKey(int key) {
		return false;
	}

	public Set<Integer> keySet() {
		return null;
	}
}
