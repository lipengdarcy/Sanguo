package org.darcy.sanguo.activity.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbActivity;

public class TurnPlateAI extends AbstractActivityItem {
	public static final int ONE_COST = 50;
	public static final int TEN_COST = 500;
	public static final int TOTAL_COUNT = 1500;
	public static final int RAFFLE = 11014;
	public Map<Integer, Map.Entry<Integer, Reward>> rewards = new HashMap();

	public Map<Integer, Map<Integer, Integer>> counts = new HashMap();

	public Map<Integer, List<Reward>> scoreRewards = new HashMap();

	public int getRoundTotalCount(int round) {
		if (!(this.counts.containsKey(Integer.valueOf(round)))) {
			round = 1;
		}
		int result = 0;
		Map<Integer, Integer> total = (Map) this.counts.get(Integer.valueOf(round));
		for (Integer count : total.values()) {
			result += count.intValue();
		}

		return result;
	}

	public List<PbActivity.TurnPlateItem> genTurnPlateItemList() {
		List list = new ArrayList();
		Iterator itx = this.rewards.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			Reward r = (Reward) ((Map.Entry) this.rewards.get(Integer.valueOf(id))).getValue();
			PbActivity.TurnPlateItem.Builder b = PbActivity.TurnPlateItem.newBuilder();
			b.setId(id);
			b.setReward(r.genPbReward());
			b.setKey(((Integer) ((Map.Entry) this.rewards.get(Integer.valueOf(id))).getKey()).intValue());
			list.add(b.build());
		}
		return list;
	}

	public int getActivityId() {
		return 9;
	}

	public boolean containsKey(int key) {
		return false;
	}

	public Set<Integer> keySet() {
		return null;
	}
}
