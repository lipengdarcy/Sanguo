package org.darcy.sanguo.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbBag;

public class Bags implements PlayerBlobEntity {
	private static final long serialVersionUID = -6566269071416652053L;
	private static final int version = 3;
	public static final int BAG_COUNT = 7;
	public static final int[] INITIAL_SIZE = { 35, 45, 100, -1, 55, 30, 30 };
	public static final int[] MAX_SIZE = { 300, 300, 300, -1, 300, 300, 300 };
	private WeakReference<Player> player;
	Bag[] bags = new Bag[7];

	private int itemId = 1;
	private AtomicInteger itemIdGen;

	public Bags() {
		for (int i = 0; i < 7; ++i) {
			this.bags[i] = new Bag(i, INITIAL_SIZE[i], 0);
		}
		this.itemIdGen = new AtomicInteger(this.itemId);
	}

	public void init(Player referent) {
		this.player = new WeakReference(referent);
		for (int i = 0; i < 7; ++i) {
			this.bags[i].init(referent);
		}
		if (this.itemIdGen == null)
			this.itemIdGen = new AtomicInteger(this.itemId);
	}

	public void initEquipAndTreasure(Player player) {
		Equipment equip;
		Bag equipBag = getBag(4);
		for (BagGrid grid : equipBag.grids)
			if (grid.getItem() != null) {
				equip = (Equipment) grid.getItem();
				if (equip.getWarriorId() > 0) {
					Warrior warrior = player.getWarriors().getWarriorById(equip.getWarriorId());
					if (warrior == null) {
						Platform.getLog()
								.logError("initEquipment error, equip has warrior,but warrior is not on stage,player:"
										+ player.getId() + "equipId:" + equip.getId() + "warriorId:"
										+ equip.getWarriorId());
						equip.setWarriorId(0);
					} else {
						warrior.getEquips().equip(equip, warrior);

						Warrior[] stageWarriors = player.getWarriors().getAllWarriorAndFellow();
						warrior.refreshkEns(stageWarriors);
						warrior.refreshAttributes(false);
					}
				}
			}
		Bag treasureBag = getBag(1);
		for (BagGrid grid : treasureBag.grids)
			if (grid.getItem() != null) {
				Treasure treasure = (Treasure) grid.getItem();
				if (treasure.getWarriorId() > 0) {
					Warrior warrior = player.getWarriors().getWarriorById(treasure.getWarriorId());
					if (warrior == null) {
						Platform.getLog()
								.logError("initTreasure error, treasure has warrior,but warrior is not on stage,player:"
										+ player.getId() + "treasureId:" + treasure.getId() + "warriorId:"
										+ treasure.getWarriorId());
						treasure.setWarriorId(0);
					} else {
						warrior.getTreasures().equip(treasure, warrior);

						Warrior[] stageWarriors = player.getWarriors().getAllWarriorAndFellow();
						warrior.refreshkEns(stageWarriors);
						warrior.refreshAttributes(false);
					}
				}
			}
	}

	public Bag[] getBags() {
		return this.bags;
	}

	public Bag getBag(int type) {
		return this.bags[type];
	}

	public int getMaxSize(int type) {
		return MAX_SIZE[type];
	}

	public int getLeftSize(int type) {
		return this.bags[type].getLeftSize();
	}

	public int getSize(int type) {
		return this.bags[type].getSize();
	}

	public int getUsedSize(int type) {
		return this.bags[type].getHasItemSize();
	}

	public BagGrid getGrid(int id, int templateId) {
		ItemTemplate it = ItemService.getItemTemplate(templateId);
		if (it != null) {
			return this.bags[it.type].getGrid(id, templateId);
		}
		return null;
	}

	public List<Item> getItemByTemplateId(int templateId) {
		ItemTemplate it = ItemService.getItemTemplate(templateId);
		if (it != null) {
			return this.bags[it.type].getItemByTemplateId(templateId);
		}
		return null;
	}

	public Item getItemById(int id) {
		int[] list = { 1, 4, 2 };
		for (int i = 0; i < list.length; ++i) {
			Item item = getItemById(id, list[i]);
			if (item != null) {
				return item;
			}
		}

		return null;
	}

	public Item getItemById(int id, int type) {
		Item item = this.bags[type].getItemById(id);
		return item;
	}

	public int getItemCount(int templateId) {
		ItemTemplate it = ItemService.getItemTemplate(templateId);
		if (it != null) {
			return this.bags[it.type].getItemCount(templateId);
		}
		return 0;
	}

	public boolean removeItem(int id, int templateId, int count, String optType) {
		ItemTemplate it = ItemService.getItemTemplate(templateId);
		if (it != null) {
			boolean result = this.bags[it.type].removeItem(id, templateId, count);
			if (result) {
				Player player = getPlayer();
				if (player != null) {
					Platform.getLog().logRemoveItem(player, it.id, it.name, id, count, getItemCount(it.id), optType);
				}
			}
			return result;
		}
		return false;
	}

	public void addItem(Item item, int count, String optType) {
		this.bags[item.getItemType()].addItem(item, count);
		Player player = getPlayer();
		if (player != null) {
			Platform.getLog().logGetItem(player, item, count, getItemCount(item.getTemplateId()), optType);
			if (item instanceof Warrior) {
				Warrior w = (Warrior) item;
				player.getStarRecord().addHeroIf(w.getTemplateId());
				player.getStarcatalogRecord().addStarcatalog(w);
			}
		}
	}

	public int getFullBag() {
		for (int i = 0; i < this.bags.length; ++i) {
			if (isFullBag(i)) {
				return i;
			}
		}

		return -1;
	}

	public boolean isFullBag(int type) {
		if (this.bags[type].getLeftSize() <= 0) {
			return (this.bags[type].getSize() == -1);
		}

		return false;
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}

	public Object clone() {
		Bags bags = new Bags();
		bags.init(getPlayer());
		bags.bags = ((Bag[]) Arrays.copyOf(this.bags, 7));
		return bags;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();
			this.itemId = in.readInt();
			this.bags = new Bag[7];
			for (int i = 0; i < 7; ++i)
				this.bags[i] = Bag.readObject(in, version);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(3);
		out.writeInt(this.itemId);
		for (int i = 0; i < 7; ++i)
			this.bags[i].writeObject(out);
	}

	public PbBag.Bags genPbBags(int bagType) {
		PbBag.Bags.Builder builder = PbBag.Bags.newBuilder();
		if (bagType == -1) {
			for (Bag bag : this.bags)
				builder.addBags(bag.genPbBag());
		} else {
			builder.addBags(getBag(bagType).genPbBag());
		}

		return builder.build();
	}

	public int getNewItemId() {
		this.itemId = this.itemIdGen.incrementAndGet();
		return this.itemId;
	}

	public Map<Reward, List<Integer>> getExcludeItemIds(List<Reward> list) {
		Map costMap = new HashMap();
		for (Reward r : list) {
			List excludeIds = new ArrayList();
			if ((r.type == 0) && (!(Item.isCumulative(r.template.type)))) {
				List<Item> itemList = getItemByTemplateId(r.template.id);
				if ((itemList != null) && (itemList.size() > 0)) {
					for (Item item : itemList) {
						if (item.getItemType() == 2) {
							Warrior warrior = (Warrior) item;
							if ((warrior.getAdvanceLevel() <= 0) && (warrior.getStageStatus() == 0)
									&& (warrior.getLevel() <= 0))
								continue;
							excludeIds.add(Integer.valueOf(warrior.getId()));
						} else if (item.getItemType() == 4) {
							Equipment equip = (Equipment) item;
							if ((equip.getWarriorId() <= 0) && (equip.getLevel() <= 0)
									&& (equip.getPolishAttr().size() <= 0) && (equip.getForgeLevel() <= 0))
								continue;
							excludeIds.add(Integer.valueOf(equip.getId()));
						} else if (item.getItemType() == 1) {
							Treasure treasure = (Treasure) item;
							if ((treasure.getWarriorId() <= 0) && (treasure.getEnhanceLevel() <= 0)
									&& (treasure.getLevel() <= 0))
								continue;
							excludeIds.add(Integer.valueOf(treasure.getId()));
						}
					}
				}
			}

			costMap.put(r, excludeIds);
		}
		return costMap;
	}

	public int getBlobId() {
		return 2;
	}
}
