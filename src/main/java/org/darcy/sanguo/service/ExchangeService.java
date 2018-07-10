package org.darcy.sanguo.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.exchange.ExchangeTemplate;
import org.darcy.sanguo.exchange.Exchanges;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.utils.ExcelUtils;
import org.darcy.sanguo.vip.Vip;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbExchange;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class ExchangeService implements Service, PacketHandler {
	public Map<Integer, ExchangeTemplate> templates = new HashMap<Integer, ExchangeTemplate>();

	private Comparator<ExchangeTemplate> exchangeCompa = new Comparator<ExchangeTemplate>() {
		public int compare(ExchangeTemplate e1, ExchangeTemplate e2) {
			return (e1.id - e2.id);
		}
	};

	public void startup() throws Exception {
		loadExchange();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public int[] getCodes() {
		return new int[] { 1089, 1091, 1271 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null) {
			return;
		}
		switch (packet.getPtCode()) {
		case 1089:
			exchangeInfo(player, packet);
			break;
		case 1091:
			exchange(player, packet);
			break;
		case 1271:
			exchangeAppointInfo(player, packet);
		}
	}

	private void loadExchange() {
		List<Row> list = ExcelUtils.getRowList("exchange.xls", 2);
		int pos = 0;
		for (Row row : list) {
			pos = 0;
			if (row == null) {
				return;
			}
			if (row.getCell(pos) == null) {
				return;
			}
			int id = (int) row.getCell(pos++).getNumericCellValue();
			String reward = row.getCell(pos++).getStringCellValue();
			int online = (int) row.getCell(pos++).getNumericCellValue();
			int countType = (int) row.getCell(pos++).getNumericCellValue();
			int count = (int) row.getCell(pos++).getNumericCellValue();
			int costType = (int) row.getCell(pos++).getNumericCellValue();
			String costs = row.getCell(pos++).getStringCellValue();
			int showType = (int) row.getCell(pos++).getNumericCellValue();
			String start = row.getCell(pos++).getStringCellValue();
			String end = row.getCell(pos++).getStringCellValue();

			ExchangeTemplate template = new ExchangeTemplate();
			template.id = id;
			if ((reward != null) && (!(reward.equals("")))) {
				template.reward = new Reward(reward);
			}
			template.countType = countType;
			template.count = count;
			template.costType = costType;
			template.showType = showType;
			if ((costs != null) && (!(costs.equals("")))) {
				String[] costArray = costs.split(",");
				for (String cost : costArray) {
					template.costs.add(new Reward(cost));
				}
			}
			try {
				template.start = Exchanges.sdf.parse(start).getTime();
				template.end = Exchanges.sdf.parse(end).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.templates.put(Integer.valueOf(id), template);
		}
	}

	private void exchangeInfo(Player player, PbPacket.Packet packet) {
		PbDown.ExchangesInfoRst.Builder builder = PbDown.ExchangesInfoRst.newBuilder();
		builder.setResult(true);
		PbExchange.ExchangeFunctionType type = null;
		try {
			PbUp.ExchangesInfo info = PbUp.ExchangesInfo.parseFrom(packet.getData());
			type = info.getType();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("消息结构错误");
			player.send(1090, builder.build());
			return;
		}
		List<ExchangeTemplate> list = getTemplateByFunctionType(type);
		Vip vip = player.getVip();
		for (ExchangeTemplate template : list) {
			if ((!(template.isOnline()))
					|| ((template.countType == -1) && (player.getExchanges().getSurplusCount(template.id, vip) == 0)))
				continue;
			builder.addItems(player.getExchanges().genExchange(template.id, vip));
		}

		builder.setResult(true);
		player.send(1090, builder.build());
	}

	private void exchange(Player player, PbPacket.Packet packet) {
		PbDown.ExchangeRst.Builder builder = PbDown.ExchangeRst.newBuilder();
		builder.setResult(true);
		int id = 0;
		int count = 0;
		try {
			PbUp.Exchange exchange = PbUp.Exchange.parseFrom(packet.getData());
			id = exchange.getId();
			count = exchange.getCount();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			builder.setResult(false);
			builder.setErrInfo("兑换失败");
			player.send(1092, builder.build());
			return;
		}
		ExchangeTemplate template = getTemplate(id);
		if ((template == null) || (!(template.isOnline()))) {
			builder.setResult(false);
			builder.setErrInfo("兑换数据不存在");
		} else {
			if (!(player.getExchanges().getExchanges().containsKey(Integer.valueOf(id)))) {
				player.getExchanges().updateExchanges(id, 0);
			}
			int surplus = player.getExchanges().getSurplusCount(id, player.getVip());
			if ((surplus != -1) && (surplus < count)) {
				if (player.getVip().shopLimits.containsKey(Integer.valueOf(id))) {
					VipService.notifyVipBuyTimeLack(player, PbCommons.VipBuyTimeType.SHOPBUY, id);
					return;
				}
				builder.setResult(false);
				builder.setErrInfo("兑换次数不足，无法兑换");
			} else if (count < 1) {
				builder.setResult(false);
				builder.setErrInfo("兑换次数有误");
			} else {
				List<Reward> list = template.getCost(player.getExchanges().getCount(id), count);
				Reward cost = ((Reward) list.get(0)).copy();
				cost.count = 0;
				for (Reward tmp : list) {
					cost.count += tmp.count;
				}
				String errStr = null;
				if ((errStr = cost.check(player)) != null) {
					builder.setResult(false);
					builder.setErrInfo(errStr);
				} else {
					Reward reward = template.reward.copy();
					reward.count = 0;
					for (int i = 0; i < count; ++i) {
						reward.count += template.reward.count;
					}
					String optType = "";
					if (template.showType == 2)
						optType = "arenaexchange";
					else if (template.showType == 1)
						optType = "worldcompetexchange";
					else {
						optType = "shop";
					}
					reward.add(player, optType);
					cost.remove(player, optType);
					player.getExchanges().updateExchanges(id, player.getExchanges().getCount(id) + count);
					builder.setResult(true);
					builder.addRewards(reward.genPbReward());
				}
			}
		}
		player.send(1092, builder.build());
	}

	private void exchangeAppointInfo(Player player, PbPacket.Packet packet) {
		PbDown.ExchangeAppointInfoRst.Builder b = PbDown.ExchangeAppointInfoRst.newBuilder().setResult(true);
		int id = 0;
		try {
			PbUp.ExchangeAppointInfo req = PbUp.ExchangeAppointInfo.parseFrom(packet.getData());
			id = req.getId();
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			b.setResult(false);
			b.setErrInfo("消息结构错误");
			player.send(1272, b.build());
			return;
		}
		ExchangeTemplate et = getTemplate(id);
		if ((et == null) || (!(et.isOnline()))) {
			b.setResult(false);
			b.setErrInfo("兑换数据不存在");
		} else {
			if (!(player.getExchanges().getExchanges().containsKey(Integer.valueOf(id)))) {
				player.getExchanges().updateExchanges(id, 0);
			}
			int surplus = player.getExchanges().getSurplusCount(id, player.getVip());
			if (surplus == 0) {
				if (!(player.getVip().shopLimits.containsKey(Integer.valueOf(id)))) {
					player.send(1272, b.build());
					return;
				}
				VipService.notifyVipBuyTimeLack(player, PbCommons.VipBuyTimeType.SHOPBUY, id);
				return;
			}

			b.addItems(player.getExchanges().genExchange(id, player.getVip()));
		}

		player.send(1272, b.build());
	}

	public ExchangeTemplate getTemplate(int id) {
		return ((ExchangeTemplate) this.templates.get(Integer.valueOf(id)));
	}

	public List<ExchangeTemplate> getTemplateByFunctionType(PbExchange.ExchangeFunctionType type) {
		List<ExchangeTemplate> list = new ArrayList<ExchangeTemplate>();
		for (ExchangeTemplate template : this.templates.values()) {
			if (template.showType == type.getNumber()) {
				list.add(template);
			}
		}
		Collections.sort(list, this.exchangeCompa);
		return list;
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
	}
}
