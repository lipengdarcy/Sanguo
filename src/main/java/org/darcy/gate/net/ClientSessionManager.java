package org.darcy.gate.net;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientSessionManager implements Runnable {
	private static AtomicInteger idGener = new AtomicInteger();

	public static ConcurrentHashMap<Integer, ClientSession> sessions = new ConcurrentHashMap<Integer, ClientSession>();
	private static boolean running = true;

	public static int getNewId() {
		return idGener.incrementAndGet();
	}

	public static ClientSession getClientSession(int id) {
		return ((ClientSession) sessions.get(Integer.valueOf(id)));
	}

	public static void addClientSession(ClientSession session) {
		sessions.put(Integer.valueOf(session.getId()), session);
	}

	public boolean update() {
		try {
			if (running) {
				Iterator<ClientSession> iter = sessions.values().iterator();
				while (iter.hasNext()) {
					ClientSession session = (ClientSession) iter.next();
					if (session.isDisconnect()) {
						sessions.remove(session.getId());
						session.close();
					} else {
						session.update();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void shutdown() throws InterruptedException {
		running = false;
		for (ClientSession cs : sessions.values())
			cs.close().sync();
	}

	public void run() {
		while (running)
			try {
				long time = System.currentTimeMillis();
				update();
				long diff = System.currentTimeMillis() - time;
				long left = 100L - diff;
				if (left > 0L) {
					Thread.sleep(left);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
