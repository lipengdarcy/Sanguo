package org.darcy.sanguo.union.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.union.League;

import sango.packet.PbLeague;

public class City {
	public static final int COUNTRY_WEI = 1;
	public static final int COUNTRY_SHU = 2;
	public static final int COUNTRY_WU = 3;
	public static final int COUNTRY_QUN = 4;
	public static final int TYPE_CITY = 1;
	public static final int TYPE_TOWN = 2;
	public static final int TYPE_VILLAGE = 3;
	private int type;
	private int country;
	private int pid = -1;
	private int lid = -1;
	private int index = -1;
	private long timeMark = 0L;

	public void changeOwner(int lid, int pid) {
		this.pid = pid;
		this.lid = lid;
		this.timeMark = System.currentTimeMillis();
	}

	public long getTimeMark() {
		return this.timeMark;
	}

	public void setTimeMark(long timeMark) {
		this.timeMark = timeMark;
	}

	public int getRate() {
		return ((Integer) LeagueCombatService.cityRates.get(Integer.valueOf(this.type))).intValue();
	}

	public int getAvilableScore() {
		int rst = 0;
		long time = System.currentTimeMillis() - this.timeMark;
		if (time > 600000L) {
			rst = 600;
		}
		rst = (int) (time / 1000L);

		return (rst * ((Integer) LeagueCombatService.cityRates.get(Integer.valueOf(this.type))).intValue());
	}

	public PbLeague.LCCity.Builder genPb() {
		PbLeague.LCCity.Builder b = PbLeague.LCCity.newBuilder();
		b.setCityType(this.type).setCountryType(this.country).setIndex(this.index);
		if ((this.lid > 0) && (this.pid > 0)) {
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(this.pid);
			League league = Platform.getLeagueManager().getLeagueById(this.lid);
			if ((mini != null) && (league != null)) {
				b.setHeroId(getBestHeroId(mini)).setName(mini.getName()).setLeagueName(league.getName());
			} else {
				b.setHeroId(-1);
				b.setLeagueName("");
				b.setName("");
			}
		} else {
			b.setHeroId(-1);
			b.setName("");
			b.setLeagueName("");
		}

		return b;
	}

	private int getBestHeroId(MiniPlayer mini) {
		int rst = mini.getHeroList()[0];
		Player p = Platform.getPlayerManager().getPlayerById(mini.getId());
		if (p != null) {
			int max = 0;
			for (Warrior w : p.getWarriors().getStands()) {
				if ((w == null) || (w.getBtlCapa() <= max))
					continue;
				max = w.getBtlCapa();
				rst = w.getTemplateId();
			}

		}

		return rst;
	}

	public int getType() {
		return this.type;
	}

	public int getCountry() {
		return this.country;
	}

	public int getPid() {
		return this.pid;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setCountry(int country) {
		this.country = country;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public City(int type, int country, int index) {
		this.type = type;
		this.country = country;
		this.index = index;
	}

	public City() {
	}

	public City(int type, int index) {
		this.type = type;
		this.country = -1;
		this.index = index;
	}

	public int getLid() {
		return this.lid;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLid(int lid) {
		this.lid = lid;
	}

	public String getName() {
		String rst = "";
		if (this.type == 1)
			return "主城";
		if (this.type == 2)
			rst = rst + "副城";
		else {
			rst = rst + "小城";
		}
		switch (this.country) {
		case 4:
			return rst + "群";
		case 2:
			return rst + "蜀";
		case 1:
			return rst + "魏";
		case 3:
			return rst + "吴";
		}
		return rst;
	}

	public String getLogName() {
		String rst = "";
		if (this.type == 1)
			return "主城";
		if (this.type == 2)
			rst = rst + "副城";
		else {
			rst = rst + "小城";
		}
		switch (this.country) {
		case 4:
			return rst + "群" + this.index;
		case 2:
			return rst + "蜀" + this.index;
		case 1:
			return rst + "魏" + this.index;
		case 3:
			return rst + "吴" + this.index;
		}
		return rst;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.type);
		out.writeInt(this.country);
		out.writeInt(this.pid);
		out.writeInt(this.lid);
		out.writeInt(this.index);
		out.writeLong(this.timeMark);
	}

	public static City readObject(ObjectInputStream in) throws IOException {
		City city = new City();
		city.type = in.readInt();
		city.country = in.readInt();
		city.pid = in.readInt();
		city.lid = in.readInt();
		city.index = in.readInt();
		city.timeMark = in.readLong();
		return city;
	}
}
