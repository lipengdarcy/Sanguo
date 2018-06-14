package org.darcy.sanguo.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.darcy.sanguo.player.Player;

import sango.packet.PbLeague;

public class LeagueRareGoods {
	public static final int version = 1;
	private int count;
	private LeagueRareGoodTemplate template;
	private Set<Integer> customers = new HashSet();

	public LeagueRareGoods(LeagueRareGoodTemplate template) {
		this.template = template;
		this.count = template.count;
	}

	public void exchange(Player player) {
		this.count -= 1;
		if (this.count < 0) {
			this.count = 0;
		}

		this.template.reward.add(player, "leagueexchange");
		this.customers.add(Integer.valueOf(player.getId()));
		player.getUnion().decContribution(player, getNeedContribution(), "leagueexchange");
	}

	public boolean canBuy(int id) {
		return (!(this.customers.contains(Integer.valueOf(id))));
	}

	public int getNeedContribution() {
		return this.template.cost;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public LeagueRareGoodTemplate getTemplate() {
		return this.template;
	}

	public void setTemplate(LeagueRareGoodTemplate template) {
		this.template = template;
	}

	public Set<Integer> getCustomers() {
		return this.customers;
	}

	public void setCustomers(Set<Integer> customers) {
		this.customers = customers;
	}

	public PbLeague.LeagueRareGoods genLeagueRareGoods(int playerId) {
		PbLeague.LeagueRareGoods.Builder b = PbLeague.LeagueRareGoods.newBuilder();
		b.setId(this.template.id);
		b.setCount(this.count);
		b.setCost(getNeedContribution());

		b.setGoods(this.template.reward.genPbReward());
		b.setCan(canBuy(playerId));
		return b.build();
	}

	public static LeagueRareGoods readObject(ObjectInputStream in) {
		try {
			in.readInt();
			int templateId = in.readInt();
			LeagueRareGoodTemplate temlate = (LeagueRareGoodTemplate) LeagueService.rareGoods
					.get(Integer.valueOf(templateId));
			LeagueRareGoods lrg = new LeagueRareGoods(temlate);
			lrg.count = in.readInt();

			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				lrg.customers.add(Integer.valueOf(in.readInt()));
			}
			return lrg;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(1);
		out.writeInt(this.template.id);
		out.writeInt(this.count);

		out.writeInt(this.customers.size());
		for (Integer id : this.customers)
			out.writeInt(id.intValue());
	}
}
