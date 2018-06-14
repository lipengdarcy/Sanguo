package org.darcy.sanguo.drop;

public abstract class AbstractDrop implements Drop {
	int type;
	float rate;

	public int getType() {
		return this.type;
	}

	public float getRate() {
		return this.rate;
	}
}
