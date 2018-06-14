package org.darcy.sanguo.hotfix;

import java.lang.instrument.Instrumentation;

public class Agent {
	public static void premain(String arg, Instrumentation inst) {
		HotSwap.inst = inst;
		System.out.println("hotswap start ok");
	}
}