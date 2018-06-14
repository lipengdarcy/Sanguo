package org.darcy.sanguo.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Mails {
	public List<Mail> mails;
	private Comparator<Mail> compa;

	public Mails() {
		this.mails = new CopyOnWriteArrayList<Mail>();
		this.compa = new Comparator<Mail>() {
			public int compare(Mail m1, Mail m2) {
				if ((m2.isRead()) && (!(m1.isRead()))) {
					return -1;
				}
				if ((m1.isRead()) && (!(m2.isRead()))) {
					return 1;
				}
				return (int) (m2.getSendTime().getTime() - m1.getSendTime().getTime());
			}
		};
	}

	public Mail getMail(int id) {
		for (Mail mail : this.mails) {
			if (mail.getId() == id) {
				return mail;
			}
		}

		return null;
	}

	public List<Mail> getMails(int type) {
		Mail m;
		Iterator localIterator;
		List ls = new ArrayList();
		if (type == 1) {
			for (localIterator = this.mails.iterator(); localIterator.hasNext();) {
				m = (Mail) localIterator.next();
				if ((m.getType() != 14) && (m.getId() != 0))
					ls.add(m);
			}
		} else if (type == 2) {
			for (localIterator = this.mails.iterator(); localIterator.hasNext();) {
				m = (Mail) localIterator.next();
				if ((m.getType() == 14) && (m.getId() != 0))
					ls.add(m);
			}
		} else {
			for (localIterator = this.mails.iterator(); localIterator.hasNext();) {
				m = (Mail) localIterator.next();
				if (m.getId() != 0) {
					ls.add(m);
				}
			}
		}
		Collections.sort(ls, this.compa);
		return ls;
	}

	public int getMailNotReadCount(int type) {
		List list = getMails(type);
		if (list.size() > 0) {
			int count = 0;
			Iterator localIterator = list.iterator();
			while (true) {
				Mail mail = (Mail) localIterator.next();
				if (!(mail.isRead()))
					++count;
				if (!(localIterator.hasNext())) {
					return count;
				}
			}
		}
		return 0;
	}

	public List<Mail> getMails() {
		return this.mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public void addMail(Mail mail) {
		this.mails.add(mail);
	}
}
