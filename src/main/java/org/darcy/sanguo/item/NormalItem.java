package org.darcy.sanguo.item;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.item.itemeffect.EffectResult;
import org.darcy.sanguo.player.Player;

import sango.packet.PbItem;

public class NormalItem extends Item {
	public NormalItem(ItemTemplate template) {
		super(template);
		this.id = 0;
	}

	public int getPrice() {
		return this.template.price;
	}

	public void setPrice(int price) {
	}

	public EffectResult used(Player player) {
		NormalItemTemplate normalItemTemplate = (NormalItemTemplate) this.template;
		if ((normalItemTemplate == null) || (normalItemTemplate.effect == null)) {
			return new EffectResult(1, 1);
		}
		EffectResult result = normalItemTemplate.effect.used(player);
		if (result.result == 0) {
			List<String> needItems = normalItemTemplate.needItems;
			List<Reward> needs = new ArrayList<Reward>();
			for (String str : needItems) {
				needs.add(new Reward(str));
			}
			if (normalItemTemplate.normalItemType != 2) {
				player.getBags().removeItem(this.id, this.template.id, 1, "itemuse");
				if (needItems.size() > 0) {
					for (Reward reward : needs) {
						if (reward.template != null) {
							reward.remove(player, "itemuse");
						}
					}
				}
			}
		}
		return result;
	}

	public PbItem.NormalItem genNormalItem() {
		PbItem.NormalItem.Builder builder = PbItem.NormalItem.newBuilder();
		builder.setItemId(this.id);
		builder.setTemplateId(getTemplateId());
		return builder.build();
	}

	public String getName() {
		return this.template.name;
	}

	public boolean canSell() {
		return (getPrice() > 0);
	}
}
