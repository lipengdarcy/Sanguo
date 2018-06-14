package org.darcy.sanguo.activity;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbExchange;

public class ActivityExchange {
	public int id;
	public Reward reward;
	public boolean online;
	public int countType;
	public int count;
	public List<Reward> costs = new ArrayList();

	public List<Reward> getCost(int count) {
		List list = new ArrayList();
		for (Reward cost : this.costs) {
			Reward tmp = cost.copy();
			tmp.count *= count;
			list.add(tmp);
		}
		return list;
	}

	public PbExchange.ActivityExchangeData genActivityExchangeData(int count) {
		PbExchange.ActivityExchangeData.Builder b = PbExchange.ActivityExchangeData.newBuilder();
		b.setId(this.id);
		b.setReward(this.reward.genPbReward());
		b.setTotal(this.count);
		b.setCount(count);
		b.setCountType(PbExchange.ExchangeCountType.valueOf(this.countType));
		for (Reward r : this.costs) {
			b.addCost(r.genPbReward());
		}
		return b.build();
	}
}
