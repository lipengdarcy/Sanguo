package org.darcy.sanguo.account;

import java.io.Serializable;

import org.darcy.sanguo.Platform;

import sango.packet.PbGm;

public class BanIp implements Serializable {
	private static final long serialVersionUID = -237698296883026422L;
	private String ip;
	private long start;
	private long banTime;
	private String reason;

	public static boolean isBan(String ip) {
		BanIp bi = (BanIp) Platform.getEntityManager().getFromEhCache(BanIp.class.getName(), ip);

		return (bi == null);
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getStart() {
		return this.start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getBanTime() {
		return this.banTime;
	}

	public void setBanTime(long banTime) {
		this.banTime = banTime;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public PbGm.BanIpInfo genBanIpInfo() {
		PbGm.BanIpInfo.Builder b = PbGm.BanIpInfo.newBuilder();
		b.setIp(this.ip);
		b.setStart(this.start);
		b.setBanTime(this.banTime);
		b.setReason(this.reason);
		return b.build();
	}
}
