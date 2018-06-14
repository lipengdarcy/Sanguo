package org.darcy.sanguo.reward;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbCommons;

public class LoginReward {
	public static int[] warrior7 = { 3515, 2512, 4514, 5513 };

	public static int warrior7Day = 7;
	public int id;
	public int day;
	public List<Reward> rewards = new ArrayList();

	public PbCommons.LoginReward genLoginReward(boolean canGet) {
		PbCommons.LoginReward.Builder builder = PbCommons.LoginReward.newBuilder();
		builder.setId(this.id);
		builder.setDay(this.day);
		builder.setCanGet(canGet);
		for (Reward reward : this.rewards) {
			builder.addRewards(reward.genPbReward());
		}
		return builder.build();
	}

	public static boolean is7Warrior(int id) {
		for (int templateId : warrior7) {
			if (templateId == id) {
				return true;
			}
		}
		return false;
	}

	public static List<PbCommons.PbReward> getWarrior7() {
		List list = new ArrayList();
		for (int id : warrior7) {
			PbCommons.PbReward.Builder b = PbCommons.PbReward.newBuilder();
			b.setType(0);
			b.setCount(1);
			b.setTemplateId(id);
			list.add(b.build());
		}
		return list;
	}
}
