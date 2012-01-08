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
		
		// TitanChat Commands
		
		if (label.equalsIgnoreCase("titanchat") || label.equalsIgnoreCase("tc")) {
			if (args.length == 1) {
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
					
				} else {
					plugin.sendWarning(player, "Invalid Command");
				}
				
			} else if (args.length == 2) {
				for (Commands command : Commands.values()) {
					if (command.toString().equalsIgnoreCase(args[0])) {
						onCommand(player, args[0], args[1]);
						return true;
					}
				}
				
				plugin.sendWarning(player, "Invalid Command");
				
			}
			
			plugin.sendWarning(player, "Invalid Argument Length");
			
		}
		return false;
	}
	
	public void onCommand(Player player, String action, String arg) {
		String channel = plugin.getChannel(player);
		
		switch (Commands.valueOf(action.toUpperCase())) {
		
		case ACCEPT:
			if (plugin.isInvited(player, arg)) {
				plugin.accept(player, arg);
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			break;
			
		case ADD:
			if (plugin.isAdmin(player)) {
				if (arg.contains(",")) {
					for (String newMember : arg.split(",")) {
						plugin.whitelistMember(plugin.getPlayer(newMember.replace(" ", "")), channel);
						chManager.whitelistMember(newMember.replace(" ", ""), channel);
					}
					
				} else {
					plugin.whitelistMember(plugin.getPlayer(arg), channel);
					chManager.whitelistMember(arg, channel);
				}
			}
			break;
			
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
			
		case BAN:
			if (plugin.isAdmin(player)) {
				plugin.ban(plugin.getServer().getPlayer(arg), channel);
				chManager.ban(arg, channel);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to ban on this channel");
			}
			break;
			
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
			
		case DECLINE:
			if (plugin.isInvited(player, arg)) {
				plugin.decline(player, arg);
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			break;
			
		case DEMOTE:
			if (plugin.isAdmin(player)) {
				plugin.demote(plugin.getPlayer(arg), channel);
				chManager.demote(plugin.getPlayer(arg).getName(), channel);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to demote players on this channel");
			}
			break;
			
		case INVITE:
			if (plugin.isAdmin(player)) {
				plugin.invite(plugin.getServer().getPlayer(arg), channel);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to invite players");
			}
			break;
			
		case JOIN:
			if (plugin.isPublic(arg)) {
				if (plugin.channelExist(arg)) {
					plugin.channelSwitch(player, channel, arg);
					
				} else {
					plugin.sendWarning(player, "No such channel");
				}
				
			} else {
				if (plugin.isAdmin(player, arg) || plugin.isMember(player, arg)) {
					plugin.channelSwitch(player, channel, arg);
					
				} else {
					plugin.sendWarning(player, "You do not have permission to join this channel");
				}
			}
			break;
			
		case KICK:
			if (plugin.isAdmin(player)) {
				plugin.kick(plugin.getPlayer(arg), channel);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to kick on this channel");
			}
			break;
			
		case PREFIX:
			if (plugin.isAdmin(player)) {
				chManager.setPrefix(channel, arg);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to change channel tags on this channel");
			}
			break;
			
		case PROMOTE:
			if (plugin.isAdmin(player)) {
				plugin.promote(plugin.getPlayer(arg), channel);
				chManager.promote(plugin.getPlayer(arg).getName(), channel);
				
			} else {
				plugin.sendWarning(player, "You do not have permission to promote players on this channel");
			}
			break;
			
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
		DEMOTE,
		INVITE,
		JOIN,
		KICK,
		PREFIX,
		PROMOTE,
		PUBLIC
	}
}
