package org.darcy.sanguo.util;

import java.util.HashMap;
import java.util.Map;

public class PlayerLockService {
	private static Map<String, String> roleLockMap = new HashMap<String, String>();
	private static final int MAX_LIMIT = 200000;
	private static int putNum = 0;

	private static String DEFAULT_LOCK = "global_lock";
	private static String INNER_LOCK = "inner_lock";

	public static String getLock(int key) {
		return getLock(String.valueOf(key));
	}

	public static String getLock(String key) {
		if (key == null) {
			return DEFAULT_LOCK;
		}
		if (putNum >= 200000) {
			roleLockMap.clear();
			putNum = 0;
		}

		String retValue = (String) roleLockMap.get(key);
		if (retValue == null) {
			synchronized (INNER_LOCK) {
				retValue = (String) roleLockMap.get(key);
				if (retValue == null) {
					roleLockMap.put(key, key);
					retValue = key;
					putNum += 1;
				}
			}
		}

		return retValue;
	}

	public static void main(String[] args) {
		String a = getLock("a");
		String b = getLock("a");
		System.err.println("same:" + a + "|" + b + "==" + (a == b));
	}
}
