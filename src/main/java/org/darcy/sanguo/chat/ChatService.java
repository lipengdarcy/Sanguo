package org.darcy.sanguo.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.darcy.sanguo.Configuration;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.hero.HeroTemplate;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.hotfix.HotSwap;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.star.StarService;
import org.darcy.sanguo.union.League;
import org.darcy.sanguo.union.Union;
import org.darcy.sanguo.util.Calc;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class ChatService implements Service, PacketHandler {
	// 全服务器聊天信息
	protected BlockingQueue<ChatMessage> worldMessages = new LinkedBlockingQueue<ChatMessage>();
	// 联盟聊天信息
	protected BlockingQueue<LeagueChatMessage> leagueMessages = new LinkedBlockingQueue<LeagueChatMessage>();

	public void startup() {
		SendMessage sendMessage = new SendMessage();
		new Thread(sendMessage, "worldMessage").start();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public int[] getCodes() {
		return new int[] { 2141 };
	}

	private void gmCommond(Player player, String msg) {
		if (msg.contains("/gm ")) {
			String[] ls = msg.split(" ");
			if (ls[1].equals("maps")) {
				player.getMapRecord().openAll();
				player.getDestinyRecord().setLeftStars(500);
			} else {
				Warrior w;
				if (ls[1].equals("heros")) {
					for (BagGrid grid : player.getBags().getBag(2).getGrids())
						if ((grid != null) && (grid.getCount() > 0)) {
							w = (Warrior) grid.getItem();
							w.setAdvanceLevel(((HeroTemplate) w.getTemplate()).advanceRule.size());
							w.setLevel(70);
						}
				} else if (ls[1].equals("nb")) {
					nb(player);
				} else if (ls[1].equals("item")) {
					int id = Integer.parseInt(ls[2]);
					int count = Integer.parseInt(ls[3]);
					player.getBags().addItem(ItemService.generateItem(id, player), count, "gmchat");
				} else {
					int value;
					if (ls[1].equals("cost")) {
						int type = Integer.parseInt(ls[2]);
						value = Integer.parseInt(ls[3]);
						if (type == 1)
							player.decJewels(value, "gmchat");
						else if (type == 2)
							player.decMoney(value, "gmchat");
						else if (type == 3)
							player.decVitality(value, "gmchat");
						else if (type == 4)
							player.decStamina(value, "gmchat");
					} else if (ls[1].equals("charge")) {
						boolean type = Boolean.parseBoolean(ls[2]);
						value = Integer.parseInt(ls[3]);
						player.addCharge(value, type);
						player.addJewels(value, "gmchat");
					} else if (ls[1].equals("run")) {
						HotSwap.runThread(ls[2]);
					} else if (ls[1].equals("define")) {
						HotSwap.redefineClass(ls[2]);
					} else {
						String message = "/gm maps   /gm heros /gm nb";
						ChatMessage m = new ChatMessage(PbCommons.ChatType.WORLD, -1, "帮助", message);
						this.worldMessages.offer(m);
					}
				}
			}
		}
	}

	public static void nb(Player player) {
		player.setLevel(80);
		Object[] heroIds = StarService.stars.keySet().toArray();
		Integer[] rands = Calc.randomGet(heroIds.length, 5);
		Warrior warrior = player.getWarriors().getMainWarrior();
		warrior.setAdvanceLevel(((HeroTemplate) warrior.getTemplate()).advanceRule.size());
		warrior.setLevel(80);
		for (int i = 0; i < rands.length; ++i) {
			int id = ((Integer) heroIds[rands[i].intValue()]).intValue();
			warrior = (Warrior) ItemService.generateItem(id, player);
			warrior.setAdvanceLevel(((HeroTemplate) warrior.getTemplate()).advanceRule.size());
			warrior.setLevel(80);
			player.getBags().addItem(warrior, 1, "gmchat");

			int index = i + 2;
			Warrior old = player.getWarriors().addWarrior(warrior, index);
			int standIndex = player.getWarriors().getStandIndex(warrior);
			if (old != null) {
				Equipment[] equips = old.getEquips().getEquips();
				for (Equipment equip : equips) {
					if (equip != null) {
						warrior.getEquips().equip(equip, warrior);
					}
				}
				Treasure[] treasures = old.getTreasures().getTreasures();
				for (Treasure treasure : treasures) {
					if (treasure != null) {
						warrior.getTreasures().equip(treasure, warrior);
					}
				}
				old.refreshkEns(new Warrior[] { old });
				player.getTacticRecord().removeBuff(old, standIndex);
				old.refreshAttributes(true);
			}
			player.getTacticRecord().addBuff(warrior, standIndex);
			player.getWarriors().refresh(true);
			player.getDataSyncManager().addStandsSync(1, player.getWarriors());
			player.getDataSyncManager().addStandsSync(2, player.getWarriors());

			Platform.getEventManager().addEvent(new Event(2023, new Object[] { player }));
		}

		player.refreshBtlCapa();
	}

	private void send(Player player, PbCommons.ChatType type, String message) {
		PbDown.ChatSendRst.Builder rst = PbDown.ChatSendRst.newBuilder();
		rst.setResult(false);

		if (Configuration.test) {
			gmCommond(player, message);
		}

		if ((message.trim().length() == 0) || (message.length() > 100)) {
			rst.setErrInfo("发送信息长度必须介于1~100字符之间");
			player.send(2142, rst.build());
			return;
		}
		Cache cache = Platform.getEntityManager().getCacheManager().getCache("chatCD");
		if (cache.get(Integer.valueOf(player.getId())) != null) {
			rst.setErrInfo("信息发送过于频繁，请稍后再试");
			player.send(2142, rst.build());
			return;
		}
		cache.put(new Element(Integer.valueOf(player.getId()), Long.valueOf(System.currentTimeMillis())));

		message = Platform.getKeyWordManager().mark("blackword.txt", message);

		if (type == PbCommons.ChatType.WORLD) {
			if (player.getLevel() >= 10)
				if ((player.getLevel() >= 30) || (player.getPool().getInt(4, 0) < 5)) {
					if (player.getLevel() < 30) {
						player.getPool().set(4, Integer.valueOf(player.getPool().getInt(4, 0) + 1));
					}
					ChatMessage msg = new ChatMessage(type, player.getId(), player.getName(), message);
					boolean r = this.worldMessages.offer(msg);
					rst.setResult(r);
				} else {
					rst.setErrInfo("30级以下玩家，每天发送消息数不能超过5条");
				}
			else
				rst.setErrInfo("主角达到10级就能发世界消息啦");
		} else if (type == PbCommons.ChatType.TEAM) {
			Union u = player.getUnion();
			if (u != null) {
				LeagueChatMessage msg = new LeagueChatMessage(type, player.getId(), player.getName(), message,
						u.getLeagueId());
				boolean r = this.leagueMessages.offer(msg);
				rst.setResult(r);
			} else {
				rst.setErrInfo("您尚未加入军团!");
			}
		}

		player.send(2142, rst.build());
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 2141:
			PbUp.ChatSend send = PbUp.ChatSend.parseFrom(packet.getData());
			send(player, send.getType(), send.getMessage().trim());
		}
	}

	public void reload() throws Exception {
		this.worldMessages.clear();
	}

	class SendMessage implements Runnable {
		public void run() {
			try {
				ChatMessage message = null;
				if (ChatService.this.worldMessages.size() > 0) {
					PbDown.ChatMessageRst.Builder rst = PbDown.ChatMessageRst.newBuilder();
					while ((message = (ChatMessage) ChatService.this.worldMessages.poll()) != null) {
						PbCommons.ChatMessage.Builder msg = PbCommons.ChatMessage.newBuilder();
						msg.setMessage(message.message).setSenderId(message.sourceId).setSenderName(message.sourceName)
								.setType(message.type);
						rst.addMessages(msg);
					}

					for (Player p : Platform.getPlayerManager().players.values()) {
						if (p != null) {
							p.send(2144, rst.build());
						}
					}
				}

				LeagueChatMessage lcm = null;
				if (ChatService.this.leagueMessages.size() > 0) {
					List list;
					Map<Integer, Object> map = new HashMap();
					while ((lcm = (LeagueChatMessage) ChatService.this.leagueMessages.poll()) != null) {
						int leagueId = lcm.leagueId;
						list = (List) map.get(Integer.valueOf(leagueId));
						if (list == null) {
							list = new ArrayList();
							map.put(Integer.valueOf(leagueId), list);
						}
						PbCommons.ChatMessage.Builder msg = PbCommons.ChatMessage.newBuilder();
						msg.setMessage(lcm.message).setSenderId(lcm.sourceId).setSenderName(lcm.sourceName)
								.setType(lcm.type);
						list.add(msg.build());
					}

					if (map.size() > 0) {
						for (Integer leagueId : map.keySet()) {
							list = (List) map.get(leagueId);
							if (list.size() > 0) {
								PbDown.ChatMessageRst.Builder rst = PbDown.ChatMessageRst.newBuilder();
								rst.addAllMessages(list);

								League l = Platform.getLeagueManager().getLeagueById(leagueId.intValue());
								if (l == null)
									continue;
								try {
									Set<Integer> ids = l.getInfo().getMembers().keySet();
									for (Integer id : ids) {
										Player p = Platform.getPlayerManager().getPlayerById(id.intValue());
										if (p != null)
											p.send(2144, rst.build());
									}
								} catch (Exception e) {
									Platform.getLog().logWarn("League Chat, get members exception:" + e);
								}
							}
						}
					}

				}

				Thread.sleep(50L);
			} catch (InterruptedException e) {
				Platform.getLog().logError(e);
			}
		}
	}
}
