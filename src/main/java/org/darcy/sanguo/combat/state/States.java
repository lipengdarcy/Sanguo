 package org.darcy.sanguo.combat.state;
 
 import java.util.HashMap;

import org.darcy.sanguo.Platform;
 
 public class States
 {
   private HashMap<Integer, State> states = new HashMap();
 
   public boolean hasState(int stateId) {
     return (this.states.get(Integer.valueOf(stateId)) == null);
   }
 
   private void addState(int stateId) {
     Platform.getLog().logCombat("addState: " + stateId);
     State state = (State)this.states.get(Integer.valueOf(stateId));
     if (state != null) {
       state.setCount(state.getCount() + 1);
     } else {
       state = new State(stateId);
       this.states.put(Integer.valueOf(stateId), state);
     }
   }
 
   public void addState(int stateId, int id)
   {
     Platform.getLog().logCombat("addState: " + stateId + " " + id);
     if ((stateId == 5) || (stateId == 8)) {
       State state = (State)this.states.get(Integer.valueOf(stateId));
       if (state != null) {
         ImunityState is = (ImunityState)state;
         is.addState(id);
       } else {
         state = new ImunityState(stateId, id);
         this.states.put(Integer.valueOf(stateId), state);
       }
     } else {
       addState(stateId);
     }
   }
 
   public void remove(int stateId, int effectId)
   {
     Platform.getLog().logCombat("removeState: " + stateId + " " + effectId);
     State s = (State)this.states.get(Integer.valueOf(stateId));
     if (s != null)
       if (s.remove(new int[] { effectId }))
         this.states.remove(Integer.valueOf(stateId));
   }
 
   public boolean canEffect(int effectId)
   {
     State state = (State)this.states.get(Integer.valueOf(5));
     if (state != null) {
       ImunityState is = (ImunityState)state;
       return is.canEffect(effectId);
     }
     return true;
   }
 
   public boolean canAddBuff(int catagoryId) {
     State state = (State)this.states.get(Integer.valueOf(8));
     if (state != null) {
       ImunityState is = (ImunityState)state;
       return is.canEffect(catagoryId);
     }
     return true;
   }
 }
