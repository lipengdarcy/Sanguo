package org.darcy.sanguo.union;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

import sango.packet.PbLeague;

public class LeagueBossDamage {
	public static final int version = 1;
	int id;
	String name;
	int bossId;
	int damage;
	boolean kill;
	int money;
	long time;

	public PbLeague.LeagueBossDamage genLeagueBossDamage() {
		PbLeague.LeagueBossDamage.Builder b = PbLeague.LeagueBossDamage.newBuilder();
		b.setBossId(this.bossId);
		b.setName(this.name);
		b.setDamage(this.damage);
		b.setMoney(this.money);
		b.setIsKill(this.kill);
		return b.build();
	}

	public static LeagueBossDamage readObject(ObjectInputStream in) {
		try {
			in.readInt();
			LeagueBossDamage damage = new LeagueBossDamage();
			damage.id = in.readInt();
			int size = in.readInt();
			byte[] bytes = new byte[size];
			in.readFully(bytes);
			damage.name = new String(bytes, Charset.forName("utf-8"));
			damage.bossId = in.readInt();
			damage.damage = in.readInt();
			damage.kill = in.readBoolean();
			damage.money = in.readInt();
			damage.time = in.readLong();
			return damage;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(1);
		out.writeInt(this.id);
		out.writeInt(this.name.getBytes(Charset.forName("utf-8")).length);
		out.write(this.name.getBytes(Charset.forName("utf-8")));
		out.writeInt(this.bossId);
		out.writeInt(this.damage);
		out.writeBoolean(this.kill);
		out.writeInt(this.money);
		out.writeLong(this.time);
	}
}
