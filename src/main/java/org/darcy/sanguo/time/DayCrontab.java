package org.darcy.sanguo.time;

import java.util.Calendar;

public class DayCrontab extends Crontab {
	public static int FIELD_COUNT = 4;

	private int[] time = new int[FIELD_COUNT];

	private boolean[] mod = new boolean[FIELD_COUNT];
	private int eventType;
	private int[] type = { 7, 11, 12, 13 };

	private boolean mark = false;

	public DayCrontab(String config, int eventType) {
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

	public boolean equals(DayCrontab cron) {
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
			int calValue = cal.get(this.type[i]);
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

	public long getNextFireTime() {
		Calendar cur = Calendar.getInstance();
		for (int i = 0; i < this.type.length; ++i) {
			if (this.time[i] >= 0) {
				cur.set(this.type[i], this.time[i]);
			}
		}
		long l = cur.getTimeInMillis();
		if (l < System.currentTimeMillis()) {
			cur.add(5, 7);
		}
		return cur.getTimeInMillis();
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
