package org.darcy.sanguo.robot;
/**
 * 系统随机生成的玩家
 * */
public class RobotData {
	public static final int TYPE_ARENA = 1;
	public static final int TYPE_WORLD = 2;
	public static final int TYPE_LOOT = 3;
	public int id;
	public int type;
	public int num;
	public int minLevel;
	public int maxLevel;
	public String accountPrefix;
	public double warriorLevelRatio;
	public int mainWarriorMaleId;
	public int mainWarriorFemaleId;
	public int warriorNum;
	public int purpleNum;
	public int blueNum;
	public int[] purplePool;
	public int[] bluePool;
	public int[] greenPool;
	public int[] whitePool;
	public boolean inDb;

	public int getMainWarriorId(int gender) {
		if (gender == 1) {
			return this.mainWarriorMaleId;
		}
		return this.mainWarriorFemaleId;
	}
}
