package org.darcy.sanguo.pay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.chujian.ChujianLog;
import org.darcy.sanguo.account.chujian.ChujianManager;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.asynccall.AsyncUpdater;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.PayService;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.util.PlayerLockService;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONObject;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class PayCheckAsyncCall extends AsyncCall {
	private static final String YINGYONGBAO = "YINGYONGBAO";
	Player player;
	String orderId;
	Receipt receipt;
	List<String> parameters;
	PayCheckGateRst checkRst;

	public PayCheckAsyncCall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.PayCheck check = PbUp.PayCheck.parseFrom(packet.getData());
		this.orderId = check.getOrderId();
		this.parameters = check.getParametersList();

		Platform.getLog().logPayCheck(this.player, this.orderId, this.parameters);
	}

	public void callback() {
		synchronized (PlayerLockService.getLock(this.player.getId())) {
			PbDown.PayCheckRst.Builder rst = PbDown.PayCheckRst.newBuilder();
			rst.setResult(false).setOrderId(this.orderId);

			if (this.receipt == null) {
				rst.setErrInfo("不存在的订单");
				rst.setPayResult(PbDown.PayCheckRst.PayResult.ERROR_ORDER);
			} else if (this.receipt.getState() == 1) {
				rst.setErrInfo("重复验证");
				rst.setPayResult(PbDown.PayCheckRst.PayResult.ERROR_ORDER);
			} else if (this.checkRst == null) {
				rst.setErrInfo("订单处理中，请稍后");
				rst.setPayResult(PbDown.PayCheckRst.PayResult.RETRY);
			} else if (this.checkRst.getState() == 2) {
				rst.setErrInfo("订单错误");
				rst.setPayResult(PbDown.PayCheckRst.PayResult.ERROR_ORDER);
			} else if (this.checkRst.getState() == 4) {
				if (PayService.isOverdue(this.orderId)) {
					rst.setErrInfo("订单尝试次数过多");
					rst.setPayResult(PbDown.PayCheckRst.PayResult.ERROR_ORDER);
				} else {
					rst.setErrInfo("订单处理中，请稍后");
					rst.setPayResult(PbDown.PayCheckRst.PayResult.RETRY);
				}
			} else if (this.receipt.getPid() != this.player.getId()) {
				rst.setErrInfo("订单不属于自己");
				rst.setPayResult(PbDown.PayCheckRst.PayResult.ERROR_ORDER);
			} else {
				PayItem item = (PayItem) ((HashMap) PayService.pays.get(this.receipt.getChannel()))
						.get(Integer.valueOf(this.receipt.getGoodsId()));
				if (item == null) {
					rst.setErrInfo("订单不属于自己");
					rst.setPayResult(PbDown.PayCheckRst.PayResult.RETRY);
				} else if (this.checkRst.getState() == 1) {
					checkSuccess(this.receipt, this.player, item);
					rst.setResult(true);
					rst.setPayResult(PbDown.PayCheckRst.PayResult.SUCCESS);
				}

			}

			this.player.send(2160, rst.build());
		}
	}

	public void netOrDB() {
		try {
			synchronized (PlayerLockService.getLock(this.player.getId() + "lock")) {
				String code;
				this.receipt = ((Receipt) ((DbService) Platform.getServiceManager().get(DbService.class))
						.get(Receipt.class, this.orderId));
				if (this.receipt == null) {
					return;
				}
				if (this.receipt.getState() == 1) {
					return;
				}
				PayCheckGateInfo info = new PayCheckGateInfo();
				info.setOrderId(this.orderId);
				info.setPlayerId(this.player.getId());
				info.setPayChannel(this.receipt.getChannel());
				if (this.receipt.getChannel().contains("APP_STORE")) {
					String r = new String(Base64.getDecoder()
							.decode(((String) this.parameters.get(0)).getBytes(Charset.forName("utf-8"))), "utf-8");
					JSONObject json = JSONObject.fromObject(r);
					r = new String(Base64.getDecoder()
							.decode(json.getString("purchase-info").getBytes(Charset.forName("utf-8"))), "utf-8");
					json = JSONObject.fromObject(r);

					info.setCoGoodsId(json.getString("product-id"));
					this.receipt.setGoodsId(PayService.getGoodsId(this.receipt.getChannel(), info.getCoGoodsId()));
					this.receipt.setCoOrderId(json.getString("transaction-id"));
					this.receipt.setPrice(PayService.getPrice(this.receipt.getChannel(), info.getCoGoodsId()));
					info.setPrice(PayService.getPrice(this.receipt.getChannel(), info.getCoGoodsId()));
					Receipt rc = DBUtil.getReceipt(this.receipt.getCoOrderId());
					if (rc == null)
						return;
					this.receipt.setState(1);
					Platform.getLog().logWarn("checked receipt:" + r);
					return;
				}

				info.setCoGoodsId(this.receipt.getCoGoodsId());
				info.setPrice(this.receipt.getPrice());

				if ((this.parameters != null) && (this.parameters.size() > 0)) {
					if ("YINGYONGBAO".equals(this.receipt.getChannel())) {
						switch (this.receipt.getGoodsId()) {
						case 1001:
							this.parameters.add("1");
							break;
						case 1002:
							this.parameters.add("2");
							break;
						default:
							this.parameters.add("0");
						}

					}

					info.setParameters(this.parameters);
				}

				URL httpUrl = new URL(Configuration.billingAdd + "/paycheck");
				HttpURLConnection http = (HttpURLConnection) httpUrl.openConnection();
				http.setConnectTimeout(5000);
				http.setReadTimeout(30000);
				http.setRequestMethod("POST");
				http.setDoInput(true);
				http.setDoOutput(true);
				PrintStream out = new PrintStream(http.getOutputStream(), true, "UTF-8");
				out.print(JSONObject.fromObject(info));
				out.flush();
				StringBuffer result = new StringBuffer();
				InputStream is = http.getInputStream();
				InputStreamReader read = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(read);

				while ((code = reader.readLine()) != null) {
					result.append(code);
				}
				String message = result.toString();
				JSONObject json = JSONObject.fromObject(message);
				this.checkRst = ((PayCheckGateRst) JSONObject.toBean(json, PayCheckGateRst.class));
			}
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}

	public static void checkSuccess(Receipt receipt, Player player, PayItem item) {
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
			Platform.getEventManager().addEvent(new Event(2065, new Object[] { player, Integer.valueOf(item.count) }));
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
