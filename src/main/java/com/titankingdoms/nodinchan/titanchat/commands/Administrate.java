package com.titankingdoms.nodinchan.titanchat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
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
					
					for (String newMember : target.split(",")) {
						if (plugin.getPlayer(target) != null) {
							members.add(plugin.getPlayer(newMember).getName());
						}
					}
					
					if (!members.isEmpty()) {
						for (String newMember : members) {
							plugin.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channelName);
							cfgManager.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channelName);
						}
						
						plugin.sendInfo(player, plugin.createList(members) + " have been added to the Member List");
						
					} else {
						plugin.sendWarning(player, "Players not online");
					}
					
				} else {
					if (plugin.getPlayer(target) != null) {
						plugin.whitelistMember(plugin.getPlayer(target), channelName);
						cfgManager.whitelistMember(plugin.getPlayer(target), channelName);
						plugin.sendInfo(player, plugin.getPlayer(target).getName() + " has been added to the Member List");
						
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
					plugin.ban(plugin.getPlayer(target), channelName);
					cfgManager.ban(plugin.getPlayer(target), channelName);
					
					for (String participant : plugin.getChannel(player).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(plugin.getPlayer(target).getName() + " has been banned from the channel");
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
					if (plugin.getChannel(channelName).getAdminList().contains(plugin.getPlayer(target).getName())) {
						plugin.demote(plugin.getPlayer(target), channelName);
						cfgManager.demote(plugin.getPlayer(target), channelName);
						plugin.sendInfo(player, "You have demoted " + plugin.getPlayer(target).getName());
						
					} else {
						plugin.sendWarning(player, plugin.getPlayer(target).getName() + " is not an Admin");
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
					plugin.channelSwitch(plugin.getPlayer(target), plugin.getChannel(plugin.getPlayer(target)).getName(), channelName);
					plugin.sendInfo(player, "You have forced " + plugin.getPlayer(target).getName() + " to join the channel");
					plugin.sendInfo(plugin.getPlayer(target), "You have been forced to join " + channelName);
					
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
					plugin.leaveChannel(plugin.getPlayer(target), channelName);
					
					for (String participant : plugin.getChannel(channelName).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(plugin.getPlayer(target).getName() + " has been kicked from the channel");
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
					plugin.mute(plugin.getPlayer(target), channelName);
					
					for (String participant : plugin.getChannel(channelName).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(plugin.getPlayer(target).getName() + " has been muted");
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
					if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
						plugin.sendWarning(player, plugin.getPlayer(target).getName() + " is already an Admin");
						
					} else {
						plugin.promote(plugin.getPlayer(target), channelName);
						cfgManager.promote(plugin.getPlayer(target), channelName);
						plugin.sendInfo(player, "You have promoted " + plugin.getPlayer(target).getName());
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
					if (plugin.getChannel(channelName).getBlackList().contains(plugin.getPlayer(target).getName())) {
						plugin.unban(plugin.getPlayer(target), channelName);
						cfgManager.unban(plugin.getPlayer(target), channelName);
						
					} else {
						plugin.sendWarning(player, plugin.getPlayer(target).getName() + " is not banned");
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
					if (plugin.getChannel(channelName).getMuteList().contains(plugin.getPlayer(target).getName())) {
						plugin.unmute(plugin.getPlayer(target), channelName);
						for (String participant : plugin.getChannel(channelName).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(plugin.getPlayer(target).getName() + " has been unmuted");
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