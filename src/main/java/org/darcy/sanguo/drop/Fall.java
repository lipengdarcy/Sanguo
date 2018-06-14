package org.darcy.sanguo.drop;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

public class Fall {
	public int type;
	public int itemId;
	public int count;

	public Fall(int type, int id, int count) {
		this.type = type;
		this.itemId = id;
		this.count = count;
	}

	public Reward genReward() {
		ItemTemplate item = null;
		if (this.type == 0) {
			item = ItemService.getItemTemplate(this.itemId);
		}
		Reward r = new Reward(this.type, this.count, item);
		return r;
	}

	public List<Gain> genGains(Player player) {
		List gains = new ArrayList();
		if (this.type == 0) {
			ItemTemplate template = ItemService.getItemTemplate(this.itemId);
			if (!(Item.isCumulative(template.type))) {
				for (int i = 0; i < this.count; ++i) {
					Item item = ItemService.generateItem(template, player);
					Gain gain = new Gain(this.type, 1, item);
					gains.add(gain);
				}
			} else {
				Item item = ItemService.generateItem(template, player);
				Gain gain = new Gain(this.type, this.count, item);
				gains.add(gain);
			}
		} else {
			Gain gain = new Gain(this.type, this.count);
			gains.add(gain);
		}
		return gains;
	}
}
