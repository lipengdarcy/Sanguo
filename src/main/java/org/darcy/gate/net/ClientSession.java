package org.darcy.gate.net;

import java.util.concurrent.ArrayBlockingQueue;

import org.darcy.gate.packet.DefaultPacketHandler;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.Account;
import org.darcy.sanguo.player.Player;

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

	private int state;// 用户session状态，0：掉线，1：在线
	private int id; // session id
	
	private ChannelHandlerContext ctx;
	
	//这2个属性是非网管属性
	private int playerId;// 玩家id
	protected Account account;

	public ClientSession(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.id = ClientSessionManager.getNewId();
		this.lastActiveTime = System.currentTimeMillis();
		this.state = 0;
	}

	public void setPlayer(Player player) {
		this.playerId = player.getId();
	}

	public Player getPlayer() {
		Player player = Platform.getPlayerManager().getPlayerById(this.playerId);
		return player;
	}
	
	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

	public int getPlayerId() {
		return this.playerId;
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
