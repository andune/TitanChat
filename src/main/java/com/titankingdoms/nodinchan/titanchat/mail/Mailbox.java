package com.titankingdoms.nodinchan.titanchat.mail;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public final class Mailbox implements Serializable {
	
	private static final long serialVersionUID = 6389202341763414739L;
	
	private final String owner;
	
	private LinkedList<Mail> mailbox;
	
	public Mailbox(String owner) {
		this.owner = owner;
		this.mailbox = new LinkedList<Mail>();
	}
	
	public boolean deleteMail(int mail) {
		try { mailbox.remove(mail); return true;
		} catch (IndexOutOfBoundsException e) { return false; }
	}
	
	public List<Mail> getMail() {
		return mailbox;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public int getUnreadMail() {
		int unread = 0;
		
		for (Mail mail : mailbox)
			if (!mail.isRead())
				unread++;
		
		return unread;
	}
	
	public Mail readMail(int mail) {
		return mailbox.get(mail);
	}
	
	public void receiveMail(String sender, String message) {
		mailbox.add(new Mail(sender, System.currentTimeMillis(), message));
	}
	
	public int size() {
		return mailbox.size();
	}
	
	public void sort() {
		Map<Long, Mail> mailMap = new HashMap<Long, Mail>();
		List<Long> times = new LinkedList<Long>();
		
		for (Mail mail : mailbox) {
			mailMap.put(mail.getSystemTime(), mail);
			times.add(mail.getSystemTime());
		}
		
		Collections.sort(times);
		
		LinkedList<Mail> mail = new LinkedList<Mail>();
		
		for (long time : times)
			mail.add(mailMap.get(time));
		
		this.mailbox = mail;
	}
	
	public static class Mail implements Serializable {
		
		private static final long serialVersionUID = -3426171776507035854L;
		
		private final String sender;
		
		private final long time;
		
		private final String message;
		
		private boolean read;
		
		public Mail(String sender, long time, String message) {
			this.sender = sender;
			this.time = time;
			this.message = message;
			this.read = false;
		}
		
		public String getDateTime() {
			return new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date(time));
		}
		
		public String getMessage() {
			return message;
		}
		
		public String getSender() {
			return sender;
		}
		
		public long getSystemTime() {
			return time;
		}
		
		public String getTitle() {
			return message.substring(0, 11) + "...";
		}
		
		public boolean isRead() {
			return read;
		}
		
		public void setRead(boolean read) {
			this.read = read;
		}
	}
}