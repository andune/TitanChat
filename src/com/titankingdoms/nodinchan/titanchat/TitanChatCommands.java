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
	
	public void onCommand(Player player, String action, String arg, String targetChannel) {
		switch (Commands.valueOf(action.toUpperCase())) {
		
		// /titanchat accept [channel]
		// Accepts invitation to join the channel
		
		case ACCEPT:
			if (plugin.isInvited(player, arg)) {
				plugin.inviteResponse(player, arg, true);
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			break;
			
		// /titanchat add [player]
		// Adds a player to the whitelist
			
		case ADD:
			if (plugin.canRank(player, targetChannel)) {
				if (arg.contains(",")) {
					List<String> members  = new ArrayList<String>();
					
					for (String newMember : arg.split(",")) {
						if (plugin.getPlayer(arg) != null) {
							members.add(plugin.getPlayer(newMember).getName());
						}
					}
					
					if (!members.isEmpty()) {
						for (String newMember : members) {
							plugin.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), targetChannel);
							chManager.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")).getName(), targetChannel);
						}
						
						plugin.sendInfo(player, plugin.createList(members) + " have been added to the Member List");
						
					} else {
						plugin.sendWarning(player, "Players not online");
					}
					
				} else {
					if (plugin.getPlayer(arg) != null) {
						plugin.whitelistMember(plugin.getPlayer(arg), targetChannel);
						chManager.whitelistMember(plugin.getPlayer(arg).getName(), targetChannel);
						plugin.sendInfo(player, plugin.getPlayer(arg).getName() + " has been added to the Member List");
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
				}
			}
			break;
			
		// /titanchat allowcolours [true/false]
		// Sets whether colour codes are allowed on the channel
			
		case ALLOWCOLORS:
		case ALLOWCOLOURS:
			if (plugin.has(player, "TitanChat.admin")) {
				if (arg.equalsIgnoreCase("true")) {
					chManager.setAllowColours(targetChannel, true);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else if (arg.equalsIgnoreCase("false")) {
					chManager.setAllowColours(targetChannel, false);
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
			if (plugin.canBan(player, targetChannel)) {
				if (plugin.getPlayer(arg) != null) {
					plugin.ban(plugin.getPlayer(arg), targetChannel);
					chManager.ban(plugin.getPlayer(arg).getName(), targetChannel);
					
					for (Player participant : plugin.getParticipants(targetChannel)) {
						participant.sendMessage(plugin.getPlayer(arg).getName() + " has been banned from the channel");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to ban on this channel");
			}
			break;
			
		// /titanchat broadcast [message]
		// Broadcasts the message globally
			
		case BROADCAST:
			if (player.hasPermission("TitanChat.broadcast")) {
				ChatColor broadcastColour = ChatColor.valueOf(plugin.getConfig().getString("broadcast.colour"));
				String tag = plugin.getConfig().getString("broadcast.tag");
				
				String msg = "";
				
				if (plugin.channelExist(targetChannel)) {
					msg = new Channel(plugin).format(player, broadcastColour, tag, arg, true);
					
				} else {
					msg = new Channel(plugin).format(player, broadcastColour, tag, arg + " " + targetChannel, true);
				}
				
				for (Player receiver : plugin.getServer().getOnlinePlayers()) {
					receiver.sendMessage(msg);
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to broadcast");
			}
			break;
			
		// /titanchat colour [colour]
		// Sets the default chat colour
			
		case COLOR:
		case COLOUR:
			if (plugin.isAdmin(player)) {
				if (ChatColor.valueOf(arg) != null) {
					chManager.setColour(targetChannel, arg.toUpperCase());
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "Invalid Colour");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the channel colour");
			}
			break;
			
		// /titanchat commands [page]
		// Command list of TitanChat
			
		case COMMANDS:
			if (arg.equalsIgnoreCase("1")) {
				player.sendMessage("TitanChat Command List (1/4)");
				player.sendMessage("accept [channel] - Accepts the channel join invitation and joins the channel");
				player.sendMessage("add [player] - Adds the player to the whitelist");
				player.sendMessage("allowcolours [true/false] - Sets whether colour codes are used; Alias: allowcolors");
				player.sendMessage("ban [player] - Bans the player from the channel");
				player.sendMessage("broadcast [message] - Broadcasts the message globally");
				
			} else if (arg.equalsIgnoreCase("2")) {
				player.sendMessage("TitanChat Command List (2/4)");
				player.sendMessage("colour [colour] - Sets the chat colour of the channel; Alias: color");
				player.sendMessage("create [channel] - Creates a channel by that name");
				player.sendMessage("decline [channel] - Declines the channel join invitation");
				player.sendMessage("delete [channel] - Deletes the channel with that name");
				player.sendMessage("demote [player] - Demotes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("3")) {
				player.sendMessage("TitanChat Command List (3/4)");
				player.sendMessage("invite [player] - Invites the player to join the channel");
				player.sendMessage("join [channel] - Joins the channel");
				player.sendMessage("kick [player] - Kicks the player from the channel");
				player.sendMessage("list - Lists all channels you have acces to");
				player.sendMessage("mute [player] - Mutes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("4")) {
				player.sendMessage("TitanChat Command List (4/4)");
				player.sendMessage("promote [player] - Promotes the player on the channel");
				player.sendMessage("public [true/false] - Sets whether the channel is public");
				player.sendMessage("tag [tag] - Sets the channel tag");
				player.sendMessage("unban [player] - Unbans the player from the channel");
				player.sendMessage("unmute [player] - Unmutes the player on the channel");
				
			} else {
				player.sendMessage("TitanChat Commands");
				player.sendMessage("Command: /titanchat [action] [argument]");
				player.sendMessage("Alias: /tc action [argument]");
				player.sendMessage("/titanchat commands [page]");
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
				plugin.inviteResponse(player, arg, false);
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
			if (plugin.canRank(player, targetChannel)) {
				if (plugin.getPlayer(arg) != null) {
					if (plugin.isAdmin(plugin.getPlayer(arg))) {
						plugin.demote(plugin.getPlayer(arg), targetChannel);
						chManager.demote(plugin.getPlayer(arg).getName(), targetChannel);
						plugin.sendInfo(player, "You have demoted " + plugin.getPlayer(arg).getName());
						
					} else {
						plugin.sendWarning(player, plugin.getPlayer(arg).getName() + " is not an Admin");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to demote players on this channel");
			}
			break;
			
		// /titanchat invite [player]
		// Invites the player to chat on the channel
			
		case INVITE:
			if (plugin.isAdmin(player)) {
				plugin.invite(plugin.getServer().getPlayer(arg), targetChannel);
				plugin.sendInfo(player, "You have invited " + plugin.getPlayer(arg).getName());
				
			} else {
				plugin.sendWarning(player, "You do not have permission to invite players on this channel");
			}
			break;
			
		// /titanchat join [channel]
		// Joins the channel
			
		case JOIN:
			if (plugin.isPublic(arg)) {
				if (plugin.isBanned(player, arg)) {
					plugin.sendWarning(player, "You're banned on the channel");
					
				} else if (plugin.channelExist(arg)) {
					plugin.channelSwitch(player, targetChannel, arg);
					plugin.sendInfo(player, "You have switched channels");
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} else {
				if (plugin.canAccess(player, arg)) {
					if (plugin.channelExist(arg)) {
						plugin.channelSwitch(player, targetChannel, arg);
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
			if (plugin.canKick(player, targetChannel)) {
				if (plugin.getPlayer(arg) != null) {
					plugin.kick(plugin.getPlayer(arg), targetChannel);
					
					for (Player participants : plugin.getParticipants(targetChannel)) {
						participants.sendMessage(plugin.getPlayer(arg).getName() + " has been kicked from the channel");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to kick on this channel");
			}
			break;
			
		// /titanchat mute [player]
		// Mutes the player
			
		case MUTE:
			if (plugin.canMute(player)) {
				if (plugin.getPlayer(arg) != null) {
					plugin.mute(plugin.getPlayer(arg), targetChannel);
					
					for (Player participants : plugin.getParticipants(targetChannel)) {
						participants.sendMessage(plugin.getPlayer(arg).getName() + " has been muted");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to mute people on this channel");
			}
			break;
			
		// /titanchat promote [player]
		// Promotes the player on the channel
			
		case PROMOTE:
			if (plugin.canRank(player, targetChannel)) {
				if (plugin.getPlayer(arg) != null) {
					if (plugin.isAdmin(plugin.getPlayer(arg))) {
						plugin.sendWarning(player, plugin.getPlayer(arg).getName() + " is already an Admin");
						
					} else {
						plugin.promote(plugin.getPlayer(arg), targetChannel);
						chManager.promote(plugin.getPlayer(arg).getName(), targetChannel);
						plugin.sendInfo(player, "You have promoted " + plugin.getPlayer(arg).getName());
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
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
					chManager.setPublic(targetChannel, true);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else if (arg.equalsIgnoreCase("false")) {
					chManager.setPublic(targetChannel, false);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "True or False?");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change the state of this channel");
			}
			break;
			
		// /titanchat silence [channel]
		// Silences the channel
			
		case SILENCE:
			if (plugin.has(player, "TitanChat.silence")) {
				if (plugin.channelExist(arg)) {
					plugin.silence(arg);
					
				} else {
					plugin.sendWarning(player, "You cannot silence non-existant channels");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to silence channels");
			}
			break;
			
		// /titanchat tag [tag]
		// Sets the channel tag
			
		case TAG:
			if (plugin.isAdmin(player)) {
				chManager.setTag(targetChannel, arg);
				plugin.sendInfo(player, "You have changed the settings");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change channel tags on this channel");
			}
			break;
			
		// /titanchat unban [plyaer]
		// Unbans the player from the channel
			
		case UNBAN:
			if (plugin.canBan(player, targetChannel)) {
				if (plugin.getPlayer(arg) != null) {
					if (plugin.isBanned(plugin.getPlayer(arg), targetChannel)) {
						plugin.unban(plugin.getPlayer(arg), targetChannel);
						chManager.unban(plugin.getPlayer(arg).getName(), targetChannel);
						
					} else {
						plugin.sendWarning(player, plugin.getPlayer(arg).getName() + " is not banned");
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to unban on this channel");
			}
			break;
			
		// /titanchat unmute [player]
		// Unmutes the player
			
		case UNMUTE:
			if (plugin.canMute(player)) {
				if (plugin.getPlayer(arg) != null) {
					if (plugin.isMuted(plugin.getPlayer(arg), targetChannel)) {
						plugin.unmute(plugin.getPlayer(arg), targetChannel);
						for (Player participants : plugin.getParticipants(targetChannel)) {
							participants.sendMessage(plugin.getPlayer(arg).getName() + " has been unmuted");
						}
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
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
		COMMANDS,
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
		SILENCE,
		TAG,
		UNBAN,
		UNMUTE
	}
}
