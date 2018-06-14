 package org.darcy.sanguo.item;
 
 import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.item.equip.ForgeAttr;
 
 public class EquipmentTemplate extends ItemTemplate
 {
   public static final int TYPE_WEAPON = 1;
   public static final int TYPE_COAT = 2;
   public static final int TYPE_HELMET = 3;
   public static final int TYPE_NECKLACE = 4;
   public int equipType;
   public Attributes attr = new Attributes();
   public int aptitude;
   public int intensifyRule;
   public int intensifyLimitRatio;
   public int hpGrow;
   public int attackGrow;
   public int phyDefenceGrow;
   public int magDefenceGrow;
   public int suitId;
   public boolean canPolish;
   public int polishRuleId;
   public int polishBaseValue;
   public int polishGrowValue;
   public int polishGrowLevel;
   public int basePolishItemCount;
   public int refineBackForgeItemCount;
   public int hpForgeGrow;
   public int attackForgeGrow;
   public int phyDefenceForgeGrow;
   public int magDefenceForgeGrow;
   public int baseRebornCost;
   public ForgeAttr[] forgeAttrs;
 
   public EquipmentTemplate(int id, String name)
   {
     super(id, 4, name);
   }
 }
