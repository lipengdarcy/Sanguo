package org.darcy.sanguo.net;

import java.util.concurrent.ArrayBlockingQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.Account;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.Calc;

import com.google.protobuf.GeneratedMessage;

import io.netty.channel.ChannelHandlerContext;
import sango.packet.PbPacket;
import sango.packet.PbPacket.Packet;

public class ClientSession {
	public static final long DISCONNECTING_TIME = 240000L;
	public static final int STATE_CONNECTING = 0;
	public static final int STATE_DISCONNECT = 1;
	protected long lastActiveTime;
	protected Account account;
	protected ArrayBlockingQueue<PbPacket.Packet> queue = new ArrayBlockingQueue<Packet>(100);
	private int state;
	private int id;
	private int pid;
	private ChannelHandlerContext ctx;

	public ClientSession() {
	}

	public ClientSession(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.id = Calc.getTempId();
		this.lastActiveTime = System.currentTimeMillis();
		this.state = 0;
	}

	public void setPlayer(Player player) {
		this.pid = player.getId();
	}

	public Player getPlayer() {
		Player player = Platform.getPlayerManager().getPlayerById(this.pid);

		return player;
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
		return ((System.currentTimeMillis() - this.lastActiveTime < 240000L) && (this.state == 0));
	}

	public void close() {
		// this.ctx.close();
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void addPacket(PbPacket.Packet packet) {
		this.queue.add(packet);
		this.lastActiveTime = System.currentTimeMillis();
	}

	public boolean update() {
		PbPacket.Packet packet = null;
		while ((packet = (PbPacket.Packet) this.queue.poll()) != null) {
			try {
				long time = System.currentTimeMillis();
				Platform.getPacketHanderManager().handlePacket(this, packet);
				long dis = System.currentTimeMillis() - time;
				if (dis > 20L)
					Platform.getLog().logWarn("Packet process too long：" + packet.getPtCode() + "time：" + dis);
			} catch (Throwable e) {
				Platform.getLog().logError(e);
			}
		}
		return false;
	}

	public void send(int ptCode, GeneratedMessage msg) {
		if (!(isDisconnect())) {
			PbPacket.Packet.Builder opt = PbPacket.Packet.newBuilder().setPtCode(ptCode);
			if (msg != null) {
				opt.setData(msg.toByteString());
			}
			this.ctx.write(opt);
			AutoFlusher.addFlush(this.ctx);
			Platform.getLog().logNet("SEND " + ptCode + ":" + this.ctx.channel().remoteAddress() + "\npacket :" + msg);
		}
	}

	public int getPid() {
		return this.pid;
	}

	public String getIp() {
		return getIp(this.ctx);
	}

	public static String getIp(ChannelHandlerContext ctx) {
		String ip = null;
		if (ctx != null) {
			ip = ctx.channel().remoteAddress().toString();
			ip = ip.substring(1, ip.indexOf(":"));
		}
		return ip;
	}
}
