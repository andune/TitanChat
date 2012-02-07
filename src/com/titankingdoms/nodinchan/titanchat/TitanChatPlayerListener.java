package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TitanChatPlayerListener implements Listener {
	
	private TitanChat plugin;
	private Format fmt;
	
	public TitanChatPlayerListener(TitanChat plugin) {
		this.plugin = plugin;
		this.fmt = new Format(plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		
		String msg = event.getMessage();
		
		if (plugin.isSilenced() && !plugin.hasVoice(player)) {
			event.setCancelled(true);
			return;
		}
		
		if (plugin.inLocal(player)) {
			int radius = plugin.getConfig().getInt("local.radius");
			
			List<Player> receivers = new ArrayList<Player>();
			
			if (!player.getNearbyEntities(radius, radius, radius).isEmpty()) {
				for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
					if (entity instanceof Player) {
						receivers.add((Player) entity);
					}
				}
			}
			
			receivers.add(player);
			
			for (Player receiver : receivers) {
				receiver.sendMessage(fmt.local(player, fmt.filter(msg)));
			}
			
			if (receivers.size() == 1)
				plugin.sendInfo(player, "Nobody hears you...");
			
			Logger.getLogger("TitanLog").info("<" + player.getName() + "> " + fmt.decolourize(msg));
			
			event.setCancelled(true);
			return;
		}
		
		if (plugin.isSilenced(plugin.getChannel(player)) && !plugin.hasVoice(player)) {
			event.setCancelled(true);
			return;
		}
		
		if (plugin.isMuted(player, plugin.getChannel(player))) {
			event.setCancelled(true);
			return;
		}
		
		if (plugin.isGlobal(plugin.getChannel(player))) {
			String message = fmt.format(player, plugin.getChannel(player), msg);
			
			for (Player receiver : plugin.getServer().getOnlinePlayers()) {
				receiver.sendMessage(message);
			}
			
		} else {
			String message = fmt.format(player, plugin.getChannel(player), msg);
			
			for (Player receiver : plugin.getParticipants(plugin.getChannel(player))) {
				receiver.sendMessage(message);
			}
			
			if (plugin.getFollowers(plugin.getChannel(player)) != null) {
				for (Player receiver : plugin.getFollowers(plugin.getChannel(player))) {
					if (!plugin.getParticipants(plugin.getChannel(player)).contains(receiver)) {
						receiver.sendMessage(message);
					}
				}
			}
		}
		
		Logger.getLogger("TitanLog").info("<" + player.getName() + "> " + fmt.decolourize(msg));
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		String channelName = "";
		
		for (String channel : plugin.getChannels()) {
			if (plugin.has(event.getPlayer(), "TitanChat.spawn." + channel)) {
				channelName = channel;
				break;
			}
		}
		
		if (channelName != "") {
			if (plugin.has(event.getPlayer(), "TitanChat.admin")) {
				if (plugin.getStaffChannel() != null) {
					channelName = plugin.getStaffChannel();
					
				} else {
					channelName = plugin.getDefaultChannel();
				}
				
			} else {
				channelName = plugin.getDefaultChannel();
			}
		}
		
		plugin.enterChannel(event.getPlayer(), channelName);
		
		if (plugin.isSilenced())
			plugin.sendWarning(event.getPlayer(), "All channels have been silenced");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.inLocal(event.getPlayer())) {
			plugin.leaveLocal(event.getPlayer());
			
		} else {
			String channelName = plugin.getChannel(event.getPlayer());
			plugin.leaveChannel(event.getPlayer(), channelName);
		}
	}
}
