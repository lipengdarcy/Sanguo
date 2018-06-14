package org.darcy.sanguo.loottreasure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.item.TreasureTemplate;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbLootTreasure;
import sango.packet.PbUser;

public class LootTreasure implements PlayerBlobEntity {
	private static final long serialVersionUID = -3622314906714169650L;
	private static int version = 1;

	public Map<Integer, List<Integer>> rivalIds = new HashMap();

	public Map<Integer, LootRobot> robots = new HashMap();

	private Map<Integer, Integer> canCompoundCount = new LinkedHashMap();

	public void init() {
		int[] baseTreasure = LootTreasureData.baseTreasure;
		for (int i : baseTreasure)
			this.canCompoundCount.put(Integer.valueOf(i), Integer.valueOf(99999));
	}

	public static void shield(Player player) {
		ShieldInfo info = ShieldInfo.getShiledInfo(player.getId());
		long cur = System.currentTimeMillis();
		if (cur - info.getStartShield() > info.getCurShieldTime()) {
			info.setStartShield(cur);
			info.setCurShieldTime(LootTreasureData.shieldTime * 1000);
		} else {
			long curShieldTime = info.getCurShieldTime() + LootTreasureData.shieldTime * 1000;
			curShieldTime = Math.min(curShieldTime, LootTreasureData.maxShieldTime * 1000);
			info.setCurShieldTime(curShieldTime);
		}
		Platform.getEntityManager().putInEhCache(ShieldInfo.class.getName(), Integer.valueOf(player.getId()), info);
	}

	public static void removeShield(Player player) {
		ShieldInfo info = ShieldInfo.getShiledInfo(player.getId());
		info.setCurShieldTime(0L);
		Platform.getEntityManager().putInEhCache(ShieldInfo.class.getName(), Integer.valueOf(player.getId()), info);
	}

	public static boolean isShield(Player player) {
		ShieldInfo info = ShieldInfo.getShiledInfo(player.getId());
		return info.isShield();
	}

	public static long getRestShieldTime(Player player) {
		ShieldInfo info = ShieldInfo.getShiledInfo(player.getId());
		long cur = System.currentTimeMillis();
		long rest = info.getCurShieldTime() - (cur - info.getStartShield());
		return Math.max(0L, rest);
	}

	public void addCompoundCount(int templateId, int addCount) {
		Integer count = (Integer) this.canCompoundCount.get(Integer.valueOf(templateId));
		if (count == null) {
			count = new Integer(0);
		}
		count = Integer.valueOf(count.intValue() + Math.max(0, addCount));
		this.canCompoundCount.put(Integer.valueOf(templateId), count);
	}

	public void removeCompoundCount(int templateId) {
		Integer count = (Integer) this.canCompoundCount.get(Integer.valueOf(templateId));
		if (count == null) {
			count = new Integer(0);
		}
		if (count.intValue() > 0) {
			count = Integer.valueOf(count.intValue() - 1);
		}
		this.canCompoundCount.put(Integer.valueOf(templateId), count);
	}

	public static int getDebrisCountByTreasure(Player player, int templateId) {
		TreasureTemplate template = (TreasureTemplate) ItemService.getItemTemplate(templateId);
		List<Integer> debris = template.debris;
		int count = 0;
		for (Integer debrisId : debris) {
			count += player.getBags().getItemCount(debrisId.intValue());
		}
		return count;
	}

	public static Map<Integer, Integer> getDebrisByTreasure(Player player, int templateId) {
		Map result = new HashMap();
		TreasureTemplate template = (TreasureTemplate) ItemService.getItemTemplate(templateId);
		List<Integer> debris = template.debris;
		for (Integer id : debris) {
			BagGrid grid = player.getBags().getGrid(0, id.intValue());
			if (grid != null) {
				result.put(Integer.valueOf(grid.getItem().getTemplateId()), Integer.valueOf(grid.getCount()));
			}
		}
		return result;
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.rivalIds = new HashMap();
			this.canCompoundCount = new LinkedHashMap();
			this.robots = new HashMap();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				int id = in.readInt();
				int count = in.readInt();
				this.canCompoundCount.put(Integer.valueOf(id), Integer.valueOf(count));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(this.canCompoundCount.size());
		Iterator itx = this.canCompoundCount.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			int count = ((Integer) this.canCompoundCount.get(Integer.valueOf(id))).intValue();
			out.writeInt(id);
			out.writeInt(count);
		}
	}

	public static PbLootTreasure.LootTreasureTarget genLootTreasureTarget(PbUser.MiniUser user, int dropType,
			boolean isRobot) {
		PbLootTreasure.LootTreasureTarget.Builder builder = PbLootTreasure.LootTreasureTarget.newBuilder();
		builder.setUser(user);
		builder.setType(PbLootTreasure.DropType.valueOf(dropType));
		builder.setIsRobot(isRobot);
		return builder.build();
	}

	public Map<Integer, Integer> getCanCompoundCount() {
		return this.canCompoundCount;
	}

	public void setCanCompoundCount(Map<Integer, Integer> canCompoundCount) {
		this.canCompoundCount = canCompoundCount;
	}

	public int getBlobId() {
		return 3;
	}
}
