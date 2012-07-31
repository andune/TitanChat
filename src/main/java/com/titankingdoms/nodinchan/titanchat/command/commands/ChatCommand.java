package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.util.Participant;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.command.info.*;
import com.titankingdoms.nodinchan.titanchat.event.BroadcastEvent;
import com.titankingdoms.nodinchan.titanchat.event.EmoteEvent;
import com.titankingdoms.nodinchan.titanchat.event.WhisperEvent;
import com.titankingdoms.nodinchan.titanchat.event.util.Message;

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
public class ChatCommand extends CommandBase {

	private ChannelManager cm;
	
	public ChatCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Broadcast Command - Broadcasts the message globally
	 */
	@Command(server = true)
	@Aliases("bc")
	@Description("Broadcasts the message globally")
	@Permission("TitanChat.broadcast")
	@Usage("broadcast [message]")
	public void broadcast(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "broadcast"); return; }
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (str.length() > 0)
				str.append(" ");
			
			str.append(word);
		}
		
		String format = plugin.getFormatHandler().broadcastFormat(sender);
		
		BroadcastEvent event = new BroadcastEvent(sender, new Message(format, str.toString()));
		plugin.getServer().getPluginManager().callEvent(event);
		
		String[] lines = plugin.getFormatHandler().regroup(event.getFormat(), event.getMessage());
		
		plugin.getServer().broadcastMessage(event.getFormat().replace("%message", lines[0]));
		
		for (String line : Arrays.copyOfRange(lines, 1, lines.length))
			plugin.getServer().broadcastMessage(line);
	}
	
	/**
	 * Emote Command - Action emote shown in channel
	 */
	@Command
	@Aliases({ "em", "me" })
	@Description("Sends an action emote to the channel")
	@Permission("TitanChat.emote")
	@Usage("emote [action]")
	public void emote(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "emote"); return; }
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (str.length() > 0)
				str.append(" ");
			
			str.append(word);
		}
		
		List<Player> recipants = new ArrayList<Player>();
		
		if (cm.getChannel(player) == null) {
			recipants.add(player);
			
		} else {
			for (Participant participant : cm.getChannel(player).getParticipants()) {
				if (participant.getPlayer() != null)
					recipants.add(participant.getPlayer());
			}
		}
		
		String format = plugin.getFormatHandler().emoteFormat(player);
		
		EmoteEvent event = new EmoteEvent(player, new Message(format, str.toString()));
		plugin.getServer().getPluginManager().callEvent(event);
		
		String[] lines = plugin.getFormatHandler().regroup(event.getFormat(), event.getMessage());
		
		for (Player recipant : recipants) {
			recipant.sendMessage(event.getFormat().replace("%action", lines[0]));
			recipant.sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		}
		
		if (recipants.size() < 2)
			player.sendMessage(ChatColor.GOLD + "Nobody hears you...");
	}
	
	/**
	 * Send Command - Sends a message to the channel
	 */
	@Command(server = true)
	@Aliases("s")
	@Description("Sends a message to the channel")
	@Usage("send [channel] [message]")
	public void send(CommandSender sender, String[] args) {
		if (args.length < 2) { invalidArgLength(sender, "send"); return; }
		
		Channel channel = null;
		
		if (cm.existsByAlias(args[0]))
			channel = cm.getChannelByAlias(args[0]);
		else
			plugin.send(MessageLevel.WARNING, sender, "No such channel");
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "send", args))
			return;
	}
	
	/**
	 * Silence Command - Silences the channel/server
	 */
	@Command(server = true)
	@Description("Silences the channel/server")
	@Usage("silence <channel>")
	public void silence(CommandSender sender, String[] args) {
		try {
			Channel channel = null;
			
			if (cm.existsByAlias(args[0]))
				channel = cm.getChannelByAlias(args[0]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
			if (channel == null)
				return;
			
			if (channel.handleCommand(sender, "silence", args))
				return;
			
		} catch (IndexOutOfBoundsException e) {
			
		}
	}
	
	/**
	 * Whisper Command - Whisper messages to players
	 */
	@Command(server = true)
	@Aliases("w")
	@Description("Whispers the message to the player")
	@Permission("TitanChat.whisper")
	@Usage("whisper [player] [message]")
	public void whisper(CommandSender sender, String[] args) {
		if (args.length < 2) { invalidArgLength(sender, "whisper"); return; }
		
		CommandSender recipant = null;
		
		if (!args[0].equalsIgnoreCase("*console")) {
			if (plugin.getPlayer(args[0]) != null)
				recipant = plugin.getPlayer(args[0]);
			else
				plugin.send(MessageLevel.WARNING, sender, "Player not online");
			
		} else { recipant = plugin.getServer().getConsoleSender(); }
		
		if (recipant == null)
			return;
		
		StringBuilder str = new StringBuilder();
		
		for (int arg = 1; arg < args.length; arg++) {
			if (str.length() > 0)
				str.append(" ");
			
			str.append(args[arg]);
		}
		
		String format = plugin.getFormatHandler().whisperFormat(sender);
		
		WhisperEvent event = new WhisperEvent(sender, recipant, new Message(format, str.toString()));
		plugin.getServer().getPluginManager().callEvent(event);
		
		String[] lines = plugin.getFormatHandler().regroup(event.getFormat(), event.getMessage());
		
		event.getSender().sendMessage(event.getFormat().replace("%message", lines[0]));
		event.getRecipant().sendMessage(event.getFormat().replace("%message", lines[0]));
		event.getSender().sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		event.getRecipant().sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
	}
	
	private boolean voiceless(Player player, Channel channel) {
		if (plugin.getPermsBridge().has(player, "TitanChat.voice"))
			return false;
		
		if (plugin.isSilenced()) {
			plugin.send(MessageLevel.WARNING, player, "The server is silenced");
			return true;
		}
		
		if (cm.isSilenced(channel)) {
			plugin.send(MessageLevel.WARNING, player, "The channel is silenced");
			return true;
		}
		
		if (cm.getParticipant(player).isMuted(channel)) {
			plugin.send(MessageLevel.WARNING, player, "You have been muted");
			return true;
		}
		
		return false;
	}
}