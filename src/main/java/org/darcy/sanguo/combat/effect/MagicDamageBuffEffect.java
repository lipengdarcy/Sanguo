 package org.darcy.sanguo.combat.effect;
 
 import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.unit.Unit;
import org.darcy.sanguo.util.RandomBox;
 
 public class MagicDamageBuffEffect extends Effect
 {
   private int minDamageRate;
   private int maxDamageRate;
 
   public MagicDamageBuffEffect(int id, int type, String description, int paramCount, int catagory)
   {
     super(id, type, description, paramCount, catagory);
   }
 
   public Effect copy()
   {
     MagicDamageBuffEffect e = new MagicDamageBuffEffect(this.id, this.type, this.description, this.paramCount, this.catagory);
     e.minDamageRate = this.minDamageRate;
     e.maxDamageRate = this.maxDamageRate;
     e.effectId = this.effectId;
     return e;
   }
 
   public void initParams()
   {
     this.effectId = Integer.parseInt(this.params[0]);
     this.minDamageRate = Integer.parseInt(this.params[1]);
     this.maxDamageRate = Integer.parseInt(this.params[2]);
   }
 
   public int[] effectBuff(Unit owner, Unit caster, Section section)
   {
     Platform.getLog().logCombat("Effect: " + this.description);
     int ran = section.getRandomBox().getNextRandom();
     int mod = this.maxDamageRate - this.minDamageRate + 1;
     int value = ran % mod + this.minDamageRate;
     this.effectType = 2;
 
     int damage = damageCalc(caster, owner, value, section);
     apply(owner, damage);
     return new int[] { 1990, damage };
   }
 
   private void apply(Unit target, int finalDamage) {
     int hp = target.getAttributes().getHp();
     int left = hp - finalDamage;
     if (left < 0) {
       left = 0;
     }
     target.getAttributes().setHp(left);
     Platform.getLog().logCombat("CombatContent: " + target.getName() + " hp:" + target.getAttributes().getHp());
   }
 
   private int damageCalc(Unit src, Unit target, int damageRate, Section section) {
     boolean crit = calcCrit(src, target, section.getRandomBox());
     boolean block = calcBlock(src, target, section.getRandomBox());
 
     int baseMagicDamage = 0;
     baseMagicDamage = (src.getAttributes().get(6) + src.getAttributes().get(5)) * damageRate / 100 - 
       target.getAttributes().get(9);
     Platform.getLog().logCombat(src.getName() + " CombatContent: atk " + (src.getAttributes().get(6) + src.getAttributes().get(5)));
     Platform.getLog().logCombat(target.getName() + " CombatContent: def " + target.getAttributes().get(9));
 
     Platform.getLog().logCombat("CombatContent: rate " + damageRate);
     Platform.getLog().logCombat("CombatContent: phy damage " + baseMagicDamage);
 
     int baseDamage = baseMagicDamage;
     if (baseDamage < 0) {
       Platform.getLog().logCombat("CombatContent: baseDamage = 1");
       baseDamage = 0;
     }
 
     if (crit) {
       baseDamage *= src.getAttributes().get(11);
       baseDamage /= 100;
       Platform.getLog().logCombat("CombatContent: 暴击: " + baseDamage);
     }
 
     if (block) {
       baseDamage /= 2;
       Platform.getLog().logCombat("CombatContent: 格挡 : " + baseDamage);
     }
 
     baseDamage = baseDamage * (10000 - target.getAttributes().get(25)) / 10000;
 
     Platform.getLog().logCombat("CombatContent: 免伤修正 : " + baseDamage);
 
     int finalDamage = 0;
     finalDamage = baseDamage + src.getAttributes().get(17) - target.getAttributes().get(18);
 
     if (finalDamage < 1) {
       finalDamage = 1;
     }
     Platform.getLog().logCombat("CombatContent: 最终伤害 : " + finalDamage);
 
     Attributes a = target.getAttributes();
     finalDamage = a.buffFinalDamageModify(finalDamage);
     Platform.getLog().logCombat("CombatContent: 最终伤害buff修正 : " + finalDamage);
 
     return finalDamage;
   }
 
   public boolean calcCrit(Unit src, Unit target, RandomBox rd) {
     Platform.getLog().logCombat("CombatContent: 计算暴击");
     int critRate = src.getAttributes().get(10) - target.getAttributes().get(12);
     int random = rd.getNextRandom();
     return (random < critRate);
   }
 
   public boolean calcBlock(Unit src, Unit target, RandomBox rd) {
     Platform.getLog().logCombat("CombatContent: 计算格挡");
     int blockRate = target.getAttributes().get(15) - src.getAttributes().get(16);
     int random = rd.getNextRandom();
     return (random < blockRate);
   }
 }
