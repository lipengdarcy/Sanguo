package org.darcy.sanguo.account;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.LeagueMember;
import org.darcy.sanguo.util.DBUtil;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class ChangeNameAsyncCall extends AsyncCall {
	Player player;
	int resultNum = 0;
	private static final int COST_JEWEL = 100;
	private boolean costItem = true;
	String name;

	public ChangeNameAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
	}

	public void callback() {
		if (this.player != null) {
			PbDown.ChangeNameRst.Builder builder = PbDown.ChangeNameRst.newBuilder();
			if (this.resultNum == 0) {
				if (this.costItem)
					this.player.getBags().removeItem(0, 10030, 1, "changename");
				else {
					this.player.decJewels(100, "changename");
				}
				builder.setResult(true);
				this.player.getWarriors().getMainWarrior().setName(this.name);
				this.player.getWarriors().getMainWarrior().refreshAttributes(true);
				if ((this.player.getUnion() != null) && (this.player.getUnion().getLeagueId() > 0)) {
					League l = Platform.getLeagueManager().getLeagueById(this.player.getUnion().getLeagueId());
					if (l != null) {
						LeagueMember lm = l.getMember(this.player.getId());
						if (lm != null) {
							lm.setName(this.player.getName());
						}
					}
				}

				Platform.getPlayerManager().updateMiniPlayer(this.player);
			} else {
				builder.setResult(false);
				if (this.resultNum == 1)
					builder.setErrInfo("修改名字失败");
				else if (this.resultNum == 2)
					builder.setErrInfo("性别选择有误");
				else if (this.resultNum == 3)
					builder.setErrInfo("名字须为1-6位");
				else if (this.resultNum == 4)
					builder.setErrInfo("名字被占用，请更换名字");
				else if (this.resultNum == 5) {
					builder.setErrInfo("元宝不足");
				}
			}
			PbDown.ChangeNameRst rst = builder.build();
			this.session.send(1010, rst);
		}
	}

	public void netOrDB() {
		PbUp.ChangeName changeName;
		this.player = this.session.getPlayer();
		if (this.player == null)
			return;
		try {
			changeName = PbUp.ChangeName.parseFrom(this.packet.getData());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			this.resultNum = 1;
			return;
		}
		String name = changeName.getName().trim();
		if ((Platform.getKeyWordManager().contains("blackword.txt", name))
				|| (Platform.getKeyWordManager().contains("nameblackword.txt", name))) {
			this.resultNum = 4;
			return;
		}
		if ((name.length() < 1) || (name.length() > 6)) {
			this.resultNum = 3;
			return;
		}
		Player tmp = DBUtil.getPlayerByName(name);
		if (tmp != null) {
			this.resultNum = 4;
			return;
		}
		if (this.player.getBags().getItemCount(10030) <= 0) {
			if (this.player.getJewels() < 100) {
				this.resultNum = 5;
				return;
			}
			this.costItem = false;
		}

		this.player.setName(name);
		this.name = name;
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this.player);
	}
}
