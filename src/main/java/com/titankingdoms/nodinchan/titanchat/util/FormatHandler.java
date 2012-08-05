package com.titankingdoms.nodinchan.titanchat.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.util.Info;
import com.titankingdoms.nodinchan.titanchat.event.chat.MessageFormatEvent;
import com.titankingdoms.nodinchan.titanchat.util.variable.VariableHandler.Variable;

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
 * FormatHandler - Handles formatting
 * 
 * @author NodinChan
 *
 */
public final class FormatHandler {
	
	private static TitanChat plugin;
	
	protected static final Debugger db = new Debugger(5);
	
	private final Pattern pattern = Pattern.compile("(?i)(&)([0-9a-fk-or])");
	
	public FormatHandler() {
		FormatHandler.plugin = TitanChat.getInstance();
	}
	
	/**
	 * Gets the broadcast format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @return The formatted message
	 */
	public String broadcastFormat(CommandSender sender) {
		return Format.BROADCAST.format(sender);
	}
	
	/**
	 * Colourises the message according to the permissions of the sender
	 * 
	 * @param sender The message sender
	 * 
	 * @param msg The message
	 * 
	 * @return The processed text
	 */
	public String colour(Player sender, String msg) {
		StringBuffer str = new StringBuffer();
		Matcher match = pattern.matcher(msg);
		
		while (match.find()) {
			ChatColor colour = ChatColor.getByChar(match.group(2).toLowerCase());
			
			if (plugin.getPermissionsHandler().has(sender, "TitanChat.colourstyle.&" + colour.getChar()))
				match.appendReplacement(str, colour.toString());
			else
				match.appendReplacement(str, "");
		}
		
		return match.appendTail(str).toString();
	}
	
	/**
	 * Colourises the message
	 * 
	 * @param text The message
	 * 
	 * @return The colourised text
	 */
	public String colourise(String text) {
		return text.replaceAll(pattern.toString(), "\u00A7$2");
	}
	
	/**
	 * Decolourises the message
	 * 
	 * @param message The message
	 * 
	 * @return The decolourised text
	 */
	public String decolourise(String message) {
		return message.replaceAll(pattern.toString(), "");
	}
	
	/**
	 * Gets the emote format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @return The formatted message
	 */
	public String emoteFormat(CommandSender sender) {
		return Format.EMOTE.format(sender);
	}
	
	/**
	 * Gets the format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @param channel The channel to send to
	 * 
	 * @param defaultMc Whether the it's default Minecraft without channels
	 * 
	 * @return The formatted message
	 */
	public String format(Player player, String channel) {
		MessageFormatEvent event = new MessageFormatEvent(player, Format.CHANNEL.format(player, channel));
		plugin.getServer().getPluginManager().callEvent(event);
		
		return event.getFormat();
	}
	
	/**
	 * Loads the basic Chat variables
	 */
	public void load() {
		plugin.getVariableManager().register(new Variable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermissionsHandler().getPlayerPrefix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%prefix";
			}
			
			@Override
			public VarType getVarType() {
				return VarType.FORMAT;
			}
			
		}, new Variable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermissionsHandler().getPlayerSuffix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%suffix";
			}
			
			@Override
			public VarType getVarType() {
				return VarType.FORMAT;
			}
			
		}, new Variable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermissionsHandler().getGroupPrefix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%gprefix";
			}
			
			@Override
			public VarType getVarType() {
				return VarType.FORMAT;
			}
			
		}, new Variable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermissionsHandler().getGroupSuffix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%gsuffix";
			}
			
			@Override
			public VarType getVarType() {
				return VarType.FORMAT;
			}
		});
	}
	
	/**
	 * Splits a line into lines so that Minecraft does not cut them into a few lines
	 * 
	 * @param line The line to split
	 * 
	 * @return The splitted line
	 */
	public String[] regroup(String format, String line) {
		List<String> lines = new LinkedList<String>();
		
		while (line.length() > 119) {
			if (lines.size() < 1) {
				if (format.contains("%message") || format.contains("%action")) {
					int index = format.indexOf("%message");
					
					if (index < 0)
						index = format.indexOf("%action");
					
					if (line.charAt(119 - index) != ' ') {
						int end = line.lastIndexOf(" ", 119 - index);
						
						if (end != -1) {
							lines.add(line.substring(0, end).trim());
							line = ChatColor.getLastColors(lines.get(lines.size() - 1)) + line.substring(end).trim();
						}
						
					} else {
						lines.add(line.substring(0, 119 - index).trim());
						line = ChatColor.getLastColors(lines.get(lines.size() - 1)) + line.substring(119 - index).trim();
					}
					
					continue;
				}
			}
			
			if (line.charAt(119) != ' ') {
				int end = line.lastIndexOf(" ", 119);
				
				if (end != -1) {
					lines.add(line.substring(0, end).trim());
					line = ChatColor.getLastColors(lines.get(lines.size() - 1)) + line.substring(end).trim();
				}
				
			} else {
				lines.add(line.substring(0, 119).trim());
				line = ChatColor.getLastColors(lines.get(lines.size() - 1)) + line.substring(119).trim();
			}
		}
		
		if (line.length() > 0)
			lines.add(ChatColor.getLastColors(lines.get(lines.size() - 1)) + line.trim());
		
		return lines.toArray(new String[0]);
	}
	
	/**
	 * Gets the whisper format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @return The formatted message
	 */
	public String whisperFormat(CommandSender sender) {
		return Format.WHISPER.format(sender);
	}
	
	/**
	 * Format - Formats
	 * 
	 * @author NodinChan
	 *
	 */
	private enum Format {
		BROADCAST {
			
			@Override
			protected String format(Object... params) {
				String format = plugin.getConfig().getString("broadcast.server.format");;
				
				if (params[0] instanceof Player) {
					format = plugin.getConfig().getString("broadcast.player.format");
					format = format.replace("%player", ((Player) params[0]).getDisplayName());
					format = format.replace("%name", ((Player) params[0]).getName());
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		CHANNEL {
			
			@Override
			protected String format(Object... params) {
				String format = "";
				
				Info info = plugin.getManager().getChannelManager().getChannel((String) params[1]).getInfo();
				
				if (!plugin.getConfig().getBoolean("formatting.use-custom-format")) {
					format = "%tag %prefix%player%suffix&f: %message";
					format = format.replace("%player", info.getNameColour() + ((Player) params[0]).getDisplayName() + "&f");
					format = format.replace("%name", info.getNameColour() + ((Player) params[0]).getName() + "&f");
					format = format.replace("%tag", info.getTag());
					format = format.replace("%message", info.getChatColour() + "%message");
					
				} else {
					format = info.getFormat();
					format = format.replace("%player", info.getNameColour() + ((Player) params[0]).getDisplayName() + "&f");
					format = format.replace("%name", info.getNameColour() + ((Player) params[0]).getName() + "&f");
					format = format.replace("%tag", info.getTag());
					format = format.replace("%message", info.getChatColour() + "%message");
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		EMOTE {
			
			@Override
			protected String format(Object... params) {
				String format = plugin.getConfig().getString("emote.server.format");
				
				if (params[0] instanceof Player) {
					format = plugin.getConfig().getString("emote.player.format");
					format = format.replace("%player", ((Player) params[0]).getDisplayName());
					format = format.replace("%name", ((Player) params[0]).getName());
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		WHISPER {
			
			@Override
			protected String format(Object... params) {
				String format = plugin.getConfig().getString("whisper.server.format");
				
				if (params[0] instanceof Player) {
					format = plugin.getConfig().getString("whisper.player.format");
					format = format.replace("%player", ((Player) params[0]).getDisplayName());
					format = format.replace("%name", ((Player) params[0]).getName());
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		};
		
		/**
		 * Gets the format
		 * 
		 * @param params Parameters for each kind of format
		 * 
		 * @return The format
		 */
		protected abstract String format(Object... params);
	}
}