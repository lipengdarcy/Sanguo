package org.darcy.sanguo.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.task.Task;
import org.darcy.sanguo.task.TaskRecord;
import org.darcy.sanguo.task.TaskTemplate;
import org.darcy.sanguo.task.finishcondition.FinishCondition;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class TaskService implements Service, EventHandler, PacketHandler {
	public static Map<Integer, FinishCondition> conditions = new HashMap();
	public static Map<Integer, TaskTemplate> taskTemplates = new HashMap();
	public int[] events;

	public void startup() throws Exception {
		loadTask();
		Platform.getPacketHanderManager().registerHandler(this);
		Platform.getEventManager().registerListener(this);
	}

	public int[] getCodes() {
		return new int[] { 1163, 1165 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		if (!(FunctionService.isOpenFunction(player.getLevel(), 6))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1163:
			taskInfo(player);
			break;
		case 1165:
			taskReward(player, packet);
		case 1164:
		}
	}

	private void loadTask() {
		Set<Integer> evs = new HashSet();

		int pos = 0;
		List<Row> list1 = ExcelUtils.getRowList("task.xls", 2, 1);
		for (Row row : list1) {

			pos = 0;
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			String clazzName = row.getCell(pos++).getStringCellValue();
			int count = (int) row.getCell(pos++).getNumericCellValue();
			try {
				Class effect = Class.forName("org.darcy.sanguo.task.finishcondition." + clazzName);
				Constructor constructor = effect.getConstructor(new Class[] { Integer.TYPE });
				Object obj = constructor.newInstance(new Object[] { Integer.valueOf(count) });
				conditions.put(Integer.valueOf(id), (FinishCondition) obj);
				((FinishCondition) obj).registerEvent();
				int[] ev = ((FinishCondition) obj).getRegisterEvent();
				for (int e : ev)
					evs.add(Integer.valueOf(e));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.events = new int[evs.size()];
		int index = 0;
		for (Iterator it = evs.iterator(); it.hasNext();) {
			int i = ((Integer) it.next()).intValue();
			this.events[index] = i;
			++index;
		}

		List<Row> list2 = ExcelUtils.getRowList("task.xls", 2);
		for (Row row : list2) {
			String[] rewardArray;
			List list;
			pos = 0;
			int id = (int) row.getCell(pos++).getNumericCellValue();
			int type = (int) row.getCell(pos++).getNumericCellValue();
			String name = row.getCell(pos++).getStringCellValue();
			String object = row.getCell(pos++).getStringCellValue();
			int minLevel = (int) row.getCell(pos++).getNumericCellValue();
			int maxLevel = (int) row.getCell(pos++).getNumericCellValue();
			int acceptActivityId = (int) row.getCell(pos++).getNumericCellValue();
			int preId = (int) row.getCell(pos++).getNumericCellValue();
			String nextIds = row.getCell(pos++).getStringCellValue();
			String rewards = row.getCell(pos++).getStringCellValue();
			int rewardActivityId = (int) row.getCell(pos++).getNumericCellValue();
			String activityRewards = row.getCell(pos++).getStringCellValue();
			int guideType = (int) row.getCell(pos++).getNumericCellValue();
			int conditionType = (int) row.getCell(pos++).getNumericCellValue();
			FinishCondition condition = null;
			if (conditionType != -1) {
				condition = ((FinishCondition) conditions.get(Integer.valueOf(conditionType))).copy();
				int count = condition.getParamCount();
				String[] params = new String[count];
				for (int j = 0; j < count; ++j) {
					row.getCell(pos).setCellType(1);
					params[j] = row.getCell(pos++).getStringCellValue();
				}
				condition.initParams(params);
				condition.registerEvent();
			}

			TaskTemplate template = new TaskTemplate();
			template.id = id;
			template.name = name;
			template.type = type;
			template.content = object;
			template.minLevel = minLevel;
			template.maxLevel = maxLevel;
			template.acceptActivityId = acceptActivityId;
			template.preId = preId;
			if (nextIds.equals("-1"))
				template.nextIds = new int[0];
			else {
				template.nextIds = Calc.split(nextIds, ",");
			}
			if (!(rewards.equals("-1"))) {
				rewardArray = rewards.split(",");
				list = new ArrayList();
				for (String str : rewardArray) {
					list.add(new Reward(str));
					template.setRewards(list);
				}
			}
			template.rewardActivityId = rewardActivityId;
			if (!(activityRewards.equals("-1"))) {
				rewardArray = activityRewards.split(",");
				list = new ArrayList();
				for (String str : rewardArray) {
					list.add(new Reward(str));
					template.setActivityRewards(list);
				}
			}
			template.guideType = guideType;
			template.condition = condition;
			template.init();
			taskTemplates.put(Integer.valueOf(id), template);
		}
	}

	private void taskInfo(Player player) {
		Task task;
		PbDown.TaskInfoRst.Builder builder = PbDown.TaskInfoRst.newBuilder().setResult(true);
		TaskRecord tr = player.getTaskRecord();
		for (Iterator localIterator = tr.getFinishedTasks().values().iterator(); localIterator.hasNext();) {
			task = (Task) localIterator.next();
			builder.addTasks(task.genTask(player, true));
		}
		for (Iterator localIterator = tr.getTasks().values().iterator(); localIterator.hasNext();) {
			task = (Task) localIterator.next();
			builder.addTasks(task.genTask(player, false));
		}
		player.send(1164, builder.build());
	}

	private void taskReward(Player player, PbPacket.Packet packet) {
		PbDown.TaskRewardRst.Builder builder = PbDown.TaskRewardRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.TaskReward reward = PbUp.TaskReward.parseFrom(packet.getData());
			id = reward.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1166, builder.build());
			return;
		}
		TaskRecord tr = player.getTaskRecord();
		Task task = tr.getFinishedTask(id);
		if (task == null) {
			builder.setResult(false);
			builder.setErrInfo("尚未完成该任务");
		} else if (player.getBags().getFullBag() != -1) {
			builder.setResult(false);
			builder.setErrInfo("背包已满，请先清理背包");
		} else {
			tr.finishTask(player, id);
			for (Reward reward : task.getRewards()) {
				builder.addRewards(reward.genPbReward());
			}
			Platform.getEventManager().addEvent(new Event(2055, new Object[] { player }));
		}
		player.send(1166, builder.build());
	}

	public static List<TaskTemplate> getTasksByType(int type) {
		List list = new ArrayList();
		for (TaskTemplate tt : taskTemplates.values()) {
			if (tt.type == type) {
				list.add(tt);
			}
		}
		return list;
	}

	public static boolean isAssignTypeTask(int id, int type) {
		TaskTemplate tt = (TaskTemplate) taskTemplates.get(Integer.valueOf(id));

		return ((tt == null) || (tt.type != type));
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	public int[] getEventCodes() {
		return this.events;
	}

	public void handleEvent(Event event) {
		processTask(event);
	}

	private void processTask(Event event) {
		Player player = (Player) event.params[0];
		player.getTaskRecord().processEvent(player, event);
	}
}
