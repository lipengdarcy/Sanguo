package org.darcy.sanguo.union;

import java.util.Map;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbLeague;

public class LeagueNormalGoods {
	public int id;
	public Reward item;
	public int count;
	public int cost;
	public int shopLevel;
	public boolean refresh;

	public PbLeague.LeagueNormalGoods genLeagueNormalGoods(LeagueMember lm) {
		PbLeague.LeagueNormalGoods.Builder b = PbLeague.LeagueNormalGoods.newBuilder();
		b.setId(this.id);
		b.setCost(this.cost);
		b.setShopLevel(this.shopLevel);
		Map map = lm.getNormalGoodsRecord();
		if (!(map.containsKey(Integer.valueOf(this.id)))) {
			map.put(Integer.valueOf(this.id), Integer.valueOf(0));
		}
		int num = ((Integer) map.get(Integer.valueOf(this.id))).intValue();
		if (num > this.count) {
			num = this.count;
			map.put(Integer.valueOf(this.id), Integer.valueOf(num));
		}
		b.setCount(this.count - num);
		b.setGoods(this.item.genPbReward());
		return b.build();
	}
}
