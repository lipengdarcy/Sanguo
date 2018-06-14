 package org.darcy.sanguo.persist;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.service.Service;
 
 public class PlayerBlobService
   implements Service
 {
   public static List<Integer> blobIds = new ArrayList();
 
   public void startup() throws Exception
   {
     loadBlobs();
   }
 
   private void loadBlobs() {
     blobIds.add(Integer.valueOf(3));
     blobIds.add(Integer.valueOf(1));
     blobIds.add(Integer.valueOf(2));
     blobIds.add(Integer.valueOf(5));
     blobIds.add(Integer.valueOf(4));
     blobIds.add(Integer.valueOf(6));
     blobIds.add(Integer.valueOf(7));
     blobIds.add(Integer.valueOf(8));
     blobIds.add(Integer.valueOf(9));
     blobIds.add(Integer.valueOf(10));
     blobIds.add(Integer.valueOf(11));
     blobIds.add(Integer.valueOf(12));
     blobIds.add(Integer.valueOf(13));
     blobIds.add(Integer.valueOf(14));
     blobIds.add(Integer.valueOf(15));
     blobIds.add(Integer.valueOf(16));
     blobIds.add(Integer.valueOf(18));
     blobIds.add(Integer.valueOf(17));
     blobIds.add(Integer.valueOf(19));
     blobIds.add(Integer.valueOf(20));
     blobIds.add(Integer.valueOf(21));
   }
 
   public void shutdown()
   {
   }
 
   public void reload()
     throws Exception
   {
   }
 }
