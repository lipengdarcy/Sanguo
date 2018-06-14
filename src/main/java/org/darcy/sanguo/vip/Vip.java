package org.darcy.sanguo.vip;

import java.util.HashMap;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.service.VipService;

public class Vip {
	public int level;
	public int charge;
	public List<Reward> vipBag;
	public int vipBagId;
	public int vipBagPrice;
	public List<Reward> dayRewards;
	public int proMapTimes;
	public int moneyTrialTimes;
	public int warriorTrialTimes;
	public int treasureTrialTimes;
	public int touchGoldTimes;
	public int trainTimes;
	public int mapTimes;
	public int randomShopRefreshTimes;
	public int divineRewardRefreshTimes;
	public int cherishShopRefreshTimes;
	public int towerLife;
	public int worldCompetitionTimes;
	public HashMap<Integer, Integer> shopLimits;

	public Vip getNext() {
		if (this.level + 1 < VipService.vips.size()) {
			return ((Vip) VipService.vips.get(this.level + 1));
		}
		return null;
	}
}
