package org.darcy.sanguo.service.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.hero.HeroTemplate;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Debris;
import org.darcy.sanguo.item.DebrisDropPath;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.EquipmentTemplate;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.item.NormalItem;
import org.darcy.sanguo.item.NormalItemTemplate;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.item.TreasureTemplate;
import org.darcy.sanguo.item.equip.ForgeAttr;
import org.darcy.sanguo.item.equip.PolishAttr;
import org.darcy.sanguo.item.equip.PolishRandom;
import org.darcy.sanguo.item.equip.Suit;
import org.darcy.sanguo.item.itemeffect.ItemEffect;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.talent.Talent;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import net.sf.json.JSONArray;
import sango.packet.PbDown;
import sango.packet.PbItem;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class ItemService implements Service, PacketHandler {
	public static final double REFINE_FACTOR = 1.0D;
	public HashMap<Integer, ItemEffect> itemEffects = new HashMap();
	public static HashMap<Integer, ItemTemplate> templates = new HashMap();

	public Map<Integer, PolishRandom> polishRandoms = new HashMap();
	public Map<Integer, PolishAttr> polishAttrs = new HashMap();

	public Map<Integer, Suit> suits = new HashMap();

	public static Map<Integer, List<DebrisDropPath>> debrisPaths = new HashMap();

	public int[] getCodes() {
		return new int[] { 1051, 1047, 1049, 1053, 1055, 1063, 1065, 1067, 1069, 1071, 1073, 1075, 1077, 1103, 1109,
				1111, 1139, 1205, 1339, 1341 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 1051:
			equipmentIntensify(player, packet);
			break;
		case 1047:
			bagSell(player, packet);
			break;
		case 1049:
			bagExtend(player, packet);
			break;
		case 1053:
			equipmentPolish(player, packet);
			break;
		case 1055:
			equipmentPolishBesure(player, packet);
			break;
		case 1063:
			treasureIntensify(player, packet);
			break;
		case 1065:
			equipmentRefine(player, packet);
			break;
		case 1067:
			equipmentReborn(player, packet);
			break;
		case 1069:
			treasureEnhance(player, packet);
			break;
		case 1071:
			treasureRefine(player, packet);
			break;
		case 1073:
			treasureReborn(player, packet);
			break;
		case 1075:
			equipmentCompound(player, packet);
			break;
		case 1077:
			treasureCompound(player, packet);
			break;
		case 1103:
			equipmentNextLevelInfo(player, packet);
			break;
		case 1109:
			treasureIntensifyInfo(player, packet);
			break;
		case 1111:
			equipmentPolishCost(player, packet);
			break;
		case 1139:
			bagExtendCost(player, packet);
			break;
		case 1205:
			debrisDropPath(player, packet);
			break;
		case 1339:
			equipmentNextForgeInfo(player, packet);
			break;
		case 1341:
			equipmentForge(player, packet);
		}
	}

	public static ItemTemplate getItemTemplate(int templateId) {
		return ((ItemTemplate) templates.get(Integer.valueOf(templateId)));
	}

	public static Item generateItem(int templateId, Player player) {
		return generateItem(getItemTemplate(templateId), player);
	}

	public static Item generateItem(ItemTemplate tplt, Player player) {
		Item item = null;
		if (tplt.type == 2) {
			item = new Warrior(tplt, player.getBags().getNewItemId());
			((Warrior) item).init(player);
		} else if (tplt.type == 3) {
			item = new Debris(tplt);
		} else if (tplt.type == 4) {
			item = new Equipment(tplt, player.getBags().getNewItemId());
			((Equipment) item).init(player);
		} else if (tplt.type == 0) {
			item = new NormalItem(tplt);
		} else if (tplt.type == 1) {
			item = new Treasure(tplt, player.getBags().getNewItemId());
			((Treasure) item).init(player);
		} else {
			throw new RuntimeException("unknow type");
		}
		return item;
	}

	public PolishRandom getPolishRandom(int id) {
		return ((PolishRandom) this.polishRandoms.get(Integer.valueOf(id)));
	}

	public PolishAttr getPolishAttr(int id) {
		return ((PolishAttr) this.polishAttrs.get(Integer.valueOf(id)));
	}

	public Suit getSuit(int id) {
		return ((Suit) this.suits.get(Integer.valueOf(id)));
	}

	private void loadItemEffects() {
		List<Row> list = ExcelUtils.getRowList("itemeffect.xls", 2);
		for (Row row : list) {
			int pos = 0;
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String description = row.getCell(pos++).getStringCellValue();
			String className = row.getCell(pos++).getStringCellValue();
			int count = (int) row.getCell(pos++).getNumericCellValue();
			try {
				Class effect = Class.forName("org.darcy.sanguo.item.itemeffect." + className);
				Constructor constructor = effect.getConstructor(new Class[] { Integer.TYPE });
				Object obj = constructor.newInstance(new Object[] { Integer.valueOf(count) });
				this.itemEffects.put(Integer.valueOf(id), (ItemEffect) obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void loadHeros() {
		List<Row> list = ExcelUtils.getRowList("wujd.xls");
		for (Row row : list) {

			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String desc = row.getCell(pos++).getStringCellValue();
			int gender = (int) row.getCell(pos++).getNumericCellValue();
			int camp = (int) row.getCell(pos++).getNumericCellValue();
			int isMonster = (int) row.getCell(pos++).getNumericCellValue();
			int quality = (int) row.getCell(pos++).getNumericCellValue();
			int iconId = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int shapeId = (int) row.getCell(pos++).getNumericCellValue();
			int ptSkillId = (int) row.getCell(pos++).getNumericCellValue();
			int angrySkillId = (int) row.getCell(pos++).getNumericCellValue();
			int fjSkillId = (int) row.getCell(pos++).getNumericCellValue();
			int charmSkillId = (int) row.getCell(pos++).getNumericCellValue();
			int messSkillId = (int) row.getCell(pos++).getNumericCellValue();
			int initFury = (int) row.getCell(pos++).getNumericCellValue();
			int attackType = (int) row.getCell(pos++).getNumericCellValue();
			int leve = (int) row.getCell(pos++).getNumericCellValue();
			int aptitude = (int) row.getCell(pos++).getNumericCellValue();
			int honor = (int) row.getCell(pos++).getNumericCellValue();
			int force = (int) row.getCell(pos++).getNumericCellValue();
			int intelligence = (int) row.getCell(pos++).getNumericCellValue();
			int phyAttack = (int) row.getCell(pos++).getNumericCellValue();
			int magAttack = (int) row.getCell(pos++).getNumericCellValue();
			int attack = (int) row.getCell(pos++).getNumericCellValue();
			int hp = (int) row.getCell(pos++).getNumericCellValue();
			int phyDefence = (int) row.getCell(pos++).getNumericCellValue();
			int magDefence = (int) row.getCell(pos++).getNumericCellValue();
			int critRate = (int) row.getCell(pos++).getNumericCellValue();
			int critTimes = (int) row.getCell(pos++).getNumericCellValue();
			int unCritRate = (int) row.getCell(pos++).getNumericCellValue();
			int dodgeRate = (int) row.getCell(pos++).getNumericCellValue();
			int hitRate = (int) row.getCell(pos++).getNumericCellValue();
			int blockRate = (int) row.getCell(pos++).getNumericCellValue();
			int unBlockRate = (int) row.getCell(pos++).getNumericCellValue();
			int finalDamage = (int) row.getCell(pos++).getNumericCellValue();
			int finalDamageDec = (int) row.getCell(pos++).getNumericCellValue();
			int cure = (int) row.getCell(pos++).getNumericCellValue();
			int cured = (int) row.getCell(pos++).getNumericCellValue();
			int poisonDamage = (int) row.getCell(pos++).getNumericCellValue();
			int poisonDamageDec = (int) row.getCell(pos++).getNumericCellValue();
			int fireDamage = (int) row.getCell(pos++).getNumericCellValue();
			int fireDamageDec = (int) row.getCell(pos++).getNumericCellValue();
			int phyDamageDec = (int) row.getCell(pos++).getNumericCellValue();
			int magDamageDec = (int) row.getCell(pos++).getNumericCellValue();
			int beCuredRate = (int) row.getCell(pos++).getNumericCellValue();
			int intensifyRule = (int) row.getCell(pos++).getNumericCellValue();
			int hpGrow = (int) row.getCell(pos++).getNumericCellValue();
			int attackGrow = (int) row.getCell(pos++).getNumericCellValue();
			int phyDefenceGrow = (int) row.getCell(pos++).getNumericCellValue();
			int magDefenceGrow = (int) row.getCell(pos++).getNumericCellValue();
			String advanceRule = row.getCell(pos++).getStringCellValue();

			row.getCell(pos).setCellType(1);
			row.getCell(pos).setCellType(1);
			String heroTalents = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String fetterInfo = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String fetterInfoB = row.getCell(pos++).getStringCellValue();
			int breakable = (int) row.getCell(pos++).getNumericCellValue();
			int initExp = (int) row.getCell(pos++).getNumericCellValue();
			int breakSpiritJade = (int) row.getCell(pos++).getNumericCellValue();
			int initRebornCost = (int) row.getCell(pos++).getNumericCellValue();
			int basePrice = (int) row.getCell(pos++).getNumericCellValue();

			HeroTemplate template = new HeroTemplate(id, name);
			template.id = id;
			template.name = name;
			template.desc = desc;
			template.gender = gender;
			template.camp = camp;
			template.isMonster = (isMonster == 1);
			template.quality = quality;
			template.iconId = iconId;
			template.shapeId = shapeId;
			template.ptSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
					.getSkill(ptSkillId);
			template.angrySkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
					.getSkill(angrySkillId);
			template.fjSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
					.getSkill(fjSkillId);
			template.charmSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
					.getSkill(charmSkillId);
			template.messSkill = ((CombatService) Platform.getServiceManager().get(CombatService.class))
					.getSkill(messSkillId);
			template.attr.set(0, initFury);
			template.atkType = attackType;
			template.enIds = Calc.split(fetterInfo, ",");
			if (!(fetterInfoB.equals("-1"))) {
				template.enIdsB = Calc.split(fetterInfoB, ",");
			}
			template.level = leve;
			template.aptitude = aptitude;
			template.attr.set(1, honor);
			template.attr.set(2, force);
			template.attr.set(3, intelligence);
			template.attr.set(4, phyAttack);
			template.attr.set(5, magAttack);
			template.attr.set(6, attack);
			template.attr.set(7, hp);
			template.attr.set(8, phyDefence);
			template.attr.set(9, magDefence);
			template.attr.set(10, critRate);
			template.attr.set(11, critTimes);
			template.attr.set(12, unCritRate);
			template.attr.set(13, dodgeRate);
			template.attr.set(14, hitRate);
			template.attr.set(15, blockRate);
			template.attr.set(16, unBlockRate);
			template.attr.set(17, finalDamage);
			template.attr.set(18, finalDamageDec);
			template.attr.set(19, cure);
			template.attr.set(20, cured);
			template.attr.set(21, poisonDamage);
			template.attr.set(22, poisonDamageDec);
			template.attr.set(23, fireDamage);
			template.attr.set(24, fireDamageDec);
			template.attr.set(25, phyDamageDec);
			template.attr.set(26, magDamageDec);
			template.attr.set(27, beCuredRate);
			template.intensifyRule = intensifyRule;
			template.hpGrow = hpGrow;
			template.attackGrow = attackGrow;
			template.phyDefenceGrow = phyDefenceGrow;
			template.magDefenceGrow = magDefenceGrow;
			if (!(advanceRule.equals("-1"))) {
				String[] advanceRules = advanceRule.split(",");
				for (String str : advanceRules) {
					int[] tmpRuleInfo = Calc.split(str, "\\|");
					template.advanceRule.put(Integer.valueOf(tmpRuleInfo[0]), Integer.valueOf(tmpRuleInfo[1]));
				}
			}

			template.initExp = initExp;
			template.breakSpiritJade = breakSpiritJade;
			template.price = basePrice;
			template.canBreak = (breakable == 1);
			template.initRebornCostJewel = initRebornCost;
			if (!(heroTalents.equals("-1"))) {
				String[] tlts = heroTalents.split(",");
				Talent[] talents = new Talent[tlts.length];
				for (int j = 0; j < tlts.length; ++j) {
					talents[j] = new Talent(tlts[j]);
				}
				template.talents = talents;
			}
			templates.put(Integer.valueOf(id), template);
		}
	}

	private void loadDebris() {
		List<Row> list = ExcelUtils.getRowList("chip.xls");
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String desc = row.getCell(pos++).getStringCellValue();
			int iconId = (int) row.getCell(pos++).getNumericCellValue();
			int itemType = (int) row.getCell(pos++).getNumericCellValue();
			int chipType = (int) row.getCell(pos++).getNumericCellValue();
			int quality = (int) row.getCell(pos++).getNumericCellValue();
			int price = (int) row.getCell(pos++).getNumericCellValue();
			int maxStack = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String reward = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String costs = row.getCell(pos++).getStringCellValue();
			int lootDropNpc = (int) row.getCell(pos++).getNumericCellValue();
			int lootDropPlayer = (int) row.getCell(pos++).getNumericCellValue();

			DebrisTemplate template = new DebrisTemplate(id, name);
			template.desc = desc;
			template.iconId = iconId;
			template.quality = quality;
			template.price = price;
			template.maxStack = maxStack;
			template.debrisType = chipType;
			if ((reward != null) && (!(reward.equals("-1")))) {
				template.reward = reward;
			}
			if ((costs != null) && (!(costs.equals("-1")))) {
				String[] array = costs.split(",");
				for (String str : array) {
					template.costs.add(str);
				}
			}
			template.lootDropNpc = lootDropNpc;
			template.lootDropPlayer = lootDropPlayer;
			templates.put(Integer.valueOf(id), template);
		}
	}

	private void loadNormalItems() {
		List<Row> list = ExcelUtils.getRowList("item.xls");
		for (Row row : list) {

			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String desc = row.getCell(pos++).getStringCellValue();
			++pos;
			int userLevel = (int) row.getCell(pos++).getNumericCellValue();
			int vipLevel = (int) row.getCell(pos++).getNumericCellValue();
			int type = (int) row.getCell(pos++).getNumericCellValue();
			int iconId = (int) row.getCell(pos++).getNumericCellValue();
			int quality = (int) row.getCell(pos++).getNumericCellValue();
			int maxCount = (int) row.getCell(pos++).getNumericCellValue();
			int canUse = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String needItems = row.getCell(pos++).getStringCellValue();
			++pos;
			int price = (int) row.getCell(pos++).getNumericCellValue();
			int effectId = (int) row.getCell(pos++).getNumericCellValue();

			NormalItemTemplate template = new NormalItemTemplate(id, name);
			template.desc = desc;
			template.userLevel = userLevel;
			template.vipLevel = vipLevel;
			template.normalItemType = type;
			template.iconId = iconId;
			template.quality = quality;
			template.maxCount = maxCount;
			template.canUse = (canUse == 1);
			if ((needItems != null) && (!(needItems.equals("-1")))) {
				String[] needArray = needItems.split(",");
				for (String str : needArray) {
					template.needItems.add(str);
				}
			}
			template.price = price;

			ItemEffect effect = (ItemEffect) this.itemEffects.get(Integer.valueOf(effectId));
			if (effect != null) {
				effect = effect.copy();
				int count = effect.getParamCount();
				String[] params = new String[count];
				for (int k = 0; k < count; ++k) {
					row.getCell(pos).setCellType(1);
					params[k] = row.getCell(pos++).getStringCellValue();
				}
				effect.initParams(params);
				template.effect = effect;
			}
			templates.put(Integer.valueOf(id), template);
		}
	}

	private void loadEquipments() {
		List<Row> list = ExcelUtils.getRowList("equipment.xls");
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			row.getCell(pos).setCellType(1);
			String desc = row.getCell(pos++).getStringCellValue();

			++pos;
			++pos;
			int quality = (int) row.getCell(pos++).getNumericCellValue();
			int aptitude = (int) row.getCell(pos++).getNumericCellValue();
			int price = (int) row.getCell(pos++).getNumericCellValue();
			int type = (int) row.getCell(pos++).getNumericCellValue();
			int suitId = (int) row.getCell(pos++).getNumericCellValue();
			int baseHp = (int) row.getCell(pos++).getNumericCellValue();
			int baseAtk = (int) row.getCell(pos++).getNumericCellValue();
			int basePhyDefence = (int) row.getCell(pos++).getNumericCellValue();
			int baseMagDefence = (int) row.getCell(pos++).getNumericCellValue();
			int hpGrow = (int) row.getCell(pos++).getNumericCellValue();
			int attackGrow = (int) row.getCell(pos++).getNumericCellValue();
			int phyDefenceGrow = (int) row.getCell(pos++).getNumericCellValue();
			int magDefenceGrow = (int) row.getCell(pos++).getNumericCellValue();
			int intensifyRule = (int) row.getCell(pos++).getNumericCellValue();
			int intensifyLimitRatio = (int) row.getCell(pos++).getNumericCellValue();
			int canPolish = (int) row.getCell(pos++).getNumericCellValue();
			int polishRule = (int) row.getCell(pos++).getNumericCellValue();
			int basePolishValue = (int) row.getCell(pos++).getNumericCellValue();
			int polishGrowValue = (int) row.getCell(pos++).getNumericCellValue();
			int polishGrowLevel = (int) row.getCell(pos++).getNumericCellValue();
			int basePolishItemCount = (int) row.getCell(pos++).getNumericCellValue();
			int refineBackForgeItemCount = (int) row.getCell(pos++).getNumericCellValue();
			int hpForgeGrow = (int) row.getCell(pos++).getNumericCellValue();
			int attackForgeGrow = (int) row.getCell(pos++).getNumericCellValue();
			int phyDefenceForgeGrow = (int) row.getCell(pos++).getNumericCellValue();
			int magDefenceForgeGrow = (int) row.getCell(pos++).getNumericCellValue();
			int baseRebornCost = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String forgeAttrs = row.getCell(pos++).getStringCellValue();

			EquipmentTemplate template = new EquipmentTemplate(id, name);
			template.desc = desc;
			template.quality = quality;
			template.aptitude = aptitude;
			template.price = price;
			template.equipType = type;
			template.suitId = suitId;
			template.attr.set(7, baseHp);
			template.attr.set(6, baseAtk);
			template.attr.set(8, basePhyDefence);
			template.attr.set(9, baseMagDefence);
			template.hpGrow = hpGrow;
			template.attackGrow = attackGrow;
			template.phyDefenceGrow = phyDefenceGrow;
			template.magDefenceGrow = magDefenceGrow;
			template.intensifyRule = intensifyRule;
			template.intensifyLimitRatio = intensifyLimitRatio;

			template.canPolish = (canPolish == 1);
			template.polishRuleId = polishRule;
			template.polishBaseValue = basePolishValue;
			template.polishGrowValue = polishGrowValue;
			template.polishGrowLevel = polishGrowLevel;
			template.basePolishItemCount = basePolishItemCount;
			template.refineBackForgeItemCount = refineBackForgeItemCount;

			template.hpForgeGrow = hpForgeGrow;
			template.attackForgeGrow = attackForgeGrow;
			template.phyDefenceForgeGrow = phyDefenceForgeGrow;
			template.magDefenceForgeGrow = magDefenceForgeGrow;

			template.baseRebornCost = baseRebornCost;
			if ((forgeAttrs != null) && (!(forgeAttrs.equals("-1")))) {
				String[] array = forgeAttrs.split(",");
				template.forgeAttrs = new ForgeAttr[array.length];
				for (int j = 0; j < array.length; ++j) {
					String[] strArray = array[j].split("\\|");
					ForgeAttr attr = new ForgeAttr();
					attr.type = Integer.valueOf(strArray[0]).intValue();
					attr.value = Integer.valueOf(strArray[1]).intValue();
					template.forgeAttrs[j] = attr;
				}
			}

			templates.put(Integer.valueOf(id), template);
		}
	}

	private void loadSuit() {
		List<Row> list = ExcelUtils.getRowList("suit.xls");
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			int totalNum = (int) row.getCell(pos++).getNumericCellValue();
			String equipIds = row.getCell(pos++).getStringCellValue();
			Suit suit = new Suit();
			suit.id = id;
			suit.name = name;
			String[] equipIdArray = equipIds.split(",");
			suit.equipIds = new int[equipIdArray.length];
			for (int j = 0; j < suit.equipIds.length; ++j) {
				suit.equipIds[j] = Integer.valueOf(equipIdArray[j]).intValue();
			}
			int maxLock = (int) row.getCell(pos++).getNumericCellValue();
			for (int j = 0; j < maxLock; ++j) {
				int num = (int) row.getCell(pos++).getNumericCellValue();
				String attrInfo = row.getCell(pos++).getStringCellValue();
				String[] attrs = attrInfo.split(",");
				Map map = new HashMap();
				for (int k = 0; k < attrs.length; ++k) {
					String[] attr = attrs[k].split("\\|");
					map.put(Integer.valueOf(attr[0]), Integer.valueOf(attr[1]));
				}
				suit.attrs.put(Integer.valueOf(num), map);
			}

			this.suits.put(Integer.valueOf(id), suit);
		}
	}

	private void loadPolishRandom() {
		List<Row> list = ExcelUtils.getRowList("randattr.xls", 2);
		for (Row row : list) {
			int pos;
			int i, j, id;

			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			PolishRandom random = new PolishRandom();
			random.id = id;
			PolishRandom.RandAttr randAttr = random.new RandAttr();
			randAttr.addRatio = (int) row.getCell(pos++).getNumericCellValue();
			randAttr.calLimit = (int) row.getCell(pos++).getNumericCellValue();
			random.randAttrs.put(Integer.valueOf(1), randAttr);

			randAttr = random.new RandAttr();
			randAttr.addRatio = (int) row.getCell(pos++).getNumericCellValue();
			randAttr.calLimit = (int) row.getCell(pos++).getNumericCellValue();
			random.randAttrs.put(Integer.valueOf(2), randAttr);

			randAttr = random.new RandAttr();
			randAttr.addRatio = (int) row.getCell(pos++).getNumericCellValue();
			randAttr.calLimit = (int) row.getCell(pos++).getNumericCellValue();
			random.randAttrs.put(Integer.valueOf(3), randAttr);

			randAttr = random.new RandAttr();
			randAttr.addRatio = (int) row.getCell(pos++).getNumericCellValue();
			randAttr.calLimit = (int) row.getCell(pos++).getNumericCellValue();
			random.randAttrs.put(Integer.valueOf(4), randAttr);

			this.polishRandoms.put(Integer.valueOf(id), random);
		}

		List<Row> list2 = ExcelUtils.getRowList("randattr.xls", 2, 1);
		for (Row row : list2) {

			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			PolishAttr attr = new PolishAttr();
			attr.id = id;
			String normalCost = row.getCell(pos++).getStringCellValue();
			String[] costArray = normalCost.split(",");
			for (String str : costArray) {
				attr.normalCost.add(new Reward(str));
			}
			int normalRandomId = (int) row.getCell(pos++).getNumericCellValue();
			attr.normalRandom = getPolishRandom(normalRandomId);
			String moneyCost = row.getCell(pos++).getStringCellValue();
			costArray = moneyCost.split(",");
			for (String str : costArray) {
				attr.moneyCost.add(new Reward(str));
			}
			int moneyRandomId = (int) row.getCell(pos++).getNumericCellValue();
			attr.moneyRandom = getPolishRandom(moneyRandomId);
			String jewelCost = row.getCell(pos++).getStringCellValue();
			costArray = jewelCost.split(",");
			for (String str : costArray) {
				attr.jewelCost.add(new Reward(str));
			}
			int jewelRandomId = (int) row.getCell(pos++).getNumericCellValue();
			attr.jewelRandom = getPolishRandom(jewelRandomId);

			int attrNum = (int) row.getCell(pos++).getNumericCellValue();
			for (int j = 0; j < attrNum; ++j) {
				PolishAttr tmp837_835 = attr;
				tmp837_835.getClass();
				PolishAttr.PolishAttrValue value = new PolishAttr().new PolishAttrValue();
				value.type = (int) row.getCell(pos++).getNumericCellValue();
				value.value = (int) row.getCell(pos++).getNumericCellValue();

				attr.attrValue.put(Integer.valueOf(j + 1), value);
			}

			this.polishAttrs.put(Integer.valueOf(id), attr);
		}
	}

	private void loadTreasures() {
		List<Row> list = ExcelUtils.getRowList("treasure.xls");
		for (Row row : list) {

			String[] arrayOfString2;
			int pos = 0;

			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();

			++pos;
			++pos;
			++pos;
			int quality = (int) row.getCell(pos++).getNumericCellValue();
			int price = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int treasureType = (int) row.getCell(pos++).getNumericCellValue();
			TreasureTemplate template = new TreasureTemplate(id, name);
			template.quality = quality;
			template.price = price;
			template.treasureType = treasureType;
			row.getCell(pos).setCellType(1);
			String baseAttr1 = row.getCell(pos++).getStringCellValue();
			if ((baseAttr1 != null) && (!(baseAttr1.equals("-1")))) {
				String[] tmp = baseAttr1.split("\\|");
				template.baseAttr.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			row.getCell(pos).setCellType(1);
			String growAttr1 = row.getCell(pos++).getStringCellValue();
			if ((growAttr1 != null) && (!(growAttr1.equals("-1")))) {
				String[] tmp = growAttr1.split("\\|");
				template.growAttr.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			row.getCell(pos).setCellType(1);
			String baseAttr2 = row.getCell(pos++).getStringCellValue();
			if ((baseAttr2 != null) && (!(baseAttr2.equals("-1")))) {
				String[] tmp = baseAttr2.split("\\|");
				template.baseAttr.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			row.getCell(pos).setCellType(1);
			String growAttr2 = row.getCell(pos++).getStringCellValue();
			if ((growAttr2 != null) && (!(growAttr2.equals("-1")))) {
				String[] tmp = growAttr2.split("\\|");
				template.growAttr.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			row.getCell(pos).setCellType(1);
			String baseAttr3 = row.getCell(pos++).getStringCellValue();
			if ((baseAttr3 != null) && (!(baseAttr3.equals("-1")))) {
				String[] tmp = baseAttr3.split("\\|");
				template.baseAttr.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			row.getCell(pos).setCellType(1);
			String growAttr3 = row.getCell(pos++).getStringCellValue();
			if ((growAttr3 != null) && (!(growAttr3.equals("-1")))) {
				String[] tmp = growAttr3.split("\\|");
				template.growAttr.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			row.getCell(pos).setCellType(1);
			String extraAttr = row.getCell(pos++).getStringCellValue();
			if ((extraAttr != null) && (!(extraAttr.equals("-1")))) {
				String[] tmp = extraAttr.split(",");
				for (String str : tmp) {
					String[] tmp2 = str.split("\\|");
					int level = Integer.valueOf(tmp2[0]).intValue();
					Map extra = (Map) template.extraAttr.get(Integer.valueOf(level));
					if (extra == null) {
						extra = new HashMap();
						template.extraAttr.put(Integer.valueOf(level), extra);
					}
					extra.put(Integer.valueOf(tmp2[1]), Integer.valueOf(tmp2[2]));
				}
			}
			int baseExp = (int) row.getCell(pos++).getNumericCellValue();
			int intensifyCostMoneyRatio = (int) row.getCell(pos++).getNumericCellValue();
			int intensifyRule = (int) row.getCell(pos++).getNumericCellValue();
			int maxLevel = (int) row.getCell(pos++).getNumericCellValue();
			template.baseExp = baseExp;
			template.intensifyCostMoneyRatio = intensifyCostMoneyRatio;
			template.intensifyRule = intensifyRule;
			template.maxLevel = maxLevel;
			String fragments = row.getCell(pos++).getStringCellValue();
			String[] idStr = fragments.split(",");
			for (int j = 0; j < idStr.length; ++j) {
				String str = idStr[j];
				template.debris.add(Integer.valueOf(str));
			}
			int grade = (int) row.getCell(pos++).getNumericCellValue();
			int canEnhance = (int) row.getCell(pos++).getNumericCellValue();
			template.grade = grade;
			template.canEnhance = (canEnhance == 1);
			if (template.canEnhance) {
				String enhanceAttr = row.getCell(pos++).getStringCellValue();
				if ((enhanceAttr != null) && (!(enhanceAttr.equals("-1")))) {
					String[] tmp = enhanceAttr.split(",");
					for (String str : tmp) {
						String[] tmp2 = str.split("\\|");
						template.enhanceAttr.put(Integer.valueOf(tmp2[0]), Integer.valueOf(tmp2[1]));
					}
				}
				int maxEnhanceLevel = (int) row.getCell(pos++).getNumericCellValue();
				template.maxEnhanceLevel = maxEnhanceLevel;
				int costNum = (int) row.getCell(pos++).getNumericCellValue();
				String costMoney = row.getCell(pos++).getStringCellValue();
				if ((costMoney != null) && (!(costMoney.equals("-1")))) {
					String[] tmp = costMoney.split(",");
					template.enhanceMoney = new int[tmp.length];
					for (int j = 0; j < tmp.length; ++j) {
						template.enhanceMoney[j] = Integer.valueOf(tmp[j]).intValue();
					}
				}
				for (int j = 0; j < costNum; ++j) {
					String costItem = row.getCell(pos++).getStringCellValue();
					if ((costItem != null) && (!(costItem.equals("-1")))) {
						String[] tmp = costItem.split(",");
						for (int k = 0; k < tmp.length; ++k) {
							List list2 = (List) template.enhanceCost.get(Integer.valueOf(k + 1));
							if (list2 == null) {
								list2 = new ArrayList();
								template.enhanceCost.put(Integer.valueOf(k + 1), list2);
							}
							list2.add(tmp[k]);
						}
					}
				}
			}
			templates.put(Integer.valueOf(id), template);
		}
	}

	private void bagSell(Player player, PbPacket.Packet packet) {
		List<PbItem.IdentifyInfo> list;
		int i;
		PbDown.BagSellRst.Builder builder = PbDown.BagSellRst.newBuilder();
		try {
			PbUp.BagSell bagSell = PbUp.BagSell.parseFrom(packet.getData());
			list = bagSell.getIdsList();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("出售物品不存在");
			player.send(1048, builder.build());
			return;
		}
		Map map = new HashMap();
		for (PbItem.IdentifyInfo info : list) {
			int itemId = info.getItemId();
			int templateId = info.getTemplateId();
			Item item = null;
			int count = 1;
			if (itemId == 0) {
				List thisItem = player.getBags().getItemByTemplateId(templateId);
				if ((thisItem != null) && (thisItem.size() > 0)) {
					item = (Item) thisItem.get(0);
					count = player.getBags().getItemCount(templateId);
				}
			} else {
				ItemTemplate template = getItemTemplate(templateId);
				item = player.getBags().getItemById(itemId, template.type);
				count = 1;
			}
			if (item == null) {
				builder.setResult(false);
				builder.setErrInfo("出售物品不存在");
				player.send(1048, builder.build());
				return;
			}
			if (!(item.canSell())) {
				builder.setResult(false);
				builder.setErrInfo("该物品不可出售");
				player.send(1048, builder.build());
				return;
			}
			map.put(item, Integer.valueOf(count));
		}
		Iterator itx = map.keySet().iterator();
		i = 0;
		while (itx.hasNext()) {
			Item item = (Item) itx.next();
			int count = ((Integer) map.get(item)).intValue();
			i += item.getPrice() * count;
			player.getBags().removeItem(item.getId(), item.getTemplateId(), count, "sell");
		}
		player.addMoney(i, "sell");

		builder.setResult(true);
		player.send(1048, builder.build());
	}

	private void bagExtendCost(Player player, PbPacket.Packet packet) {
		PbDown.BagExtendCostRst.Builder builder = PbDown.BagExtendCostRst.newBuilder();
		int bagType = 0;
		try {
			PbUp.BagExtendCost bagExtend = PbUp.BagExtendCost.parseFrom(packet.getData());
			bagType = bagExtend.getBagType();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("背包扩展失败");
			player.send(1140, builder.build());
			return;
		}
		int size = player.getBags().getSize(bagType);
		if ((size > 0) && (size >= player.getBags().getMaxSize(bagType))) {
			builder.setResult(false);
			builder.setErrInfo("背包格数已达上限，无法继续购买");
			player.send(1140, builder.build());
			return;
		}

		String str = player.getPool().getString(1);
		if (str == null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 7; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(i).append("_").append(0);
			}
			str = sb.toString();
			player.getPool().set(1, str);
		}
		int extendTime = 0;
		String[] array = str.split(",");
		for (int i = 0; i < array.length; ++i) {
			String newStr = array[i];
			if (bagType == Integer.parseInt(newStr.split("_")[0])) {
				extendTime = Integer.parseInt(newStr.split("_")[1]);
				break;
			}
		}
		int costJewel = 25 * (extendTime + 1);
		builder.setJewel(costJewel);
		builder.setBagType(bagType);
		builder.setSize(5);
		builder.setResult(true);
		player.send(1140, builder.build());
	}

	private void bagExtend(Player player, PbPacket.Packet packet) {
		PbDown.BagExtendRst.Builder builder = PbDown.BagExtendRst.newBuilder();
		int bagType = 0;
		try {
			PbUp.BagExtend bagExtend = PbUp.BagExtend.parseFrom(packet.getData());
			bagType = bagExtend.getBagType();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("背包扩展失败");
			player.send(1050, builder.build());
			return;
		}
		int size = player.getBags().getSize(bagType);
		if (size == -1) {
			builder.setResult(false);
			builder.setErrInfo("该背包无需扩展");
		} else if (size >= player.getBags().getMaxSize(bagType)) {
			builder.setResult(false);
			builder.setErrInfo("背包格数已达上限，无法继续购买");
		} else {
			String str = player.getPool().getString(1);
			if (str == null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < 7; ++i) {
					if (i != 0) {
						sb.append(",");
					}
					sb.append(i).append("_").append(0);
				}
				str = sb.toString();
			}
			int extendTime = 0;
			StringBuilder sb = new StringBuilder();
			String[] array = str.split(",");
			for (int i = 0; i < array.length; ++i) {
				String newStr = array[i];
				if (i != 0) {
					sb.append(",");
				}
				if (bagType == Integer.parseInt(newStr.split("_")[0])) {
					extendTime = Integer.parseInt(newStr.split("_")[1]);
					sb.append(newStr.split("_")[0]).append("_").append(extendTime + 1);
				} else {
					sb.append(newStr);
				}
			}
			int costJewel = 25 * (extendTime + 1);
			if (player.getJewels() < costJewel) {
				builder.setResult(false);
				builder.setErrInfo("元宝不足");
			} else {
				player.decJewels(costJewel, "extend");
				player.getBags().getBag(bagType).addBagSize(5);
				player.getPool().set(1, sb.toString());
				builder.setJewel(costJewel);
				builder.setSize(5);
				builder.setResult(true);
			}
		}
		player.send(1050, builder.build());
	}

	private void equipmentIntensify(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 40))) {
			return;
		}
		PbDown.EquipmentIntensifyRst.Builder builder = PbDown.EquipmentIntensifyRst.newBuilder();
		builder.setResult(true);
		int equipId = 0;
		int num = 0;
		try {
			PbUp.EquipmentIntensify intensify = PbUp.EquipmentIntensify.parseFrom(packet.getData());
			equipId = intensify.getEquipId();
			num = intensify.getNum();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("装备强化失败");
			player.send(1052, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else {
			int count = 0;
			for (int i = 0; i < num; ++i) {
				int needExp = 0;
				if (equip.getLevel() >= equip.getMaxLevel(player.getLevel())) {
					if (i != 0)
						break;
					builder.setResult(false);
					builder.setErrInfo("装备已达最大等级");
					player.send(1052, builder.build());
					return;
				}

				int intensifyRule = ((EquipmentTemplate) equip.template).intensifyRule;
				needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(intensifyRule,
						equip.getLevel() + 1);
				if (player.getMoney() < needExp) {
					if (i != 0)
						break;
					builder.setResult(false);
					builder.setErrInfo("银币不足");
					player.send(1052, builder.build());
					return;
				}

				if (builder.getResult()) {
					int crit = 1;
					equip.levelUp(crit);
					++count;
					player.decMoney(needExp, "equipintensify");
					builder.setResult(true);
					builder.setEquip(equip.genEquipment());
				}
			}
			if (builder.getResult()) {
				if (equip.getWarriorId() > 0) {
					Warrior warrior = player.getWarriors().getWarriorById(equip.getWarriorId());
					if (warrior == null)
						equip.setWarriorId(0);
					else {
						warrior.refreshAttributes(true);
					}
				}
				Platform.getLog().logEquipment(player, equip, "equipintensify", count);
			}
			Platform.getEventManager().addEvent(new Event(2024, new Object[] { player }));
		}
		player.send(1052, builder.build());
	}

	private void equipmentPolishCost(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 19))) {
			return;
		}
		PbDown.EquipmentPolishCostRst.Builder builder = PbDown.EquipmentPolishCostRst.newBuilder().setResult(true);
		int equipId = 0;
		try {
			PbUp.EquipmentPolishCost cost = PbUp.EquipmentPolishCost.parseFrom(packet.getData());
			equipId = cost.getEquipId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("请求装备洗炼消耗失败");
			player.send(1112, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else if (!(equip.canPolish())) {
			builder.setResult(false);
			builder.setErrInfo("紫色品质及以上的装备才可进行洗炼");
		} else {
			EquipmentTemplate template = (EquipmentTemplate) equip.getTemplate();
			PolishAttr attr = getPolishAttr(template.polishRuleId);
			builder.setResult(true);
			builder.addCosts(attr.genEquipmentPolishCostInfo(1));
			builder.addCosts(attr.genEquipmentPolishCostInfo(2));
			builder.addCosts(attr.genEquipmentPolishCostInfo(3));
		}
		player.send(1112, builder.build());
	}

	private void equipmentPolish(Player player, PbPacket.Packet packet) {
		Reward tmpReward;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 19))) {
			return;
		}
		PbDown.EquipmentPolishRst.Builder builder = PbDown.EquipmentPolishRst.newBuilder();
		builder.setResult(true);
		int equipId = 0;
		int type = 0;
		int times = 0;
		try {
			PbUp.EquipmentPolish polish = PbUp.EquipmentPolish.parseFrom(packet.getData());
			equipId = polish.getEquipId();
			type = polish.getType();
			times = polish.getTimes();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("装备洗炼失败");
			player.send(1054, builder.build());
			return;
		}
		List<Reward> actualCosts = new ArrayList();
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else if (!(equip.canPolish())) {
			builder.setResult(false);
			builder.setErrInfo("紫色品质及以上的装备才可进行洗炼");
		} else if ((type != 3) && (type != 2) && (type != 1)) {
			builder.setResult(false);
			builder.setErrInfo("装备洗炼失败");
		} else if ((times != 1) && (times != 5) && (times != 10)) {
			builder.setResult(false);
			builder.setErrInfo("装备洗炼失败");
		} else {
			EquipmentTemplate template = (EquipmentTemplate) equip.getTemplate();
			PolishAttr attr = getPolishAttr(template.polishRuleId);
			List costs = null;
			if (type == 1)
				costs = attr.normalCost;
			else if (type == 2)
				costs = attr.moneyCost;
			else {
				costs = attr.jewelCost;
			}
			for (Iterator localIterator1 = costs.iterator(); localIterator1.hasNext();) {
				String result;
				Reward reward = (Reward) localIterator1.next();
				tmpReward = reward.copy();
				tmpReward.count *= times;

				if ((result = tmpReward.check(player, new int[] { equipId })) != null) {
					builder.setResult(false);
					builder.setErrInfo(result);
					break;
				}
				actualCosts.add(tmpReward);
			}
		}
		if (builder.getResult()) {
			equip.getTmpAttr().clear();
			Map result = equip.polish(type, times);
			Map maxAttr = equip.getPolishMaxValue();
			Iterator itx = result.keySet().iterator();
			while (itx.hasNext()) {
				int attrType = ((Integer) itx.next()).intValue();
				int value = ((Integer) result.get(Integer.valueOf(attrType))).intValue() * times;
				if (value > 0) {
					if (equip.getPolishAttr(attrType) + value > ((Integer) maxAttr.get(Integer.valueOf(attrType)))
							.intValue()) {
						result.put(Integer.valueOf(attrType),
								Integer.valueOf(((Integer) maxAttr.get(Integer.valueOf(attrType))).intValue()
										- equip.getPolishAttr(attrType)));
					}
				} else if ((value < 0) && (equip.getPolishAttr(attrType) + value < 0)) {
					result.put(Integer.valueOf(attrType), Integer.valueOf(equip.getPolishAttr(attrType) * -1));
				} else {
					result.put(Integer.valueOf(attrType), Integer.valueOf(value));
				}
			}
			String logOptType = (type == 2) ? "moneypolish" : (type == 1) ? "normalpolish" : "jewelspolish";
			for (Reward reward : actualCosts) {
				reward.remove(player, logOptType, new int[] { equipId });
			}
			equip.setTmpPolishNum(times);
			equip.setTmpPolishType(type);
			equip.setTmpAttr(result);
			equip.sync();

			Platform.getLog().logEquipment(player, equip, logOptType, times);
			Platform.getEventManager().addEvent(new Event(2025, new Object[] { player, Integer.valueOf(times) }));
			builder.setResult(true);
			builder.setPolishAttr(Attributes.genAttribute(result));
		}
		player.send(1054, builder.build());
	}

	private void equipmentPolishBesure(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 19))) {
			return;
		}
		PbDown.EquipmentPolishBesureRst.Builder builder = PbDown.EquipmentPolishBesureRst.newBuilder();
		int equipId = 0;
		boolean isReplace = false;
		try {
			PbUp.EquipmentPolishBesure besure = PbUp.EquipmentPolishBesure.parseFrom(packet.getData());
			equipId = besure.getEquipId();
			isReplace = besure.getReplace();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("装备洗炼失败");
			player.send(1056, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else {
			Map tmpAttr = equip.getTmpAttr();
			if (isReplace)
				if (tmpAttr.size() == 0) {
					builder.setResult(false);
					builder.setErrInfo("装备尚未进行洗炼");
				} else {
					Map polishAttr = equip.getPolishAttr();
					Iterator itx = polishAttr.keySet().iterator();
					while (itx.hasNext()) {
						int attrType = ((Integer) itx.next()).intValue();
						int value = ((Integer) polishAttr.get(Integer.valueOf(attrType))).intValue();
						polishAttr.put(Integer.valueOf(attrType),
								Integer.valueOf(value + ((Integer) tmpAttr.get(Integer.valueOf(attrType))).intValue()));
					}

					if (equip.getWarriorId() > 0) {
						Warrior warrior = player.getWarriors().getWarriorById(equip.getWarriorId());
						if (warrior == null)
							equip.setWarriorId(0);
						else {
							warrior.refreshAttributes(true);
						}
					}
					builder.setResult(true);
				}
			else {
				builder.setResult(true);
			}
			tmpAttr.clear();
			equip.sync();
		}
		player.send(1056, builder.build());
	}

	private void equipmentRefine(Player player, PbPacket.Packet packet) {
		PbDown.EquipmentRefineRst.Builder builder = PbDown.EquipmentRefineRst.newBuilder();
		builder.setResult(true);
		Set<Integer> ids = null;
		try {
			PbUp.EquipmentRefine refine = PbUp.EquipmentRefine.parseFrom(packet.getData());
			ids = new HashSet(refine.getEquipIdsList());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}炼化失败", new Object[] { "装备" }));
			player.send(1066, builder.build());
			return;
		}
		if ((ids.size() > 6) || (ids.size() < 1)) {
			builder.setResult(false);
			builder.setErrInfo("材料数量异常");
		} else {
			List<Equipment> list = new ArrayList();
			for (Integer id : ids) {
				Equipment equip = (Equipment) player.getBags().getItemById(id.intValue(), 4);
				if (equip == null) {
					builder.setResult(false);
					builder.setErrInfo("装备不存在");
					break;
				}
				if (equip.getWarriorId() > 0) {
					builder.setResult(false);
					builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "装备" }));
					break;
				}
				if (equip.getForgeLevel() > 0) {
					builder.setResult(false);
					builder.setErrInfo(MessageFormat.format("该{0}不能被炼化", new Object[] { "装备" }));
					break;
				}
				list.add(equip);
			}

			if (builder.getResult()) {
				int addMoney = 0;
				int i = 0;
				int forgeItemCount = 0;
				for (Equipment equipment : list) {
					EquipmentTemplate template = (EquipmentTemplate) equipment.getTemplate();
					int ruleId = template.intensifyRule;
					int totalExp = ((ExpService) Platform.getServiceManager().get(ExpService.class))
							.calTotalExpByLevel(ruleId, equipment.getLevel());
					addMoney += totalExp + equipment.getPrice();
					if (template.basePolishItemCount > 0) {
						i += template.basePolishItemCount;
					}
					if ((equipment.canPolish()) && (template.basePolishItemCount > 0)) {
						i += equipment.getPolishCount() * 2;
					}
					forgeItemCount += Math.max(template.refineBackForgeItemCount, 0);
					player.getBags().removeItem(equipment.id, equipment.getTemplateId(), 1, "equiprefine");
					Platform.getLog().logEquipment(player, equipment, "equiprefine", -1);
				}
				player.addMoney(addMoney, "equiprefine");
				if (i > 0) {
					int polishItemTemplateId = 11004;
					Item item = generateItem(polishItemTemplateId, player);
					player.getBags().addItem(item, i, "equiprefine");
					builder.addRewards(Reward.genPbReward(0, i, polishItemTemplateId));
				}
				if (forgeItemCount > 0) {
					Item item = generateItem(11010, player);
					player.getBags().addItem(item, forgeItemCount, "equiprefine");
					builder.addRewards(Reward.genPbReward(0, forgeItemCount, 11010));
				}
				builder.setResult(true);
				builder.addRewards(Reward.genPbReward(2, addMoney));
			}
		}
		player.send(1066, builder.build());
	}

	private void equipmentReborn(Player player, PbPacket.Packet packet) {
		int equipId;
		PbDown.EquipmentRebornRst.Builder builder = PbDown.EquipmentRebornRst.newBuilder();
		builder.setResult(true);
		try {
			PbUp.EquipmentReborn reborn = PbUp.EquipmentReborn.parseFrom(packet.getData());
			equipId = reborn.getEquipId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1068, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else if (equip.getWarriorId() > 0) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "装备" }));
		} else if (((EquipmentTemplate) equip.getTemplate()).quality < 5) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("该{0}不能重生", new Object[] { "装备" }));
		} else if ((equip.getLevel() < 2) && (equip.getForgeLevel() < 1)) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("该{0}不能重生", new Object[] { "装备" }));
		} else {
			EquipmentTemplate template = (EquipmentTemplate) equip.getTemplate();
			int costJewel = template.baseRebornCost * (equip.getForgeLevel() + 1);
			if (player.getJewels() < costJewel) {
				builder.setResult(false);
				builder.setErrInfo("元宝不足");
			} else {
				int addMoney = 0;
				int polishItemCount = 0;
				int forgeItemCount = 0;
				int ruleId = template.intensifyRule;
				int totalExp = ((ExpService) Platform.getServiceManager().get(ExpService.class))
						.calTotalExpByLevel(ruleId, equip.getLevel());
				addMoney += totalExp;
				if (equip.canPolish()) {
					polishItemCount += equip.getPolishCount() * 2;
				}
				if ((equip.canForge()) && (equip.getForgeLevel() > 0)) {
					int forgeExp = ((ExpService) Platform.getServiceManager().get(ExpService.class))
							.calTotalExpByLevel(5008, equip.getForgeLevel());
					forgeItemCount += forgeExp;
					for (int i = 1; i <= equip.getForgeLevel(); ++i) {
						addMoney += 50000 * i;
					}
				}
				player.addMoney(addMoney, "equipreborn");
				if (polishItemCount > 0) {
					int polishItemTemplateId = 11004;
					Item item = generateItem(polishItemTemplateId, player);
					player.getBags().addItem(item, polishItemCount, "equipreborn");
					builder.addRewards(Reward.genPbReward(0, polishItemCount, polishItemTemplateId));
				}
				if (forgeItemCount > 0) {
					Item item = generateItem(11010, player);
					player.getBags().addItem(item, forgeItemCount, "equipreborn");
					builder.addRewards(Reward.genPbReward(0, forgeItemCount, 11010));
				}
				player.decJewels(costJewel, "equipreborn");
				equip.clear();

				Platform.getLog().logEquipment(player, equip, "equipreborn", -1);
				builder.setResult(true);
				builder.setEquip(equip.genEquipment());
				builder.addRewards(Reward.genPbReward(2, addMoney));
			}
		}
		player.send(1068, builder.build());
	}

	private void equipmentCompound(Player player, PbPacket.Packet packet) {
		PbDown.EquipmentCompoundRst.Builder builder = PbDown.EquipmentCompoundRst.newBuilder();
		builder.setResult(true);
		int debrisId = 0;
		try {
			PbUp.EquipmentCompound compound = PbUp.EquipmentCompound.parseFrom(packet.getData());
			debrisId = compound.getDebrisId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}合成失败", new Object[] { "装备" }));
			player.send(1076, builder.build());
			return;
		}
		List list = player.getBags().getItemByTemplateId(debrisId);
		if ((list == null) || (list.size() != 1)) {
			builder.setResult(false);
			builder.setErrInfo("碎片不足");
		} else if (player.getBags().isFullBag(4)) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else {
			Debris item = (Debris) list.get(0);
			List<Reward> costs = new ArrayList();
			DebrisTemplate template = (DebrisTemplate) item.getTemplate();
			if ((template.costs != null) && (template.costs.size() > 0)) {
				for (String str : template.costs) {
					costs.add(new Reward(str));
				}
			}
			for (Reward cost : costs) {
				String str = cost.check(player);
				if (str != null) {
					builder.setResult(false);
					builder.setErrInfo(str);
					break;
				}
			}
			if (builder.getResult()) {
				Reward reward = new Reward(template.reward);
				Reward.RewardResult result = reward.add(player, "equipcompound");
				for (Reward cost : costs) {
					cost.remove(player, "equipcompound");
				}

				builder.setResult(true);
				if (result.items.size() == 1) {
					builder.setEquip(((Equipment) result.items.get(0)).genEquipment());
				}
				Platform.getEventManager().addEvent(new Event(2050, new Object[] { player }));
			}
		}
		player.send(1076, builder.build());
	}

	private void equipmentNextLevelInfo(Player player, PbPacket.Packet packet) {
		int equipId;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 40))) {
			return;
		}
		PbDown.EquipmentNextLevelInfoRst.Builder builder = PbDown.EquipmentNextLevelInfoRst.newBuilder();
		builder.setResult(true);
		try {
			PbUp.EquipmentNextLevelInfo info = PbUp.EquipmentNextLevelInfo.parseFrom(packet.getData());
			equipId = info.getEquipId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("获取信息失败");
			player.send(1104, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		int needExp = 0;
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else if (equip.getLevel() >= 200) {
			builder.setResult(false);
			builder.setErrInfo("装备已达最大等级");
		} else {
			int intensifyRule = ((EquipmentTemplate) equip.template).intensifyRule;
			needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(intensifyRule,
					equip.getLevel() + 1);
			builder.setMoney(needExp);
			Attributes attr = new Attributes();
			equip.calBaseAttr(attr, equip.getLevel() + 1, equip.getForgeLevel());
			builder.setAttribute(attr.genAttribute());
			builder.setResult(true);
		}
		player.send(1104, builder.build());
	}

	private void equipmentNextForgeInfo(Player player, PbPacket.Packet packet) {
		int equipId;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 56))) {
			return;
		}
		PbDown.EquipmentNextForgeRst.Builder builder = PbDown.EquipmentNextForgeRst.newBuilder();
		builder.setResult(true);
		try {
			PbUp.EquipmentNextForgeReq info = PbUp.EquipmentNextForgeReq.parseFrom(packet.getData());
			equipId = info.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("获取信息失败");
			player.send(1340, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		int needExp = 0;
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else if (!(equip.canForge())) {
			builder.setResult(false);
			builder.setErrInfo("该装备无法铸造");
		} else {
			if (!(equip.isFullForgeLevel())) {
				needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5008,
						equip.getForgeLevel() + 1);
				Reward r = new Reward(0, needExp, getItemTemplate(11010));
				builder.setCost(r.genPbReward());
				int costMoney = 50000 * (equip.getForgeLevel() + 1);
				builder.setMoney(costMoney);

				Attributes attr = new Attributes();
				equip.calBaseAttr(attr, equip.getLevel(), equip.getForgeLevel() + 1);
				builder.setNextAttr(attr.genAttribute());
			}
			builder.setCurAttr(equip.getBaseAttr().genAttribute());
			builder.setResult(true);
			builder.setMaxForgeLevel(20);
		}
		player.send(1340, builder.build());
	}

	private void equipmentForge(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 56))) {
			return;
		}
		PbDown.EquipmentForgeRst.Builder builder = PbDown.EquipmentForgeRst.newBuilder();
		builder.setResult(true);
		int equipId = 0;
		try {
			PbUp.EquipmentForgeReq intensify = PbUp.EquipmentForgeReq.parseFrom(packet.getData());
			equipId = intensify.getEquipId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1342, builder.build());
			return;
		}
		Equipment equip = (Equipment) player.getBags().getItemById(equipId, 4);
		if (equip == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else if (!(equip.canForge())) {
			builder.setResult(false);
			builder.setErrInfo("该装备无法铸造");
		} else if (equip.isFullForgeLevel()) {
			builder.setResult(false);
			builder.setErrInfo("装备已达最大铸造等级");
		} else {
			int costMoney = 50000 * (equip.getForgeLevel() + 1);
			int needExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5008,
					equip.getForgeLevel() + 1);
			Reward r = new Reward(0, needExp, getItemTemplate(11010));
			String errMsg = r.check(player);
			if (errMsg != null) {
				builder.setResult(false);
				builder.setErrInfo(errMsg);
			} else if (player.getMoney() < costMoney) {
				builder.setResult(false);
				builder.setErrInfo("银币不足");
			} else {
				equip.forge();
				player.decMoney(costMoney, "equipforge");
				r.remove(player, "equipforge");

				if (equip.getWarriorId() > 0) {
					Warrior warrior = player.getWarriors().getWarriorById(equip.getWarriorId());
					if (warrior == null)
						equip.setWarriorId(0);
					else {
						warrior.refreshAttributes(true);
					}
				}
				Platform.getLog().logEquipment(player, equip, "equipforge", 1);
			}
		}
		player.send(1342, builder.build());
	}

	private void treasureIntensify(Player player, PbPacket.Packet packet) {
		int mainId;
		PbDown.TreasureIntensifyRst.Builder builder = PbDown.TreasureIntensifyRst.newBuilder();
		builder.setResult(true);

		Set<Integer> ids = null;
		try {
			PbUp.TreasureIntensify intensify = PbUp.TreasureIntensify.parseFrom(packet.getData());
			mainId = intensify.getMainId();
			ids = new HashSet(intensify.getMaterialsList());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("宝物强化失败");
			player.send(1064, builder.build());
			return;
		}
		Treasure treasure = (Treasure) player.getBags().getItemById(mainId, 1);
		if (treasure == null) {
			builder.setResult(false);
			builder.setErrInfo("宝物不存在");
		} else if (treasure.getLevel() >= treasure.getMaxLevel()) {
			builder.setResult(false);
			builder.setErrInfo("宝物已达最大等级");
		} else if ((ids.size() > 5) || (ids.size() < 1)) {
			builder.setResult(false);
			builder.setErrInfo("材料数量异常");
		} else if (ids.contains(Integer.valueOf(mainId))) {
			builder.setResult(false);
			builder.setErrInfo("不能将被强化的宝物作为强化材料");
		} else {
			int ruleId;
			List<Treasure> materials = new ArrayList();
			int addExp = 0;
			for (Integer id : ids) {
				Treasure material = (Treasure) player.getBags().getItemById(id.intValue(), 1);
				if (material != null) {
					if (material.getWarriorId() > 0) {
						builder.setResult(false);
						builder.setErrInfo("不能使用已装备的宝物做材料");
						break;
					}
					if (((TreasureTemplate) material.getTemplate()).treasureType != ((TreasureTemplate) treasure
							.getTemplate()).treasureType) {
						builder.setResult(false);
						builder.setErrInfo("材料与强化宝物类型不一致");
						break;
					}
					if ((material.getTemplate().quality == 6) && (material.getTemplateId() != 37302)
							&& (material.getTemplateId() != 36302)) {
						builder.setResult(false);
						builder.setErrInfo("不能使用该物品作为材料");
						break;
					}

					materials.add(material);
					ruleId = ((TreasureTemplate) material.getTemplate()).intensifyRule;

					addExp = addExp
							+ material.getExp() + ((ExpService) Platform.getServiceManager().get(ExpService.class))
									.calTotalExpByLevel(ruleId, material.getLevel())
							+ ((TreasureTemplate) material.getTemplate()).baseExp;
				} else {
					builder.setResult(false);
					builder.setErrInfo("材料不存在");
					break;
				}
			}
			if (builder.getResult()) {
				int costMoney = ((TreasureTemplate) treasure.template).intensifyCostMoneyRatio * addExp;
				if (player.getMoney() < costMoney) {
					builder.setResult(false);
					builder.setErrInfo("银币不足");
				} else {
					int upLevel = treasure.addExp(addExp);
					if ((upLevel > 0) && (treasure.getWarriorId() > 0)) {
						Warrior warrior = player.getWarriors().getWarriorById(treasure.getWarriorId());
						if (warrior == null)
							treasure.setWarriorId(0);
						else {
							warrior.refreshAttributes(true);
						}
					}

					player.decMoney(costMoney, "treasureintensify");
					for (Treasure trea : materials) {
						player.getBags().removeItem(trea.getId(), trea.getTemplateId(), 1, "treasureintensify");
					}
					Platform.getLog().logTreasure(player, treasure, "treasureintensify", upLevel);
					builder.setResult(true);
					builder.setTreasure(treasure.genTreasure());
				}
			}
		}
		player.send(1064, builder.build());
	}

	private void treasureIntensifyInfo(Player player, PbPacket.Packet packet) {
		int mainId;
		PbDown.TreasureIntensifyInfoRst.Builder builder = PbDown.TreasureIntensifyInfoRst.newBuilder();
		builder.setResult(true);

		List<Integer> ids = null;
		try {
			PbUp.TreasureIntensifyInfo intensify = PbUp.TreasureIntensifyInfo.parseFrom(packet.getData());
			mainId = intensify.getMainId();
			ids = intensify.getMaterialsList();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("宝物强化预览失败");
			player.send(1110, builder.build());
			return;
		}
		Treasure treasure = (Treasure) player.getBags().getItemById(mainId, 1);
		if (treasure == null) {
			builder.setResult(false);
			builder.setErrInfo("宝物不存在");
		} else if (treasure.getLevel() >= treasure.getMaxLevel()) {
			builder.setResult(false);
			builder.setErrInfo("宝物已达最大等级");
		} else if ((ids.size() > 5) || (ids.size() < 1)) {
			builder.setResult(false);
			builder.setErrInfo("材料数量异常");
		} else if (ids.contains(Integer.valueOf(mainId))) {
			builder.setResult(false);
			builder.setErrInfo("不能将被强化的宝物作为强化材料");
		} else {
			List materials = new ArrayList();
			int addExp = 0;
			for (Integer id : ids) {
				Treasure material = (Treasure) player.getBags().getItemById(id.intValue(), 1);
				if (material != null) {
					if (material.getWarriorId() > 0) {
						builder.setResult(false);
						builder.setErrInfo("不能使用已装备的宝物做材料");
						break;
					}
					if (((TreasureTemplate) material.getTemplate()).treasureType != ((TreasureTemplate) treasure
							.getTemplate()).treasureType) {
						builder.setResult(false);
						builder.setErrInfo("材料与强化宝物类型不一致");
						break;
					}
					materials.add(material);
					int ruleId = ((TreasureTemplate) material.getTemplate()).intensifyRule;

					addExp = addExp
							+ material.getExp() + ((ExpService) Platform.getServiceManager().get(ExpService.class))
									.calTotalExpByLevel(ruleId, material.getLevel())
							+ ((TreasureTemplate) material.getTemplate()).baseExp;
				} else {
					builder.setResult(false);
					builder.setErrInfo("材料不存在");
					break;
				}
			}
			if (builder.getResult()) {
				int costMoney = ((TreasureTemplate) treasure.template).intensifyCostMoneyRatio * addExp;
				builder.setMoney(costMoney);
				builder.setExp(addExp);
				int[] result = treasure.addExpPreview(addExp);
				builder.setLevel(treasure.getLevel() + result[1]);
				Map attr = new HashMap();
				treasure.calBaseAttr(attr, treasure.getLevel() + result[1]);
				builder.setAttribute(Attributes.genAttribute(attr));
				builder.setResult(true);
			}
		}
		player.send(1110, builder.build());
	}

	private void treasureEnhance(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 22))) {
			return;
		}
		PbDown.TreasureEnhanceRst.Builder builder = PbDown.TreasureEnhanceRst.newBuilder();
		builder.setResult(true);
		int treasureId = 0;
		try {
			PbUp.TreasureEnhance enhance = PbUp.TreasureEnhance.parseFrom(packet.getData());
			treasureId = enhance.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("宝物精炼失败");
			player.send(1070, builder.build());
			return;
		}
		Treasure treasure = (Treasure) player.getBags().getItemById(treasureId, 1);
		if (treasure == null) {
			builder.setResult(false);
			builder.setErrInfo("宝物不存在");
		} else if (!(treasure.canEnhance())) {
			builder.setResult(false);
			builder.setErrInfo("只有紫色品质及以上的宝物才可进行精炼");
		} else if (treasure.getEnhanceLevel() >= treasure.getMaxEnhanceLevel()) {
			builder.setResult(false);
			builder.setErrInfo("宝物已达最大精炼等级");
		} else {
			TreasureTemplate template = (TreasureTemplate) treasure.template;
			int nextEnhanceLevel = treasure.getEnhanceLevel() + 1;
			List costItems = new ArrayList();
			List<String> list = (List) template.enhanceCost.get(Integer.valueOf(nextEnhanceLevel));
			if ((list != null) && (list.size() > 0)) {
				for (String str : list) {
					Reward cost = new Reward(str);
					costItems.add(cost);
				}
			}
			Map<Reward, List<Integer>> costMap = player.getBags().getExcludeItemIds(costItems);

			Reward me = new Reward(0, 1, template);
			for (Reward r : costMap.keySet()) {
				if (r.isSame(me)) {
					List tmp = (List) costMap.get(r);
					if (tmp == null) {
						tmp = new ArrayList();
					}
					if (tmp.contains(Integer.valueOf(treasure.id)))
						break;
					tmp.add(Integer.valueOf(treasure.id));

					break;
				}
			}

			Iterator itx = costMap.keySet().iterator();
			while (itx.hasNext()) {
				Reward r = (Reward) itx.next();
				List excludeIds = (List) costMap.get(r);
				String str = null;
				if ((excludeIds != null) && (excludeIds.size() > 0))
					str = r.check(player, excludeIds);
				else {
					str = r.check(player);
				}
				if (str != null) {
					builder.setResult(false);
					builder.setErrInfo(str);
					player.send(1070, builder.build());
					return;
				}
			}

			int costMoney = template.enhanceMoney[(nextEnhanceLevel - 1)];
			if (player.getMoney() < costMoney) {
				builder.setResult(false);
				builder.setErrInfo("银币不足");
			} else {
				treasure.enhance();
				player.decMoney(costMoney, "enhance");
				itx = costMap.keySet().iterator();
				while (itx.hasNext()) {
					Reward r = (Reward) itx.next();
					List excludeIds = (List) costMap.get(r);
					if ((excludeIds != null) && (excludeIds.size() > 0))
						r.remove(player, "enhance", excludeIds);
					else {
						r.remove(player, "enhance");
					}
				}

				if (treasure.getWarriorId() > 0) {
					Warrior warrior = player.getWarriors().getWarriorById(treasure.getWarriorId());
					if (warrior == null)
						treasure.setWarriorId(0);
					else {
						warrior.refreshAttributes(true);
					}
				}
				Platform.getLog().logTreasure(player, treasure, "enhance", 1);
				builder.setResult(true);
				builder.setTreasure(treasure.genTreasure());
			}
		}
		player.send(1070, builder.build());
	}

	private void treasureRefine(Player player, PbPacket.Packet packet) {
		PbDown.TreasureRefineRst.Builder builder = PbDown.TreasureRefineRst.newBuilder();
		builder.setResult(true);
		Set<Integer> ids = null;
		try {
			PbUp.TreasureRefine refine = PbUp.TreasureRefine.parseFrom(packet.getData());
			ids = new HashSet(refine.getIdsList());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}炼化失败", new Object[] { "宝物" }));
			player.send(1072, builder.build());
			return;
		}
		if ((ids.size() > 6) || (ids.size() < 1)) {
			builder.setResult(false);
			builder.setErrInfo("材料数量异常");
		} else {
			List<Treasure> list = new ArrayList();
			for (Integer id : ids) {
				Treasure treasure = (Treasure) player.getBags().getItemById(id.intValue(), 1);
				if (treasure == null) {
					builder.setResult(false);
					builder.setErrInfo("宝物不存在");
					break;
				}
				if (treasure.getWarriorId() > 0) {
					builder.setResult(false);
					builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "宝物" }));
					break;
				}
				if ((((TreasureTemplate) treasure.template).quality < 6) || (treasure.template.id == 36302)
						|| (treasure.template.id == 37302) || (treasure.template.id == 38001)
						|| (treasure.template.id == 36301) || (treasure.template.id == 37301)) {
					builder.setResult(false);
					builder.setErrInfo(MessageFormat.format("该{0}不能被炼化", new Object[] { "宝物" }));
					break;
				}
				if ((treasure.level > 0) || (treasure.getEnhanceLevel() > 0)) {
					builder.setResult(false);
					builder.setErrInfo(MessageFormat.format("该{0}不能被炼化", new Object[] { "宝物" }));
					break;
				}
				list.add(treasure);
			}

			if (builder.getResult()) {
				int addMoney = 0;
				int bookExp = 0;
				int horseExp = 0;
				int i = 0;
				for (Treasure treasure : list) {
					TreasureTemplate template = (TreasureTemplate) treasure.getTemplate();
					int ruleId = template.intensifyRule;
					int totalExp = treasure.getExp() + ((ExpService) Platform.getServiceManager().get(ExpService.class))
							.calTotalExpByLevel(ruleId, treasure.getLevel())
							+ ((TreasureTemplate) treasure.getTemplate()).baseExp;
					if (treasure.getTreasureType() == 2)
						bookExp += totalExp;
					else {
						horseExp += totalExp;
					}
					i += 3;
					addMoney += template.intensifyCostMoneyRatio * totalExp + treasure.getPrice();
					player.getBags().removeItem(treasure.id, treasure.getTemplateId(), 1, "treasurerefine");
					Platform.getLog().logTreasure(player, treasure, "treasurerefine", -1);
					if ((treasure.canEnhance()) && (treasure.getEnhanceLevel() > 0)) {
						for (int j = 1; j <= treasure.getEnhanceLevel(); ++j) {
							List<String> list2 = (List) template.enhanceCost.get(Integer.valueOf(j));
							for (String str : list2) {
								Reward tmp = new Reward(str);
								if ((tmp.type == 0) && (tmp.template.id == 38001)) {
									i += tmp.count;
								}
							}
						}
					}
				}
				player.addMoney(addMoney, "treasurerefine");
				Treasure book = null;
				Treasure horse = null;
				if (horseExp > 0) {
					horse = (Treasure) generateItem(36301, player);
					horse.addExp(horseExp - ((TreasureTemplate) horse.getTemplate()).baseExp);
					player.getBags().addItem(horse, 1, "treasurerefine");
					builder.addRewards(Reward.genPbReward(0, 1, horse.getTemplateId()));
				}
				if (bookExp > 0) {
					book = (Treasure) generateItem(37301, player);
					book.addExp(bookExp - ((TreasureTemplate) book.getTemplate()).baseExp);
					player.getBags().addItem(book, 1, "treasurerefine");
					builder.addRewards(Reward.genPbReward(0, 1, book.getTemplateId()));
				}
				for (i = 0; i < i; ++i) {
					Item item = generateItem(38001, player);
					player.getBags().addItem(item, 1, "treasurerefine");
				}

				builder.setResult(true);
				builder.addRewards(Reward.genPbReward(2, addMoney));
				builder.addRewards(Reward.genPbReward(0, i, 38001));
			}
		}
		player.send(1072, builder.build());
	}

	private void treasureReborn(Player player, PbPacket.Packet packet) {
		PbDown.TreasureRebornRst.Builder builder = PbDown.TreasureRebornRst.newBuilder();
		builder.setResult(true);
		int id = 0;
		try {
			PbUp.TreasureReborn reborn = PbUp.TreasureReborn.parseFrom(packet.getData());
			id = reborn.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}重生失败", new Object[] { "宝物" }));
			player.send(1074, builder.build());
			return;
		}
		Treasure treasure = (Treasure) player.getBags().getItemById(id, 1);
		if (treasure == null) {
			builder.setResult(false);
			builder.setErrInfo("宝物不存在");
		} else if (treasure.getWarriorId() > 0) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "宝物" }));
		} else if ((treasure.getLevel() < 1) || (((TreasureTemplate) treasure.getTemplate()).quality < 5)) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("该{0}不能重生", new Object[] { "宝物" }));
		} else if ((treasure.template.id == 36302) || (treasure.template.id == 37302) || (treasure.template.id == 38001)
				|| (treasure.template.id == 36301) || (treasure.template.id == 37301)) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("该{0}不能重生", new Object[] { "宝物" }));
		} else {
			TreasureTemplate template = (TreasureTemplate) treasure.getTemplate();
			int costJewel = 50;
			if (player.getJewels() < costJewel) {
				builder.setResult(false);
				builder.setErrInfo("元宝不足");
			} else {
				int ruleId = template.intensifyRule;
				int totalExp = treasure.getExp() + ((ExpService) Platform.getServiceManager().get(ExpService.class))
						.calTotalExpByLevel(ruleId, treasure.getLevel());
				int addMoney = template.intensifyCostMoneyRatio * totalExp;
				List<Reward> enhanceCostItems = null;
				if (treasure.canEnhance()) {
					enhanceCostItems = treasure.getAllEnhanceCost();
				}

				player.addMoney(addMoney, "treasurereborn");
				int returnExpItemId = (template.treasureType == 2) ? 37301 : 36301;
				Treasure returnItem = (Treasure) generateItem(returnExpItemId, player);
				returnItem.addExp(totalExp);
				if ((enhanceCostItems != null) && (enhanceCostItems.size() > 0)) {
					for (Reward reward : enhanceCostItems) {
						reward.add(player, "treasurereborn");
						builder.addRewards(reward.genPbReward());
					}
				}
				player.decJewels(costJewel, "treasurereborn");
				player.getBags().addItem(returnItem, 1, "treasurereborn");
				treasure.clear();
				Platform.getLog().logTreasure(player, treasure, "treasurereborn", -1);
				builder.setResult(true);
				builder.setTreasure(treasure.genTreasure());
				builder.addRewards(Reward.genPbReward(2, addMoney));
				builder.addRewards(Reward.genPbReward(0, 1, returnItem.getTemplateId()));
			}
		}
		player.send(1074, builder.build());
	}

	private void treasureCompound(Player player, PbPacket.Packet packet) {
		PbDown.TreasureCompoundRst.Builder builder = PbDown.TreasureCompoundRst.newBuilder();
		builder.setResult(true);
		int id = 0;
		try {
			PbUp.TreasureCompound compound = PbUp.TreasureCompound.parseFrom(packet.getData());
			id = compound.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}合成失败", new Object[] { "宝物" }));
			player.send(1078, builder.build());
			return;
		}
		ItemTemplate itemTemplate = getItemTemplate(id);
		if (itemTemplate == null) {
			builder.setResult(false);
			builder.setErrInfo("宝物不存在");
		} else if (player.getBags().isFullBag(1)) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (itemTemplate.type != 1) {
			builder.setResult(false);
			builder.setErrInfo("宝物不存在");
		} else {
			TreasureTemplate template = (TreasureTemplate) getItemTemplate(id);
			List<Integer> debris = template.debris;
			for (Integer debrisId : debris) {
				if (player.getBags().getItemCount(debrisId.intValue()) < 1) {
					builder.setResult(false);
					builder.setErrInfo("材料不足");
					break;
				}
			}
			if (builder.getResult()) {
				Treasure treasure = (Treasure) generateItem(id, player);
				for (Integer debrisId : debris) {
					player.getBags().removeItem(0, debrisId.intValue(), 1, "treasurecompound");
				}
				player.getBags().addItem(treasure, 1, "treasurecompound");
				player.getLootTreasure().removeCompoundCount(id);
				builder.setResult(true);
				builder.setTreasure(treasure.genTreasure());
			}
		}
		player.send(1078, builder.build());
	}

	private void debrisDropPath(Player player, PbPacket.Packet packet) {
		PbDown.DebrisDropPathRst.Builder b = PbDown.DebrisDropPathRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.DebrisDropPath req = PbUp.DebrisDropPath.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1206, b.build());
			return;
		}
		ItemTemplate tl = (ItemTemplate) templates.get(Integer.valueOf(id));
		if (tl == null) {
			b.setResult(false);
			b.setErrInfo("碎片不存在");
		} else {
			List<DebrisDropPath> list = (List) debrisPaths.get(Integer.valueOf(id));
			if ((list != null) && (list.size() > 0)) {
				for (DebrisDropPath path : list) {
					b.addPaths(path.genDebrisPath(player));
				}
			}
		}
		player.send(1206, b.build());
	}

	public static Item getItem(Player player, int id, int type) {
		if (type == 2) {
			Map map = player.getWarriors().getWarriors();
			Iterator itx = map.keySet().iterator();
			while (itx.hasNext()) {
				int index = ((Integer) itx.next()).intValue();
				Warrior warrior = (Warrior) map.get(Integer.valueOf(index));
				if (warrior.getId() == id) {
					return warrior;
				}

			}

		}

		return player.getBags().getItemById(id, type);
	}

	public void startup() throws Exception {
		loadItemEffects();
		loadHeros();
		loadDebris();
		loadNormalItems();
		loadEquipments();
		loadSuit();
		loadPolishRandom();
		loadTreasures();

		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		templates.clear();
		loadHeros();
	}

	private static void genItemJson() throws IOException {
		Comparator comparator = new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> o1, Map<String, String> o2) {
				return (Integer.valueOf((String) o1.get("id")).intValue()
						- Integer.valueOf((String) o2.get("id")).intValue());
			}
		};
		Iterator itx = templates.keySet().iterator();
		List normalList = new ArrayList();
		List heroList = new ArrayList();
		List debrisList = new ArrayList();
		List equipList = new ArrayList();
		List treasureList = new ArrayList();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			ItemTemplate template = (ItemTemplate) templates.get(Integer.valueOf(id));
			Map item = new HashMap();
			item.put("id", String.valueOf(template.id));
			item.put("text", template.name);
			switch (template.type) {
			case 2:
				heroList.add(item);
				break;
			case 0:
				normalList.add(item);
				break;
			case 3:
				debrisList.add(item);
				break;
			case 4:
				equipList.add(item);
				break;
			case 1:
				treasureList.add(item);
			}

		}

		Collections.sort(normalList, comparator);
		JSONArray array = JSONArray.fromObject(normalList);
		File file = new File("d:/publish/item/normalItem.txt");
		BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8")));
		bw.write(array.toString());
		bw.close();
		Collections.sort(heroList, comparator);
		array = JSONArray.fromObject(heroList);
		file = new File("d:/publish/item/heroItem.txt");
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8")));
		bw.write(array.toString());
		bw.close();
		Collections.sort(debrisList, comparator);
		array = JSONArray.fromObject(debrisList);
		file = new File("d:/publish/item/debrisItem.txt");
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8")));
		bw.write(array.toString());
		bw.close();
		Collections.sort(equipList, comparator);
		array = JSONArray.fromObject(equipList);
		file = new File("d:/publish/item/equipItem.txt");
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8")));
		bw.write(array.toString());
		bw.close();
		Collections.sort(treasureList, comparator);
		array = JSONArray.fromObject(treasureList);
		file = new File("d:/publish/item/treasureItem.txt");
		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8")));
		bw.write(array.toString());
		bw.close();

		System.out.println("finish!!");
	}

	private int getEquipLevelScore(int level) {
		return ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5002, level);
	}

	private int getHeroLevelScore(int level) {
		return ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5003, level);
	}

	private int getHeroAdvanceScore(int level) {
		return ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExp(5004, level);
	}

	public int getEquipLevelScores(Player player) {
		long hasScores = 0L;
		long maxScores = 0L;
		for (Warrior w : player.getWarriors().getStands()) {
			if (w != null) {
				for (Equipment e : w.getEquips().getEquips()) {
					if (e != null) {
						hasScores += getEquipLevelScore(e.getLevel());
						maxScores += getEquipLevelScore(e.getMaxLevel(player.getLevel()));
					}
				}
			}
		}

		if (maxScores == 0L) {
			maxScores = 100L;
		}

		return (int) (hasScores * 100L / maxScores);
	}

	public int getHeroLevelScores(Player player) {
		int hasScores = 0;
		int maxScores = 0;
		int count = 0;
		for (Warrior w : player.getWarriors().getStands()) {
			if ((w != null) && (!(w.isMainWarrior()))) {
				hasScores += getHeroLevelScore(w.getLevel());
				++count;
			}
		}

		maxScores = count * getHeroLevelScore(player.getWarriors().getMainWarrior().getLevel());

		if (maxScores == 0) {
			maxScores = 100;
		}

		return (hasScores * 100 / maxScores);
	}

	public int getHeroAdvanceScores(Player player) {
		int hasScores = 0;
		int maxScores = 0;
		int count = 0;
		for (Warrior w : player.getWarriors().getStands()) {
			if (w != null) {
				hasScores += getHeroAdvanceScore(w.getAdvanceLevel());
				++count;
			}
		}
		maxScores = count * getHeroAdvanceScore(player.getWarriors().getMainWarrior().getMaxAdvanceLevel(player));
		if (maxScores == 0) {
			maxScores = 100;
		}
		return (hasScores * 100 / maxScores);
	}

	public void indexSync(Player player) {
		player.getDataSyncManager().addNumSync(16, getEquipLevelScores(player));
		player.getDataSyncManager().addNumSync(15, getHeroAdvanceScores(player));
		player.getDataSyncManager().addNumSync(14, getHeroLevelScores(player));
	}
}
