 package org.darcy.sanguo.randomshop;
 
 import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
 
 public class CherishDiscount
   implements Serializable
 {
   private static final long serialVersionUID = -7272883006337379354L;
   public static final String CACHE_KEY = "CACHEKEY#CHERISDISCOUNT";
   public long start;
   public long end;
   public int refreshCost;
   public Map<Integer, Integer> goodsDiscounts = new HashMap();
 
   public Map<Integer, Integer> vipTimes = new HashMap();
 
   public int getAddTime(int vipLevel)
   {
     if (this.vipTimes.containsKey(Integer.valueOf(vipLevel))) {
       return ((Integer)this.vipTimes.get(Integer.valueOf(vipLevel))).intValue();
     }
     return 0;
   }
 
   public int getDiscountPrice(Goods goods)
   {
     if (this.goodsDiscounts.containsKey(Integer.valueOf(goods.getGoodsType()))) {
       int discount = ((Integer)this.goodsDiscounts.get(Integer.valueOf(goods.getGoodsType()))).intValue();
       return (int)(goods.getPrice() * discount / 100.0D);
     }
     return goods.getPrice();
   }
 }
