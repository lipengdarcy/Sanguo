package org.darcy.sanguo.reward;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbCommons;

public class TimeLimitItem implements Serializable {
	private static final long serialVersionUID = 1L;
	public static int version = 1;
	public int id;
	public List<Reward> rewards;
	public int price;
	public int count;

	public PbCommons.TimeLimitReward.Builder genPbTimeLimitReward() {
		PbCommons.TimeLimitReward.Builder item = PbCommons.TimeLimitReward.newBuilder();
		item.setId(this.id).setPrice(this.price).setSurplus(0);
		StringBuilder items = new StringBuilder();
		for (Reward r : this.rewards) {
			item.addRewards(r.genPbReward());
			items.append(r).append(",");
		}
		if (items.length() > 0) {
			item.setReward(items.deleteCharAt(items.length() - 1).toString());
		}
		return item;
	}

	public static TimeLimitItem readObject(ObjectInputStream in) {
		try {
			in.readInt();
			int id = in.readInt();
			int length = in.readInt();
			List rewards = new ArrayList(length);
			for (int i = 0; i < length; ++i) {
				Reward r = Reward.readObject(in);
				if (r != null) {
					rewards.add(r);
				}
			}
			int price = in.readInt();
			int count = in.readInt();

			TimeLimitItem item = new TimeLimitItem();
			item.id = id;
			item.rewards = rewards;
			item.price = price;
			item.count = count;

			return item;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);

		out.writeInt(this.id);
		out.writeInt(this.rewards.size());
		for (int i = 0; i < this.rewards.size(); ++i) {
			((Reward) this.rewards.get(i)).writeObject(out);
		}
		out.writeInt(this.price);
		out.writeInt(this.count);
	}
}
