package org.darcy.sanguo.relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;

public class Relations {
	public static final int RECOMMEND_COUNT = 100;
	public static final int RECOMMEND_DIS_LEVEL = 40;
	public static final int MAX_GET_TIMES = 30;
	public static final int TYPE_COUNT = 2;
	public static final int TYPE_FRIEND = 0;
	public static final int TYPE_FRIEND_APPLY = 1;
	private static final int[] MAX_COUNTS = { 40, 40 };
	int id;
	Relation friends = new Relation(0);
	Relation replyers = new Relation(1);
	List<Integer> recommendPlayers = new ArrayList();
	int getStaminaTimes = 0;

	private Set<Integer> giveRecords = new HashSet();

	private HashMap<Integer, Long> givenStaminas = new HashMap();

	private static Comparator<GivenStamina> compa = new Comparator<GivenStamina>() {
		public int compare(GivenStamina o1, GivenStamina o2) {
			return (int) (o2.time - o1.time);
		}
	};

	private static Comparator<GivenStamina> deCompa = new Comparator<GivenStamina>() {
		public int compare(GivenStamina o1, GivenStamina o2) {
			return (int) (o1.time - o2.time);
		}
	};

	public int getLeftStaminaTimes() {
		int left = 30 - this.getStaminaTimes;
		if (left < 0) {
			return 0;
		}
		return left;
	}

	public Set<Integer> getGiveRecords() {
		return this.giveRecords;
	}

	public void setGiveRecords(Set<Integer> giveRecords) {
		this.giveRecords = giveRecords;
	}

	public Relation getRelation(int type) {
		switch (type) {
		case 0:
			return this.friends;
		case 1:
			return this.replyers;
		}
		return null;
	}

	public void setRelation(int type, Relation relation) {
		switch (type) {
		case 0:
			this.friends = relation;
		case 1:
			this.replyers = relation;
		}
	}

	public void init() {
		for (int i = 0; i < 2; ++i) {
			getRelation(i).init();
		}
		refreshRecommendPlayers();
	}

	public void refreshRecommendPlayers() {
		Player owner = null;
		try {
			owner = Platform.getPlayerManager().getPlayer(this.id, true, false);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (owner == null)
			return;

		this.recommendPlayers.clear();
		ArrayList<Player> ids = new ArrayList(Platform.getPlayerManager().players.values());
		Collections.shuffle(ids);
		int size = Math.min(100, ids.size());
		for (Player p : ids) {
			if (this.recommendPlayers.size() >= size)
				return;
			if ((p == null) || (p.getId() == owner.getId()) || (hasRealtion(0, p.getId()))
					|| (hasRealtion(1, p.getId())) || (Math.abs(owner.getLevel() - p.getLevel()) > 5))
				continue;
			this.recommendPlayers.add(Integer.valueOf(p.getId()));
		}
	}

	public void refresh() {
		this.giveRecords = new HashSet();
		this.getStaminaTimes = 0;
	}

	public boolean isFull(int type) {
		Relation relation = getRelation(type);
		if (relation == null) {
			return true;
		}

		return (relation.playerIds.size() < getMaxCount(type));
	}

	public HashMap<Integer, Long> getGivenStaminas() {
		return this.givenStaminas;
	}

	public void setGivenStaminas(HashMap<Integer, Long> givenStaminas) {
		this.givenStaminas = givenStaminas;
	}

	public List<GivenStamina> getOrderedGivenStaminas() {
		List list = new ArrayList();
		for (Integer pid : this.givenStaminas.keySet()) {
			list.add(new GivenStamina(pid.intValue(), ((Long) this.givenStaminas.get(pid)).longValue()));
		}
		Collections.sort(list, compa);
		return list;
	}

	public List<GivenStamina> getDeOrderedGivenStaminas() {
		List list = new ArrayList();
		for (Integer pid : this.givenStaminas.keySet()) {
			list.add(new GivenStamina(pid.intValue(), ((Long) this.givenStaminas.get(pid)).longValue()));
		}
		Collections.sort(list, deCompa);
		return list;
	}

	public void addPlayer(int type, Player target) {
		Relation relation = getRelation(type);
		if (relation == null) {
			return;
		}
		if (isFull(type)) {
			if (type < 2) {
				return;
			}
			relation.removeOldestRelation();
		}

		relation.addPlayer(target);
	}

	public int getMaxCount(int type) {
		return MAX_COUNTS[type];
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getRecommendPlayers() {
		if (this.recommendPlayers.size() == 0) {
			refreshRecommendPlayers();
		}
		return this.recommendPlayers;
	}

	public MiniPlayer getRelationPlayer(int type, int id) {
		return getRelation(type).getMiniPlayer(id);
	}

	public boolean hasRealtion(int type, int id) {
		if (getRelation(type) != null) {
			return getRelation(type).playerIds.keySet().contains(Integer.valueOf(id));
		}
		return false;
	}

	public Relation getFriends() {
		return this.friends;
	}

	public Relation getReplyers() {
		return this.replyers;
	}

	public int getGetStaminaTimes() {
		return this.getStaminaTimes;
	}

	public void setGetStaminaTimes(int getStaminaTimes) {
		this.getStaminaTimes = getStaminaTimes;
	}

	public void setFriends(Relation friends) {
		this.friends = friends;
	}

	public void setReplyers(Relation replyers) {
		this.replyers = replyers;
	}
}
