package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.channel.StandardChannel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;

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

/**
 * SettingsCommand - Commands for config modification
 * 
 * @author NodinChan
 *
 */
public class SettingsCommand extends Command {

	private static ChannelManager cm;
	
	public SettingsCommand() {
		SettingsCommand.cm = plugin.getChannelManager();
	}
	
	/**
	 * Set Command - Sets the channel settings
	 */
	@CommandID(name = "Set", aliases = "set")
	@CommandInfo(description = "Sets the channel settings (\"/tc set help\" for more info)", usage = "set [setting] <value> <channel>")
	public void set(Player player, String[] args) {
		if (args.length < 1) {
			plugin.getServer().dispatchCommand(player, "titanchat set help");
			return;
		}
		
		Settings command = Settings.fromTrigger(args[0]);
		
		if (command != null)
			command.execute(player, Arrays.copyOfRange(args, 1, args.length));
		else
			plugin.sendWarning(player, "Invalid setting command");
	}
	
	/**
	 * Settings - Settings available as commands
	 * 
	 * @author NodinChan
	 *
	 */
	public enum Settings {
		CHCOLOUR("ChColour", new String[] { "chcolour", "chcolor" }, "chcolour [colourcode] <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				if (args.length < 1) { invalidArgLength(player); return; }
				
				try {
					if (cm.exists(args[1])) {
						if (cm.getChannel(args[1]) instanceof CustomChannel) {
							TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
							return;
						}
						
						if (cm.getAdmins(cm.getChannel(args[1])).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
							((StandardChannel) cm.getChannel(args[1])).getVariables().setChatColour(args[0]);
							cm.getChannel(args[1]).save();
							
							TitanChat.getInstance().sendInfo(player, "You have changed the colour to " + args[0]);
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (channel instanceof CustomChannel) {
						TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
						return;
					}
					
					if (cm.getAdmins(channel).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
						((StandardChannel) channel).getVariables().setChatColour(args[0]);
						channel.save();
						
						TitanChat.getInstance().sendInfo(player, "You have changed the colour to " + args[0]);
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		},
		CONVERT("Convert", new String[] { "convert" }, "convert <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				try {
					if (cm.exists(args[0])) {
						if (cm.getChannel(args[0]) instanceof CustomChannel) {
							TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
							return;
						}
						
						if (TitanChat.getInstance().getPermsBridge().has(player, "TitanChat.convert") || cm.getChannel(args[0]).getAdminList().contains(player.getName())) {
							((StandardChannel) cm.getChannel(args[0])).getVariables().setConvert((((StandardChannel) cm.getChannel(args[0])).getVariables().convert()) ? false : true);
							cm.getChannel(args[0]).save();
							
							TitanChat.getInstance().sendInfo(player, "The channel now " + ((((StandardChannel) cm.getChannel(args[0])).getVariables().convert()) ? "converts" : "ignores") + " colour codes");
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (channel instanceof CustomChannel) {
						TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
						return;
					}
					
					if (TitanChat.getInstance().getPermsBridge().has(player, "TitanChat.convert") || cm.getChannel(args[0]).getAdminList().contains(player.getName())) {
						((StandardChannel) channel).getVariables().setConvert((((StandardChannel) channel).getVariables().convert()) ? false : true);
						channel.save();
						
						TitanChat.getInstance().sendInfo(player, "The channel now " + ((((StandardChannel) channel).getVariables().convert()) ? "converts" : "ignores") + " colour codes");
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		},
		HELP("Help", new String[] { "commands", "cmds", "help", "?" }, "help <setting>") {
			
			@Override
			public void execute(Player player, String[] args) {
				player.sendMessage(ChatColor.AQUA + "=== TitanChat Settings Command List ===");
				player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] <channel> : Changes the chat display colour of the channel");
				player.sendMessage(ChatColor.AQUA + "convert <channel> : Toggles colour code converting");
				player.sendMessage(ChatColor.AQUA + "help : Shows this help");
				player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] <channel> : Changes the name display colour of the channel");
				player.sendMessage(ChatColor.AQUA + "password [password] <channel> : Sets the password of the channel");
				player.sendMessage(ChatColor.AQUA + "tag [tag] <channel> : Sets the tag of the channel");
				player.sendMessage(ChatColor.AQUA + "type [type] <channel> : Sets the Type of the channel");
				player.sendMessage(ChatColor.AQUA + "specialtype [specialtype] <channel> : Sets the special Type of the channel");
			}
		},
		NCOLOUR("NColour", new String[] { "ncolour", "ncolor" }, "ncolour [colourcode] <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				if (args.length < 1) { invalidArgLength(player); return; }
				
				try {
					if (cm.exists(args[1])) {
						if (cm.getChannel(args[1]) instanceof CustomChannel) {
							TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
							return;
						}
						
						if (cm.getAdmins(cm.getChannel(args[1])).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
							((StandardChannel) cm.getChannel(args[1])).getVariables().setNameColour(args[0]);
							cm.getChannel(args[1]).save();
							
							TitanChat.getInstance().sendInfo(player, "You have changed the colour to " + args[0]);
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (channel instanceof CustomChannel) {
						TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
						return;
					}
					
					if (cm.getAdmins(channel).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
						((StandardChannel) channel).getVariables().setNameColour(args[0]);
						channel.save();
						
						TitanChat.getInstance().sendInfo(player, "You have changed the colour to " + args[0]);
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		},
		PASSWORD("Password", new String[] { "password" }, "password [password] <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				if (args.length < 1) { invalidArgLength(player); return; }
				
				try {
					if (cm.exists(args[1])) {
						if (cm.getChannel(args[1]) instanceof CustomChannel) {
							TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
							return;
						}
						
						if (cm.getAdmins(cm.getChannel(args[1])).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
							((StandardChannel) cm.getChannel(args[1])).setPassword(args[0]);
							cm.getChannel(args[1]).save();
							
							TitanChat.getInstance().sendInfo(player, "You have changed the password of " + cm.getChannel(args[1]).getName() + " to " + args[0]);
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (channel instanceof CustomChannel) {
						TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
						return;
					}
					
					if (cm.getAdmins(channel).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
						((StandardChannel) channel).setPassword(args[0]);
						channel.save();
						
						TitanChat.getInstance().sendInfo(player, "You have changed the password of " + channel.getName() + " to " + args[0]);
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		},
		TAG("Tag", new String[] { "tag" }, "tag [tag] <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				if (args.length < 1) { invalidArgLength(player); return; }
				
				try {
					if (cm.exists(args[1])) {
						if (cm.getChannel(args[1]) instanceof CustomChannel) {
							TitanChat.getInstance().sendInfo(player, "Command does not support Custom Channels");
							return;
						}
						
						if (cm.getAdmins(cm.getChannel(args[1])).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
							((StandardChannel) cm.getChannel(args[1])).getVariables().setTag(args[0]);
							cm.getChannel(args[1]).save();
							
							TitanChat.getInstance().sendInfo(player, "You have changed the settings");
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (cm.getAdmins(channel).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
						((StandardChannel) channel).getVariables().setTag(args[0]);
						channel.save();
						
						TitanChat.getInstance().sendInfo(player, "You have changed the settings");
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		},
		TYPE("Type", new String[] { "type" }, "type [type] <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				if (args.length < 1) { invalidArgLength(player); return; }
				
				try {
					if (cm.exists(args[1])) {
						if (cm.getAdmins(cm.getChannel(args[1])).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
							if (Type.fromName(args[0]) != null) {
								switch (Type.fromName(args[0])) {
								
								case CUSTOM:
								case DEFAULT:
								case NONE:
								case STAFF:
								case UNKNOWN:
									TitanChat.getInstance().sendInfo(player, "This Type is not available as a Channel Type");
									break;
									
								case PASSWORD:
								case PRIVATE:
								case PUBLIC:
									cm.getChannel(args[1]).setType(args[0]);
									cm.getChannel(args[1]).save();
									
									TitanChat.getInstance().sendInfo(player, "The channel is now " + Type.fromName(args[0]).getName());
									break;
								}
								
							} else {
								TitanChat.getInstance().sendWarning(player, "Type does not exist");
								
								StringBuilder str = new StringBuilder();
								
								for (Type type : Type.values()) {
									if (type.isSpecial())
										continue;
									
									if (str.length() > 0)
										str.append(", ");
									
									str.append(type.getName());
								}
								
								TitanChat.getInstance().sendInfo(player, "Available types: " + str.toString());
							}
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (cm.getAdmins(channel).contains(player.getName()) || TitanChat.getInstance().isStaff(player)) {
						if (Type.fromName(args[0]) != null) {
							switch (Type.fromName(args[0])) {
							
							case CUSTOM:
							case DEFAULT:
							case NONE:
							case STAFF:
							case UNKNOWN:
								TitanChat.getInstance().sendInfo(player, "This Type is not available as a channel Type");
								break;
								
							case PASSWORD:
							case PRIVATE:
							case PUBLIC:
								channel.setType(args[0]);
								channel.save();
								
								TitanChat.getInstance().sendInfo(player, "The channel is now " + Type.fromName(args[0]).getName());
								break;
							}
							
						} else {
							TitanChat.getInstance().sendWarning(player, "Type does not exist");
							
							StringBuilder str = new StringBuilder();
							
							for (Type type : Type.values()) {
								if (type.isSpecial())
									continue;
								
								if (str.length() > 0)
									str.append(", ");
								
								str.append(type.getName());
							}
							
							TitanChat.getInstance().sendInfo(player, "Available types: " + str.toString());
						}
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		},
		SPECIALTYPE("SpecialType", new String[] { "specialtype" }, "specialtype [type] <channel>") {
			
			@Override
			public void execute(Player player, String[] args) {
				if (args.length < 1) { invalidArgLength(player); return; }
				
				try {
					if (cm.exists(args[1])) {
						if (TitanChat.getInstance().isStaff(player)) {
							if (Type.fromName(args[0]) != null) {
								switch (Type.fromName(args[0])) {
								
								case CUSTOM:
								case PASSWORD:
								case PRIVATE:
								case PUBLIC:
								case UNKNOWN:
									TitanChat.getInstance().sendInfo(player, "This Type is not available as a Channel Type");
									break;
									
								case DEFAULT:
								case NONE:
								case STAFF:
									cm.getChannel(args[1]).setSpecialType(args[0]);
									cm.getChannel(args[1]).save();
									
									TitanChat.getInstance().sendInfo(player, "The channel is now " + Type.fromName(args[0]).getName());
									break;
								}
								
							} else {
								TitanChat.getInstance().sendWarning(player, "Type does not exist");
								
								StringBuilder str = new StringBuilder();
								
								for (Type type : Type.values()) {
									if (!type.isSpecial())
										continue;
									
									if (str.length() > 0)
										str.append(", ");
									
									str.append(type.getName());
								}
								
								TitanChat.getInstance().sendInfo(player, "Available types: " + str.toString());
							}
							
						} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
						
					} else { TitanChat.getInstance().sendWarning(player, "No such channel"); }
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = cm.getChannel(player);
					
					if (channel == null) {
						TitanChat.getInstance().sendWarning(player, "Specify a channel or join a channel to use this command");
						return;
					}
					
					if (TitanChat.getInstance().isStaff(player)) {
						if (Type.fromName(args[0]) != null) {
							switch (Type.fromName(args[0])) {
							
							case CUSTOM:
							case PASSWORD:
							case PRIVATE:
							case PUBLIC:
							case UNKNOWN:
								TitanChat.getInstance().sendInfo(player, "This Type is not available as a channel Type");
								break;
								
							case DEFAULT:
							case NONE:
							case STAFF:
								channel.setType(args[0]);
								channel.save();
								
								TitanChat.getInstance().sendInfo(player, "The channel is now " + Type.fromName(args[0]).getName());
								break;
							}
							
						} else {
							TitanChat.getInstance().sendWarning(player, "Type does not exist");
							
							StringBuilder str = new StringBuilder();
							
							for (Type type : Type.values()) {
								if (!type.isSpecial())
									continue;
								
								if (str.length() > 0)
									str.append(", ");
								
								str.append(type.getName());
							}
							
							TitanChat.getInstance().sendInfo(player, "Available types: " + str.toString());
						}
						
					} else { TitanChat.getInstance().sendWarning(player, "You do not have permission"); }
				}
			}
		};
		
		private String name;
		private String[] triggers;
		private String usage;
		
		private static Map<String, Settings> NAME_MAP = new HashMap<String, Settings>();
		private static Map<String, Settings> TRIGGER_MAP = new HashMap<String, Settings>();
		
		private Settings(String name, String[] triggers, String usage) {
			this.name = name;
			this.triggers = triggers;
			this.usage = usage;
		}
		
		static {
			for (Settings setting : EnumSet.allOf(Settings.class)) {
				NAME_MAP.put(setting.name.toLowerCase(), setting);
				
				for (String trigger : setting.triggers)
					TRIGGER_MAP.put(trigger.toLowerCase(), setting);
			}
		}
		
		/**
		 * Executes the command
		 * 
		 * @param player The command sender
		 * 
		 * @param args The command arguments
		 */
		public abstract void execute(Player player, String[] args);
		
		/**
		 * Gets the command from its name
		 * 
		 * @param name The name of the command
		 * 
		 * @return The command if found, otherwise null
		 */
		public static Settings fromName(String name) {
			return NAME_MAP.get(name.toLowerCase());
		}
		
		/**
		 * Gets the command from one of its triggers
		 * 
		 * @param trigger The trigger of the command
		 * 
		 * @return The command if found, otherwise null
		 */
		public static Settings fromTrigger(String trigger) {
			return TRIGGER_MAP.get(trigger.toLowerCase());
		}
		
		/**
		 * Gets the aliases of the command
		 * 
		 * @return The command aliases
		 */
		public String[] getAliases() {
			return triggers;
		}
		
		/**
		 * Gets the name of the command
		 * 
		 * @return The command name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Sends an invalid argument length warning to the Player
		 * 
		 * @param player The Player to warn
		 */
		public void invalidArgLength(Player player) {
			player.sendMessage("[TitanChat] " + ChatColor.RED + "Invalid Argument Length");
			player.sendMessage("[TitanChat] " + ChatColor.GOLD + "Usage: /titanchat set " + usage);
		}
	}
}