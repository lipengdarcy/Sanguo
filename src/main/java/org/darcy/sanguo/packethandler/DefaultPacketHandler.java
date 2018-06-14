package org.darcy.sanguo.packethandler;

import org.darcy.sanguo.net.ClientSession;

import sango.packet.PbPacket;

public class DefaultPacketHandler implements PacketHandler {
	public int[] getCodes() {
		return new int[] { 1 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		switch (packet.getPtCode()) {
		case 1:
			test(session);
		}
	}

	public void test(ClientSession session) {
	}
}
