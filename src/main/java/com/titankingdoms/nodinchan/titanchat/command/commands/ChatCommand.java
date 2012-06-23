package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.info.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.info.CommandInfo;

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
 * ChatCommand - Chat related commands
 * 
 * @author NodinChan
 *
 */
public class ChatCommand extends Command {

	private ChannelManager cm;
	
	public ChatCommand() {
		this.cm = plugin.getChannelManager();
	}
	
	/**
	 * Broadcast Command - Broadcasts the message globally
	 */
	@CommandID(name = "Broadcast", aliases = { "broadcast", "bc" }, requireChannel = false)
	@CommandInfo(description = "Broadcasts the message globally", usage = "broadcast [message]")
	public void broadcast(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Broadcast"); return; }
		
		if (!plugin.getPermsBridge().has(player, "TitanChat.broadcast")) {
			plugin.sendWarning(player, "You do not have permission");
			return;
		}
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (str.length() > 1)
				str.append(" ");
			
			str.append(word);
		}
		
		String format = plugin.getFormatHandler().broadcastFormat(player);
		
		String[] lines = plugin.getFormatHandler().regroup(format, str.toString());
		
		for (int line = 0; line < lines.length; line++) {
			if (line < 1)
				plugin.getServer().broadcastMessage(format.replace("%message", lines[0]));
			else
				plugin.getServer().broadcastMessage(lines[line]);
		}
	}
	
	/**
	 * Emote Command - Action emote shown in channel
	 */
	@CommandID(name = "Emote", aliases = { "me", "em" })
	@CommandInfo(description = "Action emote shown in channel", usage = "me [action]")
	public void emote(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Emote"); return; }
		
		if (!plugin.getPermsBridge().has(player, "TitanChat.me")) {
			plugin.sendWarning(player, "You do not have permission");
			return;
		}
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (str.length() > 1)
				str.append(" ");
			
			str.append(word);
		}
		
		List<Player> recipants = new ArrayList<Player>();
		
		if (cm.getChannel(player) == null) {
			recipants.add(player);
			
		} else {
			for (String name : cm.getChannel(player).getParticipants()) {
				if (plugin.getPlayer(name) != null && !recipants.contains(plugin.getPlayer(name)))
					recipants.add(plugin.getPlayer(name));
			}
			
			for (String name : cm.getFollowers(cm.getChannel(player))) {
				if (plugin.getPlayer(name) != null && !recipants.contains(plugin.getPlayer(name)))
					recipants.add(plugin.getPlayer(name));
			}
		}
		
		String format = plugin.getFormatHandler().emoteFormat(player);
		
		String[] lines = plugin.getFormatHandler().regroup(format, str.toString());
		
		for (Player recipant : recipants) {
			recipant.sendMessage(format.replace("%action", lines[0]));
			recipant.sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		}
		
		for (Player recipant : recipants)
			recipant.sendMessage(format.replace("%action", str.toString()));
		
		if (cm.getChannel(player) == null)
			player.sendMessage(ChatColor.GOLD + "Nobody hears you...");
	}
	
	/**
	 * Send Command - Sends a message to the channel
	 */
	@CommandID(name = "Send", aliases = "send")
	@CommandInfo(description = "Sends a message to the channel", usage = "send [channel] [message]")
	public void send(Player player, String[] args) {
		if (args.length < 2) { invalidArgLength(player, "Send"); return; }
		
		if (cm.exists(args[0])) {
			if (!cm.getChannel(args[0]).canAccess(player))
				return;
			
			if (voiceless(player, cm.getChannel(args[0])))
				return;
			
			StringBuilder str = new StringBuilder();
			
			for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
				if (str.length() > 0)
					str.append(" ");
				
				str.append(arg);
			}
			
			cm.getChannel(args[0]).sendMessage(player, str.toString());
			
		} else { plugin.sendWarning(player, "No such channel"); }
	}
	
	/**
	 * Silence Command - Silences the channel/server
	 */
	@CommandID(name = "Silence", aliases = "silence", requireChannel = false)
	@CommandInfo(description = "Silences the channel/server", usage = "silence <channel>")
	public void silence(Player player, String[] args) {
		if (plugin.getPermsBridge().has(player, "TitanChat.silence")) {
			if (!plugin.enableChannels()) {
				plugin.setSilenced((plugin.isSilenced()) ? false : true);
				
				if (plugin.isSilenced())
					plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.RED + "All channels have been silenced");
				else
					plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.GOLD + "Channels are no longer silenced");
				
				return;
			}
			
			try {
				if (cm.exists(args[0])) {
					Channel channel = cm.getChannel(args[0]);
					channel.setSilenced((channel.isSilenced()) ? false : true);
					
					for (String participant : cm.getChannel(args[0]).getParticipants()) {
						if (plugin.getPlayer(participant) != null) {
							if (channel.isSilenced())
								plugin.sendWarning(plugin.getPlayer(participant), "The channel has been silenced");
							else
								plugin.sendInfo(plugin.getPlayer(participant), "The channel is no longer silenced");
						}
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (plugin.getPermsBridge().has(player, "TitanChat.silence.server")) {
					plugin.setSilenced((plugin.isSilenced()) ? false : true);
					
					if (plugin.isSilenced())
						plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.RED + "All channels have been silenced");
					else
						plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.GOLD + "Channels are no longer silenced");
				}
			}
		}
	}
	
	/**
	 * Whisper Command - Whisper messages to players
	 */
	@CommandID(name = "Whisper", aliases = { "whisper", "w" })
	@CommandInfo(description = "Whisper messages to players", usage = "whisper [player] [message]")
	public void whisper(Player player, String[] args) {
		if (args.length < 2) { invalidArgLength(player, "Whisper"); return; }
		
		if (!plugin.getPermsBridge().has(player, "TitanChat.whisper")) {
			plugin.sendWarning(player, "You do not have permission");
			return;
		}
		
		if (plugin.getPlayer(args[0]) == null) {
			plugin.sendWarning(player, "Player not online");
			return;
		}
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (word.equals(args[0]))
				continue;
			
			if (str.length() > 1)
				str.append(" ");
			
			str.append(word);
		}
		
		String format = plugin.getFormatHandler().whisperFormat(player);
		
		String[] lines = plugin.getFormatHandler().regroup(format, str.toString());
		
		player.sendMessage(ChatColor.DARK_PURPLE + "[You -> " + plugin.getPlayer(args[0]).getDisplayName() + "] " + format.replace("%message", lines[0]));
		plugin.getPlayer(args[0]).sendMessage(format.replace("%message", lines[0]));
		player.sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		plugin.getPlayer(args[0]).sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
	}
	
	private boolean voiceless(Player player, Channel channel) {
		if (plugin.getPermsBridge().has(player, "TitanChat.voice"))
			return false;
		
		if (plugin.isSilenced()) {
			plugin.sendWarning(player, "The server is silenced");
			return true;
		}
		
		if (channel.isSilenced()) {
			plugin.sendWarning(player, "The channel is silenced");
			return true;
		}
		
		if (channel.getMuteList().contains(player.getName())) {
			plugin.sendWarning(player, "You have been muted");
			return true;
		}
		
		if (plugin.getChannelManager().isMuted(player)) {
			plugin.sendWarning(player, "You have been muted");
			return true;
		}
		
		return false;
	}
}