package org.darcy.sanguo.drop;

public abstract interface Drop {
	public static final int ITEM = 0;
	public static final int SUBDROP = 1;
	public static final int MONEY = 2;
	public static final int JEWEL = 3;
	public static final int PLAYER_EXP = 4;
	public static final int VITALITY = 5;
	public static final int STAMINA = 6;
	public static final int SPIRITJADE = 7;
	public static final int WARRIORSPIRIT = 8;
	public static final int HONOR = 9;
	public static final int PRESTIGE = 10;
	public static final int TACTICPOINT = 11;
	public static final int CHARGE = 12;
	public static final int CONTRIBUTION = 13;
	public static final int TRAINPOINT = 14;

	public abstract int getType();

	public abstract float getRate();
}
