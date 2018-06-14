 package org.darcy.sanguo.coup;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.skill.Skill;
 
 public class Coup
 {
   public int id;
   public int unLockLevel;
   public int[] skills = new int[10];
 
   public Skill getSkill(int level) {
     if (level < 1) level = 1;
     int id = this.skills[(level - 1)];
     return ((CombatService)Platform.getServiceManager().get(CombatService.class)).getSkill(id);
   }
 
   public int getNextSkill(int level) {
     if (level >= this.skills.length) {
       return -1;
     }
     if (level <= 0) {
       level = 1;
     }
 
     return this.skills[level];
   }
 }
