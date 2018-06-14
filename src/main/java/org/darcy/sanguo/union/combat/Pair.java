package org.darcy.sanguo.union.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.union.League;

import sango.packet.PbLeague;

public class Pair {
	public static final int WIN_ACC = 10;
	public static final int LOSE_ACC = 4;
	private int deffenLeagueId = -1;
	private int offenLeagueId = -1;
	private int deffenScore;
	private int offenScore;
	private boolean isEnd;
	private long lastMiniSeconds;
	private List<City> cities = new ArrayList();

	private Map<Integer, Integer> personalScores = new HashMap();
	private Map<Integer, Integer> winCount = new HashMap();
	private Map<Integer, Integer> loseCount = new HashMap();

	public Pair() {
		for (int i = 0; i < 17; ++i)
			if (i == 0) {
				this.cities.add(new City(1, i));
			} else if ((i >= 1) && (i <= 4)) {
				this.cities.add(new City(2, i, i));
			} else
				this.cities.add(new City(3, i % 4 + 1, i));
	}

	public int getAccumulateScore(int lid) {
		if (isWin(lid)) {
			return 10;
		}
		return 4;
	}

	public int getWinScore(int lid) {
		if (lid == this.offenLeagueId) {
			return (this.offenScore - this.deffenScore);
		}
		return (this.deffenScore - this.offenScore);
	}

	public int getScore(int lid) {
		if (lid == this.offenLeagueId) {
			return this.offenScore;
		}

		return this.deffenScore;
	}

	private void innerEnd() {
		for (City city : this.cities)
			if (city.getLid() != -1)
				addScore(city.getLid(), city.getPid(), city.getAvilableScore());
	}

	public void end() {
		this.isEnd = true;
		LeagueCombat combat = Platform.getLeagueManager().getCombat();
		innerEnd();
		if (isWin(this.offenLeagueId)) {
			combat.getWinners().add(Integer.valueOf(this.offenLeagueId));
			if (this.deffenLeagueId != -1)
				combat.getLosers().add(Integer.valueOf(this.deffenLeagueId));
		} else {
			combat.getLosers().add(Integer.valueOf(this.offenLeagueId));
			combat.getWinners().add(Integer.valueOf(this.deffenLeagueId));
		}
	}

	public boolean isWin(int lid) {
		if (this.offenScore == this.deffenScore) {
			return (lid != this.deffenLeagueId);
		}
		if (lid == this.offenLeagueId) {
			if (this.offenScore <= this.deffenScore)
				return false;
			return true;
		}
		return ((lid != this.deffenLeagueId) || (this.deffenScore <= this.offenScore));
	}

	public void addWin(int pid) {
		Integer value = (Integer) this.winCount.get(Integer.valueOf(pid));
		if (value == null)
			this.winCount.put(Integer.valueOf(pid), Integer.valueOf(1));
		else
			this.winCount.put(Integer.valueOf(pid), Integer.valueOf(1 + value.intValue()));
	}

	public void addLose(int pid) {
		Integer value = (Integer) this.loseCount.get(Integer.valueOf(pid));
		if (value == null)
			this.loseCount.put(Integer.valueOf(pid), Integer.valueOf(1));
		else
			this.loseCount.put(Integer.valueOf(pid), Integer.valueOf(1 + value.intValue()));
	}

	public int getBuffLeagueId(int country) {
		int i = 0;
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (City city : this.cities) {
			if ((city.getCountry() != country) || (city.getLid() <= 0))
				continue;
			Integer value = (Integer) map.get(Integer.valueOf(city.getLid()));
			if (value == null)
				map.put(Integer.valueOf(city.getLid()), Integer.valueOf(1));
			else {
				map.put(Integer.valueOf(city.getLid()), Integer.valueOf(value.intValue() + 1));
			}

		}

		int key = -1;
		for (Integer lid : map.keySet()) {
			int tmp = ((Integer) map.get(lid)).intValue();
			if (tmp > i) {
				key = lid.intValue();
				i = tmp;
			}
		}

		if (key > 0) {
			return key;
		}

		return -1;
	}

	public int getRandomTargetPid(League league) {
		Set set = new HashSet();
		for (City c : this.cities) {
			set.add(Integer.valueOf(c.getPid()));
		}
		List<Integer> ids = new ArrayList(league.getInfo().getMembers().keySet());
		Collections.shuffle(ids);
		for (Integer i : ids) {
			if (!(set.contains(i))) {
				return i.intValue();
			}
		}
		return -1;
	}

	public City getCityByOwner(int pid) {
		for (City city : this.cities) {
			if (city.getPid() == pid) {
				return city;
			}
		}
		return null;
	}

	public void endCity(City city, int newPid, int newLid) {
		int oPid = city.getPid();
		int oLid = city.getLid();
		int rst = city.getAvilableScore();
		addScore(oLid, oPid, rst);
		city.changeOwner(newLid, newPid);
	}

	public void addScore(int lid, int pid, int score) {
		if (lid == this.deffenLeagueId)
			this.deffenScore += score;
		else if (lid == this.offenLeagueId) {
			this.offenScore += score;
		}

		if (this.deffenScore > 6000) {
			this.deffenScore = 6000;
		}
		if (this.offenScore > 6000) {
			this.offenScore = 6000;
		}

		Integer myscore = (Integer) this.personalScores.get(Integer.valueOf(pid));
		if (myscore != null)
			myscore = Integer.valueOf(myscore.intValue() + score);
		else {
			myscore = Integer.valueOf(score);
		}

		this.personalScores.put(Integer.valueOf(pid), myscore);
	}

	public void occupy(City city, int pid, int lid) {
		city.changeOwner(lid, pid);
	}

	public boolean endCheck() {
		if ((this.deffenLeagueId == -1) || (this.offenLeagueId == -1)) {
			this.offenScore = 1000;
			return true;
		}
		if (getMesureScore(this.deffenLeagueId) >= 6000) {
			this.deffenScore = 6000;
			return true;
		}
		if (getMesureScore(this.offenLeagueId) >= 6000) {
			this.offenScore = 6000;
			return true;
		}

		return false;
	}

	private int getMesureScore(int lid) {
		int tmp = 0;
		if (lid == this.offenLeagueId)
			tmp = this.offenScore;
		else {
			tmp = this.deffenScore;
		}

		for (City city : this.cities) {
			if (city.getLid() == lid) {
				tmp += city.getAvilableScore();
			}
		}

		if (tmp >= 5000) {
			((LeagueCombatService) Platform.getServiceManager().get(LeagueCombatService.class)).broadcast1500If(this,
					lid);
		}

		return tmp;
	}

	public boolean isFighting() {
		return (this.isEnd);
	}

	private int getAtkAdd(int lid) {
		return 0;
	}

	public int getCityCount(int type, int lid) {
		int rst = 0;
		for (City c : this.cities) {
			if ((c.getType() == type) && (c.getLid() == lid)) {
				++rst;
			}
		}

		return rst;
	}

	public PbLeague.LCDetailNode.Builder genDetailNode() {
		PbLeague.LCDetailNode.Builder b = PbLeague.LCDetailNode.newBuilder();
		League o = Platform.getLeagueManager().getLeagueById(this.offenLeagueId);
		b.setOffenName(o.getName()).setDefenScore(this.deffenScore).setOffenWin(isWin(this.offenLeagueId))
				.setOffenScore(this.offenScore);
		if (this.deffenLeagueId == -1) {
			b.setDefenName("轮空");
		} else {
			League d = Platform.getLeagueManager().getLeagueById(this.deffenLeagueId);
			b.setDefenName(d.getName());
		}
		return b;
	}

	public PbLeague.LCResource.Builder genMyResource(int lid) {
		PbLeague.LCResource.Builder b = PbLeague.LCResource.newBuilder();
		int villageCount = getCityCount(3, lid);
		b.setAtkAdd(getAtkAdd(lid)).setCity(getCityCount(1, lid)).setCount(getMesureScore(lid)).setMaxCount(6000)
				.setRate(getResourceAddRate(lid)).setTown(getCityCount(2, lid))
				.setName(Platform.getLeagueManager().getLeagueById(lid).getName())
				.setAtkAdd((villageCount > 0)
						? ((Integer) LeagueCombatService.cityAtkAdds.get(Integer.valueOf(villageCount))).intValue()
						: 0)
				.setVallage(villageCount);
		return b;
	}

	public int getResourceAddRate(int lid) {
		int rst = 0;
		for (City city : this.cities) {
			if (city.getLid() == lid) {
				rst += city.getRate();
			}
		}
		return rst;
	}

	public PbLeague.LCResource.Builder genTargetResource(int lid) {
		return genMyResource(getTargetLid(lid));
	}

	public int getTargetLid(int lid) {
		if (lid == this.deffenLeagueId) {
			return this.offenLeagueId;
		}
		return this.deffenLeagueId;
	}

	public List<City> getCities() {
		return this.cities;
	}

	public Map<Integer, Integer> getPersonalScores() {
		return this.personalScores;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public void setPersonalScores(Map<Integer, Integer> personalScores) {
		this.personalScores = personalScores;
	}

	public int getDeffenLeagueId() {
		return this.deffenLeagueId;
	}

	public int getOffenLeagueId() {
		return this.offenLeagueId;
	}

	public int getDeffenScore() {
		return this.deffenScore;
	}

	public int getOffenScore() {
		return this.offenScore;
	}

	public void setDeffenLeagueId(int deffenLeagueId) {
		this.deffenLeagueId = deffenLeagueId;
	}

	public void setOffenLeagueId(int offenLeagueId) {
		this.offenLeagueId = offenLeagueId;
	}

	public void setDeffenScore(int deffenScore) {
		this.deffenScore = deffenScore;
	}

	public void setOffenScore(int offenScore) {
		this.offenScore = offenScore;
	}

	public Map<Integer, Integer> getWinCount() {
		return this.winCount;
	}

	public Map<Integer, Integer> getLoseCount() {
		return this.loseCount;
	}

	public void setWinCount(Map<Integer, Integer> winCount) {
		this.winCount = winCount;
	}

	public void setLoseCount(Map<Integer, Integer> loseCount) {
		this.loseCount = loseCount;
	}

	public boolean isEnd() {
		return this.isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public long getLastMiniSeconds() {
		return this.lastMiniSeconds;
	}

	public void setLastMiniSeconds(long lastMiniSeconds) {
		this.lastMiniSeconds = lastMiniSeconds;
	}

	public static Pair readObject(ObjectInputStream in) throws IOException {
		Pair p = new Pair();
		int i;
		p.deffenLeagueId = in.readInt();
		p.offenLeagueId = in.readInt();
		p.deffenScore = in.readInt();
		p.offenScore = in.readInt();
		p.isEnd = in.readBoolean();
		p.lastMiniSeconds = in.readLong();
		int size = in.readInt();
		for (i = 0; i < size; ++i) {
			p.cities.add(City.readObject(in));
		}
		size = in.readInt();
		for (i = 0; i < size; ++i) {
			p.personalScores.put(Integer.valueOf(in.readInt()), Integer.valueOf(in.readInt()));
		}
		size = in.readInt();
		for (i = 0; i < size; ++i) {
			p.winCount.put(Integer.valueOf(in.readInt()), Integer.valueOf(in.readInt()));
		}
		size = in.readInt();
		for (i = 0; i < size; ++i) {
			p.loseCount.put(Integer.valueOf(in.readInt()), Integer.valueOf(in.readInt()));
		}

		return p;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		Integer i;
		out.writeInt(this.deffenLeagueId);
		out.writeInt(this.offenLeagueId);
		out.writeInt(this.deffenScore);
		out.writeInt(this.offenScore);
		out.writeBoolean(this.isEnd);
		out.writeLong(this.lastMiniSeconds);
		int size = this.cities.size();
		out.writeInt(size);
		for (i = 0; i < size; ++i) {
			City city = (City) this.cities.get(i);
			city.writeObject(out);
		}

		Set keys = this.personalScores.keySet();
		size = keys.size();
		out.writeInt(size);
		for (Iterator localIterator = keys.iterator(); localIterator.hasNext();) {
			i = (Integer) localIterator.next();
			out.writeInt(i.intValue());
			out.writeInt(((Integer) this.personalScores.get(i)).intValue());
		}

		keys = this.winCount.keySet();
		size = keys.size();
		out.writeInt(size);
		for (Iterator localIterator = keys.iterator(); localIterator.hasNext();) {
			i = (Integer) localIterator.next();
			out.writeInt(i.intValue());
			out.writeInt(((Integer) this.winCount.get(i)).intValue());
		}

		keys = this.loseCount.keySet();
		size = keys.size();
		out.writeInt(size);
		for (Iterator localIterator = keys.iterator(); localIterator.hasNext();) {
			i = (Integer) localIterator.next();
			out.writeInt(i.intValue());
			out.writeInt(((Integer) this.loseCount.get(i)).intValue());
		}
	}
}
