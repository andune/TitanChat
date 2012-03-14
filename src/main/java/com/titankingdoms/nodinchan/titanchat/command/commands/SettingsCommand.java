package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.Type;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;

public class SettingsCommand extends Command {

	private ChannelManager cm;
	
	public SettingsCommand(TitanChat plugin) {
		super(plugin);
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "Set", triggers = "set")
	@CommandInfo(description = "Sets the channel settings", usage = "set [setting] [value/channel> <channel>")
	public void set(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Set"); return; }
		if (Settings.fromName(args[0]) == null) { plugin.sendWarning(player, "Invalid Setting"); return; }
		
		switch (Settings.fromName(args[0])) {
		
		case CHCOLOUR:
			if (args.length < 2) { Settings.CHCOLOUR.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).getVariables().setChatColour(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the colour to " + args[1]);
						
					} else { plugin.sendWarning(player, "You do not have permission"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setChatColour(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the colour to " + args[1]);
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
			}
			break;
			
		case CONVERT:
			try {
				if (cm.exists(args[1])) {
					if (plugin.isStaff(player)) {
						cm.getChannel(args[1]).getVariables().setConvert((cm.getChannel(args[1]).getVariables().convert()) ? false : true);
						cm.getChannel(args[1]).save();
						
						plugin.sendInfo(player, "The channel now " + ((cm.getChannel(args[1]).getVariables().convert()) ? "converts" : "ignores") + " colour codes");
						
					} else { plugin.sendWarning(player, "You do not have permission"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
			} catch (IndexOutOfBoundsException e) {
				if (plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setConvert((cm.getChannel(player).getVariables().convert()) ? false : true);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "The channel now " + ((cm.getChannel(player).getVariables().convert()) ? "converts" : "ignores") + " colour codes");
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
			}
			break;
			
		case NCOLOUR:
			if (args.length < 2) { Settings.NCOLOUR.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).getVariables().setNameColour(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the colour to " + args[1]);
						
					} else { plugin.sendWarning(player, "You do not have permission"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setNameColour(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the colour to " + args[1]);
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
			}
			break;
			
		case PASSWORD:
			if (args.length < 2) { Settings.PASSWORD.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).setPassword(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the password of " + cm.getChannel(player).getName() + " to " + args[1]);
						
					} else { plugin.sendWarning(player, "You do not have permission"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).setPassword(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the password of " + cm.getChannel(player).getName() + " to " + args[1]);
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
			}
			break;
			
		case TAG:
			if (args.length < 2) { Settings.TAG.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						cm.getChannel(args[2]).getVariables().setTag(args[1]);
						cm.getChannel(args[2]).save();
						
						plugin.sendInfo(player, "You have changed the settings");
						
					} else { plugin.sendWarning(player, "You do not have permission"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					cm.getChannel(player).getVariables().setTag(args[1]);
					cm.getChannel(player).save();
					
					plugin.sendInfo(player, "You have changed the settings");
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
			}
			break;
			
		case TYPE:
			if (args.length < 2) { Settings.TYPE.invalidArgLength(player); return; }
			
			try {
				if (cm.exists(args[2])) {
					if (cm.getChannel(args[2]).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
						if (Type.fromName(args[1]) != null) {
							switch (Type.fromName(args[1])) {
							
							case CUSTOM:
								plugin.sendInfo(player, "You cannot set a channel's type as custom");
								break;
							
							case DEFAULT:
							case STAFF:
								if (plugin.isStaff(player)) {
									cm.getChannel(args[2]).setType(args[1]);
									cm.getChannel(args[2]).save();
									
									plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
									
								} else { plugin.sendWarning(player, "You do not have permission"); }
								break;
								
							case PASSWORD:
							case PRIVATE:
							case PUBLIC:
								cm.getChannel(args[2]).setType(args[1]);
								cm.getChannel(args[2]).save();
								
								plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
								break;
								
							case UNKNOWN:
								plugin.sendWarning(player, "This type is unknown");
								break;
							}
							
						} else {
							plugin.sendWarning(player, "Type does not exist");
							
							StringBuilder str = new StringBuilder();
							
							for (Type typeEnum : Type.values()) {
								if (str.length() > 0)
									str.append(", ");
								
								str.append(typeEnum.getName());
							}
							
							plugin.sendInfo(player, "Available types: " + str.toString());
						}
						
					} else { plugin.sendWarning(player, "You do not have permission"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
			} catch (IndexOutOfBoundsException e) {
				if (cm.getChannel(player).getAdminList().contains(player.getName()) || plugin.isStaff(player)) {
					if (Type.fromName(args[1]) != null) {
						switch (Type.fromName(args[1])) {
						
						case CUSTOM:
							plugin.sendInfo(player, "You cannot set a channel's type as custom");
							break;
						
						case DEFAULT:
						case STAFF:
							if (plugin.isStaff(player)) {
								cm.getChannel(player).setType(args[1]);
								cm.getChannel(player).save();
								
								plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
								
							} else { plugin.sendWarning(player, "You do not have permission"); }
							break;
							
						case PASSWORD:
						case PRIVATE:
						case PUBLIC:
							cm.getChannel(player).setType(args[1]);
							cm.getChannel(player).save();
							
							plugin.sendInfo(player, "The channel is now " + Type.fromName(args[1]).getName());
							break;
							
						case UNKNOWN:
							plugin.sendWarning(player, "This type is unknown");
							break;
						}
						
					} else {
						plugin.sendWarning(player, "Type does not exist");
						
						StringBuilder str = new StringBuilder();
						
						for (Type typeEnum : Type.values()) {
							if (str.length() > 0)
								str.append(", ");
							
							str.append(typeEnum.getName());
						}
						
						plugin.sendInfo(player, "Available types: " + str.toString());
					}
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
			}
			break;
		}
	}
	
	public enum Settings {
		CHCOLOUR("ChColour", new String[] { "chcolour", "chcolor" }, "Changes the chat display colour of the channel", "chcolour [colourcode] <channel>"),
		CONVERT("Convert", new String[] { "convert" }, "Toggles colour code converting", "convert <channel>"),
		NCOLOUR("NColour", new String[] { "ncolour", "ncolor" }, "Changes the name display colour of the channel", "ncolour [colourcode] <channel>"),
		PASSWORD("Password", new String[] { "password" }, "Sets the password of the channel", "password [password] <channel>"),
		TAG("Tag", new String[] { "tag" }, "Sets the tag of the channel", "tag [tag] <channel>"),
		TYPE("Type", new String[] { "type" }, "Sets the type of the channel", "type [type] <channel>");

		private String description;
		private String name;
		private String[] names;
		private String usage;
		
		private static Map<String, Settings> NAME_MAP = new HashMap<String, Settings>();
		private static Map<String, String> DESCRIPTION_MAP = new HashMap<String, String>();
		private static Map<String, String> USAGE_MAP = new HashMap<String, String>();
		
		private Settings(String name, String[] names, String description, String usage) {
			this.name = name;
			this.names = names;
			this.description = description;
			this.usage = usage;
		}
		
		static {
			for (Settings setting : EnumSet.allOf(Settings.class)) {
				for (String name : setting.names) {
					NAME_MAP.put(name, setting);
					DESCRIPTION_MAP.put(name, setting.description);
					USAGE_MAP.put(name, setting.usage);
				}
			}
		}
		
		public static Settings fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String[] getAliases() {
			return names;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getName() {
			return name;
		}
		
		public String getUsage() {
			return usage;
		}
		
		public void invalidArgLength(Player player) {
			player.sendMessage("[TitanChat] " + ChatColor.RED + "Invalid Argument Length");
			player.sendMessage("[TitanChat] " + ChatColor.GOLD + "Usage: /tc set " + usage);
		}
	}
}