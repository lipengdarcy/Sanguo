package org.darcy.sanguo.mail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.darcy.sanguo.drop.Reward;

import sango.packet.PbCommons;

public class Mail {
	public static final int TYPE_SYSTEM_MSG = 1;
	public static final int TYPE_SYSTEM_COMP = 2;
	public static final int TYPE_SYSTEM_WEAL = 3;
	public static final int TYPE_ARENA_REWARD = 4;
	public static final int TYPE_WORLDCOM_REWARD = 5;
	public static final int TYPE_BOSS_REWARD = 6;
	public static final int TYPE_MAP_REWARD = 7;
	public static final int TYPE_DIVINE_REWARD = 8;
	public static final int TYPE_ARENA = 9;
	public static final int TYPE_WORLDCOMP = 10;
	public static final int TYPE_LOOT = 11;
	public static final int TYPE_MONTHCARD_REWARD = 12;
	public static final int TYPE_MONTHCARD = 13;
	public static final int TYPE_PLAYER = 14;
	public static final int TYPE_LEAGUE_BOSS = 15;
	public static final int TYPE_QUARTERCARD_REWARD = 16;
	public static final int TYPE_QUARTERCARD = 17;
	public static final int TYPE_7RANK_REWARD = 18;
	public static final int TYPE_LEAGUE_BOSSTIMEREWARD = 19;
	public static final int TYPE_LEAGUE_BOSSWEEKREWARD = 20;
	public static final int TYPE_LC_DAYREWARD = 24;
	public static final int TYPE_LC_WEEKREWARD = 25;
	int id;
	public int type;
	int sourceId;
	String sourceName;
	int targetId;
	String content;
	String title;
	Date sendTime;
	boolean read;
	Attachment attachment;

	public Mail() {
	}

	public Mail(int type, int sourceId, String sourceName, int targetId, String title, String content, Date sendTime,
			List<Reward> attachment) {
		this.type = type;
		this.sourceId = sourceId;
		this.sourceName = sourceName;
		this.targetId = targetId;
		this.content = content;
		this.title = title;
		this.read = false;
		if (sendTime == null)
			this.sendTime = new Date();
		else {
			this.sendTime = sendTime;
		}
		if ((attachment != null) && (attachment.size() > 0)) {
			this.attachment = new Attachment();
			this.attachment.setRewards(attachment);
		}
	}

	public PbCommons.Mail.Builder genPb() {
		PbCommons.Mail.Builder m = PbCommons.Mail.newBuilder();
		m.setContent(this.content).setSourceId(this.sourceId).setSourceName(this.sourceName).setTime(getDate())
				.setTitle(this.title).setId(this.id).setType(this.type);

		if (this.attachment != null) {
			for (Reward r : this.attachment.getRewards()) {
				m.addRewards(r.genPbReward());
			}
		}

		return m;
	}

	public String getDate() {
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		then.setTime(this.sendTime);
		int diff = now.get(6) - then.get(6);
		if (diff == 0)
			return "今天";
		if (diff == 1)
			return "昨天";
		if (diff == 2) {
			return "前天";
		}
		if (diff < 0) {
			diff += 365;
		}
		return diff + "天前";
	}

	public String getTitle() {
		return this.title;
	}

	public Date getSendTime() {
		return this.sendTime;
	}

	public boolean isRead() {
		return this.read;
	}

	public boolean getRead() {
		return this.read;
	}

	public Attachment getAttachment() {
		return this.attachment;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
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

	public int getSourceId() {
		return this.sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public int getTargetId() {
		return this.targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
