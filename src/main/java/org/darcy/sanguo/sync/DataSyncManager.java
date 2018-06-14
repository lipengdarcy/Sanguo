package org.darcy.sanguo.sync;

import java.util.concurrent.ArrayBlockingQueue;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.hero.Warriors;
import org.darcy.sanguo.player.Player;

import com.google.protobuf.MessageOrBuilder;

import sango.packet.PbDown;
import sango.packet.PbSync;

public class DataSyncManager {
	private ArrayBlockingQueue<MessageOrBuilder> dataSyncs = new ArrayBlockingQueue(1024);

	public void addNumSync(int type, int value) {
		for (MessageOrBuilder builder : this.dataSyncs) {
			if ((!(builder instanceof PbSync.PbNumSync.Builder))
					|| (((PbSync.PbNumSync.Builder) builder).getType() != type))
				continue;
			((PbSync.PbNumSync.Builder) builder).setValue(value);
			return;
		}

		NumSync sync = new NumSync(type, value);
		this.dataSyncs.add(sync.genBuilder());
	}

	public void addActivityCanGet(boolean can) {
		for (MessageOrBuilder builder : this.dataSyncs) {
			if (builder instanceof PbSync.PbActivityCanGet.Builder) {
				((PbSync.PbActivityCanGet.Builder) builder).setCan(can);
				return;
			}
		}
		ActivityCanGetSync sync = new ActivityCanGetSync(can);
		this.dataSyncs.add(sync.genBuilder());
	}

	public void addBagSizeSync(int bagType, int num) {
		for (MessageOrBuilder builder : this.dataSyncs) {
			if (builder instanceof PbSync.PbBagSizeSync.Builder) {
				PbSync.PbBagSizeSync.Builder tmpBuilder = (PbSync.PbBagSizeSync.Builder) builder;
				if (tmpBuilder.getBagType() == bagType) {
					tmpBuilder.setNum(num);
					return;
				}
			}
		}
		BagSizeSync sync = new BagSizeSync(bagType, num);
		this.dataSyncs.add(sync.genBuilder());
	}

	public void addBagSync(int bagType, BagGrid grid, int optType) {
		try {
			for (MessageOrBuilder builder : this.dataSyncs) {
				if (builder instanceof PbSync.PbBagSync.Builder) {
					PbSync.PbBagSync.Builder tmpBuilder = (PbSync.PbBagSync.Builder) builder;
					if ((tmpBuilder.getGrid().getId() != grid.getItem().getId())
							|| (tmpBuilder.getGrid().getTemplateId() != grid.getItem().getTemplateId())
							|| (tmpBuilder.getOptType() != optType))
						continue;
					tmpBuilder.setBagType(bagType);
					tmpBuilder.setOptType(optType);
					tmpBuilder.setGrid(grid.genPbBagGrid());
					return;
				}
			}

			BagSync sync = new BagSync(bagType, grid, optType);
			this.dataSyncs.add(sync.genBuilder());
		} catch (Throwable t) {
			Platform.getLog().logError(t);
		}
	}

	public void addStandsSync(int type, Warriors warriors) {
		for (MessageOrBuilder builder : this.dataSyncs) {
			if (builder instanceof PbSync.PbStandsSync.Builder) {
				PbSync.PbStandsSync.Builder tmpBuilder = (PbSync.PbStandsSync.Builder) builder;
				if (tmpBuilder.getType() == type) {
					if (type == 2)
						tmpBuilder.setStands(warriors.genStandStruct());
					else if (type == 1)
						tmpBuilder.setStages(warriors.genStageStruct());
					else if (type == 3) {
						tmpBuilder.setFriends(warriors.genFriendStruct());
					}
					return;
				}
			}
		}
		StandsSync sync = new StandsSync(type, warriors);
		this.dataSyncs.add(sync.genBuilder());
	}

	public void clear() {
		this.dataSyncs.clear();
	}

	public boolean update(Player player) {
		if (this.dataSyncs.size() > 0) {
			PbSync.Sync.Builder builder = PbSync.Sync.newBuilder();
			MessageOrBuilder build = null;
			label158: while ((build = (MessageOrBuilder) this.dataSyncs.poll()) != null) {
				try {
					if (build instanceof PbSync.PbNumSync.Builder) {
						builder.addNum(((PbSync.PbNumSync.Builder) build).build());
						break label158;
					}
					if (build instanceof PbSync.PbEquipmentSync.Builder) {
						builder.addEquip(((PbSync.PbEquipmentSync.Builder) build).build());
						break label158;
					}
					if (build instanceof PbSync.PbBagSync.Builder) {
						builder.addBag(((PbSync.PbBagSync.Builder) build).build());
						break label158;
					}
					if (build instanceof PbSync.PbBagSizeSync.Builder) {
						builder.addBagSize(((PbSync.PbBagSizeSync.Builder) build).build());
						break label158;
					}
					if (build instanceof PbSync.PbStandsSync.Builder) {
						builder.addStands(((PbSync.PbStandsSync.Builder) build).build());
						break label158;
					}
					if (build instanceof PbSync.PbActivityCanGet.Builder)
						builder.addActivityCanGet(((PbSync.PbActivityCanGet.Builder) build).build());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			PbDown.SyncDataRst.Builder down = PbDown.SyncDataRst.newBuilder();
			down.setSync(builder.build());
			player.send(1012, down.build());
		}
		return false;
	}
}
