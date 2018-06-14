package org.darcy.sanguo.recruit;

import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.Reward;

public class RecruitTemplate {
	public static final int RECRUIT_GOOD = 1;
	public static final int RECRUIT_BETTER = 2;
	public static final int RECRUIT_BEST = 3;
	public int id;
	public String desc;
	public int freeNum;
	public int freeInterval;
	public int initInterval;
	public Reward singleCost;
	public Reward tenCost;
	public DropGroup[] drops;
	public DropGroup firstDrop;
	public DropGroup insureDrop;
	public int firstInsureCount;
	public int normalInsureCount;
}
