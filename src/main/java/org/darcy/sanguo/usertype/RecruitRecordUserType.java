 package org.darcy.sanguo.usertype;
 
 import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.darcy.sanguo.recruit.RecruitRecord;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
 
 public class RecruitRecordUserType extends BlobUserType
 {
   public Object deepCopy(Object obj)
     throws HibernateException
   {
     return ((obj == null) ? null : ((RecruitRecord)obj).clone());
   }
 
   public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
     throws HibernateException, SQLException
   {
     byte[] bytes = resultSet.getBytes(names[0]);
     RecruitRecord r = null;
     if (bytes != null) {
       r = (RecruitRecord)SerialUtil.deSerialize(bytes);
     }
 
     if (r != null) {
       return r;
     }
     return new RecruitRecord();
   }
 
   public void nullSafeSet(PreparedStatement statement, Object obj, int index,SessionImplementor session)
     throws HibernateException, SQLException
   {
     byte[] bytes = SerialUtil.serialize((RecruitRecord)obj);
     if (bytes != null)
       statement.setBytes(index, bytes);
     else
       statement.setNull(index, SQL_TYPE[0]);
   }
 
   public Class returnedClass()
   {
     return RecruitRecord.class;
   }
 }
