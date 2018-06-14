 package org.darcy.sanguo.union.combat;
 
 import java.util.ArrayList;

import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.player.Player;
 
 public class RandomStage extends Stage
 {
   private Player player;
   private Player target;
 
   public RandomStage(int type, String location, String name, int senceId, Player player, Player target)
   {
     super(type, location, name, senceId);
     this.player = player;
     this.target = target;
   }
 
   public void init() {
     this.offen = new Team(this);
     this.offen.setUnits(this.player.getWarriors().getStands());
     this.deffens = new ArrayList();
     Team deffen = new Team(this);
     deffen.setUnits(this.target.getWarriors().getStands());
     this.deffens.add(deffen);
     super.init();
   }
 
   public void proccessReward(Player player)
   {
   }
 
   public boolean isPvP()
   {
     return true;
   }
 
   public void setNames()
   {
     this.defenName = this.target.getName();
     this.offenName = this.player.getName();
   }
 }
