package org.darcy.sanguo.task.finishcondition;

import java.util.List;

import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.task.Task;

public class WarriorAdvanceFC extends AbstractFCondition {
	int gole;

	public WarriorAdvanceFC(int paramCount) {
		super(paramCount);
	}

	public FinishCondition copy() {
		WarriorAdvanceFC fc = new WarriorAdvanceFC(this.paramCount);
		super.copy(fc);
		return fc;
	}

	public void registerEvent() {
		this.events = new int[] { 2022 };
	}

	public void initParams(String[] params) {
		this.gole = Integer.valueOf(params[0]).intValue();
	}

	public boolean isFinish(Player player, Task task) {
		List<BagGrid> list = player.getBags().getBag(2).getGrids();
		for (BagGrid grid : list) {
			if ((grid != null) && (grid.getItem() != null)) {
				Warrior w = (Warrior) grid.getItem();
				if ((!(w.isMainWarrior())) && (w.getAdvanceLevel() >= this.gole)) {
					return true;
				}
			}
		}
		return false;
	}
}
