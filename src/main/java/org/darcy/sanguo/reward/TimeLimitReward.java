package org.darcy.sanguo.reward;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.drop.Reward;

public class TimeLimitReward implements Serializable {
	private static final long serialVersionUID = 2054971255221908626L;
	public static final int TYPE_ONE = 1;
	public static final int TYPE_PACKET = 2;
	public static int version = 3;
	public long id;
	public int count;
	public long start;
	public long end;
	public long lastTime;
	public String name;
	public List<Reward> rewards = new ArrayList();
	public int targetType;
	public List<Integer> targets = new ArrayList();
	public int origPrice;
	public int salePrice;
	public Map<Integer, TimeLimitItem> items = new HashMap();

	public TimeLimitReward() {
		this.id = System.currentTimeMillis();
	}

	public boolean isInTimeByEnd(long time) {
		if (this.lastTime > 0L) {
			return ((time < this.start) || (time > this.end + this.lastTime));
		}
		return ((this.start > time) || (time > this.end));
	}

	public boolean isInTimeByActivate(long time) {
		return ((time < this.start) || (time > this.end));
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.id = in.readLong();
			this.count = in.readInt();
			this.start = in.readLong();
			this.end = in.readLong();
			this.lastTime = in.readLong();

			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.readFully(bytes);
			this.name = new String(bytes, Charset.forName("utf-8"));

			int size = in.readInt();
			this.rewards = new ArrayList(size);
			for (int i = 0; i < size; ++i) {
				Reward r = Reward.readObject(in);
				if (r != null) {
					this.rewards.add(r);
				}
			}

			if (version > 1) {
				this.origPrice = in.readInt();
				this.salePrice = in.readInt();
				this.targetType = in.readInt();
				int len = in.readInt();
				this.targets = new ArrayList();
				for (int i = 0; i < len; ++i) {
					this.targets.add(Integer.valueOf(in.readInt()));
				}
				if (version > 2) {
					len = in.readInt();
					this.items = new HashMap(len);
					for (int i = 0; i < len; ++i) {
						TimeLimitItem item = TimeLimitItem.readObject(in);
						if (item != null)
							this.items.put(Integer.valueOf(item.id), item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);

		out.writeLong(this.id);
		out.writeInt(this.count);
		out.writeLong(this.start);
		out.writeLong(this.end);
		out.writeLong(this.lastTime);

		out.writeInt(this.name.getBytes(Charset.forName("utf-8")).length);
		out.write(this.name.getBytes(Charset.forName("utf-8")));

		int size = this.rewards.size();
		out.writeInt(size);
		for (Reward r : this.rewards) {
			r.writeObject(out);
		}

		out.writeInt(this.origPrice);
		out.writeInt(this.salePrice);
		out.writeInt(this.targetType);
		out.writeInt(this.targets.size());
		for (int i = 0; i < this.targets.size(); ++i) {
			out.writeInt(((Integer) this.targets.get(i)).intValue());
		}

		out.writeInt(this.items.size());
		for (TimeLimitItem item : this.items.values())
			item.writeObject(out);
	}

	public String toString() {
		return "TimeLimitReward [id=" + this.id + ", count=" + this.count + ", start=" + this.start + ", end="
				+ this.end + ", lastTime=" + this.lastTime + ", name=" + this.name + ", rewards=" + this.rewards
				+ ", targetType=" + this.targetType + ", targets=" + this.targets + ", origPrice=" + this.origPrice
				+ ", salePrice=" + this.salePrice + "]";
	}
}
