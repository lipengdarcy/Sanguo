package org.darcy.sanguo.drop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.player.Player;

public class DropGroup {
	public int id;
	public String description;
	int mode;
	List<Drop> drops = new ArrayList();

	public DropGroup(int id, int mode, String description) {
		this.id = id;
		this.description = description;
		this.mode = mode;
	}

	public void addDrop(Drop drop) {
		this.drops.add(drop);
	}

	public List<Drop> getDrops() {
		return this.drops;
	}

	public List<Gain> genGains(Player player) {
		List gains = new ArrayList();
		for (Fall fall : fall()) {
			gains.addAll(fall.genGains(player));
		}
		if ((gains.size() == 0) && (this.mode == 1)) {
			throw new RuntimeException("DropGroup gain nothing, mode is weight, id:" + this.id);
		}
		return gains;
	}

	public List<Fall> fall() {

		List falls = new ArrayList();
		List selects = new ArrayList();
		if (this.mode == 1) {
			int sumweight = 0;
			for (Drop drop : this.drops) {
				sumweight = (int) (sumweight + drop.getRate());
			}
			int rnd = (int) (Math.random() * sumweight);
			int i = 0;
			for (Drop drop : this.drops) {
				i = (int) (i + drop.getRate());
				if (rnd < i) {
					selects.add(drop);
				}
			}
		} else if (this.mode == 0) {
			for (Iterator it = this.drops.iterator(); it.hasNext();) {
				Drop drop = (Drop) it.next();
				float rnd = (float) Math.random();
				if (rnd < drop.getRate()) {
					selects.add(drop);
				}
			}
		}
		for (Iterator it = selects.iterator(); it.hasNext();) {
			Fall fall;
			Drop drop = (Drop) it.next();
			if (drop instanceof ItemDrop) {
				ItemDrop itemdrop = (ItemDrop) drop;
				fall = new Fall(0, itemdrop.itemId, itemdrop.count);
				falls.add(fall);
			} else if (drop instanceof SubDrop) {
				SubDrop sd = (SubDrop) drop;
				DropService service = (DropService) Platform.getServiceManager().get(DropService.class);
				DropGroup group = service.getDropGroup(sd.groupId);
				if (group != null) {
					List<Fall> subfalls = group.fall();
					for (Fall f : subfalls)
						falls.add(f);
				}
			} else if (drop instanceof MoneyDrop) {
				MoneyDrop md = (MoneyDrop) drop;
				fall = new Fall(2, -1, md.value);
				falls.add(fall);
			} else if (drop instanceof ExpDrop) {
				ExpDrop ed = (ExpDrop) drop;
				fall = new Fall(4, -1, ed.value);
				falls.add(fall);
			} else if (drop instanceof JewelDrop) {
				JewelDrop jd = (JewelDrop) drop;
				fall = new Fall(3, -1, jd.value);
				falls.add(fall);
			} else if (drop instanceof StaminaDrop) {
				StaminaDrop pd = (StaminaDrop) drop;
				fall = new Fall(6, -1, pd.value);
				falls.add(fall);
			} else if (drop instanceof VitalityDrop) {
				VitalityDrop vd = (VitalityDrop) drop;
				fall = new Fall(5, -1, vd.value);
				falls.add(fall);
			} else if ((drop.getType() == 8) || (drop.getType() == 7) || (drop.getType() == 9) || (drop.getType() == 10)
					|| (drop.getType() == 11)) {
				DigitalDrop dd = (DigitalDrop) drop;
				fall = new Fall(drop.getType(), -1, dd.value);
				falls.add(fall);
			} else {

			}
		}

		return falls;
	}
}
