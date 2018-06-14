 package org.darcy.sanguo.map;
 
 import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.tower.TowerRecord;
 
 public class MapActivityStage extends Stage
 {
   public static final int MAX_ROUND = 30;
   private MapTemplate mapTemplate;
   private StageTemplate stageTemplate;
   private static final int channel = 0;
   private Player player;
   private List<Gain> gains;
 
   public MapActivityStage(MapTemplate mt, StageTemplate st, Player player)
   {
     super(mt.type, st.channels[0].getPositionInfo(), st.name, st.secenId);
     this.mapTemplate = mt;
     this.stageTemplate = st;
     this.player = player;
   }
 
   public void init() {
     this.offen = new Team();
     this.offen.setUnits(this.player.getWarriors().getStands());
     this.offen.setStage(this);
 
     this.sectionTemplates = this.stageTemplate.channels[0].getSectionTemlateList();
     this.deffens = new ArrayList(this.sectionTemplates.size());
     for (int i = 0; i < this.sectionTemplates.size(); ++i) {
       SectionTemplate sct = (SectionTemplate)this.sectionTemplates.get(i);
       Team deffen = new Team(this);
       deffen.setUnits(sct.getMonsters());
       this.deffens.add(deffen);
     }
     super.init();
   }
 
   public void proccessReward(Player player)
   {
     if (isWin()) {
       StageChannel sc = this.stageTemplate.channels[0];
       DropGroup drop = (DropGroup)DropService.dropGroups.get(Integer.valueOf(sc.getDropId()));
       if (drop != null) {
         this.gains = drop.genGains(player);
         if (this.gains != null) {
           for (Gain gain : this.gains) {
             if (this.mapTemplate.type == 3)
             {
               gain.gain(player, "warriortrial"); } else {
               if (this.mapTemplate.type != 4)
                 continue;
               gain.gain(player, "treasuretrial");
             }
           }
         }
       }
       MapRecord record = player.getMapRecord();
       record.decActivityMapLeftTimes(this.mapTemplate.type);
       Platform.getEventManager().addEvent(new Event(2058, new Object[] { player }));
 
       if (this.mapTemplate.type == 3)
         Platform.getEventManager().addEvent(new Event(2011, new Object[] { player }));
       else if (this.mapTemplate.type == 4) {
         Platform.getEventManager().addEvent(new Event(2012, new Object[] { player }));
       }
     }
 
     Platform.getLog().logPveFight(player, this.mapTemplate, this.stageTemplate, isWin(), 1, player.getMapRecord().getActivityMapLeftTimes(this.mapTemplate.type));
   }
 
   public List<Gain> getGains() {
     return this.gains;
   }
 
   public void setGains(List<Gain> gains) {
     this.gains = gains;
   }
 
   public MapTemplate getMapTemplate() {
     return this.mapTemplate;
   }
 
   public void setMapTemplate(MapTemplate mapTemplate) {
     this.mapTemplate = mapTemplate;
   }
 
   public StageTemplate getStageTemplate() {
     return this.stageTemplate;
   }
 
   public void setStageTemplate(StageTemplate stageTemplate) {
     this.stageTemplate = stageTemplate;
   }
 
   public static int getChannel() {
     return 0;
   }
 
   public void setNames()
   {
     this.offenName = this.player.getName();
     this.defenName = this.stageTemplate.name;
   }
 
   public boolean isPvP()
   {
     return false;
   }
 
   public void beforeCombat()
   {
     TowerRecord r = this.player.getTowerRecord();
     if (r.getLevel() < r.getMaxLevel())
       this.needCheck = false;
   }
 }
