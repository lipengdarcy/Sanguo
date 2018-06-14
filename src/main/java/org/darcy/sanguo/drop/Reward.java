package org.darcy.sanguo.drop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.item.Item;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbCommons;

public class Reward {
	public int type;
	public int count;
	public ItemTemplate template;

	public Reward(int type, int count, ItemTemplate item) {
		this.type = type;
		this.count = count;
		this.template = item;
	}

	public Reward(String rewardStr) {
		if ((rewardStr != null) && (!(rewardStr.trim().equals(""))) && (!(rewardStr.trim().equals("-1")))) {
			String[] params = rewardStr.split("\\|");
			if (params.length == 3) {
				int type = Integer.parseInt(params[0]);
				int id = Integer.parseInt(params[1]);
				int count = Integer.parseInt(params[2]);
				if (type != 0) {
					this.type = type;
					this.count = count;
				} else {
					ItemTemplate template = ItemService.getItemTemplate(id);
					if (template != null) {
						this.type = type;
						this.count = count;
						this.template = template;
					} else {
						throw new RuntimeException("new reward item can't find");
					}
				}
			}
		}
	}

	public String getStr() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.type).append("|").append((this.template == null) ? -1 : this.template.id).append("|")
				.append(this.count);
		return sb.toString();
	}

	public Reward copy() {
		return new Reward(this.type, this.count, this.template);
	}

	public boolean isSame(Reward r) {
		if (this.type == r.type) {
			if (this.type == 0) {
				if (this.template.id != r.template.id)
					return false;
				return true;
			}

			return true;
		}

		return false;
	}

	public static List<Reward> mergeReward(List<Reward> list) {
		Reward[] array = new Reward[list.size()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = ((Reward) list.get(i));
		}
		return mergeReward(array);
	}

	public static List<Reward> mergeReward(Reward[] rewards) {
		List<Reward> result = new ArrayList<Reward>();
		if (rewards != null) {
			for (Reward r : rewards) {
				boolean flag = false;
				for (Reward rr : result) {
					if (r.isSame(rr)) {
						rr.count += r.count;
						flag = true;
						break;
					}
				}
				if (!(flag)) {
					result.add(r.copy());
				}
			}
		}
		return result;
	}

	public void remove(Player player, String optType) {
		int[] array = new int[0];
		remove(player, optType, array);
	}

	public void remove(Player player, String optType, List<Integer> ids) {
		int[] array = new int[ids.size()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = ((Integer) ids.get(i)).intValue();
		}
		remove(player, optType, array);
	}

	public void remove(Player player, String optType, int[] ids) {
		if (this.type == 2) {
			player.decMoney(this.count, optType);
		} else if (this.type == 0) {
			List needItemList = player.getBags().getItemByTemplateId(this.template.id);
			Collections.sort(needItemList, new Comparator<Item>() {
				@Override
				public int compare(Item o1, Item o2) {
					return (o1.getLevel() - o2.getLevel());
				}

			});
			if (needItemList != null)
				if (!(Item.isCumulative(this.template.type))) {
					int count = 0;
					for (int i = 0; i < needItemList.size(); ++i) {
						if ((ids != null) && (ids.length > 0)) {
							boolean flag = false;
							for (int id : ids) {
								if (((Item) needItemList.get(i)).getId() == id) {
									flag = true;
									break;
								}
							}
							if (flag) {
								continue;
							}
						}
						player.getBags().removeItem(((Item) needItemList.get(i)).getId(), this.template.id, 1, optType);
						++count;
						if (count == this.count)
							return;
					}
				} else {
					player.getBags().removeItem(0, this.template.id, this.count, optType);
				}
		} else if (this.type == 3) {
			player.decJewels(this.count, optType);
		} else if (this.type == 4) {
			player.decExp(this.count);
		} else if (this.type == 6) {
			player.decStamina(this.count, optType);
		} else if (this.type == 5) {
			player.decVitality(this.count, optType);
		} else if (this.type == 7) {
			player.decSpiritJade(this.count, optType);
		} else if (this.type == 8) {
			player.decWarriorSpirit(this.count, optType);
		} else if (this.type == 9) {
			player.decHonor(this.count, optType);
		} else if (this.type == 10) {
			player.decPrestige(this.count, optType);
		} else if (this.type == 11) {
			player.getTacticRecord().setSurplusPoint(player.getTacticRecord().getSurplusPoint() - this.count);
		}
	}

	public RewardResult add(Player player, String optType) {
		RewardResult result = new RewardResult();
		result.type = this.type;
		result.count = this.count;
		if (this.type == 2) {
			player.addMoney(this.count, optType);
		} else if (this.type == 0) {
			ItemTemplate template = this.template;
			if (!(Item.isCumulative(template.type))) {
				for (int i = 0; i < this.count; ++i) {
					Item item = ItemService.generateItem(template, player);
					player.getBags().addItem(item, 1, optType);
					result.items.add(item);
				}
			} else {
				Item item = ItemService.generateItem(template, player);
				player.getBags().addItem(item, this.count, optType);
				result.items.add(item);
			}
		} else if (this.type == 3) {
			player.addJewels(this.count, optType);
		} else if (this.type == 4) {
			player.addExp(this.count, optType);
		} else if (this.type == 6) {
			player.addStamina(this.count, optType);
		} else if (this.type == 5) {
			player.addVitality(this.count, optType);
		} else if (this.type == 7) {
			player.addSpiritJade(this.count, optType);
		} else if (this.type == 8) {
			player.addWarriorSpirit(this.count, optType);
		} else if (this.type == 9) {
			player.addHonor(this.count, optType);
		} else if (this.type == 10) {
			player.addPrestige(this.count, optType);
		} else if (this.type == 11) {
			player.getTacticRecord().addPoint(this.count, player);
		}
		return result;
	}

	public String check(Player player) {
		int[] array = new int[0];
		return check(player, array);
	}

	public String check(Player player, List<Integer> ids) {
		int[] array = new int[ids.size()];
		for (int i = 0; i < array.length; ++i) {
			array[i] = ((Integer) ids.get(i)).intValue();
		}
		return check(player, array);
	}

	public String check(Player player, int[] ids) {
		if (this.type == 2) {
			if (player.getMoney() >= this.count)
				return null;
			return "银币不足";
		}
		if (this.type == 0) {
			int excludeCount = 0;
			if ((!(Item.isCumulative(this.template.type))) && (ids != null) && (ids.length > 0)) {
				for (int id : ids) {
					Item item = player.getBags().getItemById(id, this.template.type);
					if ((item != null) && (item.getTemplateId() == this.template.id)) {
						++excludeCount;
					}
				}
			}

			if (player.getBags().getItemCount(this.template.id) - excludeCount >= this.count)
				return null;
			return "物品不足";
		}
		if (this.type == 3) {
			if (player.getJewels() >= this.count)
				return null;
			return "元宝不足";
		}
		if (this.type == 4) {
			if (player.getExp() >= this.count)
				return null;
			return "经验不足";
		}
		if (this.type == 6) {
			if (player.getStamina() >= this.count)
				return null;
			return "精力不足";
		}
		if (this.type == 5) {
			if (player.getVitality() >= this.count)
				return null;
			return "体力不足";
		}
		if (this.type == 7) {
			if (player.getSpiritJade() >= this.count)
				return null;
			return "魂玉不足，炼化5星及以上武将获得更多魂玉";
		}
		if (this.type == 8) {
			if (player.getWarriorSpirit() >= this.count)
				return null;
			return "战功不足";
		}
		if (this.type == 9) {
			if (player.getHonor() >= this.count)
				return null;
			return "荣誉不足，参与争霸赛能够获得更多荣誉";
		}
		if (this.type == 10) {
			if (player.getPrestige() >= this.count)
				return null;
			return "声望不足，您可在竞技场或世界Boss战中获得声望";
		}
		if (this.type == 11) {
			if (player.getTacticRecord().getSurplusPoint() >= this.count)
				return null;
			return "声望不足，您可在竞技场或世界Boss战中获得声望";
		}

		Platform.getLog().logError("SynthItemEffect check use error : other type don't opt, type:" + this.type);
		return "类型错误";

	}

	public static Reward readObject(ObjectInputStream in) {
		try {
			int type = in.readInt();
			int count = in.readInt();
			int templateId = in.readInt();
			ItemTemplate template = ItemService.getItemTemplate(templateId);
			Reward reward = new Reward(type, count, template);
			return reward;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(this.type);
		out.writeInt(this.count);
		if (this.template == null)
			out.writeInt(-1);
		else
			out.writeInt(this.template.id);
	}

	public PbCommons.PbReward.Builder genPbReward() {
		PbCommons.PbReward.Builder builder = PbCommons.PbReward.newBuilder();
		builder.setType(this.type);
		builder.setCount(this.count);
		if (this.template != null)
			builder.setTemplateId(this.template.id);
		else {
			builder.setTemplateId(-1);
		}
		return builder;
	}

	public static PbCommons.PbReward.Builder genPbReward(int type, int count) {
		return PbCommons.PbReward.newBuilder().setType(type).setCount(count).setTemplateId(-1);
	}

	public static PbCommons.PbReward.Builder genPbReward(int type, int count, int templateId) {
		return PbCommons.PbReward.newBuilder().setType(type).setCount(count).setTemplateId(templateId);
	}

	public String toString() {
		switch (this.type) {
		case 9:
			return "荣誉" + "x" + this.count;
		case 3:
			return "元宝" + "x" + this.count;
		case 2:
			return "银币" + "x" + this.count;
		case 4:
			return "经验" + "x" + this.count;
		case 10:
			return "声望" + "x" + this.count;
		case 7:
			return "魂玉" + "x" + this.count;
		case 6:
			return "耐力" + "x" + this.count;
		case 5:
			return "体力" + "x" + this.count;
		case 8:
			return "将魂" + "x" + this.count;
		case 11:
			return "阵法点数" + "x" + this.count;
		case 0:
			return this.template.name + "x" + this.count;
		case 1:
		}
		return "";
	}

	public class RewardResult {
		public int type;
		public int count;
		public List<Item> items;

		public RewardResult() {
			this.items = new ArrayList();
		}
	}
}
