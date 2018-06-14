package org.darcy.sanguo.chat;

import sango.packet.PbCommons;

/**
 * 聊天信息
 * 
 */
public class ChatMessage {
	public PbCommons.ChatType type;
	public int sourceId;
	public String sourceName;
	public String message;

	public ChatMessage(PbCommons.ChatType type, int sourceId, String sourceName, String message) {
		this.type = type;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.message = message;
	}
}
