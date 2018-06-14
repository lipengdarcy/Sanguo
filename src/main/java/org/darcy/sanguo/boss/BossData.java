 package org.darcy.sanguo.boss;
 
 import java.util.ArrayList;
import java.util.List;
 
 public class BossData
 {
   public boolean open;
   public int id;
   public int serverId;
   public int bossLevel = 1;
   public int killer;
   public List<Integer> ranks = new ArrayList();
 
   public List<Integer> leagueRanks = new ArrayList();
 
   public boolean isOpen() {
     return this.open;
   }
 
   public int getId() {
     return this.id;
   }
 
   public int getServerId() {
     return this.serverId;
   }
 
   public int getKiller() {
     return this.killer;
   }
 
   public List<Integer> getRanks() {
     return this.ranks;
   }
 
   public void setOpen(boolean open) {
     this.open = open;
   }
 
   public void setId(int id) {
     this.id = id;
   }
 
   public void setServerId(int serverId) {
     this.serverId = serverId;
   }
 
   public void setKiller(int killer) {
     this.killer = killer;
   }
 
   public int getBossLevel() {
     return this.bossLevel;
   }
 
   public void setBossLevel(int bossLevel) {
     this.bossLevel = bossLevel;
   }
 
   public void setRanks(List<Integer> ranks) {
     this.ranks = ranks;
   }
 }
