package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.enums.Type;

public class ChannelManager {
	
	private TitanChat plugin;
	
	private int channelAmount = 0;
	private int customChAmount = 0;
	
	private List<Channel> channels;
	
	public ChannelManager(TitanChat plugin) {
		this.plugin = plugin;
		this.channels = new ArrayList<Channel>();
	}
	
	public void createChannel(Player player, String name) {
		Channel channel = new Channel(plugin, name, Type.PUBLIC);
		channels.add(channel);
		
		plugin.assignAdmin(player, channel);
		plugin.channelSwitch(player, getChannel(player), channel);
		
		channel.save();
		
		plugin.sendInfo(player, "You have created " + channel.getName());
	}
	
	public void deleteChannel(Player player, String name) {
		Channel channel = getChannel(name);
		channels.remove(channel);
		
		List<Player> playerlist = new ArrayList<Player>();
		
		for (String participant : channel.getParticipants()) {
			if (plugin.getPlayer(participant) != null)
				playerlist.add(plugin.getPlayer(participant));
		}
		
		for (Player kick : playerlist) {
			plugin.channelSwitch(kick, channel, getSpawnChannel(player));
			plugin.sendWarning(kick, channel.getName() + " has been deleted");
		}
		
		File file = new File(plugin.getChannelDir(), name + ".yml");
		file.delete();
		
		plugin.sendInfo(player, "You have deleted " + channel.getName());
	}
	
	public Channel getDefaultChannel() {
		for (Channel channel : channels) {
			if (channel.getType().equals(Type.DEFAULT))
				return channel;
		}
		
		return null;
	}
	
	public String getExact(String name) {
		return getChannel(name).getName();
	}
	
	public Channel getSpawnChannel(Player player) {
		if (getStaffChannel() != null) {
			if (plugin.has(player, "TitanChat.admin") && !plugin.has(player, "TitanChat.force." + getStaffChannel().getName()))
				return getStaffChannel();
		}
		
		for (Channel channel : channels) {
			if (plugin.has(player, "TitanChat.spawn." + channel.getName()) && !plugin.has(player, "TitanChat.force." + channel.getName()) && channel.canAccess(player))
				return channel;
		}
		
		return getDefaultChannel();
	}
	
	public Channel getStaffChannel() {
		for (Channel channel : channels) {
			if (channel.getType().equals(Type.STAFF))
				return channel;
		}
		
		return null;
	}
	
	public boolean exists(String name) {
		return getChannel(name) != null;
	}
	
	public Channel getChannel(String name) {
		for (Channel channel : channels) {
			if (channel.getName().equalsIgnoreCase(name))
				return channel;
		}
		
		return null;
	}
	
	public Channel getChannel(Player player) {
		for (Channel channel : channels) {
			if (channel.getParticipants().contains(player.getName()))
				return channel;
		}
		
		return null;
	}
	
	public int getChannelAmount() {
		return channelAmount;
	}
	
	public List<Channel> getChannels() {
		return channels;
	}
	
	public Channel loadChannel(String name) throws Exception {
		Type type = Type.fromName(new Channel(plugin, name).getConfig().getString("type"));
		
		Channel channel = new Channel(plugin, name, type);
		
		channel.setGlobal(channel.getConfig().getBoolean("global"));
		
		channel.setPassword(channel.getConfig().getString("password"));
		
		if (channel.getConfig().getStringList("admins") != null)
			channel.getAdminList().addAll(channel.getConfig().getStringList("admins"));
		
		if (channel.getConfig().getStringList("blacklist") != null)
			channel.getBlackList().addAll(channel.getConfig().getStringList("blacklist"));
		
		if (channel.getConfig().getStringList("followers") != null)
			channel.getFollowerList().addAll(channel.getConfig().getStringList("followers"));
		
		if (channel.getConfig().getStringList("whitelist") != null)
			channel.getWhiteList().addAll(channel.getConfig().getStringList("whitelist"));
		
		loadChannelVariables(channel);
		
		return channel;
	}
	
	public void loadChannels() throws Exception {
		if (!plugin.enableChannels()) { plugin.log(Level.INFO, "Channels disabled"); return; }
		
		channels.addAll(plugin.getLoader().loadChannels());
		customChAmount = channels.size();
		
		List<Channel> channels = new ArrayList<Channel>();
		
		for (String fileName : plugin.getChannelDir().list()) {
			if (exists(fileName.replace(".yml", "")) || fileName.equals("README.yml"))
				continue;
			
			Channel channel = loadChannel(fileName.replace(".yml", ""));
			
			if (channel == null)
				continue;
			
			loadChannelVariables(channel);
			
			channels.add(channel);
		}
		
		channels.addAll(channels);
		channelAmount = channels.size() - customChAmount;
		
		plugin.log(Level.INFO, "No. of channels: " + channelAmount);
		plugin.log(Level.INFO, "No. of custom channels: " + customChAmount);
		plugin.log(Level.INFO, "Channels loaded");
	}
	
	public void loadChannelVariables(Channel channel) {
		ChannelVariables variables = channel.getVariables();
		variables.setChatColour(channel.getConfig().getString("chat-display-colour"));
		variables.setConvert(channel.getConfig().getBoolean("colour-code"));
		
		if (channel.getConfig().getString("format") != null)
			variables.setFormat(channel.getConfig().getString("format"));
		
		variables.setNameColour(channel.getConfig().getString("name-display-colour"));
		variables.setTag(channel.getConfig().getString("tag"));
	}
}