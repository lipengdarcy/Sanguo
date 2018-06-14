package org.darcy.sanguo.union.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CombatData implements Serializable {
	private static final long serialVersionUID = 3488484232554203225L;
	private static final int version = 1;
	public int currentRound = 0;

	public int currentPeriod = 0;

	public List<Integer> deffens = new ArrayList();

	public List<Integer> offens = new ArrayList();

	public Map<Integer, Map<Integer, Pair>> currentCombatPairs = new HashMap<Integer, Map<Integer, Pair>>();

	public Map<Integer, Map<Integer, Pair>> lastCombatPairs = new HashMap<Integer, Map<Integer, Pair>>();

	public Map<Integer, LeagueCombatRank> rankData = new HashMap();
	public Map<Integer, LeagueCombatRank> lastRankData = new HashMap();

	private void writeObject(ObjectOutputStream out) throws IOException {
		int i;
		Map map;
		Set set;
		Pair p;
		Iterator localIterator2;
		out.writeInt(1);
		out.writeInt(this.currentRound);
		out.writeInt(this.currentPeriod);
		int size = this.deffens.size();
		out.writeInt(size);
		for (i = 0; i < size; ++i) {
			out.writeInt(((Integer) this.deffens.get(i)).intValue());
		}
		size = this.offens.size();
		out.writeInt(size);
		for (i = 0; i < size; ++i) {
			out.writeInt(((Integer) this.offens.get(i)).intValue());
		}

		Set keys = this.currentCombatPairs.keySet();
		out.writeInt(keys.size());
		for (Iterator localIterator1 = keys.iterator(); localIterator1.hasNext();) {
			i = (Integer) localIterator1.next();
			out.writeInt(i);
			map = (Map) this.currentCombatPairs.get(i);
			set = new HashSet(map.values());
			out.writeInt(set.size());
			for (localIterator2 = set.iterator(); localIterator2.hasNext();) {
				p = (Pair) localIterator2.next();
				p.writeObject(out);
			}
		}

		keys = this.lastCombatPairs.keySet();
		out.writeInt(keys.size());
		for (Iterator localIterator1 = keys.iterator(); localIterator1.hasNext();) {
			i = (Integer) localIterator1.next();
			out.writeInt(i);
			map = (Map) this.lastCombatPairs.get(i);
			set = new HashSet(map.values());
			out.writeInt(set.size());
			for (localIterator2 = set.iterator(); localIterator2.hasNext();) {
				p = (Pair) localIterator2.next();
				p.writeObject(out);
			}
		}

		keys = this.rankData.keySet();
		out.writeInt(keys.size());
		for (Iterator localIterator1 = keys.iterator(); localIterator1.hasNext();) {
			i = (Integer) localIterator1.next();
			out.writeInt(i);
			((LeagueCombatRank) this.rankData.get(i)).writeObject(out);
		}

		keys = this.lastRankData.keySet();
		out.writeInt(keys.size());
		for (Iterator localIterator1 = keys.iterator(); localIterator1.hasNext();) {
			i = (Integer) localIterator1.next();
			out.writeInt(i);
			((LeagueCombatRank) this.lastRankData.get(i)).writeObject(out);
		}
	}

	private void readObject(ObjectInputStream in) {
		int i;
		try {
			int ikey;
			int isize;
			Map map;
			int j;
			Pair p;
			this.deffens = new ArrayList();
			this.offens = new ArrayList();
			this.currentCombatPairs = new HashMap();
			this.lastCombatPairs = new HashMap();
			this.rankData = new HashMap();
			this.lastRankData = new HashMap();

			int v = in.readInt();
			this.currentRound = in.readInt();
			this.currentPeriod = in.readInt();
			int size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.deffens.add(Integer.valueOf(in.readInt()));
			}
			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.offens.add(Integer.valueOf(in.readInt()));
			}

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				ikey = in.readInt();
				isize = in.readInt();
				map = new HashMap();
				this.currentCombatPairs.put(Integer.valueOf(ikey), map);
				for (j = 0; j < isize; ++j) {
					p = Pair.readObject(in);
					if (p.getOffenLeagueId() != -1) {
						map.put(Integer.valueOf(p.getOffenLeagueId()), p);
					}
					if (p.getDeffenLeagueId() != -1) {
						map.put(Integer.valueOf(p.getDeffenLeagueId()), p);
					}
				}
			}

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				ikey = in.readInt();
				isize = in.readInt();
				map = new HashMap();
				this.lastCombatPairs.put(Integer.valueOf(ikey), map);
				for (j = 0; j < isize; ++j) {
					p = Pair.readObject(in);
					if (p.getOffenLeagueId() != -1) {
						map.put(Integer.valueOf(p.getOffenLeagueId()), p);
					}
					if (p.getDeffenLeagueId() != -1) {
						map.put(Integer.valueOf(p.getDeffenLeagueId()), p);
					}
				}
			}

			size = in.readInt();
			for (i = 0; i < size; ++i) {
				this.rankData.put(Integer.valueOf(in.readInt()), LeagueCombatRank.readObject(in));
			}

			size = in.readInt();
			for (i = 0; i < size; ++i)
				this.lastRankData.put(Integer.valueOf(in.readInt()), LeagueCombatRank.readObject(in));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
