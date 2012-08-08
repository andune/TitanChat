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
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
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
		return Format.BROADCAST.format(sender, "");
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
	public String emoteFormat(CommandSender sender, String channel) {
		return Format.EMOTE.format(sender, channel);
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
	
	public String serverToChannelFormat(Channel channel) {
		return Format.SERVER.format(plugin.getServer().getConsoleSender(), channel.getName());
	}
	
	/**
	 * Splits the line into lines of max 119 characters each
	 * 
	 * @param line The line to process
	 * 
	 * @return The processed String array
	 */
	public String[] split(String line) {
		List<String> lines = new LinkedList<String>();
		
		while (line.length() > 119) {
			int end = 119;
			
			if (line.charAt(end) != ' ')
				end = line.lastIndexOf(' ', 119);
			
			if (end == -1)
				end = 119;
			
			lines.add(line.substring(0, end));
			line = (ChatColor.getLastColors(lines.get(lines.size() - 1)) + line.substring(end)).trim();
		}
		
		if (line.length() > 0)
			lines.add(line);
		
		return lines.toArray(new String[0]);
	}
	
	/**
	 * Splits the line into lines of max 119 characters each after formatting
	 * 
	 * @param format The format to use
	 * 
	 * @param variable The variable to replace with the line
	 * 
	 * @param line The line to process
	 * 
	 * @return The processed String array
	 */
	public String[] splitAndFormat(String format, String variable, String line) {
		return split(format.replace(variable, line));
	}
	
	/**
	 * Gets the whisper format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @return The formatted message
	 */
	public String whisperFormat(CommandSender sender) {
		return Format.WHISPER.format(sender, "");
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
			protected String format(CommandSender sender, String channel) {
				String format = plugin.getConfig().getString("chat.server.broadcast");
				
				if (sender instanceof Player) {
					format = plugin.getConfig().getString("chat.player.broadcast");
					format = format.replace("%player", ((Player) sender).getDisplayName());
					format = format.replace("%name", sender.getName());
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		CHANNEL {
			
			@Override
			protected String format(CommandSender sender, String channel) {
				String format = "%tag %prefix%player%suffix&f: %message";
				
				Info info = plugin.getManager().getChannelManager().getChannel(channel).getInfo();
				
				if (plugin.getConfig().getBoolean("formatting.use-custom-format"))
					format = info.getFormat();
				
				format = format.replace("%player", ((Player) sender).getDisplayName());
				format = format.replace("%name", sender.getName());
				format = format.replace("%tag", info.getTag());
				format = format.replace("%message", info.getChatColour() + "%message");
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		EMOTE {
			
			@Override
			protected String format(CommandSender sender, String channel) {
				String format = plugin.getConfig().getString("chat.server.emote");
				
				if (channel != null && !channel.isEmpty())
					format = plugin.getConfig().getString("chat.channel-emote.format");
				else if (sender instanceof Player)
					format = plugin.getConfig().getString("chat.player.emote");
				
				if (sender instanceof Player) {
					format = format.replace("%player", ((Player) sender).getDisplayName());
					format = format.replace("%name", sender.getName());
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		SERVER {
			
			@Override
			protected String format(CommandSender sender, String channel) {
				String format = plugin.getConfig().getString("chat.serverToChannel");
				
				Info info = plugin.getManager().getChannelManager().getChannel(channel).getInfo();
				format = format.replace("%tag", info.getTag());
				format = format.replace("%message", info.getChatColour() + "%message");
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		WHISPER {
			
			@Override
			protected String format(CommandSender sender, String channel) {
				String format = plugin.getConfig().getString("chat.server.whisper");
				
				if (sender instanceof Player) {
					format = plugin.getConfig().getString("chat.player.whisper");
					format = format.replace("%player", ((Player) sender).getDisplayName());
					format = format.replace("%name", ((Player) sender).getName());
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
		protected abstract String format(CommandSender sender, String channel);
	}
}