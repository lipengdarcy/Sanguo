package org.darcy.sanguo.globaldrop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.drop.DropGroup;
import org.darcy.sanguo.drop.DropService;
import org.darcy.sanguo.drop.Gain;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbGlobalDrop;

public class GlobalDrop implements PlayerBlobEntity {
	private static final long serialVersionUID = -915652862312291397L;
	private static final int version = 1;
	private int turnCardCount;
	private int mapDropCount;
	private int openBoxCount;

	public Reward dropTurnCardReward(Player p) {
		boolean isDrop;
		this.turnCardCount += 1;

		int actualDrop = -1;
		int lootMoney = 0;

		if (TurnCardDropData.besureDrop.containsKey(Integer.valueOf(this.turnCardCount))) {
			actualDrop = ((Integer) TurnCardDropData.besureDrop.get(Integer.valueOf(this.turnCardCount))).intValue();
			isDrop = true;
		} else {
			double random = Math.random();
			if (random < TurnCardDropData.lootMoneyRatio / 10000.0D) {
				double random2 = Math.random();
				while (random2 > 0.4D) {
					random2 -= 0.4D;
				}
				random2 += 0.8D;
				lootMoney = (int) ((1000 + p.getLevel() * 50) * random2);
				isDrop = false;
			} else {
				actualDrop = TurnCardDropData.getNormalDrop(p.getLevel());
				isDrop = true;
			}
		}

		if (isDrop) {
			Gain actual = (Gain) ((DropService) Platform.getServiceManager().get(DropService.class))
					.getDropGroup(actualDrop).genGains(p).get(0);
			actual.gain(p, "turncard");
			return actual.newReward();
		}
		p.addMoney(lootMoney, "turncard");
		return new Reward(2, lootMoney, null);
	}

	public PbGlobalDrop.TurnCard.Builder dropTurnCard(Player player, Player rival) {
		boolean isDrop;
		this.turnCardCount += 1;

		int actualDrop = -1;
		int lootMoney = 0;

		if (TurnCardDropData.besureDrop.containsKey(Integer.valueOf(this.turnCardCount))) {
			actualDrop = ((Integer) TurnCardDropData.besureDrop.get(Integer.valueOf(this.turnCardCount))).intValue();
			isDrop = true;
		} else {
			double random = Math.random();
			if (random < TurnCardDropData.lootMoneyRatio / 10000.0D) {
				if (rival.getId() > 0) {
					lootMoney = (int) (rival.getMoney() * TurnCardDropData.lootMoneyPercent / 10000.0D);
					lootMoney = Math.min(lootMoney, TurnCardDropData.lootMoneyMax);
				} else {
					double random2 = Math.random();
					while (random2 > 0.4D) {
						random2 -= 0.4D;
					}
					random2 += 0.8D;
					lootMoney = (int) ((1000 + rival.getLevel() * 50) * random2);
				}
				isDrop = false;
			} else {
				actualDrop = TurnCardDropData.getNormalDrop(player.getLevel());
				isDrop = true;
			}
		}
		int showDrop = TurnCardDropData.showDrop;
		DropGroup showGroup = ((DropService) Platform.getServiceManager().get(DropService.class))
				.getDropGroup(showDrop);
		Gain show1 = (Gain) showGroup.genGains(player).get(0);
		Gain show2 = (Gain) showGroup.genGains(player).get(0);
		while (show2.equals(show1)) {
			show2 = (Gain) showGroup.genGains(player).get(0);
		}

		PbGlobalDrop.TurnCard.Builder builder = PbGlobalDrop.TurnCard.newBuilder();
		builder.addShowDrop(show1.genPbReward());
		builder.addShowDrop(show2.genPbReward());
		if (isDrop) {
			Gain actual = (Gain) ((DropService) Platform.getServiceManager().get(DropService.class))
					.getDropGroup(actualDrop).genGains(player).get(0);
			builder.setType(1);
			builder.setDrop(actual.genPbReward());

			actual.gain(player, "turncard");
		} else {
			builder.setType(2);
			int addMoney = Math.max(1000, lootMoney);
			builder.setMoney(addMoney);

			player.addMoney(addMoney, "turncard");
			if (rival.getId() > 0) {
				rival.decMoney(lootMoney, "turncard");
			}
		}
		return builder;
	}

	public void mapDrop(Player player, int num) {
		int actualDrop;
		List<PbCommons.PbReward.Builder> drops = new ArrayList();
		PbGlobalDrop.MapDrop.Builder builder = PbGlobalDrop.MapDrop.newBuilder();
		for (int i = 0; i < num; ++i) {
			actualDrop = -1;
			this.mapDropCount += 1;
			if (MapDropData.besureDrop.containsKey(Integer.valueOf(this.mapDropCount)))
				actualDrop = ((Integer) MapDropData.besureDrop.get(Integer.valueOf(this.mapDropCount))).intValue();
			else {
				actualDrop = MapDropData.getNormalDrop(player.getLevel());
			}
			List<Gain> list = ((DropService) Platform.getServiceManager().get(DropService.class))
					.getDropGroup(actualDrop).genGains(player);
			if (list.size() > 0) {
				for (Gain gain : list) {
					gain.gain(player, "mapdrop");

					boolean flag = true;
					for (PbCommons.PbReward.Builder b : drops) {
						if (b.getType() == gain.type) {
							if (gain.type == 0) {
								if (b.getTemplateId() != gain.item.getTemplateId())
									continue;
								flag = false;
								b.setCount(b.getCount() + gain.count);
								break;
							}

							flag = false;
							b.setCount(b.getCount() + gain.count);
							break;
						}
					}

					if (flag) {
						drops.add(gain.genPbReward());
					}
				}
			}
		}
		if (drops.size() > 0) {
			for (PbCommons.PbReward.Builder b : drops) {
				builder.addDrop(b.build());
			}
			PbDown.MapDropRst rst = PbDown.MapDropRst.newBuilder().setResult(true).setDrop(builder.build()).build();
			player.send(1134, rst);
		}
	}

	public List<Reward> openBoxDrop(Player player) {
		this.openBoxCount += 1;
		int actualDrop = -1;
		if (OpenBoxDropData.besureDrop.containsKey(Integer.valueOf(this.openBoxCount)))
			actualDrop = ((Integer) OpenBoxDropData.besureDrop.get(Integer.valueOf(this.openBoxCount))).intValue();
		else {
			actualDrop = OpenBoxDropData.normalDrop;
		}
		List<Gain> list = ((DropService) Platform.getServiceManager().get(DropService.class)).getDropGroup(actualDrop)
				.genGains(player);
		List rewardList = new ArrayList();
		if (list.size() > 0) {
			for (Gain gain : list) {
				gain.gain(player, "openbox");
				rewardList.add(gain.newReward());
			}
		}
		if (this.openBoxCount == OpenBoxDropData.clearTime) {
			this.openBoxCount = 0;
		}
		return rewardList;
	}

	public int getTurnCardCount() {
		return this.turnCardCount;
	}

	public void setTurnCardCount(int turnCardCount) {
		this.turnCardCount = turnCardCount;
	}

	public int getMapDropCount() {
		return this.mapDropCount;
	}

	public void setMapDropCount(int mapDropCount) {
		this.mapDropCount = mapDropCount;
	}

	public int getOpenBoxCount() {
		return this.openBoxCount;
	}

	public void setOpenBoxCount(int openBoxCount) {
		this.openBoxCount = openBoxCount;
	}

	private void readObject(ObjectInputStream in) {
		try {
			in.readInt();
			this.turnCardCount = in.readInt();
			this.mapDropCount = in.readInt();
			this.openBoxCount = in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(1);
		out.writeInt(this.turnCardCount);
		out.writeInt(this.mapDropCount);
		out.writeInt(this.openBoxCount);
	}

	public int getBlobId() {
		return 8;
	}
}
