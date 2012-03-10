package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;

public class ChannelManager {
	
	private TitanChat plugin;
	
	public ChannelManager(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void loadChannels() throws Exception {
		List<String> globalChannels = new ArrayList<String>();
		plugin.getCustomChannels().addAll(plugin.getSupportLoader().loadChannels());
		
		for (CustomChannel customChannel : plugin.getCustomChannels()) {
			Channel channel = new Channel(plugin, customChannel.getName(), Type.CUSTOM);
			channel = customChannel.load(channel);
			plugin.getChannels().add(channel);
		}
		
		for (String name : plugin.getConfig().getConfigurationSection("channels").getKeys(false)) {
			if (plugin.channelExist(name))
				continue;
			
			Channel channel = new Channel(plugin, name, Type.fromName(plugin.getConfig().getString("channels." + name + ".type")));
			
			if (plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".admins") != null)
				channel.getAdminList().addAll(plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".admins"));
			
			if (plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".whitelist") != null)
				channel.getWhiteList().addAll(plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".whitelist"));
			
			if (plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".blacklist") != null)
				channel.getBlackList().addAll(plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".blacklist"));
			
			if (plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".followers") != null)
				channel.getFollowers().addAll(plugin.getChannelConfig().getStringList("channels." + channel.getName() + ".followers"));
			
			if (channel.getType().equals(Type.PASSWORD))
				channel.setPassword(plugin.getConfig().getString("channels." + channel.getName() + ".password"));
			
			if (plugin.getConfig().get("channels." + channel.getName() + ".global") != null) {
				if (plugin.getConfig().getBoolean("channels." + name + ".global")) {
					channel.setGlobal(true);
					globalChannels.add(name);
				}
			}
			
			plugin.getChannels().add(channel);
		}
		
		plugin.log(Level.INFO, "No. of channels: " + plugin.getChannelAmount());
		plugin.log(Level.INFO, "No. of global broadcasting channels: " + globalChannels.size());
		plugin.log(Level.INFO, "No. of custom channels: " + plugin.getCustomChannels().size());
		plugin.log(Level.INFO, "TitanChat Channels Loaded");
	}
}