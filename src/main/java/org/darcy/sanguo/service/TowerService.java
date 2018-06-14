package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.SectionTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.top.Top;
import org.darcy.sanguo.tower.FinishCondition;
import org.darcy.sanguo.tower.TowerRecord;
import org.darcy.sanguo.tower.TowerStage;
import org.darcy.sanguo.tower.TowerStageTemplate;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbCommons;
import sango.packet.PbCommons.TowerReward;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbRecord;
import sango.packet.PbUp;

public class TowerService implements Service, PacketHandler, EventHandler {
	public static HashMap<Integer, TowerStageTemplate> towerTemplates = new HashMap();

	private HashMap<Integer, PbCommons.TowerReward> towerRewards = new HashMap<Integer, TowerReward>();

	private Map<Integer, String> titleNames = new HashMap<Integer, String>();

	public TowerService() {
	}

	private void loadTitleNames() {
		List<Row> list = ExcelUtils.getRowList("playertitle.xls");
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int titleId = (int) row.getCell(pos++).getNumericCellValue();
			row.getCell(pos).setCellType(1);
			String titleName = row.getCell(pos++).getStringCellValue();
			this.titleNames.put(Integer.valueOf(titleId), titleName);
		}
	}

	private void loadTowers() {
		List<Row> list = ExcelUtils.getRowList("tower.xls", 2);
		for (Row r : list) {

			int pos = 0;
			if (r.getCell(pos) == null)
				return;

			int id = (int) r.getCell(pos++).getNumericCellValue();
			String name = r.getCell(pos++).getStringCellValue();
			String layerName = r.getCell(pos++).getStringCellValue();
			int money = (int) r.getCell(pos++).getNumericCellValue();
			int warriorSpirit = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String items = r.getCell(pos++).getStringCellValue();
			int titleId = (int) r.getCell(pos++).getNumericCellValue();
			String atts = r.getCell(pos++).getStringCellValue();
			int next = (int) r.getCell(pos++).getNumericCellValue();
			int level = (int) r.getCell(pos++).getNumericCellValue();
			int sectionId = (int) r.getCell(pos++).getNumericCellValue();
			int type = (int) r.getCell(pos++).getNumericCellValue();
			int model = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String fc = r.getCell(pos++).getStringCellValue();

			TowerStageTemplate t = new TowerStageTemplate();
			t.id = id;
			t.name = name;
			t.layerName = layerName;
			t.money = money;
			t.warriorSpirit = warriorSpirit;
			if (!(items.equals("-1"))) {
				String[] rewards = items.split(",");
				t.rewards = new ArrayList();
				for (String rd : rewards) {
					t.rewards.add(new Reward(rd));
				}
			}
			t.titleId = titleId;
			if (!(atts.equals("-1"))) {
				String[] ls = atts.split(",");
				t.titleAttris = new ArrayList();
				for (String l : ls) {
					Attri a = new Attri(l);
					t.titleAttris.add(a);
				}
			}
			t.nextLayer = next;
			t.openLevel = level;
			t.section = ((SectionTemplate) MapService.sectionTemplates.get(Integer.valueOf(sectionId)));
			t.type = type;
			t.monsterModel = model;
			if (!(fc.equals("-1"))) {
				t.fCondition = new FinishCondition(fc);
			}
			towerTemplates.put(Integer.valueOf(t.id), t);
		}
	}

	private void generateBoxRewards() {
		this.towerRewards.clear();
		for (TowerStageTemplate tt : towerTemplates.values())
			if (tt.rewards != null) {
				PbCommons.TowerReward.Builder b = PbCommons.TowerReward.newBuilder();
				b.setLayer(tt.id);
				for (Reward r : tt.rewards) {
					b.addRewards(r.genPbReward());
				}
				this.towerRewards.put(Integer.valueOf(tt.id), b.build());
			}
	}

	public void startup() throws Exception {
		loadTowers();
		loadTitleNames();
		generateBoxRewards();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		towerTemplates.clear();
		loadTowers();
		generateBoxRewards();
	}

	public int[] getCodes() {
		return new int[] { 2059, 2061, 2047, 2051, 2057, 2065, 2055, 2049, 2063, 2053 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 20))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 2059:
			challenge(player);
			break;
		case 2061:
			PbUp.TowerCheck check = PbUp.TowerCheck.parseFrom(packet.getData());
			check(player, check.getStageId(), check.getStageRecord());
			break;
		case 2047:
			info(player);
			break;
		case 2051:
			mapReward(player);
			break;
		case 2057:
			multiChallenge(player);
			break;
		case 2065:
			quitMultiChallenge(player);
			break;
		case 2055:
			reset(player);
			break;
		case 2049:
			rank(player);
			break;
		case 2063:
			next(player);
			break;
		case 2053:
			PbUp.TowerStageReward s = PbUp.TowerStageReward.parseFrom(packet.getData());
			stageReward(player, s.getStageId());
		case 2048:
		case 2050:
		case 2052:
		case 2054:
		case 2056:
		case 2058:
		case 2060:
		case 2062:
		case 2064:
		}
	}

	public static String geneProgress(int level) {
		if (level > 0) {
			int m = (level - 1) / 5 + 1;
			int s = level % 5;
			if (s == 0) {
				s = 5;
			}
			StringBuffer sb = new StringBuffer();
			sb.append(m).append("-").append(s);
			return sb.toString();
		}
		return "1-0";
	}

	private void rank(Player player) {
		PbDown.TowerRank.Builder rst = PbDown.TowerRank.newBuilder();
		TowerRecord r = player.getTowerRecord();
		List tops = Platform.getTopManager().getRanks(0);
		rst.setResult(true);
		rst.setProgress(String.valueOf(r.getMaxLevel())).setRank(Platform.getTopManager().getRank(0, player.getId()));
		for (int i = 0; i < tops.size(); ++i) {
			Top top = (Top) tops.get(i);
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(top.getPid());
			rst.addRankUnits(PbDown.TowerRank.TowerRankUnit.newBuilder().setMini(mini.genMiniUser())
					.setProgress(String.valueOf(top.getValue())).setTitleID(getTitleID(top.getValue()))
					.setRank(Platform.getTopManager().getRank(0, top.getPid())));
		}
		player.send(2050, rst.build());
	}

	public static int getTitleID(int maxLevel) {
		TowerStageTemplate tt = (TowerStageTemplate) towerTemplates.get(Integer.valueOf(maxLevel));
		if (tt != null) {
			return tt.titleId;
		}

		return 0;
	}

	private void check(Player player, int level, PbRecord.StageRecord record) {
		PbDown.TowerCheckRst.Builder rst = PbDown.TowerCheckRst.newBuilder();
		rst.setResult(false).setErrInfo("服务器繁忙，请稍后再试");
		TowerRecord r = player.getTowerRecord();
		rst.setStageId(r.getLevel());
		Stage stage = r.tmpStage;
		if (stage != null) {
			TowerStage ts = (TowerStage) stage;
			rst.setStageName(ts.tst.layerName);
			stage.getRecordUtil().setInStageRecord(record);
			stage.combat(player);
			stage.proccessReward(player);
			rst.setResult(true);
			if ((stage.isWin()) && (ts.finish)) {
				List tops = Platform.getTopManager().getRanks(0);
				if ((tops == null) || (tops.isEmpty()) || (ts.tst.id > ((Top) tops.get(0)).getValue())) {
					Platform.getPlayerManager().boardCast(MessageFormat.format(
							"<p style=21>恭喜玩家</p><p style=16>{0}</p><p style=21>，全服第一个击杀了过关斩将中的</p><p style=20>{1}</p><p style=21>，成为首个获得官职{2}的玩家</p>",
							new Object[] { player.getName(), ts.tst.name,
									this.titleNames.get(Integer.valueOf(ts.tst.id)) }));
				}

				rst.setMoney(ts.tst.money);
				rst.setWarriorSoul(ts.tst.warriorSpirit);
				rst.setWin(true);
				player.send(2062, rst.build());
				if (ts.tst.type == 2) {
					PbDown.TowerBoxNotice.Builder notice = PbDown.TowerBoxNotice.newBuilder();
					for (Reward reward : ts.tst.rewards) {
						notice.addRewards(reward.genPbReward());
					}
					player.send(2068, notice.build());
				}
				Platform.getLog().logTower(player, "tower");
				return;
			}
			rst.setWin(false);

			Platform.getLog().logTower(player, "tower");
		}
		player.send(2062, rst.build());
		r.tmpStage = null;
	}

	private void challenge(Player player) {
		PbDown.TowerChallengeRst.Builder rst = PbDown.TowerChallengeRst.newBuilder();
		TowerRecord r = player.getTowerRecord();
		if (r.getLeftMultiChlngSeconds(player) > 0) {
			rst.setResult(false);
			rst.setStageId(0);
			rst.setErrInfo("扫荡中，不能挑战");
		} else if (r.getLeftChallengeTimes() > 0) {
			MapTemplate mt = MapService.getSpecialMapTemplate(10);
			StageTemplate st = mt.stageTemplates[0];
			TowerStageTemplate tst = (TowerStageTemplate) towerTemplates.get(Integer.valueOf(r.getLevel()));
			if ((tst == null) && (r.getLevel() > 0)) {
				int MAX_LEVEL = 0;
				for (TowerStageTemplate tmp : towerTemplates.values()) {
					if (tmp.nextLayer == -1) {
						MAX_LEVEL = tmp.id;
					}
				}
				tst = (TowerStageTemplate) towerTemplates.get(Integer.valueOf(MAX_LEVEL));
			}
			if ((r.isPass()) && (tst.nextLayer == -1)) {
				rst.setResult(false);
				rst.setStageId(r.getLevel());
				rst.setErrInfo("当前已经在最高层，请先重置");
			} else {
				r.setChallengeTimes(r.getChallengeTimes() + 1);
				TowerStage stage = new TowerStage(st.channels[0].getPositionInfo(), st.secenId, tst, player);
				Team offen = new Team(stage);
				Team defen = new Team(stage);
				offen.setUnits(player.getWarriors().getStands());
				defen.setUnits(tst.section.getMonsters());
				stage.init(offen, defen);
				rst.setResult(true).setStageId(r.getLevel()).setStageInfo(stage.getInfoBuilder());
				r.tmpStage = stage;
			}

		} else {
			VipService.notifyChallengeTimeBuyCost(player, PbCommons.VipBuyTimeType.TOWER);
			return;
		}
		player.send(2060, rst.build());
	}

	private void next(Player player) {
		PbDown.TowerNextRst.Builder rst = PbDown.TowerNextRst.newBuilder();
		TowerRecord r = player.getTowerRecord();
		if ((r.getLevel() % 5 == 0) && (r.isPass()))
			if (towerTemplates.get(Integer.valueOf(r.getLevel() + 1)) == null) {
				rst.setResult(false).setErrInfo("已经达到最后一关");
			} else {
				r.setLevel(r.getLevel() + 1);
				r.setPass(false);
				rst.setResult(true);
			}
		else {
			rst.setResult(false).setErrInfo("还未通关");
		}
		player.send(2064, rst.build());
	}

	private void reset(Player player) {
		PbDown.TowerResetRst.Builder rst = PbDown.TowerResetRst.newBuilder();
		TowerRecord r = player.getTowerRecord();
		if ((r.getLevel() <= 1) && (r.getLeftChallengeTimes() > 0)) {
			rst.setResult(false).setErrInfo("当前正在第一关，无需重置");
		} else if (r.getLeftFreeResetTimes() < 1) {
			rst.setResult(false).setErrInfo("今日重置次数不足");
		} else if (r.getLeftMultiChlngSeconds(player) > 0) {
			rst.setResult(false).setErrInfo("扫荡中，不能重置");
		} else {
			r.setResetTimes(r.getResetTimes() + 1);
			r.setLevel(1);
			r.setChallengeTimes(0);
			r.setPass(false);
			rst.setResult(true);
			Platform.getEventManager().addEvent(new Event(2057, new Object[] { player }));
			Platform.getLog().logTower(player, "towerreset");
		}
		player.send(2056, rst.build());
	}

	private void quitMultiChallenge(Player player) {
		PbDown.TowerQuitMultiChallengeRst.Builder rst = PbDown.TowerQuitMultiChallengeRst.newBuilder();
		TowerRecord r = player.getTowerRecord();
		if (r.getMultiChallengeTime() == 0L) {
			rst.setResult(false).setErrInfo("当前不在扫荡中");
		} else {
			r.endMultiChallenge(player);
			rst.setResult(true);
		}
		player.send(2066, rst.build());
	}

	private void multiChallenge(Player player) {
		PbDown.TowerMultiChallengeRst.Builder rst = PbDown.TowerMultiChallengeRst.newBuilder();
		TowerRecord r = player.getTowerRecord();
		if (r.getMultiChallengeTime() != 0L) {
			rst.setResult(false).setErrInfo("正在扫荡中");
		} else if ((r.getCurrentLevel() > r.getMaxLevel())
				|| ((r.isPass()) && (r.getCurrentLevel() == r.getMaxLevel()))) {
			rst.setResult(false).setErrInfo("当前已经在最高层，请先重置");
		} else {
			r.setMultiChallengeTime(System.currentTimeMillis());
			rst.setResult(true);
		}

		player.send(2058, rst.build());
	}

	private void stageReward(Player player, int stageId) {
		PbDown.TowerStageRewardRst.Builder rst = PbDown.TowerStageRewardRst.newBuilder();
		PbCommons.TowerReward r = (PbCommons.TowerReward) this.towerRewards.get(Integer.valueOf(stageId));
		if (r != null) {
			rst.setResult(true);
			rst.setRewards(r);
			rst.setStageId(stageId);
		} else {
			rst.setResult(false);
			rst.setErrInfo("该关卡没有宝箱奖励");
		}
		player.send(2054, rst.build());
	}

	private void mapReward(Player player) {
		PbDown.TowerMapRewardInfo.Builder rst = PbDown.TowerMapRewardInfo.newBuilder();
		rst.setResult(true);
		for (PbCommons.TowerReward r : this.towerRewards.values()) {
			rst.addRewards(r);
		}
		player.send(2052, rst.build());
	}

	private void info(Player player) {
		PbDown.TowerInfoRst.Builder rst = PbDown.TowerInfoRst.newBuilder();
		TowerRecord r = player.getTowerRecord();
		TowerStageTemplate tt = (TowerStageTemplate) towerTemplates.get(Integer.valueOf(r.getCurrentLevel()));

		rst.setResult(true).setCondition((tt.fCondition == null) ? "击败所有敌人" : tt.fCondition.getName())
				.setCurrentStage(r.getCurrentLevel()).setMaxStage(r.getMaxLevel()).setHasRewardBox(tt.rewards != null)
				.setLeftChallengeTimes(r.getLeftChallengeTimes())
				.setLeftMultiChallengeSeconds(r.getLeftMultiChlngSeconds(player) * 1000)
				.setLeftResetTimes(r.getLeftFreeResetTimes()).setMaxChallengeTimes(2).setMoney(tt.money)
				.setName(tt.name).setIsPassed(r.isPass()).setWarriorSpirit(tt.warriorSpirit);
		if (tt.money > 0) {
			rst.addCurrRewards(new Reward(2, tt.money, null).genPbReward());
		}
		if (tt.warriorSpirit > 0) {
			rst.addCurrRewards(new Reward(8, tt.warriorSpirit, null).genPbReward());
		}
		PbCommons.TowerReward ctr = (PbCommons.TowerReward) this.towerRewards.get(Integer.valueOf(tt.id));
		if (ctr != null) {
			for (PbCommons.PbReward pbr : ctr.getRewardsList()) {
				rst.addCurrRewards(pbr);
			}
		}

		for (PbCommons.TowerReward tr : this.towerRewards.values()) {
			rst.addRewards(tr);
		}
		int start = (r.getCurrentLevel() - 1) / 5 * 5 + 1;
		for (int i = start; i < start + 5; ++i) {
			rst.addStages(((TowerStageTemplate) towerTemplates.get(Integer.valueOf(i))).genPb());
		}
		TowerStageTemplate nowtt = (TowerStageTemplate) towerTemplates.get(Integer.valueOf(r.getMaxLevel()));
		TowerStageTemplate nexttt = (TowerStageTemplate) towerTemplates.get(Integer.valueOf(r.getMaxLevel() + 1));
		if (nowtt != null) {
			rst.setTitleID(nowtt.titleId);
			rst.setTitleAttribute(r.getTitleAttribute().genAttributeFull());
		} else {
			rst.setTitleID(0);
			rst.setTitleAttribute(new Attributes().genAttributeFull());
		}
		if (nexttt == null) {
			rst.setNextTitleID(-1);
			rst.setNextName("");
		} else {
			rst.setNextTitleID(nexttt.titleId);
			rst.setNextTitleAttribute(nexttt.getAttributes().genAttributeFull());
			rst.setNextName(nexttt.name);
		}

		player.send(2048, rst.build());
	}

	public int[] getEventCodes() {
		return new int[] { 1005 };
	}

	public void handleEvent(Event event) {
		if (event.type == 1005)
			for (Player player : Platform.getPlayerManager().players.values())
				if (player != null)
					player.getTowerRecord().checkMultiChallenge(player);
	}
}
