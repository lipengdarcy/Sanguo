package org.darcy.sanguo.loottreasure;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class LootTreasureService implements Service, PacketHandler {
	public Map<Integer, DebrisOwn> debrisOwn = new HashMap<Integer, DebrisOwn>();

	public DebrisOwn getDebrisOwn(int templateId) {
		DebrisOwn own = (DebrisOwn) this.debrisOwn.get(Integer.valueOf(templateId));
		if (own == null) {
			own = new DebrisOwn(templateId);
			this.debrisOwn.put(Integer.valueOf(templateId), own);
			Platform.getEntityManager().putInEhCache(DebrisOwn.class.getName(), Integer.valueOf(templateId), own);
		}
		return own;
	}

	public void startup() throws Exception {
		initDebrisOwn();
		loadLootData();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1099, 1101, 1105, 1107, 1135, 1305 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		if (!(FunctionService.isOpenFunction(player.getLevel(), 10))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1099:
			shield(player, packet);
			break;
		case 1101:
			lootInfo(player);
			break;
		case 1105:
			lootTargetList(player, packet);
			break;
		case 1107:
			lootTreasure(player, packet);
			break;
		case 1135:
			isNaturalShield(player);
			break;
		case 1305:
			lootTreasureTen(player, packet);
		}
	}

	private void initDebrisOwn() {
		DebrisOwn own;
		List<?> list = Platform.getEntityManager().getAllFromEhCache(DebrisOwn.class.getName());
		for (Iterator<?> localIterator = list.iterator(); localIterator.hasNext();) {
			Object object = localIterator.next();
			own = (DebrisOwn) object;
			this.debrisOwn.put(Integer.valueOf(own.getTemplateId()), own);
		}
		Collection<ItemTemplate> templates = ItemService.templates.values();
		for (ItemTemplate template : templates)
			if (template.type == 3) {
				DebrisTemplate debrisTemplate = (DebrisTemplate) template;
				if (((debrisTemplate.debrisType != 4) && (debrisTemplate.debrisType != 3))
						|| (this.debrisOwn.containsKey(Integer.valueOf(debrisTemplate.id))))
					continue;
				own = new DebrisOwn(debrisTemplate.id);
				this.debrisOwn.put(Integer.valueOf(debrisTemplate.id), own);
				Platform.getEntityManager().putInEhCache(DebrisOwn.class.getName(), Integer.valueOf(debrisTemplate.id),
						own);
			}
	}

	private void loadLootData() {
		int pos = 0;
		List<Row> list = ExcelUtils.getRowList("loot.xls", 2);
		for (Row row : list) {

			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			++pos;
			int costStamina = (int) row.getCell(pos++).getNumericCellValue();
			String baseTreasure = row.getCell(pos++).getStringCellValue();
			String shieldItem = row.getCell(pos++).getStringCellValue();
			int shieldJewel = (int) row.getCell(pos++).getNumericCellValue();
			int shieldTime = (int) row.getCell(pos++).getNumericCellValue();
			int maxShieldTime = (int) row.getCell(pos++).getNumericCellValue();
			int winMoneyRatio = (int) row.getCell(pos++).getNumericCellValue();
			int winExpRatio = (int) row.getCell(pos++).getNumericCellValue();
			int loseMoneyRatio = (int) row.getCell(pos++).getNumericCellValue();
			int loseExpRatio = (int) row.getCell(pos++).getNumericCellValue();
			String notLootPlayer = row.getCell(pos++).getStringCellValue();

			LootTreasureData.costStamina = costStamina;
			String[] strs = baseTreasure.split(",");
			LootTreasureData.baseTreasure = new int[strs.length];
			for (int i = 0; i < strs.length; ++i) {
				LootTreasureData.baseTreasure[i] = Integer.valueOf(strs[i]).intValue();
			}
			LootTreasureData.shieldItem = new Reward(shieldItem);
			LootTreasureData.shieldJewel = new Reward(3, shieldJewel, null);
			LootTreasureData.shieldTime = shieldTime;
			LootTreasureData.maxShieldTime = maxShieldTime;
			LootTreasureData.winMoneyRatio = winMoneyRatio;
			LootTreasureData.winExpRatio = winExpRatio;
			LootTreasureData.loseMoneyRatio = loseMoneyRatio;
			LootTreasureData.loseExpRatio = loseExpRatio;
			LootTreasureData.notLootPlayer = Calc.split(notLootPlayer, ",");
		}

		List<Row> list2 = ExcelUtils.getRowList("loot.xls", 2, 1);
		for (Row row : list2) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int ratio = (int) row.getCell(pos++).getNumericCellValue();
			String desc = row.getCell(pos++).getStringCellValue();
			LootTreasureData.LootDrop drop = new LootTreasureData.LootDrop(id, ratio, desc);
			LootTreasureData.drops.put(Integer.valueOf(id), drop);
		}
	}

	private void shield(Player player, PbPacket.Packet packet) {
		PbUp.LootTreasureShield.Type type;
		PbDown.LootTreasureShieldRst.Builder builder = PbDown.LootTreasureShieldRst.newBuilder();
		builder.setResult(true);
		try {
			PbUp.LootTreasureShield lootTreasureShield = PbUp.LootTreasureShield.parseFrom(packet.getData());
			type = lootTreasureShield.getType();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("免战失败");
			player.send(1100, builder.build());
			return;
		}

		if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else {
			Reward cost;
			String err;
			if (type == PbUp.LootTreasureShield.Type.ITEM)
				cost = LootTreasureData.shieldItem;
			else {
				cost = LootTreasureData.shieldJewel;
			}

			if ((err = cost.check(player)) != null) {
				builder.setResult(false);
				builder.setErrInfo(err);
			} else {
				LootTreasure.shield(player);
				cost.remove(player, "shield");
				builder.setResult(true);
				builder.setShieldTime(LootTreasure.getRestShieldTime(player));
			}
		}
		player.send(1100, builder.build());
	}

	private void lootInfo(Player player) {
		PbDown.LootTreasureInfoRst.Builder builder = PbDown.LootTreasureInfoRst.newBuilder();
		builder.setResult(true);
		builder.setShieldTime(LootTreasure.getRestShieldTime(player));
		Set<Integer> set = player.getLootTreasure().getCanCompoundCount().keySet();
		for (Integer id : set) {
			int count = ((Integer) player.getLootTreasure().getCanCompoundCount().get(id)).intValue();
			if (count > 0) {
				builder.addCanLootIds(id.intValue());
				builder.addCounts(count);
			}
		}
		player.send(1102, builder.build());
	}

	private void lootTargetList(Player player, PbPacket.Packet packet) {
		int debrisId;
		PbDown.LootTreasurePlayerListRst.Builder builder = PbDown.LootTreasurePlayerListRst.newBuilder();
		try {
			PbUp.LootTreasurePlayerList list = PbUp.LootTreasurePlayerList.parseFrom(packet.getData());
			debrisId = list.getDebrisId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("获取信息失败");
			player.send(1106, builder.build());
			return;
		}
		ItemTemplate template = ItemService.getItemTemplate(debrisId);
		if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (template == null) {
			builder.setResult(false);
			builder.setErrInfo("你不要捣乱了啊，没有这个碎片");
		} else if (player.getBags().getItemCount(debrisId) > 0) {
			builder.setResult(false);
			builder.setErrInfo("你已经拥有了该碎片，无法抢夺");
		} else if (template.type != 3) {
			builder.setResult(false);
			builder.setErrInfo("你抢夺的不是碎片,想捣乱是吧");
		} else {
			DebrisTemplate debrisTemplate = (DebrisTemplate) template;
			Integer canCompoundCount = (Integer) player.getLootTreasure().getCanCompoundCount()
					.get(Integer.valueOf(debrisTemplate.getObjectTemplateId()));
			if ((canCompoundCount == null) || (canCompoundCount.intValue() < 1)) {
				builder.setResult(false);
				builder.setErrInfo("该宝物的可合成次数为0，不能抢夺");
			} else {
				List<Integer> resultIds = new ArrayList<Integer>();
				if (LootTreasureData.canLoot()) {
					DebrisOwn own = getDebrisOwn(debrisId);
					if (own.getOwners().size() > 0) {
						int canPickCount = Math.min(own.getOwners().size(), 50);
						Integer[] subs = Calc.randomGet(own.getOwners().size(), canPickCount);
						for (Integer sub : subs) {
							int playerId = ((Integer) own.getOwners().get(sub.intValue())).intValue();
							if (playerId != player.getId()) {
								ShieldInfo info = (ShieldInfo) Platform.getEntityManager()
										.getFromEhCache(ShieldInfo.class.getName(), Integer.valueOf(playerId));
								if ((info != null) && (!(info.isShield()))) {
									resultIds.add(Integer.valueOf(playerId));
								}

							}

						}

					}

				}

				LootTargetsAsyncCall call = new LootTargetsAsyncCall(player, resultIds, debrisId);
				Platform.getThreadPool().execute(call);
				return;
			}

		}

		player.send(1106, builder.build());
	}

	private void lootTreasure(Player player, PbPacket.Packet packet) {
		int debrisId;
		int playerId;
		PbDown.LootTreasureRst.Builder builder = PbDown.LootTreasureRst.newBuilder();
		builder.setResult(true);
		try {
			PbUp.LootTreasure lootTreasure = PbUp.LootTreasure.parseFrom(packet.getData());
			debrisId = lootTreasure.getDebrisId();
			playerId = lootTreasure.getPlayerId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("夺宝失败");
			player.send(1108, builder.build());
			return;
		}
		List<?> list = (List<?>) player.getLootTreasure().rivalIds.get(Integer.valueOf(debrisId));
		if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (player.getBags().getItemCount(debrisId) > 0) {
			builder.setResult(false);
			builder.setErrInfo("你已经拥有了该碎片，无法抢夺");
		} else if ((list == null) || ((!(list.contains(Integer.valueOf(playerId))))
				&& (!(player.getLootTreasure().robots.containsKey(Integer.valueOf(playerId)))))) {
			builder.setResult(false);
			builder.setErrInfo("你不能抢夺该玩家");
		} else if (player.getStamina() < LootTreasureData.costStamina) {
			builder.setResult(false);
			builder.setErrInfo("精力不足");
		} else {
			DebrisTemplate debrisTemplate = (DebrisTemplate) ItemService.getItemTemplate(debrisId);
			Integer canCompoundCount = (Integer) player.getLootTreasure().getCanCompoundCount()
					.get(Integer.valueOf(debrisTemplate.getObjectTemplateId()));
			if ((canCompoundCount == null) || (canCompoundCount.intValue() < 1)) {
				builder.setResult(false);
				builder.setErrInfo("该宝物的可合成次数为0，不能抢夺");
			} else {
				LootTreasureAsyncCall call = new LootTreasureAsyncCall(player, debrisId, playerId);
				Platform.getThreadPool().execute(call);
				return;
			}
		}
		player.send(1108, builder.build());
	}

	private void lootTreasureTen(Player player, PbPacket.Packet packet) {
		int debrisId;
		int playerId;
		PbDown.LootTreasureTenRst.Builder builder = PbDown.LootTreasureTenRst.newBuilder();
		builder.setResult(true);
		try {
			PbUp.LootTreasureTen lootTreasure = PbUp.LootTreasureTen.parseFrom(packet.getData());
			debrisId = lootTreasure.getDebrisId();
			playerId = lootTreasure.getPlayerId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("夺宝失败");
			player.send(1306, builder.build());
			return;
		}
		if ((player.getLevel() < 30) && (player.getVip().level < 3)) {
			builder.setResult(false);
			builder.setErrInfo(MessageFormat.format("主公等级达{0}级或VIP等级达{1}级才可进行此操作",
					new Object[] { Integer.valueOf(30), Integer.valueOf(3) }));
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else if (player.getBags().getItemCount(debrisId) > 0) {
			builder.setResult(false);
			builder.setErrInfo("你已经拥有了该碎片，无法抢夺");
		} else if (!(player.getLootTreasure().robots.containsKey(Integer.valueOf(playerId)))) {
			builder.setResult(false);
			builder.setErrInfo("你不能抢夺该玩家");
		} else if (player.getStamina() < LootTreasureData.costStamina) {
			builder.setResult(false);
			builder.setErrInfo("精力不足");
		} else {
			DebrisTemplate debrisTemplate = (DebrisTemplate) ItemService.getItemTemplate(debrisId);
			Integer canCompoundCount = (Integer) player.getLootTreasure().getCanCompoundCount()
					.get(Integer.valueOf(debrisTemplate.getObjectTemplateId()));
			if ((canCompoundCount == null) || (canCompoundCount.intValue() < 1)) {
				builder.setResult(false);
				builder.setErrInfo("该宝物的可合成次数为0，不能抢夺");
			} else {
				DebrisTemplate dt = (DebrisTemplate) ItemService.getItemTemplate(debrisId);
				int dropType = dt.lootDropNpc;
				LootTreasureData.LootDrop drop = (LootTreasureData.LootDrop) LootTreasureData.drops
						.get(Integer.valueOf(dropType));
				int count = 0;
				boolean isLoot = false;
				List<Reward> turnCards = new ArrayList<Reward>();
				for (int i = 0; i < 10; ++i) {
					int costStamina = (count + 1) * LootTreasureData.costStamina;
					if (player.getStamina() < costStamina) {
						break;
					}

					++count;
					Platform.getEventManager().addEvent(new Event(2015, new Object[] { player }));

					Reward r = player.getGlobalDrop().dropTurnCardReward(player);
					if (r != null) {
						turnCards.add(r);
					}

					if (Math.random() < drop.ratio / 10000.0D) {
						Item addDebris = ItemService.generateItem(debrisId, player);
						player.getBags().addItem(addDebris, 1, "loottreasure");
						player.getLootTreasure().removeCompoundCount(dt.getObjectTemplateId());

						isLoot = true;
						break;
					}

				}

				int costStamina = LootTreasureData.costStamina * count;
				int addExp = LootTreasureData.winExpRatio * player.getLevel() * count;
				int addMoney = LootTreasureData.winMoneyRatio * player.getLevel() * count;

				player.decStamina(costStamina, "loottreasure");
				Platform.getEventManager()
						.addEvent(new Event(2018, new Object[] { player, Integer.valueOf(costStamina) }));
				player.addExp(addExp, "loottreasure");
				player.addMoney(addMoney, "loottreasure");

				builder.setMoney(addMoney);
				builder.setExp(addExp);
				builder.setIsLoot(isLoot);
				builder.setCount(count);
				turnCards = Reward.mergeReward(turnCards);
				for (Reward r : turnCards) {
					builder.addRewards(r.genPbReward());
				}
			}
		}
		player.send(1306, builder.build());
	}

	private void isNaturalShield(Player player) {
		PbDown.LootTreasureNaturalShieldRst rst = PbDown.LootTreasureNaturalShieldRst.newBuilder().setResult(true)
				.setIsNatural(!(LootTreasureData.canLoot())).build();
		player.send(1136, rst);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
