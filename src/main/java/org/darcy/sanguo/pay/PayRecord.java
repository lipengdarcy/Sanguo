package org.darcy.sanguo.pay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.ActivityService;
import org.darcy.sanguo.service.PayService;

public class PayRecord {
	public final int MIN_COUNT = 5;

	public final long MIN_TIME = 1800000L;
	public int count;
	public long startTime;

	public boolean isOverDue() {
		if (this.startTime == 0L) {
			this.startTime = System.currentTimeMillis();
			return false;
		}

		this.count += 1;

		return ((System.currentTimeMillis() - this.startTime <= 1800000L) || (this.count < 5));
	}

	public static void loginRefresh(Player player) {
		long cur = System.currentTimeMillis();

		List giveTimes = ActivityService.firstGiveResetTime;
		for (int i = 0; i < giveTimes.size(); ++i) {
			Long refreshTime = (Long) giveTimes.get(i);
			if (refreshTime.longValue() < cur) {
				Map giveRecords = player.getActivityRecord().getFistGiveRecords();
				if ((giveRecords.get(refreshTime) != null) && (((Boolean) giveRecords.get(refreshTime)).booleanValue()))
					continue;
				player.getPool().getIntegers(6).clear();
				giveRecords.put(refreshTime, Boolean.valueOf(true));
				break;
			}

		}

		List packageTimes = ActivityService.firstPackageResetTime;
		for (int i = 0; i < packageTimes.size(); ++i) {
			Long refreshTime = (Long) packageTimes.get(i);
			if (refreshTime.longValue() < cur) {
				Map packageRecords = player.getActivityRecord().getFistPackageRecords();
				if ((packageRecords.get(refreshTime) != null)
						&& (((Boolean) packageRecords.get(refreshTime)).booleanValue()))
					continue;
				if ((player.getPool().getInt(21, 0) > 0) && (!(player.getPool().getBool(8, false)))) {
					packageRecords.put(refreshTime, Boolean.valueOf(true));
				} else {
					player.getPool().set(8, Boolean.valueOf(false));
					player.getPool().set(21, Integer.valueOf(0));
					packageRecords.put(refreshTime, Boolean.valueOf(true));
					return;
				}
			}
		}
	}

	public static void refresh(Player player) {
		Set set = player.getPool().getIntegers(6);
		for (HashMap<Integer, PayItem> maps : PayService.pays.values())
			for (PayItem pay : maps.values())
				if ((pay.isEveryDayFirst) && (set.contains(Integer.valueOf(pay.goodsId))))
					set.remove(Integer.valueOf(pay.goodsId));
	}
}
