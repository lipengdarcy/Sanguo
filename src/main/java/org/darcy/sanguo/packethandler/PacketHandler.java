package org.darcy.sanguo.packethandler;

import org.darcy.sanguo.net.ClientSession;

import sango.packet.PbPacket;

public abstract interface PacketHandler {
	public abstract int[] getCodes();

	public abstract void handlePacket(ClientSession paramClientSession, PbPacket.Packet paramPacket) throws Exception;
}
