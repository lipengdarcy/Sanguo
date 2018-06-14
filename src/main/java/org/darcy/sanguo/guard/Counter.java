/*    */ package org.darcy.sanguo.guard;
/*    */ import java.util.HashMap;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Random;
/*    */ 
/*    */ public class Counter
/*    */ {
/* 10 */   private static HashMap<String, List<Long>> base = new HashMap();
/*    */ 
/*    */   public static void clear(String id) {
/* 13 */     List list = (List)base.get(id);
/* 14 */     if (list != null)
/* 15 */       list.clear();
/*    */   }
/*    */ 
/*    */   public static void add(String id)
/*    */   {
/* 20 */     List list = (List)base.get(id);
/* 21 */     if (list == null) {
/* 22 */       synchronized (base) {
/* 23 */         list = (List)base.get(id);
/* 24 */         if (list == null) {
/* 25 */           list = new LinkedList();
/* 26 */           base.put(id, list);
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 31 */     list.add(Long.valueOf(System.currentTimeMillis()));
/*    */   }
/*    */ 
/*    */   public static int check(String id, long lastMiliSeconds)
/*    */   {
/* 40 */     List list = (List)base.get(id);
/* 41 */     if (list == null) {
/* 42 */       return 0;
/*    */     }
/*    */ 
/* 45 */     long value = System.currentTimeMillis() - lastMiliSeconds;
/* 46 */     int size = list.size();
/* 47 */     int index = size - 1;
/* 48 */     while ((index >= 0) && (
/* 49 */       ((Long)list.get(index)).longValue() >= value)) {
/* 50 */       --index;
/*    */     }
/*    */ 
/* 53 */     if (size > 1000) {
/* 54 */       list.clear();
/*    */     }
/*    */ 
/* 57 */     return (size - 1 - index);
/*    */   }
/*    */ 
/*    */   public static void reset(String id) {
/* 61 */     List list = (List)base.get(id);
/* 62 */     if (list != null)
/* 63 */       list.clear();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws InterruptedException
/*    */   {
/* 68 */     Random r = new Random();
/*    */     while (true) {
/* 70 */       String s = String.valueOf(r.nextInt(100000));
/* 71 */       add(s);
/* 72 */       if (check(s, 60000L) > 100);
/* 73 */       System.out.println(s);
/* 74 */       long start = System.currentTimeMillis();
/* 75 */       int count = 1000;
/* 76 */       while (count-- > 0)
/* 77 */         check(s, 60000L);
/* 78 */       System.out.println("cost:" + (System.currentTimeMillis() - start));
/*    */     }
/*    */   }
/*    */ }