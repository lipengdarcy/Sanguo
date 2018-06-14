package org.darcy.sanguo.activity.item;

import java.util.Set;

/**
 * 游戏活动接口
 */
public abstract interface ActivityItem {
	public abstract int getActivityId();

	public abstract boolean containsKey(int paramInt);

	public abstract Set<Integer> keySet();
}
