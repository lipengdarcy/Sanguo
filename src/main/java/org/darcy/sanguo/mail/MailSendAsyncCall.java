package org.darcy.sanguo.mail;

import java.text.MessageFormat;

import org.darcy.gate.net.ClientSession;
import org.darcy.sanguo.Platform;
import org.darcy.sanguo.asynccall.AsyncCall;
import org.darcy.sanguo.asynccall.AsyncSaver;
import org.darcy.sanguo.event.Event;
import org.darcy.sanguo.player.Player;
import org.darcy.sanguo.util.DBUtil;

import com.google.protobuf.InvalidProtocolBufferException;

import sango.packet.PbDown;
import sango.packet.PbPacket;
import sango.packet.PbUp;

public class MailSendAsyncCall extends AsyncCall {
	public static final int GM_ID = -888;
	Player player;
	int targetId = -1;
	String targetName;
	String content;

	public MailSendAsyncCall(ClientSession session, PbPacket.Packet packet) throws InvalidProtocolBufferException {
		super(session, packet);
		this.player = session.getPlayer();
		PbUp.MailSend send = PbUp.MailSend.parseFrom(packet.getData());
		this.targetName = send.getName().trim();
		this.content = send.getContent().trim();
		if ((this.targetName.trim().length() == 0) || (this.content.trim().length() == 0)) {
			throw new IllegalArgumentException();
		}
		if (!(this.targetName.equalsIgnoreCase("gm")))
			this.content = Platform.getKeyWordManager().mark("blackword.txt", this.content);
	}

	public void callback() {
		PbDown.MailSendRst.Builder rst = PbDown.MailSendRst.newBuilder();
		if (this.player.getLevel() < 15) {
			rst.setResult(false);
			rst.setErrInfo("等级达到15级才能发送邮件");
		} else if (this.targetName.equals(this.player.getName())) {
			rst.setResult(false);
			rst.setErrInfo("不能给自己发邮件");
		} else if (System.currentTimeMillis() - this.player.getPool().getLong(27, 0L) < 60000L) {
			rst.setResult(false);
			rst.setErrInfo("发送邮件过于频繁，请1分钟之后再试");
		} else if (this.targetId > 0) {
			try {
				AsyncSaver as;
				Mail mail = new Mail(14, this.player.getId(), this.player.getName(), this.targetId,
						MessageFormat.format("来自“{0}”的留言", new Object[] { this.player.getName() }), this.content, null,
						null);
				Player target = Platform.getPlayerManager().getPlayer(this.targetId, false, false);
				if (target != null) {
					target.getMails().addMail(mail);
					as = new AsyncSaver(mail);
					Platform.getThreadPool().execute(as);
					Platform.getEventManager().addEvent(new Event(2079, new Object[] { target, mail }));
				} else {
					as = new AsyncSaver(mail);
					Platform.getThreadPool().execute(as);
				}
				Platform.getLog().logMail(mail);
				rst.setResult(true);
				this.player.getPool().set(27, Long.valueOf(System.currentTimeMillis()));
			} catch (Exception e) {
				e.printStackTrace();

				rst.setResult(false);
				rst.setErrInfo("该玩家不存在");
			}
		} else if (this.targetId == -888) {
			Platform.getLog().logGm(this.player, this.content);
			rst.setResult(true);
			rst.setErrInfo("邮件已发送，客服收到邮件后会与您联系");
			this.player.getPool().set(27, Long.valueOf(System.currentTimeMillis()));
		} else {
			rst.setResult(false);
			rst.setErrInfo("该玩家不存在");
		}
		this.player.send(2122, rst.build());
	}

	public void netOrDB() {
		if (this.targetName.equalsIgnoreCase("gm")) {
			this.targetId = -888;
		}
		if (!(this.targetName.equals(this.player.getName()))) {
			Player p = DBUtil.getPlayerByName(this.targetName);
			if (p != null)
				this.targetId = p.getId();
		}
	}
}
