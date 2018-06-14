package org.darcy.sanguo.loottreasure;

import java.text.MessageFormat;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerSaveCall;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.MapService;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbDown;
import sango.packet.PbGlobalDrop;

public class LootTreasureAsyncCall extends AsyncCall {
	Player player;
	int debrisId;
	int objId;
	boolean isRobot;
	Player objPlayer;
	int errorNum = 0;

	public LootTreasureAsyncCall(Player player, int debrisId, int objId) {
		super(player.getSession(), null);
		this.player = player;
		this.debrisId = debrisId;
		this.objId = objId;
	}

	public void callback() {
		PbDown.LootTreasureRst.Builder builder = PbDown.LootTreasureRst.newBuilder();
		builder.setResult(true);
		if (this.errorNum == 0) {
			if (!(this.isRobot)) {
				if (LootTreasure.isShield(this.objPlayer)) {
					builder.setResult(false);
					builder.setErrInfo("该玩家正处于免战状态，不能抢夺");
				} else {
					List list = ((LootTreasureService) Platform.getServiceManager().get(LootTreasureService.class))
							.getDebrisOwn(this.debrisId).getOwners();
					if (!(list.contains(Integer.valueOf(this.objId)))) {
						builder.setResult(false);
						builder.setErrInfo("该玩家不能被抢夺");
					}
				}
			}
			if (builder.getResult()) {
				int addExp;
				int addMoney;
				MapTemplate mt = MapService.getSpecialMapTemplate(8);
				LootTreasureStage stage = new LootTreasureStage(mt.stageTemplates[0], this.player, this.objPlayer);
				stage.init();
				stage.combat(this.player);
				boolean result = stage.isWin();
				if (!(this.isRobot)) {
					LootTreasure.removeShield(this.player);
				}

				if (result) {
					PbGlobalDrop.TurnCard.Builder b = this.player.getGlobalDrop().dropTurnCard(this.player,
							this.objPlayer);
					DebrisTemplate template = (DebrisTemplate) ItemService.getItemTemplate(this.debrisId);
					int dropType = 0;
					if (this.isRobot)
						dropType = template.lootDropNpc;
					else {
						dropType = template.lootDropPlayer;
					}
					LootTreasureData.LootDrop drop = (LootTreasureData.LootDrop) LootTreasureData.drops
							.get(Integer.valueOf(dropType));
					if (Math.random() < drop.ratio / 10000.0D) {
						Item addDebris = ItemService.generateItem(this.debrisId, this.player);
						this.player.getBags().addItem(addDebris, 1, "loottreasure");
						this.player.getLootTreasure().removeCompoundCount(template.getObjectTemplateId());

						if (!(this.isRobot)) {
							this.objPlayer.getBags().removeItem(0, this.debrisId, 1, "loottreasure");
							if (this.objPlayer.getSession() == null) {
								PlayerSaveCall objPlayerSave = new PlayerSaveCall(this.objPlayer);
								Platform.getThreadPool().execute(objPlayerSave);
							}
							StringBuffer sb = new StringBuffer();
							sb.append(addDebris.getName()).append("x1 ");
							if ((b.hasMoney()) && (b.getMoney() > 0)) {
								sb.append("银币").append(b.getMoney());
							}
							MailService.sendSystemMail(11, this.objPlayer.getId(), "宝物碎片被抢夺", MessageFormat.format(
									"<p style=19>{0}</p><p style=21>在夺宝中成功掠夺你的</p><p style=19>{1}</p><p style=21>！！主公下回可要保护好自己的宝物！！</p>",
									new Object[] { this.player.getName(), sb.toString() }));
						}
						builder.setIsLoot(true);
					} else {
						builder.setIsLoot(false);
						StringBuffer sb = new StringBuffer();
						if ((b.hasMoney()) && (b.getMoney() > 0)) {
							sb.append("银币").append(b.getMoney());
							MailService.sendSystemMail(11, this.objPlayer.getId(), "宝物碎片被抢夺", MessageFormat.format(
									"<p style=19>{0}</p><p style=21>在夺宝中成功掠夺你的</p><p style=19>{1}</p><p style=21>！！主公下回可要保护好自己的宝物！！</p>",
									new Object[] { this.player.getName(), sb.toString() }));
						}
					}
					addExp = LootTreasureData.winExpRatio * this.player.getLevel();
					addMoney = LootTreasureData.winMoneyRatio * this.player.getLevel();
					builder.setTurnCard(b);

					Platform.getEventManager().addEvent(new Event(2015, new Object[] { this.player }));
				} else {
					addExp = LootTreasureData.loseExpRatio * this.player.getLevel();
					addMoney = LootTreasureData.loseMoneyRatio * this.player.getLevel();
				}
				builder.setIsWin(result);
				builder.setStageRecord(stage.getRecordUtil().getStageRecord());
				builder.setStageInfo(stage.getInfoBuilder().build());
				builder.setMoney(addMoney);
				builder.setExp(addExp);
				builder.setStamina(LootTreasureData.costStamina);
				this.player.decStamina(LootTreasureData.costStamina, "loottreasure");
				Platform.getEventManager().addEvent(
						new Event(2018, new Object[] { this.player, Integer.valueOf(LootTreasureData.costStamina) }));
				this.player.addExp(addExp, "loottreasure");
				this.player.addMoney(addMoney, "loottreasure");
			}
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1108, builder.build());
	}

	public void netOrDB() {
		Player objPlayer = null;
		if (this.objId > 0) {
			try {
				objPlayer = Platform.getPlayerManager().getPlayer(this.objId, true, true);
			} catch (Exception e) {
				e.printStackTrace();
				this.errorNum = 1;
				return;
			}
			if (objPlayer == null) {
				this.errorNum = 1;
				return;
			}
			this.objPlayer = objPlayer;
			this.isRobot = false;
		} else {
			LootRobot data = (LootRobot) this.player.getLootTreasure().robots.get(Integer.valueOf(this.objId));
			if (data == null) {
				this.errorNum = 1;
				return;
			}
			this.objPlayer = data.getPlayer();
			this.isRobot = true;
		}
	}
}
