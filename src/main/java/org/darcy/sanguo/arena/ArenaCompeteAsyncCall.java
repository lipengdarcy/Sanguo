package org.darcy.sanguo.arena;

import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.log.LogManager;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerSaveCall;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.MapService;

import sango.packet.PbDown;
import sango.packet.PbPacket;

public class ArenaCompeteAsyncCall extends AsyncCall {
	Player player;
	int id;
	Player rival;
	Arena playerArena;
	Arena rivalArena;
	int errorNum = 0;

	public ArenaCompeteAsyncCall(ClientSession session, PbPacket.Packet packet, Player player, int id) {
		super(session, packet);
		this.player = player;
		this.id = id;
	}

	public void callback() {
		PbDown.ArenaCompeteRst.Builder builder = PbDown.ArenaCompeteRst.newBuilder();
		if (this.errorNum == 0) {
			MapTemplate mt = MapService.getSpecialMapTemplate(6);
			ArenaStage stage = new ArenaStage(mt.stageTemplates[0], this.player, this.rival);
			stage.init();
			stage.combat(this.player);
			boolean result = stage.isWin();
			builder.setStageRecord(stage.getRecordUtil().getStageRecord());
			builder.setStageInfo(stage.getInfoBuilder().build());

			int addMoney = 0;
			int addExp = 0;
			int addPrestige = 0;
			if (result) {
				addMoney = this.player.getLevel() * ArenaData.winMoneyRatio;
				addExp = this.player.getLevel() * ArenaData.winExpRatio;
				addPrestige = ArenaData.winPrestige;

				int playerRank = this.playerArena.getRank();
				int rivalRank = this.rivalArena.getRank();
				if (playerRank > rivalRank) {
					this.playerArena.setRank(rivalRank);
					this.player.getArena().addInfo(rivalRank, false, true, this.rival.getName());

					this.rivalArena.setRank(playerRank);
					this.rival.getArena().addInfo(playerRank, true, false, this.player.getName());

					ArenaSaveCall playerArenaSave = new ArenaSaveCall(this.playerArena);
					ArenaSaveCall rivalArenaSave = new ArenaSaveCall(this.rivalArena);
					Platform.getThreadPool().execute(playerArenaSave);
					Platform.getThreadPool().execute(rivalArenaSave);

					String content = MessageFormat.format(
							"<p style=19>{0}</p><p style=21>在竞技场中向你发起挑战，你被击败了，排名降至</p><p style=20>{1}</p><p style=21>名。这你能忍？快去复仇吧！</p>",
							new Object[] { this.player.getName(), Integer.valueOf(playerRank) });
					MailService.sendSystemMail(9, this.rival.getId(), "竞技场被击败", content);

					if (rivalRank < 11) {
						String msg = MessageFormat.format(
								"<p style=13>[{0}]</p><p style=17>在竞技场中成功击败了</p><p style=13>[{1}]</p><p style=17>取得了第{2}名！</p>",
								new Object[] { this.player.getName(), this.rival.getName(),
										Integer.valueOf(rivalRank) });
						Platform.getPlayerManager().boardCast(msg);
					}

				}

				builder.setTurnCard(this.player.getGlobalDrop().dropTurnCard(this.player, this.rival));
			} else {
				addMoney = this.player.getLevel() * ArenaData.loseMoneyRatio;
				addExp = this.player.getLevel() * ArenaData.loseExpRatio;
				addPrestige = ArenaData.losePrestige;

				this.rival.getArena().addInfo(this.rivalArena.getRank(), true, true, this.player.getName());
			}

			if (this.rival.getSession() == null) {
				PlayerSaveCall rivalSave = new PlayerSaveCall(this.rival);
				Platform.getThreadPool().execute(rivalSave);
			}

			this.player.decStamina(ArenaData.costStamina, "arena");
			Platform.getEventManager()
					.addEvent(new Event(2018, new Object[] { this.player, Integer.valueOf(ArenaData.costStamina) }));
			int count = this.player.getPool().getInt(3, ArenaData.challengeDayCount);
			this.player.getPool().set(3, Integer.valueOf(count - 1));

			Platform.getEventManager().addEvent(new Event(2016, new Object[] { this.player }));
			this.player.addMoney(addMoney, "arena");
			this.player.addExp(addExp, "arena");

			if (ActivityInfo.isOpenActivity(5)) {
				addPrestige *= 2;
			}
			this.player.addPrestige(addPrestige, "arena");

			Platform.getLog().logArena(this.player, true, this.rival, result);
			Platform.getLog().logArena(this.rival, false, this.player, !(result));

			builder.setResult(true);
			builder.setIsWin(result);
			builder.setMoney(addMoney);
			builder.setExp(addExp);
			builder.setStamina(ArenaData.costStamina);
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1098, builder.build());
	}

	public void netOrDB() {
		Player player = null;
		LogManager log = Platform.getLog();
		try {
			player = Platform.getPlayerManager().getPlayer(this.id, true, true);
			if (player == null) {
				log.logWarn("ArenaCompeteAsyncCall error: rival is null,id:" + this.id);
				this.errorNum = 1;
				return;
			}
			this.rival = player;
			this.playerArena = this.player.getArena();
			if (this.playerArena == null) {
				log.logWarn("ArenaCompeteAsyncCall error: playerArena is null,playerId:" + this.playerArena.getRank());
				this.errorNum = 1;
				return;
			}
			this.rivalArena = this.rival.getArena();
			if (this.rivalArena != null)
				return;
			log.logWarn("ArenaCompeteAsyncCall error: rivalArena is null,rivalId:" + this.rival.getId() + ", rank:"
					+ this.rivalArena.getRank());
			this.errorNum = 1;
			return;
		} catch (Exception e) {
			log.logError("ArenaCompeteAsyncCall error, id:" + this.id, e);
			this.errorNum = 1;
			return;
		}
	}
}
