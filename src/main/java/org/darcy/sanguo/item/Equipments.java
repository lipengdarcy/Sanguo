package org.darcy.sanguo.item;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.equip.ForgeAttr;
import org.darcy.sanguo.item.equip.Suit;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

/**
 * 武将的装备
 */
public class Equipments {
	private Equipment[] equips = new Equipment[4];

	private Map<Integer, Map<Integer, Integer>> suitAttr = new HashMap();
	private WeakReference<Player> player;

	public void clear() {
		for (int i = 0; i < this.equips.length; ++i)
			this.equips[i] = null;
	}

	public void init(Player player) {
		this.player = new WeakReference(player);
	}

	public Equipment getEquipByType(int type) {
		return this.equips[(type - 1)];
	}

	public Equipment equip(Equipment equip, Warrior warrior) {
		Player player = getPlayer();
		if (player == null) {
			return null;
		}
		int type = ((EquipmentTemplate) equip.getTemplate()).equipType;
		if (equip.getWarriorId() > 0) {
			Warrior oldWarrior = (Warrior) player.getBags().getItemById(equip.getWarriorId(), 2);
			if (oldWarrior != null) {
				oldWarrior.getEquips().unEquip(equip, oldWarrior);
			}
		}
		equip.setWarriorId(warrior.getId());
		Equipment old = null;
		if (this.equips[(type - 1)] != null) {
			old = this.equips[(type - 1)];
			old.setWarriorId(0);
			Platform.getLog().logEquip(player, old, warrior, "unequip");
		}
		this.equips[(type - 1)] = equip;
		Platform.getLog().logEquip(player, equip, warrior, "equip");
		calSuitAttrs();
		return old;
	}

	public void unEquip(Equipment equip, Warrior w) {
		int type = ((EquipmentTemplate) equip.getTemplate()).equipType;
		equip.setWarriorId(0);
		if ((this.equips[(type - 1)] != null) && (this.equips[(type - 1)].getId() == equip.getId())) {
			this.equips[(type - 1)] = null;
			Player player = getPlayer();
			if (player != null) {
				Platform.getLog().logEquip(player, equip, w, "unequip");
			}
		}
		calSuitAttrs();
	}

	public void unAllEquip() {
		for (int i = 0; i < this.equips.length; ++i)
			if (this.equips[i] != null) {
				this.equips[i].setWarriorId(0);
				this.equips[i] = null;
			}
	}

	public void calSuitAttrs() {
		int suitId;
		this.suitAttr.clear();
		Map map = new HashMap();
		for (int i = 0; i < this.equips.length; ++i) {
			if (this.equips[i] != null) {
				suitId = ((EquipmentTemplate) this.equips[i].template).suitId;
				if (suitId > 0) {
					List list = (List) map.get(Integer.valueOf(suitId));
					if (list == null) {
						list = new ArrayList();
						map.put(Integer.valueOf(suitId), list);
					}
					list.add(Integer.valueOf(this.equips[i].id));
				}
			}
		}
		if (map.size() > 0) {
			Iterator itx = map.keySet().iterator();
			while (itx.hasNext()) {
				suitId = ((Integer) itx.next()).intValue();
				List idList = (List) map.get(Integer.valueOf(suitId));
				if (idList.size() > 1) {
					Map attrs = new HashMap();
					Suit suit = ((ItemService) Platform.getServiceManager().get(ItemService.class)).getSuit(suitId);
					int num = idList.size();
					for (int i = 2; i <= num; ++i) {
						Map suitAttrs = (Map) suit.attrs.get(Integer.valueOf(i));
						if (suitAttrs != null) {
							Set<Map.Entry> set = suitAttrs.entrySet();
							for (Map.Entry entry : set) {
								int type = ((Integer) entry.getKey()).intValue();
								int value = ((Integer) entry.getValue()).intValue();
								if (attrs.containsKey(Integer.valueOf(type)))
									attrs.put(Integer.valueOf(type), Integer
											.valueOf(((Integer) attrs.get(Integer.valueOf(type))).intValue() + value));
								else {
									attrs.put((Integer) entry.getKey(), (Integer) entry.getValue());
								}
							}
						}
					}
					this.suitAttr.put(Integer.valueOf(suitId), attrs);
				}
			}
		}
	}

	public Attributes getTotalAttributes() {
		Attributes attr = new Attributes();
		for (Equipment equip : this.equips) {
			if (equip != null) {
				Attributes.addAttr(new Attributes[] { attr, equip.getBaseAttr(),
						Attributes.newAttributes(equip.getPolishAttr()) });
			}
		}
		List<Attributes> suitAttrs = getSuitAttrs();
		if (suitAttrs.size() > 0) {
			for (Attributes attributes : suitAttrs) {
				Attributes.addAttr(new Attributes[] { attr, attributes });
			}
		}
		Attributes forgeAttrs = calForgeAttrs();
		if (forgeAttrs != null) {
			Attributes.addAttr(new Attributes[] { attr, forgeAttrs });
		}
		return attr;
	}

	public Attributes calForgeAttrs() {
		int forgeLevel = 0;
		boolean first = true;
		for (Equipment e : this.equips) {
			if ((e == null) || (!(e.canForge()))) {
				return null;
			}
			if (first)
				if (e.getForgeLevel() > 0) {
					forgeLevel = e.getForgeLevel();
					first = false;
				} else if (e.getForgeLevel() < forgeLevel) {
					forgeLevel = e.getForgeLevel();
				}
		}
		if (forgeLevel == 0) {
			return null;
		}
		Map map = new HashMap();
		for (Equipment e : this.equips) {
			ForgeAttr attr = e.getForgeAttr(forgeLevel);
			if (attr != null) {
				int value = 0;
				if (map.containsKey(Integer.valueOf(attr.type))) {
					value = ((Integer) map.get(Integer.valueOf(attr.type))).intValue();
				}
				value += attr.value;
				map.put(Integer.valueOf(attr.type), Integer.valueOf(value));
			}
		}
		return Attributes.newAttributes(map);
	}

	public List<Attributes> getSuitAttrs() {
		List list = new ArrayList();
		Iterator itx = this.suitAttr.keySet().iterator();
		while (itx.hasNext()) {
			int suitId = ((Integer) itx.next()).intValue();
			list.add(Attributes.newAttributes((Map) this.suitAttr.get(Integer.valueOf(suitId))));
		}
		return list;
	}

	public int getEquipCount() {
		int count = 0;
		for (int i = 0; i < this.equips.length; ++i) {
			if (this.equips[i] != null) {
				++count;
			}
		}
		return count;
	}

	public Equipments copy() {
		Equipments equipments = new Equipments();
		int size = this.equips.length;
		for (int i = 0; i < size; ++i) {
			if (this.equips[i] != null) {
				equipments.equips[i] = this.equips[i];
			}
		}
		return equipments;
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}

	public Equipment[] getEquips() {
		return this.equips;
	}

	public void setEquips(Equipment[] equips) {
		this.equips = equips;
	}

	public Map<Integer, Map<Integer, Integer>> getSuitAttr() {
		return this.suitAttr;
	}

	public void setSuitAttr(Map<Integer, Map<Integer, Integer>> suitAttr) {
		this.suitAttr = suitAttr;
	}
}
