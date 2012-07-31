package com.titankingdoms.nodinchan.titanchat.channel.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public final class Participant {
	
	private final TitanChat plugin;
	
	private final String name;
	
	private Channel currentChannel;
	
	private final Map<String, Channel> channels;
	
	private final Invitation invitation;
	
	private final Map<String, Boolean> muted;
	
	public Participant(Player player) {
		this.plugin = TitanChat.getInstance();
		this.name = player.getName();
		this.channels = new HashMap<String, Channel>();
		this.invitation = new Invitation(player);
		this.muted = new HashMap<String, Boolean>();
	}
	
	public List<Channel> getChannels() {
		return new ArrayList<Channel>(channels.values());
	}
	
	public Channel getCurrentChannel() {
		return currentChannel;
	}
	
	public Invitation getInvitation() {
		return invitation;
	}
	
	public String getName() {
		return name;
	}
	
	public Player getPlayer() {
		return plugin.getPlayer(name);
	}
	
	public boolean isMuted(Channel channel) {
		if (!muted.containsKey(channel.getName().toLowerCase()))
			return false;
		
		return muted.get(channel.getName().toLowerCase());
	}
	
	public void join(Channel channel) {
		if (channel == null)
			return;
		
		if (!currentChannel.equals(channel))
			currentChannel = channel;
		
		if (!channels.containsKey(channel.getName()))
			channels.put(channel.getName(), channel);
	}
	
	public void leave(Channel channel) {
		if (channel == null)
			return;
		
		if (channels.containsKey(channel.getName()))
			channels.remove(channel.getName());
		
		if (currentChannel.equals(channel)) {
			if (channels.isEmpty())
				currentChannel = null;
			else
				currentChannel = getChannels().get(0);
		}
	}
	
	public void mute(Channel channel, boolean mute) {
		muted.put(channel.getName().toLowerCase(), mute);
	}
}