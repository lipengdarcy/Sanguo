package org.darcy.sanguo.destiny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.service.DestinyService;

public class DestinyRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = 7721086561433305729L;
	private int version = 1;

	private Attributes attris = new Attributes();

	private int leftStars = 0;
	private int currDestinyId;
	private int currBreakId;

	public void init() {
		Attri a;
		Iterator localIterator;
		this.attris = new Attributes();
		for (int i = 1; i <= this.currDestinyId; ++i) {
			DestinyTemplate dt = (DestinyTemplate) DestinyService.destinies.get(Integer.valueOf(i));
			if ((dt != null) && (dt.attris != null)) {
				for (localIterator = dt.attris.iterator(); localIterator.hasNext();) {
					a = (Attri) localIterator.next();
					this.attris.addAttri(a);
				}
			}
		}
		for (int i = 1; i <= this.currBreakId; ++i) {
			BreakTemplate bt = (BreakTemplate) DestinyService.breaks.get(Integer.valueOf(i));
			if ((bt != null) && (bt.attris != null))
				for (localIterator = bt.attris.iterator(); localIterator.hasNext();) {
					a = (Attri) localIterator.next();
					this.attris.addAttri(a);
				}
		}
	}

	public BreakTemplate getNextBreak() {
		return ((BreakTemplate) DestinyService.breaks.get(Integer.valueOf(this.currBreakId + 1)));
	}

	public DestinyTemplate getNextDestiny() {
		return ((DestinyTemplate) DestinyService.destinies.get(Integer.valueOf(this.currDestinyId + 1)));
	}

	public Attributes getNextAttri() {
		DestinyTemplate dt = (DestinyTemplate) DestinyService.destinies.get(Integer.valueOf(this.currDestinyId + 1));
		if (dt != null) {
			Attributes attris = new Attributes();
			Iterator localIterator = dt.attris.iterator();
			while (true) {
				Attri a = (Attri) localIterator.next();
				attris.addAttri(a);

				if (!(localIterator.hasNext())) {
					label61: return attris;
				}
			}
		}
		return new Attributes();
	}

	public void addStars() {
		this.leftStars += 1;
	}

	public void decStars(int dec) {
		this.leftStars -= dec;
	}

	public Attributes getAttris() {
		return this.attris;
	}

	public int getLeftStars() {
		return this.leftStars;
	}

	public int getCurrDestinyId() {
		return this.currDestinyId;
	}

	public void setAttris(Attributes attris) {
		this.attris = attris;
	}

	public void setLeftStars(int leftStars) {
		this.leftStars = leftStars;
	}

	public void setCurrDestinyId(int currDestinyId) {
		this.currDestinyId = currDestinyId;
	}

	public int getCurrBreakId() {
		return this.currBreakId;
	}

	public void setCurrBreakId(int currBreakId) {
		this.currBreakId = currBreakId;
	}

	private void readObject(ObjectInputStream in) {
		try {
			this.attris = new Attributes();
			in.readInt();
			this.currBreakId = in.readInt();
			this.currDestinyId = in.readInt();
			this.leftStars = in.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.version);
		out.writeInt(this.currBreakId);
		out.writeInt(this.currDestinyId);
		out.writeInt(this.leftStars);
	}

	public int getBlobId() {
		return 13;
	}
}
