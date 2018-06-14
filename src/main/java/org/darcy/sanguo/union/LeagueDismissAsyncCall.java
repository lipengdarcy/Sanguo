package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.asynccall.AsyncDelete;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;

public class LeagueDismissAsyncCall extends AsyncCall {
	League l;
	List<Union> applys;

	public LeagueDismissAsyncCall(League l) {
		super(null, null);
		this.l = l;
	}

	public void callback() {
		for (Union u : this.applys) {
			u.removeApply(this.l.getId());

			if (Platform.getPlayerManager().getPlayerById(u.getPlayerId()) == null) {
				UnionSaveCall call = new UnionSaveCall(u);
				Platform.getThreadPool().execute(call);
			}
		}

		AsyncDelete call = new AsyncDelete(this.l, Integer.valueOf(this.l.getId()));
		Platform.getThreadPool().execute(call);

		Platform.getLog().logLeague(this.l, "leaguedismiss", -1);
	}

	public void netOrDB() {
		this.applys = new ArrayList();
		for (Iterator localIterator = this.l.getInfo().getApplys().iterator(); localIterator.hasNext();) {
			int id = ((Integer) localIterator.next()).intValue();
			Player player = Platform.getPlayerManager().getPlayerById(id);
			if (player != null) {
				this.applys.add(player.getUnion());
			} else {
				Union u = null;
				try {
					u = (Union) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Union.class,
							Integer.valueOf(id));
				} catch (Exception e) {
					Platform.getLog().logError("LeagueDismissAsyncCall get union error, id:" + id, e);
				}
				if (u == null) {
					Platform.getLog().logWarn("LeagueDismissAsyncCall union null, id:" + id);
				} else
					this.applys.add(u);
			}
		}
	}
}
