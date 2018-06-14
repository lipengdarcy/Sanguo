 package org.darcy.sanguo.loottreasure;
 
 import java.io.Serializable;

import org.darcy.sanguo.Platform;
 
 public class ShieldInfo
   implements Serializable
 {
   private static final long serialVersionUID = -3797634807209622817L;
   private int id;
   private long startShield;
   private long curShieldTime;
 
   public ShieldInfo(int id)
   {
     this.id = id;
   }
 
   public static ShieldInfo getShiledInfo(int playerId)
   {
     ShieldInfo info = (ShieldInfo)Platform.getEntityManager().getFromEhCache(ShieldInfo.class.getName(), Integer.valueOf(playerId));
     if (info == null) {
       info = new ShieldInfo(playerId);
       Platform.getEntityManager().putInEhCache(ShieldInfo.class.getName(), Integer.valueOf(playerId), info);
     }
     return info;
   }
 
   public boolean isShield()
   {
     long cur = System.currentTimeMillis();
 
     return (cur - this.startShield > this.curShieldTime);
   }
 
   public int getId()
   {
     return this.id;
   }
 
   public void setId(int id) {
     this.id = id;
   }
 
   public long getStartShield() {
     return this.startShield;
   }
 
   public void setStartShield(long startShield) {
     this.startShield = startShield;
   }
 
   public long getCurShieldTime() {
     return this.curShieldTime;
   }
 
   public void setCurShieldTime(long curShieldTime) {
     this.curShieldTime = curShieldTime;
   }
 }
