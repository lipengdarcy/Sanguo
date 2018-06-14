package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.coup.Coup;
import org.darcy.sanguo.coup.CoupEn;
import org.darcy.sanguo.coup.CoupLevelUp;
import org.darcy.sanguo.coup.CoupRecord;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.LockSkill;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class CoupService implements Service, PacketHandler {
	public static HashMap<Integer, Coup> coups = new HashMap<Integer, Coup>();
	public static HashMap<Integer, CoupLevelUp> levelUps = new HashMap<Integer, CoupLevelUp>();

	public static HashMap<Integer, Integer> lvlSkills = new HashMap<Integer, Integer>();

	public static HashMap<Integer, List<CoupEn>> coupEns = (HashMap<Integer, List<CoupEn>>) new HashMap<Integer, List<CoupEn>>();

	private void loadData() {
		List<Row> list = ExcelUtils.getRowList("qiceskill.xls", 2);
		int pos = 0, i = 0;
		for (Row r : list) {

			pos = 0;
			if ((r == null) || (r.getCell(pos) == null))
				return;

			Coup coup = new Coup();
			coup.id = (int) r.getCell(pos++).getNumericCellValue();
			coup.unLockLevel = (int) r.getCell(pos++).getNumericCellValue();
			for (i = 0; i < coup.skills.length; ++i) {
				coup.skills[i] = (int) r.getCell(pos++).getNumericCellValue();
			}
			coups.put(Integer.valueOf(coup.id), coup);
		}

		List<Row> list2 = ExcelUtils.getRowList("qiceskill.xls", 2, 1);
		for (Row r : list2) {

			pos = 0;
			if ((r == null) || (r.getCell(pos) == null))
				return;

			CoupLevelUp level = new CoupLevelUp();
			level.level = (int) r.getCell(pos++).getNumericCellValue();
			level.minPlayerLevel = (int) r.getCell(pos++).getNumericCellValue();
			if (level.minPlayerLevel == -1) {
				continue;
			}
			for (i = 0; i < 3; ++i) {
				r.getCell(pos).setCellType(1);
				String s = r.getCell(pos++).getStringCellValue();
				if (!(s.equals("-1"))) {
					Reward reward = new Reward(s);
					level.needs.add(reward);
				}
			}
			levelUps.put(Integer.valueOf(level.level), level);
		}

		List<Row> list3 = ExcelUtils.getRowList("qiceskill.xls", 2, 2);
		for (Row r : list3) {
			pos = 0;
			if ((r == null) || (r.getCell(pos) == null))
				return;
			CoupEn coupEn = new CoupEn();
			coupEn.coupId = (int) r.getCell(pos++).getNumericCellValue();
			coupEn.coupLevel = (int) r.getCell(pos++).getNumericCellValue();
			r.getCell(pos).setCellType(1);
			String ids = r.getCell(pos++).getStringCellValue();
			coupEn.enIds = Calc.split(ids, ",");
			r.getCell(pos).setCellType(1);
			coupEn.desc = r.getCell(pos++).getStringCellValue();

			List<CoupEn> list_tmp = (List<CoupEn>) coupEns.get(Integer.valueOf(coupEn.coupId));
			if (list_tmp == null) {
				list_tmp = new ArrayList<CoupEn>();
				coupEns.put(Integer.valueOf(coupEn.coupId), list_tmp);
			}
			list_tmp.add(coupEn);
		}
	}

	private void generateLvlSkills() {
		lvlSkills.clear();
		for (Coup coup : coups.values())
			lvlSkills.put(Integer.valueOf(coup.unLockLevel), Integer.valueOf(coup.skills[0]));
	}

	public static int getLvlSkills(int level) {
		Integer skill = (Integer) lvlSkills.get(Integer.valueOf(level));
		if (skill != null)
			return skill.intValue();
		return -1;
	}

	public int[] getCodes() {
		return new int[] { 2125, 2123, 2129, 2127, 2181, 2189 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 8))) {
			return;
		}

		switch (packet.getPtCode()) {
		case 2125:
			PbUp.CoupActive act = PbUp.CoupActive.parseFrom(packet.getData());
			active(player, act.getId());
			break;
		case 2123:
			info(player);
			break;
		case 2129:
			PbUp.CoupUpdate up = PbUp.CoupUpdate.parseFrom(packet.getData());
			levelUp(player, up.getId());
			break;
		case 2127:
			PbUp.CoupUpdateInfo info = PbUp.CoupUpdateInfo.parseFrom(packet.getData());
			levelUpInfo(player, info.getId());
			break;
		case 2181:
			PbUp.CoupLearn learn = PbUp.CoupLearn.parseFrom(packet.getData());
			learn(player, learn.getId());
			break;
		case 2189:
			PbUp.CoupOrder order = PbUp.CoupOrder.parseFrom(packet.getData());
			List<Integer> cIds = order.getCoupIdsList();
			order(player, cIds);
		}
	}

	private void order(Player player, List<Integer> orders) {
		CoupRecord r = player.getCoupRecord();
		r.order(orders);
		r.refreshSkills(player);
		PbDown.CoupOrderRst.Builder rst = PbDown.CoupOrderRst.newBuilder();
		rst.setResult(true);
		player.send(2190, rst.build());
	}

	private void learn(Player player, int id) {
		PbDown.CoupLearnRst.Builder rst = PbDown.CoupLearnRst.newBuilder();
		rst.setResult(false);
		CoupRecord r = player.getCoupRecord();
		int level = r.getCoups()[(id - 1)];
		rst.setResult(false);
		if (level != 0) {
			rst.setErrorInfo("已经学习了该奇策");
		} else if (player.getLevel() < ((Coup) coups.get(Integer.valueOf(id))).unLockLevel) {
			rst.setErrorInfo("未到该奇策学习等级");
		} else {
			r.update(player, id);
			rst.setResult(true);
			for (int i = 0; i < 4; ++i) {
				int tmpId = i + 1;
				rst.addCanOpt(r.canOptCoup(tmpId, player));
			}

			level = r.getCoups()[(id - 1)];
			CoupLevelUp up = (CoupLevelUp) levelUps.get(Integer.valueOf(level));
			for (Reward w : up.needs) {
				rst.addNeeds(w.genPbReward());
			}

			Platform.getLog().logCoup(player, "coupunlock");
			Platform.getEventManager().addEvent(new Event(2073, new Object[] { player }));
		}
		player.send(2182, rst.build());
	}

	private void levelUp(Player player, int id) {
		PbDown.CoupUpdateRst.Builder rst = PbDown.CoupUpdateRst.newBuilder();
		CoupRecord r = player.getCoupRecord();
		int level = r.getCoups()[(id - 1)];
		CoupLevelUp up = (CoupLevelUp) levelUps.get(Integer.valueOf(level));
		rst.setResult(false);
		rst.setId(id);
		if (level == 0) {
			rst.setErrInfo("该奇策尚未学习");
		} else if (up == null) {
			rst.setErrInfo("该奇策已达最高等级");
		} else if (player.getLevel() < up.minPlayerLevel) {
			rst.setErrInfo(MessageFormat.format("人物等级达到{0}级才可升级", new Object[] { Integer.valueOf(up.minPlayerLevel) }));
		} else {
			Reward reward;
			String check = null;
			for (Iterator<?> localIterator = up.needs.iterator(); localIterator.hasNext();) {
				reward = (Reward) localIterator.next();
				check = reward.check(player);
				if (check != null) {
					break;
				}
			}
			if (check != null) {
				rst.setErrInfo(check);
			} else {
				rst.setResult(true);
				for (Iterator<?> localIterator = up.needs.iterator(); localIterator.hasNext();) {
					reward = (Reward) localIterator.next();
					reward.remove(player, "couplevelup");
				}
				r.update(player, id);
				Platform.getLog().logCoup(player, "couplevelup");

				++level;
				up = (CoupLevelUp) levelUps.get(Integer.valueOf(level));
				if (up != null) {
					rst.setNextBuff(-1);
					rst.setNextSkill(((Coup) coups.get(Integer.valueOf(id))).skills[level]);
					rst.setPlayerLevel(up.minPlayerLevel);
					for (Iterator<?> localIterator = up.needs.iterator(); localIterator.hasNext();) {
						reward = (Reward) localIterator.next();
						rst.addRewards(reward.genPbReward());
					}
				} else {
					rst.setNextBuff(-1);
					rst.setNextSkill(-1);
				}
				for (int i = 0; i < 4; ++i) {
					int tmpId = i + 1;
					rst.addCanOpt(r.canOptCoup(tmpId, player));
				}
				Platform.getEventManager().addEvent(new Event(2074, new Object[] { player }));
				player.getWarriors().getMainWarrior().refreshkEns(player.getWarriors().getAllWarriorAndFellow());
				player.getWarriors().getMainWarrior().refreshAttributes(true);
				player.getWarriors().getMainWarrior().sync();
			}

		}

		player.send(2130, rst.build());
	}

	private void levelUpInfo(Player player, int id) {
		PbDown.CoupUpdateInfoRst.Builder rst = PbDown.CoupUpdateInfoRst.newBuilder();
		CoupRecord r = player.getCoupRecord();
		int level = r.getCoups()[(id - 1)];
		CoupLevelUp up = (CoupLevelUp) levelUps.get(Integer.valueOf(level));
		rst.setResult(false);
		rst.setId(id);
		if (level == 0) {
			rst.setErrInfo("该奇策尚未学习");
		} else if (up == null) {
			rst.setErrInfo("该奇策已达最高等级");
		} else {
			rst.setResult(true);
			rst.setNextBuff(-1);
			rst.setNextSkill(((Coup) coups.get(Integer.valueOf(id))).skills[level]);
			rst.setPlayerLevel(up.minPlayerLevel);
			for (Reward reward : up.needs) {
				rst.addRewards(reward.genPbReward());
			}
		}

		player.send(2128, rst.build());
	}

	private void active(Player player, int id) {
	}

	private void info(Player player) {
		PbDown.CoupInfoRst.Builder rst = PbDown.CoupInfoRst.newBuilder();
		CoupRecord r = player.getCoupRecord();
		rst.setResult(true);
		for (LockSkill skill : r.getSkills()) {
			PbCommons.Coup.Builder coup = PbCommons.Coup.newBuilder();
			int level = r.getLevels()[(skill.getCoup().id - 1)];
			coup.setActived(skill.isOpen()).setBuffId(-1).setId(skill.getCoup().id).setLevel(level).setMaxLevel(10)
					.setOpen(skill.isOpen()).setOpenLevel(skill.getOpenLevel()).setSkillId(skill.getSkill().getId())
					.setNextSkillId(skill.getCoup().getNextSkill(level))
					.setCanOpt(r.canOptCoup(skill.getCoup().id, player));
			if (level > 0) {
				CoupLevelUp up = (CoupLevelUp) levelUps.get(Integer.valueOf(level));
				if ((up != null) && (up.needs != null)) {
					for (Reward reward : up.needs) {
						coup.addUpNeeds(reward.genPbReward());
					}
				}
			}

			List<CoupEn> ens = (List<CoupEn>) coupEns.get(Integer.valueOf(skill.getCoup().id));
			for (CoupEn en : ens) {
				if (en.coupLevel != 1) {
					PbCommons.CoupEn.Builder cb = PbCommons.CoupEn.newBuilder();
					cb.setDesc(en.desc).setUnLock(level >= en.coupLevel).setUnlockLevel(en.coupLevel);
					coup.addEns(cb);
				}
			}
			rst.addCoups(coup);
		}
		player.send(2124, rst.build());
	}

	public void startup() throws Exception {
		loadData();
		generateLvlSkills();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		coups.clear();
		levelUps.clear();
		loadData();
		generateLvlSkills();
	}
}
