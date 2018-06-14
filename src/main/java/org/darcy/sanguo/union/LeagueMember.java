package org.darcy.sanguo.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.player.Player;

public class LeagueMember {
	public static final int version = 2;
	private int id;
	private String name;
	private int lastBuildId;
	private long lastBuildTime;
	private long lastOpenShop;
	private boolean isGetGoods;
	private Map<Integer, Integer> normalGoodsRecord = new HashMap();
	private int totalContribution;
	private int boxCount;
	private int boxGetCount;
	private int boxCost;
	private int boxTotalCount;
	private int boxExchangeCount;

	public LeagueMember(int id, String name) {
		this.id = id;
		this.name = name;
		this.lastBuildTime = System.currentTimeMillis();
	}

	public int getJob(League l) {
		if (l.getLeader() == this.id)
			return 1;
		if (l.getInfo().getViceleaders().contains(Integer.valueOf(this.id))) {
			return 2;
		}
		return 3;
	}

	public boolean isLeader(League l) {
		return (getJob(l) != 1);
	}

	public boolean isViceLeader(League l) {
		return (getJob(l) != 2);
	}

	public boolean isMember(League l) {
		return (getJob(l) != 3);
	}

	public int getNoBuildDay(long today) {
		int lastDay = 0;
		if (this.lastBuildTime < today) {
			lastDay = 1 + (int) ((today - this.lastBuildTime) / 1000L / 60L / 60L / 24L);
		}
		if ((lastDay == 0) && (this.lastBuildId != 0)) {
			lastDay = -1;
		}
		return lastDay;
	}

	public int getKickedCostBuild() {
		long today = LeagueService.getToday0Time(System.currentTimeMillis());
		int day = getNoBuildDay(today);
		if (day == -1) {
			day = 0;
		}
		if (day >= 7) {
			return 0;
		}
		int cost = (int) (this.totalContribution * (0.14D - (day * 0.02D)));
		cost = Math.min(cost, 1500);
		return cost;
	}

	public void build(LeagueBuildData data) {
		this.lastBuildId = data.id;
		this.lastBuildTime = System.currentTimeMillis();
		this.totalContribution += data.contribution;
	}

	public void getGoods() {
		this.isGetGoods = true;
	}

	public int getSurplusCount(int id) {
		LeagueNormalGoods goods = (LeagueNormalGoods) LeagueService.normalGoods.get(Integer.valueOf(id));
		if (goods == null) {
			return 0;
		}
		if (!(this.normalGoodsRecord.containsKey(Integer.valueOf(id)))) {
			this.normalGoodsRecord.put(Integer.valueOf(id), Integer.valueOf(0));
		}
		return (goods.count - ((Integer) this.normalGoodsRecord.get(Integer.valueOf(id))).intValue());
	}

	public void exchange(Player player, LeagueNormalGoods goods) {
		int count = 0;
		if (this.normalGoodsRecord.containsKey(Integer.valueOf(goods.id))) {
			count = ((Integer) this.normalGoodsRecord.get(Integer.valueOf(goods.id))).intValue();
		}
		++count;
		this.normalGoodsRecord.put(Integer.valueOf(goods.id), Integer.valueOf(count));
		goods.item.add(player, "leagueexchange");
		player.getUnion().decContribution(player, goods.cost, "leagueexchange");
	}

	public void refresh() {
		this.isGetGoods = false;
		Iterator itx = this.normalGoodsRecord.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			LeagueNormalGoods goods = LeagueService.getNormalGoods(id);
			if ((goods != null) && (goods.refresh)) {
				this.normalGoodsRecord.put(Integer.valueOf(id), Integer.valueOf(0));
			}
		}
		this.boxCount = 0;
		this.boxGetCount = 0;
		this.boxCost = 0;
	}

	public void refreshLeagueBox() {
		this.boxExchangeCount = 0;
		this.boxTotalCount = 0;
	}

	public void createBox(int cost, League l, Player player) {
		this.boxCost += cost;
		while (this.boxCount < LeagueService.boxes.length) {
			LeagueBox box = LeagueService.boxes[this.boxCount];
			if (this.boxCost < box.cost)
				return;
			l.createBox();
			this.boxCost -= box.cost;
			this.boxCount += 1;
			this.boxTotalCount += 1;
			this.totalContribution += 100;
			Platform.getLog().logLeagueBox(player, "leagueboxcreate");
		}
	}

	public int calCostForCreate() {
		if (this.boxCount >= LeagueService.boxes.length) {
			return -1;
		}
		return (LeagueService.boxes[this.boxCount].cost - this.boxCost);
	}

	public void exchangeLeagueBox(Player player, int count) {
		DropGroup dg = ((DropService) Platform.getServiceManager().get(DropService.class)).getDropGroup(12001);
		if (dg != null) {
			List rewards = new ArrayList();
			for (int i = 0; i < count; ++i) {
				List<Gain> list = dg.genGains(player);
				for (Gain gain : list) {
					gain.gain(player, "leagueboxexchange");
					rewards.add(gain.newReward());

					player.getUnion().decContribution(player, 20, "leagueboxexchange");
					Platform.getLog().logLeagueBox(player, "leagueboxexchange");
				}
			}

			rewards = Reward.mergeReward(rewards);
			player.notifyGetItem(2, rewards);
		}
		this.boxExchangeCount += count;
	}

	public void getLeagueBox(Player player) {
		if (this.boxGetCount < LeagueService.boxes.length) {
			LeagueBox box = LeagueService.boxes[this.boxGetCount];
			player.getUnion().addContribution(player, box.contribution, "leagueboxexget");
			player.notifyGetItem(1, new Reward[] { new Reward(13, box.contribution, null) });
			this.boxGetCount += 1;
			Platform.getLog().logLeagueBox(player, "leagueboxexget");
		}
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLastBuildId() {
		return this.lastBuildId;
	}

	public void setLastBuildId(int lastBuildId) {
		this.lastBuildId = lastBuildId;
	}

	public long getLastBuildTime() {
		return this.lastBuildTime;
	}

	public void setLastBuildTime(long lastBuildTime) {
		this.lastBuildTime = lastBuildTime;
	}

	public long getLastOpenShop() {
		return this.lastOpenShop;
	}

	public void setLastOpenShop(long lastOpenShop) {
		this.lastOpenShop = lastOpenShop;
	}

	public boolean isGetGoods() {
		return this.isGetGoods;
	}

	public void setGetGoods(boolean isGetGoods) {
		this.isGetGoods = isGetGoods;
	}

	public Map<Integer, Integer> getNormalGoodsRecord() {
		return this.normalGoodsRecord;
	}

	public void setNormalGoodsRecord(Map<Integer, Integer> normalGoodsRecord) {
		this.normalGoodsRecord = normalGoodsRecord;
	}

	public int getTotalContribution() {
		return this.totalContribution;
	}

	public void setTotalContribution(int totalContribution) {
		this.totalContribution = totalContribution;
	}

	public int getBoxCount() {
		return this.boxCount;
	}

	public void setBoxCount(int boxCount) {
		this.boxCount = boxCount;
	}

	public int getBoxGetCount() {
		return this.boxGetCount;
	}

	public void setBoxGetCount(int boxGetCount) {
		this.boxGetCount = boxGetCount;
	}

	public int getBoxCost() {
		return this.boxCost;
	}

	public void setBoxCost(int boxCost) {
		this.boxCost = boxCost;
	}

	public int getBoxTotalCount() {
		return this.boxTotalCount;
	}

	public void setBoxTotalCount(int boxTotalCount) {
		this.boxTotalCount = boxTotalCount;
	}

	public int getBoxExchangeCount() {
		return this.boxExchangeCount;
	}

	public void setBoxExchangeCount(int boxExchangeCount) {
		this.boxExchangeCount = boxExchangeCount;
	}

	public static LeagueMember readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();
			int id = in.readInt();

			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.readFully(bytes);
			String name = new String(bytes, Charset.forName("utf-8"));

			LeagueMember lm = new LeagueMember(id, name);
			lm.lastBuildId = in.readInt();
			lm.lastBuildTime = in.readLong();
			lm.lastOpenShop = in.readLong();
			lm.isGetGoods = in.readBoolean();
			lm.totalContribution = in.readInt();

			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				int goodsId = in.readInt();
				int count = in.readInt();
				lm.normalGoodsRecord.put(Integer.valueOf(goodsId), Integer.valueOf(count));
			}

			if (version > 1) {
				lm.boxCount = in.readInt();
				lm.boxGetCount = in.readInt();
				lm.boxCost = in.readInt();
				lm.boxTotalCount = in.readInt();
				lm.boxExchangeCount = in.readInt();
			}
			return lm;
		} catch (IOException e) {
			Platform.getLog().logError("read LeagueMember error", e);
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(2);
		out.writeInt(this.id);

		out.writeInt(this.name.getBytes(Charset.forName("utf-8")).length);
		out.write(this.name.getBytes(Charset.forName("utf-8")));

		out.writeInt(this.lastBuildId);
		out.writeLong(this.lastBuildTime);
		out.writeLong(this.lastOpenShop);
		out.writeBoolean(this.isGetGoods);
		out.writeInt(this.totalContribution);

		out.writeInt(this.normalGoodsRecord.size());
		for (Integer id : this.normalGoodsRecord.keySet()) {
			int count = ((Integer) this.normalGoodsRecord.get(id)).intValue();
			out.writeInt(id.intValue());
			out.writeInt(count);
		}

		out.writeInt(this.boxCount);
		out.writeInt(this.boxGetCount);
		out.writeInt(this.boxCost);
		out.writeInt(this.boxTotalCount);
		out.writeInt(this.boxExchangeCount);
	}
}
