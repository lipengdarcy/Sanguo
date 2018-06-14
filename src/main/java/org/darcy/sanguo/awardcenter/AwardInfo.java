package org.darcy.sanguo.awardcenter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AwardInfo implements Serializable {
	private static final long serialVersionUID = 8015312272002803776L;
	private static int version = 1;

	private ConcurrentHashMap<Integer, Award> awards = new ConcurrentHashMap();

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.awards = new ConcurrentHashMap();
			int size = in.readInt();
			for (int i = 0; i < size; ++i) {
				Award award = Award.readObject(in);
				this.awards.put(Integer.valueOf(award.getId()), award);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Award award;
		out.writeInt(version);
		List<Award> awardList = new ArrayList();
		Iterator itx = this.awards.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			award = (Award) this.awards.get(Integer.valueOf(id));
			if (award != null) {
				awardList.add(award);
			}
		}
		out.writeInt(awardList.size());
		for (Award a : awardList)
			a.writeObject(out);
	}

	public ConcurrentHashMap<Integer, Award> getAwards() {
		return this.awards;
	}

	public void setAwards(ConcurrentHashMap<Integer, Award> awards) {
		this.awards = awards;
	}
}
