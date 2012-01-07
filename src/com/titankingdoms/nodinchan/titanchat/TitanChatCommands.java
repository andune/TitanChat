package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TitanChatCommands implements CommandExecutor {
	
	private TitanChat plugin;
	private ChannelManager chManager;
	
	public TitanChatCommands(TitanChat plugin) {
		this.plugin = plugin;
		this.chManager = new ChannelManager(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		String channel = plugin.getChannel(player);
		
		// TitanChat Commands
		
		if (label.equalsIgnoreCase("titanchat") || label.equalsIgnoreCase("tc")) {
			if (args.length == 1) {
				
				// /titanchat list
				// Lists all channels you have access to
				
				if (args[0].equalsIgnoreCase("list")) {
					List<String> channels = new ArrayList<String>();
					
					for (String channelName : plugin.getConfig().getConfigurationSection("channels").getKeys(false)) {
						if (player.hasPermission("TitanChat.admin")) {
							channels.add(channelName);
							
						} else {
							if (plugin.isAdmin(player, channelName) || plugin.isMember(player, channelName)) {
								channels.add(channelName);
							}
						}
					}
					
					plugin.sendInfo(player, "Channel List: " + plugin.createList(channels));
					return true;
				}
				
			} else if (args.length == 2) {
				
				// /titanchat accept [channel]
				// Accepts invitation to join the chat
				
				if (args[0].equalsIgnoreCase("accept")) {
					if (plugin.isInvited(player, args[1])) {
						plugin.accept(player, args[1]);
						return true;
						
					} else {
						plugin.sendWarning(player, "You did not receive any invitations from this channel");
						return true;
					}
				}
				
				// /titanchat allowcolours [true/false]
				// Sets whether the channel
				
				if (args[0].equalsIgnoreCase("allowcolors") || args[0].equalsIgnoreCase("allowcolours")) {
					if (player.hasPermission("TitanChat.admin")) {
						if (args[1].equalsIgnoreCase("true")) {
							chManager.setAllowColours(plugin.getChannel(player), true);
							return true;
							
						} else if (args[1].equalsIgnoreCase("false")) {
							chManager.setAllowColours(plugin.getChannel(player), false);
							return true;
							
						} else {
							plugin.sendWarning(player, "True or False?");
							return true;
						}
						
					} else {
						plugin.sendWarning(player, "You do not have permission to change the state of this channel");
						return true;
					}
				}
				
				// /titanchat ban [player]
				// Bans the player from the channel
				
				if (args[0].equalsIgnoreCase("ban")) {
					if (plugin.isAdmin(player)) {
						plugin.ban(plugin.getServer().getPlayer(args[1]), plugin.getChannel(player));
						chManager.ban(args[1], plugin.getChannel(player));
						return true;
						
					} else {
						plugin.sendWarning(player, "You do not have permission to ban on this channel");
						return true;
					}
				}
				
				// /titanchat colour [colour]
				// Sets the chat colour of the channel
				
				if (args[0].equalsIgnoreCase("colour") || args[0].equalsIgnoreCase("color")) {
					if (plugin.isAdmin(player)) {
						if (ChatColor.valueOf(args[1]) != null) {
							chManager.setColour(plugin.getChannel(player), args[1].toUpperCase());
							return true;
							
						} else {
							plugin.sendWarning(player, "Invalid Colour");
							return true;
						}
						
					} else {
						plugin.sendWarning(player, "You do not have permission to change the channel colour");
						return true;
					}
				}
				
				// /titanchat create [channel]
				// Creates a new channel
				
				if (args[0].equalsIgnoreCase("create")) {
					if (!player.hasPermission("TitanChat.create")) {
						plugin.sendWarning(player, "You do not have permission to create channels");
						return true;
					}
					
					if (plugin.channelExist(args[1])) {
						plugin.sendWarning(player, "Channel already exists");
						return true;
					}
					
					plugin.createChannel(player, args[1]);
					chManager.createChannel(player.getName(), args[1]);
					return true;
				}
				
				// /titanchat decline [channel]
				// Declines invitation to join the chat
				
				if (args[0].equalsIgnoreCase("decline")) {
					if (plugin.isInvited(player, args[1])) {
						plugin.decline(player, args[1]);
						return true;
						
					} else {
						plugin.sendWarning(player, "You did not receive any invitations from this channel");
						return true;
					}
				}
				
				// /titanchat demote [player]
				// Demotes the player on the channel
				
				if (args[0].equalsIgnoreCase("demote")) {
					if (plugin.isAdmin(player)) {
						plugin.demote(plugin.getPlayer(args[1]), plugin.getChannel(player));
						chManager.demote(args[1], plugin.getChannel(player));
						return true;
						
					} else {
						plugin.sendWarning(player, "You do not have permission to demote players on this channel");
						return true;
					}
				}
				
				// /titanchat invite [player]
				// Invites the player to chat on the channel
				
				if (args[0].equalsIgnoreCase("invite")) {
					if (plugin.isAdmin(player)) {
						plugin.invite(plugin.getServer().getPlayer(args[1]), plugin.getChannel(player));
						return true;
						
					} else {
						plugin.sendWarning(player, "You do not have permission to invite players");
						return true;
					}
				}
				
				// /titanchat join [channel]
				// Joins the channel
				
				if (args[0].equalsIgnoreCase("join")) {
					if (plugin.isPublic(args[1])) {
						if (plugin.channelExist(args[1])) {
							plugin.channelSwitch(player, plugin.getChannel(player), args[1]);
							return true;
							
						} else {
							plugin.sendWarning(player, "No such channel");
							return true;
						}
						
					} else {
						if (plugin.isAdmin(player) || plugin.isMember(player)) {
							plugin.channelSwitch(player, plugin.getChannel(player), args[1]);
							return true;
							
						} else {
							plugin.sendWarning(player, "You do not have permission to join this channel");
							return true;
						}
					}
				}
				
				// /titanchat kick [player]
				// Kicks the player from the channel
				
				if (args[0].equalsIgnoreCase("kick")) {
					if (plugin.isAdmin(player)) {
						plugin.kick(plugin.getServer().getPlayer(args[1]), plugin.getChannel(player));
						return true;
						
					} else {
						plugin.sendWarning(player, "You do not have permission to kick on this channel");
						return true;
					}
				}
				
				// /titanchat prefix [prefix]
				// Sets the channel tag
				
				if (args[0].equalsIgnoreCase("prefix")) {
					if (plugin.isAdmin(player)) {
						chManager.setPrefix(plugin.getChannel(player), args[1]);
						return true;
						
					} else {
						plugin.sendWarning(player, "You do not have permission to change channel tags on this channel");
						return true;
					}
				}
				
				// /titanchat promote [player]
				// Promotes the player on the channel
				
				if (args[0].equalsIgnoreCase("promote")) {
					if (plugin.isAdmin(player)) {
						plugin.promote(plugin.getPlayer(args[1]), plugin.getChannel(player));
						chManager.promote(args[1], plugin.getChannel(player));
						return true;
						
					} else {
						plugin.sendWarning(player, "You do not have permission to promote players on this channel");
						return true;
					}
				}
				
				// /titanchat public [true/false]
				// Sets the state of the channel
				
				if (args[0].equalsIgnoreCase("public")) {
					if (plugin.isAdmin(player)) {
						if (args[1].equalsIgnoreCase("true")) {
							chManager.setPublic(channel, true);
							return true;
							
						} else if (args[1].equalsIgnoreCase("false")) {
							chManager.setPublic(channel, false);
							return true;
							
						} else {
							plugin.sendWarning(player, "True or False?");
							return true;
						}
						
					} else {
						plugin.sendWarning(player, "You do not have permission to change the state of this channel");
						return true;
					}
				}
				
			} else {
				plugin.sendWarning(player, "Invalid Argument Length");
				return false;
			}
		}
		return false;
	}
}
