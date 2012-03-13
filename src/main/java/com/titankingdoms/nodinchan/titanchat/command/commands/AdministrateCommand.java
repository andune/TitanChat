package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;

public class AdministrateCommand extends Command {
	
	private ChannelManager cm;
	
	public AdministrateCommand(TitanChat plugin) {
		super(plugin);
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "Ban", triggers = "ban")
	@CommandInfo(description = "Bans the player from the channel", usage = "ban [player] <channel>")
	public void ban(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Ban"); return; }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (!channel.getBlackList().contains(player.getName())) {
							channel.getAdminList().remove(targetPlayer.getName());
							channel.getWhiteList().remove(targetPlayer.getName());
							channel.getBlackList().add(targetPlayer.getName());
							channel.save();
							
							plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(player));
							plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been banned");
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been banned"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel.canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (!channel.getBlackList().contains(targetPlayer.getName())) {
						channel.getAdminList().remove(targetPlayer.getName());
						channel.getWhiteList().remove(targetPlayer.getName());
						channel.getBlackList().add(targetPlayer.getName());
						channel.save();
						
						plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(player));
						plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been banned");
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been banned"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	@CommandID(name = "Force", triggers = "force")
	@CommandInfo(description = "Forces the player to join the channel", usage = "force [player] <channel>")
	public void force(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Force"); }
		
		try {
			if (cm.exists(args[1])) {
				if (plugin.has(player, "TitanChat.force")) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						
						if (!cm.getChannel(targetPlayer).equals(channel)) {
							plugin.channelSwitch(targetPlayer, cm.getChannel(targetPlayer), channel);
							plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
							plugin.sendInfo(targetPlayer, "You have been forced to join " + channel.getName());
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already in the channel"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			if (plugin.has(player, "TitanChat.force")) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					Channel channel = cm.getChannel(player);
					
					if (!cm.getChannel(targetPlayer).equals(channel)) {
						plugin.channelSwitch(targetPlayer, cm.getChannel(targetPlayer), channel);
						plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
						plugin.sendInfo(targetPlayer, "You have been forced to join " + channel.getName());
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already in the channel"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	@CommandID(name = "Kick", triggers = "kick")
	@CommandInfo(description = "Kicks the player from the channel", usage = "kick [player] <channel>")
	public void kick(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Kick"); }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canKick(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (channel.getParticipants().contains(targetPlayer.getName())) {
							plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(targetPlayer));
							plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been kicked");
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not on the channel"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel.canKick(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (channel.getParticipants().contains(targetPlayer.getName())) {
						plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(targetPlayer));
						plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been kicked");
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not on the channel"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	@CommandID(name = "Mute", triggers = "mute", requireChannel = false)
	@CommandInfo(description = "Mutes the player on the channel", usage = "mute [player] <channel>")
	public void mute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Mute"); }
		
		if (!plugin.enableChannels()) {
			if (plugin.has(player, "TitanChat.mute")) {
				if (plugin.getPlayer(args[0]) != null) {
					plugin.mute(plugin.getPlayer(args[0]), true);
					plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.GOLD + plugin.getPlayer(args[0]).getDisplayName() + " has been muted");
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
			
			return;
		}
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canMute(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (!channel.getMuteList().contains(targetPlayer.getName())) {
							channel.getMuteList().add(targetPlayer.getName());
							plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been muted");
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been muted"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel.canMute(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (!channel.getMuteList().contains(targetPlayer.getName())) {
						channel.getMuteList().add(targetPlayer.getName());
						plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been muted");
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been muted"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	@CommandID(name = "Unban", triggers = "unban")
	@CommandInfo(description = "Unbans the player from the channel", usage = "unban [player] <channel>")
	public void unban(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Unban"); }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (channel.getBlackList().contains(targetPlayer.getName())) {
							channel.getBlackList().remove(targetPlayer.getName());
							
							plugin.whitelistMember(targetPlayer, channel);
							plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + "has been unbanned");
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been banned"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel.canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (channel.getBlackList().contains(targetPlayer.getName())) {
						channel.getBlackList().remove(targetPlayer.getName());
						
						plugin.whitelistMember(targetPlayer, channel);
						plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + "has been unbanned");
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been banned"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	@CommandID(name = "Unmute", triggers = "unmute", requireChannel = false)
	@CommandInfo(description = "Unmutes the player on the channel", usage = "unmute [player] <channel>")
	public void unmute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Unmute"); }
		
		if (!plugin.enableChannels()) {
			if (plugin.has(player, "TitanChat.mute")) {
				if (plugin.getPlayer(args[0]) != null) {
					plugin.mute(plugin.getPlayer(args[0]), false);
					plugin.getServer().broadcastMessage("[TitanChat] " + ChatColor.GOLD + plugin.getPlayer(args[0]).getDisplayName() + " has been unmuted");
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
			
			return;
		}
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canMute(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (channel.getMuteList().contains(targetPlayer.getName())) {
							channel.getMuteList().remove(targetPlayer.getName());
							plugin.sendInfo(targetPlayer, "You have been unmuted on " + channel.getName());
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been unmuted");
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been muted"); }
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) {
			if (cm.getChannel(player).canMute(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					Channel channel = cm.getChannel(player);
					
					if (channel.getMuteList().contains(targetPlayer.getName())) {
						channel.getMuteList().remove(targetPlayer.getName());
						plugin.sendInfo(targetPlayer, "You have been unmuted on " + channel.getName());
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been unmuted");
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been muted"); }
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
}