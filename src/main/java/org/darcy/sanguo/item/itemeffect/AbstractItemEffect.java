 package org.darcy.sanguo.item.itemeffect;
 
 import java.lang.reflect.Constructor;
 
 public abstract class AbstractItemEffect
   implements ItemEffect
 {
   int paramCount;
   String[] params;
 
   public AbstractItemEffect(int paramCount)
   {
     this.paramCount = paramCount;
     this.params = new String[paramCount];
   }
 
   public int getParamCount() {
     return this.paramCount;
   }
 
   public ItemEffect copy()
   {
     try {
       Constructor constructor = super.getClass().getConstructor(new Class[] { Integer.TYPE });
       Object obj = constructor.newInstance(new Object[] { Integer.valueOf(this.paramCount) });
       return ((ItemEffect)obj);
     } catch (Exception e) {
       e.printStackTrace();
     }
     return null;
   }
 }
