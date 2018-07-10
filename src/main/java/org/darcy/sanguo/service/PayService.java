package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.BanCharge;
import org.darcy.sanguo.account.chujian.ChujianManager;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.pay.CodeCheckAsyncCall;
import org.darcy.sanguo.pay.OrderIdGeneAsyncCall;
import org.darcy.sanguo.pay.PayCheckAsyncCall;
import org.darcy.sanguo.pay.PayItem;
import org.darcy.sanguo.pay.PayPullAsyncCall;
import org.darcy.sanguo.pay.PayRecord;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.util.DBUtil;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.Vip;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class PayService implements Service, PacketHandler, EventHandler {
	public static final String GLOBAL_CHARGE_REWARD_KEY = "globalChargeRewardKey";
	public static char[] dic = new char[36];
	private static Random random = new Random();
	public static final int ORDERID_LENGTH = 16;
	public static HashMap<String, HashMap<Integer, PayItem>> pays = new HashMap<String, HashMap<Integer, PayItem>>();

	private static ConcurrentHashMap<String, PayRecord> repeatRecords = new ConcurrentHashMap<String, PayRecord>();

	private static Set<Integer> chargePlayerIds = new HashSet<Integer>();

	private static int chargePlayerCount = 0;

	public static int getPrice(String channel, String coGoodsId) {
		HashMap<Integer, PayItem> items = (HashMap<Integer, PayItem>) pays.get(channel);
		for (PayItem item : items.values()) {
			if (item.coGoodsId.equals(coGoodsId)) {
				return item.price;
			}
		}
		return 0;
	}

	public static int getGoodsId(String channel, String coGoodsId) {
		HashMap<Integer, PayItem> items = (HashMap<Integer, PayItem>) pays.get(channel);
		for (PayItem item : items.values()) {
			if (item.coGoodsId.equals(coGoodsId)) {
				return item.goodsId;
			}
		}
		return 0;
	}

	public static boolean isOverdue(String orderId) {
		boolean rst = false;
		PayRecord record = (PayRecord) repeatRecords.get(orderId);
		if (record == null) {
			record = new PayRecord();
			record.count = 1;
			record.startTime = System.currentTimeMillis();
			repeatRecords.put(orderId, record);
		} else if (record.isOverDue()) {
			repeatRecords.remove(orderId);
			rst = true;
		}

		return rst;
	}

	public int[] getCodes() {
		return new int[] { 2153, 2159, 2157, 2155 };
	}

	private void loadData() throws Exception {
		int pos = 0;
		List<Row> list = ExcelUtils.getRowList("pay.xls", 2);
		for (Row row : list) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}

			PayItem pay = new PayItem();
			String channel = row.getCell(pos++).getStringCellValue();
			pay.coGoodsId = row.getCell(pos++).getStringCellValue();
			pay.goodsId = (int) row.getCell(pos++).getNumericCellValue();
			pay.name = row.getCell(pos++).getStringCellValue();
			pay.count = (int) row.getCell(pos++).getNumericCellValue();
			pay.price = (int) row.getCell(pos++).getNumericCellValue();
			pay.firstRecomend = ((int) row.getCell(pos++).getNumericCellValue() == 1);
			pay.firstGive = (int) row.getCell(pos++).getNumericCellValue();
			pay.nomalGive = (int) row.getCell(pos++).getNumericCellValue();
			pay.iconId = row.getCell(pos++).getStringCellValue();
			pay.isMonthCard = ((int) row.getCell(pos++).getNumericCellValue() == 1);
			pay.isShowInPayPage = ((int) row.getCell(pos++).getNumericCellValue() == 1);
			pay.isEveryDayFirst = ((int) row.getCell(pos++).getNumericCellValue() == 1);
			pay.isLimit = ((int) row.getCell(pos++).getNumericCellValue() == 1);

			if (pay.isLimit) {
				pay.limitStart = row.getCell(pos++).getDateCellValue();
				pay.limitEnd = row.getCell(pos++).getDateCellValue();
				pay.limitGive = (int) row.getCell(pos++).getNumericCellValue();
			} else {
				pos += 3;
			}
			HashMap<Integer, PayItem> maps = (HashMap<Integer, PayItem>) pays.get(channel);
			if (maps == null) {
				maps = new HashMap<Integer, PayItem>();
				pays.put(channel, maps);
			}
			maps.put(Integer.valueOf(pay.goodsId), pay);
		}

		ChujianManager.loadData();
	}

	private void initChargePlayer() {
		List<?> list = DBUtil.getChargePlayerList();
		if ((list != null) && (list.size() > 0)) {
			for (Iterator<?> localIterator = list.iterator(); localIterator.hasNext();) {
				int id = ((Integer) localIterator.next()).intValue();
				chargePlayerIds.add(Integer.valueOf(id));
			}
		}

		Integer count = (Integer) Platform.getEntityManager().getFromEhCache("common", "globalChargeRewardKey");
		if (count == null)
			chargePlayerCount = chargePlayerIds.size();
		else
			chargePlayerCount = count.intValue();
	}

	public static void addChargePlayerId(int playerId) {
		if (!(chargePlayerIds.contains(Integer.valueOf(playerId)))) {
			int count = chargePlayerCount + 1;
			setChargePlayerCount(count);
			chargePlayerIds.add(Integer.valueOf(playerId));

			if (RewardService.globalChargeRewards.containsKey(Integer.valueOf(count)))
				for (Player p : Platform.getPlayerManager().players.values())
					if (p != null)
						Function.notifyMainNum(p, 41, 1);
		}
	}

	public static int getChargePlayerCount() {
		return chargePlayerCount;
	}

	public static void setChargePlayerCount(int count) {
		if (count != chargePlayerCount)
			chargePlayerCount = count;
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 2153:
			CodeCheckAsyncCall call = new CodeCheckAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2159:
			if (BanCharge.isBan(player.getId())) {
				player.send(2160, PbDown.PayCheckRst.newBuilder().setResult(false).setErrInfo("充值异常，请联系客服处理").build());
				return;
			}
			PayCheckAsyncCall check = new PayCheckAsyncCall(session, packet);
			Platform.getThreadPool().execute(check);
			break;
		case 2157:
			if (BanCharge.isBan(player.getId())) {
				player.send(2158,
						PbDown.PayGetOrderIdRst.newBuilder().setResult(false).setErrInfo("充值异常，请联系客服处理").build());
				return;
			}
			OrderIdGeneAsyncCall gen = new OrderIdGeneAsyncCall(session, packet);
			Platform.getThreadPool().execute(gen);
			break;
		case 2155:
			PbUp.PayInfo info = PbUp.PayInfo.parseFrom(packet.getData());
			String channel = "DEFAULT";
			if (info.hasPayChannel()) {
				channel = info.getPayChannel();
			}
			info(player, channel);
		case 2154:
		case 2156:
		case 2158:
		}
	}

	public int getLimitLeftTime(String channel) {
		HashMap<Integer, PayItem> items = (HashMap<Integer, PayItem>) pays.get(channel);
		if (items != null) {
			Date now = new Date();
			long rst = -1L;
			for (PayItem item : items.values()) {
				if ((!(item.isLimit)) || (!(item.limitStart.before(now))) || (!(item.limitEnd.after(now))))
					continue;
				long dif = item.limitEnd.getTime() - now.getTime();
				if ((rst == -1L) || (dif < rst)) {
					rst = dif;
				}

			}

			if (rst != -1L) {
				return (int) rst;
			}
		}
		return -1;
	}

	public static boolean isLimit(PayItem item) {
		Date now = new Date();

		return ((!(item.isLimit)) || (!(item.limitStart.before(now))) || (!(item.limitEnd.after(now))));
	}

	public static boolean isLimit(PayItem item, Date date) {
		return ((!(item.isLimit)) || (!(item.limitStart.before(date))) || (!(item.limitEnd.after(date))));
	}

	private void info(Player player, String channel) {
		PayRecord.loginRefresh(player);
		PbDown.PayInfoRst.Builder rst = PbDown.PayInfoRst.newBuilder();
		rst.setResult(true);
		try {
			Vip vip = player.getVip();
			Vip next = vip.getNext();
			if (next == null) {
				rst.setVipLevel(vip.level).setCharge(player.getCharge()).setMaxCharge(-1);
			} else {
				Reward r;
				Iterator<?> localIterator1;
				rst.setVipLevel(vip.level).setCharge(player.getCharge()).setMaxCharge(next.charge);
				if (!(player.isAlreadyCharge())) {
					for (localIterator1 = VipService.firstPayRewards.iterator(); localIterator1.hasNext();) {
						r = (Reward) localIterator1.next();
						rst.addRewards(r.genPbReward());
					}
				} else {
					for (localIterator1 = next.vipBag.iterator(); localIterator1.hasNext();) {
						r = (Reward) localIterator1.next();
						rst.addRewards(r.genPbReward());
					}
				}
			}

			Set<?> set = player.getPool().getIntegers(6);
			for (PayItem pay : ((HashMap<Integer, PayItem>) pays.get(channel)).values()) {
				if (pay.isShowInPayPage) {
					PbCommons.PayUnit.Builder unit = PbCommons.PayUnit.newBuilder();
					unit.setCoGoodsId(pay.coGoodsId).setCount(pay.count).setGoodsId(pay.goodsId).setPrice(pay.price)
							.setIconId(pay.iconId).setMonthcard(pay.isMonthCard);
					if ((!(set.contains(Integer.valueOf(pay.goodsId)))) && (pay.firstGive > 0)) {
						if (pay.firstRecomend)
							unit.setRecommend(true);
						else {
							unit.setRecommend(false);
						}

						if (pay.firstGive > 0)
							unit.setTips(MessageFormat.format("加送{0}元宝（双倍）",
									new Object[] { Integer.valueOf(pay.firstGive) }));
						else if (pay.nomalGive > 0) {
							unit.setTips(
									MessageFormat.format("加送{0}元宝", new Object[] { Integer.valueOf(pay.nomalGive) }));
						}

						if (pay.isEveryDayFirst)
							unit.setTodayFirst(true);
						else {
							unit.setTodayFirst(false);
						}
					} else if (isLimit(pay)) {
						unit.setRecommend(false);
						unit.setLimit(true);
						if (pay.limitGive > 0) {
							unit.setTips(
									MessageFormat.format("加送{0}元宝", new Object[] { Integer.valueOf(pay.limitGive) }));
						}
						if (!(rst.hasLimitLeftTime()))
							rst.setLimitLeftTime(getLimitLeftTime(channel));
					} else {
						unit.setRecommend(false);

						if (pay.nomalGive > 0) {
							unit.setTips(
									MessageFormat.format("加送{0}元宝", new Object[] { Integer.valueOf(pay.nomalGive) }));
						}
					}

					if ((RewardService.superReward.containsKey(Integer.valueOf(pay.goodsId)))
							&& (!(player.getRewardRecord().isParticipatedSuperRewardActivity()))) {
						unit.setSuperReward(true);
						unit.setTips(
								MessageFormat.format("加送{0}元宝（三倍）", new Object[] { Integer.valueOf(pay.firstGive) }));
					}

					rst.addItems(unit);
				}
			}
		} catch (Exception e) {
			rst.setResult(true);
			Platform.getLog().logWarn(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2156, rst.build());
	}

	public void startup() throws Exception {
		loadData();
		initChargePlayer();
		int i = 0;
		for (; i < 10; ++i) {
			dic[i] = (char) (48 + i);
		}
		for (i = 0; i < 26; ++i) {
			dic[(i + 10)] = (char) (65 + i);
		}
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		if (Configuration.pushPay) {
			// 5", 1012);
		}

		Thread cj = new Thread(new ChujianManager(), "ChujianManager");
		cj.setDaemon(true);
		cj.start();
	}

	public static String generateOrderId() {
		return generateKey(Calc.box(Configuration.serverId, 4), 16);
	}

	private static String generateKey(String head, int length) {
		StringBuffer sb = new StringBuffer(head);
		while (sb.length() < length) {
			sb.append(dic[random.nextInt(dic.length)]);
		}

		return sb.toString().toUpperCase();
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		pays.clear();
		loadData();
	}

	public int[] getEventCodes() {
		return new int[] { 1012 };
	}

	public void handleEvent(Event event) {
		if ((event.type != 1012) || (!(Configuration.pushPay)))
			return;
		PayPullAsyncCall pull = new PayPullAsyncCall();
		Platform.getThreadPool().execute(pull);
	}
}
