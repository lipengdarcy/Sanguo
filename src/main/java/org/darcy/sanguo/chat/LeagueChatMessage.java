package org.darcy.sanguo.chat;

import sango.packet.PbCommons;

public class LeagueChatMessage extends ChatMessage {
	public int leagueId;

	public LeagueChatMessage(PbCommons.ChatType type, int sourceId, String sourceName, String message, int leagueId) {
		super(type, sourceId, sourceName, message);
		this.leagueId = leagueId;
	}
}
