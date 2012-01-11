package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitanChatCommands {
	
	private TitanChat plugin;
	private ChannelManager chManager;
	
	public TitanChatCommands(TitanChat plugin) {
		this.plugin = plugin;
		this.chManager = new ChannelManager(plugin);
	}
	
	public void onCommand(Player player, String action, String arg) {
		String channel = plugin.getChannel(player);
		
		switch (Commands.valueOf(action.toUpperCase())) {
		
		// /titanchat accept [channel]
		// Accepts invitation to join the channel
		
		case ACCEPT:
			if (plugin.isInvited(player, arg)) {
				plugin.accept(player, arg);
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			break;
			
		// /titanchat add [player]
		// Adds a player to the whitelist
			
		case ADD:
			if (plugin.canWhitelist(player, channel)) {
				if (arg.contains(",")) {
					for (String newMember : arg.split(",")) {
						List<String> members = new ArrayList<String>();
						members.add(plugin.getPlayer(arg).getName());
						
						plugin.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channel);
						chManager.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")).getName(), channel);
						plugin.sendInfo(player, plugin.createList(members) + " haave been added to the Member List");
					}
					
				} else {
					plugin.whitelistMember(plugin.getPlayer(arg), channel);
					chManager.whitelistMember(plugin.getPlayer(arg).getName(), channel);
					plugin.sendInfo(player, plugin.getPlayer(arg).getName() + " has changed the settings");
				}
			}
			break;
			
		// /titanchat allowcolours [true/false]
		// Sets whether colour codes are allowed on the channel
			
		case ALLOWCOLORS:
		case ALLOWCOLOURS:
			if (plugin.has(player, "TitanChat.admin")) {
				if (arg.equalsIgnoreCase("true")) {
					chManager.setAllowColours(channel, true);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else if (arg.equalsIgnoreCase("false")) {
					chManager.setAllowColours(channel, false);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "True or False?");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the state of this channel");
			}
			break;
			
		// /titanchat ban [player]
		// Bans the player from the channel
			
		case BAN:
			if (plugin.canBan(player, channel)) {
				plugin.ban(plugin.getPlayer(arg), channel);
				chManager.ban(plugin.getPlayer(arg).getName(), channel);
				for (Player participant : plugin.getParticipants(channel)) {
					participant.sendMessage(plugin.getPlayer(arg).getName() + " has been banned from the channel");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to ban on this channel");
			}
			break;
			
		// /titanchat broadcast [message]
		// Broadcasts the message so all can see
			
		case BROADCAST:
			
			break;
			
		// /titanchat colour [colour]
		// Sets the default chat colour
			
		case COLOR:
		case COLOUR:
			if (plugin.isAdmin(player)) {
				if (ChatColor.valueOf(arg) != null) {
					chManager.setColour(channel, arg.toUpperCase());
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "Invalid Colour");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the channel colour");
			}
			break;
			
		// /titanchat create [channel]
		// Creates a new channel
			
		case CREATE:
			if (!plugin.has(player, "TitanChat.create")) {
				plugin.sendWarning(player, "You do not have permission to create channels");
			}
			
			if (plugin.channelExist(arg)) {
				plugin.sendWarning(player, "Channel already exists");
			}
			
			plugin.createChannel(player, arg);
			chManager.createChannel(player.getName(), arg);
			plugin.sendInfo(player, "You have created " + arg);
			break;
			
		// /titanchat decline [channel]
		// Declines invitation to join the channel
			
		case DECLINE:
			if (plugin.isInvited(player, arg)) {
				plugin.decline(player, arg);
				plugin.sendInfo(player, "You have declined the invitation");
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			break;
			
		// /titanchat delete [channel]
		// Deletes a channel
			
		case DELETE:
			if (plugin.has(player, "TitanChat.delete")) {
				if (plugin.channelExist(arg)) {
					if (plugin.getDefaultChannel() != arg && plugin.getStaffChannel() != arg) {
						plugin.deleteChannel(player, arg);
						chManager.deleteChannel(arg);
						plugin.sendInfo(player, "You have deleted " + arg);
						
					} else if (plugin.getDefaultChannel() == arg) {
						plugin.sendWarning(player, "You cannot delete the default channel");
						
					} else if (plugin.getStaffChannel() == arg) {
						plugin.sendWarning(player, "You cannot delete the staff channel");
					}
					
				} else {
					plugin.sendWarning(player, "Channel does not exists");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to delete channels");
			}
			break;
			
		// /titanchat demote [player]
		// Demotes the player on the channel
			
		case DEMOTE:
			if (plugin.canDemote(player, channel)) {
				if (plugin.isAdmin(plugin.getPlayer(arg))) {
					plugin.demote(plugin.getPlayer(arg), channel);
					chManager.demote(plugin.getPlayer(arg).getName(), channel);
					plugin.sendInfo(player, "You have demoted " + plugin.getPlayer(arg).getName());
					
				} else {
					plugin.sendWarning(player, plugin.getPlayer(arg).getName() + " is not an Admin");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to demote players on this channel");
			}
			break;
			
		// /titanchat invite [player]
		// Invites the player to chat on the channel
			
		case INVITE:
			if (plugin.canInvite(player, channel)) {
				plugin.invite(plugin.getServer().getPlayer(arg), channel);
				plugin.sendInfo(player, "You have invited " + plugin.getPlayer(arg).getName());
				
			} else {
				plugin.sendWarning(player, "You do not have permission to invite players");
			}
			break;
			
		// /titanchat join [channel]
		// Joins the channel
			
		case JOIN:
			if (plugin.isPublic(arg)) {
				if (plugin.isBanned(player, arg)) {
					plugin.sendWarning(player, "You're banned on the channel");
					
				} else if (plugin.channelExist(arg)) {
					plugin.channelSwitch(player, channel, arg);
					plugin.sendInfo(player, "You have switched channels");
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} else {
				if (plugin.canAccess(player, arg)) {
					if (plugin.channelExist(arg)) {
						plugin.channelSwitch(player, channel, arg);
						plugin.sendInfo(player, "You switched channels");
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to join this channel");
				}
			}
			break;
			
		// /titanchat kick [player]
		// Kicks the player from the channel
			
		case KICK:
			if (plugin.canKick(player, channel)) {
				plugin.kick(plugin.getPlayer(arg), channel);
				for (Player participants : plugin.getParticipants(channel)) {
					participants.sendMessage(plugin.getPlayer(arg).getName() + " has been kicked from the channel");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to kick on this channel");
			}
			break;
			
		// /titanchat mute [player]
		// Mutes the player
			
		case MUTE:
			if (plugin.canMute(player)) {
				plugin.mute(plugin.getPlayer(arg), channel);
				for (Player participants : plugin.getParticipants(channel)) {
					participants.sendMessage(plugin.getPlayer(arg).getName() + " has been muted");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to mute people on this channel");
			}
			break;
			
		// /titanchat promote [player]
		// Promotes the player on the channel
			
		case PROMOTE:
			if (plugin.canPromote(player, channel)) {
				if (plugin.isAdmin(plugin.getPlayer(arg))) {
					plugin.sendWarning(player, plugin.getPlayer(arg).getName() + " is already an Admin");
					
				} else {
					plugin.promote(plugin.getPlayer(arg), channel);
					chManager.promote(plugin.getPlayer(arg).getName(), channel);
					plugin.sendInfo(player, "You have promoted " + plugin.getPlayer(arg).getName());
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to promote players on this channel");
			}
			break;
			
		// /titanchat public [true/false]
		// Sets the state of the channel
			
		case PUBLIC:
			if (plugin.isAdmin(player)) {
				if (arg.equalsIgnoreCase("true")) {
					chManager.setPublic(channel, true);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else if (arg.equalsIgnoreCase("false")) {
					chManager.setPublic(channel, false);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "True or False?");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the state of this channel");
			}
			break;
			
		// /titanchat tag [tag]
		// Sets the channel tag
			
		case TAG:
			if (plugin.isAdmin(player)) {
				chManager.setTag(channel, arg);
				plugin.sendInfo(player, "You have changed the settings");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change channel tags on this channel");
			}
			break;
			
		// /titanchat unban [plyaer]
		// Unbans the player from the channel
			
		case UNBAN:
			if (plugin.canBan(player, channel)) {
				if (plugin.isBanned(plugin.getPlayer(arg), channel)) {
					plugin.unban(plugin.getPlayer(arg), channel);
					chManager.unban(plugin.getPlayer(arg).getName(), channel);
					
				} else {
					plugin.sendWarning(player, plugin.getPlayer(arg).getName() + " is not banned");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to unban on this channel");
			}
			break;
			
		// /titanchat unmute [player]
		// Unmutes the player
			
		case UNMUTE:
			if (plugin.canMute(player)) {
				if (plugin.isMuted(plugin.getPlayer(arg), channel)) {
					plugin.unmute(plugin.getPlayer(arg), channel);
					for (Player participants : plugin.getParticipants(channel)) {
						participants.sendMessage(plugin.getPlayer(arg).getName() + " has been unmuted");
					}
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to unmute on this channel");
			}
			break;
		}
	}
	
	public enum Commands {
		ACCEPT,
		ADD,
		ALLOWCOLORS,
		ALLOWCOLOURS,
		BAN,
		BROADCAST,
		COLOR,
		COLOUR,
		CREATE,
		DECLINE,
		DELETE,
		DEMOTE,
		INVITE,
		JOIN,
		KICK,
		MUTE,
		PROMOTE,
		PUBLIC,
		TAG,
		UNBAN,
		UNMUTE
	}
}
