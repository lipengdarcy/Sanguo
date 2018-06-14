package org.darcy.sanguo.union.combat;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;
import org.darcy.sanguo.union.League;

import sango.packet.PbDown;

public class RandomFightAsyncCall extends AsyncCall {
	Player player;
	Player target;
	int targetId;

	public RandomFightAsyncCall(Player player, int targetId) {
		super(null, null);
		this.player = player;
		this.targetId = targetId;
	}

	public void callback() {
		PbDown.LCRandomFightRst.Builder rst = PbDown.LCRandomFightRst.newBuilder();
		rst.setResult(false);
		try {
			League l = Platform.getLeagueManager().getLeagueById(this.player.getUnion().getLeagueId());
			LeagueCombat combat = Platform.getLeagueManager().getCombat();
			if ((l == null) || (!(l.isMember(this.player.getId())))) {
				rst.setErrInfo("您尚未加入军团!");
				this.player.send(2290, rst.build());
				return;
			}
			if (!(combat.isFighting(l.getId()))) {
				rst.setErrInfo("军团战已经结束");
				this.player.send(2290, rst.build());
				return;
			}
			if (combat.getPair(l.getId()) == null) {
				rst.setErrInfo("周一至周五零点，成员数达20人的军团才可参与匹配");
				this.player.send(2290, rst.build());
				return;
			}
			Pair pair = combat.getPair(l.getId());
			if (this.target == null) {
				rst.setErrInfo("对方军团全都龟缩城内，去嘲讽他吧");
				this.player.send(2290, rst.build());
				return;
			}
			if (combat.getLeftRandomTime(this.player.getId()) > 0L) {
				rst.setErrInfo("请耐心等待复活");
				this.player.send(2290, rst.build());
				return;
			}
			combat.setRandomFight(this.player.getId());
			MapTemplate mt = MapService.getSpecialMapTemplate(16);
			StageTemplate st = mt.stageTemplates[0];
			Stage stage = new RandomStage(17, st.channels[0].getPositionInfo(), st.name, st.secenId, this.player,
					this.target);
			stage.init();
			rst.setResult(true);
			rst.setStageInfo(stage.getInfoBuilder());
			stage.combat(this.player);
			stage.proccessReward(this.player);
			rst.setCd(combat.getLeftRandomTime(this.player.getId()));
			rst.setStageRecord(stage.getRecordUtil().getStageRecord());
			rst.setIsWin(stage.isWin());
			rst.setTarget(Platform.getPlayerManager().getMiniPlayer(this.targetId).genMiniUser());
			if (stage.isWin()) {
				rst.setScore(10);
				pair.addScore(this.player.getUnion().getLeagueId(), this.player.getId(), 10);
				((LeagueCombatService) Platform.getServiceManager().get(LeagueCombatService.class)).sync(pair);
				Platform.getLog().logLeagueCombat(Platform.getLeagueManager().getLeagueByPlayerId(this.player.getId()),
						Platform.getLeagueManager().getLeagueByPlayerId(this.target.getId()), null, "混战 ", this.player,
						this.target, false);
				this.player.send(2290, rst.build());
				return;
			}
			rst.setScore(1);
			pair.addScore(this.player.getUnion().getLeagueId(), this.player.getId(), 5);
			Platform.getLog().logLeagueCombat(Platform.getLeagueManager().getLeagueByPlayerId(this.player.getId()),
					Platform.getLeagueManager().getLeagueByPlayerId(this.target.getId()), null, "混战 ", this.player,
					this.target, false);
		} catch (Exception e) {
			Platform.getLog().logError(e);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		this.player.send(2290, rst.build());
	}

	public void netOrDB() {
		try {
			this.target = Platform.getPlayerManager().getPlayer(this.targetId, true, true);
		} catch (Exception e) {
			Platform.getLog().logError(e);
		}
	}
}
