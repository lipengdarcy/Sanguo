package org.darcy.sanguo.recruit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.activity.item.RecruitDropAI;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class RecruitService implements Service, PacketHandler {
	public Map<Integer, RecruitTemplate> recruitTemplates = new HashMap<Integer, RecruitTemplate>();

	public void startup() throws Exception {
		loadRecruitTemplate();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public RecruitTemplate getTemplate(int id) {
		return ((RecruitTemplate) this.recruitTemplates.get(Integer.valueOf(id)));
	}

	public int[] getCodes() {
		return new int[] { 1015, 1021 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 4))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1015:
			recruit(player, packet);
			break;
		case 1021:
			getRecruitInfo(player);
		}
	}

	private void loadRecruitTemplate() {
		List<Row> list = ExcelUtils.getRowList("recruit.xls", 2);
		for (Row row : list) {

			int pos = 0;

			RecruitTemplate template = new RecruitTemplate();
			template.id = (int) row.getCell(pos++).getNumericCellValue();
			template.desc = row.getCell(pos++).getStringCellValue();
			template.freeNum = (int) row.getCell(pos++).getNumericCellValue();
			template.freeInterval = (int) row.getCell(pos++).getNumericCellValue();
			template.initInterval = (int) row.getCell(pos++).getNumericCellValue();
			String singleCostStr = row.getCell(pos++).getStringCellValue();
			if ((singleCostStr != null) && (!(singleCostStr.equals("-1")))) {
				template.singleCost = new Reward(singleCostStr);
			}
			String tenCostStr = null;
			try {
				tenCostStr = row.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				tenCostStr = Integer.toString((int) row.getCell(pos).getNumericCellValue());
			}
			pos++;

			if ((tenCostStr != null) && (!(tenCostStr.equals("-1")))) {
				template.tenCost = new Reward(tenCostStr);
			}
			String drops = null;
			try {
				drops = row.getCell(pos).getStringCellValue();
			} catch (Exception e) {
				drops = Integer.toString((int) row.getCell(pos).getNumericCellValue());
			}
			pos++;
			
			int firstDropId = (int) row.getCell(pos++).getNumericCellValue();
			String insureStr = row.getCell(pos++).getStringCellValue();
			if ((insureStr != null) && (!(insureStr.equals("-1")))) {
				template.firstInsureCount = Integer.valueOf(insureStr.split(",")[0]).intValue();
				template.normalInsureCount = Integer.valueOf(insureStr.split(",")[1]).intValue();
			}
			int insureDropId = (int) row.getCell(pos++).getNumericCellValue();

			int[] dropArray = Calc.split(drops, ",");
			template.drops = new DropGroup[dropArray.length];
			for (int j = 0; j < dropArray.length; ++j) {
				template.drops[j] = ((DropService) Platform.getServiceManager().get(DropService.class))
						.getDropGroup(dropArray[j]);
			}
			if (firstDropId != -1) {
				template.firstDrop = ((DropService) Platform.getServiceManager().get(DropService.class))
						.getDropGroup(firstDropId);
			}
			if (insureDropId != -1) {
				template.insureDrop = ((DropService) Platform.getServiceManager().get(DropService.class))
						.getDropGroup(insureDropId);
			}
			this.recruitTemplates.put(Integer.valueOf(template.id), template);
		}
	}

	private void recruit(Player player, PbPacket.Packet packet) throws Exception {
		PbDown.RecruitRst.Builder builder = PbDown.RecruitRst.newBuilder();
		int type = 1;
		int num = 10;
		boolean isFree = true;
		try {
			PbUp.Recruit recruit = PbUp.Recruit.parseFrom(packet.getData());
			type = recruit.getRecruitType();
			num = recruit.getNum();
			isFree = recruit.getIsFree();
		} catch (InvalidProtocolBufferException e) {
			Platform.getLog().logError(e);
			builder.setResult(false);
			builder.setErrInfo("招募失败");
			player.send(1016, builder.build());
			return;
		}

		if ((type != 3) && (type != 2) && (type != 1)) {
			builder.setResult(false);
			builder.setErrInfo("招募类型错误");
			player.send(1016, builder.build());
			return;
		}
		if (((num != 1) && (num != 10)) || ((num == 10) && (type != 3))) {
			builder.setResult(false);
			builder.setErrInfo("招募次数不正确");
			player.send(1016, builder.build());
			return;
		}

		if ((isFree) && (((type == 1) || (num == 10) || (!(player.getRecruitRecord().isFree(type)))))) {
			builder.setResult(false);
			builder.setErrInfo("招募数据异常");
			player.send(1016, builder.build());
			return;
		}

		if (player.getBags().isFullBag(2)) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
			player.send(1016, builder.build());
			return;
		}

		RecruitTemplate template = getTemplate(type);
		Reward cost = null;
		if (num == 1) {
			if (!(isFree))
				cost = template.singleCost;
		} else {
			cost = template.tenCost;
		}
		if (cost != null) {
			if (cost.type == 0) {
				if (player.getBags().getItemCount(cost.template.id) >= cost.count) {
					builder.setResult(false);
					builder.setErrInfo("材料不足");
					player.send(1016, builder.build());
					return;
				}
			}
			if (cost.type == 3) {
				if ((isFree) || (player.getJewels() >= cost.count)) {
					builder.setResult(false);
					builder.setErrInfo("元宝不足");
					player.send(1016, builder.build());
					return;
				}
			}
			throw new Exception("recruit cost contains other type:" + cost.type);
		}
		List<String> boardCastNames = new ArrayList<String>();

		for (int i = 0; i < num; ++i) {
			DropGroup actualDrop;
			boolean isInsure = false;
			if ((type == 2) && (player.getRecruitRecord().isFirstRecruit(type))) {
				actualDrop = ((DropService) Platform.getServiceManager().get(DropService.class)).getDropGroup(10407);
			} else if ((player.getRecruitRecord().isJewelFirstRecruit(type)) && (!(isFree))) {
				actualDrop = template.firstDrop;
			} else if ((type == 1) && (player.getRecruitRecord().isFirstRecruit(type))) {
				actualDrop = template.firstDrop;
			} else if (template.insureDrop != null) {
				int insureCount = player.getRecruitRecord().getInfo(type).getInsureNum();
				if (player.getRecruitRecord().getInfo(type).insureCount + 1 == insureCount) {
					isInsure = true;
					actualDrop = template.insureDrop;
				} else {
					actualDrop = player.getRecruitRecord().getDrop(type);
				}
			} else {
				actualDrop = player.getRecruitRecord().getDrop(type);
			}
			List<Gain> list = actualDrop.genGains(player);
			for (Gain gain : list) {
				gain.gain(player, "recruit");
				builder.addHeros(((Warrior) gain.item).genWarrior());

				if (gain.item.getTemplate().quality > 5) {
					boardCastNames.add(gain.item.getTemplate().name);
				}
			}
			player.getRecruitRecord().execute(type, isInsure, isFree);
		}
		Platform.getEventManager().addEvent(new Event(2059, new Object[] { player }));

		String recruitType = "";
		String costType = "";
		if (type == 3) {
			Platform.getEventManager().addEvent(new Event(2027, new Object[] { player, Integer.valueOf(num) }));
			if (!(isFree)) {
				Platform.getEventManager().addEvent(new Event(2029, new Object[] { player, Integer.valueOf(num) }));
			}

			if (num > 1) {
				recruitType = "ten";
				costType = "gold";
			} else {
				recruitType = "best";
				costType = (isFree) ? "free" : "gold";
			}
		} else if (type == 1) {
			recruitType = "good";
			costType = "item";
		} else {
			recruitType = "better";
			costType = (isFree) ? "free" : "gold";
		}

		Platform.getLog().logRecruit(player, recruitType, costType);

		if ((num > 1) && (boardCastNames.size() > 0)) {
			StringBuilder sb = new StringBuilder();
			sb.append((String) boardCastNames.get(0));
			for (int i = 1; i < boardCastNames.size(); ++i) {
				sb.append(",").append((String) boardCastNames.get(i));
			}
			String msg = MessageFormat.format(
					"<p style=13>[{0}]</p><p style=17>在七星坛召唤了十次神将，召唤出了</p><p style=15>[{1}]</p><p style=17>，争霸天下指日可待！</p>",
					new Object[] { player.getName(), sb.toString() });
			Platform.getPlayerManager().boardCast(msg);
		}

		if (ActivityInfo.isOpenActivity(11, player)) {
			RecruitDropAI ai = (RecruitDropAI) ActivityInfo.getItem(player, 11);
			if (ai != null) {
				int extraDropType = 1;
				if (type == 3) {
					if (num > 1)
						extraDropType = 4;
					else
						extraDropType = 3;
				} else if (type == 1)
					extraDropType = 1;
				else {
					extraDropType = 2;
				}
				if (ai.isEffect(extraDropType)) {
					DropGroup drop = (DropGroup) DropService.dropGroups.get(Integer.valueOf(ai.getDrop(extraDropType)));
					if (drop != null) {
						List<Gain> gains = drop.genGains(player);
						for (Gain gain : gains) {
							gain.gain(player, "recruit");
							builder.addExtra(gain.genPbReward());
						}
					}
				}
			}
		}

		builder.setResult(true);
		builder.setRecruit(player.getRecruitRecord().getInfo(type).genRecruitInfo(player));
		player.send(1016, builder.build());
	}

	private void getRecruitInfo(Player player) {
		PbDown.RecruitInfoRst.Builder builder = PbDown.RecruitInfoRst.newBuilder();
		Collection<RecruitRecord.RecruitInfo> recruitInfos = player.getRecruitRecord().getRecruitInfos().values();
		for (RecruitRecord.RecruitInfo recruitInfo : recruitInfos) {
			builder.addRecruits(recruitInfo.genRecruitInfo(player));
		}
		builder.setResult(true);
		player.send(1022, builder.build());
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
