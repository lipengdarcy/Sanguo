package org.darcy.sanguo.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.updater.Updatable;

public class EventManager implements Updatable {
	protected Queue<Event> events = new ConcurrentLinkedQueue<Event>();
	protected HashMap<Integer, ArrayList<EventHandler>> listeners = new HashMap<Integer, ArrayList<EventHandler>>();

	public void registerListener(EventHandler listener) {
		int[] events = listener.getEventCodes();
		for (int i = 0; i < events.length; ++i) {
			ArrayList<EventHandler> list = (ArrayList<EventHandler>) this.listeners.get(Integer.valueOf(events[i]));
			if (list != null) {
				list.add(listener);
			} else {
				list = new ArrayList<EventHandler>();
				list.add(listener);
				this.listeners.put(Integer.valueOf(events[i]), list);
			}
		}
	}

	public void addEvent(Event event) {
		this.events.offer(event);
	}

	public boolean update() {
		while (!(this.events.isEmpty())) {
			Event evt = (Event) this.events.remove();
			List<EventHandler> lls = (List<EventHandler>) this.listeners.get(Integer.valueOf(evt.type));
			if (lls != null) {
				for (EventHandler l : lls) {
					try {
						l.handleEvent(evt);
					} catch (Throwable e) {
						Platform.getLog().logError(e);
					}
				}
			}
		}
		return false;
	}
}
