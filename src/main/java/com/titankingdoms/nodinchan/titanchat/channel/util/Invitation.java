package com.titankingdoms.nodinchan.titanchat.channel.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

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

public final class Invitation {
	
	private final TitanChat plugin;
	
	private final String invitee;
	
	private final Map<String, List<String>> invitors;
	
	public Invitation(Player invitee) {
		this.plugin = TitanChat.getInstance();
		this.invitee = invitee.getName();
		this.invitors = new HashMap<String, List<String>>();
	}
	
	public void invite(Channel channel, CommandSender invitor) {
		if (!invitors.containsKey(channel.getName().toLowerCase()))
			invitors.put(channel.getName().toLowerCase(), new ArrayList<String>());
		
		if (invitor instanceof Player)
			invitors.get(channel.getName().toLowerCase()).add(invitor.getName());
		else
			invitors.get(channel.getName().toLowerCase()).add("*CONSOLE*");
	}
	
	public void response(Channel channel, Response response) {
		if (!invitors.containsKey(channel.getName().toLowerCase())) {
			plugin.send(MessageLevel.WARNING, plugin.getPlayer(invitee), "You did not receive any invitations from this channel");
			return;
		}
		
		switch (response) {
		
		case ACCEPT:
			for (String invitor : invitors.get(channel.getName().toLowerCase())) {
				if (plugin.getPlayer(invitor) != null)
					plugin.send(MessageLevel.INFO, plugin.getPlayer(invitor), plugin.getPlayer(invitee).getDisplayName() + " has accepted your invitation");
				else if (invitor.equals("*CONSOLE*"))
					plugin.send(MessageLevel.INFO, plugin.getServer().getConsoleSender(), plugin.getPlayer(invitee).getDisplayName() + " has accepted your invitation");
			}
					
			break;
			
		case DECLINE:
			for (String invitor : invitors.get(channel.getName().toLowerCase())) {
				if (plugin.getPlayer(invitor) != null)
					plugin.send(MessageLevel.INFO, plugin.getPlayer(invitor), plugin.getPlayer(invitee).getDisplayName() + " has declined your invitation");
				else if (invitor.equals("*CONSOLE*"))
					plugin.send(MessageLevel.INFO, plugin.getServer().getConsoleSender(), plugin.getPlayer(invitee).getDisplayName() + " has declined your invitation");
			}
			break;
		}
		
		invitors.remove(channel.getName().toLowerCase());
	}
	
	public enum Response { ACCEPT, DECLINE }
}