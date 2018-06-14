 package org.darcy.sanguo.item;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.item.itemeffect.ItemEffect;
 
 public class NormalItemTemplate extends ItemTemplate
 {
   public static final int TYPE_MEDICINE = 0;
   public static final int TYPE_TREASUREBOX = 1;
   public static final int TYPE_SYNTH = 2;
   public static final int TYPE_PRODUCT = 3;
   public static final int TYPE_OTHER = 4;
   public int userLevel;
   public int vipLevel;
   public int normalItemType;
   public int maxCount;
   public List<String> needItems = new ArrayList();
   public ItemEffect effect;
 
   public NormalItemTemplate(int id, String name)
   {
     super(id, 0, name);
   }
 }
