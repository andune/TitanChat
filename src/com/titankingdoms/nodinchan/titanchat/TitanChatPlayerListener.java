package com.titankingdoms.nodinchan.titanchat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Status;
import com.titankingdoms.nodinchan.titanchat.support.TCSupport;

public class TitanChatPlayerListener implements Listener {
	
	private TitanChat plugin;
	
	public TitanChatPlayerListener(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
		
		event.setCancelled(true);

		Player player = event.getPlayer();
		
		String msg = event.getMessage();
		Channel channel = plugin.getChannel(player);
		
		if (channel.isSilenced() && !plugin.hasVoice(player)) {
			return;
		}
		
		if (channel.getStatus().equals(Status.LOCAL)) {
			channel.sendLocalMessage(player.getName(), plugin.getFormat().local(player, plugin.getFormat().filter(msg)));
			
			plugin.log(Level.INFO, "<" + player.getName() + "> " + plugin.getFormat().decolourize(msg));
			return;
		}
		
		if (channel.isSilenced() && !plugin.hasVoice(player)) {
			return;
		}
		
		if (channel.getMuteList().contains(player.getName()) && !plugin.hasVoice(player)) {
			return;
		}

		String message = plugin.getFormat().format(player, channel.getName(), msg);
		
		if (channel.isGlobal()) {
			channel.sendGlobalMessage(message);
			
		} else {
			channel.sendMessage(message);
		}
		
		Logger.getLogger("TitanLog").info("<" + player.getName() + "> " + plugin.getFormat().decolourize(msg));
		
		for (TCSupport support : plugin.getSupports()) {
			support.chatMade(player.getName(), msg);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		String channelName = "";
		
		if (plugin.has(event.getPlayer(), "TitanChat.admin") && plugin.has(event.getPlayer(), "TitanChat.admin.spawn")) {
			if (plugin.getStaffChannel() != null) {
				channelName = plugin.getStaffChannel().getName();
				
			} else {
				for (Channel channel : plugin.getChannels()) {
					if (plugin.has(event.getPlayer(), "TitanChat.spawn." + channel.getName())) {
						channelName = channel.getName();
						break;
					}
				}
				
				if (channelName.equals("")) {
					channelName = plugin.getDefaultChannel().getName();
				}
			}
			
		} else {
			for (Channel channel : plugin.getChannels()) {
				if (plugin.has(event.getPlayer(), "TitanChat.spawn." + channel.getName())) {
					channelName = channel.getName();
					break;
				}
			}
			
			if (channelName.equals("")) {
				channelName = plugin.getDefaultChannel().getName();
			}
		}
		
		plugin.enterChannel(event.getPlayer(), channelName);
		
		if (plugin.isSilenced())
			plugin.sendWarning(event.getPlayer(), "All channels have been silenced");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.leaveChannel(event.getPlayer(), plugin.getChannel(event.getPlayer()).getName());
	}
}