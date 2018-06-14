package org.darcy.sanguo.persist;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.activity.ActivityRecord;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.bag.Bags;
import org.darcy.sanguo.boss.BossRecord;
import org.darcy.sanguo.coup.CoupRecord;
import org.darcy.sanguo.destiny.DestinyRecord;
import org.darcy.sanguo.divine.DivineRecord;
import org.darcy.sanguo.exchange.Exchanges;
import org.darcy.sanguo.globaldrop.GlobalDrop;
import org.darcy.sanguo.glory.GloryRecord;
import org.darcy.sanguo.hero.MainWarrior;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.hero.Warriors;
import org.darcy.sanguo.item.ItemTemplate;
import org.darcy.sanguo.loottreasure.LootTreasure;
import org.darcy.sanguo.map.MapRecord;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.player.PropertyPool;
import org.darcy.sanguo.randomshop.RandomShopRecord;
import org.darcy.sanguo.recruit.RecruitRecord;
import org.darcy.sanguo.reward.RewardRecord;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.star.StarRecord;
import org.darcy.sanguo.startcatalog.StarcatalogRecord;
import org.darcy.sanguo.tactic.TacticRecord;
import org.darcy.sanguo.task.TaskRecord;
import org.darcy.sanguo.tower.TowerRecord;
import org.darcy.sanguo.util.SerialUtil;

public class PlayerBlob implements Serializable {
	private static final long serialVersionUID = 3745667579903777277L;
	private WeakReference<Player> player;
	private List<PlayerBlobEntity> blobs = new ArrayList<PlayerBlobEntity>();

	public PlayerBlob() {
	}

	public PlayerBlob(Player player) {
		this.player = new WeakReference<Player>(player);
	}

	public void init(Player referent) {
		if (getPlayer() == null) {
			this.player = new WeakReference<Player>(referent);
		}
		for (PlayerBlobEntity entity : this.blobs) {
			switch (entity.getBlobId()) {
			case 3:
				referent.setLootTreasure((LootTreasure) entity);
				break;
			case 1:
				referent.setWarriors((Warriors) entity);
				break;
			case 2:
				referent.setBags((Bags) entity);
				break;
			case 5:
				referent.setMapRecord((MapRecord) entity);
				break;
			case 4:
				referent.setRecruitRecord((RecruitRecord) entity);
				break;
			case 6:
				referent.setPool((PropertyPool) entity);
				break;
			case 7:
				referent.setExchanges((Exchanges) entity);
				break;
			case 8:
				referent.setGlobalDrop((GlobalDrop) entity);
				break;
			case 9:
				referent.setTowerRecord((TowerRecord) entity);
				break;
			case 10:
				referent.setRandomShop((RandomShopRecord) entity);
				break;
			case 11:
				referent.setBossRecord((BossRecord) entity);
				break;
			case 12:
				referent.setRewardRecord((RewardRecord) entity);
				break;
			case 13:
				referent.setDestinyRecord((DestinyRecord) entity);
				break;
			case 14:
				referent.setTacticRecord((TacticRecord) entity);
				break;
			case 15:
				referent.setCoupRecord((CoupRecord) entity);
				break;
			case 16:
				referent.setDivineRecord((DivineRecord) entity);
				break;
			case 18:
				referent.setTaskRecord((TaskRecord) entity);
				break;
			case 17:
				referent.setStarRecord((StarRecord) entity);
				break;
			case 19:
				referent.setActivityRecord((ActivityRecord) entity);
				break;
			case 20:
				referent.setGloryRecord((GloryRecord) entity);
				break;
			case 21:
				referent.setStarcatalogRecord((StarcatalogRecord) entity);
			}

		}

		this.blobs.clear();
		nullHandle(referent);
	}

	public void nullHandle(Player player) {
		if (player.getRecruitRecord() == null) {
			player.setRecruitRecord(new RecruitRecord());
			Platform.getLog().logWarn("nullHandle,RecruitRecord is null,playerId:" + player.getId());
		}
		if (player.getBags() == null) {
			player.setBags(new Bags());
			Platform.getLog().logWarn("nullHandle,Bags is null,playerId:" + player.getId());
		}
		if (player.getExchanges() == null) {
			player.setExchanges(new Exchanges());
			Platform.getLog().logWarn("nullHandle,Exchanges is null,playerId:" + player.getId());
		}
		if (player.getLootTreasure() == null) {
			player.setLootTreasure(new LootTreasure());
			Platform.getLog().logWarn("nullHandle,LootTreasure is null,playerId:" + player.getId());
		}
		if (player.getGlobalDrop() == null) {
			player.setGlobalDrop(new GlobalDrop());
			Platform.getLog().logWarn("nullHandle,GlobalDrop is null,playerId:" + player.getId());
		}
		if (player.getPool() == null) {
			player.setPool(new PropertyPool());
			Platform.getLog().logWarn("nullHandle,PropertyPool is null,playerId:" + player.getId());
		}
		if (player.getMapRecord() == null) {
			player.setMapRecord(new MapRecord());
			Platform.getLog().logWarn("nullHandle,MapRecord is null,playerId:" + player.getId());
		}
		if (player.getRandomShop() == null) {
			player.setRandomShop(new RandomShopRecord());
			Platform.getLog().logWarn("nullHandle,RandomShopRecord is null,playerId:" + player.getId());
		}
		if (player.getBossRecord() == null) {
			player.setBossRecord(new BossRecord());
			Platform.getLog().logWarn("nullHandle,BossRecord is null,playerId:" + player.getId());
		}
		if (player.getTowerRecord() == null) {
			player.setTowerRecord(new TowerRecord());
			Platform.getLog().logWarn("nullHandle,TowerRecord is null,playerId:" + player.getId());
		}
		if (player.getRewardRecord() == null) {
			player.setRewardRecord(new RewardRecord());
			Platform.getLog().logWarn("nullHandle,RewardRecord is null,playerId:" + player.getId());
		}
		if (player.getDestinyRecord() == null) {
			player.setDestinyRecord(new DestinyRecord());
			Platform.getLog().logWarn("nullHandle,DestinyRecord is null,playerId:" + player.getId());
		}
		if (player.getDivineRecord() == null) {
			player.setDivineRecord(new DivineRecord());
			Platform.getLog().logWarn("nullHandle,DivineRecord is null,playerId:" + player.getId());
		}
		if (player.getCoupRecord() == null) {
			player.setCoupRecord(new CoupRecord());
			Platform.getLog().logWarn("nullHandle,CoupRecord is null,playerId:" + player.getId());
		}
		if (player.getTacticRecord() == null) {
			player.setTacticRecord(new TacticRecord());
			Platform.getLog().logWarn("nullHandle,TacticRecord is null,playerId:" + player.getId());
		}
		if (player.getTaskRecord() == null) {
			player.setTaskRecord(new TaskRecord());
			player.getTaskRecord().refreshNewTask(player);
			Platform.getLog().logWarn("nullHandle,TaskRecord is null,playerId:" + player.getId());
		}
		if (player.getStarRecord() == null) {
			player.setStarRecord(new StarRecord());
			Platform.getLog().logWarn("nullHandle,StarRecord is null,playerId:" + player.getId());
		}
		if (player.getActivityRecord() == null) {
			player.setActivityRecord(new ActivityRecord());
			Platform.getLog().logWarn("nullHandle,ActivityRecord is null,playerId:" + player.getId());
		}
		if (player.getGloryRecord() == null) {
			player.setGloryRecord(new GloryRecord());
			Platform.getLog().logWarn("nullHandle,GloryRecord is null,playerId:" + player.getId());
		}
		if ((player.getWarriors() == null) || (!(player.getWarriors().isCorrectBlob()))) {
			List<BagGrid> list = player.getBags().getBag(2).getGrids();
			boolean flag = true;
			for (BagGrid grid : list) {
				if ((grid == null) || (grid.getItem() == null) || (grid.getItem().getTemplate().type != 2))
					continue;
				Warrior warrior = (Warrior) grid.getItem();
				if (warrior.isMainWarrior()) {
					Warriors ws = new Warriors((MainWarrior) warrior);
					player.setWarriors(ws);
					flag = false;
					Platform.getLog().logWarn(
							"nullHandle,Warriors is null,find main warrior from bag,playerId:" + player.getId());
					break;
				}
			}

			if (flag) {
				ItemTemplate template = ItemService.getItemTemplate(11);
				MainWarrior warrior = new MainWarrior(template, player.getBags().getNewItemId());
				player.getBags().addItem(warrior, 1, "blobinit");
				Warriors w = new Warriors(warrior);
				player.setWarriors(w);
				Platform.getLog().logWarn(
						"nullHandle,Warriors is null,no main warrior in bag,create a female main warrior,playerId:"
								+ player.getId());
			}
		}
		if (player.getStarcatalogRecord() == null) {
			player.setStarcatalogRecord(new StarcatalogRecord());
			Platform.getLog().logWarn("nullHandle,StarcatalogRecord is null,playerId:" + player.getId());
		}
	}

	public Player getPlayer() {
		if (this.player != null) {
			return ((Player) this.player.get());
		}
		return null;
	}

	private void readObject(ObjectInputStream in) {
		this.blobs = new ArrayList<PlayerBlobEntity>();
		try {
			int total = in.readInt();
			for (int i = 0; i < total; ++i) {
				int blobId = in.readInt();
				int size = in.readInt();
				if (size > 0) {
					byte[] bytes = new byte[size];
					in.readFully(bytes);
					PlayerBlobEntity blob = (PlayerBlobEntity) SerialUtil.deSerialize(bytes);
					if (blob != null)
						this.blobs.add(blob);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		Player player = getPlayer();
		out.writeInt(PlayerBlobService.blobIds.size());
		for (Integer blobId : PlayerBlobService.blobIds) {
			PlayerBlobEntity entity = null;
			switch (blobId.intValue()) {
			case 3:
				entity = player.getLootTreasure();
				break;
			case 1:
				entity = player.getWarriors();
				break;
			case 2:
				entity = player.getBags();
				break;
			case 5:
				entity = player.getMapRecord();
				break;
			case 4:
				entity = player.getRecruitRecord();
				break;
			case 6:
				entity = player.getPool();
				break;
			case 7:
				entity = player.getExchanges();
				break;
			case 8:
				entity = player.getGlobalDrop();
				break;
			case 9:
				entity = player.getTowerRecord();
				break;
			case 10:
				entity = player.getRandomShop();
				break;
			case 11:
				entity = player.getBossRecord();
				break;
			case 12:
				entity = player.getRewardRecord();
				break;
			case 13:
				entity = player.getDestinyRecord();
				break;
			case 14:
				entity = player.getTacticRecord();
				break;
			case 15:
				entity = player.getCoupRecord();
				break;
			case 16:
				entity = player.getDivineRecord();
				break;
			case 18:
				entity = player.getTaskRecord();
				break;
			case 17:
				entity = player.getStarRecord();
				break;
			case 19:
				entity = player.getActivityRecord();
				break;
			case 20:
				entity = player.getGloryRecord();
				break;
			case 21:
				entity = player.getStarcatalogRecord();
			}

			if (entity != null) {
				out.writeInt(blobId.intValue());
				byte[] bytes = SerialUtil.serialize(entity);
				out.writeInt(bytes.length);
				out.write(bytes);
			}
		}
	}
}
