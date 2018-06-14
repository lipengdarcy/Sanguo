package org.darcy.sanguo.boss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.union.League;

import sango.packet.PbDown;

public class BossRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = -9041443995786534048L;
	private static final int version = 2;

	@Deprecated
	public static final int INSPIRE_CD = 60;
	public static final int RESURGENCE_CD = 60;
	public static final int RESURGENCE_COST = 20;

	@Deprecated
	public static final int SILVER_INSPIRE_COST = 10000;
	public static final int GOLD_INSPIRE_COST = 100;
	public static final int GOLD_INSPIRE_PRESTIGE = 300;

	@Deprecated
	public static final int MAX_ATK_ADD_RATE = 10000;
	public static final int INSPIRE_ATK_ADD = 300;
	public static final int INSPIRE_MAX_COUNT = 40;
	public static final int GOLD_INSPIRE_CD = 60;
	private long lastDieTime;

	@Deprecated
	private long silverInspireTime;
	private int chanllengeTimes;
	private int totalDamage;

	@Deprecated
	private int atkAddRate;
	private boolean die;
	private int inspireCount;
	private long goldInspireTime;

	public void refresh() {
		this.lastDieTime = 0L;
		this.silverInspireTime = 0L;
		this.chanllengeTimes = 0;
		this.atkAddRate = 0;
		this.die = false;
		this.inspireCount = 0;
		this.goldInspireTime = 0L;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();
			this.lastDieTime = in.readLong();
			this.silverInspireTime = in.readLong();
			this.chanllengeTimes = in.readInt();
			this.totalDamage = in.readInt();
			this.atkAddRate = in.readInt();
			this.die = in.readBoolean();
			if (version > 1) {
				this.inspireCount = in.readInt();
				this.goldInspireTime = in.readLong();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(2);
		out.writeLong(this.lastDieTime);
		out.writeLong(this.silverInspireTime);
		out.writeInt(this.chanllengeTimes);
		out.writeInt(this.totalDamage);
		out.writeInt(this.atkAddRate);
		out.writeBoolean(this.die);

		out.writeInt(this.inspireCount);
		out.writeLong(this.goldInspireTime);
	}

	public void clearCheck() {
		if ((this.totalDamage != 0) && (this.chanllengeTimes == 0))
			this.totalDamage = 0;
	}

	public int getLeftResurgenceSeconds() {
		if (!(this.die)) {
			return -1;
		}
		long last = System.currentTimeMillis() - this.lastDieTime;
		int left = 60 - (int) (last / 1000L);
		if (left < 0) {
			if (this.die) {
				this.die = false;
				this.lastDieTime = 0L;
			}
			return 0;
		}
		return left;
	}

	@Deprecated
	public int getLeftInspireTime() {
		return 0;
	}

	public int getLeftGoldInspireTime() {
		long last = System.currentTimeMillis() - this.goldInspireTime;
		int left = 60 - (int) (last / 1000L);
		if (left < 0) {
			return 0;
		}
		return left;
	}

	public void inspire(Player p) {
		p.decJewels(100, "inspire");
		p.addPrestige(300, "inspire");
		this.inspireCount += 1;
		League l = Platform.getLeagueManager().getLeagueByPlayerId(p.getId());
		if (l != null) {
			l.getInfo().addWorldBossInspireCount();
		}
		this.goldInspireTime = System.currentTimeMillis();
		pushInspire(l, p);
	}

	private void pushInspire(League l, Player player) {
		PbDown.BossInspireLeagueInfoRst rst = PbDown.BossInspireLeagueInfoRst.newBuilder().setResult(true)
				.setName(player.getName()).setAtkAddRate(getAddAtkRate(player)).build();
		BossManager m = Platform.getBossManager();
		Set<Player> players = m.getPool();
		for (Player p : players)
			if (l.isMember(p.getId()))
				p.send(1368, rst);
	}

	public int getAddAtkRate(Player p) {
		int count = this.inspireCount;
		League l = Platform.getLeagueManager().getLeagueByPlayerId(p.getId());
		if (l != null) {
			count = l.getInfo().getWorldBossInspireCount();
		}
		return (count * 300);
	}

	public long getLastDieTime() {
		return this.lastDieTime;
	}

	@Deprecated
	public long getSilverInspireTime() {
		return this.silverInspireTime;
	}

	public int getChanllengeTimes() {
		return this.chanllengeTimes;
	}

	public int getTotalDamage() {
		return this.totalDamage;
	}

	@Deprecated
	public int getAtkAddRate() {
		return this.atkAddRate;
	}

	public void setAtkAddRate(int atkAddRate) {
		this.atkAddRate = atkAddRate;
	}

	public void setLastDieTime(long lastDieTime) {
		this.lastDieTime = lastDieTime;
	}

	public void setSilverInspireTime(long silverInspireTime) {
		this.silverInspireTime = silverInspireTime;
	}

	public void setChanllengeTimes(int chanllengeTimes) {
		this.chanllengeTimes = chanllengeTimes;
	}

	public void setTotalDamage(int totalDamage) {
		this.totalDamage = totalDamage;
	}

	public boolean isDie() {
		return this.die;
	}

	public void setDie(boolean die) {
		this.die = die;
	}

	public int getInspireCount() {
		return this.inspireCount;
	}

	public void setInspireCount(int inspireCount) {
		this.inspireCount = inspireCount;
	}

	public int getBlobId() {
		return 11;
	}
}
