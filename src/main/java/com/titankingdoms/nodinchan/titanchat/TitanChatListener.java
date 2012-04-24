package com.titankingdoms.nodinchan.titanchat;

import java.util.logging.Level;

import org.bukkit.Bukkit;
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

/**
 * TitanChatListener - Event Listener of TitanChat
 * 
 * @author NodinChan
 *
 */
public class TitanChatListener implements Listener {

	private TitanChat plugin;
	
	/**
	 * Listens to events and act accordingly
	 * 
	 * @param plugin TitanChat
	 */
	public TitanChatListener(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Listens to the LoadEvent
	 * 
	 * @param event LoadEvent
	 */
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
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		if (plugin.enableChannels()) {
			event.setCancelled(true);
			
			String quickMessage = plugin.getConfig().getString("channels.quick-message");
			
			if (message.startsWith(quickMessage) && !message.substring(quickMessage.length()).startsWith(" ")) {
				Channel channel = plugin.getChannelManager().getChannel(message.split(" ")[0].substring(quickMessage.length()));
				
				if (channel != null) {
					if (!channel.canAccess(player))
						return;
					
					if (voiceless(player, channel))
						return;
					
					if (channel instanceof CustomChannel) {
						String format = ((CustomChannel) channel).format(player, ((CustomChannel) channel).getFormat());
						
						MessageFormatEvent formatEvent = new MessageFormatEvent(player, format);
						plugin.getServer().getPluginManager().callEvent(formatEvent);
						
						String log = channel.sendMessage(player, formatEvent.getFormat().replace("%message", message.substring(message.split(" ")[0].length())));
						
						if (!log.equals(""))
							Bukkit.getLogger().log(Level.INFO, log);
						
						return;
					}
					
					String log = channel.sendMessage(player, message.substring(message.split(" ")[0].length()));
					
					if (!log.equals(""))
						Bukkit.getLogger().log(Level.INFO, log);
					
				} else { plugin.sendWarning(player, "No such channel"); }
				
				return;
			}
			
			Channel channel = plugin.getChannelManager().getChannel(player);
			
			if (voiceless(player, channel))
				return;
			
			if (channel instanceof CustomChannel) {
				String format = ((CustomChannel) channel).format(player, ((CustomChannel) channel).getFormat());
				
				MessageFormatEvent formatEvent = new MessageFormatEvent(player, format);
				plugin.getServer().getPluginManager().callEvent(formatEvent);
				
				String log = channel.sendMessage(player, formatEvent.getFormat().replace("%message", message));
				
				if (!log.equals(""))
					Bukkit.getLogger().log(Level.INFO, log);
				
				return;
			}
			
			String log = channel.sendMessage(player, message);
			
			if (!log.equals(""))
				Bukkit.getLogger().log(Level.INFO, log);
			
		} else {
			event.setFormat(plugin.getFormatHandler().format(player, null, true));
			
			MessageSendEvent sendEvent = new MessageSendEvent(player, plugin.getServer().getOnlinePlayers(), message);
			plugin.getServer().getPluginManager().callEvent(sendEvent);
			
			if (sendEvent.isCancelled()) {
				event.setCancelled(true);
				return;
			}
			
			String colour = plugin.getConfig().getString("channels.chat-display-colour");
			
			event.setMessage(plugin.getFormatHandler().colourise(colour + sendEvent.getMessage()));
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
	
	private boolean voiceless(Player player, Channel channel) {
		if (plugin.getPermsBridge().has(player, "TitanChat.voice"))
			return false;
		
		if (plugin.isSilenced()) {
			plugin.sendWarning(player, "The server is silenced");
			return true;
		}
		
		if (channel.isSilenced()) {
			plugin.sendWarning(player, "The channel is silenced");
			return true;
		}
		
		if (channel.getMuteList().contains(player.getName())) {
			plugin.sendWarning(player, "You have been muted");
			return true;
		}
		
		if (plugin.getChannelManager().isMuted(player)) {
			plugin.sendWarning(player, "You have been muted");
			return true;
		}
		
		return false;
	}
}