package org.darcy.sanguo.task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.TaskService;
import org.darcy.sanguo.task.finishcondition.FinishCondition;

import sango.packet.PbCommons;

public class Task {
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_DAY = 2;
	public static final int FINISH_TYPE_ONLY = 1;
	public static final int FINISH_TYPE_ACCUMULATE = 2;
	private TaskTemplate template;

	public Task(TaskTemplate template) {
		this.template = template;
	}

	public int getId() {
		return this.template.id;
	}

	public List<Reward> getRewards() {
		return this.template.getRewards();
	}

	public void reward(Player player) {
		for (Reward reward : this.template.getRewards())
			reward.add(player, "taskreward");
	}

	public void processEvent(Player player, Event event) {
		if (this.template.condition == null) {
			return;
		}
		if (this.template.events.contains(Integer.valueOf(event.type)))
			this.template.condition.processEvent(player, event, this);
	}

	public boolean isFinish(Player player) {
		if (this.template.condition == null) {
			return true;
		}
		return this.template.condition.isFinish(player, this);
	}

	public int[] getProcess(Player player) {
		if (this.template.condition == null) {
			return new int[] { 1, 1 };
		}
		return this.template.condition.getProcess(player, this);
	}

	public void initVar(Player player) {
		if (this.template.condition == null) {
			return;
		}
		this.template.condition.initVar(player, this);
	}

	public FinishCondition getCondition() {
		return this.template.condition;
	}

	public TaskTemplate getTemplate() {
		return this.template;
	}

	public void setTemplate(TaskTemplate template) {
		this.template = template;
	}

	public static Task readObject(ObjectInputStream in) {
		try {
			int id = in.readInt();
			TaskTemplate tt = (TaskTemplate) TaskService.taskTemplates.get(Integer.valueOf(id));
			if (tt == null)
				return null;
			Task task = new Task(tt);
			return task;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.template.id);
	}

	public PbCommons.Task genTask(Player player, boolean isFinish) {
		PbCommons.Task.Builder builder = PbCommons.Task.newBuilder();
		builder.setId(getId());
		builder.setType(this.template.type);
		builder.setName(this.template.name);
		builder.setContent(this.template.content);
		for (Reward reward : this.template.getRewards()) {
			builder.addRewards(reward.genPbReward());
		}
		builder.setIsFinish(isFinish);
		builder.setJump(this.template.guideType);
		int[] process = getProcess(player);
		builder.setCurProcess(process[0]);
		builder.setTotalProcess(process[1]);
		return builder.build();
	}
}
