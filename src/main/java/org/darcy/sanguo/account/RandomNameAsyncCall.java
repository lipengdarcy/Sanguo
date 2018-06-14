package org.darcy.sanguo.account;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.AccountService;
import org.darcy.sanguo.util.DBUtil;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class RandomNameAsyncCall extends AsyncCall {
	String name;
	int resultNum = 0;

	public RandomNameAsyncCall(ClientSession session, PbPacket.Packet packet) {
		super(session, packet);
	}

	public void callback() {
		PbDown.RandomNameRst.Builder builder = PbDown.RandomNameRst.newBuilder();
		if (this.resultNum == 0) {
			builder.setResult(true);
			builder.setName(this.name);
		} else {
			builder.setResult(false);
			if (this.resultNum == 1) {
				builder.setErrInfo("获取随机名字失败");
			}
		}
		this.session.send(1008, builder.build());
	}

	public void netOrDB() {
		PbUp.RandomName randomName = null;
		int gender = 1;
		try {
			randomName = PbUp.RandomName.parseFrom(this.packet.getData());
			gender = randomName.getGender();
			if ((gender != 1) && (gender != 2))
				gender = 1;
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			this.resultNum = 1;
			return;
		}
		for (int i = 0; i < 10; ++i) {
			this.name = ((AccountService) Platform.getServiceManager().get(AccountService.class)).genName(gender);
			if (!(Platform.getKeyWordManager().contains("blackword.txt", this.name))) {
				if (Platform.getKeyWordManager().contains("nameblackword.txt", this.name)) {
					continue;
				}

				Player info = DBUtil.getPlayerByName(this.name);
				if (info == null)
					return;
			}
		}
	}
}
