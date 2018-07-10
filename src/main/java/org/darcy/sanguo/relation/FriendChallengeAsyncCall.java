package org.darcy.sanguo.relation;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageChannel;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MapService;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class FriendChallengeAsyncCall extends AsyncCall {
	private Player player;
	private int targetId;
	private Player target;

	public FriendChallengeAsyncCall(ClientSession session, PbPacket.Packet packet)
			throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.FriendChallenge chg = PbUp.FriendChallenge.parseFrom(packet.getData());
		this.targetId = chg.getPlayerId();
	}

	public void callback() {
		PbDown.FriendChallengeRst.Builder rst = PbDown.FriendChallengeRst.newBuilder();
		if (this.target == null) {
			rst.setResult(false);
			rst.setErrInfo("查找玩家失败");
		} else {
			MapTemplate mt = MapService.getSpecialMapTemplate(9);
			if (mt != null) {
				StageTemplate st = mt.stageTemplates[0];
				if (st != null) {
					long ini = System.currentTimeMillis();
					StageChannel channel = st.channels[0];
					FriendStage stage = new FriendStage(channel.getPositionInfo(), mt.name, st.secenId, this.player,
							this.target);
					Team offen = new Team(stage);
					offen.setUnits(this.player.getWarriors().getStands());
					Team deffen = new Team(stage);
					deffen.setUnits(this.target.getWarriors().getStands());
					stage.init(offen, deffen);

					Platform.getLog().logWarn("ff init:" + (System.currentTimeMillis() - ini));
					long s = System.currentTimeMillis();
					stage.combat(this.player);
					Platform.getLog().logWarn("ff combat:" + (System.currentTimeMillis() - s));
					long end = System.currentTimeMillis();
					stage.proccessReward(this.player);
					rst.setResult(true);
					rst.setStageInfo(stage.getInfoBuilder());
					rst.setStageRecord(stage.getRecordUtil().getStageRecord());
					stage.isWin();
				} else {
					rst.setResult(false);
					rst.setErrInfo("服务器繁忙，请稍后再试");
				}
			} else {
				rst.setResult(false);
				rst.setErrInfo("服务器繁忙，请稍后再试");
			}

		}

		long send = System.currentTimeMillis();
		this.player.send(2076, rst.build());
		Platform.getLog().logWarn("send :" + (System.currentTimeMillis() - send));
	}

	public void netOrDB() {
		try {
			this.target = Platform.getPlayerManager().getPlayer(this.targetId, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
