package com.titankingdoms.nodinchan.titanchat.command.commands;

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
	
	@CommandID(name = "Ban", triggers = { "ban" })
	@CommandInfo(description = "Bans the player from the channel", usage = "ban [player] <channel>")
	public void ban(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Ban"); return; }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						
						channel.getAdminList().remove(targetPlayer.getName());
						channel.getWhiteList().remove(targetPlayer.getName());
						channel.getBlackList().add(targetPlayer.getName());
						channel.save();
						
						plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(player));
						plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
						
						for (String participant : cm.getChannel(player).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been banned from the channel");
						}
						
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
			if (cm.getChannel(player).canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					Channel channel = cm.getChannel(player);
					
					channel.getAdminList().remove(targetPlayer.getName());
					channel.getWhiteList().remove(targetPlayer.getName());
					channel.getBlackList().add(targetPlayer.getName());
					channel.save();
					
					plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(player));
					plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
					
					for (String participant : cm.getChannel(player).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been banned from the channel");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
	
	@CommandID(name = "Force", triggers = { "force" })
	@CommandInfo(description = "Forces the player to join the channel", usage = "force [player] <channel>")
	public void force(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Force"); }
		
		try {
			if (cm.exists(args[1])) {
				if (plugin.has(player, "TitanChat.force")) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						plugin.channelSwitch(targetPlayer, cm.getChannel(targetPlayer), cm.getChannel(args[1]));
						plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
						plugin.sendInfo(targetPlayer, "You have been forced to join " + cm.getExact(args[1]));
						
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
			if (plugin.has(player, "TitanChat.force")) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					plugin.channelSwitch(targetPlayer, cm.getChannel(targetPlayer), cm.getChannel(player));
					plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
					plugin.sendInfo(targetPlayer, "You have been forced to join " + cm.getChannel(player).getName());
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
	
	@CommandID(name = "Kick", triggers = { "kick" })
	@CommandInfo(description = "Kicks the player from the channel", usage = "kick [player] <channel>")
	public void kick(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Kick"); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canKick(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						
						plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(targetPlayer));
						plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
						
						for (String participant : channel.getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been kicked from the channel");
						}
						
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
			if (cm.getChannel(player).canKick(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					Channel channel = cm.getChannel(player);
					
					plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(targetPlayer));
					plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
					
					for (String participant : channel.getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been kicked from the channel");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
	
	@CommandID(name = "Mute", triggers = { "mute" })
	@CommandInfo(description = "Mutes the player on the channel", usage = "mute [player] <channel>")
	public void mute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Mute"); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canMute(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						channel.getMuteList().add(targetPlayer.getName());
						plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
						
						for (String participant : cm.getChannel(args[1]).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been muted");
						}
						
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
					channel.getMuteList().add(targetPlayer.getName());
					plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
					
					for (String participant : cm.getChannel(player).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been muted");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
	
	@CommandID(name = "Unban", triggers = { "unban" })
	@CommandInfo(description = "Unbans the player from the channel", usage = "unban [player] <channel>")
	public void unban(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Unban"); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (cm.getChannel(args[1]).getBlackList().contains(targetPlayer.getName())) {
							Channel channel = cm.getChannel(args[1]);
							channel.getBlackList().remove(targetPlayer.getName());
							
							plugin.whitelistMember(targetPlayer, channel);
							plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
							plugin.sendInfo(player, "You have unbanned " + targetPlayer.getDisplayName());
							
						} else {
							plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not banned");
						}
						
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
			if (cm.getChannel(player).canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (cm.getChannel(player).getBlackList().contains(targetPlayer.getName())) {
						Channel channel = cm.getChannel(player);
						channel.getBlackList().remove(targetPlayer.getName());
						
						plugin.whitelistMember(targetPlayer, channel);
						plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
						plugin.sendInfo(player, "You have unbanned " + targetPlayer.getDisplayName());
						
					} else {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not banned");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
	
	@CommandID(name = "Unmute", triggers = { "unmute" })
	@CommandInfo(description = "Unmutes the player on the channel", usage = "unmute [player] <channel>")
	public void unmute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Unmute"); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canMute(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (cm.getChannel(args[1]).getMuteList().contains(targetPlayer.getName())) {
							Channel channel = cm.getChannel(args[1]);
							channel.getMuteList().remove(targetPlayer.getName());
							plugin.sendInfo(targetPlayer, "You have been unmuted on " + channel.getName());
							
							for (String participant : cm.getChannel(args[1]).getParticipants()) {
								if (plugin.getPlayer(participant) != null)
									plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been unmuted");
							}
						}
						
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
					
					if (cm.getChannel(player).getMuteList().contains(targetPlayer.getName())) {
						Channel channel = cm.getChannel(player);
						channel.getMuteList().remove(targetPlayer.getName());
						plugin.sendInfo(targetPlayer, "You have been unmuted on " + channel.getName());
						
						for (String participant : cm.getChannel(player).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been unmuted");
						}
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
}