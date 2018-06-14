package org.darcy.sanguo.service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.payreturn.PayReturn;
import org.darcy.sanguo.player.Player;

public class PayReturnService implements Service {
	public static HashMap<String, PayReturn> charges;
	public static Set<String> noCharges;
	public static List<Reward> noChargeRewards;
	public static List<Reward> chargeRewards;
	public static boolean isOpen = false;

	public void registCheck(Player player) {
		if (!(isOpen))
			return;

		PayReturn pay = (PayReturn) charges.get(player.getAccountId());
		if (pay != null) {
			MailService.sendSystemMail(1, player.getId(), "老玩家回归奖励",
					"</p style=21>亲爱的玩家您好，您的账号已经参加过安卓删档计费测试并进行过充值，本次回归我们将送您一份老玩家“至尊礼包”，非常感谢您一直以来对我们的支持，希望您在《三国吧兄弟》的游戏世界中快乐每一天！</p>",
					new Date(), chargeRewards);

			MailService.sendSystemMail(1, player.getId(), "充值返还", MessageFormat.format(
					"</p style=21>亲爱的玩家你好，您在上次安卓删档计费测试累计充值{0}元宝，达到VIP{1}级，本次回归我们将给您全额返还，非常感谢您一直以来对我们的支持，希望您在《三国吧兄弟》的游戏世界中快乐每一天！</p>",
					new Object[] { Integer.valueOf(pay.charge), Integer.valueOf(VipService.getVip(pay.charge).level) }),
					new Date(), pay.registRewards);
		} else {
			if (!(noCharges.contains(player.getAccountId()))) {
				return;
			}

			MailService.sendSystemMail(1, player.getId(), "老玩家回归福利",
					"</p style=21>亲爱的玩家您好，您的账号已经参加过安卓删档计费测试，本次回归我们将送您一份老玩家“真情礼包”，非常感谢您一直以来对我们的支持，希望您在《三国吧兄弟》的游戏世界中快乐每一天！</p>",
					new Date(), noChargeRewards);
		}
	}

	public void newDayCheck(Player player) {
		if (!(isOpen))
			return;
		PayReturn pay = (PayReturn) charges.get(player.getAccountId());
		if (pay == null)
			return;
		int record = player.getPool().getInt(25, 0);
		if (record == 0) {
			MailService.sendSystemMail(1, player.getId(),
					MessageFormat.format("超级返利第{0}天", new Object[] { Integer.valueOf(1) }),
					MessageFormat.format(
							"</p style=21>亲爱的玩家您好，您在上次安卓删档计费测试累计充值{0}元宝，超值返利的50%元宝返还部分，我们会在首日返还10%，第二日返还10%，第三日返还20%，第四日返还10%，第五日返还10%，第六日返还10%，第七日返还30%，非常感谢您一直以来对我们的支持！</p>",
							new Object[] { Integer.valueOf(pay.charge) }),
					new Date(), (List) pay.loginRewards.get(Integer.valueOf(1)));
			player.getPool().set(25, Integer.valueOf(1));
		} else {
			int today = Calendar.getInstance().get(6);
			Calendar cal = Calendar.getInstance();
			cal.setTime(player.getLastLogout());
			int diff = today - cal.get(6);
			for (int i = 0; (i < diff) && (record < 7); ++i) {
				++record;
				Calendar c = Calendar.getInstance();
				c.add(6, i - diff);
				MailService.sendSystemMail(1, player.getId(),
						MessageFormat.format("超级返利第{0}天", new Object[] { Integer.valueOf(record) }),
						MessageFormat.format(
								"</p style=21>亲爱的玩家您好，您在上次安卓删档计费测试累计充值{0}元宝，超值返利的50%元宝返还部分，我们会在首日返还10%，第二日返还10%，第三日返还20%，第四日返还10%，第五日返还10%，第六日返还10%，第七日返还30%，非常感谢您一直以来对我们的支持！</p>",
								new Object[] { Integer.valueOf(pay.charge) }),
						c.getTime(), (List) pay.loginRewards.get(Integer.valueOf(record)));
				player.getPool().set(25, Integer.valueOf(record));
			}
		}
	}

	public void startup() throws Exception {
		loadData();
	}

	public void shutdown() {
	}

	public void reload() throws Exception {
		loadData();
	}

	private void loadData() {
	}
}
