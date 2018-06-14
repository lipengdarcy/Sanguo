package org.darcy.sanguo.sync;

import com.google.protobuf.MessageOrBuilder;

import sango.packet.PbSync;

public class NumSync extends DataSync {
	int value;
	int type;

	public NumSync(int type, int value) {
		this.type = type;
		this.value = value;
	}

	public MessageOrBuilder genBuilder() {
		PbSync.PbNumSync.Builder builder = PbSync.PbNumSync.newBuilder();
		builder.setType(this.type);
		builder.setValue(this.value);
		return builder;
	}
}
