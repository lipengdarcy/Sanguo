package org.darcy.sanguo.item.itemeffect;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

public class ItemFallItemEffect extends AbstractItemEffect {
	int itemId;
	int itemCount;

	public ItemFallItemEffect(int count) {
		super(count);
	}

	public EffectResult used(Player player) {
		ItemTemplate template = ItemService.getItemTemplate(this.itemId);

		Item item = ItemService.generateItem(template, player);
		player.getBags().addItem(item, this.itemCount, "itemuse");
		return new EffectResult(0, 2, new Reward[] { new Reward(0, this.itemCount, item.getTemplate()) });
	}

	public void initParams(String[] params) {
		this.itemId = Integer.valueOf(params[0]).intValue();
		this.itemCount = Integer.valueOf(params[1]).intValue();
	}
}
