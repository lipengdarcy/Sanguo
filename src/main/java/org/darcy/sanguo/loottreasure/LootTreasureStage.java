 package org.darcy.sanguo.loottreasure;
 
 import java.util.ArrayList;

import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.map.StageTemplate;
import org.darcy.sanguo.player.Player;
 
 public class LootTreasureStage extends Stage
 {
   public static final int MAX_ROUND = 30;
   private Player player;
   private Player rival;
 
   public LootTreasureStage(StageTemplate st, Player player, Player rival)
   {
     super(8, st.channels[0].getPositionInfo(), st.name, st.secenId);
     this.player = player;
     this.rival = rival;
   }
 
   public void init()
   {
     this.offen = new Team(this);
     this.offen.setUnits(this.player.getWarriors().getStands());
 
     Team deffen = new Team(this);
     deffen.setUnits(this.rival.getWarriors().getStands());
     this.deffens = new ArrayList(1);
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
