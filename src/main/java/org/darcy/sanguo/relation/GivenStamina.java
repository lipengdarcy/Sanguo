package org.darcy.sanguo.relation;

import org.darcy.sanguo.Platform;

import sango.packet.PbRelation;

public class GivenStamina {
	int playerId;
	long time;

	public GivenStamina(int playerId, long time) {
		this.playerId = playerId;
		this.time = time;
	}

	public PbRelation.FriendTimeUnit.Builder genPb() {
		PbRelation.FriendTimeUnit.Builder rst = PbRelation.FriendTimeUnit.newBuilder();
		rst.setMiniUser(Platform.getPlayerManager().getMiniPlayer(this.playerId).genMiniUser())
				.setTime(RelationService.getTimeStr(this.time));
		return rst;
	}
}
