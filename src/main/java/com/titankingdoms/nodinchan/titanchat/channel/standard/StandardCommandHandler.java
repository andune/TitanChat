package com.titankingdoms.nodinchan.titanchat.channel.standard;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Option;
import com.titankingdoms.nodinchan.titanchat.channel.util.CommandHandler;
import com.titankingdoms.nodinchan.titanchat.channel.util.Handler.HandlerInfo;

final class StandardCommandHandler {
	
	public static void load(StandardChannel channel) {
		if (channel == null)
			return;
		
		channel.registerCommandHandlers(new CommandHandler(channel, "Ban", new HandlerInfo(null, null, 0, 0)) {
			
			@Override
			public void onCommand(CommandSender sender, String[] args) {
				OfflinePlayer targetPlayer = plugin.getPlayer(args[0]);
				
				if (targetPlayer == null) {
					targetPlayer = plugin.getOfflinePlayer(args[0]);
					plugin.send(MessageLevel.WARNING, sender, targetPlayer.getName() + " is offline");
				}
				
				if (!channel.getOption().equals(Option.NONE)) {
					plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
					return;
					
				} else if (!hasPermission(sender, "TitanChat.ban.*") && !hasPermission(sender, "TitanChat.ban." + channel.getName())) {
					plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
					return;
					
				} else if (!channel.getAdmins().contains(sender.getName())) {
					plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
					return;
				}
				
				if (!channel.getBlacklist().contains(targetPlayer.getName())) {
					channel.getBlacklist().add(targetPlayer.getName());
					channel.getAdmins().remove(targetPlayer.getName());
					channel.save();
					
					channel.leave(targetPlayer.getName());
					
					if (targetPlayer.isOnline())
						plugin.send(MessageLevel.WARNING, targetPlayer.getPlayer(), "You have been banned from " + channel.getName());
					
					if (sender instanceof Player) {
						Channel current = plugin.getManager().getChannelManager().getChannel((Player) sender);
						
						if (current == null || !current.equals(current))
							plugin.send(MessageLevel.INFO, sender, plugin.getDisplayNameChanger().getDisplayName(targetPlayer) + " has been banned");
					}
					
					plugin.send(MessageLevel.INFO, channel, plugin.getDisplayNameChanger().getDisplayName(targetPlayer) + " has been banned");
					
				} else { plugin.send(MessageLevel.WARNING, sender, plugin.getDisplayNameChanger().getDisplayName(targetPlayer) + " has already been banned"); }
			}
			
		}, new CommandHandler(channel, "Join", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) {
				if (!(sender instanceof Player))
					return;
				
				if (channel.getOption().equals(Option.STAFF) && !plugin.isStaff((Player) sender)) {
					channel.deny((Player) sender, null);
					return;
				}
				
				if (!hasPermission(sender, "TitanChat.access.*") && !hasPermission(sender, "TitanChat.access." + channel.getName())) {
					if (channel.getPassword() != null && !channel.getPassword().equals("")) {
						try {
							String password = args[1];
							
							if (!channel.getPassword().equals(password)) {
								channel.deny((Player) sender, "Incorrect password");
								return;
							}
							
						} catch (IndexOutOfBoundsException e) {
							channel.deny((Player) sender, "Please enter a password");
							return;
						}
					}
					
					if (channel.getBlacklist().contains(sender.getName())) {
						channel.deny((Player) sender, "You are banned");
						return;
					}
					
					if (channel.getInfo().whitelistOnly() && !channel.getWhitelist().contains(sender.getName())) {
						channel.deny((Player) sender, "You are not whitelisted");
						return;
					}
				}
				
				channel.join((Player) sender);
				plugin.send(MessageLevel.INFO, sender, "You have joined " + channel.getName());
			}
			
		}, new CommandHandler(channel, "Kick", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) {
				Player targetPlayer = plugin.getPlayer(args[0]);
				
				if (targetPlayer == null) {
					plugin.send(MessageLevel.WARNING, sender, "Player not online");
					return;
				}
				
				if (!hasPermission(sender, "TitanChat.kick.*") && !hasPermission(sender, "TitanChat.kick." + channel.getName())) {
					plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
					return;
					
				} else if (!channel.getAdmins().contains(sender.getName())) {
					plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
					return;
				}
				
				if (channel.isParticipating(targetPlayer.getName())) {
					channel.leave(targetPlayer);
					
					plugin.send(MessageLevel.WARNING, targetPlayer, "You have been kicked from " + channel.getName());
					plugin.send(MessageLevel.INFO, channel, targetPlayer.getDisplayName() + " has been kicked");
					
				} else { plugin.send(MessageLevel.WARNING, sender, targetPlayer.getDisplayName() + " is not on the channel"); }
			}
			
		}, new CommandHandler(channel, "Leave", new HandlerInfo(null, null, 0, 0)) {

			@Override
			public void onCommand(CommandSender sender, String[] args) {
				if (!(sender instanceof Player))
					return;
				
				if (channel.isParticipating(sender.getName())) {
					channel.leave((Player) sender);
					plugin.send(MessageLevel.INFO, sender, "You have left " + channel.getName());
					
				} else { plugin.send(MessageLevel.WARNING, sender, "You are not on any channels"); }
			}
		});
	}
}