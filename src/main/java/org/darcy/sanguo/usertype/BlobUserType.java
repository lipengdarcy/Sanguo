package org.darcy.sanguo.usertype;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public abstract class BlobUserType implements UserType {
	//public static int[] SQL_TYPE = { Hibernate.BINARY.sqlType() };
	public static int[] SQL_TYPE = { 1 };

	public Object assemble(Serializable sobj, Object obj) throws HibernateException {
		return sobj;
	}

	public Serializable disassemble(Object obj) throws HibernateException {
		return ((Serializable) obj);
	}

	public boolean equals(Object obj0, Object obj1) throws HibernateException {
		if (obj0 == obj1) {
			return true;
		}
		if ((obj0 == null) || (obj1 == null)) {
			return false;
		}
		return obj0.equals(obj1);
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return target;
	}

	public int hashCode(Object obj) throws HibernateException {
		return obj.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public int[] sqlTypes() {
		return SQL_TYPE;
	}
}
