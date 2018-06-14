package org.darcy.sanguo.glory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.darcy.sanguo.service.GloryService;

import sango.packet.PbCommons;

public class Glory {
	public static final int MAX_LEVEL = 10;
	private int level;
	private GloryTemplate template;

	public Glory(GloryTemplate template) {
		this.level = 1;
		this.template = template;
	}

	public int getType() {
		return this.template.type;
	}

	public int getId() {
		return this.template.id;
	}

	public int getBuffByIndex(int index) {
		if ((index < 0) || (index > 5)) {
			return -1;
		}
		boolean isFront = index < 3;
		return ((isFront) ? this.template.frontBuffs[(this.level - 1)] : this.template.behindBuffs[(this.level - 1)]);
	}

	public void levelUp() {
		this.level += 1;
	}

	public void clear() {
		this.level = 1;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public GloryTemplate getTemplate() {
		return this.template;
	}

	public void setTemplate(GloryTemplate template) {
		this.template = template;
	}

	public static Glory readObject(ObjectInputStream in) {
		try {
			int level = in.readInt();
			int id = in.readInt();
			GloryTemplate gt = GloryService.getTemplate(id);
			if (gt == null)
				return null;
			Glory g = new Glory(gt);
			g.level = level;
			return g;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.level);
		out.writeInt(this.template.id);
	}

	public PbCommons.Glory genGlory() {
		return PbCommons.Glory.newBuilder().setId(getId()).setLevel(this.level).build();
	}
}
