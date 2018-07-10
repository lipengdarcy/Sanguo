package org.darcy.sanguo.randomshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.VipService;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.Vip;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbRandomShop;
import sango.packet.PbUp;

public class RandomShopService implements Service, PacketHandler, EventHandler {
	HashMap<Integer, Goods> goodsPool = new HashMap<Integer, Goods>();
	HashMap<Integer, Warehouse> warehousePool = new HashMap<Integer, Warehouse>();
	public static final int REFRESH_ITEM = 11003;
	public static final int REFRESH_PRICE = 20;
	public static final int REFRESH_CHERISH_PRICE = 100;
	public static final int CHERISH_OPEN_LEVEL = 40;
	public static final int CHERISH_OPEN_VIPLEVEL = 5;

	private void info(Player player, int shopType) {
		Goods goods;
		RandomShopRecord record = player.getRandomShop();
		record.check4Add();
		PbDown.RShopInfoRst.Builder rst = PbDown.RShopInfoRst.newBuilder();
		if (shopType == 0) {
			rst.setResult(true).setLeftFreeTimes(record.getLeftFreeTimes())
					.setLeftItemCount(player.getBags().getItemCount(11003)).setLeftSeconds(record.getLeftaAddSeconds())
					.setRefreshCost(20);
			if (record.getShop().size() == 0) {
				record.refresh();
			}
			for (Showcase show : record.getShop()) {
				PbRandomShop.RandomGood.Builder good = PbRandomShop.RandomGood.newBuilder();
				goods = (Goods) this.goodsPool.get(Integer.valueOf(show.getGoodsId()));
				int leftCount = goods.getCount() - show.getBuyCount();
				if (leftCount < 0)
					leftCount = 0;
				good.setIndex(show.getIndex()).setItemTemplateId(goods.getItemId()).setLimit(leftCount)
						.setMoneyType(goods.getMoneyType()).setPrice(goods.getPrice())
						.setGroupCount(goods.getGroupCount());
				rst.addGoods(good);
			}
		} else {
			CherishDiscount cd = getCherishDiscount();
			Vip vip = player.getVip();
			rst.setResult(true).setLeftFreeTimes(vip.cherishShopRefreshTimes - record.getCherishJewelRefreshTimes())
					.setLeftItemCount(-1).setLeftSeconds(record.getLeftCherishRefreshSeconds()).setRefreshCost(100);
			if (cd != null) {
				rst.setLeftFreeTimes(vip.cherishShopRefreshTimes + cd.getAddTime(player.getVip().level)
						- record.getCherishJewelRefreshTimes());
				if (cd.refreshCost > 0) {
					rst.setDiscountRefreshCost(cd.refreshCost);
				}
			}
			if (record.getCherishShop().size() == 0) {
				record.cherishRefreshIf(player);
			}
			for (Showcase show : record.getCherishShop()) {
				PbRandomShop.RandomGood.Builder good = PbRandomShop.RandomGood.newBuilder();
				goods = (Goods) this.goodsPool.get(Integer.valueOf(show.getGoodsId()));
				int leftCount = goods.getCount() - show.getBuyCount();
				if (leftCount < 0)
					leftCount = 0;
				good.setIndex(show.getIndex()).setItemTemplateId(goods.getItemId()).setLimit(leftCount)
						.setMoneyType(goods.getMoneyType()).setPrice(goods.getPrice())
						.setGroupCount(goods.getGroupCount());
				if (cd != null) {
					good.setDiscountPrice(cd.getDiscountPrice(goods));
				}
				rst.addGoods(good);
			}
			record.setAutoCherishRefresh(false);
		}
		player.send(2018, rst.build());
	}

	private void buyItem(Player player, int index, int shopType) {
		RandomShopRecord record = player.getRandomShop();
		Showcase show = record.getShowcase(shopType, index);

		PbDown.RShopBuyRst.Builder rst = PbDown.RShopBuyRst.newBuilder();
		rst.setResult(false).setIndex(index);
		if ((shopType == 1) && (player.getLevel() < 40) && (player.getVip().level < 5)) {
			rst.setErrInfo("尚未开启");
		} else if (show != null) {
			Goods goods = (Goods) this.goodsPool.get(Integer.valueOf(show.getGoodsId()));
			if (show.getBuyCount() < goods.getCount()) {
				int price = goods.getPrice();
				if (shopType == 1) {
					CherishDiscount cd = getCherishDiscount();
					if (cd != null) {
						price = cd.getDiscountPrice(goods);
					}
				}
				if (goods.getMoneyType() == 3) {
					if (player.getJewels() < price) {
						rst.setErrInfo("金币不足");
						player.send(2020, rst.build());
						return;
					}
					player.decJewels(price, "randomshop");
				} else if (goods.getMoneyType() == 7) {
					if (player.getSpiritJade() < price) {
						rst.setErrInfo("魂玉不足");
						player.send(2020, rst.build());
						return;
					}
					player.decSpiritJade(price, "randomshop");
				} else {
					rst.setErrInfo("未知货币需求，暂停销售");
					player.send(2020, rst.build());
					return;
				}
				ItemTemplate template = ItemService.getItemTemplate(goods.getItemId());
				show.setBuyCount(show.getBuyCount() + 1);
				if (Item.isCumulative(template.type)) {
					Item item = ItemService.generateItem(template, player);
					player.getBags().addItem(item, goods.getGroupCount(), "randomshop");
				} else {
					for (int i = 0; i < goods.getGroupCount(); ++i) {
						Item item = ItemService.generateItem(template, player);
						player.getBags().addItem(item, 1, "randomshop");
					}
				}
				rst.setReward(PbCommons.PbReward.newBuilder().setCount(goods.getGroupCount()).setTemplateId(template.id)
						.setType(0));
				rst.setResult(true);
			} else {
				rst.setErrInfo("购买的商品不够");
			}
		} else {
			rst.setErrInfo("购买的商品不存在");
		}

		player.send(2020, rst.build());
	}

	private void refresh(Player player, int shopType) {
		RandomShopRecord record = player.getRandomShop();
		PbDown.RShopRefresh.Builder rst = PbDown.RShopRefresh.newBuilder();

		if (shopType == 0) {
			if (record.getLeftFreeTimes() > 0) {
				record.setLeftFreeTimes(record.getLeftFreeTimes() - 1);
			} else if (player.getBags().getItemCount(11003) > 0) {
				player.getBags().removeItem(0, 11003, 1, "randomshoprefresh");
			} else {
				if (player.getJewels() >= 20) {
					Vip vip = player.getVip();
					if (record.getJewelRefreshTimes() < vip.randomShopRefreshTimes) {
						record.setJewelRefreshTimes(record.getJewelRefreshTimes() + 1);
						player.decJewels(20, "randomshoprefresh");
					}

					VipService.notifyVipBuyTimeLack(player, PbCommons.VipBuyTimeType.RANDOMSHOPREFRESH);
					return;
				}

				rst.setResult(false);
				rst.setErrInfo("元宝不足");
				player.send(2022, rst.build());
				return;
			}

			label152: Platform.getEventManager().addEvent(new Event(2060, new Object[] { player }));
			label368: record.refresh();
		} else {
			if ((player.getLevel() < 40) && (player.getVip().level < 5)) {
				rst.setResult(false);
				rst.setErrInfo("尚未开启");
				player.send(2022, rst.build());
				return;
			}
			CherishDiscount cd = getCherishDiscount();
			Vip vip = player.getVip();
			int refreshCount = vip.cherishShopRefreshTimes;
			int cost = 100;
			if (cd != null) {
				refreshCount += cd.getAddTime(player.getVip().level);
				if (cd.refreshCost > 0) {
					cost = cd.refreshCost;
				}
			}

			if (player.getJewels() >= cost) {
				if (record.getCherishJewelRefreshTimes() < refreshCount) {
					record.setCherishJewelRefreshTimes(record.getCherishJewelRefreshTimes() + 1);
					player.decJewels(cost, "randomshoprefresh");
				}
				VipService.notifyVipBuyTimeLack(player, PbCommons.VipBuyTimeType.CHERISHSHOPREFRESH);
				return;
			}

			rst.setResult(false);
			rst.setErrInfo("元宝不足");
			player.send(2022, rst.build());
			record.refreshCherishShop();
		}

		rst.setResult(true);
		player.send(2022, rst.build());
	}

	public void startup() {
		loadData();
		Platform.getPacketHanderManager().registerHandler(this);
		new Crontab("12 0 0", 1013);
		Platform.getEventManager().registerListener(this);
	}

	private void loadData() {
		List<Row> list = ExcelUtils.getRowList("randomshop.xls", 2, 2);
		for (Row row : list) {

			ArrayList<Integer> list1;

			int pos = 0;
			Goods goods = new Goods((int) row.getCell(pos++).getNumericCellValue(),
					row.getCell(pos++).getStringCellValue(), (int) row.getCell(pos++).getNumericCellValue(),
					(int) row.getCell(pos++).getNumericCellValue(), (int) row.getCell(pos++).getNumericCellValue(),
					(int) row.getCell(pos++).getNumericCellValue(), (int) row.getCell(pos++).getNumericCellValue(),
					(int) row.getCell(pos++).getNumericCellValue());
			this.goodsPool.put(Integer.valueOf(goods.getId()), goods);
		}

		List<Row> list2 = ExcelUtils.getRowList("randomshop.xls", 2, 1);
		for (Row row : list2) {

			int pos = 0;
			Warehouse warehouse = new Warehouse();
			warehouse.setId((int) row.getCell(pos++).getNumericCellValue());
			int count = (int) row.getCell(pos++).getNumericCellValue();
			for (int m = 0; m < count; ++m) {
				row.getCell(pos).setCellType(1);
				warehouse.addGoodList(m, row.getCell(pos++).getStringCellValue());
			}
			this.warehousePool.put(Integer.valueOf(warehouse.getId()), warehouse);

			for (Iterator<?> localIterator1 = warehouse.getGoodsLists().values().iterator(); localIterator1.hasNext();) {
				List<Integer> list_tmp = (List<Integer>) localIterator1.next();
				for (Integer gid : list_tmp) {
					if (this.goodsPool.get(gid) == null) {
						throw new IllegalArgumentException("配表错误：randomshop.xls 未知的商品ID:" + gid);
					}
				}
			}
		}

		List<Row> list3 = ExcelUtils.getRowList("randomshop.xls", 2, 0);
		for (Row row : list3) {
			int pos = 0;
			int id = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			RShopTemplate.addWarehouse(id, row.getCell(pos++).getStringCellValue());
			for (Iterator<Integer> it = ((ArrayList<Integer>) RShopTemplate.warehouses.get(Integer.valueOf(id))).iterator(); it
					.hasNext();) {
				int m = (Integer) it.next();
				if (this.warehousePool.get(m) == null) {
					throw new IllegalArgumentException("配表错误：randomshop.xls 未知的仓库ID:" + m);
				}
			}
		}

		List<Row> list4 = ExcelUtils.getRowList("randomshop.xls", 2, 3);
		for (Row row : list4) {
			int pos = 0;
			int id = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			RShopTemplate.addChrishWarehouse(id, row.getCell(pos++).getStringCellValue());
			for (Iterator<Integer> it = ((ArrayList<Integer>) RShopTemplate.cherishWarehouses.get(Integer.valueOf(id)))
					.iterator(); it.hasNext();) {
				int m = (Integer) it.next();
				if (this.warehousePool.get(m) == null)
					throw new IllegalArgumentException("配表错误：randomshop.xls 未知的仓库ID:" + m);
			}
		}

	}

	public static CherishDiscount getCherishDiscount() {
		CherishDiscount cd = (CherishDiscount) Platform.getEntityManager().getFromEhCache("persist",
				"CACHEKEY#CHERISDISCOUNT");
		if (cd != null) {
			long now = System.currentTimeMillis();
			if ((now > cd.start) && (now < cd.end)) {
				return cd;
			}
		}
		return null;
	}

	public void shutdown() {
	}

	public int[] getCodes() {
		return new int[] { 2019, 2017, 2021 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 14))) {
			return;
		}
		int shopType = 0;
		switch (packet.getPtCode()) {
		case 2019:
			PbUp.RShopBuy shop = PbUp.RShopBuy.parseFrom(packet.getData());
			int index = shop.getIndex();
			if (shop.hasShopType()) {
				shopType = shop.getShopType();
			}
			buyItem(player, index, shopType);
			break;
		case 2017:
			try {
				PbUp.RShopInfo info = PbUp.RShopInfo.parseFrom(packet.getData());
				if (info.hasShopType())
					shopType = info.getShopType();
			} catch (Exception localException) {
			}
			info(player, shopType);
			break;
		case 2021:
			try {
				PbUp.RShopRefreshC ref = PbUp.RShopRefreshC.parseFrom(packet.getData());
				if (ref.hasShopType())
					shopType = ref.getShopType();
			} catch (Exception localException1) {
			}
			refresh(player, shopType);
		case 2018:
		case 2020:
		}
	}

	public void reload() throws Exception {
		this.goodsPool.clear();
		this.warehousePool.clear();
		RShopTemplate.warehouses.clear();
		loadData();
	}

	public int[] getEventCodes() {
		return new int[] { 1013 };
	}

	public void handleEvent(Event event) {
		if (event.type == 1013)
			for (Player player : Platform.getPlayerManager().players.values())
				player.getRandomShop().cherishRefreshIf(player);
	}
}
