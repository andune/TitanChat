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
		
		for (Player receiver : plugin.getParticipants(plugin.getChannel(player))) {
			if (player.hasPermission("TitanChat.allowcolours")) {
				receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, true));
				
			} else {
				receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, ch.allowColours(player)));
			}
		}
		
		event.setCancelled(true);
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		String channelName = "";
		
		if (event.getPlayer().hasPermission("TitanChat.admin")) {
			for (String channel : plugin.getConfig().getConfigurationSection("channels").getKeys(false)) {
				if (plugin.getConfig().get("channels." + channel + ".staff") != null) {
					channelName = channel;
					
				} else {
					channelName = plugin.getDefaultChannel();
				}
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
