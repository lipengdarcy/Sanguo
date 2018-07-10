package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.NormalItem;
import org.darcy.sanguo.item.NormalItemTemplate;
import org.darcy.sanguo.item.itemeffect.EffectResult;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.OhtersInfoAsyncCall;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerLevel;
import org.darcy.sanguo.player.PropertyType;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.tactic.Tactic;
import org.darcy.sanguo.time.Crontab;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.Vip;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class PlayerService implements Service, PacketHandler, EventHandler {
	public Map<Integer, PlayerLevel> playerLevels = new HashMap();
	public int[][] manualSkills = null;

	public void startup() throws Exception {
		loadPlayerLevel();
		loadGrade();
		new Thread(new UpdatePlayerRunnable(), "UpdatePlayerRunnable").start();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
		new Crontab("0 0 0", 1002);
		PropertyType.register();
	}

	private void loadGrade() {
		List<Row> list = ExcelUtils.getRowList("grade.xls", 2);
		org.darcy.sanguo.attri.BtlCalc.FACTORS = new int[50];
		for (Row row : list) {
			int pos = 0;
			if ((row == null) || (row.getCell(pos) == null)) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			++pos;
			int value = (int) (row.getCell(pos++).getNumericCellValue() * 10.0D);
			org.darcy.sanguo.attri.BtlCalc.FACTORS[id] = value;
		}
	}

	/**
	 * 加载玩家等级数据
	 */
	private void loadPlayerLevel() {
		List<Row> list = ExcelUtils.getRowList("player.xls", 2);
		org.darcy.sanguo.attri.BtlCalc.FACTORS = new int[50];
		for (Row row : list) {
			int pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int level = (int) row.getCell(pos++).getNumericCellValue();
			int needExp = (int) row.getCell(pos++).getNumericCellValue();
			int canOnStage = (int) row.getCell(pos++).getNumericCellValue();
			int moneyReward = (int) row.getCell(pos++).getNumericCellValue();
			int staminaReward = (int) row.getCell(pos++).getNumericCellValue();
			int tacticsPointReward = (int) row.getCell(pos++).getNumericCellValue();
			int unLockFunction = (int) row.getCell(pos++).getNumericCellValue();

			PlayerLevel playerLevel = new PlayerLevel();
			playerLevel.level = level;
			playerLevel.needExp = needExp;
			playerLevel.onStageWarriorNum = canOnStage;
			playerLevel.moneyReward = moneyReward;
			playerLevel.staminaReward = staminaReward;
			playerLevel.tacticPointReward = tacticsPointReward;
			playerLevel.unLockFunction = unLockFunction;
			this.playerLevels.put(Integer.valueOf(level), playerLevel);
		}
	}

	public PlayerLevel getPlayerLevel(int level) {
		return ((PlayerLevel) this.playerLevels.get(Integer.valueOf(level)));
	}

	public int[] getCodes() {
		return new int[] { 1013, 1029, 1035, 2023, 1129, 1137, 1255, 2183 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		switch (packet.getPtCode()) {
		case 1013:
			getBag(player, packet);
			break;
		case 1029:
			useItem(player, packet);
			break;
		case 1035:
			getStageAndPosition(player);
			break;
		case 2023:
			manualSkillInfos(player);
			break;
		case 1129:
			othersInfo(player, packet);
			break;
		case 1137:
			vitalityStaminaTime(player);
			break;
		case 1255:
			JewelsLackNotice(player);
			break;
		case 2183:
			PbUp.GuideUpdate up = PbUp.GuideUpdate.parseFrom(packet.getData());
			guideUpdate(player, up.getProgress());
		}
	}

	private void manualSkillInfos(Player player) {
		PbDown.ManulSkillRst.Builder rst = PbDown.ManulSkillRst.newBuilder();
		rst.setResult(true);
		for (int i = 0; i < this.manualSkills[0].length; ++i) {
			int level = this.manualSkills[0][i];
			int sid = this.manualSkills[1][i];
			PbCommons.LockSkill.Builder s = PbCommons.LockSkill.newBuilder();
			s.setSkillId(sid).setUnLockLevel(level);
			if (level <= player.getLevel())
				s.setUnLock(true);
			else {
				s.setUnLock(false);
			}
			rst.addSkills(s);
		}
		player.send(2024, rst.build());
	}

	private void getBag(Player player, PbPacket.Packet packet) {
		PbDown.GetBagRst.Builder builder = PbDown.GetBagRst.newBuilder();
		int bagType = -1;
		try {
			bagType = PbUp.GetBag.parseFrom(packet.getData()).getBagType();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("获取背包数据失败");
			player.send(1014, builder.build());
			return;
		}
		builder.setBags(player.getBags().genPbBags(bagType));
		builder.setResult(true);
		player.send(1014, builder.build());
	}

	private void getStageAndPosition(Player player) {
		PbDown.StageAndPositionRst.Builder builder = PbDown.StageAndPositionRst.newBuilder();
		builder.setResult(true);
		builder.setStages(player.getWarriors().genStageStruct());
		builder.setStands(player.getWarriors().genStandStruct());
		builder.setFriends(player.getWarriors().genFriendStruct());
		Tactic t = player.getTacticRecord().getTactic(player.getTacticRecord().getSelect());
		if (t != null) {
			builder.setTactic(t.genTactic());
		}
		player.send(1036, builder.build());
	}

	private void useItem(Player player, PbPacket.Packet packet) {
		PbDown.ItemUseRst.Builder builder = PbDown.ItemUseRst.newBuilder();
		builder.setResult(true);
		int templateId = 0;
		int count = 0;
		try {
			PbUp.ItemUse itemUse = PbUp.ItemUse.parseFrom(packet.getData());
			templateId = itemUse.getItemId();
			count = itemUse.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("道具使用失败");
			player.send(1030, builder.build());
			return;
		}

		NormalItemTemplate template = (NormalItemTemplate) ItemService.getItemTemplate(templateId);
		if (!(template.canUse)) {
			builder.setResult(false);
			builder.setErrInfo("该物品不能使用");
		} else if (player.getLevel() < template.userLevel) {
			builder.setResult(false);
			builder.setErrInfo(
					MessageFormat.format("需主角等级达到{0}级才可操作", new Object[] { Integer.valueOf(template.userLevel) }));
		} else if (player.getBags().getItemCount(templateId) < count) {
			builder.setResult(false);
			builder.setErrInfo("物品不足");
		} else if (player.getVip().level < template.vipLevel) {
			builder.setResult(false);
			builder.setErrInfo("VIP等级不足");
		} else if (template.needItems.size() > 0) {
			List<String> needItems = template.needItems;
			List<Reward> needs = new ArrayList();
			for (String str : needItems) {
				needs.add(new Reward(str));
			}
			for (Reward reward : needs) {
				if ((reward.template == null)
						|| (player.getBags().getItemCount(reward.template.id) >= reward.count * count))
					continue;
				builder.setResult(false);
				builder.setErrInfo(
						MessageFormat.format("道具不足，使用{0}个{1}需要{2}个{3}", new Object[] { Integer.valueOf(count),
								template.name, Integer.valueOf(reward.count * count), reward.template.name }));
			}

		} else if (count > 50) {
			builder.setResult(false);
			builder.setErrInfo("物品每次最多使用50个");
		}
		if (builder.getResult()) {
			if ((template.normalItemType != 0) && (player.getBags().getFullBag() != -1)) {
				label457: builder.setResult(false);
				builder.setErrInfo("背包已满，请先清理背包");
			} else {
				List list = player.getBags().getItemByTemplateId(templateId);
				Item item = null;
				if ((list != null) && (list.size() > 0)) {
					item = (Item) player.getBags().getItemByTemplateId(templateId).get(0);
				}
				if ((item == null) || (item.getItemType() != 0)) {
					builder.setResult(false);
					builder.setErrInfo("物品类型错误");
				} else {
					List rewards = new ArrayList();
					int i = 1;
					for (i = 0; i < count; ++i) {
						EffectResult result = ((NormalItem) item).used(player);
						if (result.result == 1) {
							builder.setResult(false);
							builder.setErrInfo("道具使用失败");
							break;
						}
						builder.setResult(true);
						i = result.notifyType;
						if ((result.rewards != null) && (result.rewards.size() > 0)) {
							rewards.addAll(result.rewards);
						}
					}

					rewards = Reward.mergeReward(rewards);

					if ((((templateId == 12003) || (templateId == 12008))) && (rewards.size() > 0)) {
						boardCastGetNBItem(player, rewards,
								"<p style=13>[{0}]</p><p style=17>人品爆发，开启金宝箱时获得了</p><p style=15>{1}</p><p style=17>，真是羡煞旁人。</p>");
					}

					player.notifyGetItem(i, rewards);
				}
			}
		}
		player.send(1030, builder.build());
	}

	public static void boardCastGetNBItem(Player player, List<Reward> rewards, String content) {
		DebrisTemplate dt;
		List<String> names = new ArrayList();
		for (Reward r : rewards) {
			if (r.type != 0)
				continue;
			if (r.template.quality < 6) {
				continue;
			}
			if (r.template.type == 4) {
				names.add(r.template.name);
			} else if (r.template.type == 1) {
				if ((r.template.id != 36302) && (r.template.id != 37302)) {
					names.add(r.template.name);
				}
			} else if (r.template.type == 3) {
				dt = (DebrisTemplate) r.template;
				if (((dt.debrisType != 4) && (dt.debrisType != 3)) || (r.template.id == 23095)
						|| (r.template.id == 23096) || (r.template.id == 23097) || (r.template.id == 23089)
						|| (r.template.id == 23090) || (r.template.id == 23091))
					continue;
				names.add(r.template.name);
			}

		}

		if (names.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (String str : names) {
				sb.append(",[").append(str).append("]");
			}
			String msg = MessageFormat.format(content, new Object[] { player.getName(), sb.substring(1) });
			Platform.getPlayerManager().boardCast(msg);
		}
	}

	private void othersInfo(Player player, PbPacket.Packet packet) {
		OhtersInfoAsyncCall call = new OhtersInfoAsyncCall(player.getSession(), packet);
		Platform.getThreadPool().execute(call);
	}

	private void vitalityStaminaTime(Player player) {
		long curTime = System.currentTimeMillis();
		PbDown.VitalityStaminaTimeRst rst = PbDown.VitalityStaminaTimeRst.newBuilder().setResult(true)
				.setNextRecoverVitalityTime(player.getRestTimeToNextRecoverVitality(curTime))
				.setFullRecoverVitalityTime(player.getRestTimeToFullVitality(curTime))
				.setNextRecoverStaminaTime(player.getRestTimeToNextRecoverStamina(curTime))
				.setFullRecoverStaminaTime(player.getRestTimeToFullStamina(curTime)).build();
		player.send(1138, rst);
	}

	public static void JewelsLackNotice(Player player) {
		PbDown.JewelsLackNoticeRst.Builder b = PbDown.JewelsLackNoticeRst.newBuilder().setResult(true);
		Vip vip = player.getVip();
		if (!(player.isAlreadyCharge())) {
			b.setVipLevel(0);

			for (Reward r : VipService.firstPayRewards)
				b.addRewards(r.genPbReward());
		} else if (vip.getNext() == null) {
			b.setVipLevel(-1);
		} else {
			Vip next = vip.getNext();
			b.setVipLevel(next.level);

			for (Reward r : next.vipBag) {
				b.addRewards(r.genPbReward());
			}
		}
		player.send(1256, b.build());
	}

	public static int getSelectedIndex(int gender) {
		if (gender == 1) {
			return 1;
		}
		return 11;
	}

	public void shutdown() {
		for (Player player : Platform.getPlayerManager().players.values()) {
			if (player == null)
				continue;
			try {
				player.save();
			} catch (Exception e) {
				Platform.getLog().logError(e);
			}
		}
	}

	public void reload() throws Exception {
	}

	public int[] getEventCodes() {
		return new int[] { 1002 };
	}

	public void handleEvent(Event event) {
		if (event.type == 1002) {
			long start = System.currentTimeMillis();
			int count = 0;
			for (Player p : Platform.getPlayerManager().players.values()) {
				if (p == null)
					continue;
				try {
					p.newDayRefreshIf();
					Function.notifyMainInterfaceFunction(p);
					++count;
				} catch (Exception e) {
					Platform.getLog().logError(e);
				}
			}

			Platform.getLog().logSystem("0点更新，共" + count + "人，耗时：" + (System.currentTimeMillis() - start));
		}
	}

	private void guideUpdate(Player player, String progress) {
		float data = 0.0F;
		try {
			data = Float.parseFloat(progress);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data < 1000.0F) {
			player.getPool().set(18, progress);
		}
		PbDown.GuideUpdateRst.Builder rst = PbDown.GuideUpdateRst.newBuilder();
		rst.setResult(true);
		player.send(2184, rst.build());
		Platform.getLog().logGuide(player, progress);
	}

	public static void guideInfo(Player player) {
		PbDown.GuideInfoRst.Builder rst = PbDown.GuideInfoRst.newBuilder();
		rst.setProgress(player.getPool().getString(18, ""));
		player.send(2186, rst.build());
	}

	class UpdatePlayerRunnable implements Runnable {
		int i;

		UpdatePlayerRunnable() {
			this.i = 0;
		}

		public void run() {
			try {
				Thread.sleep(60000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (true)
				try {
					do {
						long pre = System.currentTimeMillis();
						for (Player p : Platform.getPlayerManager().players.values()) {
							if ((p == null) || (p.getId() % 10 != this.i))
								continue;
							p.save();
						}

						long l = System.currentTimeMillis() - pre;
						if (l < 60000L)
							Thread.sleep(60000L - l);
						else {
							Thread.sleep(1000L);
						}
						this.i += 1;
						if (this.i >= 10)
							this.i = 0;
					} while (this.i % 5 != 0);
					Platform.getLog().logOnline(Platform.getPlayerManager().players.size());
				} catch (Exception e) {
					Platform.getLog().logError(e);
				}
		}
	}
}
