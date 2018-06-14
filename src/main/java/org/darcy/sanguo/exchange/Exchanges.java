package org.darcy.sanguo.exchange;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.service.ExchangeService;
import org.darcy.sanguo.vip.Vip;

import sango.packet.PbExchange;

public class Exchanges implements PlayerBlobEntity {
	private static final long serialVersionUID = 2286169875991742553L;
	private static int version = 2;

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Map<Integer, Integer> exchanges = new HashMap();

	private long lastWeekRefreshTime = System.currentTimeMillis();

	public void updateExchanges(int id, int count) {
		this.exchanges.put(Integer.valueOf(id), Integer.valueOf(count));
	}

	public int getCount(int id) {
		return ((Integer) this.exchanges.get(Integer.valueOf(id))).intValue();
	}

	public int getSurplusCount(int id, Vip vip) {
		ExchangeTemplate template = ((ExchangeService) Platform.getServiceManager().get(ExchangeService.class))
				.getTemplate(id);
		if (template.count == -1) {
			return -1;
		}
		int count = 0;
		if (this.exchanges.get(Integer.valueOf(id)) != null) {
			count = ((Integer) this.exchanges.get(Integer.valueOf(id))).intValue();
		}
		int surplus = template.count - count;
		if (vip.shopLimits.get(Integer.valueOf(id)) != null) {
			surplus = ((Integer) vip.shopLimits.get(Integer.valueOf(id))).intValue() - count;
		}
		if (surplus < 0) {
			surplus = 0;
			updateExchanges(id, template.count);
		}
		return surplus;
	}

	public PbExchange.ExchangeData genExchange(int id, Vip vip) {
		ExchangeTemplate template = ((ExchangeService) Platform.getServiceManager().get(ExchangeService.class))
				.getTemplate(id);
		if (!(this.exchanges.containsKey(Integer.valueOf(id)))) {
			updateExchanges(id, 0);
		}
		PbExchange.ExchangeData.Builder builder = PbExchange.ExchangeData.newBuilder();
		builder.setId(template.id);
		builder.setReward(template.reward.genPbReward());
		builder.setCountType(PbExchange.ExchangeCountType.valueOf(template.countType));
		builder.setCostType(template.costType);
		builder.setBuyCount(getCount(id));
		for (Reward reward : template.costs) {
			builder.addCost(reward.genPbReward());
		}
		builder.setSurplusCount(getSurplusCount(id, vip));
		return builder.build();
	}

	public void refresh() {
		long now = System.currentTimeMillis();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		cal.set(7, 2);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		cal.set(14, 0);
		if (now < cal.getTimeInMillis()) {
			cal.add(3, -1);
		}
		boolean weekRefresh = this.lastWeekRefreshTime < cal.getTimeInMillis();

		ExchangeService service = (ExchangeService) Platform.getServiceManager().get(ExchangeService.class);
		Iterator itx = this.exchanges.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			ExchangeTemplate template = service.getTemplate(id);
			if (template == null) {
				itx.remove();
			} else if ((template.countType == 1) && (weekRefresh))
				updateExchanges(id, 0);
			else if (template.countType == 2) {
				updateExchanges(id, 0);
			}
		}

		if (weekRefresh)
			this.lastWeekRefreshTime = now;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();
			this.exchanges = new HashMap();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				int id = in.readInt();
				int count = in.readInt();
				this.exchanges.put(Integer.valueOf(id), Integer.valueOf(count));
			}
			if (version > 1)
				this.lastWeekRefreshTime = in.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(this.exchanges.size());
		Iterator itx = this.exchanges.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			int count = ((Integer) this.exchanges.get(Integer.valueOf(id))).intValue();
			out.writeInt(id);
			out.writeInt(count);
		}
		out.writeLong(this.lastWeekRefreshTime);
	}

	public Map<Integer, Integer> getExchanges() {
		return this.exchanges;
	}

	public void setExchanges(Map<Integer, Integer> exchanges) {
		this.exchanges = exchanges;
	}

	public long getLastWeekRefreshTime() {
		return this.lastWeekRefreshTime;
	}

	public void setLastWeekRefreshTime(long lastWeekRefreshTime) {
		this.lastWeekRefreshTime = lastWeekRefreshTime;
	}

	public int getBlobId() {
		return 7;
	}
}
