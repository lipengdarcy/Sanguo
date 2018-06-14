package org.darcy.sanguo.worldcompetition;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.drop.Reward;

public class WorldCompetitionData {
	public static final int MAX_RANK_NUM = 5000;
	public static final int SCORE_BEST = 0;
	public static final int SCORE_BETTER = 1;
	public static final int SCORE_NORMAL = 2;
	public static final int SCORE_LESS = 3;
	public static final int SCORE_LEAST = 4;
	public static final double[] scoreLimit = { 1.0D, 0.75D, 0.5D, 0.25D, 0.0D };

	public static final int[] rankLimit = { 100, -100, -500 };
	public static final int DAY_COUNT = 20;
	public static final int COST_STAMINA = 2;
	public static int baseScore;
	public static int winBaseScore;
	public static int loseBaseScore;
	public static int maxScore;
	public static int winScoreRatio;
	public static int loseScoreRatio;
	public static int winExp;
	public static int loseExp;
	public static int winHonor;
	public static int cd;
	public static int[] startTime = { 8 };

	public static int[] endTime = { 23 };

	public static int[] runWeekDay = { 2, 3, 4, 5, 6, 7 };

	public static Map<Integer, WorldCompetitionReward> rewards = new HashMap();

	public static boolean isOver(Calendar cal) {
		int[] arrayOfInt;
		int weekDay = cal.get(7);
		int j = (arrayOfInt = runWeekDay).length;
		for (int i = 0; i < j; ++i) {
			Integer tmp = Integer.valueOf(arrayOfInt[i]);
			if (weekDay == tmp.intValue()) {
				if (weekDay == 7) {
					return (beforeEnd(cal));
				}

				return false;
			}
		}

		return true;
	}

	public static boolean inCompeteTime() {
		Calendar now = Calendar.getInstance();
		if (isOver(now)) {
			return false;
		}

		return ((!(afterStart(now))) || (!(beforeEnd(now))));
	}

	private static boolean afterStart(Calendar cal) {
		int hour = cal.get(11);
		if (hour > startTime[0])
			return true;
		if (hour < startTime[0]) {
			return false;
		}
		int min = cal.get(12);
		if (min > startTime[1])
			return true;
		if (min < startTime[1]) {
			return false;
		}
		int sec = cal.get(13);

		return (sec < startTime[2]);
	}

	private static boolean beforeEnd(Calendar cal) {
		int hour = cal.get(11);
		if (hour < endTime[0])
			return true;
		if (hour > endTime[0]) {
			return false;
		}
		int min = cal.get(12);
		if (min < endTime[1])
			return true;
		if (min > endTime[1]) {
			return false;
		}
		int sec = cal.get(13);

		return (sec >= endTime[2]);
	}

	public static List<Reward> getRewardsByRank(int rank) {
		for (WorldCompetitionReward competitionReward : rewards.values()) {
			if (rank == -1) {
				if (competitionReward.end != -1)
					continue;
				return competitionReward.rewards;
			}

			if (competitionReward.end == -1) {
				if (rank < competitionReward.start)
					continue;
				return competitionReward.rewards;
			}

			if ((rank >= competitionReward.start) && (rank <= competitionReward.end)) {
				return competitionReward.rewards;
			}

		}

		return null;
	}

	public static Calendar getCurRewardTime() {
		Calendar cal = Calendar.getInstance();
		if (cal.get(7) == 1) {
			cal.add(3, -1);
		}
		cal.set(7, 7);
		cal.set(11, 23);
		cal.set(12, 10);
		cal.set(13, 0);
		cal.set(14, 0);
		return cal;
	}
}
