package com.titankingdoms.nodinchan.titanchat.util;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.channel.StandardChannel;
import com.titankingdoms.nodinchan.titanchat.channel.Variables;
import com.titankingdoms.nodinchan.titanchat.events.MessageFormatEvent;

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
	
	/**
	 * Initialises variables
	 * 
	 * @param plugin TitanChat
	 */
	public FormatHandler(TitanChat plugin) {
		FormatHandler.plugin = plugin;
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
		return text.replaceAll("(&([a-f0-9A-Fk-oK-O]))", "\u00A7$2");
	}
	
	/**
	 * Check if Channel convert colours
	 * 
	 * @param name The Channel name
	 * 
	 * @return True if converts
	 */
	public boolean colours(String name) {
		if (plugin.getChannelManager().getStaffChannel() != null) {
			if (plugin.getChannelManager().getStaffChannel().equals(plugin.getChannelManager().getChannel(name)))
				return true;
		}
		
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
		return message.replaceAll("(&([a-f0-9A-Fk-oK-O]))", "");
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
	
	private enum Format {
		BROADCAST {
			
			@Override
			public String format(Object... params) {
				String format = plugin.getConfig().getString("broadcast.player.format");
				format = format.replace("%player", ((Player) params[0]).getDisplayName());
				format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
				format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
				format = format.replace("%gprefix", plugin.getPermsBridge().getGroupPrefix((Player) params[0]));
				format = format.replace("%gsuffix", plugin.getPermsBridge().getGroupSuffix((Player) params[0]));
				
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
					format = format.replace("%player", variables.getNameColour() + ((Player) params[0]).getDisplayName());
					format = format.replace("%tag", variables.getTag());
					format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
					format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
					format = format.replace("%gprefix", plugin.getPermsBridge().getGroupPrefix((Player) params[0]));
					format = format.replace("%gsuffix", plugin.getPermsBridge().getGroupSuffix((Player) params[0]));
					format = format.replace("%message", variables.getChatColour() + "%message");
					
				} else {
					format = variables.getFormat();
					format = format.replace("%player", variables.getNameColour() + ((Player) params[0]).getDisplayName());
					format = format.replace("%tag", variables.getTag());
					format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
					format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
					format = format.replace("%gprefix", plugin.getPermsBridge().getGroupPrefix((Player) params[0]));
					format = format.replace("%gsuffix", plugin.getPermsBridge().getGroupSuffix((Player) params[0]));
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
					format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
					format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
					
				} else {
					format = plugin.getConfig().getString("formatting.format");
					
					format = format.replace("%player", "%1$s");
					format = format.replace("%message", "%2$s");
					
					format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
					format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
					format = format.replace("%gprefix", plugin.getPermsBridge().getGroupPrefix((Player) params[0]));
					format = format.replace("%gsuffix", plugin.getPermsBridge().getGroupSuffix((Player) params[0]));
				}
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		EMOTE {
			
			@Override
			public String format(Object... params) {
				String format = plugin.getConfig().getString("emote.player.format");
				format = format.replace("%player", ((Player) params[0]).getDisplayName());
				format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
				format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
				format = format.replace("%gprefix", plugin.getPermsBridge().getGroupPrefix((Player) params[0]));
				format = format.replace("%gsuffix", plugin.getPermsBridge().getGroupSuffix((Player) params[0]));
				
				return plugin.getFormatHandler().colourise(format);
			}
		},
		WHISPER {
			
			@Override
			public String format(Object... params) {
				String format = plugin.getConfig().getString("whisper.player.format");
				format = format.replace("%player", ((Player) params[0]).getDisplayName());
				format = format.replace("%prefix", plugin.getPermsBridge().getPlayerPrefix((Player) params[0]));
				format = format.replace("%suffix", plugin.getPermsBridge().getPlayerSuffix((Player) params[0]));
				format = format.replace("%gprefix", plugin.getPermsBridge().getGroupPrefix((Player) params[0]));
				format = format.replace("%gsuffix", plugin.getPermsBridge().getGroupSuffix((Player) params[0]));
				
				return plugin.getFormatHandler().colourise(format);
			}
		};
		
		public abstract String format(Object... params);
	}
}