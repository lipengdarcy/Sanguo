package org.darcy.sanguo.item;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.item.equip.ForgeAttr;
import org.darcy.sanguo.item.equip.PolishAttr;
import org.darcy.sanguo.item.equip.PolishRandom;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbEquipment;

/**
 * 武器装备
 */
public class Equipment extends Item {
	public static final int POLISH_TYPE_NORMAL = 1;// 普通洗练
	public static final int POLISH_TYPE_MONEY = 2;// 元宝洗练
	public static final int POLISH_TYPE_JEWEL = 3;//
	public static final int MAX_FORGE_LEVEL = 20;
	public static final int FORGE_EXP_RULE = 5008;
	public static final int FORGE_BASE_COST_MONEY = 50000;
	private Attributes baseAttr = new Attributes();

	private Map<Integer, Integer> polishAttr = new HashMap<Integer, Integer>();// 洗练属性

	private Map<Integer, Integer> tmpAttr = new HashMap<Integer, Integer>();

	private int tmpPolishNum = 1;

	private int tmpPolishType = 1;
	private int polishCount;
	private int warriorId = 0;
	private WeakReference<Player> player;
	private int forgeLevel;

	public Equipment(ItemTemplate template, int id) {
		super(template);
		this.id = id;
	}

	public void init(Player player) {
		this.player = new WeakReference(player);
		refreshAttr(false);
	}

	public int getEquipType() {
		return ((EquipmentTemplate) this.template).equipType;
	}

	public int getMaxLevel(int playerLevel) {
		int times = ((EquipmentTemplate) this.template).intensifyLimitRatio;
		return (playerLevel * times);
	}

	public void refreshAttr(boolean isSync) {
		this.baseAttr.clear();
		calBaseAttr(this.baseAttr, this.level, this.forgeLevel);
		if (isSync)
			sync();
	}

	public void sync() {
		Player player = getPlayer();
		if (player != null) {
			BagGrid grid = player.getBags().getGrid(this.id, getTemplateId());
			if (grid != null)
				player.getDataSyncManager().addBagSync(4, grid, 0);
		}
	}

	public void calBaseAttr(Attributes attr, int level, int forgeLevel) {
		Attributes.addAttr(new Attributes[] { attr, ((EquipmentTemplate) this.template).attr });

		int levelHp = (int) (((EquipmentTemplate) this.template).hpGrow / 100.0D * (level - 1));
		int levelAtk = (int) (((EquipmentTemplate) this.template).attackGrow / 100.0D * (level - 1));
		int levelPhyd = (int) (((EquipmentTemplate) this.template).phyDefenceGrow / 100.0D * (level - 1));
		int levelMagd = (int) (((EquipmentTemplate) this.template).magDefenceGrow / 100.0D * (level - 1));

		int forgeHp = (int) (((EquipmentTemplate) this.template).hpForgeGrow / 100.0D * forgeLevel);
		int forgeAtk = (int) (((EquipmentTemplate) this.template).attackForgeGrow / 100.0D * forgeLevel);
		int forgePhyd = (int) (((EquipmentTemplate) this.template).phyDefenceForgeGrow / 100.0D * forgeLevel);
		int forgeMagd = (int) (((EquipmentTemplate) this.template).magDefenceForgeGrow / 100.0D * forgeLevel);

		attr.set(7, attr.get(7) + levelHp + forgeHp);
		attr.set(6, attr.get(6) + levelAtk + forgeAtk);
		attr.set(8, attr.get(8) + levelPhyd + forgePhyd);
		attr.set(9, attr.get(9) + levelMagd + forgeMagd);
	}

	public void levelUp(int level) {
		setLevel(this.level + level);
		refreshAttr(true);
	}

	public void forge() {
		this.forgeLevel += 1;
		refreshAttr(true);
	}

	public int getPrice() {
		return this.template.price;
	}

	public void setPrice(int price) {
	}

	public boolean canPolish() {
		return ((EquipmentTemplate) this.template).canPolish;
	}

	public boolean canForge() {
		return (this.template.quality < 6);
	}

	public boolean isFullForgeLevel() {
		return (this.forgeLevel < 20);
	}

	public ForgeAttr getForgeAttr(int forgeLevel) {
		return ((EquipmentTemplate) this.template).forgeAttrs[(forgeLevel - 1)];
	}

	public Map<Integer, Integer> getPolishMaxValue() {
		EquipmentTemplate template = (EquipmentTemplate) this.template;
		PolishAttr attr = ((ItemService) Platform.getServiceManager().get(ItemService.class))
				.getPolishAttr(template.polishRuleId);
		int totalPoint = template.polishBaseValue + template.polishGrowValue * this.level / template.polishGrowLevel;
		Map result = new HashMap();
		Iterator itx = attr.attrValue.keySet().iterator();
		while (itx.hasNext()) {
			int index = ((Integer) itx.next()).intValue();
			PolishAttr.PolishAttrValue attrValue = (PolishAttr.PolishAttrValue) attr.attrValue
					.get(Integer.valueOf(index));
			int baseValue = attrValue.value;
			int actualValue = (int) Math.floor(totalPoint / baseValue);
			result.put(Integer.valueOf(attrValue.type), Integer.valueOf(actualValue));
		}
		return result;
	}

	public int getPolishAttr(int type) {
		Integer value = (Integer) this.polishAttr.get(Integer.valueOf(type));
		if (value == null) {
			value = Integer.valueOf(0);
			this.polishAttr.put(Integer.valueOf(type), value);
		}
		return value.intValue();
	}

	public Map<Integer, Integer> polish(int type, int polishCount) {
		EquipmentTemplate template = (EquipmentTemplate) this.template;
		PolishAttr attr = ((ItemService) Platform.getServiceManager().get(ItemService.class))
				.getPolishAttr(template.polishRuleId);
		PolishRandom polishRandom = null;
		if (type == 1)
			polishRandom = attr.normalRandom;
		else if (type == 2)
			polishRandom = attr.moneyRandom;
		else if (type == 3) {
			polishRandom = attr.jewelRandom;
		}
		Map result = new HashMap();
		Map map = polishRandom.getPolishChange();
		Iterator itx = attr.attrValue.keySet().iterator();
		Random random = new Random();
		while (itx.hasNext()) {
			int index = ((Integer) itx.next()).intValue();
			PolishAttr.PolishAttrValue attrValue = (PolishAttr.PolishAttrValue) attr.attrValue
					.get(Integer.valueOf(index));
			int calLimit = ((PolishRandom.RandAttr) polishRandom.randAttrs.get(Integer.valueOf(index))).calLimit;
			int calValue = random.nextInt(calLimit + 1);
			int baseValue = attrValue.value;
			int actualValue = (int) Math.floor(calValue / baseValue);
			if (!(((Boolean) map.get(Integer.valueOf(index))).booleanValue())) {
				actualValue *= -1;
			}
			result.put(Integer.valueOf(attrValue.type), Integer.valueOf(actualValue));
		}
		this.polishCount += polishCount;
		return result;
	}

	public void clear() {
		setLevel(1);
		setForgeLevel(0);
		setPolishCount(0);
		this.tmpAttr.clear();
		this.polishAttr.clear();
		refreshAttr(true);
	}

	public PbEquipment.Equipment genEquipment() {
		PbEquipment.Equipment.Builder builder = PbEquipment.Equipment.newBuilder();
		builder.setId(this.id);
		builder.setType(((EquipmentTemplate) this.template).equipType);
		builder.setTemplateId(this.template.id);
		builder.setLevel(this.level);
		builder.setBaseAttr(this.baseAttr.genAttribute());
		if (canPolish()) {
			builder.setPolishAttr(Attributes.genAttribute(this.polishAttr));
			builder.setPolishMaxAttr(Attributes.genAttribute(getPolishMaxValue()));
		}
		builder.setWarriorId(this.warriorId);
		if (this.tmpAttr.size() == 0) {
			builder.setIsPolished(false);
		} else {
			builder.setIsPolished(true);
			builder.setTmpAttr(Attributes.genAttribute(this.tmpAttr));
			builder.setTmpNum(this.tmpPolishNum);
			builder.setTmpType(this.tmpPolishType);
		}
		builder.setForgeLevel(this.forgeLevel);

		return builder.build();
	}

	@Deprecated
	public PbEquipment.EquipmentForgeSuitAttr genSuitAttr(int level) {
		PbEquipment.EquipmentForgeSuitAttr.Builder b = PbEquipment.EquipmentForgeSuitAttr.newBuilder();
		ForgeAttr attr = getForgeAttr(level);
		if (attr == null) {
			return null;
		}
		Player p = getPlayer();
		if (p == null) {
			return null;
		}
		b.setAttr(attr.getAttr().genAttribute());
		b.setLevel(level);
		return b.build();
	}

	public String getName() {
		return this.template.name;
	}

	public Attributes getBaseAttr() {
		return this.baseAttr;
	}

	public void setBaseAttr(Attributes baseAttr) {
		this.baseAttr = baseAttr;
	}

	public Map<Integer, Integer> getPolishAttr() {
		return this.polishAttr;
	}

	public void setPolishAttr(Map<Integer, Integer> polishAttr) {
		this.polishAttr = polishAttr;
	}

	public Map<Integer, Integer> getTmpAttr() {
		return this.tmpAttr;
	}

	public void setTmpAttr(Map<Integer, Integer> tmpAttr) {
		this.tmpAttr = tmpAttr;
	}

	public int getWarriorId() {
		return this.warriorId;
	}

	public void setWarriorId(int warriorId) {
		this.warriorId = warriorId;
		sync();
	}

	public boolean canSell() {
		return (getPrice() > 0);
	}

	public int getPolishCount() {
		return this.polishCount;
	}

	public void setPolishCount(int polishCount) {
		this.polishCount = polishCount;
	}

	public int getTmpPolishNum() {
		return this.tmpPolishNum;
	}

	public void setTmpPolishNum(int tmpPolishNum) {
		this.tmpPolishNum = tmpPolishNum;
	}

	public int getTmpPolishType() {
		return this.tmpPolishType;
	}

	public void setTmpPolishType(int tmpPolishType) {
		this.tmpPolishType = tmpPolishType;
	}

	public int getForgeLevel() {
		return this.forgeLevel;
	}

	public void setForgeLevel(int forgeLevel) {
		this.forgeLevel = forgeLevel;
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}
}
