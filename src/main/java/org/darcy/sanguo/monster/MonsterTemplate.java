 package org.darcy.sanguo.monster;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.combat.skill.Skill;
 
 public class MonsterTemplate
 {
   public int id;
   public String name;
   public int level;
   public int icon;
   public int shapeId;
   public Skill ptSkill;
   public Skill angrySkill;
   public int actType;
   public Skill fjSkill;
   public Attributes attributes = new Attributes();
   public String desc;
   public int gender;
   public int camp;
   public int isMonster;
   public int classes;
   public Skill mfSkill;
   public Skill hlSkill;
   public int quality;
   public int[] buffIds;
 
   public List<Buff> getBuffs()
   {
     List buffs = null;
     if ((this.buffIds != null) && (this.buffIds.length > 0)) {
       buffs = new ArrayList();
       CombatService cs = (CombatService)Platform.getServiceManager().get(CombatService.class);
       for (int bid : this.buffIds) {
         Buff b = cs.getBuff(bid);
         if (b != null) {
           buffs.add(b.copy());
         }
       }
     }
 
     return buffs;
   }
 }
