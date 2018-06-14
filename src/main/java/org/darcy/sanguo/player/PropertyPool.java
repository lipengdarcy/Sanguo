package org.darcy.sanguo.player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.arena.ArenaData;
import org.darcy.sanguo.persist.PlayerBlobEntity;

public class PropertyPool implements PlayerBlobEntity {
	private static final long serialVersionUID = -432878224537486973L;
	private static int VERSION = 1;

	private Map<Integer, Object> properties = new HashMap();

	public void refresh() {
		set(2, Integer.valueOf(20));
		set(3, Integer.valueOf(ArenaData.challengeDayCount));
		set(7, Boolean.valueOf(false));
		set(10, Integer.valueOf(0));
		set(11, Integer.valueOf(0));
		set(12, Integer.valueOf(0));
		set(13, Integer.valueOf(0));
		set(14, Integer.valueOf(0));
		set(15, Integer.valueOf(0));
		set(16, Integer.valueOf(0));
		set(17, Integer.valueOf(0));
		set(4, Integer.valueOf(0));
	}

	private void readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();
			this.properties = new HashMap();
			int count = in.readInt();
			for (int i = 0; i < count; ++i) {
				int type = in.readInt();
				int persitenceType = in.readInt();
				switch (persitenceType) {
				case 2:
					this.properties.put(Integer.valueOf(type), Byte.valueOf((byte) in.read()));
					break;
				case 4:
					this.properties.put(Integer.valueOf(type), Double.valueOf(in.readDouble()));
					break;
				case 5:
					this.properties.put(Integer.valueOf(type), Long.valueOf(in.readLong()));
					break;
				case 1:
					this.properties.put(Integer.valueOf(type), Integer.valueOf(in.readInt()));
					break;
				case 3:
					int length = in.readInt();
					byte[] bytes = new byte[length];
					in.read(bytes);
					this.properties.put(Integer.valueOf(type), new String(bytes));
					break;
				case 6:
					int size = in.readInt();
					Set set = new HashSet();
					for (int m = 0; m < size; ++m) {
						set.add(Integer.valueOf(in.readInt()));
					}
					this.properties.put(Integer.valueOf(type), set);
					break;
				case 7:
					boolean value = in.readBoolean();
					this.properties.put(Integer.valueOf(type), Boolean.valueOf(value));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(VERSION);
		out.writeInt(this.properties.size());
		Iterator itx = this.properties.keySet().iterator();
		while (itx.hasNext()) {
			int type = ((Integer) itx.next()).intValue();
			int persitenceType = PropertyType.getPersistenceType(type);
			Object obj = this.properties.get(Integer.valueOf(type));
			out.writeInt(type);
			out.writeInt(persitenceType);
			switch (persitenceType) {
			case 2:
				out.writeByte(((Integer) obj).intValue());
				break;
			case 4:
				out.writeDouble(((Double) obj).doubleValue());
				break;
			case 5:
				out.writeLong(((Long) obj).longValue());
				break;
			case 1:
				out.writeInt(((Integer) obj).intValue());
				break;
			case 3:
				String str = obj.toString();
				out.writeInt(str.getBytes().length);
				out.writeBytes(str);
				break;
			case 6:
				Set<Integer> set = (Set) obj;
				out.writeInt(set.size());
				for (Integer i : set) {
					out.writeInt(i.intValue());
				}
				break;
			case 7:
				boolean b = ((Boolean) obj).booleanValue();
				out.writeBoolean(b);
			}
		}
	}

	public void set(int type, Object obj) {
		if (PropertyType.getPersistenceType(type) != -1)
			this.properties.put(Integer.valueOf(type), obj);
	}

	public int getInt(int type, int value) {
		if (!(this.properties.containsKey(Integer.valueOf(type)))) {
			this.properties.put(Integer.valueOf(type), Integer.valueOf(value));
			return value;
		}
		return ((Integer) this.properties.get(Integer.valueOf(type))).intValue();
	}

	public long getLong(int type, long value) {
		if (!(this.properties.containsKey(Integer.valueOf(type)))) {
			this.properties.put(Integer.valueOf(type), Long.valueOf(value));
			return value;
		}
		return ((Long) this.properties.get(Integer.valueOf(type))).longValue();
	}

	public byte getByte(int type, byte value) {
		if (!(this.properties.containsKey(Integer.valueOf(type)))) {
			this.properties.put(Integer.valueOf(type), Byte.valueOf(value));
			return value;
		}
		return ((Byte) this.properties.get(Integer.valueOf(type))).byteValue();
	}

	public String getString(int type, String value) {
		if (!(this.properties.containsKey(Integer.valueOf(type)))) {
			this.properties.put(Integer.valueOf(type), value);
			return value;
		}
		return ((String) this.properties.get(Integer.valueOf(type)));
	}

	public String getString(int type) {
		return ((String) this.properties.get(Integer.valueOf(type)));
	}

	public boolean getBool(int type, boolean dft) {
		if (this.properties.containsKey(Integer.valueOf(type))) {
			return ((Boolean) this.properties.get(Integer.valueOf(type))).booleanValue();
		}
		return dft;
	}

	public Set<Integer> getIntegers(int type) {
		Set set = null;
		try {
			set = (Set) this.properties.get(Integer.valueOf(type));
		} catch (Exception localException) {
		}
		if (set == null) {
			set = new HashSet();
			this.properties.put(Integer.valueOf(type), set);
		}
		return set;
	}

	public double getDouble(int type, double value) {
		if (!(this.properties.containsKey(Integer.valueOf(type)))) {
			this.properties.put(Integer.valueOf(type), Double.valueOf(value));
			return value;
		}
		return ((Double) this.properties.get(Integer.valueOf(type))).doubleValue();
	}

	public int getBlobId() {
		return 6;
	}
}
