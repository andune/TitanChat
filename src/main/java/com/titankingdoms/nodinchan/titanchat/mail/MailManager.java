package com.titankingdoms.nodinchan.titanchat.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

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

public class MailManager implements Listener {
	
	private final TitanChat plugin;
	
	private final Map<String, Mailbox> mailboxes;
	
	private final boolean enable;
	
	public MailManager() {
		this.plugin = TitanChat.getInstance();
		this.enable = this.plugin.getConfig().getBoolean("mail.enable");
		
		if (enable)
			this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		this.mailboxes = new HashMap<String, Mailbox>();
	}
	
	public boolean enable() {
		return enable;
	}
	
	public Mailbox getMailbox(String name) {
		return mailboxes.get(name);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(new FileOutputStream(new File(plugin.getMailDir(), event.getPlayer().getName() + ".mailbox")));
			output.writeObject(mailboxes.get(event.getPlayer().getName()));
			output.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Mailbox mailbox = null;
		
		if (!mailboxes.containsKey(event.getPlayer().getName())) {
			File mb = new File(plugin.getMailDir(), event.getPlayer().getName() + ".mailbox");
			
			try {
				if (mb.createNewFile()) {
					mailboxes.put(event.getPlayer().getName(), (mailbox = new Mailbox(event.getPlayer().getName())));
					mailboxes.get(event.getPlayer().getName()).receiveMail("Sender", "Welcome to " + plugin.getServer().getName() + "!");
					ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(mb));
					output.writeObject(mailboxes.get(event.getPlayer().getName()));
					output.close();
					
				} else {
					ObjectInputStream input = new ObjectInputStream(new FileInputStream(mb));
					mailboxes.put(event.getPlayer().getName(), (mailbox = (Mailbox) input.readObject()));
					input.close();
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			mailbox = mailboxes.get(event.getPlayer().getName());
		}
		
		if (mailbox.getUnreadMail() > 0)
			plugin.sendInfo(event.getPlayer(), "You have " + mailbox.getUnreadMail() + " unread mail");
	}
	
	public void unload() {
		for (Mailbox mailbox : mailboxes.values()) {
			try {
				File mb = new File(plugin.getMailDir(), mailbox.getOwner() + ".mailbox");
				
				ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(mb));
				output.writeObject(mailbox);
				output.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public enum Mail {
		READ("read") {
			
			@Override
			public void execute(Player player, String[] args) {
				
			}
		},
		SEND("send") {
			
			@Override
			public void execute(Player player, String[] args) {
				
			}
		};
		
		private String name;
		private static Map<String, Mail> NAME_MAP = new HashMap<String, Mail>();
		
		private Mail(String name) {
			this.name = name;
		}
		
		static {
			for (Mail mail : EnumSet.allOf(Mail.class))
				NAME_MAP.put(mail.name, mail);
		}
		
		public abstract void execute(Player player, String[] args);
		
		public static Mail fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String getName() {
			return name;
		}
	}
}