package org.darcy.sanguo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.item.MapDropAI;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.item.DebrisDropPath;
import org.darcy.sanguo.map.ClearMap;
import org.darcy.sanguo.map.ClearStage;
import org.darcy.sanguo.map.MapActivityStage;
import org.darcy.sanguo.map.MapProStage;
import org.darcy.sanguo.map.MapRecord;
import org.darcy.sanguo.map.MapStage;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.MoneyTrialStage;
import org.darcy.sanguo.map.NPCHelp;
import org.darcy.sanguo.map.SectionTemplate;
import org.darcy.sanguo.map.StageChannel;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.top.Top;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.ChallengeAddPrice;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbRecord;
import sango.packet.PbStageMap;
import sango.packet.PbStageMap.StageInfo;
import sango.packet.PbUp;

public class MapService implements Service, PacketHandler {
	public static HashMap<Integer, SectionTemplate> sectionTemplates = new HashMap<Integer, SectionTemplate>();
	public static HashMap<Integer, StageTemplate> stageTemplates = new HashMap<Integer, StageTemplate>();
	public static HashMap<Integer, MapTemplate> mapTemplates = new HashMap<Integer, MapTemplate>();
	public static HashMap<Integer, NPCHelp> helps = new HashMap<Integer, NPCHelp>();
	public static int[] moneyTrialRewardDamage;
	public static String[] moneyTrialRewardReward;

	private static void loadHelps() {
		List<Row> list = ExcelUtils.getRowList("npchelp.xls", 2);
		for (Row r : list) {
			int pos = 0;
			if (r.getCell(pos) == null)
				return;
			NPCHelp help = new NPCHelp();
			help.stageId = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String buffs = r.getCell(pos++).getStringCellValue();
			if (!(buffs.equals("-1"))) {
				help.buffs = Calc.split(buffs, ",");
			}
			help.mainWarriorIndex = (int) r.getCell(pos++).getNumericCellValue();
			for (int i = 0; i < 6; ++i) {
				help.npcs[i] = (int) r.getCell(pos++).getNumericCellValue();
			}
			r.getCell(pos).setCellType(1);
			String sections = r.getCell(pos++).getStringCellValue();
			help.sectionIds = Calc.split(sections, ",");
			helps.put(Integer.valueOf(help.stageId), help);
		}
	}

	private static void loadLevel() {
		List<Row> list = ExcelUtils.getRowList("section.xls", 2);
		int rowindex = 0;
		for (Row r : list) {
			if (rowindex == 0)
				continue;
			rowindex++;

			int pos = 0;
			if (r.getCell(pos) == null)
				return;

			int id = (int) r.getCell(pos++).getNumericCellValue();
			int auto = (int) r.getCell(pos++).getNumericCellValue();
			int[] mid = new int[6];
			for (int i = 0; i < 6; ++i) {
				mid[i] = (int) r.getCell(pos++).getNumericCellValue();
			}

			SectionTemplate st = new SectionTemplate();
			st.id = id;

			st.bossIndex = auto;
			st.monsterids = mid;
			sectionTemplates.put(Integer.valueOf(id), st);
		}

	}

	private static void loadStage() {
		List<Row> list = ExcelUtils.getRowList("map.xls", 2, 1);
		int rowindex = 0;
		for (Row r : list) {
			if (rowindex == 0)
				continue;
			rowindex++;

			int pos = 0;
			if (r == null) {
				return;
			}
			if (r.getCell(pos) == null) {
				return;
			}

			int id = (int) r.getCell(pos++).getNumericCellValue();
			int preId = (int) r.getCell(pos++).getNumericCellValue();
			int nextId = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			int senceId = (int) r.getCell(pos++).getNumericCellValue();
			int iconId = (int) r.getCell(pos++).getNumericCellValue();
			int rentou = (int) r.getCell(pos++).getNumericCellValue();
			int maxChanllengeTimes = (int) r.getCell(pos++).getNumericCellValue();
			int vitalityCost = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String previewRewards = r.getCell(pos++).getStringCellValue();

			StageTemplate st = new StageTemplate();
			st.id = id;
			st.preId = preId;
			st.nextId = nextId;
			st.name = name;
			st.type = type;
			st.secenId = senceId;
			st.icon = iconId;
			st.maxChanllengeTimes = maxChanllengeTimes;
			st.vitalityCost = vitalityCost;
			st.rentou = rentou;
			st.setPreViewRewards(previewRewards);

			for (int i = 0; i < 3; ++i) {
				if (r.getCell(pos) == null) {
					pos += 6;
				} else {
					r.getCell(pos).setCellType(1);
					String sectionList = r.getCell(pos++).getStringCellValue();
					r.getCell(pos).setCellType(1);
					String positon = r.getCell(pos++).getStringCellValue();
					int money = (int) r.getCell(pos++).getNumericCellValue();
					int warriorSpirit = (int) r.getCell(pos++).getNumericCellValue();
					int dropId = (int) r.getCell(pos++).getNumericCellValue();
					if (sectionList.equals("-1")) {
						st.channels[i] = null;
					} else {
						StageChannel c = new StageChannel();
						try {
							c.setSectionList(Calc.split(sectionList, ","));
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("sectionList:" + sectionList + ",ri:i:" + i);
						}
						c.setPositionInfo(positon);
						c.setMoney(money);
						c.setWarriorSpirit(warriorSpirit);
						c.setDropId(dropId);
						st.channels[i] = c;
					}
				}
			}
			stageTemplates.put(Integer.valueOf(id), st);
		}
	}

	private static void loadMap() {
		List<Row> list = ExcelUtils.getRowList("map.xls", 2, 1);
		int rowindex = 0;
		for (Row r : list) {
			if (rowindex == 0)
				continue;
			rowindex++;
			StageTemplate st;

			int pos = 0;
			if (r.getCell(pos) == null)
				return;

			int id = (int) r.getCell(pos++).getNumericCellValue();
			int preId = (int) r.getCell(pos++).getNumericCellValue();
			int nextId = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			int iconId = (int) r.getCell(pos++).getNumericCellValue();
			int mapImage = (int) r.getCell(pos++).getNumericCellValue();
			int resourceId = (int) r.getCell(pos++).getNumericCellValue();
			int startLevel = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String stageList = r.getCell(pos++).getStringCellValue();

			MapTemplate mt = new MapTemplate();
			mt.id = id;
			mt.preId = preId;
			mt.nextId = nextId;
			mt.name = name;
			mt.iconId = iconId;
			mt.type = type;
			mt.mapImage = mapImage;
			mt.resourceId = resourceId;
			mt.openLevel = startLevel;
			int[] stageIds = Calc.split(stageList, ",");
			mt.stageTemplates = new StageTemplate[stageIds.length];
			for (int i = 0; i < stageIds.length; ++i) {
				st = (StageTemplate) stageTemplates.get(Integer.valueOf(stageIds[i]));
				if (st != null) {
					mt.stageTemplates[i] = st;
				}

			}

			if (mt.type == 0) {
				StageTemplate[] arrayOfStageTemplate;
				for (int i = 0; i < mt.stageTemplates.length; ++i) {
					st = mt.stageTemplates[i];
					if ((st.preViewRewards != null) && (st.preViewRewards.size() > 0)) {
						for (Reward reward : st.preViewRewards) {
							if ((reward.type == 0) && (reward.template.type == 3)) {
								List list1 = (List) ItemService.debrisPaths.get(Integer.valueOf(reward.template.id));
								if (list1 == null) {
									list1 = new ArrayList();
									ItemService.debrisPaths.put(Integer.valueOf(reward.template.id), list1);
								}
								DebrisDropPath ddp = new DebrisDropPath(mt.id, st.id);
								list1.add(ddp);
							}
						}
					}
				}
			}

			mapTemplates.put(Integer.valueOf(id), mt);
			if (r.getCell(pos) == null) {
				continue;
			}

			r.getCell(pos).setCellType(1);
			String starNeeds = r.getCell(pos++).getStringCellValue();
			mt.startNeeds = Calc.split(starNeeds, "\\|");

			for (int i = 0; i < 3; ++i) {
				if (i < mt.startNeeds.length) {
					r.getCell(pos).setCellType(1);
					String preViewReward = r.getCell(pos++).getStringCellValue();
					mt.setStarRewards(i, preViewReward);
				} else {
					++pos;
				}
			}

			for (int i = 0; i < 3; ++i)
				if (i < mt.startNeeds.length) {
					int dropId = (int) r.getCell(pos++).getNumericCellValue();
					mt.dropIds[i] = dropId;
				} else {
					++pos;
				}
		}
	}

	private void loadMoneyTrialReward() {
		List<Row> list = ExcelUtils.getRowList("tree.xls");
		int i = 0;
		for (Row row : list) {
			moneyTrialRewardDamage = new int[list.size()];
			moneyTrialRewardReward = new String[list.size()];
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int damage = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String reward = row.getCell(pos++).getStringCellValue();

			moneyTrialRewardDamage[i] = damage;
			moneyTrialRewardReward[i] = reward;
			i++;
		}
	}

	public void startup() throws Exception {
		loadLevel();
		loadStage();
		loadMap();
		loadHelps();
		loadMoneyTrialReward();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		sectionTemplates.clear();
		stageTemplates.clear();
		mapTemplates.clear();
		helps.clear();
		loadLevel();
		loadStage();
		loadMap();
		loadHelps();
	}

	public int[] getCodes() {
		return new int[] { 2001, 2007, 2005, 2003, 2009, 2013, 2015, 2043, 2045, 2039, 2041, 1113, 1115, 1117, 2101,
				1119, 1143, 2177 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		int mapId;
		int stageId;
		int channel;
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 2001:
			mapInfos(player);
			break;
		case 2007:
			PbUp.MapRewardBox rb = PbUp.MapRewardBox.parseFrom(packet.getData());
			mapId = rb.getMapId();
			int index = rb.getIndex();
			rewardBox(player, mapId, index);
			break;
		case 2005:
			PbUp.StageDetail detail = PbUp.StageDetail.parseFrom(packet.getData());
			mapId = detail.getMapId();
			stageId = detail.getStageId();
			stageDetail(player, mapId, stageId);
			break;
		case 2003:
			PbUp.StageInfos infos = PbUp.StageInfos.parseFrom(packet.getData());
			mapId = infos.getMapId();
			stageInfos(player, mapId);
			break;
		case 2009:
			PbUp.MapChanllenge chanllenge = PbUp.MapChanllenge.parseFrom(packet.getData());
			stageId = chanllenge.getStageId();
			mapId = chanllenge.getMapId();
			channel = chanllenge.getChannel();
			if (stageId < 0) {
				guidInfo(player, stageId);
				return;
			}
			chanllengeInfo(player, mapId, stageId, channel);

			break;
		case 2013:
			PbUp.MapCheck check = PbUp.MapCheck.parseFrom(packet.getData());
			mapId = check.getMapId();
			stageId = check.getStageId();
			channel = check.getChannel();
			check(player, mapId, stageId, channel, check.getStageRecord());
			break;
		case 2015:
			PbUp.MapGetRewardBox box = PbUp.MapGetRewardBox.parseFrom(packet.getData());
			getMapReward(player, box.getMapId(), box.getIndex());
			break;
		case 2043:
			if (!(FunctionService.isOpenFunction(player.getLevel(), 13))) {
				return;
			}
			PbUp.MapProChallengeInfo c = PbUp.MapProChallengeInfo.parseFrom(packet.getData());
			mapId = c.getMapId();
			proMapChallenge(player, mapId);
			break;
		case 2045:
			if (!(FunctionService.isOpenFunction(player.getLevel(), 13))) {
				return;
			}
			PbUp.MapProCheck chk = PbUp.MapProCheck.parseFrom(packet.getData());
			proMapCheck(player, chk.getMapId(), chk.getStageRecord());
			break;
		case 2039:
			if (!(FunctionService.isOpenFunction(player.getLevel(), 13))) {
				return;
			}
			proMapInfos(player);
			break;
		case 2041:
			if (!(FunctionService.isOpenFunction(player.getLevel(), 13))) {
				return;
			}
			PbUp.MapProStageInfo info = PbUp.MapProStageInfo.parseFrom(packet.getData());
			mapId = info.getMapId();
			proMapStageInfo(player, mapId);
			break;
		case 1113:
			activityMapInfo(player);
			break;
		case 1115:
			activityMapChallenge(player, packet);
			break;
		case 1117:
			activityMapCheck(player, packet);
			break;
		case 1119:
			moneyTrial(player);
			break;
		case 2101:
			if (!(FunctionService.isOpenFunction(player.getLevel(), 28))) {
				return;
			}
			PbUp.MapMulitChanllenge m = PbUp.MapMulitChanllenge.parseFrom(packet.getData());
			mapMultiChallenge(player, m.getMapId(), m.getStageId(), m.getChannel(), m.getTimes());
			break;
		case 1143:
			mapRank(player);
			break;
		case 2177:
			clearCD(player);
		}
	}

	private void mapRank(Player player) {
		PbDown.MapRank.Builder builder = PbDown.MapRank.newBuilder();
		builder.setResult(true).setRank(Platform.getTopManager().getRank(1, player.getId()))
				.setStar(player.getMapRecord().getTotalEarnedStars());
		List<Top> list = Platform.getTopManager().getRanks(1);
		for (Top top : list) {
			PbDown.MapRank.MapRankUnit.Builder unitBuilder = PbDown.MapRank.MapRankUnit.newBuilder();
			unitBuilder.setRank(Platform.getTopManager().getRank(1, top.getPid()));
			unitBuilder.setStar(top.getValue());
			unitBuilder.setMini(Platform.getPlayerManager().getMiniPlayer(top.getPid()).genMiniUser());
			int[] mapIds = Calc.split(top.getNote(), ",");
			unitBuilder.setMapName(((MapTemplate) mapTemplates.get(Integer.valueOf(mapIds[0]))).name);
			unitBuilder.setStageName(((StageTemplate) stageTemplates.get(Integer.valueOf(mapIds[1]))).name);
			builder.addRankUnits(unitBuilder.build());
		}
		player.send(1144, builder.build());
	}

	private void mapMultiChallenge(Player player, int mapId, int stageId, int chanel, int times) {
		if (times < 0)
			return;
		boolean globalDrop = false;
		MapRecord mr = player.getMapRecord();
		ClearMap cm = mr.getClearMap(mapId);
		PbDown.MapMulitChanllengeRst.Builder rst = PbDown.MapMulitChanllengeRst.newBuilder();
		rst.setChannel(chanel).setStageId(stageId).setMapId(mapId).setResult(false);
		if (mr.getLeftMultiChalengeMiliSeconds() <= 0)
			if (cm != null) {
				ClearStage cs = cm.getClearStage(stageId);
				if (cs != null) {
					if (cs.isFinished(chanel)) {
						if (cs.getLeftChanllengeTimes() >= times) {
							if (player.getVitality() >= cs.getTemplate().vitalityCost * times) {
								if (player.getBags().getFullBag() == -1) {
									int dropId = cs.getTemplate().channels[chanel].getDropId();
									DropGroup dg = ((DropService) Platform.getServiceManager().get(DropService.class))
											.getDropGroup(dropId);
									player.decVitality(times * cs.getTemplate().vitalityCost, "pve");
									int exp = player.getLevel() * 10;
									int money = cs.getTemplate().channels[chanel].getMoney();
									int warrior = cs.getTemplate().channels[chanel].getWarriorSpirit();
									for (int i = 0; i < times; ++i) {
										DropGroup drop;
										PbCommons.MultiChallengeReward.Builder mb = PbCommons.MultiChallengeReward
												.newBuilder();
										mb.setExp(exp).setMoney(money).setWarriorSpirit(warrior);
										List<Gain> gains = dg.genGains(player);

										if (ActivityInfo.isOpenActivity(8, player)) {
											MapDropAI ai = (MapDropAI) ActivityInfo.getItem(player, 8);
											if (ai != null) {
												drop = (DropGroup) DropService.dropGroups
														.get(Integer.valueOf(ai.dropId));
												if (drop != null) {
													gains.addAll(drop.genGains(player));
												}
											}
										}
										for (Gain g : gains) {
											g.gain(player, "pve");
											mb.addRewards(g.genPbReward());
										}
										player.addExp(exp, "pve");
										player.addWarriorSpirit(warrior, "pve");
										player.addMoney(money, "pve");
										rst.addRewards(mb);
									}
									rst.setResult(true);
									globalDrop = true;
									cs.chanllengeTimes += times;
									mr.setMultiChallengeTime(System.currentTimeMillis());
									rst.setMultiChallengeCD(mr.getLeftMultiChalengeMiliSeconds());

									Platform.getLog().logPveFight(player,
											(MapTemplate) mapTemplates.get(Integer.valueOf(mapId)),
											(StageTemplate) stageTemplates.get(Integer.valueOf(stageId)), true, times,
											cs.getLeftChanllengeTimes());
									Platform.getEventManager()
											.addEvent(new Event(2007, new Object[] { player, Integer.valueOf(times) }));
								}
								rst.setErrInfo("背包已满，请先清理背包");
							}
							rst.setErrInfo("体力不足");

						}
						VipService.notifyChallengeTimeBuyCost(player, PbCommons.VipBuyTimeType.MAP, mapId, stageId);
						return;
					}

					rst.setErrInfo("还未通关");
				} else {
					rst.setErrInfo("还未通关");
				}
			} else {
				rst.setErrInfo("地图还未开通");
			}
		else {
			rst.setErrInfo("扫荡时间未到");
		}
		label614: player.send(2102, rst.build());
		if (globalDrop)
			player.getGlobalDrop().mapDrop(player, times);
	}

	private void proMapCheck(Player player, int mapId, PbRecord.StageRecord inRecord) {
		MapRecord mr = player.getMapRecord();
		MapProStage stage = (MapProStage) mr.getTmpStage();
		PbDown.MapProCheckRst.Builder rst = PbDown.MapProCheckRst.newBuilder();
		if (stage != null) {
			rst.setMapId(mapId).setStageId(stage.getStageTemplate().id);
			if (mapId == stage.getMapTemplate().id) {
				stage.getRecordUtil().setInStageRecord(inRecord);
				stage.combat(player);
				stage.proccessReward(player);
				rst.setResult(true).setStageName(stage.getStageTemplate().name);
				if (stage.isWin()) {
					rst.setWin(true).setMoney(stage.getMoney()).setExp(stage.getExp())
							.setWarriorSoul(stage.getWariorSpirit());
					List<Gain> gains = stage.getGains();
					if (gains != null)
						for (Gain gain : gains)
							rst.addRewards(gain.genPbReward());
				} else {
					rst.setWin(false);
				}
			} else {
				rst.setResult(false);
				rst.setWin(false);
				rst.setErrInfo("战斗关卡不匹配~");
			}
		} else {
			rst.setResult(false);
			rst.setWin(false);
			rst.setErrInfo("没有战斗记录~");
		}

		player.send(2046, rst.build());
		mr.setTmpStage(null);
	}

	private void proMapChallenge(Player player, int mapId) {
		MapRecord r = player.getMapRecord();
		PbDown.MapProChanllengeRst.Builder chanllenge = PbDown.MapProChanllengeRst.newBuilder().setMapId(mapId);
		if (r.isOpenedProMap(mapId)) {
			MapTemplate mt = (MapTemplate) mapTemplates.get(Integer.valueOf(mapId));
			StageTemplate st = mt.stageTemplates[0];
			if (mt != null) {
				if (r.getLeftProMapChallengeTimes() < 1) {
					VipService.notifyChallengeTimeBuyCost(player, PbCommons.VipBuyTimeType.PROMAP);
					return;
				}
				Team offen = new Team();
				offen.setUnits(player.getWarriors().getStands());
				Stage stage = new MapProStage(offen, mt, st, player);
				stage.init();
				r.setTmpStage(stage);
				chanllenge.setResult(true).setStageId(st.id).setMapId(mapId).setStageInfo(stage.getInfoBuilder());
				player.send(2044, chanllenge.build());
				return;
			}
		}

		chanllenge.setResult(false).setStageId(0).setMapId(mapId).setErrInfo("服务器繁忙，请稍后再试");
		player.send(2010, chanllenge.build());
	}

	private void proMapStageInfo(Player player, int mapId) {
		MapRecord r = player.getMapRecord();
		PbDown.MapProStageInfoRst.Builder rst = PbDown.MapProStageInfoRst.newBuilder();
		if (r.isOpenedProMap(mapId)) {
			MapTemplate mt = (MapTemplate) mapTemplates.get(Integer.valueOf(mapId));
			StageTemplate st = mt.stageTemplates[0];
			rst.setResult(true).setIconId(st.rentou).setId(mapId).setMoney(st.channels[0].getMoney()).setName(st.name)
					.setType(st.type).setIsClear(r.hasClearProMap(mapId))
					.setWarriorSoul(st.channels[0].getWarriorSpirit());
			for (Reward reward : st.preViewRewards)
				rst.addRewards(reward.genPbReward());
		} else {
			rst.setResult(false);
			rst.setErrInfo("关卡尚未开放");
		}

		player.send(2042, rst.build());
	}

	private void proMapInfos(Player player) {
		MapRecord r = player.getMapRecord();
		PbDown.MapProInfosRst.Builder rst = PbDown.MapProInfosRst.newBuilder();
		rst.setResult(true).setLeftTimes(r.getLeftProMapChallengeTimes());
		for (MapTemplate mt : r.getOpenedProMaps()) {
			PbStageMap.MapInfo.Builder mapInfo = PbStageMap.MapInfo.newBuilder().setIconId(mt.iconId).setId(mt.id)
					.setIsOpen(true).setOpenLevel(mt.openLevel).setName(mt.name);
			rst.addMapInfos(mapInfo);
		}

		int nextId = 1001;
		if (r.getOpenedProMaps().size() > 0) {
			nextId = ((MapTemplate) r.getOpenedProMaps().get(r.getOpenedProMaps().size() - 1)).nextId;
		}
		if (nextId != -1) {
			MapTemplate mt = (MapTemplate) mapTemplates.get(Integer.valueOf(nextId));
			if (mt != null) {
				int preId = mt.preId;
				ClearMap cm = r.getClearMap(preId);
				if ((cm != null) && (cm.isFinished())) {
					preId = mt.id - 1;
				}
				String preMap = ((MapTemplate) mapTemplates.get(Integer.valueOf(preId))).name;
				PbStageMap.MapInfo.Builder mapInfo = PbStageMap.MapInfo.newBuilder().setIconId(mt.iconId).setId(mt.id)
						.setIsOpen(false).setOpenPreMap(preMap).setOpenLevel(mt.openLevel).setName(mt.name);

				rst.addMapInfos(mapInfo);
			}
		}

		player.send(2040, rst.build());
	}

	private void getMapReward(Player player, int mapId, int index) {
		MapRecord mr = player.getMapRecord();
		ClearMap cm = mr.getClearMap(mapId);
		PbDown.MapGetRewardBoxRst.Builder rst = PbDown.MapGetRewardBoxRst.newBuilder();
		if (cm != null) {
			MapTemplate mt = cm.getTemplate();
			if (cm.isFetchedRewardBox(index)) {
				rst.setResult(false);
				rst.setErrInfo("已经领取过该奖励");
			} else if (cm.getEarnedStars() < mt.startNeeds[index]) {
				rst.setResult(false);
				rst.setErrInfo("积累星数不足，请通过更多普通关卡来获得星数");
			} else if (player.getBags().getFullBag() != -1) {
				rst.setResult(false);
				rst.setErrInfo("背包已满，请先清理背包");
			} else {
				rst.setResult(true);

				int dorpId = mt.dropIds[index];
				DropGroup dg = (DropGroup) DropService.dropGroups.get(Integer.valueOf(dorpId));
				if (dg != null) {
					List<Gain> gains = dg.genGains(player);
					for (Gain g : gains) {
						g.gain(player, "mapreward");
					}
				}

				cm.fetchRewardBox(index);
				Platform.getEventManager().addEvent(new Event(2075, new Object[] { player }));
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("你都没打过这个副本");
		}
		player.send(2016, rst.build());
	}

	private void check(Player player, int mapId, int stageId, int channel, PbRecord.StageRecord stageRecord) {
		MapRecord mr = player.getMapRecord();
		MapStage stage = (MapStage) mr.getTmpStage();
		PbDown.MapCheckRst.Builder rst = PbDown.MapCheckRst.newBuilder();
		rst.setMapId(mapId).setStageId(stageId).setChannel(channel);
		if (stage != null) {
			if ((mapId == stage.getMapTemplate().id) && (stageId == stage.getStageTemplate().id)
					&& (channel == stage.getChannel())) {
				stage.getRecordUtil().setInStageRecord(stageRecord);
				stage.combat(player);
				stage.proccessReward(player);
				rst.setResult(true).setStageName(stage.getStageTemplate().name);
				if (stage.isWin()) {
					rst.setWin(true).setMoney(stage.getMoney()).setExp(stage.getExp())
							.setWarriorSoul(stage.getWariorSpirit());
					List<Gain> gains = stage.getGains();
					if (gains != null)
						for (Gain gain : gains)
							rst.addRewards(gain.genPbReward());
				} else {
					rst.setWin(false);
				}
			} else {
				rst.setResult(false);
				rst.setWin(false);
				rst.setErrInfo("战斗关卡不匹配~");
			}
		} else {
			rst.setResult(false);
			rst.setWin(false);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2014, rst.build());

		if (rst.getWin()) {
			player.getGlobalDrop().mapDrop(player, 1);
		}

		mr.setTmpStage(null);
	}

	private void guidInfo(Player player, int stageId) {
		PbDown.MapChanllengeRst.Builder chanllenge = PbDown.MapChanllengeRst.newBuilder();
		chanllenge.setChannel(0).setMapId(stageId).setStageId(stageId);

		Team offen = new Team();
		offen.setUnits(player.getWarriors().getStands());
		MapStage stage = new MapStage(offen, (MapTemplate) mapTemplates.get(Integer.valueOf(stageId)),
				(StageTemplate) stageTemplates.get(Integer.valueOf(stageId)), 0, player);
		stage.init(player);
		chanllenge.setResult(true).setStageInfo(stage.getInfoBuilder());
		stage.clearNpcBuff(player);
		player.send(2010, chanllenge.build());
	}

	private void chanllengeInfo(Player player, int mapId, int stageId, int channel) {
		MapRecord mr = player.getMapRecord();
		ClearMap cm = mr.getClearMap(mapId);
		PbDown.MapChanllengeRst.Builder chanllenge = PbDown.MapChanllengeRst.newBuilder();
		chanllenge.setChannel(channel).setMapId(mapId).setStageId(stageId);

		if (cm != null) {
			ClearStage cs = cm.getClearStage(stageId);
			if (cs != null) {
				if (player.getVitality() < cs.getTemplate().vitalityCost) {
					chanllenge.setResult(false).setErrInfo("体力不足");
					player.send(2010, chanllenge.build());
					return;
				}
				if (cs.getLeftChanllengeTimes() < 1) {
					VipService.notifyChallengeTimeBuyCost(player, PbCommons.VipBuyTimeType.MAP, mapId, stageId);
					return;
				}
				Team offen = new Team();
				offen.setUnits(player.getWarriors().getStands());
				MapStage stage = new MapStage(offen, cm.getTemplate(), cs.getTemplate(), channel, player);
				stage.init(player);
				mr.setTmpStage(stage);
				chanllenge.setResult(true).setStageInfo(stage.getInfoBuilder());
				stage.clearNpcBuff(player);
				player.send(2010, chanllenge.build());
			}
		}
	}

	private void mapInfos(Player player) {
		PbStageMap.MapInfo.Builder mapInfo;
		MapRecord r = player.getMapRecord();
		List clearMaps = r.getClearMaps();
		PbDown.MapInfosRst.Builder mapInfos = PbDown.MapInfosRst.newBuilder();
		mapInfos.setResult(true);

		boolean open = false;
		for (int i = 0; i < clearMaps.size(); ++i) {
			ClearMap cm = (ClearMap) clearMaps.get(i);
			open = cm.getTemplate().openLevel <= player.getLevel();
			mapInfo = PbStageMap.MapInfo.newBuilder().setEarnedStars(cm.getEarnedStars()).setFinished(cm.isFinished())
					.setIconId(cm.getTemplate().iconId).setId(cm.id).setMaxStars(cm.getTemplate().getMaxStars())
					.setOpenLevel(cm.getTemplate().openLevel).setIsOpen(open).setName(cm.getTemplate().name)
					.setHasGotStarReward(cm.hasGotStarReward());
			mapInfos.addMapInfos(mapInfo);
			if (!(open)) {
				break;
			}
		}

		if ((((ClearMap) clearMaps.get(clearMaps.size() - 1)).getTemplate().openLevel != -1) && (open)) {
			int preViewId = ((ClearMap) clearMaps.get(clearMaps.size() - 1)).getTemplate().nextId;
			MapTemplate mt = (MapTemplate) mapTemplates.get(Integer.valueOf(preViewId));
			if (mt != null) {
				mapInfo = PbStageMap.MapInfo.newBuilder().setEarnedStars(0).setFinished(false).setIconId(mt.iconId)
						.setId(mt.id).setIsOpen(false).setMaxStars(0).setOpenLevel(mt.openLevel).setName(mt.name);
				mapInfos.addMapInfos(mapInfo);
			}
		}

		player.send(2002, mapInfos.build());
	}

	private void rewardBox(Player player, int mapId, int index) {
		MapTemplate mt = (MapTemplate) mapTemplates.get(Integer.valueOf(mapId));
		ClearMap cm = player.getMapRecord().getClearMap(mapId);
		if ((mt != null) && (index >= 0) && (index < mt.starRewards.length) && (cm != null)) {
			List rewards = mt.starRewards[index];
			if (rewards != null) {
				PbDown.MapRewardBoxRst.Builder box = PbDown.MapRewardBoxRst.newBuilder().setIndex(index)
						.setStars(mt.startNeeds[index]);
				for (int i = 0; i < rewards.size(); ++i) {
					Reward r = (Reward) rewards.get(i);
					if (r != null) {
						PbCommons.PbReward.Builder pbReward = PbCommons.PbReward.newBuilder().setCount(r.count)
								.setType(r.type).setTemplateId((r.template == null) ? -1 : r.template.id);
						box.addRewards(pbReward);
					}
				}

				box.setResult(true);
				box.setFetched(cm.isFetchedRewardBox(index));

				player.send(2008, box.build());
			}
		}
	}

	private void stageInfos(Player player, int mapId) {
		MapRecord r = player.getMapRecord();
		ClearMap cm = r.getClearMap(mapId);
		if (cm != null) {
			int[] arrayOfInt;
			List stages = cm.getStages();
			PbDown.StageInfosRst.Builder stageInfos = PbDown.StageInfosRst.newBuilder();
			stageInfos.setResult(true);
			int stars = 0;
			for (int i = 0; i < stages.size(); ++i) {
				ClearStage cs = (ClearStage) stages.get(i);
				PbStageMap.StageInfo.newBuilder().setEarnedStars(cs.getStars()).setIconId(cs.getTemplate().icon)
						.setId(cs.id).setMaxStars(cs.getTemplate().getMaxStars()).setName(cs.getTemplate().name)
						.setMapImage(cm.getTemplate().mapImage).setType(cs.getTemplate().type);
				StageInfo stageInfo = stageInfos.getStages(i);
				stageInfos.addStages(stageInfo);
				stars += cs.getStars();
			}
			stageInfos.setEarnedStars(stars).setMapId(mapId).setMaxStages(cm.getTemplate().stageTemplates.length)
					.setMaxStars(cm.getTemplate().getMaxStars());
			for (int j = 0; j < cm.getTemplate().startNeeds.length; ++j) {
				int v = cm.getTemplate().startNeeds[j];
				stageInfos.addBoxes(v);
			}
			for (int j = 0; j < cm.getTemplate().startNeeds.length; ++j) {
				stageInfos.addIsGot(cm.isFetchedRewardBox(j));
			}

			if (!(r.getFirstMaps().contains(Integer.valueOf(mapId)))) {
				r.getFirstMaps().add(Integer.valueOf(mapId));
				stageInfos.setFirst(true);
			} else {
				stageInfos.setFirst(false);
			}

			player.send(2004, stageInfos.build());
		}
	}

	private void stageDetail(Player player, int mapId, int stageId) {
		MapRecord r = player.getMapRecord();
		ClearMap cm = r.getClearMap(mapId);
		if (cm != null) {
			ClearStage cs = cm.getClearStage(stageId);
			if (cs != null) {
				StageTemplate st = cs.getTemplate();
				PbDown.StageDetailRst.Builder sd = PbDown.StageDetailRst.newBuilder()
						.setMaxChanllengeTimes(st.maxChanllengeTimes).setIconId(st.rentou).setId(st.id)
						.setMultiCDClearPrice(getClearCDPrice(player))
						.setLeftChanllengeTimes(cs.getLeftChanllengeTimes()).setName(cs.getTemplate().name)
						.setType(cs.getTemplate().type).setMultiChallengeCD(r.getLeftMultiChalengeMiliSeconds())
						.setVitalityCost(cs.getTemplate().vitalityCost);
				for (int i = 0; i < st.channels.length; ++i) {
					StageChannel sc = st.channels[i];
					if (sc != null) {
						PbStageMap.ChannelInfo.Builder ci = PbStageMap.ChannelInfo.newBuilder()
								.setFinish(cs.finishRecords[i]).setMoney(sc.getMoney())
								.setWarriorSoul(sc.getWarriorSpirit()).setIndex(i);
						sd.addChannels(ci);
					}
				}
				for (int i = 0; i < st.preViewRewards.size(); ++i) {
					Reward rd = (Reward) st.preViewRewards.get(i);
					PbCommons.PbReward.Builder reward = PbCommons.PbReward.newBuilder().setType(rd.type)
							.setTemplateId((rd.template == null) ? -1 : rd.template.id).setCount(rd.count);
					sd.addRewards(reward);
				}
				sd.setResult(true);

				if (!(r.getFirstStages().contains(Integer.valueOf(stageId)))) {
					r.getFirstStages().add(Integer.valueOf(stageId));
					sd.setFirst(true);
				} else {
					sd.setFirst(false);
				}
				int resetCount = cs.getResetTimes();
				if (resetCount >= player.getVip().mapTimes) {
					sd.setResetPrice(-1);
				} else {
					int price = ChallengeAddPrice.getPrice(3, 1 + resetCount);
					sd.setResetPrice(price);
				}
				player.send(2006, sd.build());
			}
		}
	}

	private void activityMapInfo(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 18))) {
			return;
		}
		PbDown.ActivityMapInfoRst rst = PbDown.ActivityMapInfoRst.newBuilder().setResult(true)
				.addMapInfos(player.getMapRecord().genActivityMapData(3))
				.addMapInfos(player.getMapRecord().genActivityMapData(4))
				.addMapInfos(player.getMapRecord().genActivityMapData(5)).build();
		player.send(1114, rst);
	}

	private void activityMapChallenge(Player player, PbPacket.Packet packet) {
		int mapId;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 18))) {
			return;
		}
		PbDown.ActivityMapChallengeRst.Builder builder = PbDown.ActivityMapChallengeRst.newBuilder().setResult(true);
		try {
			mapId = PbUp.ActivityMapChallenge.parseFrom(packet.getData()).getMapId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("挑战活动副本失败");
			player.send(1116, builder.build());
			return;
		}
		MapRecord record = player.getMapRecord();
		MapTemplate mt = (MapTemplate) mapTemplates.get(Integer.valueOf(mapId));
		if (mt == null) {
			builder.setResult(false);
			builder.setErrInfo("活动副本不存在");
		} else {
			if (record.getActivityMapLeftTimes(mt.type) <= 0) {
				PbCommons.VipBuyTimeType type = (mt.type == 3) ? PbCommons.VipBuyTimeType.WARRIORTRIAL
						: PbCommons.VipBuyTimeType.TREASURETRIAL;
				VipService.notifyChallengeTimeBuyCost(player, type);
				return;
			}
			StageTemplate st = mt.stageTemplates[0];

			Stage stage = null;
			if ((mt.type != 3) && (mt.type != 4)) {
				builder.setResult(false);
				builder.setErrInfo("活动副本类型错误");
			} else if (!(MapRecord.isOpenActivity(mt.type))) {
				builder.setResult(false);
				if (mt.type == 3)
					builder.setErrInfo("讨伐黄巾尚未开启");
				else if (mt.type == 4)
					builder.setErrInfo("金书银马尚未开启");
			} else {
				stage = new MapActivityStage(mt, st, player);
				stage.init();
				record.setTmpStage(stage);
				builder.setResult(true);
				builder.setMapId(mapId);
				builder.setStageInfo(stage.getInfoBuilder());
			}
		}
		player.send(1116, builder.build());
	}

	private void activityMapCheck(Player player, PbPacket.Packet packet) {
		int mapId;
		PbRecord.StageRecord stageRecord;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 18))) {
			return;
		}
		PbDown.ActivityMapCheckRst.Builder builder = PbDown.ActivityMapCheckRst.newBuilder().setResult(true);
		try {
			PbUp.ActivityMapCheck check = PbUp.ActivityMapCheck.parseFrom(packet.getData());
			mapId = check.getMapId();
			stageRecord = check.getStageRecord();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("活动副本校验失败");
			player.send(1118, builder.build());
			return;
		}
		MapRecord record = player.getMapRecord();
		MapActivityStage stage = (MapActivityStage) record.getTmpStage();
		if (stage == null) {
			builder.setResult(false);
			builder.setErrInfo("尚未挑战活动副本");
		} else {
			MapTemplate mt = stage.getMapTemplate();
			if (mapId != mt.id) {
				builder.setResult(false);
				builder.setErrInfo("活动副本数据异常");
			} else {
				stage.getRecordUtil().setInStageRecord(stageRecord);
				stage.combat(player);
				stage.proccessReward(player);
				builder.setResult(true);
				if (stage.isWin()) {
					builder.setWin(true);
					List<Gain> gains = stage.getGains();
					if (gains != null) {
						List<Reward> list = new ArrayList();
						for (Gain gain : gains) {
							list.add(gain.newReward());
						}
						list = Reward.mergeReward(list);
						for (Reward r : list)
							builder.addRewards(r.genPbReward());
					}
				} else {
					builder.setWin(false);
				}
			}
		}
		player.send(1118, builder.build());
		record.setTmpStage(null);
	}

	private void moneyTrial(Player player) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 18))) {
			return;
		}
		PbDown.MoneyTrialRst.Builder builder = PbDown.MoneyTrialRst.newBuilder();
		builder.setResult(true);
		MapTemplate mt = getSpecialMapTemplate(5);
		MapRecord record = player.getMapRecord();
		if (record.getActivityMapLeftTimes(5) <= 0) {
			VipService.notifyChallengeTimeBuyCost(player, PbCommons.VipBuyTimeType.MONEYTRIAL);
			return;
		}
		StageTemplate st = mt.stageTemplates[0];
		MoneyTrialStage stage = new MoneyTrialStage(mt, st, player);
		stage.init();
		stage.combat(player);
		stage.proccessReward(player);
		builder.setResult(true);
		builder.setWin(stage.isWin());
		builder.setStageRecord(stage.getRecordUtil().getStageRecord());
		builder.setStageInfo(stage.getInfoBuilder().build());
		builder.setMoney(stage.getMoney());
		builder.setDamage(stage.getDamage());

		player.send(1120, builder.build());
	}

	public static MapTemplate getSpecialMapTemplate(int mapType) {
		for (MapTemplate template : mapTemplates.values()) {
			if (template.type == mapType) {
				return template;
			}
		}
		return null;
	}

	public static Reward getRewardByDamage(int damage) {
		for (int i = 0; i < moneyTrialRewardDamage.length; ++i) {
			if (damage >= moneyTrialRewardDamage[i]) {
				if (i != moneyTrialRewardDamage.length - 1)
					continue;
				return new Reward(moneyTrialRewardReward[i]);
			}

			if (i == 0) {
				return null;
			}
			return new Reward(moneyTrialRewardReward[(i - 1)]);
		}

		return null;
	}

	private void clearCD(Player player) {
		int price = getClearCDPrice(player);
		PbDown.MapCDClearRst.Builder rst = PbDown.MapCDClearRst.newBuilder();
		if (player.getJewels() < price) {
			rst.setResult(false).setErrorInfo("元宝不足");
		} else {
			player.decJewels(price, "mapcdclear");
			player.getMapRecord().setMultiChallengeTime(0L);
			int times = player.getPool().getInt(10, 0);
			player.getPool().set(10, Integer.valueOf(times + 1));
			rst.setResult(true);
		}

		player.send(2178, rst.build());
	}

	public int getClearCDPrice(Player player) {
		Integer[] exps = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExps(5001);
		int times = player.getPool().getInt(10, 0);
		int price = 0;
		if (times >= exps.length)
			price = exps[(exps.length - 1)].intValue();
		else {
			price = exps[times].intValue();
		}
		if (price < 0) {
			throw new IllegalArgumentException("清除连战cd价格配置错误");
		}
		return price;
	}
}
