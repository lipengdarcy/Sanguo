package org.darcy.sanguo.task.finishcondition;

import java.util.List;

import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;

public class EquipGetAndIntensifyFC extends AbstractFCondition {
	int num;
	int level;

	public EquipGetAndIntensifyFC(int paramCount) {
		super(paramCount);
	}

	public FinishCondition copy() {
		EquipGetAndIntensifyFC fc = new EquipGetAndIntensifyFC(this.paramCount);
		super.copy(fc);
		return fc;
	}

	public void registerEvent() {
		this.events = new int[] { 2024 };
	}

	public void initParams(String[] params) {
		this.num = Integer.valueOf(params[0]).intValue();
		this.level = Integer.valueOf(params[1]).intValue();
	}

	public boolean isFinish(Player player, Task task) {
		List<BagGrid> list = player.getBags().getBag(4).getGrids();
		if (list.size() < this.num) {
			return false;
		}
		int count = 0;
		for (BagGrid grid : list) {
			if (grid.getItem().getLevel() >= this.level) {
				++count;
				if (count >= this.num) {
					return true;
				}
			}
		}
		return false;
	}

	public int[] getProcess(Player player, Task task) {
		int[] process = new int[2];
		process[1] = this.num;
		int count = getCount(player);
		process[0] = Math.min(this.num, count);
		return process;
	}

	private int getCount(Player player) {
		List<BagGrid> list = player.getBags().getBag(4).getGrids();
		int count = 0;
		for (BagGrid grid : list) {
			if (grid.getItem().getLevel() >= this.level) {
				++count;
				if (count >= this.num) {
					return count;
				}
			}
		}
		return count;
	}
}
