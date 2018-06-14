 package org.darcy.sanguo.usertype;
 
 import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
 
 public class ListIntUserType extends BlobUserType
 {
   public Object deepCopy(Object obj)
     throws HibernateException
   {
     return obj;
   }
 
   public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session,Object owner)
     throws HibernateException, SQLException
   {
     List r = new ArrayList();
 
     byte[] bytes = resultSet.getBytes(names[0]);
     if (bytes != null) {
       DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
       try {
         int count = dis.readInt();
         for (int i = 0; i < count; ++i)
           r.add(Integer.valueOf(dis.readInt()));
       }
       catch (Exception e) {
         e.printStackTrace();
       }
     }
 
     return r;
   }
 
   public void nullSafeSet(PreparedStatement statement, Object obj, int index, SessionImplementor session)
     throws HibernateException, SQLException
   {
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     DataOutputStream dos = new DataOutputStream(bos);
     try {
       List set = (List)obj;
       dos.writeInt(set.size());
       for (Iterator localIterator = set.iterator(); localIterator.hasNext(); ) { int key = ((Integer)localIterator.next()).intValue();
         dos.writeInt(key);
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
     byte[] bytes = bos.toByteArray();
     if (bytes != null)
       statement.setBytes(index, bytes);
     else
       statement.setNull(index, SQL_TYPE[0]);
   }
 
   public Class returnedClass()
   {
     return List.class;
   }
 }
