package org.darcy.sanguo.task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityInfo;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.TaskService;

public class TaskRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = -7021406727584990448L;
	private final int version = 1;

	private ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap();
	private Map<Integer, Task> finishedTasks = new HashMap();
	private List<Integer> pastTasks = new ArrayList();
	private Map<Integer, TaskVarStore> varStores = new HashMap();

	public void processEvent(Player player, Event event) {
		for (Task task : this.tasks.values())
			task.processEvent(player, event);
	}

	public void init(Player player) {
		update(player);
	}

	public void update(Player player) {
		Iterator itx = this.tasks.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			Task task = (Task) this.tasks.get(Integer.valueOf(id));
			if (task.isFinish(player)) {
				itx.remove();
				this.finishedTasks.put(Integer.valueOf(id), task);
				Platform.getLog().logTask(player, task, "taskfinish");

				Platform.getEventManager().addEvent(new Event(2046, new Object[] { player }));
			}
		}
	}

	public void update(Player player, int id) {
		Task task = (Task) this.tasks.get(Integer.valueOf(id));
		if ((task != null) && (task.isFinish(player))) {
			this.tasks.remove(Integer.valueOf(id));
			this.finishedTasks.put(Integer.valueOf(id), task);
			Platform.getLog().logTask(player, task, "taskfinish");

			Platform.getEventManager().addEvent(new Event(2046, new Object[] { player }));
		}
	}

	public void refreshNewTask(Player player) {
		for (TaskTemplate tt : TaskService.taskTemplates.values())
			acceptTask(player, tt);
	}

	public void refreshDayTask(Player player) {
		int id;
		Task task;
		Iterator itx = this.tasks.keySet().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			task = (Task) this.tasks.get(Integer.valueOf(id));
			if (task.getTemplate().type == 2) {
				itx.remove();
				task.initVar(player);
			} else {
				if ((task.getTemplate().acceptActivityId == -1)
						|| (ActivityInfo.isOpenActivity(task.getTemplate().acceptActivityId, player)))
					continue;
				itx.remove();
				task.initVar(player);
			}
		}

		itx = this.finishedTasks.keySet().iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			task = (Task) this.finishedTasks.get(Integer.valueOf(id));
			if (task.getTemplate().type == 2) {
				itx.remove();
				task.initVar(player);
			} else {
				if ((task.getTemplate().acceptActivityId == -1)
						|| (ActivityInfo.isOpenActivity(task.getTemplate().acceptActivityId, player)))
					continue;
				itx.remove();
				task.initVar(player);
			}
		}

		itx = this.pastTasks.iterator();
		while (itx.hasNext()) {
			id = ((Integer) itx.next()).intValue();
			if (TaskService.isAssignTypeTask(id, 2)) {
				itx.remove();
			} else {
				TaskTemplate tt = (TaskTemplate) TaskService.taskTemplates.get(Integer.valueOf(id));
				if ((tt.acceptActivityId == -1) || (ActivityInfo.isOpenActivity(tt.acceptActivityId, player)))
					continue;
				itx.remove();
			}

		}

		refreshNewTask(player);
	}

	public List<Task> finishTask(Player player, int id) {
		Task task = (Task) this.finishedTasks.get(Integer.valueOf(id));
		if (task != null) {
			this.finishedTasks.remove(Integer.valueOf(id));
			this.pastTasks.add(Integer.valueOf(id));
			task.initVar(player);
			task.reward(player);
			if (task.getTemplate().nextIds.length > 0) {
				int[] arrayOfInt;
				List list = new ArrayList();
				int j = (arrayOfInt = task.getTemplate().nextIds).length;
				int i = 0;
				while (true) {
					Integer tmpId = Integer.valueOf(arrayOfInt[i]);
					Task tmpTask = acceptTask(player, tmpId.intValue());
					if (tmpTask != null)
						list.add(tmpTask);
					++i;
					if (i >= j) {
						return list;
					}
				}
			}
		}
		return null;
	}

	public Task acceptTask(Player player, int id) {
		TaskTemplate template = (TaskTemplate) TaskService.taskTemplates.get(Integer.valueOf(id));
		if (template != null) {
			return acceptTask(player, template);
		}
		return null;
	}

	public Task acceptTask(Player player, TaskTemplate tt) {
		if (canAcceptTask(player, tt)) {
			Task task = new Task(tt);
			this.tasks.put(Integer.valueOf(task.getId()), task);
			Platform.getLog().logTask(player, task, "taskaccept");
			update(player, task.getId());
			return task;
		}
		return null;
	}

	public boolean canAcceptTask(Player player, TaskTemplate tt) {
		if (isPastTask(tt.id)) {
			return false;
		}
		if (getFinishedTask(tt.id) != null) {
			return false;
		}
		if (getRunningTask(tt.id) != null) {
			return false;
		}
		if ((tt.preId != -1) && (!(isPastTask(tt.preId)))) {
			return false;
		}
		if ((player.getLevel() < tt.minLevel) || (player.getLevel() > tt.maxLevel)) {
			return false;
		}
		if ((tt.acceptActivityId != -1) && (!(ActivityInfo.isOpenActivity(tt.acceptActivityId, player)))) {
			return false;
		}
		return tt.condition.canAccept();
	}

	public Task getFinishedTask(int id) {
		return ((Task) this.finishedTasks.get(Integer.valueOf(id)));
	}

	public Task getRunningTask(int id) {
		return ((Task) this.tasks.get(Integer.valueOf(id)));
	}

	public boolean isPastTask(int id) {
		return this.pastTasks.contains(Integer.valueOf(id));
	}

	public void setVar(Task task, int index, int value) {
		TaskVarStore store = (TaskVarStore) this.varStores.get(Integer.valueOf(task.getId()));
		if (store != null) {
			store.vars[index] = value;
		} else {
			store = new TaskVarStore();
			store.id = task.getId();
			store.vars = new int[task.getCondition().getParamCount()];
			store.vars[index] = value;
			this.varStores.put(Integer.valueOf(store.id), store);
		}
	}

	public int getVar(Task task, int index) {
		TaskVarStore tstore = (TaskVarStore) this.varStores.get(Integer.valueOf(task.getId()));
		if (tstore != null) {
			return tstore.vars[index];
		}
		return 0;
	}

	public Map<Integer, Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(ConcurrentHashMap<Integer, Task> tasks) {
		this.tasks = tasks;
	}

	public Map<Integer, Task> getFinishedTasks() {
		return this.finishedTasks;
	}

	public void setFinishedTasks(Map<Integer, Task> finishedTasks) {
		this.finishedTasks = finishedTasks;
	}

	public int getBlobId() {
		return 18;
	}

	private void readObject(ObjectInputStream in) {
		try {
			Task task;
			int v = in.readInt();
			int size = in.readInt();
			this.tasks = new ConcurrentHashMap();
			for (int i = 0; i < size; ++i) {
				task = Task.readObject(in);
				if (task != null) {
					this.tasks.put(Integer.valueOf(task.getId()), task);
				}
			}
			size = in.readInt();
			this.finishedTasks = new HashMap();
			int i;
			for (i = 0; i < size; ++i) {
				task = Task.readObject(in);
				this.finishedTasks.put(Integer.valueOf(task.getId()), task);
			}
			size = in.readInt();
			this.pastTasks = new ArrayList();
			for (i = 0; i < size; ++i) {
				this.pastTasks.add(Integer.valueOf(in.readInt()));
			}
			size = in.readInt();
			this.varStores = new HashMap();
			for (i = 0; i < size; ++i) {
				TaskVarStore store = TaskVarStore.readObject(in);
				this.varStores.put(Integer.valueOf(store.id), store);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(1);
		out.writeInt(this.tasks.size());
		for (Task task : this.tasks.values()) {
			if (task != null) {
				task.writeObject(out);
			}
		}
		List<Task> finishs = new ArrayList(this.finishedTasks.values());
		out.writeInt(finishs.size());
		for (Task task : finishs) {
			task.writeObject(out);
		}
		out.writeInt(this.pastTasks.size());
		for (Integer i : this.pastTasks) {
			out.writeInt(i.intValue());
		}
		out.writeInt(this.varStores.size());
		for (TaskVarStore store : this.varStores.values())
			store.writeObject(out);
	}
}
