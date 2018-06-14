package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.glory.GloryRecord;
import org.darcy.sanguo.hero.Formation;
import org.darcy.sanguo.hero.HeroAdvance;
import org.darcy.sanguo.hero.HeroTemplate;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Debris;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Equipments;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.item.Treasures;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.tactic.TacticRecord;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbStandStruct;
import sango.packet.PbUp;

public class HeroService implements Service, PacketHandler {
	public HashMap<Integer, HeroAdvance> heroAdvances = new HashMap<Integer, HeroAdvance>();

	public static Map<Integer, HashMap<Integer, Integer>> advanceCapaBonus = new HashMap<Integer, HashMap<Integer, Integer>>();

	public void startup() throws Exception {
		loadFormation();
		loadHeroAdvance();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1017, 1019, 1023, 1025, 1027, 1031, 1037, 1043, 1045, 1057, 1059, 1061, 1285, 1299 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 1017:
			heroIntensify(player, packet);
			break;
		case 1019:
			heroAdvance(player, packet);
			break;
		case 1023:
			heroCompound(player, packet);
			break;
		case 1025:
			heroRefine(player, packet);
			break;
		case 1027:
			heroReborn(player, packet);
			break;
		case 1031:
			warriorChange(player, packet);
			break;
		case 1037:
			getWarriorNextLevelInfo(player, packet);
			break;
		case 1043:
			fellowChange(player, packet);
			break;
		case 1045:
			updateStands(player, packet);
			break;
		case 1057:
			equip(player, packet);
			break;
		case 1059:
			unEquip(player, packet);
			break;
		case 1061:
			getWarriorNextAdvanceLevelInfo(player, packet);
			break;
		case 1285:
			fellowDownStage(player, packet);
			break;
		case 1299:
			gloryUsedInfo(player);
		}
	}

	private void loadHeroAdvance() {
		List<Row> list = ExcelUtils.getRowList("advance.xls", 2);
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int playerLevel = (int) row.getCell(pos++).getNumericCellValue();
			int heroLevel = (int) row.getCell(pos++).getNumericCellValue();
			int newHeroId = (int) row.getCell(pos++).getNumericCellValue();
			int moneyCost = (int) row.getCell(pos++).getNumericCellValue();
			String costStr = row.getCell(pos++).getStringCellValue();
			int heroNum = (int) row.getCell(pos++).getNumericCellValue();
			int advanceValue = (int) row.getCell(pos++).getNumericCellValue();

			HeroAdvance advance = new HeroAdvance();
			advance.id = id;
			advance.playerLevel = playerLevel;
			advance.heroLevel = heroLevel;
			advance.newHeroId = newHeroId;
			advance.costMoney = moneyCost;
			if ((costStr != null) && (!(costStr.equals("-1")))) {
				String[] costArray = costStr.split(",");
				for (String str : costArray) {
					int[] costInfo = Calc.split(str, "\\|");
					advance.costList.put(Integer.valueOf(costInfo[0]), Integer.valueOf(costInfo[1]));
				}
			}
			advance.heroNum = heroNum;
			advance.advanceValue = advanceValue;

			this.heroAdvances.put(Integer.valueOf(id), advance);
		}

		List<Row> list2 = ExcelUtils.getRowList("advance.xls", 2, 1);
		for (Row row : list2) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int aptitude = (int) row.getCell(pos++).getNumericCellValue();
			int advanceLevel = (int) row.getCell(pos++).getNumericCellValue();
			int capa = (int) row.getCell(pos++).getNumericCellValue();
			HashMap map = (HashMap) advanceCapaBonus.get(Integer.valueOf(aptitude));
			if (map == null) {
				map = new HashMap();
				advanceCapaBonus.put(Integer.valueOf(aptitude), map);
			}
			map.put(Integer.valueOf(advanceLevel), Integer.valueOf(capa));
		}
	}

	private void loadFormation() {
		int i=0, j;
		List<Row> list = ExcelUtils.getRowList("formation.xls", 2);
		for (Row row : list) {
			if(i==0)
				continue;
			i++;
			int[] tmp;
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			pos++;
			String openSort = row.getCell(pos++).getStringCellValue();
			String openPositionLevel = row.getCell(pos++).getStringCellValue();
			int leadPosition = (int) row.getCell(pos++).getNumericCellValue();
			
			String openNumByLevel = row.getCell(pos++).getStringCellValue();
			String openFriendByLevel = row.getCell(pos++).getStringCellValue();

			String[] array = openSort.split(",");
			for (j = 0; j < array.length; ++j) {
				Formation.openSort[j] = Integer.valueOf(array[j]).intValue();
			}
			array = openPositionLevel.split(",");
			for (j = 0; j < array.length; ++j) {
				Formation.openPositionLevel[j] = Integer.valueOf(array[j]).intValue();
			}
			Formation.leadPosition = leadPosition;
			array = openNumByLevel.split(",");
			for (j = 0; j < array.length; ++j) {
				tmp = Calc.split(array[j], "\\|");
				Formation.openIndexByLevel.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
			array = openFriendByLevel.split(",");
			for (j = 0; j < array.length; ++j) {
				tmp = Calc.split(array[j], "\\|");
				Formation.openFriendByLevel.put(Integer.valueOf(tmp[0]), Integer.valueOf(tmp[1]));
			}
		}
	}

	private void heroAdvance(Player player, PbPacket.Packet packet) {
		int templateId;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 2))) {
			return;
		}
		PbDown.HeroAdvanceRst.Builder builder = PbDown.HeroAdvanceRst.newBuilder();
		int heroId = 0;
		PbUp.HeroAdvance advance = null;
		try {
			advance = PbUp.HeroAdvance.parseFrom(packet.getData());
			heroId = advance.getHeroId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("武将突破失败");
			player.send(1020, builder.build());
			return;
		}

		Warrior hero = (Warrior) ItemService.getItem(player, heroId, 2);
		if (hero == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
			player.send(1020, builder.build());
			return;
		}
		int canAdvanceMaxLevel = ((HeroTemplate) hero.getTemplate()).advanceRule.size();
		if (canAdvanceMaxLevel <= 0) {
			builder.setResult(false);
			builder.setErrInfo("该武将不可突破");
			player.send(1020, builder.build());
			return;
		}
		if (hero.getAdvanceLevel() >= canAdvanceMaxLevel) {
			builder.setResult(false);
			builder.setErrInfo("已达到最大突破等级");
			player.send(1020, builder.build());
			return;
		}
		int advanceRuleId = ((Integer) ((HeroTemplate) hero.getTemplate()).advanceRule
				.get(Integer.valueOf(hero.getAdvanceLevel()))).intValue();
		HeroAdvance rule = getHeroAdvance(advanceRuleId);
		if (player.getLevel() < rule.playerLevel) {
			builder.setResult(false);
			builder.setErrInfo(
					MessageFormat.format("主角达到{0}级才可突破", new Object[] { Integer.valueOf(rule.playerLevel) }));
			player.send(1020, builder.build());
			return;
		}

		if (hero.getLevel() < rule.heroLevel) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("武将达到{0}级才可突破", new Object[] { Integer.valueOf(rule.heroLevel) }));
			player.send(1020, builder.build());
			return;
		}

		Map removeMap = new HashMap();
		Map costItem = new HashMap();
		Map costList = rule.costList;
		Set<Map.Entry> set = costList.entrySet();
		for (Map.Entry entry : set) {
			templateId = ((Integer) entry.getKey()).intValue();
			int num = ((Integer) entry.getValue()).intValue();
			ItemTemplate template = ItemService.getItemTemplate(templateId);
			if (!(Item.isCumulative(template.type))) {
				costItem.put(Integer.valueOf(templateId), Integer.valueOf(num));
			} else {
				if (player.getBags().getItemCount(templateId) < num) {
					builder.setResult(false);
					builder.setErrInfo("材料不足");
					player.send(1020, builder.build());
					return;
				}
				removeMap.put((Item) player.getBags().getItemByTemplateId(templateId).get(0), Integer.valueOf(num));
			}

		}

		if (rule.heroNum > 0) {
			costItem.put(Integer.valueOf(hero.getTemplateId()), Integer.valueOf(rule.heroNum));
		}

		if (costItem.size() > 0) {
			Set itemSet = costItem.entrySet();
			for (Iterator it = itemSet.iterator(); it.hasNext();) {
				Map.Entry entry1 = (Map.Entry) it.next();
				int itemTemplateId = ((Integer) entry1.getKey()).intValue();
				int num = ((Integer) entry1.getValue()).intValue();
				List<Item> list = player.getBags().getItemByTemplateId(itemTemplateId);
				boolean flag = true;
				if ((list != null) && (list.size() >= num)) {
					List canPick = new ArrayList();
					for (Item item : list) {
						if (item.getItemType() == 2) {
							Warrior warrior = (Warrior) item;
							if ((warrior.getAdvanceLevel() != 0) || (warrior.getStageStatus() != 0)
									|| (warrior.getId() == heroId))
								continue;
							canPick.add(warrior);
						} else if (item.getItemType() == 4) {
							Equipment equip = (Equipment) item;
							if (equip.getWarriorId() < 1)
								canPick.add(equip);
						} else if (item.getItemType() == 1) {
							Treasure treasure = (Treasure) item;
							if (treasure.getWarriorId() < 1) {
								canPick.add(treasure);
							}
						}
					}
					if (canPick.size() >= num) {
						flag = false;
						Collections.sort(canPick, new Comparator<Item>() {
							public int compare(Item o1, Item o2) {
								return (o1.getLevel() - o2.getLevel());
							}
						});
						for (int i = 0; i < num; ++i) {
							removeMap.put((Item) canPick.get(i), Integer.valueOf(1));
						}
					}
				}
				if (!(flag))
					continue;
				builder.setResult(false);
				builder.setErrInfo("材料不足");
				player.send(1020, builder.build());
				return;
			}

		}

		if (player.getMoney() < rule.costMoney) {
			builder.setResult(false);
			builder.setErrInfo("银币不足");
			player.send(1020, builder.build());
			return;
		}

		hero.advance(rule.newHeroId);
		player.decMoney(rule.costMoney, "advance");
		Platform.getLog().logWarrior(player, hero, "advance", 1);
		if (removeMap.size() > 0) {
			Set removeSet = removeMap.entrySet();
			for (Iterator it = removeSet.iterator(); it.hasNext();) {
				Map.Entry entry1 = (Map.Entry) it.next();
				Item item = (Item) entry1.getKey();
				player.getBags().removeItem(((Item) entry1.getKey()).getId(), item.getTemplateId(),
						((Integer) entry1.getValue()).intValue(), "advance");
			}
		}

		if (hero.isMainWarrior()) {
			Platform.getEventManager().addEvent(new Event(2021, new Object[] { player }));
		} else if (hero.getAdvanceLevel() >= 5) {
			String msg = MessageFormat.format(
					"<p style=13>[{0}]</p><p style=17>成功把</p><p style=14>[{1}]</p><p style=17>突破到+{2}，实力大幅度的提高了！</p>",
					new Object[] { player.getName(), hero.getName(), Integer.valueOf(hero.getAdvanceLevel()) });
			Platform.getPlayerManager().boardCast(msg);
		}

		Platform.getEventManager()
				.addEvent(new Event(2022, new Object[] { player, Integer.valueOf(hero.getAdvanceLevel()) }));

		builder.setResult(true);
		builder.setHero(hero.genWarrior());
		player.send(1020, builder.build());
	}

	private void heroIntensify(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 39))) {
			return;
		}
		PbDown.HeroIntensifyRst.Builder builder = PbDown.HeroIntensifyRst.newBuilder();
		int type = 1;
		int mainId = 0;
		int num = 0;
		List<Integer> ids = null;
		PbUp.HeroIntensify intensify = null;
		try {
			intensify = PbUp.HeroIntensify.parseFrom(packet.getData());
			type = intensify.getType();
			mainId = intensify.getHeroId();
			if (type != 2) {
				builder.setResult(false);
				builder.setErrInfo("强化类型有误");
				player.send(1018, builder.build());
				return;
			}
			if (type == 1) {
				ids = intensify.getMaterialsList();
			}
			num = intensify.getNum();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("武将升级失败");
			player.send(1018, builder.build());
			return;
		}

		if (player.getWarriors().getMainWarrior().getId() == mainId) {
			label146: builder.setResult(false);
			builder.setErrInfo("主武将不能升级");
			player.send(1018, builder.build());
			return;
		}
		Warrior main = (Warrior) ItemService.getItem(player, mainId, 2);
		if (main == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
			player.send(1018, builder.build());
			return;
		}
		if (main.getLevel() >= 200) {
			builder.setResult(false);
			builder.setErrInfo("武将已达最大等级");
			player.send(1018, builder.build());
			return;
		}

		if (type == 1) {
			if ((ids.size() > 5) || (ids.size() < 1)) {
				builder.setResult(false);
				builder.setErrInfo("材料数量异常");
				player.send(1018, builder.build());
				return;
			}
			List<Warrior> materialHeros = new ArrayList<Warrior>();
			int addExp = 0;
			for (Integer id : ids) {
				Warrior hero = (Warrior) player.getBags().getItemById(id.intValue(), 2);
				if (hero != null) {
					if (hero.getStageStatus() != 0) {
						builder.setResult(false);
						builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "武将" }));
						player.send(1026, builder.build());
						return;
					}
					if (((HeroTemplate) hero.getTemplate()).quality >= 5) {
						builder.setResult(false);
						builder.setErrInfo("不能使用五星武将作为升级材料");
						player.send(1018, builder.build());
						return;
					}
					materialHeros.add(hero);
					int ruleId = ((HeroTemplate) hero.getTemplate()).intensifyRule;

					addExp = addExp
							+ hero.getExp() + ((ExpService) Platform.getServiceManager().get(ExpService.class))
									.calTotalExpByLevel(ruleId, hero.getLevel())
							+ ((HeroTemplate) hero.getTemplate()).initExp;
				} else {
					builder.setResult(false);
					builder.setErrInfo("材料不存在");
					player.send(1018, builder.build());
					return;
				}
			}
			int mainRule = ((HeroTemplate) main.getTemplate()).intensifyRule;
			int tmpExp = main.getExp() + addExp + ((ExpService) Platform.getServiceManager().get(ExpService.class))
					.calTotalExpByLevel(mainRule, main.getLevel()) + ((HeroTemplate) main.getTemplate()).initExp;
			int newLevel = ((ExpService) Platform.getServiceManager().get(ExpService.class)).calLevelByExp(mainRule,
					tmpExp);
			if (newLevel > player.getLevel()) {
				builder.setResult(false);
				builder.setErrInfo("武将等级不能超过主角等级");
				player.send(1018, builder.build());
				return;
			}
			if (player.getMoney() < addExp) {
				builder.setResult(false);
				builder.setErrInfo("银币不足");
				player.send(1018, builder.build());
				return;
			}
			main.addExp(addExp, player.getLevel());
			player.decMoney(addExp, "warriorintensify");
			for (Warrior hero : materialHeros)
				player.getBags().removeItem(hero.getId(), hero.getTemplateId(), 1, "warriorintensify");
		} else {
			if (num < 1) {
				builder.setResult(false);
				builder.setErrInfo("武将升级失败");
				player.send(1018, builder.build());
				return;
			}
			int count = 0;
			for (int i = 0; i < num; ++i) {
				if (main.getLevel() >= player.getLevel()) {
					if (i != 0)
						break;
					builder.setResult(false);
					builder.setErrInfo("武将等级不能超过主角等级");
					player.send(1018, builder.build());
					return;
				}

				int restExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getRestExpToNextLevel(
						((HeroTemplate) main.getTemplate()).intensifyRule, main.getExp(), main.getLevel() + 1);
				if (player.getMoney() < restExp) {
					if (i != 0)
						break;
					builder.setResult(false);
					builder.setErrInfo("银币不足");
					player.send(1018, builder.build());
					return;
				}

				if (player.getWarriorSpirit() < restExp) {
					if (i != 0)
						break;
					builder.setResult(false);
					builder.setErrInfo("战功不足");
					player.send(1018, builder.build());
					return;
				}

				++count;
				main.addExp(restExp, player.getLevel());
				player.decMoney(restExp, "warriorintensify");
				player.decWarriorSpirit(restExp, "warriorintensify");
			}
			Platform.getLog().logWarrior(player, main, "warriorintensify", count);
			Platform.getEventManager().addEvent(new Event(2033, new Object[] { player }));
		}
		builder.setResult(true);
		builder.setHero(main.genWarrior());
		player.send(1018, builder.build());
	}

	private void heroCompound(Player player, PbPacket.Packet packet) {
		PbDown.HeroCompoundRst.Builder builder = PbDown.HeroCompoundRst.newBuilder();
		int heroDebrisId = 0;
		try {
			PbUp.HeroCompound compound = PbUp.HeroCompound.parseFrom(packet.getData());
			heroDebrisId = compound.getHeroDebrisId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}合成失败", new Object[] { "英雄" }));
			player.send(1024, builder.build());
			return;
		}

		List list = player.getBags().getItemByTemplateId(heroDebrisId);
		if ((list == null) || (list.size() == 0)) {
			builder.setResult(false);
			builder.setErrInfo("碎片不足");
			player.send(1024, builder.build());
			return;
		}
		Debris item = (Debris) list.get(0);
		if (player.getBags().isFullBag(2)) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
			player.send(1024, builder.build());
			return;
		}

		String errorInfo = item.canCompound(player);
		if (errorInfo != null) {
			builder.setResult(false);
			builder.setErrInfo(errorInfo);
			player.send(1024, builder.build());
			return;
		}

		List<Reward> costs = new ArrayList();
		DebrisTemplate template = (DebrisTemplate) item.getTemplate();
		if ((template.costs != null) && (template.costs.size() > 0)) {
			for (String str : template.costs) {
				costs.add(new Reward(str));
			}
		}
		Reward reward = new Reward(template.reward);
		Reward.RewardResult result = reward.add(player, "warriorcompound");
		for (Reward cost : costs) {
			cost.remove(player, "warriorcompound");
		}

		builder.setResult(true);
		if (result.items.size() == 1) {
			builder.setHero(((Warrior) result.items.get(0)).genWarrior());
		}
		Platform.getEventManager().addEvent(new Event(2049, new Object[] { player }));
		player.send(1024, builder.build());
	}

	private void heroRefine(Player player, PbPacket.Packet packet) {
		int i;
		PbDown.HeroRefineRst.Builder builder = PbDown.HeroRefineRst.newBuilder();
		Set<Integer> ids = null;
		try {
			PbUp.HeroRefine refine = PbUp.HeroRefine.parseFrom(packet.getData());
			ids = new HashSet(refine.getHeroIdsList());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}炼化失败", new Object[] { "武将" }));
			player.send(1026, builder.build());
			return;
		}

		if ((ids.size() > 6) || (ids.size() < 1)) {
			builder.setResult(false);
			builder.setErrInfo("材料数量异常");
			player.send(1026, builder.build());
			return;
		}
		List<Warrior> heros = new ArrayList<Warrior>();
		for (Integer id : ids) {
			Warrior hero = (Warrior) player.getBags().getItemById(id.intValue(), 2);
			if (hero == null) {
				builder.setResult(false);
				builder.setErrInfo("武将不存在");
				player.send(1026, builder.build());
				return;
			}
			if (hero.getStageStatus() != 0) {
				builder.setResult(false);
				builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "武将" }));
				player.send(1026, builder.build());
				return;
			}
			if ((hero.getAdvanceLevel() > 0) || (!(((HeroTemplate) hero.getTemplate()).canBreak))) {
				builder.setResult(false);
				builder.setErrInfo(MessageFormat.format("该{0}不能被炼化", new Object[] { "武将" }));
				player.send(1026, builder.build());
				return;
			}
			heros.add(hero);
		}
		int addWarriorSpirit = 0;
		i = 0;
		int addSpiritJade = 0;
		for (Warrior hero : heros) {
			int ruleId = ((HeroTemplate) hero.getTemplate()).intensifyRule;
			int totalExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).calTotalExpByLevel(ruleId,
					hero.getLevel());
			addWarriorSpirit += (int) Math.floor(totalExp / 1.0D + ((HeroTemplate) hero.getTemplate()).initExp);
			i += totalExp + hero.getPrice();
			addSpiritJade += ((HeroTemplate) hero.getTemplate()).breakSpiritJade;
			player.getBags().removeItem(hero.getId(), hero.getTemplateId(), 1, "warriorrefine");
			Platform.getLog().logWarrior(player, hero, "warriorrefine", -1);
		}

		player.addWarriorSpirit(addWarriorSpirit, "warriorrefine");
		player.addMoney(i, "warriorrefine");
		player.addSpiritJade(addSpiritJade, "warriorrefine");

		builder.setResult(true);
		builder.addRewards(Reward.genPbReward(8, addWarriorSpirit).build());
		builder.addRewards(Reward.genPbReward(2, i).build());
		builder.addRewards(Reward.genPbReward(7, addSpiritJade).build());
		player.send(1026, builder.build());
	}

	private void heroReborn(Player player, PbPacket.Packet packet) {

		PbDown.HeroRebornRst.Builder builder = PbDown.HeroRebornRst.newBuilder();
		int id = 0;
		try {
			PbUp.HeroReborn reborn = PbUp.HeroReborn.parseFrom(packet.getData());
			id = reborn.getHeroId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}重生失败", new Object[] { "武将" }));
			player.send(1028, builder.build());
			return;
		}
		Warrior hero = (Warrior) player.getBags().getItemById(id, 2);
		if (hero == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
			player.send(1028, builder.build());
			return;
		}
		if (hero.getStageStatus() != 0) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("请先卸下{0}再进行操作", new Object[] { "武将" }));
			player.send(1026, builder.build());
			return;
		}
		HeroTemplate template = (HeroTemplate) hero.getTemplate();
		if ((template.quality < 5) || (hero.getLevel() < 2)) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("该{0}不能重生", new Object[] { "武将" }));
			player.send(1028, builder.build());
			return;
		}
		int costJewel = template.initRebornCostJewel * (hero.getAdvanceLevel() + 1);
		if (player.getJewels() < costJewel) {
			builder.setResult(false);
			builder.setErrInfo("元宝不足");
			player.send(1028, builder.build());
			return;
		}

		int addWarriorSpirit = 0;
		int addMoney = 0;

		Map addItem = new HashMap();

		int ruleId = ((HeroTemplate) hero.getTemplate()).intensifyRule;
		int totalExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).calTotalExpByLevel(ruleId,
				hero.getLevel());
		addWarriorSpirit += (int) Math.floor(totalExp / 1.0D);
		addMoney += totalExp;

		Map advanceRuleMap = template.advanceRule;
		for (int i = 0; i < hero.getAdvanceLevel(); ++i) {
			HeroAdvance advance = ((HeroService) Platform.getServiceManager().get(HeroService.class))
					.getHeroAdvance(((Integer) advanceRuleMap.get(Integer.valueOf(i))).intValue());
			addMoney += advance.costMoney;
			if (advance.heroNum > 0) {
				Integer count = (Integer) addItem.get(Integer.valueOf(template.id));
				if (count == null) {
					count = new Integer(0);
				}
				count = Integer.valueOf(count.intValue() + 1);
				addItem.put(Integer.valueOf(template.id), count);
			}
			Set<Entry<Integer, Integer>> set = advance.costList.entrySet();
			for (Map.Entry entry : set) {
				int itemTemplateId = ((Integer) entry.getKey()).intValue();
				int num = ((Integer) entry.getValue()).intValue();
				Integer count = (Integer) addItem.get(Integer.valueOf(itemTemplateId));
				if (count == null) {
					count = new Integer(0);
				}
				count = Integer.valueOf(count.intValue() + num);
				addItem.put(Integer.valueOf(itemTemplateId), count);
			}
		}

		if ((addItem.size() > 0) && (player.getBags().getFullBag() != -1)) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
			player.send(1028, builder.build());
			return;
		}

		hero.clear();
		player.addWarriorSpirit(addWarriorSpirit, "warriorreborn");
		player.addMoney(addMoney, "warriorreborn");
		player.decJewels(costJewel, "warriorreborn");
		Platform.getLog().logWarrior(player, hero, "warriorreborn", -1);

		Set<Map.Entry> set = addItem.entrySet();
		for (Map.Entry entry : set) {
			int templateId = ((Integer) entry.getKey()).intValue();
			int num = ((Integer) entry.getValue()).intValue();
			ItemTemplate it = ItemService.getItemTemplate(templateId);
			if (Item.isCumulative(it.type)) {
				Item item = ItemService.generateItem(it, player);
				player.getBags().addItem(item, num, "warriorreborn");
			} else {
				for (int i = 0; i < num; ++i) {
					Item item = ItemService.generateItem(it, player);
					player.getBags().addItem(item, 1, "warriorreborn");
				}
			}
			builder.addRewards(Reward.genPbReward(0, num, templateId).build());
		}
		builder.addRewards(Reward.genPbReward(8, addWarriorSpirit).build());
		builder.addRewards(Reward.genPbReward(2, addMoney).build());
		builder.setResult(true);
		builder.setHero(hero.genWarrior());
		player.send(1028, builder.build());
	}

	private void warriorChange(Player player, PbPacket.Packet packet) {
		PbDown.ChangeWarriorRst.Builder builder = PbDown.ChangeWarriorRst.newBuilder();
		int index = 0;
		int newHeroId = 0;
		try {
			PbUp.ChangeWarrior changeWarrior = PbUp.ChangeWarrior.parseFrom(packet.getData());
			index = changeWarrior.getIndex();
			newHeroId = changeWarrior.getHeroId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("更换武将失败");
			player.send(1032, builder.build());
			return;
		}
		Warrior warrior = (Warrior) player.getBags().getItemById(newHeroId, 2);
		if ((index < 2) || (index > player.getWarriors().getStands().length)) {
			builder.setResult(false);
			builder.setErrInfo("更换武将失败");
		} else if (warrior == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
		} else if (Formation.getOpenLevelByIndex(index) > player.getLevel()) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}级开启",
					new Object[] { Integer.valueOf(Formation.getOpenLevelByIndex(index)) }));
		} else if (warrior.getStageStatus() == 1) {
			builder.setResult(false);
			builder.setErrInfo("该武将已上阵");
		} else if (warrior.getStageStatus() == 2) {
			builder.setResult(false);
			builder.setErrInfo("该武将已是副将");
		} else if (player.getWarriors().isSameWarriorOnStage(warrior.getTemplateId()) != 0) {
			builder.setResult(false);
			builder.setErrInfo("已有该武将上阵或作为副将");
		} else {
			Warrior old = player.getWarriors().addWarrior(warrior, index);
			int standIndex = player.getWarriors().getStandIndex(warrior);
			if (old != null) {
				Equipment[] equips = old.getEquips().getEquips();
				for (Equipment equip : equips) {
					if (equip != null) {
						warrior.getEquips().equip(equip, warrior);
					}
				}
				Treasure[] treasures = old.getTreasures().getTreasures();
				for (Treasure treasure : treasures) {
					if (treasure != null) {
						warrior.getTreasures().equip(treasure, warrior);
					}
				}
				old.refreshkEns(new Warrior[] { old });
				player.getTacticRecord().removeBuff(old, standIndex);
				player.getGloryRecord().removeBuff(old, standIndex);
				old.refreshAttributes(true);
			}
			Platform.getLog().logStage(player, warrior.getId(), (old == null) ? -1 : old.getId());
			player.getTacticRecord().addBuff(warrior, standIndex);
			player.getGloryRecord().addBuff(warrior, standIndex);
			player.getWarriors().refresh(true);
			player.getDataSyncManager().addStandsSync(1, player.getWarriors());
			player.getDataSyncManager().addStandsSync(2, player.getWarriors());

			Platform.getEventManager().addEvent(new Event(2023, new Object[] { player }));

			builder.setResult(true);
			builder.setIndex(player.getWarriors().getStageIndex(warrior));
			builder.setHero(warrior.genWarrior());
		}
		player.send(1032, builder.build());
	}

	private void fellowChange(Player player, PbPacket.Packet packet) {
		PbDown.ChangeFellowRst.Builder builder = PbDown.ChangeFellowRst.newBuilder();
		int index = 0;
		int newHeroId = 0;
		try {
			PbUp.ChangeFellow change = PbUp.ChangeFellow.parseFrom(packet.getData());
			index = change.getIndex();
			newHeroId = change.getHeroId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("更换副将失败");
			player.send(1044, builder.build());
			return;
		}
		Warrior warrior = (Warrior) player.getBags().getItemById(newHeroId, 2);
		if ((index < 1) || (index > Formation.openFriendByLevel.size())) {
			builder.setResult(false);
			builder.setErrInfo("更换副将失败");
		} else if (warrior == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
		} else if (Formation.getOpenLevelByFriendIndex(index) > player.getLevel()) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("{0}级开启",
					new Object[] { Integer.valueOf(Formation.getOpenLevelByFriendIndex(index)) }));
		} else if (warrior.getStageStatus() == 1) {
			builder.setResult(false);
			builder.setErrInfo("该武将已上阵");
		} else if (warrior.getStageStatus() == 2) {
			builder.setResult(false);
			builder.setErrInfo("该武将已是副将");
		} else if (player.getWarriors().isSameWarriorOnStage(warrior.getTemplateId()) != 0) {
			builder.setResult(false);
			builder.setErrInfo("已有该武将上阵或作为副将");
		} else {
			Warrior old = player.getWarriors().addFriend(warrior, index);
			if (old != null) {
				old.refreshkEns(new Warrior[] { old });
				old.refreshAttributes(true);
			}
			player.getWarriors().refresh(true);
			player.getDataSyncManager().addStandsSync(3, player.getWarriors());
			Platform.getLog().logFellow(player, warrior.getId(), (old == null) ? -1 : old.getId());
			builder.setResult(true);
			builder.setIndex(player.getWarriors().getStageIndex(warrior));
			builder.setHero(warrior.genWarrior());

			Platform.getEventManager().addEvent(new Event(2048, new Object[] { player }));
		}
		player.send(1044, builder.build());
	}

	private void fellowDownStage(Player player, PbPacket.Packet packet) {
		PbDown.DownFellowRst.Builder b = PbDown.DownFellowRst.newBuilder();
		int index = 0;
		try {
			PbUp.DownFellow change = PbUp.DownFellow.parseFrom(packet.getData());
			index = change.getIndex();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1286, b.build());
			return;
		}
		Warrior fellow = (Warrior) player.getWarriors().getFriends().get(Integer.valueOf(index));
		if (fellow == null) {
			b.setResult(false);
			b.setErrInfo("武将不存在");
		} else {
			player.getWarriors().downFriend(fellow);
			if (fellow != null) {
				fellow.refreshkEns(new Warrior[] { fellow });
				fellow.refreshAttributes(true);
			}
			player.getWarriors().refresh(true);
			player.getDataSyncManager().addStandsSync(3, player.getWarriors());
			Platform.getLog().logFellow(player, -1, fellow.getId());
			b.setResult(true);
			Platform.getEventManager().addEvent(new Event(2083, new Object[] { player }));
		}

		player.send(1286, b.build());
	}

	private void getWarriorNextLevelInfo(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 39))) {
			return;
		}
		PbDown.WarriorNextLevelInfoRst.Builder builder = PbDown.WarriorNextLevelInfoRst.newBuilder();
		int heroId = 0;
		try {
			PbUp.WarriorNextLevelInfo info = PbUp.WarriorNextLevelInfo.parseFrom(packet.getData());
			heroId = info.getHeroId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("获取信息失败");
			player.send(1038, builder.build());
			return;
		}

		Warrior warrior = (Warrior) ItemService.getItem(player, heroId, 2);
		if (warrior == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
		} else if (warrior.getLevel() == 200) {
			builder.setResult(false);
			builder.setErrInfo("武将已达最大等级");
		} else {
			Attributes attr = new Attributes();
			warrior.calAttributes(attr, warrior.getLevel() + 1, warrior.getAdvanceLevel());
			int restExp = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getRestExpToNextLevel(
					((HeroTemplate) warrior.getTemplate()).intensifyRule, warrior.getExp(), warrior.getLevel() + 1);
			builder.setAttribute(attr.genAttribute());
			builder.setWarriorSpirit(restExp);
			builder.setMoney(restExp);
			builder.setResult(true);
		}
		player.send(1038, builder.build());
	}

	private void getWarriorNextAdvanceLevelInfo(Player player, PbPacket.Packet packet) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 2))) {
			return;
		}
		PbDown.WarriorNextAdvanceLevelInfoRst.Builder builder = PbDown.WarriorNextAdvanceLevelInfoRst.newBuilder();
		int heroId = 0;
		try {
			PbUp.WarriorNextAdvanceLevelInfo info = PbUp.WarriorNextAdvanceLevelInfo.parseFrom(packet.getData());
			heroId = info.getHeroId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("获取信息失败");
			player.send(1062, builder.build());
			return;
		}
		Warrior warrior = (Warrior) ItemService.getItem(player, heroId, 2);
		if (warrior == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
		} else {
			int canAdvanceMaxLevel = ((HeroTemplate) warrior.getTemplate()).advanceRule.size();
			if (canAdvanceMaxLevel <= 0) {
				builder.setResult(false);
				builder.setErrInfo("该武将不可突破");
			} else if (warrior.getAdvanceLevel() >= canAdvanceMaxLevel) {
				builder.setResult(false);
				builder.setErrInfo("已达到最大突破等级");
			} else {
				int advanceRuleId = ((Integer) ((HeroTemplate) warrior.getTemplate()).advanceRule
						.get(Integer.valueOf(warrior.getAdvanceLevel()))).intValue();
				HeroAdvance rule = getHeroAdvance(advanceRuleId);
				Map costList = rule.costList;
				Set<Map.Entry> set = costList.entrySet();
				for (Map.Entry entry : set) {
					int templateId = ((Integer) entry.getKey()).intValue();
					int num = ((Integer) entry.getValue()).intValue();
					builder.addCosts(Reward.genPbReward(0, num, templateId));
				}
				if (rule.heroNum > 0) {
					builder.addCosts(Reward.genPbReward(0, rule.heroNum, warrior.getTemplateId()));
				}
				builder.setMoney(rule.costMoney);
				Attributes attr = new Attributes();
				warrior.calAttributes(attr, warrior.getLevel(), warrior.getAdvanceLevel() + 1);
				builder.setAttribute(attr.genAttribute());
				builder.setResult(true);
			}
		}
		player.send(1062, builder.build());
	}

	private void updateStands(Player player, PbPacket.Packet packet) {
		List<PbStandStruct.StandUnit> list;
		int tacticId;
		int standIndex;
		Warrior warrior;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 3))) {
			return;
		}
		PbDown.UpdateStandsRst.Builder builder = PbDown.UpdateStandsRst.newBuilder();
		try {
			PbUp.UpdateStands updateStage = PbUp.UpdateStands.parseFrom(packet.getData());
			PbStandStruct.StandStruct standStruct = updateStage.getStands();
			list = standStruct.getStandsList();
			tacticId = updateStage.getTacticId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("调整布阵失败");
			player.send(1046, builder.build());
			return;
		}
		Map map = new HashMap();
		for (PbStandStruct.StandUnit standUnit : list) {
			int id = standUnit.getId();
			standIndex = standUnit.getStandIndex();
			warrior = player.getWarriors().getWarriorById(id);
			if ((warrior == null) || (!(Formation.isOpenByPosition(standIndex, player.getLevel())))
					|| (map.containsValue(warrior))) {
				builder.setResult(false);
				builder.setErrInfo("调整布阵失败");
				player.send(1046, builder.build());
				return;
			}
			map.put(Integer.valueOf(standIndex), warrior);
		}
		if (map.size() != player.getWarriors().getWarriorsCount()) {
			builder.setResult(false);
			builder.setErrInfo("调整布阵失败");
			player.send(1046, builder.build());
			return;
		}
		TacticRecord tr = player.getTacticRecord();
		if ((tacticId != -1) && (!(tr.getTactics().containsKey(Integer.valueOf(tacticId))))) {
			builder.setResult(false);
			builder.setErrInfo("尚未领悟该阵法， 无法选择");
			player.send(1046, builder.build());
			return;
		}

		player.getGloryRecord().unEffectAll(player);

		Warrior[] stands = player.getWarriors().getStands();
		for (int i = 0; i < stands.length; ++i) {
			stands[i] = null;
		}
		Iterator itx = map.keySet().iterator();
		while (itx.hasNext()) {
			standIndex = ((Integer) itx.next()).intValue();
			warrior = (Warrior) map.get(Integer.valueOf(standIndex));
			stands[standIndex] = warrior;
		}
		Platform.getLog().logStage(player, -1, -1);
		player.getDataSyncManager().addStandsSync(2, player.getWarriors());

		player.getGloryRecord().effectAll(player);
		Platform.getLog().logTactic(player, "tacticselect");
		builder.setResult(true);
		player.send(1046, builder.build());
	}

	private void equip(Player player, PbPacket.Packet packet) {
		PbDown.EquipRst.Builder builder = PbDown.EquipRst.newBuilder();
		builder.setResult(true);
		PbUp.Equip.optType type = PbUp.Equip.optType.Single;
		int heroId = 0;
		int equipId = 0;
		try {
			PbUp.Equip equip = PbUp.Equip.parseFrom(packet.getData());
			type = equip.getType();
			heroId = equip.getHeroId();
			equipId = equip.getEquipId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("装备失败");
			player.send(1058, builder.build());
			return;
		}
		Warrior hero = (Warrior) ItemService.getItem(player, heroId, 2);
		if (hero == null) {
			builder.setResult(false);
			builder.setErrInfo("武将不存在");
		} else if (hero.getStageStatus() != 1) {
			builder.setResult(false);
			builder.setErrInfo("该武将未上阵");
		}
		if (builder.getResult()) {
			if (type == PbUp.Equip.optType.Single) {
				Item item = player.getBags().getItemById(equipId);
				if (item == null) {
					builder.setResult(false);
					builder.setErrInfo("装备不存在");
				} else {
					int oldWarriorId;
					Warrior[] stageWarriors;
					Warrior oldWarrior;
					if (item.getItemType() == 4) {
						Equipment equipment = (Equipment) item;
						oldWarriorId = equipment.getWarriorId();
						hero.getEquips().equip(equipment, hero);
						stageWarriors = player.getWarriors().getAllWarriorAndFellow();
						if (oldWarriorId > 0) {
							oldWarrior = player.getWarriors().getWarriorById(oldWarriorId);
							if (oldWarrior != null) {
								oldWarrior.refreshkEns(stageWarriors);
								oldWarrior.refreshAttributes(true);
							}
						}
						hero.refreshkEns(stageWarriors);
						hero.refreshAttributes(true);
						builder.setResult(true);
					} else if (item.getItemType() == 1) {
						Treasure treasure = (Treasure) item;
						oldWarriorId = treasure.getWarriorId();
						hero.getTreasures().equip(treasure, hero);
						stageWarriors = player.getWarriors().getAllWarriorAndFellow();
						if (oldWarriorId > 0) {
							oldWarrior = player.getWarriors().getWarriorById(oldWarriorId);
							if (oldWarrior != null) {
								oldWarrior.refreshkEns(stageWarriors);
								oldWarrior.refreshAttributes(true);
							}
						}
						hero.refreshkEns(stageWarriors);
						hero.refreshAttributes(true);
						builder.setResult(true);
					} else {
						builder.setResult(false);
						builder.setErrInfo("装备失败");
					}
				}
			} else if (type == PbUp.Equip.optType.All) {
				Equipments equips = hero.getEquips();
				equips.unAllEquip();
				List list = new ArrayList(player.getBags().getBag(4).getGrids());
				Collections.sort(list, new Comparator<BagGrid>() {
					public int compare(BagGrid o1, BagGrid o2) {
						Equipment equip1 = (Equipment) o1.getItem();
						Equipment equip2 = (Equipment) o2.getItem();
						if (equip1.getEquipType() == equip2.getEquipType()) {
							if (equip1.getTemplate().quality < equip2.getTemplate().quality)
								return 1;
							if (equip1.getTemplate().quality == equip2.getTemplate().quality) {
								return (equip2.getLevel() - equip1.getLevel());
							}
							return -1;
						}

						return (equip1.getEquipType() - equip2.getEquipType());
					}
				});
				for (int i = 0; i < equips.getEquips().length; ++i) {
					for (int j = 0; j < list.size(); ++j) {
						Equipment equip = (Equipment) ((BagGrid) list.get(j)).getItem();
						if ((equip.getEquipType() == i + 1) && (equip.getWarriorId() == 0)) {
							equips.equip(equip, hero);
							break;
						}
					}
				}

				Treasures treasures = hero.getTreasures();
				treasures.unAllEquip();
				List list2 = new ArrayList(player.getBags().getBag(1).getGrids());
				Collections.sort(list2, new Comparator<BagGrid>() {
					public int compare(BagGrid o1, BagGrid o2) {
						Treasure t1 = (Treasure) o1.getItem();
						Treasure t2 = (Treasure) o2.getItem();
						if (t1.getTreasureType() == t2.getTreasureType()) {
							if (t1.getTemplate().quality < t2.getTemplate().quality)
								return 1;
							if (t1.getTemplate().quality == t2.getTemplate().quality) {
								return (t2.getLevel() - t1.getLevel());
							}
							return -1;
						}

						return (t1.getTreasureType() - t2.getTreasureType());
					}
				});
				for (int i = 0; i < treasures.getTreasures().length; ++i) {
					for (int j = 0; j < list2.size(); ++j) {
						Treasure treasure = (Treasure) ((BagGrid) list2.get(j)).getItem();
						if ((treasure.getTreasureType() == i + 1) && (treasure.getWarriorId() == 0)) {
							treasures.equip(treasure, hero);
							break;
						}
					}
				}
				Warrior[] stageWarriors = player.getWarriors().getAllWarriorAndFellow();
				hero.refreshkEns(stageWarriors);
				hero.refreshAttributes(true);
				builder.setResult(true);
			} else {
				builder.setResult(false);
				builder.setErrInfo("装备失败");
			}
		}
		player.send(1058, builder.build());
	}

	private void unEquip(Player player, PbPacket.Packet packet) {
		PbDown.UnEquipRst.Builder builder = PbDown.UnEquipRst.newBuilder();
		builder.setResult(true);
		int equipId = 0;
		try {
			PbUp.UnEquip unEquip = PbUp.UnEquip.parseFrom(packet.getData());
			equipId = unEquip.getEquipId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("卸下装备失败");
			player.send(1060, builder.build());
			return;
		}
		Item item = player.getBags().getItemById(equipId);
		if (item == null) {
			builder.setResult(false);
			builder.setErrInfo("装备不存在");
		} else {
			Warrior hero;
			Warrior[] stageWarriors;
			if (item.getItemType() == 4) {
				Equipment equipment = (Equipment) item;
				if (equipment.getWarriorId() == 0) {
					builder.setResult(false);
					builder.setErrInfo("该装备已在背包里");
				} else {
					hero = player.getWarriors().getWarriorById(equipment.getWarriorId());
					if (hero == null) {
						builder.setResult(false);
						builder.setErrInfo("该装备已在背包里");
					} else {
						hero.getEquips().unEquip(equipment, hero);
						stageWarriors = player.getWarriors().getAllWarriorAndFellow();
						hero.refreshkEns(stageWarriors);
						hero.refreshAttributes(true);
						builder.setResult(true);
					}
				}
			} else if (item.getItemType() == 1) {
				Treasure treasure = (Treasure) item;
				if (treasure.getWarriorId() == 0) {
					builder.setResult(false);
					builder.setErrInfo("该宝物已在背包里");
				} else {
					hero = player.getWarriors().getWarriorById(treasure.getWarriorId());
					if (hero == null) {
						builder.setResult(false);
						builder.setErrInfo("该宝物已在背包里");
					} else {
						hero.getTreasures().unEquip(treasure, hero);
						stageWarriors = player.getWarriors().getAllWarriorAndFellow();
						hero.refreshkEns(stageWarriors);
						hero.refreshAttributes(true);
						builder.setResult(true);
					}
				}
			} else {
				builder.setResult(false);
				builder.setErrInfo("装备不存在");
			}
		}
		player.send(1060, builder.build());
	}

	private void gloryUsedInfo(Player player) {
		PbDown.GloryUsedInfoRst.Builder b = PbDown.GloryUsedInfoRst.newBuilder().setResult(true);
		GloryRecord gr = player.getGloryRecord();
		b.addAllGroup(gr.getGloryGroups());
		player.send(1300, b.build());
	}

	public HeroAdvance getHeroAdvance(int id) {
		return ((HeroAdvance) this.heroAdvances.get(Integer.valueOf(id)));
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
