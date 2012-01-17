package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
			if (plugin.channelExist(arg)) {
				if (plugin.isInvited(player, arg)) {
					plugin.inviteResponse(player, arg, true);
					
				} else {
					plugin.sendWarning(player, "You did not receive any invitations from this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat add [player]
		// Adds a player to the whitelist
			
		case ADD:
			if (plugin.channelExist(targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat allowcolours [channel]
		// Sets whether colour codes are allowed on the channel
			
		case ALLOWCOLORS:
		case ALLOWCOLOURS:
			if (plugin.channelExist(arg)) {
				if (plugin.has(player, "TitanChat.admin")) {
					chManager.setAllowColours(arg, (new Channel(plugin).allowColours(arg)) ? false : true);
					plugin.sendInfo(player, "The channel now " + ((new Channel(plugin).allowColours(arg)) ? "allows" : "disallows") + " colours");
					
				} else {
					plugin.sendWarning(player, "You do not have permission to change the state of this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat ban [player]
		// Bans the player from the channel
			
		case BAN:
			if (plugin.channelExist(targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat broadcast [message]
		// Broadcasts the message globally
			
		case BROADCAST:
			if (player.hasPermission("TitanChat.broadcast")) {
				try {
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
					
					Logger.getLogger("TitanLog").info("<" + player.getName() + "> " + ChatColor.stripColor(msg));
					
				} catch (IllegalArgumentException e) {
					plugin.sendWarning(player, "Invalid Colour");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to broadcast");
			}
			break;
			
		// /titanchat colour [colour]
		// Sets the default chat colour
			
		case COLOR:
		case COLOUR:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.isAdmin(player, targetChannel)) {
					try {
						ChatColor.valueOf(arg.toUpperCase());
						chManager.setColour(targetChannel, arg.toUpperCase());
						plugin.sendInfo(player, "You have changed the colour to " + arg);
					} catch (IllegalArgumentException e) {
						plugin.sendWarning(player, "Invalid Colour");
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to change the channel colour");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat commands [page]
		// Command list of TitanChat
			
		case COMMANDS:
			if (arg.equalsIgnoreCase("1")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/5) ==");
				player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
				player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
				player.sendMessage(ChatColor.AQUA + "allowcolours [channel] - Sets whether colour codes are used; Alias: allowcolors");
				player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
				player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
				
			} else if (arg.equalsIgnoreCase("2")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/5) ==");
				player.sendMessage(ChatColor.AQUA + "colour [colour] - Sets the chat colour of the channel; Alias: color");
				player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
				player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
				player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
				player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("3")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/5) ==");
				player.sendMessage(ChatColor.AQUA + "filter [phrase] - Adds the phrase to the filter");
				player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
				player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
				player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
				player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
				
			} else if (arg.equalsIgnoreCase("4")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/5) ==");
				player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
				player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have acces to");
				player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
				player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
				player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
				
			} else if (arg.equalsIgnoreCase("5")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/5) ==");
				player.sendMessage(ChatColor.AQUA + "status [channel] - Sets whether the channel is private");
				player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
				player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
				player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
				player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
				
			} else {
				player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
				player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
				player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
			}
			break;
			
		// /titanchat create [channel]
		// Creates a new channel
			
		case CREATE:
			if (plugin.getConfig().getInt("channel-limit") < 0) {
				if (plugin.has(player, "TitanChat.create")) {
					if (plugin.channelExist(arg)) {
						plugin.sendWarning(player, "Channel already exists");
						
					} else {
						plugin.createChannel(player, arg);
						chManager.createChannel(player.getName(), arg);
						plugin.sendInfo(player, "You have created " + arg);
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to create channels");
				}
				
			} else if (plugin.getChannelAmount() < plugin.getConfig().getInt("channel-limit")) {
				if (plugin.has(player, "TitanChat.create")) {
					if (plugin.channelExist(arg)) {
						plugin.sendWarning(player, "Channel already exists");
						
					} else {
						plugin.createChannel(player, arg);
						chManager.createChannel(player.getName(), arg);
						plugin.sendInfo(player, "You have created " + arg);
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to create channels");
				}
				
			} else {
				plugin.sendWarning(player, "Cannot create channel - Limit Passed");
			}
			break;
			
		// /titanchat decline [channel]
		// Declines invitation to join the channel
			
		case DECLINE:
			if (plugin.channelExist(arg)) {
				if (plugin.isInvited(player, arg)) {
					plugin.inviteResponse(player, arg, false);
					plugin.sendInfo(player, "You have declined the invitation");
					
				} else {
					plugin.sendWarning(player, "You did not receive any invitations from this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
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
			if (plugin.channelExist(targetChannel)) {
				if (plugin.canRank(player, targetChannel)) {
					if (plugin.getPlayer(arg) != null) {
						if (plugin.isAdmin(plugin.getPlayer(arg), targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		// /titanchat filter [phrase]
		// Adds the phrase to the filter
			
		case FILTER:
			if (plugin.isStaff(player)) {
				if (plugin.channelExist(targetChannel)) {
					chManager.filter(arg);
					plugin.sendInfo(player, "'" +  arg + "' has been filtered");
					
				} else {
					chManager.filter(arg + " " + targetChannel);
					plugin.sendInfo(player, "'" + arg + " " + targetChannel + "' has been filtered");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to use this command");
			}
			break;
			
		// /titanchat follow [channel]
		// Follows the channel
			
		case FOLLOW:
			if (plugin.channelExist(arg)) {
				if (plugin.canAccess(player, arg)) {
					if (plugin.isFollowing(player, arg)) {
						plugin.sendWarning(player, "You are already following " + arg);
						
					} else {
						plugin.follow(player, arg);
						chManager.follow(player.getName(), arg);
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to follow that channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat force [player]
		// Forces the player to join the channel
			
		case FORCE:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.has(player, "TitanChat.force")) {
					if (plugin.getPlayer(arg) != null) {
						plugin.channelSwitch(plugin.getPlayer(arg), plugin.getChannel(plugin.getPlayer(arg)), targetChannel);
						plugin.sendInfo(player, "You have forced " + plugin.getPlayer(arg).getName() + " to join the channel");
						plugin.sendInfo(plugin.getPlayer(arg), "You have been forced to join " + targetChannel);
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat invite [player]
		// Invites the player to chat on the channel
			
		case INVITE:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.isAdmin(player, targetChannel)) {
					if (plugin.getPlayer(arg) != null) {
						plugin.invite(plugin.getServer().getPlayer(arg), targetChannel);
						plugin.sendInfo(player, "You have invited " + plugin.getPlayer(arg).getName());
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to invite players on this channel");
				}
			}
			break;
			
		// /titanchat join [channel]
		// Joins the channel
			
		case JOIN:
			if (plugin.inLocal(player)) {
				plugin.local(player, arg);
				plugin.sendInfo(player, "You switched channels");
				
			} else if (arg.equalsIgnoreCase("local")) {
				plugin.local(player, plugin.getChannel(player));
				plugin.sendInfo(player, "You switched channels");
				
			} else {
				if (plugin.channelExist(arg)) {
					if (plugin.isPublic(arg)) {
						if (plugin.isBanned(player, arg)) {
							plugin.sendWarning(player, "You're banned on the channel");
							
						} else {
							plugin.channelSwitch(player, targetChannel, arg);
							plugin.sendInfo(player, "You have switched channels");
						}
						
					} else {
						if (plugin.canAccess(player, arg)) {
							plugin.channelSwitch(player, targetChannel, arg);
							plugin.sendInfo(player, "You switched channels");
							
						} else {
							plugin.sendWarning(player, "You do not have permission to join this channel");
						}
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
			}
			break;
			
		// /titanchat kick [player]
		// Kicks the player from the channel
			
		case KICK:
			if (plugin.channelExist(targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat mute [player]
		// Mutes the player
			
		case MUTE:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.canMute(player, targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat promote [player]
		// Promotes the player on the channel
			
		case PROMOTE:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.canRank(player, targetChannel)) {
					if (plugin.getPlayer(arg) != null) {
						if (plugin.isAdmin(plugin.getPlayer(arg), targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat silence [channel]
		// Silences the channel
			
		case SILENCE:
			if (plugin.has(player, "TitanChat.silence")) {
				if (plugin.channelExist(arg)) {
					plugin.silence(arg);
					
					for (Player participant : plugin.getParticipants(arg)) {
						if (plugin.isSilenced(arg)) {
							plugin.sendWarning(participant, "The channel has been silenced");
							
						} else {
							plugin.sendInfo(participant, "The channel is no longer silenced");
						}
					}
					
				} else {
					plugin.sendWarning(player, "You cannot silence non-existant channels");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to silence channels");
			}
			break;
			
		// /titanchat status [channel]
		// Sets the state of the channel
			
		case STATUS:
			if (plugin.channelExist(arg)) {
				if (plugin.isAdmin(player, arg)) {
					chManager.setPublic(arg, (plugin.isPublic(arg)) ? false : true);
					plugin.sendInfo(player, "The channel is now " + ((plugin.isPublic(arg)) ? "public" : "private"));
					
				} else {
					plugin.sendWarning(player, "You do not have permission to change the state of this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat tag [tag]
		// Sets the channel tag
			
		case TAG:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.isAdmin(player, targetChannel)) {
					chManager.setTag(targetChannel, arg);
					plugin.sendInfo(player, "You have changed the settings");
					
				} else {
					plugin.sendWarning(player, "You do not have permission to change channel tags on this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat unban [player]
		// Unbans the player from the channel
			
		case UNBAN:
			if (plugin.channelExist(targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat unfollow [channel]
		// Unfollows a channel
			
		case UNFOLLOW:
			if (plugin.channelExist(arg)) {
				if (plugin.isFollowing(player, arg)) {
					plugin.unfollow(player, arg);
					chManager.unfollow(player.getName(), arg);
					
				} else {
					plugin.sendWarning(player, "You are not following " + arg);
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		// /titanchat unmute [player]
		// Unmutes the player
			
		case UNMUTE:
			if (plugin.channelExist(targetChannel)) {
				if (plugin.canMute(player, targetChannel)) {
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
				
			} else {
				plugin.sendWarning(player, "No such channel");
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
		FILTER,
		FOLLOW,
		FORCE,
		INVITE,
		JOIN,
		KICK,
		MUTE,
		PROMOTE,
		SILENCE,
		STATUS,
		TAG,
		UNBAN,
		UNFOLLOW,
		UNMUTE
	}
}
