package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.support.Addon;

public class TitanChatListener implements Listener {
	
	private TitanChat plugin;
	
	public TitanChatListener(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		if (plugin.enableChannels()) {
			event.setCancelled(true);
			
			Channel channel = plugin.getChannelManager().getChannel(player);
			
			if (plugin.isSilenced() || channel.isSilenced() || channel.getMuteList().contains(player.getName()) || plugin.muted(player)) {
				if (!plugin.hasVoice(player))
					return;
			}
			
			if (channel instanceof CustomChannel) {
				((CustomChannel) channel).sendMessage(((CustomChannel) channel).format(player, msg));
				return;
			}

			String message = plugin.getFormat().format(player, channel.getName(), msg);
			channel.sendMessage(message);
			
		} else {
			event.setFormat(plugin.getFormat().format(player));
			event.setMessage(plugin.getFormat().colourise(msg));
		}
		
		for (Addon addon : plugin.getAddons()) {
			addon.chatMade(player.getName(), msg);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event) {
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