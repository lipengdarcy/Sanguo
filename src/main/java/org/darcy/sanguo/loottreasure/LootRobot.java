package org.darcy.sanguo.loottreasure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.hero.Warriors;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.robot.RobotData;
import org.darcy.sanguo.service.AccountService;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbUser;

public class LootRobot {
	private static Random random = new Random();
	public RobotData data;
	public int id;
	public int level;
	public String name;
	public int btlCapability;
	public List<Integer> ids = new ArrayList<Integer>();

	private static String[] lasts = { "杨", "黄", "邢", "曹", "蔡", "李", "曲", "郭", "王", "吴", "张", "周", "晁", "齐" };
	private static String[] names = { "光", "卫", "东", "侠", "晓", "慧", "嘉", "维", "翾", "耀", "霆", "树", "楠", "含", "韵", "浩",
			"逸", "波", "驰", "小", "军", "宇", "新", "林", "正", "昊" };

	public LootRobot(RobotData data, int id, int level, String name) {
		this.data = data;
		this.id = id;
		this.level = level;
		int gender = (Math.random() < 0.5D) ? 2 : 1;
		this.name = name;

		int selectIndex = data.getMainWarriorId(gender);
		this.ids.add(Integer.valueOf(selectIndex));

		for (int j = 0; j < data.warriorNum; ++j) {
			int[] warriorPool = null;
			if (j < data.purpleNum) {
				warriorPool = data.purplePool;
			} else if (j < data.purpleNum + data.blueNum) {
				warriorPool = data.bluePool;
			} else {
				double rnd = random.nextInt(2);
				if (rnd == 0.0D)
					warriorPool = data.greenPool;
				else {
					warriorPool = data.whitePool;
				}
			}
			int templateId = warriorPool[random.nextInt(warriorPool.length)];
			this.ids.add(Integer.valueOf(templateId));
		}
	}

	public Player getPlayer() {
		Player player = new Player();
		player.setName(this.name);
		ItemTemplate template = ItemService.getItemTemplate(((Integer) this.ids.get(0)).intValue());
		MainWarrior warrior = new MainWarrior(template, player.getBags().getNewItemId());
		player.getBags().addItem(warrior, 1, "lootrobot");
		Warriors w = new Warriors(warrior);
		player.setWarriors(w);
		player.getCoupRecord().init(player);

		w.getMainWarrior().setLevel((int) (this.level / this.data.warriorLevelRatio));
		for (int i = 1; i < this.ids.size(); ++i) {
			int templateId = ((Integer) this.ids.get(i)).intValue();
			Warrior tmp = (Warrior) ItemService.generateItem(templateId, player);
			player.getBags().addItem(tmp, 1, "lootrobot");

			w.addWarrior(tmp, i + 1);
			tmp.setLevel((int) (this.level / this.data.warriorLevelRatio));
			tmp.refreshTalents();
		}
		w.refresh(false);
		return player;
	}

	public PbUser.MiniUser genMiniUser() {
		PbUser.MiniUser.Builder builder = PbUser.MiniUser.newBuilder();
		builder.setId(this.id);
		builder.setName(this.name);
		builder.setLevel(this.level);
		builder.setBtlCapability(this.btlCapability);
		builder.addAllWarriors(this.ids);
		builder.setMainWarriorId(((Integer) this.ids.get(0)).intValue());
		return builder.build();
	}

	public static List<String> genName(int size) {
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < size; ++i) {
			int gender = (Math.random() < 0.5D) ? 2 : 1;
			String name = ((AccountService) Platform.getServiceManager().get(AccountService.class)).genName(gender);
			if (list.contains(name)) {
				--i;
			} else {
				list.add(name);
			}
		}
		return list;
	}
}
