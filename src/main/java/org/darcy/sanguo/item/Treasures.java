package org.darcy.sanguo.item;

import java.lang.ref.WeakReference;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.player.Player;

public class Treasures {
	private Treasure[] treasures = new Treasure[2];
	private WeakReference<Player> player;

	public void init(Player player) {
		this.player = new WeakReference(player);
	}

	public Treasure equip(Treasure treasure, Warrior warrior) {
		Player player = getPlayer();
		if (player == null) {
			return null;
		}
		int type = ((TreasureTemplate) treasure.template).treasureType;
		if (treasure.getWarriorId() > 0) {
			Warrior oldWarrior = (Warrior) player.getBags().getItemById(treasure.getWarriorId(), 2);
			if (oldWarrior != null) {
				oldWarrior.getTreasures().unEquip(treasure, oldWarrior);
			}
		}
		treasure.setWarriorId(warrior.getId());
		Treasure old = null;
		if (this.treasures[(type - 1)] != null) {
			old = this.treasures[(type - 1)];
			old.setWarriorId(0);
			Platform.getLog().logEquip(player, old, warrior, "unequip");
		}
		this.treasures[(type - 1)] = treasure;
		Platform.getLog().logEquip(player, treasure, warrior, "equip");
		return old;
	}

	public void unEquip(Treasure treasure, Warrior w) {
		int type = ((TreasureTemplate) treasure.template).treasureType;
		treasure.setWarriorId(0);
		if ((this.treasures[(type - 1)] != null) && (this.treasures[(type - 1)].getId() == treasure.getId())) {
			this.treasures[(type - 1)] = null;
			Player player = getPlayer();
			if (player != null)
				Platform.getLog().logEquip(player, treasure, w, "unequip");
		}
	}

	public void unAllEquip() {
		for (int i = 0; i < this.treasures.length; ++i)
			if (this.treasures[i] != null) {
				this.treasures[i].setWarriorId(0);
				this.treasures[i] = null;
			}
	}

	public Attributes getTotalAttributes() {
		Attributes attr = new Attributes();
		for (Treasure treasure : this.treasures) {
			if (treasure != null) {
				Attributes.addAttr(new Attributes[] { attr, Attributes.newAttributes(treasure.getAttr()),
						Attributes.newAttributes(treasure.getExtraAttr()),
						Attributes.newAttributes(treasure.getEnhanceAttr()) });
			}
		}
		return attr;
	}

	public Treasure[] getTreasures() {
		return this.treasures;
	}

	public void setTreasures(Treasure[] treasures) {
		this.treasures = treasures;
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}
}
