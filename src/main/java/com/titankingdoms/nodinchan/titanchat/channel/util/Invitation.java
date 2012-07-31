package com.titankingdoms.nodinchan.titanchat.channel.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public final class Invitation {
	
	private final TitanChat plugin;
	
	private final String invitee;
	
	private final Map<String, List<String>> invitors;
	
	public Invitation(Player invitee) {
		this.plugin = TitanChat.getInstance();
		this.invitee = invitee.getName();
		this.invitors = new HashMap<String, List<String>>();
	}
	
	public void invite(Channel channel, Player invitor) {
		if (!invitors.containsKey(channel.getName().toLowerCase()))
			invitors.put(channel.getName().toLowerCase(), new ArrayList<String>());
		
		invitors.get(channel.getName().toLowerCase()).add(invitor.getName());
	}
	
	public void response(Channel channel, Response response) {
		if (!invitors.containsKey(channel.getName().toLowerCase())) {
			plugin.send(MessageLevel.WARNING, plugin.getPlayer(invitee), "You did not receive any invitations from this channel");
			return;
		}
		
		switch (response) {
		
		case ACCEPT:
			for (String invitor : invitors.get(channel.getName().toLowerCase()))
				if (plugin.getPlayer(invitor) != null)
					plugin.send(MessageLevel.INFO, plugin.getPlayer(invitor), plugin.getPlayer(invitee).getDisplayName() + " has accepted your invitation");
			break;
			
		case DECLINE:
			for (String invitor : invitors.get(channel.getName().toLowerCase()))
				if (plugin.getPlayer(invitor) != null)
					plugin.send(MessageLevel.INFO, plugin.getPlayer(invitor), plugin.getPlayer(invitee).getDisplayName() + " has declined your invitation");
			break;
		}
		
		invitors.remove(channel.getName().toLowerCase());
	}
	
	public enum Response { ACCEPT, DECLINE }
}