package org.darcy.sanguo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.boss.BossManager;
import org.darcy.sanguo.boss.BossRecord;
import org.darcy.sanguo.boss.BossReward;
import org.darcy.sanguo.boss.BossStage;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.map.MapTemplate;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbBoss;
import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class BossService implements Service, PacketHandler, EventHandler {
	public static List<BossReward> rewards = new ArrayList<BossReward>();
	public static List<BossReward> leagueRewards = new ArrayList<BossReward>();

	public static BossReward getReward(int rank) {
		for (BossReward r : rewards) {
			if ((r.minRank <= rank) && (r.maxRank >= rank)) {
				return r;
			}
		}
		if (rewards.size() == 0)
			return null;
		return ((BossReward) rewards.get(rewards.size() - 1));
	}

	public void loadRewards() {
		int pos = 0;
		List<Row> list = ExcelUtils.getRowList("worldboss.xls");
		for (Row r : list) {
			pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;
			BossReward br = new BossReward();
			br.id = (int) r.getCell(pos++).getNumericCellValue();
			br.minRank = (int) r.getCell(pos++).getNumericCellValue();
			br.maxRank = (int) r.getCell(pos++).getNumericCellValue();
			br.money = (int) r.getCell(pos++).getNumericCellValue();
			br.prestige = (int) r.getCell(pos++).getNumericCellValue();
			br.dropId = (int) r.getCell(pos++).getNumericCellValue();
			rewards.add(br);
		}

		List<Row> list2 = ExcelUtils.getRowList("bossleague.xls");
		for (Row r : list2) {
			pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;
			BossReward br = new BossReward();
			br.id = (int) r.getCell(pos++).getNumericCellValue();
			br.minRank = (int) r.getCell(pos++).getNumericCellValue();
			br.maxRank = (int) r.getCell(pos++).getNumericCellValue();
			br.money = (int) r.getCell(pos++).getNumericCellValue();
			br.prestige = (int) r.getCell(pos++).getNumericCellValue();
			br.dropId = (int) r.getCell(pos++).getNumericCellValue();
			leagueRewards.add(br);
		}
	}

	public int[] getCodes() {
		return new int[] { 2033, 2025, 2027, 2029, 2031, 2037, 1365 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 24))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 2033:
			chanllenge(player);
			break;
		case 2025:
			bossInfo(player);
			break;
		case 2027:
			PbUp.BossInspire inspire = PbUp.BossInspire.parseFrom(packet.getData());
			inspire(player, inspire.getInspireType());
			break;
		case 2029:
			rank(player);
			break;
		case 2031:
			resurgence(player);
			break;
		case 2037:
			exitBoss(player);
			break;
		case 1365:
			leagueRank(player);
		}
	}

	private void bossInfo(Player player) {
		PbDown.BossInfoRst.Builder rst = PbDown.BossInfoRst.newBuilder();
		BossManager manager = Platform.getBossManager();
		BossRecord record = player.getBossRecord();
		rst.setResult(true).setIsOpen(manager.isOpen());
		if (manager.isOpen()) {
			record.clearCheck();
			PbBoss.Bossing.Builder bsg = PbBoss.Bossing.newBuilder();
			bsg.setRank(manager.getRank(player)).setAtkAddRate(record.getAddAtkRate(player))
					.setAttackTimes(record.getChanllengeTimes()).setDamage(record.getTotalDamage())
					.setDamageRate((int) (record.getTotalDamage() * 10000L / manager.getBoss().getAttributes().get(7)))
					.setHeroTemplateId(manager.getBoss().getTemplateId())
					.setHp(manager.getBoss().getAttributes().getHp())
					.setLeftSeconds(manager.getLeftChanllengeSeconds() * 1000).setLevel(manager.data.bossLevel)
					.setMaxHp(manager.getBoss().getAttributes().get(7)).setName(manager.getBoss().getName())
					.setResurgenceCost(20).setResurgenceLeftSeconds(record.getLeftResurgenceSeconds() * 1000)
					.setSilverInspireLeftSeconds(record.getLeftInspireTime() * 1000)
					.setGoldInspireCd(record.getLeftGoldInspireTime() * 1000).setSilverInspireCost(10000)
					.setGoldInspireCost(100)
					.setState((record.isDie()) ? PbBoss.Bossing.PlayerState.DEAD : PbBoss.Bossing.PlayerState.ALIVE);
			if (player.getUnion() == null)
				bsg.setLeagueRank(-1);
			else {
				bsg.setLeagueRank(manager.getLeagueRank(player.getUnion().getLeagueId()));
			}
			rst.setBossing(bsg);
		} else {
			PbBoss.BossState.Builder bst = PbBoss.BossState.newBuilder();
			Player killer = manager.getKiller();
			bst.setKiller((killer != null) ? killer.getName() : "").setLeftSeconds(manager.getLeftOpenSeconds() * 1000)
					.setLevel(manager.data.bossLevel).setName(manager.getBoss().getName())
					.setLastRank(manager.getRank(player))
					.setKillerId((killer != null) ? manager.getKiller().getId() : -1).setOpenTime("21:00");
			List ranks = manager.getRanks();
			if (ranks != null) {
				for (int i = 0; (i < 3) && (i < ranks.size()); ++i) {
					Player p = null;
					try {
						p = Platform.getPlayerManager().getPlayer(((Integer) ranks.get(i)).intValue(), true, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (p != null) {
						bst.addWinners(p.getName());
					}
				}
			}
			rst.setBossState(bst);
		}
		player.send(2026, rst.build());
		if (manager.isOpen())
			manager.addPlayer(player);
	}

	private void exitBoss(Player player) {
		Platform.getBossManager().removePlayer(player);
	}

	private void inspire(Player player, PbCommons.InspireType type) {
		PbDown.BossInspireRst.Builder rst = PbDown.BossInspireRst.newBuilder();
		BossRecord r = player.getBossRecord();
		rst.setInspireType(type);

		if (r.getInspireCount() >= 40) {
			rst.setResult(false);
			rst.setErrInfo("已经达到最大鼓舞等级");
		} else if (type == PbCommons.InspireType.GOLD) {
			League l = Platform.getLeagueManager().getLeagueByPlayerId(player.getId());
			if ((l != null) && (l.getInfo().getWorldBossInspireCount() >= 40)) {
				rst.setResult(false);
				rst.setErrInfo("已经达到最大鼓舞等级");
			} else if (r.getLeftGoldInspireTime() > 0) {
				rst.setResult(false);
				rst.setErrInfo("冷却时间未到，无法操作");
			} else if (player.getJewels() < 100) {
				rst.setResult(false);
				rst.setErrInfo("元宝不足");
			} else {
				r.inspire(player);
				rst.setResult(true);
				rst.setAtkAddRate(r.getAddAtkRate(player));
				rst.setInspireType(type);
				rst.setGoldCd(r.getLeftGoldInspireTime() * 1000);
			}
		} else if (type == PbCommons.InspireType.SILVER) {
			rst.setResult(false);
			rst.setErrInfo("您无权进行该操作");
		}
		player.send(2028, rst.build());
	}

	private void rank(Player player) {
		PbDown.BossRankRst.Builder rst = PbDown.BossRankRst.newBuilder();
		BossManager m = Platform.getBossManager();
		List ranks = m.getRanks();
		rst.setResult(true);
		Player killer = m.getKiller();
		if (killer != null) {
			PbBoss.BossRankNode.Builder node = PbBoss.BossRankNode.newBuilder();
			node.setDamage(killer.getBossRecord().getTotalDamage()).setMini(killer.getMiniPlayer().genMiniUser())
					.setRank(0);
			rst.addNodes(node);
		}
		int count = 10;
		for (int i = 0; (i < ranks.size()) && (i < count); ++i) {
			int rank = i + 1;
			int pid = ((Integer) ranks.get(i)).intValue();
			try {
				Player p = Platform.getPlayerManager().getPlayer(pid, true, true);
				if (p != null) {
					PbBoss.BossRankNode.Builder node = PbBoss.BossRankNode.newBuilder();
					node.setDamage(p.getBossRecord().getTotalDamage()).setMini(p.getMiniPlayer().genMiniUser())
							.setRank(rank);
					rst.addNodes(node);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		player.send(2030, rst.build());
	}

	private void resurgence(Player player) {
		BossRecord r = player.getBossRecord();
		if (r.getLeftResurgenceSeconds() <= 0) {
			r.setDie(false);
			chanllenge(player);
			return;
		}
		PbDown.BossResurgenceRst.Builder rst = PbDown.BossResurgenceRst.newBuilder();
		if (player.getJewels() < 20) {
			rst.setResult(false).setErrInfo("元宝不足");
		} else {
			player.decJewels(20, "bosscdclear");
			r.setDie(false);
			rst.setResult(true);
		}

		player.send(2032, rst.build());
	}

	private void chanllenge(Player player) {
		PbDown.BossChallengeRst.Builder rst = PbDown.BossChallengeRst.newBuilder();
		BossManager m = Platform.getBossManager();
		BossRecord r = player.getBossRecord();

		if (!(m.isOpen())) {
			rst.setResult(false).setErrInfo("挑战已经关闭");
		} else if ((r.isDie()) && (r.getLeftResurgenceSeconds() > 0)) {
			rst.setResult(false).setErrInfo("死人是不会动的");
		} else {
			MapTemplate mt = MapService.getSpecialMapTemplate(2);
			StageTemplate st = mt.stageTemplates[0];
			BossStage stage = new BossStage(st.channels[0].getPositionInfo(), mt.name, st.secenId, player, m.getBoss(),
					r.getAddAtkRate(player));
			stage.init();
			stage.combat(player);
			stage.proccessReward(player);
			int rank = Platform.getBossManager().getRank(player);
			r.setChanllengeTimes(r.getChanllengeTimes() + 1);
			rst.setResult(true).setDamage(stage.damage).setMoney(0).setPrestige(10).setRank(rank)
					.setStageInfo(stage.getInfoBuilder()).setStageRecord(stage.getRecordUtil().getStageRecord());
		}

		player.send(2034, rst.build());
	}

	private void leagueRank(Player player) {
		PbDown.BossLeagueRankRst.Builder b = PbDown.BossLeagueRankRst.newBuilder().setResult(true);
		if (player.getUnion() != null) {
			b.setLeagueId(player.getUnion().getLeagueId());
		}
		BossManager bm = Platform.getBossManager();
		List ranks = bm.getLeagueRanks();
		int count = 50;
		for (int i = 0; (i < ranks.size()) && (i < count); ++i) {
			int rank = i + 1;
			int id = ((Integer) ranks.get(i)).intValue();
			League l = Platform.getLeagueManager().getLeagueById(id);
			if (l != null) {
				int damage = l.getInfo().getWorldBossDamage();
				if (damage > 0) {
					PbBoss.BossLeagueRankNode.Builder nb = PbBoss.BossLeagueRankNode.newBuilder();
					nb.setRank(rank);
					nb.setDamage(l.getInfo().getWorldBossDamage());
					nb.setLeagueName(l.getName());
					nb.setLeader(l.getMember(l.getLeader()).getName());
					nb.setCount(l.getMemberCount());
					nb.setLimit(l.getMemberLimit());
					nb.setLevel(l.getLevel());
					nb.setId(id);

					b.addNodes(nb);
				}
			}
		}
		player.send(1366, b.build());
	}

	public void startup() throws Exception {
		loadRewards();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		new Crontab("21 0 0", 1008);
		new Crontab("21 15 0", 1009);
		Platform.getBossManager().init();
	}

	public void pushDamage(Player player, int damage) {
		PbDown.BossDamageRst rst = PbDown.BossDamageRst.newBuilder().addUnits(PbBoss.BossDamageUnit.newBuilder()
				.setDamage(damage).setName(player.getName()).setPlayerId(player.getId())).build();
		BossManager m = Platform.getBossManager();
		Set<Player> players = m.getPool();
		for (Player p : players)
			p.send(2036, rst);
	}

	public void shutdown() {
		if (Platform.getBossManager().isOpen()) {
			Platform.getBossManager().endBoss();
		}
		Platform.getBossManager().save();
	}

	public void reload() throws Exception {
	}

	public int[] getEventCodes() {
		return new int[] { 1008, 1009 };
	}

	public void handleEvent(Event event) {
		if (event.type == 1008) {
			Platform.getBossManager().startBoss();
			for (Player player : Platform.getPlayerManager().players.values()) {
				if (player != null) {
					Function.notifyMainNum(player, 19, 1);
				}
			}
			Platform.getPlayerManager().boardCast("<p style=17>铜雀化身已降临，请各路诸讨伐铜雀化身！</p>");
		} else if (event.type == 1009) {
			BossManager m = Platform.getBossManager();
			if (m.isOpen()) {
				m.endBoss();
				for (Player player : Platform.getPlayerManager().players.values()) {
					if (player != null) {
						Function.notifyMainNum(player, 19, 0);
					}
				}
				Platform.getPlayerManager().boardCast(m.getBoardCastMsg());
			}
		}
	}
}
