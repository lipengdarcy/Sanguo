package org.darcy.gate.net;

import java.util.concurrent.ArrayBlockingQueue;

import org.darcy.gate.packet.DefaultPacketHandler;

import com.google.protobuf.GeneratedMessage;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import sango.packet.PbPacket;
import sango.packet.PbPacket.Packet;

public class ClientSession {
	public static final long DISCONNECTING_TIME = 240000L;
	public static final int STATE_CONNECTING = 0;
	public static final int STATE_DISCONNECT = 1;
	protected long lastActiveTime;
	protected ArrayBlockingQueue<PbPacket.Packet> queue = new ArrayBlockingQueue<Packet>(2048);
	private int state;//用户session状态，0：掉线，1：在线
	private int id;
	private ChannelHandlerContext ctx;

	public ClientSession(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.id = ClientSessionManager.getNewId();
		this.lastActiveTime = System.currentTimeMillis();
		this.state = 0;
	}

	public int getId() {
		return this.id;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getLastActiveTime() {
		return this.lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public void disconnect() {
		this.state = 1;
	}

	public boolean isDisconnect() {
		return (this.state == 0);
	}

	public ChannelFuture close() {
		return this.ctx.close();
	}

	public void addPacket(PbPacket.Packet packet) {
		this.queue.add(packet);
		this.lastActiveTime = System.currentTimeMillis();
	}

	public boolean update() {
		PbPacket.Packet packet = null;
		if (System.currentTimeMillis() - this.lastActiveTime >= 240000L) {
			disconnect();
		}
		while ((packet = (PbPacket.Packet) this.queue.poll()) != null) {
			try {
				long time = System.currentTimeMillis();
				DefaultPacketHandler.handlePacket(this, packet);
				long l1 = System.currentTimeMillis() - time;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void send(int ptCode, GeneratedMessage msg) {
		if (!(isDisconnect())) {
			PbPacket.Packet opt = PbPacket.Packet.newBuilder().setPtCode(ptCode).setData(msg.toByteString()).build();
			this.ctx.writeAndFlush(opt);
			System.out.println("Sent ptCode:" + ptCode + " " + " packet:" + msg);
		}
	}
}
