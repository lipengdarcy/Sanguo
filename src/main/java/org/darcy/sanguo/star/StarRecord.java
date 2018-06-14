package org.darcy.sanguo.star;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;

public class StarRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = -8352553340241149687L;
	private static final int version = 2;
	private HashMap<Integer, Attributes> attris = new HashMap();

	private HashMap<Integer, Integer> heroExps = new HashMap();

	private HashMap<Integer, Integer> heroLevels = new HashMap();

	private HashMap<Integer, int[]> trainLevels = new HashMap();

	private int heroId = -1;

	private int maxAchieve = -1;

	public void setTrainLevel(int heroId, int index, int level) {
		int[] rst = (int[]) this.trainLevels.get(Integer.valueOf(heroId));
		if (rst == null) {
			rst = new int[4];
		}
		rst[index] = level;
		this.trainLevels.put(Integer.valueOf(heroId), rst);
	}

	public int[] getTrainLevel(int heroId) {
		int[] rst = (int[]) this.trainLevels.get(Integer.valueOf(heroId));
		if (rst == null) {
			rst = new int[4];
		}
		return rst;
	}

	public void refreshStarAttris(int heroId) {
		Attributes a = new Attributes();
		int level = getLevel(heroId);
		Star star = (Star) StarService.stars.get(Integer.valueOf(heroId));
		Attri[] ats = (Attri[]) StarService.attris.get(Integer.valueOf(star.attriId));
		while (level > 0) {
			Attri tmp = ats[(level - 1)];
			a.addAttri(tmp);
			--level;
		}
		this.attris.put(Integer.valueOf(heroId), a);
	}

	private void readObject(ObjectInputStream in) {
		try {
			int key;
			int value;
			this.heroExps = new HashMap();
			this.heroLevels = new HashMap();
			this.attris = new HashMap();
			this.trainLevels = new HashMap();
			this.heroId = -1;
			this.maxAchieve = -1;
			int version = in.readInt();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				int heroId = in.readInt();
				Attributes a = Attributes.readObject(in);
				this.attris.put(Integer.valueOf(heroId), a);
			}
			size = in.readInt();
			for (int i = 0; i < size; ++i) {
				key = in.readInt();
				value = in.readInt();
				this.heroExps.put(Integer.valueOf(key), Integer.valueOf(value));
			}
			this.heroId = in.readInt();
			size = in.readInt();
			for (int i = 0; i < size; ++i) {
				key = in.readInt();
				value = in.readInt();
				this.heroLevels.put(Integer.valueOf(key), Integer.valueOf(value));
			}
			this.maxAchieve = in.readInt();
			if (version > 1) {
				size = in.readInt();
				for (int i = 0; i < size; ++i) {
					key = in.readInt();
					int[] ls = new int[4];
					for (int j = 0; j < 4; ++j) {
						ls[j] = in.readInt();
					}
					this.trainLevels.put(Integer.valueOf(key), ls);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Integer key;
		int value;
		out.writeInt(2);
		out.writeInt(this.attris.size());
		for (Integer heroId : this.attris.keySet()) {
			out.writeInt(heroId.intValue());
			((Attributes) this.attris.get(heroId)).writeObject(out);
		}
		out.writeInt(this.heroExps.size());
		for (Iterator<Integer> it = this.heroExps.keySet().iterator(); it.hasNext();) {
			key = (Integer) it.next();
			value = ((Integer) this.heroExps.get(key)).intValue();
			out.writeInt(key.intValue());
			out.writeInt(value);
		}
		out.writeInt(this.heroId);
		out.writeInt(this.heroLevels.size());
		for (Iterator<Integer> it = this.heroLevels.keySet().iterator(); it.hasNext();) {
			key = (Integer) it.next();
			value = ((Integer) this.heroLevels.get(key)).intValue();
			out.writeInt(key.intValue());
			out.writeInt(value);
		}
		out.writeInt(this.maxAchieve);
		out.writeInt(this.trainLevels.size());
		for (Iterator<Integer> it = this.trainLevels.keySet().iterator(); it.hasNext();) {
			key = (Integer) it.next();
			int[] ls = (int[]) this.trainLevels.get(key);
			out.writeInt(key.intValue());
			for (int i = 0; i < 4; ++i)
				out.writeInt(ls[i]);
		}
	}

	public void refreshMaxAchieve(Player player) {
		int total = getTotalLevel();
		for (Achieve ac : StarService.achieves)
			if ((total >= ac.gole) && (ac.id > this.maxAchieve)) {
				this.maxAchieve = ac.id;
				Platform.getEventManager().addEvent(new Event(3003, new Object[] { player, ac.name,
						MessageFormat.format("人物精力上限+{0}", new Object[] { Integer.valueOf(ac.stamina) }) }));
				player.addStaminaLimt(ac.stamina);
			}
	}

	public void init() {
	}

	public int getTotalLevel() {
		int rst = 0;
		for (Iterator localIterator = this.heroLevels.values().iterator(); localIterator.hasNext();) {
			int l = ((Integer) localIterator.next()).intValue();
			rst += l;
		}
		return rst;
	}

	public int getExp(int heroId) {
		Integer i = (Integer) this.heroExps.get(Integer.valueOf(heroId));

		return ((i == null) ? 0 : i.intValue());
	}

	public void addHeroIf(int heroId) {
		Integer i = (Integer) this.heroLevels.get(Integer.valueOf(heroId));
		if ((i == null) && (StarService.allStars.contains(Integer.valueOf(heroId))))
			this.heroLevels.put(Integer.valueOf(heroId), Integer.valueOf(0));
	}

	public int getLevel(int heroId) {
		Integer i = (Integer) this.heroLevels.get(Integer.valueOf(heroId));

		return ((i == null) ? -1 : i.intValue());
	}

	public Attributes getAttributes(int tpltId) {
		Attributes a = (Attributes) this.attris.get(Integer.valueOf(tpltId));
		if (a == null) {
			a = new Attributes();
			this.attris.put(Integer.valueOf(tpltId), a);
		}
		return a;
	}

	public Attributes getTrainAttribues(int tpltId) {
		Attributes a = new Attributes();
		int[] levels = getTrainLevel(tpltId);
		if (levels != null) {
			ExpService es = (ExpService) Platform.getServiceManager().get(ExpService.class);
			for (int i = 0; i < levels.length; ++i) {
				int level = levels[i];
				if (level > 0) {
					int base = level * StarService.TRAIN_PER_LEVLE[i];
					int starLevel = getLevel(tpltId);
					if (starLevel > 0) {
						int bounus = es.getExp(5007, starLevel);
						base = base * (100 + bounus) / 100;
					}
					if (i == 0)
						a.set(7, base);
					else if (i == 1)
						a.set(6, base);
					else if (i == 2)
						a.set(8, base);
					else if (i == 3) {
						a.set(9, base);
					}
				}
			}
		}
		return a;
	}

	public int getHeroId() {
		return this.heroId;
	}

	public void setAttributes(int tpltId, Attributes attributes) {
		this.attris.put(Integer.valueOf(tpltId), attributes);
	}

	public void setHeroId(int heroId) {
		this.heroId = heroId;
	}

	public HashMap<Integer, Integer> getHeroExps() {
		return this.heroExps;
	}

	public HashMap<Integer, Integer> getHeroLevels() {
		return this.heroLevels;
	}

	public void setHeroExps(HashMap<Integer, Integer> heroExps) {
		this.heroExps = heroExps;
	}

	public void setHeroLevels(HashMap<Integer, Integer> heroLevels) {
		this.heroLevels = heroLevels;
	}

	public int getBlobId() {
		return 17;
	}
}
