package org.darcy.sanguo.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract interface SerialData extends Serializable {
	
	public abstract Object clone();

	public abstract void readObject(ObjectInputStream paramObjectInputStream)
			throws IOException, ClassNotFoundException;

	public abstract void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException;
}
