package org.darcy.sanguo.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.darcy.sanguo.service.MapService;

public class ClearStage {
	public static final int CHANNEL_SIZE = 3;
	public int id;
	public int chanllengeTimes;
	public boolean[] finishRecords = new boolean[3];
	int resetTimes;

	public int getLeftChanllengeTimes() {
		return (getTemplate().maxChanllengeTimes - this.chanllengeTimes);
	}

	public void setResetTimes(int resetTimes) {
		this.resetTimes = resetTimes;
	}

	public int getResetTimes() {
		return this.resetTimes;
	}

	public StageTemplate getTemplate() {
		return ((StageTemplate) MapService.stageTemplates.get(Integer.valueOf(this.id)));
	}

	public void finish(int index) {
		this.finishRecords[index] = true;
	}

	public boolean isFinished(int index) {
		return this.finishRecords[index];
	}

	public boolean isFinished() {
		for (boolean b : this.finishRecords) {
			if (b) {
				return true;
			}
		}
		return false;
	}

	public int getStars() {
		int rst = 0;
		for (boolean b : this.finishRecords) {
			if (b) {
				++rst;
			}
		}
		return rst;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.id);
		out.writeInt(this.chanllengeTimes);
		for (boolean b : this.finishRecords) {
			out.writeBoolean(b);
		}
		out.writeInt(this.resetTimes);
	}

	public static ClearStage readObject(ObjectInputStream in, int version) throws IOException {
		ClearStage cs = new ClearStage();
		cs.id = in.readInt();
		cs.chanllengeTimes = in.readInt();
		for (int i = 0; i < 3; ++i) {
			cs.finishRecords[i] = in.readBoolean();
		}
		if (version > 1) {
			cs.resetTimes = in.readInt();
		}
		return cs;
	}

	public int getChanllengeTimes() {
		return this.chanllengeTimes;
	}

	public void setChanllengeTimes(int chanllengeTimes) {
		this.chanllengeTimes = chanllengeTimes;
	}
}
