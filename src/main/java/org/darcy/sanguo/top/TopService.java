package org.darcy.sanguo.top;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.arena.ArenaRankAsync;
import org.darcy.sanguo.boss.BossManager;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.TowerService;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.Union;
import org.darcy.sanguo.worldcompetition.WorldCompetition;
import org.darcy.sanguo.worldcompetition.WorldCompetitionService;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class TopService implements Service, PacketHandler {
	public static final int TOP_ARENA_MAX_COUNT = 50;
	public static final int TOP_COMPETITION_MAX_COUNT = 50;
	public static final int TOP_LEAGUE_MAX_COUNT = 50;

	public void startup() throws Exception {
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}

	public int[] getCodes() {
		return new int[] { 2211 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 2211:
			info(player, PbUp.RankInfo.parseFrom(packet.getData()).getRankType());
		}
	}

	private void info(Player player, int rankType) {
		switch (rankType) {
		case 2:
			infoBtl(player);
			break;
		case 3:
			infoLevel(player);
			break;
		case 4:
			ArenaRankAsync arena = new ArenaRankAsync(player);
			Thread t = new Thread(arena, "ArenaRankAsync");
			Platform.getThreadPool().execute(t);
			break;
		case 5:
			infoCopetition(player);
			break;
		case 1:
			infoMap(player);
			break;
		case 0:
			infoTower(player);
			break;
		case 6:
			infoLeague(player);
			break;
		case 7:
			infoBossLeague(player);
		}
	}

	private void infoLeague(Player player) {
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		int leagueId = -1;
		Union uion = player.getUnion();
		if (uion != null) {
			leagueId = uion.getLeagueId();
		}

		if (leagueId > 0) {
			League league = Platform.getLeagueManager().getLeagueById(leagueId);
			rst.setMyInfo(String.valueOf(league.getCostBuildValue()));
			rst.setMyRank(league.getRank());
		} else {
			rst.setMyInfo("您尚未加入军团!");
			rst.setMyRank(-1);
		}
		rst.setChange(0);

		List<League> list = Platform.getLeagueManager().getRank(50);
		for (League top : list) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			node.setIconId(-1);
			node.setInfo(MessageFormat.format("{0}-({1}/{2})", new Object[] { Integer.valueOf(top.getCostBuildValue()),
					Integer.valueOf(top.getMemberCount()), Integer.valueOf(top.getMemberLimit()) }));
			node.setLevel(top.getLevel());
			node.setName(top.getName());
			node.setPlayerId(-1);
			node.setRank(top.getRank());
			node.setInfoName(top.getMember(top.getLeader()).getName());
			rst.addNodes(node);
		}

		player.send(2212, rst.build());
	}

	private void infoBossLeague(Player player) {
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		int leagueId = -1;
		Union uion = player.getUnion();
		if (uion != null) {
			leagueId = uion.getLeagueId();
		}
		rst.setChange(0);

		boolean isRank = false;
		BossManager bm = Platform.getBossManager();
		List ranks = bm.getLeagueRanks();
		int count = 50;
		for (int i = 0; (i < ranks.size()) && (i < count); ++i) {
			int rank = i + 1;
			int id = ((Integer) ranks.get(i)).intValue();
			League l = Platform.getLeagueManager().getLeagueById(id);
			int damage = l.getInfo().getWorldBossDamage();
			if (damage > 0) {
				PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
				node.setIconId(-1);
				node.setInfo(MessageFormat.format("{0}-({1}/{2})", new Object[] { Integer.valueOf(damage),
						Integer.valueOf(l.getMemberCount()), Integer.valueOf(l.getMemberLimit()) }));
				node.setLevel(l.getLevel());
				node.setName(l.getName());
				node.setPlayerId(-1);
				node.setRank(rank);
				node.setInfoName(l.getMember(l.getLeader()).getName());
				rst.addNodes(node);

				if ((leagueId > 0) && (leagueId == l.getId())) {
					rst.setMyInfo(String.valueOf(l.getInfo().getWorldBossDamage()));
					rst.setMyRank(rank);
					isRank = true;
				}
			}
		}

		if (!(isRank)) {
			rst.setMyInfo("您尚未加入军团!");
			rst.setMyRank(-1);
		}

		player.send(2212, rst.build());
	}

	private void infoTower(Player player) {
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		rst.setMyInfo(String.valueOf(player.getTowerRecord().getMaxLevel()));
		rst.setMyRank(Platform.getTopManager().getRank(0, player.getId()));
		rst.setChange(0);

		List<Top> list = Platform.getTopManager().getRanks(0);
		for (Top top : list) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(top.getPid());
			if (mini == null) {
				continue;
			}
			node.setIconId(mini.getHeroList()[0]);
			node.setInfo(String.valueOf(top.getValue()));
			node.setLevel(mini.getLevel());
			node.setName(mini.getName());
			node.setPlayerId(mini.getId());
			node.setRank(top.getRank());
			League l = Platform.getLeagueManager().getLeagueByPlayerId(mini.getId());
			if (l != null) {
				node.setInfoName(l.getName());
			}
			node.setTitleId(TowerService.getTitleID(top.getValue()));
			rst.addNodes(node);
		}

		player.send(2212, rst.build());
	}

	private void infoMap(Player player) {
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		rst.setMyInfo(String.valueOf(player.getMapRecord().getTotalEarnedStars()));
		rst.setMyRank(Platform.getTopManager().getRank(1, player.getId()));
		rst.setChange(0);

		List<Top> list = Platform.getTopManager().getRanks(1);
		for (Top top : list) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(top.getPid());
			if (mini == null) {
				continue;
			}
			node.setIconId(mini.getHeroList()[0]);
			node.setInfo(String.valueOf(top.getValue()));
			node.setLevel(mini.getLevel());
			node.setName(mini.getName());
			node.setPlayerId(mini.getId());
			node.setRank(top.getRank());
			League l = Platform.getLeagueManager().getLeagueByPlayerId(mini.getId());
			if (l != null) {
				node.setInfoName(l.getName());
			}
			node.setTitleId(Platform.getTopManager().getTitleId(mini.getId()));
			rst.addNodes(node);
		}

		player.send(2212, rst.build());
	}

	private void infoLevel(Player player) {
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		rst.setMyInfo(String.valueOf(player.getLevel()));
		rst.setMyRank(Platform.getTopManager().getRank(3, player.getId()));
		rst.setChange(Platform.getTopManager().getChange(player.getId(), 3));

		List<Top> list = Platform.getTopManager().getRanks(3);
		for (Top top : list) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(top.getPid());
			if (mini == null) {
				continue;
			}
			node.setIconId(mini.getHeroList()[0]);
			node.setInfo(String.valueOf(top.getValue()));
			node.setLevel(mini.getLevel());
			node.setName(mini.getName());
			node.setPlayerId(mini.getId());
			node.setRank(top.getRank());
			League l = Platform.getLeagueManager().getLeagueByPlayerId(mini.getId());
			if (l != null) {
				node.setInfoName(l.getName());
			}
			node.setTitleId(Platform.getTopManager().getTitleId(mini.getId()));
			rst.addNodes(node);
		}

		player.send(2212, rst.build());
	}

	private void infoCopetition(Player player) {
		WorldCompetition competition;
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		WorldCompetition world = player.getWorldCompetition();
		if (world != null) {
			rst.setMyInfo(String.valueOf(player.getWorldCompetition().getScore()));
			rst.setMyRank(player.getWorldCompetition().getRank());
		} else {
			rst.setMyInfo("尚未开启");
			rst.setMyRank(-1);
		}
		rst.setChange(0);

		WorldCompetitionService ws = (WorldCompetitionService) Platform.getServiceManager()
				.get(WorldCompetitionService.class);
		List<WorldCompetition> list = new ArrayList();
		for (int i = 1; i <= 50; ++i) {
			competition = (WorldCompetition) ws.rankCompetitions.get(Integer.valueOf(i));
			if (competition != null) {
				list.add(competition);
			}
		}
		for (WorldCompetition cp : list) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(cp.getPlayerId());
			if (mini == null) {
				continue;
			}
			node.setIconId(mini.getHeroList()[0]);
			node.setInfo(String.valueOf(cp.getScore()));
			node.setLevel(mini.getLevel());
			node.setName(mini.getName());
			node.setPlayerId(mini.getId());
			node.setRank(cp.getRank());
			League l = Platform.getLeagueManager().getLeagueByPlayerId(mini.getId());
			if (l != null) {
				node.setInfoName(l.getName());
			}
			node.setTitleId(Platform.getTopManager().getTitleId(mini.getId()));
			rst.addNodes(node);
		}

		player.send(2212, rst.build());
	}

	private void infoBtl(Player player) {
		PbDown.RankInfoRst.Builder rst = PbDown.RankInfoRst.newBuilder();
		rst.setResult(true);

		rst.setMyInfo(String.valueOf(player.getBtlCapability()));
		rst.setMyRank(Platform.getTopManager().getRank(2, player.getId()));
		rst.setChange(Platform.getTopManager().getChange(player.getId(), 2));

		List<Top> list = Platform.getTopManager().getRanks(2);
		for (Top top : list) {
			PbCommons.RankNode.Builder node = PbCommons.RankNode.newBuilder();
			MiniPlayer mini = Platform.getPlayerManager().getMiniPlayer(top.getPid());
			if (mini == null) {
				continue;
			}
			node.setIconId(mini.getHeroList()[0]);
			node.setInfo(String.valueOf(top.getValue()));
			node.setLevel(mini.getLevel());
			node.setName(mini.getName());
			node.setPlayerId(mini.getId());
			node.setRank(top.getRank());
			League l = Platform.getLeagueManager().getLeagueByPlayerId(mini.getId());
			if (l != null) {
				node.setInfoName(l.getName());
			}
			node.setTitleId(Platform.getTopManager().getTitleId(mini.getId()));
			rst.addNodes(node);
		}

		player.send(2212, rst.build());
	}
}
