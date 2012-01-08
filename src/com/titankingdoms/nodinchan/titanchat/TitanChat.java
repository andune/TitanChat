package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.titankingdoms.nodinchan.titanchat.TitanChatCommands.Commands;

public class TitanChat extends JavaPlugin {
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	private String defaultChannel = "";
	
	private File channelConfigFile = null;
	private FileConfiguration channelConfig = null;
	
	private Map<String, List<Player>> channelAdmins = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> channelMembers = new HashMap<String, List<Player>>();
	private Map<Player, String> channel = new HashMap<Player, String>();
	private Map<Player, List<String>> invitations = new HashMap<Player, List<String>>();
	private Map<String, List<Player>> invited = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> participants = new HashMap<String, List<Player>>();
	
	// Accept the channel join request
	
	public void accept(Player player, String newCh) {
		String channel = getChannel(player);
		channelSwitch(player, channel, newCh);
		
		List<Player> players = new ArrayList<Player>();
		
		for (Player invitedPlayer : invited.get(newCh)) {
			players.add(invitedPlayer);
		}
		
		players.remove(player);
		
		if (players.isEmpty()) {
			invited.remove(newCh);
			
		} else {
			invited.put(newCh, players);
		}
		
		List<String> channels = new ArrayList<String>();
		
		for (String channelInvitation : invitations.get(player)) {
			channels.add(channelInvitation);
		}
		
		channels.remove(newCh);
		
		if (channels.isEmpty()) {
			invitations.remove(player);
			
		} else {
			invitations.put(player, channels);
		}
	}
	
	// Assigning an Admin to a channel
	
	public void assignAdmin(Player player, String channelName) {
		if (channelAdmins.isEmpty() || channelAdmins.get(channelName) == null) {
			List<Player> admins = new ArrayList<Player>();
			admins.add(player);
			channelAdmins.put(channelName, admins);
			
		} else {
			List<Player> admins = channelAdmins.get(channelName);
			admins.add(player);
			channelAdmins.put(channelName, admins);
		}
		
		sendInfo(player, "You are now an Admin of " + channelName);
	}
	
	// Banning a Member from a channel
	
	public void ban(Player player, String channelName) {
		if (isAdmin(player)) {
			List<Player> admins = new ArrayList<Player>();
			
			for (Player admin : channelAdmins.get(channelName)) {
				admins.add(admin);
			}
			
			admins.remove(player);
			
			if (admins.isEmpty()) {
				channelAdmins.remove(channelName);
				
			} else {
				channelAdmins.put(channelName, admins);
			}
			
		} else if (isMember(player)) {
			List<Player> members = new ArrayList<Player>();
			
			for (Player member : channelMembers.get(channelName)) {
				members.add(member);
			}
			
			members.remove(player);
			
			if (members.isEmpty()) {
				channelMembers.remove(channelName);
				
			} else {
				channelMembers.put(channelName, members);
			}
		}
		
		channelSwitch(player, getChannel(player), getDefaultChannel());
		sendWarning(player, "You have been banned from " + channelName);
	}
	
	// Check if a channel exists
	
	public boolean channelExist(String channelName) {
		return (getConfig().getConfigurationSection("channels").getKeys(false).contains(channelName));
	}
	
	// Switching channels
	
	public void channelSwitch(Player player, String oldCh, String newCh) {
		leaveChannel(player, oldCh);
		enterChannel(player, newCh);
	}
	
	// Creating channels
	
	public void createChannel(Player player, String channelName) {
		assignAdmin(player, channelName);
		channelSwitch(player, getChannel(player), channelName);
		sendInfo(player, "You have created " + channelName + " channel");
	}
	
	public boolean canAccess(Player player, String channelName) {
		if (player.hasPermission("TitanChat.access.*"))
			return true;
		
		if (player.hasPermission("TitanChat.access." + channelName))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		if (isMember(player, channelName))
			return true;
		
		return false;
	}
	
	public boolean canBan(Player player, String channelName) {
		if (player.hasPermission("TitanChat.ban.*"))
			return true;
		
		if (player.hasPermission("TitanChat.ban." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canDemote(Player player, String channelName) {
		if (player.hasPermission("TitanChat.demote.*"))
			return true;
		
		if (player.hasPermission("TitanChat.demote." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canInvite(Player player, String channelName) {
		if (player.hasPermission("TitanChat.invite.*"))
			return true;
		
		if (player.hasPermission("TitanChat.invite." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canKick(Player player, String channelName) {
		if (player.hasPermission("TitanChat.kick.*"))
			return true;
		
		if (player.hasPermission("TitanChat.kick." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canPromote(Player player, String channelName) {
		if (player.hasPermission("TitanChat.promote.*"))
			return true;
		
		if (player.hasPermission("TitanChat.promote." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canWhitelist(Player player, String channelName) {
		if (player.hasPermission("TitanChat.whitelist.*"))
			return true;
		
		if (player.hasPermission("TitanChat.whitelist." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public String createList(List<String> channels) {
		StringBuilder str = new StringBuilder();
		
		for (String item : channels) {
			if (str.length() > 0) {
				str.append(", ");
			}
			
			str.append(item);
		}
		
		return str.toString();
	}
	
	// Declining a channel join request
	
	public void decline(Player player, String newCh) {
		List<Player> players = new ArrayList<Player>();
		
		for (Player invitedPlayer : invited.get(newCh)) {
			players.add(invitedPlayer);
		}
		
		players.remove(player);
		
		if (players.isEmpty()) {
			invited.remove(newCh);
			
		} else {
			invited.put(newCh, players);
		}
		
		List<String> channels = new ArrayList<String>();
		
		for (String channelInvitation : invitations.get(player)) {
			channels.add(channelInvitation);
		}
		
		channels.remove(newCh);
		
		if (channels.isEmpty()) {
			invitations.remove(player);
			
		} else {
			invitations.put(player, channels);
		}
	}
	
	public void deleteChannel(Player player, String channelName) {
		if (getParticipants(channelName) != null) {
			List<Player> players = new ArrayList<Player>();
			
			for (Player participant : getParticipants(channelName)) {
				players.add(participant);
			}
			
			for (Player participant : players) {
				kick(participant, channelName);
				sendWarning(participant, channelName + " has been deleted");
			}
			
			participants.remove(channelName);
		}
		
		if (getChannelAdmins(channelName) != null) {
			channelAdmins.remove(channelName);
		}
		
		if (getChannelMembers(channelName) != null) {
			channelMembers.remove(channelName);
		}
	}
	
	// Demoting a player on a channel
	
	public void demote(Player player, String channelName) {
		List<Player> admins = new ArrayList<Player>();
		
		for (Player admin : channelAdmins.get(channelName)) {
			admins.add(admin);
		}
		
		admins.remove(player);
		
		if (admins.isEmpty()) {
			channelAdmins.remove(channelName);
			
		} else {
			channelAdmins.put(channelName, admins);
		}
		
		whitelistMember(player, channelName);
		sendInfo(player, "You have been demoted in " + channelName);
	}
	
	// Entering channels
	
	public void enterChannel(Player player, String channelName) {
		if (getParticipants(channelName) != null) {
			for (Player receiver : getParticipants(channelName)) {
				sendInfo(receiver, player.getDisplayName() + " has joined the channel");
			}
		}
		
		channel.put(player, channelName);
		
		if (participants.isEmpty() || participants.get(channelName).isEmpty()) {
			List<Player> players = new ArrayList<Player>();
			players.add(player);
			participants.put(channelName, players);
			
		} else {
			List<Player> players = participants.get(channelName);
			players.add(player);
			participants.put(channelName, players);
		}
	}
	
	// Gets the channel the player is in
	
	public String getChannel(Player player) {
		return channel.get(player);
	}
	
	// Gets the admins of the channel
	
	public List<Player> getChannelAdmins(String channelName) {
		return channelAdmins.get(channelName);
	}
	
	// Gets the player config of channels
	
	public FileConfiguration getChannelConfig() {
		if (channelConfig == null) {
			reloadChannelConfig();
		}
		
		return channelConfig;
	}
	
	// Gets the members of the channel
	
	public List<Player> getChannelMembers(String channelName) {
		return channelMembers.get(channelName);
	}
	
	// Gets the name of the default channel
	
	public String getDefaultChannel() {
		for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
			if (getConfig().get("channels." + channel + ".default") != null) {
				defaultChannel = channel;
			}
		}
		return defaultChannel;
	}
	
	// Gets the participants of a channel
	
	public List<Player> getParticipants(String channelName) {
		return participants.get(channelName);
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	public void infoLog(String info) {
		log.info("[" + this + "] " + info);
	}
	
	// Inviting a player to join a channel
	
	public void invite(Player player, String channelName) {
		if (invited.isEmpty()) {
			List<Player> players = new ArrayList<Player>();
			players.add(player);
			invited.put(channelName, players);
			
		} else {
			List<Player> players = invited.get(channelName);
			players.add(player);
			invited.put(channelName, players);
		}
		
		if (invitations.isEmpty()) {
			List<String> channels = new ArrayList<String>();
			channels.add(channelName);
			invitations.put(player, channels);
			
		} else {
			List<String> channels = invitations.get(player);
			channels.add(channelName);
			invitations.put(player, channels);
		}
		
		sendInfo(player, "You have been invited to chat on " + channelName);
	}
	
	// Check if the player is an admin of that channel
	
	public boolean isAdmin(Player player) {
		if (player.hasPermission("TitanChat.admin"))
			return true;
		
		List<Player> players = new ArrayList<Player>();
		
		if (!channelAdmins.get(getChannel(player)).isEmpty()) {
			players = channelAdmins.get(getChannel(player));
			
			if (players.contains(player))
				return true;
		}
		
		return false;
	}
	
	public boolean isAdmin(Player player, String channelName) {
		if (player.hasPermission("TitanChat.admin"))
			return true;
		
		List<Player> players = new ArrayList<Player>();
		
		if (!channelAdmins.get(channelName).isEmpty()) {
			players = channelAdmins.get(channelName);
			
			if (players.contains(player))
				return true;
		}
		
		return false;
	}
	
	// Check if the player is invited
	
	public boolean isInvited(Player player, String channelName) {
		if (invited.isEmpty())
			return false;
		
		else if (invited.get(channelName).isEmpty())
			return false;
		
		else if (invited.get(channelName).contains(player))
			return true;
		
		else
			return false;
	}
	
	// Check if the channel is public
	
	public boolean isPublic(String channelName) {
		if (getConfig().get("channels." + channelName + ".public") != null) {
			if (getConfig().getBoolean("channels." + channelName + ".public"))
				return true;
		}
		
		if (channelName == getDefaultChannel())
			return true;
		
		return false;
	}
	
	// Check if the player is a member of that channel
	
	public boolean isMember(Player player) {
		List<Player> players = new ArrayList<Player>();
		
		if (!channelMembers.get(getChannel(player)).isEmpty()) {
			players = channelMembers.get(getChannel(player));
			
			if (players.contains(player))
				return true;
		}
		
		return false;
	}
	
	public boolean isMember(Player player, String channelName) {
		if (player.hasPermission("TitanChat.access." + channelName))
			return true;
		
		List<Player> players = new ArrayList<Player>();
		
		if (!channelMembers.get(channelName).isEmpty()) {
			players = channelMembers.get(channelName);
			
			if (players.contains(player))
				return true;
		}
		
		return false;
	}
	
	// Kicks a player from a channel
	
	public void kick(Player player, String channelName) {
		channelSwitch(player, channelName, getDefaultChannel());
		sendWarning(player, "You have been kicked from " + channelName);
	}
	
	// Leaves the channel
	
	public void leaveChannel(Player player, String channelName) {
		channel.remove(player);
		List<Player> players = participants.get(channelName);
		players.remove(player);
		
		if (players.isEmpty()) {
			participants.remove(channelName);
			
		} else {
			participants.put(channelName, players);
		}
		
		if (getParticipants(channelName) != null) {
			for (Player receiver : getParticipants(channelName)) {
				sendInfo(receiver, player.getDisplayName() + " has left the channel");
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		// TitanChat Commands
		
		if (label.equalsIgnoreCase("titanchat") || label.equalsIgnoreCase("tc")) {
			if (args.length == 1) {
				
				// /titanchat list
				// Lists out the channels you have access to
				
				if (args[0].equalsIgnoreCase("list")) {
					List<String> channels = new ArrayList<String>();
					
					for (String channelName : getConfig().getConfigurationSection("channels").getKeys(false)) {
						if (canAccess(player, channelName)) {
							channels.add(channelName);
						}
					}
					
					sendInfo(player, "Channel List: " + createList(channels));
					return true;
					
				} else {
					sendWarning(player, "Invalid Command");
				}
				
			} else if (args.length == 2) {
				for (Commands command : Commands.values()) {
					if (command.toString().equalsIgnoreCase(args[0])) {
						new TitanChatCommands(this).onCommand(player, args[0], args[1]);
						return true;
					}
				}
				
				sendWarning(player, "Invalid Command");
			}
			
			sendWarning(player, "Invalid Argument Length");
		}
		
		return false;
	}
	
	@Override
	public void onDisable() {
		infoLog("is now disabled");
	}
	
	@Override
	public void onEnable() {
		infoLog("is now enabling...");
		
		if (getDefaultChannel() == null) {
			log.warning("[" + this + "] Default channel not defined");
		}
		
		File config = new File(getDataFolder() + "config.yml");
		File channelConfig = new File(getDataFolder() + "channels.yml");
		
		if (!config.exists()) {
			infoLog("Loading default config");
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		if (!channelConfig.exists()) {
			infoLog("Loading default channel players config");
			getChannelConfig().options().copyDefaults(true);
			saveChannelConfig();
		}
		
		prepareChannelCommunities();
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.PLAYER_CHAT, new TitanChatPlayerListener(this), Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_JOIN, new TitanChatPlayerListener(this), Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_QUIT, new TitanChatPlayerListener(this), Priority.Highest, this);
		
		infoLog("is now enabled");
	}
	
	// Prepares the list of Admins and Members
	
	public void prepareChannelCommunities() {
		for (String channel : getChannelConfig().getConfigurationSection("channels").getKeys(false)) {
			List<String> adminNames = new ArrayList<String>();
			List<String> memberNames = new ArrayList<String>();
			
			if (getChannelConfig().getStringList("channels." + channel + ".admins") != null) {
				adminNames = getChannelConfig().getStringList("channels." + channel + ".admins");
			}
			
			if (getChannelConfig().getStringList("channels." + channel + ".members") != null) {
				memberNames = getChannelConfig().getStringList("channels." + channel + ".members");
			}
			
			List<Player> admins = new ArrayList<Player>();
			List<Player> members = new ArrayList<Player>();
			
			if (!adminNames.isEmpty()) {
				for (String name : adminNames) {
					admins.add(getPlayer(name));
				}
				
				channelAdmins.put(channel, admins);
				
				infoLog("Admins of " + channel + " found");
			}
			
			if (!memberNames.isEmpty()) {
				for (String name : memberNames) {
					members.add(getPlayer(name));
				}
				
				channelMembers.put(channel, members);
				
				infoLog("Members of " + channel + " found");
			}
		}
		
		infoLog("Chat Communities Loaded");
	}
	
	// Promote a player on a channel
	
	public void promote(Player player, String channelName) {
		List<Player> members = new ArrayList<Player>();
		
		for (Player member : channelMembers.get(channelName)) {
			members.add(member);
		}
		
		members.remove(player);
		
		if (members.isEmpty()) {
			channelMembers.remove(channelName);
			
		} else {
			channelMembers.put(channelName, members);
		}
		
		assignAdmin(player, channelName);
		sendInfo(player, "You have been promoted in " + channelName);
	}
	
	// Reloads the player config of the channels
	
	public void reloadChannelConfig() {
		if (channelConfigFile == null) {
			channelConfigFile = new File(getDataFolder(), "channels.yml");
		}
		
		channelConfig = YamlConfiguration.loadConfiguration(channelConfigFile);
		
		InputStream defConfigStream = getResource("channels.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			channelConfig.setDefaults(defConfig);
		}
	}
	
	// Saves the player config of the channels
	
	public void saveChannelConfig() {
		if (channelConfig == null || channelConfigFile == null)
			return;
		
		try {
			channelConfig.save(channelConfigFile);
		} catch (IOException e) {
			log.severe("Could not save config to " + channelConfigFile);
		}
	}
	
	public void sendInfo(Player player, String info) {
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + info);
	}
	
	public void sendWarning(Player player, String warning) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + warning);
	}
	
	// Whitelisting a Member on a channel
	
	public void whitelistMember(Player player, String channelName) {
		if (channelMembers.isEmpty() || channelMembers.get(channelName) == null) {
			List<Player> members = new ArrayList<Player>();
			members.add(player);
			channelMembers.put(channelName, members);
			
		} else {
			List<Player> members = channelMembers.get(channelName);
			members.add(player);
			channelMembers.put(channelName, members);
		}
		
		sendInfo(player, "You are now a Member of " + channelName);
	}
}
