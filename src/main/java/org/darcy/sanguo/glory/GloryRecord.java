package org.darcy.sanguo.glory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.GloryService;

import sango.packet.PbCommons;

public class GloryRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 2314067588195337922L;
	private int version = 1;
	public static final int LEVELUP_COST_ITEM_ID = 11007;
	public static final int RESET_COST_EXP_ID = 202;
	public static final int LEVELUP_EXP_ID = 201;
	public static final int FIRST_ACCQUIRE_GLORY_ID = 1;
	public static final int GLORY_TYPE_NUM = 3;
	private Map<Integer, Glory> glories = new HashMap();

	private Map<Integer, Integer> used = new HashMap();

	public void openGlory(int type) {
		List<GloryTemplate> list = GloryService.getTemplatesByType(type);
		if (list.size() > 0)
			for (GloryTemplate gt : list) {
				Glory g = new Glory(gt);
				this.glories.put(Integer.valueOf(g.getId()), g);
			}
	}

	public void init(Player player) {
		unEffectAll(player);
		effectAll(player);
	}

	public void reset(Player p, int id) {
		Glory g = (Glory) this.glories.get(Integer.valueOf(id));
		if (g == null) {
			return;
		}
		boolean use = false;
		if (this.used.containsValue(Integer.valueOf(id))) {
			unUse(g.getType(), p);
			use = true;
		}
		g.clear();
		if (use)
			use(id, p);
	}

	public void use(int id, Player p) {
	}

	public void effect(Player p, int type) {
	}

	public void effectAll(Player p) {
	}

	public void unUse(int type, Player p) {
		if (this.used.get(Integer.valueOf(type)) == null) {
			return;
		}
		Glory g = (Glory) this.glories.get(this.used.get(Integer.valueOf(type)));
		if (g == null) {
			return;
		}

		unEffect(p, g.getType());
		this.used.remove(Integer.valueOf(type));
	}

	public void unEffect(Player p, int type) {
		if (this.used.get(Integer.valueOf(type)) == null) {
			return;
		}
		Glory g = (Glory) this.glories.get(this.used.get(Integer.valueOf(type)));
		if (g == null) {
			return;
		}
		Warrior[] ws = p.getWarriors().getStands();
		for (int i = 0; i < ws.length; ++i) {
			Warrior w = ws[i];
			if (w != null) {
				int buffId = g.getBuffByIndex(i);
				if (buffId != -1)
					w.removeGloryBuff(buffId);
			}
		}
	}

	public void unEffectAll(Player p) {
	}

	public void levelUp(Player p, int id) {
		Glory g = (Glory) this.glories.get(Integer.valueOf(id));
		if (g == null) {
			return;
		}
		if (this.used.containsValue(Integer.valueOf(id))) {
			unUse(g.getType(), p);
		}
		g.levelUp();
		use(g.getId(), p);
	}

	private List<Integer> getCurBuffByIndex(int standIndex) {
		List list = new ArrayList();
		for (Integer id : this.used.values()) {
			Glory g = (Glory) this.glories.get(id);
			if (g != null) {
				list.add(Integer.valueOf(g.getBuffByIndex(standIndex)));
			}
		}
		return list;
	}

	public void addBuff(Warrior w, int standIndex) {
		List<Integer> buffIds = getCurBuffByIndex(standIndex);
		if (buffIds.size() > 0)
			for (Integer buffId : buffIds)
				if (buffId.intValue() != -1)
					w.addGloryBuff(buffId.intValue());
	}

	public void removeBuff(Warrior w, int standIndex) {
		List<Integer> buffIds = getCurBuffByIndex(standIndex);
		if (buffIds.size() > 0)
			for (Integer buffId : buffIds)
				if (buffId.intValue() != -1)
					w.removeGloryBuff(buffId.intValue());
	}

	public Glory getGlory(int id) {
		return ((Glory) this.glories.get(Integer.valueOf(id)));
	}

	public boolean isUsed(int id) {
		return this.used.containsValue(Integer.valueOf(id));
	}

	public Map<Integer, Glory> getGlories() {
		return this.glories;
	}

	public void setGlories(Map<Integer, Glory> glories) {
		this.glories = glories;
	}

	public Map<Integer, Integer> getUsed() {
		return this.used;
	}

	public void setUsed(Map<Integer, Integer> used) {
		this.used = used;
	}

	public int getBlobId() {
		return 20;
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			int size = in.readInt();
			this.glories = new HashMap();
			for (int i = 0; i < size; ++i) {
				Glory g = Glory.readObject(in);
				if (g != null) {
					this.glories.put(Integer.valueOf(g.getId()), g);
				}
			}
			size = in.readInt();
			this.used = new HashMap();
			for (int i = 0; i < size; ++i) {
				int id = in.readInt();
				Glory g = (Glory) this.glories.get(Integer.valueOf(id));
				if (g != null)
					this.used.put(Integer.valueOf(g.getType()), Integer.valueOf(g.getId()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		int id;
		out.writeInt(this.version);
		out.writeInt(this.glories.size());
		Iterator itx = this.glories.keySet().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			Glory g = (Glory) this.glories.get(Integer.valueOf(id));
			g.writeObject(out);
		}
		out.writeInt(this.used.size());
		itx = this.used.values().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			out.writeInt(id);
		}
	}

	public List<PbCommons.GloryGroup> getGloryGroups() {
		List list = new ArrayList();
		for (Integer type : this.used.keySet()) {
			int id = ((Integer) this.used.get(type)).intValue();
			Glory g = (Glory) this.glories.get(Integer.valueOf(id));
			list.add(PbCommons.GloryGroup.newBuilder().setType(type.intValue()).setUsed(id).addG(g.genGlory()).build());
		}
		return list;
	}
}
