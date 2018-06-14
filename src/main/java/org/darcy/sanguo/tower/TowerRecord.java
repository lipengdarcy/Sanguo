package org.darcy.sanguo.tower;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.TowerService;

public class TowerRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 497092236162092638L;
	private static final int version = 2;
	private int level = 1;
	private int maxLevel;
	private boolean pass;
	private int challengeTimes;
	public static final int MAX_CHALLENGE_TIMES = 2;
	private int resetTimes;
	public static final int MAX_FREE_RESET_TIMES = 2;
	private long multiChallengeTime;
	public static final int MULTI_CHALLENGE_CD = 30;
	public Stage tmpStage;
	private Attributes titleAttribute = new Attributes();

	public int getTitleId() {
		TowerStageTemplate tt = (TowerStageTemplate) TowerService.towerTemplates.get(Integer.valueOf(this.maxLevel));
		if (tt != null) {
			return tt.titleId;
		}

		return 0;
	}

	public void addAttris(List<Attri> attris) {
		if (attris == null)
			return;
		for (Attri a : attris)
			this.titleAttribute.addAttri(a);
	}

	public void endMultiChallenge(Player player) {
		int startLevel = this.level;
		this.level = getCurrentLevel();
		int endLevel = this.level;
		if (this.pass) {
			++endLevel;
		}
		List ls = new ArrayList();
		for (int i = startLevel; (i < endLevel) && (i <= this.maxLevel); ++i) {
			TowerStageTemplate tt = (TowerStageTemplate) TowerService.towerTemplates.get(Integer.valueOf(i));
			if (tt != null) {
				ls.add(new Reward(2, tt.money, null));
				ls.add(new Reward(8, tt.warriorSpirit, null));
				if (tt.rewards != null) {
					ls.addAll(tt.rewards);
				}
			}
		}
		ls = Reward.mergeReward(ls);
		if (ls.size() > 0) {
			Date sendTime = new Date(this.multiChallengeTime + (endLevel - startLevel) * 30 * 1000);
			String startStageName = TowerService.geneProgress(
					((TowerStageTemplate) TowerService.towerTemplates.get(Integer.valueOf(startLevel))).id);
			String endStageName = TowerService.geneProgress(
					((TowerStageTemplate) TowerService.towerTemplates.get(Integer.valueOf(endLevel - 1))).id);
			MailService.sendSystemMail(7, player.getId(), "过关斩将扫荡奖励", MessageFormat.format(
					"<p style=21>恭喜主公，您在过关斩将中对</p><p style=20>【{0}】</p><p style=21>至</p><p style=20>【{1}】</p><p style=21>进行了扫荡，获得奖励如下：</p>",
					new Object[] { startStageName, endStageName }), sendTime, ls);
		}
		this.multiChallengeTime = 0L;
		Platform.getLog().logTower(player, "towermultichallenge");
	}

	public Attributes getTitleAttribute() {
		return this.titleAttribute;
	}

	public void setTitleAttribute(Attributes titleAttribute) {
		this.titleAttribute = titleAttribute;
	}

	public int getLeftMultiChlngSeconds(Player player) {
		if (this.multiChallengeTime == 0L) {
			return 0;
		}

		int total = (this.maxLevel + 1 - this.level) * 30;
		int last = (int) ((System.currentTimeMillis() - this.multiChallengeTime) / 1000L);
		int rst = total - last;
		if (rst <= 0) {
			endMultiChallenge(player);
			return 0;
		}

		return rst;
	}

	public void checkMultiChallenge(Player player) {
		getLeftMultiChlngSeconds(player);
	}

	public int getLeftChallengeTimes() {
		int rst = 2 - this.challengeTimes;
		if (rst < 0) {
			return 0;
		}
		return rst;
	}

	public int getLeftFreeResetTimes() {
		int rst = 2 - this.resetTimes;
		if (rst < 0) {
			return 0;
		}
		return rst;
	}

	public int getCurrentLevel() {
		int MAX_LEVEL = 0;
		int rst = 0;
		for (TowerStageTemplate tst : TowerService.towerTemplates.values()) {
			if (tst.id > MAX_LEVEL) {
				MAX_LEVEL = tst.id;
			}
		}

		if (this.multiChallengeTime == 0L) {
			if (this.level < 1) {
				Platform.getLog().logError("wrong level : " + this.level);
				this.level = 1;
			}
			rst = this.level;
		} else {
			if (this.multiChallengeTime > System.currentTimeMillis()) {
				this.multiChallengeTime = System.currentTimeMillis();
			}
			int add = (int) ((System.currentTimeMillis() - this.multiChallengeTime) / 1000L / 30L);
			if (add + this.level <= this.maxLevel)
				rst = add + this.level;
			else {
				rst = this.maxLevel + 1;
			}
			setPass(false);
		}

		if (rst > MAX_LEVEL) {
			setPass(true);
			return MAX_LEVEL;
		}

		return rst;
	}

	public int getLevel() {
		return this.level;
	}

	public int getMaxLevel() {
		return this.maxLevel;
	}

	public boolean isPass() {
		return this.pass;
	}

	public int getChallengeTimes() {
		return this.challengeTimes;
	}

	public int getResetTimes() {
		return this.resetTimes;
	}

	public long getMultiChallengeTime() {
		return this.multiChallengeTime;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMaxLevel(int maxLevel) {
		if (maxLevel > this.maxLevel)
			this.maxLevel = maxLevel;
	}

	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public void setChallengeTimes(int challengeTimes) {
		this.challengeTimes = challengeTimes;
	}

	public void setResetTimes(int resetTimes) {
		this.resetTimes = resetTimes;
	}

	public void setMultiChallengeTime(long multiChallengeTime) {
		this.multiChallengeTime = multiChallengeTime;
	}

	public void refresh() {
		this.resetTimes = 0;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int v = in.readInt();
			this.challengeTimes = in.readInt();
			this.level = in.readInt();
			this.maxLevel = in.readInt();
			this.multiChallengeTime = in.readLong();
			this.pass = in.readBoolean();
			this.resetTimes = in.readInt();
			this.titleAttribute = new Attributes();
			if (v > 1)
				this.titleAttribute = Attributes.readObject(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(2);
		out.writeInt(this.challengeTimes);
		out.writeInt(this.level);
		out.writeInt(this.maxLevel);
		out.writeLong(this.multiChallengeTime);
		out.writeBoolean(this.pass);
		out.writeInt(this.resetTimes);

		this.titleAttribute.writeObject(out);
	}

	public int getBlobId() {
		return 9;
	}
}
