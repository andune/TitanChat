package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;

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
	
	@CommandID(name = "Broadcast", triggers = "broadcast", requireChannel = false)
	@CommandInfo(description = "Broadcasts the message globally", usage = "broadcast [message]")
	public void broadcast(Player player, String[] args) {
		if (args.length < 1 || !plugin.has(player, "TitanChat.broadcast")) { return; }
		
		StringBuilder broadcastStr = new StringBuilder();
		
		for (String word : args) {
			if (broadcastStr.length() > 1)
				broadcastStr.append(" ");
			
			broadcastStr.append(word);
		}
		
		plugin.getServer().broadcastMessage(plugin.getFormatHandler().broadcastFormat(player, broadcastStr.toString()));
		plugin.getLogger().info("<" + player.getName() + "> " + broadcastStr.toString());
	}
	
	@CommandID(name = "Emote", triggers = { "me", "em" })
	@CommandInfo(description = "Action emote shown in channel", usage = "me [action]")
	public void emote(Player player, String[] args) {
		if (args.length < 1 || !plugin.has(player, "TitanChat.me")) { return; }
		
		StringBuilder meStr = new StringBuilder();
		
		for (String word : args) {
			if (meStr.length() > 1)
				meStr.append(" ");
			
			meStr.append(word);
		}
		
		cm.getChannel(player).sendMessage(plugin.getFormatHandler().emoteFormat(player, meStr.toString()));
		plugin.getLogger().info("* " + player.getName() + " " + meStr.toString());
	}
	
	@CommandID(name = "Silence", triggers = "silence", requireChannel = false)
	@CommandInfo(description = "Silences the channel/server", usage = "silence [channel]")
	public void silence(Player player, String[] args) {
		if (plugin.has(player, "TitanChat.silence")) {
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
				if (plugin.has(player, "TitanChat.silence.server")) {
					plugin.setSilenced((plugin.isSilenced()) ? false : true);
					
					if (plugin.isSilenced())
						plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.RED + "All channels have been silenced");
					else
						plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.GOLD + "Channels are no longer silenced");
				}
			}
		}
	}
}