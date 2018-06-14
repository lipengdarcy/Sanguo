package org.darcy.sanguo.drop;

import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.player.Player;

import sango.packet.PbCommons;

public class Gain {
	public int type;
	public int count;
	public Item item;

	public Gain(int type, int count) {
		this.type = type;
		this.count = count;
	}

	public Gain(int type, int count, Item item) {
		this.type = type;
		this.count = count;
		this.item = item;
	}

	public void gain(Player player, String optType) {
		if (this.type == 4)
			player.addExp(this.count, optType);
		else if (this.type == 2)
			player.addMoney(this.count, optType);
		else if (this.type == 0)
			player.getBags().addItem(this.item, this.count, optType);
		else if (this.type == 3)
			player.addJewels(this.count, optType);
		else if (this.type == 6)
			player.addStamina(this.count, optType);
		else if (this.type == 5)
			player.addVitality(this.count, optType);
		else if (this.type == 7)
			player.addSpiritJade(this.count, optType);
		else if (this.type == 8)
			player.addWarriorSpirit(this.count, optType);
		else if (this.type == 9)
			player.addHonor(this.count, optType);
		else if (this.type == 10)
			player.addPrestige(this.count, optType);
		else if (this.type == 11)
			player.getTacticRecord().addPoint(this.count, player);
	}

	public boolean equals(Gain gain) {
		if (gain == null)
			return false;
		if (gain.type != this.type)
			return false;
		if (gain.count != this.count)
			return false;
		if (this.type != 0)
			return false;
		if ((this.item == null) || (gain.item == null)) {
			return false;
		}
		return (gain.item.getTemplateId() != this.item.getTemplateId());
	}

	public PbCommons.PbReward.Builder genPbReward() {
		PbCommons.PbReward.Builder b = PbCommons.PbReward.newBuilder();
		b.setCount(this.count).setType(this.type).setTemplateId((this.item == null) ? -1 : this.item.getTemplate().id);
		return b;
	}

	public Reward newReward() {
		return new Reward(this.type, this.count, (this.item == null) ? null : this.item.getTemplate());
	}
}
