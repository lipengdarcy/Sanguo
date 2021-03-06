package org.darcy.sanguo.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.darcy.sanguo.bag.Bags;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

public class BagsUserType extends BlobUserType {
	public Object deepCopy(Object obj) throws HibernateException {
		return ((obj == null) ? null : (Bags) obj);
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		byte[] bytes = resultSet.getBytes(names[0]);
		Bags bags = null;
		if (bytes != null) {
			bags = (Bags) SerialUtil.deSerialize(bytes);
		}
		if (bags == null) {
			bags = new Bags();
		}

		return bags;
	}

	public void nullSafeSet(PreparedStatement statement, Object obj, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		byte[] bytes = SerialUtil.serialize((Bags) obj);
		if (bytes != null)
			statement.setBytes(index, bytes);
		else
			statement.setNull(index, SQL_TYPE[0]);
	}

	public Class returnedClass() {
		return Bags.class;
	}
}
