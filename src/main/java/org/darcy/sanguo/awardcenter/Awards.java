package org.darcy.sanguo.awardcenter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;

import sango.packet.PbAwards;

public class Awards {
	public static final int MAX_REMAINING = 1209600000;
	private int playerId;
	private AwardInfo info = new AwardInfo();
	private int awardIdCount;
	private AtomicInteger awardIdGen;

	public Awards() {
		this.awardIdGen = new AtomicInteger(this.awardIdCount);
	}

	public void init() {
		this.awardIdGen = new AtomicInteger(this.awardIdCount);
	}

	public void save() {
		((DbService) Platform.getServiceManager().get(DbService.class)).update(this);
	}

	public int getId() {
		this.awardIdCount = this.awardIdGen.incrementAndGet();
		return this.awardIdCount;
	}

	public int getAwardCount() {
		return this.info.getAwards().size();
	}

	public void addAward(Award award, int playerId) {
		int id = getId();
		award.setId(id);
		this.info.getAwards().put(Integer.valueOf(id), award);
		Platform.getLog().logGetAward(playerId, award);
	}

	public Award getAward(int id) {
		return ((Award) this.info.getAwards().get(Integer.valueOf(id)));
	}

	public void removeAward(int id) {
		this.info.getAwards().remove(Integer.valueOf(id));
	}

	public boolean accept(int id, Player player) {
		Award award = getAward(id);
		if (award == null) {
			return false;
		}
		if (!(award.isValid())) {
			removeAward(id);
			return false;
		}
		award.award(player);
		removeAward(id);
		return true;
	}

	public boolean acceptAll(Player player) {
		Map map = new HashMap(this.info.getAwards());
		Iterator itx = map.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			accept(id, player);
		}
		return true;
	}

	public void update() {
		Map map = new HashMap(this.info.getAwards());
		Iterator itx = map.keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			Award award = (Award) map.get(Integer.valueOf(id));
			if (!(award.isValid()))
				removeAward(id);
		}
	}

	public PbAwards.AwardsInfo genAwardsInfo() {
		PbAwards.AwardsInfo.Builder builder = PbAwards.AwardsInfo.newBuilder();
		Iterator itx = this.info.getAwards().keySet().iterator();
		while (itx.hasNext()) {
			int id = ((Integer) itx.next()).intValue();
			Award award = (Award) this.info.getAwards().get(Integer.valueOf(id));
			if (!(award.isValid()))
				itx.remove();
			else {
				builder.addInfos(award.genAwardInfo());
			}
		}
		return builder.build();
	}

	public int getPlayerId() {
		return this.playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public AwardInfo getInfo() {
		return this.info;
	}

	public void setInfo(AwardInfo info) {
		this.info = info;
	}

	public int getAwardIdCount() {
		return this.awardIdCount;
	}

	public void setAwardIdCount(int awardIdCount) {
		this.awardIdCount = awardIdCount;
	}
}
