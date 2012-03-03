package com.titankingdoms.nodinchan.titanchat.commands;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.support.Command;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;

public class TitanChatCommandHandler {
	
	private TitanChat plugin;
	private ConfigManager cfgManager;

	private Administrate admin;
	private ChannelSettings chSettings;
	private Invite invite;
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	public TitanChatCommandHandler(TitanChat plugin) {
		this.plugin = plugin;
		this.cfgManager = new ConfigManager(plugin);
		this.admin = new Administrate(plugin);
		this.chSettings = new ChannelSettings(plugin);
		this.invite = new Invite(plugin);
	}
	
	public void onCommand(Player player, String cmd, String[] args) {
		if (Commands.fromName(cmd) != null) {
			if (args.length < 1) {
				Channel channel = plugin.getChannel(player);
				String channelName = plugin.getChannel(player).getName();
				
				switch (Commands.fromName(cmd)) {
				
				case CONVERTCOLOR:
				case CONVERTCOLOUR:
					if (plugin.has(player, "TitanChat.admin")) {
						plugin.getConfigManager().setConvertColours(channelName, (plugin.getFormat().colours(channelName)) ? false : true);
						plugin.sendInfo(player, "The channel now " + ((plugin.getFormat().colours(channelName)) ? "converts" : "ignores") + " colour codes");
						
					} else {
						plugin.sendWarning(player, "You do not have permission to change this setting");
					}
					break;
					
				case COMMANDS:
					player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
					player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
					player.sendMessage(ChatColor.AQUA + "Alias: /tc [command] [arguments]");
					player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					break;
					
				case INFO:
					String participantList = "";
					String followerList = "";
					
					if (channel.getParticipants().isEmpty())
						participantList = "None";
					else
						participantList = plugin.createList(channel.getParticipants());
					
					if (channel.getFollowers().isEmpty())
						followerList = "None";
					else
						followerList = plugin.createList(channel.getFollowers());
					
					player.sendMessage(ChatColor.AQUA + "Participants: " + participantList);
					player.sendMessage(ChatColor.AQUA + "Followers: " + followerList);
					break;
					
				case LIST:
					List<String> channelList = new ArrayList<String>();
					
					for (Channel ch : plugin.getChannels()) {
						if (ch.canAccess(player)) {
							channelList.add(ch.getName());
						}
					}
					
					if (channelList.isEmpty())
						plugin.sendInfo(player, "Channel list: None");
					else
						plugin.sendInfo(player, "Channel list: " + plugin.createList(channelList));
					break;
					
				case RELOAD:
					if (plugin.isStaff(player)) {
						plugin.log(Level.INFO, "Reloading configs...");
						plugin.sendInfo(player, "Reloading configs...");
						
						plugin.saveConfig();
						plugin.saveChannelConfig();
						
						plugin.reloadConfig();
						plugin.reloadChannelConfig();
						
						try { plugin.prepareChannels(); } catch (Exception e) {}
						
						plugin.log(Level.INFO, "Configs reloaded");
						plugin.sendInfo(player, "Configs reloaded");
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					break;
					
				case SILENCE:
					plugin.setSilence((plugin.isSilenced()) ? false : true);
					
					for (Player receiver : plugin.getServer().getOnlinePlayers()) {
						if (plugin.isSilenced()) {
							plugin.sendWarning(receiver, "All channels have been silenced");
							
						} else {
							plugin.sendInfo(receiver, "Channels are no longer silenced");
						}
					}
					break;
				}
				
				return;
			}
			
			if (args.length < 2) {
				String channelName = plugin.getChannel(player).getName();
				
				switch (Commands.fromName(cmd)) {
				
				case ACCEPT:
					invite.accept(player, args[0]);
					
				case ADD:
					admin.add(player, args[0], channelName);
					break;
					
				case BAN:
					admin.ban(player, args[0], channelName);
					break;
					
				case BROADCAST:
					if (plugin.has(player, "TitanChat.broadcast")) {
						String msg = plugin.getFormat().broadcastFormat(player, args[0]);
						
						plugin.getServer().broadcastMessage(msg);
						
						log.info("<" + player.getName() + ">" + args[0]);
						
					} else {
						plugin.sendWarning(player, "You do not have permission to broadcast");
					}
					break;
					
				case CHCOLOR:
				case CHCOLOUR:
					chSettings.channelColour(player, args[0], channelName);
					break;
					
				case CONVERTCOLOR:
				case CONVERTCOLOUR:
					chSettings.convertColour(player, args[0]);
					break;
					
				case COMMANDS:
					if (args[0].equalsIgnoreCase("1")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/6) ==");
						player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
						player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
						player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
						player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] - Sets the display colour of the channel; Alias: chcolor");
						
					} else if (args[0].equalsIgnoreCase("2")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/6) ==");
						player.sendMessage(ChatColor.AQUA + "convertcolour [channel] - Sets whether colour codes will be converted on the channel; Alias: convertcolor");
						player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
						player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
						player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
						player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
						
					} else if (args[0].equalsIgnoreCase("3")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/6) ==");
						player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
						player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
						player.sendMessage(ChatColor.AQUA + "format [format] - Sets the format of the channel");
						player.sendMessage(ChatColor.AQUA + "info [channel] - Gives the participants and followers of the channel");
						player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
						
					} else if (args[0].equalsIgnoreCase("4")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/6) ==");
						player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
						player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
						player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have access to");
						player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] - Sets the display colour of the player name; Alias: ncolor");
						
					} else if (args[0].equalsIgnoreCase("5")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/6) ==");
						player.sendMessage(ChatColor.AQUA + "password [password] - Sets the password of the channel");
						player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "reload - Reloads the configs");
						player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
						player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
						
					} else if (args[0].equalsIgnoreCase("6")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (6/6) ==");
						player.sendMessage(ChatColor.AQUA + "type [password/private/public] - Sets the state of the channel");
						player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
						
					} else {
						player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
						player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
						player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
						player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					}
					break;
					
				case CREATE:
					if (plugin.getConfig().getInt("channel-limit") < 0) {
						if (plugin.has(player, "TitanChat.create")) {
							if (plugin.channelExist(args[0])) {
								plugin.sendWarning(player, "Channel already exists");
								
							} else {
								plugin.createChannel(player, args[0]);
								cfgManager.createChannel(player, args[0]);
								plugin.sendInfo(player, "You have created " + args[0]);
							}
							
						} else {
							plugin.sendWarning(player, "You do not have permission to create channels");
						}
						
					} else if (plugin.getChannelAmount() < plugin.getConfig().getInt("channel-limit")) {
						if (plugin.has(player, "TitanChat.create")) {
							if (plugin.channelExist(args[0])) {
								plugin.sendWarning(player, "Channel already exists");
								
							} else {
								plugin.createChannel(player, args[0]);
								cfgManager.createChannel(player, args[0]);
								plugin.sendInfo(player, "You have created " + args[0]);
							}
							
						} else {
							plugin.sendWarning(player, "You do not have permission to create channels");
						}
						
					} else {
						plugin.sendWarning(player, "Cannot create channel - Limit Passed");
					}
					break;
					
				case DECLINE:
					invite.decline(player, args[0]);
					break;
					
				case DELETE:
					if (plugin.has(player, "TitanChat.delete")) {
						if (plugin.channelExist(args[0])) {
							if (plugin.getDefaultChannel().getName() != args[0] && plugin.getStaffChannel().getName() != args[0]) {
								plugin.deleteChannel(player, plugin.getChannel(args[0]));
								cfgManager.deleteChannel(args[0]);
								plugin.sendInfo(player, "You have deleted " + args[0]);
								
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
					admin.demote(player, args[0], channelName);
					break;
					
				case FOLLOW:
					chSettings.follow(player, args[0]);
					break;
					
				case FORCE:
					admin.force(player, args[0], channelName);
					break;
					
				case FORMAT:
					chSettings.format(player, args[0], channelName);
					break;
					
				case INFO:
					if (plugin.channelExist(args[0])) {
						if (plugin.getChannel(args[0]).canAccess(player)) {
							String participantList = plugin.createList(plugin.getChannel(args[0]).getParticipants());
							String followerList = plugin.createList(plugin.getChannel(args[0]).getFollowers());
							
							if (plugin.getChannel(args[0]).getParticipants().isEmpty())
								participantList = "None";
							
							if (plugin.getChannel(args[0]).getFollowers().isEmpty())
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
					invite.invite(player, args[0], channelName);
					break;
					
				case JOIN:
					if (plugin.channelExist(args[0])) {
						Channel channel = plugin.getChannel(args[0]);
						
						if (channel.getType().equals(Type.CUSTOM)) {
							if (plugin.getCustomChannel(channel).onJoin(player)) {
								plugin.channelSwitch(player, plugin.getChannel(channelName), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, "You do not have permission to join " + channel.getName());
							}
							
						} else if (channel.getType().equals(Type.DEFAULT)) {
							plugin.channelSwitch(player, plugin.getChannel(channelName), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else if (channel.getType().equals(Type.PASSWORD)) {
							plugin.sendWarning(player, channel.getName() + " is a password protected channel");
							
						} else if (channel.getType().equals(Type.PRIVATE)) {
							if (plugin.getChannel(args[0]).canAccess(player)) {
								plugin.channelSwitch(player, plugin.getChannel(channelName), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, channel.getName() + "is a private channel");
							}
							
						} else if (channel.getType().equals(Type.PUBLIC)) {
							if (channel.canAccess(player)) {
								plugin.channelSwitch(player, plugin.getChannel(channelName), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, "You're banned on the channel");
							}
							
						} else if (channel.getType().equals(Type.STAFF)) {
							if (plugin.isStaff(player)) {
								plugin.channelSwitch(player, plugin.getChannel(channelName), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, "You do not have permission to join the staff channel");
							}
							
						} else {
							plugin.sendWarning(player, "That channel has not been loaded properly");
						}
						
					} else {
						plugin.sendWarning(player, "No such channel");
					}
					break;
					
				case KICK:
					admin.kick(player, args[0], channelName);
					break;
					
				case MUTE:
					admin.mute(player, args[0], channelName);
					break;
					
				case NCOLOR:
				case NCOLOUR:
					chSettings.nameColour(player, args[0], channelName);
					break;
					
				case PASSWORD:
					chSettings.password(player, args[0], channelName);
					break;
					
				case PROMOTE:
					admin.promote(player, args[0], channelName);
					break;
					
				case RELOAD:
					if (plugin.isStaff(player)) {
						plugin.log(Level.INFO, "Reloading configs...");
						plugin.sendInfo(player, "Reloading configs...");
						
						plugin.saveConfig();
						plugin.saveChannelConfig();
						
						plugin.reloadConfig();
						plugin.reloadChannelConfig();
						
						try { plugin.prepareChannels(); } catch (Exception e) {}
						
						plugin.log(Level.INFO, "Configs reloaded");
						plugin.sendInfo(player, "Configs reloaded");
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					break;
					
				case SILENCE:
					if (plugin.has(player, "TitanChat.silence")) {
						if (plugin.channelExist(args[0])) {
							plugin.getChannel(args[0]).setSilence((plugin.getChannel(args[0]).isSilenced()) ? false : true);
							
							for (String participant : plugin.getChannel(args[0]).getParticipants()) {
								if (plugin.getPlayer(participant) != null) {
									if (plugin.getChannel(args[0]).isSilenced())
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
					
				case TAG:
					chSettings.tag(player, args[0], channelName);
					break;
					
				case TYPE:
					chSettings.type(player, args[0], channelName);
					break;
					
				case UNBAN:
					admin.unban(player, args[0], channelName);
					break;
					
				case UNFOLLOW:
					chSettings.unfollow(player, args[0]);
					break;
					
				case UNMUTE:
					admin.unmute(player, args[0], channelName);
					break;
				}
				
				return;
			}
			
			if (args.length < 3) {
				switch (Commands.fromName(cmd)) {
				
				case ADD:
					admin.add(player, args[0], args[1]);
					break;
					
				case BAN:
					admin.ban(player, args[0], args[1]);
					break;
					
				case BROADCAST:
					if (player.hasPermission("TitanChat.broadcast")) {
						plugin.getServer().broadcastMessage(args[0] + " " + args[1]);
						
						String msg = plugin.getFormat().broadcastFormat(player, args[0] + " " + args[1]);
						
						log.info("<" + player.getName() + "> " + msg);
						
					} else {
						plugin.sendWarning(player, "You do not have permission to broadcast");
					}
					break;
					
				case CHCOLOR:
				case CHCOLOUR:
					chSettings.channelColour(player, args[0], args[1]);
					break;
					
				case COMMANDS:
					if (args[0].equalsIgnoreCase("1")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/6) ==");
						player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
						player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
						player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
						player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] - Sets the display colour of the channel; Alias: chcolor");
						
					} else if (args[0].equalsIgnoreCase("2")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/6) ==");
						player.sendMessage(ChatColor.AQUA + "convertcolour [channel] - Sets whether colour codes will be converted on the channel; Alias: convertcolor");
						player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
						player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
						player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
						player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
						
					} else if (args[0].equalsIgnoreCase("3")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/6) ==");
						player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
						player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
						player.sendMessage(ChatColor.AQUA + "format [format] - Sets the format of the channel");
						player.sendMessage(ChatColor.AQUA + "info [channel] - Gives the participants and followers of the channel");
						player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
						
					} else if (args[0].equalsIgnoreCase("4")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/6) ==");
						player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
						player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
						player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have access to");
						player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] - Sets the display colour of the player name; Alias: ncolor");
						
					} else if (args[0].equalsIgnoreCase("5")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/6) ==");
						player.sendMessage(ChatColor.AQUA + "password [password] - Sets the password of the channel");
						player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "reload - Reloads the configs");
						player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
						player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
						
					} else if (args[0].equalsIgnoreCase("6")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (6/6) ==");
						player.sendMessage(ChatColor.AQUA + "type [password/private/public] - Sets the state of the channel");
						player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
						
					} else {
						player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
						player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
						player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
						player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					}
					break;
					
				case DEMOTE:
					admin.demote(player, args[0], args[1]);
					break;
				
				case FORCE:
					admin.force(player, args[0], args[1]);
					break;
					
				case FORMAT:
					chSettings.format(player, args[0] + " " + args[1], plugin.getChannel(player).getName());
					break;
					
				case INVITE:
					invite.invite(player, args[0], args[1]);
					break;
					
				case JOIN:
					if (plugin.channelExist(args[0])) {
						Channel channel = plugin.getChannel(args[0]);
						
						if (channel.getType().equals(Type.CUSTOM)) {
							if (plugin.getCustomChannel(channel).onJoin(player)) {
								plugin.channelSwitch(player, plugin.getChannel(player), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, "You do not have permission to join " + channel.getName());
							}
							
						} else if (channel.getType().equals(Type.DEFAULT)) {
							plugin.channelSwitch(player, plugin.getChannel(player), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else if (channel.getType().equals(Type.PASSWORD)) {
							if (plugin.correctPass(channel, args[1])) {
								plugin.channelSwitch(player, plugin.getChannel(player), channel);
								plugin.sendInfo(player, "You switched channels");
								
							} else {
								plugin.sendWarning(player, "Incorrect password");
							}
							
						} else if (channel.getType().equals(Type.PRIVATE)) {
							if (plugin.getChannel(args[0]).canAccess(player)) {
								plugin.channelSwitch(player, plugin.getChannel(player), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, channel.getName() + "is a private channel");
							}
							
						} else if (channel.getType().equals(Type.PUBLIC)) {
							if (channel.canAccess(player)) {
								plugin.channelSwitch(player, plugin.getChannel(player), channel);
								plugin.sendInfo(player, "You have switched channels");
								
							} else {
								plugin.sendWarning(player, "You're banned on the channel");
							}
							
						} else if (channel.getType().equals(Type.STAFF)) {
							if (plugin.isStaff(player)) {
								plugin.channelSwitch(player, plugin.getChannel(player), channel);
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
					admin.kick(player, args[0], args[1]);
					break;
					
				case MUTE:
					admin.mute(player, args[0], args[1]);
					break;
					
				case NCOLOR:
				case NCOLOUR:
					chSettings.nameColour(player, args[0], args[1]);
					break;
					
				case PASSWORD:
					chSettings.password(player, args[0], args[1]);
					break;
					
				case PROMOTE:
					admin.promote(player, args[0], args[1]);
					break;
					
				case RELOAD:
					if (plugin.isStaff(player)) {
						plugin.log(Level.INFO, "Reloading configs...");
						plugin.sendInfo(player, "Reloading configs...");
						
						plugin.saveConfig();
						plugin.saveChannelConfig();
						
						plugin.reloadConfig();
						plugin.reloadChannelConfig();
						
						try { plugin.prepareChannels(); } catch (Exception e) {}
						
						plugin.log(Level.INFO, "Configs reloaded");
						plugin.sendInfo(player, "Configs reloaded");
						
					} else {
						plugin.sendWarning(player, "You do not have permission");
					}
					break;
					
				case TAG:
					chSettings.tag(player, args[0], args[1]);
					break;
					
				case TYPE:
					chSettings.type(player, args[0], args[1]);
					break;
					
				case UNBAN:
					admin.unban(player, args[0], args[1]);
					break;
					
				case UNMUTE:
					admin.unmute(player, args[0], args[1]);
					break;
				}
				
				return;
			}
			
			if (args.length > 3) {
				switch (Commands.fromName(cmd)) {
				
				case BROADCAST:
					if (player.hasPermission("TitanChat.broadcast")) {
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						String msg = plugin.getFormat().broadcastFormat(player, str.toString());
						
						plugin.getServer().broadcastMessage(msg);
						
						log.info("<" + player.getName() + "> " + str.toString());
						
					} else {
						plugin.sendWarning(player, "You do not have permission to broadcast");
					}
					break;
					
				case COMMANDS:
					if (args[1].equalsIgnoreCase("1")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (1/6) ==");
						player.sendMessage(ChatColor.AQUA + "accept [channel] - Accepts the channel join invitation and joins the channel");
						player.sendMessage(ChatColor.AQUA + "add [player] - Adds the player to the whitelist");
						player.sendMessage(ChatColor.AQUA + "ban [player] - Bans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "broadcast [message] - Broadcasts the message globally");
						player.sendMessage(ChatColor.AQUA + "chcolour [colourcode] - Sets the display colour of the channel; Alias: chcolor");
						
					} else if (args[1].equalsIgnoreCase("2")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (2/6) ==");
						player.sendMessage(ChatColor.AQUA + "convertcolour [channel] - Sets whether colour codes will be converted on the channel; Alias: convertcolor");
						player.sendMessage(ChatColor.AQUA + "create [channel] - Creates a channel by that name");
						player.sendMessage(ChatColor.AQUA + "decline [channel] - Declines the channel join invitation");
						player.sendMessage(ChatColor.AQUA + "delete [channel] - Deletes the channel with that name");
						player.sendMessage(ChatColor.AQUA + "demote [player] - Demotes the player on the channel");
						
					} else if (args[1].equalsIgnoreCase("3")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (3/6) ==");
						player.sendMessage(ChatColor.AQUA + "follow [channel] - Follows the channel and receive chat");
						player.sendMessage(ChatColor.AQUA + "force [player] - Forces the player to join the channel");
						player.sendMessage(ChatColor.AQUA + "format [format] - Sets the format of the channel");
						player.sendMessage(ChatColor.AQUA + "info [channel] - Gives the participants and followers of the channel");
						player.sendMessage(ChatColor.AQUA + "invite [player] - Invites the player to join the channel");
						
					} else if (args[1].equalsIgnoreCase("4")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (4/6) ==");
						player.sendMessage(ChatColor.AQUA + "join [channel] - Joins the channel");
						player.sendMessage(ChatColor.AQUA + "kick [player] - Kicks the player from the channel");
						player.sendMessage(ChatColor.AQUA + "list - Lists all channels you have access to");
						player.sendMessage(ChatColor.AQUA + "mute [player] - Mutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "ncolour [colourcode] - Sets the display colour of the player name; Alias: ncolor");
						
					} else if (args[1].equalsIgnoreCase("5")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (5/6) ==");
						player.sendMessage(ChatColor.AQUA + "password [password] - Sets the password of the channel");
						player.sendMessage(ChatColor.AQUA + "promote [player] - Promotes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "reload - Reloads the configs");
						player.sendMessage(ChatColor.AQUA + "silence [channel] - Silences the channel; Leave out [channel] to silence all");
						player.sendMessage(ChatColor.AQUA + "tag [tag] - Sets the channel tag");
						
					} else if (args[1].equalsIgnoreCase("6")) {
						player.sendMessage(ChatColor.AQUA + "== TitanChat Command List (6/6) ==");
						player.sendMessage(ChatColor.AQUA + "type [password/private/public] - Sets the state of the channel");
						player.sendMessage(ChatColor.AQUA + "unban [player] - Unbans the player from the channel");
						player.sendMessage(ChatColor.AQUA + "unmute [player] - Unmutes the player on the channel");
						player.sendMessage(ChatColor.AQUA + "unfollow [channel] - Unfollows the channel");
						
					} else {
						player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
						player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
						player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
						player.sendMessage(ChatColor.AQUA + "/titanchat commands [page]");
					}
					break;
					
				case FORMAT:
					StringBuilder str = new StringBuilder();
					
					for (String word : args) {
						if (str.length() > 0) {
							str.append(" ");
						}
						
						str.append(word);
					}
					
					chSettings.format(player, str.toString(), plugin.getChannel(player).getName());
					break;
				}
				
				return;
			}
			
			plugin.sendWarning(player, "Invalid Argument Length");
			
		} else {
			if (runCommands(player, cmd, args))
				return;
			
			plugin.sendWarning(player, "Invalid Command");
		}
	}
	
	public boolean runCommands(Player player, String cmd, String[] args) {
		for (Command command : plugin.getCommands()) {
			if (command.execute(player, cmd, args))
				return true;
		}
		
		return false;
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
		TAG("tag"),
		TYPE("type"),
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