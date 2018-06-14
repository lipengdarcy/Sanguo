package org.darcy.sanguo.sync;

import com.google.protobuf.MessageOrBuilder;

import sango.packet.PbSync;

public class ActivityCanGetSync extends DataSync {
	boolean canGet;

	public ActivityCanGetSync(boolean can) {
		this.canGet = can;
	}

	public MessageOrBuilder genBuilder() {
		PbSync.PbActivityCanGet.Builder builder = PbSync.PbActivityCanGet.newBuilder();
		builder.setCan(this.canGet);
		return builder;
	}
}
