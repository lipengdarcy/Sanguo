package org.darcy.sanguo.divine;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;

public class DivineService implements Service, PacketHandler {
	public static int[] weights = new int[0];
	public static int[] firstChangeWeights = new int[0];
	public static List<DivineReward> rewards = new ArrayList();

	public int[] getCodes() {
		return new int[] { 2109, 2105, 2117, 2115, 2103, 2113, 2107, 2111 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 23))) {
			return;
		}

		switch (packet.getPtCode()) {
		case 2109:
			break;
		case 2105:
			divine(player);
			break;
		case 2117:
			finish(player);
			break;
		case 2115:
			getReward(player);
			break;
		case 2103:
			info(player);
			break;
		case 2113:
			refresh(player);
			break;
		case 2107:
			rewardsInfo(player);
			break;
		case 2111:
			update(player);
		case 2104:
		case 2106:
		case 2108:
		case 2110:
		case 2112:
		case 2114:
		case 2116:
		}
	}

	private void refresh(Player player) {
		DivineReward reward;
		PbDown.DivineRefreshRst.Builder rst = PbDown.DivineRefreshRst.newBuilder();
		DivineRecord r = player.getDivineRecord();
		rst.setResult(false);

		boolean canRefresh = false;
		for (Iterator localIterator = rewards.iterator(); localIterator.hasNext();) {
			reward = (DivineReward) localIterator.next();
			if (!(r.getRewardRecords().contains(Integer.valueOf(reward.id)))) {
				canRefresh = true;
				break;
			}
		}
		if (!(canRefresh))
			rst.setErrInfo("当前没有可领取的奖励，不能刷新");
		else if (player.getDivineRecord().isUpdated()) {
			if (player.getJewels() < 50) {
				rst.setErrInfo("元宝不足");
			} else {
				player.decJewels(50, "divinerefresh");
				innerRefresh(player);
				for (Iterator localIterator = rewards.iterator(); localIterator.hasNext();) {
					reward = (DivineReward) localIterator.next();
					rst.addRewards(reward.genPb(player.getDivineRecord()));
				}
				rst.setResult(true);
				player.getDivineRecord().setRefreshTimes(player.getDivineRecord().getRefreshTimes() + 1);
				Platform.getLog().logDivine(player, "divinerefresh");
			}
		} else
			rst.setErrInfo("占卜还未升级");

		player.send(2114, rst.build());
	}

	public void innerRefresh(Player player) {
		List rwards = new ArrayList();
		DropService ds = (DropService) Platform.getServiceManager().get(DropService.class);
		for (DivineReward dr : rewards) {
			DropGroup dg = ds.getDropGroup(dr.dropId);
			List gains = dg.genGains(player);
			rwards.add(((Gain) gains.get(0)).newReward());
		}
		player.getDivineRecord().setRewards(rwards);
	}

	private void update(Player player) {
		PbDown.DivineUpdateRst.Builder rst = PbDown.DivineUpdateRst.newBuilder();
		rst.setResult(false);
		if (player.getJewels() < 200) {
			rst.setErrInfo("元宝不足");
		} else if (player.getLevel() >= 50) {
			rst.setResult(true);
			innerRefresh(player);
			player.getDivineRecord().setUpdated(true);
			player.decJewels(200, "divine_update");
			for (DivineReward reward : rewards)
				rst.addRewards(reward.genPb(player.getDivineRecord()));
		} else {
			rst.setErrInfo(MessageFormat.format("需主角等级达到{0}级才可操作", new Object[] { Integer.valueOf(50) }));
		}

		player.send(2112, rst.build());
	}

	private void getReward(Player player) {
		PbDown.DivineGetRewardRst.Builder rst = PbDown.DivineGetRewardRst.newBuilder();
		if (player.getBags().getFullBag() == -1) {
			DivineRecord r = player.getDivineRecord();
			if (r.getRewardsCount() < 1) {
				rst.setResult(false);
				rst.setErrInfo("没有可领取的奖励");
			} else {
				for (DivineReward reward : rewards) {
					if ((!(r.getRewardRecords().contains(Integer.valueOf(reward.id))))
							&& (r.getTotalScores() >= reward.needScore)) {
						Reward rwd = reward.getReward(r);
						rwd.add(player, "divine");
						r.getRewardRecords().add(Integer.valueOf(reward.id));
						rst.addRewards(rwd.genPbReward());
						break;
					}
				}
				rst.setResult(true);
				Platform.getEventManager().addEvent(new Event(2085, new Object[] { player }));
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("背包已满，请先清理背包");
		}

		player.send(2116, rst.build());
	}

	private void rewardsInfo(Player player) {
	}

	private void finish(Player player) {
	}

	private void divine(Player player) {
		DivineRecord r = player.getDivineRecord();
		PbDown.DivineRst.Builder rst = PbDown.DivineRst.newBuilder();
		rst.setResult(false);
		if (r.getTotalScores() > 1000) {
			rst.setErrInfo("已经达到最大卦数，请明天再来");
			player.send(2106, rst.build());
			return;
		}
		if (r.getLeftDivineTimes() > 0) {
			r.setDivineTimes(r.getDivineTimes() + 1);
		} else if (player.getJewels() >= 10) {
			player.decJewels(10, "divine");
		} else {
			rst.setErrInfo("元宝不足");
			player.send(2106, rst.build());
			return;
		}

		int[] divine = new int[5];
		PbCommons.Divine.Builder db = PbCommons.Divine.newBuilder();
		int[] theWeights = weights;
		boolean first = player.getPool().getBool(19, true);
		if (first) {
			theWeights = firstChangeWeights;
			player.getPool().set(19, Boolean.valueOf(false));
		}
		for (int i = 0; i < 5; ++i) {
			divine[i] = Calc.weight(theWeights);
			db.addQuality(divine[i]);
		}
		r.setDivine(divine);
		int score = r.getCurrentScore(divine);
		r.setTotalScores(r.getTotalScores() + score);
		db.setScores(score);
		rst.setDivine(db);
		rst.setResult(true);
		Platform.getLog().logDivine(player, "divine");
		Platform.getEventManager().addEvent(new Event(2031, new Object[] { player }));

		player.send(2106, rst.build());
	}

	private void info(Player player) {
		PbDown.DivineInfoRst.Builder rst = PbDown.DivineInfoRst.newBuilder();
		DivineRecord r = player.getDivineRecord();

		rst.setLeftTimes(r.getLeftDivineTimes()).setMaxTimes(5).setResult(true).setRewardCount(r.getRewardsCount())
				.setTotalScore(r.getTotalScores());
		if (r.getDivine() != null) {
			PbCommons.Divine.Builder db = PbCommons.Divine.newBuilder();
			db.setScores(r.getCurrentScore(r.getDivine()));
			for (int i : r.getDivine()) {
				db.addQuality(i);
			}
			rst.setDivine(db);
		}

		rst.setDivinePrice(10);
		rst.setPrice((r.isUpdated()) ? 50 : 200);
		for (DivineReward reward : rewards) {
			rst.addRewards(reward.genPb(r));
		}
		rst.setUpdated(r.isUpdated());
		rst.setPrice((r.isUpdated()) ? 50 : 200);
		rst.setUpdateLevel(50);
		rst.setLeftRefreshTimes(r.getLeftRefreshTimes(player));

		player.send(2104, rst.build());
	}

	private void loadData() {
		List<Row> list = ExcelUtils.getRowList("qiceskill.xls", 2);
		weights = new int[list.size()];
		firstChangeWeights = new int[list.size()];

		int pos = 0, i = 0;
		for (Row r : list) {
			weights[i] = (int) r.getCell(1).getNumericCellValue();
			firstChangeWeights[i] = (int) r.getCell(2).getNumericCellValue();
			i++;
		}

		List<Row> list2 = ExcelUtils.getRowList("qiceskill.xls", 2, 1);
		for (Row r : list2) {
			pos = 0;
			if (r.getCell(pos) == null)
				return;
			DivineReward dr = new DivineReward();
			dr.id = (int) r.getCell(pos++).getNumericCellValue();
			dr.needScore = (int) r.getCell(pos++).getNumericCellValue();
			String a = null;
			try {
				a = r.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				a = Integer.toString((int) r.getCell(pos).getNumericCellValue());
			}
			pos++;
			dr.reward = new Reward(a);
			dr.dropId = (int) r.getCell(pos++).getNumericCellValue();
			rewards.add(dr);
		}
	}

	public void startup() throws Exception {
		loadData();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		loadData();
	}
}
