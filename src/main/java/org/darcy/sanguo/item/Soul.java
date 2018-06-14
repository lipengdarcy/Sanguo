 package org.darcy.sanguo.item;
 
 public class Soul extends Item
 {
   public Soul(ItemTemplate template)
   {
     super(template);
   }
 
   public int getPrice()
   {
     return 0;
   }
 
   public void setPrice(int price)
   {
   }
 
   public String getName()
   {
     return this.template.name;
   }
 
   public boolean canSell()
   {
     return (getPrice() > 0);
   }
 }
