package org.darcy.sanguo.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.service.MailService;

public class LeagueBossReward {
	public static final int version = 2;
	int id;
	long time;
	int rank;
	int damage;
	int level;
	List<Reward> rewards = new ArrayList();

	public void sendMail(int playerId, boolean isQuit, League l) {
		String content;
		if (isQuit)
			content = MessageFormat.format(
					"<p style=21>恭喜主公，昨日您在军团</p><p style=19>【{0}】</p><p style=21>中对军团Boss造成</p><p style=19>【{1}】</p><p style=21>的总伤害量。</p>\n<p style=21>根据昨日Boss最高等级以及您的军团伤害排名情况，现为您颁发以下奖励：</p>",
					new Object[] { l.getName(), Integer.valueOf(this.damage) });
		else {
			content = MessageFormat.format(
					"<p style=21>恭喜主公，昨日您所在军团【{0}】的军团Boss的最高等级为</p><p style=19>【{1}】</p><p style=21>。另外，主公对军团Boss造成了</p><p style=19>【{2}】</p><p style=21>的总伤害量，排名军团第</p><p style=20>【{3}】</p><p style=21>名。</p>\n<p style=21>根据昨日Boss最高等级以及您的军团伤害排名情况，现为您颁发以下奖励：</p>",
					new Object[] { l.getName(), Integer.valueOf(this.level), Integer.valueOf(this.damage),
							Integer.valueOf(this.rank) });
		}
		MailService.sendSystemMail(15, playerId, "军团Boss奖励", content, new Date(this.time), this.rewards);
	}

	public static LeagueBossReward readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();
			LeagueBossReward reward = new LeagueBossReward();
			reward.id = in.readInt();
			reward.time = in.readLong();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				Reward r = Reward.readObject(in);
				if (r != null) {
					reward.rewards.add(r);
				}
			}
			if (version > 1) {
				reward.rank = in.readInt();
				reward.damage = in.readInt();
				reward.level = in.readInt();
			}
			return reward;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(2);
		out.writeInt(this.id);
		out.writeLong(this.time);
		out.writeInt(this.rewards.size());
		for (Reward r : this.rewards) {
			r.writeObject(out);
		}
		out.writeInt(this.rank);
		out.writeInt(this.damage);
		out.writeInt(this.level);
	}
}
