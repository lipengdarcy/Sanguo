package org.darcy.sanguo;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
	public static int serverId;
	public static String name;
	public static String resourcedir;
	public static String serverIp;
	public static int serverPort = 8080;
	public static String[] gateIps;
	public static int[] gatePorts;
	public static boolean test = false;
	public static String channel = "";
	public static String gmIp;
	public static int gmPort;
	public static String billingAdd;
	public static String version;
	public static final int MAX_PLAYERS = 3000;
	public static int[] numbers;
	public static boolean pushPay = false;
	public static String gmHeart;
	public static Map<Integer, Integer> functions = new HashMap<Integer, Integer>();
}
