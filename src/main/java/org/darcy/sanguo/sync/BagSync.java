package org.darcy.sanguo.sync;

import org.darcy.sanguo.bag.BagGrid;

import com.google.protobuf.MessageOrBuilder;

import sango.packet.PbSync;

public class BagSync extends DataSync {
	public static final int OPT_UPDATE = 0;
	public static final int OPT_ADD = 1;
	public static final int OPT_DELETE = 2;
	int bagType;
	BagGrid grid;
	int optType;

	public BagSync(int bagType, BagGrid grid, int optType) {
		this.bagType = bagType;
		this.grid = grid;
		this.optType = optType;
	}

	public MessageOrBuilder genBuilder() {
		PbSync.PbBagSync.Builder builder = PbSync.PbBagSync.newBuilder();
		builder.setBagType(this.bagType);
		builder.setOptType(this.optType);
		builder.setGrid(this.grid.genPbBagGrid());
		return builder;
	}
}
