 package org.darcy.sanguo.hero;
 
 import java.util.HashMap;
import java.util.Map;

import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.skill.Skill;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.talent.Talent;
 
 public class HeroTemplate extends ItemTemplate
 {
   public int gender;
   public int camp;
   public boolean isMonster;
   public int shapeId;
   public Skill fjSkill;
   public Skill ptSkill;
   public Skill angrySkill;
   public Skill charmSkill;
   public Skill messSkill;
   public int atkType;
   public int aptitude;
   public Attributes attr = new Attributes();
   public int intensifyRule;
   public int hpGrow;
   public int attackGrow;
   public int phyDefenceGrow;
   public int magDefenceGrow;
   public Map<Integer, Integer> advanceRule = new HashMap();
   public int initExp;
   public int breakSpiritJade;
   public boolean canBreak;
   public int initRebornCostJewel;
   public int[] enIds;
   public int[] enIdsB;
   public Talent[] talents;
 
   public HeroTemplate(int id, String name)
   {
     super(id, 2, name);
   }
 
   public String toString()
   {
     return "HeroTemplate [id=" + this.id + ", name=" + this.name + ", desc=" + this.desc + 
       ", gender=" + this.gender + ", camp=" + this.camp + ", isMonster=" + 
       this.isMonster + ", quality=" + this.quality + ", iconId=" + this.iconId + 
       ", shapeId=" + this.shapeId + ", fjSkill=" + this.fjSkill + 
       ", ptSkill=" + this.ptSkill + ", angrySkill=" + this.angrySkill + 
       ", charmSkill=" + this.charmSkill + ", messSkill=" + this.messSkill + 
       ", atkType=" + this.atkType + ", level=" + this.level + ", aptitude=" + 
       this.aptitude + ", " + this.attr + 
       ", intensifyRule=" + this.intensifyRule + ", hpGrow=" + this.hpGrow + 
       ", attackGrow=" + this.attackGrow + ", phyDefenceGrow=" + 
       this.phyDefenceGrow + ", magDefenceGrow=" + this.magDefenceGrow + "]";
   }
 }
