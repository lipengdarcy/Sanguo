 package org.darcy.sanguo.relation;
 
 import java.util.ArrayList;

import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.player.Player;
 
 public class FriendStage extends Stage
 {
   private Player player;
   private Player rival;
 
   public FriendStage(String location, String name, int senceId, Player player, Player rival)
   {
     super(9, location, name, senceId);
     this.player = player;
     this.rival = rival;
   }
 
   public void init(Team offen, Team deffen) {
     this.offen = offen;
     this.deffens = new ArrayList();
     this.deffens.add(deffen);
     super.init();
   }
 
   public void proccessReward(Player player)
   {
   }
 
   public void setNames()
   {
     this.offenName = this.player.getName();
     this.defenName = this.rival.getName();
   }
 
   public boolean isPvP()
   {
     return true;
   }
 }
