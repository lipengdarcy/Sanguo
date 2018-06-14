package org.darcy.sanguo.awardcenter;

public class AwardsSaveCall implements Runnable {
	private Awards awards;

	public AwardsSaveCall(Awards awards) {
		this.awards = awards;
	}

	public void run() {
		this.awards.save();
	}
}
