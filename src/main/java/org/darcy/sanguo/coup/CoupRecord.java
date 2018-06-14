package org.darcy.sanguo.coup;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.hero.LockSkill;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.CoupService;

public class CoupRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 8192930237603959612L;
	private final int version = 1;

	private LockSkill[] skills = new LockSkill[4];

	private int[] levels = { 1 };

	private int[] index = { 1, 2, 3, 4 };

	public void update(Player player, int id) {
		this.levels[(id - 1)] += 1;
		refreshSkills(player);
	}

	public void init(Player player) {
		refreshSkills(player);
	}

	public void order(List<Integer> orders) {
		for (int i = 0; i < orders.size(); ++i)
			this.index[i] = ((Integer) orders.get(i)).intValue();
	}

	public void refreshSkills(Player player) {
		for (int i = 0; i < this.skills.length; ++i) {
			int coupId = this.index[i];
			Coup coup = (Coup) CoupService.coups.get(Integer.valueOf(coupId));
			if (this.levels[(coupId - 1)] > 0)
				this.skills[i] = new LockSkill(coup.getSkill(this.levels[(coupId - 1)]).getId(), coup.unLockLevel, true,
						coup);
			else {
				this.skills[i] = new LockSkill(coup.getSkill(this.levels[(coupId - 1)]).getId(), coup.unLockLevel,
						false, coup);
			}
		}

		player.getWarriors().getMainWarrior().setManualSkills(this.skills);
	}

	public int getSkillId(int id) {
		if (this.levels[(id - 1)] == 0) {
			return ((Coup) CoupService.coups.get(Integer.valueOf(id))).skills[0];
		}
		return ((Coup) CoupService.coups.get(Integer.valueOf(id))).skills[(this.levels[(id - 1)] - 1)];
	}

	public int[] getCoups() {
		return this.levels;
	}

	public void setCoups(int[] coups) {
		this.levels = coups;
	}

	public int getBlobId() {
		return 15;
	}

	public int[] getLevels() {
		return this.levels;
	}

	public boolean canOptCoup(int id, Player player) {
		int level = this.levels[(id - 1)];
		if (level == 0) {
			if (player.getLevel() < ((Coup) CoupService.coups.get(Integer.valueOf(id))).unLockLevel)
				return false;
			return true;
		}

		CoupLevelUp up = (CoupLevelUp) CoupService.levelUps.get(Integer.valueOf(level));
		if ((up == null) || (player.getLevel() < up.minPlayerLevel))
			return false;
		String check = null;
		for (Reward reward : up.needs) {
			check = reward.check(player);
			if (check != null) {
				break;
			}
		}

		return (check != null);
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.skills = new LockSkill[4];
			this.levels = new int[] { 1 };
			this.index = new int[] { 1, 2, 3, 4 };

			int size = in.readInt();
			for (int i = 0; i < this.levels.length; ++i) {
				this.levels[i] = in.readInt();
			}
			size = in.readInt();
			for (int i = 0; i < this.index.length; ++i)
				this.index[i] = in.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(1);
		out.writeInt(this.levels.length);
		for (int i : this.levels) {
			out.writeInt(i);
		}
		out.writeInt(this.index.length);
		for (int i : this.index)
			out.writeInt(i);
	}

	public LockSkill[] getSkills() {
		return this.skills;
	}

	public void setSkills(LockSkill[] skills) {
		this.skills = skills;
	}
}
