package com.titankingdoms.nodinchan.titanchat.commands;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Status;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;

public class TitanChatCommandHandler {
	
	private TitanChat plugin;
	private ConfigManager cfgManager;
	private MiniCommandHandler commands;
	
	public TitanChatCommandHandler(TitanChat plugin) {
		this.plugin = plugin;
		this.cfgManager = new ConfigManager(plugin);
		this.commands = new MiniCommandHandler(plugin);
	}
	
	public MiniCommandHandler getMCH() {
		return commands;
	}
	
	public void onCommand(Player player, String action, String arg) {
		String channelName = plugin.getChannel(player).getName();
		
		switch (Commands.fromName(action)) {
		
		case ACCEPT:
			commands.getInvite().accept(player, arg);
			
		case ADD:
			commands.getAdmin().add(player, arg, channelName);
			break;
			
		case BAN:
			commands.getAdmin().ban(player, arg, channelName);
			break;
			
		case BROADCAST:
			if (plugin.has(player, "TitanChat.broadcast")) {
				String msg = plugin.getFormat().broadcast(player, plugin.getFormat().filter(arg));
				
				plugin.getServer().broadcastMessage(msg);
				
				plugin.log(Level.INFO, "<" + player.getName() + ">" + arg);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to broadcast");
			}
			break;
			
		case CHCOLOR:
		case CHCOLOUR:
			commands.getChannelSettings().channelColour(player, arg, channelName);
			break;
			
		case CONVERTCOLOR:
		case CONVERTCOLOUR:
			commands.getChannelSettings().convertColour(player, arg);
			break;
			
		case COMMANDS:
			if (arg.equalsIgnoreCase("1")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/6) ==");
				player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
				player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
				player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
				player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
				player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] - Sets the display colour of the channel; Alias: chcolor");
				
			} else if (arg.equalsIgnoreCase("2")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/6) ==");
				player.sendMessage(ChatColor.AQUA + "convertcolour [channel] - Sets whether colour codes will be converted on the channel; Alias: convertcolor");
				player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
				player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
				player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
				player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("3")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/6) ==");
				player.sendMessage(ChatColor.AQUA + "filter [phrase] - Adds the phrase to the filter");
				player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
				player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
				player.sendMessage(ChatColor.AQUA + "format [format] - Sets the format of the channel");
				player.sendMessage(ChatColor.AQUA + "info [channel] - Gives the participants and followers of the channel");
				
			} else if (arg.equalsIgnoreCase("4")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/6) ==");
				player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
				player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
				player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
				player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have access to");
				player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("5")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/6) ==");
				player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] - Sets the display colour of the player name; Alias: ncolor");
				player.sendMessage(ChatColor.AQUA + "password [password] - Sets the password of the channel");
				player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
				player.sendMessage(ChatColor.AQUA + "reload - Reloads the configs");
				player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
				
			} else if (arg.equalsIgnoreCase("6")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (6/6) ==");
				player.sendMessage(ChatColor.AQUA + "status [password/private/public] - Sets the state of the channel");
				player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
				player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
				player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
				player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
				
			} else {
				player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
				player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
				player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
			}
			break;
			
		case CREATE:
			if (plugin.getConfig().getInt("channel-limit") < 0) {
				if (plugin.has(player, "TitanChat.create")) {
					if (plugin.channelExist(arg)) {
						plugin.sendWarning(player, "Channel already exists");
						
					} else {
						plugin.createChannel(player, arg);
						cfgManager.createChannel(player, arg);
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
						cfgManager.createChannel(player, arg);
						plugin.sendInfo(player, "You have created " + arg);
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to create channels");
				}
				
			} else {
				plugin.sendWarning(player, "Cannot create channel - Limit Passed");
			}
			break;
			
		case DECLINE:
			commands.getInvite().decline(player, arg);
			break;
			
		case DELETE:
			if (plugin.has(player, "TitanChat.delete")) {
				if (plugin.channelExist(arg)) {
					if (plugin.getDefaultChannel().getName() != arg && plugin.getStaffChannel().getName() != arg) {
						plugin.deleteChannel(player, arg);
						cfgManager.deleteChannel(arg);
						plugin.sendInfo(player, "You have deleted " + arg);
						
					} else {
						plugin.sendWarning(player, "You cannot delete this channel");
					}
					
				} else {
					plugin.sendWarning(player, "Channel does not exists");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to delete channels");
			}
			break;
			
		case DEMOTE:
			commands.getAdmin().demote(player, arg, channelName);
			break;
			
		case FILTER:
			if (plugin.isStaff(player)) {
				cfgManager.filter(arg);
				plugin.sendInfo(player, "'" +  arg + "' has been filtered");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to use this command");
			}
			break;
			
		case FOLLOW:
			commands.getChannelSettings().follow(player, arg);
			break;
			
		case FORCE:
			commands.getAdmin().force(player, arg, channelName);
			break;
			
		case FORMAT:
			commands.getChannelSettings().format(player, arg, channelName);
			break;
			
		case INFO:
			if (plugin.channelExist(arg)) {
				if (plugin.getChannel(arg).canAccess(player)) {
					String participantList = plugin.createList(plugin.getChannel(arg).getParticipants());
					String followerList = plugin.createList(plugin.getChannel(arg).getFollowers());
					
					if (plugin.getChannel(arg).getParticipants().isEmpty())
						participantList = "None";
					
					if (plugin.getChannel(arg).getFollowers().isEmpty())
						followerList = "None";
					
					player.sendMessage(ChatColor.AQUA + "Participants: " + participantList);
					player.sendMessage(ChatColor.AQUA + "Followers: " + followerList);
					
				} else {
					plugin.sendWarning(player, "You do not have access to that channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		case INVITE:
			commands.getInvite().invite(player, arg, channelName);
			break;
			
		case JOIN:
			if (plugin.channelExist(arg)) {
				if (plugin.getChannel(arg).getStatus().equals(Status.DEFAULT)) {
					plugin.channelSwitch(player, channelName, arg);
					plugin.sendInfo(player, "You have switched channels");
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.PASSWORD)) {
					plugin.sendWarning(player, plugin.getChannel(arg).getName() + " is a password protected channel");
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.PRIVATE)) {
					if (plugin.getChannel(arg).canAccess(player)) {
						plugin.channelSwitch(player, channelName, arg);
						plugin.sendInfo(player, "You have switched channels");
						
					} else {
						plugin.sendWarning(player, plugin.getChannel(arg).getName() + "is a private channel");
					}
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.PUBLIC)) {
					if (plugin.getChannel(arg).canAccess(player)) {
						plugin.channelSwitch(player, channelName, arg);
						plugin.sendInfo(player, "You have switched channels");
						
					} else {
						plugin.sendWarning(player, "You're banned on the channel");
					}
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.STAFF)) {
					if (plugin.isStaff(player)) {
						plugin.channelSwitch(player, channelName, arg);
						plugin.sendInfo(player, "You have switched channels");
						
					} else {
						plugin.sendWarning(player, "You do not have permission to join the staff channel");
					}
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		case KICK:
			commands.getAdmin().kick(player, arg, channelName);
			break;
			
		case MUTE:
			commands.getAdmin().mute(player, arg, channelName);
			break;
			
		case NCOLOR:
		case NCOLOUR:
			commands.getChannelSettings().nameColour(player, arg, channelName);
			break;
			
		case PASSWORD:
			commands.getChannelSettings().password(player, arg, channelName);
			break;
			
		case PROMOTE:
			commands.getAdmin().promote(player, arg, channelName);
			break;
			
		case SILENCE:
			if (plugin.has(player, "TitanChat.silence")) {
				if (plugin.channelExist(arg)) {
					plugin.getChannel(arg).setSilence((plugin.getChannel(arg).isSilenced()) ? false : true);
					
					for (String participant : plugin.getChannel(arg).getParticipants()) {
						if (plugin.getPlayer(participant) != null) {
							if (plugin.getChannel(arg).isSilenced())
								plugin.sendWarning(plugin.getPlayer(participant), "The channel has been silenced");
							else
								plugin.sendInfo(player, "The channel is no longer silenced");
						}
					}
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to silence channels");
			}
			break;
			
		case STATUS:
			commands.getChannelSettings().status(player, arg, channelName);
			break;
			
		case TAG:
			commands.getChannelSettings().tag(player, arg, channelName);
			break;
			
		case UNBAN:
			commands.getAdmin().unban(player, arg, channelName);
			break;
			
		case UNFOLLOW:
			commands.getChannelSettings().unfollow(player, arg);
			break;
			
		case UNMUTE:
			commands.getAdmin().unmute(player, arg, channelName);
			break;
		}
	}
	
	public void onCommand(Player player, String action, String arg, String targetChannel) {
		switch (Commands.fromName(action)) {
			
		case ADD:
			commands.getAdmin().add(player, arg, targetChannel);
			break;
			
		case BAN:
			commands.getAdmin().ban(player, arg, targetChannel);
			break;
			
		case BROADCAST:
			if (player.hasPermission("TitanChat.broadcast")) {
				plugin.getServer().broadcastMessage(arg + " " + targetChannel);
				
				String msg = plugin.getFormat().broadcast(player, plugin.getFormat().filter(arg + " " + targetChannel));
				
				plugin.log(Level.INFO, "<" + player.getName() + "> " + msg);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to broadcast");
			}
			break;
			
		case CHCOLOR:
		case CHCOLOUR:
			commands.getChannelSettings().channelColour(player, arg, targetChannel);
			break;
			
		case COMMANDS:
			if (arg.equalsIgnoreCase("1")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/6) ==");
				player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
				player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
				player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
				player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
				player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] - Sets the display colour of the channel; Alias: chcolor");
				
			} else if (arg.equalsIgnoreCase("2")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/6) ==");
				player.sendMessage(ChatColor.AQUA + "convertcolour [channel] - Sets whether colour codes will be converted on the channel; Alias: convertcolor");
				player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
				player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
				player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
				player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("3")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/6) ==");
				player.sendMessage(ChatColor.AQUA + "filter [phrase] - Adds the phrase to the filter");
				player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
				player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
				player.sendMessage(ChatColor.AQUA + "format [format] - Sets the format of the channel");
				player.sendMessage(ChatColor.AQUA + "info [channel] - Gives the participants and followers of the channel");
				
			} else if (arg.equalsIgnoreCase("4")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/6) ==");
				player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
				player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
				player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
				player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have access to");
				player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
				
			} else if (arg.equalsIgnoreCase("5")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/6) ==");
				player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] - Sets the display colour of the player name; Alias: ncolor");
				player.sendMessage(ChatColor.AQUA + "password [password] - Sets the password of the channel");
				player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
				player.sendMessage(ChatColor.AQUA + "reload - Reloads the configs");
				player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
				
			} else if (arg.equalsIgnoreCase("6")) {
				player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (6/6) ==");
				player.sendMessage(ChatColor.AQUA + "status [password/private/public] - Sets the state of the channel");
				player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
				player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
				player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
				player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
				
			} else {
				player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				player.sendMessage(ChatColor.AQUA + "Command: /titanchat [action] [argument]");
				player.sendMessage(ChatColor.AQUA + "Alias: /tc action [argument]");
				player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
			}
			break;
			
		case DEMOTE:
			commands.getAdmin().demote(player, arg, targetChannel);
			break;
			
		case FILTER:
			if (plugin.isStaff(player)) {
				cfgManager.filter(arg + " " + targetChannel);
				plugin.sendInfo(player, "'" + arg + " " + targetChannel + "' has been filtered");
				
			} else {
				plugin.sendWarning(player, "You do not have permission to use this command");
			}
			break;
		
		case FORCE:
			commands.getAdmin().force(player, arg, targetChannel);
			break;
			
		case FORMAT:
			commands.getChannelSettings().format(player, arg + " " + targetChannel, plugin.getChannel(player).getName());
			break;
			
		case INVITE:
			commands.getInvite().invite(player, arg, targetChannel);
			break;
			
		case JOIN:
			if (plugin.channelExist(arg)) {
				if (plugin.getChannel(arg).getStatus().equals(Status.DEFAULT)) {
					plugin.channelSwitch(player, plugin.getChannel(player).getName(), arg);
					plugin.sendInfo(player, "You have switched channels");
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.PASSWORD)) {
					if (plugin.correctPass(arg, targetChannel)) {
						plugin.channelSwitch(player, targetChannel, arg);
						plugin.sendInfo(player, "You switched channels");
						
					} else {
						plugin.sendWarning(player, "Incorrect password");
					}
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.PRIVATE)) {
					if (plugin.getChannel(arg).canAccess(player)) {
						plugin.channelSwitch(player, plugin.getChannel(player).getName(), arg);
						plugin.sendInfo(player, "You have switched channels");
						
					} else {
						plugin.sendWarning(player, plugin.getChannel(arg).getName() + "is a private channel");
					}
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.PUBLIC)) {
					if (plugin.getChannel(arg).canAccess(player)) {
						plugin.channelSwitch(player, plugin.getChannel(player).getName(), arg);
						plugin.sendInfo(player, "You have switched channels");
						
					} else {
						plugin.sendWarning(player, "You're banned on the channel");
					}
					
				} else if (plugin.getChannel(arg).getStatus().equals(Status.STAFF)) {
					if (plugin.isStaff(player)) {
						plugin.channelSwitch(player, plugin.getChannel(player).getName(), arg);
						plugin.sendInfo(player, "You have switched channels");
						
					} else {
						plugin.sendWarning(player, "You do not have permission to join the staff channel");
					}
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			break;
			
		case KICK:
			commands.getAdmin().kick(player, arg, targetChannel);
			break;
			
		case MUTE:
			commands.getAdmin().mute(player, arg, targetChannel);
			break;
			
		case NCOLOR:
		case NCOLOUR:
			commands.getChannelSettings().nameColour(player, arg, targetChannel);
			break;
			
		case PASSWORD:
			commands.getChannelSettings().password(player, arg, targetChannel);
			break;
			
		case PROMOTE:
			commands.getAdmin().promote(player, arg, targetChannel);
			break;
			
		case STATUS:
			commands.getChannelSettings().status(player, arg, targetChannel);
			break;
			
		case TAG:
			commands.getChannelSettings().tag(player, arg, targetChannel);
			break;
			
		case UNBAN:
			commands.getAdmin().unban(player, arg, targetChannel);
			break;
			
		case UNMUTE:
			commands.getAdmin().unmute(player, arg, targetChannel);
			break;
		}
	}
	
	public enum Commands {
		ACCEPT("accept"),
		ADD("add"),
		BAN("ban"),
		BROADCAST("broadcast"),
		CHCOLOR("chcolor"),
		CHCOLOUR("chcolour"),
		CONVERTCOLOR("convertcolor"),
		CONVERTCOLOUR("convertcolour"),
		COMMANDS("commands"),
		CREATE("create"),
		DECLINE("decline"),
		DELETE("delete"),
		DEMOTE("demote"),
		FILTER("filter"),
		FOLLOW("follow"),
		FORCE("force"),
		FORMAT("format"),
		INFO("info"),
		INVITE("invite"),
		JOIN("join"),
		KICK("kick"),
		LIST("list"),
		MUTE("mute"),
		NCOLOR("ncolor"),
		NCOLOUR("ncolour"),
		PASSWORD("password"),
		PROMOTE("promote"),
		RELOAD("reload"),
		SILENCE("silence"),
		STATUS("status"),
		TAG("tag"),
		UNBAN("unban"),
		UNFOLLOW("unfollow"),
		UNMUTE("unmute");
		
		private String name;
		
		private static Map<String, Commands> NAME_MAP = new HashMap<String, Commands>();
		
		private Commands(String name) {
			this.name = name;
		}
		
		static {
			for (Commands command : EnumSet.allOf(Commands.class)) {
				NAME_MAP.put(command.name, command);
			}
		}
		
		public static Commands fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String getName() {
			return name;
		}
	}
}
