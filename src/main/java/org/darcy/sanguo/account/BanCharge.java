package org.darcy.sanguo.account;

import java.io.Serializable;

import org.darcy.sanguo.Platform;

import sango.packet.PbGm;

public class BanCharge implements Serializable {
	private static final long serialVersionUID = 5199282231580507931L;
	private int id;
	private String accountId;
	private String name;
	private long start;
	private long banTime;
	private String reason;

	public static boolean isBan(int id) {
		BanCharge ban = (BanCharge) Platform.getEntityManager().getFromEhCache(BanCharge.class.getName(),
				Integer.valueOf(id));

		return (ban == null);
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PbGm.BanChargeInfo genBanChargeInfo() {
		PbGm.BanChargeInfo.Builder b = PbGm.BanChargeInfo.newBuilder();
		b.setId(this.id);
		b.setAccount(this.accountId);
		b.setStart(this.start);
		b.setBanTime(this.banTime);
		b.setReason(this.reason);
		b.setName(this.name);
		return b.build();
	}
}
