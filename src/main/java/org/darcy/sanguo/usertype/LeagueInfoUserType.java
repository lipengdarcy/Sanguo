 package org.darcy.sanguo.usertype;
 
 import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.darcy.sanguo.arena.ArenaInfo;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.LeagueInfo;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
 
 public class LeagueInfoUserType extends BlobUserType
 {
   public Object deepCopy(Object obj)
     throws HibernateException
   {
     return obj;
   }
 
   public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
     throws HibernateException, SQLException
   {
     byte[] bytes = resultSet.getBytes(names[0]);
     LeagueInfo info = null;
     if (bytes != null) {
       info = (LeagueInfo)SerialUtil.deSerialize(bytes);
     }
     if ((info == null) && (owner instanceof League)) {
       info = new LeagueInfo();
     }
 
     return info;
   }
 
   public void nullSafeSet(PreparedStatement statement, Object obj, int index,SessionImplementor session)
     throws HibernateException, SQLException
   {
     byte[] bytes = SerialUtil.serialize((LeagueInfo)obj);
     if (bytes != null)
       statement.setBytes(index, bytes);
     else
       statement.setNull(index, SQL_TYPE[0]);
   }
 
   public Class returnedClass()
   {
     return ArenaInfo.class;
   }
 }
