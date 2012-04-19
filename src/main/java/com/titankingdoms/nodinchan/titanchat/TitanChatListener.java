package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.nodinchan.ncloader.LoadEvent;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.events.MessageFormatEvent;
import com.titankingdoms.nodinchan.titanchat.events.MessageSendEvent;

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
 * TitanChatListener - Listeners
 * 
 * @author NodinChan
 *
 */
public final class TitanChatListener implements Listener {

	private TitanChat plugin;
	
	public TitanChatListener(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLoad(LoadEvent event) {
		if (event.getLoadable() instanceof Addon && event.getPlugin() instanceof TitanChat)
			plugin.getAddonManager().setJarFile((Addon) event.getLoadable(), event.getJarFile());
		
		if (event.getLoadable() instanceof CustomChannel && event.getPlugin() instanceof TitanChat)
			plugin.getChannelManager().setJarFile((CustomChannel) event.getLoadable(), event.getJarFile());
	}
	
	/**
	 * Listens to the PlayerChatEvent
	 * 
	 * @param event PlayerChatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		if (plugin.enableChannels()) {
			event.setCancelled(true);
			
			if (msg.startsWith(plugin.getConfig().getString("channels.quick-message")) && !msg.substring(1).startsWith(" ")) {
				Channel channel = plugin.getChannelManager().getChannel(msg.split(" ")[0].substring(1));
					
				if (channel != null) {
					if (!channel.canAccess(player))
						return;
					
					if (!plugin.hasVoice(player)) {
						if (plugin.isSilenced()) { plugin.sendWarning(player, "The server is silenced"); return; }
						if (channel.isSilenced()) { plugin.sendWarning(player, "The channel is silenced"); return; }
						if (channel.getMuteList().contains(player.getName())) { plugin.sendWarning(player, "You have been muted"); return; }
						if (plugin.muted(player)) { plugin.sendWarning(player, "You have been muted"); return; }
					}
					
					String message = msg.replace(msg.split(" ")[0] + " ", "");
					
					if (channel instanceof CustomChannel) {
						MessageFormatEvent formatEvent = new MessageFormatEvent(player, ((CustomChannel) channel).getFormat());
						plugin.getServer().getPluginManager().callEvent(formatEvent);
						
						((CustomChannel) channel).sendMessage(player, ((CustomChannel) channel).format(player, formatEvent.getFormat(), message));
						return;
					}
					
					message = plugin.getFormatHandler().format(player, channel.getName(), message);
					channel.sendMessage(player, message);
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
				return;
			}
			
			Channel channel = plugin.getChannelManager().getChannel(player);
			
			if (!plugin.hasVoice(player)) {
				if (plugin.isSilenced()) { plugin.sendWarning(player, "The server is silenced"); return; }
				if (channel.isSilenced()) { plugin.sendWarning(player, "The channel is silenced"); return; }
				if (channel.getMuteList().contains(player.getName())) { plugin.sendWarning(player, "You have been muted"); return; }
				if (plugin.muted(player)) { plugin.sendWarning(player, "You have been muted"); return; }
			}
			
			if (channel instanceof CustomChannel) {
				MessageFormatEvent formatEvent = new MessageFormatEvent(player, ((CustomChannel) channel).getFormat());
				plugin.getServer().getPluginManager().callEvent(formatEvent);
				
				((CustomChannel) channel).sendMessage(player, ((CustomChannel) channel).format(player, formatEvent.getFormat(), msg));
				return;
			}
			
			String message = plugin.getFormatHandler().format(player, channel.getName(), msg);
			channel.sendMessage(player, message);
			
		} else {
			event.setFormat(plugin.getFormatHandler().format(player));
			
			MessageSendEvent sendEvent = new MessageSendEvent(player, plugin.getServer().getOnlinePlayers(), msg);
			plugin.getServer().getPluginManager().callEvent(sendEvent);
			
			if (sendEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			}
			
			msg = sendEvent.getMessage();
			
			event.setMessage(plugin.getFormatHandler().colourise(msg));
		}
	}
	
	/**
	 * Listens to the PlayerJoinEvent
	 * 
	 * @param event PlayerJoinEvent
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!plugin.enableChannels())
			return;
		
		if (plugin.getChannelManager().getChannel(event.getPlayer()) != null)
			return;
		
		Channel channel = plugin.getChannelManager().getSpawnChannel(event.getPlayer());
		channel.join(event.getPlayer());
		
		if (plugin.isSilenced())
			plugin.sendWarning(event.getPlayer(), "All channels are silenced");
		else if (channel.isSilenced())
			plugin.sendWarning(event.getPlayer(), channel.getName() + " is silenced");
	}
}