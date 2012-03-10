package com.titankingdoms.nodinchan.titanchat.command;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.support.Command;
import com.titankingdoms.nodinchan.titanchat.util.ConfigManager;

public class TitanChatCommandHandler {
	
	private TitanChat plugin;
	private ConfigManager cfgManager;

	private Administrate admin;
	private ChannelSettings chSettings;
	private Invite invite;
	private Util util;
	
	public TitanChatCommandHandler(TitanChat plugin) {
		this.plugin = plugin;
		this.cfgManager = new ConfigManager(plugin);
		this.admin = new Administrate(plugin);
		this.chSettings = new ChannelSettings(plugin);
		this.invite = new Invite(plugin);
		this.util = new Util(plugin);
	}
	
	public void invalidArgLength(Player player, Commands command) {
		plugin.sendWarning(player, "Invalid Argument Length");
		plugin.sendInfo(player, "Usage: " + command.getUsage());
	}
	
	public void onCommand(Player player, String cmd, String[] args) {
		if (Commands.fromName(cmd) != null) {
			Commands command = Commands.fromName(cmd);
			
			switch (command) {
			
			case ACCEPT:
				try { invite.accept(player, args[0]); } catch (IndexOutOfBoundsException e) { invalidArgLength(player, command); }
				break;
				
			case ADD:
				if (args.length < 1) { invalidArgLength(player, command); return; }
				try { admin.add(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.add(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case BAN:
				if (args.length < 1) { invalidArgLength(player, command); return; }
				try { admin.ban(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.ban(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case BROADCAST:
				if (args.length < 1 || !plugin.has(player, "TitanChat.broadcast")) { return; }
				
				StringBuilder broadcastStr = new StringBuilder();
				
				for (String word : args) {
					if (broadcastStr.length() > 1)
						broadcastStr.append(" ");
					
					broadcastStr.append(word);
				}
				
				plugin.getServer().broadcastMessage(plugin.getFormat().broadcastFormat(player, broadcastStr.toString()));
				plugin.getLogger().info("<" + player.getName() + "> " + broadcastStr.toString());
				break;
				
			case CHCOLOUR:
				if (args.length < 1) { invalidArgLength(player, command); return; }
				try { chSettings.channelColour(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { chSettings.channelColour(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case COLOURCODES:
				String black = plugin.getFormat().colourise("&0") + "&0";
				String darkblue = plugin.getFormat().colourise("&1") + "&1";
				String green = plugin.getFormat().colourise("&2") + "&2";
				String darkaqua = plugin.getFormat().colourise("&3") + "&3";
				String red = plugin.getFormat().colourise("&4") + "&4";
				String purple = plugin.getFormat().colourise("&5") + "&5";
				String gold = plugin.getFormat().colourise("&6") + "&6";
				String silver = plugin.getFormat().colourise("&7") + "&7";
				String grey = plugin.getFormat().colourise("&8") + "&8";
				String blue = plugin.getFormat().colourise("&9") + "&9";
				String lightgreen = plugin.getFormat().colourise("&a") + "&a";
				String aqua = plugin.getFormat().colourise("&b") + "&b";
				String lightred = plugin.getFormat().colourise("&c") + "&c";
				String lightpurple = plugin.getFormat().colourise("&d") + "&d";
				String yellow = plugin.getFormat().colourise("&e") + "&e";
				String white = plugin.getFormat().colourise("&f") + "&f";
				String magical = plugin.getFormat().colourise("&kMagical");
				String comma = ChatColor.WHITE + ",";
				
				player.sendMessage(ChatColor.AQUA + "=== Colour Codes ===");
				player.sendMessage(black + comma + darkblue + comma + green + comma + darkaqua + comma);
				player.sendMessage(red + comma + purple + comma + gold + comma + silver + comma);
				player.sendMessage(grey + comma + blue + comma + lightgreen + comma + aqua + comma);
				player.sendMessage(lightred + comma + lightpurple + comma + yellow + comma + white + comma);
				player.sendMessage("And also the Magical &k (" + magical + ChatColor.WHITE + ")");
				break;
			
			case COMMANDS:
				try {
					int page = Integer.parseInt(args[0]);
					int numPages = Commands.values().length / 5;
					int start = page * 5;
					int end = start + 5;
					
					if (Commands.values().length % 5 != 0 && (numPages * 5) - Commands.values().length < 0)
						numPages++;
					
					if (end > Commands.values().length)
						end = Commands.values().length;
					
					if (page > 0 || page < numPages) {
						player.sendMessage(ChatColor.AQUA + "=== TitanChat Command List (" + page + "/" + numPages + ") ===");
						for (int cmdNum = start; cmdNum < end; cmdNum++) {
							player.sendMessage(ChatColor.AQUA + Commands.values()[cmdNum].toString().toLowerCase());
						}
						plugin.sendInfo(player, "Arguments: [NECESSARY] <OPTIONAL>");
						plugin.sendInfo(player, "'/titanchat commands [command]' for more info");
						
					} else {
						player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
						player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
						player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
						plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
					}
					
				} catch (IndexOutOfBoundsException e) {
					player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
					player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
					player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
					plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
					
				} catch (NumberFormatException e) {
					if (Commands.fromName(args[0]) == null) {
						plugin.sendWarning(player, "No info on command");
						return;
					}
					
					player.sendMessage(ChatColor.AQUA + "=== " + Commands.fromName(args[0]).getName() + " Command ===");
					player.sendMessage(ChatColor.AQUA + "Description: " + Commands.fromName(args[0]).getDescription());
					
					StringBuilder cmdStr = new StringBuilder();
					
					for (String alias : Commands.fromName(args[0]).getAliases()) {
						if (cmdStr.length() > 0)
							cmdStr.append(", ");
						
						cmdStr.append(alias);
					}
					
					player.sendMessage(ChatColor.AQUA + "Aliases: " + cmdStr.toString());
					player.sendMessage(ChatColor.AQUA + "Usage: " + Commands.fromName(args[0]).getUsage());
				}
				break;
				
			case CONVERT:
				String channelName = "";
				
				try { channelName = plugin.getExactName(args[0]); } catch (IndexOutOfBoundsException e) { channelName = plugin.getChannel(player).getName(); }
				
				if (plugin.isStaff(player)) {
					plugin.getConfigManager().setConvertColours(channelName, (plugin.getFormat().colours(channelName)) ? false : true);
					plugin.sendInfo(player, "The channel now " + ((plugin.getFormat().colours(channelName)) ? "converts" : "ignores") + " colour codes");
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
				break;
				
			case CREATE:
				try {
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
					
				} catch (IndexOutOfBoundsException e) {
					invalidArgLength(player, command);
				}
				break;
				
			case DECLINE:
				try { invite.decline(player, args[0]); } catch (IndexOutOfBoundsException e) { invalidArgLength(player, command); }
				break;
				
			case DELETE:
				try {
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
					
				} catch (IndexOutOfBoundsException e) {
					invalidArgLength(player, command);
				}
				break;
				
			case DEMOTE:
				if (args.length < 1) { invalidArgLength(player, command); return; }
				try { admin.demote(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.demote(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case FOLLOW:
				try { chSettings.follow(player, args[0]); } catch (IndexOutOfBoundsException e) { invalidArgLength(player, command); }
				break;
				
			case FORCE:
				if (args.length < 1) { invalidArgLength(player, command); return; }
				try { admin.force(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.force(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case INFO:
				try {
					Channel channel = plugin.getChannel(args[0]);
					
					if (channel != null) {
						if (channel.canAccess(player)) {
							player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
							player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
							player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowers()));
						}
						
					} else {
						plugin.sendWarning(player, "No such channel");
					}
					
				} catch (IndexOutOfBoundsException e) {
					Channel channel = plugin.getChannel(player);
					
					player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
					player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
					player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowers()));
				}
				break;
				
			case INVITE:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { invite.invite(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { invite.invite(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case JOIN:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				
				if (plugin.channelExist(args[0])) {
					Channel channel = plugin.getChannel(args[0]);
					String password = "";
					
					try { password = args[1]; } catch (IndexOutOfBoundsException e) {}
					
					switch (channel.getType()) {
					
					case CUSTOM:
						if (plugin.getCustomChannel(channel).onJoin(player)) {
							plugin.channelSwitch(player, plugin.getChannel(player), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else {
							plugin.sendWarning(player, "You do not have permission to join " + channel.getName());
						}
						break;
					
					case DEFAULT:
						plugin.channelSwitch(player, plugin.getChannel(player), channel);
						plugin.sendInfo(player, "You have switched channels");
						break;
						
					case PASSWORD:
						if (password.equals("")) {
							plugin.sendWarning(player, "You need to enter a password");
							
						} else {
							if (plugin.correctPass(channel, password)) {
								if (channel.canAccess(player)) {
									plugin.channelSwitch(player, plugin.getChannel(player), channel);
									plugin.sendInfo(player, "You have switched channels");
									
								} else {
									plugin.sendWarning(player, "You are banned on this channel");
								}
								
							} else {
								plugin.sendWarning(player, "Incorrect password");
							}
						}
						break;
						
					case PRIVATE:
						if (channel.canAccess(player)) {
							plugin.channelSwitch(player, plugin.getChannel(player), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else {
							plugin.sendWarning(player, "You are not on the whitelist");
						}
						break;
						
					case PUBLIC:
						if (channel.canAccess(player)) {
							plugin.channelSwitch(player, plugin.getChannel(player), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else {
							plugin.sendWarning(player, "You are banned on this channel");
						}
						break;
						
					case STAFF:
						if (plugin.isStaff(player)) {
							plugin.channelSwitch(player, plugin.getChannel(player), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else {
							plugin.sendWarning(player, "You do not have permission to join " + channel.getName());
						}
						break;
					}
				}
				break;
				
			case KICK:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { admin.kick(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.kick(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case LIST:
				List<String> channels = new ArrayList<String>();
				
				for (Channel channel : plugin.getChannels()) {
					if (channel.canAccess(player))
						channels.add(channel.getName());
				}
				
				player.sendMessage(ChatColor.AQUA + "Channels: " + plugin.createList(channels));
				break;
				
			case MUTE:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { admin.mute(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.mute(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case NCOLOUR:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { chSettings.nameColour(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { chSettings.nameColour(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case PASSWORD:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { chSettings.password(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { chSettings.password(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case PROMOTE:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { admin.promote(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.promote(player, args[0], plugin.getChannel(player).getName()); }
				break;
			
			case RELOAD:
				util.reload(player);
				break;
				
			case SILENCE:
				if (plugin.has(player, "TitanChat.silence")) {
					try {
						if (plugin.channelExist(args[0])) {
							Channel channel = plugin.getChannel(args[0]);
							channel.setSilenced((channel.isSilenced()) ? false : true);
							
							for (String participant : plugin.getChannel(args[0]).getParticipants()) {
								if (plugin.getPlayer(participant) != null) {
									if (channel.isSilenced())
										plugin.sendWarning(plugin.getPlayer(participant), "The channel has been silenced");
									else
										plugin.sendInfo(player, "The channel is no longer silenced");
								}
							}
							
						} else {
							plugin.sendWarning(player, "No such channel");
						}
						
					} catch (IndexOutOfBoundsException e) {
						plugin.setSilenced((plugin.isSilenced()) ? false : true);
						
						for (Player receiver : plugin.getServer().getOnlinePlayers()) {
							if (plugin.isSilenced())
								plugin.sendWarning(receiver, "All channels have been silenced");
							else
								plugin.sendInfo(receiver, "Channels are no longer silenced");
						}
					}
				}
				break;
				
			case TAG:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { chSettings.tag(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { chSettings.tag(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case TYPE:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { chSettings.type(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { chSettings.type(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case UNBAN:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { admin.unban(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.unban(player, args[0], plugin.getChannel(player).getName()); }
				break;
				
			case UNFOLLOW:
				try { chSettings.unfollow(player, args[0]); } catch (IndexOutOfBoundsException e) { invalidArgLength(player, command); }
				break;
				
			case UNMUTE:
				if (args.length < 0) { invalidArgLength(player, command); return; }
				try { admin.unmute(player, args[0], args[1]); } catch (IndexOutOfBoundsException e) { admin.unmute(player, args[0], plugin.getChannel(player).getName()); }
				break;
			}
			
			return;
			
		} else {
			if (runCommands(player, cmd, args))
				return;
		}
		
		plugin.sendWarning(player, "Invalid Command");
		plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
	}
	
	public boolean runCommands(Player player, String cmd, String[] args) {
		for (Command command : plugin.getCommands()) {
			if (command.getAliases().contains(cmd)) {
				if (command.execute(player, command, args))
					return true;
			}
		}
		
		return false;
	}
	
	public enum Commands {
		ACCEPT("Accept", new String[] { "accept" }, "Accepts the channel join invitation and joins the channel", "accept [channel]"),
		ADD("Add", new String[] { "add" }, "Whitelists the player for the channel", "add [player] <channel>"),
		BAN("Ban", new String[] { "ban" }, "Bans the player from the channel", "ban [player] <channel>"),
		BROADCAST("Broadcast", new String[] { "broadcast" }, "Broadcasts the message globally", "broadcast [message]"),
		CHCOLOUR("ChColour", new String[] { "chcolour", "chcolor" }, "Changes the chat display colour of the channel", "chcolour [colourcode] <channel>"),
		COLOURCODES("ColourCodes", new String[] { "colourcodes", "colorcodes", "colours", "colors", "codes" }, "Lists out avalable colour codes and respective colours", "colourcodes"),
		COMMANDS("Commands", new String[] { "commands" }, "Shows the Command List", "commands <page/command>"),
		CONVERT("Convert", new String[] { "convert" }, "Toggles colour code converting", "convert <channel>"),
		CREATE("Create", new String[] { "create" }, "Creates a new channel", "create [channel]"),
		DECLINE("Decline", new String[] { "decline" }, "Declines the channel join invitation", "decline [channel]"),
		DELETE("Delete", new String[] { "delete" }, "Deletes the channel", "delete [channel]"),
		DEMOTE("Demote", new String[] { "demote" }, "Demotes the player of the channel", "demote [player] <channel>"),
		FOLLOW("Follow", new String[] { "follow" }, "Follows the channel", "follow [channel]"),
		FORCE("Force", new String[] { "force" }, "Forces the player to join the channel", "force [player] <channel>"),
		INFO("Info", new String[] { "info" }, "Gets the participants and followers of the channel", "info <channel>"),
		INVITE("Invite", new String[] { "invite" }, "Invites the player to join the channel", "invite [player] <channel>"),
		JOIN("Join", new String[] { "join" }, "Joins the channel", "join [channel] <password>"),
		KICK("Kick", new String[] { "kick" }, "Kicks the player from the channel", "kick [player] <channel>"),
		LIST("List", new String[] { "list" }, "Lists all channels you have access to", "list"),
		MUTE("Mute", new String[] { "mute" }, "Mutes the player on the channel", "mute [player] <channel>"),
		NCOLOUR("NColour", new String[] { "ncolour", "ncolor" }, "Changes the name display colour of the channel", "ncolour [colourcode] <channel>"),
		PASSWORD("Password", new String[] { "password" }, "Sets the password of the channel", "password [password] <channel>"),
		PROMOTE("Promote", new String[] { "promote" }, "Promotes the player of the channel", "promote [player] <channel>"),
		RELOAD("Reload", new String[] { "reload" }, "Reloads the config", "reload"),
		SILENCE("Silence", new String[] { "silence" }, "Silences the channel/server", "silence <channel>"),
		TAG("Tag", new String[] { "tag" }, "Sets the tag of the channel", "tag [tag] <channel>"),
		TYPE("Type", new String[] { "type" }, "Sets the type of the channel", "type [type] <channel>"),
		UNBAN("Unban", new String[] { "unban" }, "Unbans the player from the channel", "unban [player] <channel>"),
		UNFOLLOW("Unfollow", new String[] { "unfollow" }, "Unfollows the channel", "unfollow [channel]"),
		UNMUTE("Unmute", new String[] { "unmute" }, "Unmutes the player on the channel", "unmute [player] <channel>");
		
		private String name;
		private String[] names;
		private String description;
		private String usage;
		
		private static Map<String, Commands> NAME_MAP = new HashMap<String, Commands>();
		private static Map<String, String> DESCRIPTION_MAP = new HashMap<String, String>();
		private static Map<String, String> USAGE_MAP = new HashMap<String, String>();
		
		private Commands(String name, String[] names, String description, String usage) {
			this.name = name;
			this.names = names;
			this.description = description;
			this.usage = usage;
		}
		
		static {
			for (Commands command : EnumSet.allOf(Commands.class)) {
				for (String name : command.names) {
					NAME_MAP.put(name, command);
					DESCRIPTION_MAP.put(name, command.description);
					USAGE_MAP.put(name, command.usage);
				}
			}
		}
		
		public static Commands fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String[] getAliases() {
			return names;
		}
		
		public String getName() {
			return name;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getUsage() {
			return usage;
		}
	}
}