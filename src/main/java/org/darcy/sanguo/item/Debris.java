package org.darcy.sanguo.item;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;

import sango.packet.PbItem;

/**
 * 碎片
 */
public class Debris extends Item {
	public static final int TYPE_DEBRI_HERO = 1;
	public static final int TYPE_DEBRI_EQUIP = 2;
	public static final int TYPE_DEBRI_TREASURE_HORSE = 3;
	public static final int TYPE_DEBRI_TREASURE_BOOK = 4;

	public Debris(ItemTemplate template) {
		super(template);
		this.id = 0;
	}

	public int getPrice() {
		return this.template.price;
	}

	public void setPrice(int price) {
	}

	public String canCompound(Player player) {
		List<Reward> costs = new ArrayList();
		DebrisTemplate template = (DebrisTemplate) this.template;
		if ((template.costs != null) && (template.costs.size() > 0)) {
			for (String str : template.costs) {
				costs.add(new Reward(str));
			}
		}
		for (Reward cost : costs) {
			String str = cost.check(player);
			if (str != null) {
				return str;
			}
		}
		return null;
	}

	public String getName() {
		return this.template.name;
	}

	public PbItem.Debris genDebris() {
		PbItem.Debris.Builder builder = PbItem.Debris.newBuilder();
		DebrisTemplate template = (DebrisTemplate) this.template;
		builder.setItemId(this.id);
		builder.setTemplateId(getTemplateId());
		builder.setType(template.debrisType);
		builder.setTargetId(template.getObjectTemplateId());
		return builder.build();
	}

	public boolean canSell() {
		return (getPrice() > 0);
	}
}
