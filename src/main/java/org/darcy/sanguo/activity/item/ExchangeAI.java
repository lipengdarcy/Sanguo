package org.darcy.sanguo.activity.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.activity.ActivityExchange;

public class ExchangeAI extends AbstractActivityItem {
	public Map<Integer, ActivityExchange> exchanges = new HashMap<Integer, ActivityExchange>();

	public void addExchange(int id, ActivityExchange ae) {
		if (!(this.exchanges.containsKey(Integer.valueOf(id))))
			this.exchanges.put(Integer.valueOf(id), ae);
	}

	public int getActivityId() {
		return 1;
	}

	public boolean containsKey(int key) {
		return this.exchanges.containsKey(Integer.valueOf(key));
	}

	public Set<Integer> keySet() {
		return this.exchanges.keySet();
	}
}
