package org.darcy.sanguo.loottreasure;

import java.util.ArrayList;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.item.DebrisTemplate;
import org.darcy.sanguo.player.MiniPlayer;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.robot.RobotData;
import org.darcy.sanguo.service.RobotService;
import org.darcy.sanguo.service.common.ItemService;
import org.darcy.sanguo.util.Calc;

import sango.packet.PbDown;
import sango.packet.PbLootTreasure;
import sango.packet.PbUser;

public class LootTargetsAsyncCall extends AsyncCall {
	List<Integer> ids;
	int templateId;
	Player player;
	List<PbLootTreasure.LootTreasureTarget> canPicks = new ArrayList();

	int errorNum = 0;

	public LootTargetsAsyncCall(Player player, List<Integer> ids, int templateId) {
		super(player.getSession(), null);
		this.ids = ids;
		this.templateId = templateId;
		this.player = player;
	}

	public void callback() {
		PbDown.LootTreasurePlayerListRst.Builder builder = PbDown.LootTreasurePlayerListRst.newBuilder();
		if (this.errorNum == 0) {
			builder.setResult(true);
			List<PbLootTreasure.LootTreasureTarget> targets = new ArrayList();
			if (this.canPicks.size() > 3) {
				Integer[] subs = Calc.randomGet(this.canPicks.size(), 3);
				for (Integer sub : subs)
					targets.add((PbLootTreasure.LootTreasureTarget) this.canPicks.get(sub.intValue()));
			} else {
				for (PbLootTreasure.LootTreasureTarget tmp : this.canPicks) {
					targets.add(tmp);
				}

			}

			List list = (List) this.player.getLootTreasure().rivalIds.get(Integer.valueOf(this.templateId));
			if (list == null) {
				list = new ArrayList();
				this.player.getLootTreasure().rivalIds.put(Integer.valueOf(this.templateId), list);
			}
			list.clear();
			for (PbLootTreasure.LootTreasureTarget target : targets) {
				builder.addTargets(target);
				list.add(Integer.valueOf(target.getUser().getId()));
			}

			if (targets.size() < 4) {
				DebrisTemplate template = (DebrisTemplate) ItemService.getItemTemplate(this.templateId);
				RobotData data = getRobotDataByLevel(this.player.getLevel());
				this.player.getLootTreasure().robots.clear();
				List names = LootRobot.genName(4 - targets.size());
				for (int k = 0; k < 4 - targets.size(); ++k) {
					LootRobot robot = new LootRobot(data, -1 - k, this.player.getLevel(), (String) names.get(k));
					this.player.getLootTreasure().robots.put(Integer.valueOf(robot.id), robot);
					PbUser.MiniUser user = robot.genMiniUser();
					PbLootTreasure.LootTreasureTarget target = LootTreasure.genLootTreasureTarget(user,
							template.lootDropNpc, true);
					builder.addTargets(target);
				}
			}
		} else {
			builder.setResult(false);
			builder.setErrInfo("数据异常");
		}
		this.session.send(1106, builder.build());
	}

	public void netOrDB() {
		DebrisTemplate template = (DebrisTemplate) ItemService.getItemTemplate(this.templateId);
		for (Integer id : this.ids) {
			MiniPlayer player = Platform.getPlayerManager().getMiniPlayer(id.intValue());
			if ((player == null) || (player.getLevel() < this.player.getLevel() - 5)
					|| (player.getLevel() > this.player.getLevel() + 5))
				continue;
			this.canPicks.add(LootTreasure.genLootTreasureTarget(player.genMiniUser(), template.lootDropPlayer, false));
		}
	}

	private RobotData getRobotDataByLevel(int level) {
		List list = RobotService.getRobotsByType(3);
		int i;
		for (i = 0; i < list.size(); ++i) {
			if ((level >= ((RobotData) list.get(i)).minLevel) && (level < ((RobotData) list.get(i)).maxLevel)) {
				return ((RobotData) list.get(i));
			}
		}
		return ((RobotData) list.get(i - 1));
	}
}
