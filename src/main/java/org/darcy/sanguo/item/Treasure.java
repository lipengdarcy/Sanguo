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
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.player.Player;

import sango.packet.PbTreasure;

/**
 * 宝物装备
 */
public class Treasure extends Item {
	private WeakReference<Player> player;
	private int exp;
	private int enhanceLevel;
	private int warriorId = 0;

	private Map<Integer, Integer> attr = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> enhanceAttr = new HashMap<Integer, Integer>();

	private Map<Integer, Integer> extraAttr = new HashMap<Integer, Integer>();

	public Treasure(ItemTemplate template, int id) {
		super(template);
		this.id = id;
		this.level = 0;
	}

	public void init(Player player) {
		this.player = new WeakReference<Player>(player);
		refreshAttr(false);
	}

	public void refreshAttr(boolean isSync) {
		this.attr.clear();
		calBaseAttr(this.attr, this.level);
		calExtraAttr(this.extraAttr);
		calEnhanceAttr(this.enhanceAttr);
		if (isSync)
			sync();
	}

	public void calBaseAttr(Map<Integer, Integer> attr, int level) {
		attr.clear();
		TreasureTemplate template = (TreasureTemplate) this.template;

		Iterator<?> itx = template.baseAttr.keySet().iterator();
		while (itx.hasNext()) {
			int type = ((Integer) itx.next()).intValue();
			int value = ((Integer) template.baseAttr.get(Integer.valueOf(type))).intValue();
			if (template.growAttr.get(Integer.valueOf(type)) != null) {
				int growValue = ((Integer) template.growAttr.get(Integer.valueOf(type))).intValue();
				value += growValue * level;
			}
			attr.put(Integer.valueOf(type), Integer.valueOf(value));
		}
	}

	public void calExtraAttr(Map<Integer, Integer> attr) {
		attr.clear();

		TreasureTemplate template = (TreasureTemplate) this.template;
		Iterator<?> itx = template.extraAttr.keySet().iterator();
		while (itx.hasNext()) {
			int tmpLevel = ((Integer) itx.next()).intValue();
			if (this.level >= tmpLevel) {
				Map map = (Map) template.extraAttr.get(Integer.valueOf(tmpLevel));
				Set<Map.Entry> set = map.entrySet();
				for (Map.Entry entry : set) {
					int type = ((Integer) entry.getKey()).intValue();
					int value = ((Integer) entry.getValue()).intValue();
					attr.put(Integer.valueOf(type), Integer.valueOf(value));
				}
			}
		}
	}

	public void calEnhanceAttr(Map<Integer, Integer> attr) {
		attr.clear();
		if ((!(canEnhance())) || (this.enhanceLevel <= 0))
			return;
		TreasureTemplate template = (TreasureTemplate) this.template;
		Iterator itx = template.enhanceAttr.keySet().iterator();
		while (itx.hasNext()) {
			int type = ((Integer) itx.next()).intValue();
			int value = ((Integer) template.enhanceAttr.get(Integer.valueOf(type))).intValue() * this.enhanceLevel;
			attr.put(Integer.valueOf(type), Integer.valueOf(value));
		}
	}

	public void sync() {
		Player player = getPlayer();
		if (player != null) {
			BagGrid grid = player.getBags().getGrid(this.id, getTemplateId());
			if (grid != null)
				player.getDataSyncManager().addBagSync(1, grid, 0);
		}
	}

	public int getTreasureType() {
		return ((TreasureTemplate) this.template).treasureType;
	}

	public int getMaxLevel() {
		return ((TreasureTemplate) this.template).maxLevel;
	}

	public int getMaxEnhanceLevel() {
		return ((TreasureTemplate) this.template).maxEnhanceLevel;
	}

	public boolean canEnhance() {
		return ((TreasureTemplate) this.template).canEnhance;
	}

	public List<Reward> getAllEnhanceCost() {
		TreasureTemplate template = (TreasureTemplate) this.template;
		List costItems = new ArrayList();

		Map itemMaps = new HashMap();

		Map otherMaps = new HashMap();
		for (int i = 1; i <= this.enhanceLevel; ++i) {
			List list = (List) template.enhanceCost.get(Integer.valueOf(i));
			if ((list != null) && (list.size() > 0)) {
				for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
					Reward old;
					String str = (String) localIterator.next();
					Reward cost = new Reward(str);
					if (cost.type == 0) {
						old = (Reward) itemMaps.get(Integer.valueOf(cost.template.id));
						if (old != null)
							old.count += cost.count;
						else
							itemMaps.put(Integer.valueOf(cost.template.id), cost);
					} else {
						old = (Reward) otherMaps.get(Integer.valueOf(cost.type));
						if (old != null)
							old.count += cost.count;
						else {
							otherMaps.put(Integer.valueOf(cost.type), cost);
						}
					}
				}
			}
		}
		costItems.addAll(itemMaps.values());
		costItems.addAll(otherMaps.values());
		return costItems;
	}

	public int addExp(int addExp) {
		int[] result = addExpPreview(addExp);
		setExp(result[0]);
		if (result[1] > 0) {
			levelUp(result[1]);
		}
		refreshAttr(true);
		return result[1];
	}

	public int[] addExpPreview(int addExp) {
		int baseExp = this.exp + addExp;
		int maxLevel = getMaxLevel();
		int levelUp = 0;

		while (this.level + levelUp < maxLevel) {
			int nextLevel = this.level + levelUp + 1;
			int expRuleId = ((TreasureTemplate) this.template).intensifyRule;
			int needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(expRuleId,
					nextLevel);
			if (baseExp < needExp)
				break;
			++levelUp;
			baseExp -= needExp;
		}

		return new int[] { baseExp, levelUp };
	}

	private void levelUp(int addLevel) {
		setLevel(this.level + addLevel);
	}

	public void enhance() {
		setEnhanceLevel(this.enhanceLevel + 1);
		refreshAttr(true);
	}

	public void clear() {
		setExp(0);
		setLevel(0);
		setEnhanceLevel(0);
		refreshAttr(true);
	}

	public PbTreasure.Treasure genTreasure() {
		PbTreasure.Treasure.Builder builder = PbTreasure.Treasure.newBuilder();
		builder.setId(this.id);
		builder.setType(((TreasureTemplate) this.template).treasureType);
		builder.setTemplateId(this.template.id);
		builder.setLevel(this.level);
		builder.setExp(this.exp);
		builder.setWarriorId(this.warriorId);
		builder.setAttr(Attributes.genAttribute(this.attr));
		builder.setEnhanceLevel(this.enhanceLevel);
		int expRuleId = ((TreasureTemplate) this.template).intensifyRule;
		int needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(expRuleId,
				this.level + 1);
		builder.setNeedExp(needExp);
		if (canEnhance()) {
			builder.setEnhanceAttr(Attributes.genAttribute(this.enhanceAttr));
		}
		return builder.build();
	}

	public int getPrice() {
		return this.template.price;
	}

	public void setPrice(int price) {
	}

	public String getName() {
		return this.template.name;
	}

	public boolean canSell() {
		return (getPrice() > 0);
	}

	public int getExp() {
		return this.exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getEnhanceLevel() {
		return this.enhanceLevel;
	}

	public void setEnhanceLevel(int enhanceLevel) {
		this.enhanceLevel = enhanceLevel;
	}

	public int getWarriorId() {
		return this.warriorId;
	}

	public void setWarriorId(int warriorId) {
		this.warriorId = warriorId;
		sync();
	}

	public Map<Integer, Integer> getAttr() {
		return this.attr;
	}

	public void setAttr(Map<Integer, Integer> attr) {
		this.attr = attr;
	}

	public Map<Integer, Integer> getEnhanceAttr() {
		return this.enhanceAttr;
	}

	public void setEnhanceAttr(Map<Integer, Integer> enhanceAttr) {
		this.enhanceAttr = enhanceAttr;
	}

	public Map<Integer, Integer> getExtraAttr() {
		return this.extraAttr;
	}

	public void setExtraAttr(Map<Integer, Integer> extraAttr) {
		this.extraAttr = extraAttr;
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}
}
