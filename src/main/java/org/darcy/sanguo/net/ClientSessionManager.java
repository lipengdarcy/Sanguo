package org.darcy.sanguo.net;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.updater.Updatable;

public class ClientSessionManager implements Updatable {
	public ConcurrentHashMap<Integer, ClientSession> sessions = new ConcurrentHashMap<Integer, ClientSession>();
	private boolean running = true;

	public ClientSession getClientSession(int id) {
		return ((ClientSession) this.sessions.get(Integer.valueOf(id)));
	}

	public void addClientSession(ClientSession session) {
		this.sessions.put(Integer.valueOf(session.getId()), session);
	}

	public boolean update() {
		try {
			if (this.running) {
				Iterator<ClientSession> iter = this.sessions.values().iterator();
				while (iter.hasNext()) {
					ClientSession session = (ClientSession) iter.next();
					if (session.isDisconnect()) {
						this.sessions.remove(Integer.valueOf(session.getId()));
						session.close();
						Platform.getLog().logWorld("ClientSession disconnect, playerId:" + session.getPid());
					} else {
						session.update();
					}
				}
			}
		} catch (Throwable e) {
			Platform.getLog().logError(e);
		}
		return false;
	}

	public void shutdown() {
		this.running = false;
		for (ClientSession cs : this.sessions.values())
			cs.close();
	}
}
