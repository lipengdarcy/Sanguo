package org.darcy.sanguo.combat.skill;

import java.util.ArrayList;
import java.util.List;

public class Skill {
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_ANGRY = 1;
	public static final int TYPE_MANUAL = 2;
	public static final int ANGRY_SKILL_TRIGGER_VALUE = 4;
	public static final int ANGRY_SKILL_ADD_VALUE = 2;
	public static final int MANUAL_SKILL_ADD_VALUE = 1;
	private int id;
	private int type;
	private int actionGroupId;
	private int iconId;
	private String name;
	private String description;
	private List<Behavior> behaviors = new ArrayList();
	private boolean followAtk;
	private boolean beatBack;
	private String unBeatAction;
	private int killPointCost;

	public Skill(int type, int id, String name) {
		this.type = type;
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getIconId() {
		return this.iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Behavior> getBehaviors() {
		return this.behaviors;
	}

	public boolean isConditionType(ConditionType ct) {
		if (ct == null)
			return true;
		if (ct == ConditionType.ALL)
			return true;
		if (ct == ConditionType.ANGRY) {
			if (this.type != 1)
				return false;
			return true;
		}
		if (ct == ConditionType.NORMAL) {
			if (this.type != 0)
				return false;
			return true;
		}

		return ((ct != ConditionType.NORMAL_ANGRY) || ((this.type != 1) && (this.type != 0)));
	}

	public void setBehaviors(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}

	public boolean isFollowAtk() {
		return this.followAtk;
	}

	public void setFollowAtk(boolean followAtk) {
		this.followAtk = followAtk;
	}

	public int getKillPointCost() {
		return this.killPointCost;
	}

	public void setKillPointCost(int killPointCost) {
		this.killPointCost = killPointCost;
	}

	public int getActionGroupId() {
		return this.actionGroupId;
	}

	public void setActionGroupId(int actionGroupId) {
		this.actionGroupId = actionGroupId;
	}

	public boolean isBeatBack() {
		return this.beatBack;
	}

	public void setBeatBack(boolean beatBack) {
		this.beatBack = beatBack;
	}

	public String getUnBeatAction() {
		return this.unBeatAction;
	}

	public void setUnBeatActionID(String unBeatAction) {
		this.unBeatAction = unBeatAction;
	}

	public static enum ConditionType {
		ALL, NORMAL, ANGRY, NORMAL_ANGRY;

		public static ConditionType valueOf(int value) {
			switch (value) {
			case 0:
				return ALL;
			case 1:
				return NORMAL;
			case 2:
				return ANGRY;
			case 3:
				return NORMAL_ANGRY;
			}
			return ALL;
		}
	}
}
