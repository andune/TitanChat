package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.support.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.support.Support;

public class TitanChatListener implements Listener {
	
	private TitanChat plugin;
	
	public TitanChatListener(TitanChat plugin) {
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
		
		if (plugin.isSilenced() || channel.isSilenced() || channel.getMuteList().contains(player.getName())) {
			if (!plugin.hasVoice(player))
				return;
		}
		
		if (channel.getType().equals(Type.CUSTOM)) {
			CustomChannel customChannel = plugin.getCustomChannel(channel);
			customChannel.sendMessage(player, customChannel.format(player, msg));
			return;
		}

		String message = plugin.getFormat().format(player, channel.getName(), msg);
		
		if (channel.isGlobal())
			channel.sendGlobalMessage(message);
		else
			channel.sendMessage(message);
		
		for (Support support : plugin.getSupports()) {
			support.chatMade(player.getName(), msg);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Channel channel = plugin.getSpawnChannel(event.getPlayer());
		channel.join(event.getPlayer());
		
		if (plugin.isSilenced())
			plugin.sendWarning(event.getPlayer(), "All channels are silenced");
		else if (plugin.getChannel(channel.getName()).isSilenced())
			plugin.sendWarning(event.getPlayer(), channel.getName() + " is silenced");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Channel channel = plugin.getChannel(event.getPlayer());
		channel.leave(event.getPlayer());
	}
}