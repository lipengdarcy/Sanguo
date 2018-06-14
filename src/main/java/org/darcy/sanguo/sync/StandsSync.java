package org.darcy.sanguo.sync;

import org.darcy.sanguo.hero.Warriors;

import com.google.protobuf.MessageOrBuilder;

import sango.packet.PbSync;

public class StandsSync extends DataSync {
	public static final int STANDS_STAGE = 1;
	public static final int STANDS_STANDS = 2;
	public static final int STANDS_FELLOW = 3;
	int type;
	Warriors warriors;

	public StandsSync(int type, Warriors warriors) {
		this.type = type;
		this.warriors = warriors;
	}

	public MessageOrBuilder genBuilder() {
		PbSync.PbStandsSync.Builder builder = PbSync.PbStandsSync.newBuilder();
		builder.setType(this.type);
		if (this.type == 2)
			builder.setStands(this.warriors.genStandStruct());
		else if (this.type == 1)
			builder.setStages(this.warriors.genStageStruct());
		else if (this.type == 3) {
			builder.setFriends(this.warriors.genFriendStruct());
		}
		return builder;
	}
}
