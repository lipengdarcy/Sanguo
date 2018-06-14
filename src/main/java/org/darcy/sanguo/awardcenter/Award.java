package org.darcy.sanguo.awardcenter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;

import sango.packet.PbAwards;

public class Award {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("M月d日 H时");
	public static final int AWARD_SYS_COMPENSATION = 1;
	public static final int AWARD_GM = 2;
	public static final int AWARD_WORLDCOMPETITION = 3;
	public static final int AWARD_ARENA = 4;
	public static final int AWARD_TOWER_MULTI = 5;
	public static final int AWARD_BOSS = 6;
	public static final int AWARD_DIVINE = 7;
	public static final int AWARD_SIGN = 8;
	private int id;
	private int type;
	private long time;
	private List<Reward> rewards = new ArrayList();

	public boolean isValid() {
		return (this.time + 1209600000L <= System.currentTimeMillis());
	}

	public void award(Player player) {
		for (Reward reward : this.rewards)
			reward.add(player, getOptType());
	}

	public String getOptType() {
		String optType = "";
		switch (this.type) {
		case 1:
		case 2:
			optType = "system";
			break;
		case 3:
			optType = "worldcompetreward";
			break;
		case 4:
			optType = "arenareward";
			break;
		case 5:
			optType = "towerreward";
			break;
		case 6:
			optType = "bossreward";
			break;
		case 7:
			optType = "divine";
			break;
		case 8:
			optType = "signreward";
			break;
		default:
			optType = String.valueOf(this.type);
		}
		return optType;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public List<Reward> getRewards() {
		return this.rewards;
	}

	public void setRewards(List<Reward> rewards) {
		this.rewards = rewards;
	}

	public PbAwards.AwardInfo genAwardInfo() {
		PbAwards.AwardInfo.Builder builder = PbAwards.AwardInfo.newBuilder();
		builder.setId(this.id);
		builder.setType(this.type);
		builder.setTime(sdf.format(new Date(this.time)));
		for (Reward reward : this.rewards) {
			builder.addRewards(reward.genPbReward());
		}
		return builder.build();
	}

	public static Award readObject(ObjectInputStream in) {
		try {
			int id = in.readInt();
			int type = in.readInt();
			long time = in.readLong();
			Award award = new Award();
			award.setId(id);
			award.setType(type);
			award.setTime(time);
			int rewardLength = in.readInt();
			for (int i = 0; i < rewardLength; ++i) {
				award.rewards.add(Reward.readObject(in));
			}
			return award;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.id);
		out.writeInt(this.type);
		out.writeLong(this.time);
		out.writeInt(this.rewards.size());
		for (Reward reward : this.rewards)
			reward.writeObject(out);
	}
}
