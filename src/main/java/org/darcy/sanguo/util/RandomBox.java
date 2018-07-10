package org.darcy.sanguo.util;

import org.darcy.sanguo.Platform;

public class RandomBox {
	public static final int RANDOM_COUNT = 20;
	private int[] randoms;
	private int randomIndex;

	public RandomBox() {
		this.randomIndex = 0;

		this.randoms = new int[20];
		for (int i = 0; i < 20; ++i)
			this.randoms[i] = Calc.nextInt(10000);
	}

	public int getNextRandom() {
		if (this.randomIndex >= 20) {
			this.randomIndex = 0;
		}
		int rst = this.randoms[(this.randomIndex++)];
		Platform.getLog().logCombat("use random : " + rst);
		return rst;
	}

	public int[] getRandoms() {
		return this.randoms;
	}
}
