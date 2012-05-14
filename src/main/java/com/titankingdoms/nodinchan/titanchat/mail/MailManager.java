package com.titankingdoms.nodinchan.titanchat.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
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
	
	private static MailManager instance;
	
	private final Map<String, Mailbox> mailboxes;
	
	private final boolean enable;
	
	public MailManager() {
		this.plugin = TitanChat.getInstance();
		MailManager.instance = this;
		
		this.enable = this.plugin.getConfig().getBoolean("mail.enable");
		
		if (enable)
			this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		this.mailboxes = new HashMap<String, Mailbox>();
		
		if (getMailDir().mkdir())
			plugin.log(Level.INFO, "Creating mail directory...");
	}
	
	public boolean enable() {
		return enable;
	}
	
	public static MailManager getInstance() {
		return instance;
	}
	
	public Mailbox getMailbox(String name) {
		return mailboxes.get(name);
	}
	
	/**
	 * Gets the Mail Directory
	 * 
	 * @return The Mail Directory
	 */
	public File getMailDir() {
		return new File(plugin.getDataFolder(), "mail");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(new FileOutputStream(new File(getMailDir(), event.getPlayer().getName() + ".mailbox")));
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
			File mb = new File(getMailDir(), event.getPlayer().getName() + ".mailbox");
			
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
				File mb = new File(getMailDir(), mailbox.getOwner() + ".mailbox");
				
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
		CHECK("check") {
			
			@Override
			public void execute(Player player, String[] args) {
				Mailbox mailbox = MailManager.getInstance().getMailbox(player.getName());
				
				try {
					int page = Integer.parseInt(args[0]) - 1;
					int numPages = mailbox.size() / 10;
					int start = page * 10;
					int end = start + 10;
					
					if (mailbox.size() % 10 != 0 && (numPages * 10) - mailbox.size() < 0)
						numPages++;
					
					if (end > mailbox.size())
						end = mailbox.size();
					
					if (page + 1 > 0 || page + 1 <= numPages) {
						player.sendMessage(ChatColor.AQUA + "=== TitanChat Mail System (" + (page + 1) + "/" + numPages + ") ===");
						for (int mailNum = start; mailNum < end; mailNum++) {
							com.titankingdoms.nodinchan.titanchat.mail.Mailbox.Mail mail = mailbox.getMail().get(mailNum);
							String no = spaceBefore((mailNum + 1) + "", 3) + (mailNum + 1) + spaceAfter((mailNum + 1) + "", 3);
							String title = mail.getTitle();
							String sender = spaceBefore(mail.getSender(), 16) + mail.getSender() + spaceAfter(mail.getSender(), 16);
							
							String read = (mail.isRead()) ? "Read" : "Unread";
							
							player.sendMessage(ChatColor.AQUA + " " + no + "  " + title + "  " + sender + "  " + read);
							
						}
						player.sendMessage(ChatColor.AQUA + " No.      Title           Sender       Read ");
						
					} else {
						TitanChat.getInstance().getServer().dispatchCommand(player, "titanchat mail check 1");
					}
					
				} catch (IndexOutOfBoundsException e) {
					TitanChat.getInstance().getServer().dispatchCommand(player, "titanchat mail check 1");
					
				} catch (NumberFormatException e) {
					TitanChat.getInstance().sendWarning(player, "Invalid Page Number");
				}
			}
		},
		DELETE("delete") {
			
			@Override
			public void execute(Player player, String[] args) {
				Mailbox mailbox = MailManager.getInstance().getMailbox(player.getName());
				
				try {
					if (args.length < 1)
						throw new IndexOutOfBoundsException();
					
					int deleted = 0;
					
					if (mailbox.deleteMail(Integer.parseInt(args[0]) - 1))
						deleted++;
					
					for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
						try {
							if (mailbox.deleteMail(Integer.parseInt(arg)))
								deleted++;
							
						} catch (NumberFormatException e) {}
					}
					
					if (deleted == 1)
						TitanChat.getInstance().sendInfo(player, "Successfully deleted mail");
					else if (deleted > 0)
						TitanChat.getInstance().sendInfo(player, "Successfully deleted selection");
					else
						TitanChat.getInstance().sendWarning(player, "Failed to delete mail");
					
				} catch (IndexOutOfBoundsException e) {
					if (mailbox.getSelection().size() < 1) {
						TitanChat.getInstance().sendWarning(player, "Failed to find mail");
						return;
					}
					
					for (int mail : mailbox.getSelection())
						mailbox.deleteMail(mail);
					
					TitanChat.getInstance().sendInfo(player, "Successfully deleted selection");
					
				} catch (NumberFormatException e) {
					if (args[0].equalsIgnoreCase("all")) {
						for (int mail = 0; mail < mailbox.size(); mail++)
							mailbox.deleteMail(mail);
						TitanChat.getInstance().sendInfo(player, "Successfully deleted all mail");
						
					} else {
						TitanChat.getInstance().sendWarning(player, "Failed to find mail");
					}
				}
			}
		},
		HELP("help") {
			
			@Override
			public void execute(Player player, String[] args) {
				player.sendMessage(ChatColor.AQUA + "=== TitanChat Mail System Help ===");
				player.sendMessage(ChatColor.AQUA + "CHECK                    - Checks mail box for mail");
				player.sendMessage(ChatColor.AQUA + "DELETE [no.] [no.]...    - Deletes mail");
				player.sendMessage(ChatColor.AQUA + "HELP                     - Displays this help");
				player.sendMessage(ChatColor.AQUA + "READ <no.>               - Reads the mail");
				player.sendMessage(ChatColor.AQUA + "SEL <no.> <no.>...       - Selects the list of mail");
				player.sendMessage(ChatColor.AQUA + "SEND <target> <message>  - Sends the mail to the target");
				player.sendMessage(ChatColor.AQUA + "SETREAD [no.] [no.]...   - Sets all the mail to read");
				player.sendMessage(ChatColor.AQUA + "SETUNREAD [no.] [no.]... - Sets all the mail to unread");
				player.sendMessage(ChatColor.AQUA + "\"[no.]\" is the ID of the mail you see when you check mail.");
			}
		},
		READ("read") {
			
			@Override
			public void execute(Player player, String[] args) {
				try {
					com.titankingdoms.nodinchan.titanchat.mail.Mailbox.Mail mail = MailManager.getInstance().getMailbox(player.getName()).readMail(Integer.parseInt(args[0]));
					player.sendMessage("Sender: " + mail.getSender());
					player.sendMessage("Date: " + mail.getDateTime());
					player.sendMessage("Title: " + mail.getTitle());
					player.sendMessage(mail.getMessage());
					
				} catch (IndexOutOfBoundsException e) {
					TitanChat.getInstance().sendWarning(player, "Failed to find mail");
					
				} catch (NumberFormatException e) {
					TitanChat.getInstance().sendWarning(player, "Failed to find mail");
				}
			}
		},
		SEL("sel") {
			
			@Override
			public void execute(Player player, String[] args) {
				Mailbox mailbox = MailManager.getInstance().getMailbox(player.getName());
				
				List<Integer> mailList = new ArrayList<Integer>();
				
				for (String arg : args) {
					try {
						int mail = Integer.parseInt(arg) - 1;
						
						if (mail < 0 || mail > mailbox.size())
							continue;
						
						mailList.add(mail);
						
					} catch (NumberFormatException e) {}
				}
				
				Collections.sort(mailList);
				
				mailbox.getSelection().clear();
				mailbox.getSelection().addAll(mailList);
				TitanChat.getInstance().sendInfo(player, "You have selected " + mailList.size() + " mail");
			}
		},
		SELECT("select") {
			
			@Override
			public void execute(Player player, String[] args) {
				Mailbox mailbox = MailManager.getInstance().getMailbox(player.getName());
				
				List<Integer> mailList = new ArrayList<Integer>();
				
				for (String arg : args) {
					try {
						int mail = Integer.parseInt(arg) - 1;
						
						if (mail < 0 || mail > mailbox.size())
							continue;
						
						mailList.add(mail);
						
					} catch (NumberFormatException e) {}
				}
				
				Collections.sort(mailList);
				
				mailbox.getSelection().clear();
				mailbox.getSelection().addAll(mailList);
				TitanChat.getInstance().sendInfo(player, "You have selected " + mailList.size() + " mail");
			}
		},
		SEND("send") {
			
			@Override
			public void execute(Player player, String[] args) {
				
			}
		},
		SETREAD("setread") {
			
			@Override
			public void execute(Player player, String[] args) {
				Mailbox mailbox = MailManager.getInstance().getMailbox(player.getName());
				
				try {
					if (args.length < 1)
						throw new IndexOutOfBoundsException();
					
					int set = 0;
					
					mailbox.readMail(Integer.parseInt(args[0]) - 1).setRead(true);
					set++;
					
					for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
						try {
							mailbox.readMail(Integer.parseInt(arg) - 1).setRead(true);
							set++;
							
						} catch (NumberFormatException e) {}
					}
					
					if (set == 1)
						TitanChat.getInstance().sendInfo(player, "Successfully set mail as read");
					else if (set > 0)
						TitanChat.getInstance().sendInfo(player, "Successfully set selection as read");
					else
						TitanChat.getInstance().sendWarning(player, "Failed to set mail as read");
					
				} catch (IndexOutOfBoundsException e) {
					if (mailbox.getSelection().size() < 1) {
						TitanChat.getInstance().sendWarning(player, "Failed to find mail");
						return;
					}
					
					for (int mail : mailbox.getSelection())
						mailbox.readMail(mail).setRead(true);
					
					TitanChat.getInstance().sendInfo(player, "Successfully set selection as read");
					
				} catch (NumberFormatException e) {
					if (args[0].equalsIgnoreCase("all")) {
						for (com.titankingdoms.nodinchan.titanchat.mail.Mailbox.Mail mail : mailbox.getMail())
							mail.setRead(true);
						
						TitanChat.getInstance().sendInfo(player, "Successfully set all mail as read");
						
					} else {
						TitanChat.getInstance().sendWarning(player, "Failed to find mail");
					}
				}
			}
		},
		SETUNREAD("setunread") {
			
			@Override
			public void execute(Player player, String[] args) {
				Mailbox mailbox = MailManager.getInstance().getMailbox(player.getName());
				
				try {
					if (args.length < 1)
						throw new IndexOutOfBoundsException();
					
					int set = 0;
					
					mailbox.readMail(Integer.parseInt(args[0]) - 1).setRead(false);
					set++;
					
					for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
						try {
							mailbox.readMail(Integer.parseInt(arg) - 1).setRead(false);
							set++;
							
						} catch (NumberFormatException e) {}
					}
					
					if (set == 1)
						TitanChat.getInstance().sendInfo(player, "Successfully set mail as unread");
					else if (set > 0)
						TitanChat.getInstance().sendInfo(player, "Successfully set selection as unread");
					else
						TitanChat.getInstance().sendWarning(player, "Failed to set mail as unread");
					
				} catch (IndexOutOfBoundsException e) {
					if (mailbox.getSelection().size() < 1) {
						TitanChat.getInstance().sendWarning(player, "Failed to find mail");
						return;
					}
					
					for (int mail : mailbox.getSelection())
						mailbox.readMail(mail).setRead(false);
					
					TitanChat.getInstance().sendInfo(player, "Successfully set selection as unread");
					
				} catch (NumberFormatException e) {
					if (args[0].equalsIgnoreCase("all")) {
						for (com.titankingdoms.nodinchan.titanchat.mail.Mailbox.Mail mail : mailbox.getMail())
							mail.setRead(false);
						
						TitanChat.getInstance().sendInfo(player, "Successfully set all mail as unread");
						
					} else {
						TitanChat.getInstance().sendWarning(player, "Failed to find mail");
					}
				}
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
		
		public String spaceAfter(String word, int limit) {
			double spaces = (limit - word.length()) * 0.5;
			
			StringBuilder str = new StringBuilder();
			
			for (int space = 0; space < (spaces > ((int) spaces) ? (int) spaces + 1 : spaces); space++)
				str.append(" ");
			
			return str.toString();
		}
		
		public String spaceBefore(String word, int limit) {
			int spaces = (int) ((limit - word.length()) * 0.5);
			
			StringBuilder str = new StringBuilder();
			
			for (int space = 0; space < spaces; space++)
				str.append(" ");
			
			return str.toString();
		}
	}
}