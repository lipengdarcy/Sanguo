package org.darcy.sanguo.randomshop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Showcase {
	private int index;
	private int buyCount;
	private int warehouseIndex;
	private int goodsId;

	public Showcase(int index, int buyCount, int warehouseIndex, int goodsId) {
		this.index = index;
		this.buyCount = buyCount;
		this.warehouseIndex = warehouseIndex;
		this.goodsId = goodsId;
	}

	public int getWarehouseIndex() {
		return this.warehouseIndex;
	}

	public void setWarehouseIndex(int warehouseIndex) {
		this.warehouseIndex = warehouseIndex;
	}

	public int getIndex() {
		return this.index;
	}

	public int getBuyCount() {
		return this.buyCount;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}

	public int getGoodsId() {
		return this.goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.index);
		out.writeInt(this.buyCount);
		out.writeInt(this.warehouseIndex);
		out.writeInt(this.goodsId);
	}

	public static Showcase readObject(ObjectInputStream in) throws IOException {
		int i = in.readInt();
		int b = in.readInt();
		int w = in.readInt();
		int g = in.readInt();
		Showcase s = new Showcase(i, b, w, g);
		return s;
	}
}
