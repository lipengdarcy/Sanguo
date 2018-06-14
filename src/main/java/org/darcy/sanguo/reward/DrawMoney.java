package org.darcy.sanguo.reward;

import java.util.ArrayList;
import java.util.List;

public class DrawMoney {
	public int num;
	public int cost;
	public List<DrawMoneyWeight> rewards = new ArrayList();

	public int getMaxGet() {
		return ((DrawMoneyWeight) this.rewards.get(this.rewards.size() - 1)).end;
	}

	public int getReward() {
		int start = 0;
		int end = 0;
		int sum = 0;
		for (DrawMoneyWeight dmw : this.rewards) {
			sum += dmw.weight;
		}
		int rnd = (int) (Math.random() * sum);
		int i = 0;
		for (DrawMoneyWeight dmw : this.rewards) {
			i += dmw.weight;
			if (rnd < i) {
				start = dmw.start;
				end = dmw.end;
				break;
			}
		}
		return ((int) (Math.random() * (end - start + 1)) + start);
	}

	public class DrawMoneyWeight {
		public int weight;
		public int start;
		public int end;

		
	}
}
