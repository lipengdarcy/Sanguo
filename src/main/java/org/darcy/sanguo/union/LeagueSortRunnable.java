package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.CallBackable;

public class LeagueSortRunnable implements Runnable {
	public void run() {
		CallBackable call = new CallBackable() {
			public void callback() {
				Platform.getLog().logSystem("league sort start...");
				Map rankLeagues = Platform.getLeagueManager().getRankLeagues();
				rankLeagues.clear();

				List<League> list = new ArrayList(Platform.getLeagueManager().getLeagues().values());

				Collections.sort(list, new LeagueManager.LeagueSortComparator());

				for (int i = 1; i <= list.size(); ++i) {
					((League) list.get(i - 1)).setRank(i);
				}

				for (League l : list) {
					rankLeagues.put(Integer.valueOf(l.getRank()), l);
				}

				Platform.getLog().logSystem("league sort end...");
			}
		};
		Platform.getCallBackManager().addCallBack(call);
	}
}
