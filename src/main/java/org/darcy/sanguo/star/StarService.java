package org.darcy.sanguo.star;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.event.EventHandler;
import org.darcy.sanguo.exp.ExpService;
import org.darcy.sanguo.hero.HeroTemplate;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.Service;
import org.darcy.sanguo.service.common.FunctionService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.Calc;
import org.darcy.sanguo.utils.ExcelUtils;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class StarService implements PacketHandler, Service, EventHandler {
	public static final int STAR_MAX_LEVEL = 50;
	public static final int TRAIN_MAX_LEVEL = 120;
	public static final int TRAIN_RESET_PRICEID = 5005;
	public static final int TRAIN_UPDATE_PRICEID = 5006;
	public static final int STAR_LEVEL_BONOUS = 5007;
	public static final int[] TRAIN_PER_LEVLE = { 100, 10, 10, 10 };

	public static HashMap<Integer, Star> stars = new HashMap<Integer, Star>();

	public static HashMap<Integer, Attri[]> attris = new HashMap<Integer, Attri[]>();

	public static List<Achieve> achieves = new ArrayList<Achieve>();

	public static HashMap<Integer, int[]> ratios = new HashMap<Integer, int[]>();

	public static HashMap<Integer, int[]> shows = new HashMap<Integer, int[]>();

	public static Set<Integer> allStars = new HashSet<Integer>();

	public static HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();

	public void loadData() throws Exception, IOException {
		int i, id, pos;
		List<Row> list = ExcelUtils.getRowList("star.xls", 2);
		for (Row r : list) {
			pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			Star star = new Star();
			star.heroId = (int) r.getCell(pos++).getNumericCellValue();
			++pos;
			star.expId = (int) r.getCell(pos++).getNumericCellValue();
			star.attriId = (int) r.getCell(pos++).getNumericCellValue();
			star.ratioId = (int) r.getCell(pos++).getNumericCellValue();
			stars.put(Integer.valueOf(star.heroId), star);
		}

		List<Row> list2 = ExcelUtils.getRowList("star.xls", 2, 1);
		for (Row r : list2) {
			pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			id = (int) r.getCell(pos++).getNumericCellValue();
			Attri[] ats = new Attri[50];

			for (i = 0; i < 50; ++i) {
				String atr = r.getCell(pos++).getStringCellValue();
				ats[i] = new Attri(atr);
			}
			attris.put(Integer.valueOf(id), ats);
		}

		List<Row> list3 = ExcelUtils.getRowList("star.xls", 2, 2);
		for (Row r : list3) {
			pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			Achieve a = new Achieve();
			a.id = (int) r.getCell(pos++).getNumericCellValue();
			a.name = r.getCell(pos++).getStringCellValue();
			a.desc = r.getCell(pos++).getStringCellValue();
			a.gole = (int) r.getCell(pos++).getNumericCellValue();
			a.stamina = (int) r.getCell(pos++).getNumericCellValue();
			a.quality = (int) r.getCell(pos++).getNumericCellValue();
			achieves.add(a);
		}

		List<Row> list4 = ExcelUtils.getRowList("star.xls", 2, 3);
		for (Row r : list4) {
			pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			id = (int) r.getCell(pos++).getNumericCellValue();
			int[] ats = new int[50];

			for (i = 0; i < 50; ++i) {
				ats[i] = (int) r.getCell(pos++).getNumericCellValue();
			}
			ratios.put(Integer.valueOf(id), ats);
		}

		List<Row> list5 = ExcelUtils.getRowList("star.xls", 2, 4);
		for (Row r : list5) {
			pos = 0;
			if (r == null)
				break;
			if (r.getCell(pos) == null)
				break;
			id = (int) r.getCell(pos++).getNumericCellValue();
			int count = (int) r.getCell(pos++).getNumericCellValue();
			int[] ats = new int[count];

			for (i = 0; i < count; ++i) {
				ats[i] = (int) r.getCell(pos++).getNumericCellValue();
				allStars.add(Integer.valueOf(ats[i]));
			}

			shows.put(Integer.valueOf(id), ats);
		}

		List<Row> list6 = ExcelUtils.getRowList("star.xls", 2, 5);
		for (Row r : list6) {
			pos = 0;
			if (r == null)
				return;
			if (r.getCell(pos) == null)
				return;
			id = (int) r.getCell(pos++).getNumericCellValue();
			int exp = (int) r.getCell(pos++).getNumericCellValue();
			items.put(Integer.valueOf(id), Integer.valueOf(exp));
		}
	}

	public void startup() throws Exception {
		loadData();
		Platform.getEventManager().registerListener(this);
		Platform.getPacketHanderManager().registerHandler(this);
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		achieves.clear();
		attris.clear();
		ratios.clear();
		shows.clear();
		allStars.clear();
		items.clear();
		loadData();
	}

	public int[] getCodes() {
		return new int[] { 2137, 2135, 2139, 2131, 2145, 2133, 2207, 2205, 2197, 2199, 2203, 2201 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;
		if (!(FunctionService.isOpenFunction(player.getLevel(), 15))) {
			return;
		}
		switch (packet.getPtCode()) {
		case 2137:
			achieve(player);
			break;
		case 2135:
			PbUp.StarAttris as = PbUp.StarAttris.parseFrom(packet.getData());
			attris(player, as.getWarriorTemplateId());
			break;
		case 2139:
			PbUp.StarGive give = PbUp.StarGive.parseFrom(packet.getData());
			give(player, give.getWarriorTemplateId(), give.getItemTemplateId());
			break;
		case 2131:
			info(player);
			break;
		case 2133:
			PbUp.StarList list = PbUp.StarList.parseFrom(packet.getData());
			list(player, list.getIndex());
			break;
		case 2145:
			PbUp.StarSelect select = PbUp.StarSelect.parseFrom(packet.getData());
			select(player, select.getWarriorTemplateId());
			break;
		case 2197:
			PbUp.StarGiveMulti multi = PbUp.StarGiveMulti.parseFrom(packet.getData());
			int heroId = multi.getHeroId();
			giveMulti(player, heroId);
			break;
		case 2207:
			PbUp.StarChange ch = PbUp.StarChange.parseFrom(packet.getData());
			change(player, ch.getLeftHeroId(), ch.getRightHeroId());
			break;
		case 2205:
			PbUp.StarChangeInfo info = PbUp.StarChangeInfo.parseFrom(packet.getData());
			changeInfo(player, info.getHeroId());
			break;
		case 2199:
			PbUp.StarTrainInfo ti = PbUp.StarTrainInfo.parseFrom(packet.getData());
			trainInfo(player, ti.getHeroId());
			break;
		case 2203:
			PbUp.StarTrainReset reset = PbUp.StarTrainReset.parseFrom(packet.getData());
			trainReset(player, reset.getHeroId(), reset.getIndex());
			break;
		case 2201:
			PbUp.StarTrainUpdate up = PbUp.StarTrainUpdate.parseFrom(packet.getData());
			trainUpdate(player, up.getHeroId(), up.getIndex());
		}
	}

	private void change(Player player, int leftH, int rightH) {
		PbDown.StarChangeRst.Builder rst = PbDown.StarChangeRst.newBuilder();
		rst.setResult(false);
		try {
			StarRecord r = player.getStarRecord();
			HeroTemplate left = (HeroTemplate) ItemService.getItemTemplate(leftH);
			HeroTemplate right = (HeroTemplate) ItemService.getItemTemplate(rightH);
			if (left.aptitude == right.aptitude) {
				int price = getChangePrice(right.aptitude);
				if (player.getJewels() >= price) {
					Item item;
					Warrior w;
					int levelLeft = r.getLevel(leftH);
					if (levelLeft < 0)
						levelLeft = 0;
					int levelRight = r.getLevel(rightH);
					if (levelRight < 0)
						levelRight = 0;
					int expLeft = r.getExp(leftH);
					int expRight = r.getExp(rightH);
					player.decJewels(price, "startchange");
					r.getHeroExps().put(Integer.valueOf(leftH), Integer.valueOf(expRight));
					r.getHeroExps().put(Integer.valueOf(rightH), Integer.valueOf(expLeft));
					r.getHeroLevels().put(Integer.valueOf(leftH), Integer.valueOf(levelRight));
					r.getHeroLevels().put(Integer.valueOf(rightH), Integer.valueOf(levelLeft));
					r.refreshStarAttris(rightH);
					r.refreshStarAttris(leftH);
					for (Iterator<?> localIterator = player.getBags().getItemByTemplateId(leftH)
							.iterator(); localIterator.hasNext();) {
						item = (Item) localIterator.next();
						w = (Warrior) item;
						w.refreshAttributes(true);
					}
					for (Iterator<?> localIterator = player.getBags().getItemByTemplateId(rightH)
							.iterator(); localIterator.hasNext();) {
						item = (Item) localIterator.next();
						w = (Warrior) item;
						w.refreshAttributes(true);
					}
					rst.setResult(true);
					Platform.getLog().logInherit(player, leftH, rightH);
				}
				rst.setErrorInfo("元宝不足");
			}
			rst.setErrorInfo("品质不同的武将无法进行传承");
		} catch (Exception e) {
			rst.setErrorInfo("服务器繁忙，请稍后再试");
			e.printStackTrace();
		}
		player.send(2208, rst.build());
		if (rst.getResult()) {
			changeInfo(player, leftH);
			changeInfo(player, rightH);
		}
	}

	private void changeInfo(Player player, int heroId) {
		PbDown.StarChangeInfoRst.Builder rst = PbDown.StarChangeInfoRst.newBuilder();
		rst.setResult(false);
		rst.setHeroId(heroId);
		try {
			StarRecord r = player.getStarRecord();
			int level = r.getLevel(heroId);
			if (level < 0)
				level = 0;
			rst.setLevel(level);
			HeroTemplate ht = (HeroTemplate) ItemService.getItemTemplate(heroId);
			int price = getChangePrice(ht.aptitude);
			if (price > 0) {
				rst.setResult(true);
				rst.setAttri(r.getAttributes(heroId).genAttribute());
				rst.setTrainAttri(r.getTrainAttribues(heroId).genAttribute());
				rst.setPrice(price);
			}
			rst.setErrorInfo("该武将无法传承");
		} catch (Exception e) {
			rst.setErrorInfo("服务器繁忙，请稍后再试");
		}
		player.send(2206, rst.build());
	}

	public int getChangePrice(int aptitude) {
		switch (aptitude) {
		case 10:
			return 20;
		case 12:
			return 30;
		case 13:
			return 50;
		case 15:
			return 100;
		case 11:
		case 14:
		}
		return 0;
	}

	private void trainReset(Player player, int heroId, int idx) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 50))) {
			return;
		}
		PbDown.StarTrainResetRst.Builder rst = PbDown.StarTrainResetRst.newBuilder();
		rst.setResult(false);
		rst.setIndex(idx);
		try {
			StarRecord r = player.getStarRecord();
			ExpService es = (ExpService) Platform.getServiceManager().get(ExpService.class);
			int price = 100;
			int add = 0;
			if (player.getJewels() >= price) {
				int level;
				for (int index = 0; index < 4; ++index) {
					level = r.getTrainLevel(heroId)[index];
					if (level > 0) {
						int total = 0;
						while (level > 0) {
							total += es.getExp(5006, level);
							--level;
						}

						add += total;
						r.setTrainLevel(heroId, index, 0);
					}
				}

				rst.setResult(true);
				player.getRewardRecord().addTrainPoint(player, add, "startrainreset");
				player.decJewels(price, "startrainreset");
				for (Item item : player.getBags().getItemByTemplateId(heroId)) {
					Warrior w = (Warrior) item;
					w.refreshAttributes(true);
				}
			}
			rst.setErrorInfo("元宝不足");
		} catch (Exception e) {
			rst.setErrorInfo("服务器繁忙，请稍后再试");
		}
		player.send(2204, rst.build());
		if (rst.getResult())
			trainInfo(player, heroId);
	}

	private void trainUpdate(Player player, int heroId, int index) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 50))) {
			return;
		}
		PbDown.StarTrainUpdateRst.Builder rst = PbDown.StarTrainUpdateRst.newBuilder();
		rst.setResult(false);
		try {
			StarRecord r = player.getStarRecord();
			ExpService es = (ExpService) Platform.getServiceManager().get(ExpService.class);
			int level = r.getTrainLevel(heroId)[index];
			if (level < 120) {
				int price = es.getExp(5006, level + 1);
				int points = player.getPool().getInt(22, 0);
				if (points >= price) {
					points -= price;
					player.getPool().set(22, Integer.valueOf(points));
					r.setTrainLevel(heroId, index, level + 1);
					rst.setResult(true);
					for (Item item : player.getBags().getItemByTemplateId(heroId)) {
						Warrior w = (Warrior) item;
						w.refreshAttributes(true);
					}
					player.getDataSyncManager().addNumSync(18, player.getPool().getInt(22, 0));
					Platform.getLog().logTrain(player, heroId, index, price);
				}
				rst.setErrorInfo("训练点数不足");
			}
			rst.setErrorInfo("已经达到最大训练等级");
		} catch (Exception e) {
			rst.setErrorInfo("服务器繁忙，请稍后再试");
		}

		player.send(2202, rst.build());
		if (rst.getResult())
			trainInfo(player, heroId);
	}

	private void trainInfo(Player player, int heroId) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 50))) {
			return;
		}
		PbDown.StarTrainInfoRst.Builder rst = PbDown.StarTrainInfoRst.newBuilder();
		rst.setResult(false);
		rst.setHeroId(heroId);
		try {
			StarRecord r = player.getStarRecord();
			int starLevel = r.getLevel(heroId);
			if (starLevel < 0)
				starLevel = 0;
			int bonous = 0;
			ExpService es = (ExpService) Platform.getServiceManager().get(ExpService.class);
			if (starLevel > 0) {
				bonous = es.getExp(5007, starLevel);
			}
			int[] levels = r.getTrainLevel(heroId);
			for (int i = 0; i < levels.length; ++i) {
				int level = levels[i];
				rst.addLevels(level);
				rst.addBases(level * TRAIN_PER_LEVLE[i]);
				rst.addRates(bonous);
				if (level > 0)
					rst.addResetPrice(100);
				else {
					rst.addResetPrice(100);
				}
				if (level < 120)
					rst.addUpdatePrice(es.getExp(5006, level + 1));
				else {
					rst.addUpdatePrice(-1);
				}
				rst.addUpdate(TRAIN_PER_LEVLE[i]);
			}
			rst.setAttri(r.getAttributes(heroId).genAttribute());
			rst.setTrainAttri(r.getTrainAttribues(heroId).genAttribute());
			rst.setResult(true);
		} catch (Exception e) {
			rst.setErrorInfo("服务器繁忙，请稍后再试");
		}

		player.send(2200, rst.build());
	}

	private void achieve(Player player) {
		PbDown.StarAchievesRst.Builder rst = PbDown.StarAchievesRst.newBuilder();
		StarRecord r = player.getStarRecord();
		rst.setResult(true);
		int totalLevel = r.getTotalLevel();
		for (Achieve ac : achieves) {
			PbCommons.StarAchieve.Builder a = PbCommons.StarAchieve.newBuilder();
			a.setDescription(ac.desc).setFinished(totalLevel >= ac.gole).setLevel(ac.id).setName(ac.name)
					.setQuality(ac.quality).setTotalLevel(totalLevel).setGole(ac.gole)
					.setReward(MessageFormat.format("人物精力上限+{0}", new Object[] { Integer.valueOf(ac.stamina) }));
			rst.addAchieves(a);
		}
		player.send(2138, rst.build());
	}

	private void attris(Player player, int heroId) {
		PbDown.StarAttrisRst.Builder rst = PbDown.StarAttrisRst.newBuilder();
		rst.setResult(true);
		try {
			Star star = (Star) stars.get(Integer.valueOf(heroId));
			Attri[] ats = (Attri[]) attris.get(Integer.valueOf(star.attriId));
			int i = 0;
			do {
				PbCommons.StarAttri.Builder sb = PbCommons.StarAttri.newBuilder();
				Attri a = ats[i];
				sb.setLevel(i + 1).setAttriId(a.getAid()).setValue(a.getValue());
				rst.addAttris(sb);

				++i;
				if (i >= ats.length)
					;
			} while (i < 50);
		} catch (Exception e) {
			e.printStackTrace();
			rst.setResult(false);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2136, rst.build());
	}

	private void giveMulti(Player player, int heroId) {
		if (!(FunctionService.isOpenFunction(player.getLevel(), 75))) {
			return;
		}

		ArrayList<Integer> ls = new ArrayList<Integer>(items.keySet());
		Collections.sort(ls);
		StarRecord r = player.getStarRecord();
		PbDown.StarGiveMultiRst.Builder rst = PbDown.StarGiveMultiRst.newBuilder();
		rst.setResult(false);
		rst.setHeroId(heroId);
		try {
			int count = 0;

			for (Integer itemId : ls) {
				Integer addExp = (Integer) items.get(itemId);
				while ((player.getBags().getItemCount(itemId.intValue()) > 0) && (count < 20)) {
					int addLevel;
					if (addExp == null)
						break;
					Star star = (Star) stars.get(Integer.valueOf(heroId));
					if (star == null)
						break;
					int level = r.getLevel(heroId);
					if (level < 0)
						level = 0;
					if (level >= 50)
						break;
					int exp = r.getExp(heroId);
					int ratio = getRatio(heroId, level, exp);
					boolean levelUp = false;
					boolean crit = Calc.nextInt(10000) < ratio;
					player.getBags().removeItem(0, itemId.intValue(), 1, "stargive");
					Attributes a = r.getAttributes(heroId);
					if (a == null) {
						a = new Attributes();
						r.setAttributes(heroId, a);
					}

					if ((crit) && (exp + addExp.intValue() <= getMaxExp(heroId, level))) {
						levelUp = true;
						r.getHeroLevels().put(Integer.valueOf(heroId), Integer.valueOf(level + 1));
						Attri attri = ((Attri[]) attris.get(Integer.valueOf(star.attriId)))[level];
						a.addAttri(attri);
						player.getWarriors().getMainWarrior().refreshAttributes(true);
						r.refreshMaxAchieve(player);
						r.getHeroExps().put(Integer.valueOf(heroId), Integer.valueOf(0));
					} else {
						crit = false;
						int totalExp = addExp.intValue() + exp;
						addLevel = 0;
						while ((totalExp >= getMaxExp(heroId, addLevel + level)) && (level + addLevel < 50)) {
							totalExp -= getMaxExp(heroId, addLevel + level);
							++addLevel;
							Attri attri = ((Attri[]) attris.get(Integer.valueOf(star.attriId)))[(level + addLevel - 1)];
							a.addAttri(attri);
							levelUp = true;
						}

						r.getHeroExps().put(Integer.valueOf(heroId), Integer.valueOf(totalExp));
						r.getHeroLevels().put(Integer.valueOf(heroId), Integer.valueOf(level + addLevel));
					}
					if (levelUp) {
						levelUp = true;
						r.refreshMaxAchieve(player);
						for (Item item : player.getBags().getItemByTemplateId(heroId)) {
							Warrior w = (Warrior) item;
							w.refreshAttributes(true);
						}
					}

					++count;
					Platform.getEventManager().addEvent(new Event(2030, new Object[] { player, Integer.valueOf(1) }));
					Platform.getLog().logStar(player, heroId, itemId.intValue(), addExp.intValue(), crit);
				}

			}

			rst.setResult(true);
			rst.setTimes(count);
		} catch (Exception e) {
			rst.setErrorInfo("服务器繁忙，请稍后再试");
			rst.setTimes(0);
		}

		player.send(2198, rst.build());
		select(player, heroId);
	}

	private void give(Player player, int heroId, int itemId) {
		StarRecord r = player.getStarRecord();
		PbDown.StarGiveRst.Builder rst = PbDown.StarGiveRst.newBuilder();
		rst.setResult(false);
		try {
			Integer addExp = (Integer) items.get(Integer.valueOf(itemId));
			if (player.getBags().getItemCount(itemId) > 0) {
				if (addExp != null) {
					Star star = (Star) stars.get(Integer.valueOf(heroId));
					if (star != null) {
						int level = r.getLevel(heroId);
						if (level < 0)
							level = 0;
						if (level < 50) {
							int addLevel;
							rst.setResult(true);
							int exp = r.getExp(heroId);
							int ratio = getRatio(heroId, level, exp);
							boolean levelUp = false;
							boolean crit = Calc.nextInt(10000) < ratio;
							player.getBags().removeItem(0, itemId, 1, "stargive");
							Attributes a = r.getAttributes(heroId);
							if (a == null) {
								a = new Attributes();
								r.setAttributes(heroId, a);
							}

							if ((crit) && (exp + addExp.intValue() <= getMaxExp(heroId, level))) {
								levelUp = true;
								r.getHeroLevels().put(Integer.valueOf(heroId), Integer.valueOf(level + 1));
								Attri attri = ((Attri[]) attris.get(Integer.valueOf(star.attriId)))[level];
								a.addAttri(attri);
								player.getWarriors().getMainWarrior().refreshAttributes(true);
								r.refreshMaxAchieve(player);
								r.getHeroExps().put(Integer.valueOf(heroId), Integer.valueOf(0));
							} else {
								crit = false;
								int totalExp = addExp.intValue() + exp;
								addLevel = 0;
								while ((totalExp >= getMaxExp(heroId, addLevel + level)) && (level + addLevel < 50)) {
									totalExp -= getMaxExp(heroId, addLevel + level);
									++addLevel;
									Attri attri = ((Attri[]) attris.get(Integer.valueOf(star.attriId)))[(level
											+ addLevel - 1)];
									a.addAttri(attri);
									levelUp = true;
								}

								r.getHeroExps().put(Integer.valueOf(heroId), Integer.valueOf(totalExp));
								r.getHeroLevels().put(Integer.valueOf(heroId), Integer.valueOf(level + addLevel));
							}
							if (levelUp) {
								levelUp = true;
								r.refreshMaxAchieve(player);
								for (Item item : player.getBags().getItemByTemplateId(heroId)) {
									Warrior w = (Warrior) item;
									w.refreshAttributes(true);
								}
							}

							rst.setCrit(crit).setAddExp(addExp.intValue()).setLevelUp(levelUp);
							Platform.getEventManager()
									.addEvent(new Event(2030, new Object[] { player, Integer.valueOf(1) }));
							Platform.getLog().logStar(player, heroId, itemId, addExp.intValue(), crit);
						}
						rst.setErrInfo("名将历练已经达到最高级，无法继续培养");
					}
					rst.setErrInfo("该小兵毫无名气，不能给他赠送");
				}
				rst.setErrInfo("该物品不能赠送");
			}
			rst.setErrInfo("物品不足");
		} catch (Exception e) {
			e.printStackTrace();
			rst.setResult(false);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2140, rst.build());
		if (rst.getResult())
			select(player, heroId);
	}

	private void list(Player player, int index) {
		StarRecord r = player.getStarRecord();
		PbDown.StarListRst.Builder rst = PbDown.StarListRst.newBuilder();
		rst.setResult(true);
		try {
			PbCommons.Star.Builder star;
			int level = 0;
			rst.setTotalLevel(r.getTotalLevel());
			for (int heroId : (int[]) shows.get(Integer.valueOf(0))) {
				level = r.getLevel(heroId);
				if ((level < 0) && (player.getBags().getItemCount(heroId) < 1)) {
					continue;
				}
				if (level < 0)
					level = 0;
				star = PbCommons.Star.newBuilder();
				star.setWarriorTemplateId(heroId).setLevel(level);
				rst.addWeis(star);
			}
			for (int heroId : (int[]) shows.get(Integer.valueOf(1))) {
				level = r.getLevel(heroId);
				if ((level < 0) && (player.getBags().getItemCount(heroId) < 1)) {
					continue;
				}
				if (level < 0)
					level = 0;
				star = PbCommons.Star.newBuilder();
				star.setWarriorTemplateId(heroId).setLevel(level);
				rst.addShus(star);
			}
			for (int heroId : (int[]) shows.get(Integer.valueOf(2))) {
				level = r.getLevel(heroId);
				if ((level < 0) && (player.getBags().getItemCount(heroId) < 1)) {
					continue;
				}
				if (level < 0)
					level = 0;
				star = PbCommons.Star.newBuilder();
				star.setWarriorTemplateId(heroId).setLevel(level);
				rst.addWus(star);
			}
			for (int heroId : (int[]) shows.get(Integer.valueOf(3))) {
				level = r.getLevel(heroId);
				if ((level < 0) && (player.getBags().getItemCount(heroId) < 1)) {
					continue;
				}
				if (level < 0)
					level = 0;
				star = PbCommons.Star.newBuilder();
				star.setWarriorTemplateId(heroId).setLevel(level);
				rst.addQuns(star);
			}
		} catch (Exception e) {
			e.printStackTrace();
			rst.setResult(false);
			rst.setErrInfo("服务器繁忙，请稍后再试");
		}

		player.send(2134, rst.build());
	}

	public int getMaxExp(int heroId, int level) {
		if (level < 0)
			level = 0;
		Star star = (Star) stars.get(Integer.valueOf(heroId));
		if ((level >= 50) || (star == null)) {
			return -1;
		}

		Integer[] exps = ((ExpService) Platform.getServiceManager().get(ExpService.class)).getExps(star.expId);

		return exps[level].intValue();
	}

	public int getRatio(int heroId, int level, int exp) {
		if (level < 0)
			level = 0;
		Star star = (Star) stars.get(Integer.valueOf(heroId));
		if ((level >= 50) || (star == null)) {
			return -1;
		}

		int base = ((int[]) ratios.get(Integer.valueOf(star.ratioId)))[level];
		int add = 500000 / (getMaxExp(heroId, level) - exp);
		int rst = base + add;
		return ((rst > 10000) ? 10000 : rst);
	}

	private void info(Player player) {
		PbDown.StarInfoRst.Builder rst = PbDown.StarInfoRst.newBuilder();
		rst.setResult(true);
		StarRecord r = player.getStarRecord();
		int heroId = r.getHeroId();
		rst.setWarriorTemplateId(heroId);
		if (heroId != -1) {
			int exp = r.getExp(heroId);
			Attributes a = r.getAttributes(heroId);
			if (a == null) {
				a = new Attributes();
			}
			rst.setAttributes(a.genAttribute());
			rst.setTrainAttributes(r.getTrainAttribues(heroId).genAttribute());
			rst.setExp(exp).setLevel(r.getLevel(heroId)).setMaxExp(getMaxExp(heroId, r.getLevel(heroId)))
					.setRate(getRatio(heroId, r.getLevel(heroId), exp));
		}
		for (int i = 0; i < 4; ++i) {
			for (int h : (int[]) shows.get(Integer.valueOf(i))) {
				int level = r.getLevel(h);
				if ((level >= 0) || (player.getBags().getItemCount(h) > 0)) {
					if (level < 0)
						level = 0;
					rst.addHeros(PbCommons.Star.newBuilder().setWarriorTemplateId(h).setLevel(level));
				}
			}
		}
		player.send(2132, rst.build());
	}

	private void select(Player player, int heroId) {
		PbDown.StarSelecteRst.Builder rst = PbDown.StarSelecteRst.newBuilder();
		rst.setResult(true);
		StarRecord r = player.getStarRecord();
		if (stars.get(Integer.valueOf(heroId)) != null) {
			r.setHeroId(heroId);
			int level = r.getLevel(heroId);
			if (level < 0)
				level = 0;
			rst.setLevel(level);
			int exp = r.getExp(heroId);
			if (heroId != -1) {
				rst.setExp(exp).setMaxExp(getMaxExp(heroId, level)).setRatio(getRatio(heroId, level, exp));
				Attributes a = r.getAttributes(heroId);
				if (a == null) {
					a = new Attributes();
				}
				rst.setAttri(a.genAttribute());
				rst.setTrainAttri(r.getTrainAttribues(heroId).genAttribute());
			}
		} else {
			rst.setResult(false);
			rst.setErrInfo("该小兵毫无名气，不能给他赠送");
		}
		player.send(2146, rst.build());
	}

	public int[] getEventCodes() {
		return new int[] { 3003 };
	}

	public void handleEvent(Event event) {
		if ((event.type != 3003) || (event.params.length != 3))
			return;
		Object playerObj = event.params[0];
		Object nameObj = event.params[1];
		Object staminaObj = event.params[2];
		if ((playerObj instanceof Player) && (nameObj instanceof String) && (staminaObj instanceof String)) {
			PbDown.StarAchievePush builder = PbDown.StarAchievePush.newBuilder().setResult(true)
					.setAchieve((String) nameObj).setAddStamina((String) staminaObj).build();
			Player player = (Player) playerObj;
			player.send(3032, builder);
		}
	}
}
