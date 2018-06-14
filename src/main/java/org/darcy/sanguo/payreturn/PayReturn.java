 package org.darcy.sanguo.payreturn;
 
 import java.util.HashMap;
import java.util.List;

import org.darcy.sanguo.drop.Reward;
 
 public class PayReturn
 {
   public String accountId;
   public int charge;
   public List<Reward> registRewards;
   public HashMap<Integer, List<Reward>> loginRewards = new HashMap();
 
   public List<Reward> getLoginRewards(int day) {
     return ((List)this.loginRewards.get(Integer.valueOf(day)));
   }
 }
