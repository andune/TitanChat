package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TitanChatPlayerListener extends PlayerListener {
	
	private TitanChat plugin;
	private Channel ch;
	
	public TitanChatPlayerListener(TitanChat plugin) {
		this.plugin = plugin;
		this.ch = new Channel(plugin);
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		
		String msg = event.getMessage();
		
		if (plugin.isMuted(player, plugin.getChannel(player))) {
			event.setCancelled(true);
			return;
		}
		
		if (plugin.isGlobal(plugin.getChannel(player))) {
			for (Player receiver : plugin.getServer().getOnlinePlayers()) {
				if (plugin.has(player, "TitanChat.allowcolours")) {
					receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, true));
					
				} else {
					receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, ch.allowColours(player)));
				}
			}
			
		} else {
			for (Player receiver : plugin.getParticipants(plugin.getChannel(player))) {
				if (plugin.has(player, "TitanChat.allowcolours")) {
					receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, true));
					
				} else {
					receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, ch.allowColours(player)));
				}
			}
		}
		
		plugin.getLogger().info("<" + player.getName() + "> " + ch.decolourize(msg));
		
		event.setCancelled(true);
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		String channelName = "";
		
		if (event.getPlayer().hasPermission("TitanChat.admin")) {
			if (plugin.getStaffChannel() != null) {
				channelName = plugin.getStaffChannel();
				
			} else {
				channelName = plugin.getDefaultChannel();
			}
			
		} else {
			channelName = plugin.getDefaultChannel();
		}
		
		plugin.enterChannel(event.getPlayer(), channelName);
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		String channelName = plugin.getChannel(event.getPlayer());
		plugin.leaveChannel(event.getPlayer(), channelName);
	}
}
