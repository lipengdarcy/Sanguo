package org.darcy.sanguo.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncDelete;
import org.darcy.sanguo.asynccall.AsyncSaver;
import org.darcy.sanguo.asynccall.AsyncUpdater;
import org.darcy.sanguo.asynccall.CallBackable;
import org.darcy.sanguo.drop.Reward;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.mail.GlobalMail;
import org.darcy.sanguo.mail.Mail;
import org.darcy.sanguo.mail.MailSendAsyncCall;
import org.darcy.sanguo.mail.Mails;
import org.darcy.sanguo.net.ClientSession;
import org.darcy.sanguo.packethandler.PacketHandler;
import org.darcy.sanguo.persist.DbService;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.DBUtil;

import sango.packet.PbCommons;
import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class MailService implements Service, PacketHandler {
	public static final int MAIL_OVERDUE_TIME = 1209600000;

	public void startup() {
		checkOverdueData();
		Platform.getPacketHanderManager().registerHandler(this);
	}

	private void checkOverdueData() {
		long time = System.currentTimeMillis();
		Date date = new Date(time - 1209600000L);
		DBUtil.clearOverDueMail(date);
	}

	public void shutdown() {
	}

	public int[] getCodes() {
		return new int[] { 2119, 2121, 2195, 2193, 2191 };
	}

	public void handlePacket(ClientSession session, PbPacket.Packet packet) throws Exception {
		Player player = session.getPlayer();
		if (player == null)
			return;

		switch (packet.getPtCode()) {
		case 2119:
			PbUp.MailInfos info = PbUp.MailInfos.parseFrom(packet.getData());
			mails(player, info.getTab());
			break;
		case 2121:
			MailSendAsyncCall call = new MailSendAsyncCall(session, packet);
			Platform.getThreadPool().execute(call);
			break;
		case 2195:
			PbUp.MailDelete de = PbUp.MailDelete.parseFrom(packet.getData());
			delete(player, de.getIdsList());
			break;
		case 2193:
			PbUp.MailGetAttach get = PbUp.MailGetAttach.parseFrom(packet.getData());
			getAttach(player, get.getId());
			break;
		case 2191:
			PbUp.MailRead r = PbUp.MailRead.parseFrom(packet.getData());
			read(player, r.getId());
		}
	}

	private void delete(Player player, List<Integer> ids) {
		Mails mails = player.getMails();
		for (Iterator localIterator = ids.iterator(); localIterator.hasNext();) {
			int id = ((Integer) localIterator.next()).intValue();
			Mail mail = mails.getMail(id);
			if ((mail != null) && (mail.getAttachment() == null)) {
				mails.mails.remove(mail);
				AsyncDelete de = new AsyncDelete(mail, null);
				Platform.getThreadPool().execute(de);
			}
		}

		PbDown.MailDeleteRst.Builder rst = PbDown.MailDeleteRst.newBuilder();
		rst.setResult(true);
		player.send(2196, rst.build());
	}

	private void getAttach(Player player, int id) {
		Mails mails = player.getMails();
		Mail mail = mails.getMail(id);
		PbDown.MailGetAttachRst.Builder rst = PbDown.MailGetAttachRst.newBuilder();
		rst.setResult(false);

		if (mail == null) {
			rst.setErrorInfo("邮件不存在");
		} else if (mail.getAttachment() == null) {
			rst.setErrorInfo("没有附件");
		} else {
			for (Reward r : mail.getAttachment().getRewards()) {
				r.add(player, "mail" + mail.type);
			}
			player.notifyGetItem(2, mail.getAttachment().getRewards());
			mail.setAttachment(null);
			mails.mails.remove(mail);
			rst.setResult(true);
			AsyncDelete de = new AsyncDelete(mail, null);
			Platform.getThreadPool().execute(de);
			Platform.getEventManager().addEvent(new Event(2080, new Object[] { player }));
		}

		player.send(2194, rst.build());
	}

	private void read(Player player, int mailId) {
		Mails mails = player.getMails();
		Mail mail = mails.getMail(mailId);

		PbDown.MailReadRst.Builder rst = PbDown.MailReadRst.newBuilder();
		if (mail == null) {
			rst.setResult(false);
			rst.setErrorInfo("邮件不存在");
		} else {
			rst.setResult(true);
			rst.setMail(mail.genPb());
		}
		rst.setId(mailId);
		player.send(2192, rst.build());

		if ((mail == null) || (mail.isRead()) || (mail.getAttachment() != null))
			return;
		mail.setRead(true);
		AsyncUpdater updater = new AsyncUpdater(mail);
		Platform.getThreadPool().execute(updater);
		Platform.getEventManager().addEvent(new Event(2080, new Object[] { player }));
	}

	private void mails(Player player, int type) {
		List<Mail> mails = player.getMails().getMails(type);
		PbDown.MailInfosRst.Builder rst = PbDown.MailInfosRst.newBuilder();
		rst.setResult(true).setTab(type);

		for (Mail mail : mails) {
			rst.addMails(PbCommons.MailSummary.newBuilder().setId(mail.getId()).setRead(mail.isRead())
					.setTime(mail.getDate()).setType(mail.getType()).setSourceName(mail.getSourceName()));
		}

		player.send(2120, rst.build());
	}

	public static void sendSystemMail(int type, int targetId, String title, String content, Date sendTime,
			List<Reward> attachment) {
		Mail mail = new Mail(type, -1, "", targetId, title, content, sendTime, attachment);
		Player target = null;
		try {
			target = Platform.getPlayerManager().getPlayer(targetId, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AsyncSaver saver = new AsyncSaver(mail);
		Platform.getThreadPool().execute(saver);
		Platform.getLog().logMail(mail);
		if (target != null) {
			target.getMails().addMail(mail);
			Platform.getEventManager().addEvent(new Event(2079, new Object[] { target, mail }));
		}
	}

	public static void sendSystemMail(int type, int targetId, String title, String content) {
		sendSystemMail(type, targetId, title, content, null, null);
	}

	public static void sendSystemMailByThread(int type, int targetId, String title, String content, Date sendTime,
			List<Reward> attachment) {
		Mail mail = new Mail(type, -1, "", targetId, title, content, sendTime, attachment);
		Player target = null;
		try {
			target = Platform.getPlayerManager().getPlayer(targetId, false, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		((DbService) Platform.getServiceManager().get(DbService.class)).add(mail);
		Platform.getLog().logMail(mail);
		if (target == null) {
			return;
		}
		target.getMails().addMail(mail);

		Platform.getCallBackManager().addCallBack(new CallBackable() {
			public void callback() {
				Platform.getEventManager().addEvent(new Event(2079, new Object[] { mail }));
			}
		});
	}

	public static void checkAndendGlobalMail(Player p, int type) {
		List list = Platform.getEntityManager().getAllFromEhCache(GlobalMail.class.getName());
		if (list.size() == 0)
			return;
		Date now = new Date();
		for (Iterator localIterator = list.iterator(); localIterator.hasNext();) {
			Object object = localIterator.next();
			try {
				GlobalMail gm = (GlobalMail) object;
				Mail mail = null;
				if (type == 1) {
					if (gm.getType() == type) {
						long lastLogin = p.getLastLogin().getTime();
						if ((lastLogin < gm.getStart()) && (now.getTime() >= gm.getStart())
								&& (now.getTime() <= gm.getEnd()) && (p.getLevel() >= gm.getMinLevel())
								&& (p.getLevel() <= gm.getMaxLevel()))
							mail = new Mail(gm.getMailType(), -1, "", p.getId(), gm.getTitle(), gm.getContent(), now,
									gm.getRewards());
					}
				} else if ((type == 2) && (((gm.getType() == 2) || (gm.getType() == 1)))) {
					long regist = p.getRegisterTime();
					if ((regist >= gm.getStart()) && (regist <= gm.getEnd()) && (p.getLevel() >= gm.getMinLevel())
							&& (p.getLevel() <= gm.getMaxLevel())) {
						mail = new Mail(gm.getMailType(), -1, "", p.getId(), gm.getTitle(), gm.getContent(), now,
								gm.getRewards());
					}
				}

				if (mail != null) {
					((DbService) Platform.getServiceManager().get(DbService.class)).add(mail);
					Platform.getLog().logMail(mail);

					p.getMails().addMail(mail);
					Platform.getEventManager().addEvent(new Event(2079, new Object[] { p, mail }));
				}
			} catch (Exception e) {
				Platform.getLog().logError(e);
			}
		}
	}

	public void reload() throws Exception {
	}
}
