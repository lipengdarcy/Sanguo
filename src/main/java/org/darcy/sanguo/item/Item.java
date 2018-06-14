package org.darcy.sanguo.item;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.service.common.ItemService;

public abstract class Item {
	public static final int TYPE_NORMALITEM = 0;
	public static final int TYPE_TREASURE = 1;
	public static final int TYPE_HERO = 2;
	public static final int TYPE_DEBRIS = 3;
	public static final int TYPE_EQUIPMENT = 4;
	public static final int TYPE_PET = 5;
	public static final int TYPE_SOUL = 6;
	public static final int ITEM_ID_FORGE_ITEM = 11010;
	public static final int ITEM_ID_XILIANSHI = 11004;
	public static final int ITEM_ID_BAOWUJINGHUA = 38001;
	public static final int ITEM_ID_JINGYANYINSHU = 37301;
	public static final int ITEM_ID_JINGYANJINSHU = 37302;
	public static final int ITEM_ID_JINGYANJINMA = 36302;
	public static final int ITEM_ID_JINGYANYINMA = 36301;
	public static final int ITEM_ID_SHIELD = 11002;
	public static final int ITEM_ID_MALE_HERO = 1;
	public static final int ITEM_ID_FEMALE_HERO = 11;
	public static final int ITEM_ID_CHANGE_NAME = 10030;
	public static final int ITEM_ID_GOLDEN_BOX = 12003;
	public static final int ITEM_ID_GOLDEN_KEY = 12008;
	public static final int ITEM_ID_JINGYANJINSHU_ONE = 23095;
	public static final int ITEM_ID_JINGYANJINSHU_TWO = 23096;
	public static final int ITEM_ID_JINGYANJINSHU_THREE = 23097;
	public static final int ITEM_ID_JINGYANJINMA_ONE = 23089;
	public static final int ITEM_ID_JINGYANJINMA_TWO = 23090;
	public static final int ITEM_ID_JINGYANJINMA_THREE = 23091;
	public static final int ITEM_ID_QICEFU = 11005;
	public static final int DEFAULT_ID = 0;
	public int id;
	public int level = 1;
	public ItemTemplate template;

	public Item(ItemTemplate template) {
		this.template = template;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public abstract String getName();

	public int getTemplateId() {
		return this.template.id;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public abstract int getPrice();

	public abstract void setPrice(int paramInt);

	public abstract boolean canSell();

	public ItemTemplate getTemplate() {
		return this.template;
	}

	public void setTemplate(ItemTemplate template) {
		this.template = template;
	}

	public int getItemType() {
		return this.template.type;
	}

	public static boolean isCumulative(int type) {
		return ((type == 4) || (type == 2) || (type == 1));
	}

	public static Item readObject(ObjectInputStream in, int version) {
		try {
			int id = in.readInt();
			int templateId = in.readInt();
			ItemTemplate tplt = ItemService.getItemTemplate(templateId);
			Item item = null;
			if (tplt.type == 2) {
				boolean isMain = in.readBoolean();
				if (isMain)
					item = new MainWarrior(tplt, id);
				else {
					item = new Warrior(tplt, id);
				}
				Warrior hero = (Warrior) item;
				hero.setExp(in.readInt());
				hero.setAdvanceLevel(in.readInt());
				hero.setLevel(in.readInt());
				if (version != 1) {
					hero.setEnsUpdated(in.readBoolean());
					hero.setEnsPlanB(in.readBoolean());
				}
			} else if (tplt.type == 5) {
				item = new Pet(tplt);
			} else if (tplt.type == 4) {
				item = new Equipment(tplt, id);
				Equipment equip = (Equipment) item;
				equip.setWarriorId(in.readInt());
				equip.setLevel(in.readInt());
				equip.setPolishCount(in.readInt());
				int polishAttrCount = in.readInt();
				for (int i = 0; i < polishAttrCount; ++i) {
					int equipAttrType = in.readInt();
					int value = in.readInt();
					equip.getPolishAttr().put(Integer.valueOf(equipAttrType), Integer.valueOf(value));
				}
				if (version > 2)
					equip.setForgeLevel(in.readInt());
			} else if (tplt.type == 3) {
				item = new Debris(tplt);
			} else if (tplt.type == 0) {
				item = new NormalItem(tplt);
			} else if (tplt.type == 1) {
				item = new Treasure(tplt, id);
				Treasure treasure = (Treasure) item;
				treasure.setWarriorId(in.readInt());
				treasure.setLevel(in.readInt());
				treasure.setExp(in.readInt());
				treasure.setEnhanceLevel(in.readInt());
			} else {
				Platform.getLog().logWorld("known item    templateid: " + templateId);
				return null;
			}

			return item;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.id);
		out.writeInt(this.template.id);

		int type = getTemplate().type;
		if (type == 2) {
			Warrior hero = (Warrior) this;
			out.writeBoolean(hero.isMainWarrior());
			out.writeInt(hero.getExp());
			out.writeInt(hero.getAdvanceLevel());
			out.writeInt(hero.getLevel());
			out.writeBoolean(hero.isEnsUpdated());
			out.writeBoolean(hero.isEnsPlanB());
		} else {
			if (type == 3)
				return;
			if (type == 4) {
				Equipment equip = (Equipment) this;
				out.writeInt(equip.getWarriorId());
				out.writeInt(this.level);
				out.writeInt(equip.getPolishCount());
				Map polishAttr = equip.getPolishAttr();
				out.writeInt(polishAttr.size());
				Iterator itx = polishAttr.keySet().iterator();
				while (itx.hasNext()) {
					int equipAttrType = ((Integer) itx.next()).intValue();
					int value = ((Integer) polishAttr.get(Integer.valueOf(equipAttrType))).intValue();
					out.writeInt(equipAttrType);
					out.writeInt(value);
				}

				out.writeInt(equip.getForgeLevel());
			} else {
				if ((type == 5) || (type == 0))
					return;
				if (type == 1) {
					Treasure treasure = (Treasure) this;
					out.writeInt(treasure.getWarriorId());
					out.writeInt(this.level);
					out.writeInt(treasure.getExp());
					out.writeInt(treasure.getEnhanceLevel());
				} else {
					if (type == 6) {
						return;
					}
					Platform.getLog().logWorld("Item writeObject type error,type: " + type + ",itemId:" + this.id);
				}
			}
		}
	}
}
