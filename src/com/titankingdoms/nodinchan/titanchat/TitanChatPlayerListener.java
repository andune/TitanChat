package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
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
		
		if (plugin.isSilenced() && !plugin.hasVoice(player)) {
			event.setCancelled(true);
			return;
		}
		
		if (plugin.inLocal(player)) {
			ChatColor colour = ChatColor.valueOf(plugin.getConfig().getString("local.colour"));
			String tag = plugin.getConfig().getString("local.tag");
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
				receiver.sendMessage(ch.format(player, colour, tag, msg, true));
			}
			
			if (receivers.size() == 1)
				plugin.sendInfo(player, "Nobody hears you...");
			
			Logger.getLogger("TitanLog").info("<" + player.getName() + "> " + ch.decolourize(msg));
			
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
			
			if (plugin.getFollowers(plugin.getChannel(player)) != null) {
				for (Player receiver : plugin.getFollowers(plugin.getChannel(player))) {
					if (plugin.has(player, "TitanChat.allowcolours")) {
						receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, true));
						
					} else {
						receiver.sendMessage(ch.format(player, ch.getChannelColour(player), ch.getChannelTag(player), msg, ch.allowColours(player)));
					}
				}
			}
		}
		
		Logger.getLogger("TitanLog").info("<" + player.getName() + "> " + ch.decolourize(msg));
		
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
		
		if (plugin.isSilenced())
			plugin.sendWarning(event.getPlayer(), "All channels have been silenced");
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (plugin.inLocal(event.getPlayer())) {
			plugin.leaveLocal(event.getPlayer());
			
		} else {
			String channelName = plugin.getChannel(event.getPlayer());
			plugin.leaveChannel(event.getPlayer(), channelName);
		}
	}
}
