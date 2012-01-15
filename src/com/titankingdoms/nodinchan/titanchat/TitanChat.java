package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.titankingdoms.nodinchan.titanchat.TitanChatCommands.Commands;

import de.bananaco.permissions.Permissions;

public class TitanChat extends JavaPlugin {
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	private String defaultChannel = "";
	private String staffChannel = "";
	
	private File channelConfigFile = null;
	private FileConfiguration channelConfig = null;
	
	private boolean silence = false;
	
	private Map<String, List<Player>> channelAdmins = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> channelMembers = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> channelBans = new HashMap<String, List<Player>>();
	private Map<Player, String> channel = new HashMap<Player, String>();
	private Map<Player, List<String>> invitations = new HashMap<Player, List<String>>();
	private Map<String, List<Player>> invited = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> muted = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> participants = new HashMap<String, List<Player>>();
	private Map<String, Boolean> silenced = new HashMap<String, Boolean>();
	
	private Permission permission;
	private Chat chat;
	
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
		
		if (channelBans.isEmpty() || channelBans.get(channelName) == null) {
			List<Player> banned = new ArrayList<Player>();
			banned.add(player);
			channelBans.put(channelName, banned);
			
		} else {
			List<Player> banned = channelBans.get(channelName);
			banned.add(player);
			channelBans.put(channelName, banned);
		}
		
		channelSwitch(player, getChannel(player), getDefaultChannel());
		sendWarning(player, "You have been banned from " + channelName);
	}
	
	public boolean canAccess(Player player, String channelName) {		
		if (has(player, "TitanChat.access.*"))
			return true;
		
		if (has(player, "TitanChat.access." + channelName))
			return true;
		
		if (isAdmin(player, channelName))
			return true;
		
		if (isMember(player, channelName))
			return true;
		
		return false;
	}
	
	public boolean canBan(Player player, String channelName) {
		if (has(player, "TitanChat.ban.*"))
			return true;
		
		if (has(player, "TitanChat.ban." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canKick(Player player, String channelName) {
		if (has(player, "TitanChat.kick.*"))
			return true;
		
		if (has(player, "TitanChat.kick." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canMute(Player player) {
		if (has(player, "TitanChat.silence"))
			return true;
		
		if (has(player, "TitanChat.mute"))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
	}
	
	public boolean canRank(Player player, String channelName) {
		if (has(player, "TitanChat.rank.*"))
			return true;
		
		if (has(player, "TitanChat.rank." + channelName))
			return true;
		
		if (isAdmin(player))
			return true;
		
		return false;
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
		silenced.put(channelName, false);
		sendInfo(player, "You have created " + channelName + " channel");
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
	
	// Deleting channels
	
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
		
		if (channelAdmins.get(channelName) != null) {
			channelAdmins.remove(channelName);
		}
		
		if (channelMembers.get(channelName) != null) {
			channelMembers.remove(channelName);
		}
		
		if (channelBans.get(channelName) != null) {
			channelBans.remove(channelName);
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
	
	// Gets the player config of channels
	
	public FileConfiguration getChannelConfig() {
		if (channelConfig == null) {
			reloadChannelConfig();
		}
		
		return channelConfig;
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
	
	// Gets the prefix of the player
	
	public String getPrefix(Player player) {
		String prefix = "";
		
		if (vault() && chat != null) {
			prefix = chat.getPlayerPrefix(player);
			
		} else if (getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			
			if (user != null) {
				prefix = user.getPrefix();
			}
			
		} else if (getServer().getPluginManager().getPlugin("bPermissions") != null) {
			prefix = Permissions.getInfoReader().getPrefix(player);
		}
		
		return prefix;
	}
	
	// Gets the name of the staff channel
	
	public String getStaffChannel() {
		for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
			if (getConfig().get("channels." + channel + ".staff") != null) {
				staffChannel = channel;
			}
		}
		return staffChannel;
	}
	
	// Gets the suffix of the player
	
	public String getSuffix(Player player) {
		String suffix = "";
		
		if (vault() && chat != null) {
			suffix = chat.getPlayerSuffix(player);
			
		} else if (getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
			
			if (user != null) {
				suffix = user.getSuffix();
			}
			
		} else if (getServer().getPluginManager().getPlugin("bPermissions") != null) {
			suffix = Permissions.getInfoReader().getSuffix(player);
		}
		
		return suffix;
	}
	
	// Check for permission
	
	public boolean has(Player player, String permissionNode) {
		if (permission != null) {
			return permission.has(player, permissionNode);
		}
		
		return player.hasPermission(permissionNode);
	}
	
	// Check for Voice
	
	public boolean hasVoice(Player player) {
		return has(player, "TitanChat.voice");
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
	
	// Responding to invites
	
	public void inviteResponse(Player player, String channelName, boolean accept) {
		List<Player> players = new ArrayList<Player>();
		
		for (Player invitedPlayer : invited.get(channelName)) {
			players.add(invitedPlayer);
		}
		
		players.remove(player);
		
		if (players.isEmpty()) {
			invited.remove(channelName);
			
		} else {
			invited.put(channelName, players);
		}
		
		List<String> channels = new ArrayList<String>();
		
		for (String channelInvitation : invitations.get(player)) {
			channels.add(channelInvitation);
		}
		
		channels.remove(channelName);
		
		if (channels.isEmpty()) {
			invitations.remove(player);
			
		} else {
			invitations.put(player, channels);
		}
		
		if (accept) {
			String channel = getChannel(player);
			channelSwitch(player, channel, channelName);
		}
	}
	
	// Check if the player is an admin of that channel
	
	public boolean isAdmin(Player player) {
		if (isStaff(player))
			return true;
		
		List<Player> players = new ArrayList<Player>();
		
		if (!channelAdmins.get(getChannel(player)).isEmpty()) {
			players = channelAdmins.get(getChannel(player));
		}
		
		if (players.contains(player))
			return true;
		
		return false;
	}
	
	public boolean isAdmin(Player player, String channelName) {
		if (isStaff(player))
			return true;
		
		List<Player> players = new ArrayList<Player>();
		
		if (!channelAdmins.get(channelName).isEmpty()) {
			players = channelAdmins.get(channelName);
		}
		
		if (players.contains(player))
			return true;
		
		return false;
	}
	
	// Check if the player is banned
	
	public boolean isBanned(Player player, String channelName) {
		List<Player> players = new ArrayList<Player>();
		
		if (!channelBans.isEmpty()) {
			players = channelBans.get(channelName);
		}
		
		if (players.contains(player))
			return true;
		
		return false;
	}
	
	// Checks if the channel broadcasts to all channels
	
	public boolean isGlobal(String channelName) {
		for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
			if (getConfig().get("channels." + channel + ".global") != null) {
				return getConfig().getBoolean("channels." + channel + "global");
			}
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
		if (getStaffChannel() == channelName)
			return false;
		
		if (getDefaultChannel() == channelName)
			return true;
		
		if (getConfig().get("channels." + channelName + ".public") != null) {
			if (getConfig().getBoolean("channels." + channelName + ".public"))
				return true;
		}
		
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
		if (has(player, "TitanChat.access." + channelName))
			return true;
		
		List<Player> players = new ArrayList<Player>();
		
		if (!channelMembers.get(channelName).isEmpty()) {
			players = channelMembers.get(channelName);
			
			if (players.contains(player))
				return true;
		}
		
		return false;
	}
	
	// Check if the player is muted
	
	public boolean isMuted(Player player, String channelName) {
		if (muted.isEmpty() || muted.get(channelName) == null)
			return false;
		
		if (muted.get(channelName).contains(player))
			return true;
		
		return false;
	}
	
	// Check if the server is silenced
	
	public boolean isSilenced() {
		return silence;
	}
	
	// Check if the channel is silenced
	
	public boolean isSilenced(String channelName) {
		if (channelName == getDefaultChannel())
			return silence;
		
		if (channelName == getStaffChannel())
			return false;
		
		return silenced.get(channelName);
	}
	
	// Check if the player is staff
	
	public boolean isStaff(Player player) {
		if (has(player, "TitanChat.admin"))
			return true;
		
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
	
	// Mutes the player
	
	public void mute(Player player, String channelName) {
		if (muted.isEmpty() || muted.get(channelName) == null) {
			List<Player> players = new ArrayList<Player>();
			players.add(player);
			muted.put(channelName, players);
			
		} else {
			List<Player> players = muted.get(channelName);
			players.add(player);
			muted.put(channelName, players);
		}
		sendWarning(player, "You have been muted on " + channelName);
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
					
				}
				
				// /titanchat commands
				// The TitanChat Command Directory
				
				if (args[0].equalsIgnoreCase("commands")) {
					player.sendMessage("TitanChat Commands");
					player.sendMessage("Command: /titanchat [action] [argument]");
					player.sendMessage("Alias: /tc action [argument]");
					player.sendMessage("/titanchat commands [page]");
					return true;
					
				}
				
				if (args[0].equalsIgnoreCase("silence")) {
					silence = (silence) ? false : true;
					return true;
				}
				
				sendWarning(player, "Invalid Command");
				
			} else if (args.length == 2) {
				for (Commands command : Commands.values()) {
					if (command.toString().equalsIgnoreCase(args[0])) {
						new TitanChatCommands(this).onCommand(player, args[0], args[1], getChannel(player));
						return true;
					}
				}
				
				sendWarning(player, "Invalid Command");
				
			} else if (args.length == 3) {
				if (channelExist(args[2])) {
					for (Commands command : Commands.values()) {
						if (command.toString().equalsIgnoreCase(args[0])) {
							new TitanChatCommands(this).onCommand(player, args[0], args[1], args[2]);
							return true;
						}
					}
					
				} else {
					sendWarning(player, "Channel does not exist");
				}
				
				sendWarning(player, "Invalid Command");
				
			} else {
				
				// /titanchat broadcast [message]
				// Broadcasts the message globally
				
				if (args[0].equalsIgnoreCase("broadcast")) {
					if (player.hasPermission("TitanChat.broadcast")) {
						ChatColor broadcastColour = ChatColor.valueOf(getConfig().getString("broadcast.colour"));
						String tag = getConfig().getString("broadcast.tag");
						
						StringBuilder str = new StringBuilder();
						
						for (int word = 1; word < args.length ; word++) {
							if (str.length() > 0) {
								str.append(" ");
							}
							
							str.append(args[word]);
						}
						
						String msg = new Channel(this).format(player, broadcastColour, tag, str.toString(), true);
						
						for (Player receiver : getServer().getOnlinePlayers()) {
							receiver.sendMessage(msg);
						}
						
						return true;
						
					} else {
						sendWarning(player, "You do not have permission to broadcast");
					}
					
				} else {
					sendWarning(player, "Invalid Command");
				}
			}
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
			getServer().getPluginManager().disablePlugin(this);
		}
		
		if (vault()) {
			if (setupPermission()) {
				infoLog(permission.getName() + " detected");
				infoLog("Using " + permission.getName() + " for permissions");
			}
			
			if (setupChat()) {
				infoLog("Prefix and suffixes supported");
			}
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
		if (channelAdmins != null) {
			channelAdmins.clear();
		}
		
		if (channelMembers != null) {
			channelMembers.clear();
		}
		
		if (channelBans != null) {
			channelBans.clear();
		}
		
		for (String channel : getChannelConfig().getConfigurationSection("channels").getKeys(false)) {
			List<String> adminNames = new ArrayList<String>();
			List<String> memberNames = new ArrayList<String>();
			List<String> bannedNames = new ArrayList<String>();
			
			if (getChannelConfig().getStringList("channels." + channel + ".admins") != null) {
				adminNames = getChannelConfig().getStringList("channels." + channel + ".admins");
			}
			
			if (getChannelConfig().getStringList("channels." + channel + ".members") != null) {
				memberNames = getChannelConfig().getStringList("channels." + channel + ".members");
			}
			
			if (getChannelConfig().getStringList("channels." + channel + ".black-list") != null) {
				bannedNames = getChannelConfig().getStringList("channels." + channel + ".black-list");
			}
			
			List<Player> admins = new ArrayList<Player>();
			List<Player> members = new ArrayList<Player>();
			List<Player> banned = new ArrayList<Player>();
			
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
			
			if (!bannedNames.isEmpty()) {
				for (String name : bannedNames) {
					banned.add(getPlayer(name));
				}
				
				channelBans.put(channel, banned);
				
				infoLog("Banned Players of " + channel + " found");
			}
		}
		
		for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
			silenced.put(channel, false);
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
	
	public boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		
		return (chat != null);
	}
	
	public boolean setupPermission() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		
		return (permission != null);
	}
	
	// Toggle between silencing for channels
	
	public void silence(String channelName) {
		boolean silence = (silenced.get(channelName)) ? false : true;
		silenced.put(channelName, silence);
	}
	
	// Unbans the player from the channel
	
	public void unban(Player player, String channelName) {
		List<Player> banned = new ArrayList<Player>();
		
		for (Player bannedPlayer : channelBans.get(channelName)) {
			banned.add(bannedPlayer);
		}
		
		banned.remove(player);
		
		if (banned.isEmpty()) {
			channelBans.remove(channelName);
			
		} else {
			channelBans.put(channelName, banned);
		}
		
		if (!isPublic(channelName)) {
			whitelistMember(player, channelName);
		}
		
		sendInfo(player, "You have been unbanned from " + channelName);
	}
	
	// Unmutes the player
	
	public void unmute(Player player, String channelName) {
		List<Player> mutes = new ArrayList<Player>();
		
		for (Player mute : muted.get(channelName)) {
			mutes.add(mute);
		}
		
		mutes.remove(player);
		
		if (mutes.isEmpty()) {
			muted.remove(channelName);
			
		} else {
			muted.put(channelName, mutes);
		}
		
		sendInfo(player, "You have been unmuted on " + channelName);
	}
	
	public boolean vault() {
		return getServer().getPluginManager().getPlugin("Vault") != null;
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
