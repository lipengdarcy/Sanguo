package org.darcy.sanguo.notice;

import java.text.SimpleDateFormat;
import java.util.Date;

import sango.packet.PbCommons;
import sango.packet.PbGm;

public class Notice {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public int id;
	public String title;
	public String content;
	public Date start;
	public Date end;
	public int weight;

	public PbCommons.Notice genNotice() {
		PbCommons.Notice.Builder b = PbCommons.Notice.newBuilder();
		b.setId(this.id);
		b.setTitle(this.title);
		b.setContent(this.content);
		return b.build();
	}

	public PbGm.GmNotice genGmNotice() {
		PbGm.GmNotice.Builder b = PbGm.GmNotice.newBuilder();
		b.setId(this.id);
		b.setTitle(this.title);
		b.setContent(this.content);
		b.setStart(this.start.getTime());
		b.setEnd(this.end.getTime());
		b.setWeight(this.weight);
		return b.build();
	}
}
