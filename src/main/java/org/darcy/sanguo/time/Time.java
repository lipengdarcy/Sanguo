package org.darcy.sanguo.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.event.Event;

public class Time implements Runnable {
	private static List<Crontab> crontabs = new ArrayList<Crontab>();

	public static void registeCrontab(Crontab crontab) {
		boolean has = false;
		for (Crontab cron : crontabs) {
			if (cron.equals(crontab)) {
				has = true;
				break;
			}
		}
		if (!(has))
			crontabs.add(crontab);
	}

	public boolean update() {
		Calendar cal = Calendar.getInstance();
		for (Crontab cron : crontabs) {
			if (cron.match(cal)) {
				Platform.getEventManager().addEvent(new Event(cron.getEventType()));
			}
		}

		return false;
	}

	public void run() {
		try {
			Thread.sleep(100L);
			update();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
