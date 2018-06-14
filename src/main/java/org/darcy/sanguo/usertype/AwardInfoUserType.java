package org.darcy.sanguo.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.darcy.sanguo.awardcenter.AwardInfo;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

public class AwardInfoUserType extends BlobUserType {
	public Object deepCopy(Object obj) throws HibernateException {
		return ((obj == null) ? null : (AwardInfo) obj);
	}

	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		byte[] bytes = resultSet.getBytes(names[0]);
		AwardInfo awards = null;
		if (bytes != null) {
			awards = (AwardInfo) SerialUtil.deSerialize(bytes);
		}
		if (awards == null) {
			awards = new AwardInfo();
		}

		return awards;
	}

	public void nullSafeSet(PreparedStatement statement, Object obj, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		byte[] bytes = SerialUtil.serialize((AwardInfo) obj);
		if (bytes != null)
			statement.setBytes(index, bytes);
		else
			statement.setNull(index, SQL_TYPE[0]);
	}

	public Class returnedClass() {
		return AwardInfo.class;
	}
}
