package org.darcy.sanguo.union.combat;

import java.text.MessageFormat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;

import sango.packet.PbDown;

public class LCLootAsyncCall extends AsyncCall {
	private Player player;
	private City city;
	private Pair pair;
	private Player target;

	public LCLootAsyncCall(Player player, City city, Pair pair) {
		super(null, null);
		this.player = player;
		this.city = city;
		this.pair = pair;
	}

	public void callback() {
		PbDown.LCLootRst.Builder rst = PbDown.LCLootRst.newBuilder();
		rst.setResult(false);
		rst.setIndex(this.city.getIndex());
		if (this.target == null)
			rst.setErrInfo("服务器繁忙，请稍后再试");
		else if (this.target.getId() != this.city.getPid())
			rst.setErrInfo("城池已经易主，请重新发起挑战");
		else {
			try {
				Stage stage;
				MapTemplate mt;
				StageTemplate st;
				if (this.city.getType() == 1) {
					mt = MapService.getSpecialMapTemplate(14);
					st = mt.stageTemplates[0];
					stage = new CityStage(14, st.channels[0].getPositionInfo(), st.name, st.secenId, this.player,
							this.target, this.pair);
				} else if (this.city.getType() == 2) {
					mt = MapService.getSpecialMapTemplate(15);
					st = mt.stageTemplates[0];
					stage = new TownStage(15, st.channels[0].getPositionInfo(), st.name, st.secenId, this.player,
							this.target, this.pair, this.city);
				} else {
					mt = MapService.getSpecialMapTemplate(16);
					st = mt.stageTemplates[0];
					stage = new VillageStage(16, st.channels[0].getPositionInfo(), st.name, st.secenId, this.player,
							this.target);
				}

				stage.init();
				rst.setResult(true);
				rst.setStageInfo(stage.getInfoBuilder());
				stage.combat(this.player);
				stage.proccessReward(this.player);
				rst.setStageRecord(stage.getRecordUtil().getStageRecord());
				rst.setIsWin(stage.isWin());
				rst.setTarget(Platform.getPlayerManager().getMiniPlayer(this.target.getId()).genMiniUser());
				LeagueCombat combat = Platform.getLeagueManager().getCombat();
				LeagueCombatService cs = (LeagueCombatService) Platform.getServiceManager()
						.get(LeagueCombatService.class);
				if (stage.isWin()) {
					this.pair.endCity(this.city, this.player.getId(), this.player.getUnion().getLeagueId());
					combat.setLootDieTime(this.target.getId());
					this.pair.addLose(this.target.getId());
					this.pair.addWin(this.player.getId());
					cs.reviveInfo(this.target, PbDown.LCReviveInfo.LCDeadType.LOOTED);
					cs.sync(this.pair);
					this.pair.addScore(this.player.getUnion().getLeagueId(), this.player.getId(), 10);
					cs.broadcast(MessageFormat.format(
							"<p style=77>【{0}】</p><p style=21>成功击败【{1}】，帮助军团获得了</p><p style=84>【{2}】</p><p style=21>的占领权！</p>",
							new Object[] { this.player.getName(), this.target.getName(), this.city.getName() }),
							Platform.getLeagueManager().getLeagueById(this.city.getLid()));
					cs.broadcast(MessageFormat.format(
							"<p style=84>【{0}】</p><p style=21>失守！！</p><p style=21>目前正被敌军</p><p style=76>【{1}】</p><p style=21>占领！！</p>",
							new Object[] { this.city.getName(), this.player.getName() }),
							Platform.getLeagueManager().getLeagueById(this.pair.getTargetLid(this.city.getLid())));
					Platform.getLog().logLeagueCombat(
							Platform.getLeagueManager().getLeagueByPlayerId(this.player.getId()),
							Platform.getLeagueManager().getLeagueByPlayerId(this.target.getId()), this.city, "城池挑战",
							this.player, this.target, true);
				}
				combat.setLootDieTime(this.player.getId());
				this.pair.addWin(this.target.getId());
				this.pair.addLose(this.player.getId());
				this.pair.addScore(this.player.getUnion().getLeagueId(), this.player.getId(), 5);
				cs.reviveInfo(this.player, PbDown.LCReviveInfo.LCDeadType.LOOT_FAILED);
				Platform.getLog().logLeagueCombat(Platform.getLeagueManager().getLeagueByPlayerId(this.player.getId()),
						Platform.getLeagueManager().getLeagueByPlayerId(this.target.getId()), this.city, "城池挑战",
						this.player, this.target, false);
			} catch (Exception e) {
				e.printStackTrace();
				rst.setErrInfo("服务器繁忙，请稍后再试");
			}
		}
		label782: this.player.send(2286, rst.build());
	}

	public void netOrDB() {
		int tid = this.city.getPid();
		if (tid <= 0)
			return;
		try {
			this.target = Platform.getPlayerManager().getPlayer(tid, true, true);
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}
}
