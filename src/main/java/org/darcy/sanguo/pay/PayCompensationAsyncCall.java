package org.darcy.sanguo.pay;

import java.util.HashMap;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerSaveCall;
import org.darcy.sanguo.service.PayService;
import org.darcy.sanguo.util.PlayerLockService;

public class PayCompensationAsyncCall extends AsyncCall {
	Player player;
	Receipt receipt;
	String orderId;
	int errorNum = 0;

	public PayCompensationAsyncCall(String orderId) {
		super(null, null);
		this.orderId = orderId;
	}

	public void callback() {
		synchronized (PlayerLockService.getLock(this.player.getId())) {
			StringBuilder sb = new StringBuilder("pay compestation");
			sb.append(", orderId:").append(this.orderId).append(", player:").append(this.player.getId())
					.append(", result:");

			if (this.errorNum == 0) {
				PayItem item = (PayItem) ((HashMap) PayService.pays.get(this.receipt.getChannel()))
						.get(Integer.valueOf(this.receipt.getGoodsId()));
				if (item == null) {
					sb.append("item not exist, item:" + this.receipt.getGoodsId());
				} else {
					boolean success = true;
					if (item.isMonthCard) {
						if (item.goodsId == 1001) {
							if (this.player.getActivityRecord().isBuyMonthCard(1001)) {
								sb.append("already buy monthcard, please compensate manual!!!");
								success = false;
							}

						} else if ((item.goodsId == 1002) && (this.player.getActivityRecord().isBuyMonthCard(1002))) {
							sb.append("already buy quartercard, please compensate manual!!!");
							success = false;
						}
					}

					if (success) {
						PayCheckAsyncCall.checkSuccess(this.receipt, this.player, item);
						if (Platform.getPlayerManager().getPlayerById(this.player.getId()) == null) {
							PlayerSaveCall call = new PlayerSaveCall(this.player);
							Platform.getThreadPool().execute(call);
						}
						sb.append("success!!!");
					}
				}
			} else if (this.errorNum == 1) {
				sb.append("receipt null");
			} else if (this.errorNum == 2) {
				sb.append("receipt already success");
			} else if (this.errorNum == 3) {
				sb.append("player null");
			} else if (this.errorNum == 4) {
				sb.append("receipt and player not match");
			} else if (this.errorNum == 5) {
				sb.append("data exception");
			}

			Platform.getLog().logWarn(sb.toString());
		}
	}

	public void netOrDB() {
		try {
			this.receipt = ((Receipt) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Receipt.class,
					this.orderId));
			if (this.receipt == null) {
				this.errorNum = 1;
				return;
			}
			if (this.receipt.getState() == 1) {
				this.errorNum = 2;
				return;
			}
			this.player = Platform.getPlayerManager().getPlayer(this.receipt.getPid(), true, true);
			if (this.player != null)
				return;
			this.errorNum = 3;
			return;
		} catch (Exception e) {
			Platform.getLog().logError(e);
			this.errorNum = 5;
		}
	}
}
