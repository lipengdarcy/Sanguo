package org.darcy.sanguo.sync;

import com.google.protobuf.MessageOrBuilder;

import sango.packet.PbSync;

public class BagSizeSync extends DataSync {
	int bagType;
	int num;

	public BagSizeSync(int bagType, int num) {
		this.bagType = bagType;
		this.num = num;
	}

	public MessageOrBuilder genBuilder() {
		PbSync.PbBagSizeSync.Builder builder = PbSync.PbBagSizeSync.newBuilder();
		builder.setBagType(this.bagType);
		builder.setNum(this.num);
		return builder;
	}
}
