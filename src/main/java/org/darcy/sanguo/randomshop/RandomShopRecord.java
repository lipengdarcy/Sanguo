package org.darcy.sanguo.randomshop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.Calc;

public class RandomShopRecord implements PlayerBlobEntity {
	public static final int SHOP_RANDOM = 0;
	public static final int SHOP_CHERISH = 1;
	private static final long serialVersionUID = -7046540351274997021L;
	private static final int version = 2;
	public static final int MAX_FREE_TIMES = 2;
	private ArrayList<Showcase> shop = new ArrayList();
	private HashMap<Integer, Integer> showRecords = new HashMap();
	private long lastAddTime = 0L;
	private int jewelRefreshTimes = 0;
	private int leftFreeTimes = 2;
	public static final int REFRESH_CD = 7200000;
	private ArrayList<Showcase> cherishShop = new ArrayList();

	private HashMap<Integer, Integer> cherishShowRecords = new HashMap();

	private long cherishLastRefreshTime = 0L;

	private int cherishJewelRefreshTimes = 0;
	private boolean autoCherishRefresh = false;

	public int getLeftCherishRefreshSeconds() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(11) >= 12) {
			cal.add(6, 1);
			cal.set(11, 12);
			cal.set(12, 0);
			cal.set(13, 0);
			cal.set(14, 0);

			return (int) ((cal.getTimeInMillis() - System.currentTimeMillis()) / 1000L);
		}

		cal.set(11, 12);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);

		return (int) ((cal.getTimeInMillis() - System.currentTimeMillis()) / 1000L);
	}

	public void cherishRefreshIf(Player player) {
		if ((player.getLevel() < 40) && (player.getVip().level < 5)) {
			return;
		}
		boolean refresh = false;
		Calendar yes = Calendar.getInstance();
		yes.add(6, -1);
		yes.set(11, 12);
		yes.set(12, 0);
		yes.set(13, 0);
		yes.set(14, 0);
		Calendar today = Calendar.getInstance();
		today.set(11, 12);
		today.set(12, 0);
		today.set(13, 0);
		today.set(14, 0);

		if (this.cherishLastRefreshTime == 0L)
			refresh = true;
		else if (this.cherishLastRefreshTime < yes.getTimeInMillis())
			refresh = true;
		else if ((this.cherishLastRefreshTime < today.getTimeInMillis())
				&& (System.currentTimeMillis() >= today.getTimeInMillis())) {
			refresh = true;
		}

		if (refresh) {
			refreshCherishShop();
			this.cherishLastRefreshTime = System.currentTimeMillis();
			this.autoCherishRefresh = true;
		}
	}

	private void readObject(ObjectInputStream in) {
		try {
			int key;
			int value;
			int v = in.readInt();
			int shopCount = in.readInt();
			this.shop = new ArrayList();
			for (int i = 0; i < shopCount; ++i) {
				this.shop.add(Showcase.readObject(in));
			}
			int showCount = in.readInt();
			this.showRecords = new HashMap();
			for (int i = 0; i < showCount; ++i) {
				key = in.readInt();
				value = in.readInt();
				this.showRecords.put(Integer.valueOf(key), Integer.valueOf(value));
			}
			this.lastAddTime = in.readLong();
			this.leftFreeTimes = in.readInt();
			this.jewelRefreshTimes = in.readInt();

			this.cherishShop = new ArrayList();
			this.cherishShowRecords = new HashMap();
			if (v < 2) {
				return;
			}
			this.cherishLastRefreshTime = in.readLong();
			this.cherishJewelRefreshTimes = in.readInt();
			shopCount = in.readInt();
			int i;
			for (i = 0; i < shopCount; ++i) {
				this.cherishShop.add(Showcase.readObject(in));
			}
			showCount = in.readInt();
			for (i = 0; i < showCount; ++i) {
				key = in.readInt();
				value = in.readInt();
				this.cherishShowRecords.put(Integer.valueOf(key), Integer.valueOf(value));
			}
			this.autoCherishRefresh = in.readBoolean();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Showcase s;
		Integer i;
		out.writeInt(2);

		out.writeInt(this.shop.size());
		for (Iterator localIterator = this.shop.iterator(); localIterator.hasNext();) {
			s = (Showcase) localIterator.next();
			s.writeObject(out);
		}

		out.writeInt(this.showRecords.size());
		for (Iterator localIterator = this.showRecords.keySet().iterator(); localIterator.hasNext();) {
			i = (Integer) localIterator.next();
			out.writeInt(i.intValue());
			out.writeInt(((Integer) this.showRecords.get(i)).intValue());
		}

		out.writeLong(this.lastAddTime);
		out.writeInt(this.leftFreeTimes);
		out.writeInt(this.jewelRefreshTimes);

		out.writeLong(this.cherishLastRefreshTime);
		out.writeInt(this.cherishJewelRefreshTimes);
		out.writeInt(this.cherishShop.size());
		for (Iterator localIterator = this.cherishShop.iterator(); localIterator.hasNext();) {
			Showcase a = (Showcase) localIterator.next();
			a.writeObject(out);
		}

		out.writeInt(this.cherishShowRecords.size());
		for (Iterator localIterator = this.cherishShowRecords.keySet().iterator(); localIterator.hasNext();) {
			i = (Integer) localIterator.next();
			out.writeInt(i.intValue());
			out.writeInt(((Integer) this.cherishShowRecords.get(i)).intValue());
		}
		out.writeBoolean(this.autoCherishRefresh);
	}

	public void check4Add() {
		if (getLeftaAddSeconds() == 0) {
			long now = System.currentTimeMillis();
			long diff = now - this.lastAddTime;
			long times = diff / 7200000L;
			this.lastAddTime = getLast2Hour(now);
			if ((this.leftFreeTimes < 2) && (times > 0L)) {
				RandomShopRecord tmp50_49 = this;
				tmp50_49.leftFreeTimes = (int) (tmp50_49.leftFreeTimes + times);
				if (this.leftFreeTimes > 2)
					this.leftFreeTimes = 2;
			}
		}
	}

	public int getLeftaAddSeconds() {
		long now = System.currentTimeMillis();
		long rst = 7200000L - (now - this.lastAddTime);
		if (rst < 0L) {
			rst = 0L;
		} else if (rst > 7200000L) {
			rst = 7200000L;
			this.lastAddTime = getLast2Hour(now);
		}

		return ((int) rst / 1000);
	}

	private long getLast2Hour(long now) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		int hour = cal.get(11) / 2 * 2;
		cal.set(11, hour);
		cal.set(12, 0);
		cal.set(13, 0);
		return cal.getTimeInMillis();
	}

	public int getLeftFreeTimes() {
		return this.leftFreeTimes;
	}

	public void setLeftFreeTimes(int time) {
		this.leftFreeTimes = time;
	}

	public ArrayList<Showcase> getShop() {
		return this.shop;
	}

	public HashMap<Integer, Integer> getShowRecords() {
		return this.showRecords;
	}

	public long getLastRefreshTime() {
		return this.lastAddTime;
	}

	public int getJewelRefreshTimes() {
		return this.jewelRefreshTimes;
	}

	public int getCherishJewelRefreshTimes() {
		return this.cherishJewelRefreshTimes;
	}

	public void setCherishJewelRefreshTimes(int cherishJewelRefreshTimes) {
		this.cherishJewelRefreshTimes = cherishJewelRefreshTimes;
	}

	public void setJewelRefreshTimes(int jewelRefreshTimes) {
		this.jewelRefreshTimes = jewelRefreshTimes;
	}

	public void setShowRecords(HashMap<Integer, Integer> showRecords) {
		this.showRecords = showRecords;
	}

	public void setLastRefreshTime(long lastRefreshTime) {
		this.lastAddTime = lastRefreshTime;
	}

	public void setShop(ArrayList<Showcase> shop) {
		this.shop = shop;
	}

	public Showcase getShowcase(int shopType, int index) {
		Showcase s;
		Iterator localIterator;
		if (shopType == 1) {
			for (localIterator = this.cherishShop.iterator(); localIterator.hasNext();) {
				s = (Showcase) localIterator.next();
				if (s.getIndex() == index)
					return s;
			}
		} else {
			for (localIterator = this.shop.iterator(); localIterator.hasNext();) {
				s = (Showcase) localIterator.next();
				if (s.getIndex() == index) {
					return s;
				}
			}
		}

		return null;
	}

	public boolean isAutoCherishRefresh() {
		return this.autoCherishRefresh;
	}

	public void setAutoCherishRefresh(boolean autoCherishRefresh) {
		this.autoCherishRefresh = autoCherishRefresh;
	}

	public ArrayList<Showcase> getCherishShop() {
		return this.cherishShop;
	}

	public void setCherishShop(ArrayList<Showcase> cherishShop) {
		this.cherishShop = cherishShop;
	}

	public long getCherishLastRefreshTime() {
		return this.cherishLastRefreshTime;
	}

	public void setCherishLastRefreshTime(long cherishLastRefreshTime) {
		this.cherishLastRefreshTime = cherishLastRefreshTime;
	}

	protected Object clone() throws CloneNotSupportedException {
		RandomShopRecord r = new RandomShopRecord();
		r.lastAddTime = this.lastAddTime;
		for (Integer i : this.showRecords.keySet()) {
			r.showRecords.put(i, (Integer) this.showRecords.get(i));
		}
		for (Showcase s : this.shop) {
			Showcase sc = new Showcase(s.getIndex(), s.getBuyCount(), s.getWarehouseIndex(), s.getGoodsId());
			r.shop.add(sc);
		}
		return r;
	}

	public void newDayRefresh() {
		this.jewelRefreshTimes = 0;
		this.cherishJewelRefreshTimes = 0;
		refresh();
	}

	public synchronized void refreshCherishShop() {
		RandomShopService rss = (RandomShopService) Platform.getServiceManager().get(RandomShopService.class);
		for (Integer warehousesId : RShopTemplate.cherishWarehouses.keySet()) {
			ArrayList warehouses = (ArrayList) RShopTemplate.cherishWarehouses.get(warehousesId);
			if (warehouses != null) {
				int warehouseIndex = 0;
				Showcase showcase = getShowcase(1, warehousesId.intValue());
				if (showcase != null) {
					warehouseIndex = showcase.getWarehouseIndex();
				}

				int count = 500;
				while (count-- > 0) {
					if (warehouseIndex < warehouses.size()) {
						Integer warehouseId = (Integer) warehouses.get(warehouseIndex);
						Warehouse warehouse = (Warehouse) rss.warehousePool.get(warehouseId);
						Integer srcds = (Integer) this.cherishShowRecords.get(warehousesId);
						int showRecord = 0;
						if (srcds != null) {
							showRecord = srcds.intValue();
						}
						int randomIndex = getRandomIndex(showRecord, warehouse.getGoodsLists().size());
						if (randomIndex != -1) {
							ArrayList list = (ArrayList) warehouse.getGoodsLists().get(Integer.valueOf(randomIndex));
							int goodsId = ((Integer) list.get(Calc.nextInt(list.size()))).intValue();
							Showcase show = new Showcase(warehousesId.intValue(), 0, warehouseIndex, goodsId);
							this.cherishShop.remove(showcase);
							this.cherishShop.add(show);
							showRecord |= 1 << randomIndex;
							this.cherishShowRecords.put(warehousesId, Integer.valueOf(showRecord));
							break;
						}
					}

					++warehouseIndex;

					this.cherishShowRecords.put(warehousesId, Integer.valueOf(0));
					if (warehouseIndex < warehouses.size())
						continue;
					warehouseIndex = 0;
				}
			}

			Collections.shuffle(this.cherishShop);
		}
	}

	public synchronized void refresh() {
		RandomShopService rss = (RandomShopService) Platform.getServiceManager().get(RandomShopService.class);
		for (Integer warehousesId : RShopTemplate.warehouses.keySet()) {
			ArrayList warehouses = (ArrayList) RShopTemplate.warehouses.get(warehousesId);
			if (warehouses != null) {
				int warehouseIndex = 0;
				Showcase showcase = getShowcase(0, warehousesId.intValue());
				if (showcase != null) {
					warehouseIndex = showcase.getWarehouseIndex();
				}

				int count = 500;
				while (count-- > 0) {
					if (warehouseIndex < warehouses.size()) {
						Integer warehouseId = (Integer) warehouses.get(warehouseIndex);
						Warehouse warehouse = (Warehouse) rss.warehousePool.get(warehouseId);
						Integer srcds = (Integer) this.showRecords.get(warehousesId);
						int showRecord = 0;
						if (srcds != null) {
							showRecord = srcds.intValue();
						}
						int randomIndex = getRandomIndex(showRecord, warehouse.getGoodsLists().size());
						if (randomIndex != -1) {
							ArrayList list = (ArrayList) warehouse.getGoodsLists().get(Integer.valueOf(randomIndex));
							int goodsId = ((Integer) list.get(Calc.nextInt(list.size()))).intValue();
							Showcase show = new Showcase(warehousesId.intValue(), 0, warehouseIndex, goodsId);
							this.shop.remove(showcase);
							this.shop.add(show);
							showRecord |= 1 << randomIndex;
							this.showRecords.put(warehousesId, Integer.valueOf(showRecord));
							break;
						}
					}

					++warehouseIndex;

					this.showRecords.put(warehousesId, Integer.valueOf(0));
					if (warehouseIndex < warehouses.size())
						continue;
					warehouseIndex = 0;
				}
			}

			Collections.shuffle(this.shop);
		}
	}

	private int getRandomIndex(int record, int size) {
		if (Calc.count1(record) > size) {
			return -1;
		}
		int mask = 0;
		for (int i = 0; i < size; ++i) {
			mask |= 1 << i;
		}
		int left = mask ^ record;
		int count = Calc.count1(left);
		if (count <= 0) {
			return -1;
		}
		int r = Calc.nextInt(count);
		int pos = 0;
		while (pos < 32) {
			if ((left & 1 << pos) != 0) {
				--r;
				if (r < 0) {
					return pos;
				}
			}
			++pos;
		}

		return -1;
	}

	public int getBlobId() {
		return 10;
	}
}
