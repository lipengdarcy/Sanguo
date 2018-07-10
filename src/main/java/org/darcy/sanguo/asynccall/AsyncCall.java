package org.darcy.sanguo.asynccall;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import sango.packet.PbPacket;

/**
 * 异步调用抽象类
 */
public abstract class AsyncCall implements Runnable, CallBackable {
	protected ClientSession session;
	protected PbPacket.Packet packet;

	public AsyncCall(ClientSession session, PbPacket.Packet packet) {
		this.session = session;
		this.packet = packet;
	}

	public void run() {
		netOrDB();
		Platform.getCallBackManager().addCallBack(this);
	}

	public abstract void netOrDB();
}
