package org.darcy.sanguo.worldcompetition;

import java.text.MessageFormat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.asynccall.AsyncUpdater;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.MapService;

import sango.packet.PbDown;

public class WorldCompetitionCompeteAsyncCall extends AsyncCall {
	Player player;
	int id;
	Player competitor;
	int errorNum = 0;

	public WorldCompetitionCompeteAsyncCall(Player player, int id) {
		super(player.getSession(), null);
		this.player = player;
		this.id = id;
	}

	public void callback() {
		PbDown.WorldCompetitionCompeteRst.Builder builder = PbDown.WorldCompetitionCompeteRst.newBuilder();
		if (this.errorNum == 0) {
			MapTemplate mt = MapService.getSpecialMapTemplate(7);
			WorldCompetitionStage stage = new WorldCompetitionStage(mt.stageTemplates[0], this.player, this.competitor);
			stage.init();
			stage.combat(this.player);
			boolean result = stage.isWin();

			int honor = 0;
			int exp = 0;
			if (result) {
				WorldCompetitionService service = (WorldCompetitionService) Platform.getServiceManager()
						.get(WorldCompetitionService.class);
				WorldCompetition competition = this.player.getWorldCompetition();
				WorldCompetition competition2 = this.competitor.getWorldCompetition();

				int winScore = (int) (WorldCompetitionData.winBaseScore
						+ WorldCompetitionData.winScoreRatio / 10000.0D * competition2.getScore());
				winScore = (winScore > WorldCompetitionData.maxScore) ? WorldCompetitionData.maxScore : winScore;
				competition.setScore(competition.getScore() + winScore);
				builder.setAddScore(winScore);
				Platform.getEventManager()
						.addEvent(new Event(2017, new Object[] { this.player, Integer.valueOf(winScore) }));

				int loseScore = (int) (WorldCompetitionData.loseBaseScore
						+ WorldCompetitionData.loseScoreRatio / 10000.0D * competition2.getScore());
				loseScore = (loseScore > WorldCompetitionData.maxScore) ? WorldCompetitionData.maxScore : loseScore;
				loseScore = Math.min(competition2.getScore(), loseScore);
				service.lostScoreModify(competition2, loseScore, competition);

				MailService.sendSystemMail(10, this.competitor.getId(), "争霸赛被击败", MessageFormat.format(
						"<p style=19>{0}</p><p style=21>在争霸赛中向你发起挑战，你被击败了，被抢夺了</p><p style=20>{1}</p><p style=21>积分。这你能忍？快去复仇吧！</p>",
						new Object[] { this.player.getName(), Integer.valueOf(loseScore) }));

				boolean isError = false;
				isError = service.sort(competition2, false);
				if (!(isError)) {
					isError = service.sort(competition, true);
				}

				if (isError) {
					((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class)).sort();
				}
				((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
						.clearOutData();

				if (Platform.getPlayerManager().getPlayerById(competition2.getPlayerId()) == null) {
					Platform.getThreadPool().execute(new AsyncUpdater(competition2));
				}

				service.refreshCompetitor(this.player, false);

				honor = WorldCompetitionData.winHonor;
				exp = this.player.getLevel() * WorldCompetitionData.winExp;

				if (competition.getEnemy().contains(Integer.valueOf(this.id))) {
					competition.getEnemy().remove(new Integer(this.id));
				}
				if (!(competition2.getEnemy().contains(Integer.valueOf(this.player.getId())))) {
					competition2.getEnemy().add(Integer.valueOf(this.player.getId()));
				}

				builder.setTurnCard(this.player.getGlobalDrop().dropTurnCard(this.player, this.competitor));
			} else {
				exp = this.player.getLevel() * WorldCompetitionData.loseExp;
			}
			this.player.decStamina(2, "worldcompet");
			Platform.getEventManager().addEvent(new Event(2018, new Object[] { this.player, Integer.valueOf(2) }));

			if (ActivityInfo.isOpenActivity(6)) {
				honor *= 2;
			}
			this.player.addHonor(honor, "worldcompet");
			this.player.addExp(exp, "worldcompet");
			builder.setStamina(2);
			builder.setExp(exp);
			builder.setHonor(honor);

			int count = this.player.getPool().getInt(2, 20);
			this.player.getPool().set(2, Integer.valueOf(count - 1));

			Platform.getLog().logCompetition(this.player, true, this.competitor, result);
			Platform.getLog().logCompetition(this.competitor, false, this.player, !(result));

			builder.setStageRecord(stage.getRecordUtil().getStageRecord());
			builder.setStageInfo(stage.getInfoBuilder().build());
			builder.setResult(true);
			builder.setIsWin(result);
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1086, builder.build());
	}

	public void netOrDB() {
		Player player = null;
		try {
			player = Platform.getPlayerManager().getPlayer(this.id, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			this.errorNum = 1;
			return;
		}
		if (player == null) {
			this.errorNum = 1;
			return;
		}
		this.competitor = player;
	}
}
