package org.darcy.sanguo.player;

import java.util.Date;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.util.Calc;

import sango.packet.PbUser;

public class MiniPlayer {
	private int id;
	private String name;
	private int level;
	private String heroIds;
	private int btlCapability;
	private int[] heroList;
	private Date lastLogout;

	public MiniPlayer() {
	}

	public MiniPlayer(int id, String name, int level, String heroIds, int btlCapability, Date lastLogout) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.heroIds = heroIds;
		this.btlCapability = btlCapability;
		this.lastLogout = lastLogout;
	}

	public int[] getHeroList() {
		if (this.heroList == null) {
			this.heroList = Calc.split(this.heroIds, ",");
		}
		return this.heroList;
	}

	public void setHeroList(int[] heroList) {
		this.heroList = heroList;
	}

	public PbUser.MiniUser genMiniUser() {
		PbUser.MiniUser.Builder builder = PbUser.MiniUser.newBuilder();
		builder.setId(this.id);
		builder.setName(this.name);
		builder.setLevel(this.level);
		builder.setBtlCapability(this.btlCapability);
		builder.setTitileId(Platform.getTopManager().getTitleId(this.id));
		int[] heros = getHeroList();
		for (int i = 0; i < heros.length; ++i) {
			if (i == 0) {
				builder.setMainWarriorId(heros[i]);
			}
			builder.addWarriors(heros[i]);
		}
		League l = Platform.getLeagueManager().getLeagueByPlayerId(this.id);
		if (l != null) {
			builder.setLeague(l.getName());
		}
		return builder.build();
	}

	public String getHeroIds() {
		return this.heroIds;
	}

	public void setHeroIds(String heroIds) {
		this.heroIds = heroIds;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getLevel() {
		return this.level;
	}

	public int getBtlCapability() {
		return this.btlCapability;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setBtlCapability(int btlCapability) {
		this.btlCapability = btlCapability;
	}

	public Date getLastLogout() {
		return this.lastLogout;
	}

	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}

	public boolean isOnline() {
		try {
			return (Platform.getPlayerManager().getPlayer(this.id, false, false) == null);
		} catch (Exception e) {
		}
		return false;
	}
}
