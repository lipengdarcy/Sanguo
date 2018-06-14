package org.darcy.sanguo.bag;

import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Debris;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.NormalItem;
import org.darcy.sanguo.item.Treasure;

import sango.packet.PbBag;

public class BagGrid {
	private Item item;
	private int count;

	public int getCount() {
		return this.count;
	}

	public Item getItem() {
		return this.item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public void setCount(int count) {
		this.count = count;
	}

	protected void add(Item tempitem, int addValue) {
	}

	protected void dec(Item item, int decValue) {
	}

	public PbBag.BagGrid genPbBagGrid() {
		PbBag.BagGrid.Builder builder = PbBag.BagGrid.newBuilder();
		builder.setCount(this.count);
		builder.setId(this.item.getId());
		builder.setTemplateId(this.item.getTemplateId());
		if (this.item.getItemType() == 0)
			builder.setNormal(((NormalItem) this.item).genNormalItem());
		else if (this.item.getItemType() == 2)
			builder.setHero(((Warrior) this.item).genWarrior());
		else if (this.item.getItemType() == 3)
			builder.setDebris(((Debris) this.item).genDebris());
		else if (this.item.getItemType() == 4)
			builder.setEquip(((Equipment) this.item).genEquipment());
		else if (this.item.getItemType() == 1) {
			builder.setTreasure(((Treasure) this.item).genTreasure());
		}
		return builder.build();
	}
}
