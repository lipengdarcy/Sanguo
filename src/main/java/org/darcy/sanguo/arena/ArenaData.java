package org.darcy.sanguo.arena;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;

public class ArenaData {
	public static final int RIVAL_COUNT = 10;
	public static final int LOW_RIVAL_COUNT = 2;
	public static int challengeDayCount;
	public static int winMoneyRatio;
	public static int loseMoneyRatio;
	public static int winExpRatio;
	public static int loseExpRatio;
	public static int costStamina;
	public static int winPrestige;
	public static int losePrestige;
	public static int[] rankArray;
	public static int[] intervalArray;

	public static int[] getRivals(int rank) {
		int[] result = new int[10];
		int arenaCount = Platform.getWorld().getCurArenaCount();
		if (arenaCount < 10) {
			result = new int[arenaCount - 1];
			int i = 0;
			while (true) {
				if (i + 1 != rank)
					result[i] = (i + 1);
				++i;
				if (i >= result.length) {
					label40: return result;
				}
			}
		}

		if (rank <= 8) {
			int j = 0;
			for (int i = 0; i < result.length; ++i) {
				if (j + 1 != rank)
					result[i] = (j + 1);
				else {
					--i;
				}
				++j;
			}
		} else {
			int interval;
			int tmpRank = rank;
			int highRank = 8;
			List rankList = new ArrayList();
			for (int i = 0; i < 2; ++i) {
				interval = getInterval(tmpRank);
				tmpRank += interval;
				if (tmpRank > arenaCount)
					++highRank;
				else {
					rankList.add(Integer.valueOf(tmpRank));
				}
			}

			tmpRank = rank;
			for (int i = 0; i < highRank; ++i) {
				interval = getInterval(tmpRank);

				tmpRank -= interval;
				rankList.add(Integer.valueOf(tmpRank));
			}
			for (int i = 0; i < result.length; ++i) {
				result[i] = ((Integer) rankList.get(i)).intValue();
			}
		}
		return result;
	}

	private static int getInterval(int rank) {
		int interval = 0;
		for (int j = 0; j < rankArray.length; ++j) {
			if (j == rankArray.length - 1) {
				if (rank <= rankArray[j])
					continue;
				interval = intervalArray[j];
				break;
			}

			if ((rank > rankArray[j]) && (rank <= rankArray[(j + 1)])) {
				interval = intervalArray[j];
				break;
			}
		}

		return interval;
	}
}
