package org.darcy.sanguo.startcatalog;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.darcy.sanguo.attri.Attri;
import org.darcy.sanguo.attri.Attributes;
import org.darcy.sanguo.bag.BagGrid;
import org.darcy.sanguo.function.Function;
import org.darcy.sanguo.hero.HeroTemplate;
import org.darcy.sanguo.hero.Warrior;
import org.darcy.sanguo.persist.PlayerBlobEntity;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.service.common.ItemService;

import sango.packet.PbCommons;

public class StarcatalogRecord implements PlayerBlobEntity {
	private static final long serialVersionUID = -8352553340241149687L;
	private static final int version = 1;
	private Map<Integer, WarriorInfo> infos = new HashMap();
	private Map<Integer, Boolean> eventids = new HashMap();

	public Map<Integer, WarriorInfo> getInfos() {
		return this.infos;
	}

	public Map<Integer, Boolean> getEventids() {
		return this.eventids;
	}

	public WarriorInfo getWarrior(int id) {
		return ((WarriorInfo) this.infos.get(Integer.valueOf(id)));
	}

	public void activeCatalog(Player p) {
		Warrior w;
		for (BagGrid grid : p.getBags().getBag(2).getGrids()) {
			if ((grid != null) && (grid.getCount() > 0)) {
				w = (Warrior) grid.getItem();
				addStarcatalog(w);
			}

		}

		Set<Integer> temp = new HashSet(p.getStarRecord().getHeroLevels().keySet());
		temp.removeAll(this.infos.keySet());
		for (Integer i : temp) {
			addStarcatalog((HeroTemplate) ItemService.getItemTemplate(i.intValue()), p);
		}

		check(p);
	}

	public Attributes eventAttrAmount() {
		Attributes as = new Attributes();
		for (Integer id : this.eventids.keySet()) {
			if (((Boolean) this.eventids.get(id)).booleanValue()) {
				StarcatalogEvent e = (StarcatalogEvent) StarcatalogService.events.get(id);
				if (e != null) {
					List<Attri> attris = e.getAttrs();
					if ((attris != null) && (attris.size() > 0)) {
						for (Attri a : attris) {
							as.addAttri(a);
						}
					}
				}
			}
		}
		return as;
	}

	public Attributes attrAdd(int group, int favor) {
		Attributes as = new Attributes();
		Attri[] attris = (Attri[]) StarcatalogService.attrgroups.get(Integer.valueOf(group));
		for (int i = 0; i <= favor; ++i) {
			as.addAttri(attris[i]);
		}
		return as;
	}

	public List<PbCommons.StarCatalogInfo> genAllStarcatalog() {
		List infos = new ArrayList();
		PbCommons.StarCatalogInfo.Builder wei = PbCommons.StarCatalogInfo.newBuilder().setCamp(1);
		PbCommons.StarCatalogInfo.Builder shu = PbCommons.StarCatalogInfo.newBuilder().setCamp(2);
		PbCommons.StarCatalogInfo.Builder wu = PbCommons.StarCatalogInfo.newBuilder().setCamp(3);
		PbCommons.StarCatalogInfo.Builder qun = PbCommons.StarCatalogInfo.newBuilder().setCamp(4);

		for (Starcatalog starcatalog : StarcatalogService.catalogs.values()) {
			WarriorInfo wi = (WarriorInfo) this.infos.get(Integer.valueOf(starcatalog.heroid));
			PbCommons.StarCatalog.Builder b = PbCommons.StarCatalog.newBuilder().setId(starcatalog.heroid).setMaxFavor(
					((Attri[]) StarcatalogService.attrgroups.get(Integer.valueOf(starcatalog.attrid))).length);
			if (wi == null) {
				b.setFavor(0)
						.setCost(((Integer[]) StarcatalogService.costGroups.get(Integer.valueOf(starcatalog.costid)))[0]
								.intValue())
						.setNextattr(attrAdd(starcatalog.attrid, 0).genAttribute()).setHas(false)
						.setItemCost(((Integer[]) StarcatalogService.costGroups
								.get(Integer.valueOf(starcatalog.itemCostid)))[0].intValue());
			} else {
				b.setFavor(wi.favor).setHas(true);
				if (wi.favor > 0) {
					b.setCurattr(attrAdd(starcatalog.attrid, wi.favor - 1).genAttribute());
				}

				if ((wi.favor < ((Attri[]) StarcatalogService.attrgroups.get(Integer.valueOf(
						((Starcatalog) StarcatalogService.catalogs.get(Integer.valueOf(wi.id))).attrid))).length)
						&& (wi.favor < 100)) {
					b.setNextattr(attrAdd(starcatalog.attrid, wi.favor).genAttribute())
							.setCost(((Integer[]) StarcatalogService.costGroups
									.get(Integer.valueOf(starcatalog.costid)))[wi.favor].intValue())
							.setItemCost(((Integer[]) StarcatalogService.costGroups
									.get(Integer.valueOf(starcatalog.itemCostid)))[wi.favor].intValue());
				}
			}

			HeroTemplate ht = (HeroTemplate) ItemService.templates.get(Integer.valueOf(starcatalog.heroid));
			switch (ht.camp) {
			case 1:
				wei.addScs(b);
				break;
			case 2:
				shu.addScs(b);
				break;
			case 3:
				wu.addScs(b);
				break;
			case 4:
				qun.addScs(b);
			}

		}

		infos.add(wei.build());
		infos.add(shu.build());
		infos.add(wu.build());
		infos.add(qun.build());

		return infos;
	}

	public int curWarriors() {
		return this.infos.size();
	}

	public Attributes attrAmount() {
		Attributes as = new Attributes();
		for (WarriorInfo wi : this.infos.values()) {
			Starcatalog catalog = (Starcatalog) StarcatalogService.catalogs.get(Integer.valueOf(wi.id));
			if (catalog != null)
				Attributes.addAttr(new Attributes[] { as, attrAdd(catalog.attrid, wi.favor - 1) });
		}
		return as;
	}

	public int favorAmount() {
		int count = 0;
		for (WarriorInfo wi : this.infos.values())
			count += wi.favor;
		return count;
	}

	private boolean addStarcatalog(HeroTemplate template, Player p) {
		boolean flag = false;
		WarriorInfo info = (WarriorInfo) this.infos.get(Integer.valueOf(template.id));
		if ((info == null) && (StarcatalogService.catalogs.containsKey(Integer.valueOf(template.id)))) {
			info = new WarriorInfo();
			info.id = template.id;
			info.camp = template.camp;
			info.favor = 1;
			this.infos.put(Integer.valueOf(info.id), info);

			Map warriors = p.getWarriors().getWarriors();
			Iterator itx = warriors.keySet().iterator();
			while (itx.hasNext()) {
				((Warrior) warriors.get(itx.next())).refreshAttributes(true);
			}

			flag = true;
		}
		return flag;
	}

	public void addStarcatalog(Warrior w) {
		Player p = w.getPlayer();
		if ((p != null) && (addStarcatalog((HeroTemplate) w.getTemplate(), p)))
			check(p);
	}

	public void check(Player p) {
		Map events = StarcatalogService.events;
		HashSet<Integer> clone = new HashSet(events.keySet());
		clone.removeAll(this.eventids.keySet());
		boolean trigger = false;
		for (Integer i : clone)
			if (checkEvent((StarcatalogEvent) events.get(i)))
				trigger = true;
		if (trigger)
			Function.notifyMainNum(p, 45, 1);
	}

	private boolean checkEvent(StarcatalogEvent e) {
		WarriorInfo wi;
		if ((e == null) || (this.eventids.containsKey(Integer.valueOf(e.getId())))) {
			return false;
		}
		boolean canAdd = false;
		switch (e.getType()) {
		case 3:
			int camp = Integer.parseInt((String) e.getParams().get(0));
			int number = Integer.parseInt((String) e.getParams().get(1));
			int count = 0;
			for (WarriorInfo wi1 : this.infos.values()) {
				if (wi1.camp == camp)
					++count;
			}
			if (count >= number)
				canAdd = true;
			break;
		case 2:
			String warriors = (String) e.getParams().get(0);
			Object ids = new HashSet();
			for (String w : warriors.split(",")) {
				((Set) ids).add(Integer.valueOf(Integer.parseInt(w)));
			}
			if (this.infos.keySet().containsAll((Collection) ids))
				canAdd = true;
			break;
		case 1:
			int num = Integer.parseInt(((String) e.getParams().get(0)).toString());
			if (this.infos.size() >= num)
				canAdd = true;
			break;
		case 4:
			int amount = Integer.parseInt((String) e.getParams().get(0));
			if (favorAmount() >= amount)
				canAdd = true;
			break;
		case 5:
			String ws = (String) e.getParams().get(0);
			int favor = Integer.parseInt((String) e.getParams().get(1));
			boolean flag = true;
			for (String w : ws.split(",")) {
				wi = (WarriorInfo) this.infos.get(Integer.valueOf(Integer.parseInt(w)));
				if ((wi == null) || (wi.favor < favor)) {
					flag = false;
					break;
				}
			}
			canAdd = flag;
			break;
		case 6:
			int cp = Integer.parseInt((String) e.getParams().get(0));
			int amt = Integer.parseInt((String) e.getParams().get(1));
			int i1 = 0;
			for (WarriorInfo wi1 : this.infos.values()) {
				if (wi1.camp == cp)
					i1 += wi1.favor;
			}
			if (i1 >= amt) {
				canAdd = true;
			}
		}

		if (canAdd)
			this.eventids.put(Integer.valueOf(e.getId()), Boolean.valueOf(false));
		return canAdd;
	}

	private void readObject(ObjectInputStream in) {
		try {
			int version = in.readInt();

			int size = in.readInt();
			this.infos = new HashMap();
			for (int i = 0; i < size; ++i) {
				WarriorInfo info = new WarriorInfo();
				info.camp = in.readInt();
				info.favor = in.readInt();
				info.id = in.readInt();
				this.infos.put(Integer.valueOf(info.id), info);
			}

			size = in.readInt();
			this.eventids = new HashMap();
			for (int i = 0; i < size; ++i)
				this.eventids.put(Integer.valueOf(in.readInt()), Boolean.valueOf(in.readBoolean()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(1);

		out.writeInt(this.infos.size());
		for (WarriorInfo wi : this.infos.values()) {
			out.writeInt(wi.camp);
			out.writeInt(wi.favor);
			out.writeInt(wi.id);
		}

		out.writeInt(this.eventids.size());
		for (Integer i : this.eventids.keySet()) {
			out.writeInt(i.intValue());
			out.writeBoolean(((Boolean) this.eventids.get(i)).booleanValue());
		}
	}

	public int getBlobId() {
		return 21;
	}
}
