package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public final class ChannelManager {
	
	private final TitanChat plugin;
	
	private int channelAmount = 0;
	private int customChAmount = 0;
	
	private List<Channel> channels;
	
	private Map<Channel, Map<String, List<String>>> channelInvitors;
	
	public ChannelManager(TitanChat plugin) {
		this.plugin = plugin;
		this.channels = new ArrayList<Channel>();
		this.channelInvitors = new HashMap<Channel, Map<String, List<String>>>();
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
		
		List<String> pastParticipants = new ArrayList<String>();
		pastParticipants.addAll(channel.getParticipants());
		
		for (String participant : pastParticipants) {
			if (plugin.getPlayer(participant) != null)
				plugin.channelSwitch(plugin.getPlayer(participant), channel, getSpawnChannel(player));
		}
		
		plugin.sendWarning(pastParticipants, channel.getName() + " has been deleted");
		
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
			if (plugin.has(player, "TitanChat.admin") && !plugin.has(player, "TitanChat.forcejoin." + getStaffChannel().getName()))
				return getStaffChannel();
		}
		
		for (Channel channel : channels) {
			if (plugin.has(player, "TitanChat.spawn." + channel.getName()) && !plugin.has(player, "TitanChat.forcejoin." + channel.getName()) && channel.canAccess(player))
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
		
		for (String fileName : plugin.getChannelDir().list()) {
			if (exists(fileName.replace(".yml", "")) || fileName.equals("README.yml"))
				continue;
			
			Channel channel = loadChannel(fileName.replace(".yml", ""));
			
			if (channel == null)
				continue;
			
			loadChannelVariables(channel);
			
			channels.add(channel);
		}
		
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
	
	public void onInvite(Channel channel, Player invitor, Player invitee) {
		Map<String, List<String>> invitors = channelInvitors.get(channel);
		List<String> invitorlist = (invitors.get(invitee.getName()) != null) ? invitors.get(invitee.getName()) : new ArrayList<String>();
		invitorlist.add(invitor.getName());
		invitors.put(invitee.getName(), invitorlist);
		channelInvitors.put(channel, invitors);
	}
	
	public void onInviteRespond(Channel channel, Player invitee, boolean accept) {
		Map<String, List<String>> invitors = channelInvitors.get(channel);
		
		for (String invitor : invitors.get(invitee.getName())) {
			if (plugin.getPlayer(invitor) != null) {
				if (accept)
					plugin.sendInfo(plugin.getPlayer(invitor), invitee.getDisplayName() + " has accepted your invitation");
				else
					plugin.sendInfo(plugin.getPlayer(invitor), invitee.getDisplayName() + " has declined your invitation");
			}
		}
		
		invitors.remove(invitee.getName());
		channelInvitors.put(channel, invitors);
	}
	
	public void register(Channel channel) {
		channels.add(channel);
		plugin.log(Level.INFO, "A new channel, " + channel.getName() + ", has been registered");
	}
	
	public void unload() {
		for (Channel channel : channels) { channel.save(); }
		channels.clear();
	}
}