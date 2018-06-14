 package org.darcy.sanguo.map;
 
 import org.darcy.sanguo.monster.Monster;
import org.darcy.sanguo.monster.MonsterService;
import org.darcy.sanguo.monster.MonsterTemplate;
import org.darcy.sanguo.unit.Unit;
 
 public class SectionTemplate
 {
   public int id;
   public boolean auto;
   public int bossIndex = -1;
   public int[] monsterids;
 
   public Unit[] getMonsters()
   {
     Unit[] units = new Unit[6];
     for (int i = 0; i < this.monsterids.length; ++i) {
       int mid = this.monsterids[i];
       MonsterTemplate mt = (MonsterTemplate)MonsterService.monsterTemplates.get(Integer.valueOf(mid));
       if (mt != null) {
         Monster m = new Monster(mt);
         units[i] = m;
       }
     }
 
     return units;
   }
 }
