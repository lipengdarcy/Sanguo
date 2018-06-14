package org.darcy.sanguo.tactic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.TacticService;

public class TacticRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 5539287554978423610L;
	private int version = 1;
	public static final int MAX_TACTIC = 5;
	public static final double LEGEND_COMPREHEND_RATIO = 0.1D;
	public static final int RESET_COST_JEWEL = 50;
	public static final int FIRST_COMPREHEND_TACTIC_TYPE = 0;
	public static final int FIRST_COMPREHEND_TACTIC_ID = 1;
	private Map<Integer, Tactic> tactics = new LinkedHashMap();

	private int select = -1;
	private int surplusPoint;
	private int totalPoint;
	private int comprehendCount;

	public void init(Player player) {
		updateTactic(player, this.select);
	}

	public boolean canComprehend() {
		if (this.tactics.size() < 5) {
			return true;
		}
		for (Tactic tactic : this.tactics.values()) {
			if (tactic.canLevelUp()) {
				return true;
			}
		}
		return false;
	}

	public Tactic comprehend(Player player) {
		if ((this.surplusPoint < 1) || (!(canComprehend()))) {
			return null;
		}
		Random random = new Random();
		Tactic tactic = null;
		if (this.comprehendCount == 0) {
			TacticTemplate tt = (TacticTemplate) TacticService.getTemplates(0).get(Integer.valueOf(1));
			tactic = new Tactic(tt);
			this.tactics.put(Integer.valueOf(tactic.getId()), tactic);
		} else {
			int index;
			if (this.tactics.size() < 5) {
				Map map = null;
				if (Math.random() < 0.1D)
					map = new HashMap(TacticService.getTemplates(1));
				else {
					map = new HashMap(TacticService.getTemplates(0));
				}
				for (Integer id : this.tactics.keySet()) {
					map.remove(id);
				}
				List list = new ArrayList(map.keySet());
				index = random.nextInt(list.size());
				int id = ((Integer) list.get(index)).intValue();
				TacticTemplate tt = (TacticTemplate) map.get(Integer.valueOf(id));
				tactic = new Tactic(tt);
				this.tactics.put(Integer.valueOf(id), tactic);
			} else {
				List list = new ArrayList();
				for (Tactic t : this.tactics.values()) {
					if (t.canLevelUp()) {
						list.add(Integer.valueOf(t.getId()));
					}
				}
				index = random.nextInt(list.size());
				int id = ((Integer) list.get(index)).intValue();
				tactic = (Tactic) this.tactics.get(Integer.valueOf(id));
				tactic.levelUp();
				if (id == this.select) {
					updateTactic(player, id);
				}
			}
		}

		if (this.surplusPoint > 0) {
			this.surplusPoint -= 1;
		}
		this.comprehendCount += 1;
		Platform.getEventManager().addEvent(new Event(2077, new Object[] { player }));
		return tactic;
	}

	public void addPoint(int point, Player player) {
	}

	public Tactic getTactic(int id) {
		return ((Tactic) this.tactics.get(Integer.valueOf(id)));
	}

	public int getCurBuffByIndex(int index) {
		Tactic curTactic = (Tactic) this.tactics.get(Integer.valueOf(this.select));
		if (curTactic == null) {
			return -1;
		}
		return curTactic.getBuffByIndex(index);
	}

	public int getTacticCount() {
		return this.tactics.size();
	}

	public void reset(Player player) {
		removeAllTacticBuff(player);
		this.surplusPoint = this.totalPoint;
		Platform.getEventManager().addEvent(new Event(2076, new Object[] { player }));
		this.select = -1;
		this.tactics.clear();
	}

	public void updateTactic(Player player, int id) {
		Tactic newTactic = (Tactic) this.tactics.get(Integer.valueOf(id));
		if (newTactic == null) {
			return;
		}
		removeAllTacticBuff(player);
		Warrior[] ws = player.getWarriors().getStands();
		for (int i = 0; i < ws.length; ++i) {
			Warrior w = ws[i];
			if (w != null) {
				int buffId = newTactic.getBuffByIndex(i);
				if (buffId != -1) {
					w.addTacticBuff(buffId);
				}
			}
		}
		player.getWarriors().refresh(true);
	}

	public void removeAllTacticBuff(Player player) {
		Tactic curTactic = (Tactic) this.tactics.get(Integer.valueOf(this.select));
		if (curTactic == null) {
			return;
		}
		Warrior[] ws = player.getWarriors().getStands();
		for (int i = 0; i < ws.length; ++i) {
			Warrior w = ws[i];
			if (w != null) {
				int buffId = curTactic.getBuffByIndex(i);
				if (buffId != -1)
					w.removeTacticBuff(buffId);
			}
		}
	}

	public void addBuff(Warrior w, int standIndex) {
		int buffId = getCurBuffByIndex(standIndex);
		if (buffId != -1)
			w.addTacticBuff(buffId);
	}

	public void removeBuff(Warrior w, int standIndex) {
		int buffId = getCurBuffByIndex(standIndex);
		if (buffId != -1)
			w.removeTacticBuff(buffId);
	}

	public Tactic getSelectTactic() {
		return ((Tactic) this.tactics.get(Integer.valueOf(this.select)));
	}

	public int getSelect() {
		return this.select;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public Map<Integer, Tactic> getTactics() {
		return this.tactics;
	}

	public void setTactics(Map<Integer, Tactic> tactics) {
		this.tactics = tactics;
	}

	public int getSurplusPoint() {
		return this.surplusPoint;
	}

	public void setSurplusPoint(int surplusPoint) {
		this.surplusPoint = surplusPoint;
	}

	public int getTotalPoint() {
		return this.totalPoint;
	}

	public void setTotalPoint(int totalPoint) {
		this.totalPoint = totalPoint;
	}

	public int getComprehendCount() {
		return this.comprehendCount;
	}

	public void setComprehendCount(int comprehendCount) {
		this.comprehendCount = comprehendCount;
	}

	public int getBlobId() {
		return 14;
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.select = in.readInt();
			this.surplusPoint = in.readInt();
			this.totalPoint = in.readInt();
			this.comprehendCount = in.readInt();
			this.tactics = new LinkedHashMap();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				Tactic t = Tactic.readObject(in);
				if (t != null)
					this.tactics.put(Integer.valueOf(t.getId()), t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.version);
		out.writeInt(this.select);
		out.writeInt(this.surplusPoint);
		out.writeInt(this.totalPoint);
		out.writeInt(this.comprehendCount);
		out.writeInt(this.tactics.size());
		List<Tactic> list = new ArrayList(this.tactics.values());
		for (Tactic t : list)
			t.writeObject(out);
	}
}
