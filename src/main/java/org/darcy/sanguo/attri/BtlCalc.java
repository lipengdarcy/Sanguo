package org.darcy.sanguo.attri;

public class BtlCalc {
	public static int[] FACTORS = new int[50];

	public static int calc(Attributes attributes) {
		int rst = 0;
		for (int i = 0; i < FACTORS.length; ++i) {
			int value = attributes.get(i);
			int factor = FACTORS[i];
			rst += (int) (value * factor / 10L);
		}
		if (rst < 0) {
			rst = 2147483647;
		}
		return rst;
	}
}
