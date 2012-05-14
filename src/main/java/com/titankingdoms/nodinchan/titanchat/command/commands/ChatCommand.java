package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;
import com.titankingdoms.nodinchan.titanchat.events.MessageReceiveEvent;
import com.titankingdoms.nodinchan.titanchat.events.MessageSendEvent;
import com.titankingdoms.nodinchan.titanchat.mail.MailManager.Mail;

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
	@CommandID(name = "Broadcast", triggers = { "broadcast", "bc" }, requireChannel = false)
	@CommandInfo(description = "Broadcasts the message globally", usage = "broadcast [message]")
	public void broadcast(Player player, String[] args) {
		if (args.length < 1 || !plugin.getPermsBridge().has(player, "TitanChat.broadcast")) { return; }
		
		StringBuilder str = new StringBuilder();
		
		for (String word : args) {
			if (str.length() > 1)
				str.append(" ");
			
			str.append(word);
		}
		
		MessageSendEvent event = new MessageSendEvent(player, plugin.getServer().getOnlinePlayers(), str.toString());
		plugin.getServer().getPluginManager().callEvent(event);
		
		plugin.getServer().broadcastMessage(plugin.getFormatHandler().broadcastFormat(player).replace("%message", event.getMessage()));
	}
	
	/**
	 * Emote Command - Action emote shown in channel
	 */
	@CommandID(name = "Emote", triggers = { "me", "em" })
	@CommandInfo(description = "Action emote shown in channel", usage = "me [action]")
	public void emote(Player player, String[] args) {
		if (args.length < 1 || !plugin.getPermsBridge().has(player, "TitanChat.me")) { return; }
		
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
		
		MessageSendEvent event = new MessageSendEvent(player, recipants, str.toString());
		plugin.getServer().getPluginManager().callEvent(event);
		
		String format = plugin.getFormatHandler().emoteFormat(player);
		
		for (Player recipant : recipants) {
			MessageReceiveEvent receiveEvent = new MessageReceiveEvent(player, recipant, format.replace("%action", "%message"), event.getMessage());
			plugin.getServer().getPluginManager().callEvent(receiveEvent);
			
			if (receiveEvent.isCancelled()) { continue; }
			
			recipant.sendMessage(receiveEvent.getFormattedMessage());
		}
		
		if (cm.getChannel(player) == null)
			player.sendMessage(ChatColor.GOLD + "Nobody hears you...");
	}
	
	/**
	 * Mail Command - Manages mail
	 */
	@CommandID(name = "Mail", triggers = "mail", requireChannel = false)
	@CommandInfo(description = "Manages mail", usage = "mail <command> <arguments>")
	public void mail(Player player, String[] args) {
		if (!plugin.getMailManager().enable()) {
			plugin.sendWarning(player, "The Mail System has been disabled");
			return;
		}
		
		if (args.length < 1) {
			plugin.getServer().dispatchCommand(player, "titanchat mail help");
			return;
		}
		
		Mail mail = Mail.fromName(args[0].toLowerCase());
		
		if (mail != null)
			mail.execute(player, Arrays.copyOfRange(args, 1, args.length));
		else
			plugin.sendWarning(player, "Invalid mail command");
	}
	
	/**
	 * Silence Command - Silences the channel/server
	 */
	@CommandID(name = "Silence", triggers = "silence", requireChannel = false)
	@CommandInfo(description = "Silences the channel/server", usage = "silence [channel]")
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
	@CommandID(name = "whisper", triggers = { "whisper", "w" })
	@CommandInfo(description = "Whisper messages to players", usage = "whisper [player] [message]")
	public void whisper(Player player, String[] args) {
		if (args.length < 2 || !plugin.getPermsBridge().has(player, "TitanChat.whisper")) { return; }
		
		if (plugin.getPlayer(args[0]) == null || !args[0].equalsIgnoreCase("console")) {
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
		
		if (!args[0].equalsIgnoreCase("console")) {
			MessageSendEvent event = new MessageSendEvent(player, new Player[] { plugin.getPlayer(args[0]) }, str.toString());
			plugin.getServer().getPluginManager().callEvent(event);
			
			String format = plugin.getFormatHandler().whisperFormat(player);
			
			MessageReceiveEvent receiveEvent = new MessageReceiveEvent(player, plugin.getPlayer(args[0]), format, event.getMessage());
			plugin.getServer().getPluginManager().callEvent(receiveEvent);
			
			player.sendMessage(ChatColor.DARK_PURPLE + "[You -> " + plugin.getPlayer(args[0]).getDisplayName() + "] " + event.getMessage());
			plugin.getPlayer(args[0]).sendMessage(receiveEvent.getFormattedMessage());
			
		} else { plugin.getLogger().info(player.getName() + " whispers: " + str.toString()); }
	}
}