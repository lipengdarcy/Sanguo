package org.darcy.sanguo.event;

public abstract interface EventHandler {
	
	public abstract int[] getEventCodes();

	public abstract void handleEvent(Event paramEvent);
}
