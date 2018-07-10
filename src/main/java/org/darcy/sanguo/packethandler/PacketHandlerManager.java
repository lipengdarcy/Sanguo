package org.darcy.sanguo.packethandler;

import java.util.HashMap;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;

import sango.packet.PbPacket;

public class PacketHandlerManager {
	protected HashMap<Integer, PacketHandler> handlers = new HashMap<Integer, PacketHandler>();

	public PacketHandler getHandler(short code) {
		return ((PacketHandler) this.handlers.get(Integer.valueOf(code)));
	}

	public PacketHandlerManager() {
		registerHandler(new DefaultPacketHandler());
	}

	public void registerHandler(PacketHandler handler) {
		int[] codes = handler.getCodes();
		for (int i = 0; i < codes.length; ++i)
			this.handlers.put(Integer.valueOf(codes[i]), handler);
	}

	public void handlePacket(ClientSession clientSession, PbPacket.Packet packet) {
		PacketHandler handler = (PacketHandler) this.handlers.get(Integer.valueOf(packet.getPtCode()));
		try {
			if (handler != null) {
				handler.handlePacket(clientSession, packet);
				return;
			}
			Platform.getLog().logWorld("error ptcode:" + packet.getPtCode());
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}
}
