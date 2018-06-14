package org.darcy.sanguo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.awardcenter.Awards;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.hero.Warriors;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.loottreasure.ShieldInfo;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.relation.Relations;
import org.darcy.sanguo.robot.RobotData;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.worldcompetition.WorldCompetition;
import org.darcy.sanguo.worldcompetition.WorldCompetitionService;

public class RobotService implements Service {
	public static Map<Integer, RobotData> robots = new HashMap<Integer, RobotData>();

	public static int robotNum = 0;

	public void startup() throws Exception {
	}

	private void loadRobotData() {
		int pos = 0;
		List<Row> list = ExcelUtils.getRowList("robot.xls", 2);
		for (Row row : list) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int type = (int) row.getCell(pos++).getNumericCellValue();
			int inDb = (int) row.getCell(pos++).getNumericCellValue();
			int num = (int) row.getCell(pos++).getNumericCellValue();
			String level = row.getCell(pos++).getStringCellValue();
			String accountPrefix = row.getCell(pos++).getStringCellValue();
			double warriorLevelRatio = row.getCell(pos++).getNumericCellValue();
			String mainWarriorIdStr = row.getCell(pos++).getStringCellValue();
			int warriorNum = (int) row.getCell(pos++).getNumericCellValue();
			int purpleNum = (int) row.getCell(pos++).getNumericCellValue();
			int blueNum = (int) row.getCell(pos++).getNumericCellValue();
			String purplePool = row.getCell(pos++).getStringCellValue();
			String bluePool = row.getCell(pos++).getStringCellValue();
			String greenPool = row.getCell(pos++).getStringCellValue();
			String whitePool = row.getCell(pos++).getStringCellValue();

			RobotData data = new RobotData();
			data.id = id;
			data.type = type;
			data.inDb = (inDb == 1);
			data.num = num;
			int[] levels = Calc.split(level, ",");
			data.minLevel = levels[0];
			data.maxLevel = levels[1];
			data.accountPrefix = accountPrefix;
			data.warriorLevelRatio = warriorLevelRatio;
			int[] mainWarriorIds = Calc.split(mainWarriorIdStr, ",");
			data.mainWarriorMaleId = mainWarriorIds[0];
			data.mainWarriorFemaleId = mainWarriorIds[1];
			data.warriorNum = warriorNum;
			data.purpleNum = purpleNum;
			data.blueNum = blueNum;
			data.purplePool = Calc.split(purplePool, ",");
			data.bluePool = Calc.split(bluePool, ",");
			data.greenPool = Calc.split(greenPool, ",");
			data.whitePool = Calc.split(whitePool, ",");

			robots.put(Integer.valueOf(id), data);
		}
	}

	private void createRobot() {
		if (Platform.getWorld().isAlreadyCreateRobot()) {
			return;
		}
		Random random = new Random();
		List<String> names = new ArrayList<String>();
		Iterator<Integer> itx = robots.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			RobotData data = (RobotData) robots.get(Integer.valueOf(id));
			if (data.inDb)
				for (int i = 0; i < data.num; ++i) {
					String name;
					int level;
					int gender = (i % 2 == 0) ? 1 : 2;
					int selectIndex = data.getMainWarriorId(gender);
					String uuid = data.accountPrefix + i;
					do {
						name = "机关人" + ((AccountService) Platform.getServiceManager().get(AccountService.class))
								.genName(gender);
					} while (names.contains(name));
					names.add(name);

					if (data.maxLevel == data.minLevel)
						level = data.maxLevel;
					else {
						level = data.minLevel + random.nextInt(data.maxLevel - data.minLevel + 1);
					}

					Player player = new Player();
					player.setName(name);
					player.setAccountId(uuid);
					player.setChannelType(1);
					player.setGender(gender);
					player.setRegisterTime(System.currentTimeMillis());

					ItemTemplate template = ItemService.getItemTemplate(selectIndex);
					MainWarrior warrior = new MainWarrior(template, player.getBags().getNewItemId());
					player.getBags().addItem(warrior, 1, "regist");
					Warriors w = new Warriors(warrior);
					player.setWarriors(w);
					player.setHeroIds(w.generateHeroIds());

					((DbService) Platform.getServiceManager().get(DbService.class)).add(player);
					if (player.getId() <= 0) {
						--i;
						Platform.getLog().logWarn("机器人创建失败!" + player.getName());
					} else {
						if (FunctionService.isOpenFunction(level, 21)) {
							WorldCompetition competition = WorldCompetition.newWorldCompetition(player.getId());
							((WorldCompetitionService) Platform.getServiceManager().get(WorldCompetitionService.class))
									.addCompetition(competition);
							((DbService) Platform.getServiceManager().get(DbService.class)).add(competition);
							player.setWorldCompetition(competition);
							robotNum += 1;
						}

						if (FunctionService.isOpenFunction(level, 12)) {
							Arena arena = new Arena(Platform.getWorld().getNewArenaCount());
							arena.setPlayerId(player.getId());
							((DbService) Platform.getServiceManager().get(DbService.class)).add(arena);
							player.setArena(arena);
						}

						ShieldInfo shieldInfo = new ShieldInfo(player.getId());
						Platform.getEntityManager().putInEhCache(ShieldInfo.class.getName(),
								Integer.valueOf(player.getId()), shieldInfo);

						Awards awards = new Awards();
						awards.setPlayerId(player.getId());
						player.setAwards(awards);
						((DbService) Platform.getServiceManager().get(DbService.class)).add(awards);

						player.init();

						Relations r = new Relations();
						r.setId(player.getId());
						player.setRelations(r);
						r.init();
						((DbService) Platform.getServiceManager().get(DbService.class)).add(r);

						player.setLevel(level);
						w.getMainWarrior().updateLevel(player.getLevel(), false);

						for (int j = 0; j < data.warriorNum; ++j) {
							int[] warriorPool = null;
							if (j < data.purpleNum) {
								warriorPool = data.purplePool;
							} else if (j < data.purpleNum + data.blueNum) {
								warriorPool = data.bluePool;
							} else {
								int rnd = random.nextInt(2);
								if (rnd == 0)
									warriorPool = data.greenPool;
								else {
									warriorPool = data.whitePool;
								}
							}
							int templateId = warriorPool[random.nextInt(warriorPool.length)];
							Warrior tmp = (Warrior) ItemService.generateItem(templateId, player);
							player.getBags().addItem(tmp, 1, "regist");

							w.addWarrior(tmp, j + 2);
							tmp.setLevel((int) (level / data.warriorLevelRatio));
							tmp.refreshTalents();
						}
						w.refresh(false);
						player.refreshBtlCapa();
						player.save();
						Platform.getLog().logWorld("机器人创建成功：" + player.getAccountId() + "," + player.getName());
					}
				}
		}
		Platform.getWorld().setCreateRobot(1);
	}

	public static List<RobotData> getRobotsByType(int type) {
		List<RobotData> list = new ArrayList<RobotData>();
		for (RobotData robotData : robots.values()) {
			if (robotData.type == type) {
				list.add(robotData);
			}
		}
		return list;
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	public static void main(String[] args) {
		String[] last = { "杨", "黄", "邢", "曹", "蔡", "李", "曲", "郭", "王", "吴", "张", "周", "晁", "齐" };
		String[] name = { "光", "卫", "东", "侠", "晓", "慧", "维", "翾", "耀", "霆", "树", "楠", "含", "韵", "浩", "逸", "波", "驰", "小",
				"军", "宇", "新", "林", "正", "昊" };
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; ++i) {
			sb.setLength(0);
			sb.append(last[random.nextInt(last.length)]);
			if (random.nextDouble() > 0.5D) {
				sb.append(name[random.nextInt(name.length)]);
			}
			sb.append(name[random.nextInt(name.length)]);
			System.out.println(sb.toString());
		}
	}
}
