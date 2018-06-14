 package org.darcy.sanguo.item.equip;
 
 import org.darcy.sanguo.attri.Attributes;
 
 public class ForgeAttr
 {
   public int type;
   public int value;
 
   public Attributes getAttr()
   {
     Attributes attr = new Attributes();
     attr.set(this.type, this.value);
     return attr;
   }
 }
