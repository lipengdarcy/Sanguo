package org.darcy.sanguo.usertype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

public class EnemyUserType extends BlobUserType {
	public static final short VERSION_ID = 1;

	public Object deepCopy(Object obj) throws HibernateException {
		return obj;
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		byte[] bytes = resultSet.getBytes(names[0]);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);
		List enemy = new ArrayList();
		try {
			int version = dis.readInt();
			int size = dis.readInt();
			for (int i = 0; i < size; ++i) {
				int id = dis.readInt();
				enemy.add(Integer.valueOf(id));
			}
			return enemy;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return enemy;
	}

	public void nullSafeSet(PreparedStatement statement, Object obj, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			List<Integer> enemy = (List) obj;
			dos.writeInt(1);
			dos.writeInt(enemy.size());
			for (Integer integer : enemy)
				dos.writeInt(integer.intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] bytes = baos.toByteArray();
		if (bytes != null)
			statement.setBytes(index, bytes);
		else
			statement.setNull(index, SQL_TYPE[0]);
	}

	public Class returnedClass() {
		return List.class;
	}
}
