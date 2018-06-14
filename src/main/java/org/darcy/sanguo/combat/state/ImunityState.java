 package org.darcy.sanguo.combat.state;
 
 import java.util.HashMap;
 
 public class ImunityState extends State
 {
   private HashMap<Integer, Integer> effectIds = new HashMap();
 
   public ImunityState(int id, int effectId)
   {
     super(id);
     this.effectIds.put(Integer.valueOf(effectId), Integer.valueOf(1));
   }
 
   public boolean canEffect(int effectId)
   {
     return (this.effectIds.get(Integer.valueOf(effectId)) != null);
   }
 
   public void addState(int effectId)
   {
     Integer count = (Integer)this.effectIds.get(Integer.valueOf(effectId));
     if (count != null)
       this.effectIds.put(Integer.valueOf(effectId), count = Integer.valueOf(count.intValue() + 1));
     else
       this.effectIds.put(Integer.valueOf(effectId), Integer.valueOf(1));
   }
 
   public boolean remove(int[] params)
   {
     int effectId = params[0];
     Integer count = (Integer)this.effectIds.get(Integer.valueOf(effectId));
     if ((count != null) && (count.intValue() > 1)) {
       count = Integer.valueOf(count.intValue() - 1);
       this.effectIds.put(Integer.valueOf(effectId), count);
       return false;
     }
     this.effectIds.remove(Integer.valueOf(effectId));
 
     return (this.effectIds.size() != 0);
   }
 }
