package org.darcy.sanguo.hotfix;

import org.darcy.sanguo.Platform;

public class HotFix implements Runnable {
	public void run() {
		Platform.getLog().logWorld("i'm hotfix");
	}
}