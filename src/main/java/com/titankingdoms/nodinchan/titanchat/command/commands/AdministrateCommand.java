package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.command.info.*;

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
 * AdministrateCommand - Commands for Channel administration
 * 
 * @author NodinChan
 *
 */
public class AdministrateCommand extends CommandBase {

	private ChannelManager cm;
	
	public AdministrateCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Ban Command - Bans the player from the channel
	 */
	@ChCommand
	@Aliases("b")
	@Description("Bans the player from the channel")
	@Usage("ban [player] <channel>")
	public void ban(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "ban"); return; }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (!channel.getBlackList().contains(player.getName())) {
							ban(player, targetPlayer, channel, true);
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been banned"); }
						
					} else {
						OfflinePlayer targetPlayer = plugin.getPlayer(args[0]);
						plugin.sendInfo(player, targetPlayer.getName() + " is offline");
						
						if (!channel.getBlackList().contains(player.getName())) {
							ban(player, targetPlayer, channel, false);
							
						} else { plugin.sendWarning(player, targetPlayer.getName() + " has already been banned"); }
					}
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else {
				if (args[1].toLowerCase().startsWith(":tag")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (channel.canBan(player)) {
							if (plugin.getPlayer(args[0]) != null) {
								Player targetPlayer = plugin.getPlayer(args[0]);
								
								if (!channel.getBlackList().contains(player.getName())) {
									ban(player, targetPlayer, channel, true);
									
								} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been banned"); }
								
							} else {
								OfflinePlayer targetPlayer = plugin.getPlayer(args[0]);
								plugin.sendInfo(player, targetPlayer.getName() + " is offline");
								
								if (!channel.getBlackList().contains(player.getName())) {
									ban(player, targetPlayer, channel, false);
									
								} else { plugin.sendWarning(player, targetPlayer.getName() + " has already been banned"); }
							}
							
						} else { plugin.sendWarning(player, "You do not have permission"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			if (channel.canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (!channel.getBlackList().contains(targetPlayer.getName())) {
						channel.getAdminList().remove(targetPlayer.getName());
						channel.getWhiteList().remove(targetPlayer.getName());
						channel.getBlackList().add(targetPlayer.getName());
						channel.save();
						
						if (channel.equals(cm.getChannel(targetPlayer)))
							cm.chSwitch(targetPlayer, cm.getSpawnChannel(targetPlayer));
						
						plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been banned");
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been banned"); }
					
				} else {
					OfflinePlayer targetPlayer = plugin.getPlayer(args[0]);
					plugin.sendInfo(player, targetPlayer.getName() + " is offline");
					
					if (!channel.getBlackList().contains(player.getName())) {
						channel.getAdminList().remove(targetPlayer.getName());
						channel.getWhiteList().remove(targetPlayer.getName());
						channel.getBlackList().add(targetPlayer.getName());
						channel.save();
						
						plugin.sendInfo(channel.getParticipants(), targetPlayer.getName() + " has been banned");
						
					} else { plugin.sendWarning(player, targetPlayer.getName() + " has already been banned"); }
				}
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	private void ban(Player player, OfflinePlayer targetPlayer, Channel channel, boolean online) {
		channel.getAdminList().remove(targetPlayer.getName());
		channel.getWhiteList().remove(targetPlayer.getName());
		channel.getBlackList().add(targetPlayer.getName());
		channel.save();
		
		if (online) {
			if (channel.equals(cm.getChannel(targetPlayer.getPlayer())))
				cm.chSwitch(targetPlayer.getPlayer(), cm.getSpawnChannel(targetPlayer.getPlayer()));
			
			plugin.sendWarning(targetPlayer.getPlayer(), "You have been banned from " + channel.getName());
		}
		
		plugin.sendInfo(channel.getParticipants(), targetPlayer.getPlayer().getDisplayName() + " has been banned");
	}
	
	/**
	 * Force Command - Forces the player to join the channel
	 */
	@ChCommand
	@Description("Forces the player to join the channel")
	@Usage("force [player] <channel>")
	public void force(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "force"); }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (plugin.getPermsBridge().has(player, "TitanChat.force")) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (!channel.equals(cm.getChannel(targetPlayer))) {
							force(player, targetPlayer, channel);
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already in the channel"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else {
				if (args[1].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (plugin.getPermsBridge().has(player, "TitanChat.force")) {
							if (plugin.getPlayer(args[0]) != null) {
								Player targetPlayer = plugin.getPlayer(args[0]);
								
								if (!channel.equals(cm.getChannel(targetPlayer))) {
									force(player, targetPlayer, channel);
									
								} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already in the channel"); }
								
							} else { plugin.sendWarning(player, "Player not online"); }
							
						} else { plugin.sendWarning(player, "You do not have permission"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			if (plugin.getPermsBridge().has(player, "TitanChat.force")) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (!channel.equals(cm.getChannel(targetPlayer))) {
						force(player, targetPlayer, channel);
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already in the channel"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	private void force(Player player, Player targetPlayer, Channel channel) {
		cm.chSwitch(targetPlayer, channel);
		plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
		plugin.sendInfo(targetPlayer, "You have been forced to join " + channel.getName());
	}
	
	/**
	 * Kick Command - Kicks the player from the channel
	 */
	@ChCommand
	@Aliases("k")
	@Description("Kicks the player from the channel")
	@Usage("kick [player] <channel>")
	public void kick(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "kick"); }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.canKick(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (channel.getParticipants().contains(targetPlayer.getName())) {
							kick(player, targetPlayer, channel);
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not on the channel"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else {
				if (args[1].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (channel.canKick(player)) {
							if (plugin.getPlayer(args[0]) != null) {
								Player targetPlayer = plugin.getPlayer(args[0]);
								
								if (channel.getParticipants().contains(targetPlayer.getName())) {
									kick(player, targetPlayer, channel);
									
								} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not on the channel"); }
								
							} else { plugin.sendWarning(player, "Player not online"); }
							
						} else { plugin.sendWarning(player, "You do not have permission"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			if (channel.canKick(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (channel.getParticipants().contains(targetPlayer.getName())) {
						kick(player, targetPlayer, channel);
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not on the channel"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	private void kick(Player player, Player targetPlayer, Channel channel) {
		cm.chSwitch(targetPlayer, cm.getSpawnChannel(targetPlayer));
		plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
		plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been kicked");
	}
	
	/**
	 * Mute Command - Mutes the player on the channel
	 */
	@Command
	@Description("Mutes the player on the channel")
	@Usage("mute [player] <channel>")
	public void mute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Mute"); }
		
		if (!plugin.enableChannels()) {
			if (plugin.getPermsBridge().has(player, "TitanChat.mute")) {
				if (plugin.getPlayer(args[0]) != null) {
					cm.mute(plugin.getPlayer(args[0]), true);
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
							mute(player, targetPlayer, channel);
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been muted"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else {
				if (args[1].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (channel.canMute(player)) {
							if (plugin.getPlayer(args[0]) != null) {
								Player targetPlayer = plugin.getPlayer(args[0]);
								
								if (!channel.getMuteList().contains(targetPlayer.getName())) {
									mute(player, targetPlayer, channel);
									
								} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been muted"); }
								
							} else { plugin.sendWarning(player, "Player not online"); }
							
						} else { plugin.sendWarning(player, "You do not have permission"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			if (channel.canMute(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (!channel.getMuteList().contains(targetPlayer.getName())) {
						mute(player, targetPlayer, channel);
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has already been muted"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	private void mute(Player player, Player targetPlayer, Channel channel) {
		channel.getMuteList().add(targetPlayer.getName());
		plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
		plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been muted");
	}
	
	/**
	 * Unban Command - Unbans the player from the channel
	 */
	@ChCommand
	@Aliases("ub")
	@Description("Unbans the player from the channel")
	@Usage("unban [player] <channel>")
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
							
							cm.whitelistMember(targetPlayer, channel);
							plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + "has been unbanned");
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been banned"); }
						
					} else {
						OfflinePlayer targetPlayer = plugin.getOfflinePlayer(args[0]);
						plugin.sendInfo(player, targetPlayer.getName() + " is offline");
						
						if (channel.getBlackList().contains(targetPlayer.getName())) {
							channel.getBlackList().remove(targetPlayer.getName());
							
							cm.whitelistMember(targetPlayer, channel);
							plugin.sendInfo(channel.getParticipants(), targetPlayer.getName() + "has been unbanned");
							
						} else { plugin.sendWarning(player, targetPlayer.getName() + " has not been banned"); }
					}
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else {
				if (args[1].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (channel.canBan(player)) {
							if (plugin.getPlayer(args[0]) != null) {
								Player targetPlayer = plugin.getPlayer(args[0]);
								
								if (channel.getBlackList().contains(targetPlayer.getName())) {
									unban(player, targetPlayer, channel, true);
									
								} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been banned"); }
								
							} else {
								OfflinePlayer targetPlayer = plugin.getOfflinePlayer(args[0]);
								plugin.sendInfo(player, targetPlayer.getName() + " is offline");
								
								if (channel.getBlackList().contains(targetPlayer.getName())) {
									unban(player, targetPlayer, channel, false);
									
								} else { plugin.sendWarning(player, targetPlayer.getName() + " has not been banned"); }
							}
							
						} else { plugin.sendWarning(player, "You do not have permission"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			if (channel.canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (channel.getBlackList().contains(targetPlayer.getName())) {
						unban(player, targetPlayer, channel, true);
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been banned"); }
					
				} else {
					OfflinePlayer targetPlayer = plugin.getOfflinePlayer(args[0]);
					plugin.sendInfo(player, targetPlayer.getName() + " is offline");
					
					if (channel.getBlackList().contains(targetPlayer.getName())) {
						unban(player, targetPlayer, channel, false);
						
					} else { plugin.sendWarning(player, targetPlayer.getName() + " has not been banned"); }
				}
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	private void unban(Player player, OfflinePlayer targetPlayer, Channel channel, boolean online) {
		channel.getBlackList().remove(targetPlayer.getName());
		cm.whitelistMember(targetPlayer, channel);
		
		if (online)
			plugin.sendInfo(targetPlayer.getPlayer(), "You have been unbanned from " + channel.getName());
		
		plugin.sendInfo(channel.getParticipants(), targetPlayer.getName() + "has been unbanned");
	}
	
	/**
	 * Unmute Command - Unmutes the player on the channel
	 */
	@Command
	@Description("Unmutes the player on the channel")
	@Usage("unmute [player] <channel>")
	public void unmute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Unmute"); }
		
		if (!plugin.enableChannels()) {
			if (plugin.getPermsBridge().has(player, "TitanChat.mute")) {
				if (plugin.getPlayer(args[0]) != null) {
					cm.mute(plugin.getPlayer(args[0]), false);
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
							unmute(player, targetPlayer, channel);
							
						} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been muted"); }
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else {
				if (args[1].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (channel.canMute(player)) {
							if (plugin.getPlayer(args[0]) != null) {
								Player targetPlayer = plugin.getPlayer(args[0]);
								
								if (channel.getMuteList().contains(targetPlayer.getName())) {
									unmute(player, targetPlayer, channel);
									
								} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been muted"); }
								
							} else { plugin.sendWarning(player, "Player not online"); }
							
						} else { plugin.sendWarning(player, "You do not have permission"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			if (channel.canMute(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (channel.getMuteList().contains(targetPlayer.getName())) {
						unmute(player, targetPlayer, channel);
						
					} else { plugin.sendWarning(player, targetPlayer.getDisplayName() + " has not been muted"); }
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission"); }
		}
	}
	
	private void unmute(Player player, Player targetPlayer, Channel channel) {
		channel.getMuteList().remove(targetPlayer.getName());
		plugin.sendInfo(targetPlayer, "You have been unmuted on " + channel.getName());
		plugin.sendInfo(channel.getParticipants(), targetPlayer.getDisplayName() + " has been unmuted");
	}
}