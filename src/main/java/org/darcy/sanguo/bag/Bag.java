package org.darcy.sanguo.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Debris;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.loottreasure.DebrisOwn;
import org.darcy.sanguo.loottreasure.LootTreasure;
import org.darcy.sanguo.loottreasure.LootTreasureData;
import org.darcy.sanguo.loottreasure.LootTreasureService;
import org.darcy.sanguo.player.Player;

import sango.packet.PbBag;

public class Bag {
	public static final int BAG_ADD_PER_SIZE = 5;
	public static final int BAG_ADD_BASE_COST_JEWEL = 25;
	int type;
	int size;
	int addSize;
	List<BagGrid> grids = new LinkedList();
	private WeakReference<Player> player;

	public Bag(int type, int size, int addSize) {
		this.type = type;
		this.size = size;
		this.addSize = addSize;
	}

	public void init(Player player) {
		this.player = new WeakReference(player);

		if (!(Item.isCumulative(this.type))) {
			BagGrid grid;
			Iterator localIterator;
			switch (this.type) {
			case 2:
				for (localIterator = this.grids.iterator(); localIterator.hasNext();) {
					grid = (BagGrid) localIterator.next();
					Warrior warrior = (Warrior) grid.getItem();
					warrior.init(player);
				}
				break;
			case 4:
				for (localIterator = this.grids.iterator(); localIterator.hasNext();) {
					grid = (BagGrid) localIterator.next();
					Equipment equip = (Equipment) grid.getItem();
					equip.init(player);
				}
				break;
			case 1:
				for (localIterator = this.grids.iterator(); localIterator.hasNext();) {
					grid = (BagGrid) localIterator.next();
					Treasure treasure = (Treasure) grid.getItem();
					treasure.init(player);
				}
			case 3:
			}
		}
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void addBagSize(int addSize) {
		this.addSize += addSize;
		Player player = getPlayer();
		if (player != null)
			player.getDataSyncManager().addBagSizeSync(this.type, getSize());
	}

	public int getSize() {
		if (this.size == -1) {
			return this.size;
		}
		return (this.size + this.addSize);
	}

	public int getAddSize() {
		return this.addSize;
	}

	public void setAddSize(int addSize) {
		this.addSize = addSize;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<BagGrid> getGrids() {
		return this.grids;
	}

	public int getHasItemSize() {
		return this.grids.size();
	}

	public int getLeftSize() {
		if (getSize() == -1) {
			return -1;
		}
		return (getSize() - getHasItemSize());
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}

	public BagGrid getGrid(int id, int templateId) {
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		while (it.hasNext()) {
			grid = (BagGrid) it.next();
			if ((grid != null) && (grid.getItem() != null) && (grid.getItem().getTemplateId() == templateId)
					&& (grid.getItem().getId() == id)) {
				return grid;
			}
		}
		return null;
	}

	public int getItemCount(int id, int templateId) {
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		while (it.hasNext()) {
			grid = (BagGrid) it.next();
			if ((grid != null) && (grid.getItem() != null) && (grid.getItem().getTemplateId() == templateId)
					&& (grid.getItem().getId() == id)) {
				return grid.getCount();
			}
		}
		return 0;
	}

	public List<Item> getItemByTemplateId(int templateId) {
		List list = new ArrayList();
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		while (it.hasNext()) {
			grid = (BagGrid) it.next();
			if ((grid == null) || (grid.getItem() == null) || (grid.getItem().getTemplateId() != templateId))
				continue;
			list.add(grid.getItem());
		}

		return list;
	}

	public Item getItemById(int id) {
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		while (it.hasNext()) {
			grid = (BagGrid) it.next();
			if ((grid != null) && (grid.getItem() != null) && (grid.getItem().getId() == id)) {
				return grid.getItem();
			}
		}

		return null;
	}

	public int getItemCount(int templateId) {
		int count = 0;
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		while (it.hasNext()) {
			grid = (BagGrid) it.next();
			if ((grid == null) || (grid.getItem() == null) || (grid.getItem().getTemplateId() != templateId))
				continue;
			count += grid.getCount();
		}

		return count;
	}

	public boolean removeItem(int id, int templateId, int count) {
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		while (it.hasNext()) {
			grid = (BagGrid) it.next();
			if ((grid == null) || (grid.getItem() == null) || (grid.getItem().getTemplateId() != templateId)
					|| (grid.getItem().getId() != id))
				continue;
			if (grid.getCount() < count) {
				return false;
			}
			grid.setCount(grid.getCount() - count);

			if (grid.getCount() == 0) {
				sync(grid, 2);
				it.remove();
			} else {
				sync(grid, 0);
			}
			debrisOwnRemove(grid);
		}

		return true;
	}

	private void sync(BagGrid grid, int optType) {
		Player player = getPlayer();
		if (player != null)
			player.getDataSyncManager().addBagSync(this.type, grid, optType);
	}

	protected void addItem(Item item, int count) {
		BagGrid grid = null;
		Iterator it = this.grids.iterator();
		if (Item.isCumulative(this.type)) {
			while (it.hasNext()) {
				grid = (BagGrid) it.next();
				if ((grid == null) || (grid.getItem() == null)
						|| (grid.getItem().getTemplateId() != item.getTemplateId())
						|| (grid.getItem().getId() != item.getId()))
					continue;
				grid.setCount(grid.getCount() + count);
				sync(grid, 0);
				cumulativeAdd(item, count);
				return;
			}

			grid = new BagGrid();
			grid.setCount(count);
			grid.setItem(item);
			this.grids.add(grid);
			cumulativeAdd(item, count);
		} else {
			if (count != 1) {
				throw new RuntimeException("非叠加道具一次只能添加1个，id：" + item.getTemplateId());
			}

			grid = new BagGrid();
			grid.setCount(count);
			grid.setItem(item);
			this.grids.add(grid);
		}
		sync(grid, 1);
	}

	public void cumulativeAdd(Item item, int count) {
		if (this.type == 3) {
			Player player;
			Debris debris = (Debris) item;
			DebrisTemplate template = (DebrisTemplate) debris.getTemplate();
			if ((template.debrisType == 4) || (template.debrisType == 3)) {
				if (!(LootTreasureData.isDebrisOwn(template.getObjectTemplateId()))) {
					return;
				}
				player = getPlayer();
				if (player != null) {
					Map map = LootTreasure.getDebrisByTreasure(player, template.getObjectTemplateId());
					boolean flag = true;
					if (map.size() == 1) {
						int debrisTemplateId = ((Integer) map.keySet().iterator().next()).intValue();
						int debrisCount = ((Integer) map.get(Integer.valueOf(debrisTemplateId))).intValue();
						if (debrisCount == 1) {
							flag = false;
						}
					}
					if (flag) {
						Set<Map.Entry> set = map.entrySet();
						for (Map.Entry entry : set) {
							int debrisTemplateId = ((Integer) entry.getKey()).intValue();
							DebrisOwn own = ((LootTreasureService) Platform.getServiceManager()
									.get(LootTreasureService.class)).getDebrisOwn(debrisTemplateId);
							List list = own.getOwners();
							if (!(list.contains(Integer.valueOf(player.getId())))) {
								list.add(Integer.valueOf(player.getId()));
								Platform.getEntityManager().putInEhCache(DebrisOwn.class.getName(),
										Integer.valueOf(debrisTemplateId), own);
							}
						}
					}
					player.getLootTreasure().addCompoundCount(template.getObjectTemplateId(), count);
				}
			} else {
				player = getPlayer();
				if (player != null)
					if (template.debrisType == 1) {
						if (debris.canCompound(player) == null)
							Platform.getEventManager().addEvent(new Event(2035, new Object[] { player }));
					} else {
						if ((template.debrisType != 2) || (debris.canCompound(player) != null))
							return;
						Platform.getEventManager().addEvent(new Event(2036, new Object[] { player }));
					}
			}
		} else {
			if ((this.type != 0) || (item.getTemplateId() != 11005))
				return;
			Player player = getPlayer();
			if (player != null)
				Platform.getEventManager().addEvent(new Event(2072, new Object[] { player }));
		}
	}

	public void debrisOwnRemove(BagGrid grid) {
		if (this.type == 3) {
			Item item = grid.getItem();
			Debris debris = (Debris) item;
			DebrisTemplate template = (DebrisTemplate) debris.getTemplate();
			if ((template.debrisType != 4) && (template.debrisType != 3))
				return;
			if (!(LootTreasureData.isDebrisOwn(template.getObjectTemplateId()))) {
				return;
			}
			Player player = getPlayer();
			if (player != null) {
				int debrisCount;
				List<Integer> dealList = new ArrayList();
				if (grid.getCount() <= 0) {
					dealList.add(Integer.valueOf(template.id));
				}
				Map map = LootTreasure.getDebrisByTreasure(player, template.getObjectTemplateId());
				if (map.size() == 1) {
					int debrisTemplateId = ((Integer) map.keySet().iterator().next()).intValue();
					debrisCount = ((Integer) map.get(Integer.valueOf(debrisTemplateId))).intValue();
					if (debrisCount == 1) {
						if (debrisTemplateId == template.id) {
							if (!(dealList.contains(Integer.valueOf(template.id))))
								dealList.add(Integer.valueOf(template.id));
						} else {
							dealList.add(Integer.valueOf(debrisTemplateId));
						}
					}
				}
				for (Integer id : dealList) {
					DebrisOwn own = ((LootTreasureService) Platform.getServiceManager().get(LootTreasureService.class))
							.getDebrisOwn(id.intValue());
					own.getOwners().remove(new Integer(player.getId()));
					Platform.getEntityManager().putInEhCache(DebrisOwn.class.getName(), id, own);
				}
			}
		}
	}

	public static Bag readObject(ObjectInputStream in, int version) {
		try {
			int type = in.readInt();
			int addSize = in.readInt();
			int itemSize = in.readInt();
			Bag bag = new Bag(type, Bags.INITIAL_SIZE[type], addSize);
			for (int i = 0; i < itemSize; ++i) {
				int count = in.readInt();
				Item item = Item.readObject(in, version);
				bag.addItem(item, count);
			}

			return bag;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.type);
		out.writeInt(this.addSize);
		out.writeInt(getHasItemSize());
		for (BagGrid grid : this.grids) {
			out.writeInt(grid.getCount());
			grid.getItem().writeObject(out);
		}
	}

	public PbBag.Bag genPbBag() {
		PbBag.Bag.Builder builder = PbBag.Bag.newBuilder();
		builder.setType(this.type);
		builder.setSize(getSize());
		for (BagGrid grid : this.grids) {
			builder.addGrids(grid.genPbBagGrid());
		}
		return builder.build();
	}
}
