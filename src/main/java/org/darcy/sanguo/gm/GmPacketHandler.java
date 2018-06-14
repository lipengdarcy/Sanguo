package org.darcy.sanguo.gm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.account.BanAccount;
import org.darcy.sanguo.account.BanCharge;
import org.darcy.sanguo.account.BanIp;
import org.darcy.sanguo.asynccall.CallBackable;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.hotfix.HotSwap;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.item.Equipment;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.item.Treasure;
import org.darcy.sanguo.mail.GlobalMail;
import org.darcy.sanguo.notice.Notice;
import org.darcy.sanguo.pay.PayCompensationAsyncCall;
import org.darcy.sanguo.pay.Receipt;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PlayerSaveCall;
import org.darcy.sanguo.randomshop.CherishDiscount;
import org.darcy.sanguo.reward.TimeLimitItem;
import org.darcy.sanguo.reward.TimeLimitReward;
import org.darcy.sanguo.service.MailService;
import org.darcy.sanguo.service.NoticeService;
import org.darcy.sanguo.service.PayService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.DBUtil;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import io.netty.channel.ChannelHandlerContext;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import sango.packet.PbGm;
import sango.packet.PbPacket;

public class GmPacketHandler {

	public static void handlePacket(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		switch (packet.getPtCode()) {
		case 206:
			getUserInfo(ctx, packet);
			break;
		case 208:
			addItem(ctx, packet);
			break;
		case 210:
			getOnlinePlayer(ctx);
			break;
		case 212:
			getBag(ctx, packet);
			break;
		case 214:
			getEhCacheInfo(ctx, packet);
			break;
		case 216:
			shutdownEhcache(ctx);
			break;
		case 218:
			kickout(ctx, packet);
			break;
		case 220:
			getNoticeInfo(ctx);
			break;
		case 222:
			updateNotice(ctx, packet);
			break;
		case 224:
			removeNotice(ctx, packet);
			break;
		case 226:
			boardCast(ctx, packet);
			break;
		case 228:
			redefine(ctx, packet);
			break;
		case 230:
			runThread(ctx, packet);
			break;
		case 232:
			banAccountInfo(ctx);
			break;
		case 234:
			banAccount(ctx, packet);
			break;
		case 236:
			banAccountRemove(ctx, packet);
			break;
		case 238:
			banIpInfo(ctx);
			break;
		case 240:
			banIp(ctx, packet);
			break;
		case 242:
			banIpRemove(ctx, packet);
			break;
		case 244:
			compesation(ctx, packet);
			break;
		case 246:
			decItem(ctx, packet);
			break;
		case 248:
			banChargeInfo(ctx);
			break;
		case 250:
			banCharge(ctx, packet);
			break;
		case 252:
			banChargeRemove(ctx, packet);
			break;
		case 254:
			getReceiptInfo(ctx, packet);
			break;
		case 256:
			payCompensation(ctx, packet);
			break;
		case 258:
			serverDataInfo(ctx);
			break;
		case 260:
			serverDataUpdate(ctx, packet);
			break;
		case 262:
			addTimeLimitReward(ctx, packet);
			break;
		case 264:
			timeLimitRewardInfo(ctx);
			break;
		case 266:
			removeTimeLimitReward(ctx, packet);
			break;
		case 268:
			cherishDiscount(ctx, packet);
			break;
		case 281:
			addRewardJP(ctx, packet);
		case 207:
		case 209:
		case 211:
		case 213:
		case 215:
		case 217:
		case 219:
		case 221:
		case 223:
		case 225:
		case 227:
		case 229:
		case 231:
		case 233:
		case 235:
		case 237:
		case 239:
		case 241:
		case 243:
		case 245:
		case 247:
		case 249:
		case 251:
		case 253:
		case 255:
		case 257:
		case 259:
		case 261:
		case 263:
		case 265:
		case 267:
		case 269:
		case 270:
		case 271:
		case 272:
		case 273:
		case 274:
		case 275:
		case 276:
		case 277:
		case 278:
		case 279:
		case 280:
		}
	}

	private static void addRewardJP(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.AddRewardJPServer.Builder builder = PbGm.AddRewardJPServer.newBuilder();
		builder.setResult(false).setMsg("添加失败（限购ID重复，或添加物品总数量已达5个，或该限时礼包已结束）");
		try {
			PbGm.AddRewardJPGM req = PbGm.AddRewardJPGM.parseFrom(packet.getData());
			List list = Platform.getEntityManager().getAllFromEhCache(TimeLimitReward.class.getName());
			if (list != null) {
				for (Iterator localIterator1 = list.iterator(); localIterator1.hasNext();) {
					Object obj = localIterator1.next();
					TimeLimitReward reward = (TimeLimitReward) obj;
					if ((!(reward.isInTimeByActivate(System.currentTimeMillis()))) || (reward.id != req.getId())
							|| (reward.items.containsKey(Integer.valueOf(req.getSubId())))
							|| (reward.items.size() >= 5))
						continue;
					TimeLimitItem item = new TimeLimitItem();
					item.id = req.getSubId();
					item.price = req.getPrice();
					item.count = req.getCount();

					PbGm.AddItem addItem = req.getItem();
					int money = addItem.getMoney();
					int jewel = addItem.getJewel();
					int exp = addItem.getExp();
					int vitality = addItem.getVitality();
					int stamina = addItem.getStamina();
					int warriorSpirit = addItem.getWarriorSpirit();
					int spiritJade = addItem.getSpiritJade();
					int honor = addItem.getHonor();
					int prestige = addItem.getPrestige();
					List<PbGm.AddItemInfo> itemList = addItem.getItemsList();
					List rewards = new ArrayList();
					if (money > 0) {
						rewards.add(new Reward(2, money, null));
					}
					if (jewel > 0) {
						rewards.add(new Reward(3, jewel, null));
					}
					if (exp > 0) {
						rewards.add(new Reward(4, exp, null));
					}
					if (vitality > 0) {
						rewards.add(new Reward(5, vitality, null));
					}
					if (stamina > 0) {
						rewards.add(new Reward(6, stamina, null));
					}
					if (warriorSpirit > 0) {
						rewards.add(new Reward(8, warriorSpirit, null));
					}
					if (spiritJade > 0) {
						rewards.add(new Reward(7, spiritJade, null));
					}
					if (honor > 0) {
						rewards.add(new Reward(9, honor, null));
					}
					if (prestige > 0) {
						rewards.add(new Reward(10, prestige, null));
					}

					for (PbGm.AddItemInfo addItemInfo : itemList) {
						if (addItemInfo.getNum() > 0) {
							ItemTemplate template = ItemService.getItemTemplate(addItemInfo.getId());
							if ((!(Item.isCumulative(template.type))) && (addItemInfo.getNum() > 100)) {
								continue;
							}
							rewards.add(new Reward(0, addItemInfo.getNum(), template));
						}
					}

					if ((rewards.size() > 0) && (rewards.size() < 2)) {
						item.rewards = rewards;
						reward.items.put(Integer.valueOf(item.id), item);
						Platform.getEntityManager().putInEhCache(TimeLimitReward.class.getName(),
								Long.valueOf(reward.id), reward);
						builder.setResult(true).setMsg("添加成功");
					} else {
						builder.setResult(false).setMsg("添加失败，添加的道具数量必须为1个！");
					}
				}
			}
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false).setMsg("数据异常！");
		}

		send(ctx, 282, builder.build());
	}

	private static void addTimeLimitReward(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.AddTimeLimitRewardServer.Builder builder = PbGm.AddTimeLimitRewardServer.newBuilder();
		builder.setResult("success");
		try {
			PbGm.AddTimeLimitRewardGM req = PbGm.AddTimeLimitRewardGM.parseFrom(packet.getData());
			int count = req.getCount();
			long start = req.getStart();
			long end = req.getEnd();
			int last = req.getLast();
			String name = req.getName();
			int targetType = req.getTargetType();
			String targets = req.getTargets();
			int origPrice = req.getOrigPrice();
			int salePrice = req.getSalePrice();

			if ((count < 0) || (last < 0) || (start >= end)) {
				builder.setResult("时间设置有误");
				send(ctx, 261, builder.build());
				return;
			}
			long now = System.currentTimeMillis();
			if (end < now) {
				builder.setResult("结束时间已过，无法激活礼包");
				send(ctx, 261, builder.build());
				return;
			}

			List list = Platform.getEntityManager().getAllFromEhCache(TimeLimitReward.class.getName());
			if (list != null) {
				for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
					Object obj = localIterator.next();
					TimeLimitReward reward = (TimeLimitReward) obj;
					if ((start <= reward.end + reward.lastTime) && (end + last * 60 * 1000 >= reward.start)) {
						builder.setResult("礼包时间与已有礼包重复，请检查");
						send(ctx, 261, builder.build());
						return;
					}
				}
			}

			TimeLimitReward tlr = new TimeLimitReward();
			tlr.count = count;
			tlr.end = end;
			tlr.start = start;
			tlr.name = name;
			tlr.lastTime = (last * 60 * 1000);
			tlr.targetType = targetType;
			if (targetType == 1) {
				String[] ts = targets.split(",");
				tlr.targets = new ArrayList(ts.length);
				for (int i = 0; i < ts.length; ++i) {
					tlr.targets.add(Integer.valueOf(Integer.parseInt(ts[i])));
				}
			}
			tlr.origPrice = origPrice;
			tlr.salePrice = salePrice;

			builder.setId(tlr.id);
			int exist = (int) ((end - now) / 1000L + last * 60);
			Platform.getEntityManager().putInEhCache(TimeLimitReward.class.getName(), Long.valueOf(tlr.id), tlr, exist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		send(ctx, 261, builder.build());
	}

	private static void timeLimitRewardInfo(ChannelHandlerContext ctx) {
		PbGm.TimeLimitRewardInfoServer.Builder b = PbGm.TimeLimitRewardInfoServer.newBuilder();
		List list = Platform.getEntityManager().getAllFromEhCache(TimeLimitReward.class.getName());
		for (Iterator localIterator1 = list.iterator(); localIterator1.hasNext();) {
			Object obj = localIterator1.next();
			TimeLimitReward tlr = (TimeLimitReward) obj;
			PbGm.GmTimeLimitReward.Builder gb = PbGm.GmTimeLimitReward.newBuilder();
			gb.setId(tlr.id).setStart(tlr.start).setEnd(tlr.end).setLast((int) (tlr.lastTime / 1000L / 60L))
					.setCount(tlr.count).setTargets(tlr.targets.toString()).setGoosId(-1)
					.setReward(tlr.rewards.toString()).setName(tlr.name).setTargetType(tlr.targetType)
					.setSalePrice(tlr.salePrice).setOrigPrice(tlr.origPrice);
			for (TimeLimitItem item : tlr.items.values()) {
				gb.addItems(item.genPbTimeLimitReward());
			}
			b.addInfos(gb.build());
		}
		send(ctx, 263, b.build());
	}

	private static void removeTimeLimitReward(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		long id;
		PbGm.TimeLimitRewardRemoveServer.Builder b = PbGm.TimeLimitRewardRemoveServer.newBuilder();
		try {
			PbGm.TimeLimitRewardRemoveGM req = PbGm.TimeLimitRewardRemoveGM.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("删除限时礼包失败!");
			send(ctx, 265, b.build());
			return;
		}
		Platform.getEntityManager().deleteFromEhCache(TimeLimitReward.class.getName(), Long.valueOf(id));
		b.setResult("success");
		send(ctx, 265, b.build());
	}

	private static void send(ChannelHandlerContext ctx, int ptCode, GeneratedMessage msg) {
		PbPacket.Packet opt = PbPacket.Packet.newBuilder().setPtCode(ptCode).setData(msg.toByteString()).build();
		ctx.writeAndFlush(opt);
	}

	private static void getUserInfo(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.GetUserInfoRst.Builder rstBuilder = PbGm.GetUserInfoRst.newBuilder();
		try {
			Player player;
			PbGm.PlayerInfo.Builder info;
			PbGm.GetUserInfo getUserInfo = PbGm.GetUserInfo.parseFrom(packet.getData());
			int pageIndex = getUserInfo.getIndex();
			int pageSize = getUserInfo.getSize();
			if (getUserInfo.getType() == PbGm.GetUserInfo.GetType.BYPLAYERID) {
				int playerId = getUserInfo.getId();
				boolean online = true;
				player = Platform.getPlayerManager().getPlayerById(playerId);
				if (player == null) {
					online = false;

					player = Platform.getPlayerManager().getPlayer(playerId, true, false);
				}
				if (player != null) {
					info = PbGm.PlayerInfo.newBuilder().setAccountId(player.getAccountId()).setId(playerId)
							.setName(player.getName()).setLevel(player.getLevel()).setExp(player.getExp())
							.setVitality(player.getVitality()).setMoney(player.getMoney()).setJewel(player.getJewels())
							.setWarriorSpirit(player.getWarriorSpirit()).setSpiritJade(player.getSpiritJade())
							.setOnline(online).setVipLevel(player.getVip().level).setStamina(player.getStamina())
							.setTrainPoint(player.getPool().getInt(22, 0));
					if (online)
						info.setIp(player.getSession().getIp());
					else {
						info.setIp("-1");
					}
					rstBuilder.addInfos(info);
				}
				rstBuilder.setCount((player == null) ? 0 : 1);
				send(ctx, 205, rstBuilder.build());
				return;
			}
			if (getUserInfo.getType() == PbGm.GetUserInfo.GetType.BYNAME) {
				String blurName = getUserInfo.getName();
				List list = DBUtil.getPlayerByBlurName(blurName, pageIndex, pageSize);
				rstBuilder.setCount(DBUtil.getPlayerCountByBlurName(blurName));

				for (Iterator it = list.iterator(); it.hasNext();) {
					player = (Player) it.next();

					PbGm.PlayerInfo a = PbGm.PlayerInfo.newBuilder().setId(player.getId())
							.setAccountId(player.getAccountId()).setName(player.getName()).setLevel(player.getLevel())
							.setExp(player.getExp()).setVitality(player.getVitality()).setMoney(player.getMoney())
							.setJewel(player.getJewels()).setWarriorSpirit(player.getWarriorSpirit())
							.setSpiritJade(player.getSpiritJade()).setVipLevel(player.getVip().level)
							.setStamina(player.getStamina()).setTrainPoint(player.getPool().getInt(22, 0)).build();
					rstBuilder.addInfos(a);
				}
				send(ctx, 205, rstBuilder.build());
				return;
			}
			if (getUserInfo.getType() == PbGm.GetUserInfo.GetType.ONLINE) {
				Iterator itx = Platform.getPlayerManager().players.keySet().iterator();
				rstBuilder.setCount(Platform.getPlayerManager().players.size());
				int count = 0;
				int start = pageIndex * pageSize;
				int end = start + pageSize;
				while (itx.hasNext()) {
					player = Platform.getPlayerManager().getPlayerById(((Integer) itx.next()).intValue());
					if (player != null) {
						if ((count >= start) && (count < end)) {
							info = PbGm.PlayerInfo.newBuilder().setId(player.getId())
									.setAccountId(player.getAccountId()).setName(player.getName())
									.setLevel(player.getLevel()).setExp(player.getExp())
									.setVitality(player.getVitality()).setMoney(player.getMoney())
									.setJewel(player.getJewels()).setWarriorSpirit(player.getWarriorSpirit())
									.setSpiritJade(player.getSpiritJade()).setOnline(true)
									.setVipLevel(player.getVip().level).setStamina(player.getStamina())
									.setTrainPoint(player.getPool().getInt(22, 0));
							if (player.getSession() != null) {
								info.setIp(player.getSession().getIp());
							}
							rstBuilder.addInfos(info);
						}
						++count;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		send(ctx, 205, rstBuilder.build());
	}

	private static void getOnlinePlayer(ChannelHandlerContext ctx) {
		PbGm.GetOnlinePlayerRst.Builder builder = PbGm.GetOnlinePlayerRst.newBuilder();
		Iterator itx = Platform.getPlayerManager().players.keySet().iterator();
		while (itx.hasNext()) {
			Player player = Platform.getPlayerManager().getPlayerById(((Integer) itx.next()).intValue());
			if (player != null) {
				PbGm.PlayerInfo info = PbGm.PlayerInfo.newBuilder().setId(player.getId())
						.setAccountId(player.getAccountId()).setName(player.getName()).build();
				builder.addPlayers(info);
			}
		}
		send(ctx, 209, builder.build());
	}

	private static void addItem(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.AddItemServer.Builder builder = PbGm.AddItemServer.newBuilder();
		builder.setResult(true);
		List errorIdList = new ArrayList();
		List ids = new ArrayList();
		try {
			PbGm.AddItemGm req = PbGm.AddItemGm.parseFrom(packet.getData());
			List idList = req.getIdsList();
			List<String> nameList = req.getNamesList();
			int type = req.getMail().getType().getNumber();
			String title = req.getMail().getTitle();
			String content = req.getMail().getContent();

			PbGm.AddItem addItem = req.getItem();
			int money = addItem.getMoney();
			int jewel = addItem.getJewel();
			int exp = addItem.getExp();
			int vitality = addItem.getVitality();
			int stamina = addItem.getStamina();
			int warriorSpirit = addItem.getWarriorSpirit();
			int spiritJade = addItem.getSpiritJade();
			int honor = addItem.getHonor();
			int prestige = addItem.getPrestige();
			List<PbGm.AddItemInfo> itemList = addItem.getItemsList();

			List rewards = new ArrayList();
			if (money > 0) {
				rewards.add(new Reward(2, money, null));
			}
			if (jewel > 0) {
				rewards.add(new Reward(3, jewel, null));
			}
			if (exp > 0) {
				rewards.add(new Reward(4, exp, null));
			}
			if (vitality > 0) {
				rewards.add(new Reward(5, vitality, null));
			}
			if (stamina > 0) {
				rewards.add(new Reward(6, stamina, null));
			}
			if (warriorSpirit > 0) {
				rewards.add(new Reward(8, warriorSpirit, null));
			}
			if (spiritJade > 0) {
				rewards.add(new Reward(7, spiritJade, null));
			}
			if (honor > 0) {
				rewards.add(new Reward(9, honor, null));
			}
			if (prestige > 0) {
				rewards.add(new Reward(10, prestige, null));
			}
			for (PbGm.AddItemInfo addItemInfo : itemList) {
				if (addItemInfo.getNum() > 0) {
					ItemTemplate template = ItemService.getItemTemplate(addItemInfo.getId());
					if ((!(Item.isCumulative(template.type))) && (addItemInfo.getNum() > 100)) {
						continue;
					}
					rewards.add(new Reward(0, addItemInfo.getNum(), template));
				}
			}
			if ((idList != null) && (idList.size() > 0)) {
				ids.addAll(idList);
			}
			if ((nameList != null) && (nameList.size() > 0)) {
				for (String name : nameList) {
					Player p = DBUtil.getPlayerByName(name);
					if (p != null) {
						ids.add(Integer.valueOf(p.getId()));
					}
				}
			}
			if (ids.size() > 0) {
				int mailType = (type == 2) ? 3 : (type == 1) ? 2 : 1;
				new AddAwardsThread(ids, rewards, mailType, title, content).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (errorIdList.size() > 0) {
			builder.addAllIds(errorIdList);
		}
		send(ctx, 207, builder.build());
	}

	private static void getBag(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.GmGetBagRst.Builder builder = PbGm.GmGetBagRst.newBuilder();
		try {
			PbGm.GmGetBag getBag = PbGm.GmGetBag.parseFrom(packet.getData());
			int type = getBag.getBagType();
			builder.setBagType(type);
			int playerId = getBag.getId();

			Player player = Platform.getPlayerManager().getPlayerById(playerId);
			if (player == null) {
				player = Platform.getPlayerManager().getPlayer(playerId, true, true);
			}
			if (player != null) {
				List grids;
				BagGrid bagGrid;
				Iterator localIterator;
				if (type == 0) {
					grids = player.getBags().getBag(type).getGrids();
					for (localIterator = grids.iterator(); localIterator.hasNext();) {
						bagGrid = (BagGrid) localIterator.next();
						if ((bagGrid != null) && (bagGrid.getItem() != null)) {
							PbGm.NormalItemInfo.Builder builder2 = PbGm.NormalItemInfo.newBuilder();
							builder2.setName(bagGrid.getItem().getName());
							builder2.setCount(bagGrid.getCount());
							builder2.setTemplate(bagGrid.getItem().getTemplateId());
							builder.addNormals(builder2.build());
						}
					}
				} else if (type == 2) {
					grids = player.getBags().getBag(type).getGrids();
					Collections.sort(grids, new Comparator<BagGrid>() {
						public int compare(BagGrid o1, BagGrid o2) {
							Warrior hero1 = (Warrior) o1.getItem();
							Warrior hero2 = (Warrior) o2.getItem();
							if (hero1.getStageStatus() == hero2.getStageStatus()) {
								if (hero1.getId() > hero2.getId()) {
									return 1;
								}
								return -1;
							}
							if (hero1.getStageStatus() == 1)
								return -1;
							if (hero1.getStageStatus() == 0) {
								return 1;
							}
							if (hero2.getStageStatus() == 1) {
								return 1;
							}
							return -1;
						}
					});
					for (localIterator = grids.iterator(); localIterator.hasNext();) {
						bagGrid = (BagGrid) localIterator.next();
						if ((bagGrid != null) && (bagGrid.getItem() != null)) {
							Warrior hero = (Warrior) bagGrid.getItem();
							PbGm.HeroInfo.Builder builder2 = PbGm.HeroInfo.newBuilder();
							builder2.setId(hero.getId());
							builder2.setName(hero.getName());
							builder2.setExp((hero.isMainWarrior()) ? player.getExp() : hero.getExp());
							builder2.setLevel(hero.getLevel());
							builder2.setAdvanceLevel(hero.getAdvanceLevel());
							builder2.setQuality(hero.getTemplate().quality);
							builder2.setStageStatus(hero.getStageStatus());
							builder2.setTemplate(hero.getTemplateId());
							builder.addHeros(builder2.build());
						}
					}
				} else if (type == 3) {
					grids = player.getBags().getBag(type).getGrids();
					for (localIterator = grids.iterator(); localIterator.hasNext();) {
						bagGrid = (BagGrid) localIterator.next();
						if ((bagGrid != null) && (bagGrid.getItem() != null)) {
							PbGm.DebrisInfo.Builder builder2 = PbGm.DebrisInfo.newBuilder();
							builder2.setName(bagGrid.getItem().getName());
							builder2.setCount(bagGrid.getCount());
							builder2.setTemplate(bagGrid.getItem().getTemplateId());
							builder2.setType(((DebrisTemplate) bagGrid.getItem().getTemplate()).debrisType);
							builder.addDebris(builder2.build());
						}
					}
				} else if (type == 4) {
					grids = player.getBags().getBag(type).getGrids();
					for (localIterator = grids.iterator(); localIterator.hasNext();) {
						bagGrid = (BagGrid) localIterator.next();
						if ((bagGrid != null) && (bagGrid.getItem() != null)) {
							Equipment equip = (Equipment) bagGrid.getItem();
							PbGm.EquipInfo.Builder builder2 = PbGm.EquipInfo.newBuilder();
							builder2.setId(equip.getId());
							builder2.setTemplate(equip.getTemplateId());
							builder2.setName(equip.getName());
							builder2.setLevel(equip.getLevel());
							builder2.setQuality(equip.getTemplate().quality);
							if (equip.getWarriorId() > 0)
								builder2.setWarrior(player.getBags().getItemById(equip.getWarriorId(), 2).getName()
										+ "(" + equip.getWarriorId() + ")");
							else {
								builder2.setWarrior("-");
							}
							builder2.setBaseAttr(equip.getBaseAttr().genAttribute().toString());
							builder2.setPolishAttr(Attributes.genAttribute(equip.getPolishAttr()).toString());
							builder.addEquips(builder2.build());
						}
					}
				} else if (type == 1) {
					grids = player.getBags().getBag(type).getGrids();
					for (localIterator = grids.iterator(); localIterator.hasNext();) {
						bagGrid = (BagGrid) localIterator.next();
						if ((bagGrid != null) && (bagGrid.getItem() != null)) {
							Treasure t = (Treasure) bagGrid.getItem();
							PbGm.TreasureInfo.Builder builder2 = PbGm.TreasureInfo.newBuilder();
							builder2.setId(t.getId());
							builder2.setTemplate(t.getTemplateId());
							builder2.setName(t.getName());
							builder2.setLevel(t.getLevel());
							builder2.setQuality(t.getTemplate().quality);
							if (t.getWarriorId() > 0)
								builder2.setWarrior(player.getBags().getItemById(t.getWarriorId(), 2).getName() + "("
										+ t.getWarriorId() + ")");
							else {
								builder2.setWarrior("-");
							}
							Attributes attr = new Attributes();
							Attributes.addAttr(new Attributes[] { attr, Attributes.newAttributes(t.getAttr()),
									Attributes.newAttributes(t.getExtraAttr()),
									Attributes.newAttributes(t.getEnhanceAttr()) });
							builder2.setAttr(attr.genAttribute().toString());
							builder2.setEnhanceLevel(t.getEnhanceLevel());
							builder.addTreasures(builder2.build());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		send(ctx, 211, builder.build());
	}

	private static void getEhCacheInfo(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.GetEhCacheInfoRst.Builder builder = PbGm.GetEhCacheInfoRst.newBuilder();
		try {
			PbGm.GetEhCacheInfo info = PbGm.GetEhCacheInfo.parseFrom(packet.getData());
			int pageIndex = info.getIndex();
			int pageSize = info.getSize();
			CacheManager cacheManager = Platform.getEntityManager().getCacheManager();
			String[] cacheNames = cacheManager.getCacheNames();
			int count = 0;
			int start = pageIndex * pageSize;
			int end = start + pageSize;
			for (String str : cacheNames) {
				Ehcache cache = Platform.getEntityManager().getEhCache(str);
				if (cache != null) {
					List keys = cache.getKeys();
					for (Iterator localIterator = keys.iterator(); localIterator.hasNext();) {
						Object obj = localIterator.next();
						Element element = cache.get(obj);
						if (element != null) {
							if ((start <= count) && (count < end)) {
								PbGm.EhCacheInfo cacheInfo = PbGm.EhCacheInfo.newBuilder().setCacheName(str)
										.setKey(element.getObjectKey().toString())
										.setValue(element.getObjectValue().toString()).build();
								builder.addInfos(cacheInfo);
							}
							++count;
						}
					}
				}
			}

			builder.setCount(count);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		send(ctx, 213, builder.build());
	}

	private static void getReceiptInfo(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.GetReceiptServer.Builder rstBuilder = PbGm.GetReceiptServer.newBuilder();
		try {

			PbGm.GetReceiptGM req = PbGm.GetReceiptGM.parseFrom(packet.getData());
			List<Receipt> list = new ArrayList();
			if (req.getType() == PbGm.GetReceiptGM.Type.ID) {
				int playerId = req.getId();
				list = DBUtil.getReceitByPlayer(playerId);
			} else if (req.getType() == PbGm.GetReceiptGM.Type.ORDERID) {
				String orderId = req.getOrderId();
				Receipt r = (Receipt) ((DbService) Platform.getServiceManager().get(DbService.class)).get(Receipt.class,
						orderId);
				if (r != null) {
					list.add(r);
				}
			}
			if ((list != null) && (list.size() > 0))
				for (Receipt r : list) {
					PbGm.GmReceipt.Builder b = PbGm.GmReceipt.newBuilder();
					b.setId(r.getPid());
					b.setName("");
					b.setChannel(r.getChannel());
					b.setGoodsId(String.valueOf(r.getGoodsId()));
					b.setCogoodsId(r.getCoGoodsId());
					b.setCreate(r.getCreateTime().getTime());
					if (r.getUpdateTime() != null)
						b.setUpdate(r.getUpdateTime().getTime());
					else {
						b.setUpdate(r.getCreateTime().getTime());
					}
					b.setOrderId(r.getOrderId());
					b.setPrice(r.getPrice());
					b.setState(r.getState());

					rstBuilder.addReceipts(b);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		send(ctx, 253, rstBuilder.build());
	}

	private static void getNoticeInfo(ChannelHandlerContext ctx) {
		PbGm.NoticeInfoServer.Builder b = PbGm.NoticeInfoServer.newBuilder();
		for (Notice n : NoticeService.notices.values()) {
			b.addNotices(n.genGmNotice());
		}
		send(ctx, 219, b.build());
	}

	private static void updateNotice(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.UpdateNoticeServer.Builder b = PbGm.UpdateNoticeServer.newBuilder();
		int id = -1;
		String title = "";
		String content = "";
		long start = 0L;
		long end = 0L;
		int weight = 0;
		try {
			PbGm.UpdateNoticeGM req = PbGm.UpdateNoticeGM.parseFrom(packet.getData());
			PbGm.GmNotice gn = req.getNotice();
			id = gn.getId();
			title = gn.getTitle();
			content = gn.getContent();
			start = gn.getStart();
			end = gn.getEnd();
			weight = gn.getWeight();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("公告更新失败!");
			send(ctx, 221, b.build());
			return;
		}
		Notice notice = new Notice();
		notice.id = id;
		notice.title = title;
		notice.content = content;
		notice.start = new Date(start);
		notice.end = new Date(end);
		notice.weight = weight;

		OptNoticeCallback call = new OptNoticeCallback(notice, 1);
		Platform.getCallBackManager().addCallBack(call);

		b.setResult("success");
		send(ctx, 221, b.build());
	}

	private static void removeNotice(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.RemoveNoticeServer.Builder b = PbGm.RemoveNoticeServer.newBuilder();
		int id = -1;
		try {
			PbGm.RemoveNoticeGM req = PbGm.RemoveNoticeGM.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("公告删除失败!");
			send(ctx, 223, b.build());
			return;
		}
		Notice notice = new Notice();
		notice.id = id;

		OptNoticeCallback call = new OptNoticeCallback(notice, 2);
		Platform.getCallBackManager().addCallBack(call);

		b.setResult("success");
		send(ctx, 223, b.build());
	}

	private static void boardCast(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.NoticeServer.Builder b = PbGm.NoticeServer.newBuilder();
		String notice = null;
		int num = 1;
		long interval = 0L;
		try {
			PbGm.NoticeGM req = PbGm.NoticeGM.parseFrom(packet.getData());
			notice = req.getNotice();
			if (req.hasNum()) {
				num = req.getNum();
			}
			if (req.hasInterval())
				interval = req.getInterval();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("全服广播失败!");
			send(ctx, 225, b.build());
			return;
		}

		GmService.addGmNotice(notice, num, interval);
		b.setResult("全服广播成功！");
		send(ctx, 225, b.build());
	}

	private static void shutdownEhcache(ChannelHandlerContext ctx) {
		Platform.getEntityManager().shutDown();
		Platform.getLog().logWarn("Ehcache shutdown!!!!");
		PbGm.ShutdownEhCacheRst.Builder builder = PbGm.ShutdownEhCacheRst.newBuilder();
		builder.setResult("Ehcache关闭成功");
		send(ctx, 215, builder.build());
	}

	private static void kickout(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.KickOutRst.Builder b = PbGm.KickOutRst.newBuilder();
		int id = -1;
		try {
			PbGm.KickOut out = PbGm.KickOut.parseFrom(packet.getData());
			id = out.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("踢人失败，请稍后重试!");
			send(ctx, 217, b.build());
			return;
		}
		if (id == -1) {
			Platform.getPlayerManager().kickOutAll();
			b.setResult("踢人成功，刷新在线玩家查看");
		} else {
			Player p = Platform.getPlayerManager().getPlayerById(id);
			if (p == null) {
				b.setResult("该玩家不在线");
			} else {
				p.getSession().disconnect();
				b.setResult("踢人成功，刷新在线玩家查看");
			}
		}
		send(ctx, 217, b.build());
	}

	private static void payCompensation(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.PayCompensationServer.Builder b = PbGm.PayCompensationServer.newBuilder();
		String orderId = null;
		try {
			PbGm.PayCompensationGM req = PbGm.PayCompensationGM.parseFrom(packet.getData());
			orderId = req.getOrderId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("消息结构错误，请稍后重试");
			send(ctx, 255, b.build());
			return;
		}
		PayCompensationAsyncCall call = new PayCompensationAsyncCall(orderId);
		Platform.getThreadPool().execute(call);
		b.setResult("操作完成，请重新查询");
		send(ctx, 255, b.build());
	}

	private static void redefine(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.RedefineServer.Builder b = PbGm.RedefineServer.newBuilder();
		String str = null;
		try {
			PbGm.RedefineGM out = PbGm.RedefineGM.parseFrom(packet.getData());
			str = out.getStr();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("重新加载类失败！！！");
			send(ctx, 227, b.build());
			return;
		}
		boolean flag = HotSwap.redefineClass(str);
		b.setResult((flag) ? "重新加载类成功" : "重新加载类失败！！！");
		send(ctx, 227, b.build());
	}

	private static void runThread(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.RunServer.Builder b = PbGm.RunServer.newBuilder();
		String str = null;
		try {
			PbGm.RunGM out = PbGm.RunGM.parseFrom(packet.getData());
			str = out.getStr();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("启动线程失败！！！");
			send(ctx, 229, b.build());
			return;
		}
		boolean flag = HotSwap.runThread(str);
		b.setResult((flag) ? "启动线程成功" : "启动线程失败！！！");
		send(ctx, 229, b.build());
	}

	private static void banChargeInfo(ChannelHandlerContext ctx) {
		PbGm.BanChargeInfoServer.Builder b = PbGm.BanChargeInfoServer.newBuilder();
		List list = Platform.getEntityManager().getAllFromEhCache(BanCharge.class.getName());
		for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
			Object obj = localIterator.next();
			BanCharge bc = (BanCharge) obj;
			b.addInfos(bc.genBanChargeInfo());
		}
		send(ctx, 247, b.build());
	}

	private static void banCharge(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List<String> names;
		String reason;
		BanCharge bc;
		PbGm.BanChargeServer.Builder b = PbGm.BanChargeServer.newBuilder();

		int time = 0;
		try {
			PbGm.BanChargeGM req = PbGm.BanChargeGM.parseFrom(packet.getData());
			names = req.getNamesList();
			time = req.getTime();
			reason = req.getReason();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("封停充值失败！！！");
			send(ctx, 249, b.build());
			return;
		}
		List<String> fails = new ArrayList();
		for (String name : names) {
			Player p = Platform.getPlayerManager().getPlayer(name);
			if (p == null) {
				p = DBUtil.getPlayerByName(name);
				if (p == null)
					fails.add(name);
			} else {
				bc = new BanCharge();
				bc.setId(p.getId());
				bc.setAccountId(p.getAccountId());
				bc.setName(name);
				bc.setReason(reason);
				bc.setStart(System.currentTimeMillis());
				bc.setBanTime(time * 1000);
				Platform.getEntityManager().putInEhCache(BanCharge.class.getName(), Integer.valueOf(p.getId()), bc,
						time);
				Platform.getLog().logBan("bancharge", name, bc.getStart(), time / 60, reason);
			}
		}
		String result = "封停成功！";
		if (fails.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("操作完成，以下账号不存在:[");
			for (String str : fails) {
				sb.append(str).append(",");
			}
			sb.append("]");
			result = sb.toString();
		}
		b.setResult(result);
		send(ctx, 249, b.build());
	}

	private static void banChargeRemove(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List<String> names;
		PbGm.BanChargeRemoveServer.Builder b = PbGm.BanChargeRemoveServer.newBuilder();
		try {
			PbGm.BanChargeRemoveGM req = PbGm.BanChargeRemoveGM.parseFrom(packet.getData());
			names = req.getNamesList();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("解封账号失败！！！");
			send(ctx, 251, b.build());
			return;
		}
		List<String> fails = new ArrayList<String>();
		for (String name : names) {
			Player p = Platform.getPlayerManager().getPlayer(name);
			if (p == null) {
				p = DBUtil.getPlayerByName(name);
				if (p == null)
					fails.add(name);
			} else {
				Platform.getEntityManager().deleteFromEhCache(BanCharge.class.getName(), Integer.valueOf(p.getId()));
				Platform.getLog().logBan("banchargeremove", name, -1L, -1, "null");
			}
		}
		String result = "解封成功！";
		if (fails.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("操作完成，以下账号不存在:[");
			for (String str : fails) {
				sb.append(str).append(",");
			}
			sb.append("]");
			result = sb.toString();
		}
		b.setResult(result);
		send(ctx, 251, b.build());
	}

	private static void banAccountInfo(ChannelHandlerContext ctx) {
		PbGm.BanAccountInfoServer.Builder b = PbGm.BanAccountInfoServer.newBuilder();
		List list = Platform.getEntityManager().getAllFromEhCache(BanAccount.class.getName());
		for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
			Object obj = localIterator.next();
			BanAccount ba = (BanAccount) obj;
			b.addInfos(ba.genBanAccountInfo());
		}
		send(ctx, 231, b.build());
	}

	private static void banAccount(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List ids;
		String reason;
		BanAccount ba;
		PbGm.BanAccountServer.Builder b = PbGm.BanAccountServer.newBuilder();

		int time = 0;
		try {
			PbGm.BanAccountGM req = PbGm.BanAccountGM.parseFrom(packet.getData());
			ids = req.getIdsList();
			time = req.getTime();
			reason = req.getReason();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("封停账号失败！！！");
			send(ctx, 233, b.build());
			return;
		}
		List fails = new ArrayList();
		for (Iterator localIterator = ids.iterator(); localIterator.hasNext();) {
			int id = ((Integer) localIterator.next()).intValue();
			try {
				Player p = Platform.getPlayerManager().getPlayer(id, true, false);
				if (p == null) {
					fails.add(Integer.valueOf(id));
				}
				if (Platform.getPlayerManager().getPlayerById(id) != null) {
					p.getSession().disconnect();
				}

				ba = new BanAccount();
				ba.setId(p.getId());
				ba.setAccountId(p.getAccountId());
				ba.setName(p.getName());
				ba.setReason(reason);
				ba.setStart(System.currentTimeMillis());
				ba.setBanTime(time * 1000);
				Platform.getEntityManager().putInEhCache(BanAccount.class.getName(), p.getAccountId(), ba, time);
				label289: Platform.getLog().logBan("banaccount", String.valueOf(id), ba.getStart(), time / 60, reason);
			} catch (Exception e) {
				e.printStackTrace();
				fails.add(Integer.valueOf(id));
			}
		}
		String result = "封停成功！";
		if (fails.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("操作完成，以下账号不存在:[");
			for (Iterator it = fails.iterator(); it.hasNext();) {
				int str = ((Integer) it.next()).intValue();
				sb.append(str).append(",");
			}
			sb.append("]");
			result = sb.toString();
		}
		b.setResult(result);
		send(ctx, 233, b.build());
	}

	private static void banAccountRemove(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List ids;
		PbGm.BanAccountRemoveServer.Builder b = PbGm.BanAccountRemoveServer.newBuilder();
		try {
			PbGm.BanAccountRemoveGM req = PbGm.BanAccountRemoveGM.parseFrom(packet.getData());
			ids = req.getIdsList();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("解封账号失败！！！");
			send(ctx, 235, b.build());
			return;
		}
		List fails = new ArrayList();
		for (Iterator localIterator1 = ids.iterator(); localIterator1.hasNext();) {
			int id = ((Integer) localIterator1.next()).intValue();
			try {
				Player p = Platform.getPlayerManager().getPlayer(id, true, false);
				if (p == null) {
					fails.add(Integer.valueOf(id));
				}
				Platform.getEntityManager().deleteFromEhCache(BanAccount.class.getName(), p.getAccountId());
				Platform.getLog().logBan("banaccountremove", String.valueOf(id), -1L, -1, "null");
			} catch (Exception e) {
				e.printStackTrace();
				fails.add(Integer.valueOf(id));
			}
		}
		String result = "解封成功！";
		if (fails.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("操作完成，以下账号不存在:[");
			for (Iterator localIterator2 = fails.iterator(); localIterator2.hasNext();) {
				int id = ((Integer) localIterator2.next()).intValue();
				sb.append(id).append(",");
			}
			sb.append("]");
			result = sb.toString();
		}
		b.setResult(result);
		send(ctx, 235, b.build());
	}

	private static void banIpInfo(ChannelHandlerContext ctx) {
		PbGm.BanIpInfoServer.Builder b = PbGm.BanIpInfoServer.newBuilder();
		List list = Platform.getEntityManager().getAllFromEhCache(BanIp.class.getName());
		for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
			Object obj = localIterator.next();
			BanIp bi = (BanIp) obj;
			b.addInfos(bi.genBanIpInfo());
		}
		send(ctx, 237, b.build());
	}

	private static void banIp(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List<String> ips;
		String reason;
		PbGm.BanIpServer.Builder b = PbGm.BanIpServer.newBuilder();

		int time = 0;
		try {
			PbGm.BanIpGM req = PbGm.BanIpGM.parseFrom(packet.getData());
			ips = req.getIpsList();
			time = req.getTime();
			reason = req.getReason();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("封停IP失败！！！");
			send(ctx, 239, b.build());
			return;
		}
		for (String ip : ips) {
			BanIp bi = new BanIp();
			bi.setIp(ip);
			bi.setStart(System.currentTimeMillis());
			bi.setBanTime(time * 1000);
			bi.setReason(reason);
			Platform.getEntityManager().putInEhCache(BanIp.class.getName(), ip, bi, time);
			Platform.getLog().logBan("banip", ip, bi.getStart(), time / 60, reason);
		}
		b.setResult("封停成功！");
		send(ctx, 239, b.build());
	}

	private static void banIpRemove(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List<String> ips;
		PbGm.BanIpRemoveServer.Builder b = PbGm.BanIpRemoveServer.newBuilder();
		try {
			PbGm.BanIpRemoveGM req = PbGm.BanIpRemoveGM.parseFrom(packet.getData());
			ips = req.getIpsList();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("解封IP失败！！！");
			send(ctx, 241, b.build());
			return;
		}
		for (String ip : ips) {
			Platform.getEntityManager().deleteFromEhCache(BanIp.class.getName(), ip);
			Platform.getLog().logBan("banipremove", ip, -1L, -1, "null");
		}
		b.setResult("解封成功！");
		send(ctx, 241, b.build());
	}

	private static void serverDataInfo(ChannelHandlerContext ctx) {
		PbGm.ServerDataServer.Builder b = PbGm.ServerDataServer.newBuilder();
		b.addData(
				genData(PbGm.GmServerData.Type.GLOBAL_CHARGE_COUNT, String.valueOf(PayService.getChargePlayerCount())));
		send(ctx, 257, b.build());
	}

	private static PbGm.GmServerData genData(PbGm.GmServerData.Type type, String value) {
		PbGm.GmServerData.Builder dataBuilder = PbGm.GmServerData.newBuilder();
		dataBuilder.setType(type);
		dataBuilder.setValue(value);
		return dataBuilder.build();
	}

	private static void serverDataUpdate(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		List<PbGm.GmServerData> list;
		PbGm.ServerDataUpdateServer.Builder b = PbGm.ServerDataUpdateServer.newBuilder();
		try {
			PbGm.ServerDataUpdateGM req = PbGm.ServerDataUpdateGM.parseFrom(packet.getData());
			list = req.getDataList();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult("修改服务器数据失败！！！");
			send(ctx, 259, b.build());
			return;
		}
		for (PbGm.GmServerData data : list) {
			try {
				if (data.getType() == PbGm.GmServerData.Type.GLOBAL_CHARGE_COUNT) {
					int value = Integer.valueOf(data.getValue()).intValue();
					PayService.setChargePlayerCount(value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		b.setResult("修改完成，请重新查询");
		send(ctx, 259, b.build());
	}

	private static void compesation(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.CompesationServer.Builder builder = PbGm.CompesationServer.newBuilder();
		builder.setResult(true);
		int count = 0;
		try {
			PbGm.CompesationGm req = PbGm.CompesationGm.parseFrom(packet.getData());
			int minLevel = req.getMinLevel();
			int maxLevel = req.getMaxLevel();
			long start = req.getStart();
			long end = req.getEnd();
			int optType = req.getType().getNumber();

			int type = req.getMail().getType().getNumber();
			String title = req.getMail().getTitle();
			String content = req.getMail().getContent();

			if ((minLevel < 0) || (maxLevel < 0) || (minLevel > maxLevel) || (start >= end)) {
				builder.setResult(false);
				send(ctx, 243, builder.build());
				return;
			}
			PbGm.AddItem addItem = req.getItem();
			int money = addItem.getMoney();
			int jewel = addItem.getJewel();
			int exp = addItem.getExp();
			int vitality = addItem.getVitality();
			int stamina = addItem.getStamina();
			int warriorSpirit = addItem.getWarriorSpirit();
			int spiritJade = addItem.getSpiritJade();
			int honor = addItem.getHonor();
			int prestige = addItem.getPrestige();
			List<PbGm.AddItemInfo> itemList = addItem.getItemsList();
			List rewards = new ArrayList();
			if (money > 0) {
				rewards.add(new Reward(2, money, null));
			}
			if (jewel > 0) {
				rewards.add(new Reward(3, jewel, null));
			}
			if (exp > 0) {
				rewards.add(new Reward(4, exp, null));
			}
			if (vitality > 0) {
				rewards.add(new Reward(5, vitality, null));
			}
			if (stamina > 0) {
				rewards.add(new Reward(6, stamina, null));
			}
			if (warriorSpirit > 0) {
				rewards.add(new Reward(8, warriorSpirit, null));
			}
			if (spiritJade > 0) {
				rewards.add(new Reward(7, spiritJade, null));
			}
			if (honor > 0) {
				rewards.add(new Reward(9, honor, null));
			}
			if (prestige > 0) {
				rewards.add(new Reward(10, prestige, null));
			}
			for (PbGm.AddItemInfo addItemInfo : itemList) {
				if (addItemInfo.getNum() > 0) {
					ItemTemplate template = ItemService.getItemTemplate(addItemInfo.getId());
					if ((!(Item.isCumulative(template.type))) && (addItemInfo.getNum() > 100)) {
						continue;
					}
					rewards.add(new Reward(0, addItemInfo.getNum(), template));
				}

			}

			List ids = DBUtil.getCompesationIds(new Date(start), new Date(end), minLevel, maxLevel, optType);
			if ((ids != null) && (ids.size() > 0)) {
				count = ids.size();
				int i = (type == 2) ? 3 : (type == 1) ? 2 : 1;
				new AddAwardsThread(ids, rewards, i, title, content).start();
			}

			long now = System.currentTimeMillis();
			if (end > now) {
				GlobalMail gm = new GlobalMail();
				gm.setType((optType == 1) ? 1 : 2);
				gm.setStart(start);
				gm.setEnd(end);
				gm.setMinLevel(minLevel);
				gm.setMaxLevel(maxLevel);
				gm.setMailType((type == 2) ? 3 : (type == 1) ? 2 : 1);
				gm.setTitle(title);
				gm.setContent(content);
				gm.setRewards(rewards);

				Platform.getEntityManager().putInEhCache(GlobalMail.class.getName(), Long.valueOf(gm.getId()), gm,
						(int) (end - now) / 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		builder.setCount(count);
		send(ctx, 243, builder.build());
	}

	private static void cherishDiscount(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.CherishDiscountServer.Builder builder = PbGm.CherishDiscountServer.newBuilder();
		builder.setResult("ok");
		try {
			PbGm.CherishDiscountGM req = PbGm.CherishDiscountGM.parseFrom(packet.getData());
			long start = req.getStart();
			long end = req.getEnd();
			int cost = req.getRefreshCost();

			if (start >= end) {
				builder.setResult("time is error");
				send(ctx, 267, builder.build());
				return;
			}
			CherishDiscount cd = new CherishDiscount();
			cd.start = start;
			cd.end = end;
			if (cost > 0) {
				cd.refreshCost = cost;
			}
			if (req.getGoodsCount() > 0) {
				for (PbGm.CherishDiscountGM.GoodsDiscount gd : req.getGoodsList()) {
					int type = gd.getType();
					int discount = gd.getDiscount();
					cd.goodsDiscounts.put(Integer.valueOf(type), Integer.valueOf(discount));
				}
			}
			if (req.getTimesCount() > 0) {
				for (PbGm.CherishDiscountGM.RefreshTime rt : req.getTimesList()) {
					int level = rt.getVipLevel();
					int addTime = rt.getAddTime();
					cd.vipTimes.put(Integer.valueOf(level), Integer.valueOf(addTime));
				}
			}
			Platform.getEntityManager().putInEhCache("persist", "CACHEKEY#CHERISDISCOUNT", cd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		send(ctx, 267, builder.build());
	}

	private static void decItem(ChannelHandlerContext ctx, PbPacket.Packet packet) {
		PbGm.DecItemServer.Builder builder = PbGm.DecItemServer.newBuilder();
		builder.setResult("操作成功！");
		try {
			PbGm.DecItemGm req = PbGm.DecItemGm.parseFrom(packet.getData());
			int id = req.getId();
			PbGm.DecItem decItem = req.getItem();

			Player player = Platform.getPlayerManager().getPlayer(id, true, true);
			if (player == null) {
				builder.setResult("操作失败！玩家不存在");
				send(ctx, 245, builder.build());
				return;
			}
			// player, decItem
			CallBackable call = new CallBackable() {
				PbGm.DecItem decItem;

				public void callback() {
					int money = this.decItem.getMoney();
					int jewel = this.decItem.getJewel();
					int vitality = this.decItem.getVitality();
					int stamina = this.decItem.getStamina();
					int warriorSpirit = this.decItem.getWarriorSpirit();
					int spiritJade = this.decItem.getSpiritJade();
					int honor = this.decItem.getHonor();
					int prestige = this.decItem.getPrestige();
					List<Integer> unCumulativeList = this.decItem.getUnCumulativeIdList();
					List<PbGm.CumulativeItemInfo> cumulativeList = this.decItem.getInfoList();

					if (money > 0) {
						player.decMoney(Math.min(player.getMoney(), money), "gmremove");
					}
					if (jewel > 0) {
						player.decJewels(Math.min(player.getJewels(), jewel), "gmremove");
					}
					if (vitality > 0) {
						player.decVitality(Math.min(player.getVitality(), vitality), "gmremove");
					}
					if (stamina > 0) {
						player.decStamina(Math.min(player.getStamina(), stamina), "gmremove");
					}
					if (warriorSpirit > 0) {
						player.decWarriorSpirit(Math.min(player.getWarriorSpirit(), warriorSpirit), "gmremove");
					}
					if (spiritJade > 0) {
						player.decSpiritJade(Math.min(player.getSpiritJade(), spiritJade), "gmremove");
					}
					if (honor > 0) {
						player.decHonor(Math.min(player.getHonor(), honor), "gmremove");
					}
					if (prestige > 0) {
						player.decPrestige(Math.min(player.getPrestige(), prestige), "gmremove");
					}
					for (Integer tmp : unCumulativeList) {
						Item i = player.getBags().getItemById(tmp.intValue());
						if (i != null) {
							if (i instanceof Warrior) {
								Warrior w = (Warrior) i;
								if (w.isMainWarrior()) {
									continue;
								}
								w.getEquips().unAllEquip();
								w.getTreasures().unAllEquip();
								if (w.getStageStatus() == 1) {
									player.getWarriors().downWarrior(w);
									player.getWarriors().refresh(true);
								} else if (w.getStageStatus() == 2) {
									player.getWarriors().downFriend(w);
									player.getWarriors().refresh(true);
								}
							} else {
								Warrior w;
								if (i instanceof Equipment) {
									Equipment e = (Equipment) i;
									if (e.getWarriorId() > 0) {
										w = player.getWarriors().getWarriorById(e.getWarriorId());
										w.getEquips().unEquip(e, w);
										player.getWarriors().refresh(true);
									}
								} else if (i instanceof Treasure) {
									Treasure t = (Treasure) i;
									if (t.getWarriorId() > 0) {
										w = player.getWarriors().getWarriorById(t.getWarriorId());
										w.getTreasures().unEquip(t, w);
										player.getWarriors().refresh(true);
									}
								}
							}
							player.getBags().removeItem(i.getId(), i.getTemplateId(), 1, "gmremove");
						}
					}
					for (PbGm.CumulativeItemInfo info : cumulativeList) {
						int templateId = info.getId();
						int num = info.getNum();
						ItemTemplate it = ItemService.getItemTemplate(templateId);
						if ((it != null) && (Item.isCumulative(it.type))) {
							int count = player.getBags().getItemCount(templateId);
							if (num <= count) {
								player.getBags().removeItem(0, templateId, num, "gmremove");
							}
						}
					}

					if (player.getSession() == null) {
						PlayerSaveCall save = new PlayerSaveCall(player);
						Platform.getThreadPool().execute(save);
					}
				}
			};
			Platform.getCallBackManager().addCallBack(call);
		} catch (Exception e) {
			e.printStackTrace();
		}
		send(ctx, 245, builder.build());
	}

	static class AddAwardsThread extends Thread {
		List<Integer> ids;
		List<Reward> rewards;
		int type;
		String title;
		String content;

		public AddAwardsThread(List<Integer> ids, List<Reward> rewards, int type, String title, String content) {
			this.ids = ids;
			this.rewards = rewards;
			this.type = type;
			this.title = title;
			this.content = content;
		}

		public void run() {
			long start = System.currentTimeMillis();
			for (Integer id : this.ids) {
				MailService.sendSystemMailByThread(this.type, id.intValue(), this.title, this.content, new Date(start),
						this.rewards);
			}
			Platform.getLog().logSystem("compesation player count:" + this.ids.size() + ",spend time :"
					+ (System.currentTimeMillis() - start));
		}
	}

	static class OptNoticeCallback implements CallBackable {
		static final int OPT_UPDATE = 1;
		static final int OPT_REMOVE = 2;
		Notice notice;
		int opt;

		public OptNoticeCallback(Notice notice, int opt) {
			this.notice = notice;
			this.opt = opt;
		}

		public void callback() {
			if (this.opt == 1)
				NoticeService.updateNotice(this.notice);
			else if (this.opt == 2)
				NoticeService.removeNotice(this.notice);
		}
	}
}
