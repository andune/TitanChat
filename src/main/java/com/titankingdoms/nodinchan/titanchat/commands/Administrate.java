package com.titankingdoms.nodinchan.titanchat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;

public class Administrate {
	
	private TitanChat plugin;
	private ConfigManager cfgManager;
	
	public Administrate(TitanChat plugin) {
		this.plugin = plugin;
		this.cfgManager = new ConfigManager(plugin);
	}
	
	public void add(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canRank(player)) {
				if (target.contains(",")) {
					List<String> members  = new ArrayList<String>();
					List<String> display = new ArrayList<String>();
					
					for (String newMember : target.split(",")) {
						if (plugin.getPlayer(target) != null) {
							members.add(plugin.getPlayer(newMember).getName());
							display.add(plugin.getPlayer(newMember).getDisplayName());
						}
					}
					
					if (!members.isEmpty()) {
						for (String newMember : members) {
							plugin.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channelName);
							cfgManager.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channelName);
						}
						
						plugin.sendInfo(player, plugin.createList(display) + " have been added to the Member List");
						
					} else {
						plugin.sendWarning(player, "Players not online");
					}
					
				} else {
					if (plugin.getPlayer(target) != null) {
						plugin.whitelistMember(plugin.getPlayer(target), channelName);
						cfgManager.whitelistMember(plugin.getPlayer(target), channelName);
						plugin.sendInfo(player, plugin.getPlayer(target).getDisplayName() + " has been added to the Member List");
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void ban(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canBan(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					Channel channel = plugin.getChannel(channelName);
					
					channel.getAdminList().remove(targetPlayer.getName());
					channel.getWhiteList().remove(targetPlayer.getName());
					channel.getBlackList().add(targetPlayer.getName());
					
					plugin.channelSwitch(targetPlayer, channel.getName(), plugin.getSpawnChannel(player).getName());
					plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
					
					cfgManager.ban(targetPlayer, channelName);
					
					for (String participant : plugin.getChannel(player).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been banned from the channel");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void demote(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canRank(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					
					if (plugin.getChannel(channelName).getAdminList().contains(targetPlayer.getName())) {
						Channel channel = plugin.getChannel(channelName);
						channel.getAdminList().remove(targetPlayer.getName());
						plugin.sendInfo(targetPlayer, "You have been demoted in " + channel.getName());
						
						cfgManager.demote(targetPlayer, channelName);
						plugin.sendInfo(player, "You have demoted " + targetPlayer.getDisplayName());
						
					} else {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not an Admin");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void force(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.has(player, "TitanChat.force")) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					plugin.channelSwitch(targetPlayer, plugin.getChannel(targetPlayer).getName(), channelName);
					plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
					plugin.sendInfo(targetPlayer, "You have been forced to join " + plugin.getExactName(channelName));
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void kick(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canKick(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					Channel channel = plugin.getChannel(channelName);
					
					plugin.channelSwitch(targetPlayer, channel.getName(), plugin.getSpawnChannel(targetPlayer).getName());
					plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
					
					for (String participant : channel.getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been kicked from the channel");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void mute(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canMute(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					Channel channel = plugin.getChannel(channelName);
					channel.getMuteList().add(targetPlayer.getName());
					plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
					
					for (String participant : plugin.getChannel(channelName).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been muted");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void promote(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canRank(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					
					if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already an Admin");
						
					} else {
						plugin.assignAdmin(targetPlayer, channelName);
						plugin.sendInfo(player, "You have been promoted in " + plugin.getExactName(channelName));
						
						cfgManager.promote(targetPlayer, channelName);
						plugin.sendInfo(player, "You have promoted " + targetPlayer.getDisplayName());
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void unban(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canBan(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					
					if (plugin.getChannel(channelName).getBlackList().contains(targetPlayer.getName())) {
						Channel channel = plugin.getChannel(channelName);
						channel.getBlackList().remove(targetPlayer.getName());
						
						plugin.whitelistMember(targetPlayer, channelName);
						plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
						
						cfgManager.unban(targetPlayer, channelName);
						plugin.sendInfo(player, "You have unbanned " + targetPlayer.getDisplayName());
						
					} else {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not banned");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void unmute(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).canMute(player)) {
				if (plugin.getPlayer(target) != null) {
					Player targetPlayer = plugin.getPlayer(target);
					
					if (plugin.getChannel(channelName).getMuteList().contains(targetPlayer.getName())) {
						Channel channel = plugin.getChannel(channelName);
						channel.getMuteList().remove(targetPlayer.getName());
						plugin.sendInfo(targetPlayer, "You have been unmuted on " + channel.getName());
						
						for (String participant : plugin.getChannel(channelName).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been unmuted");
						}
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to do this on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
}