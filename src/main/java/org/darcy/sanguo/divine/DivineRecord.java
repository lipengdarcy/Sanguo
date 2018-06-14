package org.darcy.sanguo.divine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.common.ItemService;

public class DivineRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 1951713321706878528L;
	private final int version = 2;
	public static final int MAX_DIVINE_TIMES = 5;
	public static final int DIVINE_COUNT = 5;
	public static final int UPDATE_PRICE = 200;
	public static final int REFRESH_PRICE = 50;
	public static final int UPDATE_LEVEL = 50;
	public static final int DIVINE_PRICE = 10;
	public static final int MAX_SCORE = 1000;
	private int divineTimes;
	private int totalScores;
	private int refreshTimes;
	private int[] divine;
	private Set<Integer> rewardRecords = new HashSet();
	private boolean updated;
	private List<Reward> rewards;

	public int getLeftRefreshTimes(Player player) {
		return -1;
	}

	public void refresh(Player player) {
		if (this.totalScores > 0) {
			List list = new ArrayList();
			for (DivineReward reward : DivineService.rewards) {
				if ((!(getRewardRecords().contains(Integer.valueOf(reward.id))))
						&& (getTotalScores() >= reward.needScore)) {
					Reward rwd = reward.getReward(this);
					list.add(rwd);
				}
			}
			if (list.size() > 0) {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(player.getRefreshTime());
				MailService.sendSystemMail(8, player.getId(), "占卜奖励补发", MessageFormat.format(
						"<p style=21>主公，您于</p><p style=20>{0}月{1}日</p><p style=21>占卜获得了</p><p style=19>{2}</p><p style=21>卦数，但未领取奖励。现为您补发奖励，请快快收下：</p>",
						new Object[] { Integer.valueOf(c.get(2) + 1), Integer.valueOf(c.get(5)),
								Integer.valueOf(this.totalScores) }),
						c.getTime(), list);
			}

		}

		this.divineTimes = 0;
		this.rewardRecords.clear();
		this.totalScores = 0;
		this.refreshTimes = 0;
		this.divine = null;
		player.getPool().set(19, Boolean.valueOf(true));

		if (this.updated)
			((DivineService) Platform.getServiceManager().get(DivineService.class)).innerRefresh(player);
	}

	public int getCurrentScore(int[] divine) {
		if (divine != null) {
			int[] arrayOfInt;
			int rst = 0;
			int j = (arrayOfInt = divine).length;
			int i = 0;
			while (true) {
				int d = arrayOfInt[i];
				if (d == 0)
					rst += 5;
				else
					++rst;
				++i;
				if (i >= j) {
					return rst;
				}
			}
		}
		return 0;
	}

	public int getLeftDivineTimes() {
		int left = 5 - this.divineTimes;
		if (left < 0) {
			return 0;
		}
		return left;
	}

	public int getRewardsCount() {
		int rst = 0;
		for (DivineReward dr : DivineService.rewards) {
			if ((dr.needScore <= this.totalScores) && (!(this.rewardRecords.contains(Integer.valueOf(dr.id))))) {
				++rst;
			}
		}
		return rst;
	}

	public int getDivineTimes() {
		return this.divineTimes;
	}

	public int getTotalScores() {
		return this.totalScores;
	}

	public Set<Integer> getRewardRecords() {
		return this.rewardRecords;
	}

	public void setDivineTimes(int divineTimes) {
		this.divineTimes = divineTimes;
	}

	public void setTotalScores(int totalScores) {
		this.totalScores = totalScores;
	}

	public boolean isUpdated() {
		return this.updated;
	}

	public int getRefreshTimes() {
		return this.refreshTimes;
	}

	public void setRefreshTimes(int refreshTime) {
		this.refreshTimes = refreshTime;
	}

	public List<Reward> getRewards() {
		return this.rewards;
	}

	public void setRewards(List<Reward> rewards) {
		this.rewards = rewards;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public void setRewardRecords(Set<Integer> rewardRecords) {
		this.rewardRecords = rewardRecords;
	}

	public int getBlobId() {
		return 16;
	}

	private void readObject(ObjectInputStream in) {
		try {
			this.rewardRecords = new HashSet();
			this.rewards = new ArrayList();

			int v = in.readInt();
			if (v == 1) {
				in.readInt();
			}
			int size = in.readInt();
			if (size > 0) {
				this.divine = new int[size];
				for (int i = 0; i < size; ++i) {
					this.divine[i] = in.readInt();
				}
			}
			this.divineTimes = in.readInt();
			this.refreshTimes = in.readInt();
			size = in.readInt();
			this.rewardRecords = new HashSet();
			for (int i = 0; i < size; ++i) {
				this.rewardRecords.add(Integer.valueOf(in.readInt()));
			}
			size = in.readInt();
			this.rewards = new ArrayList();
			for (int i = 0; i < size; ++i) {
				int type = in.readInt();
				int count = in.readInt();
				int tid = in.readInt();
				ItemTemplate tplt = null;
				if (tid != -1) {
					tplt = ItemService.getItemTemplate(tid);
				}
				this.rewards.add(new Reward(type, count, tplt));
			}
			this.totalScores = in.readInt();
			this.updated = in.readBoolean();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(2);
		if (this.divine != null) {
			out.writeInt(this.divine.length);
			for (int i : this.divine)
				out.writeInt(i);
		} else {
			out.writeInt(0);
		}
		out.writeInt(this.divineTimes);
		out.writeInt(this.refreshTimes);
		out.writeInt(this.rewardRecords.size());
		for (Integer i : this.rewardRecords) {
			out.writeInt(i.intValue());
		}
		if (this.rewards != null) {
			out.writeInt(this.rewards.size());
			for (Reward r : this.rewards) {
				out.writeInt(r.type);
				out.writeInt(r.count);
				if (r.template == null)
					out.writeInt(-1);
				else
					out.writeInt(r.template.id);
			}
		} else {
			out.writeInt(0);
		}
		out.writeInt(this.totalScores);
		out.writeBoolean(this.updated);
	}

	public int[] getDivine() {
		return this.divine;
	}

	public void setDivine(int[] divine) {
		this.divine = divine;
	}
}
