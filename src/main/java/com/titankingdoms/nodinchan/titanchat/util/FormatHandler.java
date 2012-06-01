package com.titankingdoms.nodinchan.titanchat.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.channel.StandardChannel;
import com.titankingdoms.nodinchan.titanchat.channel.Variables;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.event.MessageFormatEvent;
import com.titankingdoms.nodinchan.titanchat.util.variable.Variable.IVariable;

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
	public String broadcastFormat(Player player) {
		MessageFormatEvent event = new MessageFormatEvent(player, Format.BROADCAST.format(player));
		plugin.getServer().getPluginManager().callEvent(event);
		
		return event.getFormat();
	}
	
	/**
	 * Colourises the message
	 * 
	 * @param text The message
	 * 
	 * @return The colourised text
	 */
	public String colourise(String text) {
		return text.replaceAll("(&([a-f0-9A-Fk-oK-OrR]))", "\u00A7$2");
	}
	
	/**
	 * Check if Channel convert colours
	 * 
	 * @param name The Channel name
	 * 
	 * @return True if converts
	 */
	public boolean colours(String name) {
		if (plugin.getChannelManager().getChannel(name).getSpecialType().equals(Type.STAFF))
			return true;
		
		if (plugin.getChannelManager().getChannel(name) instanceof CustomChannel)
			return true;
		
		return ((StandardChannel) plugin.getChannelManager().getChannel(name)).getVariables().convert();
	}
	
	/**
	 * Decolourises the message
	 * 
	 * @param message The message
	 * 
	 * @return The decolourised text
	 */
	public String decolourise(String message) {
		return message.replaceAll("(&([a-f0-9A-Fk-oK-OrR]))", "");
	}
	
	/**
	 * Gets the emote format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @return The formatted message
	 */
	public String emoteFormat(Player player) {
		MessageFormatEvent event = new MessageFormatEvent(player, Format.EMOTE.format(player));
		plugin.getServer().getPluginManager().callEvent(event);
		
		return event.getFormat();
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
	public String format(Player player, String channel, boolean defaultMc) {
		if (defaultMc) {
			MessageFormatEvent event = new MessageFormatEvent(player, Format.DEFAULT.format(player));
			plugin.getServer().getPluginManager().callEvent(event);
			
			return event.getFormat();
			
		} else {
			MessageFormatEvent event = new MessageFormatEvent(player, Format.CHANNEL.format(player, channel));
			plugin.getServer().getPluginManager().callEvent(event);
			
			return event.getFormat();
		}
	}
	
	/**
	 * Loads the basic Chat variables
	 */
	public void load() {
		plugin.getVariableManager().register(new IVariable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermsBridge().getPlayerPrefix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%prefix";
			}
			
		}, new IVariable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermsBridge().getPlayerSuffix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%suffix";
			}
			
		}, new IVariable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermsBridge().getGroupPrefix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%gprefix";
			}
			
		}, new IVariable() {
			
			@Override
			public Class<? extends Event> getEvent() {
				return MessageFormatEvent.class;
			}
			
			@Override
			public String getReplacement(Player sender, Player... recipants) {
				return colourise(plugin.getPermsBridge().getGroupSuffix(sender));
			}
			
			@Override
			public String getVariable() {
				return "%gsuffix";
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
		List<String> lines = new ArrayList<String>();
		
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
							line = line.substring(end).trim();
						}
						
					} else {
						lines.add(line.substring(0, 119 - index).trim());
						line = line.substring(119 - index).trim();
					}
					
					continue;
				}
			}
			
			if (line.charAt(119) != ' ') {
				int end = line.lastIndexOf(" ", 119);
				
				if (end != -1) {
					lines.add(line.substring(0, end).trim());
					line = line.substring(end).trim();
				}
				
			} else {
				lines.add(line.substring(0, 119).trim());
				line = line.substring(119).trim();
			}
		}
		
		if (line.length() > 0)
			lines.add(line.trim());
		
		return lines.toArray(new String[lines.size()]);
	}
	
	/**
	 * Gets the whisper format and formats the message
	 * 
	 * @param player The sender
	 * 
	 * @return The formatted message
	 */
	public String whisperFormat(Player player) {
		MessageFormatEvent event = new MessageFormatEvent(player, Format.WHISPER.format(player));
		plugin.getServer().getPluginManager().callEvent(event);
		
		return event.getFormat();
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
			public String format(Object... params) {
				String format = plugin.getConfig().getString("broadcast.player.format");
				format = format.replace("%player", ((Player) params[0]).getDisplayName());
				return plugin.getFormatHandler().colourise(format);
			}
		},
		CHANNEL {
			
			@Override
			public String format(Object... params) {
				String format = "";
				
				if (plugin.getChannelManager().getChannel((String) params[1]) instanceof CustomChannel) {
					CustomChannel channel = (CustomChannel) plugin.getChannelManager().getChannel((String) params[1]);
					return channel.format((Player) params[0], channel.getFormat());
				}
				
				Variables variables = ((StandardChannel) plugin.getChannelManager().getChannel((String) params[1])).getVariables();
				
				if (plugin.useDefaultFormat()) {
					format = "%tag %prefix%player%suffix&f: %message";
					format = format.replace("%player", variables.getNameColour() + ((Player) params[0]).getDisplayName() + "&f");
					format = format.replace("%tag", variables.getTag());
					format = format.replace("%message", variables.getChatColour() + "%message");
					
				} else {
					format = variables.getFormat();
					format = format.replace("%player", variables.getNameColour() + ((Player) params[0]).getDisplayName() + "&f");
					format = format.replace("%tag", variables.getTag());
					format = format.replace("%message", variables.getChatColour() + "%message");
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		DEFAULT {
			
			@Override
			public String format(Object... params) {
				String format = "";
				
				if (plugin.useDefaultFormat()) {
					format = "<%prefix&1$s%suffix&f> %2$s";
					
				} else {
					format = plugin.getConfig().getString("formatting.format");
					
					format = format.replace("%player", "%1$s");
					format = format.replace("%message", "%2$s");
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		EMOTE {
			
			@Override
			public String format(Object... params) {
				String format = plugin.getConfig().getString("emote.player.format");
				format = format.replace("%player", ((Player) params[0]).getDisplayName());
				return plugin.getFormatHandler().colourise(format);
			}
		},
		WHISPER {
			
			@Override
			public String format(Object... params) {
				String format = plugin.getConfig().getString("whisper.player.format");
				format = format.replace("%player", ((Player) params[0]).getDisplayName());
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
		public abstract String format(Object... params);
	}
}