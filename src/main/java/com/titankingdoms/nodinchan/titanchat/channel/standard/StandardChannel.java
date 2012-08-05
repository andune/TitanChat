package com.titankingdoms.nodinchan.titanchat.channel.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.Participant;

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
 * StandardChannel - Represents a standard channel
 * 
 * @author NodinChan
 *
 */
public final class StandardChannel extends Channel {
	
	/**
	 * Standard Type
	 */
	public StandardChannel() {}
	
	/**
	 * Standard Channel
	 * 
	 * @param name The channel name
	 * 
	 * @param option The channel option
	 */
	public StandardChannel(String name, Option option) {
		super(name, option);
	}
	
	@Override
	public boolean access(Player player) {
		if (getOption().equals(Option.STAFF) && !plugin.isStaff(player))
			return false;
		
		if (getBlacklist().contains(player.getName()))
			return false;
		
		if (getInfo().whitelistOnly() && !getWhitelist().contains(player.getName()))
			return false;
		
		return true;
	}
	
	@Override
	public Channel create(CommandSender sender, String name, Option option) {
		StandardChannel channel = new StandardChannel(name, option);
		
		if (sender instanceof Player)
			channel.getAdmins().add(sender.getName());
		
		return channel;
	}
	
	@Override
	public String getType() {
		return "Standard";
	}
	
	@Override
	public Channel load(String name, Option option) {
		StandardChannel channel = new StandardChannel(name, option);
		
		if (channel.getConfig().get("admins") != null)
			channel.getAdmins().addAll(channel.getConfig().getStringList("admins"));
		
		if (channel.getConfig().get("blacklist") != null)
			channel.getBlacklist().addAll(channel.getConfig().getStringList("blacklist"));
		
		if (channel.getConfig().get("followers") != null)
			channel.getFollowers().addAll(channel.getConfig().getStringList("followers"));
		
		if (channel.getConfig().get("whitelist") != null)
			channel.getWhitelist().addAll(channel.getConfig().getStringList("whitelist"));
		
		return channel;
	}
	
	@Override
	public String sendMessage(Player sender, String message) {
		List<Player> recipants = new ArrayList<Player>();
		
		switch (getInfo().range()) {
		
		case CHANNEL:
			for (Participant participant : getParticipants())
				if (participant.getPlayer() != null)
					recipants.add(participant.getPlayer());
			break;
			
		case GLOBAL:
			recipants.addAll(Arrays.asList(plugin.getServer().getOnlinePlayers()));
			break;
			
		case LOCAL:
			for (Entity entity : sender.getNearbyEntities(getInfo().radius(), getInfo().radius(), getInfo().radius()))
				if (entity instanceof Player)
					recipants.add((Player) entity);
			break;
			
		case WORLD:
			for (Player recipant : sender.getWorld().getPlayers())
				recipants.add(recipant);
			break;
		}
		
		return sendMessage(sender, recipants, message);
	}
}