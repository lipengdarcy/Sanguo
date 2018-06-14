package org.darcy.sanguo.union;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.arena.Arena;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;

import sango.packet.PbDown;
import sango.packet.PbLeague;

public class MyLeagueAsyncCall extends AsyncCall {
	Player player;
	League l;
	List<PbLeague.LeagueMember> members;

	public MyLeagueAsyncCall(Player player, League l) {
		super(player.getSession(), null);
		this.player = player;
		this.l = l;
	}

	public void callback() {
		PbDown.LeagueMyRst.Builder b = PbDown.LeagueMyRst.newBuilder().setResult(true);
		PbLeague.MyLeague.Builder mlb = PbLeague.MyLeague.newBuilder();
		mlb.setLeague(this.l.genLeague());
		mlb.setBuildValue(this.l.getBuildValue());
		mlb.setNotice(this.l.getNotice());
		mlb.setJob(PbLeague.LeagueJob.valueOf(this.l.getMember(this.player.getId()).getJob(this.l)));
		if (mlb.getJob().getNumber() != 3) {
			mlb.setIsApply(this.l.getApplyCount() > 0);
		}
		mlb.addAllMembers(this.members);
		mlb.setIsActivity(this.l.isActivity(this.l.getMember(this.player.getId())));
		b.setLeague(mlb.build());
		this.player.send(1180, b.build());
	}

	public void netOrDB()
   {
     long now = System.currentTimeMillis();
     long today = LeagueService.getToday0Time(now);
 
     this.members = new ArrayList();
     List<LeagueMember> list = new ArrayList(this.l.getInfo().getMembers().values());
     label458: for (LeagueMember member : list)
       try {
         PbLeague.LeagueMember.Builder b = PbLeague.LeagueMember.newBuilder();
         b.setId(member.getId());
         b.setJob(PbLeague.LeagueJob.valueOf(member.getJob(this.l)));
         b.setContribution(member.getTotalContribution());
         int noBuildDay = member.getNoBuildDay(today);
         b.setDay(noBuildDay);
         if (noBuildDay == -1) {
           LeagueBuildData data = LeagueService.getBuildData(member.getLastBuildId());
           if (data != null) {
             b.setBuild(data.cost.genPbReward());
           }
         }
         int btlCapability = 0;
         int rank = 0;
         Player player = Platform.getPlayerManager().getPlayerById(member.getId());
         if (player != null) {
           btlCapability = player.getBtlCapability();
           if (player.getArena() != null)
             rank = player.getArena().getRank();
           else {
             rank = -1;
           }
           b.setState(-1);
           b.setName(player.getName());
           b.setLevel(player.getLevel());
         } else {
           MiniPlayer mp = Platform.getPlayerManager().getMiniPlayer(member.getId());
           btlCapability = mp.getBtlCapability();
           long lastLogout = mp.getLastLogout().getTime();
           b.setState((int)((now - lastLogout) / 1000L / 60L));
           b.setName(mp.getName());
           b.setLevel(mp.getLevel());
           Arena arena = null;
           try {
             arena = (Arena)((DbService)Platform.getServiceManager().get(DbService.class)).get(Arena.class, Integer.valueOf(member.getId()));
           } catch (Exception e) {
             Platform.getLog().logError("MyLeagueAsyncCall get Arena exception, id:" + member.getId(), e);
           }
           if (arena == null)
             rank = -1;
           else {
             rank = arena.getRank();
           }
         }
         b.setBtlCapability(btlCapability);
         b.setRank(rank);
         this.members.add(b.build());
       } catch (Exception e) {
         Platform.getLog().logError("get my league error", e);
       }
   }
}
