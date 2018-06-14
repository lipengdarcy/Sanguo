 package org.darcy.sanguo.usertype;
 
 import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.darcy.sanguo.persist.PlayerBlob;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
 
 public class PlayerBlobUserType extends BlobUserType
 {
   public Object deepCopy(Object obj)
     throws HibernateException
   {
     return ((obj == null) ? null : (PlayerBlob)obj);
   }
 
   public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
     throws HibernateException, SQLException
   {
     byte[] bytes = resultSet.getBytes(names[0]);
     PlayerBlob blob = null;
     if (bytes != null) {
       blob = (PlayerBlob)SerialUtil.deSerialize(bytes);
     }
     if (blob == null) {
       blob = new PlayerBlob((Player)owner);
     }
 
     return blob;
   }
 
   public void nullSafeSet(PreparedStatement statement, Object obj, int index, SessionImplementor session)
     throws HibernateException, SQLException
   {
     byte[] bytes = SerialUtil.serialize((PlayerBlob)obj);
     if (bytes != null)
       statement.setBytes(index, bytes);
     else
       statement.setNull(index, SQL_TYPE[0]);
   }
 
   public Class returnedClass()
   {
     return PlayerBlob.class;
   }
 }
