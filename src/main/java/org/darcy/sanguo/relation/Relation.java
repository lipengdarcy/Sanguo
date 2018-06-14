 package org.darcy.sanguo.relation;
 
 import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
 
 public class Relation
   implements Serializable
 {
   private static final long serialVersionUID = 5047495554972655989L;
   int type;
   private static final int version = 1;
   ConcurrentHashMap<Integer, Long> playerIds = new ConcurrentHashMap();
 
   private void readObject(ObjectInputStream in) {
     try {
       in.readInt();
       this.type = in.readInt();
       this.playerIds = new ConcurrentHashMap();
       int count = in.readInt();
       for (int i = 0; i < count; ++i) {
         int key = in.readInt();
         long value = in.readLong();
         this.playerIds.put(Integer.valueOf(key), Long.valueOf(value));
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
 
   private void writeObject(ObjectOutputStream out)
     throws IOException
   {
     out.writeInt(1);
     out.writeInt(this.type);
     out.writeInt(this.playerIds.size());
     for (Iterator localIterator = this.playerIds.keySet().iterator(); localIterator.hasNext(); ) { int key = ((Integer)localIterator.next()).intValue();
       out.writeInt(key);
       out.writeLong(((Long)this.playerIds.get(Integer.valueOf(key))).longValue());
     }
   }
 
   public Relation()
   {
   }
 
   public long getTime(int pid) {
     Long t = (Long)this.playerIds.get(Integer.valueOf(pid));
     if (t != null) {
       return t.longValue();
     }
     return -1L;
   }
 
   public void init()
   {
     for (Iterator localIterator = this.playerIds.keySet().iterator(); localIterator.hasNext(); ) { int id = ((Integer)localIterator.next()).intValue();
       Platform.getPlayerManager().getMiniPlayer(id);
     }
   }
 
   public Relation(int type) {
     this.type = type;
   }
 
   public MiniPlayer getMiniPlayer(int id) {
     if (!(this.playerIds.keySet().contains(Integer.valueOf(id)))) {
       return null;
     }
     return Platform.getPlayerManager().getMiniPlayer(id);
   }
 
   public List<MiniPlayer> getMiniPlayers()
   {
     List players = new ArrayList();
     for (Iterator localIterator = this.playerIds.keySet().iterator(); localIterator.hasNext(); ) { int id = ((Integer)localIterator.next()).intValue();
       MiniPlayer p = getMiniPlayer(id);
       if (p != null) {
         players.add(p);
       }
     }
     return players;
   }
 
   public synchronized void addPlayer(Player player) {
     if (this.playerIds.get(Integer.valueOf(player.getId())) == null)
       this.playerIds.put(Integer.valueOf(player.getId()), Long.valueOf(System.currentTimeMillis()));
   }
 
   public int getType()
   {
     return this.type;
   }
 
   public void setType(int type) {
     this.type = type;
   }
 
   public ConcurrentHashMap<Integer, Long> getPlayerIds()
   {
     return this.playerIds;
   }
 
   protected void removeOldestRelation() {
     int id = 0;
     long time = 9223372036854775807L;
 
     for (Iterator localIterator = this.playerIds.keySet().iterator(); localIterator.hasNext(); ) { int i = ((Integer)localIterator.next()).intValue();
       if (((Long)this.playerIds.get(Integer.valueOf(i))).longValue() < time) {
         id = i;
         time = ((Long)this.playerIds.get(Integer.valueOf(i))).longValue();
       }
     }
 
     this.playerIds.remove(Integer.valueOf(id));
   }
 
   public void addPlayerId(int id, long time) {
     this.playerIds.put(Integer.valueOf(id), Long.valueOf(time));
   }
 
   public synchronized void removePlayer(int id) {
     this.playerIds.remove(Integer.valueOf(id));
   }
 
   public void clear() {
     this.playerIds.clear();
   }
 }
