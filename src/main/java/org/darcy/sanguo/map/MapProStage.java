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
 
 public class MapProStage extends Stage
 {
   public static final int MAX_ROUND = 30;
   private MapTemplate mapTemplate;
   private StageTemplate stageTemplate;
   private static final int channel = 0;
   private List<Gain> gains;
   private int money;
   private int wariorSpirit;
   private int exp;
   private Player player;
 
   public MapProStage(Team offen, MapTemplate mt, StageTemplate st, Player player)
   {
     super(1, st.channels[0].getPositionInfo(), st.name, st.secenId);
     this.mapTemplate = mt;
     this.offen = offen;
     this.stageTemplate = st;
     this.offen.setStage(this);
     this.player = player;
   }
 
   public void init() {
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
       this.money = sc.getMoney();
       player.addMoney(this.money, "mappro");
       this.wariorSpirit = sc.getWarriorSpirit();
       player.addWarriorSpirit(this.wariorSpirit, "mappro");
       this.exp = (player.getLevel() * 15);
       player.addExp(this.exp, "mappro");
       DropGroup drop = (DropGroup)DropService.dropGroups.get(Integer.valueOf(sc.getDropId()));
       if (drop != null) {
         this.gains = drop.genGains(player);
         if (this.gains != null) {
           for (Gain gain : this.gains) {
             gain.gain(player, "mappro");
           }
         }
       }
       MapRecord mr = player.getMapRecord();
       mr.setProMapChallengeTimes(mr.getProMapChallengeTimes() + 1);
       mr.addClearProMap(this.mapTemplate.id);
       if (!(mr.isOpenedProMap(this.mapTemplate.nextId))) {
         mr.refreshProMaps();
       }
 
       Platform.getEventManager().addEvent(new Event(2009, new Object[] { player }));
     }
     Platform.getLog().logPveFight(player, this.mapTemplate, this.stageTemplate, isWin(), 1, player.getMapRecord().getLeftProMapChallengeTimes());
   }
 
   public MapTemplate getMapTemplate() {
     return this.mapTemplate;
   }
 
   public StageTemplate getStageTemplate() {
     return this.stageTemplate;
   }
 
   public int getChannel() {
     return 0;
   }
 
   public void setMapTemplate(MapTemplate mapTemplate) {
     this.mapTemplate = mapTemplate;
   }
 
   public void setStageTemplate(StageTemplate stageTemplate) {
     this.stageTemplate = stageTemplate;
   }
 
   public List<Gain> getGains() {
     return this.gains;
   }
 
   public int getMoney() {
     return this.money;
   }
 
   public int getWariorSpirit() {
     return this.wariorSpirit;
   }
 
   public int getExp() {
     return this.exp;
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
     if (this.player.getMapRecord().hasClearProMap(this.mapTemplate.id))
       this.needCheck = false;
   }
 }
