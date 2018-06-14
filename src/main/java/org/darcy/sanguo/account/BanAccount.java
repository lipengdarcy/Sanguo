package org.darcy.sanguo.account;

import java.io.Serializable;

import org.darcy.sanguo.Platform;

import sango.packet.PbGm;

public class BanAccount implements Serializable {
	private static final long serialVersionUID = 1042122807679049818L;
	private int id;
	private String accountId;
	private String name;
	private long start;
	private long banTime;
	private String reason;

	public static boolean isBan(String account) {
		BanAccount ban = (BanAccount) Platform.getEntityManager().getFromEhCache(BanAccount.class.getName(), account);

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

	public PbGm.BanAccountInfo genBanAccountInfo() {
		PbGm.BanAccountInfo.Builder b = PbGm.BanAccountInfo.newBuilder();
		b.setId(this.id);
		b.setAccount(this.accountId);
		b.setStart(this.start);
		b.setBanTime(this.banTime);
		b.setReason(this.reason);
		b.setName(this.name);
		return b.build();
	}
}
