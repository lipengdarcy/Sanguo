package org.darcy.sanguo.pay;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.PayService;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class OrderIdGeneAsyncCall extends AsyncCall {
	Player player;
	String orderId;
	String channel;
	int itemId;
	int errorNum = 0;

	public OrderIdGeneAsyncCall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.GetOrderId get = PbUp.GetOrderId.parseFrom(packet.getData());
		this.channel = get.getPayChannel();
		this.itemId = get.getItemId();

		if ((this.channel == null) || (this.channel.length() < 1) || (this.itemId == 0))
			throw new IllegalArgumentException();
	}

	public void callback() {
		PbDown.PayGetOrderIdRst.Builder rst = PbDown.PayGetOrderIdRst.newBuilder();
		if (this.errorNum == 0) {
			if (this.orderId == null)
				rst.setResult(false).setErrInfo("服务器繁忙，请稍后再试");
			else
				rst.setResult(true).setOrderId(this.orderId);
		} else {
			int surplus;
			if (this.errorNum == 1) {
				surplus = this.player.getActivityRecord().getMonthCardSurplus(1001);
				rst.setResult(false)
						.setErrInfo(MessageFormat.format("在{0}天后可再次购买。", new Object[] { Integer.valueOf(surplus) }));
			} else if (this.errorNum == 2) {
				surplus = this.player.getActivityRecord().getMonthCardSurplus(1002);
				rst.setResult(false)
						.setErrInfo(MessageFormat.format("在{0}天后可再次购买。", new Object[] { Integer.valueOf(surplus) }));
			} else if (this.errorNum == 3) {
				rst.setResult(false).setErrInfo("您尚未购买月卡，无法升级季卡");
			}
		}
		this.player.send(2158, rst.build());
	}

	public void netOrDB() {
		PayItem item = (PayItem) ((HashMap) PayService.pays.get(this.channel)).get(Integer.valueOf(this.itemId));
		if (item == null) {
			return;
		}
		if (item.isMonthCard) {
			if (this.itemId == 1001) {
				if (!(this.player.getActivityRecord().isBuyMonthCard(1001)))
					this.errorNum = 1;
				return;
			}
			if ((this.itemId == 1002) && (this.player.getActivityRecord().isBuyMonthCard(1002))) {
				this.errorNum = 2;
				return;
			}
		}

		int count = 100;
		while (count-- > 0) {
			try {
				this.orderId = PayService.generateOrderId();
				Receipt r = new Receipt();
				r.setOrderId(this.orderId);
				r.setPid(this.player.getId());
				r.setChannel(this.channel);
				r.setCoGoodsId(item.coGoodsId);
				r.setGoodsId(item.goodsId);
				r.setPrice(item.price);
				r.setState(0);
				r.setUpdateTime(new Date());
				((DbService) Platform.getServiceManager().get(DbService.class)).add(r);
				count = -100;
			} catch (Exception e) {
				Platform.getLog().logWarn(e);
			}
		}

		if (count != -101) {
			this.orderId = null;
			Platform.getLog().logError("generate orderid failed.");
		}
	}
}
