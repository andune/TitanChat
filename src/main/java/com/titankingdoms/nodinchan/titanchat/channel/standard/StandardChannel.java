package com.titankingdoms.nodinchan.titanchat.channel.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.Participant;

public final class StandardChannel extends Channel {
	
	public StandardChannel() {}
	
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
		
		StandardCommandHandler.load(channel);
		StandardSetting.load(channel);
		
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
		
		StandardCommandHandler.load(channel);
		StandardSetting.load(channel);
		
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
	
	public String sendMessage(Player sender, String message) {
		List<Player> recipants = new ArrayList<Player>();
		
		if (!getInfo().global()) {
			for (Participant participant : getParticipants())
				if (participant.getPlayer() != null)
					recipants.add(participant.getPlayer());
			
		} else { recipants.addAll(Arrays.asList(plugin.getServer().getOnlinePlayers())); }
		
		return sendMessage(sender, recipants, message);
	}
}