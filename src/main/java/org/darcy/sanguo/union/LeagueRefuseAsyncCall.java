package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.List;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;

import sango.packet.PbDown;

public class LeagueRefuseAsyncCall extends AsyncCall {
	League l;
	List<Integer> ids;
	List<Union> us;
	int errorNum = 0;

	public LeagueRefuseAsyncCall(ClientSession session, League l, List<Integer> ids) {
		super(session, null);
		this.l = l;
		this.ids = ids;
	}

	public void callback() {
		PbDown.LeagueRefuseRst.Builder b = PbDown.LeagueRefuseRst.newBuilder().setResult(true);

		if (this.errorNum == 0) {
			for (Union u : this.us) {
				int id = u.getPlayerId();
				if (this.l.isMember(id))
					continue;
				if (!(this.l.isApply(id))) {
					continue;
				}
				this.l.removeApply(id);
				u.removeApply(this.l.getId());

				if (Platform.getPlayerManager().getPlayerById(id) == null) {
					UnionSaveCall call = new UnionSaveCall(u);
					Platform.getThreadPool().execute(call);
				}

				Platform.getLog().logLeague(this.l, "leaguerefuse", id);
			}
		}
		this.session.send(1196, b.build());
	}

	public void netOrDB()
   {
     this.us = new ArrayList();
     label165: for (Integer id : this.ids) {
       Player player = Platform.getPlayerManager().getPlayerById(id.intValue());
       if (player != null) {
         this.us.add(player.getUnion());
       } else {
         Union u = null;
         try {
           u = (Union)((DbService)Platform.getServiceManager().get(DbService.class)).get(Union.class, id);
         } catch (Exception e) {
           Platform.getLog().logError("LeagueRefuseAsyncCall get union error, id:" + id, e);
         }
         if (u == null) {
           Platform.getLog().logWarn("LeagueRefuseAsyncCall union null, id:" + id);
         }
         else
           this.us.add(u);
       }
     }
   }
}
