package org.darcy.sanguo.service.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.mail.Mail;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.union.Union;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.worldcompetition.WorldCompetition;
import org.darcy.sanguo.worldcompetition.WorldCompetitionService;

import sango.packet.PbDown;

public class FunctionService implements Service, EventHandler {
	public static Map<Integer, Function> functions = new HashMap<Integer, Function>();

	public void startup() throws Exception {
		loadFunction();
		Platform.getEventManager().registerListener(this);
	}

	private void loadFunction() {
		List<Row> list = ExcelUtils.getRowList("function.xls");
		for (Row row : list) {		
				int pos = 0;
				if (row == null) {
					return;
				}
				if (row.getCell(pos) == null) {
					return;
				}
				int id = (int) row.getCell(pos++).getNumericCellValue();
				String name = row.getCell(pos++).getStringCellValue();
				int isShow = (int) row.getCell(pos++).getNumericCellValue();
				int iconId = (int) row.getCell(pos++).getNumericCellValue();
				int level = (int) row.getCell(pos++).getNumericCellValue();
				String openContent = row.getCell(pos++).getStringCellValue();
				String needContent = row.getCell(pos++).getStringCellValue();
				int jump = (int) row.getCell(pos++).getNumericCellValue();
				Function f = new Function();
				f.id = id;
				f.name = name;
				f.isShow = (isShow == 1);
				f.iconId = iconId;
				f.openLevel = level;
				f.openContent = openContent;
				f.needContent = needContent;
				f.jump = jump;
				functions.put(Integer.valueOf(id), f);
			}
		}

	public static boolean isOpenFunction(int level, int functionId) {
		Function f = (Function) functions.get(Integer.valueOf(functionId));
		if (f == null) {
			return false;
		}

		return (level < f.openLevel);
	}

	public static void openNewFunction(Player player) {
		if ((player.getWorldCompetition() == null) && (isOpenFunction(player.getLevel(), 21))) {
			WorldCompetition competition = WorldCompetition.newWorldCompetition(player.getId());
			((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
					.addCompetition(competition);
			((DbService) Platform.getServiceManager().get(DbService.class)).add(competition);
			player.setWorldCompetition(competition);
			boolean isError = ((WorldCompetitionService) Platform.getServiceManager()
					.get(WorldCompetitionService.class)).sort(competition, true);
			if (isError) {
				((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class)).sort();
			}
			((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class)).clearOutData();
		}

		if ((player.getArena() == null) && (isOpenFunction(player.getLevel(), 12))) {
			Arena arena = new Arena(Platform.getWorld().getNewArenaCount());
			arena.setPlayerId(player.getId());
			((DbService) Platform.getServiceManager().get(DbService.class)).add(arena);
			player.setArena(arena);
		}

		if ((player.getUnion() != null) || (!(isOpenFunction(player.getLevel(), 16))))
			return;
		Union u = new Union();
		u.setPlayerId(player.getId());
		((DbService) Platform.getServiceManager().get(DbService.class)).add(u);
		player.setUnion(u);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	public int[] getEventCodes() {
		return new int[] { 2034, 2047, 2037, 2038, 2023, 2048, 2083, 2035, 2049, 2036, 2050, 2040, 2041, 2051, 2052,
				2053, 2054, 2042, 2045, 2061, 2063, 2062, 2064, 2065, 2066, 2067, 2069, 2068, 2070, 2046, 2055, 2057,
				2031, 2085, 2058, 2059, 2060, 2028, 2072, 2073, 2074, 2075, 2076, 2077, 2079, 2080, 2081, 2082, 2084,
				2088, 2087, 2089, 2090, 2091, 2092, 2093, 2094, 2095, 2096, 2098, 2099, 2100, 2101, 2102, 2103, 2108,
				2109, 2104 };
	}

	public void handleEvent(Event event) {
		Player player = (Player) event.params[0];
		if (player.getSession() == null) {
			return;
		}
		if (event.type == 2028) {
			Function.notifyMainInterfaceFunction(player);
		} else if (event.type == 2034) {
			Function.notifyMainCheck(player, new int[] { 2 });
			Function.notifyMainCheck(player, new int[] { 27 });
		} else if (event.type == 2047) {
			Function.notifyMainCheck(player, new int[] { 2 });
		} else if (event.type == 2037) {
			Function.notifyMainNum(player, 3, 1);
		} else if (event.type == 2038) {
			Function.notifyMainNum(player, 31, 1);
		} else if (event.type == 2023) {
			Function.notifyMainCheck(player, new int[] { 3 });
		} else if (event.type == 2048) {
			Function.notifyMainCheck(player, new int[] { 31 });
		} else if (event.type == 2083) {
			Function.notifyMainNum(player, 31, 1);
		} else if (event.type == 2035) {
			Function.notifyMainNum(player, 4, 1);
		} else if (event.type == 2036) {
			Function.notifyMainNum(player, 5, 1);
		} else if (event.type == 2049) {
			Function.notifyMainCheck(player, new int[] { 4 });
		} else if (event.type == 2050) {
			Function.notifyMainCheck(player, new int[] { 5 });
		} else if (event.type == 2040) {
			Function.notifyMainNum(player, 7, 1);
		} else if ((event.type == 2051) || (event.type == 2052)) {
			Function.notifyMainCheck(player, new int[] { 7 });
		} else if (event.type == 2041) {
			Function.notifyMainNum(player, 29, 1);
		} else if (event.type == 2053) {
			Function.notifyMainCheck(player, new int[] { 29 });
		} else if (event.type == 2054) {
			Function.notifyMainCheck(player, new int[] { 17 });
		} else if (event.type == 2042) {
			Function.notifyMainCheck(player, new int[] { 8 });
		} else if (event.type == 2045) {
			Function.notifyMainCheck(player, new int[] { 16 });
		} else if (event.type == 2062) {
			Function.notifyMainCheck(player, new int[] { 20 });
		} else if (event.type == 2063) {
			Function.notifyMainCheck(player, new int[] { 20 });
		} else if (event.type == 2046) {
			player.send(1222, PbDown.TaskFinishedRst.newBuilder().setResult(true).build());
			Function.notifyMainNum(player, 9, 1);
		} else if (event.type == 2055) {
			Function.notifyMainCheck(player, new int[] { 9 });
		} else if (event.type == 2057) {
			Function.notifyMainCheck(player, new int[] { 11 });
		} else if ((event.type == 2031) || (event.type == 2085)) {
			Function.notifyMainCheck(player, new int[] { 12 });
		} else if (event.type == 2058) {
			Function.notifyMainCheck(player, new int[] { 13 });
		} else if (event.type == 2059) {
			Function.notifyMainCheck(player, new int[] { 14 });
		} else if (event.type == 2060) {
			Function.notifyMainCheck(player, new int[] { 15 });
		} else if (event.type == 2061) {
			Function.notifyMainCheck(player, new int[] { 18 });
		} else if (event.type == 2064) {
			Function.notifyMainCheck(player, new int[] { 21 });
		} else if (event.type == 2065) {
			Function.notifyMainCheck(player, new int[] { 22, 40 });
		} else if (event.type == 2066) {
			Function.notifyMainCheck(player, new int[] { 22 });
		} else if ((event.type == 2067) || (event.type == 2069)) {
			Function.notifyMainCheck(player, new int[] { 23 });
		} else if ((event.type == 2068) || (event.type == 2070)) {
			Function.notifyMainCheck(player, new int[] { 24 });
		} else if ((event.type == 2072) || (event.type == 2073) || (event.type == 2074)) {
			Function.notifyMainCheck(player, new int[] { 26 });
		} else if (event.type == 2075) {
			Function.notifyMainCheck(player, new int[] { 27 });
		} else if (event.type == 2076) {
			Function.notifyMainNum(player, 28, 1);
		} else if (event.type == 2077) {
			Function.notifyMainCheck(player, new int[] { 28 });
		} else if (event.type == 2080) {
			Function.notifyMainCheck(player, new int[] { 10, 1 });
		} else if (event.type == 2079) {
			Mail m = (Mail) event.params[1];
			if (m == null)
				return;
			if (m.getType() == 14)
				Function.notifyMainNum(player, 10, 1);
			else
				Function.notifyMainNum(player, 1, 1);
		} else if (event.type == 2081) {
			Function.notifyMainCheck(player, new int[] { 30 });
		} else if (event.type == 2082) {
			Function.notifyMainNum(player, 25, 0);
		} else if (event.type == 2084) {
			Function.notifyMainCheck(player, new int[] { 32 });
		} else if (event.type == 2087) {
			Function.notifyMainNum(player, 33, 1);
		} else if (event.type == 2088) {
			Function.notifyMainCheck(player, new int[] { 33 });
		} else if ((event.type == 2090) || (event.type == 2089)) {
			Function.notifyMainCheck(player, new int[] { 34 });
		} else if ((event.type == 2091) || (event.type == 2092)) {
			Function.notifyMainCheck(player, new int[] { 35 });
		} else if (event.type == 2093) {
			Function.notifyMainNum(player, 36, 1);
		} else if (event.type == 2094) {
			Function.notifyMainCheck(player, new int[] { 36 });
		} else if (event.type == 2095) {
			Function.notifyMainCheck(player, new int[] { 38 });
		} else if (event.type == 2096) {
			Function.notifyMainCheck(player, new int[] { 37 });
		} else if (event.type == 2098) {
			Function.notifyMainCheck(player, new int[] { 39 });
		} else if ((event.type == 2099) || (event.type == 2100)) {
			Function.notifyMainCheck(player, new int[] { 40 });
		} else if (event.type == 2101) {
			Function.notifyMainCheck(player, new int[] { 41 });
		} else if (event.type == 2102) {
			Function.notifyMainCheck(player, new int[] { 42 });
		} else if (event.type == 2103) {
			Function.notifyMainCheck(player, new int[] { 44 });
		} else if (event.type == 2108) {
			Function.notifyMainCheck(player, new int[] { 46 });
		} else if (event.type == 2109) {
			Function.notifyMainNum(player, 46, 1);
		} else if (event.type == 2104) {
			Function.notifyMainCheck(player, new int[] { 43 });
		}
	}
}
