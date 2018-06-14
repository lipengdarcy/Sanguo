package org.darcy.sanguo.tactic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.darcy.sanguo.service.common.TacticService;

import sango.packet.PbCommons;

public class Tactic {
	public static final int MAX_LEVEL = 5;
	private int level;
	private TacticTemplate template;

	public Tactic(TacticTemplate template) {
		this.template = template;
	}

	public int getBuffByIndex(int index) {
		if ((index < 0) || (index > 5)) {
			return -1;
		}
		if (this.level > 5) {
			this.level = 5;
		}
		int[] buffs = this.template.buffs[this.level];
		return buffs[index];
	}

	public boolean canLevelUp() {
		return (this.level >= 5);
	}

	public void levelUp() {
		if (this.level < 5)
			this.level += 1;
	}

	public int getId() {
		return this.template.id;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public TacticTemplate getTemplate() {
		return this.template;
	}

	public void setTemplate(TacticTemplate template) {
		this.template = template;
	}

	public static Tactic readObject(ObjectInputStream in) {
		try {
			int level = in.readInt();
			int id = in.readInt();
			int type = in.readInt();
			Map map = (Map) TacticService.tactics.get(Integer.valueOf(type));
			if (map == null)
				return null;
			TacticTemplate tt = (TacticTemplate) map.get(Integer.valueOf(id));
			if (tt == null)
				return null;
			Tactic t = new Tactic(tt);
			t.setLevel(level);
			return t;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.level);
		out.writeInt(this.template.id);
		out.writeInt(this.template.type);
	}

	public PbCommons.Tactic genTactic() {
		return PbCommons.Tactic.newBuilder().setId(getId()).setLevel(this.level).build();
	}
}
