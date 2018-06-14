package org.darcy.sanguo.time;

import java.util.Calendar;

public class Crontab {
	public static int FIELD_COUNT = 3;

	public static int HOUR = 0;
	public static int MIN = 0;
	public static int SEC = 0;

	private int[] time = new int[FIELD_COUNT];

	private boolean[] mod = new boolean[FIELD_COUNT];
	private int eventType;
	private boolean mark = false;

	protected Crontab() {
	}

	public Crontab(String config, int eventType) {
		this.eventType = eventType;
		try {
			String[] ls = config.split(" ");
			for (int i = 0; i < FIELD_COUNT; ++i)
				if (ls[i].contains("/")) {
					this.mod[i] = true;
					this.time[i] = Integer.parseInt(ls[i].split("/")[1]);
				} else if (ls[i].equals("*")) {
					this.mod[i] = false;
					this.time[i] = -1;
				} else {
					this.mod[i] = false;
					this.time[i] = Integer.parseInt(ls[i]);
				}
		} catch (Exception e) {
			throw new RuntimeException("schedual config error :" + config);
		}
		Time.registeCrontab(this);
	}

	public boolean equals(Crontab cron) {
		if (this.eventType != cron.getEventType())
			return false;
		for (int i = 0; i < this.mod.length; ++i) {
			if (this.mod[i] != cron.mod[i]) {
				return false;
			}
		}

		for (int i = 0; i < this.time.length; ++i) {
			if (this.time[i] != this.time[i]) {
				return false;
			}
		}

		return true;
	}

	public int getEventType() {
		return this.eventType;
	}

	private boolean innerMatch(Calendar cal) {
		for (int i = 0; i < FIELD_COUNT; ++i) {
			int calValue = cal.get(i + 11);
			if (this.mod[i]) {
				if (calValue % this.time[i] == 0)
					continue;
				return false;
			}
			if ((this.time[i] != -1) && (this.time[i] != calValue)) {
				return false;
			}

		}

		return true;
	}

	public boolean match(Calendar cal) {
		boolean rst = innerMatch(cal);
		if (rst) {
			if (this.mark) {
				return false;
			}
			this.mark = true;
			if (this.eventType == 1002) {
				System.err.println(this);
			}
			return true;
		}

		this.mark = false;
		return false;
	}
}
