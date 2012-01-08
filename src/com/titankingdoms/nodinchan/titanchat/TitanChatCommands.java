package com.titankingdoms.nodinchan.titanchat;

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
						plugin.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channel);
						chManager.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")).getName(), channel);
					}
					
				} else {
					plugin.whitelistMember(plugin.getPlayer(arg), channel);
					chManager.whitelistMember(plugin.getPlayer(arg).getName(), channel);
				}
			}
			break;
			
		// /titanchat allowcolours [true/false]
		// Sets whether colour codes are allowed on the channel
			
		case ALLOWCOLORS:
		case ALLOWCOLOURS:
			if (player.hasPermission("TitanChat.admin")) {
				if (arg.equalsIgnoreCase("true")) {
					chManager.setAllowColours(channel, true);
					
				} else if (arg.equalsIgnoreCase("false")) {
					chManager.setAllowColours(channel, false);
					
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
				
			} else {
				plugin.sendWarning(player, "You do not have permission to ban on this channel");
			}
			break;
			
		// /titanchat colour [colour]
		// Sets the default chat colour
			
		case COLOR:
		case COLOUR:
			if (plugin.isAdmin(player)) {
				if (ChatColor.valueOf(arg) != null) {
					chManager.setColour(channel, arg.toUpperCase());
					
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
			if (!player.hasPermission("TitanChat.create")) {
				plugin.sendWarning(player, "You do not have permission to create channels");
			}
			
			if (plugin.channelExist(arg)) {
				plugin.sendWarning(player, "Channel already exists");
			}
			
			plugin.createChannel(player, arg);
			chManager.createChannel(player.getName(), arg);
			break;
			
		// /titanchat decline [channel]
		// Declines invitation to join the channel
			
		case DECLINE:
			if (plugin.isInvited(player, arg)) {
				plugin.decline(player, arg);
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			break;
			
		// /titanchat delete [channel]
		// Deletes a channel
			
		case DELETE:
			if (player.hasPermission("TitanChat.admin")) {
				if (plugin.channelExist(arg)) {
					plugin.deleteChannel(player, arg);
					chManager.deleteChannel(arg);
					
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
				
			} else {
				plugin.sendWarning(player, "You do not have permission to invite players");
			}
			break;
			
		// /titanchat join [channel]
		// Joins the channel
			
		case JOIN:
			if (plugin.isPublic(arg)) {
				if (plugin.channelExist(arg)) {
					plugin.channelSwitch(player, channel, arg);
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} else {
				if (plugin.canAccess(player, arg)) {
					if (plugin.channelExist(arg)) {
						plugin.channelSwitch(player, channel, arg);
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
				
			} else {
				plugin.sendWarning(player, "You do not have permission to kick on this channel");
			}
			break;
			
		// /titanchat prefix [tag]
		// Sets the channel tag
			
		case PREFIX:
			if (plugin.isAdmin(player)) {
				chManager.setPrefix(channel, arg);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change channel tags on this channel");
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
					
				} else if (arg.equalsIgnoreCase("false")) {
					chManager.setPublic(channel, false);
					
				} else {
					plugin.sendWarning(player, "True or False?");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the state of this channel");
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
		COLOR,
		COLOUR,
		CREATE,
		DECLINE,
		DELETE,
		DEMOTE,
		INVITE,
		JOIN,
		KICK,
		PREFIX,
		PROMOTE,
		PUBLIC
	}
}
