package org.darcy.sanguo.arena;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

/**
 * 武将挑战
 */
public class BeChallengedInfo {
	public int id;
	public long time;
	public boolean isWin;
	public String name = "null";

	public BeChallengedInfo() {
	}

	public BeChallengedInfo(int id, boolean isWin, long time, String name) {
		this.id = id;
		this.isWin = isWin;
		this.time = time;
		if (name != null)
			this.name = name;
	}

	public void readObject(ObjectInputStream in) {
		try {
			this.id = in.readInt();
			this.isWin = in.readBoolean();
			this.time = in.readLong();
			int length = in.readInt();
			byte[] bytes = new byte[length];

			in.readFully(bytes);
			this.name = new String(bytes, Charset.forName("utf-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.id);
		out.writeBoolean(this.isWin);
		out.writeLong(this.time);
		out.writeInt(this.name.getBytes(Charset.forName("utf-8")).length);
		out.write(this.name.getBytes(Charset.forName("utf-8")));
	}
}
