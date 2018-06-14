 package org.darcy.sanguo.union.combat;
 
 import java.util.ArrayList;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.combat.CombatService;
import org.darcy.sanguo.combat.Section;
import org.darcy.sanguo.combat.Stage;
import org.darcy.sanguo.combat.Team;
import org.darcy.sanguo.combat.buff.Buff;
import org.darcy.sanguo.map.SectionTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.unit.Unit;
 
 public class TownStage extends Stage
 {
   private Player player;
   private Player target;
   private Pair pair;
   private City city;
   private Team buffTeam;
 
   public TownStage(int type, String location, String name, int senceId, Player player, Player target, Pair pair, City city)
   {
     super(type, location, name, senceId);
 
     this.pair = pair;
     this.player = player;
     this.city = city;
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
 
     int buffLeagueId = this.pair.getBuffLeagueId(this.city.getCountry());
     if (buffLeagueId != -1) {
       if (buffLeagueId == this.player.getUnion().getLeagueId())
         this.buffTeam = this.offen;
       else {
         this.buffTeam = ((Team)this.deffens.get(0));
       }
 
     }
 
     Buff buff = ((CombatService)Platform.getServiceManager().get(CombatService.class)).getBuff(((Integer)LeagueCombatService.countryBuffs.get(Integer.valueOf(this.city.getCountry()))).intValue());
     if (buff != null)
       for (Unit u : this.buffTeam.getUnits())
         if (u != null) {
           Buff bf = buff.copy();
           bf.setOwner(u);
           u.getBuffs().addBuff(bf);
         }
   }
 
   public void proccessReward(Player player)
   {
     int buffId = ((Integer)LeagueCombatService.countryBuffs.get(Integer.valueOf(this.city.getCountry()))).intValue();
     for (Unit u : this.buffTeam.getUnits())
       if (u != null)
         u.getBuffs().removeBuff(buffId);
   }
 
   public boolean isPvP()
   {
     return true;
   }
 
   public void setNames()
   {
     this.offenName = this.player.getName();
     this.defenName = this.target.getName();
   }
 
   public void combat(Player player)
   {
     Team deffen;
     for (int i = 0; i < this.deffens.size(); ++i) {
       this.recordUtil.prepareNewSection(i);
       this.recordUtil.newSection(i, this.offen);
       if (i == 0) {
         this.section = new Section(this);
         SectionTemplate sct = (SectionTemplate)this.sectionTemplates.get(0);
         this.section.init(sct.auto, this.offen, (Team)this.deffens.get(0));
         this.section.combat();
       } else {
         deffen = (Team)this.deffens.get(i);
         SectionTemplate st = (SectionTemplate)this.sectionTemplates.get(i);
         this.section.next(st.auto, deffen);
         this.section.combat();
       }
       if (!(this.section.isWin())) {
         break;
       }
     }
 
     this.offen.rest(Unit.RestType.STAGE);
     for (Team t : this.deffens) {
       t.rest(Unit.RestType.STAGE);
     }
 
     this.recordUtil.setResult(this.section.isWin());
     Platform.getLog().logCombat(getRecordUtil());
   }
 }
