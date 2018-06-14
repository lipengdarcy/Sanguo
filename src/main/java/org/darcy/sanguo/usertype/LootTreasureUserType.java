 package org.darcy.sanguo.usertype;
 
 import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.darcy.sanguo.loottreasure.LootTreasure;
import org.darcy.sanguo.util.SerialUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
 
 public class LootTreasureUserType extends BlobUserType
 {
   public Object deepCopy(Object obj)
     throws HibernateException
   {
     return ((obj == null) ? null : (LootTreasure)obj);
   }
 
   public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session,Object owner)
     throws HibernateException, SQLException
   {
     byte[] bytes = resultSet.getBytes(names[0]);
     LootTreasure lootTreasure = null;
     if (bytes != null) {
       lootTreasure = (LootTreasure)SerialUtil.deSerialize(bytes);
     }
     if (lootTreasure == null) {
       lootTreasure = new LootTreasure();
     }
 
     return lootTreasure;
   }
 
   public void nullSafeSet(PreparedStatement statement, Object obj, int index,SessionImplementor session)
     throws HibernateException, SQLException
   {
     byte[] bytes = SerialUtil.serialize((LootTreasure)obj);
     if (bytes != null)
       statement.setBytes(index, bytes);
     else
       statement.setNull(index, SQL_TYPE[0]);
   }
 
   public Class returnedClass()
   {
     return LootTreasure.class;
   }
 }
