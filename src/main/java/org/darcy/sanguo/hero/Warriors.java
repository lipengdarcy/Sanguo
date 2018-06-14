package org.darcy.sanguo.hero;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;

import sango.packet.PbStandStruct;

public class Warriors implements PlayerBlobEntity {
	private static final long serialVersionUID = -7505778869059053150L;
	private static int VERSION = 1;

	private Map<Integer, Integer[]> warriorIds = new HashMap();

	private Map<Integer, Integer> friendIds = new HashMap();

	private Warrior[] stands = new Warrior[6];

	private Map<Integer, Warrior> warriors = new HashMap();

	private Map<Integer, Warrior> friends = new HashMap();
	private WeakReference<Player> player;

	public Warriors(MainWarrior warrior) {
		addWarrior(warrior, 1);
	}

	public Warrior addFriend(Warrior warrior, int index) {
		Warrior old = (Warrior) this.friends.get(Integer.valueOf(index));
		this.friends.put(Integer.valueOf(index), warrior);
		return old;
	}

	public void downFriend(Warrior w) {
		if (this.friends.containsValue(w)) {
			Iterator itx = this.friends.keySet().iterator();
			while (itx.hasNext()) {
				int key = ((Integer) itx.next()).intValue();
				Warrior tmp = (Warrior) this.friends.get(Integer.valueOf(key));
				if (tmp.getId() == w.getId())
					itx.remove();
			}
		}
	}

	public Warrior addWarrior(Warrior warrior, int index) {
		Warrior old = (Warrior) this.warriors.get(Integer.valueOf(index));
		this.warriors.put(Integer.valueOf(index), warrior);
		if (this.warriors.size() == 1)
			this.stands[Formation.leadPosition] = warrior;
		else {
			for (int i = 0; i < Formation.openSort.length; ++i) {
				Warrior tmp = this.stands[Formation.openSort[i]];
				if (old != null) {
					if ((tmp == null) || (tmp.getId() != old.getId()))
						continue;
					this.stands[Formation.openSort[i]] = warrior;
					break;
				}

				if (tmp == null) {
					this.stands[Formation.openSort[i]] = warrior;
					break;
				}

			}

		}

		return old;
	}

	public void downWarrior(Warrior w) {
		if (this.warriors.containsValue(w)) {
			int stageIndex = getStageIndex(w);
			if (stageIndex != -1) {
				this.warriors.remove(Integer.valueOf(stageIndex));
			}
			int standIndex = getStandIndex(w);
			if (standIndex != -1)
				this.stands[standIndex] = null;
		}
	}

	public int isSameWarriorOnStage(int templateId) {
		Warrior warrior;
		Iterator itx = this.warriors.keySet().iterator();
		while (itx.hasNext()) {
			warrior = (Warrior) this.warriors.get(itx.next());
			if (warrior.getTemplateId() == templateId) {
				return 1;
			}
		}
		itx = this.friends.keySet().iterator();
		while (itx.hasNext()) {
			warrior = (Warrior) this.friends.get(itx.next());
			if (warrior.getTemplateId() == templateId) {
				return 2;
			}
		}
		return 0;
	}

	private void refreshEns() {
		Warrior[] heros = getAllWarriorAndFellow();
		Iterator itx = this.warriors.keySet().iterator();
		while (itx.hasNext()) {
			((Warrior) this.warriors.get(itx.next())).refreshkEns(heros);
		}
		itx = this.friends.keySet().iterator();
		while (itx.hasNext())
			((Warrior) this.friends.get(itx.next())).refreshkEns(heros);
	}

	public Warrior[] getAllWarriorAndFellow() {
		int size = this.warriors.size() + this.friends.size();
		Warrior[] heros = new Warrior[size];
		int i = 0;
		Iterator itx = this.warriors.keySet().iterator();
		while (itx.hasNext()) {
			heros[i] = ((Warrior) this.warriors.get(itx.next()));
			++i;
		}
		itx = this.friends.keySet().iterator();
		while (itx.hasNext()) {
			heros[i] = ((Warrior) this.friends.get(itx.next()));
			++i;
		}
		return heros;
	}

	public String generateHeroIds() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 6; ++i) {
			Warrior w = (Warrior) this.warriors.get(Integer.valueOf(i));
			if (i != 1) {
				sb.append(",");
			}
			if (w != null)
				sb.append(w.getTemplateId());
			else {
				sb.append(-1);
			}
		}
		return sb.toString();
	}

	public void init(Player player) {
		int id;
		Iterator itx = this.warriorIds.keySet().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			Warrior warrior = (Warrior) player.getBags().getItemById(id, 2);
			if (warrior == null) {
				Platform.getLog().logError(
						"Warriors init warrior error, warrior is not in bag,playerId:" + player.getId() + ",id:" + id);
			} else {
				Integer[] tmp = (Integer[]) this.warriorIds.get(Integer.valueOf(id));
				this.warriors.put(tmp[0], warrior);
				this.stands[tmp[1].intValue()] = warrior;
			}
		}
		itx = this.friendIds.keySet().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			Warrior friend = (Warrior) player.getBags().getItemById(id, 2);
			if (friend == null) {
				Platform.getLog().logError(
						"Warriors init friend error, warrior is not in bag,playerId:" + player.getId() + ",id:" + id);
			} else {
				Integer tmp = (Integer) this.friendIds.get(Integer.valueOf(id));
				this.friends.put(tmp, friend);
			}

		}

		getMainWarrior().setName(player.getName());

		refreshEns();

		this.player = new WeakReference(player);
	}

	public int getStageStatus(int warriorId) {
		for (Warrior warrior : this.warriors.values()) {
			if (warrior.getId() == warriorId) {
				return 1;
			}
		}
		for (Warrior friend : this.friends.values()) {
			if (friend.getId() == warriorId) {
				return 2;
			}
		}
		return 0;
	}

	public void refresh(boolean isSync) {
		refreshEns();
		Iterator itx = this.warriors.keySet().iterator();
		while (itx.hasNext()) {
			((Warrior) this.warriors.get(itx.next())).refreshAttributes(isSync);
		}
		itx = this.friends.keySet().iterator();
		while (itx.hasNext())
			((Warrior) this.friends.get(itx.next())).refreshAttributes(isSync);
	}

	public int getWarriorsCount() {
		return this.warriors.size();
	}

	public int getFellowCount() {
		return this.friends.size();
	}

	public MainWarrior getMainWarrior() {
		Iterator itx = this.warriors.keySet().iterator();
		while (itx.hasNext()) {
			int key = ((Integer) itx.next()).intValue();
			if (((Warrior) this.warriors.get(Integer.valueOf(key))).isMainWarrior) {
				return ((MainWarrior) this.warriors.get(Integer.valueOf(key)));
			}
		}
		return null;
	}

	public Warrior getWarriorByIndex(int index) {
		return ((Warrior) this.warriors.get(Integer.valueOf(index)));
	}

	public Warrior getWarriorByStandIndex(int standIndex) {
		if ((standIndex >= this.stands.length) || (standIndex < 0)) {
			return null;
		}
		return this.stands[standIndex];
	}

	public Warrior getWarriorById(int id) {
		Iterator itx = this.warriors.keySet().iterator();
		while (itx.hasNext()) {
			int key = ((Integer) itx.next()).intValue();
			Warrior tmp = (Warrior) this.warriors.get(Integer.valueOf(key));
			if (tmp.getId() == id) {
				return tmp;
			}
		}
		return null;
	}

	public Warrior getFriendById(int id) {
		Iterator itx = this.friends.keySet().iterator();
		while (itx.hasNext()) {
			int key = ((Integer) itx.next()).intValue();
			Warrior friend = (Warrior) this.friends.get(Integer.valueOf(key));
			if (friend.getId() == id) {
				return friend;
			}
		}
		return null;
	}

	public int getStageIndex(Warrior warrior) {
		if (this.warriors.containsValue(warrior)) {
			Iterator itx = this.warriors.keySet().iterator();
			while (itx.hasNext()) {
				int key = ((Integer) itx.next()).intValue();
				Warrior tmp = (Warrior) this.warriors.get(Integer.valueOf(key));
				if (tmp.getId() == warrior.getId()) {
					return key;
				}
			}
		}
		return -1;
	}

	public int getStandIndex(Warrior warrior) {
		for (int i = 0; i < this.stands.length; ++i) {
			if ((this.stands[i] != null) && (warrior.getId() == this.stands[i].getId())) {
				return i;
			}
		}

		return -1;
	}

	public PbStandStruct.StandStruct genStandStruct() {
		PbStandStruct.StandStruct.Builder builder = PbStandStruct.StandStruct.newBuilder();
		for (int i = 0; i < this.stands.length; ++i) {
			PbStandStruct.StandUnit.Builder unitBuilder = PbStandStruct.StandUnit.newBuilder();
			unitBuilder.setStandIndex(i);
			if (Formation.isOpenByPosition(i, getPlayer().getLevel())) {
				Warrior warrior = this.stands[i];
				if (warrior != null)
					unitBuilder.setId(warrior.getId());
				else
					unitBuilder.setId(0);
			} else {
				unitBuilder.setId(-1);
			}

			builder.addStands(unitBuilder.build());
		}
		return builder.build();
	}

	public PbStandStruct.StageStruct genStageStruct() {
		PbStandStruct.StageStruct.Builder builder = PbStandStruct.StageStruct.newBuilder();
		Map openIndexByLevel = Formation.openIndexByLevel;
		Iterator itx2 = openIndexByLevel.keySet().iterator();
		Player player = getPlayer();
		while (itx2.hasNext()) {
			PbStandStruct.StageUnit.Builder unitBuilder = PbStandStruct.StageUnit.newBuilder();
			int stageIndex = ((Integer) itx2.next()).intValue();
			int openLevel = ((Integer) openIndexByLevel.get(Integer.valueOf(stageIndex))).intValue();
			unitBuilder.setIndex(stageIndex);
			if (player.getLevel() < openLevel) {
				unitBuilder.setId(-1);
			} else {
				Iterator itx = this.warriors.keySet().iterator();
				boolean flag = true;
				while (itx.hasNext()) {
					int index = ((Integer) itx.next()).intValue();
					if (stageIndex == index) {
						flag = false;
						unitBuilder.setId(((Warrior) this.warriors.get(Integer.valueOf(index))).getId());
						break;
					}
				}
				if (flag) {
					unitBuilder.setId(0);
				}
			}
			builder.addStage(unitBuilder.build());
		}
		return builder.build();
	}

	public PbStandStruct.FellowStruct genFriendStruct() {
		PbStandStruct.FellowStruct.Builder builder = PbStandStruct.FellowStruct.newBuilder();
		Map openFriendByLevel = Formation.openFriendByLevel;
		Iterator itx2 = openFriendByLevel.keySet().iterator();
		Player player = getPlayer();
		while (itx2.hasNext()) {
			PbStandStruct.FellowUnit.Builder unitBuilder = PbStandStruct.FellowUnit.newBuilder();
			int friendIndex = ((Integer) itx2.next()).intValue();
			int openLevel = ((Integer) openFriendByLevel.get(Integer.valueOf(friendIndex))).intValue();
			unitBuilder.setIndex(friendIndex);
			if (player.getLevel() < openLevel) {
				unitBuilder.setId(-1);
			} else {
				Iterator itx = this.friends.keySet().iterator();
				boolean flag = true;
				while (itx.hasNext()) {
					int index = ((Integer) itx.next()).intValue();
					if (friendIndex == index) {
						flag = false;
						unitBuilder.setId(((Warrior) this.friends.get(Integer.valueOf(index))).getId());
						break;
					}
				}
				if (flag) {
					unitBuilder.setId(0);
				}
			}
			builder.addFriend(unitBuilder.build());
		}
		return builder.build();
	}

	public boolean isCorrectBlob() {
		return ((this.warriorIds == null) || (this.warriorIds.size() < 1) || (this.friendIds == null));
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.warriors = new HashMap();
			this.stands = new Warrior[6];
			this.friends = new HashMap();
			this.warriorIds = new HashMap();
			int warriorCount = in.readInt();
			for (int i = 0; i < warriorCount; ++i) {
				int id = in.readInt();
				int index = in.readInt();
				int standIndex = in.readInt();
				this.warriorIds.put(Integer.valueOf(id),
						new Integer[] { Integer.valueOf(index), Integer.valueOf(standIndex) });
			}
			this.friendIds = new HashMap();
			int friendCount = in.readInt();
			for (int i = 0; i < friendCount; ++i) {
				int id = in.readInt();
				int friendIndex = in.readInt();
				this.friendIds.put(Integer.valueOf(id), Integer.valueOf(friendIndex));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		int key;
		Warrior warrior;
		out.writeInt(VERSION);

		out.writeInt(this.warriors.size());
		Iterator itx = this.warriors.keySet().iterator();
		this.warriorIds.clear();
		while (itx.hasNext()) {
			key = ((Integer) itx.next()).intValue();
			warrior = (Warrior) this.warriors.get(Integer.valueOf(key));
			if (warrior != null) {
				int standIndex = getStandIndex(warrior);
				out.writeInt(warrior.getId());
				out.writeInt(key);
				out.writeInt(standIndex);
				this.warriorIds.put(Integer.valueOf(warrior.getId()),
						new Integer[] { Integer.valueOf(key), Integer.valueOf(standIndex) });
			}
		}

		out.writeInt(this.friends.size());
		itx = this.friends.keySet().iterator();
		this.friendIds.clear();
		while (itx.hasNext()) {
			key = ((Integer) itx.next()).intValue();
			warrior = (Warrior) this.friends.get(Integer.valueOf(key));
			if (warrior != null) {
				out.writeInt(warrior.getId());
				out.writeInt(key);
				this.friendIds.put(Integer.valueOf(warrior.getId()), Integer.valueOf(key));
			}
		}
	}

	public Object clone() {
		int key;
		Warriors newWarriors = new Warriors(getMainWarrior());
		Iterator itx = this.warriorIds.keySet().iterator();
		while (itx.hasNext()) {
			key = ((Integer) itx.next()).intValue();
			Integer[] tmp = (Integer[]) this.warriorIds.get(Integer.valueOf(key));
			newWarriors.warriorIds.put(Integer.valueOf(key), (Integer[]) Arrays.copyOf(tmp, tmp.length));
		}
		itx = this.friendIds.keySet().iterator();
		while (itx.hasNext()) {
			key = ((Integer) itx.next()).intValue();
			Integer tmp = (Integer) this.friendIds.get(Integer.valueOf(key));
			newWarriors.friendIds.put(Integer.valueOf(key), tmp);
		}
		return newWarriors;
	}

	public Map<Integer, Warrior> getWarriors() {
		return this.warriors;
	}

	public void setWarriors(Map<Integer, Warrior> warriors) {
		this.warriors = warriors;
	}

	public Map<Integer, Warrior> getFriends() {
		return this.friends;
	}

	public Warrior[] getStands() {
		return this.stands;
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}

	public int getBlobId() {
		return 1;
	}
}
