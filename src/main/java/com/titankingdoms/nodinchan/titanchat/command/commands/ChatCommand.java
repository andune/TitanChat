package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
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
	
	/**
	 * Broadcast Command - Broadcasts the message globally
	 */
	@Command(server = true)
	@Aliases("bc")
	@Description("Broadcasts the message globally")
	@Permission("TitanChat.broadcast")
	@Usage("broadcast [message]")
	public void broadcast(CommandSender sender, Channel channel, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "broadcast"); return; }
		
		if (!plugin.getConfig().getBoolean("chat." + ((sender instanceof Player) ? "player" : "server") + ".enable")) {
			plugin.send(MessageLevel.WARNING, sender, "Broadcast Command Disabled");
			return;
		}
		
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
	public void emote(Player player, Channel channel, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "emote"); return; }
		
		if (!plugin.getConfig().getBoolean("chat.player.enable")) {
			plugin.send(MessageLevel.WARNING, player, "Emote Command Disabled");
			return;
		}
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (str.length() > 0)
				str.append(" ");
			
			str.append(word);
		}
		
		List<Player> recipants = new ArrayList<Player>();
		
		if (channel == null) {
			recipants.add(player);
			
		} else {
			for (Participant participant : channel.getParticipants()) {
				if (participant.getPlayer() != null)
					recipants.add(participant.getPlayer());
			}
		}
		
		String format = plugin.getFormatHandler().emoteFormat(player);
		
		EmoteEvent event = new EmoteEvent(player, new Message(format, str.toString()));
		plugin.getServer().getPluginManager().callEvent(event);
		
		if (channel == null) {
			String[] lines = plugin.getFormatHandler().regroup(event.getFormat(), event.getMessage());
			
			player.sendMessage(event.getFormat().replace("%action", lines[0]));
			player.sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
			
		} else { channel.send(event.getFormat().replace("%action", event.getMessage())); }
	}
	
	/**
	 * Send Command - Sends a message to the channel
	 */
	@Command(channel = true, server = true)
	@Aliases("s")
	@Description("Sends a message to the channel")
	@Usage("send [message]")
	public void send(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "send", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "send"); return; }
	}
	
	/**
	 * Silence Command - Silences the channel/server
	 */
	@Command(server = true)
	@Description("Silences the channel/server")
	@Usage("silence")
	public void silence(CommandSender sender, Channel channel, String[] args) {
		if (channel == null) {
			plugin.setSilenced(!plugin.isSilenced());
			return;
		}
		
		if (channel.handleCommand(sender, "silence", args))
			return;
	}
	
	/**
	 * Whisper Command - Whisper messages to players
	 */
	@Command(server = true)
	@Aliases("w")
	@Description("Whispers the message to the player")
	@Permission("TitanChat.whisper")
	@Usage("whisper [player] [message]")
	public void whisper(CommandSender sender, Channel channel, String[] args) {
		if (args.length < 2) { invalidArgLength(sender, "whisper"); return; }
		
		if (!plugin.getConfig().getBoolean("chat." + ((sender instanceof Player) ? "player" : "server") + ".enable")) {
			plugin.send(MessageLevel.WARNING, sender, "Whisper Command Disabled");
			return;
		}
		
		CommandSender recipant = null;
		
		if (!args[0].equalsIgnoreCase("#!")) {
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
		String sendFormat = plugin.getFormatHandler().colourise("&5[You -> " + sender.getName() + "] %message");
		
		WhisperEvent event = new WhisperEvent(sender, recipant, new Message(format, str.toString()));
		plugin.getServer().getPluginManager().callEvent(event);
		
		String[] lines = plugin.getFormatHandler().regroup(event.getFormat(), event.getMessage());
		
		event.getSender().sendMessage(sendFormat.replace("%message", lines[0]));
		event.getRecipant().sendMessage(event.getFormat().replace("%message", lines[0]));
		event.getSender().sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
		event.getRecipant().sendMessage(Arrays.copyOfRange(lines, 1, lines.length));
	}
}