package org.darcy.sanguo.worldcompetition;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.CallBackable;

public class WorldCompetitionSortRunnable implements Runnable {
	public void run() {
		CallBackable call = new CallBackable() {
			public void callback() {
				((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class)).sort();
				((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
						.clearOutData();
			}
		};
		Platform.getCallBackManager().addCallBack(call);
	}
}
