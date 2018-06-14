package org.darcy.sanguo.usertype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.arena.ArenaInfo;
import org.darcy.sanguo.arena.RankInfo;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

public class ArenaInfoUserType extends BlobUserType {
	public Object deepCopy(Object obj) throws HibernateException {
		return ((obj == null) ? null : (ArenaInfo) obj);
	}

	public Class returnedClass() {
		return ArenaInfo.class;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		byte[] bytes = rs.getBytes(names[0]);
		ArenaInfo info = null;
		if (bytes != null) {
			info = (ArenaInfo) SerialUtil.deSerialize(bytes);
		}
		if ((info == null) && (owner instanceof Arena)) {
			Arena arena = (Arena) owner;
			info = new ArenaInfo();
			arena.setCurId(1);
			Calendar rewardCal = Arena.getActualRewardCalendar(Calendar.getInstance());
			RankInfo rankInfo = new RankInfo(arena.getCurId(), arena.getRank(), rewardCal.getTimeInMillis());
			info.getRankInfos().put(Integer.valueOf(rankInfo.id), rankInfo);
		}

		return info;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		byte[] bytes = SerialUtil.serialize((ArenaInfo) value);
		if (bytes != null)
			st.setBytes(index, bytes);
		else
			st.setNull(index, SQL_TYPE[0]);

	}
}
