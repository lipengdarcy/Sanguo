package org.darcy.sanguo.charge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.chujian.ChujianLog;
import org.darcy.sanguo.account.chujian.ChujianManager;
import org.darcy.sanguo.asynccall.AsyncUpdater;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.pay.PayItem;
import org.darcy.sanguo.pay.PayRecord;
import org.darcy.sanguo.pay.Receipt;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.PayService;
import org.darcy.sanguo.updater.Updatable;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.util.PlayerLockService;

public class ChargeService implements Updatable {
	public static final Comparator<PayItem> COMPARATOR = new Comparator<PayItem>() {
		public int compare(PayItem o1, PayItem o2) {
			return (o2.count - o1.count);
		}
	};

	public static int MONTH_CARD_ID = 1001;
	public static int MONTH_RMB = 18;
	public static int CHARGE_RATE = 10;
	public static String CHANNEL = "DEFAULT";

	public boolean update() {
		List<Charge> charges = Platform.getEntityManager().query(Charge.class, "from Charge c where c.done = 0", new Object[0]);

		for (Charge charge : charges) {
			String playerName = charge.getName();
			Player player = DBUtil.getPlayerByName(playerName);
			if (player == null)
				Platform.getLog().logSystem("没找到玩家: " + playerName);
			else
				try {
					player = Platform.getPlayerManager().getPlayer(player.getId(), true, true);

					if (charge.getRmb() > 0) {
						rmb(player, charge);
					}

					charge.setDone(1);
					Platform.getEntityManager().update(charge);
				} catch (Exception e) {
					Platform.getLog().logSystem("玩家" + playerName + "充值失败! ");
				}
		}
		return false;
	}

	private void rmb(Player player, Charge charge) {
		List items = new ArrayList(((HashMap) PayService.pays.get(CHANNEL)).values());

		Collections.sort(items, COMPARATOR);

		int count = charge.getRmb() * CHARGE_RATE;

		int money = charge.getRmb() * 100;

		PayItem item = getItem(items, money);
		if (item == null) {
			normalCharge(player, count);
			return;
		}
		do {
			success(player, CHANNEL, item.goodsId);
			money -= item.price;
			item = getItem(items, money);
		} while (item != null);

		normalCharge(player, money / 100 * CHARGE_RATE);

		if (charge.getRmb() > MONTH_RMB)
			player.getActivityRecord().addMonthCard(MONTH_CARD_ID, player, CHANNEL);
	}

	private PayItem getItem(List<PayItem> items, int money) {
		PayItem item = null;
		for (int i = 0; i < items.size(); ++i) {
			PayItem temp = (PayItem) items.get(i);
			if (money >= temp.price) {
				item = temp;
				break;
			}
		}
		return item;
	}

	private void normalCharge(Player player, int count) {
		if (count <= 0) {
			return;
		}

		synchronized (PlayerLockService.getLock(player.getId())) {
			PayRecord.loginRefresh(player);

			if (!(player.isAlreadyCharge())) {
				Platform.getEventManager().addEvent(new Event(2065, new Object[] { player, Integer.valueOf(count) }));
			}
			player.addCharge(count, true);
			player.addJewels(count, "charge");
			PayService.addChargePlayerId(player.getId());
		}
	}

	public static Receipt genRecipt(Player player, String channel, PayItem item) {
		String orderId = PayService.generateOrderId();
		Receipt r = new Receipt();
		r.setOrderId(orderId);
		r.setPid(player.getId());
		r.setChannel(channel);
		r.setCoGoodsId(item.coGoodsId);
		r.setGoodsId(item.goodsId);
		r.setPrice(item.price);
		r.setState(0);
		r.setUpdateTime(new Date());
		return r;
	}

	public static void success(Player player, String channel, int goodsId) {
		synchronized (PlayerLockService.getLock(player.getId())) {
			PayItem item = (PayItem) ((HashMap) PayService.pays.get(channel)).get(Integer.valueOf(goodsId));

			Receipt receipt = genRecipt(player, channel, item);

			receipt.setCoOrderId("先不填");
			receipt.setState(1);
			receipt.setUpdateTime(new Date());
			int count = item.count;

			PayRecord.loginRefresh(player);

			Set set = player.getPool().getIntegers(6);
			if ((set.contains(Integer.valueOf(item.goodsId))) || (item.firstGive <= 0)) {
				if (PayService.isLimit(item, receipt.getCreateTime()))
					count += item.limitGive;
				else
					count += item.nomalGive;
			} else {
				set.add(Integer.valueOf(item.goodsId));
				if (item.firstGive > 0)
					count += item.firstGive;
				else {
					count += item.nomalGive;
				}
			}

			if (!(player.isAlreadyCharge())) {
				Platform.getEventManager()
						.addEvent(new Event(2065, new Object[] { player, Integer.valueOf(item.count) }));
			}
			player.addCharge(item.count, true);
			player.addJewels(count, "charge");

			AsyncUpdater saver = new AsyncUpdater(receipt);
			Platform.getThreadPool().execute(saver);

			if (item.isMonthCard) {
				player.getActivityRecord().addMonthCard(item.goodsId, player, receipt.getChannel());
			}
			PayService.addChargePlayerId(player.getId());

			player.getRewardRecord().superRewardHandler(item.goodsId);

			Platform.getLog().logCharge(player, receipt);
			ChujianManager.addLog(new ChujianLog(receipt.getChannel(), player.getAccountId(), player.getLevel(),
					receipt.getOrderId(), String.valueOf(receipt.getGoodsId()), receipt.getPrice() / 100.0F, count,
					Configuration.serverId));
		}
	}
}
